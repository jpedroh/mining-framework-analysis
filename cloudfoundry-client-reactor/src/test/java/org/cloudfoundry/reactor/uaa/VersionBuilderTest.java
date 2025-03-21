package org.cloudfoundry.reactor.uaa;
import io.netty.handler.codec.http.HttpHeaders;
import org.cloudfoundry.uaa.Versioned;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public final class VersionBuilderTest {
  private final HttpHeaders outbound = mock(HttpHeaders.class);

  @Test public void augment() {
    VersionBuilder.augment(this.outbound, new StubVersioned("test-version"));
    verify(this.outbound).set("If-Match", "test-version");
  }

  @Test public void augmentNotVersioned() {
    VersionBuilder.augment(this.outbound, new Object());
    verifyNoInteractions(this.outbound);
  }

  @Test public void augmentNullVersion() {
    VersionBuilder.augment(this.outbound, new StubVersioned(null));
    verifyNoInteractions(this.outbound);
  }

  private static final class StubVersioned implements Versioned {
    private final String version;

    private StubVersioned(String version) {
      this.version = version;
    }

    @Override public String getVersion() {
      return this.version;
    }
  }
}