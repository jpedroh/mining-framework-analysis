package com.github.joelittlejohn.embedmongo;
import static java.util.Collections.*;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import com.github.joelittlejohn.embedmongo.log.Loggers;
import com.github.joelittlejohn.embedmongo.log.Loggers.LoggingStyle;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.distribution.Versions;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.exceptions.DistributionException;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.runtime.Network;
import de.flapdoodle.embed.process.store.IArtifactStore;

/**
 * When invoked, this goal starts an instance of mongo. The required binaries
 * are downloaded if no mongo release is found in <code>~/.embedmongo</code>.
 * 
 * @see <a
 *      href="http://github.com/flapdoodle-oss/embedmongo.flapdoodle.de">http://github.com/flapdoodle-oss/embedmongo.flapdoodle.de</a>
 */
@Mojo(name = "start", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST) public class StartEmbeddedMongoMojo extends AbstractEmbeddedMongoMojo {
  private static final String PACKAGE_NAME = StartEmbeddedMongoMojo.class.getPackage().getName();

  public static final String MONGOD_CONTEXT_PROPERTY_NAME = PACKAGE_NAME + ".mongod";

  /**
     * The location of a directory that will hold the MongoDB data files.
     * 
     * @since 0.1.0
     */
  @Parameter(property = "embedmongo.databaseDirectory") private File databaseDirectory;

  /**
     * An IP address for the MongoDB instance to be bound to during its
     * execution.
     * 
     * @since 0.1.4
     */
  @Parameter(property = "embedmongo.bindIp") private String bindIp;

  /**
     * @since 0.1.3
     */
  @Parameter(property = "embedmongo.logging") private String logging;

  /**
     * @since 0.1.7
     */
  @Parameter(property = "embedmongo.logFile", defaultValue = "embedmongo.log") private String logFile;

  /**
     * @since 0.1.7
     */
  @Parameter(property = "embedmongo.logFileEncoding", defaultValue = "utf-8") private String logFileEncoding;

  /**
     * The base URL to be used when downloading MongoDB
     * 
     * @since 0.1.10
     */
  @Parameter(property = "embedmongo.downloadPath", defaultValue = "http://fastdl.mongodb.org/") private String downloadPath;

  /**
     * Should authorization be enabled for MongoDB
     * 
     */
  @Parameter(property = "embedmongo.authEnabled", defaultValue = "false") private boolean authEnabled;

  @Override protected void onSkip() {
    getLog().debug("skip=true, not starting embedmongo");
  }

  /**
     * Should journaling be enabled for MongoDB
     *
     * @parameter expression="${embedmongo.journal}" default-value="false"
     */
  private boolean journal;

