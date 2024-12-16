package net.helpscout.api.model;

import java.util.Date;
import java.util.List;
import net.helpscout.api.cbo.Status;
import net.helpscout.api.model.ref.CustomerRef;
import net.helpscout.api.model.ref.MailboxRef;
import net.helpscout.api.model.ref.PersonRef;
import net.helpscout.api.model.ref.UserRef;
import net.helpscout.api.model.thread.LineItem;


public class Conversation {
<<<<<<< LEFT
	private Long id;
	private Long folderId;
=======
	private int id;
	private String type;
	private int folderId;
>>>>>>> RIGHT
	private boolean isDraft;
	private Long number;
	private Source source;
<<<<<<< LEFT
	private String type;
=======
>>>>>>> RIGHT

	private UserRef owner;
	private MailboxRef mailbox;
	private CustomerRef customer;
	private int threadCount;
	private Status status;
	private String subject;
	private String preview;
	private Date createdAt;
	private Date modifiedAt;
	private String closedAt;
	private UserRef closedBy;

	private PersonRef createdBy;

	private List<String> ccList;
	private List<String> bccList;
	private List<String> tags;

	private List<LineItem> threads;

<<<<<<< LEFT
	public Long getId() {
=======
	public int getId() {
>>>>>>> RIGHT
		return id;
	}

<<<<<<< LEFT
	public void setId(Long id) {
		this.id = id;
	}

	public Long getFolderId() {
=======
	public String getType() {
		return type;
	}

	public int getFolderId() {
>>>>>>> RIGHT
		return folderId;
	}

<<<<<<< LEFT
	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

=======
>>>>>>> RIGHT
	public boolean isDraft() {
		return isDraft;
	}

<<<<<<< LEFT
	public void setDraft(boolean draft) {
		isDraft = draft;
	}

	public Long getNumber() {
=======
	public int getNumber() {
>>>>>>> RIGHT
		return number;
	}

<<<<<<< LEFT
	public void setNumber(Long number) {
		this.number = number;
=======
	public UserRef getOwner() {
		return owner;
>>>>>>> RIGHT
	}

<<<<<<< LEFT
=======
	public MailboxRef getMailbox() {
		return mailbox;
	}

	public CustomerRef getCustomer() {
		return customer;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public Status getStatus() {
		return status;
	}

	public String getSubject() {
		return subject;
	}

	public String getPreview() {
		return preview;
	}

>>>>>>> RIGHT
	public Source getSource() {
		return source;
	}

<<<<<<< LEFT
	public void setSource(Source source) {
		this.source = source;
=======
	public PersonRef getCreatedBy() {
		return createdBy;
>>>>>>> RIGHT
	}

<<<<<<< LEFT
	public String getType() {
		return type;
=======
	public boolean isCreatedByCustomer() {
		return createdBy != null && createdBy instanceof CustomerRef;
>>>>>>> RIGHT
	}

<<<<<<< LEFT
	public void setType(String type) {
		this.type = type;
	}

	public UserRef getOwner() {
		return owner;
	}

	public void setOwner(UserRef owner) {
		this.owner = owner;
	}

	public MailboxRef getMailbox() {
		return mailbox;
	}

	public void setMailbox(MailboxRef mailbox) {
		this.mailbox = mailbox;
	}

	public CustomerRef getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerRef customer) {
		this.customer = customer;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public Date getCreatedAt() {
=======
	public Calendar getCreatedAt() {
>>>>>>> RIGHT
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getClosedAt() {
		return closedAt;
	}

<<<<<<< LEFT
	public void setClosedAt(String closedAt) {
		this.closedAt = closedAt;
	}

=======
>>>>>>> RIGHT
	public UserRef getClosedBy() {
		return closedBy;
	}

<<<<<<< LEFT
	public void setClosedBy(UserRef closedBy) {
		this.closedBy = closedBy;
=======
	public boolean hasCcList() {
		return ccList != null && ccList.size() > 0;
>>>>>>> RIGHT
	}

<<<<<<< LEFT
	public PersonRef getCreatedBy() {
		return createdBy;
	}

	public boolean isCreatedByCustomer() {
		return createdBy != null && createdBy instanceof CustomerRef;
	}

	public void setCreatedBy(PersonRef createdBy) {
		this.createdBy = createdBy;
	}

=======
>>>>>>> RIGHT
	public List<String> getCcList() {
		return ccList;
	}

<<<<<<< LEFT
	public boolean hasCcList() {
		return ccList != null && ccList.size() > 0;
=======
	public boolean hasBccList() {
		return bccList != null && bccList.size() > 0;
>>>>>>> RIGHT
	}

<<<<<<< LEFT
	public void setCcList(List<String> ccList) {
		this.ccList = ccList;
	}

=======
>>>>>>> RIGHT
	public List<String> getBccList() {
		return bccList;
	}

<<<<<<< LEFT
	public boolean hasBccList() {
		return bccList != null && bccList.size() > 0;
=======
	public boolean hasTags() {
		return tags != null && tags.size() > 0;
>>>>>>> RIGHT
	}

<<<<<<< LEFT
	public void setBccList(List<String> bccList) {
		this.bccList = bccList;
	}

=======
>>>>>>> RIGHT
	public List<String> getTags() {
		return tags;
	}

<<<<<<< LEFT
	public boolean hasTags() {
		return tags != null && tags.size() > 0;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<LineItem> getThreads() {
		return threads;
	}

=======
>>>>>>> RIGHT
	public boolean hasThreads() {
		return threads != null && threads.size() > 0;
	}

<<<<<<< LEFT
	public void setThreads(List<LineItem> threads) {
		this.threads = threads;
=======
	public List<LineItem> getThreads() {
		return threads;
>>>>>>> RIGHT
	}
}