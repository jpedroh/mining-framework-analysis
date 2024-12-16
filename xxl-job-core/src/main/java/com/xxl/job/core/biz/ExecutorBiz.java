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
    public abstract ReturnT<String> beat();

    /**
     * idle beat
     *
     * @param jobId
     * @return
     */
    public abstract ReturnT<String> idleBeat(IdleBeatParam idleBeatParam);

    /**
     * run
     * @param triggerParam
     * @return
     */
    public abstract ReturnT<String> run(TriggerParam triggerParam);

    /**
     * kill
     * @param jobId
     * @return
     */
    public abstract ReturnT<String> kill(KillParam killParam);

    /**
     * log
     *
     * @param logDateTim
     * 		
     * @param logId
     * 		
     */
    public abstract ReturnT<LogResult> log(LogParam logParam);
}