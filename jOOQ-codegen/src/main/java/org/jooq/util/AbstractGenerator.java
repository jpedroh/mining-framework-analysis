package org.jooq.util;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.jooq.tools.JooqLogger;
import org.jooq.util.jaxb.JpaVersion;

abstract class AbstractGenerator implements Generator {
  private static final JooqLogger log = JooqLogger.getLogger(AbstractGenerator.class);

  boolean generateDeprecated = true;

  boolean generateDeprecationOnUnknownTypes = true;

  boolean generateIndexes = true;

  boolean generateRelations = true;

  boolean generateInstanceFields = true;

  boolean generateGeneratedAnnotation = true;

  boolean useSchemaVersionProvider = false;

  boolean useCatalogVersionProvider = false;

  boolean generateRoutines = true;

  boolean generateSequences = true;

  boolean generateUDTs = true;

  boolean generateTables = true;

  boolean generateRecords = true;

  boolean generateRecordsImplementingRecordN = true;

  boolean generatePojos = false;

  boolean generatePojosEqualsAndHashCode = false;

  boolean generatePojosToString = true;

  boolean generateImmutablePojos = false;

  boolean generateInterfaces = false;

  boolean generateImmutableInterfaces = false;

  boolean generateDaos = false;

  boolean generateJPAAnnotations = false;

  boolean generateValidationAnnotations = false;

  boolean generateSpringAnnotations = false;

  boolean generateQueues = true;

  boolean generateLinks = true;

  boolean generateKeys = true;

  boolean generateGlobalObjectReferences = true;

  boolean generateGlobalCatalogReferences = true;

  boolean generateGlobalSchemaReferences = true;

  boolean generateGlobalRoutineReferences = true;

  boolean generateGlobalSequenceReferences = true;

  boolean generateGlobalTableReferences = true;

  boolean generateGlobalUDTReferences = true;

  boolean generateGlobalQueueReferences = true;

  boolean generateGlobalLinkReferences = true;

  boolean generateGlobalKeyReferences = true;

  boolean generateFluentSetters = false;

  boolean generateJavaBeansGettersAndSetters = false;

  boolean generateVarargsSetters = true;

  String generateFullyQualifiedTypes = "";

  boolean generateJavaTimeTypes = false;

  boolean generateTableValuedFunctions = false;

  boolean generateEmptyCatalogs = false;

  boolean generateEmptySchemas = false;

  boolean generatePrimaryKeyTypes = false;

  protected GeneratorStrategyWrapper strategy;

  protected String targetEncoding = "UTF-8";

  final Language language;

  AbstractGenerator(Language language) {
    this.language = language;
  }

  enum Language {
    JAVA,
    SCALA,
    XML
  }

  void logDatabaseParameters(Database db) {
    String url = "";
    try {
      Connection connection = db.getConnection();
      if (connection != null) {
        url = connection.getMetaData().getURL();
      }
    } catch (SQLException ignore) {
    }
    log.info("License parameters");
    log.info("----------------------------------------------------------");
    log.info("  Thank you for using jOOQ and jOOQ\'s code generator");
    log.info("");
    log.info("Database parameters");
    log.info("----------------------------------------------------------");
    log.info("  dialect", db.getDialect());
    log.info("  URL", url);
    log.info("  target dir", getTargetDirectory());
    log.info("  target package", getTargetPackage());
    log.info("  includes", Arrays.asList(db.getIncludes()));
    log.info("  excludes", Arrays.asList(db.getExcludes()));
    log.info("  includeExcludeColumns", db.getIncludeExcludeColumns());
    log.info("----------------------------------------------------------");
  }

  void logGenerationRemarks(Database db) {
    log.info("Generation remarks");
    log.info("----------------------------------------------------------");
    if (contains(db.getIncludes(), ',') && db.getIncluded().isEmpty()) {
      log.info("  includes", "The <includes/> element takes a Java regular expression, not a comma-separated list. This might be why no objects were included.");
    }
    if (contains(db.getExcludes(), ',') && db.getExcluded().isEmpty()) {
      log.info("  excludes", "The <excludes/> element takes a Java regular expression, not a comma-separated list. This might be why no objects were excluded.");
    }
  }

  private boolean contains(String[] array, char c) {
    if (array == null) {
      return false;
    }
    for (String string : array) {
      if (string != null && string.indexOf(c) > -1) {
        return true;
      }
    }
    return false;
  }

  @Override public void setStrategy(GeneratorStrategy strategy) {
    this.strategy = new GeneratorStrategyWrapper(this, strategy, language);
  }

  @Override public GeneratorStrategy getStrategy() {
    return strategy;
  }

  @Override public boolean generateDeprecated() {
    return generateDeprecated;
  }

  @Override public void setGenerateDeprecated(boolean generateDeprecated) {
    this.generateDeprecated = generateDeprecated;
  }

