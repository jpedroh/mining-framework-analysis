package controllers;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import conf.XCMailrConf;
import etc.HelperUtils;
import filters.AdminFilter;
import filters.SecureFilter;
import filters.WhitelistFilter;
import models.Domain;
import models.MailStatistics;
import models.MailStatisticsKey;
import models.MailTransaction;
import models.PageList;
import models.User;
import models.UserFormData;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.params.PathParam;

/**
 * Handles all Actions for the Administration-Section
 * 
 * @author Patrick Thum, Xceptance Software Technologies GmbH, Germany.
 */
@FilterWith(value = { SecureFilter.class, AdminFilter.class }) @Singleton public class AdminHandler {
  @Inject XCMailrConf xcmConfiguration;

  @Inject Messages messages;

  @Inject MailrMessageSenderFactory mailSender;

  @Inject CachingSessionHandler cachingSessionHandler;

  private static final Pattern PATTERN_DOMAINS = Pattern.compile("^[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,6}");

  /**
     * Shows the Administration-Index-Page.
     * 
     * @param context
     *            the Context of this Request
     * @return the Admin-Index-Page
     */
  public Result showAdmin(Context context) {
    return Results.html();
  }

  /**
     * Shows a list of all {@link models.User users} in the DB.
     * 
     * @param context
     *            the context of this request
     * @return a list of all users
     */
  public Result showUsers(Context context) {
    Result result = Results.html();
    User user = context.getAttribute("user", User.class);
    result.render("uid", user.getId());
    HelperUtils.parseEntryValue(context, xcmConfiguration.APP_DEFAULT_ENTRYNO);
    int entries = Integer.parseInt(context.getSession().get("no"));
    String searchString = context.getParameter("s", "");
    PageList<User> pagedUserList = new PageList<User>(User.findUserLike(searchString), entries);
    result.render("users", pagedUserList);
    if (!searchString.equals("")) {
      result.render("searchValue", searchString);
    }
    return result;
  }

  /**
     * Shows a list of all {@link models.Status status} in the DB.
     * 
     * @param context
     *            the context of this request
     * @return status list
     */
  public Result showSummedTransactions(Context context) {
    return Results.html().render("stats", MailTransaction.getStatusList());
  }

  /**
     * Shows a paginated list of all {@link models.MailTransaction mail-transactions} in the DB.
     * 
     * @param context
     *            the context of this request
     * @return the page to show paginated mail-transactions
     */
  public Result pagedMTX(Context context, @Param(value = "p") int page) {
    HelperUtils.parseEntryValue(context, xcmConfiguration.APP_DEFAULT_ENTRYNO);
    int entries = Integer.parseInt(context.getSession().get("no"));
    page = (page == 0) ? 1 : page;
    PageList<MailTransaction> pagedMailTransactionList = new PageList<MailTransaction>(MailTransaction.getSortedAndLimitedList(xcmConfiguration.MTX_LIMIT), entries);
    return Results.html().render("plist", pagedMailTransactionList).render("curPage", page);
  }

  /**
     * Delete a time-specified number of mail-transactions.
     * 
     * @param time
     *            the time in days (all before will be deleted)
     * @return mail-transactions overview page
     */
  public Result deleteMTXProcess(@PathParam(value = "time") Integer time, Context context) {
    if (time == null) {
      return Results.redirect(context.getContextPath() + "/admin/mtxs");
    }
    if (time == -1) {
      MailTransaction.deleteTxInPeriod(null);
    } else {
      DateTime dt = DateTime.now().minusDays(time);
      MailTransaction.deleteTxInPeriod(dt.getMillis());
    }
    return Results.redirect(context.getContextPath() + "/admin/mtxs");
  }

  /**
     * Activates or deactivates the user with the given ID.
     * 
     * @param userId
     *            ID of a user
     * @param context
     *            the context of this request
     * @return users overview page
     */
  public Result activateUserProcess(@PathParam(value = "id") Long userId, Context context) {
    User executingUser = context.getAttribute("user", User.class);
    if (executingUser.getId() == userId) {
      return Results.redirect(context.getContextPath() + "/admin/users");
    }
    boolean active = User.activate(userId);
    User user = User.getById(userId);
    String from = xcmConfiguration.ADMIN_ADDRESS;
    String host = xcmConfiguration.MB_HOST;
    Optional<String> optLanguage = Optional.of(user.getLanguage());
    String subject = messages.get(active ? "user_Activate_Title" : "user_Deactivate_Title", optLanguage, host).get();
    String content = messages.get(active ? "user_Activate_Message" : "user_Deactivate_Message", optLanguage, user.getForename()).get();
    mailSender.sendMail(from, user.getMail(), content, subject);
    if (!active) {
      cachingSessionHandler.deleteUsersSessions(User.getById(userId));
    }
    return Results.redirect(context.getContextPath() + "/admin/users");
  }

