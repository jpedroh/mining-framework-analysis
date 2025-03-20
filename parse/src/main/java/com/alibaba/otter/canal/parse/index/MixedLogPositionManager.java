package com.alibaba.otter.canal.parse.index;
import java.util.concurrent.ExecutorService;
import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import java.util.concurrent.Executors;
import com.alibaba.otter.canal.parse.exception.CanalParseException;
import org.slf4j.Logger;
import com.alibaba.otter.canal.protocol.position.LogPosition;
import org.slf4j.LoggerFactory;

/**
 * Created by yinxiu on 17/3/17. Email: marklin.hz@gmail.com Memory first.
 * Asynchronous commit position info to ZK.
 */
public class MixedLogPositionManager extends AbstractLogPositionManager {
  private final Logger logger = LoggerFactory.getLogger(MixedLogPositionManager.class);

  private final MemoryLogPositionManager memoryLogPositionManager;

  private final ZooKeeperLogPositionManager zooKeeperLogPositionManager;

  private final ExecutorService executor;

  public MixedLogPositionManager(ZkClientx zkClient) {
    if (zkClient == null) {
      throw new NullPointerException("null zkClient");
    }
    this.memoryLogPositionManager = new MemoryLogPositionManager();
    this.zooKeeperLogPositionManager = new ZooKeeperLogPositionManager(zkClient);
    this.executor = Executors.newFixedThreadPool(1);
  }

  @Override public void start() {
    super.start();
    if (!memoryLogPositionManager.isStart()) {
      memoryLogPositionManager.start();
    }
    if (!zooKeeperLogPositionManager.isStart()) {
      zooKeeperLogPositionManager.start();
    }
  }

  @Override public void stop() {
    super.stop();
    executor.shutdown();
    zooKeeperLogPositionManager.stop();
    memoryLogPositionManager.stop();
  }

  @Override public LogPosition getLatestIndexBy(String destination) {
    LogPosition logPosition = memoryLogPositionManager.getLatestIndexBy(destination);
    if (logPosition != null) {
      return logPosition;
    }
    logPosition = zooKeeperLogPositionManager.getLatestIndexBy(destination);
    if (logPosition != null) {
      memoryLogPositionManager.persistLogPosition(destination, logPosition);
    }
    return logPosition;
  }

  @Override public void persistLogPosition(final String destination, final LogPosition logPosition) throws CanalParseException {
    memoryLogPositionManager.persistLogPosition(destination, logPosition);
    executor.submit(new Runnable() {
      public void run() {
        try {
          zooKeeperLogPositionManager.persistLogPosition(destination, logPosition);
        } catch (Exception e) {
          logger.error("ERROR # persist to zookeeper has an error", e);
        }
      }
    });
  }
}