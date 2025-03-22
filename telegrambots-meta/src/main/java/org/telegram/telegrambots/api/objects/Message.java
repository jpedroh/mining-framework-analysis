package org.telegram.telegrambots.api.objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.telegram.telegrambots.api.interfaces.BotApiObject;
import org.telegram.telegrambots.api.objects.games.Game;
import org.telegram.telegrambots.api.objects.payments.Invoice;
import org.telegram.telegrambots.api.objects.payments.SuccessfulPayment;
import java.util.List;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * This object represents a message.
 */
public class Message implements BotApiObject {
  private static final String MESSAGEID_FIELD = "message_id";

  private static final String FROM_FIELD = "from";

  private static final String DATE_FIELD = "date";

  private static final String CHAT_FIELD = "chat";

  private static final String FORWARDFROM_FIELD = "forward_from";

  private static final String FORWARDFROMCHAT_FIELD = "forward_from_chat";

  private static final String FORWARDDATE_FIELD = "forward_date";

  private static final String TEXT_FIELD = "text";

  private static final String ENTITIES_FIELD = "entities";

  private static final String AUDIO_FIELD = "audio";

  private static final String DOCUMENT_FIELD = "document";

  private static final String PHOTO_FIELD = "photo";

  private static final String STICKER_FIELD = "sticker";

  private static final String VIDEO_FIELD = "video";

  private static final String CONTACT_FIELD = "contact";

  private static final String LOCATION_FIELD = "location";

  private static final String VENUE_FIELD = "venue";

  private static final String PINNED_MESSAGE_FIELD = "pinned_message";

  private static final String NEWCHATMEMBERS_FIELD = "new_chat_members";

  private static final String LEFTCHATMEMBER_FIELD = "left_chat_member";

  private static final String NEWCHATTITLE_FIELD = "new_chat_title";

  private static final String NEWCHATPHOTO_FIELD = "new_chat_photo";

  private static final String DELETECHATPHOTO_FIELD = "delete_chat_photo";

  private static final String GROUPCHATCREATED_FIELD = "group_chat_created";

  private static final String REPLYTOMESSAGE_FIELD = "reply_to_message";

  private static final String VOICE_FIELD = "voice";

  private static final String CAPTION_FIELD = "caption";

  private static final String SUPERGROUPCREATED_FIELD = "supergroup_chat_created";

  private static final String CHANNELCHATCREATED_FIELD = "channel_chat_created";

  private static final String MIGRATETOCHAT_FIELD = "migrate_to_chat_id";

  private static final String MIGRATEFROMCHAT_FIELD = "migrate_from_chat_id";

  private static final String EDITDATE_FIELD = "edit_date";

  private static final String GAME_FIELD = "game";

  private static final String FORWARDFROMMESSAGEID_FIELD = "forward_from_message_id";

  private static final String INVOICE_FIELD = "invoice";

  private static final String SUCCESSFUL_PAYMENT_FIELD = "successful_payment";

  private static final String VIDEO_NOTE_FIELD = "video_note";

  @JsonProperty(value = MESSAGEID_FIELD) private Integer messageId;

  @JsonProperty(value = FROM_FIELD) private User from;

  @JsonProperty(value = DATE_FIELD) private Integer date;

  @JsonProperty(value = CHAT_FIELD) private Chat chat;

  @JsonProperty(value = FORWARDFROM_FIELD) private User forwardFrom;

  @JsonProperty(value = FORWARDFROMCHAT_FIELD) private Chat forwardFromChat;

  @JsonProperty(value = FORWARDDATE_FIELD) private Integer forwardDate;

  @JsonProperty(value = TEXT_FIELD) private String text;

  /**
     * Optional. For text messages, special entities like usernames, URLs,
     * bot commands, etc. that appear in the text
     */
  @JsonProperty(value = ENTITIES_FIELD) private List<MessageEntity> entities;

  @JsonProperty(value = AUDIO_FIELD) private Audio audio;

  @JsonProperty(value = DOCUMENT_FIELD) private Document document;

  @JsonProperty(value = PHOTO_FIELD) private List<PhotoSize> photo;

  @JsonProperty(value = STICKER_FIELD) private Sticker sticker;

  @JsonProperty(value = VIDEO_FIELD) private Video video;

