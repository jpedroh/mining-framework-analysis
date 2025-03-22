package net.helpscout.api.model.thread;
import net.helpscout.api.cbo.ThreadState;
import net.helpscout.api.cbo.ThreadType;
import net.helpscout.api.model.Attachment;
import java.util.List;

public class AbstractThread extends BaseLineItem implements ConversationThread {
  private Long id;

  private ThreadState state;

  private 
<<<<<<< /usr/src/app/output/helpscout/helpscout-api-java/7dde5bbe5d07ba446e8d8b7b942d378b40f2b5c3/src/main/java/net/helpscout/api/model/thread/AbstractThread.java/left.java
  String
=======
  ThreadType
>>>>>>> /usr/src/app/output/helpscout/helpscout-api-java/7dde5bbe5d07ba446e8d8b7b942d378b40f2b5c3/src/main/java/net/helpscout/api/model/thread/AbstractThread.java/right.java
   type;

  private String body;

  private List<String> toList;

  private List<String> ccList;

  private List<String> bccList;

  private List<Attachment> attachments;

  public boolean isPublished() {
    return this.state == ThreadState.Published;
  }

  public boolean isDraft() {
    return this.state == ThreadState.Draft;
  }

  public boolean isHeldForReview() {
    return this.state == ThreadState.UnderReview;
  }

  public boolean hasAttachments() {
    return this.attachments != null && this.attachments.size() > 0;
  }

  public Long getId() {
    return id;
  }

  public ThreadState getState() {
    return state;
  }

  public void setType(
<<<<<<< /usr/src/app/output/helpscout/helpscout-api-java/7dde5bbe5d07ba446e8d8b7b942d378b40f2b5c3/src/main/java/net/helpscout/api/model/thread/AbstractThread.java/left.java
  String
=======
  ThreadType
>>>>>>> /usr/src/app/output/helpscout/helpscout-api-java/7dde5bbe5d07ba446e8d8b7b942d378b40f2b5c3/src/main/java/net/helpscout/api/model/thread/AbstractThread.java/right.java
   type) {
    this.type = type;
  }

  public String getBody() {
    return body;
  }

  public 
<<<<<<< /usr/src/app/output/helpscout/helpscout-api-java/7dde5bbe5d07ba446e8d8b7b942d378b40f2b5c3/src/main/java/net/helpscout/api/model/thread/AbstractThread.java/left.java
  String
=======
  ThreadType
>>>>>>> /usr/src/app/output/helpscout/helpscout-api-java/7dde5bbe5d07ba446e8d8b7b942d378b40f2b5c3/src/main/java/net/helpscout/api/model/thread/AbstractThread.java/right.java
   getType() {
    return type;
  }

  public List<String> getToList() {
    return toList;
  }

  public List<String> getCcList() {
    return ccList;
  }

  public List<String> getBccList() {
    return bccList;
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setState(ThreadState state) {
    this.state = state;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public void setToList(List<String> toList) {
    this.toList = toList;
  }

  public void setCcList(List<String> ccList) {
    this.ccList = ccList;
  }

  public void setBccList(List<String> bccList) {
    this.bccList = bccList;
  }

  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }
}