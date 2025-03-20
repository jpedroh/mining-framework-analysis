package com.cloudbees.plugins.credentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.BulkChange;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.Functions;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.model.ManagementLink;
import hudson.security.GlobalSecurityConfiguration;
import hudson.util.FormApply;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import jenkins.model.GlobalConfigurationCategory;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.interceptor.RequirePOST;

/**
 * {@link ManagementLink} to expose the global credentials configuration screen.
 *
 * @see CredentialsProviderManager.Configuration
 * @see GlobalCredentialsConfiguration.Category
 * @since 2.0
 */
@Extension(ordinal = Integer.MAX_VALUE - 212) public class GlobalCredentialsConfiguration extends ManagementLink implements Describable<GlobalCredentialsConfiguration> {
  /**
     * Our logger.
     */
  private static final Logger LOGGER = Logger.getLogger(GlobalSecurityConfiguration.class.getName());

  /**
     * Our filter.
     */
  @SuppressWarnings(value = { "rawtypes" }) public static final Predicate<Descriptor> FILTER = (d) -> d.getCategory() instanceof Category;

  /**
     * {@inheritDoc}
     */
  @Override public String getIconFileName() {
    return ExtensionList.lookup(CredentialsDescriptor.class).isEmpty() ? null : "/plugin/credentials/images/credentials.svg";
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getDisplayName() {
    return getDescriptor().getDisplayName();
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getDescription() {
    return Messages.GlobalCredentialsConfiguration_Description();
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getUrlName() {
    return "configureCredentials";
  }

  public String getCategoryName() {
    return "SECURITY";
  }

  /**
     * Handles the form submission
     *
     * @param req the request.
     * @return the response.
     * @throws IOException if something goes wrong.
     * @throws ServletException if something goes wrong.
     * @throws FormException if something goes wrong.
     */
  @RequirePOST @NonNull @Restricted(value = NoExternalUse.class) @SuppressWarnings(value = { "unused" }) public synchronized HttpResponse doConfigure(@NonNull StaplerRequest req) throws IOException, ServletException, FormException {
    Jenkins jenkins = Jenkins.get();
    jenkins.checkPermission(Jenkins.ADMINISTER);
    BulkChange bc = new BulkChange(jenkins);
    try {
      boolean result = configure(req, req.getSubmittedForm());
      LOGGER.log(Level.FINE, "credentials configuration saved: " + result);
      jenkins.save();
      return FormApply.success(result ? req.getContextPath() + "/manage" : req.getContextPath() + "/" + getUrlName());
    }  finally {
      bc.commit();
    }
  }

  /**
     * Performs the configuration.
     *
     * @param req  the request.
     * @param json the JSON object.
     * @return {@code false} to keep the client in the same config page.
     * @throws FormException if something goes wrong.
     */
  private boolean configure(StaplerRequest req, JSONObject json) throws FormException {
    Jenkins j = Jenkins.get();
    j.checkPermission(Jenkins.ADMINISTER);
    boolean result = true;
    for (Descriptor<?> d : Functions.getSortedDescriptorsForGlobalConfigByDescriptor(FILTER)) {
      result &= configureDescriptor(req, json, d);
    }
    return result;
  }

  /**
     * Performs the configuration of a specific {@link Descriptor}.
     *
     * @param req  the request.
     * @param json the JSON object.
     * @param d    the {@link Descriptor}.
     * @return {@code false} to keep the client in the same config page.
     * @throws FormException if something goes wrong.
     */
  private boolean configureDescriptor(StaplerRequest req, JSONObject json, Descriptor<?> d) throws FormException {
    String name = d.getJsonSafeClassName();
    JSONObject js = json.has(name) ? json.getJSONObject(name) : new JSONObject();
    json.putAll(js);
    return d.configure(req, js);
  }

  /**
     * {@inheritDoc}
     */
  @SuppressWarnings(value = { "unchecked" }) @Override public Descriptor<GlobalCredentialsConfiguration> getDescriptor() {
    return Jenkins.get().getDescriptorOrDie(getClass());
  }

  @Extension public static final class DescriptorImpl extends Descriptor<GlobalCredentialsConfiguration> {
    /**
         * {@inheritDoc}
         */
    @NonNull @Override public String getDisplayName() {
      return Messages.GlobalCredentialsConfiguration_DisplayName();
    }
  }

  @Extension @Symbol(value = "globalCredentialsConfiguration") public static class Category extends GlobalConfigurationCategory {
    /**
         * {@inheritDoc}
         */
    @Override public String getShortDescription() {
      return Messages.GlobalCredentialsConfiguration_Description();
    }

    /**
         * {@inheritDoc}
         */
    @Override public String getDisplayName() {
      return Messages.GlobalCredentialsConfiguration_DisplayName();
    }
  }
}