  /**
     * Pro- or demotes the {@link models.User user} with the given ID.
     * 
     * @param userId
     *            ID of the user to pro/demote
     * @param context
     *            the context of this request
     * @return users overview page
     */
  public Result promoteUserProcess(@PathParam(value = "id") Long userId, Context context) {
    User user = context.getAttribute("user", User.class);
    if (user.getId() != userId) {
      User.promote(userId);
      cachingSessionHandler.updateUsersSessions(User.getById(userId));
    }
    return Results.redirect(context.getContextPath() + "/admin/users");
  }

  /**
     * Deletes the {@link models.User user} with the given ID.
     * 
     * @param deleteUserId
     *            the ID of the user to delete
     * @param context
     *            the context of this request
     * @return users overview page
     */
  public Result deleteUserProcess(@PathParam(value = "id") Long deleteUserId, Context context) {
    User user = context.getAttribute("user", User.class);
    if (user.getId() != deleteUserId) {
      cachingSessionHandler.deleteUsersSessions(User.getById(deleteUserId));
      User.delete(deleteUserId);
    }
    return Results.redirect(context.getContextPath() + "/admin/users");
  }

  /**
     * Searches for an {@link models.User user}.
     * 
     * @param context
     *            the context of this request
     * @return found users as JSON array
     */
  public Result jsonUserSearch(Context context) {
    List<User> userList;
    String searchString = context.getParameter("s", "");
    userList = (searchString.equals("")) ? new ArrayList<User>() : User.findUserLike(searchString);
    UserFormData userData;
    List<UserFormData> userDatalist = new ArrayList<UserFormData>();
    for (User currentUser : userList) {
      userData = UserFormData.prepopulate(currentUser);
      userDatalist.add(userData);
    }
    return Results.json().render(userDatalist);
  }

  /**
     * Shows a page that contains a list of all domains allowed for registration.
     * 
     * @param context
     *            the context of this request
     * @return overview of all white-listed domains
     */
  @FilterWith(value = WhitelistFilter.class) public Result showDomainWhitelist(Context context) {
    List<Domain> domainList = Domain.getAll();
    return Results.html().render("domains", domainList);
  }

  /**
     * Displays the Remove-Domain Page to decide whether the admin wants to delete all users to the requested domain or
     * just the domain itself.
     * 
     * @param context
     *            the context of this request
     * @param remDomainId
     *            the ID of the domain
     * @return the removeDomainConfirmation-Page
     */
  @FilterWith(value = WhitelistFilter.class) public Result callRemoveDomain(Context context, @Param(value = "removeDomainsSelection") Long remDomainId) {
    Domain domain = Domain.getById(remDomainId);
    Result result = Results.html().template("/views/AdminHandler/removeDomainConfirmation.ftl.html");
    return result.render("domain", domain);
  }

  /**
     * Handles the action requested in the removeDomainConfirmation.
     * 
     * @param context
     *            the context of this request
     * @param action
     *            the action to do (abort, deleteUsersAndDomain or deleteDomain)
     * @param domainId
     *            the ID of the domain
     * @return overview of all white-listed domains
     */
  @FilterWith(value = WhitelistFilter.class) public Result handleRemoveDomain(Context context, @Param(value = "action") String action, @Param(value = "domainId") long domainId) {
    Result result = Results.redirect(context.getContextPath() + "/admin/whitelist");
    if (StringUtils.isBlank(action)) {
      return result;
    }
    if (action.equals("deleteUsersAndDomain")) {
      Domain domain = Domain.getById(domainId);
      List<User> usersToDelete = User.getUsersOfDomain(domain.getDomainname());
      for (User userToDelete : usersToDelete) {
        cachingSessionHandler.deleteUsersSessions(userToDelete);
        User.delete(userToDelete.getId());
      }
      domain.delete();
    } else {
      if (action.equals("deleteDomain")) {
        Domain.delete(domainId);
      }
    }
    return result;
  }

