package com.google.jenkins.plugins.credentials.oauth;
import static com.google.api.client.http.HttpStatusCodes.STATUS_CODE_NOT_FOUND;
import static com.google.api.client.http.HttpStatusCodes.STATUS_CODE_OK;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.jenkins.plugins.util.MetadataReader;
import hudson.Extension;
import hudson.util.FormValidation;
import java.io.IOException;
import java.util.List;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Tests for {@link GoogleRobotMetadataCredentials}. */
public class GoogleRobotMetadataCredentialsTest {
  @Rule public JenkinsRule jenkins = new JenkinsRule();

  @Mock private GoogleCredential credential;

  private static final CredentialsScope CREDENTIALS_SCOPE = CredentialsScope.GLOBAL;

  public static class Module extends GoogleRobotMetadataCredentialsModule {
    @Override public MetadataReader getMetadataReader() {
      return reader;
    }

    public final MockHttpTransport transport = spy(new MockHttpTransport());

    public final MetadataReader reader = new MetadataReader.Default(transport.createRequestFactory());

    public final MockLowLevelHttpRequest request = spy(new MockLowLevelHttpRequest());

    public void stubRequest(String url, int statusCode, String responseContent) throws IOException {
      request.setResponse(new MockLowLevelHttpResponse().setStatusCode(statusCode).setContent(responseContent));
      doReturn(request).when(transport).buildRequest("GET", url);
    }

    private void verifyRequest(String url) throws IOException {
      verify(transport).buildRequest("GET", url);
      verify(request).execute();
      assertEquals("Google", getOnlyElement(request.getHeaderValues("Metadata-Flavor")));
    }

    @Override public HttpTransport getHttpTransport() {
      return transport;
    }
  }

