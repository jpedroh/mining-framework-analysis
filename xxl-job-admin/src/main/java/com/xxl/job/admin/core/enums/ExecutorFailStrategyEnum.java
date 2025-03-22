<<<<<<< /usr/src/app/output/xuxueli/xxl-job/ccb72d1146b56a19353422c5d2a1a56fe76aa4b3/xxl-job-admin/src/main/java/com/xxl/job/admin/core/enums/ExecutorFailStrategyEnum.java/left.java
package com.xxl.job.admin.core.enums;

import com.xxl.job.admin.core.util.I18nUtil;

/**
 * Created by xuxueli on 17/5/9.
 */
public enum ExecutorFailStrategyEnum {
     //无失败策略
    NULL(I18nUtil.getString("jobconf_fail_null")),
     //调度失败重试
    FAIL_TRIGGER_RETRY(I18nUtil.getString("jobconf_fail_trigger_retry")),
     //执行失败重试
    FAIL_HANDLE_RETRY(I18nUtil.getString("jobconf_fail_handle_retry"));

    private final String title;
    private ExecutorFailStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ExecutorFailStrategyEnum match(String name, ExecutorFailStrategyEnum defaultItem) {
        if (name != null) {
            for (ExecutorFailStrategyEnum item: ExecutorFailStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

}
||||||| /usr/src/app/output/xuxueli/xxl-job/ccb72d1146b56a19353422c5d2a1a56fe76aa4b3/xxl-job-admin/src/main/java/com/xxl/job/admin/core/enums/ExecutorFailStrategyEnum.java/base.java
package com.xxl.job.admin.core.enums;

import com.xxl.job.admin.core.util.I18nUtil;

/**
 * Created by xuxueli on 17/5/9.
 */
public enum ExecutorFailStrategyEnum {

    NULL(I18nUtil.getString("jobconf_fail_null")),

    FAIL_TRIGGER_RETRY(I18nUtil.getString("jobconf_fail_trigger_retry")),

    FAIL_HANDLE_RETRY(I18nUtil.getString("jobconf_fail_handle_retry"));

    private final String title;
    private ExecutorFailStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ExecutorFailStrategyEnum match(String name, ExecutorFailStrategyEnum defaultItem) {
        if (name != null) {
            for (ExecutorFailStrategyEnum item: ExecutorFailStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

}
=======
fatal: path 'xxl-job-admin/src/main/java/com/xxl/job/admin/core/enums/ExecutorFailStrategyEnum.java' does not exist in '4eae26a35367fc5635b6b10d00327a2f39fb8f20'
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/ccb72d1146b56a19353422c5d2a1a56fe76aa4b3/xxl-job-admin/src/main/java/com/xxl/job/admin/core/enums/ExecutorFailStrategyEnum.java/right.java
