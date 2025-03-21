package org.wltea.analyzer.dic;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.plugin.analysis.ik.AnalysisIkPlugin;
import org.wltea.analyzer.cfg.Configuration;
import org.apache.logging.log4j.Logger;

/**
 * 词典管理类,单子模式
 */
public class Dictionary {
  private static Dictionary singleton;

  private DictSegment _MainDict;

  private DictSegment _SurnameDict;

  private DictSegment _QuantifierDict;

  private DictSegment _SuffixDict;

  private DictSegment _PrepDict;

  private DictSegment _StopWords;

  /**
	 * 配置对象
	 */
  private Configuration configuration;

  private static final Logger logger = ESLoggerFactory.getLogger(Monitor.class.getName());

  private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

  public static final String PATH_DIC_MAIN = "main.dic";

  public static final String PATH_DIC_SURNAME = "surname.dic";

  public static final String PATH_DIC_QUANTIFIER = "quantifier.dic";

  public static final String PATH_DIC_SUFFIX = "suffix.dic";

  public static final String PATH_DIC_PREP = "preposition.dic";

  public static final String PATH_DIC_STOP = "stopword.dic";

  public static final String PATH_DIC_REMOTE = "remote.dic";

  private final static String FILE_NAME = "IKAnalyzer.cfg.xml";

  private final static String EXT_DICT = "ext_dict";

  private final static String REMOTE_EXT_DICT = "remote_ext_dict";

  private final static String EXT_STOP = "ext_stopwords";

  private final static String REMOTE_EXT_STOP = "remote_ext_stopwords";

  private final static String REBASE_REMOTE_EXT_DICT = "rebase_remote_ext_dict";

  private static Path conf_dir;

  private static Properties props;

  private Dictionary(Configuration cfg) {
    this.configuration = cfg;
    this.props = new Properties();
    this.conf_dir = cfg.getEnvironment().configFile().resolve(AnalysisIkPlugin.PLUGIN_NAME);
    Path configFile = conf_dir.resolve(FILE_NAME);
    InputStream input = null;
    try {
      logger.info("try load config from {}", configFile);
      input = new FileInputStream(configFile.toFile());
    } catch (FileNotFoundException e) {
      conf_dir = cfg.getConfigInPluginDir();
      configFile = conf_dir.resolve(FILE_NAME);
      try {
        logger.info("try load config from {}", configFile);
        input = new FileInputStream(configFile.toFile());
      } catch (FileNotFoundException ex) {
        logger.error("ik-analyzer", e);
      }
    }
    if (input != null) {
      try {
        props.loadFromXML(input);
      } catch (InvalidPropertiesFormatException e) {
        logger.error("ik-analyzer", e);
      } catch (IOException e) {
        logger.error("ik-analyzer", e);
      }
    }
  }

  public static String getProperty(String key) {
    if (props != null) {
      return props.getProperty(key);
    }
    return null;
  }

  /**
	 * 词典初始化 由于IK Analyzer的词典采用Dictionary类的静态方法进行词典初始化
	 * 只有当Dictionary类被实际调用时，才会开始载入词典， 这将延长首次分词操作的时间 该方法提供了一个在应用加载阶段就初始化字典的手段
	 *
	 * @return Dictionary
	 */
  public static synchronized Dictionary initial(Configuration cfg) {
    if (singleton == null) {
      synchronized (Dictionary.class) {
        if (singleton == null) {
          singleton = new Dictionary(cfg);
          singleton.loadMainDict();
          singleton.loadSurnameDict();
          singleton.loadQuantifierDict();
          singleton.loadSuffixDict();
          singleton.loadPrepDict();
          singleton.loadStopWordDict();
          if (cfg.isEnableRemoteDict()) {
            for (String location : singleton.getRemoteExtDictionarys()) {
              pool.scheduleAtFixedRate(new Monitor(location), 10, 60, TimeUnit.SECONDS);
            }
            for (String location : singleton.getRemoteExtStopWordDictionarys()) {
              pool.scheduleAtFixedRate(new Monitor(location), 10, 60, TimeUnit.SECONDS);
            }
          }
          return singleton;
        }
      }
    }
    return singleton;
  }

