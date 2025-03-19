package org.n0pe.mojo.asadmin;
import org.apache.commons.lang.StringUtils;
import org.n0pe.asadmin.AsAdminCmdList;
import org.n0pe.asadmin.commands.Deployment;

/**
 * @goal deploy
 * @description AsAdmin deploy mojo
 */
public class DeployMojo extends AbstractAsadminMojo {
  /**
     * @parameter
     */
  private String target;


<<<<<<< /usr/src/app/output/eskatos/asadmin/6fb154963dfd021025038412792ef1dcc5399017/asadmin-maven-plugin/src/main/java/org/n0pe/mojo/asadmin/DeployMojo.java/left.java
  /**
     * @parameter default-value="false"
     */
  private boolean force;
=======
  /**
     * @parameter
     */
  private Boolean availabilityenabled = null;
>>>>>>> /usr/src/app/output/eskatos/asadmin/6fb154963dfd021025038412792ef1dcc5399017/asadmin-maven-plugin/src/main/java/org/n0pe/mojo/asadmin/DeployMojo.java/right.java


  @Override protected AsAdminCmdList getAsCommandList() {
    getLog().info("Deploying application archive: " + appArchive);
    final AsAdminCmdList list = new AsAdminCmdList();
    final Deployment d = new Deployment().archive(appArchive).target(target);
    if ("war".equalsIgnoreCase(mavenProject.getPackaging()) && !StringUtils.isEmpty(contextRoot)) {
      d.withContextRoot(contextRoot);
    }
    if (!StringUtils.isEmpty(appName)) {
      d.appName(appName);
    }
    list.add(d.
<<<<<<< /usr/src/app/output/eskatos/asadmin/6fb154963dfd021025038412792ef1dcc5399017/asadmin-maven-plugin/src/main/java/org/n0pe/mojo/asadmin/DeployMojo.java/left.java
    force(force)
=======
    availability(availabilityenabled)
>>>>>>> /usr/src/app/output/eskatos/asadmin/6fb154963dfd021025038412792ef1dcc5399017/asadmin-maven-plugin/src/main/java/org/n0pe/mojo/asadmin/DeployMojo.java/right.java
    .deploy());
    setPatterns(d);
    return list;
  }
}