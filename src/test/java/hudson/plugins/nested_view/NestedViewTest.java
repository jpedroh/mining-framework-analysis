package hudson.plugins.nested_view;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import hudson.model.AllView;
import hudson.model.Cause.UserCause;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import static hudson.model.Result.*;
import hudson.security.csrf.CrumbIssuer;
import static hudson.util.FormValidation.Kind.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Bug;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;

/**
 * Test interaction of nested-view plugin with Jenkins core.
 * @author Alan Harder
 */
public class NestedViewTest {
  @Rule public JenkinsRule rule = new JenkinsRule();

  @Test public void test() throws Exception {
    rule.createFreeStyleProject("Abcd");
    rule.createFreeStyleProject("Efgh");
    WebClient wc = rule.createWebClient();
    HtmlForm form = wc.goTo("newView").getFormByName("createItem");
    form.getInputByName("name").setValueAttribute("test-nest");
    form.getInputByValue("hudson.plugins.nested_view.NestedView").setChecked(true);
    rule.submit(form);
    form = wc.goTo("view/test-nest/newView").getFormByName("createItem");
    form.getInputByName("name").setValueAttribute("subview");
    form.getInputByValue("hudson.model.ListView").setChecked(true);
    form = rule.submit(form).getFormByName("viewConfig");
    form.getInputByName("useincluderegex").setChecked(true);
    form.getInputByName("includeRegex").setValueAttribute("E.*");
    rule.submit(form);
    form = wc.goTo("view/test-nest/newView").getFormByName("createItem");
    form.getInputByName("name").setValueAttribute("subnest");
    form.getInputByValue("hudson.plugins.nested_view.NestedView").setChecked(true);
    rule.submit(form);
    form = wc.goTo("view/test-nest/newView").getFormByName("createItem");
    form.getInputByName("name").setValueAttribute("suball");
    form.getInputByValue("hudson.model.AllView").setChecked(true);
    rule.submit(form);
    HtmlPage page = wc.goTo("view/test-nest/");
    assertNotNull(page.getAnchorByHref("/jenkins/view/test-nest/view/subview/"));
    assertNotNull(page.getAnchorByHref("/jenkins/view/test-nest/view/subnest/"));
    assertNotNull(page.getAnchorByHref("/jenkins/view/test-nest/view/suball/"));
    form = wc.goTo("view/test-nest/configure").getFormByName("viewConfig");
    List<HtmlOption> options = form.getSelectByName("defaultView").getOptions();
    assertEquals("", options.get(0).getValueAttribute());
    assertEquals("suball", options.get(1).getValueAttribute());
    assertEquals("subview", options.get(2).getValueAttribute());
    assertEquals(3, options.size());
    options.get(1).setSelected(true);
    rule.submit(form);
    page = wc.goTo("view/test-nest/");
    assertNotNull(page.getAnchorByHref("job/Efgh/"));
    page = wc.goTo("view/test-nest/view/subnest/");
    assertNotNull(page.getAnchorByHref("/jenkins/view/test-nest/view/subnest/newView"));
  }

  @Test public void testGetWorstResult() throws Exception {
    NestedView view = new NestedView("test");
    view.setOwner(rule.jenkins);
    assertSame(null, NestedView.getWorstResult(view));
    view.addView(new AllView("foo", view));
    assertSame(null, NestedView.getWorstResult(view));
    FreeStyleProject p = rule.createFreeStyleProject();
    assertSame(null, NestedView.getWorstResult(view));
    rule.assertBuildStatusSuccess(p.scheduleBuild2(0, new UserCause()).get());
    assertSame(SUCCESS, NestedView.getWorstResult(view));
    FreeStyleProject bad = rule.createFreeStyleProject();
    bad.getBuildersList().add(new FailureBuilder());
    assertSame(SUCCESS, NestedView.getWorstResult(view));
    rule.assertBuildStatus(FAILURE, bad.scheduleBuild2(0, new UserCause()).get());
    assertSame(FAILURE, NestedView.getWorstResult(view));
    bad.disable();
    assertSame(SUCCESS, NestedView.getWorstResult(view));
  }

  @Test public void testStatusOfEmptyNest() throws Exception {
    NestedView parent = new NestedView("parent");
    parent.setOwner(rule.jenkins);
    NestedView child = new NestedView("child");
    parent.addView(child);
    assertSame(null, NestedView.getWorstResult(child));
    assertSame(null, NestedView.getWorstResult(parent));
  }

  @Test public void testDoViewExistsCheck() {
    NestedView view = new NestedView("test");
    view.setOwner(rule.jenkins);
    view.addView(new ListView("foo", view));
    assertSame(OK, view.doViewExistsCheck(null).kind);
    assertSame(OK, view.doViewExistsCheck("").kind);
    assertSame(OK, view.doViewExistsCheck("bar").kind);
    assertSame(ERROR, view.doViewExistsCheck("foo").kind);
  }

