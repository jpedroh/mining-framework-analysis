package com.acciente.oacc.sql;
import com.acciente.oacc.sql.internal.encryptor.PasswordEncryptors;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class SQLAccessControlSystemInitializer {
  private static final String PROP_DbUrl = "-dburl";

  private static final String PROP_DbUser = "-dbuser";

  private static final String PROP_DbPwd = "-dbpwd";

  private static final String PROP_DbSchema = "-dbschema";

  private static final String PROP_PwdEncryptor = "-pwdencryptor";

  private static final String PROP_OACCRootPwd = "-oaccsystempwd";

  public static void main(String[] args) throws SQLException, IOException {
    if (args.length == 0) {
      System.out.println("Usage: java com.acciente.oacc.SQLAccessControlSystemInitializer" + PROP_DbUrl + "=<db-url> " + PROP_DbUser + "=<db-user> " + PROP_DbPwd + "=<db-password> " + PROP_PwdEncryptor + "=" + join("|", PasswordEncryptors.getSupportedEncryptorNames()) + " " + PROP_OACCRootPwd + "=<OACC-system-password> " + " [ " + PROP_DbSchema + "=<db-schema>]");
      return;
    }
    Properties initArgs = new Properties();
    for (String arg : args) {
      initArgs.load(new StringReader(arg));
    }
    String dbUrl;
    String dbUser;
    String dbPwd;
    String dbSchema;
    String pwdEncryptor;
    String oaccRootPwd;
    dbUrl = initArgs.getProperty(PROP_DbUrl);
    dbUser = initArgs.getProperty(PROP_DbUser);
    dbPwd = initArgs.getProperty(PROP_DbPwd);
    dbSchema = initArgs.getProperty(PROP_DbSchema, "TEST_OACC");
    pwdEncryptor = initArgs.getProperty(PROP_PwdEncryptor);
    oaccRootPwd = initArgs.getProperty(PROP_OACCRootPwd);
    if (dbUrl == null) {
      throw new IllegalArgumentException(PROP_DbUrl + " is required!");
    }
    if (dbUser == null) {
      throw new IllegalArgumentException(PROP_DbUser + " is required!");
    }
    if (dbPwd == null) {
      throw new IllegalArgumentException(PROP_DbPwd + " is required!");
    }
    if (pwdEncryptor == null) {
      throw new IllegalArgumentException(PROP_PwdEncryptor + " is required!");
    }
    if (oaccRootPwd == null) {
      throw new IllegalArgumentException(PROP_OACCRootPwd + " is required!");
    }
    initializeOACC(dbUrl, dbUser, dbPwd, dbSchema, oaccRootPwd.toCharArray(), PasswordEncryptors.getPasswordEncryptor(pwdEncryptor));
  }

  public static void initializeOACC(String dbUrl, String dbUser, String dbPwd, String dbSchema, char[] oaccRootPwd, PasswordEncryptor passwordEncryptor) throws SQLException {
    System.out.println("Connecting to OACC database @ " + dbUrl);
    Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
    try {
      com.acciente.oacc.sql.internal.SQLAccessControlSystemInitializer.initializeOACC(connection, dbSchema, oaccRootPwd, passwordEncryptor);
    }  finally {
      System.out.println("Disconnecting from OACC database @ " + dbUrl);
      connection.close();
    }
    System.out.println("Initialize..OK!");
  }

  public static void initializeOACC(Connection connection, String dbSchema, char[] oaccRootPwd, PasswordEncryptor passwordEncryptor) throws SQLException {
    com.acciente.oacc.sql.internal.SQLAccessControlSystemInitializer.initializeOACC(connection, dbSchema, oaccRootPwd, passwordEncryptor);
  }

  /**
    * Provides identical functionality of the Strings.join() method in Java 8
    *
    * @param delimiter the delimiter to insert between elements
    * @param elements the elements to concatenate
    * @return
    */
  private static String join(final String delimiter, final List<String> elements) {
    if (elements == null || elements.size() == 0) {
      return "";
    }
    final StringBuilder result = new StringBuilder(elements.get(0));
    for (String element : elements.subList(1, elements.size())) {
      result.append(delimiter);
      result.append(element);
    }
    return result.toString();
  }
}