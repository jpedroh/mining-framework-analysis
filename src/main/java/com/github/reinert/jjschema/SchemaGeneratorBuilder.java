package com.github.reinert.jjschema;

/**
 * A SchemaGenerator builder for creating SchemaGenerators considering some options.
 *
 * @author reinert
 */
public class SchemaGeneratorBuilder {
  private SchemaGeneratorBuilder() {
  }

  public static ConfigurationStep draftV4Schema() {
    return new ConfigurationStep(new JsonSchemaGeneratorV4());
  }

  public static ConfigurationStep draftV4HyperSchema() {
    return new ConfigurationStep(new HyperSchemaGeneratorV4(new JsonSchemaGeneratorV4()));
  }

  static public class ConfigurationStep {
    final JsonSchemaGenerator generator;

    ConfigurationStep(JsonSchemaGenerator generator) {
      this.generator = generator;
    }

    public ConfigurationStep setAutoPutSchemaVersion(boolean autoPutVersion) {
      generator.autoPutVersion = autoPutVersion;
      return this;
    }

    public final JsonSchemaGenerator build() {
      return generator;
    }
  }
}