  /**
     * Adds a domain to the white-list.
     * 
     * @param context
     *            the context of this request
     * @param domainName
     *            the name of the domain to add
     * @return overview of all white-listed domains
     */
  @FilterWith(value = WhitelistFilter.class) public Result addDomain(Context context, @Param(value = "domainName") String domainName) {
    Result result = Results.redirect(context.getContextPath() + "/admin/whitelist");
    if (StringUtils.isBlank(domainName)) {
      context.getFlashScope().error("adminAddDomain_Flash_EmptyField");
      return result;
    }
    if (!PATTERN_DOMAINS.matcher(domainName).matches()) {
      context.getFlashScope().error("adminAddDomain_Flash_InvalidDomain");
      return result;
    }
    if (Domain.exists(domainName)) {
      context.getFlashScope().error("adminAddDomain_Flash_DomainExists");
      return result;
    }
    Domain domain = new Domain(domainName);
    domain.save();
    context.getFlashScope().success("adminAddDomain_Flash_Success");
    return result;
  }

  /**
     * Shows statistics about received emails
     * 
     * @param context
     *            the context of this request
     * @return
     */
  public Result showEmailStatistics(Context context) {
    Result html = Results.html();
    List<Long> dailyDroppedMails = new LinkedList<>();
    List<Long> dailyForwardedMails = new LinkedList<>();
    List<Long> dailyTimestamps = new LinkedList<>();
    processStatisticsData(getStatistics(0, true), dailyDroppedMails, dailyForwardedMails, dailyTimestamps);
    html.render("lastDayTimestamps", dailyTimestamps);
    html.render("lastDayDroppedData", dailyDroppedMails);
    html.render("lastDayForwardedData", dailyForwardedMails);
    List<Long> weeklyDroppedMails = new LinkedList<>();
    List<Long> weeklyForwardedMails = new LinkedList<>();
    List<Long> weeklyTimestamps = new LinkedList<>();
    reduceStatisticsData(4, getStatistics(6, false), weeklyDroppedMails, weeklyForwardedMails, weeklyTimestamps);
    html.render("lastWeekTimestamps", weeklyTimestamps);
    html.render("lastWeekDroppedData", weeklyDroppedMails);
    html.render("lastWeekForwardedData", weeklyForwardedMails);
    HelperUtils.parseEntryValue(context, xcmConfiguration.APP_DEFAULT_ENTRYNO);
    int entriesPerPage;
    try {
      entriesPerPage = Integer.parseInt(context.getSession().get("no"));
    } catch (NumberFormatException e) {
      entriesPerPage = 15;
    }
    List<MailStatistics> todaysDroppedMailSender = getMailSenderList(0);
    PageList<MailStatistics> pagedTodaysDroppedMailSender = new PageList<>(todaysDroppedMailSender, entriesPerPage);
    List<MailStatistics> weeksDroppedMailSender = getMailSenderList(6);
    PageList<MailStatistics> pagedWeeksDroppedMailSender = new PageList<>(weeksDroppedMailSender, entriesPerPage);
    html.render("todaysDroppedSenderTable", pagedTodaysDroppedMailSender);
    html.render("weeksDroppedSenderTable", pagedWeeksDroppedMailSender);
    return html;
  }

  /**
     * Returns a list of email sender domains that were received in the given last N days
     * 
     * @param lastNDays
     *            A positive integer that specifies how many days should be aggregated
     * @return
     */
  private List<MailStatistics> getMailSenderList(int lastNDays) {
    if (lastNDays < 0) {
      lastNDays = 0;
    }
    StringBuilder sql = new StringBuilder();
    sql.append("select ms.FROM_DOMAIN, sum(ms.DROP_COUNT) as \"dropped\", sum(ms.FORWARD_COUNT) as \"forwarded\"");
    sql.append("  from MAIL_STATISTICS ms");
    sql.append(" where ms.DATE >= CURRENT_DATE() - " + lastNDays);
    sql.append(" group by ms.FROM_DOMAIN");
    sql.append(" order by \"dropped\" desc;");
    List<SqlRow> droppedMail = Ebean.createSqlQuery(sql.toString()).findList();
    List<MailStatistics> droppedMailSender = new LinkedList<>();
    droppedMail.forEach((SqlRow row) -> {
      MailStatistics ms = new MailStatistics();
      MailStatisticsKey key = new MailStatisticsKey(null, 0, row.getString("FROM_DOMAIN"), null);
      ms.setKey(key);
      ms.setDropCount(row.getInteger("dropped"));
      ms.setForwardCount(row.getInteger("forwarded"));
      droppedMailSender.add(ms);
    });
    return droppedMailSender;
  }