  @Extension public static class MockDescriptor extends GoogleRobotMetadataCredentials.Descriptor {
    public MockDescriptor() {
      super(new Module());
    }
  }

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    GoogleRobotMetadataCredentials.Descriptor.disableForTesting = true;
  }

  @Test @WithoutJenkins public void accessTokenTest() throws Exception {
    final Module module = new Module();
    GoogleRobotMetadataCredentials newCreds = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", PROJECT_ID, module);
    Credential cred = newCreds.getGoogleCredential(new TestGoogleOAuth2DomainRequirement(FAKE_SCOPE));
    module.stubRequest(METADATA_ENDPOINT, STATUS_CODE_OK, "{\"access_token\":\"" + ACCESS_TOKEN + "\"," + "\"expires_in\":1234," + "\"token_type\":\"Bearer\"}");
    try {
      assertTrue(cred.refreshToken());
      assertEquals(ACCESS_TOKEN, cred.getAccessToken());
    }  finally {
      module.verifyRequest(METADATA_ENDPOINT);
    }
  }

  @Test @WithoutJenkins public void getUsernameTest() throws Exception {
    final Module module = new Module();
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", PROJECT_ID, module);
    module.stubRequest("http://metadata/computeMetadata/v1/instance/" + "service-accounts/default/email", STATUS_CODE_OK, USERNAME);
    assertEquals(USERNAME, credentials.getUsername());
    assertEquals(CredentialsScope.GLOBAL, credentials.getScope());
  }

  @Test(expected = IllegalStateException.class) @WithoutJenkins public void getUsernameWithNotFoundExceptionTest() throws Exception {
    final Module module = new Module();
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", PROJECT_ID, module);
    module.stubRequest("http://metadata/computeMetadata/v1/instance/" + "service-accounts/default/email", STATUS_CODE_NOT_FOUND, USERNAME);
    credentials.getUsername();
  }

  @Test(expected = IllegalStateException.class) @WithoutJenkins public void getUsernameWithUnknownIOExceptionTest() throws Exception {
    final Module module = new Module();
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", PROJECT_ID, module);
    module.stubRequest("http://metadata/computeMetadata/v1/instance/" + "service-accounts/default/email", 409, USERNAME);
    credentials.getUsername();
  }

  @Test public void defaultProjectTest() throws Exception {
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", "doesn\'t matter", null);
    final GoogleRobotMetadataCredentials.Descriptor descriptor = credentials.getDescriptor();
    final Module module = (Module) descriptor.getModule();
    module.stubRequest("http://metadata/computeMetadata/v1/project/project-id", STATUS_CODE_OK, PROJECT_ID);
    assertEquals(PROJECT_ID, descriptor.defaultProject());
  }

  @Test public void defaultProjectNotFoundTest() throws Exception {
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", "doesn\'t matter", null);
    final GoogleRobotMetadataCredentials.Descriptor descriptor = credentials.getDescriptor();
    final Module module = (Module) descriptor.getModule();
    module.stubRequest("http://metadata/computeMetadata/v1/project/project-id", STATUS_CODE_NOT_FOUND, PROJECT_ID);
    assertNull(descriptor.defaultProject());
  }

  @Test public void defaultProjectUnknownIOExceptionTest() throws Exception {
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", "doesn\'t matter", null);
    final GoogleRobotMetadataCredentials.Descriptor descriptor = credentials.getDescriptor();
    final Module module = (Module) descriptor.getModule();
    module.stubRequest("http://metadata/computeMetadata/v1/project/project-id", 409, PROJECT_ID);
    assertNull(descriptor.defaultProject());
  }

  @Test public void defaultScopesTest() throws Exception {
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", "doesn\'t matter", null);
    final GoogleRobotMetadataCredentials.Descriptor descriptor = credentials.getDescriptor();
    final Module module = (Module) descriptor.getModule();
    module.stubRequest("http://metadata/computeMetadata/v1/instance/" + "service-accounts/default/scopes", STATUS_CODE_OK, Joiner.on("\n").join(SCOPES));
    assertEquals(SCOPES, descriptor.defaultScopes());
  }

  @Test public void defaultScopesNotFoundTest() throws Exception {
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", "doesn\'t matter", null);
    final GoogleRobotMetadataCredentials.Descriptor descriptor = credentials.getDescriptor();
    final Module module = (Module) descriptor.getModule();
    module.stubRequest("http://metadata/computeMetadata/v1/instance/" + "service-accounts/default/scopes", STATUS_CODE_NOT_FOUND, Joiner.on("\n").join(SCOPES));
    assertEquals(0, descriptor.defaultScopes().size());
  }

  @Test public void defaultScopesUnknownIOExceptionTest() throws Exception {
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", "doesn\'t matter", null);
    final GoogleRobotMetadataCredentials.Descriptor descriptor = credentials.getDescriptor();
    final Module module = (Module) descriptor.getModule();
    module.stubRequest("http://metadata/computeMetadata/v1/instance/" + "service-accounts/default/scopes", 409, Joiner.on("\n").join(SCOPES));
    assertEquals(0, descriptor.defaultScopes().size());
  }

  @Test public void testGetById() throws Exception {
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", PROJECT_ID, null);
    SystemCredentialsProvider.getInstance().getCredentials().add(credentials);
    Module module = (Module) credentials.getDescriptor().getModule();
    module.stubRequest("http://metadata/computeMetadata/v1/instance/" + "service-accounts/default/scopes", STATUS_CODE_OK, "does.not.Matter");
    assertSame(credentials, GoogleRobotCredentials.getById(credentials.getId()));
  }

  @Test public void testName() throws Exception {
    GoogleRobotMetadataCredentials credentials = new GoogleRobotMetadataCredentials(CREDENTIALS_SCOPE, "", PROJECT_ID, null);
    SystemCredentialsProvider.getInstance().getCredentials().add(credentials);
    assertEquals(PROJECT_ID, CredentialsNameProvider.name(credentials));
    assertEquals(PROJECT_ID, new GoogleRobotNameProvider().getName(credentials));
  }

  @Test public void testProjectIdValidation() throws Exception {
    GoogleRobotMetadataCredentials.Descriptor descriptor = (GoogleRobotMetadataCredentials.Descriptor) Jenkins.getInstance().getDescriptorOrDie(GoogleRobotMetadataCredentials.class);
    assertEquals(FormValidation.Kind.OK, descriptor.doCheckProjectId(PROJECT_ID).kind);
    assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckProjectId(null).kind);
    assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckProjectId("").kind);
  }

  private static String METADATA_ENDPOINT = OAuth2Utils.getMetadataServerUrl() + "/computeMetadata/v1/" + "instance/service-accounts/default/token";

  private static final String USERNAME = "bazinga";

  private static final String ACCESS_TOKEN = "ThE.ToKeN";

  private static final String PROJECT_ID = "foo.com:bar-baz";

  private static final String FAKE_SCOPE = "my.fake.scope";

  private static final List<String> SCOPES = ImmutableList.of("scope1", "scope2", "scope3");
}