package org.jooq.util;
import javax.annotation.Generated;
import org.jooq.util.jaxb.JpaVersion;

public interface Generator {
  void generate(Database database);

  void setStrategy(GeneratorStrategy strategy);

  GeneratorStrategy getStrategy();

  boolean generateDeprecated();

  void setGenerateDeprecated(boolean generateDeprecated);

  boolean generateDeprecationOnUnknownTypes();

  void setGenerateDeprecationOnUnknownTypes(boolean generateDeprecationOnUnknownTypes);

  boolean generateIndexes();

  void setGenerateIndexes(boolean generateIndexes);

  boolean generateRelations();

  void setGenerateRelations(boolean generateRelations);

  boolean generateTableValuedFunctions();

  void setGenerateTableValuedFunctions(boolean generateTableValuedFunctions);

  boolean generateInstanceFields();

  void setGenerateInstanceFields(boolean generateInstanceFields);

  boolean generateGeneratedAnnotation();

  void setGenerateGeneratedAnnotation(boolean generateGeneratedAnnotation);

  boolean useSchemaVersionProvider();

  void setUseSchemaVersionProvider(boolean useSchemaVersionProvider);

  boolean useCatalogVersionProvider();

  void setUseCatalogVersionProvider(boolean useCatalogVersionProvider);

  boolean generateRoutines();

  void setGenerateRoutines(boolean generateRoutines);

  boolean generateSequences();

  void setGenerateSequences(boolean generateSequences);

  boolean generateUDTs();

  void setGenerateUDTs(boolean generateUDTs);

  boolean generateTables();

  void setGenerateTables(boolean generateTables);

  boolean generateRecords();

  void setGenerateRecords(boolean generateRecords);

  boolean generateRecordsImplementingRecordN();

  void setGenerateRecordsImplementingRecordN(boolean generateRecordsImplementingRecordN);

  boolean generatePojos();

  void setGeneratePojos(boolean generatePojos);

  boolean generateImmutablePojos();

  void setGenerateImmutablePojos(boolean generateImmutablePojos);

  boolean generateInterfaces();

  void setGenerateInterfaces(boolean generateInterfaces);

  boolean generateImmutableInterfaces();

  void setGenerateImmutableInterfaces(boolean generateImmutableInterfaces);

  boolean generateDaos();

  void setGenerateDaos(boolean generateDaos);

  boolean generateJPAAnnotations();

  void setGenerateJPAAnnotations(boolean generateJPAAnnotations);

  boolean generateValidationAnnotations();

  void setGenerateValidationAnnotations(boolean generateValidationAnnotations);

  boolean generateSpringAnnotations();

  void setGenerateSpringAnnotations(boolean generateSpringAnnotations);

  boolean generateGlobalObjectReferences();

  void setGenerateGlobalObjectReferences(boolean generateGlobalObjectReferences);

  boolean generateGlobalCatalogReferences();

  void setGenerateGlobalCatalogReferences(boolean globalCatalogReferences);

  boolean generateGlobalSchemaReferences();

  void setGenerateGlobalSchemaReferences(boolean globalSchemaReferences);

  boolean generateGlobalRoutineReferences();

  void setGenerateGlobalRoutineReferences(boolean globalRoutineReferences);

  boolean generateGlobalSequenceReferences();

  void setGenerateGlobalSequenceReferences(boolean globalSequenceReferences);

  boolean generateGlobalTableReferences();

  void setGenerateGlobalTableReferences(boolean globalTableReferences);

  boolean generateGlobalUDTReferences();

  void setGenerateGlobalUDTReferences(boolean globalUDTReferences);

  boolean generateGlobalQueueReferences();

  void setGenerateGlobalQueueReferences(boolean globalQueueReferences);

  boolean generateGlobalLinkReferences();

  void setGenerateGlobalLinkReferences(boolean globalLinkReferences);

  boolean generateGlobalKeyReferences();

  void setGenerateGlobalKeyReferences(boolean globalKeyReferences);

  boolean generateQueues();

  void setGenerateQueues(boolean queues);

  boolean generateLinks();

  void setGenerateLinks(boolean links);

  boolean generateKeys();

  void setGenerateKeys(boolean keys);

  @Deprecated boolean fluentSetters();

  @Deprecated void setFluentSetters(boolean fluentSetters);

  boolean generateFluentSetters();

  void setGenerateFluentSetters(boolean fluentSetters);

  boolean generateJavaBeansGettersAndSetters();

  void setGenerateJavaBeansGettersAndSetters(boolean javaBeansGettersAndSetters);

  boolean generateVarargsSetters();

  void setGenerateVarargsSetters(boolean varargsSetters);

  boolean generatePojosEqualsAndHashCode();

  void setGeneratePojosEqualsAndHashCode(boolean generatePojosEqualsAndHashCode);

  boolean generatePojosToString();

  void setGeneratePojosToString(boolean generatePojosToString);

  @Deprecated String fullyQualifiedTypes();

  @Deprecated void setFullyQualifiedTypes(String fullyQualifiedTypes);

  String generateFullyQualifiedTypes();

  void setGenerateFullyQualifiedTypes(String generateFullyQualifiedTypes);

  boolean generateJavaTimeTypes();

  void setGenerateJavaTimeTypes(boolean generateJavaTimeTypes);

  boolean generateEmptyCatalogs();

  void setGenerateEmptyCatalogs(boolean generateEmptyCatalogs);

  boolean generateEmptySchemas();

  void setGenerateEmptySchemas(boolean generateEmptySchemas);

  boolean generatePrimaryKeyTypes();

  void setGeneratePrimaryKeyTypes(boolean generatePrimaryKeyTypes);

  String getTargetDirectory();

  void setTargetDirectory(String directory);

  String getTargetEncoding();

  void setTargetEncoding(String encoding);

  String getTargetPackage();

  void setTargetPackage(String packageName);

  JpaVersion generateJpaVersion();

  void setGenerateJpaVersion(JpaVersion generateJpaVersion);
}