  public List<String> getExtDictionarys() {
    List<String> extDictFiles = new ArrayList<String>(2);
    String extDictCfg = getProperty(EXT_DICT);
    if (extDictCfg != null) {
      String[] filePaths = extDictCfg.split(";");
      for (String filePath : filePaths) {
        if (filePath != null && !"".equals(filePath.trim())) {
          Path file = PathUtils.get(filePath.trim());
          extDictFiles.add(file.toString());
        }
      }
    }
    return extDictFiles;
  }

  public List<String> getRemoteExtDictionarys() {
    List<String> remoteExtDictFiles = new ArrayList<String>(2);
    String remoteExtDictCfg = getProperty(REMOTE_EXT_DICT);
    if (remoteExtDictCfg != null) {
      String[] filePaths = remoteExtDictCfg.split(";");
      for (String filePath : filePaths) {
        if (filePath != null && !"".equals(filePath.trim())) {
          remoteExtDictFiles.add(filePath);
        }
      }
    }
    return remoteExtDictFiles;
  }

  public List<String> getExtStopWordDictionarys() {
    List<String> extStopWordDictFiles = new ArrayList<String>(2);
    String extStopWordDictCfg = getProperty(EXT_STOP);
    if (extStopWordDictCfg != null) {
      String[] filePaths = extStopWordDictCfg.split(";");
      for (String filePath : filePaths) {
        if (filePath != null && !"".equals(filePath.trim())) {
          Path file = PathUtils.get(filePath.trim());
          extStopWordDictFiles.add(file.toString());
        }
      }
    }
    return extStopWordDictFiles;
  }

  public List<String> getRemoteExtStopWordDictionarys() {
    List<String> remoteExtStopWordDictFiles = new ArrayList<String>(2);
    String remoteExtStopWordDictCfg = getProperty(REMOTE_EXT_STOP);
    if (remoteExtStopWordDictCfg != null) {
      String[] filePaths = remoteExtStopWordDictCfg.split(";");
      for (String filePath : filePaths) {
        if (filePath != null && !"".equals(filePath.trim())) {
          remoteExtStopWordDictFiles.add(filePath);
        }
      }
    }
    return remoteExtStopWordDictFiles;
  }

  public static String getDictRoot() {
    return conf_dir.toAbsolutePath().toString();
  }

  /**
	 * 获取词典单子实例
	 *
	 * @return Dictionary 单例对象
	 */
  public static Dictionary getSingleton() {
    if (singleton == null) {
      throw new IllegalStateException("\u8bcd\u5178\u5c1a\u672a\u521d\u59cb\u5316\uff0c\u8bf7\u5148\u8c03\u7528initial\u65b9\u6cd5");
    }
    return singleton;
  }

  /**
	 * 批量加载新词条
	 *
	 * @param words
	 *            Collection<String>词条列表
	 */
  public void addWords(Collection<String> words) {
    if (words != null) {
      for (String word : words) {
        if (word != null) {
          singleton._MainDict.fillSegment(word.trim().toCharArray());
        }
      }
    }
  }

  /**
	 * 批量移除（屏蔽）词条
	 */
  public void disableWords(Collection<String> words) {
    if (words != null) {
      for (String word : words) {
        if (word != null) {
          singleton._MainDict.disableSegment(word.trim().toCharArray());
        }
      }
    }
  }

  /**
	 * 检索匹配主词典
	 *
	 * @return Hit 匹配结果描述
	 */
  public Hit matchInMainDict(char[] charArray) {
    return singleton._MainDict.match(charArray);
  }

  /**
	 * 检索匹配主词典
	 *
	 * @return Hit 匹配结果描述
	 */
  public Hit matchInMainDict(char[] charArray, int begin, int length) {
    return singleton._MainDict.match(charArray, begin, length);
  }

  /**
	 * 检索匹配量词词典
	 *
	 * @return Hit 匹配结果描述
	 */
  public Hit matchInQuantifierDict(char[] charArray, int begin, int length) {
    return singleton._QuantifierDict.match(charArray, begin, length);
  }

  /**
	 * 从已匹配的Hit中直接取出DictSegment，继续向下匹配
	 *
	 * @return Hit
	 */
  public Hit matchWithHit(char[] charArray, int currentIndex, Hit matchedHit) {
    DictSegment ds = matchedHit.getMatchedDictSegment();
    return ds.match(charArray, currentIndex, 1, matchedHit);
  }

