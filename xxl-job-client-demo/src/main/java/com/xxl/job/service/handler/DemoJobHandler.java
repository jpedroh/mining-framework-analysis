package com.xxl.job.service.handler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xxl.job.client.handler.HandlerRepository;
import com.xxl.job.client.handler.IJobHandler;

/**
 * demo job handler
 * @author xuxueli 2015-12-19 19:43:36
 */
@Service
public class DemoJobHandler extends IJobHandler {
	private static transient Logger logger = LoggerFactory.getLogger(DemoJobHandler.class);
	
	public DemoJobHandler() {
		HandlerRepository.regist("demoJobHandler", this);
	}
	
	@Override
	public JobHandleStatus handle(String... params) throws Exception {
		logger.info(" ... params:" + params);
<<<<<<< /usr/src/app/output/xuxueli/xxl-job/d8f9c071e9c8b26b257c2131d4d24fd9d800a8f1/xxl-job-client-demo/src/main/java/com/xxl/job/service/handler/DemoJobHandler.java/left.java
		for (int i = 0; i < 60; i++) {
||||||| /usr/src/app/output/xuxueli/xxl-job/d8f9c071e9c8b26b257c2131d4d24fd9d800a8f1/xxl-job-client-demo/src/main/java/com/xxl/job/service/handler/DemoJobHandler.java/base.java
		for (int i = 0; ; i++) {
=======
		for (int i = 0; i < 10; i++) {
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/d8f9c071e9c8b26b257c2131d4d24fd9d800a8f1/xxl-job-client-demo/src/main/java/com/xxl/job/service/handler/DemoJobHandler.java/right.java
			TimeUnit.SECONDS.sleep(1);
			logger.info("handler run:{}", i);
		}
		return JobHandleStatus.SUCCESS;
	}
	
	public static void main(String[] args) {
		System.out.println(DemoJobHandler.class.getName());
		System.out.println(DemoJobHandler.class);
	}
	
}
