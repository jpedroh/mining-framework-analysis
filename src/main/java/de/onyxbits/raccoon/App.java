package de.onyxbits.raccoon;
import java.io.File;
import javax.swing.SwingUtilities;
import org.apache.commons.cli.ParseException;
import org.apache.http.client.HttpClient;
import com.akdeniz.googleplaycrawler.GooglePlayAPI;
import de.onyxbits.raccoon.gui.MainActivity;
import de.onyxbits.raccoon.io.Archive;
import de.onyxbits.raccoon.rss.Loader;

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
  public static final String VERSIONSTRING = "3.5";

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
	 * Relative path to the homedir for caching files.
	 */
  public static final String CACHEDIR = "cache";

  /**
	 * Relative path, root directory for the app.
	 */
  public static final String HOMEDIR = "Raccoon";

  /**
	 * Time to live in the cache (1 week).
	 */
  public static final long CACHETTL = 1000 * 60 * 60 * 24 * 7;

  /**
	 * Application Entry
	 * 
	 * @param args
	 * @throws ParseException
	 */
  public static void main(String[] args) throws ParseException {
    getDir(HOMEDIR).mkdirs();
    getDir(EXTDIR).mkdirs();
    getDir(CACHEDIR).mkdirs();
    getDir(ARCHIVEDIR).mkdirs();
    if (args == null || args.length == 0) {
      try {
        long now = System.currentTimeMillis();
        File[] lst = getDir(CACHEDIR).listFiles();
        for (File f : lst) {
          if (f.lastModified() < now - CACHETTL) {
            f.delete();
          }
        }
        Thread t = new Thread(new Loader());
        t.start();
        t.join(1500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
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
    String ua = archive.getUserAgent();
    GooglePlayAPI ret = new GooglePlayAPI(uid, pwd, aid);

<<<<<<< /usr/src/app/output/onyxbits/raccoon/ad6961824ce68e2e909617b87fe3f852e6e915d8/src/main/java/de/onyxbits/raccoon/App.java/left.java
    if (ua != null) {
      ret.setUseragent(ua);
    }
=======
    HttpClient client = archive.getProxyClient();
>>>>>>> /usr/src/app/output/onyxbits/raccoon/ad6961824ce68e2e909617b87fe3f852e6e915d8/src/main/java/de/onyxbits/raccoon/App.java/right.java

    if (client != null) {
      ret.setClient(client);
    }
    ret.setToken(archive.getAuthToken());
    if (ret.getToken() == null) {
      ret.login();
      archive.setAuthToken(ret.getToken());
    }
    return ret;
  }
}