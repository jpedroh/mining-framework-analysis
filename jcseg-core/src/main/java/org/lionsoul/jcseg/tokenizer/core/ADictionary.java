package org.lionsoul.jcseg.tokenizer.core;
import java.io.*;
import java.io.Serializable;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.lionsoul.jcseg.tokenizer.Word;
import org.lionsoul.jcseg.util.StringUtil;

/**
 * Dictionary abstract super class
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public abstract class ADictionary implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
     * the default auto load task file name 
    */
  public static final String AL_TODO_FILE = "lex-autoload.todo";

  protected JcsegTaskConfig config;

  protected boolean sync;

  /**auto load thread */
  private Thread autoloadThread = null;

  /**
     * maximum length for the Chinese words after the LATIN word
     * or the one before it used to match Chinese and English mix word, 
     * like 'B超,AA制...' or style compose style like '卡拉ok'.
     * 
     * since 2.0.1 the value will be reset during the lexicon load process
     */
  volatile public int mixSuffixLength = 1;

  volatile public int mixPrefixLength = 1;

  /**
     * synonyms buffer
    */
  private final List<String[]> synBuffer = Collections.synchronizedList(new ArrayList<String[]>());

  private final Map<String, SynonymsEntry> rootMap = new HashMap<String, SynonymsEntry>();

  /**
     * initialize the ADictionary
     * 
     * @param   config
     * @param   sync
    */
  public ADictionary(JcsegTaskConfig config, Boolean sync) {
    this.sync = sync.booleanValue();
    this.config = config;
  }

  /**
     * load all the words from a specified lexicon file
     * 
     * @param   file
     * @throws  IOException 
     * @throws  FileNotFoundException 
     * @throws  NumberFormatException 
     */
  public void load(File file) throws NumberFormatException, FileNotFoundException, IOException {
    loadWords(config, this, file, synBuffer);
  }

  /**
     * load all the words from a specified lexicon path
     * 
     * @param   file
     * @throws  IOException 
     * @throws  FileNotFoundException 
     * @throws  NumberFormatException 
    */
  public void load(String file) throws NumberFormatException, FileNotFoundException, IOException {
    loadWords(config, this, file, synBuffer);
  }

  /**
     * load all the words from a specified lexicon input stream
     * 
     * @param   is
     * @throws  IOException 
     * @throws  NumberFormatException 
    */
  public void load(InputStream is) throws NumberFormatException, IOException {
    loadWords(config, this, is, synBuffer);
  }

  /**
     * load the all the words from all the files under a specified lexicon directory
     * 
     * @param   lexDir
     * @throws  IOException
     */
  public void loadDirectory(String lexDir) throws IOException {
    File path = new File(lexDir);
    if (!path.exists()) {
      throw new IOException("Lexicon directory [" + lexDir + "] does\'n exists.");
    }
    File[] files = path.listFiles(new FilenameFilter() {
      @Override public boolean accept(File dir, String name) {
        return (name.startsWith("lex-") && name.endsWith(".lex"));
      }
    });
    for (File file : files) {
      load(file);
    }
  }

  /**
     * load all the words from all the files under the specified class path.
     * 
     * added at 2016/07/12:
     * only in the jar file could the ZipInputStream available
     * add IDE classpath supported here
     *
     * @since   1.9.9
     * @throws  IOException
    */
  public void loadClassPath() throws IOException {
    Class<?> dClass = this.getClass();
    CodeSource codeSrc = this.getClass().getProtectionDomain().getCodeSource();
    if (codeSrc == null) {
      return;
    }
    String codePath = codeSrc.getLocation().getPath();
    if (codePath.toLowerCase().endsWith(".jar")) {
      ZipInputStream zip = new ZipInputStream(codeSrc.getLocation().openStream());
      while (true) {
        ZipEntry e = zip.getNextEntry();
        if (e == null) {
          break;
        }
        String fileName = e.getName();
        if (fileName.endsWith(".lex") && fileName.startsWith("lexicon/lex-")) {
          load(dClass.getResourceAsStream("/" + fileName));
        }
      }
    } else {
      loadDirectory(codePath + "/lexicon");
    }
  }

  /**
     * 1, synonyms words to synonyms entry
     * 2, loop each synonyms word and set the IWord#synEntry
     * 3, clear the synonyms buffer
    */
  public void resetSynonymsNet() {
    synchronized (synBuffer) {
      if (synBuffer.size() == 0) {
        return;
      }
      Iterator<String[]> it = synBuffer.iterator();
      while (it.hasNext()) {
        String[] synLine = it.next();
        IWord baseWord = get(ILexicon.CJK_WORD, synLine[0]);
        if (baseWord == null) {
          continue;
        }
        SynonymsEntry synEntry = rootMap.get(baseWord.getValue());
        if (synEntry == null) {
          synEntry = new SynonymsEntry(baseWord);
          rootMap.put(baseWord.getValue(), synEntry);
          synEntry.add(baseWord);
        }
        for (int i = 1; i < synLine.length; i++) {
          String[] parts = synLine[i].split("\\s*/\\s*");
          IWord synWord = get(ILexicon.CJK_WORD, parts[0]);
          if (synWord == null) {
            synWord = new Word(parts[0], IWord.T_CJK_WORD);
            add(ILexicon.CJK_WORD, synWord);
          }
          if (synWord.getPartSpeech() == null) {
            synWord.setPartSpeech(baseWord.getPartSpeech());
          }
          if (synWord.getEntity() == null) {
            synWord.setEntity(baseWord.getEntity());
          }
          if (parts.length > 1) {
            synWord.setPinyin(parts[1]);
          }
          synEntry.add(synWord);
        }
      }
      synBuffer.clear();
    }
  }

  /**
     * start the lexicon autoload thread
    */
  public void startAutoload() {
    if (autoloadThread != null || config.getLexiconPath() == null) {
      return;
    }
    autoloadThread = new Thread(new Runnable() {
      @Override public void run() {
        String[] paths = config.getLexiconPath();
        AutoLoadFile[] files = new AutoLoadFile[paths.length];
        for (int i = 0; i < files.length; i++) {
          files[i] = new AutoLoadFile(paths[i] + "/" + AL_TODO_FILE);
          files[i].setLastUpdateTime(files[i].getFile().lastModified());
        }
        while (true) {
          try {
            Thread.sleep(config.getPollTime() * 1000);
          } catch (InterruptedException e) {
            break;
          }
          File f = null;
          AutoLoadFile af = null;
          for (int i = 0; i < files.length; i++) {
            af = files[i];
            f = files[i].getFile();
            if (!f.exists()) {
              continue;
            }
            if (f.lastModified() <= af.getLastUpdateTime()) {
              continue;
            }
            try {
              BufferedReader reader = new BufferedReader(new FileReader(f));
              String line = null;
              while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.indexOf('#') != -1) {
                  continue;
                }
                if ("".equals(line)) {
                  continue;
                }
                load(paths[i] + "/" + line);
              }
              reader.close();
              FileWriter fw = new FileWriter(f);
              fw.write("");
              fw.close();
              af.setLastUpdateTime(f.lastModified());
            } catch (IOException e) {
              break;
            }
          }
          resetSynonymsNet();
        }
      }
    });
    autoloadThread.setDaemon(true);
    autoloadThread.start();
  }

  public void stopAutoload() {
    if (autoloadThread != null) {
      autoloadThread.interrupt();
      autoloadThread = null;
    }
  }

  public boolean isSync() {
    return sync;
  }

  /**
     * loop up the dictionary, check the given key is in the dictionary or not
     * 
     * @param t
     * @param key
     * @return true for matched false for not match.
     */
  public abstract boolean match(int t, String key);

  /**
     * directly add a IWord item to the dictionary
     * 
     * @param   t
     * @param   word
    */
  public abstract IWord add(int t, IWord word);

  /**
     * add a new word to the dictionary with its statistics frequency
     * 
     * @param   t
     * @param   key
     * @param   fre
     * @param   type
     * @param   entity
     * @return  IWord
     */
  public abstract IWord add(int t, String key, int fre, int type, String[] entity);

  /**
     * add a new word to the dictionary
     * 
     * @param   t
     * @param   key
     * @param   fre
     * @param   type
     * @return  IWord
     */
  public abstract IWord add(int t, String key, int fre, int type);

  /**
     * add a new word to the dictionary
     * 
     * @param   t
     * @param   key
     * @param   type
     * @return  IWord
     */
  public abstract IWord add(int t, String key, int type);

  /**
     * add a new word to the dictionary
     * 
     * @param   t
     * @param   key
     * @param   type
     * @param   entity
     * @return  IWord
     */
  public abstract IWord add(int t, String key, int type, String[] entity);

  /**
     * return the IWord associate with the given key.
     * if there is not mapping for the key null will be return
     * 
     * @param t
     * @param key
     */
  public abstract IWord get(int t, String key);

  /**
     * remove the mapping associate with the given key
     * 
     * @param t
     * @param key
     */
  public abstract void remove(int t, String key);

  /**
     * return the size of the dictionary
     * 
     * @param    t
     * @return int
     */
  public abstract int size(int t);

  /**
     * get the key's type index located in ILexicon interface
     * 
     * @param   key
     * @return  int
     */
  public static int getIndex(String key) {
    if (key == null) {
      return -1;
    }
    key = key.toUpperCase();
    if (key.startsWith("CJK_WORD")) {
      return ILexicon.CJK_WORD;
    } else {
      if (key.startsWith("CJK_CHAR")) {
        return ILexicon.CJK_CHAR;
      } else {
        if (key.startsWith("CJK_UNIT")) {
          return ILexicon.CJK_UNIT;
        } else {
          if (key.startsWith("CN_LNAME_ADORN")) {
            return ILexicon.CN_LNAME_ADORN;
          } else {
            if (key.startsWith("CN_LNAME")) {
              return ILexicon.CN_LNAME;
            } else {
              if (key.startsWith("CN_SNAME")) {
                return ILexicon.CN_SNAME;
              } else {
                if (key.startsWith("CN_DNAME_1")) {
                  return ILexicon.CN_DNAME_1;
                } else {
                  if (key.startsWith("CN_DNAME_2")) {
                    return ILexicon.CN_DNAME_2;
                  } else {
                    if (key.startsWith("STOP_WORD")) {
                      return ILexicon.STOP_WORD;
                    } else {
                      if (key.startsWith("DOMAIN_SUFFIX")) {
                        return ILexicon.DOMAIN_SUFFIX;
                      } else {
                        if (key.startsWith("NUMBER_UNIT")) {
                          return ILexicon.NUMBER_UNIT;
                        } else {
                          if (key.startsWith("CJK_SYN")) {
                            return ILexicon.CJK_SYN;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return ILexicon.CJK_WORD;
  }

  public JcsegTaskConfig getConfig() {
    return config;
  }

  public void setConfig(JcsegTaskConfig config) {
    this.config = config;
  }

  /**
     * load all the words in the specified lexicon file into the dictionary
     * 
     * @param   config
     * @param   dic
     * @param   file
     * @param   buffer
     * @throws  IOException 
     * @throws  FileNotFoundException 
     * @throws  NumberFormatException 
     */
  public static void loadWords(JcsegTaskConfig config, ADictionary dic, File file, List<String[]> buffer) throws NumberFormatException, FileNotFoundException, IOException {
    loadWords(config, dic, new FileInputStream(file), buffer);
  }

  /**
     * load all the words from a specified lexicon file path
     * 
     * @param   config
     * @param   dic
     * @param   file
     * @param   buffer
     * @throws  IOException 
     * @throws  FileNotFoundException 
     * @throws  NumberFormatException 
    */
  public static void loadWords(JcsegTaskConfig config, ADictionary dic, String file, List<String[]> buffer) throws NumberFormatException, FileNotFoundException, IOException {
    loadWords(config, dic, new FileInputStream(file), buffer);
  }

  /**
     * load words from a InputStream
     * 
     * @param   config
     * @param   dic
     * @param   is
     * @param   buffer
     * @throws  IOException 
     * @throws  NumberFormatException 
    */
  public static void loadWords(JcsegTaskConfig config, ADictionary dic, InputStream is, List<String[]> buffer) throws NumberFormatException, IOException {
    boolean isFirstLine = true;
    int t = -1;
    String line = null, gEntity = null;
    BufferedReader buffReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    while ((line = buffReader.readLine()) != null) {
      line = line.trim();
      if ("".equals(line)) {
        continue;
      }
      if (line.charAt(0) == '#' && line.length() > 1) {
        continue;
      }
      if (isFirstLine == true) {
        t = ADictionary.getIndex(line);
        isFirstLine = false;
        if (t >= 0) {
          continue;
        }
      }
      if (line.charAt(0) == ':' && line.length() > 1) {
        String[] directive = line.substring(1).toLowerCase().split("\\s+");
        if (directive[0].equals("entity")) {
          if (directive.length > 1) {
            String args = directive[1].trim();
            gEntity = "null".equals(args) ? null : Entity.get(args);
          }
        }
        continue;
      }
      IWord tword = null;
      String[] wd = null;
      switch (t) {
        case ILexicon.CN_SNAME:
        case ILexicon.CN_LNAME:
        case ILexicon.CN_DNAME_1:
        case ILexicon.CN_DNAME_2:
        if (line.length() == 1) {
          dic.add(t, line, IWord.T_CJK_WORD);
        }
        break;
        case ILexicon.NUMBER_UNIT:
        wd = line.split("\\s*/\\s*");
        IWord uw = dic.add(t, wd[0], IWord.T_CJK_WORD);
        if (wd.length == 1) {
          dic.add(ILexicon.CJK_WORD, uw);
        } else {
          if (wd.length == 2) {
            String entity = "null".equals(wd[1]) ? null : Entity.get(wd[1]);
            uw.addEntity(entity);
            dic.add(ILexicon.CJK_CHAR, uw);
          }
        }
        break;
        case ILexicon.CJK_UNIT:
        wd = line.split("\\s*/\\s*");
        IWord w = dic.add(t, wd[0], IWord.T_CJK_WORD);
        if (wd.length == 1) {
          dic.add(ILexicon.CJK_WORD, w);
        } else {
          if (wd.length == 2) {
            String entity = "null".equals(wd[1]) ? null : Entity.get(wd[1]);
            w.addEntity(entity);
            dic.add(ILexicon.CJK_WORD, w).addEntity(entity);
            ;
          } else {
            if (wd.length > 3) {
              String entity = "null".equals(wd[3]) ? null : Entity.get(wd[3]);
              w.addEntity(entity);
              if (config.LOAD_PARAMETER && wd.length > 4 && !wd[4].equals("null")) {
                w.setParameter(wd[4]);
              }
              dic.add(ILexicon.CJK_WORD, w).addEntity(entity);
              ;
              tword = w;
            } else {
              dic.add(ILexicon.CJK_WORD, w);
              tword = w;
            }
          }
        }
        break;
        case ILexicon.CN_LNAME_ADORN:
        dic.add(t, line, IWord.T_CJK_WORD);
        break;
        case ILexicon.STOP_WORD:
        dic.add(ILexicon.STOP_WORD, line, IWord.T_CJK_WORD);
        break;
        case ILexicon.DOMAIN_SUFFIX:
        wd = line.split("\\s*/\\s*");
        dic.add(t, wd[0], IWord.T_BASIC_LATIN);
        break;
        case ILexicon.CJK_SYN:
        wd = line.split("\\s*,\\s*");
        if (wd.length > 1 && buffer != null) {
          buffer.add(wd);
        }
        wd = null;
        break;
        case ILexicon.CJK_WORD:
        case ILexicon.CJK_CHAR:
        wd = line.split("\\s*/\\s*", 5);
        if (wd.length < 4) {
          System.out.println("Word: \"" + wd[0] + "\" format error. -ignored");
          continue;
        }
        if (t == ILexicon.CJK_CHAR) {
          if (!StringUtil.isDigit(wd[4])) {
            System.out.println("Word: \"" + wd[0] + "\" format error(single word degree should be an integer). -ignored");
            continue;
          }
        }
        int latinIndex = StringUtil.latinIndexOf(wd[0]);
        tword = dic.get(t, wd[0]);
        if (tword == null) {
          if (t == ILexicon.CJK_CHAR) {
            tword = dic.add(ILexicon.CJK_WORD, wd[0], Integer.parseInt(wd[4]), IWord.T_CJK_WORD);
          } else {
            tword = dic.add(t, wd[0], IWord.T_CJK_WORD);
          }
        }
        if (config.LOAD_CJK_ENTITY && t != ILexicon.CJK_CHAR) {
          if (wd.length > 3) {
            if ("unset".equals(wd[3])) {
              tword.setEntity(null);
            } else {
              if ("extend".equals(wd[3])) {
                tword.addEntity(gEntity);
              } else {
                if ("null".equals(wd[3])) {
                  tword.addEntity(gEntity);
                } else {
                  tword.addEntity(Entity.get(wd[3]));
                }
              }
            }
          } else {
            if (gEntity != null) {
              tword.addEntity(gEntity);
            }
          }
        }
        if (config.LOAD_PARAMETER && t != ILexicon.CJK_CHAR) {
          if (wd.length > 4 && !wd[4].equals("null")) {
            tword.setParameter(wd[4]);
          }
        }
        if (latinIndex >= 0) {
          if (latinIndex > 0) {
            resetPrefixLength(config, dic, latinIndex);
            dic.add(ILexicon.MIX_ASSIST_WORD, wd[0].substring(0, latinIndex), IWord.T_CJK_WORD);
          }
          int cjkIndex = StringUtil.CJKIndexOf(wd[0], latinIndex + 1);
          if (cjkIndex > -1) {
            resetSuffixLength(config, dic, wd[0].length() - cjkIndex);
            dic.add(ILexicon.MIX_ASSIST_WORD, wd[0].substring(cjkIndex), IWord.T_CJK_WORD);
          }
          if (latinIndex > 0 && cjkIndex > -1) {
            dic.add(ILexicon.MIX_ASSIST_WORD, wd[0].substring(0, cjkIndex), IWord.T_BASIC_LATIN);
          }
        }
        break;
      }
      if (tword != null) {
        if (config.LOAD_CJK_PINYIN && !"null".equals(wd[2])) {
          tword.setPinyin(wd[2]);
        }
        String[] arr = tword.getPartSpeech();
        if (config.LOAD_CJK_POS && !"null".equals(wd[1])) {
          String[] pos = wd[1].split(",");
          for (int j = 0; j < pos.length; j++) {
            pos[j] = pos[j].trim();
            boolean add = true;
            if (arr != null) {
              for (int i = 0; i < arr.length; i++) {
                if (pos[j].equals(arr[i])) {
                  add = false;
                  break;
                }
              }
            }
            if (add) {
              tword.addPartSpeech(pos[j].trim());
            }
          }
        }
      }
    }
    buffReader.close();
    buffReader = null;
  }

  /**
     * check and reset the value of {@link ADictionary#mixPrefixLength}
     * 
     * @param   config
     * @param   dic
     * @param   mixLength
     * @return  boolean
    */
  public static boolean resetPrefixLength(JcsegTaskConfig config, ADictionary dic, int mixLength) {
    if (mixLength <= config.MAX_LENGTH && mixLength > dic.mixPrefixLength) {
      dic.mixPrefixLength = mixLength;
      return true;
    }
    return false;
  }

  /**
     * check and reset the value of the {@link ADictionary#mixSuffixLength}
     * 
     * @param   config
     * @param   dic
     * @param   mixLength
     * @return  boolean
    */
  public static boolean resetSuffixLength(JcsegTaskConfig config, ADictionary dic, int mixLength) {
    if (mixLength <= config.MAX_LENGTH && mixLength > dic.mixSuffixLength) {
      dic.mixSuffixLength = mixLength;
      return true;
    }
    return false;
  }
}