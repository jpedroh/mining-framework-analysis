package org.openapitools.codegen;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class encapsulates the OpenAPI discriminator construct, as specified at
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#discriminatorObject.
 *
 * When request bodies or response payloads may be one of a number of different schemas,
 * a discriminator object can be used to aid in serialization, deserialization, and validation.
 * The discriminator is a specific object in a schema which is used to inform the consumer of
 * the specification of an alternative schema based on the value associated with it.
 */
public class CodegenDiscriminator {
  private String propertyName;

  private String propertyBaseName;

  private String propertyGetter;

  private String propertyType;

  private Map<String, String> mapping;

  private Set<MappedModel> mappedModels = new LinkedHashSet<>();

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getPropertyGetter() {
    return propertyGetter;
  }

  public void setPropertyGetter(String propertyGetter) {
    this.propertyGetter = propertyGetter;
  }

  public String getPropertyBaseName() {
    return propertyBaseName;
  }

  public void setPropertyBaseName(String propertyBaseName) {
    this.propertyBaseName = propertyBaseName;
  }

  public String getPropertyType() {
    return propertyType;
  }

  public void setPropertyType(String propertyType) {
    this.propertyType = propertyType;
  }

  public Map<String, String> getMapping() {
    return mapping;
  }

  public void setMapping(Map<String, String> mapping) {
    this.mapping = mapping;
  }

  public Set<MappedModel> getMappedModels() {
    return mappedModels;
  }

  public void setMappedModels(Set<MappedModel> mappedModels) {
    this.mappedModels = mappedModels;
  }

  public static class MappedModel implements Comparable<MappedModel> {
    private String mappingName;

    private String modelName;

    public MappedModel(String mappingName, String modelName) {
      if (mappingName == null) {
        throw new RuntimeException("Discriminator mapping name cannot be null for model \'" + modelName + "\'");
      }
      this.mappingName = mappingName;
      this.modelName = modelName;
    }

    @Override public int compareTo(MappedModel other) {
      if (getMappingName() == null && other.getMappingName() == null) {
        return 0;
      } else {
        if (getMappingName() == null) {
          return 1;
        } else {
          if (other.getMappingName() == null) {
            return -1;
          }
        }
      }
      return getMappingName().compareTo(other.getMappingName());
    }

    public String getMappingName() {
      return mappingName;
    }

    public void setMappingName(String mappingName) {
      this.mappingName = mappingName;
    }

    public String getModelName() {
      return modelName;
    }

    public void setModelName(String modelName) {
      this.modelName = modelName;
    }

    @Override public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      MappedModel that = (MappedModel) o;
      return Objects.equals(mappingName, that.mappingName) && Objects.equals(modelName, that.modelName);
    }

    @Override public int hashCode() {
      return Objects.hash(mappingName, modelName);
    }
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CodegenDiscriminator that = (CodegenDiscriminator) o;
    return Objects.equals(propertyName, that.propertyName) && Objects.equals(propertyBaseName, that.propertyBaseName) && Objects.equals(mapping, that.mapping) && Objects.equals(mappedModels, that.mappedModels);
  }

  @Override public int hashCode() {
    return Objects.hash(propertyName, propertyBaseName, mapping, mappedModels);
  }

  @Override public String toString() {
    final StringBuffer sb = new StringBuffer("CodegenDiscriminator{");
    sb.append("propertyName=\'").append(propertyName).append('\'');
    sb.append(", propertyBaseName=\'").append(propertyBaseName).append('\'');
    sb.append(", mapping=").append(mapping);
    sb.append(", mappedModels=").append(mappedModels);
    sb.append('}');
    return sb.toString();
  }
}