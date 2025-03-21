package org.sonar.plugins.ldap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nullable;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 * @author Evgeny Mandrikov
 */
public final class ContextHelper {
  private static final Logger LOG = LoggerFactory.getLogger(ContextHelper.class);

  private ContextHelper() {
  }

  /**
	 * <pre>
	 * public void useContextNicely() throws NamingException {
	 * 	InitialDirContext context = null;
	 * 	boolean threw = true;
	 * 	try {
	 * 		context = new InitialDirContext();
	 * 		// Some code which does something with the Context and may throw a
	 * 		// NamingException
	 * 		threw = false; // No throwable thrown
	 * 	} finally {
	 * 		// Close context
	 * 		// If an exception occurs, only rethrow it if (threw==false)
	 * 		close(context, threw);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param context
	 *            the {@code Context} object to be closed, or null, in which
	 *            case this method does nothing
	 * @param swallowIOException
	 *            if true, don't propagate {@code NamingException} thrown by the
	 *            {@code close} method
	 * @throws NamingException
	 *             if {@code swallowIOException} is false and {@code close}
	 *             throws a {@code NamingException}.
	 */
  public static void close(@Nullable Context context, boolean swallowIOException) throws NamingException {
    if (context == null) {
      return;
    }
    try {
      context.close();
    } catch (NamingException e) {
      if (swallowIOException) {
        LOG.warn("NamingException thrown while closing context.", e);
      } else {
        throw e;
      }
    }
  }

  public static void closeQuetly(@Nullable Context context) {
    try {
      close(context, true);
    } catch (NamingException e) {
      LOG.error("Unexpected NamingException", e);
    }
  }
}