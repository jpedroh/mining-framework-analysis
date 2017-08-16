package com.smartsheet.api.models;

import com.smartsheet.api.models.enums.AccessLevel;
import com.smartsheet.api.models.enums.ShareScope;
import com.smartsheet.api.models.enums.ShareType;

import java.util.Date;

/*
 * #[license]
 * Smartsheet SDK for Java
 * %%
 * Copyright (C) 2014 Smartsheet
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * %[license]
 */

/**
 * Represents a Share Object.
 * @see <a href="http://help.smartsheet.com/customer/portal/articles/520104-sharing-sheets">Sharing Sheets</a>
 */
public class Share extends NamedModel<String> {
	/**
	 * Represents the access level for this specific share.
	 */
	private AccessLevel accessLevel;

	/**
	 * Represents the email for this specific share.
	 */
	private String email;

	/**
	 * Represents the userId if the share is of type {@link ShareType#USER}
	 */
	private Long userId;

	/**
	 * Represents the groupId if the share is of type {@link ShareType#GROUP}
	 */
	private Long groupId;

	/**
	 * Indicates what type of share this is.
	 */
	private ShareType type;

	/**
	 * The scope of this share. One of the following values:
	 *	ITEM: an item-level share (i.e., the specific object to which the Share applies is shared with the user or group)
	 * 	WORKSPACE: a workspace-level share (i.e., the workspace that contains the object to which the Share applies is shared with the user or group)
	 */
	private ShareScope scope;
	
	/**
	 * Time that the share was created.
	 */
	private Date createdAt;
	
	/**
	 * Time that the share was modified.
	 */
	private Date modifiedAt;
	
	/**
	 * Represents the subject of the email that will optionally be sent to notify the recipient.
	 */
	private String subject;

	/**
	 * Represents the message to be included in the body of the email.
	 */
	private String message;

	/**
	 * Represents the flag to indicate whether or not to send a copy of the email to the sharer of the sheet.
	 */
	private Boolean ccMe;

	/**
	 * Gets the subject of the email that will optionally be sent to notify the recipient.
	 *
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject of the email that will optionally be sent to notify the recipient.
	 *
	 * @param subject the subject of the email
	 */
	public Share setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	/**
	 * Gets the message to be included in the body of the email.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message to be included in the body of the email.
	 *
	 * @param message the message
	 */
	public Share setMessage(String message) {
		this.message = message;
		return this;
	}

	/**
	 * Gets the flag to indicate whether or not to send a copy of the email to the sharer
	 *
	 * @return the flag for CC
	 */
	public Boolean isCcMe() {
		return ccMe;
	}

	/**
	 * Sets the flag to indicate whether or not to send a copy of the email to the sharer.
	 *
	 * @param ccMe the flag for CC
	 */
	public Share setCcMe(Boolean ccMe) {
		this.ccMe = ccMe;
		return this;
	}

	/**
	 * Gets the access level for this specific share.
	 *
	 * @return the access level
	 */
	public AccessLevel getAccessLevel() {
		return accessLevel;
	}

