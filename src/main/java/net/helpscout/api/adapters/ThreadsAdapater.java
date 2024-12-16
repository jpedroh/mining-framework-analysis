package net.helpscout.api.adapters;

import com.google.gson.*;
import java.lang.reflect.Type;
import net.helpscout.api.cbo.JsonThreadLocal;
import net.helpscout.api.cbo.ThreadType;
import net.helpscout.api.model.thread.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThreadsAdapater implements JsonSerializer<LineItem> , JsonDeserializer<LineItem> {
	final static Logger log = LoggerFactory.getLogger(ThreadStateAdapter.class);

	private GsonBuilder gson;

	public ThreadsAdapater(GsonBuilder gson) {
		this.gson = gson;
	}

	public LineItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		JsonThreadLocal.set(json);
		LineItem item = null;
		String theType = obj.get("type").getAsString();
		if (ThreadType.LineItem.getLabel().equals(theType)) {
			item = gson.create().fromJson(json, BaseLineItem.class);
		} else if (ThreadType.Message.getLabel().equals(theType)) {
			item = gson.create().fromJson(json, Message.class);
		} else if (ThreadType.Customer.getLabel().equals(theType)) {
			item = gson.create().fromJson(json, Customer.class);
		} else if (ThreadType.Note.getLabel().equals(theType)) {
			item = gson.create().fromJson(json, Note.class);
		} else if (ThreadType.ForwardParent.getLabel().equals(theType)) {
			item = gson.create().fromJson(json, ForwardParent.class);
		} else if (ThreadType.ForwardChild.getLabel().equals(theType)) {
			item = gson.create().fromJson(json, ForwardChild.class);
		} else if ("chat".equals(theType)) {
			item = gson.create().fromJson(json, Chat.class);
		}
		JsonThreadLocal.unset();
		return item;
	}

	public JsonElement serialize(LineItem lineItem, Type typeOfT, JsonSerializationContext context) {
		AbstractThread thread = (AbstractThread)lineItem;
		if (lineItem.getClass().isAssignableFrom(BaseLineItem.class)) {
			thread.setType(ThreadType.LineItem.getLabel());
		} else if (lineItem.getClass().isAssignableFrom(Message.class)) {
			thread.setType(ThreadType.Message.getLabel());
		} else if (lineItem.getClass().isAssignableFrom(Note.class)) {
			thread.setType(ThreadType.Note.getLabel());
		} else if (lineItem.getClass().isAssignableFrom(Customer.class)) {
			thread.setType(ThreadType.Customer.getLabel());
		} else if (lineItem.getClass().isAssignableFrom(ForwardParent.class)) {
			thread.setType(ThreadType.ForwardParent.getLabel());
		} else if (lineItem.getClass().isAssignableFrom(ForwardChild.class)) {
			thread.setType(ThreadType.ForwardChild.getLabel());
		}
		JsonElement json = gson.create().toJsonTree(thread);
		log.debug("BKD => Thread: " + json.toString());
		return json;
	}
}