  /**
     * Function to retrieve email statistics data for the given last n days. In case sliding window is true, then the
     * result will be day overlapping based on the current quarter hour of the day
     * 
     * @param lastNDays
     * @param slidingWindow
     *            boolean value inidicating whether a sliding window (day overlapping) result is desired. If false the
     *            result will only contain full days including the current
     * @return
     */
  private List<SqlRow> getStatistics(int lastNDays, boolean slidingWindow) {
    if (lastNDays < 0) {
      lastNDays = 0;
    }
    if (slidingWindow) {
      lastNDays++;
    }
    String newLine = "\n";
    Ebean.createSqlUpdate("set @currentQuarter = (hour(CURRENT_TIME()) * 4) + minute(CURRENT_TIME()) / 15;").execute();
    StringBuilder sb = new StringBuilder(5000);
    sb.append("select temp.DATE");
    sb.append(", temp.X as QUARTER_HOUR");
    sb.append(", coalesce(sum(ms.DROP_COUNT), 0) as sum_dropped");
    sb.append(", coalesce(sum(ms.FORWARD_COUNT), 0) as sum_forwarded ").append(newLine);
    sb.append("from (");
    sb.append("select date,X ");
    sb.append("from (").append(newLine);
    for (int i = 0; i < lastNDays + 1; i++) {
      if (i > 0) {
        sb.append("union ");
      }
      sb.append("select CURRENT_DATE() - " + i + " as date from dual").append(newLine);
    }
    sb.append(") ").append(newLine);
    sb.append("cross join (select X from system_range(0,95))").append(newLine);
    sb.append(") temp").append(newLine);
    sb.append("left ");
    sb.append("join  MAIL_STATISTICS ms");
    sb.append("  on  ms.DATE = temp.date ").append(newLine);
    sb.append(" and ms.quarter_hour = temp.X").append(newLine);
    if (slidingWindow) {
      sb.append("where ");
      sb.append("(temp.DATE < CURRENT_DATE()");
      sb.append(" and temp.X > @currentQuarter)");
      sb.append(" or (temp.DATE = CURRENT_DATE()");
      sb.append(" and temp.X <= @currentQuarter)").append(newLine);
    }
    sb.append("group by temp.DATE, temp.X").append(newLine);
    sb.append("order by temp.date, temp.X;").append(newLine);
    return Ebean.createSqlQuery(sb.toString()).findList();
  }

  private void processStatisticsData(List<SqlRow> statisticsData, List<Long> droppedMails, List<Long> forwardedMails, List<Long> timestamps) {
    for (SqlRow sqlRow : statisticsData) {
      Date date = sqlRow.getDate("DATE");
      int quarterHour = sqlRow.getInteger("QUARTER_HOUR");
      Timestamp timestamp = new Timestamp(date.getTime() + (quarterHour * 15 * 60 * 1000));
      timestamps.add(timestamp.getTime());
      droppedMails.add(sqlRow.getLong("SUM_DROPPED"));
      forwardedMails.add(sqlRow.getLong("SUM_FORWARDED"));
    }
  }

  /**
     * Reduces a result set by combining consecutive result rows by adding the dropped mail count as well as the
     * forwarded mail count. The date will be taken from the first row of the the subset
     * 
     * @param rowsToCombine
     *            Number of rows to combine in result
     * @param statisticsData
     *            The result set from the database
     * @param outDroppedMails
     *            Out parameter! An empty list of longs
     * @param outForwardedMails
     *            Out parameter! An empty list of longs
     * @param outTimestamps
     *            Out parameter! An empty list of timestamps
     */
  private void reduceStatisticsData(int rowsToCombine, List<SqlRow> statisticsData, List<Long> outDroppedMails, List<Long> outForwardedMails, List<Long> outTimestamps) {
    SqlRow sqlRow;
    for (int i = 0; i < statisticsData.size(); i += rowsToCombine) {
      sqlRow = statisticsData.get(i);
      Date date = sqlRow.getDate("DATE");
      int quarterHour = sqlRow.getInteger("QUARTER_HOUR");
      Timestamp timestamp = new Timestamp(date.getTime() + (quarterHour * 15 * 60 * 1000));
      outTimestamps.add(timestamp.getTime());
      long sumDropped = 0;
      long sumForwarded = 0;
      for (int h = 0; h < rowsToCombine; h++) {
        sqlRow = statisticsData.get(i + h);
        sumDropped += sqlRow.getLong("SUM_DROPPED");
        sumForwarded += sqlRow.getLong("SUM_FORWARDED");
      }
      outDroppedMails.add(sumDropped);
      outForwardedMails.add(sumForwarded);
    }
  }
}