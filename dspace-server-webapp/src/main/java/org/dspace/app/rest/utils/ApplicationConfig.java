package org.dspace.app.rest.utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * This class provides extra configuration for our Spring Boot Application
 * <p>
 * NOTE: @ComponentScan on "org.dspace.app.configuration" provides a way for other DSpace modules or plugins
 * to "inject" their own Spring configurations / subpaths into our Spring Boot webapp.
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 * @author Tim Donohue
 */
@Configuration @ComponentScan(value = { "org.dspace.app.rest.converter", "org.dspace.app.rest.repository", "org.dspace.app.rest.utils", "org.dspace.app.configuration", "org.dspace.iiif", "org.dspace.app.iiif" }) public class ApplicationConfig {
  @Value(value = "${rest.cors.allowed-origins}") private String[] corsAllowedOrigins;

  @Value(value = "${iiif.cors.allowed-origins}") private String[] iiifCorsAllowedOrigins;

  @Value(value = "${rest.cors.allow-credentials:true}") private boolean corsAllowCredentials;

  @Value(value = "${iiif.cors.allow-credentials:true}") private boolean iiifCorsAllowCredentials;

  @Value(value = "${dspace.ui.url:http://localhost:4000}") private String uiURL;

  /**
     * Return the array of allowed origins (client URLs) for the CORS "Access-Control-Allow-Origin" header
     * Used by Application class
     * @param corsOrigins list of allowed origins for the dspace api or iiif endpoints
     * @return Array of URLs
     */
  public String[] getCorsAllowedOrigins(String[] corsOrigins) {
    if (corsOrigins != null) {
      for (int i = 0; i < corsOrigins.length; i++) {
        if (corsOrigins[i].endsWith("/")) {
          corsOrigins[i] = StringUtils.removeEnd(corsOrigins[i], "/");
        }
      }
      return corsOrigins;
    } else {
      if (uiURL != null) {
        return new String[] { uiURL };
      }
    }
    return null;
  }

  /**
     * Returns the rest.cors.allowed-origins defined in DSpace configuration.
     * @return allowed origins
     */
  public String[] getCorsAllowedOriginsConfig() {
    return this.corsAllowedOrigins;
  }

  /**
     * Returns the rest.iiif.cors.allowed-origins (for IIIF access) defined in DSpace configuration.
     * @return allowed origins
     */
  public String[] getIiifAllowedOriginsConfig() {
    return this.iiifCorsAllowedOrigins;
  }

  /**
     * Return whether to allow credentials (cookies) on CORS requests. This is used to set the
     * CORS "Access-Control-Allow-Credentials" header in Application class.
     * @return true or false
     */
  public boolean getCorsAllowCredentials() {
    return corsAllowCredentials;
  }

  /**
     * Return whether to allow credentials (cookies) on IIIF requests. This is used to set the
     * CORS "Access-Control-Allow-Credentials" header in Application class. Defaults to false.
     * @return true or false
     */
  public boolean getIiifAllowCredentials() {
    return iiifCorsAllowCredentials;
  }
}