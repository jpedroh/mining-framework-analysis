package org.jooq.util;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.jooq.tools.StringUtils.defaultIfNull;
import static org.jooq.tools.StringUtils.defaultString;
import static org.jooq.tools.StringUtils.isBlank;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.validation.SchemaFactory;
import org.jooq.Constants;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.JooqLogger.Level;
import org.jooq.tools.StringUtils;
import org.jooq.tools.jdbc.JDBCUtils;
import org.jooq.util.jaxb.Catalog;
import org.jooq.util.jaxb.Configuration;
import org.jooq.util.jaxb.Generate;
import org.jooq.util.jaxb.Jdbc;
import org.jooq.util.jaxb.Matchers;
import org.jooq.util.jaxb.Property;
import org.jooq.util.jaxb.Schema;
import org.jooq.util.jaxb.Strategy;
import org.jooq.util.jaxb.Target;
import org.jooq.util.jaxb.JpaVersion;

public class GenerationTool {
  public static final String DEFAULT_TARGET_ENCODING = "UTF-8";

  public static final String DEFAULT_TARGET_DIRECTORY = "target/generated-sources/jooq";

  public static final String DEFAULT_TARGET_PACKAGENAME = "org.jooq.generated";

  private static final JooqLogger log = JooqLogger.getLogger(GenerationTool.class);

  private ClassLoader loader;

  private DataSource dataSource;

  private Connection connection;

  private boolean close;

