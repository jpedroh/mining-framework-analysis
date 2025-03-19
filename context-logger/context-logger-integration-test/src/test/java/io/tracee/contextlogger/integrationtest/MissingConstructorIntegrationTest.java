package io.tracee.contextlogger.integrationtest;
import org.hamcrest.MatcherAssert;
import io.tracee.contextlogger.TraceeContextLogger;
import org.hamcrest.Matchers;
import io.tracee.contextlogger.profile.Profile;
import org.junit.Test;

/**
 * Integration test to check behavior for custom context data providers and wrapper that have no no args constructor.
 */
public class MissingConstructorIntegrationTest {
  @Test public void should_handle_missing_no_args_constructor_gently() {
    String result = TraceeContextLogger.create().config().enforceProfile(Profile.ENHANCED).apply().build().createJson(TestBrokenImplicitContentDataProviderWithoutDefaultConstructor.class);
    MatcherAssert.assertThat(result, Matchers.startsWith("{\"java.lang.Class\""));
  }

  @Test public void should_wrap_with_external_wrappers_correctly_using_enhanced_profile() {
    String result = TraceeContextLogger.create().config().enforceProfile(Profile.ENHANCED).apply().build().createJson(new BrokenCustomContextDataWrapperWithMissingNoargsConstructor());
    MatcherAssert.assertThat(result, Matchers.startsWith("{\"io.tracee.contextlogger.integrationtest.BrokenCustomContextDataWrapperWithMissingNoargsConstructor\""));
  }
}