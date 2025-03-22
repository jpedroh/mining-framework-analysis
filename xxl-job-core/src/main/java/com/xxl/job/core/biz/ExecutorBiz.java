package com.xxl.job.core.biz;
import com.xxl.job.core.biz.model.*;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface ExecutorBiz {
  /**
     * beat
     * @return
     */
  public ReturnT<String> beat();

  /**
     * idle beat
     *
     * @param jobId
     * @return
     */
  public ReturnT<String> idleBeat(
<<<<<<< /usr/src/app/output/xuxueli/xxl-job/c5c9325aee1204646605eee5e7cb212a9eb97028/xxl-job-core/src/main/java/com/xxl/job/core/biz/ExecutorBiz.java/left.java
  long jobId
=======
  IdleBeatParam idleBeatParam
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/c5c9325aee1204646605eee5e7cb212a9eb97028/xxl-job-core/src/main/java/com/xxl/job/core/biz/ExecutorBiz.java/right.java
  );

  /**
     * kill
     * @param jobId
     * @return
     */
  public ReturnT<String> kill(
<<<<<<< /usr/src/app/output/xuxueli/xxl-job/c5c9325aee1204646605eee5e7cb212a9eb97028/xxl-job-core/src/main/java/com/xxl/job/core/biz/ExecutorBiz.java/left.java
  long jobId
=======
  KillParam killParam
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/c5c9325aee1204646605eee5e7cb212a9eb97028/xxl-job-core/src/main/java/com/xxl/job/core/biz/ExecutorBiz.java/right.java
  );

  /**
     * run
     * @param triggerParam
     * @return
     */
  public ReturnT<String> run(TriggerParam triggerParam);

  /**
     * log
     * @param logDateTim
     * @param logId
     * @param fromLineNum
     * @return
     */
  public ReturnT<LogResult> log(LogParam logParam);
}