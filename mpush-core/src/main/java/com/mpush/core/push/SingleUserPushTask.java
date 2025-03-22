package com.mpush.core.push;
import com.mpush.api.Message;
import com.mpush.api.connection.Connection;
import com.mpush.api.spi.push.IPushMessage;
import com.mpush.common.ServerNodes;
import com.mpush.common.message.PushMessage;
import com.mpush.common.router.RemoteRouter;
import com.mpush.core.ack.AckTask;
import com.mpush.core.ack.AckTaskQueue;
import com.mpush.common.qps.FlowControl;
import com.mpush.core.router.LocalRouter;
import com.mpush.core.router.RouterCenter;
import com.mpush.tools.log.Logs;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.util.concurrent.ScheduledExecutorService;
import static com.mpush.common.ServerNodes.GS;

/**
 * Created by ohun on 16/10/24.
 *
 * @author ohun@live.cn (夜色)
 */
public final class SingleUserPushTask implements PushTask {
  private final FlowControl flowControl;

  private final IPushMessage message;

  public SingleUserPushTask(IPushMessage message, FlowControl flowControl) {
    this.flowControl = flowControl;
    this.message = message;
  }

  @Override public ScheduledExecutorService getExecutor() {
    return ((Message) message).getConnection().getChannel().eventLoop();
  }

  /**
     * 处理PushClient发送过来的Push推送请求
     * <p>
     * 查寻路由策略，先查本地路由，本地不存在，查远程，（注意：有可能远程查到也是本机IP）
     * <p>
     * 正常情况本地路由应该存在，如果不存在或链接失效，有以下几种情况：
     * <p>
     * 1.客户端重连，并且链接到了其他机器
     * 2.客户端下线，本地路由失效，远程路由还未清除
     * 3.PushClient使用了本地缓存，但缓存数据已经和实际情况不一致了
     * <p>
     * 对于三种情况的处理方式是, 再重新查寻下远程路由：
     * 1.如果发现远程路由是本机，直接删除，因为此时的路由已失效 (解决场景2)
     * 2.如果用户真在另一台机器，让PushClient清理下本地缓存后，重新推送 (解决场景1,3)
     * <p>
     */
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
  private boolean checkLocal(IPushMessage message) {
    String userId = message.getUserId();
    int clientType = message.getClientType();
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
      PushCenter.I.getPushListener().onFailure(message);
      Logs.PUSH.error("[SingleUserPush] push message to client failure, tcp sender too busy, message={}, conn={}", message, connection);
      return true;
    }
    if (flowControl.checkQps()) {
      PushMessage pushMessage = PushMessage.build(connection).setContent(message.getContent());
      pushMessage.getPacket().addFlag(message.getFlags());
      pushMessage.send(new PushFutureListener(message, pushMessage.getSessionId()));
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
  private void checkRemote(IPushMessage message) {
    String userId = message.getUserId();
    int clientType = message.getClientType();
    RemoteRouter remoteRouter = RouterCenter.I.getRemoteRouterManager().lookup(userId, clientType);
    if (remoteRouter == null || remoteRouter.isOffline()) {
      PushCenter.I.getPushListener().onOffline(message);
      Logs.PUSH.info("[SingleUserPush] remote router not exists user offline, message={}", message);
      return;
    }
    if (remoteRouter.getRouteValue().isThisPC(GS.getHost(), GS.getPort())) {
      PushCenter.I.getPushListener().onOffline(message);
      RouterCenter.I.getRemoteRouterManager().unRegister(userId, clientType);
      Logs.PUSH.info("[SingleUserPush] find remote router in this pc, but local router not exists, userId={}, clientType={}, router={}", userId, clientType, remoteRouter);
      return;
    }
    PushCenter.I.getPushListener().onRedirect(message);
    Logs.PUSH.info("[SingleUserPush] find router in another pc, userId={}, clientType={}, router={}", userId, clientType, remoteRouter);
  }

  private static final class PushFutureListener implements ChannelFutureListener {
    private final IPushMessage message;

    private final int messageId;

    private PushFutureListener(IPushMessage message, int messageId) {
      this.message = message;
      this.messageId = messageId;
    }

    @Override public void operationComplete(ChannelFuture future) throws Exception {
      if (future.isSuccess()) {
        if (message.isNeedAck()) {
          addAckTask(messageId);
        } else {
          PushCenter.I.getPushListener().onSuccess(message);
        }
        Logs.PUSH.info("[SingleUserPush] push message to client success, message={}", message);
      } else {
        PushCenter.I.getPushListener().onFailure(message);
        Logs.PUSH.error("[SingleUserPush] push message to client failure, message={}, conn={}", message, future.channel());
      }
    }

    /**
         * 添加ACK任务到队列, 等待客户端响应
         *
         * @param messageId 下发到客户端待ack的消息的sessionId
         */
    private void addAckTask(int messageId) {
      message.finalized();
      AckTask task = AckTask.from(messageId).setCallback(new PushAckCallback(message));
      AckTaskQueue.I.add(task, message.getTimeoutMills());
    }
  }
}