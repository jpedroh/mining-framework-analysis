package org.uma.jmetal.util;

import java.io.Serializable;
import java.util.logging.Logger;

/**
<<<<<<< /usr/src/app/output/jmetal/jmetal/603a9332cec32fcec56db88c2c486c419723d77c/jmetal-core/src/main/java/org/uma/jmetal/util/JMetalLogger.java/left.java
 * This class provides a logger.
||||||| /usr/src/app/output/jmetal/jmetal/603a9332cec32fcec56db88c2c486c419723d77c/jmetal-core/src/main/java/org/uma/jmetal/util/JMetalLogger.java/base.java
 * This class provides some facilities to manage loggers. One might use the
 * static logger of this class or use its own, custom logger. Also, we provide
 * the static method {@link #configureLoggers(File)} for configuring the loggers
 * easily. This method is automatically called before any use of the static
 * logger, but if you want it to apply on other loggers it is preferable to call
 * it explicitly at the beginning of your main() method.
=======
 * This class provides some facilities provides a logger
>>>>>>> /usr/src/app/output/jmetal/jmetal/603a9332cec32fcec56db88c2c486c419723d77c/jmetal-core/src/main/java/org/uma/jmetal/util/JMetalLogger.java/right.java
 * 
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class JMetalLogger implements Serializable {

	public static final Logger logger = Logger.getLogger(JMetalLogger.class
			.getName());
}
