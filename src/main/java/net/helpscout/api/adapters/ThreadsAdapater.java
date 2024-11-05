package net.helpscout.api.adapters;
import com.google.gson.*;
import net.helpscout.api.cbo.JsonThreadLocal;
import net.helpscout.api.cbo.ThreadType;
import net.helpscout.api.model.thread.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Type;

public class ThreadsAdapater implements JsonDeserializer<LineItem> {
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
    } else {
      if (ThreadType.Message.getLabel().equals(theType)) {
        item = gson.create().fromJson(json, Message.class);
      } else {
        if (ThreadType.Customer.getLabel().equals(theType)) {
          item = gson.create().fromJson(json, Customer.class);
        } else {
          if (ThreadType.Note.getLabel().equals(theType)) {
            item = gson.create().fromJson(json, Note.class);
          } else {
            if (ThreadType.ForwardParent.getLabel().equals(theType)) {
              item = gson.create().fromJson(json, ForwardParent.class);
            } else {
              if (ThreadType.ForwardChild.getLabel().equals(theType)) {
                item = gson.create().fromJson(json, ForwardChild.class);
              } else {
                if (ThreadType.Chat.getLabel().equals(theType)) {
                  item = gson.create().fromJson(json, Chat.class);
                }
              }
            }
          }
        }
      }
    }
    JsonThreadLocal.unset();
    return item;
  }
}