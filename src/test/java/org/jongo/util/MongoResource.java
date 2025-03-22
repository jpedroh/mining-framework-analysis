package org.jongo.util;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.NullProcessor;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.runtime.Network;
import de.flapdoodle.embed.process.store.IArtifactStore;
import java.net.UnknownHostException;

public class MongoResource {
  public DB getDb(String dbname) {
    return getInstance().getDB(dbname);
  }

  public MongoDatabase getDatabase(String dbname) {
    return getInstance().getDatabase(dbname);
  }

  public MongoClient getInstance() {
    String isDisabled = System.getProperty("embedmongo.disabled");
    if (isDisabled != null && isDisabled.equals("true")) {
      return LocalMongo.instance;
    } else {
      return EmbeddedMongo.instance;
    }
  }

  private static class EmbeddedMongo {
    private static MongoClient instance = getInstance();

    private static MongoClient getInstance() {
      try {
        Command mongoD = Command.MongoD;
        int port = RandomPortNumberGenerator.pickAvailableRandomEphemeralPortNumber();
        de.flapdoodle.embed.process.config.store.DownloadConfigBuilder downloadConfigBuilder = new DownloadConfigBuilder().defaultsForCommand(mongoD).defaults().artifactStorePath(getMongoPath());
        IArtifactStore artifactStore = new ArtifactStoreBuilder().defaults(mongoD).executableNaming(new UserTempNaming()).download(downloadConfigBuilder).build();
        IStreamProcessor output = new NullProcessor();
        ProcessOutput processOutput = new ProcessOutput(output, output, output);
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(mongoD).processOutput(processOutput).artifactStore(artifactStore).build();
        Version version = getVersion();
        MongoCmdOptionsBuilder mongoCmdOptionsBuilder = new MongoCmdOptionsBuilder();
        if (version.compareTo(Version.V3_2_0) > -1) {
          mongoCmdOptionsBuilder.useStorageEngine("ephemeralForTest");
        }
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(version).cmdOptions(
<<<<<<< /usr/src/app/output/bguerout/jongo/ef3b9719963e125da4191c6917e6453edd04c410/src/test/java/org/jongo/util/MongoResource.java/left.java
        new MongoCmdOptionsBuilder().useStorageEngine("ephemeralForTest").build()
=======
        mongoCmdOptionsBuilder.build()
>>>>>>> /usr/src/app/output/bguerout/jongo/ef3b9719963e125da4191c6917e6453edd04c410/src/test/java/org/jongo/util/MongoResource.java/right.java
        ).net(
<<<<<<< /usr/src/app/output/bguerout/jongo/ef3b9719963e125da4191c6917e6453edd04c410/src/test/java/org/jongo/util/MongoResource.java/left.java
        new Net(port, Network.localhostIsIPv6())
=======
        network
>>>>>>> /usr/src/app/output/bguerout/jongo/ef3b9719963e125da4191c6917e6453edd04c410/src/test/java/org/jongo/util/MongoResource.java/right.java
        ).build();
        MongodStarter.getInstance(runtimeConfig).prepare(mongodConfig).start();
        return createClient(port);
      } catch (Exception e) {
        throw new RuntimeException("Failed to initialize Embedded Mongo instance: " + e, e);
      }
    }

    private static IDirectory getMongoPath() {
      String path = System.getProperty("jongo.test.embedmongo.dir");
      if (path == null) {
        return new UserHome(".embedmongo");
      }
      return new FixedPath(path);
    }

    private static Version getVersion() {
      String version = System.getProperty("embedmongo.version");
      if (version == null) {
        return Version.V4_0_2;
      }
      return Version.valueOf("V" + version.replaceAll("\\.", "_"));
    }
  }

  private static class LocalMongo {
    private static MongoClient instance = getInstance();

    private static MongoClient getInstance() {
      try {
        return createClient(27017);
      } catch (Exception e) {
        throw new RuntimeException("Failed to initialize local Mongo instance: " + e, e);
      }
    }
  }

  private static MongoClient createClient(int port) throws UnknownHostException {
    MongoClient mongo = new MongoClient("127.0.0.1", port);
    mongo.setWriteConcern(WriteConcern.SAFE);
    return mongo;
  }
}