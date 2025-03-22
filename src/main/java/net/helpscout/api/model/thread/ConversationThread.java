package net.helpscout.api.model.thread;
import net.helpscout.api.cbo.Status;
import net.helpscout.api.cbo.ThreadState;
import net.helpscout.api.cbo.ThreadType;
import net.helpscout.api.model.Attachment;
import net.helpscout.api.model.ref.MailboxRef;
import net.helpscout.api.model.ref.PersonRef;
import net.helpscout.api.model.ref.UserRef;
import java.util.Date;
import java.util.List;

public interface ConversationThread {
  public Long getId();

  public 
<<<<<<< /usr/src/app/output/helpscout/helpscout-api-java/7dde5bbe5d07ba446e8d8b7b942d378b40f2b5c3/src/main/java/net/helpscout/api/model/thread/ConversationThread.java/left.java
  String
=======
  ThreadType
>>>>>>> /usr/src/app/output/helpscout/helpscout-api-java/7dde5bbe5d07ba446e8d8b7b942d378b40f2b5c3/src/main/java/net/helpscout/api/model/thread/ConversationThread.java/right.java
   getType();

  public boolean isPublished();

  public boolean isDraft();

  public boolean isHeldForReview();

  public boolean hasAttachments();

  public ThreadState getState();

  public String getBody();

  public List<String> getToList();

  public List<String> getCcList();

  public List<String> getBccList();

  public List<Attachment> getAttachments();

  public boolean isAssigned();

  public boolean isActive();

  public boolean isPending();

  public boolean isClosed();

  public boolean isSpam();

  public boolean isStatusChange();

  public UserRef getAssignedTo();

  public Status getStatus();

  public PersonRef getCreatedBy();

  public Date getCreatedAt();

  public MailboxRef getFromMailbox();

  public void setId(Long id);

  public void setType(
<<<<<<< /usr/src/app/output/helpscout/helpscout-api-java/7dde5bbe5d07ba446e8d8b7b942d378b40f2b5c3/src/main/java/net/helpscout/api/model/thread/ConversationThread.java/left.java
  String
=======
  ThreadType
>>>>>>> /usr/src/app/output/helpscout/helpscout-api-java/7dde5bbe5d07ba446e8d8b7b942d378b40f2b5c3/src/main/java/net/helpscout/api/model/thread/ConversationThread.java/right.java
   type);

  public void setState(ThreadState state);

  public void setStatus(Status status);

  public void setBody(String body);

  public void setToList(List<String> toList);

  public void setCcList(List<String> ccList);

  public void setBccList(List<String> bccList);

  public void setAttachments(List<Attachment> attachments);

  public void setAssignedTo(UserRef assignedTo);

  public void setCreatedBy(PersonRef person);
}