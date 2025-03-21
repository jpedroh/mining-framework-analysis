package com.bsb.common.vaadin.embed.application;
import com.bsb.common.vaadin.embed.EmbedVaadinConfig;
import com.bsb.common.vaadin.embed.EmbedVaadinServer;
import com.bsb.common.vaadin.embed.EmbedVaadinServerBuilder;
import com.vaadin.ui.UI;
import java.util.Properties;

/**
 * A builder for a server embedding an application.
 *
 * @author Stephane Nicoll
 */
public class EmbedVaadinApplication extends EmbedVaadinServerBuilder<EmbedVaadinApplication, EmbedVaadinServer> {
  private final Class<? extends UI> uiClass;

  private EmbedVaadinConfig config;

  /**
     * Creates a new instance for the specified application.
     *
     * @param applicationClass the class of the application to deploy
     */
  public EmbedVaadinApplication(Class<? extends UI> uiClass) {
    super();
    assertNotNull(uiClass, "uiClass could not be null.");
    this.uiClass = uiClass;
    initializeConfig(EmbedVaadinConfig.loadProperties());
  }

  /**
     * Returns the {@link UI} type that was used to initialize this instance, if any.
     *
     * @return the UI class or <tt>null</tt> if a component was set
     */
  protected Class<? extends UI> getUiClass() {
    return uiClass;
  }

  @Override protected EmbedVaadinApplication self() {
    return this;
  }

  @Override public EmbedVaadinServer build() {
    return new ApplicationBasedEmbedVaadinTomcat(getConfig(), getUiClass());
  }

  @Override public EmbedVaadinApplication withConfigProperties(Properties properties) {
    initializeConfig(properties);
    return self();
  }

  @Override protected EmbedVaadinConfig getConfig() {
    return config;
  }

  /**
     * Initializes a default configuration.
     *
     * @param properties the configuration
     */
  private void initializeConfig(Properties properties) {
    assertNotNull(properties, "properties could not be null.");
    this.config = new EmbedVaadinConfig(properties);
  }
}