  @JsonProperty(value = CONTACT_FIELD) private Contact contact;

  @JsonProperty(value = LOCATION_FIELD) private Location location;

  @JsonProperty(value = VENUE_FIELD) private Venue venue;

  @JsonProperty(value = PINNED_MESSAGE_FIELD) private Message pinnedMessage;

  @JsonProperty(value = NEWCHATMEMBERS_FIELD) private List<User> newChatMembers;

  @JsonProperty(value = LEFTCHATMEMBER_FIELD) private User leftChatMember;

  @JsonProperty(value = NEWCHATTITLE_FIELD) private String newChatTitle;

  @JsonProperty(value = NEWCHATPHOTO_FIELD) private List<PhotoSize> newChatPhoto;

  @JsonProperty(value = DELETECHATPHOTO_FIELD) private Boolean deleteChatPhoto;

  @JsonProperty(value = GROUPCHATCREATED_FIELD) private Boolean groupchatCreated;

  @JsonProperty(value = REPLYTOMESSAGE_FIELD) private Message replyToMessage;

  @JsonProperty(value = VOICE_FIELD) private Voice voice;

  @JsonProperty(value = CAPTION_FIELD) private String caption;

  /**
     * Optional. Service message: the supergroup has been created.
     * This field can‘t be received in a message coming through updates,
     * because bot can’t be a member of a supergroup when it is created.
     * It can only be found in reply_to_message
     * if someone replies to a very first message in a directly created supergroup.
     */
  @JsonProperty(value = SUPERGROUPCREATED_FIELD) private Boolean superGroupCreated;

  /**
     * Optional. Service message: the channel has been created.
     * This field can‘t be received in a message coming through updates,
     * because bot can’t be a member of a channel when it is created.
     * It can only be found in reply_to_message if someone
     * replies to a very first message in a channel.
     */
  @JsonProperty(value = CHANNELCHATCREATED_FIELD) private Boolean channelChatCreated;

  /**
     * Optional. The group has been migrated to a supergroup with the specified identifier.
     * This number may be greater than 32 bits and some programming languages
     * may have difficulty/silent defects in interpreting it.
     * But it smaller than 52 bits, so a signed 64 bit integer or double-precision
     * float type are safe for storing this identifier.
     */
  @JsonProperty(value = MIGRATETOCHAT_FIELD) private Long migrateToChatId;

  /**
     * Optional. The supergroup has been migrated from a group with the specified identifier.
     * This number may be greater than 32 bits and some programming languages
     * may have difficulty/silent defects in interpreting it.
     * But it smaller than 52 bits, so a signed 64 bit integer or double-precision
     * float type are safe for storing this identifier.
     */
  @JsonProperty(value = MIGRATEFROMCHAT_FIELD) private Long migrateFromChatId;

  @JsonProperty(value = EDITDATE_FIELD) private Integer editDate;

  @JsonProperty(value = GAME_FIELD) private Game game;

  @JsonProperty(value = FORWARDFROMMESSAGEID_FIELD) private Integer forwardFromMessageId;

  @JsonProperty(value = INVOICE_FIELD) private Invoice invoice;

  @JsonProperty(value = SUCCESSFUL_PAYMENT_FIELD) private SuccessfulPayment successfulPayment;

  @JsonProperty(value = VIDEO_NOTE_FIELD) private VideoNote videoNote;

  public Message() {
    super();
  }

  public Integer getMessageId() {
    return messageId;
  }

  public User getFrom() {
    return from;
  }

  public Integer getDate() {
    return date;
  }

  public Chat getChat() {
    return chat;
  }

  public User getForwardFrom() {
    return forwardFrom;
  }

  public Integer getForwardDate() {
    return forwardDate;
  }

  public String getText() {
    return text;
  }

  public List<MessageEntity> getEntities() {
    if (entities != null) {
      entities.forEach((x) -> x.computeText(text));
    }
    return entities;
  }

  public Audio getAudio() {
    return audio;
  }

  public Document getDocument() {
    return document;
  }

  public List<PhotoSize> getPhoto() {
    return photo;
  }

  public Sticker getSticker() {
    return sticker;
  }

  public Video getVideo() {
    return video;
  }

  public Contact getContact() {
    return contact;
  }

  public Location getLocation() {
    return location;
  }

