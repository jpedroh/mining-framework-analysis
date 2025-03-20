package com.cloudbees.plugins.credentials.impl;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.Util;
import hudson.util.Secret;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Concrete implementation of {@link StandardUsernamePasswordCredentials}.
 *
 * @since 1.6
 */
@SuppressWarnings(value = { "unused" }) public class UsernamePasswordCredentialsImpl extends BaseStandardCredentials implements StandardUsernamePasswordCredentials {
  /**
     * The username.
     */
  @NonNull private final String username;

  /**
     * The password.
     */
  @NonNull private final Secret password;

  @Nullable private Boolean usernameSecret = false;

  /**
     * Constructor.
     *
     * @param scope       the credentials scope
     * @param id          the ID or {@code null} to generate a new one.
     * @param description the description.
     * @param username    the username.
     * @param password    the password.
     */
  @DataBoundConstructor @SuppressWarnings(value = { "unused" }) public UsernamePasswordCredentialsImpl(@CheckForNull CredentialsScope scope, @CheckForNull String id, @CheckForNull String description, @CheckForNull String username, @CheckForNull String password) {
    super(scope, id, description);
    this.username = Util.fixNull(username);
    this.password = Secret.fromString(password);
  }

  private Object readResolve() {
    if (usernameSecret == null) {
      usernameSecret = true;
    }
    return this;
  }

  /**
     * {@inheritDoc}
     */
  @NonNull public Secret getPassword() {
    return password;
  }

  /**
     * {@inheritDoc}
     */
  @NonNull public String getUsername() {
    return username;
  }

  @Override public boolean isUsernameSecret() {
    return Boolean.TRUE.equals(usernameSecret);
  }

  @DataBoundSetter public void setUsernameSecret(boolean usernameSecret) {
    this.usernameSecret = usernameSecret;
  }

  @Extension(ordinal = 1) @Symbol(value = "usernamePassword") public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
    /**
         * {@inheritDoc}
         */
    @NonNull @Override public String getDisplayName() {
      return Messages.UsernamePasswordCredentialsImpl_DisplayName();
    }

    /**
         * {@inheritDoc}
         */
    @Override public String getIconClassName() {
      return "icon-credentials-userpass";
    }
  }
}