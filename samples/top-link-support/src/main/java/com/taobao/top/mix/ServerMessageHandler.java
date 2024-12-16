package com.taobao.top.mix;

import com.taobao.top.link.endpoint.EndpointContext;
import com.taobao.top.link.endpoint.Identity;
import com.taobao.top.link.endpoint.MessageHandler;
import java.util.Map;


public class ServerMessageHandler implements MessageHandler {
	@Override
	public void onMessage(Map<String, String> message, Identity messageFrom) {
		// System.out.println("onMessage:" + message);
	}

	@Override
	public void onMessage(EndpointContext context) throws Exception {
		// System.out.println("onMessage and reply:" + context.getMessage());
		// process client call here
		context.reply(context.getMessage());
	}
}