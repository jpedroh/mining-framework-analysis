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
     * @param idleBeatParam
     * @return
     */
<<<<<<< /usr/src/app/output/xuxueli/xxl-job/c5c9325aee1204646605eee5e7cb212a9eb97028/xxl-job-core/src/main/java/com/xxl/job/core/biz/ExecutorBiz.java/left.java
    public ReturnT<String> idleBeat(long jobId);
||||||| /usr/src/app/output/xuxueli/xxl-job/c5c9325aee1204646605eee5e7cb212a9eb97028/xxl-job-core/src/main/java/com/xxl/job/core/biz/ExecutorBiz.java/base.java
    public ReturnT<String> idleBeat(int jobId);
=======
    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam);
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/c5c9325aee1204646605eee5e7cb212a9eb97028/xxl-job-core/src/main/java/com/xxl/job/core/biz/ExecutorBiz.java/right.java

    /**
     * run
     * @param triggerParam
     * @return
     */
    public ReturnT<String> run(TriggerParam triggerParam);

    /**
     * kill
     * @param killParam
     * @return
     */
<<<<<<< /usr/src/app/output/xuxueli/xxl-job/c5c9325aee1204646605eee5e7cb212a9eb97028/xxl-job-core/src/main/java/com/xxl/job/core/biz/ExecutorBiz.java/left.java
    public ReturnT<String> kill(long jobId);
||||||| /usr/src/app/output/xuxueli/xxl-job/c5c9325aee1204646605eee5e7cb212a9eb97028/xxl-job-core/src/main/java/com/xxl/job/core/biz/ExecutorBiz.java/base.java
    public ReturnT<String> kill(int jobId);
=======
    public ReturnT<String> kill(KillParam killParam);
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/c5c9325aee1204646605eee5e7cb212a9eb97028/xxl-job-core/src/main/java/com/xxl/job/core/biz/ExecutorBiz.java/right.java

    /**
     * log
     * @param logParam
     * @return
     */
    public ReturnT<LogResult> log(LogParam logParam);

}
