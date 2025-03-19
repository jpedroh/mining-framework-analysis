package com.acciente.oacc.sql.internal;
import com.acciente.oacc.AccessControlContext;
import com.acciente.oacc.DomainCreatePermissions;
import com.acciente.oacc.DomainPermissions;
import com.acciente.oacc.Resources;
import com.acciente.oacc.sql.PasswordEncryptor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAccessControlSystemInitializer {
  public static void initializeOACC(Connection connection, String dbSchema, char[] oaccRootPwd, PasswordEncryptor passwordEncryptor) throws SQLException {
    SchemaNameValidator.assertValid(dbSchema);
    final String schemaNameAndTablePrefix = dbSchema != null ? dbSchema + ".OAC_" : "OAC_";
    System.out.println("Checking database...needs empty tables");
    PreparedStatement statement = null;
    ResultSet resultSet;
    try {
      statement = connection.prepareStatement("SELECT  DomainId FROM " + schemaNameAndTablePrefix + "Domain WHERE DomainId = 0");
      resultSet = statement.executeQuery();
      if (resultSet.next()) {
        System.out.println("Cannot initialize, likely that this OACC is already initialized! (check: found a system domain)");
        resultSet.close();
        return;
      }
      statement.close();
      System.out.println("Initializing database...assuming empty tables (will fail safely if tables have data)");
      statement = connection.prepareStatement("INSERT INTO " + schemaNameAndTablePrefix + "Domain( DomainId, DomainName ) VALUES ( 0, ? )");
      statement.setString(1, AccessControlContext.SYSTEM_DOMAIN);
      statement.executeUpdate();
      statement.close();
      statement = connection.prepareStatement("INSERT INTO " + schemaNameAndTablePrefix + "ResourceClass( ResourceClassId, ResourceClassName, IsAuthenticatable, IsUnauthenticatedCreateAllowed ) VALUES ( 0, ?, 1, 0 )");
      statement.setString(1, AccessControlContext.SYSTEM_RESOURCE_CLASS);
      statement.executeUpdate();
      statement.close();
      statement = connection.prepareStatement("INSERT INTO " + schemaNameAndTablePrefix + "Resource( ResourceId, ResourceClassId, DomainId ) VALUES ( 0, 0, 0 )");
      statement.executeUpdate();
      statement.close();
      statement = connection.prepareStatement("INSERT INTO " + schemaNameAndTablePrefix + "ResourcePassword( ResourceId, Password ) VALUES ( 0, ? )");
      char[] boundPassword = null;
      try {
        boundPassword = PasswordUtils.computeBoundPassword(Resources.getInstance(0), oaccRootPwd);
        statement.setString(1, passwordEncryptor.encryptPassword(boundPassword));
        statement.executeUpdate();
      }  finally {
        PasswordUtils.cleanPassword(boundPassword);
      }
      statement.close();
      statement = connection.prepareStatement("INSERT INTO " + schemaNameAndTablePrefix + "Grant_DomPerm_Sys( AccessorResourceId, GrantorResourceId, AccessedDomainId, SysPermissionId, IsWithGrant )" + " VALUES ( 0, 0, 0, ?, 1 )");
      statement.setLong(1, DomainPermissions.getInstance(DomainPermissions.SUPER_USER).getSystemPermissionId());
      statement.executeUpdate();
      statement.close();
      statement = connection.prepareStatement("INSERT INTO " + schemaNameAndTablePrefix + "Grant_DomCrPerm_Sys( AccessorResourceId, GrantorResourceId, SysPermissionId, IsWithGrant )" + " VALUES ( 0, 0, ?, 1 )");
      statement.setLong(1, DomainCreatePermissions.getInstance(DomainCreatePermissions.CREATE).getSystemPermissionId());
      statement.executeUpdate();
      statement.close();
      statement = connection.prepareStatement("INSERT INTO " + schemaNameAndTablePrefix + "Grant_DomCrPerm_PostCr_Sys( AccessorResourceId, GrantorResourceId, PostCreateSysPermissionId, PostCreateIsWithGrant, IsWithGrant )" + " VALUES ( 0, 0, ?, 1, 1 )");
      statement.setLong(1, DomainPermissions.getInstance(DomainPermissions.SUPER_USER).getSystemPermissionId());
      statement.executeUpdate();
      statement.setLong(1, DomainPermissions.getInstance(DomainPermissions.CREATE_CHILD_DOMAIN).getSystemPermissionId());
      statement.executeUpdate();
      statement.setLong(1, DomainPermissions.getInstance(DomainPermissions.DELETE).getSystemPermissionId());
      statement.executeUpdate();
      statement.close();
    }  finally {
      if (statement != null) {
        statement.close();
      }
    }
  }
}