package org.teknux.jettybootstrap.handler;
import java.text.MessageFormat;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.teknux.jettybootstrap.JettyBootstrapException;

abstract public class AbstractAppJettyHandler extends AbstractJettyHandler {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  private final static Logger logger = LoggerFactory.getLogger(AbstractAppJettyHandler.class);
>>>>>>> /usr/src/app/output/teknux-org/jetty-bootstrap/2852e8553b9363628dc21c1186599e714ee1cdad/jetty-bootstrap/src/main/java/org/teknux/jettybootstrap/handler/AbstractAppJettyHandler.java/right.java


  private String contextPath = null;

  public String getContextPath() {
    return contextPath;
  }

  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  @Override protected Handler createHandler() throws JettyBootstrapException {
    WebAppContext webAppContext = new WebAppContext();
    return initWebAppContext(webAppContext);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public static String[] addConfigurationClasses(String[] defaultConfigurationClasses, AdditionalWebAppJettyConfigurationClass[] additionalsWebappConfigurationClasses) {
    List<String> configurationClasses = new ArrayList<String>(Arrays.asList(defaultConfigurationClasses));
    for (AdditionalWebAppJettyConfigurationClass additionalWebappConfigurationClass : additionalsWebappConfigurationClasses) {
      if (additionalWebappConfigurationClass.getClasses() == null || additionalWebappConfigurationClass.getPosition() == null) {
        logger.warn("Bad support class name");
      } else {
        if (classesExists(additionalWebappConfigurationClass.getClasses())) {
          int index = 0;
          if (additionalWebappConfigurationClass.getReferenceClass() == null) {
            if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
              index = configurationClasses.size();
            }
          } else {
            index = configurationClasses.indexOf(additionalWebappConfigurationClass.getReferenceClass());
            if (index == -1) {
              if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
                logger.warn("[{}] reference unreachable, add at the end", additionalWebappConfigurationClass.getReferenceClass());
                index = configurationClasses.size();
              } else {
                logger.warn("[{}] reference unreachable, add at the top", additionalWebappConfigurationClass.getReferenceClass());
                index = 0;
              }
            } else {
              if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
                index++;
              }
            }
          }
          configurationClasses.addAll(index, additionalWebappConfigurationClass.getClasses());
          for (String className : additionalWebappConfigurationClass.getClasses()) {
            logger.debug("[{}] support added", className);
          }
        } else {
          for (String className : additionalWebappConfigurationClass.getClasses()) {
            logger.debug("[{}] not available", className);
          }
        }
      }
    }
    for (String configurationClasse : configurationClasses) {
      logger.trace("Jetty WebAppContext Configuration => " + configurationClasse);
    }
    return configurationClasses.toArray(new String[configurationClasses.size()]);
  }
>>>>>>> /usr/src/app/output/teknux-org/jetty-bootstrap/2852e8553b9363628dc21c1186599e714ee1cdad/jetty-bootstrap/src/main/java/org/teknux/jettybootstrap/handler/AbstractAppJettyHandler.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  public static boolean classesExists(List<String> classNames) {
    for (String className : classNames) {
      try {
        Class.forName(className);
      } catch (ClassNotFoundException e) {
        return false;
      }
    }
    return true;
  }
>>>>>>> /usr/src/app/output/teknux-org/jetty-bootstrap/2852e8553b9363628dc21c1186599e714ee1cdad/jetty-bootstrap/src/main/java/org/teknux/jettybootstrap/handler/AbstractAppJettyHandler.java/right.java


  /**
	 * The name of Temporary Application directory
	 * 
	 * @return name
	 */
  abstract public String getAppTempDirName();

  abstract protected WebAppContext initWebAppContext(WebAppContext webAppContext);


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * Create constraint which redirect to Secure Port
	 * 
	 * @return @ConstraintSecurityHandler
	 */
  public static ConstraintSecurityHandler getConstraintSecurityHandlerConfidential() {
    Constraint constraint = new Constraint();
    constraint.setDataConstraint(Constraint.DC_CONFIDENTIAL);
    ConstraintMapping constraintMapping = new ConstraintMapping();
    constraintMapping.setConstraint(constraint);
    constraintMapping.setPathSpec("/*");
    ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
    constraintSecurityHandler.addConstraintMapping(constraintMapping);
    return constraintSecurityHandler;
  }
>>>>>>> /usr/src/app/output/teknux-org/jetty-bootstrap/2852e8553b9363628dc21c1186599e714ee1cdad/jetty-bootstrap/src/main/java/org/teknux/jettybootstrap/handler/AbstractAppJettyHandler.java/right.java


  @Override public String toString() {
    return MessageFormat.format("{0} on contextPath [{1}]", super.toString(), getContextPath());
  }
}