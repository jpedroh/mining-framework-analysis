package org.fusesource.jansi.internal;
import static org.fusesource.hawtjni.runtime.FieldFlag.CONSTANT;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_INITIALIZER;
import static org.fusesource.hawtjni.runtime.ArgFlag.*;
import org.fusesource.hawtjni.runtime.ArgFlag;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.JniClass;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;

/**
 * Interface to access some low level POSIX functions.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@JniClass public class CLibrary {
  private static final Library LIBRARY = new Library("jansi", CLibrary.class);

  static {
    LIBRARY.load();
    init();
  }

  @JniMethod(flags = { CONSTANT_INITIALIZER }) private static native void init();

  @JniField(flags = { CONSTANT }, conditional = "defined(STDIN_FILENO)") public static int STDIN_FILENO;

  @JniField(flags = { CONSTANT }, conditional = "defined(STDOUT_FILENO)") public static int STDOUT_FILENO;

  @JniField(flags = { CONSTANT }, conditional = "defined(STDERR_FILENO)") public static int STDERR_FILENO;

  @JniField(flags = { CONSTANT }, accessor = "1", conditional = "defined(HAVE_ISATTY)") public static boolean HAVE_ISATTY;

  @JniMethod(conditional = "defined(HAVE_ISATTY)") public static native int isatty(@JniArg int fd);

  @JniMethod(conditional = "FALSE") public static native String ttyname(@JniArg int filedes);

  @JniMethod(conditional = "defined(HAVE_OPENPTY)") public static native int openpty(@JniArg(cast = "int *", flags = { NO_IN }) int[] amaster, @JniArg(cast = "int *", flags = { NO_IN }) int[] aslave, @JniArg(cast = "char *", flags = { NO_IN }) byte[] name, @JniArg(cast = "struct termios *", flags = { NO_OUT }) Termios termios, @JniArg(cast = "struct winsize *", flags = { NO_OUT }) WinSize winsize);

  @JniMethod(conditional = "defined(HAVE_TCGETATTR)") public static native int tcgetattr(@JniArg int filedes, @JniArg(cast = "struct termios *", flags = { NO_IN }) Termios termios);

  @JniMethod(conditional = "defined(HAVE_TCSETATTR)") public static native int tcsetattr(@JniArg int filedes, @JniArg int optional_actions, @JniArg(cast = "struct termios *", flags = { NO_OUT }) Termios termios);

  @JniField(flags = { CONSTANT }, conditional = "defined(TCSANOW)") public static int TCSANOW;

  @JniField(flags = { CONSTANT }, conditional = "defined(TCSADRAIN)") public static int TCSADRAIN;

  @JniField(flags = { CONSTANT }, conditional = "defined(TCSAFLUSH)") public static int TCSAFLUSH;

  @JniField(flags = { CONSTANT }, conditional = "defined(TIOCGETA)") public static long TIOCGETA;

  @JniField(flags = { CONSTANT }, conditional = "defined(TIOCSETA)") public static long TIOCSETA;

  @JniField(flags = { CONSTANT }, conditional = "defined(TIOCGETD)") public static long TIOCGETD;

  @JniField(flags = { CONSTANT }, conditional = "defined(TIOCSETD)") public static long TIOCSETD;

  @JniField(flags = { CONSTANT }, conditional = "defined(TIOCGWINSZ)") public static long TIOCGWINSZ;

  @JniField(flags = { CONSTANT }, conditional = "defined(TIOCSWINSZ)") public static long TIOCSWINSZ;

  @JniMethod(conditional = "defined(HAVE_IOCTL)") public static native int ioctl(@JniArg int filedes, @JniArg long request, @JniArg int[] params);

  @JniMethod(conditional = "defined(HAVE_IOCTL) && defined(HAVE_OPENPTY)") public static native int ioctl(@JniArg int filedes, @JniArg long request, @JniArg(flags = ArgFlag.POINTER_ARG) WinSize params);

  @JniClass(flags = { ClassFlag.STRUCT }, name = "winsize", conditional = "defined(HAVE_OPENPTY)") public static class WinSize {
    static {
      LIBRARY.load();
      init();
    }

    @JniMethod(flags = { CONSTANT_INITIALIZER }) private static native void init();

    @JniField(flags = { CONSTANT }, accessor = "sizeof(struct winsize)") public static int SIZEOF;

    @JniField(accessor = "ws_row") public short ws_row;

    @JniField(accessor = "ws_col") public short ws_col;

    @JniField(accessor = "ws_xpixel") public short ws_xpixel;

    @JniField(accessor = "ws_ypixel") public short ws_ypixel;

    public WinSize() {
    }

    public WinSize(short ws_row, short ws_col) {
      this.ws_row = ws_row;
      this.ws_col = ws_col;
    }
  }

  @JniClass(flags = { ClassFlag.STRUCT }, name = "termios", conditional = "defined(HAVE_OPENPTY)") public static class Termios {
    static {
      LIBRARY.load();
      init();
    }

    @JniMethod(flags = { CONSTANT_INITIALIZER }) private static native void init();

    @JniField(flags = { CONSTANT }, accessor = "sizeof(struct termios)") public static int SIZEOF;

    @JniField(accessor = "c_iflag") public long c_iflag;

    @JniField(accessor = "c_oflag") public long c_oflag;

    @JniField(accessor = "c_cflag") public long c_cflag;

    @JniField(accessor = "c_lflag") public long c_lflag;

    @JniField(accessor = "c_cc") public byte[] c_cc = new byte[20];

    @JniField(accessor = "c_ispeed") public long c_ispeed;

    @JniField(accessor = "c_ospeed") public long c_ospeed;
  }

  /**
     * Control characters
     */
  public static final int VEOF = 0;

  public static final int VEOL = 1;

  public static final int VEOL2 = 2;

  public static final int VERASE = 3;

  public static final int VWERASE = 4;

  public static final int VKILL = 5;

  public static final int VREPRINT = 6;

  public static final int VINTR = 8;

  public static final int VQUIT = 9;

  public static final int VSUSP = 10;

  public static final int VDSUSP = 11;

  public static final int VSTART = 12;

  public static final int VSTOP = 13;

  public static final int VLNEXT = 14;

  public static final int VDISCARD = 15;

  public static final int VMIN = 16;

  public static final int VTIME = 17;

  public static final int VSTATUS = 18;

  /**
     * Input flags - software input processing
     */
  public static final int IGNBRK = 0x00000001;

  public static final int BRKINT = 0x00000002;

  public static final int IGNPAR = 0x00000004;

  public static final int PARMRK = 0x00000008;

  public static final int INPCK = 0x00000010;

  public static final int ISTRIP = 0x00000020;

  public static final int INLCR = 0x00000040;

  public static final int IGNCR = 0x00000080;

  public static final int ICRNL = 0x00000100;

  public static final int IXON = 0x00000200;

  public static final int IXOFF = 0x00000400;

  public static final int IXANY = 0x00000800;

  public static final int OPOST = 0x00000001;

  public static final int ONLCR = 0x00000002;

  public static final int CCTS_OFLOW = 0x00010000;

  public static final int CRTS_IFLOW = 0x00020000;

  public static final int CRTSCTS = (CCTS_OFLOW | CRTS_IFLOW);

  public static final int CDTR_IFLOW = 0x00040000;

  public static final int CDSR_OFLOW = 0x00080000;

  public static final int CCAR_OFLOW = 0x00100000;

  public static final int ECHOE = 0x00000002;

  public static final int ECHOK = 0x00000004;

  public static final int ECHO = 0x00000008;

  public static final int ECHONL = 0x00000010;

  public static final int ISIG = 0x00000080;

  public static final int ICANON = 0x00000100;

  public static final int IEXTEN = 0x00000400;

  public static final int TOSTOP = 0x00400000;

  public static final int NOFLSH = 0x80000000;
}