	/**
	 * Sets the access level for this specific share.
	 *
	 * @param accessLevel the new access level
	 */
	public Share setAccessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
		return this;
	}

	/**
	 * Gets the email for this specific share.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email for this specific share.
	 *
	 * @param email the new email
	 */
	public Share setEmail(String email) {
		this.email = email;
		return this;
	}

	/**
	 * Get the scope of this share. One of ITEM or WORKSPACE
	 * 
	 * @return scope
	 */
	public ShareScope getScope() {
		return scope;
	}
	
	/**
	 * Set the scope of this share. One of ITEM or WORKSPACE
	 *
	 * @param scope
	 */
	public Share setScope(ShareScope scope) {
		this.scope = scope;
		return this;
	}
	
	/**
	 * Gets the time that the share was created
	 * 
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}
	
	/**
	 * Sets the time that the share was created
	 *
	 * @param createdAt
	 */
	public Share setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}
	
	/**
	 * Gets the time that the share was last modified
	 * 
	 * @return modifiedAt
	 */
	public Date getModifiedAt() {
		return modifiedAt;
	}
	
	/**
	 * Sets the time that the share was last modified
	 *
	 * @param modifiedAt
	 */
	public Share setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
		return this;
	}
	
	/**
	 * A convenience class for creating a {@link Share} with the necessary fields for sharing the sheet to one user.
	 */
	public static class ShareToOneUserBuilder {
		private AccessLevel accessLevel;
		private String email;
		private Long userId;

		/**
		 * Access level for this specific share.
		 *
		 * @param accessLevel the access level
		 * @return the share to one builder
		 */
		public ShareToOneUserBuilder setAccessLevel(AccessLevel accessLevel) {
			this.accessLevel = accessLevel;
			return this;
		}

		/**
		 * Email address for this specific share.
		 *
		 * @param email the email
		 * @return the share to one builder
		 */
		public ShareToOneUserBuilder setEmail(String email) {
			this.email = email;
			return this;
		}
		/**
		 * User Id for this share.
		 *
		 * @param userId the User Id.
		 * @return the share to one builder
		 */
		public ShareToOneUserBuilder setUserId(Long userId) {
			this.userId = userId;
			return this;
		}

		/**
		 * Gets the access level.
		 *
		 * @return the access level
		 */
		public AccessLevel getAccessLevel() {
			return accessLevel;
		}

		/**
		 * Gets the email.
		 *
		 * @return the email
		 */
		public String getEmail() {
			return email;
		}
		/**
		 * Gets the user Id.
		 *
		 * @return the user Id
		 */
		public Long getUserId() {
			return userId;
		}

		/**
		 * Builds the {@link Share} object.
		 *
		 * @return the share
		 */
		public Share build() {
			if(accessLevel == null ||
				email == null && userId == null ||
				email != null && userId != null){
						throw new InstantiationError("You must provide one and only one of emailAddress and userId, and accessLevel is required");
			}

			Share share = new Share();
			share.accessLevel = accessLevel;
			share.email = email;
			share.userId = userId;
			share.type = ShareType.USER;
			return share;
		}
	}

	/**
	 * A convenience class for creating a {@link Share} with the necessary fields for sharing the sheet to one {@link Group}.
	 */
	public static class ShareToOneGroupBuilder {
		private AccessLevel accessLevel;
		private Long groupId;

		/**
		 * Access level for this specific share.
		 *
		 * @param accessLevel the access level
		 * @return the share to one builder
		 */
		public ShareToOneGroupBuilder setAccessLevel(AccessLevel accessLevel) {
			this.accessLevel = accessLevel;
			return this;
		}

		/**
		 * Group Id for this share.
		 *
		 * @param groupId the group Id.
		 * @return the share to one builder
		 */
		public ShareToOneGroupBuilder setUserId(Long groupId) {
			this.groupId = groupId;
			return this;
		}

		/**
		 * Gets the access level.
		 *
		 * @return the access level
		 */
		public AccessLevel getAccessLevel() {
			return accessLevel;
		}
		/**
		 * Gets the user Id.
		 *
		 * @return the user Id
		 */
		public Long getGroupId() {
			return groupId;
		}

		/**
		 * Builds the {@link Share} object.
		 *
		 * @return the share
		 */
		public Share build() {
			if(accessLevel == null || groupId != null){
						throw new InstantiationError("You must provide a groupId and accessLevel");
			}

			Share share = new Share();
			share.accessLevel = accessLevel;
			share.groupId = groupId;
			share.type = ShareType.GROUP;
			return share;
		}
	}
	/**
	 * A convenience class for creating a {@link Share} with the necessary fields to update a specific share.
	 */
	public static class UpdateShareBuilder {
		private AccessLevel accessLevel;
		private String id;

		/**
		 * Access level for this specific share.
		 *
		 * @return the builder
		 */
		public String getShareId() {
			return id;
		}

		public UpdateShareBuilder setShareId(String shareId) {
			this.id = shareId;
			return this;
		}
		/**
		 * Access level for the share.
		 *
		 * @param accessLevel the access level
		 * @return the update share builder
		 */
		public UpdateShareBuilder setAccessLevel(AccessLevel accessLevel) {
			this.accessLevel = accessLevel;
			return this;
		}

		/**
		 * Gets the access level.
		 *
		 * @return the access level
		 */
		public AccessLevel getAccessLevel() {
			return accessLevel;
		}

		/**
		 * Builds the {@link Share} object.
		 *
		 * @return the share
		 */
		public Share build() {
			if(accessLevel == null || id == null){
				throw new InstantiationError("The access level and share id must be specified.");
			}
			
			Share share = new Share();
			share.accessLevel = accessLevel;
			share.setId(id);
			return share;
		}
	}

	/**
	 * A convenience class for creating a {@link Share} with the necessary fields to create a {@link ShareType#USER} {@link Share}.
	 * You must set one and only of of emailAddress and userId.
	 */
	public static class CreateUserShareBuilder {
		private String email;
		private Long userId;
		private AccessLevel accessLevel;
		
		/**
		 * Email address for the {@link ShareType#USER} share.
		 *
		 * @param emailAddress the email address
		 * @return the {@link CreateUserShareBuilder}
		 */
		public CreateUserShareBuilder setEmailAddress(String emailAddress) {
			this.email = emailAddress;
			return this;
		}

		/**
		 * Gets the email address.
		 *
		 * @return the email address
		 */
		public String getEmailAddress() {
			return email;
		}
		/**
		 * User ID for the {@link ShareType#USER} share.
		 *
		 * @param userId user id
		 * @return the update share builder
		 */
		public CreateUserShareBuilder setUserId(Long userId) {
			this.userId = userId;;
			return this;
		}
		
		/**
		 * Gets the user Id.
		 *
		 * @return the email address
		 */
		public Long getUserId() {
			return userId;
		}

		public AccessLevel getAccessLevel() {
			return accessLevel;
		}

		public CreateUserShareBuilder setAccessLevel(AccessLevel accessLevel) {
			this.accessLevel = accessLevel;
			return this;
		}

		/**
		 * Builds the {@link Share} object.
		 *
		 * @return the share
		 */
		public Share build() {
			if(email == null && userId == null ||
			   email != null && userId != null){
				throw new InstantiationError("You must provide one and only one of emailAddress and userId");
			}

//			if (accessLevel == null){
//				throw new InstantiationError("You must provide share access level.");
//			}
			
			Share share = new Share();
			share.userId  = userId;
			share.email = email;
			share.accessLevel = accessLevel;
			return share;
		}
	}
	
	/**
	 * A convenience class for creating a {@link Share} with the necessary fields to create a {@link ShareType#GROUP} {@link Share}.
	 * You must set groupId
	 */
	public static class CreateGroupShareBuilder {
		private Long groupId;
		
		/**
		 * Group ID for the {@link ShareType#GROUP} share.
		 *
		 * @param groupId the group id
		 * @return the update share builder
		 */
		public CreateGroupShareBuilder setGroupId(Long groupId) {
			this.groupId = groupId;;
			return this;
		}
		
		/**
		 * Gets the user Id.
		 *
		 * @return the email address
		 */
		public Long getGroupId() {
			return groupId;
		}

		/**
		 * Builds the {@link Share} object.
		 *
		 * @return the share
		 */
		public Share build() {
			if(groupId == null ){
				throw new InstantiationError("You must provide a groupId");
			}
			
			Share share = new Share();
			share.groupId  = groupId;
			share.type = ShareType.GROUP;
			return share;
		}
	}
	/**
	 * @return the userId, <code>null</code> if is {@link ShareType#GROUP}
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public Share setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	/**
	 * @return the groupId, <code>null</code> if is {@link ShareType#USER}
	 */
	public Long getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public Share setGroupId(Long groupId) {
		this.groupId = groupId;
		return this;
	}

	/**
	 * @return the type
	 */
	public ShareType getType() {
		return type;
	}

	/**
	 * @param type the {@link ShareType} to set
	 */
	public Share setType(ShareType type) {
		this.type = type;
		return this;
	}
}
