package org.telegram.abilitybots.api.bot;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.logging.BotLogger;
import org.telegram.telegrambots.generics.LongPollingBot;
import org.telegram.telegrambots.util.WebhookUtils;
import static org.telegram.abilitybots.api.db.MapDBContext.onlineInstance;

/**
 * The <b>father</b> of all ability bots. Bots that need to utilize abilities need to extend this bot.
 * <p>
 * It's important to note that this bot strictly extends {@link TelegramLongPollingBot}.
 * <p>
 * All bots extending the {@link AbilityBot} get implicit abilities:
 * <ul>
 * <li>/claim - Claims this bot</li>
 * <ul>
 * <li>Sets the user as the {@link Privacy#CREATOR} of the bot</li>
 * <li>Only the user with the ID returned by {@link AbilityBot#creatorId()} can genuinely claim the bot</li>
 * </ul>
 * <li>/report - reports all user-defined commands (abilities)</li>
 * <ul>
 * <li>The same format acceptable by BotFather</li>
 * </ul>
 * <li>/commands - returns a list of all possible bot commands based on the privacy of the requesting user</li>
 * <li>/backup - returns a backup of the bot database</li>
 * <li>/recover - recovers the database</li>
 * <li>/promote <code>@username</code> - promotes user to bot admin</li>
 * <li>/demote <code>@username</code> - demotes bot admin to user</li>
 * <li>/ban <code>@username</code> - bans the user from accessing your bot commands and features</li>
 * <li>/unban <code>@username</code> - lifts the ban from the user</li>
 * </ul>
 * <p>
 * Additional information of the implicit abilities are present in the methods that declare them.
 * <p>
 * The two most important handles in the AbilityBot are the {@link DBContext} <b><code>db</code></b> and the {@link MessageSender} <b><code>sender</code></b>.
 * All bots extending AbilityBot can use both handles in their update consumers.
 *
 * @author Abbas Abou Daya
 */
@SuppressWarnings(value = { "WeakerAccess", "UnusedReturnValue", "ConfusingArgumentToVarargsMethod", "ConstantConditions" }) public abstract class AbilityBot extends BaseAbilityBot implements LongPollingBot {
  protected AbilityBot(String botToken, String botUsername, DBContext db, DefaultBotOptions botOptions) {
    super(botToken, botUsername, db, botOptions);
  }

  protected AbilityBot(String botToken, String botUsername, DBContext db) {
    this(botToken, botUsername, db, new DefaultBotOptions());
  }

  protected AbilityBot(String botToken, String botUsername, DefaultBotOptions botOptions) {
    this(botToken, botUsername, onlineInstance(botUsername), botOptions);
  }

  protected AbilityBot(String botToken, String botUsername) {
    this(botToken, botUsername, onlineInstance(botUsername));
  }

  /**
   * This method contains the stream of actions that are applied on any update.
   * <p>
   * It will correctly handle addition of users into the DB and the execution of abilities and replies.
   *
   * @param update the update received by Telegram's API
   */
  @Override public void onUpdateReceived(Update update) {
    super.onUpdateReceived(update);
  }

  @Override public void clearWebhook() throws TelegramApiRequestException {
    WebhookUtils.clearWebhook(this);
  }


<<<<<<< /usr/src/app/output/rubenlagus/telegrambots/7daebe13188848b2d59fae12af6df41667fe7c8a/telegrambots-abilities/src/main/java/org/telegram/abilitybots/api/bot/AbilityBot.java/left.java
  /**
   * This backup ability returns the object defined by {@link DBContext#backup()} as a message document.
   * <p>
   * This is a high-profile ability and is restricted to the CREATOR only.
   * <p>
   * Usage: <code>/backup</code>
   *
   * @return the ability to back-up the database of the bot
   */
  public Ability backupDB() {
    return builder().name(BACKUP).locality(USER).privacy(CREATOR).input(0).action((ctx) -> {
      File backup = new File("backup.json");
      try (PrintStream printStream = new PrintStream(backup)) {
        printStream.print(db.backup());
        sender.sendDocument(new SendDocument().setDocument(backup).setChatId(ctx.chatId()));
      } catch (FileNotFoundException e) {
        BotLogger.error("Error while fetching backup", TAG, e);
      } catch (TelegramApiException e) {
        BotLogger.error("Error while sending document/backup file", TAG, e);
      }
    }).build();
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.
}