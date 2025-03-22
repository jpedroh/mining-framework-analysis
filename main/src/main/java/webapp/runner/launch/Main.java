package webapp.runner.launch;
import com.beust.jcommander.JCommander;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.ExpandWar;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.catalina.users.MemoryUserDatabaseFactory;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.scan.StandardJarScanner;
import webapp.runner.launch.valves.StdoutAccessLogValve;
import javax.naming.CompositeName;
import javax.naming.StringRefAddr;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * This is the main entry point to webapp-runner. Helpers are called to parse the arguments. Tomcat configuration and
 * launching takes place here.
 */
public class Main {
  private static final String AUTH_ROLE = "user";

  public static void main(String[] args) throws Exception {
    CommandLineParams commandLineParams = new CommandLineParams();
    JCommander jCommander = new JCommander(commandLineParams, args);
    if (commandLineParams.help) {
      jCommander.usage();
      System.exit(1);
    }
    if (commandLineParams.paths.size() == 0) {
      commandLineParams.paths.add("src/main/webapp");
    }
    final Tomcat tomcat = new Tomcat();
    tomcat.setBaseDir(resolveTomcatBaseDir(commandLineParams.port, commandLineParams.tempDirectory));
    Connector nioConnector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    nioConnector.setPort(commandLineParams.port);
    if (!commandLineParams.attributes.isEmpty()) {
      System.out.println("Connector attributes");
      for (final Map.Entry<String, String> entry : commandLineParams.attributes.entrySet()) {
        final String key = entry.getKey();
        final String value = entry.getValue();
        if (nioConnector.setProperty(entry.getKey(), entry.getValue())) {
          System.out.println("property: " + key + " - " + value + "(OK)");
        } else {
          System.out.println("property: " + key + " - " + value + " (Could not be set, no effect!)");
        }
      }
    }
    nioConnector.setMaxPostSize(commandLineParams.maxPostSize);
    if (commandLineParams.enableSSL) {
      nioConnector.setSecure(true);
      nioConnector.setProperty("SSLEnabled", "true");
      nioConnector.setProperty("allowUnsafeLegacyRenegotiation", "false");
      String pathToTrustStore = System.getProperty("javax.net.ssl.trustStore");
      if (pathToTrustStore != null) {
        nioConnector.setProperty("sslProtocol", "tls");
        File truststoreFile = new File(pathToTrustStore);
        nioConnector.setAttribute("truststoreFile", truststoreFile.getAbsolutePath());
        System.out.println(truststoreFile.getAbsolutePath());
        nioConnector.setAttribute("trustStorePassword", System.getProperty("javax.net.ssl.trustStorePassword"));
      }
      String pathToKeystore = System.getProperty("javax.net.ssl.keyStore");
      if (pathToKeystore != null) {
        File keystoreFile = new File(pathToKeystore);
        nioConnector.setAttribute("keystoreFile", keystoreFile.getAbsolutePath());
        System.out.println(keystoreFile.getAbsolutePath());
        nioConnector.setAttribute("keystorePass", System.getProperty("javax.net.ssl.keyStorePassword"));
      }
      if (commandLineParams.enableClientAuth) {
        nioConnector.setAttribute("clientAuth", true);
      }
    }
    if (commandLineParams.proxyBaseUrl.length() > 0) {
      URI uri = new URI(commandLineParams.proxyBaseUrl);
      String scheme = uri.getScheme();
      nioConnector.setProxyName(uri.getHost());
      nioConnector.setScheme(scheme);
      if (scheme.equals("https") && !nioConnector.getSecure()) {
        nioConnector.setSecure(true);
      }
      if (uri.getPort() > 0) {
        nioConnector.setProxyPort(uri.getPort());
      } else {
        if (scheme.equals("http")) {
          nioConnector.setProxyPort(80);
        } else {
          if (scheme.equals("https")) {
            nioConnector.setProxyPort(443);
          }
        }
      }
    }
    if (null != commandLineParams.uriEncoding) {
      nioConnector.setURIEncoding(commandLineParams.uriEncoding);
    }
    nioConnector.setUseBodyEncodingForURI(commandLineParams.useBodyEncodingForURI);
    if (commandLineParams.enableCompression) {
      nioConnector.setProperty("compression", "on");
      nioConnector.setProperty("compressableMimeType", commandLineParams.compressableMimeTypes);
    }
    if (!commandLineParams.bindOnInit) {
      nioConnector.setProperty("bindOnInit", "false");
    }
    if (commandLineParams.maxThreads > 0) {
      ProtocolHandler handler = nioConnector.getProtocolHandler();
      if (handler instanceof AbstractProtocol) {
        AbstractProtocol protocol = (AbstractProtocol) handler;
        protocol.setMaxThreads(commandLineParams.maxThreads);
      } else {
        System.out.println("WARNING: Could not set maxThreads!");
      }
    }
    tomcat.setConnector(nioConnector);
    tomcat.setPort(commandLineParams.port);
    if (commandLineParams.paths.size() > 1) {
      System.out.println("WARNING: multiple paths are specified, but no longer supported. First path will be used.");
    }
    String path = commandLineParams.paths.get(0);
    Context ctx;
    File war = new File(path);
    if (!war.exists()) {
      System.err.println("The specified path \"" + path + "\" does not exist.");
      jCommander.usage();
      System.exit(1);
    }
    if (commandLineParams.contextPath.length() > 0 && !commandLineParams.contextPath.startsWith("/")) {
      System.out.println("WARNING: You entered a path: [" + commandLineParams.contextPath + "]. Your path should start with a \'/\'. Tomcat will update this for you, but you may still experience issues.");
    }
    final String ctxName = commandLineParams.contextPath;
    if (commandLineParams.expandWar && commandLineParams.expandWarFile && war.isFile()) {
      File appBase = new File(System.getProperty(Globals.CATALINA_BASE_PROP), tomcat.getHost().getAppBase());
      if (appBase.exists()) {
        appBase.delete();
      }
      appBase.mkdir();
      URL fileUrl = new URL("jar:" + war.toURI().toURL() + "!/");
      String expandedDir = null;
      String expandedDirName = commandLineParams.expandedDirName;
      Path expandedDirPath = Paths.get(expandedDirName);
      if (expandedDirPath.isAbsolute()) {
        Host tempHost = tomcat.getHost();
        tempHost.setAppBase(new File(expandedDirName).getAbsolutePath());
        expandedDir = ExpandWar.expand(tempHost, fileUrl, "");
      } else {
        expandedDir = ExpandWar.expand(tomcat.getHost(), fileUrl, "/" + expandedDirName);
      }
      System.out.println("Expanding " + war.getName() + " into " + expandedDir);
      System.out.println("Adding Context " + ctxName + " for " + expandedDir);
      ctx = tomcat.addWebapp(ctxName, expandedDir);
    } else {
      System.out.println("Adding Context " + ctxName + " for " + war.getPath());
      ctx = tomcat.addWebapp(ctxName, war.getAbsolutePath());
    }
    ((StandardContext) ctx).setUnpackWAR(false);
    if (!commandLineParams.shutdownOverride) {
      ctx.addLifecycleListener(new LifecycleListener() {
        public void lifecycleEvent(LifecycleEvent event) {
          if (event.getLifecycle().getState() == LifecycleState.FAILED) {
            Server server = tomcat.getServer();
            if (server instanceof StandardServer) {
              System.err.println("SEVERE: Context [" + ctxName + "] failed in [" + event.getLifecycle().getClass().getName() + "] lifecycle. Allowing Tomcat to shutdown.");
              ((StandardServer) server).stopAwait();
            }
          }
        }
      });
    }
    if (commandLineParams.scanBootstrapClassPath) {
      StandardJarScanner scanner = new StandardJarScanner();
      scanner.setScanBootstrapClassPath(true);
      ctx.setJarScanner(scanner);
    }
    if (commandLineParams.contextXml != null) {
      System.out.println("Using context config: " + commandLineParams.contextXml);
      ctx.setConfigFile(new File(commandLineParams.contextXml).toURI().toURL());
    }
    if (commandLineParams.sessionStore != null) {
      SessionStore.getInstance(commandLineParams.sessionStore).configureSessionStore(commandLineParams, ctx);
    }
    if (commandLineParams.sessionTimeout != null) {
      ctx.setSessionTimeout(commandLineParams.sessionTimeout);
    }
    addShutdownHook(tomcat);
    if (commandLineParams.enableNaming || commandLineParams.enableBasicAuth || commandLineParams.tomcatUsersLocation != null) {
      tomcat.enableNaming();
    }
    if (commandLineParams.enableBasicAuth) {
      enableBasicAuth(ctx, commandLineParams.enableSSL);
    }
    if (commandLineParams.accessLog) {
      Host host = tomcat.getHost();
      StdoutAccessLogValve valve = new StdoutAccessLogValve();
      valve.setEnabled(true);
      valve.setPattern(commandLineParams.accessLogPattern);
      host.getPipeline().addValve(valve);
    }
    tomcat.start();
    if (commandLineParams.enableBasicAuth || commandLineParams.tomcatUsersLocation != null) {
      configureUserStore(tomcat, commandLineParams);
    }
    commandLineParams = null;
    tomcat.getServer().await();
  }

