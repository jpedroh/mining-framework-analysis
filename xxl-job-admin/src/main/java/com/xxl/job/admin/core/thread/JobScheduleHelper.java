package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.cron.CronExpression;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


/**
 * @author xuxueli 2019-05-21
 */
public class JobScheduleHelper {
    private static Logger logger = LoggerFactory.getLogger(JobScheduleHelper.class);

    private static JobScheduleHelper instance = new JobScheduleHelper();

    public static JobScheduleHelper getInstance(){
        return instance;
    }

    // pre read
    public static final long PRE_READ_MS = 5000;    // pre read

    private Thread scheduleThread;

    private Thread ringThread;

    private volatile boolean scheduleThreadToStop = false;

    private volatile boolean ringThreadToStop = false;

    private static volatile Map<Long, List<Long>> ringData = new ConcurrentHashMap<>();

    public void start() {
        // schedule thread
        scheduleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(5000 - (System.currentTimeMillis() % 1000));
                } catch (java.lang.InterruptedException e) {
                    if (!scheduleThreadToStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
                logger.info(">>>>>>>>> init xxl-job admin scheduler success.");
                // pre-read count: treadpool-size * trigger-qps (each trigger cost 50ms, qps = 1000/50 = 20)
                int preReadCount = (XxlJobAdminConfig.getAdminConfig().getTriggerPoolFastMax() + XxlJobAdminConfig.getAdminConfig().getTriggerPoolSlowMax()) * 20;
                while (!scheduleThreadToStop) {
                    // Scan Job
                    long start = System.currentTimeMillis();
                    JpaTransactionManager transactionManager = null;
                    TransactionStatus transactionStatus = null;
                    boolean preReadSuc = true;
                    try {
                        DefaultTransactionDefinition transDefinition = new DefaultTransactionDefinition();
                        transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
                        transactionManager = XxlJobAdminConfig.getAdminConfig().getTransactionManager();
                        transactionStatus = transactionManager.getTransaction(transDefinition);
                        XxlJobAdminConfig.getAdminConfig().getXxlJobLockDao().getJobLockForUpdate();
                        // tx start
                        // 1、pre read
                        long nowTime = System.currentTimeMillis();
                        Sort sort = Sort.by("id").ascending();
                        // ORDER BY id ASC LIMIT #{preReadCount}
                        PageRequest pageRequest = PageRequest.of(0, preReadCount, sort);
                        List<XxlJobInfo> scheduleList = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleJobQuery(nowTime + PRE_READ_MS, pageRequest);
                        if ((scheduleList != null) && (scheduleList.size() > 0)) {
                            // 2、push time-ring
                            for (XxlJobInfo jobInfo : scheduleList) {
                                // time-ring jump
                                if (nowTime > (jobInfo.getTriggerNextTime() + PRE_READ_MS)) {
                                    // 2.1、trigger-expire > 5s：pass && make next-trigger-time
                                    logger.warn(">>>>>>>>>>> xxl-job, schedule misfire, jobId = " + jobInfo.getId());
                                    // fresh next
                                    refreshNextValidTime(jobInfo, new Date());
                                } else if (nowTime > jobInfo.getTriggerNextTime()) {
                                    // 2.2、trigger-expire < 5s：direct-trigger && make next-trigger-time
                                    // 1、trigger
                                    JobTriggerPoolHelper.trigger(jobInfo.getId(), TriggerTypeEnum.CRON, -1, null, null, null);
                                    logger.debug(">>>>>>>>>>> xxl-job, schedule push trigger : jobId = " + jobInfo.getId());
                                    // 2、fresh next
                                    refreshNextValidTime(jobInfo, new Date());
                                    // next-trigger-time in 5s, pre-read again
                                    if ((jobInfo.getTriggerStatus() == 1) && ((nowTime + PRE_READ_MS) > jobInfo.getTriggerNextTime())) {
                                        // 1、make ring second
                                        int ringSecond = ((int) ((jobInfo.getTriggerNextTime() / 1000) % 60));
                                        // 2、push time ring
                                        pushTimeRing(ringSecond, jobInfo.getId());
                                        // 3、fresh next
                                        refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                                    }
                                } else {
                                    // 2.3、trigger-pre-read：time-ring trigger && make next-trigger-time
                                    // 1、make ring second
                                    int ringSecond = ((int) ((jobInfo.getTriggerNextTime() / 1000) % 60));
                                    // 2、push time ring
                                    pushTimeRing(ringSecond, jobInfo.getId());
                                    // 3、fresh next
                                    refreshNextValidTime(jobInfo, new Date(jobInfo.getTriggerNextTime()));
                                }
                            }
                            // 3、update trigger info
                            for (XxlJobInfo jobInfo : scheduleList) {
                                XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().scheduleUpdate(jobInfo);
                            }
                        } else {
                            preReadSuc = false;
                        }
                        // tx stop
                    } catch (java.lang.Exception e) {
                        if (!scheduleThreadToStop) {
                            logger.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread error:{}", e);
                        }
                    } finally {
                        // commit
                        if ((transactionManager != null) && (transactionStatus != null)) {
                            try {
                                transactionManager.commit(transactionStatus);
                            } catch (TransactionException e) {
                                if (!scheduleThreadToStop) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                    long cost = System.currentTimeMillis() - start;
                    // Wait seconds, align second
                    if (cost < 1000) {
                        // scan-overtime, not wait
                        try {
                            // pre-read period: success > scan each second; fail > skip this period;
                            TimeUnit.MILLISECONDS.sleep((preReadSuc ? 1000 : PRE_READ_MS) - (System.currentTimeMillis() % 1000));
                        } catch (java.lang.InterruptedException e) {
                            if (!scheduleThreadToStop) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                } 
                logger.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#scheduleThread stop");
            }
        });
        scheduleThread.setDaemon(true);
        scheduleThread.setName("xxl-job, admin JobScheduleHelper#scheduleThread");
        scheduleThread.start();
        // ring thread
        ringThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // align second
                try {
                    TimeUnit.MILLISECONDS.sleep(1000 - (System.currentTimeMillis() % 1000));
                } catch (java.lang.InterruptedException e) {
                    if (!ringThreadToStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
                while (!ringThreadToStop) {
                    try {
                        // second data
                        List<Long> ringItemData = new ArrayList<>();
                        int nowSecond = Calendar.getInstance().get(Calendar.SECOND);// 避免处理耗时太长，跨过刻度，向前校验一个刻度；

                        for (int i = 0; i < 2; i++) {
                            List<Long> tmpData = ringData.remove(((nowSecond + 60) - i) % 60);
                            if (tmpData != null) {
                                ringItemData.addAll(tmpData);
                            }
                        }
                        // ring trigger
                        logger.debug(((">>>>>>>>>>> xxl-job, time-ring beat : " + nowSecond) + " = ") + Arrays.asList(ringItemData));
                        if (ringItemData.size() > 0) {
                            // do trigger
                            for (long jobId : ringItemData) {
                                // do trigger
                                JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.CRON, -1, null, null, null);
                            }
                            // clear
                            ringItemData.clear();
                        }
                    } catch (java.lang.Exception e) {
                        if (!ringThreadToStop) {
                            logger.error(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread error:{}", e);
                        }
                    }
                    // next second, align second
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000 - (System.currentTimeMillis() % 1000));
                    } catch (java.lang.InterruptedException e) {
                        if (!ringThreadToStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                } 
                logger.info(">>>>>>>>>>> xxl-job, JobScheduleHelper#ringThread stop");
            }
        });
        ringThread.setDaemon(true);
        ringThread.setName("xxl-job, admin JobScheduleHelper#ringThread");
        ringThread.start();
    }

    private void refreshNextValidTime(XxlJobInfo jobInfo, Date fromTime) throws ParseException {
        Date nextValidTime = new CronExpression(jobInfo.getJobCron()).getNextValidTimeAfter(fromTime);
        if (nextValidTime != null) {
            jobInfo.setTriggerLastTime(jobInfo.getTriggerNextTime());
            jobInfo.setTriggerNextTime(nextValidTime.getTime());
        } else {
            jobInfo.setTriggerStatus(0);
            jobInfo.setTriggerLastTime(0);
            jobInfo.setTriggerNextTime(0);
        }
    }

    private void pushTimeRing(long ringSecond, long jobId) {
        // push async ring
        List<Long> ringItemData = ringData.get(ringSecond);
        if (ringItemData == null) {
            ringItemData = new ArrayList<Long>();
            ringData.put(ringSecond, ringItemData);
        }
        ringItemData.add(jobId);
        logger.debug(((">>>>>>>>>>> xxl-job, schedule push time-ring : " + ringSecond) + " = ") + Arrays.asList(ringItemData));
    }

    public void toStop() {
        // 1、stop schedule
        scheduleThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);// wait

        } catch (java.lang.InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        if (scheduleThread.getState() != Thread.State.TERMINATED) {
            // interrupt and wait
            scheduleThread.interrupt();
            try {
                scheduleThread.join();
            } catch (java.lang.InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        // if has ring data
        boolean hasRingData = false;
        if (!ringData.isEmpty()) {
            for (long second : ringData.keySet()) {
                List<Long> tmpData = ringData.get(second);
                if ((tmpData != null) && (tmpData.size() > 0)) {
                    hasRingData = true;
                    break;
                }
            }
        }
        if (hasRingData) {
            try {
                TimeUnit.SECONDS.sleep(8);
            } catch (java.lang.InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        // stop ring (wait job-in-memory stop)
        ringThreadToStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (java.lang.InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        if (ringThread.getState() != Thread.State.TERMINATED) {
            // interrupt and wait
            ringThread.interrupt();
            try {
                ringThread.join();
            } catch (java.lang.InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info(">>>>>>>>>>> xxl-job, JobScheduleHelper stop");
    }
}