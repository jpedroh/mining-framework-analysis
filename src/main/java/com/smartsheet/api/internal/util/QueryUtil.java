package com.smartsheet.api.internal.util;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;

public class QueryUtil {
  public QueryUtil() {
  }

  /**
     * Returns a comma seperated list of items as a string
     * @param list the collecion
     * @param <T> the type
     * @return comma separated string
     */
  public static <T extends java.lang.Object> String generateCommaSeparatedList(Collection<T> list) {
    if (list == null || list.size() == 0) {
      return "";
    }
    StringBuilder result = new StringBuilder();
    for (Object obj : list) {
      result.append(',').append(obj.toString());
    }
    return result.length() == 0 ? "" : result.substring(1);
  }

  public static String generateUrl(String baseUrl, Map<String, Object> parameters) {
    if (baseUrl == null) {
      baseUrl = "";
    }
    return baseUrl + generateQueryString(parameters);
  }

  /**
     * Returns a query string.
     *
     * @param parameters the map of query string keys and values
     * @return the query string
     */
  protected static String generateQueryString(Map<String, Object> parameters) {
    if (parameters == null || parameters.size() == 0) {
      return "";
    }
    StringBuilder result = new StringBuilder();
    try {
      for (Map.Entry<String, Object> entry : parameters.entrySet()) {
        if (entry.getKey() != null && (entry.getValue() != null && !entry.getValue().toString().equals(""))) {
          result.append('&').append(URLEncoder.encode(entry.getKey(), "utf-8")).append("=").append(URLEncoder.encode(entry.getValue().toString(), "utf-8"));
        }
      }
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return result.length() == 0 ? "" : "?" + result.substring(1);
  }
}