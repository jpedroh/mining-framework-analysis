package org.openapitools.codegen.utils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openapitools.codegen.config.GlobalSettings;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
  /**
     * Set the cache size (entry count) of the sanitizedNameCache, camelizedWordsCache and underscoreWords.
     */
  public static final String NAME_CACHE_SIZE_PROPERTY = "org.openapitools.codegen.utils.namecache.cachesize";

  /**
     * Set the cache expiry (in seconds) of the sanitizedNameCache, camelizedWordsCache and underscoreWords.
     */
  public static final String NAME_CACHE_EXPIRY_PROPERTY = "org.openapitools.codegen.utils.namecache.expireafter.seconds";

  private static Cache<Pair<String, Boolean>, String> camelizedWordsCache;

  private static Cache<String, String> underscoreWords;

  static {
    int cacheSize = Integer.parseInt(GlobalSettings.getProperty(NAME_CACHE_SIZE_PROPERTY, "200"));
    int cacheExpiry = Integer.parseInt(GlobalSettings.getProperty(NAME_CACHE_EXPIRY_PROPERTY, "5"));
    camelizedWordsCache = Caffeine.newBuilder().maximumSize(cacheSize).expireAfterAccess(cacheExpiry, TimeUnit.SECONDS).ticker(Ticker.systemTicker()).build();
    underscoreWords = Caffeine.newBuilder().maximumSize(cacheSize).expireAfterAccess(cacheExpiry, TimeUnit.SECONDS).ticker(Ticker.systemTicker()).build();
  }

  /**
     * Underscore the given word.
     * Copied from Twitter elephant bird
     * https://github.com/twitter/elephant-bird/blob/master/core/src/main/java/com/twitter/elephantbird/util/Strings.java
     *
     * @param word The word
     * @return The underscored version of the word
     */
  public static String underscore(final String word) {
    return underscoreWords.get(word, (wordToUnderscore) -> {
      String result;
      String firstPattern = "([A-Z]+)([A-Z][a-z])";
      String secondPattern = "([a-z\\d])([A-Z])";
      String replacementPattern = "$1_$2";
      result = wordToUnderscore.replaceAll("\\.", "/");
      result = result.replaceAll("\\$", "__");
      result = result.replaceAll(firstPattern, replacementPattern);
      result = result.replaceAll(secondPattern, replacementPattern);
      result = result.replace('-', '_');
      result = result.replace(' ', '_');
      result = result.toLowerCase(Locale.ROOT);
      return result;
    });
  }

  /**
     * Dashize the given word.
     *
     * @param word The word
     * @return The dashized version of the word, e.g. "my-name"
     */
  public static String dashize(String word) {
    return underscore(word).replaceAll("[_ ]+", "-");
  }

  /**
     * Camelize name (parameter, property, method, etc) with upper case for first letter
     * copied from Twitter elephant bird
     * https://github.com/twitter/elephant-bird/blob/master/core/src/main/java/com/twitter/elephantbird/util/Strings.java
     *
     * @param word string to be camelize
     * @return camelized string
     */
  public static String camelize(String word) {
    return camelize(word, false);
  }

  private static Pattern camelizeSlashPattern = Pattern.compile("\\/(.?)");

  private static Pattern camelizeUppercasePattern = Pattern.compile("(\\.?)(\\w)([^\\.]*)$");

  private static Pattern camelizeCharsPattern = Pattern.compile("([_ -])(.)");

  /**
     * Camelize name (parameter, property, method, etc)
     *
     * @param inputWord                 string to be camelize
     * @param lowercaseFirstLetter lower case for first letter if set to true
     * @return camelized string
     */
  public static String camelize(final String inputWord, boolean lowercaseFirstLetter) {
    Pair<String, Boolean> key = new ImmutablePair<>(inputWord, lowercaseFirstLetter);
    return camelizedWordsCache.get(key, (pair) -> {
      String word = pair.getKey();
      Boolean lowerFirstLetter = pair.getValue();
      Matcher m = camelizeSlashPattern.matcher(word);
      while (m.find()) {
        word = m.replaceFirst("." + m.group(1));
        m = camelizeSlashPattern.matcher(word);
      }
      String[] parts = word.split("\\.");
      StringBuilder f = new StringBuilder();
      for (String z : parts) {
        if (z.length() > 0) {
          f.append(Character.toUpperCase(z.charAt(0))).append(z.substring(1));
        }
      }
      word = f.toString();
      m = camelizeSlashPattern.matcher(word);
      while (m.find()) {
        word = m.replaceFirst("" + Character.toUpperCase(m.group(1).charAt(0)) + m.group(1).substring(1));
        m = camelizeSlashPattern.matcher(word);
      }
      m = camelizeUppercasePattern.matcher(word);
      if (m.find()) {
        String rep = m.group(1) + m.group(2).toUpperCase(Locale.ROOT) + m.group(3);
        rep = rep.replaceAll("\\$", "\\\\\\$");
        word = m.replaceAll(rep);
      }
      m = camelizeCharsPattern.matcher(word);
      while (m.find()) {
        String original = m.group(2);
        String upperCase = original.toUpperCase(Locale.ROOT);
        if (original.equals(upperCase)) {
          word = word.replaceFirst("[_ -]", "");
        } else {
          word = m.replaceFirst(upperCase);
        }
        m = camelizeCharsPattern.matcher(word);
      }
      if (lowerFirstLetter && word.length() > 0) {
        int i = 0;
        char charAt = word.charAt(i);
        while (i + 1 < word.length() && !((charAt >= 'a' && charAt <= 'z') || (charAt >= 'A' && charAt <= 'Z'))) {
          i = i + 1;
          charAt = word.charAt(i);
        }
        i = i + 1;
        word = word.substring(0, i).toLowerCase(Locale.ROOT) + word.substring(i);
      }
      word = word.replaceAll("_", "");
      return word;
    });
  }

  /**
     * Return the name with escaped characters.
     *
     * @param name the name to be escaped
     * @param replacementMap map of replacement characters for non-allowed characters
     * @param charactersToAllow characters that are not escaped
     * @param appendToReplacement String to append to replaced characters.
     * @return the escaped word
     * <p>
     * throws Runtime exception as word is not escaped properly.
     */
  public static String escape(String name, Map<String, String> replacementMap, List<String> charactersToAllow, String appendToReplacement) {
    String result = name.chars().mapToObj((c) -> {
      String character = "" + (char) c;
      if (charactersToAllow != null && charactersToAllow.contains(character)) {
        return character;
      } else {
        if (replacementMap.containsKey(character)) {
          return replacementMap.get(character) + (appendToReplacement != null ? appendToReplacement : "");
        } else {
          return character;
        }
      }
    }).reduce((c1, c2) -> "" + c1 + c2).orElse(null);
    if (result != null) {
      return result;
    }
    throw new RuntimeException("Word \'" + name + "\' could not be escaped.");
  }
}