  @Override @SuppressWarnings(value = { "unchecked" }) public void executeStart() throws MojoExecutionException, MojoFailureException {
    if (hasProxyConfigured()) {
      this.addProxySelector();
    }
    MongodExecutable executable;
    try {
      final ICommandLinePostProcessor commandLinePostProcessor;
      if (authEnabled) {
        commandLinePostProcessor = new ICommandLinePostProcessor() {
          @Override public List<String> process(final Distribution distribution, final List<String> args) {
            args.remove("--noauth");
            args.add("--auth");
            return args;
          }
        };
      } else {
        commandLinePostProcessor = new ICommandLinePostProcessor.Noop();
      }
      IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(Command.MongoD).processOutput(getOutputConfig()).artifactStore(getArtifactStore()).commandLinePostProcessor(commandLinePostProcessor).build();
      int port = getPort();
      if (isRandomPort()) {
        port = PortUtils.allocateRandomPort();
      }
      savePortToProjectProperties(port);
      IMongodConfig config = new MongodConfigBuilder().version(getVersion()).net(new Net(bindIp, port, Network.localhostIsIPv6())).replication(new Storage(getDataDirectory(), null, 0)).build();
      executable = MongodStarter.getInstance(runtimeConfig).prepare(config);
    } catch (UnknownHostException e) {
      throw new MojoExecutionException("Unable to determine if localhost is ipv6", e);
    } catch (DistributionException e) {
      throw new MojoExecutionException("Failed to download MongoDB distribution: " + e.withDistribution(), e);
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to Config MongoDB: ", e);
    }
    try {
      MongodProcess mongod = executable.start();
      if (isWait()) {
        while (true) {
          try {
            TimeUnit.MINUTES.sleep(5);
          } catch (InterruptedException e) {
            break;
          }
        }
      }
      getPluginContext().put(MONGOD_CONTEXT_PROPERTY_NAME, mongod);
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to start the mongod", e);
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override @SuppressWarnings(value = { "unchecked" }) public void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().debug("skip=true, not starting embedmongo");
      return;
    }
    if (this.proxyHost != null && this.proxyHost.length() > 0) {
      this.addProxySelector();
    }
    MongodExecutable executable;
    try {
      final ICommandLinePostProcessor commandLinePostProcessor;
      if (authEnabled) {
        commandLinePostProcessor = new ICommandLinePostProcessor() {
          @Override public List<String> process(final Distribution distribution, final List<String> args) {
            args.remove("--noauth");
            args.add("--auth");
            return args;
          }
        };
      } else {
        commandLinePostProcessor = new ICommandLinePostProcessor.Noop();
      }
      IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(Command.MongoD).processOutput(getOutputConfig()).artifactStore(getArtifactStore()).commandLinePostProcessor(commandLinePostProcessor).build();
      if (randomPort) {
        port = PortUtils.allocateRandomPort();
      }
      savePortToProjectProperties();
      IMongodConfig config = new MongodConfigBuilder().version(getVersion()).net(new Net(bindIp, port, Network.localhostIsIPv6())).replication(new Storage(getDataDirectory(), null, 0)).cmdOptions(new MongoCmdOptionsBuilder().useNoJournal(!journal).build()).build();
      executable = MongodStarter.getInstance(runtimeConfig).prepare(config);
    } catch (UnknownHostException e) {
      throw new MojoExecutionException("Unable to determine if localhost is ipv6", e);
    } catch (DistributionException e) {
      throw new MojoExecutionException("Failed to download MongoDB distribution: " + e.withDistribution(), e);
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to Config MongoDB: ", e);
    }
    try {
      MongodProcess mongod = executable.start();
      if (wait) {
        while (true) {
          try {
            TimeUnit.MINUTES.sleep(5);
          } catch (InterruptedException e) {
            break;
          }
        }
      }
      getPluginContext().put(MONGOD_CONTEXT_PROPERTY_NAME, mongod);
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to start the mongod", e);
    }
  }
>>>>>>> /usr/src/app/output/joelittlejohn/embedmongo-maven-plugin/e407eb6955fe9325b7b0c2ce4069e7d640c1cf81/src/main/java/com/github/joelittlejohn/embedmongo/StartEmbeddedMongoMojo.java/right.java


  private ProcessOutput getOutputConfig() throws MojoFailureException {
    LoggingStyle loggingStyle = LoggingStyle.valueOf(logging.toUpperCase());
    switch (loggingStyle) {
      case CONSOLE:
      return Loggers.console();
      case FILE:
      return Loggers.file(logFile, logFileEncoding);
      case NONE:
      return Loggers.none();
      default:
      throw new MojoFailureException("Unexpected logging style encountered: \"" + logging + "\" -> " + loggingStyle);
    }
  }

  private IArtifactStore getArtifactStore() {
    IDownloadConfig downloadConfig = new DownloadConfigBuilder().defaultsForCommand(Command.MongoD).downloadPath(downloadPath).build();
    return new ArtifactStoreBuilder().defaults(Command.MongoD).download(downloadConfig).build();
  }

  private void addProxySelector() {
    final String proxyUser = getProxyUser();
    final String proxyPassword = getProxyPassword();
    final String proxyHost = getProxyHost();
    final int proxyPort = getProxyPort();
    if (proxyUser != null && proxyPassword != null) {
      Authenticator.setDefault(new Authenticator() {
        @Override public PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
        }
      });
    }
    final ProxySelector defaultProxySelector = ProxySelector.getDefault();
    ProxySelector.setDefault(new ProxySelector() {
      @Override public List<Proxy> select(URI uri) {
        if (uri.getHost().equals("fastdl.mongodb.org")) {
          return singletonList(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
        } else {
          return defaultProxySelector.select(uri);
        }
      }

      @Override public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
      }
    });
  }

  private String getDataDirectory() {
    if (databaseDirectory != null) {
      return databaseDirectory.getAbsolutePath();
    } else {
      return null;
    }
  }
}