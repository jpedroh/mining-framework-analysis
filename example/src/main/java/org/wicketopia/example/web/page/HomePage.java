package org.wicketopia.example.web.page;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.wicketopia.persistence.PersistenceProvider;

/**
 * Homepage
 */
public class HomePage extends BasePage {
  private static final long serialVersionUID = 1L;

  @SpringBean private PersistenceProvider persistenceProvider;

  @SpringBean private AuthenticationManager authenticationManager;

  public HomePage() {

<<<<<<< /usr/src/app/output/jwcarman/wicketopia/65dc57a69735d064d0c1c05fa67493b94c05b159/example/src/main/java/org/wicketopia/example/web/page/HomePage.java/left.java
    add(new Scaffold<Person>("scaffold", Person.class, persistenceProvider));
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/jwcarman/wicketopia/65dc57a69735d064d0c1c05fa67493b94c05b159/example/src/main/java/org/wicketopia/example/web/page/HomePage.java/left.java
    add(new Link("loginAdmin") {
      @Override public void onClick() {
        final UsernamePasswordAuthenticationToken tok = new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(tok));
        setResponsePage(HomePage.class);
      }

      @Override public boolean isVisible() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
      }
    });
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/jwcarman/wicketopia/65dc57a69735d064d0c1c05fa67493b94c05b159/example/src/main/java/org/wicketopia/example/web/page/HomePage.java/left.java
    add(new Link("logout") {
      @Override public void onClick() {
        SecurityContextHolder.clearContext();
        setResponsePage(HomePage.class);
      }

      @Override public boolean isVisible() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
      }
    });
=======
>>>>>>> Unknown file: This is a bug in JDime.
  }
}