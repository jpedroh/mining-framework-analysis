package org.apache.commons.net.ftp.parser;
import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Implementation FTPFileEntryParser and FTPFileListParser for pre MacOS-X Systems.
 *
 * @see org.apache.commons.net.ftp.FTPFileEntryParser FTPFileEntryParser (for usage instructions)
 * @since 3.1
 */
public class MacOsPeterFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
  static final String DEFAULT_DATE_FORMAT = "MMM d yyyy";

  static final String DEFAULT_RECENT_DATE_FORMAT = "MMM d HH:mm";

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
     */
  private static final String REGEX = "([bcdelfmpSs-])" + "(((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-]))((r|-)(w|-)([xsStTL-])))\\+?\\s+" + "(" + "(folder\\s+)" + "|" + "((\\d+)\\s+(\\d+)\\s+)" + ")" + "(\\d+)\\s+" + "((?:\\d+[-/]\\d+[-/]\\d+)|(?:\\S{3}\\s+\\d{1,2})|(?:\\d{1,2}\\s+\\S{3}))\\s+" + "(\\d+(?::\\d+)?)\\s+" + "(\\S*)(\\s*.*)";

  /**
     * The default constructor for a UnixFTPEntryParser object.
     *
     * @throws IllegalArgumentException
     * Thrown if the regular expression is unparseable.  Should not be seen
     * under normal conditions.  It it is seen, this is a sign that
     * <code>REGEX</code> is  not a valid regular expression.
     */
  public MacOsPeterFTPEntryParser() {
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
  public MacOsPeterFTPEntryParser(final FTPClientConfig config) {
    super(REGEX);
    configure(config);
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
      final String hardLinkCount = "0";
      final String usr = null;
      final String grp = null;
      final String filesize = group(20);
      final String datestr = group(21) + " " + group(22);
      String name = group(23);
      final String endtoken = group(24);
      try {
        file.setTimestamp(super.parseTimestamp(datestr));
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
        file.setPermission(access, FTPFile.READ_PERMISSION, (!group(g).equals("-")));
        file.setPermission(access, FTPFile.WRITE_PERMISSION, (!group(g + 1).equals("-")));
        final String execPerm = group(g + 2);
        file.setPermission(access, FTPFile.EXECUTE_PERMISSION, !execPerm.equals("-") && !Character.isUpperCase(execPerm.charAt(0)));
      }
      if (!isDevice) {
        try {
          file.setHardLinkCount(Integer.parseInt(hardLinkCount));
        } catch (final NumberFormatException e) {
        }
      }
      file.setUser(null);
      file.setGroup(null);
      try {
        file.setSize(Long.parseLong(filesize));
      } catch (final NumberFormatException e) {
      }
      if (null == endtoken) {
        file.setName(name);
      } else {
        name += endtoken;
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
      }
      return file;
    }
    return null;
  }
}