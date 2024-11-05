package org.jooq.util.jaxb;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jooq.util.jaxb.tools.StringAdapter;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlAccessorType(value = XmlAccessType.FIELD) @XmlType(name = "Generate", propOrder = {  }) @SuppressWarnings(value = { "all" }) public class Generate implements Serializable {
  private final static long serialVersionUID = 31100L;

  @XmlElement(defaultValue = "true") protected Boolean indexes = true;

  @XmlElement(defaultValue = "true") protected Boolean relations = true;

  @XmlElement(defaultValue = "true") protected Boolean deprecated = true;

  @XmlElement(defaultValue = "true") protected Boolean deprecationOnUnknownTypes = true;

  @XmlElement(defaultValue = "true") protected Boolean instanceFields = true;

  @XmlElement(defaultValue = "true") protected Boolean generatedAnnotation = true;

  @XmlElement(defaultValue = "true") protected Boolean routines = true;

  @XmlElement(defaultValue = "true") protected Boolean sequences = true;

  @XmlElement(defaultValue = "true") protected Boolean udts = true;

  @XmlElement(defaultValue = "true") protected Boolean queues = true;

  @XmlElement(defaultValue = "true") protected Boolean links = true;

  @XmlElement(defaultValue = "true") protected Boolean keys = true;

  @XmlElement(defaultValue = "true") protected Boolean tables = true;

  @XmlElement(defaultValue = "true") protected Boolean records = true;

  @XmlElement(defaultValue = "true") protected Boolean recordsImplementingRecordN = true;

  @XmlElement(defaultValue = "false") protected Boolean pojos = false;

  @XmlElement(defaultValue = "false") protected Boolean pojosEqualsAndHashCode = false;

  @XmlElement(defaultValue = "true") protected Boolean pojosToString = true;

  @XmlElement(defaultValue = "false") protected Boolean immutablePojos = false;

  @XmlElement(defaultValue = "false") protected Boolean interfaces = false;

  @XmlElement(defaultValue = "false") protected Boolean immutableInterfaces = false;

  @XmlElement(defaultValue = "false") protected Boolean daos = false;

  @XmlElement(defaultValue = "false") protected Boolean jpaAnnotations = false;

  @XmlElement(defaultValue = "false") protected Boolean validationAnnotations = false;

  @XmlElement(defaultValue = "false") protected Boolean springAnnotations = false;

  @XmlElement(defaultValue = "true") protected Boolean globalObjectReferences = true;

  @XmlElement(defaultValue = "true") protected Boolean globalCatalogReferences = true;

  @XmlElement(defaultValue = "true") protected Boolean globalSchemaReferences = true;

  @XmlElement(defaultValue = "true") protected Boolean globalTableReferences = true;

  @XmlElement(defaultValue = "true") protected Boolean globalSequenceReferences = true;

  @XmlElement(defaultValue = "true") protected Boolean globalUDTReferences = true;

  @XmlElement(defaultValue = "true") protected Boolean globalRoutineReferences = true;

  @XmlElement(defaultValue = "true") protected Boolean globalQueueReferences = true;

  @XmlElement(defaultValue = "true") protected Boolean globalLinkReferences = true;

  @XmlElement(defaultValue = "true") protected Boolean globalKeyReferences = true;

  @XmlElement(defaultValue = "false") protected Boolean fluentSetters = false;

  @XmlElement(defaultValue = "false") protected Boolean javaBeansGettersAndSetters = false;

  @XmlElement(defaultValue = "true") protected Boolean varargSetters = true;

  @XmlElement(defaultValue = "") @XmlJavaTypeAdapter(value = StringAdapter.class) protected String fullyQualifiedTypes = "";

  @XmlElement(defaultValue = "false") protected Boolean emptyCatalogs = false;

  @XmlElement(defaultValue = "false") protected Boolean emptySchemas = false;

  @XmlElement(defaultValue = "false") protected Boolean javaTimeTypes = false;

  @XmlElement(defaultValue = "false") protected Boolean primaryKeyTypes = false;

  public Boolean isIndexes() {
    return indexes;
  }

  public void setIndexes(Boolean value) {
    this.indexes = value;
  }

  public Boolean isRelations() {
    return relations;
  }

  public void setRelations(Boolean value) {
    this.relations = value;
  }

  public Boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated(Boolean value) {
    this.deprecated = value;
  }

  public Boolean isDeprecationOnUnknownTypes() {
    return deprecationOnUnknownTypes;
  }

  public void setDeprecationOnUnknownTypes(Boolean value) {
    this.deprecationOnUnknownTypes = value;
  }

  public Boolean isInstanceFields() {
    return instanceFields;
  }

  public void setInstanceFields(Boolean value) {
    this.instanceFields = value;
  }

  public Boolean isGeneratedAnnotation() {
    return generatedAnnotation;
  }

  public void setGeneratedAnnotation(Boolean value) {
    this.generatedAnnotation = value;
  }

  public Boolean isRoutines() {
    return routines;
  }

  public void setRoutines(Boolean value) {
    this.routines = value;
  }

  public Boolean isSequences() {
    return sequences;
  }

  public void setSequences(Boolean value) {
    this.sequences = value;
  }

  public Boolean isUdts() {
    return udts;
  }

  public void setUdts(Boolean value) {
    this.udts = value;
  }

  public Boolean isQueues() {
    return queues;
  }

  public void setQueues(Boolean value) {
    this.queues = value;
  }

  public Boolean isLinks() {
    return links;
  }

  public void setLinks(Boolean value) {
    this.links = value;
  }

  public Boolean isKeys() {
    return keys;
  }

  public void setKeys(Boolean value) {
    this.keys = value;
  }

  public Boolean isTables() {
    return tables;
  }

  public void setTables(Boolean value) {
    this.tables = value;
  }

  public Boolean isRecords() {
    return records;
  }

  public void setRecords(Boolean value) {
    this.records = value;
  }

  public Boolean isRecordsImplementingRecordN() {
    return recordsImplementingRecordN;
  }

  public void setRecordsImplementingRecordN(Boolean value) {
    this.recordsImplementingRecordN = value;
  }

  public Boolean isPojos() {
    return pojos;
  }

  public void setPojos(Boolean value) {
    this.pojos = value;
  }

  public Boolean isPojosEqualsAndHashCode() {
    return pojosEqualsAndHashCode;
  }

  public void setPojosEqualsAndHashCode(Boolean value) {
    this.pojosEqualsAndHashCode = value;
  }

  public Boolean isPojosToString() {
    return pojosToString;
  }

  public void setPojosToString(Boolean value) {
    this.pojosToString = value;
  }

  public Boolean isImmutablePojos() {
    return immutablePojos;
  }

  public void setImmutablePojos(Boolean value) {
    this.immutablePojos = value;
  }

  public Boolean isInterfaces() {
    return interfaces;
  }

  public void setInterfaces(Boolean value) {
    this.interfaces = value;
  }

  public Boolean isImmutableInterfaces() {
    return immutableInterfaces;
  }

  public void setImmutableInterfaces(Boolean value) {
    this.immutableInterfaces = value;
  }

  public Boolean isDaos() {
    return daos;
  }

  public void setDaos(Boolean value) {
    this.daos = value;
  }

  public Boolean isJpaAnnotations() {
    return jpaAnnotations;
  }

  public void setJpaAnnotations(Boolean value) {
    this.jpaAnnotations = value;
  }

  public Boolean isValidationAnnotations() {
    return validationAnnotations;
  }

  public void setValidationAnnotations(Boolean value) {
    this.validationAnnotations = value;
  }

  public Boolean isSpringAnnotations() {
    return springAnnotations;
  }

  public void setSpringAnnotations(Boolean value) {
    this.springAnnotations = value;
  }

  public Boolean isGlobalObjectReferences() {
    return globalObjectReferences;
  }

  public void setGlobalObjectReferences(Boolean value) {
    this.globalObjectReferences = value;
  }

  public Boolean isGlobalCatalogReferences() {
    return globalCatalogReferences;
  }

  public void setGlobalCatalogReferences(Boolean value) {
    this.globalCatalogReferences = value;
  }

  public Boolean isGlobalSchemaReferences() {
    return globalSchemaReferences;
  }

  public void setGlobalSchemaReferences(Boolean value) {
    this.globalSchemaReferences = value;
  }

  public Boolean isGlobalTableReferences() {
    return globalTableReferences;
  }

  public void setGlobalTableReferences(Boolean value) {
    this.globalTableReferences = value;
  }

  public Boolean isGlobalSequenceReferences() {
    return globalSequenceReferences;
  }

  public void setGlobalSequenceReferences(Boolean value) {
    this.globalSequenceReferences = value;
  }

  public Boolean isGlobalUDTReferences() {
    return globalUDTReferences;
  }

  public void setGlobalUDTReferences(Boolean value) {
    this.globalUDTReferences = value;
  }

  public Boolean isGlobalRoutineReferences() {
    return globalRoutineReferences;
  }

  public void setGlobalRoutineReferences(Boolean value) {
    this.globalRoutineReferences = value;
  }

  public Boolean isGlobalQueueReferences() {
    return globalQueueReferences;
  }

  public void setGlobalQueueReferences(Boolean value) {
    this.globalQueueReferences = value;
  }

  public Boolean isGlobalLinkReferences() {
    return globalLinkReferences;
  }

  public void setGlobalLinkReferences(Boolean value) {
    this.globalLinkReferences = value;
  }

  public Boolean isGlobalKeyReferences() {
    return globalKeyReferences;
  }

  public void setGlobalKeyReferences(Boolean value) {
    this.globalKeyReferences = value;
  }

  public Boolean isFluentSetters() {
    return fluentSetters;
  }

  public void setFluentSetters(Boolean value) {
    this.fluentSetters = value;
  }

  public Boolean isJavaBeansGettersAndSetters() {
    return javaBeansGettersAndSetters;
  }

  public void setJavaBeansGettersAndSetters(Boolean value) {
    this.javaBeansGettersAndSetters = value;
  }

  public Boolean isVarargSetters() {
    return varargSetters;
  }

  public void setVarargSetters(Boolean value) {
    this.varargSetters = value;
  }

  public String getFullyQualifiedTypes() {
    return fullyQualifiedTypes;
  }

  public void setFullyQualifiedTypes(String value) {
    this.fullyQualifiedTypes = value;
  }

  public Boolean isEmptyCatalogs() {
    return emptyCatalogs;
  }

  public void setEmptyCatalogs(Boolean value) {
    this.emptyCatalogs = value;
  }

  public Boolean isEmptySchemas() {
    return emptySchemas;
  }

  public void setEmptySchemas(Boolean value) {
    this.emptySchemas = value;
  }

  public Boolean isJavaTimeTypes() {
    return javaTimeTypes;
  }

  public void setJavaTimeTypes(Boolean value) {
    this.javaTimeTypes = value;
  }

  public Boolean isPrimaryKeyTypes() {
    return primaryKeyTypes;
  }

  public void setPrimaryKeyTypes(Boolean value) {
    this.primaryKeyTypes = value;
  }

  public Generate withIndexes(Boolean value) {
    setIndexes(value);
    return this;
  }

  public Generate withRelations(Boolean value) {
    setRelations(value);
    return this;
  }

  public Generate withDeprecated(Boolean value) {
    setDeprecated(value);
    return this;
  }

  public Generate withDeprecationOnUnknownTypes(Boolean value) {
    setDeprecationOnUnknownTypes(value);
    return this;
  }

  public Generate withInstanceFields(Boolean value) {
    setInstanceFields(value);
    return this;
  }

  public Generate withGeneratedAnnotation(Boolean value) {
    setGeneratedAnnotation(value);
    return this;
  }

  public Generate withRoutines(Boolean value) {
    setRoutines(value);
    return this;
  }

  public Generate withSequences(Boolean value) {
    setSequences(value);
    return this;
  }

  public Generate withUdts(Boolean value) {
    setUdts(value);
    return this;
  }

  public Generate withQueues(Boolean value) {
    setQueues(value);
    return this;
  }

  public Generate withLinks(Boolean value) {
    setLinks(value);
    return this;
  }

  public Generate withKeys(Boolean value) {
    setKeys(value);
    return this;
  }

  public Generate withTables(Boolean value) {
    setTables(value);
    return this;
  }

  public Generate withRecords(Boolean value) {
    setRecords(value);
    return this;
  }

  public Generate withRecordsImplementingRecordN(Boolean value) {
    setRecordsImplementingRecordN(value);
    return this;
  }

  public Generate withPojos(Boolean value) {
    setPojos(value);
    return this;
  }

  public Generate withPojosEqualsAndHashCode(Boolean value) {
    setPojosEqualsAndHashCode(value);
    return this;
  }

  public Generate withPojosToString(Boolean value) {
    setPojosToString(value);
    return this;
  }

  public Generate withImmutablePojos(Boolean value) {
    setImmutablePojos(value);
    return this;
  }

  public Generate withInterfaces(Boolean value) {
    setInterfaces(value);
    return this;
  }

  public Generate withImmutableInterfaces(Boolean value) {
    setImmutableInterfaces(value);
    return this;
  }

  public Generate withDaos(Boolean value) {
    setDaos(value);
    return this;
  }

  public Generate withJpaAnnotations(Boolean value) {
    setJpaAnnotations(value);
    return this;
  }

  public Generate withValidationAnnotations(Boolean value) {
    setValidationAnnotations(value);
    return this;
  }

  public Generate withSpringAnnotations(Boolean value) {
    setSpringAnnotations(value);
    return this;
  }

  public Generate withGlobalObjectReferences(Boolean value) {
    setGlobalObjectReferences(value);
    return this;
  }

  public Generate withGlobalCatalogReferences(Boolean value) {
    setGlobalCatalogReferences(value);
    return this;
  }

  public Generate withGlobalSchemaReferences(Boolean value) {
    setGlobalSchemaReferences(value);
    return this;
  }

  public Generate withGlobalTableReferences(Boolean value) {
    setGlobalTableReferences(value);
    return this;
  }

  public Generate withGlobalSequenceReferences(Boolean value) {
    setGlobalSequenceReferences(value);
    return this;
  }

  public Generate withGlobalUDTReferences(Boolean value) {
    setGlobalUDTReferences(value);
    return this;
  }

  public Generate withGlobalRoutineReferences(Boolean value) {
    setGlobalRoutineReferences(value);
    return this;
  }

  public Generate withGlobalQueueReferences(Boolean value) {
    setGlobalQueueReferences(value);
    return this;
  }

  public Generate withGlobalLinkReferences(Boolean value) {
    setGlobalLinkReferences(value);
    return this;
  }

  public Generate withGlobalKeyReferences(Boolean value) {
    setGlobalKeyReferences(value);
    return this;
  }

  public Generate withFluentSetters(Boolean value) {
    setFluentSetters(value);
    return this;
  }

  public Generate withJavaBeansGettersAndSetters(Boolean value) {
    setJavaBeansGettersAndSetters(value);
    return this;
  }

  public Generate withVarargSetters(Boolean value) {
    setVarargSetters(value);
    return this;
  }

  public Generate withFullyQualifiedTypes(String value) {
    setFullyQualifiedTypes(value);
    return this;
  }

  public Generate withEmptyCatalogs(Boolean value) {
    setEmptyCatalogs(value);
    return this;
  }

  public Generate withEmptySchemas(Boolean value) {
    setEmptySchemas(value);
    return this;
  }

  public Generate withJavaTimeTypes(Boolean value) {
    setJavaTimeTypes(value);
    return this;
  }

  public Generate withPrimaryKeyTypes(Boolean value) {
    setPrimaryKeyTypes(value);
    return this;
  }

  @XmlSchemaType(name = "string") protected JpaVersion jpaVersion;

  public JpaVersion getJpaVersion() {
    return jpaVersion;
  }

  public void setJpaVersion(JpaVersion value) {
    this.jpaVersion = value;
  }

  public Generate withJpaVersion(JpaVersion value) {
    setJpaVersion(value);
    return this;
  }
}