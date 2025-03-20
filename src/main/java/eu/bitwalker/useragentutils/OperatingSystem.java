package eu.bitwalker.useragentutils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Enum constants for most common operating systems.
 * @author harald
 */public enum OperatingSystem {
  WINDOWS(Manufacturer.MICROSOFT, null, 1, "Windows", new String[] { "Windows" }, new String[] { "Palm", "ggpht.com" }, DeviceType.COMPUTER, null),
  WINDOWS_10(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 24, "Windows 10", new String[] { "Windows NT 6.4", "Windows NT 10" }, null, DeviceType.COMPUTER, null),
  WINDOWS_81(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 23, "Windows 8.1", new String[] { "Windows NT 6.3" }, null, DeviceType.COMPUTER, null),
  WINDOWS_8(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 22, "Windows 8", new String[] { "Windows NT 6.2" }, new String[] { "Xbox", "Xbox One" }, DeviceType.COMPUTER, null),
  WINDOWS_7(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 21, "Windows 7", new String[] { "Windows NT 6.1" }, new String[] { "Xbox", "Xbox One" }, DeviceType.COMPUTER, null),
  WINDOWS_VISTA(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 20, "Windows Vista", new String[] { "Windows NT 6" }, new String[] { "Xbox", "Xbox One" }, DeviceType.COMPUTER, null),
  WINDOWS_2000(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 15, "Windows 2000", new String[] { "Windows NT 5.0" }, null, DeviceType.COMPUTER, null),
  WINDOWS_XP(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 10, "Windows XP", new String[] { "Windows NT 5" }, new String[] { "ggpht.com" }, DeviceType.COMPUTER, null),
  WINDOWS_PHONE8_1(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 53, "Windows Phone 8.1", new String[] { "Windows Phone 8.1" }, null, DeviceType.MOBILE, null),
  WINDOWS_PHONE8(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 52, "Windows Phone 8", new String[] { "Windows Phone 8" }, null, DeviceType.MOBILE, null),
  WINDOWS_MOBILE7(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 51, "Windows Phone 7", new String[] { "Windows Phone OS 7" }, null, DeviceType.MOBILE, null),
  WINDOWS_MOBILE(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 50, "Windows Mobile", new String[] { "Windows CE" }, null, DeviceType.MOBILE, null),
  WINDOWS_98(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 5, "Windows 98", new String[] { "Windows 98", "Win98" }, new String[] { "Palm" }, DeviceType.COMPUTER, null),
  XBOX_OS(Manufacturer.MICROSOFT, OperatingSystem.WINDOWS, 62, "Xbox OS", new String[] { "Xbox", "Xbox One" }, new String[] {  }, DeviceType.GAME_CONSOLE, null),
  ANDROID(Manufacturer.GOOGLE, null, 0, "Android", new String[] { "Android" }, null, DeviceType.MOBILE, null),
  ANDROID5(Manufacturer.GOOGLE, OperatingSystem.ANDROID, 5, "Android 5.x", new String[] { "Android 5", "Android-5" }, new String[] { "glass" }, DeviceType.MOBILE, null),
  ANDROID5_TABLET(Manufacturer.GOOGLE, OperatingSystem.ANDROID5, 50, "Android 5.x Tablet", new String[] { "Android 5", "Android-5" }, new String[] { "mobile", "glass" }, DeviceType.TABLET, null),
  ANDROID4(Manufacturer.GOOGLE, OperatingSystem.ANDROID, 4, "Android 4.x", new String[] { "Android 4", "Android-4" }, new String[] { "glass" }, DeviceType.MOBILE, null),
  ANDROID4_TABLET(Manufacturer.GOOGLE, OperatingSystem.ANDROID4, 40, "Android 4.x Tablet", new String[] { "Android 4", "Android-4" }, new String[] { "mobile", "glass" }, DeviceType.TABLET, null),
  ANDROID4_WEARABLE(Manufacturer.GOOGLE, OperatingSystem.ANDROID, 400, "Android 4.x", new String[] { "Android 4" }, null, DeviceType.WEARABLE, null),
  ANDROID3_TABLET(Manufacturer.GOOGLE, OperatingSystem.ANDROID, 30, "Android 3.x Tablet", new String[] { "Android 3" }, null, DeviceType.TABLET, null),
  ANDROID2(Manufacturer.GOOGLE, OperatingSystem.ANDROID, 2, "Android 2.x", new String[] { "Android 2" }, null, DeviceType.MOBILE, null),
  ANDROID2_TABLET(Manufacturer.GOOGLE, OperatingSystem.ANDROID2, 20, "Android 2.x Tablet", new String[] { "Kindle Fire", "GT-P1000", "SCH-I800" }, null, DeviceType.TABLET, null),
  ANDROID1(Manufacturer.GOOGLE, OperatingSystem.ANDROID, 1, "Android 1.x", new String[] { "Android 1" }, null, DeviceType.MOBILE, null),
  ANDROID_MOBILE(Manufacturer.GOOGLE, OperatingSystem.ANDROID, 11, "Android Mobile", new String[] { "Mobile" }, null, DeviceType.MOBILE, null),
  ANDROID_TABLET(Manufacturer.GOOGLE, OperatingSystem.ANDROID, 12, "Android Tablet", new String[] { "Tablet" }, null, DeviceType.TABLET, null),
  CHROME_OS(Manufacturer.GOOGLE, null, 1000, "Chrome OS", new String[] { "CrOS" }, null, DeviceType.COMPUTER, null),
  WEBOS(Manufacturer.HP, null, 11, "WebOS", new String[] { "webOS" }, null, DeviceType.MOBILE, null),
  PALM(Manufacturer.HP, null, 10, "PalmOS", new String[] { "Palm" }, null, DeviceType.MOBILE, null),
  MEEGO(Manufacturer.NOKIA, null, 3, "MeeGo", new String[] { "MeeGo" }, null, DeviceType.MOBILE, null),
  IOS(Manufacturer.APPLE, null, 2, "iOS", new String[] { "iPhone OS", "like Mac OS X" }, null, DeviceType.MOBILE, null),
  iOS9_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 90, "iOS 9 (iPhone)", new String[] { "iPhone OS 9" }, null, DeviceType.MOBILE, null),
  iOS8_4_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 49, "iOS 8.4 (iPhone)", new String[] { "iPhone OS 8_4" }, null, DeviceType.MOBILE, null),
  iOS8_3_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 48, "iOS 8.3 (iPhone)", new String[] { "iPhone OS 8_3" }, null, DeviceType.MOBILE, null),
  iOS8_2_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 47, "iOS 8.2 (iPhone)", new String[] { "iPhone OS 8_2" }, null, DeviceType.MOBILE, null),
  iOS8_1_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 46, "iOS 8.1 (iPhone)", new String[] { "iPhone OS 8_1" }, null, DeviceType.MOBILE, null),
  iOS8_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 45, "iOS 8 (iPhone)", new String[] { "iPhone OS 8" }, null, DeviceType.MOBILE, null),
  iOS7_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 44, "iOS 7 (iPhone)", new String[] { "iPhone OS 7" }, null, DeviceType.MOBILE, null),
  iOS6_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 43, "iOS 6 (iPhone)", new String[] { "iPhone OS 6" }, null, DeviceType.MOBILE, null),
  iOS5_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 42, "iOS 5 (iPhone)", new String[] { "iPhone OS 5" }, null, DeviceType.MOBILE, null),
  iOS4_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 41, "iOS 4 (iPhone)", new String[] { "iPhone OS 4" }, null, DeviceType.MOBILE, null),
  MAC_OS_X_IPAD(Manufacturer.APPLE, OperatingSystem.IOS, 50, "Mac OS X (iPad)", new String[] { "iPad" }, null, DeviceType.TABLET, null),
  iOS9_IPAD(Manufacturer.APPLE, OperatingSystem.MAC_OS_X_IPAD, 58, "iOS 9 (iPad)", new String[] { "OS 9" }, null, DeviceType.TABLET, null),
  iOS8_4_IPAD(Manufacturer.APPLE, OperatingSystem.MAC_OS_X_IPAD, 57, "iOS 8.4 (iPad)", new String[] { "OS 8_4" }, null, DeviceType.TABLET, null),
  iOS8_3_IPAD(Manufacturer.APPLE, OperatingSystem.MAC_OS_X_IPAD, 56, "iOS 8.3 (iPad)", new String[] { "OS 8_3" }, null, DeviceType.TABLET, null),
  iOS8_2_IPAD(Manufacturer.APPLE, OperatingSystem.MAC_OS_X_IPAD, 55, "iOS 8.2 (iPad)", new String[] { "OS 8_2" }, null, DeviceType.TABLET, null),
  iOS8_1_IPAD(Manufacturer.APPLE, OperatingSystem.MAC_OS_X_IPAD, 54, "iOS 8.1 (iPad)", new String[] { "OS 8_1" }, null, DeviceType.TABLET, null),
  iOS8_IPAD(Manufacturer.APPLE, OperatingSystem.MAC_OS_X_IPAD, 53, "iOS 8 (iPad)", new String[] { "OS 8_0" }, null, DeviceType.TABLET, null),
  iOS7_IPAD(Manufacturer.APPLE, OperatingSystem.MAC_OS_X_IPAD, 52, "iOS 7 (iPad)", new String[] { "OS 7" }, null, DeviceType.TABLET, null),
  iOS6_IPAD(Manufacturer.APPLE, OperatingSystem.MAC_OS_X_IPAD, 51, "iOS 6 (iPad)", new String[] { "OS 6" }, null, DeviceType.TABLET, null),
  MAC_OS_X_IPHONE(Manufacturer.APPLE, OperatingSystem.IOS, 40, "Mac OS X (iPhone)", new String[] { "iPhone" }, null, DeviceType.MOBILE, null),
  MAC_OS_X_IPOD(Manufacturer.APPLE, OperatingSystem.IOS, 30, "Mac OS X (iPod)", new String[] { "iPod" }, null, DeviceType.MOBILE, null),
  MAC_OS_X(Manufacturer.APPLE, null, 10, "Mac OS X", new String[] { "Mac OS X", "CFNetwork" }, null, DeviceType.COMPUTER, null),
  MAC_OS(Manufacturer.APPLE, null, 1, "Mac OS", new String[] { "Mac" }, null, DeviceType.COMPUTER, null),
  MAEMO(Manufacturer.NOKIA, null, 2, "Maemo", new String[] { "Maemo" }, null, DeviceType.MOBILE, null),
  BADA(Manufacturer.SAMSUNG, null, 2, "Bada", new String[] { "Bada" }, null, DeviceType.MOBILE, null),
  GOOGLE_TV(Manufacturer.GOOGLE, null, 100, "Android (Google TV)", new String[] { "GoogleTV" }, null, DeviceType.DMR, null),
  KINDLE(Manufacturer.AMAZON, null, 1, "Linux (Kindle)", new String[] { "Kindle" }, null, DeviceType.TABLET, null),
  KINDLE3(Manufacturer.AMAZON, OperatingSystem.KINDLE, 30, "Linux (Kindle 3)", new String[] { "Kindle/3" }, null, DeviceType.TABLET, null),
  KINDLE2(Manufacturer.AMAZON, OperatingSystem.KINDLE, 20, "Linux (Kindle 2)", new String[] { "Kindle/2" }, null, DeviceType.TABLET, null),
  LINUX(Manufacturer.OTHER, null, 2, "Linux", new String[] { "Linux", "CamelHttpStream" }, null, DeviceType.COMPUTER, null),
  SYMBIAN(Manufacturer.SYMBIAN, null, 1, "Symbian OS", new String[] { "Symbian", "Series60" }, null, DeviceType.MOBILE, null),
  SYMBIAN9(Manufacturer.SYMBIAN, OperatingSystem.SYMBIAN, 20, "Symbian OS 9.x", new String[] { "SymbianOS/9", "Series60/3" }, null, DeviceType.MOBILE, null),
  SYMBIAN8(Manufacturer.SYMBIAN, OperatingSystem.SYMBIAN, 15, "Symbian OS 8.x", new String[] { "SymbianOS/8", "Series60/2.6", "Series60/2.8" }, null, DeviceType.MOBILE, null),
  SYMBIAN7(Manufacturer.SYMBIAN, OperatingSystem.SYMBIAN, 10, "Symbian OS 7.x", new String[] { "SymbianOS/7" }, null, DeviceType.MOBILE, null),
  SYMBIAN6(Manufacturer.SYMBIAN, OperatingSystem.SYMBIAN, 5, "Symbian OS 6.x", new String[] { "SymbianOS/6" }, null, DeviceType.MOBILE, null),
  SERIES40(Manufacturer.NOKIA, null, 1, "Series 40", new String[] { "Nokia6300" }, null, DeviceType.MOBILE, null),
  SONY_ERICSSON(Manufacturer.SONY_ERICSSON, null, 1, "Sony Ericsson", new String[] { "SonyEricsson" }, null, DeviceType.MOBILE, null),
  SUN_OS(Manufacturer.SUN, null, 1, "SunOS", new String[] { "SunOS" }, null, DeviceType.COMPUTER, null),
  PSP(Manufacturer.SONY, null, 1, "Sony Playstation", new String[] { "Playstation" }, null, DeviceType.GAME_CONSOLE, null),
  WII(Manufacturer.NINTENDO, null, 1, "Nintendo Wii", new String[] { "Wii" }, null, DeviceType.GAME_CONSOLE, null),
  BLACKBERRY(Manufacturer.BLACKBERRY, null, 1, "BlackBerryOS", new String[] { "BlackBerry" }, null, DeviceType.MOBILE, null),
  BLACKBERRY7(Manufacturer.BLACKBERRY, OperatingSystem.BLACKBERRY, 7, "BlackBerry 7", new String[] { "Version/7" }, null, DeviceType.MOBILE, null),
  BLACKBERRY6(Manufacturer.BLACKBERRY, OperatingSystem.BLACKBERRY, 6, "BlackBerry 6", new String[] { "Version/6" }, null, DeviceType.MOBILE, null),
  BLACKBERRY_TABLET(Manufacturer.BLACKBERRY, null, 100, "BlackBerry Tablet OS", new String[] { "RIM Tablet OS" }, null, DeviceType.TABLET, null),
  ROKU(Manufacturer.ROKU, null, 1, "Roku OS", new String[] { "Roku" }, null, DeviceType.DMR, null),
  PROXY(Manufacturer.OTHER, null, 10, "Proxy", new String[] { "ggpht.com" }, null, DeviceType.UNKNOWN, null),
  UNKNOWN_MOBILE(Manufacturer.OTHER, null, 3, "Unknown mobile", new String[] { "Mobile" }, null, DeviceType.MOBILE, null),
  UNKNOWN_TABLET(Manufacturer.OTHER, null, 4, "Unknown tablet", new String[] { "Tablet" }, null, DeviceType.TABLET, null),
  UNKNOWN(Manufacturer.OTHER, null, 1, "Unknown", new String[0], null, DeviceType.UNKNOWN, null)
  ;

  private final short id;

  private final String name;

  private final String[] aliases;

  private final String[] excludeList;

  private final Manufacturer manufacturer;

  private final DeviceType deviceType;

  private final OperatingSystem parent;

  private List<OperatingSystem> children;

  private Pattern versionRegEx;

  private static List<OperatingSystem> topLevelOperatingSystems;

  private OperatingSystem(Manufacturer manufacturer, OperatingSystem parent, int versionId, String name, String[] aliases, String[] exclude, DeviceType deviceType, String versionRegexString) {
    this.manufacturer = manufacturer;
    this.parent = parent;
    this.children = new ArrayList<OperatingSystem>();
    this.id = (short) ((manufacturer.getId() << 8) + (byte) versionId);
    this.name = name;
    this.aliases = Utils.toLowerCase(aliases);
    this.excludeList = Utils.toLowerCase(exclude);
    this.deviceType = deviceType;
    if (versionRegexString != null) {
      this.versionRegEx = Pattern.compile(versionRegexString);
    }
    if (this.parent == null) {
      addTopLevelOperatingSystem(this);
    } else {
      this.parent.children.add(this);
    }
  }

  private static void addTopLevelOperatingSystem(OperatingSystem os) {
    if (topLevelOperatingSystems == null) {
      topLevelOperatingSystems = new ArrayList<OperatingSystem>();
    }
    topLevelOperatingSystems.add(os);
  }

  public short getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Deprecated public boolean isMobileDevice() {
    return deviceType.equals(DeviceType.MOBILE);
  }

  public DeviceType getDeviceType() {
    return deviceType;
  }

  public OperatingSystem getGroup() {
    if (this.parent != null) {
      return parent.getGroup();
    }
    return this;
  }

  /**
	 * Returns the manufacturer of the operating system
	 * @return the manufacturer
	 */
  public Manufacturer getManufacturer() {
    return manufacturer;
  }

  /**
     * Checks if the given user-agent string matches to the operating system. 
     * Only checks for one specific operating system. 
     * @param agentString
     * @return boolean
     */
  public boolean isInUserAgentString(String agentString) {
    if (agentString == null) {
      return false;
    }
    final String agentLowerCaseString = agentString.toLowerCase();
    return isInUserAgentStringLowercase(agentLowerCaseString);
  }

  private boolean isInUserAgentStringLowercase(final String agentLowerCaseString) {
    return Utils.contains(agentLowerCaseString, aliases);
  }

  /**
     * Checks if the given user-agent does not contain one of the tokens which should not match.
     * In most cases there are no excluding tokens, so the impact should be small.
     * @param agentLowerCaseString
     * @return
     */
  private boolean containsExcludeTokenLowercase(final String agentLowerCaseString) {
    return Utils.contains(agentLowerCaseString, excludeList);
  }

  private OperatingSystem checkUserAgentLowercase(String agentStringLowercase) {
    if (this.isInUserAgentStringLowercase(agentStringLowercase)) {
      if (this.children.size() > 0) {
        for (OperatingSystem childOperatingSystem : this.children) {
          OperatingSystem match = childOperatingSystem.checkUserAgentLowercase(agentStringLowercase);
          if (match != null) {
            return match;
          }
        }
      }
      if (!this.containsExcludeTokenLowercase(agentStringLowercase)) {
        return this;
      }
    }
    return null;
  }

  /**
	 * Parses user agent string and returns the best match. 
	 * Returns OperatingSystem.UNKNOWN if there is no match.
	 * @param agentString
	 * @return OperatingSystem
	 */
  public static OperatingSystem parseUserAgentString(String agentString) {
    return parseUserAgentString(agentString, topLevelOperatingSystems);
  }

  public static OperatingSystem parseUserAgentLowercaseString(String agentString) {
    if (agentString == null) {
      return OperatingSystem.UNKNOWN;
    }
    return parseUserAgentLowercaseString(agentString, topLevelOperatingSystems);
  }

  /**
     * Parses the user agent string and returns the best match for the given operating systems. 
     * Returns OperatingSystem.UNKNOWN if there is no match.
     * Be aware that if the order of the provided operating systems is incorrect or the set is too limited it can lead to false matches!
     * @param agentString
     * @return OperatingSystem
     */
  public static OperatingSystem parseUserAgentString(String agentString, List<OperatingSystem> operatingSystems) {
    if (agentString != null) {
      final String agentLowercaseString = agentString.toLowerCase();
      return parseUserAgentLowercaseString(agentLowercaseString, operatingSystems);
    }
    return OperatingSystem.UNKNOWN;
  }

  private static OperatingSystem parseUserAgentLowercaseString(final String agentLowercaseString, List<OperatingSystem> operatingSystems) {
    for (OperatingSystem operatingSystem : operatingSystems) {
      OperatingSystem match = operatingSystem.checkUserAgentLowercase(agentLowercaseString);
      if (match != null) {
        return match;
      }
    }
    return OperatingSystem.UNKNOWN;
  }

  /**
	 * Returns the enum constant of this type with the specified id.
	 * Throws IllegalArgumentException if the value does not exist.
	 * @param id
	 * @return 
	 */
  public static OperatingSystem valueOf(short id) {
    for (OperatingSystem operatingSystem : OperatingSystem.values()) {
      if (operatingSystem.getId() == id) {
        return operatingSystem;
      }
    }
    throw new IllegalArgumentException("No enum const for id " + id);
  }
}