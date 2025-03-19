package br.com.caelum.vraptor.authz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.authz.annotation.AuthzBypass;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Default authorization interceptor implementation. Check for situations on the
 * test cases.
 * 
 * @author douglas campos
 * @author guilherme silveira
 */
@Intercepts @RequestScoped public class Authz implements Interceptor {
  private static final Logger log = LoggerFactory.getLogger(Authz.class);

  private final AuthzInfo authInfo;

  private final Authorizator authorizator;

  private final Result result;

  public Authz(Authorizator authorizator, AuthzInfo authInfo, Result result) {
    this.authorizator = authorizator;
    this.authInfo = authInfo;
    this.result = result;
  }

  @Override public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
    if (
<<<<<<< /usr/src/app/output/turini/vraptor-authz/ff9a0b0405f0d759760b73d8963d788c837bc9d0/src/main/java/br/com/caelum/vraptor/authz/Authz.java/left.java
    authorizable != null && isAllowed(method, authorizable)
=======
    authInfo != null
>>>>>>> /usr/src/app/output/turini/vraptor-authz/ff9a0b0405f0d759760b73d8963d788c837bc9d0/src/main/java/br/com/caelum/vraptor/authz/Authz.java/right.java
    ) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
      Authorizable authorizable = authInfo.getAuthorizable();
>>>>>>> /usr/src/app/output/turini/vraptor-authz/ff9a0b0405f0d759760b73d8963d788c837bc9d0/src/main/java/br/com/caelum/vraptor/authz/Authz.java/right.java

      if (authorizable != null) {
        Set<Role> roles = authorizable.roles();
        for (Role role : roles) {
          if (authorizator.isAllowed(role, method)) {
            stack.next(method, resourceInstance);
            return;
          }
        }
      }

<<<<<<< /usr/src/app/output/turini/vraptor-authz/ff9a0b0405f0d759760b73d8963d788c837bc9d0/src/main/java/br/com/caelum/vraptor/authz/Authz.java/left.java
      stack
=======
      authInfo
>>>>>>> /usr/src/app/output/turini/vraptor-authz/ff9a0b0405f0d759760b73d8963d788c837bc9d0/src/main/java/br/com/caelum/vraptor/authz/Authz.java/right.java
      .
<<<<<<< /usr/src/app/output/turini/vraptor-authz/ff9a0b0405f0d759760b73d8963d788c837bc9d0/src/main/java/br/com/caelum/vraptor/authz/Authz.java/left.java
      next(method, resourceInstance)
=======
      handleAuthError(result)
>>>>>>> /usr/src/app/output/turini/vraptor-authz/ff9a0b0405f0d759760b73d8963d788c837bc9d0/src/main/java/br/com/caelum/vraptor/authz/Authz.java/right.java
      ;
      return;
    } else {
      log.error("no AuthInfo found!");
      throw new IllegalStateException("No AuthInfo found");
    }
  }

  private boolean isAllowed(ResourceMethod method, Authorizable authorizable) {
    for (Role role : authorizable.roles()) {
      if (authorizator.isAllowed(role, method)) {
        return true;
      }
    }
    return false;
  }

  @Override public boolean accepts(ResourceMethod method) {
    if (method.getMethod().isAnnotationPresent(AuthzBypass.class)) {
      return false;
    }
    return true;
  }
}