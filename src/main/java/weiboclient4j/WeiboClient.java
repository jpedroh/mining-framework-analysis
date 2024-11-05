package weiboclient4j;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.type.TypeReference;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.SinaWeiboApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import weiboclient4j.model.Status;
import weiboclient4j.params.Paging;
import weiboclient4j.params.Parameters;
import static weiboclient4j.utils.JsonUtils.parseJsonObject;
import static weiboclient4j.utils.JsonUtils.readValue;
import static weiboclient4j.utils.StringUtils.isNotBlank;

public class WeiboClient {
  private static final Logger log = Logger.getLogger("weibo_client");

  private static final String BASE_URL = "http://api.t.sina.com.cn/";

  private static final String P_ID = "id";

  private static final String P_IDS = "ids";

  private static final String P_BASE_APP = "base_app";

  private static final String P_STATUS_TYPE = "feature";

  private static final String P_WITH_NEW_STATUS = "with_new_status";

  private static final String P_TYPE = "type";

  private static final String P_LANGUAGE = "language";

  private static final String P_USER_ID = "user_id";

  private static final String P_SCREEN_NAME = "screen_name";

  private static final String P_CURSOR = "cursor";

  private static final String P_CATEGORY = "category";

  private static final String P_WITH_REASON = "with_reason";

  private static final String P_TREND_NAME = "trend_name";

  private OAuthService service;

  private Token accessToken;

  private long userId;

  public WeiboClient(String apiKey, String apiSecret) {
    init(apiKey, apiSecret);
  }

  public WeiboClient(String apiKey, String apiSecret, Token accessToken) {
    init(apiKey, apiSecret);
    setAccessToken(accessToken);
  }

  private void init(String apiKey, String apiSecret) {
    service = new ServiceBuilder().provider(SinaWeiboApi.class).apiKey(apiKey).apiSecret(apiSecret).build();
  }

  WeiboClient() {
  }

  private static final Pattern USER_ID_PATTERN = Pattern.compile("user_id=(\\d+)");

  public void setAccessToken(Token accessToken) {
    this.accessToken = accessToken;
    try {
      if (accessToken.getRawResponse() != null) {
        Matcher matcher = USER_ID_PATTERN.matcher(accessToken.getRawResponse());
        if (matcher.find()) {
          setUserId(Long.parseLong(matcher.group(1)));
        }
      }
    } catch (Exception e) {
    }
  }

  public Token getRequestToken() {
    return service.getRequestToken();
  }

  public String getAuthorizationURL(Token requestToken) {
    return service.getAuthorizationUrl(requestToken);
  }