  public void setClassLoader(ClassLoader loader) {
    this.loader = loader;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      log.error("Usage : GenerationTool <configuration-file>");
      System.exit(-1);
      return;
    }
    for (String arg : args) {
      InputStream in = GenerationTool.class.getResourceAsStream(arg);
      try {
        if (in == null && !arg.startsWith("/")) {
          in = GenerationTool.class.getResourceAsStream("/" + arg);
        }
        if (in == null && new File(arg).exists()) {
          in = new FileInputStream(new File(arg));
        }
        if (in == null) {
          log.error("Cannot find " + arg + " on classpath, or in directory " + new File(".").getCanonicalPath());
          log.error("-----------");
          log.error("Please be sure it is located");
          log.error("  - on the classpath and qualified as a classpath location.");
          log.error("  - in the local directory or at a global path in the file system.");
          System.exit(-1);
          return;
        }
        log.info("Initialising properties", arg);
        generate(load(in));
      } catch (Exception e) {
        log.error("Cannot read " + arg + ". Error : " + e.getMessage(), e);
        System.exit(-1);
        return;
      } finally {
        if (in != null) {
          in.close();
        }
      }
    }
  }

  @Deprecated public static void main(Configuration configuration) throws Exception {
    new GenerationTool().run(configuration);
  }

  public static void generate(String xml) throws Exception {
    new GenerationTool().run(load(new ByteArrayInputStream(xml.getBytes(DEFAULT_TARGET_ENCODING))));
  }

  public static void generate(Configuration configuration) throws Exception {
    new GenerationTool().run(configuration);
  }

  @SuppressWarnings(value = { "unchecked" }) public void run(Configuration configuration) throws Exception {
    if (configuration.getLogging() != null) {
      switch (configuration.getLogging()) {
        case TRACE:
        JooqLogger.globalThreshold(Level.TRACE);
        break;
        case DEBUG:
        JooqLogger.globalThreshold(Level.DEBUG);
        break;
        case INFO:
        JooqLogger.globalThreshold(Level.INFO);
        break;
        case WARN:
        JooqLogger.globalThreshold(Level.WARN);
        break;
        case ERROR:
        JooqLogger.globalThreshold(Level.ERROR);
        break;
        case FATAL:
        JooqLogger.globalThreshold(Level.FATAL);
        break;
      }
    }
    if (log.isDebugEnabled()) {
      StringWriter writer = new StringWriter();
      JAXB.marshal(configuration, writer);
      log.debug("Input configuration", writer.toString());
    }
    Jdbc j = configuration.getJdbc();
    org.jooq.util.jaxb.Generator g = configuration.getGenerator();
    if (g == null) {
      throw new GeneratorException("The <generator/> tag is mandatory. For details, see " + Constants.NS_CODEGEN);
    }
    if (g.getStrategy() == null) {
      g.setStrategy(new Strategy());
    }
    if (g.getTarget() == null) {
      g.setTarget(new Target());
    }
    try {
      if (connection == null) {
        close = true;
        if (dataSource != null) {
          connection = dataSource.getConnection();
        } else {
          if (j != null) {
            Class<? extends Driver> driver = (Class<? extends Driver>) loadClass(driverClass(j));
            Properties properties = properties(j.getProperties());
            if (!properties.containsKey("user")) {
              properties.put("user", defaultString(defaultString(j.getUser(), j.getUsername())));
            }
            if (!properties.containsKey("password")) {
              properties.put("password", defaultString(j.getPassword()));
            }
            connection = driver.newInstance().connect(defaultString(j.getUrl()), properties);
          }
        }
      }
      j = defaultIfNull(j, new Jdbc());
      Class<Generator> generatorClass = (Class<Generator>) (!isBlank(g.getName()) ? loadClass(trim(g.getName())) : JavaGenerator.class);
      Generator generator = generatorClass.newInstance();
      GeneratorStrategy strategy;
      Matchers matchers = g.getStrategy().getMatchers();
      if (matchers != null) {
        strategy = new MatcherStrategy(matchers);
        if (g.getStrategy().getName() != null) {
          log.warn("WARNING: Matchers take precedence over custom strategy. Strategy ignored: " + g.getStrategy().getName());
          g.getStrategy().setName(null);
        }
      } else {
        Class<GeneratorStrategy> strategyClass = (Class<GeneratorStrategy>) (!isBlank(g.getStrategy().getName()) ? loadClass(trim(g.getStrategy().getName())) : DefaultGeneratorStrategy.class);
        strategy = strategyClass.newInstance();
      }
      generator.setStrategy(strategy);
      org.jooq.util.jaxb.Database d = defaultIfNull(g.getDatabase(), new org.jooq.util.jaxb.Database());
      String databaseName = trim(d.getName());
      Class<? extends Database> databaseClass = !isBlank(databaseName) ? (Class<? extends Database>) loadClass(databaseName) : connection != null ? databaseClass(connection) : databaseClass(j);
      Database database = databaseClass.newInstance();
      database.setProperties(properties(d.getProperties()));
      List<Catalog> catalogs = d.getCatalogs();
      List<Schema> schemata = d.getSchemata();
      boolean catalogsEmpty = catalogs.isEmpty();
      boolean schemataEmpty = schemata.isEmpty();
      if (catalogsEmpty) {
        Catalog catalog = new Catalog();
        catalog.setInputCatalog(trim(d.getInputCatalog()));
        catalog.setOutputCatalog(trim(d.getOutputCatalog()));
        catalog.setOutputCatalogToDefault(d.isOutputCatalogToDefault());
        catalogs.add(catalog);
        if (!StringUtils.isBlank(catalog.getInputCatalog())) {
          catalogsEmpty = false;
        }
        if (schemataEmpty) {
          Schema schema = new Schema();
          schema.setInputSchema(trim(d.getInputSchema()));
          schema.setOutputSchema(trim(d.getOutputSchema()));
          schema.setOutputSchemaToDefault(d.isOutputSchemaToDefault());
          catalog.getSchemata().add(schema);
          if (!StringUtils.isBlank(schema.getInputSchema())) {
            schemataEmpty = false;
          }
        } else {
          catalog.getSchemata().addAll(schemata);
          if (!StringUtils.isBlank(d.getInputSchema())) {
            log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/inputSchema and /configuration/generator/database/schemata");
          }
          if (!StringUtils.isBlank(d.getOutputSchema())) {
            log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/outputSchema and /configuration/generator/database/schemata");
          }
        }
      } else {
        if (!StringUtils.isBlank(d.getInputCatalog())) {
          log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/inputCatalog and /configuration/generator/database/catalogs");
        }
        if (!StringUtils.isBlank(d.getOutputCatalog())) {
          log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/outputCatalog and /configuration/generator/database/catalogs");
        }
        if (!StringUtils.isBlank(d.getInputSchema())) {
          log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/inputSchema and /configuration/generator/database/catalogs");
        }
        if (!StringUtils.isBlank(d.getOutputSchema())) {
          log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/outputSchema and /configuration/generator/database/catalogs");
        }
        if (!schemataEmpty) {
          log.warn("WARNING: Cannot combine configuration properties /configuration/generator/database/catalogs and /configuration/generator/database/schemata");
        }
      }
      for (Catalog catalog : catalogs) {
        if ("".equals(catalog.getOutputCatalog())) {
          log.warn("WARNING: Empty <outputCatalog/> should not be used to model default outputCatalogs. Use <outputCatalogToDefault>true</outputCatalogToDefault>, instead. See also: https://github.com/jOOQ/jOOQ/issues/3018");
        }
        if (TRUE.equals(catalog.isOutputCatalogToDefault())) {
          catalog.setOutputCatalog("");
        } else {
          if (catalog.getOutputCatalog() == null) {
            catalog.setOutputCatalog(trim(catalog.getInputCatalog()));
          }
        }
        for (Schema schema : catalog.getSchemata()) {
          if (catalogsEmpty && schemataEmpty && StringUtils.isBlank(schema.getInputSchema())) {
            if (!StringUtils.isBlank(j.getSchema())) {
              log.warn("WARNING: The configuration property jdbc.Schema is deprecated and will be removed in the future. Use /configuration/generator/database/inputSchema instead");
            }
            schema.setInputSchema(trim(j.getSchema()));
          }
          if ("".equals(schema.getOutputSchema())) {
            log.warn("WARNING: Empty <outputSchema/> should not be used to model default outputSchemas. Use <outputSchemaToDefault>true</outputSchemaToDefault>, instead. See also: https://github.com/jOOQ/jOOQ/issues/3018");
          }
          if (TRUE.equals(schema.isOutputSchemaToDefault())) {
            schema.setOutputSchema("");
          } else {
            if (schema.getOutputSchema() == null) {
              schema.setOutputSchema(trim(schema.getInputSchema()));
            }
          }
        }
      }
      if (catalogsEmpty) {
        log.info("No <inputCatalog/> was provided. Generating ALL available catalogs instead.");
      }
      if (catalogsEmpty && schemataEmpty) {
        log.info("No <inputSchema/> was provided. Generating ALL available schemata instead.");
      }
      database.setConnection(connection);
      database.setConfiguredCatalogs(catalogs);
      database.setConfiguredSchemata(schemata);
      database.setIncludes(new String[] { defaultString(d.getIncludes()) });
      database.setExcludes(new String[] { defaultString(d.getExcludes()) });
      database.setIncludeExcludeColumns(TRUE.equals(d.isIncludeExcludeColumns()));
      database.setIncludeForeignKeys(!FALSE.equals(d.isIncludeForeignKeys()));
      database.setIncludePackages(!FALSE.equals(d.isIncludePackages()));
      database.setIncludeIndexes(!FALSE.equals(d.isIncludeIndexes()));
      database.setIncludePrimaryKeys(!FALSE.equals(d.isIncludePrimaryKeys()));
      database.setIncludeRoutines(!FALSE.equals(d.isIncludeRoutines()));
      database.setIncludeSequences(!FALSE.equals(d.isIncludeSequences()));
      database.setIncludeTables(!FALSE.equals(d.isIncludeTables()));
      database.setIncludeUDTs(!FALSE.equals(d.isIncludeUDTs()));
      database.setIncludeUniqueKeys(!FALSE.equals(d.isIncludeUniqueKeys()));
      database.setRecordVersionFields(new String[] { defaultString(d.getRecordVersionFields()) });
      database.setRecordTimestampFields(new String[] { defaultString(d.getRecordTimestampFields()) });
      database.setSyntheticPrimaryKeys(new String[] { defaultString(d.getSyntheticPrimaryKeys()) });
      database.setOverridePrimaryKeys(new String[] { defaultString(d.getOverridePrimaryKeys()) });
      database.setSyntheticIdentities(new String[] { defaultString(d.getSyntheticIdentities()) });
      database.setConfiguredCustomTypes(d.getCustomTypes());
      database.setConfiguredEnumTypes(d.getEnumTypes());
      database.setConfiguredForcedTypes(d.getForcedTypes());
      if (d.getRegexFlags() != null) {
        database.setRegexFlags(d.getRegexFlags());
      }
      SchemaVersionProvider svp = null;
      CatalogVersionProvider cvp = null;
      if (!StringUtils.isBlank(d.getSchemaVersionProvider())) {
        try {
          svp = (SchemaVersionProvider) Class.forName(d.getSchemaVersionProvider()).newInstance();
          log.info("Using custom schema version provider : " + svp);
        } catch (Exception ignore) {
          if (d.getSchemaVersionProvider().toLowerCase().startsWith("select")) {
            svp = new SQLSchemaVersionProvider(connection, d.getSchemaVersionProvider());
            log.info("Using SQL schema version provider : " + d.getSchemaVersionProvider());
          } else {
            svp = new ConstantSchemaVersionProvider(d.getSchemaVersionProvider());
          }
        }
      }
      if (!StringUtils.isBlank(d.getCatalogVersionProvider())) {
        try {
          cvp = (CatalogVersionProvider) Class.forName(d.getCatalogVersionProvider()).newInstance();
          log.info("Using custom catalog version provider : " + cvp);
        } catch (Exception ignore) {
          if (d.getCatalogVersionProvider().toLowerCase().startsWith("select")) {
            cvp = new SQLCatalogVersionProvider(connection, d.getCatalogVersionProvider());
            log.info("Using SQL catalog version provider : " + d.getCatalogVersionProvider());
          } else {
            cvp = new ConstantCatalogVersionProvider(d.getCatalogVersionProvider());
          }
        }
      }
      if (svp == null) {
        svp = new ConstantSchemaVersionProvider(null);
      }
      if (cvp == null) {
        cvp = new ConstantCatalogVersionProvider(null);
      }
      database.setSchemaVersionProvider(svp);
      database.setCatalogVersionProvider(cvp);
      if (!StringUtils.isBlank(d.getOrderProvider())) {
        Class<?> orderProvider = Class.forName(d.getOrderProvider());
        if (Comparator.class.isAssignableFrom(orderProvider)) {
          database.setOrderProvider((Comparator<Definition>) orderProvider.newInstance());
        } else {
          log.warn("Order provider must be of type java.util.Comparator: " + orderProvider);
        }
      }
      if (d.getEnumTypes().size() > 0) {
        log.warn("DEPRECATED", "The configuration property /configuration/generator/database/enumTypes is experimental and deprecated and will be removed in the future.");
      }
      if (Boolean.TRUE.equals(d.isDateAsTimestamp())) {
        log.warn("DEPRECATED", "The configuration property /configuration/generator/database/dateAsTimestamp is deprecated as it is superseded by custom bindings and converters. It will thus be removed in the future.");
      }
      if (d.isDateAsTimestamp() != null) {
        database.setDateAsTimestamp(d.isDateAsTimestamp());
      }
      if (d.isUnsignedTypes() != null) {
        database.setSupportsUnsignedTypes(d.isUnsignedTypes());
      }
      if (d.isIgnoreProcedureReturnValues() != null) {
        database.setIgnoreProcedureReturnValues(d.isIgnoreProcedureReturnValues());
      }
      if (Boolean.TRUE.equals(d.isIgnoreProcedureReturnValues())) {
        log.warn("DEPRECATED", "The <ignoreProcedureReturnValues/> flag is deprecated and used for backwards-compatibility only. It will be removed in the future.");
      }
      if (StringUtils.isBlank(g.getTarget().getPackageName())) {
        g.getTarget().setPackageName(DEFAULT_TARGET_PACKAGENAME);
      }
      if (StringUtils.isBlank(g.getTarget().getDirectory())) {
        g.getTarget().setDirectory(DEFAULT_TARGET_DIRECTORY);
      }
      if (StringUtils.isBlank(g.getTarget().getEncoding())) {
        g.getTarget().setEncoding(DEFAULT_TARGET_ENCODING);
      }
      generator.setTargetPackage(g.getTarget().getPackageName());
      generator.setTargetDirectory(g.getTarget().getDirectory());
      generator.setTargetEncoding(g.getTarget().getEncoding());
      if (g.getGenerate() == null) {
        g.setGenerate(new Generate());
      }
      if (g.getGenerate().isIndexes() != null) {
        generator.setGenerateIndexes(g.getGenerate().isIndexes());
      }
      if (g.getGenerate().isRelations() != null) {
        generator.setGenerateRelations(g.getGenerate().isRelations());
      }
      if (g.getGenerate().isDeprecated() != null) {
        generator.setGenerateDeprecated(g.getGenerate().isDeprecated());
      }
      if (g.getGenerate().isDeprecationOnUnknownTypes() != null) {
        generator.setGenerateDeprecationOnUnknownTypes(g.getGenerate().isDeprecationOnUnknownTypes());
      }
      if (g.getGenerate().isInstanceFields() != null) {
        generator.setGenerateInstanceFields(g.getGenerate().isInstanceFields());
      }
      if (g.getGenerate().isGeneratedAnnotation() != null) {
        generator.setGenerateGeneratedAnnotation(g.getGenerate().isGeneratedAnnotation());
      }
      if (g.getGenerate().isRoutines() != null) {
        generator.setGenerateRoutines(g.getGenerate().isRoutines());
      }
      if (g.getGenerate().isSequences() != null) {
        generator.setGenerateSequences(g.getGenerate().isSequences());
      }
      if (g.getGenerate().isUdts() != null) {
        generator.setGenerateUDTs(g.getGenerate().isUdts());
      }
      if (g.getGenerate().isTables() != null) {
        generator.setGenerateTables(g.getGenerate().isTables());
      }
      if (g.getGenerate().isRecords() != null) {
        generator.setGenerateRecords(g.getGenerate().isRecords());
      }
      if (g.getGenerate().isRecordsImplementingRecordN() != null) {
        generator.setGenerateRecordsImplementingRecordN(g.getGenerate().isRecordsImplementingRecordN());
      }
      if (g.getGenerate().isPojos() != null) {
        generator.setGeneratePojos(g.getGenerate().isPojos());
      }
      if (g.getGenerate().isImmutablePojos() != null) {
        generator.setGenerateImmutablePojos(g.getGenerate().isImmutablePojos());
      }
      if (g.getGenerate().isInterfaces() != null) {
        generator.setGenerateInterfaces(g.getGenerate().isInterfaces());
      }
      if (g.getGenerate().isImmutableInterfaces() != null) {
        generator.setGenerateImmutableInterfaces(g.getGenerate().isImmutableInterfaces());
      }
      if (g.getGenerate().isDaos() != null) {
        generator.setGenerateDaos(g.getGenerate().isDaos());
      }
      if (g.getGenerate().isJpaAnnotations() != null) {
        generator.setGenerateJPAAnnotations(g.getGenerate().isJpaAnnotations());
        if (g.getGenerate().getJpaVersion() != null) {
          generator.setGenerateJpaVersion(g.getGenerate().getJpaVersion());
        } else {
          generator.setGenerateJpaVersion(JpaVersion.latest());
        }
      } else {
        generator.setGenerateJpaVersion(null);
      }
      if (g.getGenerate().isValidationAnnotations() != null) {
        generator.setGenerateValidationAnnotations(g.getGenerate().isValidationAnnotations());
      }
      if (g.getGenerate().isSpringAnnotations() != null) {
        generator.setGenerateSpringAnnotations(g.getGenerate().isSpringAnnotations());
      }
      if (g.getGenerate().isQueues() != null) {
        generator.setGenerateQueues(g.getGenerate().isQueues());
      }
      if (g.getGenerate().isLinks() != null) {
        generator.setGenerateLinks(g.getGenerate().isLinks());
      }
      if (g.getGenerate().isKeys() != null) {
        generator.setGenerateKeys(g.getGenerate().isKeys());
      }
      if (g.getGenerate().isGlobalObjectReferences() != null) {
        generator.setGenerateGlobalObjectReferences(g.getGenerate().isGlobalObjectReferences());
      }
      if (g.getGenerate().isGlobalCatalogReferences() != null) {
        generator.setGenerateGlobalCatalogReferences(g.getGenerate().isGlobalCatalogReferences());
      }
      if (g.getGenerate().isGlobalSchemaReferences() != null) {
        generator.setGenerateGlobalSchemaReferences(g.getGenerate().isGlobalSchemaReferences());
      }
      if (g.getGenerate().isGlobalRoutineReferences() != null) {
        generator.setGenerateGlobalRoutineReferences(g.getGenerate().isGlobalRoutineReferences());
      }
      if (g.getGenerate().isGlobalSequenceReferences() != null) {
        generator.setGenerateGlobalSequenceReferences(g.getGenerate().isGlobalSequenceReferences());
      }
      if (g.getGenerate().isGlobalTableReferences() != null) {
        generator.setGenerateGlobalTableReferences(g.getGenerate().isGlobalTableReferences());
      }
      if (g.getGenerate().isGlobalUDTReferences() != null) {
        generator.setGenerateGlobalUDTReferences(g.getGenerate().isGlobalUDTReferences());
      }
      if (g.getGenerate().isGlobalQueueReferences() != null) {
        generator.setGenerateGlobalQueueReferences(g.getGenerate().isGlobalQueueReferences());
      }
      if (g.getGenerate().isGlobalLinkReferences() != null) {
        generator.setGenerateGlobalLinkReferences(g.getGenerate().isGlobalLinkReferences());
      }
      if (g.getGenerate().isGlobalKeyReferences() != null) {
        generator.setGenerateGlobalKeyReferences(g.getGenerate().isGlobalKeyReferences());
      }
      if (g.getGenerate().isFluentSetters() != null) {
        generator.setGenerateFluentSetters(g.getGenerate().isFluentSetters());
      }
      if (g.getGenerate().isJavaBeansGettersAndSetters() != null) {
        generator.setGenerateJavaBeansGettersAndSetters(g.getGenerate().isJavaBeansGettersAndSetters());
      }
      if (g.getGenerate().isVarargSetters() != null) {
        generator.setGenerateVarargsSetters(g.getGenerate().isVarargSetters());
      }
      if (g.getGenerate().isPojosEqualsAndHashCode() != null) {
        generator.setGeneratePojosEqualsAndHashCode(g.getGenerate().isPojosEqualsAndHashCode());
      }
      if (g.getGenerate().isPojosToString() != null) {
        generator.setGeneratePojosToString(g.getGenerate().isPojosToString());
      }
      if (g.getGenerate().getFullyQualifiedTypes() != null) {
        generator.setGenerateFullyQualifiedTypes(g.getGenerate().getFullyQualifiedTypes());
      }
      if (g.getGenerate().isJavaTimeTypes() != null) {
        generator.setGenerateJavaTimeTypes(g.getGenerate().isJavaTimeTypes());
      }
      if (g.getGenerate().isEmptyCatalogs() != null) {
        generator.setGenerateEmptyCatalogs(g.getGenerate().isEmptyCatalogs());
      }
      if (g.getGenerate().isEmptySchemas() != null) {
        generator.setGenerateEmptySchemas(g.getGenerate().isEmptySchemas());
      }
      if (g.getGenerate().isPrimaryKeyTypes() != null) {
        generator.setGeneratePrimaryKeyTypes(g.getGenerate().isPrimaryKeyTypes());
      }
      if (g.getDatabase() == null) {
        g.setDatabase(new org.jooq.util.jaxb.Database());
      }
      if (!StringUtils.isBlank(g.getDatabase().getSchemaVersionProvider())) {
        generator.setUseSchemaVersionProvider(true);
      }
      if (!StringUtils.isBlank(g.getDatabase().getCatalogVersionProvider())) {
        generator.setUseCatalogVersionProvider(true);
      }
      if (g.getDatabase().isTableValuedFunctions() != null) {
        generator.setGenerateTableValuedFunctions(g.getDatabase().isTableValuedFunctions());
      } else {
        generator.setGenerateTableValuedFunctions(true);
      }
      strategy.setInstanceFields(generator.generateInstanceFields());
      strategy.setJavaBeansGettersAndSetters(generator.generateJavaBeansGettersAndSetters());
      if (true) {
        ;
      } else {
        if (g.getGenerate().isJavaTimeTypes() != null) {
          log.warn("INVALID CONFIG", "The java.time API cannot be used in the Java 6 distribution of jOOQ 3.9+");
          generator.setGenerateJavaTimeTypes(false);
        }
      }
      generator.generate(database);
    }  finally {
      if (close && connection != null) {
        connection.close();
      }
    }
  }

  private Properties properties(List<Property> properties) {
    Properties result = new Properties();
    for (Property p : properties) {
      result.put(p.getKey(), p.getValue());
    }
    return result;
  }

  private String driverClass(Jdbc j) {
    String result = j.getDriver();
    if (result == null) {
      result = JDBCUtils.driver(j.getUrl());
      log.info("Database", "Inferring driver " + result + " from URL " + j.getUrl());
    }
    return result;
  }

  private Class<? extends Database> databaseClass(Jdbc j) {
    return databaseClass(j.getUrl());
  }

  private Class<? extends Database> databaseClass(Connection c) {
    try {
      return databaseClass(c.getMetaData().getURL());
    } catch (SQLException e) {
      throw new GeneratorException("Error when reading URL from JDBC connection", e);
    }
  }

  private Class<? extends Database> databaseClass(String url) {
    if (isBlank(url)) {
      throw new GeneratorException("No JDBC URL configured.");
    }
    Class<? extends Database> result = Databases.databaseClass(JDBCUtils.dialect(url));
    log.info("Database", "Inferring database " + result.getName() + " from URL " + url);
    return result;
  }

  private Class<?> loadClass(String className) throws ClassNotFoundException {
    try {
      if (loader == null) {
        try {
          return Class.forName(className);
        } catch (ClassNotFoundException e) {
          return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
      } else {
        return loader.loadClass(className);
      }
    } catch (ClassNotFoundException e) {
      if (className.startsWith("org.jooq.util.") && className.endsWith("Database")) {
        log.warn("Type not found", "Your configured database type was not found. This can have several reasons:\n" + "- You want to use a commercial jOOQ Edition, but you pulled the Open Source Edition from Maven Central.\n" + "- You have mis-typed your class name.");
      }
      throw e;
    }
  }

  private static String trim(String string) {
    return (string == null ? null : string.trim());
  }

  public static long copyLarge(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[1024 * 4];
    long count = 0;
    int n = 0;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  public static Configuration load(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    copyLarge(in, out);
    String xml = out.toString();
    xml = xml.replaceAll("<(\\w+:)?configuration xmlns(:\\w+)?=\"http://www.jooq.org/xsd/jooq-codegen-\\d+\\.\\d+\\.\\d+.xsd\">", "<$1configuration xmlns$2=\"" + Constants.NS_CODEGEN + "\">");
    xml = xml.replace("<configuration>", "<configuration xmlns=\"" + Constants.NS_CODEGEN + "\">");
    try {
      SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      javax.xml.validation.Schema schema = sf.newSchema(GenerationTool.class.getResource("/xsd/" + Constants.XSD_CODEGEN));
      JAXBContext ctx = JAXBContext.newInstance(Configuration.class);
      Unmarshaller unmarshaller = ctx.createUnmarshaller();
      unmarshaller.setSchema(schema);
      unmarshaller.setEventHandler(new ValidationEventHandler() {
        @Override public boolean handleEvent(ValidationEvent event) {
          log.warn("Unmarshal warning", event.getMessage());
          return true;
        }
      });
      return (Configuration) unmarshaller.unmarshal(new StringReader(xml));
    } catch (Exception e) {
      throw new GeneratorException("Error while reading XML configuration", e);
    }
  }
}