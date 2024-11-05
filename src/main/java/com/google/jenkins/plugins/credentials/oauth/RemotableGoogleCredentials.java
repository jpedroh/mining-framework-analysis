package com.google.jenkins.plugins.credentials.oauth;
import static com.google.common.base.Preconditions.checkNotNull;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Ordering;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.joda.time.DateTime;

/**
 * As some implementations of {@link GoogleRobotCredentials} are bound to the controller, this
 * ephemeral credential is remoted in place of those. The use case is basically that when a plugin
 * needs to remote credential C, with some requirement R, it would instead remote {@code
 * C.forRemote(R)} to instantiate one of these.
 *
 * <p>TODO(mattmoor): Consider ways to use channels to remove the time limitation that this has
 * (access token expires).
 *
 * @author Matt Moore
 */
final class RemotableGoogleCredentials extends GoogleRobotCredentials {
  /**
   * Construct a remotable credential. This should never be used directly, which is why this class
   * is {@code package-private}. This should only be called from {@link
   * GoogleRobotCredentials#forRemote}.
   */
  @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "False positive from what I can see in Ordering.natural().nullsFirst()") public RemotableGoogleCredentials(GoogleRobotCredentials credentials, GoogleOAuth2ScopeRequirement requirement, GoogleRobotCredentialsModule module) throws GeneralSecurityException {
    super(credentials.getCredentialsScope() == null ? CredentialsScope.GLOBAL : credentials.getScope(), "", checkNotNull(credentials).getProjectId(), checkNotNull(module));
    this.username = credentials.getUsername();
    Credential credential = credentials.getGoogleCredential(checkNotNull(requirement));
    try {
      Long rawExpiration = credential.getExpiresInSeconds();
      if (Ordering.natural().nullsFirst().compare(rawExpiration, MINIMUM_DURATION_SECONDS) < 0) {
        if (!credential.refreshToken()) {
          throw new GeneralSecurityException(Messages.RemotableGoogleCredentials_NoAccessToken());
        }
      }
    } catch (IOException e) {
      throw new GeneralSecurityException(Messages.RemotableGoogleCredentials_NoAccessToken(), e);
    }
    this.accessToken = checkNotNull(credential.getAccessToken());
    this.expiration = new DateTime().plusSeconds(checkNotNull(credential.getExpiresInSeconds()).intValue()).getMillis();
  }

  /**
   * Construct a remotable credential. This should never be used directly - this constructor is only
   * for migrating old credentials that had no id and relied on the projectId during readResolve().
   */
  private RemotableGoogleCredentials(CredentialsScope scope, String id, String projectId, GoogleRobotCredentialsModule module, String username, String accessToken, long expiration) {
    super(scope, id, projectId, module);
    this.username = username;
    this.accessToken = accessToken;
    this.expiration = expiration;
  }

  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", justification = "for migrating older credentials that did not have a separate id field, and would really " + "have a null id when attempted to deserialize. readResolve overwrites these nulls") private Object readResolve() throws Exception {
    return new RemotableGoogleCredentials(getCredentialsScope() == null ? CredentialsScope.GLOBAL : getCredentialsScope(), getId() == null ? getProjectId() : getId(), getProjectId(), getModule(), username, accessToken, expiration);
  }

  /** {@inheritDoc} */
  @Override public CredentialsScope getScope() {
    return getCredentialsScope();
  }

  /** {@inheritDoc} */
  @Override public AbstractGoogleRobotCredentialsDescriptor getDescriptor() {
    throw new UnsupportedOperationException(Messages.RemotableGoogleCredentials_BadGetDescriptor());
  }

  /** {@inheritDoc} */
  @Override public String getUsername() {
    return username;
  }

  /** {@inheritDoc} */
  @Override public Credential getGoogleCredential(GoogleOAuth2ScopeRequirement requirement) throws GeneralSecurityException {
    long lifetimeSeconds = (expiration - new DateTime().getMillis()) / 1000;
    return new GoogleCredential.Builder().setTransport(getModule().getHttpTransport()).setJsonFactory(getModule().getJsonFactory()).build().setAccessToken(accessToken).setExpiresInSeconds(lifetimeSeconds);
  }

  /** The identity of the credential. */
  private final String username;

  /** The access token eagerly retrieved from the original credential. */
  private final String accessToken;

  /** The time at which the accessToken will expire. */
  private final long expiration;

  /**
   * The minimum duration {@code 5 minutes} to allow for an access token before attempting to
   * refresh it.
   */
  private static final Long MINIMUM_DURATION_SECONDS = 300L;
}