  @Override public boolean generateDeprecationOnUnknownTypes() {
    return generateDeprecationOnUnknownTypes;
  }

  @Override public void setGenerateDeprecationOnUnknownTypes(boolean generateDeprecationOnUnknownTypes) {
    this.generateDeprecationOnUnknownTypes = generateDeprecationOnUnknownTypes;
  }

  @Override public boolean generateIndexes() {
    return generateIndexes;
  }

  @Override public void setGenerateIndexes(boolean generateIndexes) {
    this.generateIndexes = generateIndexes;
  }

  @Override public boolean generateRelations() {
    return generateRelations || generateTables || generateDaos;
  }

  @Override public void setGenerateRelations(boolean generateRelations) {
    this.generateRelations = generateRelations;
  }

  @Override public boolean generateTableValuedFunctions() {
    return generateTableValuedFunctions;
  }

  @Override public void setGenerateTableValuedFunctions(boolean generateTableValuedFunctions) {
    this.generateTableValuedFunctions = generateTableValuedFunctions;
  }

  @Override public boolean generateInstanceFields() {
    return generateInstanceFields;
  }

  @Override public void setGenerateInstanceFields(boolean generateInstanceFields) {
    this.generateInstanceFields = generateInstanceFields;
  }

  @Override public boolean generateGeneratedAnnotation() {
    return generateGeneratedAnnotation || useSchemaVersionProvider || useCatalogVersionProvider;
  }

  @Override public void setGenerateGeneratedAnnotation(boolean generateGeneratedAnnotation) {
    this.generateGeneratedAnnotation = generateGeneratedAnnotation;
  }

  @Override public boolean useSchemaVersionProvider() {
    return useSchemaVersionProvider;
  }

  @Override public void setUseSchemaVersionProvider(boolean useSchemaVersionProvider) {
    this.useSchemaVersionProvider = useSchemaVersionProvider;
  }

  @Override public boolean useCatalogVersionProvider() {
    return useCatalogVersionProvider;
  }

  @Override public void setUseCatalogVersionProvider(boolean useCatalogVersionProvider) {
    this.useCatalogVersionProvider = useCatalogVersionProvider;
  }

  @Override public boolean generateRoutines() {
    return generateRoutines;
  }

  @Override public void setGenerateRoutines(boolean generateRoutines) {
    this.generateRoutines = generateRoutines;
  }

  @Override public boolean generateSequences() {
    return generateSequences;
  }

  @Override public void setGenerateSequences(boolean generateSequences) {
    this.generateSequences = generateSequences;
  }

  @Override public boolean generateUDTs() {
    return generateUDTs;
  }

  @Override public void setGenerateUDTs(boolean generateUDTs) {
    this.generateUDTs = generateUDTs;
  }

  @Override public boolean generateTables() {
    return generateTables || generateRecords || generateDaos;
  }

  @Override public void setGenerateTables(boolean generateTables) {
    this.generateTables = generateTables;
  }

  @Override public boolean generateRecords() {
    return generateRecords || generateDaos;
  }

  @Override public void setGenerateRecords(boolean generateRecords) {
    this.generateRecords = generateRecords;
  }

  @Override public boolean generateRecordsImplementingRecordN() {
    return generateRecords() && generateRecordsImplementingRecordN;
  }

  @Override public void setGenerateRecordsImplementingRecordN(boolean generateRecordsImplementingRecordN) {
    this.generateRecordsImplementingRecordN = generateRecordsImplementingRecordN;
  }

  @Override public boolean generatePojos() {
    return generatePojos || generateImmutablePojos || generateDaos;
  }

  @Override public void setGeneratePojos(boolean generatePojos) {
    this.generatePojos = generatePojos;
  }

  @Override public boolean generateImmutablePojos() {
    return generateImmutablePojos;
  }

  @Override public void setGenerateImmutablePojos(boolean generateImmutablePojos) {
    this.generateImmutablePojos = generateImmutablePojos;
  }

  @Override public boolean generateInterfaces() {
    return generateInterfaces || generateImmutableInterfaces;
  }

  @Override public void setGenerateInterfaces(boolean generateInterfaces) {
    this.generateInterfaces = generateInterfaces;
  }

  @Override public boolean generateImmutableInterfaces() {
    return generateImmutableInterfaces || (generateInterfaces && generateImmutablePojos);
  }

  @Override public void setGenerateImmutableInterfaces(boolean generateImmutableInterfaces) {
    this.generateImmutableInterfaces = generateImmutableInterfaces;
  }

  @Override public boolean generateDaos() {
    return generateDaos;
  }

  @Override public void setGenerateDaos(boolean generateDaos) {
    this.generateDaos = generateDaos;
  }

  @Override public boolean generateJPAAnnotations() {
    return generateJPAAnnotations;
  }

