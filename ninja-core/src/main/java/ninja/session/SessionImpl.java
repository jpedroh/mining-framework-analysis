package ninja.session;
import com.google.common.collect.ImmutableMap;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.utils.Clock;
import ninja.utils.CookieDataCodec;
import ninja.utils.CookieEncryption;
import ninja.utils.Crypto;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Inject;

public class SessionImpl implements Session {
  private final static Logger logger = LoggerFactory.getLogger(SessionImpl.class);

  private final Crypto crypto;

  private final CookieEncryption encryption;

  private final Clock time;

  private Long sessionExpireTimeInMs;

  private final Long defaultSessionExpireTimeInMs;

  private final Boolean sessionSendOnlyIfChanged;

  private final Boolean sessionTransferredOverHttpsOnly;

  private final Boolean sessionHttpOnly;

  private final String applicationCookieDomain;

  private final Map<String, String> data = new HashMap<String, String>();

  /** Has cookie been changed => only send new cookie stuff has been changed */
  private boolean sessionDataHasBeenChanged = false;

  private final String sessionCookieName;

  @Inject public SessionImpl(Crypto crypto, CookieEncryption encryption, NinjaProperties ninjaProperties, Clock clock) {
    this.crypto = crypto;
    this.encryption = encryption;
    this.time = clock;
    Integer sessionExpireTimeInSeconds = ninjaProperties.getInteger(NinjaConstant.sessionExpireTimeInSeconds);
    if (sessionExpireTimeInSeconds != null) {
      this.defaultSessionExpireTimeInMs = sessionExpireTimeInSeconds * 1000L;
    } else {
      this.defaultSessionExpireTimeInMs = null;
    }
    this.sessionExpireTimeInMs = defaultSessionExpireTimeInMs;
    this.sessionSendOnlyIfChanged = ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionSendOnlyIfChanged, true);
    this.sessionTransferredOverHttpsOnly = ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionTransferredOverHttpsOnly, true);
    this.sessionHttpOnly = ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionHttpOnly, true);
    this.applicationCookieDomain = ninjaProperties.get(NinjaConstant.applicationCookieDomain);
    String applicationCookiePrefix = ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix);
    this.sessionCookieName = applicationCookiePrefix + ninja.utils.NinjaConstant.SESSION_SUFFIX;
  }

  @Override public void init(Context context) {
    try {
      Cookie cookie = context.getCookie(sessionCookieName);
      if (cookie != null && cookie.getValue() != null && !cookie.getValue().trim().isEmpty()) {
        String value = cookie.getValue();
        String sign = value.substring(0, value.indexOf("-"));
        String payload = value.substring(value.indexOf("-") + 1);
        if (CookieDataCodec.safeEquals(sign, crypto.signHmacSha1(payload))) {
          payload = encryption.decrypt(payload);
          CookieDataCodec.decode(data, payload);
        }
        if (data.containsKey(EXPIRY_TIME_KEY)) {
          Long expiryTime = Long.parseLong(data.get(EXPIRY_TIME_KEY));
          if (expiryTime >= 0) {
            sessionExpireTimeInMs = expiryTime;
          }
        }
        checkExpire();
      }
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      logger.error("Encoding exception - this must not happen", unsupportedEncodingException);
    }
  }

  protected boolean shouldExpire() {
    if (sessionExpireTimeInMs != null) {
      if (!data.containsKey(TIMESTAMP_KEY)) {
        return true;
      }
      Long timestamp = Long.parseLong(data.get(TIMESTAMP_KEY));
      return (timestamp + sessionExpireTimeInMs < time.currentTimeMillis());
    }
    return false;
  }

  @Override public void setExpiryTime(Long expiryTimeMs) {
    if (expiryTimeMs == null) {
      data.remove(EXPIRY_TIME_KEY);
      sessionExpireTimeInMs = defaultSessionExpireTimeInMs;
      sessionDataHasBeenChanged = true;
    } else {
      data.put(EXPIRY_TIME_KEY, "" + expiryTimeMs);
      sessionExpireTimeInMs = expiryTimeMs;
    }
    if (sessionExpireTimeInMs != null) {
      if (!data.containsKey(TIMESTAMP_KEY)) {
        data.put(TIMESTAMP_KEY, "" + time.currentTimeMillis());
      }
      checkExpire();
      sessionDataHasBeenChanged = true;
    }
  }

  private void checkExpire() {
    if (sessionExpireTimeInMs != null) {
      if (shouldExpire()) {
        sessionDataHasBeenChanged = true;
        data.clear();
      } else {
        data.put(TIMESTAMP_KEY, "" + time.currentTimeMillis());
      }
    }
  }

  @Override public String getId() {
    if (!data.containsKey(ID_KEY)) {
      put(ID_KEY, UUID.randomUUID().toString());
    }
    return get(ID_KEY);
  }

  @Override public Map<String, String> getData() {
    return ImmutableMap.copyOf(data);
  }

  @Override public String getAuthenticityToken() {
    if (!data.containsKey(AUTHENTICITY_KEY)) {
      put(AUTHENTICITY_KEY, UUID.randomUUID().toString());
    }
    return get(AUTHENTICITY_KEY);
  }

  @Override public void save(Context context, Result result) {
    if (!sessionDataHasBeenChanged && (sessionExpireTimeInMs == null || sessionSendOnlyIfChanged)) {
      return;
    }
    if (isEmpty()) {
      if (context.hasCookie(sessionCookieName)) {
        Cookie.Builder expiredSessionCookie = Cookie.builder(sessionCookieName, "");
        expiredSessionCookie.setPath(context.getContextPath() + "/");
        expiredSessionCookie.setMaxAge(0);
        result.addCookie(expiredSessionCookie.build());
      }
      return;
    }
    if (sessionExpireTimeInMs != null && !data.containsKey(TIMESTAMP_KEY)) {
      data.put(TIMESTAMP_KEY, Long.toString(System.currentTimeMillis()));
    }
    try {
      String sessionData = CookieDataCodec.encode(data);
      sessionData = encryption.encrypt(sessionData);
      String sign = crypto.signHmacSha1(sessionData);
      Cookie.Builder cookie = Cookie.builder(sessionCookieName, sign + "-" + sessionData);
      cookie.setPath(context.getContextPath() + "/");
      if (applicationCookieDomain != null) {
        cookie.setDomain(applicationCookieDomain);
      }
      if (sessionExpireTimeInMs != null) {
        cookie.setMaxAge((int) (sessionExpireTimeInMs / 1000L));
      }
      if (sessionTransferredOverHttpsOnly != null) {
        cookie.setSecure(sessionTransferredOverHttpsOnly);
      }
      if (sessionHttpOnly != null) {
        cookie.setHttpOnly(sessionHttpOnly);
      }
      result.addCookie(cookie.build());
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      logger.error("Encoding exception - this must not happen", unsupportedEncodingException);
      throw new RuntimeException(unsupportedEncodingException);
    }
  }

  @Override public void put(String key, String value) {
    if (key.contains(":")) {
      throw new IllegalArgumentException("Character \':\' is invalid in a session key.");
    }
    sessionDataHasBeenChanged = true;
    if (value == null) {
      remove(key);
    } else {
      data.put(key, value);
    }
  }

  @Override public String get(String key) {
    return data.get(key);
  }

  @Override public String remove(String key) {
    sessionDataHasBeenChanged = true;
    String result = get(key);
    data.remove(key);
    return result;
  }

  @Override public void clear() {
    sessionDataHasBeenChanged = true;
    data.clear();
  }

  @Override public boolean isEmpty() {
    int itemsToIgnore = 0;
    if (data.containsKey(TIMESTAMP_KEY)) {
      itemsToIgnore++;
    }
    if (data.containsKey(EXPIRY_TIME_KEY)) {
      itemsToIgnore++;
    }
    return (data.isEmpty() || data.size() == itemsToIgnore);
  }
}