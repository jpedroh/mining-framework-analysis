package org.sonar.plugins.ldap;
import org.junit.Test;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CallbackHandlerImplTest {
  @Test public void test() throws Exception {
    NameCallback nameCallback = new NameCallback("username");
    PasswordCallback passwordCallback = new PasswordCallback("password", false);
    new CallbackHandlerImpl("tester", "secret").handle(new Callback[] { nameCallback, passwordCallback });
    assertThat(nameCallback.getName()).isEqualTo("tester");
    assertThat(passwordCallback.getPassword()).isEqualTo("secret".toCharArray());
  }

  @Test(expected = UnsupportedCallbackException.class) public void unsupportedCallback() throws Exception {
    new CallbackHandlerImpl("tester", "secret").handle(new Callback[] { mock(Callback.class) });
  }
}