  @Override public void setGenerateJPAAnnotations(boolean generateJPAAnnotations) {
    this.generateJPAAnnotations = generateJPAAnnotations;
  }

  @Override public boolean generateValidationAnnotations() {
    return generateValidationAnnotations;
  }

  @Override public void setGenerateValidationAnnotations(boolean generateValidationAnnotations) {
    this.generateValidationAnnotations = generateValidationAnnotations;
  }

  @Override public boolean generateSpringAnnotations() {
    return generateSpringAnnotations;
  }

  @Override public void setGenerateSpringAnnotations(boolean generateSpringAnnotations) {
    this.generateSpringAnnotations = generateSpringAnnotations;
  }

  @Override public boolean generateGlobalObjectReferences() {
    return generateGlobalObjectReferences;
  }

  @Override public void setGenerateGlobalObjectReferences(boolean generateGlobalObjectReferences) {
    this.generateGlobalObjectReferences = generateGlobalObjectReferences;
  }

  @Override public boolean generateGlobalCatalogReferences() {
    return generateGlobalObjectReferences() && generateGlobalCatalogReferences;
  }

  @Override public void setGenerateGlobalCatalogReferences(boolean globalCatalogReferences) {
    this.generateGlobalCatalogReferences = globalCatalogReferences;
  }

  @Override public boolean generateGlobalSchemaReferences() {
    return generateGlobalObjectReferences() && generateGlobalSchemaReferences;
  }

  @Override public void setGenerateGlobalSchemaReferences(boolean globalSchemaReferences) {
    this.generateGlobalSchemaReferences = globalSchemaReferences;
  }

  @Override public boolean generateGlobalRoutineReferences() {
    return generateRoutines() && generateGlobalObjectReferences() && generateGlobalRoutineReferences;
  }

  @Override public void setGenerateGlobalRoutineReferences(boolean generateGlobalRoutineReferences) {
    this.generateGlobalRoutineReferences = generateGlobalRoutineReferences;
  }

  @Override public boolean generateGlobalSequenceReferences() {
    return generateSequences() && generateGlobalObjectReferences() && generateGlobalSequenceReferences;
  }

  @Override public void setGenerateGlobalSequenceReferences(boolean generateGlobalSequenceReferences) {
    this.generateGlobalSequenceReferences = generateGlobalSequenceReferences;
  }

  @Override public boolean generateGlobalTableReferences() {
    return generateTables() && generateGlobalObjectReferences() && generateGlobalTableReferences;
  }

  @Override public void setGenerateGlobalTableReferences(boolean generateGlobalTableReferences) {
    this.generateGlobalTableReferences = generateGlobalTableReferences;
  }

  @Override public boolean generateGlobalUDTReferences() {
    return generateUDTs() && generateGlobalObjectReferences() && generateGlobalUDTReferences;
  }

  @Override public void setGenerateGlobalUDTReferences(boolean generateGlobalUDTReferences) {
    this.generateGlobalUDTReferences = generateGlobalUDTReferences;
  }

  @Override public boolean generateGlobalQueueReferences() {
    return generateQueues() && generateGlobalObjectReferences() && generateGlobalQueueReferences;
  }

  @Override public void setGenerateGlobalQueueReferences(boolean globalQueueReferences) {
    this.generateGlobalQueueReferences = globalQueueReferences;
  }

  @Override public boolean generateGlobalLinkReferences() {
    return generateLinks() && generateGlobalObjectReferences() && generateGlobalLinkReferences;
  }

  @Override public void setGenerateGlobalLinkReferences(boolean globalLinkReferences) {
    this.generateGlobalLinkReferences = globalLinkReferences;
  }

  @Override public boolean generateGlobalKeyReferences() {
    return generateKeys() && generateGlobalObjectReferences() && generateGlobalKeyReferences;
  }

  @Override public void setGenerateGlobalKeyReferences(boolean globalKeyReferences) {
    this.generateGlobalKeyReferences = globalKeyReferences;
  }

  @Override public boolean generateQueues() {
    return generateQueues;
  }

  @Override public void setGenerateQueues(boolean queues) {
    this.generateQueues = queues;
  }

  @Override public boolean generateLinks() {
    return generateLinks;
  }

  @Override public void setGenerateLinks(boolean links) {
    this.generateLinks = links;
  }

  @Override public boolean generateKeys() {
    return generateKeys;
  }

  @Override public void setGenerateKeys(boolean keys) {
    this.generateKeys = keys;
  }

  @Override @Deprecated public boolean fluentSetters() {
    return generateFluentSetters();
  }

  @Override @Deprecated public void setFluentSetters(boolean fluentSetters) {
    setGenerateFluentSetters(fluentSetters);
  }

  @Override public boolean generateFluentSetters() {
    return generateFluentSetters;
  }

