package de.onyxbits.raccoon;
import java.io.File;
import javax.swing.SwingUtilities;
import org.apache.commons.cli.ParseException;
import com.akdeniz.googleplaycrawler.GooglePlayAPI;
import de.onyxbits.raccoon.gui.MainActivity;
import de.onyxbits.raccoon.io.Archive;

/**
 * Just the application launcher.
 * 
 * @author patrick
 * 
 */
public class App {
  /**
	 * Version identifier.
	 */
  public static final String VERSIONSTRING = "3.0";

  /**
	 * Relative path for keeping extension jars in
	 */
  public static final String EXTDIR = "ext";

  /**
	 * Relative path for keeping archives in (the user is not required to put
	 * archives here, its just the suggested folder).
	 */
  public static final String ARCHIVEDIR = "archives";

  /**
	 * Relative path, root directory for the app.
	 */
  public static final String HOMEDIR = "Raccoon";

  /**
	 * Application Entry
	 * 
	 * @param args
	 * @throws ParseException
	 */
  public static void main(String[] args) throws ParseException {
    getDir(HOMEDIR).mkdirs();
    getDir(EXTDIR).mkdirs();
    getDir(ARCHIVEDIR).mkdirs();
    if (args == null || args.length == 0) {
      SwingUtilities.invokeLater(new MainActivity(null));
    } else {
      new CliService(args).run();
    }
  }

  /**
	 * Query the location of a directory.
	 * 
	 * @param which
	 *          EXTDIR, ARCHIVEDIR, or HOMEDIR.
	 * @return the file (may or may not exist).
	 */
  public static File getDir(String which) {
    File root = new File(System.getProperty("user.home"), HOMEDIR);
    if (System.getProperty("raccoon.home") != null) {
      root = new File(System.getProperty("raccoon.home"));
    }
    if (which.equals(HOMEDIR)) {
      return root;
    }
    return new File(root, which);
  }

  /**
	 * Utility method for hooking up with Google Play.
	 * 
	 * @param archive
	 *          The archive from which to take configuration data.
	 * @return a ready to use connection
	 * @throws Exception
	 *           if something goes seriously wrong.
	 */
  public static synchronized GooglePlayAPI createConnection(Archive archive) throws Exception {
    String pwd = archive.getPassword();
    String uid = archive.getUserId();
    String aid = archive.getAndroidId();
    GooglePlayAPI ret = new GooglePlayAPI(uid, pwd, aid);
    if (archive.getProxyClient() != null) {
      ret.setClient(archive.getProxyClient());
    }
    ret.setToken(archive.getAuthToken());
    if (ret.getToken() == null) {
      ret.login();
      archive.setAuthToken(ret.getToken());
    }
    return ret;
  }
}