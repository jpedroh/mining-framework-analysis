package com.github.mongobee;
import static com.mongodb.ServerAddress.defaultHost;
import static com.mongodb.ServerAddress.defaultPort;
import static org.springframework.util.StringUtils.hasText;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.github.mongobee.changeset.ChangeEntry;
import com.github.mongobee.dao.ChangeEntryDao;
import com.github.mongobee.exception.MongobeeChangeSetException;
import com.github.mongobee.exception.MongobeeConfigurationException;
import com.github.mongobee.exception.MongobeeConnectionException;
import com.github.mongobee.exception.MongobeeException;
import com.github.mongobee.utils.ChangeService;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class Mongobee implements InitializingBean {
  private static final Logger logger = LoggerFactory.getLogger(Mongobee.class);

  private static final String DEFAULT_CHANGELOG_COLLECTION_NAME = "dbchangelog";

  private static final String DEFAULT_LOCK_COLLECTION_NAME = "mongobeelock";

  private ChangeEntryDao dao;

  private boolean enabled = true;

  private String changeLogsScanPackage;

  private MongoClientURI mongoClientURI;

  private MongoClient mongoClient;

  private String dbName;

  private Environment springEnvironment;

  private ApplicationContext applicationContext;

  private MongoTemplate mongoTemplate;

  private Jongo jongo;

  public Mongobee() {
    this(new MongoClientURI("mongodb://" + defaultHost() + ":" + defaultPort() + "/"));
  }

  public Mongobee(MongoClientURI mongoClientURI) {
    this.mongoClientURI = mongoClientURI;
    this.setDbName(mongoClientURI.getDatabase());
    this.dao = new ChangeEntryDao(DEFAULT_CHANGELOG_COLLECTION_NAME, DEFAULT_LOCK_COLLECTION_NAME, DEFAULT_WAIT_FOR_LOCK, DEFAULT_CHANGE_LOG_LOCK_WAIT_TIME, DEFAULT_CHANGE_LOG_LOCK_POLL_RATE, DEFAULT_THROW_EXCEPTION_IF_CANNOT_OBTAIN_LOCK);
  }

  public Mongobee(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
    this.dao = new ChangeEntryDao(DEFAULT_CHANGELOG_COLLECTION_NAME, DEFAULT_LOCK_COLLECTION_NAME, DEFAULT_WAIT_FOR_LOCK, DEFAULT_CHANGE_LOG_LOCK_WAIT_TIME, DEFAULT_CHANGE_LOG_LOCK_POLL_RATE, DEFAULT_THROW_EXCEPTION_IF_CANNOT_OBTAIN_LOCK);
  }

  public Mongobee(String mongoURI) {
    this(new MongoClientURI(mongoURI));
  }

  @Override public void afterPropertiesSet() throws Exception {
    execute();
  }

  public void execute() throws MongobeeException {
    if (!isEnabled()) {
      logger.info("Mongobee is disabled. Exiting.");
      return;
    }
    validateConfig();
    if (this.mongoClient != null) {
      dao.connectMongoDb(this.mongoClient, dbName);
    } else {
      dao.connectMongoDb(this.mongoClientURI, dbName);
    }
    if (!dao.acquireProcessLock()) {
      logger.info("Mongobee did not acquire process lock. Exiting.");
      return;
    }
    logger.info("Mongobee acquired process lock, starting the data migration sequence..");
    try {
      executeMigration();
    }  finally {
      logger.info("Mongobee is releasing process lock.");
      dao.releaseProcessLock();
    }
    logger.info("Mongobee has finished his job.");
  }

  private void executeMigration() throws MongobeeException {
    ChangeService service = new ChangeService(changeLogsScanPackage, springEnvironment);
    for (Class<?> changelogClass : service.fetchChangeLogs()) {
      Object changelogInstance = null;
      try {
        changelogInstance = changelogClass.getConstructor().newInstance();
        List<Method> changesetMethods = service.fetchChangeSets(changelogInstance.getClass());
        for (Method changesetMethod : changesetMethods) {
          ChangeEntry changeEntry = service.createChangeEntry(changesetMethod);
          try {
            if (dao.isNewChange(changeEntry)) {
              executeChangeSetMethod(changesetMethod, changelogInstance, dao.getDb(), dao.getMongoDatabase());
              dao.save(changeEntry);
              logger.info(changeEntry + " applied");
            } else {
              if (service.isRunAlwaysChangeSet(changesetMethod)) {
                executeChangeSetMethod(changesetMethod, changelogInstance, dao.getDb(), dao.getMongoDatabase());
                logger.info(changeEntry + " reapplied");
              } else {
                logger.info(changeEntry + " passed over");
              }
            }
          } catch (MongobeeChangeSetException e) {
            logger.error(e.getMessage());
          }
        }
      } catch (NoSuchMethodException e) {
        throw new MongobeeException(e.getMessage(), e);
      } catch (IllegalAccessException e) {
        throw new MongobeeException(e.getMessage(), e);
      } catch (InvocationTargetException e) {
        Throwable targetException = e.getTargetException();
        throw new MongobeeException(targetException.getMessage(), e);
      } catch (InstantiationException e) {
        throw new MongobeeException(e.getMessage(), e);
      }
    }
  }

  private Object executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance, DB db, MongoDatabase mongoDatabase) throws IllegalAccessException, InvocationTargetException, MongobeeChangeSetException {
    if (changeSetMethod.getParameterTypes().length == 1 && changeSetMethod.getParameterTypes()[0].equals(DB.class)) {
      logger.debug("method with DB argument");
      return changeSetMethod.invoke(changeLogInstance, db);
    } else {
      if (changeSetMethod.getParameterTypes().length == 1 && changeSetMethod.getParameterTypes()[0].equals(Jongo.class)) {
        logger.debug("method with Jongo argument");
        return changeSetMethod.invoke(changeLogInstance, jongo != null ? jongo : new Jongo(db));
      } else {
        if (changeSetMethod.getParameterTypes().length == 1 && changeSetMethod.getParameterTypes()[0].equals(MongoTemplate.class)) {
          logger.debug("method with MongoTemplate argument");
          return changeSetMethod.invoke(changeLogInstance, mongoTemplate != null ? mongoTemplate : new MongoTemplate(db.getMongo(), dbName));
        } else {
          if (changeSetMethod.getParameterTypes().length == 2 && changeSetMethod.getParameterTypes()[0].equals(MongoTemplate.class) && changeSetMethod.getParameterTypes()[1].equals(Environment.class)) {
            logger.debug("method with MongoTemplate and environment arguments");
            return changeSetMethod.invoke(changeLogInstance, mongoTemplate != null ? mongoTemplate : new MongoTemplate(db.getMongo(), dbName), springEnvironment);
          } else {
            if (changeSetMethod.getParameterTypes().length == 2 && changeSetMethod.getParameterTypes()[0].equals(MongoTemplate.class) && changeSetMethod.getParameterTypes()[1].equals(ApplicationContext.class)) {
              logger.debug("method with MongoTemplate and Spring ApplicationContext arguments");
              return changeSetMethod.invoke(changeLogInstance, mongoTemplate != null ? mongoTemplate : new MongoTemplate(db.getMongo(), dbName), applicationContext);
            } else {
              if (changeSetMethod.getParameterTypes().length == 1 && changeSetMethod.getParameterTypes()[0].equals(MongoDatabase.class)) {
                logger.debug("method with DB argument");
                return changeSetMethod.invoke(changeLogInstance, mongoDatabase);
              } else {
                if (changeSetMethod.getParameterTypes().length == 0) {
                  logger.debug("method with no params");
                  return changeSetMethod.invoke(changeLogInstance);
                } else {
                  throw new MongobeeChangeSetException("ChangeSet method " + changeSetMethod.getName() + " has wrong arguments list. Please see docs for more info!");
                }
              }
            }
          }
        }
      }
    }
  }

  private void validateConfig() throws MongobeeConfigurationException {
    if (!hasText(dbName)) {
      throw new MongobeeConfigurationException("DB name is not set. It should be defined in MongoDB URI or via setter");
    }
    if (!hasText(changeLogsScanPackage)) {
      throw new MongobeeConfigurationException("Scan package for changelogs is not set: use appropriate setter");
    }
  }

  public boolean isExecutionInProgress() throws MongobeeConnectionException {
    return dao.isProccessLockHeld();
  }

  public Mongobee setDbName(String dbName) {
    this.dbName = dbName;
    return this;
  }

  public Mongobee setMongoClientURI(MongoClientURI mongoClientURI) {
    this.mongoClientURI = mongoClientURI;
    return this;
  }

  public Mongobee setChangeLogsScanPackage(String changeLogsScanPackage) {
    this.changeLogsScanPackage = changeLogsScanPackage;
    return this;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Mongobee setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  public Mongobee setSpringEnvironment(Environment environment) {
    this.springEnvironment = environment;
    return this;
  }

  public Mongobee setSpringApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    return this;
  }

  public Mongobee setMongoTemplate(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
    return this;
  }

  public Mongobee setJongo(Jongo jongo) {
    this.jongo = jongo;
    return this;
  }

  public Mongobee setChangelogCollectionName(String changelogCollectionName) {
    this.dao.setChangelogCollectionName(changelogCollectionName);
    return this;
  }

  public Mongobee setLockCollectionName(String lockCollectionName) {
    this.dao.setLockCollectionName(lockCollectionName);
    return this;
  }

  public void close() {
    dao.close();
  }

  private static final boolean DEFAULT_WAIT_FOR_LOCK = false;

  private static final long DEFAULT_CHANGE_LOG_LOCK_WAIT_TIME = 5L;

  private static final long DEFAULT_CHANGE_LOG_LOCK_POLL_RATE = 10L;

  private static final boolean DEFAULT_THROW_EXCEPTION_IF_CANNOT_OBTAIN_LOCK = false;

  public Mongobee setWaitForLock(boolean waitForLock) {
    this.dao.setWaitForLock(waitForLock);
    return this;
  }

  public Mongobee setChangeLogLockWaitTime(long changeLogLockWaitTime) {
    this.dao.setChangeLogLockWaitTime(changeLogLockWaitTime);
    return this;
  }

  public Mongobee setChangeLogLockPollRate(long changeLogLockPollRate) {
    this.dao.setChangeLogLockPollRate(changeLogLockPollRate);
    return this;
  }

  public Mongobee setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.dao.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return this;
  }
}