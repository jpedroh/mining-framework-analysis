package me.zhengjie.utils;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.*;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.GenConfig;
import me.zhengjie.domain.ColumnInfo;
import org.springframework.util.ObjectUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.*;
import static me.zhengjie.utils.FileUtil.SYS_TEM_DIR;

@Slf4j @SuppressWarnings(value = { "unchecked", "all" }) public class GenUtil {
  private static final String TIMESTAMP = "Timestamp";

  private static final String BIGDECIMAL = "BigDecimal";

  public static final String PK = "PRI";

  public static final String EXTRA = "auto_increment";

  private static List<String> getAdminTemplateNames() {
    List<String> templateNames = new ArrayList<>();
    templateNames.add("Entity");
    templateNames.add("Dto");
    templateNames.add("Mapper");
    templateNames.add("Controller");
    templateNames.add("QueryCriteria");
    templateNames.add("Service");
    templateNames.add("ServiceImpl");
    templateNames.add("Repository");
    return templateNames;
  }

  private static List<String> getFrontTemplateNames() {
    List<String> templateNames = new ArrayList<>();
    templateNames.add("index");
    templateNames.add("api");
    return templateNames;
  }

  public static List<Map<String, Object>> preview(List<ColumnInfo> columns, GenConfig genConfig) {
    Map<String, Object> genMap = getGenMap(columns, genConfig);
    List<Map<String, Object>> genList = new ArrayList<>();
    List<String> templates = getAdminTemplateNames();
    TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
    for (String templateName : templates) {
      Map<String, Object> map = new HashMap<>(1);
      Template template = engine.getTemplate("generator/admin/" + templateName + ".ftl");
      map.put("content", template.render(genMap));
      map.put("name", templateName);
      genList.add(map);
    }
    templates = getFrontTemplateNames();
    for (String templateName : templates) {
      Map<String, Object> map = new HashMap<>(1);
      Template template = engine.getTemplate("generator/front/" + templateName + ".ftl");
      map.put(templateName, template.render(genMap));
      map.put("content", template.render(genMap));
      map.put("name", templateName);
      genList.add(map);
    }
    return genList;
  }

  public static String download(List<ColumnInfo> columns, GenConfig genConfig) throws IOException {
    String tempPath = SYS_TEM_DIR + "eladmin-gen-temp" + File.separator + genConfig.getTableName() + File.separator;
    Map<String, Object> genMap = getGenMap(columns, genConfig);
    TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
    List<String> templates = getAdminTemplateNames();
    for (String templateName : templates) {
      Template template = engine.getTemplate("generator/admin/" + templateName + ".ftl");
      String filePath = getAdminFilePath(templateName, genConfig, genMap.get("className").toString(), tempPath + "eladmin" + File.separator);
      assert filePath != null;
      File file = new File(filePath);
      if (!genConfig.getCover() && FileUtil.exist(file)) {
        continue;
      }
      genFile(file, template, genMap);
    }
    templates = getFrontTemplateNames();
    for (String templateName : templates) {
      Template template = engine.getTemplate("generator/front/" + templateName + ".ftl");
      String path = tempPath + "eladmin-web" + File.separator;
      String apiPath = path + "src" + File.separator + "api" + File.separator;
      String srcPath = path + "src" + File.separator + "views" + File.separator + genMap.get("changeClassName").toString() + File.separator;
      String filePath = getFrontFilePath(templateName, apiPath, srcPath, genMap.get("changeClassName").toString());
      assert filePath != null;
      File file = new File(filePath);
      if (!genConfig.getCover() && FileUtil.exist(file)) {
        continue;
      }
      genFile(file, template, genMap);
    }
    return tempPath;
  }