  /**
   * Gets or creates temporary Tomcat base directory within target dir
   *
   * @param port port of web process
   * @return absolute dir path
   * @throws IOException if dir fails to be created
   */
  static String resolveTomcatBaseDir(Integer port, String tempDirectory) throws IOException {
    final File baseDir = tempDirectory != null ? new File(tempDirectory) : new File(System.getProperty("user.dir") + "/target/tomcat." + port);
    if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
      throw new IOException("Could not create temp dir: " + baseDir);
    }
    try {
      return baseDir.getCanonicalPath();
    } catch (IOException e) {
      return baseDir.getAbsolutePath();
    }
  }

  static void enableBasicAuth(Context ctx, boolean enableSSL) {
    LoginConfig loginConfig = new LoginConfig();
    loginConfig.setAuthMethod("BASIC");
    ctx.setLoginConfig(loginConfig);
    ctx.addSecurityRole(AUTH_ROLE);
    SecurityConstraint securityConstraint = new SecurityConstraint();
    securityConstraint.addAuthRole(AUTH_ROLE);
    if (enableSSL) {
      securityConstraint.setUserConstraint(TransportGuarantee.CONFIDENTIAL.toString());
    }
    SecurityCollection securityCollection = new SecurityCollection();
    securityCollection.addPattern("/*");
    securityConstraint.addCollection(securityCollection);
    ctx.addConstraint(securityConstraint);
  }

  static void configureUserStore(final Tomcat tomcat, final CommandLineParams commandLineParams) throws Exception {
    String tomcatUsersLocation = commandLineParams.tomcatUsersLocation;
    if (tomcatUsersLocation == null) {
      tomcatUsersLocation = "../../tomcat-users.xml";
    }
    javax.naming.Reference ref = new javax.naming.Reference("org.apache.catalina.UserDatabase");
    ref.add(new StringRefAddr("pathname", tomcatUsersLocation));
    MemoryUserDatabase memoryUserDatabase = (MemoryUserDatabase) new MemoryUserDatabaseFactory().getObjectInstance(ref, new CompositeName("UserDatabase"), null, null);
    if (commandLineParams.basicAuthUser != null && commandLineParams.basicAuthPw != null) {
      memoryUserDatabase.setReadonly(false);
      Role user = memoryUserDatabase.createRole(AUTH_ROLE, AUTH_ROLE);
      memoryUserDatabase.createUser(commandLineParams.basicAuthUser, commandLineParams.basicAuthPw, commandLineParams.basicAuthUser).addRole(user);
      memoryUserDatabase.save();
    } else {
      if (System.getenv("BASIC_AUTH_USER") != null && System.getenv("BASIC_AUTH_PW") != null) {
        memoryUserDatabase.setReadonly(false);
        Role user = memoryUserDatabase.createRole(AUTH_ROLE, AUTH_ROLE);
        memoryUserDatabase.createUser(System.getenv("BASIC_AUTH_USER"), System.getenv("BASIC_AUTH_PW"), System.getenv("BASIC_AUTH_USER")).addRole(user);
        memoryUserDatabase.save();
      }
    }
    System.out.println("MemoryUserDatabase: " + memoryUserDatabase);
    tomcat.getServer().getGlobalNamingContext().addToEnvironment("UserDatabase", memoryUserDatabase);
    org.apache.tomcat.util.descriptor.web.ContextResource ctxRes = new org.apache.tomcat.util.descriptor.web.ContextResource();
    ctxRes.setName("UserDatabase");
    ctxRes.setAuth("Container");
    ctxRes.setType("org.apache.catalina.UserDatabase");
    ctxRes.setDescription("User database that can be updated and saved");
    ctxRes.setProperty("factory", "org.apache.catalina.users.MemoryUserDatabaseFactory");
    ctxRes.setProperty("pathname", tomcatUsersLocation);
    tomcat.getServer().getGlobalNamingResources().addResource(ctxRes);
    tomcat.getEngine().setRealm(new org.apache.catalina.realm.UserDatabaseRealm());
  }

  /**
   * Stops the embedded Tomcat server.
   */
  static void addShutdownHook(final Tomcat tomcat) {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          if (tomcat != null) {
            tomcat.getServer().stop();
          }
        } catch (LifecycleException exception) {
          throw new RuntimeException("WARNING: Cannot Stop Tomcat " + exception.getMessage(), exception);
        }
      }
    });
  }
}