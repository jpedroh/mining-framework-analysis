package zemberek.ner;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zemberek.core.turkish.Turkish;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;

/**
 * Post processes named entities by removing suffixes from last word. TODO: requires some
 * refactoring.
 *
 * Originally written by Ayça Müge Sevinç. *
 */
public class PostProcessedNE {
  public String type;

  public String wordList[];

  private String lastWord;

  private int relKiCount;

  private String longestLemma;

  private String longFormatforLongestLemma;

  private String longestNLemma;

  private String longFormatforNLongestLemma;

  private String longestNStem;

  private String longFormatforLongestNStem;

  public NamedEntity orginalNE;

  public NamedEntity postProcessedNE;

  public PostProcessedNE(NamedEntity namedEntity) {
    longestLemma = "";
    longFormatforLongestLemma = "";
    longestNLemma = "";
    longFormatforNLongestLemma = "";
    longestNStem = "";
    longFormatforLongestNStem = "";
    wordList = namedEntity.content().split(" ");
    lastWord = wordList[wordList.length - 1];
    type = namedEntity.type;
    relKiCount = 0;
    orginalNE = namedEntity;
    postProcessedNE = namedEntity;
  }

  private String capitalize(String lemma) {
    if (lemma.length() == 0) {
      return lemma;
    }
    String first = lemma.substring(0, 1).toUpperCase(Turkish.LOCALE);
    return lemma.length() < 2 ? first : first + lemma.substring(1);
  }

  private NamedEntity apostropheRemoved() {
    String apostropheRemoved = "";
    if (lastWord.contains("\'")) {
      apostropheRemoved = lastWord.substring(0, lastWord.indexOf('\''));
    }
    if (lastWord.contains("\u2019")) {
      apostropheRemoved = lastWord.substring(0, lastWord.indexOf('\u2019'));
    }
    postProcessedNE = updateLastWord(orginalNE, apostropheRemoved);
    return postProcessedNE;
  }

  private NamedEntity updateLastWord(NamedEntity namedEntity, String lastWord) {
    boolean boolLastWord = false;
    List<NerToken> tokens = new ArrayList<>(wordList.length);
    for (int i = 0; i < wordList.length; i++) {
      String s = wordList[i];
      NePosition position;
      if (wordList.length == 1) {
        position = NePosition.UNIT;
        boolLastWord = true;
      } else {
        if (i == 0) {
          position = NePosition.BEGIN;
        } else {
          if (i == wordList.length - 1) {
            position = NePosition.LAST;
            boolLastWord = true;
          } else {
            position = NePosition.INSIDE;
          }
        }
      }
      if (boolLastWord) {
        tokens.add(new NerToken(i, lastWord, type, position));
      } else {
        tokens.add(new NerToken(i, s, type, position));
      }
    }
    return new NamedEntity(type, tokens);
  }

  private boolean derivedForms(String longFormat) {
    if (longFormat.contains("|")) {
      String lastIG = longFormat.substring(longFormat.lastIndexOf("|") + 1);
      String firstIG = longFormat.substring(1, longFormat.indexOf("|"));
      firstIG = firstIG.substring(firstIG.indexOf("] ") + 1);
      firstIG = firstIG.substring(firstIG.indexOf(":") + 1);
      boolean isLastIGNominalPossessive3rdPerson = lastIG.contains("Noun") && (lastIG.contains("P2sg") || lastIG.contains("P2pl") || lastIG.contains("P1pl") || lastIG.contains("P1sg"));
      boolean isFirstIGNominalPossessive3rdPerson = firstIG.contains("Noun") && (firstIG.contains("P2sg") || firstIG.contains("P2pl") || firstIG.contains("P1pl") || firstIG.contains("P1sg"));
      if (isFirstIGNominalPossessive3rdPerson && lastIG.contains("Verb")) {
        return true;
      }
      if (isLastIGNominalPossessive3rdPerson) {
        return true;
      }
      while (longFormat.contains("ki:Rel\u2192")) {
        longFormat = longFormat.substring(0, longFormat.lastIndexOf("|"));
        if (longFormat.contains("|")) {
          lastIG = longFormat.substring(longFormat.lastIndexOf("|") + 1);
        } else {
          lastIG = firstIG;
        }
        relKiCount++;
      }
    }
    return false;
  }

  private boolean unInflectedNominalForm(String longFormat) {
    boolean isNomimalPossesive3rdPerson = longFormat.contains("Noun") && (longFormat.contains("P2sg") || longFormat.contains("P2pl") || longFormat.contains("P1pl") || longFormat.contains("P1sg"));
    if (!longFormat.contains("Noun")) {
      return true;
    }
    if (isNomimalPossesive3rdPerson) {
      return true;
    }
    return false;
  }

