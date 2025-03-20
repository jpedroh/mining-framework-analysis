package org.apache.commons.net.ftp.parser;
import java.text.ParseException;
import java.util.List;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Implementation FTPFileEntryParser and FTPFileListParser for standard
 * Unix Systems.
 *
 * This class is based on the logic of Daniel Savarese's
 * DefaultFTPListParser, but adapted to use regular expressions and to fit the
 * new FTPFileEntryParser interface.
 * @see org.apache.commons.net.ftp.FTPFileEntryParser FTPFileEntryParser (for usage instructions)
 */
public class UnixFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
  static final String DEFAULT_DATE_FORMAT = "MMM d yyyy";

  static final String DEFAULT_RECENT_DATE_FORMAT = "MMM d HH:mm";

  static final String NUMERIC_DATE_FORMAT = "yyyy-MM-dd HH:mm";

  private static final String JA_MONTH = "\u6708";

  private static final String JA_DAY = "\u65e5";

  private static final String JA_YEAR = "\u5e74";

  private static final String DEFAULT_DATE_FORMAT_JA = "M\'" + JA_MONTH + "\' d\'" + JA_DAY + "\' yyyy\'" + JA_YEAR + "\'";

  private static final String DEFAULT_RECENT_DATE_FORMAT_JA = "M\'" + JA_MONTH + "\' d\'" + JA_DAY + "\' HH:mm";

  /**
     * Some Linux distributions are now shipping an FTP server which formats
     * file listing dates in an all-numeric format:
     * <code>"yyyy-MM-dd HH:mm</code>.
     * This is a very welcome development,  and hopefully it will soon become
     * the standard.  However, since it is so new, for now, and possibly
     * forever, we merely accomodate it, but do not make it the default.
     * <p>
     * For now end users may specify this format only via
     * <code>UnixFTPEntryParser(FTPClientConfig)</code>.
     * Steve Cohen - 2005-04-17
     */
  public static final FTPClientConfig NUMERIC_DATE_CONFIG = new FTPClientConfig(FTPClientConfig.SYST_UNIX, NUMERIC_DATE_FORMAT, null);

  /**
     * this is the regular expression used by this parser.
     *
     * Permissions:
     *    r   the file is readable
     *    w   the file is writable
     *    x   the file is executable
     *    -   the indicated permission is not granted
     *    L   mandatory locking occurs during access (the set-group-ID bit is
     *        on and the group execution bit is off)
     *    s   the set-user-ID or set-group-ID bit is on, and the corresponding
     *        user or group execution bit is also on
     *    S   undefined bit-state (the set-user-ID bit is on and the user
     *        execution bit is off)
     *    t   the 1000 (octal) bit, or sticky bit, is on [see chmod(1)], and
     *        execution is on
     *    T   the 1000 bit is turned on, and execution is off (undefined bit-
     *        state)
     *    e   z/OS external link bit
     *    Final letter may be appended:
     *    +   file has extended security attributes (e.g. ACL)
     *    Note: local listings on MacOSX also use '@';
     *    this is not allowed for here as does not appear to be shown by FTP servers
     *    {@code @}   file has extended attributes
     */
  private static final String REGEX = "([bcdelfmpSs-])" + "(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?" + "\\s*" + "(\\d+)" + "\\s+" + "(?:(\\S+(?:\\s\\S+)*?)\\s+)?" + "(?:(\\S+(?:\\s\\S+)*)\\s+)?" + "(\\d+(?:,\\s*\\d+)?)" + "\\s+" + "(" + "(?:\\d+[-/]\\d+[-/]\\d+)" + "|(?:\\S{3}\\s+\\d{1,2})" + "|(?:\\d{1,2}\\s+\\S{3})" + "|(?:\\d{1,2}" + JA_MONTH + "\\s+\\d{1,2}" + JA_DAY + ")" + ")" + "\\s+" + "((?:\\d+(?::\\d+)?)|(?:\\d{4}" + JA_YEAR + "))" + "\\s" + "(.*)";

  final boolean trimLeadingSpaces;

  /**
     * The default constructor for a UnixFTPEntryParser object.
     *
     * @throws IllegalArgumentException
     * Thrown if the regular expression is unparseable.  Should not be seen
     * under normal conditions.  It it is seen, this is a sign that
     * <code>REGEX</code> is  not a valid regular expression.
     */
  public UnixFTPEntryParser() {
    this(null);
  }

  /**
     * This constructor allows the creation of a UnixFTPEntryParser object with
     * something other than the default configuration.
     *
     * @param config The {@link FTPClientConfig configuration} object used to
     * configure this parser.
     * @throws IllegalArgumentException
     * Thrown if the regular expression is unparseable.  Should not be seen
     * under normal conditions.  It it is seen, this is a sign that
     * <code>REGEX</code> is  not a valid regular expression.
     * @since 1.4
     */
  public UnixFTPEntryParser(final FTPClientConfig config) {
    this(config, false);
  }

  /**
     * This constructor allows the creation of a UnixFTPEntryParser object with
     * something other than the default configuration.
     *
     * @param config The {@link FTPClientConfig configuration} object used to
     * configure this parser.
     * @param trimLeadingSpaces if {@code true}, trim leading spaces from file names
     * @throws IllegalArgumentException
     * Thrown if the regular expression is unparseable.  Should not be seen
     * under normal conditions.  It it is seen, this is a sign that
     * <code>REGEX</code> is  not a valid regular expression.
     * @since 3.4
     */
  public UnixFTPEntryParser(final FTPClientConfig config, final boolean trimLeadingSpaces) {
    super(REGEX);
    configure(config);
    this.trimLeadingSpaces = trimLeadingSpaces;
  }

  /**
     * Defines a default configuration to be used when this class is
     * instantiated without a {@link  FTPClientConfig  FTPClientConfig}
     * parameter being specified.
     * @return the default configuration for this parser.
     */
  @Override protected FTPClientConfig getDefaultConfiguration() {
    return new FTPClientConfig(FTPClientConfig.SYST_UNIX, DEFAULT_DATE_FORMAT, DEFAULT_RECENT_DATE_FORMAT);
  }

  /**
     * Parses a line of a unix (standard) FTP server file listing and converts
     * it into a usable format in the form of an <code> FTPFile </code>
     * instance.  If the file listing line doesn't describe a file,
     * <code> null </code> is returned, otherwise a <code> FTPFile </code>
     * instance representing the files in the directory is returned.
     *
     * @param entry A line of text from the file listing
     * @return An FTPFile instance corresponding to the supplied entry
     */
  @Override public FTPFile parseFTPEntry(final String entry) {
    final FTPFile file = new FTPFile();
    file.setRawListing(entry);
    final int type;
    boolean isDevice = false;
    if (matches(entry)) {
      final String typeStr = group(1);
      final String hardLinkCount = group(15);
      final String usr = group(16);
      final String grp = group(17);
      final String filesize = group(18);
      final String datestr = group(19) + " " + group(20);
      String name = group(21);
      if (trimLeadingSpaces) {
        name = name.replaceFirst("^\\s+", "");
      }
      try {
        if (group(19).contains(JA_MONTH)) {
          final FTPTimestampParserImpl jaParser = new FTPTimestampParserImpl();
          jaParser.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX, DEFAULT_DATE_FORMAT_JA, DEFAULT_RECENT_DATE_FORMAT_JA));
          file.setTimestamp(jaParser.parseTimestamp(datestr));
        } else {
          file.setTimestamp(super.parseTimestamp(datestr));
        }
      } catch (final ParseException e) {
      }
      switch (typeStr.charAt(0)) {
        case 'd':
        type = FTPFile.DIRECTORY_TYPE;
        break;
        case 'e':
        type = FTPFile.SYMBOLIC_LINK_TYPE;
        break;
        case 'l':
        type = FTPFile.SYMBOLIC_LINK_TYPE;
        break;
        case 'b':
        case 'c':
        isDevice = true;
        type = FTPFile.FILE_TYPE;
        break;
        case 'f':
        case '-':
        type = FTPFile.FILE_TYPE;
        break;
        default:
        type = FTPFile.UNKNOWN_TYPE;
      }
      file.setType(type);
      int g = 4;
      for (int access = 0; access < 3; access++, g += 4) {
        file.setPermission(access, FTPFile.READ_PERMISSION, !group(g).equals("-"));
        file.setPermission(access, FTPFile.WRITE_PERMISSION, !group(g + 1).equals("-"));
        final String execPerm = group(g + 2);
        file.setPermission(access, FTPFile.EXECUTE_PERMISSION, !execPerm.equals("-") && !Character.isUpperCase(execPerm.charAt(0)));
      }
      if (!isDevice) {
        try {
          file.setHardLinkCount(Integer.parseInt(hardLinkCount));
        } catch (final NumberFormatException e) {
        }
      }
      file.setUser(usr);
      file.setGroup(grp);
      try {
        file.setSize(Long.parseLong(filesize));
      } catch (final NumberFormatException e) {
      }
      if (type == FTPFile.SYMBOLIC_LINK_TYPE) {
        final int end = name.indexOf(" -> ");
        if (end == -1) {
          file.setName(name);
        } else {
          file.setName(name.substring(0, end));
          file.setLink(name.substring(end + 4));
        }
      } else {
        file.setName(name);
      }
      return file;
    }
    return null;
  }

  /**
     * Preparse the list to discard "total nnn" lines
     */
  @Override public List<String> preParse(final List<String> original) {
    original.removeIf((entry) -> entry.matches("^total \\d+$"));
    return original;
  }
}