  public static void generatorCode(List<ColumnInfo> columnInfos, GenConfig genConfig) throws IOException {
    Map<String, Object> genMap = getGenMap(columnInfos, genConfig);
    TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
    List<String> templates = getAdminTemplateNames();
    for (String templateName : templates) {
      Template template = engine.getTemplate("generator/admin/" + templateName + ".ftl");
      String rootPath = System.getProperty("user.dir");
      String filePath = getAdminFilePath(templateName, genConfig, genMap.get("className").toString(), rootPath);
      assert filePath != null;
      File file = new File(filePath);
      if (!genConfig.getCover() && FileUtil.exist(file)) {
        continue;
      }
      genFile(file, template, genMap);
    }
    templates = getFrontTemplateNames();
    for (String templateName : templates) {
      Template template = engine.getTemplate("generator/front/" + templateName + ".ftl");
      String filePath = getFrontFilePath(templateName, genConfig.getApiPath(), genConfig.getPath(), genMap.get("changeClassName").toString());
      assert filePath != null;
      File file = new File(filePath);
      if (!genConfig.getCover() && FileUtil.exist(file)) {
        continue;
      }
      genFile(file, template, genMap);
    }
  }

  private static Map<String, Object> getGenMap(List<ColumnInfo> columnInfos, GenConfig genConfig) {
    Map<String, Object> genMap = new HashMap<>(16);
    genMap.put("apiAlias", genConfig.getApiAlias());
    genMap.put("package", genConfig.getPack());
    genMap.put("moduleName", genConfig.getModuleName());
    genMap.put("author", genConfig.getAuthor());
    genMap.put("date", LocalDate.now().toString());
    genMap.put("tableName", genConfig.getTableName());
    String className = StringUtils.toCapitalizeCamelCase(genConfig.getTableName());
    String changeClassName = StringUtils.toCamelCase(genConfig.getTableName());
    if (StringUtils.isNotEmpty(genConfig.getPrefix())) {
      className = StringUtils.toCapitalizeCamelCase(StrUtil.removePrefix(genConfig.getTableName(), genConfig.getPrefix()));
      changeClassName = StringUtils.toCamelCase(StrUtil.removePrefix(genConfig.getTableName(), genConfig.getPrefix()));
      changeClassName = StringUtils.uncapitalize(changeClassName);
    }
    genMap.put("className", className);
    genMap.put("changeClassName", changeClassName);
    genMap.put("hasTimestamp", false);
    genMap.put("queryHasTimestamp", false);
    genMap.put("hasBigDecimal", false);
    genMap.put("queryHasBigDecimal", false);
    genMap.put("hasQuery", false);
    genMap.put("auto", false);
    genMap.put("hasDict", false);
    genMap.put("hasDateAnnotation", false);
    List<Map<String, Object>> columns = new ArrayList<>();
    List<Map<String, Object>> queryColumns = new ArrayList<>();
    List<String> dicts = new ArrayList<>();
    List<Map<String, Object>> betweens = new ArrayList<>();
    List<Map<String, Object>> isNotNullColumns = new ArrayList<>();
    String columnPrefix = genConfig.getColumnPrefix();
    for (ColumnInfo column : columnInfos) {
      Map<String, Object> listMap = new HashMap<>(16);
      listMap.put("remark", column.getRemark());
      listMap.put("columnKey", column.getKeyType());
      String colType = ColUtil.cloToJava(column.getColumnType());
      String changeColumnName = "";
      if (StrUtil.isNotBlank(columnPrefix)) {
        changeColumnName = StringUtils.toCamelCase(StrUtil.removePrefix(column.getColumnName(), columnPrefix));
      } else {
        changeColumnName = StringUtils.toCamelCase(column.getColumnName());
      }
      String capitalColumnName = "";
      if (StrUtil.isNotBlank(columnPrefix)) {
        capitalColumnName = StringUtils.toCapitalizeCamelCase(StrUtil.removePrefix(column.getColumnName(), columnPrefix));
      } else {
        capitalColumnName = StringUtils.toCapitalizeCamelCase(column.getColumnName());
      }
      if (PK.equals(column.getKeyType())) {
        genMap.put("pkColumnType", colType);
        genMap.put("pkChangeColName", changeColumnName);
        genMap.put("pkCapitalColName", capitalColumnName);
      }
      if (TIMESTAMP.equals(colType)) {
        genMap.put("hasTimestamp", true);
      }
      if (BIGDECIMAL.equals(colType)) {
        genMap.put("hasBigDecimal", true);
      }
      if (EXTRA.equals(column.getExtra())) {
        genMap.put("auto", true);
      }
      if (StringUtils.isNotBlank(column.getDictName())) {
        genMap.put("hasDict", true);
        if (!dicts.contains(column.getDictName())) {
          dicts.add(column.getDictName());
        }
      }
      listMap.put("columnType", colType);
      listMap.put("columnName", column.getColumnName());
      listMap.put("istNotNull", column.getNotNull());
      listMap.put("columnShow", column.getListShow());
      listMap.put("formShow", column.getFormShow());
      listMap.put("formType", StringUtils.isNotBlank(column.getFormType()) ? column.getFormType() : "Input");
      listMap.put("changeColumnName", changeColumnName);
      listMap.put("capitalColumnName", capitalColumnName);
      listMap.put("dictName", column.getDictName());
      listMap.put("dateAnnotation", column.getDateAnnotation());
      if (StringUtils.isNotBlank(column.getDateAnnotation())) {
        genMap.put("hasDateAnnotation", true);
      }
      if (column.getNotNull()) {
        isNotNullColumns.add(listMap);
      }
      if (!StringUtils.isBlank(column.getQueryType())) {
        listMap.put("queryType", column.getQueryType());
        genMap.put("hasQuery", true);
        if (TIMESTAMP.equals(colType)) {
          genMap.put("queryHasTimestamp", true);
        }
        if (BIGDECIMAL.equals(colType)) {
          genMap.put("queryHasBigDecimal", true);
        }
        if ("between".equalsIgnoreCase(column.getQueryType())) {
          betweens.add(listMap);
        } else {
          queryColumns.add(listMap);
        }
      }
      columns.add(listMap);
    }
    genMap.put("columns", columns);
    genMap.put("queryColumns", queryColumns);
    genMap.put("dicts", dicts);
    genMap.put("betweens", betweens);
    genMap.put("isNotNullColumns", isNotNullColumns);
    return genMap;
  }