  public Token getAccessToken(Token requestToken, Verifier verifier) {
    setAccessToken(service.getAccessToken(requestToken, verifier));
    return accessToken;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public long getUserId() {
    if (userId <= 0) {
      try {
        User user = get("account/verify_credentials.json", User.class);
        userId = user.getId();
      } catch (WeiboClientException e) {
        log.log(Level.WARNING, "Failed getting uid from account/verify_credentials.json: " + e.getMessage(), e);
      }
    }
    return userId;
  }

  public User showCurrentUser() throws WeiboClientException {
    assert userId > 0;
    return showUser(userId);
  }

  static final public TypeReference<List<Status>> TYPE_STATUS_LIST = new TypeReference<List<Status>>() { };

  public List<Status> parseStatusList(String json) throws WeiboClientException {
    return parseJsonObject(json, TYPE_STATUS_LIST);
  }

  public List<Status> getPublicStatuses() throws WeiboClientException {
    return getPublicStatuses(Paging.EMPTY, Parameters.create());
  }

  public List<Status> getPublicStatuses(int count) throws WeiboClientException {
    return getPublicStatuses(Paging.create().count(count), Parameters.create());
  }

  public List<Status> getPublicStatuses(int count, boolean baseApp) throws WeiboClientException {
    return getPublicStatuses(Paging.create().count(count), Parameters.create().add(P_BASE_APP, baseApp));
  }

  private List<Status> getPublicStatuses(Paging paging, Parameters params) throws WeiboClientException {
    return get("statuses/public_timeline", TYPE_STATUS_LIST, paging, params);
  }

  public List<Status> getFriendsStatuses() throws WeiboClientException {
    return getFriendsStatuses(Paging.EMPTY);
  }

  public List<Status> getFriendsStatuses(Paging paging) throws WeiboClientException {
    return getFriendsStatuses(paging, false, StatusType.ALL);
  }

  public List<Status> getFriendsStatuses(Paging paging, boolean baseApp, StatusType type) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_BASE_APP, baseApp);
    if (type != null) {
      params.add(P_STATUS_TYPE, type.ordinal());
    }
    return get("statuses/friends_timeline", TYPE_STATUS_LIST, paging, params);
  }

  public List<Status> getUserStatuses(long userId) throws WeiboClientException {
    return getUserStatuses(userId, Paging.EMPTY);
  }

  public List<Status> getUserStatuses(long userId, Paging paging) throws WeiboClientException {
    return getUserStatuses(userId, paging, false, null);
  }

  public List<Status> getUserStatuses(long userId, Paging paging, boolean baseApp, StatusType type) throws WeiboClientException {
    return getUserStatuses(userId, null, paging, baseApp, type);
  }

  public List<Status> getUserStatuses(String screenName) throws WeiboClientException {
    return getUserStatuses(screenName, Paging.EMPTY);
  }

  public List<Status> getUserStatuses(String screenName, Paging paging) throws WeiboClientException {
    return getUserStatuses(screenName, paging, false, null);
  }

  public List<Status> getUserStatuses(String screenName, Paging paging, boolean baseApp, StatusType type) throws WeiboClientException {
    return getUserStatuses(0, screenName, paging, baseApp, type);
  }

  private List<Status> getUserStatuses(long userId, String screenName, Paging paging, boolean baseApp, StatusType type) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_BASE_APP, baseApp);
    if (userId > 0) {
      params.add(P_USER_ID, userId);
    }
    if (isNotBlank(screenName)) {
      params.add(P_SCREEN_NAME, screenName.trim());
    }
    if (type != null) {
      params.add(P_STATUS_TYPE, type.ordinal());
    }
    return get("statuses/user_timeline", TYPE_STATUS_LIST, paging, params);
  }

  public List<Status> getMentionsStatuses() throws WeiboClientException {
    return getMentionsStatuses(Paging.EMPTY);
  }

  public List<Status> getMentionsStatuses(Paging paging) throws WeiboClientException {
    return get("statuses/mentions", TYPE_STATUS_LIST, paging);
  }

  static final public TypeReference<List<Comment>> TYPE_COMMENT_LIST = new TypeReference<List<Comment>>() { };

  public List<Comment> parseCommentList(String json) throws WeiboClientException {
    return parseJsonObject(json, TYPE_COMMENT_LIST);
  }

  public List<Comment> getMyComments() throws WeiboClientException {
    return getMyComments(Paging.EMPTY);
  }

  public List<Comment> getMyComments(Paging paging) throws WeiboClientException {
    return get("statuses/comments_timeline", TYPE_COMMENT_LIST, paging);
  }

  public List<Comment> getCommentsByMe() throws WeiboClientException {
    return getCommentsByMe(Paging.EMPTY);
  }

  public List<Comment> getCommentsByMe(Paging paging) throws WeiboClientException {
    return get("statuses/comments_by_me", TYPE_COMMENT_LIST, paging);
  }

  public List<Comment> getCommentsToMe() throws WeiboClientException {
    return getCommentsToMe(Paging.EMPTY);
  }

  public List<Comment> getCommentsToMe(Paging paging) throws WeiboClientException {
    return get("statuses/comments_to_me", TYPE_COMMENT_LIST, paging);
  }

  public List<Comment> getComments(long statusId) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_ID, statusId);
    return getComments(Paging.EMPTY, params);
  }

  public List<Comment> getComments(long statusId, int count, int page) throws WeiboClientException {
    Paging paging = Paging.create().count(count).page(page);
    Parameters params = Parameters.create().add(P_ID, statusId);
    return getComments(paging, params);
  }

  private List<Comment> getComments(Paging paging, Parameters params) throws WeiboClientException {
    return get("statuses/comments", TYPE_COMMENT_LIST, paging, params);
  }

  static final public TypeReference<List<Count>> TYPE_COUNT_LIST = new TypeReference<List<Count>>() { };

  public List<Count> parseCountList(String json) throws WeiboClientException {
    return parseJsonObject(json, TYPE_COUNT_LIST);
  }

  public List<Count> getCounts(long... ids) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_IDS, ids);
    return get("statuses/counts", TYPE_COUNT_LIST, params);
  }

  public List<Status> getRepostStatuses(long statusId) throws WeiboClientException {
    return getRepostStatuses(statusId, Paging.EMPTY);
  }

  public List<Status> getRepostStatuses(long statusId, Paging paging) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_ID, statusId);
    return get("statuses/repost_timeline", TYPE_STATUS_LIST, paging, params);
  }

  public List<Status> getRepostStatusesByUser(long userId) throws WeiboClientException {
    return getRepostStatusesByUser(userId, Paging.EMPTY);
  }

  public List<Status> getRepostStatusesByUser(long userId, Paging paging) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_ID, userId);
    return get("statuses/repost_by_me", TYPE_STATUS_LIST, paging, params);
  }

  public UnreadCount getUnreadCount() throws WeiboClientException {
    return getUnreadCount(Paging.EMPTY, Parameters.create());
  }

  public UnreadCount getUnreadCount(boolean withNewStatus, long sinceId) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_WITH_NEW_STATUS, withNewStatus);
    Paging paging = Paging.create().sinceId(sinceId);
    return getUnreadCount(paging, params);
  }

  private UnreadCount getUnreadCount(Paging paging, Parameters params) throws WeiboClientException {
    return get("statuses/unread", UnreadCount.class, paging, params);
  }

  public Result resetCount(CounterType type) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_TYPE, type.ordinal() + 1);
    return post("statuses/reset_count", Result.class, params);
  }

  static final public TypeReference<List<Emotion>> TYPE_EMOTION_LIST = new TypeReference<List<Emotion>>() { };

  public List<Emotion> parseEmotionList(String json) throws WeiboClientException {
    return parseJsonObject(json, TYPE_EMOTION_LIST);
  }

  public List<Emotion> getEmotions() throws WeiboClientException {
    return getEmotions(Parameters.create());
  }

  public List<Emotion> getEmotions(EmotionType type, Language language) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_TYPE, type.name().toLowerCase()).add(P_LANGUAGE, language.name().toLowerCase());
    return getEmotions(params);
  }

  private List<Emotion> getEmotions(Parameters params) throws WeiboClientException {
    return get("emotions", TYPE_EMOTION_LIST, params);
  }

  public Status showStatus(long id) throws WeiboClientException {
    return get("statuses/show/" + id, Status.class);
  }

  public User showUser(long userId) throws WeiboClientException {
    return get("users/show", User.class, Parameters.create().add(P_USER_ID, userId));
  }

  public User showUser(String screenName) throws WeiboClientException {
    return get("users/show", User.class, Parameters.create().add(P_SCREEN_NAME, screenName));
  }

  public List<User> getFriends() throws WeiboClientException {
    return getFriends(Paging.EMPTY, Parameters.create());
  }

  public List<User> getFriends(String screenName) throws WeiboClientException {
    return getFriends(Paging.EMPTY, Parameters.create().add(P_SCREEN_NAME, screenName));
  }

  public List<User> getFriends(String screenName, int cursor, int count) throws WeiboClientException {
    Paging paging = Paging.create().count(count);
    Parameters params = Parameters.create().add(P_SCREEN_NAME, screenName).add(P_CURSOR, cursor);
    return getFriends(paging, params);
  }

  public List<User> getFriends(long userId) throws WeiboClientException {
    return getFriends(Paging.EMPTY, Parameters.create().add(P_USER_ID, userId));
  }

  public List<User> getFriends(long userId, int cursor, int count) throws WeiboClientException {
    Paging paging = Paging.create().count(count);
    Parameters params = Parameters.create().add(P_USER_ID, userId).add(P_CURSOR, cursor);
    return getFriends(paging, params);
  }

  private List<User> getFriends(Paging paging, Parameters params) throws WeiboClientException {
    return get("statuses/friends", TYPE_USER_LIST, paging, params);
  }

  public List<User> getFollowers() throws WeiboClientException {
    return getFollowers(Paging.EMPTY, Parameters.create());
  }

  public List<User> getFollowers(long userId) throws WeiboClientException {
    return getFollowers(Paging.EMPTY, Parameters.create().add(P_USER_ID, userId));
  }

  public List<User> getFollowers(long userId, int cursor, int count) throws WeiboClientException {
    Paging paging = Paging.create().count(count);
    Parameters params = Parameters.create().add(P_USER_ID, userId).add(P_CURSOR, cursor);
    return getFollowers(paging, params);
  }

  public List<User> getFollowers(String screenName) throws WeiboClientException {
    return getFollowers(Paging.EMPTY, Parameters.create().add(P_SCREEN_NAME, screenName));
  }

  public List<User> getFollowers(String screenName, int cursor, int count) throws WeiboClientException {
    Paging paging = Paging.create().count(count);
    Parameters params = Parameters.create().add(P_SCREEN_NAME, screenName).add(P_CURSOR, cursor);
    return getFollowers(paging, params);
  }

  private List<User> getFollowers(Paging paging, Parameters params) throws WeiboClientException {
    return get("statuses/followers", TYPE_USER_LIST, paging, params);
  }

  static final public TypeReference<List<User>> TYPE_USER_LIST = new TypeReference<List<User>>() { };

  public List<User> parseUserList(String json) throws WeiboClientException {
    return parseJsonObject(json, TYPE_USER_LIST);
  }

  public List<User> getHotUsers() throws WeiboClientException {
    return getHotUsers(Parameters.create());
  }

  public List<User> getHotUsers(UserCategory category) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_CATEGORY, category.name().toLowerCase());
    return getHotUsers(params);
  }

  private List<User> getHotUsers(Parameters params) throws WeiboClientException {
    return get("users/hot", TYPE_USER_LIST, params);
  }

  public List<User> getSuggestedUsers() throws WeiboClientException {
    return get("users/suggestions", TYPE_USER_LIST);
  }

  static final public TypeReference<List<SuggestedUser>> TYPE_SUGGESTED_USER_LIST = new TypeReference<List<SuggestedUser>>() { };

  public List<SuggestedUser> parseSuggestedUserList(String json) throws WeiboClientException {
    return parseJsonObject(json, TYPE_SUGGESTED_USER_LIST);
  }

  public List<SuggestedUser> getSuggestedUsersWithReason() throws WeiboClientException {
    Parameters params = Parameters.create().add(P_WITH_REASON, 1);
    return get("users/suggestions", TYPE_SUGGESTED_USER_LIST, params);
  }

  static final public TypeReference<List<Trend>> TYPE_TREND_LIST = new TypeReference<List<Trend>>() { };

  public List<Trend> parseTrendList(String json) throws WeiboClientException {
    return parseJsonObject(json, TYPE_TREND_LIST);
  }

  public List<Trend> getTrends(long userId) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_USER_ID, userId);
    return getTrends(Paging.EMPTY, params);
  }

  public List<Trend> getTrends(long userId, int page, int count) throws WeiboClientException {
    Paging paging = Paging.create().page(page).count(count);
    Parameters params = Parameters.create().add(P_USER_ID, userId);
    return getTrends(paging, params);
  }

  private List<Trend> getTrends(Paging paging, Parameters params) throws WeiboClientException {
    return get("trends", TYPE_TREND_LIST, paging, params);
  }

  public List<Status> getTrendStatuses(String trendName) throws WeiboClientException {
    return get("trends/statuses", TYPE_STATUS_LIST, Parameters.create().add(P_TREND_NAME, trendName));
  }

  public GlobalTrendList getTrendsHourly() throws WeiboClientException {
    return getTrendsHourly(Parameters.create());
  }

  public GlobalTrendList getTrendsHourly(boolean baseApp) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_BASE_APP, baseApp ? 1 : 0);
    return getTrendsHourly(params);
  }

  private GlobalTrendList getTrendsHourly(Parameters params) throws WeiboClientException {
    return new GlobalTrendList(getContentAsJsonNode("trends/hourly", Paging.EMPTY, params));
  }

  public GlobalTrendList getTrendsDaily() throws WeiboClientException {
    return getTrendsDaily(Parameters.create());
  }

  public GlobalTrendList getTrendsDaily(boolean baseApp) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_BASE_APP, baseApp ? 1 : 0);
    return getTrendsDaily(params);
  }

  private GlobalTrendList getTrendsDaily(Parameters params) throws WeiboClientException {
    return new GlobalTrendList(getContentAsJsonNode("trends/daily", Paging.EMPTY, params));
  }

  public GlobalTrendList getTrendsWeekly() throws WeiboClientException {
    return getTrendsWeekly(Parameters.create());
  }

  public GlobalTrendList getTrendsWeekly(boolean baseApp) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_BASE_APP, baseApp ? 1 : 0);
    return getTrendsWeekly(params);
  }

  private GlobalTrendList getTrendsWeekly(Parameters params) throws WeiboClientException {
    return new GlobalTrendList(getContentAsJsonNode("trends/weekly", Paging.EMPTY, params));
  }

  public IdList getFriendsIds(long userId, int cursor, int count) throws WeiboClientException {
    Paging paging = Paging.create().count(count);
    Parameters params = Parameters.create().add(P_USER_ID, userId).add(P_CURSOR, cursor);
    return get("friends/ids", IdList.class, paging, params);
  }

  public IdList getFollowerIds(long userId, int cursor, int count) throws WeiboClientException {
    Paging paging = Paging.create().count(count);
    Parameters params = Parameters.create().add(P_USER_ID, userId).add(P_CURSOR, cursor);
    return get("followers/ids", IdList.class, paging, params);
  }

  static final public TypeReference<List<Tag>> TYPE_TAG_LIST = new TypeReference<List<Tag>>() { };

  public List<Tag> parseTagList(String json) throws WeiboClientException {
    return parseJsonObject(json, TYPE_TAG_LIST);
  }

  public List<Tag> getTags(long userId) throws WeiboClientException {
    Parameters params = Parameters.create().add(P_USER_ID, userId);
    return getTags(Paging.EMPTY, params);
  }

  public List<Tag> getTags(long userId, int count, int page) throws WeiboClientException {
    Paging paging = Paging.create().count(count).page(page);
    Parameters params = Parameters.create().add(P_USER_ID, userId);
    return getTags(paging, params);
  }

  private List<Tag> getTags(Paging paging, Parameters params) throws WeiboClientException {
    return Tag.parseTags(getContentAsJsonNode("tags", paging, params));
  }

  public List<Status> getFavoriteStatuses() throws WeiboClientException {
    return getFavoriteStatuses(Paging.EMPTY);
  }

  public List<Status> getFavoriteStatuses(int page) throws WeiboClientException {
    Paging paging = Paging.create().page(page);
    return getFavoriteStatuses(paging);
  }

  private List<Status> getFavoriteStatuses(Paging paging) throws WeiboClientException {
    return get("favorites", TYPE_STATUS_LIST, paging);
  }

  static final public TypeReference<List<Url>> TYPE_URL_LIST = new TypeReference<List<Url>>() { };

  public List<Url> parseUrlList(String json) throws WeiboClientException {
    return parseJsonObject(json, TYPE_URL_LIST);
  }

  public Url expandShortUrl(String shortUrl) throws WeiboClientException {
    List<Url> urls = expandShortUrls(shortUrl);
    if (urls != null && urls.size() > 0) {
      return urls.get(0);
    }
    return null;
  }

  private List<Url> expandShortUrls(String... shortUrls) throws WeiboClientException {
    Parameters params = Parameters.create();
    for (String shortUrl : shortUrls) {
      params.add("url_short", shortUrl);
    }
    return get("short_url/expand", TYPE_URL_LIST, params);
  }

  private Response getContent(Verb verb, String path, Paging paging, Parameters params) {
    OAuthRequest request = new OAuthRequest(verb, BASE_URL + path + ".json");
    params.add(paging);
    Map<String, String> paramMap = params.buildParameters();
    for (String key : paramMap.keySet()) {
      request.addQuerystringParameter(key, paramMap.get(key));
    }
    service.signRequest(accessToken, request);
    return request.send();
  }

  private JsonNode getContentAsJsonNode(String path, Paging paging, Parameters params) throws WeiboClientException {
    Response response = getContent(Verb.GET, path, paging, params);
    try {
      return readValue(response.getBody(), JsonNode.class);
    } catch (IOException e) {
      log.warning("Parse failed: " + response.getBody());
      throw new WeiboClientException(e);
    }
  }

  public <T extends java.lang.Object> T get(String path, Class<T> clazz) throws WeiboClientException {
    return get(path, clazz, Paging.EMPTY, Parameters.create());
  }

  public <T extends java.lang.Object> T get(String path, Class<T> clazz, Parameters params) throws WeiboClientException {
    Response response = getContent(Verb.GET, path, Paging.EMPTY, params);
    return parseJsonObject(response, clazz);
  }

  public <T extends java.lang.Object> T get(String path, Class<T> clazz, Paging paging, Parameters params) throws WeiboClientException {
    Response response = getContent(Verb.GET, path, paging, params);
    return parseJsonObject(response, clazz);
  }

  public <T extends java.lang.Object> List<T> get(String path, TypeReference<List<T>> type) throws WeiboClientException {
    return get(path, type, Paging.EMPTY, Parameters.create());
  }

  public <T extends java.lang.Object> List<T> get(String path, TypeReference<List<T>> type, Paging paging) throws WeiboClientException {
    return get(path, type, paging, Parameters.create());
  }

  public <T extends java.lang.Object> List<T> get(String path, TypeReference<List<T>> type, Parameters params) throws WeiboClientException {
    return get(path, type, Paging.EMPTY, params);
  }

  public <T extends java.lang.Object> List<T> get(String path, TypeReference<List<T>> type, Paging paging, Parameters params) throws WeiboClientException {
    Response response = getContent(Verb.GET, path, paging, params);
    return parseJsonObject(response, type);
  }

  public <T extends java.lang.Object> T post(String path, Class<T> clazz) throws WeiboClientException {
    return post(path, clazz, Paging.EMPTY, Parameters.create());
  }

  public <T extends java.lang.Object> T post(String path, Class<T> clazz, Parameters params) throws WeiboClientException {
    Response response = getContent(Verb.POST, path, Paging.EMPTY, params);
    return parseJsonObject(response, clazz);
  }

  public <T extends java.lang.Object> T post(String path, Class<T> clazz, Paging paging, Parameters params) throws WeiboClientException {
    Response response = getContent(Verb.POST, path, paging, params);
    return parseJsonObject(response, clazz);
  }

  public <T extends java.lang.Object> List<T> post(String path, TypeReference<List<T>> type, Paging paging) throws WeiboClientException {
    return post(path, type, paging, Parameters.create());
  }

  public <T extends java.lang.Object> List<T> post(String path, TypeReference<List<T>> type, Parameters params) throws WeiboClientException {
    return post(path, type, Paging.EMPTY, params);
  }

  public <T extends java.lang.Object> List<T> post(String path, TypeReference<List<T>> type, Paging paging, Parameters params) throws WeiboClientException {
    Response response = getContent(Verb.POST, path, paging, params);
    return parseJsonObject(response, type);
  }
}