  /**
	 * 判断是否是停止词
	 *
	 * @return boolean
	 */
  public boolean isStopWord(char[] charArray, int begin, int length) {
    return singleton._StopWords.match(charArray, begin, length).isMatch();
  }

  /**
	 * 加载主词典及扩展词典
	 */
  private void loadMainDict() {
    _MainDict = new DictSegment((char) 0);
    Path file = PathUtils.get(getDictRoot(), Dictionary.PATH_DIC_MAIN);
    InputStream is = null;
    try {
      is = new FileInputStream(file.toFile());
    } catch (FileNotFoundException e) {
      logger.error(e.getMessage(), e);
    }
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
      String theWord = null;
      do {
        theWord = br.readLine();
        if (theWord != null && !"".equals(theWord.trim())) {
          _MainDict.fillSegment(theWord.trim().toCharArray());
        }
      } while(theWord != null);
    } catch (IOException e) {
      logger.error("ik-analyzer", e);
    } finally {
      try {
        if (is != null) {
          is.close();
          is = null;
        }
      } catch (IOException e) {
        logger.error("ik-analyzer", e);
      }
    }
    this.loadExtDict();
    this.loadRemoteExtDict();
  }

  /**
	 * 加载用户配置的扩展词典到主词库表
	 */
  private void loadExtDict() {
    List<String> extDictFiles = getExtDictionarys();
    if (extDictFiles != null) {
      InputStream is = null;
      for (String extDictName : extDictFiles) {
        logger.info("[Dict Loading] " + extDictName);
        Path file = PathUtils.get(getDictRoot(), extDictName);
        try {
          is = new FileInputStream(file.toFile());
        } catch (FileNotFoundException e) {
          logger.error("ik-analyzer", e);
        }
        if (is == null) {
          continue;
        }
        try {
          BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
          String theWord = null;
          do {
            theWord = br.readLine();
            if (theWord != null && !"".equals(theWord.trim())) {
              _MainDict.fillSegment(theWord.trim().toCharArray());
            }
          } while(theWord != null);
        } catch (IOException e) {
          logger.error("ik-analyzer", e);
        } finally {
          try {
            if (is != null) {
              is.close();
              is = null;
            }
          } catch (IOException e) {
            logger.error("ik-analyzer", e);
          }
        }
      }
    }
  }

  /**
	 * 加载远程扩展词典到主词库表
	 */
  private void loadRemoteExtDict() {
    List<String> remoteExtDictFiles = getRemoteExtDictionarys();
    for (String location : remoteExtDictFiles) {
      logger.info("[Dict Loading] " + location);
      List<String> lists = getRemoteWords(location);
      if (lists == null) {
        logger.error("[Dict Loading] " + location + "\u52a0\u8f7d\u5931\u8d25");
        continue;
      }
      for (String theWord : lists) {
        if (theWord != null && !"".equals(theWord.trim())) {
          logger.info(theWord);
          _MainDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
        }
      }
    }
  }

  /**
	 * 从远程服务器上下载自定义词条
	 */
  private static List<String> getRemoteWords(String location) {
    List<String> buffer = new ArrayList<String>();
    RequestConfig rc = RequestConfig.custom().setConnectionRequestTimeout(10 * 1000).setConnectTimeout(10 * 1000).setSocketTimeout(60 * 1000).build();
    CloseableHttpClient httpclient = HttpClients.createDefault();
    CloseableHttpResponse response;
    BufferedReader in;
    HttpGet get = new HttpGet(location);
    get.setConfig(rc);
    try {
      response = httpclient.execute(get);
      if (response.getStatusLine().getStatusCode() == 200) {
        String charset = "UTF-8";
        if (response.getEntity().getContentType().getValue().contains("charset=")) {
          String contentType = response.getEntity().getContentType().getValue();
          charset = contentType.substring(contentType.lastIndexOf("=") + 1);
        }
        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
        String line;
        while ((line = in.readLine()) != null) {
          buffer.add(line);
        }
        in.close();
        response.close();
        String isRebase = getProperty(REBASE_REMOTE_EXT_DICT);
        if (isRebase.equals("true")) {
          return writeRemoteWords(buffer);
        }
        return buffer;
      }
      response.close();
    } catch (ClientProtocolException e) {
      logger.error("getRemoteWords {} error", e, location);
    } catch (IllegalStateException e) {
      logger.error("getRemoteWords {} error", e, location);
    } catch (IOException e) {
      logger.error("getRemoteWords {} error", e, location);
    }
    return buffer;
  }

  /**
	 * 加载用户扩展的停止词词典
	 */
  private void loadStopWordDict() {
    _StopWords = new DictSegment((char) 0);
    Path file = PathUtils.get(getDictRoot(), Dictionary.PATH_DIC_STOP);
    InputStream is = null;
    try {
      is = new FileInputStream(file.toFile());
    } catch (FileNotFoundException e) {
      logger.error(e.getMessage(), e);
    }
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
      String theWord = null;
      do {
        theWord = br.readLine();
        if (theWord != null && !"".equals(theWord.trim())) {
          _StopWords.fillSegment(theWord.trim().toCharArray());
        }
      } while(theWord != null);
    } catch (IOException e) {
      logger.error("ik-analyzer", e);
    } finally {
      try {
        if (is != null) {
          is.close();
          is = null;
        }
      } catch (IOException e) {
        logger.error("ik-analyzer", e);
      }
    }
    List<String> extStopWordDictFiles = getExtStopWordDictionarys();
    if (extStopWordDictFiles != null) {
      is = null;
      for (String extStopWordDictName : extStopWordDictFiles) {
        logger.info("[Dict Loading] " + extStopWordDictName);
        file = PathUtils.get(getDictRoot(), extStopWordDictName);
        try {
          is = new FileInputStream(file.toFile());
        } catch (FileNotFoundException e) {
          logger.error("ik-analyzer", e);
        }
        if (is == null) {
          continue;
        }
        try {
          BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
          String theWord = null;
          do {
            theWord = br.readLine();
            if (theWord != null && !"".equals(theWord.trim())) {
              _StopWords.fillSegment(theWord.trim().toCharArray());
            }
          } while(theWord != null);
        } catch (IOException e) {
          logger.error("ik-analyzer", e);
        } finally {
          try {
            if (is != null) {
              is.close();
              is = null;
            }
          } catch (IOException e) {
            logger.error("ik-analyzer", e);
          }
        }
      }
    }
    List<String> remoteExtStopWordDictFiles = getRemoteExtStopWordDictionarys();
    for (String location : remoteExtStopWordDictFiles) {
      logger.info("[Dict Loading] " + location);
      List<String> lists = getRemoteWords(location);
      if (lists == null) {
        logger.error("[Dict Loading] " + location + "\u52a0\u8f7d\u5931\u8d25");
        continue;
      }
      for (String theWord : lists) {
        if (theWord != null && !"".equals(theWord.trim())) {
          logger.info(theWord);
          _StopWords.fillSegment(theWord.trim().toLowerCase().toCharArray());
        }
      }
    }
  }

  /**
	 * 加载量词词典
	 */
  private void loadQuantifierDict() {
    _QuantifierDict = new DictSegment((char) 0);
    Path file = PathUtils.get(getDictRoot(), Dictionary.PATH_DIC_QUANTIFIER);
    InputStream is = null;
    try {
      is = new FileInputStream(file.toFile());
    } catch (FileNotFoundException e) {
      logger.error("ik-analyzer", e);
    }
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
      String theWord = null;
      do {
        theWord = br.readLine();
        if (theWord != null && !"".equals(theWord.trim())) {
          _QuantifierDict.fillSegment(theWord.trim().toCharArray());
        }
      } while(theWord != null);
    } catch (IOException ioe) {
      logger.error("Quantifier Dictionary loading exception.");
    } finally {
      try {
        if (is != null) {
          is.close();
          is = null;
        }
      } catch (IOException e) {
        logger.error("ik-analyzer", e);
      }
    }
  }

  private void loadSurnameDict() {
    _SurnameDict = new DictSegment((char) 0);
    Path file = PathUtils.get(getDictRoot(), Dictionary.PATH_DIC_SURNAME);
    InputStream is = null;
    try {
      is = new FileInputStream(file.toFile());
    } catch (FileNotFoundException e) {
      logger.error("ik-analyzer", e);
    }
    if (is == null) {
      throw new RuntimeException("Surname Dictionary not found!!!");
    }
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
      String theWord;
      do {
        theWord = br.readLine();
        if (theWord != null && !"".equals(theWord.trim())) {
          _SurnameDict.fillSegment(theWord.trim().toCharArray());
        }
      } while(theWord != null);
    } catch (IOException e) {
      logger.error("ik-analyzer", e);
    } finally {
      try {
        if (is != null) {
          is.close();
          is = null;
        }
      } catch (IOException e) {
        logger.error("ik-analyzer", e);
      }
    }
  }

  private void loadSuffixDict() {
    _SuffixDict = new DictSegment((char) 0);
    Path file = PathUtils.get(getDictRoot(), Dictionary.PATH_DIC_SUFFIX);
    InputStream is = null;
    try {
      is = new FileInputStream(file.toFile());
    } catch (FileNotFoundException e) {
      logger.error("ik-analyzer", e);
    }
    if (is == null) {
      throw new RuntimeException("Suffix Dictionary not found!!!");
    }
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
      String theWord;
      do {
        theWord = br.readLine();
        if (theWord != null && !"".equals(theWord.trim())) {
          _SuffixDict.fillSegment(theWord.trim().toCharArray());
        }
      } while(theWord != null);
    } catch (IOException e) {
      logger.error("ik-analyzer", e);
    } finally {
      try {
        is.close();
        is = null;
      } catch (IOException e) {
        logger.error("ik-analyzer", e);
      }
    }
  }

  private void loadPrepDict() {
    _PrepDict = new DictSegment((char) 0);
    Path file = PathUtils.get(getDictRoot(), Dictionary.PATH_DIC_PREP);
    InputStream is = null;
    try {
      is = new FileInputStream(file.toFile());
    } catch (FileNotFoundException e) {
      logger.error("ik-analyzer", e);
    }
    if (is == null) {
      throw new RuntimeException("Preposition Dictionary not found!!!");
    }
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
      String theWord;
      do {
        theWord = br.readLine();
        if (theWord != null && !"".equals(theWord.trim())) {
          _PrepDict.fillSegment(theWord.trim().toCharArray());
        }
      } while(theWord != null);
    } catch (IOException e) {
      logger.error("ik-analyzer", e);
    } finally {
      try {
        is.close();
        is = null;
      } catch (IOException e) {
        logger.error("ik-analyzer", e);
      }
    }
  }

  public void reLoadMainDict() {
    logger.info("\u91cd\u65b0\u52a0\u8f7d\u8bcd\u5178...");
    Dictionary tmpDict = new Dictionary(configuration);
    tmpDict.configuration = getSingleton().configuration;
    tmpDict.loadMainDict();
    tmpDict.loadStopWordDict();
    _MainDict = tmpDict._MainDict;
    _StopWords = tmpDict._StopWords;
    logger.info("\u91cd\u65b0\u52a0\u8f7d\u8bcd\u5178\u5b8c\u6bd5...");
  }

  /**
	 * 把远程扩展字典的词备份下来，并放入用户自定义的扩展字典中
	 */
  private static List<String> writeRemoteWords(List<String> buffer) throws IOException {
    if (buffer == null || buffer.size() == 0) {
      return new ArrayList<>();
    }
    Set<String> bufferSet = new HashSet<>(buffer);
    if (bufferSet.size() == 0) {
      return new ArrayList<>();
    }
    Path path = PathUtils.get(getDictRoot(), Dictionary.PATH_DIC_REMOTE);
    File file = path.toFile();
    List<String> words = new ArrayList<>();
    FileReader fr = new FileReader(file);
    BufferedReader br = new BufferedReader(fr);
    String line;
    while ((line = br.readLine()) != null) {
      words.add(line);
    }
    for (String str : words) {
      if (bufferSet.contains(str)) {
        bufferSet.remove(str);
      }
    }
    fr.close();
    br.close();
    FileWriter fw = new FileWriter(file, true);
    for (String buf : bufferSet) {
      fw.append("\n");
      fw.append(buf);
    }
    fw.close();
    return new ArrayList<>(bufferSet);
  }
}