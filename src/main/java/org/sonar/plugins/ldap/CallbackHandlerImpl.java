package org.sonar.plugins.ldap;
import javax.security.auth.callback.*;
import java.io.IOException;

/**
 * @author Evgeny Mandrikov
 */
public class CallbackHandlerImpl implements CallbackHandler {
  private String name;

  private String password;

  public CallbackHandlerImpl(String name, String password) {
    this.name = name;
    this.password = password;
  }

  public void handle(Callback[] callbacks) throws UnsupportedCallbackException, IOException {
    for (Callback callBack : callbacks) {
      if (callBack instanceof NameCallback) {
        NameCallback nameCallback = (NameCallback) callBack;
        nameCallback.setName(name);
      } else {
        if (callBack instanceof PasswordCallback) {
          PasswordCallback passwordCallback = (PasswordCallback) callBack;
          passwordCallback.setPassword(password.toCharArray());
        } else {
          throw new UnsupportedCallbackException(callBack, "Callback not supported");
        }
      }
    }
  }
}