  private NamedEntity MorphologicalAnalysisForNamedEntity(TurkishMorphology morphology) {
    {
      WordAnalysis results = morphology.analyze(lastWord);
      if (results.analysisCount() == 0) {
        return orginalNE;
      }
      for (SingleAnalysis result : results) {
        String longFormat = result.formatLong();
        if (derivedForms(longFormat)) {
          continue;
        } else {
          if (unInflectedNominalForm(longFormat)) {
            continue;
          }
        }
        List<String> ListOfLemmas = result.getLemmas();
        List<String> ListOfStems = result.getStems();
        if (ListOfLemmas == null || ListOfLemmas.isEmpty()) {
          return orginalNE;
        }
        String LastLemma = ListOfLemmas.get(ListOfLemmas.size() - 1);
        String LastStem = ListOfStems.get(ListOfStems.size() - 1);
        int x = 0;
        while (relKiCount > 0 && LastLemma.endsWith("ki") && LastStem.endsWith("ki")) {
          LastLemma = ListOfLemmas.get(ListOfLemmas.size() - 2 - x);
          LastStem = ListOfStems.get(ListOfStems.size() - 2 - x);
          relKiCount--;
          x++;
        }
        String CLastLemma = capitalize(LastLemma);
        String CLastStem = capitalize(LastStem);
        if (longestLemma.length() <= CLastLemma.length() && (longFormat.contains("Noun,Prop]") || longFormat.contains("Noun,Abbrv]"))) {
          longestLemma = CLastLemma;
          longFormatforLongestLemma = longFormat;
        }
        if (longestNLemma.length() <= CLastLemma.length() && longFormat.contains("Noun]")) {
          longestNLemma = CLastLemma;
          longFormatforNLongestLemma = longFormat;
        }
        if (longestNStem.length() <= CLastStem.length() && longFormat.contains("Noun]")) {
          longestNStem = CLastStem;
          longFormatforLongestNStem = longFormat;
        }
        if (longFormat.contains("Noun,Abbrv]") || longFormat.contains("Noun,Prop]")) {
          int ok = 1;
          for (int i = 0; i < 2 && i < longestLemma.length(); i++) {
            if (!Character.isUpperCase(lastWord.charAt(i))) {
              ok = 0;
              break;
            }
          }
          if (ok == 1) {
            longestLemma = longestLemma.toUpperCase();
          }
        }
      }
      if (longestLemma.length() == 0 && longestNLemma.length() > 0) {
        longestLemma = longestNLemma;
        longFormatforLongestLemma = longFormatforNLongestLemma;
      }
      if (longestLemma.length() == 0 && longestNLemma.length() == 0) {
        return orginalNE;
      }
      postProcessedNE = chooseBestLemmaBasedOnNamedEntityType();
      return postProcessedNE;
    }
  }

  private NamedEntity chooseBestLemmaBasedOnNamedEntityType() {
    if (wordList.length == 1) {
      postProcessedNE = updateLastWord(orginalNE, longestLemma);
      return postProcessedNE;
    } else {
      if (type.equals("PERSON")) {
        postProcessedNE = updateLastWord(orginalNE, longestLemma);
        return postProcessedNE;
      } else {
        if (type.equals("ORGANIZATION") || type.equals("LOCATION")) {
          String lemmaPos = longFormatforLongestLemma.substring(0, longFormatforLongestLemma.indexOf(']'));
          String lemma = lemmaPos.substring(1, lemmaPos.indexOf(':'));
          String pos = lemmaPos.substring(lemmaPos.indexOf(':') + 1, lemmaPos.length());
          if (!longFormatforNLongestLemma.equals("")) {
            String nLemmaPos = longFormatforNLongestLemma.substring(0, longFormatforNLongestLemma.indexOf(']'));
            String nCat = longFormatforNLongestLemma.substring(longFormatforNLongestLemma.indexOf(' '));
            nCat = nCat.substring(nCat.indexOf(':') + 1);
            if (nCat.contains("Noun+A3sg+") && nCat.contains(":P3sg")) {
              String suffix = nCat.substring(nCat.indexOf("Noun+A3sg+") + "Noun+A3sg+".length(), nCat.indexOf(":P3sg"));
              postProcessedNE = updateLastWord(orginalNE, longestNStem + suffix);
              return postProcessedNE;
            }
            Pattern pattern = Pattern.compile("Noun\\+l.r:A3pl\\+.*:P3.*");
            Matcher matcher = pattern.matcher(nCat);
            if (matcher.find()) {
              String suffix1 = nCat.substring(nCat.indexOf("Noun+") + "Noun+".length(), nCat.indexOf(":A3pl"));
              String suffix2 = nCat.substring(nCat.indexOf("r:A3pl") + "r:A3pl+".length(), nCat.indexOf(":P3"));
              postProcessedNE = updateLastWord(orginalNE, longestNStem + suffix1 + suffix2);
              return postProcessedNE;
            }
          } else {
            if (longestNLemma.equals("") && (pos.equals("Noun,Prop") || pos.equals("Noun,Abbrv")) && lemma.equals(longestLemma)) {
              postProcessedNE = updateLastWord(orginalNE, longestLemma);
              return postProcessedNE;
            }
          }
        }
      }
      return orginalNE;
    }
  }

  public NamedEntity postProcessNER(TurkishMorphology morphology) {
    if (lastWord.contains("\'") || lastWord.contains("\u2019")) {
      return apostropheRemoved();
    }
    return MorphologicalAnalysisForNamedEntity(morphology);
  }
}