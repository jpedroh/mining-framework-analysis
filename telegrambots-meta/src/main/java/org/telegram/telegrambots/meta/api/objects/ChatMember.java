package org.telegram.telegrambots.meta.api.objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import java.time.Instant;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief This object contains information about one member of the chat.
 * @date 20 of May of 2016
 */
public class ChatMember implements BotApiObject {
  private static final String USER_FIELD = "user";

  private static final String STATUS_FIELD = "status";

  private static final String UNTILDATE_FIELD = "until_date";

  private static final String CANBEEDITED_FIELD = "can_be_edited";

  private static final String CANCHANGEINFORMATION_FIELD = "can_change_information";

  private static final String CANPOSTMESSAGES_FIELD = "can_post_messages";

  private static final String CANEDITMESSAGES_FIELD = "can_edit_messages";

  private static final String CANDELETEMESSAGES_FIELD = "can_delete_messages";

  private static final String CANINVITEUSERS_FIELD = "can_invite_users";

  private static final String CANRESTRICTUSERS_FIELD = "can_restrict_users";

  private static final String CANPINMESSAGES_FIELD = "can_pin_messages";

  private static final String CANPROMOTEMEMBERS_FIELD = "can_promote_members";

  private static final String CANSENDMESSAGES_FIELD = "can_send_messages";

  private static final String CANSENDMEDIAMESSAGES_FIELD = "can_send_media_messages";

  private static final String CANSENDOTHERMESSAGES_FIELD = "can_send_other_messages";

  private static final String CANADDWEBPAGEPREVIEWS_FIELD = "can_add_web_page_previews";

  @JsonProperty(value = USER_FIELD) private User user;

  @JsonProperty(value = STATUS_FIELD) private String status;

  @JsonProperty(value = UNTILDATE_FIELD) private Integer untilDate;

  @JsonProperty(value = CANBEEDITED_FIELD) private Boolean canBeEdited;

  @JsonProperty(value = CANCHANGEINFORMATION_FIELD) private Boolean canChangeInformation;

  @JsonProperty(value = CANPOSTMESSAGES_FIELD) private Boolean canPostMessages;

  @JsonProperty(value = CANEDITMESSAGES_FIELD) private Boolean canEditMessages;

  @JsonProperty(value = CANDELETEMESSAGES_FIELD) private Boolean canDeleteMessages;

  @JsonProperty(value = CANINVITEUSERS_FIELD) private Boolean canInviteUsers;

  @JsonProperty(value = CANRESTRICTUSERS_FIELD) private Boolean canRestrictUsers;

  @JsonProperty(value = CANPINMESSAGES_FIELD) private Boolean canPinMessages;

  @JsonProperty(value = CANPROMOTEMEMBERS_FIELD) private Boolean canPromoteMembers;

  @JsonProperty(value = CANSENDMESSAGES_FIELD) private Boolean canSendMessages;

  @JsonProperty(value = CANSENDMEDIAMESSAGES_FIELD) private Boolean canSendMediaMessages;

  @JsonProperty(value = CANSENDOTHERMESSAGES_FIELD) private Boolean canSendOtherMessages;

  @JsonProperty(value = CANADDWEBPAGEPREVIEWS_FIELD) private Boolean canAddWebPagePreviews;

  public ChatMember() {
    super();
  }

  public User getUser() {
    return user;
  }

  public String getStatus() {
    return status;
  }

  public Integer getUntilDate() {
    return untilDate;
  }

  public Instant getUntilDateAsInstant() {
    if (untilDate == null) {
      return null;
    }
    return Instant.ofEpochSecond(untilDate);
  }

  public Boolean getCanBeEdited() {
    return canBeEdited;
  }

  public Boolean getCanChangeInformation() {
    return canChangeInformation;
  }

  public Boolean getCanPostMessages() {
    return canPostMessages;
  }

  public Boolean getCanEditMessages() {
    return canEditMessages;
  }

  public Boolean getCanDeleteMessages() {
    return canDeleteMessages;
  }

  public Boolean getCanInviteUsers() {
    return canInviteUsers;
  }

  public Boolean getCanRestrictUsers() {
    return canRestrictUsers;
  }

  public Boolean getCanPinMessages() {
    return canPinMessages;
  }

  public Boolean getCanPromoteMembers() {
    return canPromoteMembers;
  }

  public Boolean getCanSendMessages() {
    return canSendMessages;
  }

  public Boolean getCanSendMediaMessages() {
    return canSendMediaMessages;
  }

  public Boolean getCanSendOtherMessages() {
    return canSendOtherMessages;
  }

  public Boolean getCanAddWebPagePreviews() {
    return canAddWebPagePreviews;
  }

  @Override public String toString() {
    return "ChatMember{" + "user=" + user + ", status=\'" + status + '\'' + ", untilDate=" + untilDate + ", canBeEdited=" + canBeEdited + ", canChangeInformation=" + canChangeInformation + ", canPostMessages=" + canPostMessages + ", canEditMessages=" + canEditMessages + ", canDeleteMessages=" + canDeleteMessages + ", canInviteUsers=" + canInviteUsers + ", canRestrictUsers=" + canRestrictUsers + ", canPinMessages=" + canPinMessages + ", canPromoteMembers=" + canPromoteMembers + ", canSendMessages=" + canSendMessages + ", canSendMediaMessages=" + canSendMediaMessages + ", canSendOtherMessages=" + canSendOtherMessages + ", canAddWebPagePreviews=" + canAddWebPagePreviews + '}';
  }
}