  public Venue getVenue() {
    return venue;
  }

  public Message getPinnedMessage() {
    return pinnedMessage;
  }

  public List<User> getNewChatMembers() {
    return newChatMembers;
  }

  public User getLeftChatMember() {
    return leftChatMember;
  }

  public String getNewChatTitle() {
    return newChatTitle;
  }

  public List<PhotoSize> getNewChatPhoto() {
    return newChatPhoto;
  }

  public Boolean getDeleteChatPhoto() {
    return deleteChatPhoto;
  }

  public Boolean getGroupchatCreated() {
    return groupchatCreated;
  }

  public Message getReplyToMessage() {
    return replyToMessage;
  }

  public Voice getVoice() {
    return voice;
  }

  public String getCaption() {
    return caption;
  }

  public Boolean getSuperGroupCreated() {
    return superGroupCreated;
  }

  public Boolean getChannelChatCreated() {
    return channelChatCreated;
  }

  public Long getMigrateToChatId() {
    return migrateToChatId;
  }

  public Long getMigrateFromChatId() {
    return migrateFromChatId;
  }

  public Integer getForwardFromMessageId() {
    return forwardFromMessageId;
  }

  public boolean isGroupMessage() {
    return chat.isGroupChat();
  }

  public boolean isUserMessage() {
    return chat.isUserChat();
  }

  public boolean isChannelMessage() {
    return chat.isChannelChat();
  }

  public boolean isSuperGroupMessage() {
    return chat.isSuperGroupChat();
  }

  public Long getChatId() {
    return chat.getId();
  }

  public boolean hasText() {
    return text != null && !text.isEmpty();
  }

  public boolean isCommand() {
    if (hasText() && entities != null) {
      for (MessageEntity entity : entities) {
        if (entity != null && entity.getOffset() == 0 && EntityType.BOTCOMMAND.equals(entity.getType())) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean hasDocument() {
    return this.document != null;
  }

  public boolean isReply() {
    return this.replyToMessage != null;
  }

  public boolean hasLocation() {
    return location != null;
  }

  public Chat getForwardFromChat() {
    return forwardFromChat;
  }

  public Integer getEditDate() {
    return editDate;
  }

  public Game getGame() {
    return game;
  }

  private boolean hasGame() {
    return game != null;
  }

  public boolean hasEntities() {
    return entities != null && !entities.isEmpty();
  }

  public boolean hasPhoto() {
    return photo != null && !photo.isEmpty();
  }

  public boolean hasInvoice() {
    return invoice != null;
  }

  public boolean hasSuccessfulPayment() {
    return successfulPayment != null;
  }

  public Invoice getInvoice() {
    return invoice;
  }

  public SuccessfulPayment getSuccessfulPayment() {
    return successfulPayment;
  }

  public VideoNote getVideoNote() {
    return videoNote;
  }

  @Override public String toString() {
    return "Message{" + "messageId=" + messageId + ", from=" + from + ", date=" + date + ", chat=" + chat + ", forwardFrom=" + forwardFrom + ", forwardFromChat=" + forwardFromChat + ", forwardDate=" + forwardDate + ", text=\'" + text + '\'' + ", entities=" + entities + ", audio=" + audio + ", document=" + document + ", photo=" + photo + ", sticker=" + sticker + ", video=" + video + ", contact=" + contact + ", location=" + location + ", venue=" + venue + ", pinnedMessage=" + pinnedMessage + ", newChatMembers=" + newChatMembers + ", leftChatMember=" + leftChatMember + ", newChatTitle=\'" + newChatTitle + '\'' + ", newChatPhoto=" + newChatPhoto + ", deleteChatPhoto=" + deleteChatPhoto + ", groupchatCreated=" + groupchatCreated + ", replyToMessage=" + replyToMessage + ", voice=" + voice + ", caption=\'" + caption + '\'' + ", superGroupCreated=" + superGroupCreated + ", channelChatCreated=" + channelChatCreated + ", migrateToChatId=" + migrateToChatId + ", migrateFromChatId=" + migrateFromChatId + ", editDate=" + editDate + ", game=" + game + ", forwardFromMessageId=" + forwardFromMessageId + ", invoice=" + invoice + ", successfulPayment=" + successfulPayment + ", videoNote=" + videoNote + '}';
  }
}