  @Bug(value = 25315) public void testUploadXml() throws Exception {
    NestedView parent = new NestedView("parent");
    rule.jenkins.addView(parent);
    assertEquals(rule.jenkins, parent.getOwner());
    NestedView child = new NestedView("child");
    parent.addView(child);
    child.setOwner(parent);
    WebClient wc = rule.createWebClient();
    wc.goTo("view/parent/view/child/");
    String xml = wc.goToXml("view/parent/config.xml").getContent();
    assertFalse(xml, xml.contains("<owner"));
    CrumbIssuer issuer = rule.jenkins.getCrumbIssuer();
    WebRequest req = new WebRequest(new URL(rule.getURL(), "/createView?name=clone&" + issuer.getDescriptor().getCrumbRequestField() + "=" + issuer.getCrumb(null)), HttpMethod.POST);
    req.setAdditionalHeader("Content-Type", "application/xml");
    req.setRequestBody(xml);
    wc.getPage(req);
    NestedView clone = (NestedView) rule.jenkins.getView("clone");
    assertNotNull(clone);
    assertEquals(rule.jenkins, clone.getOwner());
    child = (NestedView) clone.getView("child");
    assertNotNull(child);
    assertEquals(clone, child.getOwner());
    wc.goTo("view/clone/view/child/");
    req = new WebRequest(wc.createCrumbedUrl("view/parent/config.xml"), HttpMethod.POST);
    req.setAdditionalHeader("Content-Type", "application/xml");
    req.setRequestBody(xml);
    wc.getPage(req);
    parent = (NestedView) rule.jenkins.getView("parent");
    assertNotNull(parent);
    assertEquals(rule.jenkins, parent.getOwner());
    child = (NestedView) parent.getView("child");
    assertNotNull(child);
    assertEquals(parent, child.getOwner());
    wc.goTo("view/parent/view/child/");
  }

  @Test(expected = FailingHttpStatusCodeException.class) public void testDotConfigXmlOwnerSettings() throws Exception {
    NestedView root = new NestedView("nestedRoot");
    root.setOwner(rule.jenkins);
    ListView viewLevel1 = new ListView("listViewlvl1", root);
    NestedView subviewLevel1 = new NestedView("nestedViewlvl1");
    subviewLevel1.setOwner(root);
    NestedView subviewLevel2 = new NestedView("nestedViewlvl2");
    subviewLevel2.setOwner(subviewLevel1);
    ListView viewLevel2 = new ListView("listViewlvl2", subviewLevel1);
    ListView viewLevel3 = new ListView("listViewlvl3", subviewLevel2);
    root.addView(viewLevel1);
    root.addView(subviewLevel1);
    subviewLevel1.addView(subviewLevel2);
    subviewLevel1.addView(viewLevel2);
    subviewLevel2.addView(viewLevel3);
    rule.jenkins.addView(root);
    root.save();
    WebClient wc = rule.createWebClient();
    URL url = new URL(rule.jenkins.getRootUrl() + root.getUrl() + "config.xml");
    XmlPage page = wc.getPage(url);
    String configDotXml = page.getWebResponse().getContentAsString();
    configDotXml = configDotXml.replace("listViewlvl1", "new");
    url = new URL(rule.jenkins.getRootUrl() + root.getUrl() + "config.xml/?.crumb=test");
    WebRequest s = new WebRequest(url, HttpMethod.POST);
    s.setRequestBody(configDotXml);
    wc.addRequestHeader("Content-Type", "application/xml");
    HtmlPage p = wc.getPage(s);
  }

  @Ignore(value = "TODO pending baseline with https://github.com/jenkinsci/jenkins/pull/1798") @Issue(value = "JENKINS-25276") @Test public void testRenameJob() throws IOException {
    FreeStyleProject project = rule.createFreeStyleProject("project");
    NestedView view = new NestedView("nested");
    view.setOwner(rule.jenkins);
    rule.jenkins.addView(view);
    ListView subview = new ListView("listView", view);
    view.addView(subview);
    subview.add(project);
    assertTrue("Subview \'listView\' should contains item \'project\'", subview.contains(project));
    project.renameTo("project-renamed");
    assertTrue("Subview contains renamed item.", subview.contains(project));
  }

  @Issue(value = "JENKINS-59466") @Test public void testSetViewNoOwner() throws IOException {
    FreeStyleProject project = rule.createFreeStyleProject("project");
    NestedView view = new NestedView("nested");
    rule.jenkins.addView(view);
    assertEquals("Jenkins", view.getOwner().getDisplayName());
    ListView subview = new ListView("listView", view);
    view.addView(subview);
    subview.add(project);
    assertTrue("Subview \'listView\' should contains item \'project\'", subview.contains(project));
  }
}