  @Override public void setGenerateFluentSetters(boolean fluentSetters) {
    this.generateFluentSetters = fluentSetters;
  }

  @Override public boolean generateJavaBeansGettersAndSetters() {
    return generateJavaBeansGettersAndSetters;
  }

  @Override public void setGenerateJavaBeansGettersAndSetters(boolean javaBeansGettersAndSetters) {
    this.generateJavaBeansGettersAndSetters = javaBeansGettersAndSetters;
  }

  @Override public boolean generateVarargsSetters() {
    return generateVarargsSetters;
  }

  @Override public void setGenerateVarargsSetters(boolean varargsSetters) {
    this.generateVarargsSetters = varargsSetters;
  }

  @Override public boolean generatePojosEqualsAndHashCode() {
    return generatePojosEqualsAndHashCode;
  }

  @Override public void setGeneratePojosEqualsAndHashCode(boolean generatePojosEqualsAndHashCode) {
    this.generatePojosEqualsAndHashCode = generatePojosEqualsAndHashCode;
  }

  @Override public boolean generatePojosToString() {
    return generatePojosToString;
  }

  @Override public void setGeneratePojosToString(boolean generatePojosToString) {
    this.generatePojosToString = generatePojosToString;
  }

  @Override @Deprecated public String fullyQualifiedTypes() {
    return generateFullyQualifiedTypes();
  }

  @Override @Deprecated public void setFullyQualifiedTypes(String fullyQualifiedTypes) {
    setGenerateFullyQualifiedTypes(fullyQualifiedTypes);
  }

  @Override public String generateFullyQualifiedTypes() {
    return generateFullyQualifiedTypes;
  }

  @Override public void setGenerateFullyQualifiedTypes(String generateFullyQualifiedTypes) {
    this.generateFullyQualifiedTypes = generateFullyQualifiedTypes;
  }

  @Override public boolean generateJavaTimeTypes() {
    return generateJavaTimeTypes;
  }

  @Override public void setGenerateJavaTimeTypes(boolean generateJavaTimeTypes) {
    this.generateJavaTimeTypes = generateJavaTimeTypes;
  }

  @Override public boolean generateEmptyCatalogs() {
    return generateEmptyCatalogs;
  }

  @Override public void setGenerateEmptyCatalogs(boolean generateEmptyCatalogs) {
    this.generateEmptyCatalogs = generateEmptyCatalogs;
  }

  @Override public boolean generateEmptySchemas() {
    return generateEmptySchemas;
  }

  @Override public void setGenerateEmptySchemas(boolean generateEmptySchemas) {
    this.generateEmptySchemas = generateEmptySchemas;
  }

  @Override public boolean generatePrimaryKeyTypes() {
    return generatePrimaryKeyTypes;
  }

  @Override public void setGeneratePrimaryKeyTypes(boolean generatePrimaryKeyTypes) {
    this.generatePrimaryKeyTypes = generatePrimaryKeyTypes;
  }

  @Override public void setTargetDirectory(String directory) {
    strategy.setTargetDirectory(directory);
  }

  @Override public String getTargetDirectory() {
    return strategy.getTargetDirectory();
  }

  @Override public void setTargetPackage(String packageName) {
    strategy.setTargetPackage(packageName);
  }

  @Override public String getTargetPackage() {
    return strategy.getTargetPackage();
  }

  @Override public String getTargetEncoding() {
    return targetEncoding;
  }

  @Override public void setTargetEncoding(String encoding) {
    this.targetEncoding = encoding;
  }

  protected void empty(File file, String suffix) {
    empty(file, suffix, Collections.<File>emptySet(), Collections.<File>emptySet());
  }

  protected void empty(File file, String suffix, Set<File> keep, Set<File> ignore) {
    if (file != null) {
      if (file.getParentFile() == null) {
        log.warn("WARNING: Root directory configured for code generation. Not deleting anything from previous generations!");
        return;
      }
      for (File i : ignore) {
        if (file.getAbsolutePath().startsWith(i.getAbsolutePath())) {
          return;
        }
      }
      if (file.isDirectory()) {
        File[] children = file.listFiles();
        if (children != null) {
          for (File child : children) {
            empty(child, suffix, keep, ignore);
          }
        }
        File[] childrenAfterDeletion = file.listFiles();
        if (childrenAfterDeletion != null && childrenAfterDeletion.length == 0) {
          file.delete();
        }
      } else {
        if (file.getName().endsWith(suffix) && !keep.contains(file)) {
          file.delete();
        }
      }
    }
  }

  JpaVersion generateJpaVersion;

  @Override public JpaVersion generateJpaVersion() {
    return generateJpaVersion;
  }

  @Override public void setGenerateJpaVersion(JpaVersion generateJpaVersion) {
    this.generateJpaVersion = generateJpaVersion;
  }
}