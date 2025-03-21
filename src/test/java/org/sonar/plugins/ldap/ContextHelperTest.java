package org.sonar.plugins.ldap;
import org.junit.Test;
import javax.naming.Context;
import javax.naming.NamingException;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class ContextHelperTest {
  @Test public void shouldSwallow() throws Exception {
    Context context = mock(Context.class);
    doThrow(new NamingException()).when(context).close();
    ContextHelper.close(context, true);
    ContextHelper.closeQuetly(context);
  }

  @Test(expected = NamingException.class) public void shouldNotSwallow() throws Exception {
    Context context = mock(Context.class);
    doThrow(new NamingException()).when(context).close();
    ContextHelper.close(context, false);
  }

  @Test public void normal() throws NamingException {
    ContextHelper.close(null, true);
    ContextHelper.closeQuetly(null);
    ContextHelper.close(mock(Context.class), true);
  }
}