package org.fenixedu.academic.util;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.dto.GenericPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.DomainObject;

public class UniqueAcronymCreator<T extends DomainObject> {
  private static final Logger logger = LoggerFactory.getLogger(UniqueAcronymCreator.class);

  private final Function<T, String> slotAccessor;

  private final Function<T, String> acronymAccessor;

  private final Set<T> objects;

  private static boolean toLowerCase = true;

  public UniqueAcronymCreator(Function<T, String> slotAccessor, Function<T, String> acronymAccessor, Set<T> objects) throws Exception {
    this.slotAccessor = slotAccessor;
    this.acronymAccessor = acronymAccessor;
    this.objects = new TreeSet<T>(Comparator.comparing(acronymAccessor));
    this.objects.addAll(objects);
  }

  private final Map<String, T> existingAcronyms = new HashMap<String, T>();

  private void initialize() throws Exception {
    existingAcronyms.clear();
    colisions.clear();
    for (final T object : objects) {
      String objectAcronym = acronymAccessor.apply(object);
      if (objectAcronym != null) {
        if (existingAcronyms.containsKey(objectAcronym)) {
          throw new Exception("given object list doesn\'t have unique acronyms!");
        }
        existingAcronyms.put(objectAcronym, object);
      }
    }
  }

  private T object;

  private static String[] splitsName;

  private final Set<T> colisions = new HashSet<T>();

  public GenericPair<String, Set<T>> create(T object) throws Exception {
    initialize();
    final String slotValue = slotAccessor.apply(object);
    final String slotValueWithNoAccents = noAccent(slotValue);
    if (logger.isDebugEnabled()) {
      logger.info("slotValue -> " + slotValue);
      logger.info("slotValueWithNoAccents -> " + slotValueWithNoAccents);
    }
    this.object = object;
    splitsName = slotValueWithNoAccents.split(" ");
    String acronym = constructBasicAcronym(new StringBuilder());
    if (canAccept(acronym)) {
      return new GenericPair<String, Set<T>>(acronym, colisions);
    } else {
      acronym = constructExtendedAcronym(acronym);
      if (canAccept(acronym)) {
        return new GenericPair<String, Set<T>>(acronym, colisions);
      } else {
        int index = 3;
        StringBuilder acronymAux = new StringBuilder(acronym.toString());
        while (!canAccept(acronym.toString()) && (index <= slotValueWithNoAccents.length())) {
          acronymAux = appendLastChar(index, acronymAux);
          acronym = acronymAux.toString();
          if (canAccept(acronym)) {
            return new GenericPair<String, Set<T>>(acronym, colisions);
          }
          index++;
        }
      }
    }
    throw new Exception("unable to create acronym!");
  }

  private boolean canAccept(String acronym) {
    if (!existingAcronyms.containsKey(acronym)) {
      if (logger.isDebugEnabled()) {
        logger.info("canAccept, true -> " + acronym);
      }
      return true;
    } else {
      if (existingAcronyms.get(acronym).equals(object)) {
        if (logger.isDebugEnabled()) {
          logger.info("canAccept, true -> " + acronym);
        }
        return true;
      } else {
        colisions.add(existingAcronyms.get(acronym));
        if (logger.isDebugEnabled()) {
          logger.info("canAccept, false -> " + acronym);
        }
        return false;
      }
    }
  }

