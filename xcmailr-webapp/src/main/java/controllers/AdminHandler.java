package controllers;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import io.ebean.DB;
import io.ebean.SqlRow;
import io.ebean.Transaction;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import conf.XCMailrConf;
import etc.HelperUtils;
import filters.AdminFilter;
import filters.SecureFilter;
import filters.WhitelistFilter;
import models.Domain;
import models.MailStatisticsJson;
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
import services.MailrMessageSenderFactory;

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

  private static final Pattern PATTERN_DOMAINS = Pattern.compile("^[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,6}", Pattern.CASE_INSENSITIVE);

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
  public Result pagedMTX(Context context, @Param(value = "p") Optional<Integer> page) {
    HelperUtils.parseEntryValue(context, xcmConfiguration.APP_DEFAULT_ENTRYNO);
    int entries = Integer.parseInt(context.getSession().get("no"));
    int _page = page.orElse(1);
    PageList<MailTransaction> pagedMailTransactionList = new PageList<MailTransaction>(MailTransaction.getSortedAndLimitedList(xcmConfiguration.MTX_LIMIT), entries);
    return Results.html().render("plist", pagedMailTransactionList).render("curPage", _page);
  }

  /**
     * Delete a time-specified number of mail-transactions.
     * 
     * @param time
     *            the time in days (all before will be deleted)
     * @return mail-transactions overview page
     */
  public Result deleteMTXProcess(@PathParam(value = "time") Optional<Integer> time, Context context) {
    final Integer _time = time.orElse(null);
    if (_time != null) {
      if (_time == -1) {
        MailTransaction.deleteTxInPeriod(null);
      } else {
        DateTime dt = DateTime.now().minusDays(_time);
        MailTransaction.deleteTxInPeriod(dt.getMillis());
      }
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
    if (executingUser.getId() != userId) {
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
  @FilterWith(value = WhitelistFilter.class) public Result handleRemoveDomain(Context context, @Param(value = "action") Optional<String> action, @Param(value = "domainId") Long domainId) {
    Result result = Results.redirect(context.getContextPath() + "/admin/whitelist");
    String _action = action.orElse(null);
    if ("deleteUsersAndDomain".equals(_action)) {
      Domain domain = Domain.getById(domainId);
      List<User> usersToDelete = User.getUsersOfDomain(domain.getDomainname());
      for (User userToDelete : usersToDelete) {
        cachingSessionHandler.deleteUsersSessions(userToDelete);
        User.delete(userToDelete.getId());
      }
      domain.delete();
    } else {
      if ("deleteDomain".equals(_action)) {
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
  @FilterWith(value = WhitelistFilter.class) public Result addDomain(Context context, @Param(value = "domainName") Optional<String> domainName) {
    Result result = Results.redirect(context.getContextPath() + "/admin/whitelist");
    String _domainName = domainName.orElse(null);
    if (StringUtils.isBlank(_domainName)) {
      context.getFlashScope().error("adminAddDomain_Flash_EmptyField");
      return result;
    }
    if (!PATTERN_DOMAINS.matcher(_domainName).matches()) {
      context.getFlashScope().error("adminAddDomain_Flash_InvalidDomain");
      return result;
    }
    if (Domain.exists(_domainName)) {
      context.getFlashScope().error("adminAddDomain_Flash_DomainExists");
      return result;
    }
    Domain domain = new Domain(_domainName);
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
  public Result showEmailStatistics(Context context, @Param(value = "dayPage") Optional<Integer> dayPage, @Param(value = "weekPage") Optional<Integer> weekPage, @Param(value = "sortDailyList") Optional<String> sortDailyList, @Param(value = "sortWeeklyList") Optional<String> sortWeeklyList) {
    Result html = Results.html();
    List<Long> dailyDroppedMails = new LinkedList<>();
    List<Long> dailyForwardedMails = new LinkedList<>();
    List<Long> dailyTimestamps = new LinkedList<>();
    reduceStatisticsData(4, getStatistics(0, true), dailyDroppedMails, dailyForwardedMails, dailyTimestamps);
    html.render("lastDayTimestamps", dailyTimestamps);
    html.render("lastDayDroppedData", dailyDroppedMails);
    html.render("lastDayForwardedData", dailyForwardedMails);
    List<Long> weeklyDroppedMails = new LinkedList<>();
    List<Long> weeklyForwardedMails = new LinkedList<>();
    List<Long> weeklyTimestamps = new LinkedList<>();
    reduceStatisticsData(4, getStatistics(xcmConfiguration.MAIL_STATISTICS_MAX_DAYS - 1, false), weeklyDroppedMails, weeklyForwardedMails, weeklyTimestamps);
    html.render("lastWeekTimestamps", weeklyTimestamps);
    html.render("lastWeekDroppedData", weeklyDroppedMails);
    html.render("lastWeekForwardedData", weeklyForwardedMails);
    return html;
  }

  public Result getEmailSenderTablePage(Context context, @Param(value = "scope") String scope, @Param(value = "page") Optional<Integer> page, @Param(value = "offset") Optional<Integer> offset, @Param(value = "limit") Optional<Integer> limit, @Param(value = "sort") Optional<String> sort, @Param(value = "order") Optional<String> order) {
    List<MailStatisticsJson> data = null;
    final int _offset = offset.orElse(0);
    final int _limit = limit.orElse(0);
    final String _sort = sort.orElse(null);
    final String _order = order.orElse(null);
    switch (scope) {
      case "day":
      data = getMailSenderList(0, _sort, _order);
      break;
      case "week":
      data = getMailSenderList(6, _sort, _order);
      break;
      default:
      return Results.badRequest();
    }
    final int total = data.size();
    final List<MailStatisticsJson> rows;
    if (_offset < 0 || _limit <= 0 || total == 0) {
      rows = Collections.emptyList();
    } else {
      rows = data.subList(Math.min(_offset, total), Math.min(_offset + _limit, total));
    }
    return Results.json().render("rows", rows).render("total", total);
  }

  /**
     * Returns a list of email sender domains that were received in the given last N days
     * 
     * @param lastNDays
     *            A positive integer that specifies how many days should be aggregated
     * @return
     */
  private List<MailStatisticsJson> getMailSenderList(int lastNDays, String sort, String order) {
    if (lastNDays < 0) {
      lastNDays = 0;
    }
    StringBuilder sql = new StringBuilder();
    sql.append("select ms.FROM_DOMAIN as \"fromDomain\", sum(ms.DROP_COUNT) as \"droppedCount\", sum(ms.FORWARD_COUNT) as \"forwardedCount\"");
    sql.append("  from MAIL_STATISTICS ms");
    sql.append(" where ms.DATE >= CURRENT_DATE() - " + lastNDays);
    sql.append(" group by ms.FROM_DOMAIN");
    String orderColumn = getOrderColumn(sort);
    String orderBy = getOrderDirection(order);
    sql.append(" order by \"" + orderColumn + "\" " + orderBy);
    List<SqlRow> droppedMail = DB.sqlQuery(sql.toString()).findList();
    List<MailStatisticsJson> droppedMailSender = new LinkedList<>();
    int rowIdx = 0;
    for (final SqlRow row : droppedMail) {
      MailStatisticsJson ms = new MailStatisticsJson();
      ms.id = rowIdx++;
      ms.droppedCount = row.getInteger("droppedCount");
      ms.forwardedCount = row.getInteger("forwardedCount");
      ms.fromDomain = row.getString("fromDomain");
      droppedMailSender.add(ms);
    }
    ;
    return droppedMailSender;
  }

  private String getOrderColumn(String orderBy) {
    String orderColumn = "droppedCount";
    List<String> validColumns = new ArrayList<>();
    validColumns.add("fromDomain");
    validColumns.add("droppedCount");
    validColumns.add("forwardedCount");
    if (validColumns.contains(orderBy)) {
      orderColumn = orderBy;
    }
    return orderColumn;
  }

  private String getOrderDirection(String orderBy) {
    String order = "desc";
    List<String> validOrder = new ArrayList<>();
    validOrder.add("asc");
    validOrder.add("desc");
    if (validOrder.contains(orderBy)) {
      order = orderBy;
    }
    return order;
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
    Transaction transaction = DB.beginTransaction();
    DB.sqlUpdate("set @startingQuarter = ((hour(CURRENT_TIME()) + 1)  * 4)").execute();
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
      sb.append(" and temp.X >= @startingQuarter)");
      sb.append(" or (temp.DATE = CURRENT_DATE()");
      sb.append(" and temp.X < @startingQuarter)").append(newLine);
    }
    sb.append("group by temp.DATE, temp.X").append(newLine);
    sb.append("order by temp.date, temp.X;").append(newLine);
    List<SqlRow> result = DB.sqlQuery(sb.toString()).findList();
    transaction.commit();
    return result;
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