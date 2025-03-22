package com.mpush.core.push;
import com.mpush.api.connection.Connection;
import com.mpush.common.ErrorCode;
import com.mpush.common.message.ErrorMessage;
import com.mpush.common.message.OkMessage;
import com.mpush.common.message.PushMessage;
import com.mpush.common.message.gateway.GatewayPushMessage;
import com.mpush.common.router.RemoteRouter;
import com.mpush.core.ack.AckCallback;
import com.mpush.core.ack.AckTask;
import com.mpush.core.ack.AckTaskQueue;
import com.mpush.core.router.LocalRouter;
import com.mpush.core.router.RouterCenter;
import com.mpush.tools.log.Logs;
import java.util.concurrent.ScheduledExecutorService;
import static com.mpush.common.ErrorCode.OFFLINE;
import static com.mpush.common.ErrorCode.PUSH_CLIENT_FAILURE;
import static com.mpush.common.ErrorCode.ROUTER_CHANGE;
import static com.mpush.zk.node.ZKServerNode.GS_NODE;

/**
 * Created by ohun on 16/10/24.
 *
 * @author ohun@live.cn (夜色)
 */
public final class SingleUserPushTask implements PushTask {
  private final FlowControl flowControl;

  private final GatewayPushMessage message;

  public SingleUserPushTask(GatewayPushMessage message, FlowControl flowControl) {
    this.flowControl = flowControl;
    this.message = message;
  }

  @Override public ScheduledExecutorService getExecutor() {
    return message.getConnection().getChannel().eventLoop();
  }

  @Override public void run() {
    if (!checkLocal(message)) {
      checkRemote(message);
    }
  }

  /**
     * 检查本地路由，如果存在并且链接可用直接推送
     * 否则要检查下远程路由
     *
     * @param message message
     * @return true/false true:success
     */
  private boolean checkLocal(final GatewayPushMessage message) {
    String userId = message.userId;
    int clientType = message.clientType;
    LocalRouter localRouter = RouterCenter.I.getLocalRouterManager().lookup(userId, clientType);
    if (localRouter == null) {
      return false;
    }
    Connection connection = localRouter.getRouteValue();
    if (!connection.isConnected()) {
      Logs.PUSH.warn("[SingleUserPush] find local router but conn disconnected, message={}, conn={}", message, connection);
      RouterCenter.I.getLocalRouterManager().unRegister(userId, clientType);
      return false;
    }
    if (!connection.getChannel().isWritable()) {
      ErrorMessage.from(message).setErrorCode(PUSH_CLIENT_FAILURE).setData(userId + ',' + clientType).sendRaw();
      Logs.PUSH.error("[SingleUserPush] push message to client failure, tcp sender too busy, message={}, conn={}", message, connection);
      return true;
    }
    if (flowControl.checkQps()) {
      PushMessage pushMessage = PushMessage.build(connection).setContent(message.content);
      pushMessage.getPacket().addFlag(message.getPacket().flags);
      pushMessage.send((future) -> {
        if (future.isSuccess()) {
          if (message.needAck()) {
            addAckTask(message, pushMessage.getSessionId());
          } else {
            OkMessage.from(message).setData(userId + ',' + clientType).sendRaw();
          }
          Logs.PUSH.info("[SingleUserPush] push message to client success, message={}", message);
        } else {
          ErrorMessage.from(message).setErrorCode(PUSH_CLIENT_FAILURE).setData(userId + ',' + clientType).sendRaw();
          Logs.PUSH.error("[SingleUserPush] push message to client failure, message={}, conn={}", message, connection);
        }
      });
    } else {
      PushCenter.I.delayTask(flowControl.getDelay(), this);
    }
    return true;
  }

  /**
     * 检测远程路由，
     * 如果不存在直接返回用户已经下线
     * 如果是本机直接删除路由信息
     * 如果是其他机器让PushClient重推
     *
     * @param message message
     */
  private void checkRemote(GatewayPushMessage message) {
    String userId = message.userId;
    int clientType = message.clientType;
    RemoteRouter remoteRouter = RouterCenter.I.getRemoteRouterManager().lookup(userId, clientType);
    if (remoteRouter == null || remoteRouter.isOffline()) {
      ErrorMessage.from(message).setErrorCode(OFFLINE).setData(userId + ',' + clientType).sendRaw();
      Logs.PUSH.info("[SingleUserPush] remote router not exists user offline, message={}", message);
      return;
    }
    if (remoteRouter.getRouteValue().isThisPC(GS_NODE.getIp(), GS_NODE.getPort())) {
      ErrorMessage.from(message).setErrorCode(OFFLINE).setData(userId + ',' + clientType).sendRaw();
      RouterCenter.I.getRemoteRouterManager().unRegister(userId, clientType);
      Logs.PUSH.info("[SingleUserPush] find remote router in this pc, but local router not exists, userId={}, clientType={}, router={}", userId, clientType, remoteRouter);
      return;
    }
    ErrorMessage.from(message).setErrorCode(ROUTER_CHANGE).setData(userId + ',' + clientType).sendRaw();
    Logs.PUSH.info("[SingleUserPush] find router in another pc, userId={}, clientType={}, router={}", userId, clientType, remoteRouter);
  }

  /**
     * 添加ACK任务到队列, 等待客户端响应
     *
     * @param message   网关消息
     * @param messageId 下发到客户端待ack的消息的sessionId
     */
  private static void addAckTask(GatewayPushMessage message, int messageId) {
    message.getPacket().body = null;
    message.content = null;
    AckTask task = AckTask.from(message, messageId).setCallback(new GatewayPushAckCallback(message));
    AckTaskQueue.I.add(task, message.timeout);
  }

  private static class GatewayPushAckCallback implements AckCallback {
    private final GatewayPushMessage message;

    private GatewayPushAckCallback(GatewayPushMessage message) {
      this.message = message;
    }

    @Override public void onSuccess(AckTask task) {
      if (!message.getConnection().isConnected()) {
        Logs.PUSH.warn("receive client ack, gateway connection is closed, task={}", task);
        return;
      }
      OkMessage okMessage = OkMessage.from(message);
      okMessage.setData(message.userId + ',' + message.clientType);
      okMessage.sendRaw();
      Logs.PUSH.info("receive client ack and response gateway client success, task={}", task);
    }

    @Override public void onTimeout(AckTask task) {
      if (!message.getConnection().isConnected()) {
        Logs.PUSH.warn("push message timeout client not ack, gateway connection is closed, task={}", task);
        return;
      }
      ErrorMessage errorMessage = ErrorMessage.from(message);
      errorMessage.setData(message.userId + ',' + message.clientType);
      errorMessage.setErrorCode(ErrorCode.ACK_TIMEOUT);
      errorMessage.sendRaw();
      Logs.PUSH.warn("push message timeout client not ack, task={}", task);
    }
  }
}