  private static String constructBasicAcronym(StringBuilder acronym) {
    for (int i = 0; i < splitsName.length; i++) {
      if (splitsName[i].indexOf("(") == 0) {
        int closingBracketsSplit = i;
        for ( ; closingBracketsSplit < splitsName.length && !splitsName[closingBracketsSplit].contains(")"); closingBracketsSplit++) {
          ;
        }
        if (closingBracketsSplit == i) {
          String toAppend = splitsName[i].substring(0 + 1, splitsName[i].indexOf(")"));
          acronym.append("-").append(toAppend);
          if (logger.isDebugEnabled()) {
            logger.info("constructBasicAcronym, found a \'(...)\', appendding " + toAppend);
          }
        } else {
          String toAppend = splitsName[i].substring(1, splitsName[i].length());
          for (int iter = i + 1; iter < closingBracketsSplit; iter++) {
            if ((!isValidRejection(splitsName[iter]) && splitsName[iter].length() >= 3) || hasNumber(splitsName[iter])) {
              toAppend += splitsName[iter];
            }
          }
          toAppend += splitsName[closingBracketsSplit].substring(0, splitsName[closingBracketsSplit].length() - 1);
          i = closingBracketsSplit + 1;
          acronym.append("-").append(toAppend);
          if (logger.isDebugEnabled()) {
            logger.info("constructBasicAcronym, found a \'(... ...)\', appendding " + toAppend);
          }
        }
      } else {
        if (splitsName[i].indexOf("-") == 0) {
          if ((i + 1) <= splitsName.length - 1) {
            if (!isValidRejection(splitsName[i + 1])) {
              String toAppend = (splitsName[i + 1].length() < 4) ? splitsName[i + 1].toUpperCase() : String.valueOf(splitsName[i + 1].charAt(0)).toUpperCase();
              i = i + 1;
              if (toAppend.length() == 1) {
                acronym.append(toAppend);
                if (logger.isDebugEnabled()) {
                  logger.info("constructBasicAcronym, found a \'- ...\', appendding letter " + toAppend);
                }
              } else {
                acronym.append("-").append(toAppend);
                if (logger.isDebugEnabled()) {
                  logger.info("constructBasicAcronym, found a \'- ...\', appendding " + toAppend);
                }
              }
            } else {
              acronym.append("-");
              if (logger.isDebugEnabled()) {
                logger.info("constructBasicAcronym, found a \'- ...\', only appendding \'-\'");
              }
            }
          } else {
            if (!splitsName[i].equals("-")) {
              String toAppend = splitsName[i].substring(1, splitsName[i].length() - 1);
              toAppend = (toAppend.length() < 4) ? toAppend.toUpperCase() : String.valueOf(toAppend.charAt(0)).toUpperCase();
              acronym.append("-").append(toAppend);
              if (logger.isDebugEnabled()) {
                logger.info("constructBasicAcronym, found a \'-...\' at the end, appendding " + toAppend);
              }
            }
          }
        } else {
          if (isValidNumeration(splitsName[i]) || hasNumber(splitsName[i])) {
            acronym.append("-").append(splitsName[i].toUpperCase());
            if (logger.isDebugEnabled()) {
              logger.info("constructBasicAcronym, found a numeration, appendding " + splitsName[i].toUpperCase());
            }
          } else {
            if (!isValidRejection(splitsName[i]) && splitsName[i].length() >= 3) {
              if (splitsName[i].contains("-")) {
                int index = splitsName[i].indexOf("-");
                acronym.append(splitsName[i].charAt(0)).append(splitsName[i].charAt(index + 1));
                if (logger.isDebugEnabled()) {
                  logger.info("constructBasicAcronym, found a \'-\', appendding " + splitsName[i].charAt(0) + splitsName[i].charAt(index + 1));
                }
              } else {
                acronym.append(splitsName[i].charAt(0));
                if (logger.isDebugEnabled()) {
                  logger.info("constructBasicAcronym, appendding " + splitsName[i].charAt(0));
                }
              }
            }
          }
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.info("constructBasicAcronym, returning " + acronym.toString());
    }
    return acronym.toString();
  }

  private static boolean isValidRejection(String string) {
    if (rejectionSet.contains(StringUtils.lowerCase(string))) {
      if (logger.isDebugEnabled()) {
        logger.info("isValidRejection, true -> " + string);
      }
      return true;
    }
    if (logger.isDebugEnabled()) {
      logger.info("isValidRejection, false -> " + string);
    }
    return false;
  }

  private static boolean isValidNumeration(String string) {
    if (numerationSet.contains(StringUtils.upperCase(string))) {
      if (logger.isDebugEnabled()) {
        logger.info("isValidNumeration, true -> " + string);
      }
      return true;
    }
    if (logger.isDebugEnabled()) {
      logger.info("isValidNumeration, false -> " + string);
    }
    return false;
  }

  private static boolean hasNumber(String string) {
    for (int i = 0; i < string.length(); ++i) {
      char c = string.charAt(i);
      if (numberSet.contains(String.valueOf(c))) {
        if (logger.isDebugEnabled()) {
          logger.info("hasNumber, true -> " + string);
        }
        return true;
      }
    }
    if (logger.isDebugEnabled()) {
      logger.info("hasNumber, false -> " + string);
    }
    return false;
  }

  private static boolean isValidAcception(String string) {
    if (acceptSet.contains(StringUtils.upperCase(string))) {
      if (logger.isDebugEnabled()) {
        logger.info("isValidAcception, true -> " + string);
      }
      return true;
    }
    if (logger.isDebugEnabled()) {
      logger.info("isValidAcception, false -> " + string);
    }
    return false;
  }

  private static String constructExtendedAcronym(String basicAcronym) {
    StringBuilder extendedAcronym = new StringBuilder();
    int length = splitsName.length;
    if (logger.isDebugEnabled()) {
      logger.info("constructExtendedAcronym, sliptsName length " + length);
    }
    if (length == 1) {
      extendedAcronym.append(splitsName[0].charAt(0));
      String toAppend = splitsName[0].substring(1, 3);
      if (logger.isDebugEnabled()) {
        logger.info("constructExtendedAcronym, length 1, appending " + toAppend);
      }
      extendedAcronym.append(((toLowerCase) ? toAppend.toLowerCase() : toAppend.toUpperCase()));
      return extendedAcronym.toString();
    }
    appendLast(extendedAcronym.append(basicAcronym));
    if (logger.isDebugEnabled()) {
      logger.info("constructExtendedAcronym, returning " + extendedAcronym.toString());
    }
    return extendedAcronym.toString();
  }

  private static void appendLast(StringBuilder acronym) {
    if (logger.isDebugEnabled()) {
      logger.info("appendLast, called with " + acronym);
    }
    for (int i = splitsName.length - 1; i > -1; i--) {
      if (!isValidAcception(splitsName[i]) && splitsName[i].length() >= 3 && !isValidNumeration(splitsName[i])) {
        String toAppend = splitsName[i].substring(1, 3);
        toAppend = (toLowerCase) ? toAppend.toLowerCase() : toAppend.toUpperCase();
        if (acronym.toString().contains("-")) {
          int hiffen = acronym.toString().indexOf("-");
          if ((hiffen + 1 < acronym.toString().length()) && (isValidNumeration(String.valueOf(acronym.charAt(hiffen + 1))) || hasNumber(String.valueOf(acronym.charAt(hiffen + 1))))) {
            acronym.insert(hiffen, toAppend);
            if (logger.isDebugEnabled()) {
              logger.info("appendLast, found a \'-\', appending before hiffen " + toAppend);
            }
          } else {
            acronym.append(toAppend);
            if (logger.isDebugEnabled()) {
              logger.info("appendLast, found a \'-\', appending in end " + toAppend);
            }
          }
        } else {
          if (logger.isDebugEnabled()) {
            logger.info("appendLast, appending " + toAppend);
          }
          acronym.append(toAppend);
        }
        break;
      }
    }
  }

  private static StringBuilder appendLastChar(int index, StringBuilder acronym) {
    if (logger.isDebugEnabled()) {
      logger.info("appendLastChar, called with index " + index + " and " + acronym);
    }
    for (int i = splitsName.length - 1; i > -1; i--) {
      if (!(isValidAcception(splitsName[i])) && splitsName[i].length() > index) {
        String toAppend = (splitsName[i].substring(index, index + 1));
        toAppend = (toLowerCase) ? toAppend.toLowerCase() : toAppend.toUpperCase();
        if (acronym.toString().contains("-")) {
          int hiffen = acronym.toString().indexOf("-");
          if (isValidNumeration(String.valueOf(acronym.charAt(hiffen + 1))) || hasNumber(String.valueOf(acronym.charAt(hiffen + 1)))) {
            acronym.insert(hiffen, toAppend);
            if (logger.isDebugEnabled()) {
              logger.info("appendLastChar, found a \'-\', appending before hiffen " + toAppend);
            }
          } else {
            acronym.append(toAppend);
            if (logger.isDebugEnabled()) {
              logger.info("appendLastChar, found a \'-\', appending in end " + toAppend);
            }
          }
        } else {
          if (logger.isDebugEnabled()) {
            logger.info("appendLastChar, appending " + toAppend);
          }
          acronym.append(toAppend);
        }
        break;
      }
    }
    return acronym;
  }

  private static Set<String> rejectionSet = new HashSet<String>();

  private static Set<String> acceptSet = new HashSet<String>();

  private static Set<String> numberSet = new HashSet<String>();

  private static Set<String> numerationSet = new HashSet<String>();

  static {
    rejectionSet.add("\u00e0s");
    rejectionSet.add("\u00e0");
    rejectionSet.add("com");
    rejectionSet.add("sobre");
    rejectionSet.add("de");
    rejectionSet.add("e");
    rejectionSet.add("para");
    rejectionSet.add("em");
    rejectionSet.add("do");
    rejectionSet.add("dos");
    rejectionSet.add("da");
    rejectionSet.add("das");
    rejectionSet.add("na");
    rejectionSet.add("no");
    rejectionSet.add("nas");
    rejectionSet.add("nos");
    rejectionSet.add("por");
    rejectionSet.add("aos");
    rejectionSet.add("ao");
    rejectionSet.add("a)");
    rejectionSet.add("b)");
    rejectionSet.add("c)");
    rejectionSet.add("d)");
    rejectionSet.add("e)");
    rejectionSet.add("(m)");
    rejectionSet.add("(m/l)");
    rejectionSet.add("(d/m)");
    rejectionSet.add("(m/d)");
    rejectionSet.add("(a)");
    rejectionSet.add("(p)");
    rejectionSet.add("(md)");
    rejectionSet.add("(sie)");
    rejectionSet.add("(sm)");
    rejectionSet.add("(taguspark)");
    rejectionSet.add("");
    acceptSet.add("-");
    acceptSet.add("B");
    acceptSet.add("C");
    acceptSet.add("A)");
    acceptSet.add("B)");
    acceptSet.add("C)");
    acceptSet.add("D)");
    acceptSet.add("E)");
    acceptSet.add("(M)");
    acceptSet.add("(M/L)");
    acceptSet.add("(D/M)");
    acceptSet.add("(M/D)");
    acceptSet.add("(A)");
    acceptSet.add("(P)");
    acceptSet.add("(MD)");
    acceptSet.add("(SM)");
    acceptSet.add("(SIE)");
    acceptSet.add("(TAGUSPARK)");
    numberSet.add("1");
    numberSet.add("2");
    numberSet.add("3");
    numberSet.add("4");
    numberSet.add("5");
    numberSet.add("6");
    numberSet.add("7");
    numberSet.add("8");
    numberSet.add("9");
    numberSet.add("10");
    numerationSet.add("I");
    numerationSet.add("II");
    numerationSet.add("III");
    numerationSet.add("IV");
    numerationSet.add("V");
    numerationSet.add("VI");
    numerationSet.add("VII");
    numerationSet.add("VIII");
    numerationSet.add("IX");
    numerationSet.add("X");
    numerationSet.add("XX");
    numerationSet.add("XXI");
  }

  private static StringBuilder sbna = null;

  private static String noAccent(String ptxt) {
    if (sbna == null) {
      sbna = new StringBuilder();
    } else {
      sbna.setLength(0);
    }
    if (ptxt == null) {
      return null;
    }
    for (int i = 0; i < ptxt.length(); ++i) {
      char c = ptxt.charAt(i);
      switch (c) {
        case '\u00c3':
        sbna.append('A');
        break;
        case '\u00c0':
        sbna.append('A');
        break;
        case '\u00c1':
        sbna.append('A');
        break;
        case '\u00c2':
        sbna.append('A');
        break;
        case '\u00c4':
        sbna.append('A');
        break;
        case '\u00c5':
        sbna.append('A');
        break;
        case '\u00e0':
        sbna.append('a');
        break;
        case '\u00e1':
        sbna.append('a');
        break;
        case '\u00e2':
        sbna.append('a');
        break;
        case '\u00e3':
        sbna.append('a');
        break;
        case '\u00e4':
        sbna.append('a');
        break;
        case '\u00e5':
        sbna.append('a');
        break;
        case '\u00c7':
        sbna.append('C');
        break;
        case '\u00e7':
        sbna.append('c');
        break;
        case '\u00c8':
        sbna.append('E');
        break;
        case '\u00c9':
        sbna.append('E');
        break;
        case '\u00ca':
        sbna.append('E');
        break;
        case '\u00cb':
        sbna.append('E');
        break;
        case '\u00e8':
        sbna.append('e');
        break;
        case '\u00e9':
        sbna.append('e');
        break;
        case '\u00ea':
        sbna.append('e');
        break;
        case '\u00eb':
        sbna.append('e');
        break;
        case '\u00cc':
        sbna.append('I');
        break;
        case '\u00cd':
        sbna.append('I');
        break;
        case '\u00ce':
        sbna.append('I');
        break;
        case '\u00cf':
        sbna.append('I');
        break;
        case '\u00ec':
        sbna.append('i');
        break;
        case '\u00ed':
        sbna.append('i');
        break;
        case '\u00ee':
        sbna.append('i');
        break;
        case '\u00ef':
        sbna.append('i');
        break;
        case '\u00d1':
        sbna.append('N');
        break;
        case '\u00f1':
        sbna.append('n');
        break;
        case '\u00d2':
        sbna.append('O');
        break;
        case '\u00d3':
        sbna.append('O');
        break;
        case '\u00d4':
        sbna.append('O');
        break;
        case '\u00d5':
        sbna.append('O');
        break;
        case '\u00d6':
        sbna.append('O');
        break;
        case '\u00f2':
        sbna.append('o');
        break;
        case '\u00f3':
        sbna.append('o');
        break;
        case '\u00f4':
        sbna.append('o');
        break;
        case '\u00f5':
        sbna.append('o');
        break;
        case '\u00f6':
        sbna.append('o');
        break;
        case '\u00d9':
        sbna.append('U');
        break;
        case '\u00da':
        sbna.append('U');
        break;
        case '\u00db':
        sbna.append('U');
        break;
        case '\u00dc':
        sbna.append('U');
        break;
        case '\u00f9':
        sbna.append('u');
        break;
        case '\u00fa':
        sbna.append('u');
        break;
        case '\u00fb':
        sbna.append('u');
        break;
        case '\u00fc':
        sbna.append('u');
        break;
        case '\u00dd':
        sbna.append('Y');
        break;
        case '\u00fd':
        sbna.append('Y');
        break;
        case '\u00ff':
        sbna.append('y');
        break;
        case ',':
        sbna.append(' ');
        break;
        case ':':
        sbna.append(" - ");
        break;
        case '-':
        sbna.append(" -");
        break;
        case '\u00ba':
        sbna.append(' ');
        break;
        case '\u00aa':
        sbna.append(' ');
        break;
        case '/':
        sbna.append(' ');
        break;
        case '.':
        sbna.append(' ');
        break;
        case '(':
        sbna.append(" (");
        break;
        default:
        sbna.append(c);
      }
    }
    return sbna.toString();
  }
}