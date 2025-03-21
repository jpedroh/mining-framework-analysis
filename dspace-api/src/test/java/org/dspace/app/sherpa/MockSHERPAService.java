package org.dspace.app.sherpa;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.dspace.app.sherpa.v2.SHERPAPublisherResponse;
import org.dspace.app.sherpa.v2.SHERPAResponse;

/**
 * Mock implementation for SHERPA API service (used by SHERPA submit service to check
 * journal policies).
 * This class will return mock SHERPA responses so they can be parsed and turned
 * into external data objects downstream.
 *
 * @author Kim Shepherd
 */
public class MockSHERPAService extends SHERPAService {
  /**
     * Simple overridden performRequest so that we do attempt to build the URI but rather than make
     * an actual HTTP call, return parsed SHERPAResponse for The Lancet based on known-good JSON stored with our
     * test resources.
     * If URI creation, parsing, or IO fails along the way, a SHERPAResponse with an error message set will be
     * returned.
     * @param value a journal / publication name, or ID, etc.
     * @return  SHERPAResponse
     */
  @Override public SHERPAResponse performRequest(String type, String field, String predicate, String value, int start, int limit) {
    try {
      String endpoint = configurationService.getProperty("sherpa.romeo.url", "https://v2.sherpa.ac.uk/cgi/retrieve");
      String apiKey = configurationService.getProperty("sherpa.romeo.apikey");
      InputStream content = null;
      try {
        URI uri = prepareQuery(value, endpoint, apiKey);
        if (uri == null) {
          return new SHERPAResponse("Error building URI");
        }
        content = getContent(value.concat(".json"));
        if (Objects.isNull(content)) {
          content = getContent("thelancet.json");
        }
        return new SHERPAResponse(content, SHERPAResponse.SHERPAFormat.JSON);
      } catch (URISyntaxException e) {
        return new SHERPAResponse(e.getMessage());
      } finally {
        if (content != null) {
          content.close();
        }
      }
    } catch (IOException e) {
      return new SHERPAResponse(e.getMessage());
    }
  }

  private InputStream getContent(String fileName) {
    return getClass().getResourceAsStream(fileName);
  }

  /**
     * Simple overridden performPublisherRequest so that we do attempt to build the URI but rather than make
     * an actual HTTP call, return parsed SHERPAPublisherResponse for PLOS based on known-good JSON stored with our
     * test resources.
     * If URI creation, parsing, or IO fails along the way, a SHERPAPublisherResponse with an error message set will be
     * returned.
     * @param value a journal / publication name, or ID, etc.
     * @return  SHERPAResponse
     */
  @Override public SHERPAPublisherResponse performPublisherRequest(String type, String field, String predicate, String value, int start, int limit) {
    try {
      String endpoint = configurationService.getProperty("sherpa.romeo.url", "https://v2.sherpa.ac.uk/cgi/retrieve");
      String apiKey = configurationService.getProperty("sherpa.romeo.apikey");
      InputStream content = null;
      try {
        URI unuseduri = prepareQuery(value, endpoint, apiKey);
        content = getClass().getResourceAsStream("plos.json");
        return new SHERPAPublisherResponse(content, SHERPAPublisherResponse.SHERPAFormat.JSON);
      } catch (URISyntaxException e) {
        return new SHERPAPublisherResponse(e.getMessage());
      } finally {
        if (content != null) {
          content.close();
        }
      }
    } catch (IOException e) {
      return new SHERPAPublisherResponse(e.getMessage());
    }
  }
}