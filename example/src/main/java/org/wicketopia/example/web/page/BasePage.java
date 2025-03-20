package org.wicketopia.example.web.page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BasePage extends WebPage implements IHeaderContributor {
  private static final long serialVersionUID = 1L;

  @SpringBean private AuthenticationManager authenticationManager;

  public BasePage() {
    init();
  }

  private void init() {
    setOutputMarkupId(true);
    add(new Label("titleLabel", getTitleModel()).setRenderBodyOnly(true));
    add(new Label("captionLabel", getCaptionModel()).setRenderBodyOnly(true));
    add(new Label("copyrightLabel", resourceModel("page.copyright", new GregorianCalendar().get(Calendar.YEAR))).setEscapeModelStrings(false));
    add(new StyleSheetReference("stylesheet", BasePage.class, "style.css"));
    add(new FeedbackPanel("feedback").setOutputMarkupPlaceholderTag(true));
    add(new BookmarkablePageLink<Void>("homeLink", HomePage.class));
    add(new Link("login") {
      @Override public void onClick() {
        final UsernamePasswordAuthenticationToken tok = new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(tok));
        setResponsePage(BasePage.this.getClass());
        setRedirect(true);
      }

      @Override public boolean isVisible() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
      }
    });
    add(new Link("logout") {
      @Override public void onClick() {
        SecurityContextHolder.clearContext();
        setResponsePage(BasePage.this.getClass());
        setRedirect(true);
      }

      @Override public boolean isVisible() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
      }
    });
  }

  public BasePage(IModel<?> model) {
    super(model);
    init();
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public BasePage(IPageMap pageMap) {
    super(pageMap);
    init();
  }
>>>>>>> /usr/src/app/output/jwcarman/wicketopia/65dc57a69735d064d0c1c05fa67493b94c05b159/example/src/main/java/org/wicketopia/example/web/page/BasePage.java/right.java


  public BasePage(PageParameters parameters) {
    super(parameters);
    init();
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public BasePage(IPageMap pageMap, IModel<?> model) {
    super(pageMap, model);
    init();
  }
>>>>>>> /usr/src/app/output/jwcarman/wicketopia/65dc57a69735d064d0c1c05fa67493b94c05b159/example/src/main/java/org/wicketopia/example/web/page/BasePage.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  public BasePage(IPageMap pageMap, PageParameters parameters) {
    super(pageMap, parameters);
    init();
  }
>>>>>>> /usr/src/app/output/jwcarman/wicketopia/65dc57a69735d064d0c1c05fa67493b94c05b159/example/src/main/java/org/wicketopia/example/web/page/BasePage.java/right.java


  public void renderHead(IHeaderResponse header) {
    header.renderCSSReference(new PackageResourceReference(BasePage.class, "style.css"));
  }

  /**
     * Returns a model which can be used to set the page's caption.  This implementation
     * merely returns a {@link org.apache.wicket.model.ResourceModel} which corresponds to the "page.caption" localized
     * string for this page.
     *
     * @return a model which can be used to set the page's caption
     */
  protected IModel<String> getCaptionModel() {
    return resourceModel("page.caption");
  }

  /**
     * Returns a model which can be used to set the page's title.  This implementation
     * merely returns a {@link org.apache.wicket.model.ResourceModel} which corresponds to the "page.title" localized
     * string for this page.
     *
     * @return a model which can be used to set the page's title
     */
  protected IModel<String> getTitleModel() {
    return resourceModel("page.title");
  }

  /**
     * Creates a resource model which corresponds to this page's <code>key</code> localized
     * resource string.
     *
     * @param key    the resource string's key
     * @param params the optional parameters
     * @return a resource model which corresponds to this page's <code>key</code> localized
     *         resource string
     */
  protected IModel<String> resourceModel(String key, Object... params) {
    if (params == null || params.length == 0) {
      return new ResourceModel(key, "[" + key + "]");
    } else {
      return new StringResourceModel(key, this, null, params, "[" + key + "]");
    }
  }
}