  private static String getAdminFilePath(String templateName, GenConfig genConfig, String className, String rootPath) {
    String projectPath = rootPath + File.separator + genConfig.getModuleName();
    String packagePath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
    if (!ObjectUtils.isEmpty(genConfig.getPack())) {
      packagePath += genConfig.getPack().replace(".", File.separator) + File.separator;
    }
    if ("Entity".equals(templateName)) {
      return packagePath + "domain" + File.separator + className + ".java";
    }
    if ("Controller".equals(templateName)) {
      return packagePath + "rest" + File.separator + className + "Controller.java";
    }
    if ("Service".equals(templateName)) {
      return packagePath + "service" + File.separator + className + "Service.java";
    }
    if ("ServiceImpl".equals(templateName)) {
      return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
    }
    if ("Dto".equals(templateName)) {
      return packagePath + "service" + File.separator + "dto" + File.separator + className + "Dto.java";
    }
    if ("QueryCriteria".equals(templateName)) {
      return packagePath + "service" + File.separator + "dto" + File.separator + className + "QueryCriteria.java";
    }
    if ("Mapper".equals(templateName)) {
      return packagePath + "service" + File.separator + "mapstruct" + File.separator + className + "Mapper.java";
    }
    if ("Repository".equals(templateName)) {
      return packagePath + "repository" + File.separator + className + "Repository.java";
    }
    return null;
  }

  private static String getFrontFilePath(String templateName, String apiPath, String path, String apiName) {
    if ("api".equals(templateName)) {
      return apiPath + File.separator + apiName + ".js";
    }
    if ("index".equals(templateName)) {
      return path + File.separator + "index.vue";
    }
    return null;
  }

  private static void genFile(File file, Template template, Map<String, Object> map) throws IOException {
    Writer writer = null;
    try {
      FileUtil.touch(file);
      writer = new FileWriter(file);
      template.render(map, writer);
    } catch (TemplateException | IOException e) {
      throw new RuntimeException(e);
    } finally {
      assert writer != null;
      writer.close();
    }
  }
}