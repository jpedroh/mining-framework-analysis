package lv.semti.morphology.Testi;
import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import lv.semti.morphology.analyzer.*;
import lv.semti.morphology.attributes.*;
import lv.semti.morphology.lexicon.*;

public class MorphologyTest {
  private static Analyzer locītājs;

  private void assertNounInflection(List<Wordform> forms, String number, String nounCase, String gender, String validForm) {
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_Case, nounCase);
    testset.addAttribute(AttributeNames.i_Number, number);
    if (!gender.isEmpty()) {
      testset.addAttribute(AttributeNames.i_Gender, gender);
    }
    assertInflection(forms, testset, validForm);
  }

  private void assertInflection(List<Wordform> forms, AttributeValues testset, String validForm) {
    boolean found = false;
    for (Wordform wf : forms) {
      if (wf.isMatchingWeak(testset)) {
        if (!validForm.equalsIgnoreCase(wf.getToken())) {
          System.err.printf("Found a different form");
          wf.describe(new PrintWriter(System.err));
        }
        assertEquals(validForm, wf.getToken());
        found = true;
        break;
      }
    }
    if (!found) {
      System.err.printf("assertInflection failed: looking for \'%s\'\n", validForm);
      testset.describe(new PrintWriter(System.err));
      System.err.println("In:");
      for (Wordform wf : forms) {
        wf.describe(new PrintWriter(System.err));
        System.err.println("\t---");
      }
    }
    assertTrue(found);
  }

  private void assertLemma(String word, String expectedLemma) {
    Word analysis = locītājs.analyze(word);
    assertTrue(analysis.isRecognized());
    Wordform forma = analysis.getBestWordform();
    assertEquals(expectedLemma, forma.getValue(AttributeNames.i_Lemma));
  }

  @SuppressWarnings(value = { "unused" }) private void describe(List<Wordform> formas) {
    PrintWriter izeja;
    try {
      izeja = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"));
      for (Wordform forma : formas) {
        forma.describe(izeja);
        izeja.println();
      }
      izeja.flush();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  @BeforeClass public static void setUpBeforeClass() {
    try {
      locītājs = new Analyzer(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Before public void defaultsettings() {
    locītājs.defaultSettings();
    locītājs.setCacheSize(0);
    locītājs.clearCache();
  }

  @Test public void cirvis() {
    Word cirvis = locītājs.analyze("cirvis");
    assertTrue(cirvis.isRecognized());
    assertEquals("ncmsn2", cirvis.wordforms.get(0).getTag());
  }

  @Test public void nadziņi() {
    locītājs.enableDiminutive = true;
    Word nadziņi = locītājs.analyze("nadzi\u0146i");
    assertTrue(nadziņi.isRecognized());
    Wordform forma = nadziņi.getBestWordform();
    assertEquals("nadzi\u0146\u0161", forma.getValue(AttributeNames.i_Lemma));
  }

  @Test public void meitenīte() {
    Word meitenīte = locītājs.analyze("meiten\u012bte");
    assertTrue(meitenīte.isRecognized());
    assertEquals(1, meitenīte.wordformsCount());
  }

  @Test public void otrajās() {
    Word otrajās = locītājs.analyze("otraj\u0101s");
    assertTrue(otrajās.isRecognized());
    assertEquals("otr\u0101", otrajās.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void simtiem() {
    Word simtiem = locītājs.analyze("simtiem");
    assertTrue(simtiem.isRecognized());
    assertEquals("simts", simtiem.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void ēdīs() {
    Word ēdīs = locītājs.analyze("\u0113d\u012bs");
    assertTrue(ēdīs.isRecognized());
  }

  @Test public void ceļu() {
    Word ceļu = locītājs.analyze("ce\u013cu");
    assertTrue(ceļu.isRecognized());
    AttributeValues verbs = new AttributeValues();
    verbs.addAttribute("V\u0101rd\u0161\u0137ira", "Darb\u012bbas v\u0101rds");
    ceļu.filterByAttributes(verbs);
    assertTrue(ceļu.isRecognized());
  }

  @Test public void sniga() {
    Word sniga = locītājs.analyze("sniga");
    assertTrue(sniga.isRecognized());
    assertEquals(null, sniga.wordforms.get(0).getValue("Verbu grupa no vec\u0101 projekta"));
  }

  @Test public void bieži() {
    Word bieži = locītājs.analyze("bie\u017ei");
    assertTrue(bieži.isRecognized());
    boolean irPareizā = false;
    for (Wordform vārdforma : bieži.wordforms) {
      if (vārdforma.getValue(AttributeNames.i_Lemma).equals("bie\u017ei")) {
        irPareizā = true;
      }
    }
    assertEquals(true, irPareizā);
  }

  @Test public void zaļāk() {
    Word zaļāk = locītājs.analyze("za\u013c\u0101k");
    assertTrue(zaļāk.isRecognized());
    assertEquals("P\u0101r\u0101k\u0101", zaļāk.wordforms.get(0).getValue("Pak\u0101pe"));
    Word viszaļāk = locītājs.analyze("visza\u013c\u0101k");
    assertTrue(viszaļāk.isRecognized());
    assertEquals("Visp\u0101r\u0101k\u0101", viszaļāk.wordforms.get(0).getValue("Pak\u0101pe"));
  }

  @Test public void ātrākVisātrāk() {
    Word ātrāks = locītājs.analyze("\u0101tr\u0101ks");
    assertTrue(ātrāks.isRecognized());
    assertEquals("P\u0101r\u0101k\u0101", ātrāks.wordforms.get(0).getValue("Pak\u0101pe"));
    Word visātrākais = locītājs.analyze("vis\u0101tr\u0101kais");
    assertTrue(visātrākais.isRecognized());
    assertEquals("Visp\u0101r\u0101k\u0101", visātrākais.wordforms.get(0).getValue("Pak\u0101pe"));
  }

  @Test public void pieveicis() {
    locītājs.enablePrefixes = true;
    Word pieveicis = locītājs.analyze("pieveicis");
    assertTrue(pieveicis.isRecognized());
    assertEquals("vmnpdmsnasnpn", pieveicis.wordforms.get(0).getTag());
  }

  @Test public void paņēmis() {
    Word paņēmis = locītājs.analyze("pa\u0146\u0113mis");
    assertTrue(paņēmis.isRecognized());
    assertEquals("vmnpdmsnasnpn", paņēmis.wordforms.get(0).getTag());
  }

  @Test public void durkls() {
    Word durkls = locītājs.analyze("durkls");
    if (durkls.isRecognized()) {
      assertEquals("1", durkls.wordforms.get(0).getValue(AttributeNames.i_ParadigmID));
    }
  }

  @Test public void lasis() {
    List<Wordform> lasis = locītājs.generateInflections("lasis");
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    assertInflection(lasis, testset, "la\u0161a");
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
    assertInflection(lasis, testset, "la\u0161iem");
    Word w = locītājs.analyze("lasiem");
    assertFalse(w.isRecognized());
  }

  @Test public void skansts() {
    List<Wordform> skansts = locītājs.generateInflections("skansts");
    assertNotEquals(skansts.size(), 0);
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
    assertInflection(skansts, testset, "skan\u0161u");
  }

  @Test public void debesis() {
    List<Wordform> debesis = locītājs.generateInflectionsFromParadigm("debesis", 3);
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    testset.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
    assertInflection(debesis, testset, "debe\u0161a");
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
    assertInflection(debesis, testset, "debe\u0161iem");
    List<Wordform> debess = locītājs.generateInflectionsFromParadigm("debess", 35);
    testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    testset.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
    assertInflection(debess, testset, "debess");
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
    assertInflection(debess, testset, "debesu");
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
    assertInflection(debess, testset, "debes\u012bm");
  }

  @Test public void balss() {
    List<Wordform> balss = locītājs.generateInflections("balss");
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
    assertInflection(balss, testset, "balsu");
    Word w = locītājs.analyze("bal\u0161u");
    assertFalse(w.isRecognized());
  }

  @Ignore(value = "Ir zin\u0101ma probl\u0113ma, ka T\u0113zaurs.lv JSON eksport\u0101 ir vair\u0101kas morfo-leks\u0113mas kas n\u0101k no vienas t\u0113zaurs-leks\u0113mas un t\u0101d\u0113\u013c ir ar vien\u0101du leks\u0113mas ID") @Test public void numuri() {
    HashMap<Integer, Paradigm> vārdgrupuNr = new HashMap<Integer, Paradigm>();
    HashMap<Integer, Lexeme> leksēmuNr = new HashMap<Integer, Lexeme>();
    HashMap<Integer, Ending> galotņuNr = new HashMap<Integer, Ending>();
    for (Paradigm vārdgrupa : locītājs.paradigms) {
      if (vārdgrupuNr.get(vārdgrupa.getID()) != null) {
        fail("Atk\u0101rtojas v\u0101rdgrupas nr " + vārdgrupa.getID());
      }
      vārdgrupuNr.put(vārdgrupa.getID(), vārdgrupa);
      for (Lexeme leksēma : vārdgrupa.lexemes) {
        if (leksēmuNr.get(leksēma.getID()) != null) {
          leksēma.describe(new PrintWriter(System.err));
          leksēmuNr.get(leksēma.getID()).describe(new PrintWriter(System.err));
          fail(String.format("Atk\u0101rtojas leks\u0113mas nr %d : \'%s\' un \'%s\'", leksēma.getID(), leksēma.getStem(0), leksēmuNr.get(leksēma.getID()).getStem(0)));
        }
        leksēmuNr.put(leksēma.getID(), leksēma);
      }
      for (Ending ending : vārdgrupa.endings) {
        if (galotņuNr.get(ending.getID()) != null) {
          fail("Atk\u0101rtojas galotnes nr " + ending.getID());
        }
        galotņuNr.put(ending.getID(), ending);
      }
    }
  }

  @Test public void crap() {
    Word crap = locītājs.analyze("crap");
    assertFalse(crap.isRecognized());
    locītājs.enableGuessing = true;
    locītājs.enableAllGuesses = true;
    locītājs.guessInflexibleNouns = true;
    crap = locītājs.analyze("crap");
    assertTrue(crap.isRecognized());
    assertEquals(AttributeNames.v_Ending, crap.wordforms.get(0).getValue(AttributeNames.i_Guess));
  }

  @Test public void ātrums() {
    long sākums = System.currentTimeMillis();
    locītājs.enableVocative = true;
    locītājs.enableDiminutive = true;
    locītājs.enablePrefixes = false;
    locītājs.enableAllGuesses = true;
    locītājs.meklētsalikteņus = false;
    int skaits = 0;
    for (int i = 1; i < 100; i++) {
      locītājs.analyze("cirvis");
      locītājs.analyze("roku");
      locītājs.analyze("nepadom\u0101jot");
      locītājs.analyze("Kirils");
      locītājs.analyze("parakt");
      locītājs.analyze("bundzi\u0146as");
      locītājs.analyze("pokemoniz\u0113t");
      locītājs.analyze("xyzzyt");
      locītājs.analyze("\u017evirblis");
      locītājs.analyze("Murgain\u0161teineniem");
      skaits += 10;
    }
    long beigas = System.currentTimeMillis();
    long starpība = beigas - sākums;
    System.out.printf("%d piepras\u012bjumi sekund\u0113 (%d ms)\n", skaits * 1000 / starpība, starpība);
  }

  @Test public void dubultLeksēmas() throws UnsupportedEncodingException {
    PrintWriter izeja = null;
    izeja = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"));
    for (Paradigm vārdgrupa : locītājs.paradigms) {
      for (ArrayList<Lexeme> leksēmas : vārdgrupa.getLexemesByStem().get(0).values()) {
        for (int i = 0; i < leksēmas.size(); i++) {
          for (int j = i + 1; j < leksēmas.size(); j++) {
            Lexeme l1 = leksēmas.get(i);
            Lexeme l2 = leksēmas.get(j);
            boolean sakrīt = true;
            for (int s = 0; s < vārdgrupa.getStems(); s++) {
              if (!l1.getStem(s).equals(l2.getStem(s))) {
                sakrīt = false;
              }
            }
            for (Entry<String, String> pāris : l1.entrySet()) {
              if (pāris.getKey().equals("Leks\u0113mas nr")) {
                continue;
              }
              String otraVērtība = l1.getValue(pāris.getKey());
              if (!pāris.getValue().equals(otraVērtība)) {
                sakrīt = false;
              }
            }
            for (Entry<String, String> pāris : l2.entrySet()) {
              if (pāris.getKey().equals("Leks\u0113mas nr")) {
                continue;
              }
              String otraVērtība = l1.getValue(pāris.getKey());
              if (!pāris.getValue().equals(otraVērtība)) {
                sakrīt = false;
              }
            }
            if (sakrīt) {
              System.err.println("Atk\u0101rtojas leks\u0113mas:");
              l1.describe(new PrintWriter(System.err));
              l2.describe(new PrintWriter(System.err));
            }
          }
        }
      }
    }
    izeja.flush();
  }

  @Test public void ticket9() {
    Word turiene = locītājs.analyze("turiene");
    assertTrue(turiene.isRecognized());
    assertEquals("turiene", turiene.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    Word turienēm = locītājs.analyze("turien\u0113m");
    assertFalse(turienēm.isRecognized());
    Word bikses = locītājs.analyze("bikses");
    assertTrue(bikses.isRecognized());
    assertEquals("bikses", bikses.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    Word bikse = locītājs.analyze("bikse");
    assertFalse(bikse.isRecognized());
    Word augstpapēžu = locītājs.analyze("augstpap\u0113\u017eu");
    assertTrue(augstpapēžu.isRecognized());
    assertEquals("augstpap\u0113\u017eu", augstpapēžu.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    Word augstpapēdis = locītājs.analyze("augstpap\u0113dis");
    assertFalse(augstpapēdis.isRecognized());
  }

  @Test public void ticket29() {
    Word neviens = locītājs.analyze("neviens");
    assertTrue(neviens.isRecognized());
  }

  @Test public void ticket37() {
    locītājs.enablePrefixes = true;
    Word panest = locītājs.analyze("panest");
    assertTrue(panest.isRecognized());
    assertEquals("vmnn0t1000n", panest.wordforms.get(0).getTag());
  }

  @Test public void ticket16() {
    Word trūkst = locītājs.analyze("tr\u016bkst");
    assertTrue(trūkst.isRecognized());
    for (Wordform wordform : trūkst.wordforms) {
      assertFalse(wordform.isMatchingStrong(AttributeNames.i_Person, "2"));
    }
  }

  @Test public void ticket65() {
    Word dodas = locītājs.analyze("dodas");
    assertTrue(dodas.isRecognized());
    assertEquals(AttributeNames.v_Active, dodas.wordforms.get(0).getValue(AttributeNames.i_Voice));
    assertEquals("vmyip_130an", dodas.wordforms.get(0).getTag());
  }

  @Test public void ticket76() {
    Word simt = locītājs.analyze("simt");
    assertTrue(simt.isRecognized());
    assertEquals(AttributeNames.v_Hundreds, simt.wordforms.get(0).getValue(AttributeNames.i_Order));
    assertEquals("mcs_p0", simt.wordforms.get(0).getTag());
  }

  @Test public void ticket84() {
    Word griezis = locītājs.analyze("griezis");
    assertTrue(griezis.isRecognized());
    boolean atrasts = false;
    for (Wordform wordform : griezis.wordforms) {
      if (wordform.isMatchingStrong(AttributeNames.i_Izteiksme, AttributeNames.v_Participle)) {
        atrasts = true;
      }
    }
    assertTrue(atrasts);
    Word griezies = locītājs.analyze("griezies");
    assertTrue(griezies.isRecognized());
    atrasts = false;
    for (Wordform wordform : griezis.wordforms) {
      if (wordform.isMatchingStrong(AttributeNames.i_Izteiksme, AttributeNames.v_Participle)) {
        atrasts = true;
      }
    }
    assertTrue(atrasts);
  }

  @Test public void tuStum() {
    Word stum = locītājs.analyze("stum");
    assertTrue(stum.isRecognized());
    assertEquals("2", stum.wordforms.get(0).getValue(AttributeNames.i_Person));
    assertEquals(AttributeNames.v_Tagadne, stum.wordforms.get(0).getValue(AttributeNames.i_Laiks));
  }

  @Test public void man() {
    Word man = locītājs.analyze("man");
    assertTrue(man.isRecognized());
    assertEquals("es", man.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void vairāki() {
    Word vairāki = locītājs.analyze("vair\u0101ki");
    assertTrue(vairāki.isRecognized());
    assertEquals(AttributeNames.v_Pronoun, vairāki.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
  }

  @Test public void daudzus() {
    Word daudzus = locītājs.analyze("daudzus");
    assertTrue(daudzus.isRecognized());
    assertEquals(AttributeNames.v_Pronoun, daudzus.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
  }

  @Test public void jāpasaka() {
    Word jāpasaka = locītājs.analyze("j\u0101pasaka");
    assertTrue(jāpasaka.isRecognized());
  }

  @Test public void vajag() {
    Word vajag = locītājs.analyze("vajag");
    assertTrue(vajag.isRecognized());
  }

  @Test public void Vilis() {
    Word viņi = locītājs.analyze("vi\u0146i");
    assertTrue(viņi.isRecognized());
    assertEquals(1, viņi.wordformsCount());
  }

  @Test public void atgādinām() {
    Word atgādinām = locītājs.analyze("atg\u0101din\u0101m");
    assertTrue(atgādinām.isRecognized());
    assertEquals(2, atgādinām.wordformsCount());
    Word atgādināt = locītājs.analyze("atg\u0101din\u0101t");
    assertTrue(atgādināt.isRecognized());
    assertEquals(2, atgādināt.wordformsCount());
    Word atgādinat = locītājs.analyze("atg\u0101dinat");
    assertFalse(atgādinat.isRecognized());
    Word atgādinam = locītājs.analyze("atg\u0101dinam");
    assertFalse(atgādinam.isRecognized());
  }

  @Test public void bijušais() {
    Word bijušais = locītājs.analyze("biju\u0161ais");
    assertTrue(bijušais.isRecognized());
    Word bijusī = locītājs.analyze("bijus\u012b");
    assertTrue(bijusī.isRecognized());
    Word bijušajiem = locītājs.analyze("biju\u0161ajiem");
    assertTrue(bijušajiem.isRecognized());
  }

  @Test public void video() {
    Word video = locītājs.analyze("video");
    assertTrue(video.isRecognized());
    assertTrue(video.wordformsCount() == 1);
  }

  @Test public void neviens() {
    Word neviens = locītājs.analyze("neviens");
    assertTrue(neviens.isRecognized());
    assertEquals(AttributeNames.v_Pronoun, neviens.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
    assertEquals(AttributeNames.v_Yes, neviens.wordforms.get(0).getValue(AttributeNames.i_Noliegums));
    assertEquals(AttributeNames.v_Nenoteiktie, neviens.wordforms.get(0).getValue(AttributeNames.i_VvTips));
    Word nekas = locītājs.analyze("nekas");
    assertTrue(nekas.isRecognized());
    assertEquals(AttributeNames.v_Pronoun, nekas.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
    assertEquals(AttributeNames.v_Yes, nekas.wordforms.get(0).getValue(AttributeNames.i_Noliegums));
    assertEquals(AttributeNames.v_Nenoteiktie, nekas.wordforms.get(0).getValue(AttributeNames.i_VvTips));
    Word nekāds = locītājs.analyze("nek\u0101ds");
    assertTrue(nekāds.isRecognized());
    int ind = 0;
    while (ind < nekāds.wordformsCount() && !AttributeNames.v_Pronoun.equalsIgnoreCase(nekāds.wordforms.get(ind).getValue(AttributeNames.i_PartOfSpeech))) {
      ind++;
    }
    assertTrue(ind < nekāds.wordformsCount());
    assertEquals(AttributeNames.v_Yes, nekāds.wordforms.get(ind).getValue(AttributeNames.i_Noliegums));
  }

  @Test public void atnes() {
    Word atnes = locītājs.analyze("atnes");
    assertTrue(atnes.isRecognized());
    AttributeValues filtrs = new AttributeValues();
    filtrs.addAttribute("Izteiksme", "Pav\u0113les");
    atnes.filterByAttributes(filtrs);
    assertTrue(atnes.isRecognized());
  }

  @Test public void jāatceras() {
    Word jāatceras = locītājs.analyze("j\u0101atceras");
    assertTrue(jāatceras.isRecognized());
    AttributeValues filtrs = new AttributeValues();
    filtrs.addAttribute("Izteiksme", "Vajadz\u012bbas");
    jāatceras.filterByAttributes(filtrs);
    assertTrue(jāatceras.isRecognized());
  }

  @Test public void jāmāk() {
    Word jāmāk = locītājs.analyze("j\u0101m\u0101k");
    assertTrue(jāmāk.isRecognized());
    AttributeValues filtrs = new AttributeValues();
    filtrs.addAttribute("Izteiksme", "Vajadz\u012bbas");
    jāmāk.filterByAttributes(filtrs);
    assertTrue(jāmāk.isRecognized());
  }

  @Test public void vislabāk() {
    Word vislabāk = locītājs.analyze("vislab\u0101k");
    assertTrue(vislabāk.isRecognized());
    boolean irPareizā = false;
    for (Wordform vārdforma : vislabāk.wordforms) {
      if (vārdforma.getValue(AttributeNames.i_Lemma).equals("labi")) {
        irPareizā = true;
      }
    }
    assertEquals(true, irPareizā);
  }

  @Test public void vairāk() {
    Word vairāk = locītājs.analyze("vair\u0101k");
    assertTrue(vairāk.isRecognized());
    boolean irPareizā = false;
    for (Wordform vārdforma : vairāk.wordforms) {
      if (vārdforma.getValue(AttributeNames.i_Lemma).equals("daudz")) {
        irPareizā = true;
      }
    }
    assertEquals(true, irPareizā);
  }

  @Test public void deminutive() {
    locītājs.enableDiminutive = true;
    Word cirvītis = locītājs.analyze("cirv\u012btis");
    Word pļava = locītājs.analyze("p\u013cavi\u0146a");
    assertTrue(cirvītis.isRecognized());
    assertTrue(pļava.isRecognized());
    boolean irPareizā = false;
    for (Wordform vārdforma : cirvītis.wordforms) {
      if (vārdforma.getValue(AttributeNames.i_Lemma).equals("cirv\u012btis")) {
        irPareizā = true;
        assertEquals(AttributeNames.v_Deminutive, vārdforma.getValue(AttributeNames.i_Guess));
      }
    }
    assertEquals(true, irPareizā);
    irPareizā = false;
    for (Wordform vārdforma : pļava.wordforms) {
      if (vārdforma.getValue(AttributeNames.i_Lemma).equals("p\u013cavi\u0146a")) {
        irPareizā = true;
      }
    }
    assertEquals(true, irPareizā);
  }

  @Test public void riebties() {
    locītājs.enableGuessing = true;
    Word riebties = locītājs.analyze("riebties");
    assertTrue(riebties.isRecognized());
    assertEquals("riebties", riebties.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void sa() {
    locītājs.enablePrefixes = true;
    Word sa = locītājs.analyze("");
    assertFalse(sa.isRecognized());
  }

  @Test public void noliegumu_lemma() {
    locītājs.enablePrefixes = true;
    Word nenest = locītājs.analyze("nenes\u0101t");
    assertTrue(nenest.isRecognized());
    assertEquals("nenest", nenest.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    assertEquals("nest", nenest.wordforms.get(0).getValue(AttributeNames.i_SourceLemma));
  }

  @Test public void kususi() {
    locītājs.enablePrefixes = true;
    List<Word> tokens = Splitting.tokenize(locītājs, "Vai esi piekususi?");
    Word piekususi = tokens.get(2);
    assertTrue(piekususi.isRecognized());
    assertEquals("piekususi", piekususi.getToken());
    assertEquals("piekususi", piekususi.wordforms.get(0).getToken());
  }

  @Test public void tokenizesafety() {
    String text = "V\u012brs ar cirvi piekusa joklmnasdasd1239612321 *(&(*^)@!!@# /t/txxx/n\t\nasdas cimdi\u0146i cimdi\u0146ze\u0137\u012btes";
    LinkedList<Word> tokens = Splitting.tokenize(locītājs, text);
    String wordtokens = "";
    for (Word w : tokens) {
      wordtokens += w.getToken();
      for (Wordform wf : w.wordforms) {
        assertEquals(w.getToken(), wf.getToken());
      }
    }
    assertEquals(text.replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", ""), wordtokens);
    locītājs.enableVocative = true;
    locītājs.enableDiminutive = true;
    locītājs.enablePrefixes = true;
    locītājs.enableGuessing = true;
    locītājs.enableAllGuesses = true;
    locītājs.meklētsalikteņus = true;
    tokens = Splitting.tokenize(locītājs, text);
    wordtokens = "";
    for (Word w : tokens) {
      wordtokens += w.getToken();
      for (Wordform wf : w.wordforms) {
        assertEquals(w.getToken(), wf.getToken());
      }
    }
    assertEquals(text.replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", ""), wordtokens);
  }

  @Test public void saīsinājumi() {
    Word uc = locītājs.analyze("u.c.");
    assertTrue(uc.isRecognized());
    assertEquals("y", uc.wordforms.get(0).getTag());
  }

  @Test public void nopūzdamās() {
    Word nopūzdamās = locītājs.analyze("p\u016bzdam\u0101s");
    assertTrue(nopūzdamās.isRecognized());
    Word nopūsdamās = locītājs.analyze("p\u016bsdam\u0101s");
    assertFalse(nopūsdamās.isRecognized());
    Word grūzdams = locītājs.analyze("gr\u016bzdams");
    assertTrue(grūzdams.isRecognized());
    Word mezdams = locītājs.analyze("mezdams");
    assertTrue(mezdams.isRecognized());
    Word elsdams = locītājs.analyze("elsdams");
    assertTrue(elsdams.isRecognized());
    Word milzdams = locītājs.analyze("milzdams");
    assertTrue(milzdams.isRecognized());
    Word nesdams = locītājs.analyze("nesdams");
    assertTrue(nesdams.isRecognized());
  }

  @Test public void ts() {
    Word nopūsts = locītājs.analyze("p\u016bsts");
    assertTrue(nopūsts.isRecognized());
    Word grūsts = locītājs.analyze("gr\u016bsts");
    assertTrue(grūsts.isRecognized());
    Word mests = locītājs.analyze("mests");
    assertTrue(mests.isRecognized());
    Word elsts = locītājs.analyze("elsts");
    assertTrue(elsts.isRecognized());
    Word mēzts = locītājs.analyze("m\u0113zts");
    assertTrue(mēzts.isRecognized());
    Word nests = locītājs.analyze("nests");
    assertTrue(nests.isRecognized());
  }

  @Test public void source_lemma() {
    Word balta = locītājs.analyze("baltas");
    assertTrue(balta.isRecognized());
    assertEquals("balta", balta.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    assertEquals("balts", balta.wordforms.get(0).getValue(AttributeNames.i_SourceLemma));
    locītājs.enablePrefixes = false;
    Word miršana = locītājs.analyze("mir\u0161ana");
    assertTrue(miršana.isRecognized());
    assertEquals("mir\u0161ana", miršana.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    assertEquals("mirt", miršana.wordforms.get(0).getValue(AttributeNames.i_SourceLemma));
  }

  @Test public void residuals() {
    Word slīpsvītra = locītājs.analyze("/");
    assertTrue(slīpsvītra.isRecognized());
    assertEquals("zx", slīpsvītra.wordforms.get(0).getTag());
    Word dr = locītājs.analyze("dr.");
    assertTrue(dr.isRecognized());
    assertEquals("y", dr.wordforms.get(0).getTag());
    Word plus = locītājs.analyze("+");
    assertTrue(plus.isRecognized());
    assertEquals("xx", plus.wordforms.get(0).getTag());
  }

  @Test public void numbers() {
    Word num = locītājs.analyze("123456");
    assertTrue(num.isRecognized());
    assertEquals("xn", num.wordforms.get(0).getTag());
    assertEquals("123456", num.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    Word ord = locītājs.analyze("15.");
    assertTrue(ord.isRecognized());
    assertEquals("xo", ord.wordforms.get(0).getTag());
  }

  @Test public void pieci() {
    Word pieci = locītājs.analyze("pieci");
    assertTrue(pieci.isRecognized());
    assertEquals("pieci", pieci.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    pieci = locītājs.analyze("5");
    assertTrue(pieci.isRecognized());
    assertEquals("5", pieci.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void iejāt() {
    locītājs.enablePrefixes = true;
    Word iejāt = locītājs.analyze("iej\u0101t");
    assertTrue(iejāt.isRecognized());
    assertEquals("iej\u0101t", iejāt.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void labākais() {
    Word ātrāks = locītājs.analyze("lab\u0101ks");
    assertTrue(ātrāks.isRecognized());
    assertEquals("P\u0101r\u0101k\u0101", ātrāks.wordforms.get(0).getValue("Pak\u0101pe"));
    Word visātrākais = locītājs.analyze("lab\u0101kais");
    assertTrue(visātrākais.isRecognized());
  }

  @Test public void reziduāļi() {
    locītājs.enableDiminutive = true;
    locītājs.enablePrefixes = true;
    locītājs.enableGuessing = true;
    locītājs.enableAllGuesses = true;
    locītājs.meklētsalikteņus = true;
    Word m = locītājs.analyze("M.");
    assertTrue(m.isRecognized());
    assertEquals(AttributeNames.v_Abbreviation, m.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
  }

  @Test public void atstarpes() {
    locītājs.enableDiminutive = true;
    locītājs.enablePrefixes = true;
    locītājs.enableGuessing = true;
    locītājs.enableAllGuesses = true;
    locītājs.meklētsalikteņus = true;
    Word ne = locītājs.analyze("ne ");
    assertTrue(ne.isRecognized());
    assertEquals("ne", ne.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void gunta2012mai() {
    Word atguvies = locītājs.analyze("atguvies");
    assertTrue(atguvies.isRecognized());
    Word sizdams = locītājs.analyze("sizdams");
    assertTrue(sizdams.isRecognized());
    Word sēzdamies = locītājs.analyze("s\u0113zdamies");
    assertTrue(sēzdamies.isRecognized());
    Word sarūdzis = locītājs.analyze("sar\u016bdzis");
    assertTrue(sarūdzis.isRecognized());
    Word irties = locītājs.analyze("irties");
    assertTrue(irties.isRecognized());
    Word tekalēt = locītājs.analyze("tekal\u0113t");
    assertTrue(tekalēt.isRecognized());
    Word kļūt = locītājs.analyze("k\u013c\u016bt");
    assertTrue(kļūt.isRecognized());
    Word proti = locītājs.analyze("proti");
    assertTrue(proti.isRecognized());
  }

  @Test public void lūzīs() {
    Word lūzīs = locītājs.analyze("l\u016bz\u012bs");
    assertTrue(lūzīs.isRecognized());
    assertEquals("l\u016bzt", lūzīs.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void ģenerēšana() {
    List<Wordform> formas = locītājs.generateInflections("Valdis");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Valda");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Valdim");
    formas = locītājs.generateInflections("Raitis");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Raita");
    formas = locītājs.generateInflections("cer\u0113t");
  }

  @Test public void ģenerēšanaNezināmiem() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    assertTrue("Valdis".matches("\\p{Lu}.*"));
    assertTrue("\u0100dolfs".matches("\\p{Lu}.*"));
    assertFalse("valdis".matches("\\p{Lu}.*"));
    assertFalse("\u0101dolfs".matches("\\p{Lu}.*"));
    Word zolā = locītājs.analyze("Zol\u0101");
    assertTrue(zolā.isRecognized());
    assertEquals(AttributeNames.v_Noun, zolā.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
    ArrayList<Wordform> formas = locītājs.generateInflections("Zol\u0101");
    assertTrue(formas.size() > 0);
  }

  @Test public void vešana() {
    Word vešana = locītājs.analyze("ve\u0161ana");
    assertTrue(vešana.isRecognized());
    assertEquals("vest", vešana.wordforms.get(0).getValue(AttributeNames.i_SourceLemma));
    Word vesšana = locītājs.analyze("ves\u0161ana");
    assertFalse(vesšana.isRecognized());
    Word mēzšana = locītājs.analyze("m\u0113z\u0161ana");
    assertFalse(mēzšana.isRecognized());
  }

  @Test public void nelokaamie() {
    locītājs.enableDiminutive = true;
    locītājs.enablePrefixes = true;
    locītājs.enableGuessing = true;
    locītājs.enableAllGuesses = true;
    locītājs.meklētsalikteņus = true;
    locītājs.guessInflexibleNouns = true;
    Word vārds = locītājs.analyze("TrrT");
    assertTrue(vārds.isRecognized());
    assertEquals("Trrt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("GAIZINAISI-\u01003");
    assertTrue(vārds.isRecognized());
    assertEquals("Gaizinaisi-\u01003", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    assertEquals(AttributeNames.v_Residual, vārds.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
    vārds = locītājs.analyze("0.40");
    assertTrue(vārds.isRecognized());
    assertEquals("0.40", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    assertEquals(AttributeNames.v_Residual, vārds.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
    assertEquals(AttributeNames.v_Number, vārds.wordforms.get(0).getValue(AttributeNames.i_ResidualType));
    vārds = locītājs.analyze("6/7");
    assertTrue(vārds.isRecognized());
    assertEquals("6/7", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    assertEquals(AttributeNames.v_Residual, vārds.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
    assertEquals(AttributeNames.v_Number, vārds.wordforms.get(0).getValue(AttributeNames.i_ResidualType));
    vārds = locītājs.analyze("....");
    assertTrue(vārds.isRecognized());
    for (Wordform wf : vārds.wordforms) {
      assertEquals("....", wf.getValue(AttributeNames.i_Lemma));
    }
  }

  @Test public void personvaardi_Varis() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("Valdis", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Valda");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Valdim");
    formas = locītājs.generateInflections("\u010caikovskis", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "\u010caikovska");
    formas = locītājs.generateInflections("C\u0113sis", true);
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "C\u0113su");
    formas = locītājs.generateInflections("Raitis", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Raita");
    formas = locītājs.generateInflections("Auzi\u0146\u0161", true);
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "Auzi\u0146u");
    formas = locītājs.generateInflections("Ivis", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Ivja");
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "Ivju");
    formas = locītājs.generateInflections("Egl\u012bts", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Egl\u012b\u0161a");
    formas = locītājs.generateInflections("\u0160virkste", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, AttributeNames.v_Feminine, "\u0160virkstes");
    formas = locītājs.generateInflections("Ta\u013cikova", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, AttributeNames.v_Feminine, "Ta\u013cikovas");
    formas = locītājs.generateInflections("B\u0113rzi\u0146\u0161", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Nominative, AttributeNames.v_Masculine, "B\u0113rzi\u0146\u0161");
    formas = locītājs.generateInflections("D\u012bcis", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Masculine, "D\u012bcim");
    formas = locītājs.generateInflections("Asna", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Asnai");
    formas = locītājs.generateInflections("Lielais", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Masculine, "Lielajam");
    formas = locītājs.generateInflections("Maz\u0101", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Mazajai");
    formas = locītājs.generateInflections("Za\u013cais", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Masculine, "Za\u013cajam");
    formas = locītājs.generateInflections("Santis", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, AttributeNames.v_Masculine, "Santa");
  }

  @Test public void no_iepirkšanās() {
    Word vārds = locītājs.analyze("no iepirk\u0161an\u0101s");
    assertFalse(vārds.isRecognized());
    vārds = locītājs.analyze("uz kino");
    assertFalse(vārds.isRecognized());
    vārds = locītājs.analyze("nocirvis");
    assertFalse(vārds.isRecognized());
  }

  @Test public void cache() {
    locītājs.setCacheSize(1000);
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    Word vārds = locītājs.analyze("sacelt");
    assertTrue(vārds.isRecognized());
    assertEquals("sacelt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("celt");
    assertTrue(vārds.isRecognized());
    assertEquals("celt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void mazajai() {
    Word mazajai = locītājs.analyze("mazajai");
    assertTrue(mazajai.isRecognized());
    assertEquals("maza", mazajai.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void personvārdi_Varis2() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("Pauls", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Paul");
    formas = locītājs.generateInflections("Laura", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Laura");
    formas = locītājs.generateInflections("Lauri\u0146a", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Lauri\u0146");
    formas = locītājs.generateInflections("Made", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Made");
    formas = locītājs.generateInflections("Krist\u012bn\u012bte", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Krist\u012bn\u012bt");
    formas = locītājs.generateInflections("Margrieta", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Margrieta");
  }

  @Test public void leksikoni() {
    Word pokemons = locītājs.analyze("Bisjakovs");
    assertFalse(pokemons.isRecognized());
  }

  @Test public void daudzskaitlinieki() {
    Word augstpapēžu = locītājs.analyzeLemma("augstpap\u0113\u017eu");
    assertTrue(augstpapēžu.isRecognized());
  }

  @Test public void personvārdi_Varis3() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("Auzi\u0146\u0161", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Auzi\u0146");
    assertTrue(locītājs.analyze("Miervalda").isRecognized());
    assertTrue(locītājs.analyze("Mierval\u017ea").isRecognized());
    formas = locītājs.generateInflections("Miervaldis", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Miervalda");
  }

  @Test public void Laura10Aug() {
    Word vārds = locītājs.analyze("vienai");
    assertTrue(vārds.isRecognized());
    assertEquals("viena", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("pirmajai");
    assertTrue(vārds.isRecognized());
    assertEquals("pirm\u0101", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("tre\u0161\u0101s");
    assertTrue(vārds.isRecognized());
    assertEquals("tre\u0161\u0101", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("piecsimt");
    assertTrue(vārds.isRecognized());
    assertEquals("mcc0p0", vārds.wordforms.get(0).getTag());
  }

  @Test public void personvārdi_Varis4() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("J\u0113kabs");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "J\u0113kab");
    formas = locītājs.generateInflections("M\u0101rti\u0146\u0161");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "M\u0101rti\u0146");
    formas = locītājs.generateInflections("Mikus");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Miku");
    formas = locītājs.generateInflections("Ingus");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Ingu");
    formas = locītājs.generateInflections("Kalns");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Kaln");
    formas = locītājs.generateInflections("Liepi\u0146\u0161");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Liepi\u0146");
    formas = locītājs.generateInflections("Za\u0137is");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Za\u0137i");
    formas = locītājs.generateInflections("Ledus");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Ledu");
    formas = locītājs.generateInflections("Platais");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Platais");
    formas = locītājs.generateInflections("Lielais");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Lielais");
    formas = locītājs.generateInflections("Biezais");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Biezais");
    formas = locītājs.generateInflections("Silvija");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Silvij");
    formas = locītājs.generateInflections("Kadrije");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Kadrij");
    formas = locītājs.generateInflections("Karl\u012bne");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Karl\u012bn");
    formas = locītājs.generateInflections("Vilhelm\u012bne");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Vilhelm\u012bn");
    formas = locītājs.generateInflections("Skaidr\u012bte");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Skaidr\u012bt");
    formas = locītājs.generateInflections("Juli\u0101na");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Juli\u0101n");
    formas = locītājs.generateInflections("Egl\u012bte");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Egl\u012bt");
    formas = locītājs.generateInflections("Lapsi\u0146a");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Lapsi\u0146");
    formas = locītājs.generateInflections("Pils\u0113tniece");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Pils\u0113tniec");
    formas = locītājs.generateInflections("Saln\u0101ja");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Saln\u0101j");
    formas = locītājs.generateInflections("Gark\u0101je");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Gark\u0101je");
    formas = locītājs.generateInflections("Zeidmane");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Zeidmane");
    formas = locītājs.generateInflections("Kreice");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Kreice");
    formas = locītājs.generateInflections("Kreija");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Kreija");
    formas = locītājs.generateInflections("Kreitenberga");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "Kreitenberga");
  }

  @Test public void personvārdi_Varis5() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("Arvydas", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Arvydas");
    formas = locītājs.generateInflections("R\u012bta", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "R\u012bta");
    formas = locītājs.generateInflections("r\u012bta", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "r\u012bta");
  }

  @Test public void laura_Aug13() {
    locītājs.enableGuessing = true;
    List<Wordform> formas = locītājs.generateInflections("Fredis");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Freda");
    formas = locītājs.generateInflections("Alda");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Aldas");
    Word freda = locītājs.analyze("Freda");
    assertTrue(freda.isRecognized());
    boolean irPareizā = false;
    for (Wordform vārdforma : freda.wordforms) {
      if (vārdforma.getValue(AttributeNames.i_Lemma).equals("Fredis")) {
        irPareizā = true;
      }
    }
    assertEquals(true, irPareizā);
  }

  @Test public void laura_Aug13_2() {
    locītājs.enableGuessing = true;
    Word sia = locītājs.analyze("SIA");
    assertTrue(sia.isRecognized());
    Word numur = locītājs.analyze("numur");
    assertTrue(numur.isRecognized());
    boolean irPareizā = false;
    for (Wordform vārdforma : numur.wordforms) {
      if (vārdforma.getValue(AttributeNames.i_Lemma).equals("numurs") && vārdforma.isMatchingStrong(AttributeNames.i_Case, AttributeNames.v_Nominative)) {
        irPareizā = true;
      }
    }
    assertEquals(true, irPareizā);
  }

  @Test public void GuntaAug22() {
    Word vārds = locītājs.analyze("\u0113d");
    assertTrue(vārds.isRecognized());
    boolean irPareizā = false;
    for (Wordform vārdforma : vārds.wordforms) {
      if (vārdforma.isMatchingStrong(AttributeNames.i_Person, "2")) {
        irPareizā = true;
      }
    }
    assertTrue(irPareizā);
    vārds = locītājs.analyze("paz\u016bd");
    assertTrue(vārds.isRecognized());
    for (Wordform vārdforma : vārds.wordforms) {
      assertFalse(vārdforma.isMatchingStrong(AttributeNames.i_Person, "2"));
    }
  }

  @Test public void InflectionSep4() {
    List<Wordform> formas = locītājs.generateInflections("iem\u0101c\u012bties");
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_EndingID, "1057");
    assertInflection(formas, testset, "iem\u0101coties");
    testset.addAttribute(AttributeNames.i_EndingID, "1027");
    assertInflection(formas, testset, "j\u0101iem\u0101c\u0101s");
    testset.addAttribute(AttributeNames.i_EndingID, "1210");
    assertInflection(formas, testset, "j\u0101iem\u0101coties");
    formas = locītājs.generateInflections("m\u0101c\u012bt");
    testset.addAttribute(AttributeNames.i_EndingID, "472");
    assertInflection(formas, testset, "m\u0101c\u0101m");
    testset.addAttribute(AttributeNames.i_EndingID, "474");
    assertInflection(formas, testset, "m\u0101ca");
    testset.addAttribute(AttributeNames.i_EndingID, "487");
    assertInflection(formas, testset, "j\u0101m\u0101ca");
    testset.addAttribute(AttributeNames.i_EndingID, "1204");
    assertInflection(formas, testset, "j\u0101m\u0101cot");
    formas = locītājs.generateInflections("m\u0101c\u0113t");
    testset.addAttribute(AttributeNames.i_EndingID, "1779");
    assertInflection(formas, testset, "m\u0101ku");
    testset.addAttribute(AttributeNames.i_EndingID, "1780");
    assertInflection(formas, testset, "m\u0101ki");
    testset.addAttribute(AttributeNames.i_EndingID, "1781");
    assertInflection(formas, testset, "m\u0101kam");
    testset.addAttribute(AttributeNames.i_EndingID, "1783");
    assertInflection(formas, testset, "m\u0101k");
    testset.addAttribute(AttributeNames.i_EndingID, "1794");
    assertInflection(formas, testset, "j\u0101m\u0101k");
    testset.addAttribute(AttributeNames.i_EndingID, "2328");
    assertInflection(formas, testset, "j\u0101m\u0101kot");
    formas = locītājs.generateInflections("tec\u0113t");
    testset.addAttribute(AttributeNames.i_EndingID, "1779");
    assertInflection(formas, testset, "teku");
    testset.addAttribute(AttributeNames.i_EndingID, "1780");
    assertInflection(formas, testset, "teci");
    testset.addAttribute(AttributeNames.i_EndingID, "1781");
    assertInflection(formas, testset, "tekam");
    testset.addAttribute(AttributeNames.i_EndingID, "1783");
    assertInflection(formas, testset, "tek");
    AttributeValues filter = new AttributeValues();
    filter.addAttribute(AttributeNames.i_ParadigmID, "45");
    formas = locītājs.generateInflections("gul\u0113t", false, filter);
    testset.addAttribute(AttributeNames.i_EndingID, "1780");
    assertInflection(formas, testset, "guli");
    testset.addAttribute(AttributeNames.i_EndingID, "1783");
    assertInflection(formas, testset, "gu\u013c");
    testset.addAttribute(AttributeNames.i_EndingID, "1798");
    assertInflection(formas, testset, "guliet");
    testset.addAttribute(AttributeNames.i_EndingID, "2328");
    assertInflection(formas, testset, "j\u0101gu\u013cot");
    formas = locītājs.generateInflections("aizgul\u0113ties");
    testset.addAttribute(AttributeNames.i_EndingID, "2337");
    assertInflection(formas, testset, "aizgu\u013cos");
    formas = locītājs.generateInflections("vajadz\u0113t");
    testset.addAttribute(AttributeNames.i_EndingID, "1779");
    assertInflection(formas, testset, "vajagu");
    testset.addAttribute(AttributeNames.i_EndingID, "1781");
    assertInflection(formas, testset, "vajagam");
    testset.addAttribute(AttributeNames.i_EndingID, "1783");
    assertInflection(formas, testset, "vajag");
    testset.addAttribute(AttributeNames.i_EndingID, "1794");
    assertInflection(formas, testset, "j\u0101vajag");
    testset.addAttribute(AttributeNames.i_EndingID, "2328");
    assertInflection(formas, testset, "j\u0101vajagot");
    formas = locītājs.generateInflections("moc\u012bt", false, filter);
    testset.addAttribute(AttributeNames.i_EndingID, "1780");
    assertInflection(formas, testset, "moki");
    formas = locītājs.generateInflections("slodz\u012bt");
    testset.addAttribute(AttributeNames.i_EndingID, "1779");
    assertInflection(formas, testset, "slogu");
    formas = locītājs.generateInflections("mesties");
    testset.addAttribute(AttributeNames.i_EndingID, "1072");
    assertInflection(formas, testset, "me\u0161an\u0101s");
    formas = locītājs.generateInflections("p\u016bsties");
    testset.addAttribute(AttributeNames.i_EndingID, "1087");
    assertInflection(formas, testset, "p\u016bties");
    Word vārds = locītājs.analyze("gulo\u0161s");
    assertTrue(vārds.isRecognized());
    vārds = locītājs.analyze("gu\u013co\u0161s");
    assertTrue(vārds.isRecognized());
  }

  @Test public void gunta_20120911() {
    Word vārds = locītājs.analyze("nest");
    assertTrue(vārds.isRecognized());
    vārds = locītājs.analyze("nes\u012bs");
    assertTrue(vārds.isRecognized());
    vārds = locītājs.analyze("vest");
    assertTrue(vārds.isRecognized());
    vārds = locītājs.analyze("ved\u012bs");
    assertTrue(vārds.isRecognized());
    vārds = locītājs.analyze("vess");
    assertFalse(vārds.isRecognized());
    vārds = locītājs.analyze("ves\u012bs");
    vārds = locītājs.analyze("ness");
    assertFalse(vārds.isRecognized());
  }

  @Test public void pazūdi() {
    Word vārds = locītājs.analyze("paz\u016bdi");
    assertTrue(vārds.isRecognized());
    boolean irPareizā = false;
    for (Wordform vārdforma : vārds.wordforms) {
      if (vārdforma.getValue(AttributeNames.i_EndingID).equals("790")) {
        irPareizā = true;
      }
    }
    assertEquals(true, irPareizā);
    List<Wordform> formas = locītājs.generateInflections("pazust");
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_EndingID, "790");
    assertInflection(formas, testset, "paz\u016bdi");
    formas = locītājs.generateInflections("atrast");
    testset.addAttribute(AttributeNames.i_EndingID, "790");
    assertInflection(formas, testset, "atrodi");
  }

  @Test public void vajadzības_minēšana() {
    locītājs.enablePrefixes = true;
    Word vārds = locītājs.analyze("rakt");
    assertTrue(vārds.isRecognized());
    assertEquals("rakt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("aizrakt");
    assertTrue(vārds.isRecognized());
    assertEquals("aizrakt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("j\u0101rok");
    assertTrue(vārds.isRecognized());
    assertEquals("rakt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("j\u0101aizrok");
    assertTrue(vārds.isRecognized());
    assertEquals("aizrakt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void divdabju_pārākās_formas() {
    Word vārds = locītājs.analyze("izkusu\u0161ais");
    assertTrue(vārds.isRecognized());
    assertEquals("izkust", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("izkusu\u0161\u0101kais");
    assertTrue(vārds.isRecognized());
    assertEquals("izkust", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("visizkusu\u0161\u0101kais");
    assertTrue(vārds.isRecognized());
    assertEquals("izkust", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("visveikt\u0101kais");
    assertTrue(vārds.isRecognized());
    assertEquals("veikt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("vislas\u012bt\u0101kais");
    assertTrue(vārds.isRecognized());
    assertEquals("las\u012bt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("veicu");
    assertTrue(vārds.isRecognized());
    assertEquals("veikt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("veiku\u0161ais");
    assertTrue(vārds.isRecognized());
    assertEquals("veikt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("veicu\u0161ais");
    assertFalse(vārds.isRecognized());
    vārds = locītājs.analyze("sar\u016bgu");
    assertTrue(vārds.isRecognized());
    assertEquals("sar\u016bgt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("sar\u016bgu\u0161ais");
    assertTrue(vārds.isRecognized());
    assertEquals("sar\u016bgt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("sar\u016bdzu\u0161ais");
    assertFalse(vārds.isRecognized());
    vārds = locītājs.analyze("zaigoju\u0161\u0101ks");
    assertTrue(vārds.isRecognized());
    assertEquals("zaigot", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("zaigojo\u0161\u0101ks");
    assertTrue(vārds.isRecognized());
    assertEquals("zaigot", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("vislas\u012bju\u0161\u0101kais");
    assertTrue(vārds.isRecognized());
    assertEquals("las\u012bt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("laso\u0161\u0101ks");
    assertTrue(vārds.isRecognized());
    assertEquals("las\u012bt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("vislas\u0101m\u0101kais");
    assertTrue(vārds.isRecognized());
    assertEquals("las\u012bt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("saprotam\u0101ks");
    assertTrue(vārds.isRecognized());
    assertEquals("saprast", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("viszaigojo\u0161\u0101k");
    assertTrue(vārds.isRecognized());
    assertEquals("zaigojo\u0161i", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void normunds20130128() {
    Word vārds = locītājs.analyze("m\u0101c");
    assertTrue(vārds.isRecognized());
    assertEquals("m\u0101kt", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    assertEquals(3, vārds.wordforms.size());
    List<Wordform> formas = locītājs.generateInflections("p\u013caut");
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    testset.addAttribute(AttributeNames.i_Person, "3");
    testset.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Iisteniibas);
    testset.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Tagadne);
    assertInflection(formas, testset, "p\u013cauj");
    testset.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Pagaatne);
    assertInflection(formas, testset, "p\u013c\u0101va");
    formas = locītājs.generateInflections("k\u013caut");
    testset.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Tagadne);
    assertInflection(formas, testset, "k\u013cauj");
    testset.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Pagaatne);
    assertInflection(formas, testset, "k\u013c\u0101va");
    formas = locītājs.generateInflections("iek\u013caut");
    testset.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Tagadne);
    assertInflection(formas, testset, "iek\u013cauj");
    testset.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Pagaatne);
    assertInflection(formas, testset, "iek\u013c\u0101va");
  }

  @Test public void vienādās_nenoteiksmes() {
    Paradigm pirmā = locītājs.paradigmByID(15);
    Paradigm otrā = locītājs.paradigmByID(16);
    Paradigm trešā = locītājs.paradigmByID(17);
    LinkedList<Lexeme> leksēmas = new LinkedList<Lexeme>();
    leksēmas.addAll(pirmā.lexemes);
    leksēmas.addAll(otrā.lexemes);
    leksēmas.addAll(trešā.lexemes);
    for (Lexeme lex : leksēmas) {
      LinkedList<Lexeme> alternatīvas = new LinkedList<Lexeme>();
      ArrayList<Lexeme> xx = pirmā.getLexemesByStem().get(0).get(lex.getStem(0));
      if (xx != null) {
        alternatīvas.addAll(xx);
      }
      xx = otrā.getLexemesByStem().get(0).get(lex.getStem(0));
      if (xx != null) {
        alternatīvas.addAll(xx);
      }
      xx = trešā.getLexemesByStem().get(0).get(lex.getStem(0));
      if (xx != null) {
        alternatīvas.addAll(xx);
      }
      for (Lexeme alternatīva : alternatīvas) {
        if (lex.getID() < alternatīva.getID()) {
          if (lex.getParadigm() != alternatīva.getParadigm()) {
          }
          if (lex.getParadigm() == pirmā && alternatīva.getParadigm() == pirmā && (!lex.getStem(1).equalsIgnoreCase(alternatīva.getStem(1)) || !lex.getStem(2).equalsIgnoreCase(alternatīva.getStem(2)))) {
          }
        }
      }
    }
  }

  @Test public void personvārdi_Varis6() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("Edvards", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Edvarda");
    formas = locītājs.generateInflections("Ludis", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Luda");
    formas = locītājs.generateInflections("Krists", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Kristam");
    formas = locītājs.generateInflections("Sta\u0146islava", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Sta\u0146islavai");
    formas = locītājs.generateInflections("Raisa", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Raisai");
    formas = locītājs.generateInflections("Alberta", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Albertai");
    formas = locītājs.generateInflections("Gunta", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Guntai");
  }

  @Test public void gunta19dec_3() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    Word vārds = locītājs.analyze("ragus");
    assertTrue(vārds.isRecognized());
    assertEquals("rags", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("dermatovenerologi");
    assertTrue(vārds.isRecognized());
    assertEquals("dermatovenerologs", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void normunds_2013feb25() {
    List<Wordform> formas = locītājs.generateInflections("dzied\u0101t");
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    testset.addAttribute(AttributeNames.i_Person, "1");
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
    testset.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Iisteniibas);
    testset.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Tagadne);
    assertInflection(formas, testset, "dziedam");
    testset.removeAttribute(AttributeNames.i_Number);
    testset.addAttribute(AttributeNames.i_Person, "3");
    assertInflection(formas, testset, "dzied");
    Word vārds = locītājs.analyze("dziedam");
    assertTrue(vārds.isRecognized());
    assertEquals("dzied\u0101t", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("dzied");
    assertTrue(vārds.isRecognized());
    assertEquals("dzied\u0101t", vārds.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    vārds = locītājs.analyze("dzied\u0101m");
    assertFalse(vārds.isRecognized());
  }

  @Test public void pp20130313() {
    List<Wordform> formas = locītājs.generateInflections("rakt");
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    testset.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Vajadziibas);
    assertInflection(formas, testset, "j\u0101rok");
  }

  @Test public void varis20130221() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("Liepa", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Liepai");
    AttributeValues filter = new AttributeValues();
    filter.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
    formas = locītājs.generateInflections("Liepa", true, filter);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Liepam");
    formas = locītājs.generateInflections("Lielais", true, filter);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Masculine, "Lielajam");
    formas = locītājs.generateInflections("Vald\u012b\u0161ana", true, filter);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Masculine, "Vald\u012b\u0161anam");
    filter.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
    formas = locītājs.generateInflections("Dzelzs", true, filter);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Dzelzij");
    formas = locītājs.generateInflections("Maz\u0101", true, filter);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Mazajai");
    formas = locītājs.generateInflections("Vald\u012b\u0161ana", true, filter);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, AttributeNames.v_Feminine, "Vald\u012b\u0161anai");
  }

  @Test public void varis20130317() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    assertTrue("Biez\u0101".matches("\\p{Lu}.*"));
    assertTrue("BIEZ\u0100".matches("\\p{Lu}.*"));
    List<Wordform> formas = locītājs.generateInflections("Biez\u0101", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Biezajai");
    formas = locītājs.generateInflections("BIEZ\u0100", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "BIEZAJAI");
    AttributeValues filter = new AttributeValues();
    filter.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Feminine);
    formas = locītājs.generateInflections("V\u012aTOLA", true, filter);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "V\u012aTOLAI");
    formas = locītājs.generateInflections("BAG\u0100T\u0100", true, filter);
    assertTrue(formas.size() > 0);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "BAG\u0100TAJAI");
    formas = locītājs.generateInflections("V\u012btola", true, filter);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "V\u012btolai");
    filter.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
    formas = locītājs.generateInflections("Kirill", true);
    assertTrue(formas.size() > 0);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Kirill");
    formas = locītājs.generateInflections("Andrej", true);
    assertTrue(formas.size() > 0);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "Andrej");
  }

  @Test public void laura_20130605() {
    Word viņš = locītājs.analyze("vi\u0146\u0161");
    assertTrue(viņš.isRecognized());
    assertEquals("pp3msnn", viņš.wordforms.get(0).getTag());
    Word ciršana = locītājs.analyze("cir\u0161ana");
    assertTrue(ciršana.isRecognized());
    assertEquals("ncfsn4", ciršana.wordforms.get(0).getTag());
    assertEquals("cir\u0161ana", ciršana.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    Word mazgāšanās = locītājs.analyze("mazg\u0101\u0161anos");
    assertTrue(mazgāšanās.isRecognized());
    assertEquals("ncfsar", mazgāšanās.getBestWordform().getTag());
    locītājs.enableGuessing = true;
    Word izpaudusies = locītājs.analyze("izpaudusies");
    assertTrue(izpaudusies.isRecognized());
  }

  @Test public void gunta_20130605() {
    Word attiecas = locītājs.analyze("attiecas");
    assertTrue(attiecas.isRecognized());
    assertEquals("attiekties", attiecas.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    Word esošo = locītājs.analyze("eso\u0161o");
    assertTrue(esošo.isRecognized());
    assertEquals("b\u016bt", esošo.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    Word mācās = locītājs.analyze("m\u0101c\u0101s");
    assertTrue(mācās.isRecognized());
    boolean found = false;
    for (Wordform wf : mācās.wordforms) {
      if (wf.isMatchingStrong(AttributeNames.i_Lemma, "m\u0101c\u012bties")) {
        found = true;
      }
    }
    assertTrue(found);
    Word acīmredzot = locītājs.analyze("ac\u012bmredzot");
    assertTrue(acīmredzot.isRecognized());
    assertEquals("ac\u012bmredzot", acīmredzot.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    Word lielākoties = locītājs.analyze("liel\u0101koties");
    assertTrue(lielākoties.isRecognized());
    assertEquals("liel\u0101koties", lielākoties.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  /**
     * Korpusa analīze - vārdi, kuriem analizators neiedeva nevienu sakarīgu variantu
     */
  @Test public void korpuss_20130605() {
    Word ņem = locītājs.analyze("\u0146em");
    assertTrue(ņem.isRecognized());
    assertEquals("\u0146emt", ņem.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    List<Wordform> formas = locītājs.generateInflections("\u0146emt");
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    testset.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Tagadne);
    testset.addAttribute(AttributeNames.i_Person, "2");
    testset.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Iisteniibas);
    assertInflection(formas, testset, "\u0146em");
    boolean found = false;
    for (Wordform wf : ņem.wordforms) {
      if (wf.isMatchingStrong(AttributeNames.i_Person, "2")) {
        found = true;
      }
    }
    assertTrue(found);
    formas = locītājs.generateInflections("p\u0101riet");
    testset.addAttribute(AttributeNames.i_Person, "3");
    assertInflection(formas, testset, "p\u0101riet");
  }

  @Test public void korpuss_20130606() {
    Word acs = locītājs.analyze("acs");
    assertTrue(acs.isRecognized());
    assertEquals("acs", acs.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    assertEquals(AttributeNames.v_Feminine, acs.wordforms.get(0).getValue(AttributeNames.i_Gender));
    List<Wordform> formas = locītājs.generateInflections("atk\u0101pties");
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    testset.addAttribute(AttributeNames.i_Person, "2");
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    testset.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Paveeles);
    assertInflection(formas, testset, "atk\u0101pies");
  }

  @Test public void uri() {
    Word url = locītājs.analyze("www.pillar.lv");
    assertTrue(url.isRecognized());
    assertEquals("xu", url.wordforms.get(0).getTag());
  }

  @Ignore(value = "J\u0101skat\u0101s p\u0113c t\u0113zaura datu pievieno\u0161anas") @Test public void obligātiatpazīstamie() throws IOException {
    {
      BufferedReader ieeja;
      String rinda;
      ieeja = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("mandatory.txt"), "UTF-8"));
      while ((rinda = ieeja.readLine()) != null) {
        if (rinda.contains("#") || rinda.isEmpty()) {
          continue;
        }
        List<Word> vārdi = Splitting.tokenize(locītājs, rinda);
        for (Word vārds : vārdi) {
          if (!vārds.isRecognized()) {
            System.err.printf("Neatpaz\u012bts v\u0101rds \'%s\' fr\u0101z\u0113 \'%s\'\n", vārds.getToken(), rinda);
          }
        }
      }
      ieeja.close();
    }
  }

  @Test public void lociishanas_lielie_burti() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("Valdis", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Valda");
    formas = locītājs.generateInflections("V\u012b\u0137e-Freiberga", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "V\u012b\u0137es-Freibergas");
    formas = locītājs.generateInflections("\u017dverelo-Freiberga", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "\u017dverelo-Freibergas");
    formas = locītājs.generateInflections("R\u012bga-Best", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "R\u012bga-Best");
    formas = locītājs.generateInflections("Best-R\u012bga", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Best-R\u012bgas");
    formas = locītājs.generateInflections("Rudaus-Rudovskis", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Rudaus-Rudovska");
    formas = locītājs.generateInflections("Pav\u013cuta-Deslandes", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "Pav\u013cutas-Deslandes");
  }

  @Test public void jaundzimushais() {
    Word w = locītājs.analyze("jaundzimu\u0161ajam");
    assertTrue(w.isRecognized());
    assertEquals(AttributeNames.v_Adjective, w.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
    assertEquals("jaundzimis", w.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    w = locītājs.analyze("jaundzimus\u012b");
    assertTrue(w.isRecognized());
    assertEquals(AttributeNames.v_Adjective, w.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
    assertEquals("jaundzimusi", w.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    w = locītājs.analyze("galvenajam");
    assertTrue(w.isRecognized());
    assertEquals(AttributeNames.v_Adjective, w.wordforms.get(0).getValue(AttributeNames.i_PartOfSpeech));
    assertEquals("galvenais", w.wordforms.get(0).getValue(AttributeNames.i_Lemma));
  }

  @Test public void guessinglimits() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = false;
    locītājs.guessVerbs = false;
    locītājs.guessNouns = true;
    locītājs.enableAllGuesses = true;
    Word w = locītājs.analyze("xxxbs");
    assertTrue(w.isRecognized());
    w = locītājs.analyze("xxxes");
    for (Wordform wf : w.wordforms) {
      assertFalse(wf.isMatchingStrong(AttributeNames.i_Declension, "1"));
    }
  }

  @Test public void izskanjas() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = false;
    locītājs.guessVerbs = true;
    locītājs.enableAllGuesses = true;
    Word austrumlatvija = locītājs.analyze("Austrumlatvija");
    assertTrue(austrumlatvija.isRecognized());
    Word w = locītājs.analyze("miru\u0161ais");
    assertTrue(w.isRecognized());
  }

  @Test public void inflect_garbage_collection() {
    locītājs.generateInflections("\u0160a\u0161liki");
    Word bulduri = locītājs.analyze("\u0160a\u0161liki");
    assertTrue(bulduri.isRecognized());
    for (Wordform wf : bulduri.wordforms) {
      assertEquals("\u0161a\u0161liks", wf.getValue(AttributeNames.i_Lemma));
    }
  }

  @Test public void mijas6dekl() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("acs", true);
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "acu");
    formas = locītājs.generateInflections("auss", true);
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "ausu");
    formas = locītājs.generateInflections("zoss", true);
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "zosu");
    formas = locītājs.generateInflections("dakts", true);
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "daktu");
    formas = locītājs.generateInflections("\u0161alts", true);
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "\u0161altu");
    formas = locītājs.generateInflections("maksts", true);
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "makstu");
  }

  @Ignore(value = "nav skaidra poz\u012bcija par vokat\u012bviem") @Test public void vokatiivi() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("koks", true);
    describe(formas);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "koks");
    formas = locītājs.generateInflections("pazi\u0146a", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "pazi\u0146a");
  }

  @Test public void kviesis() {
    Word w = locītājs.analyze("kvie\u0161i");
    assertTrue(w.isRecognized());
    assertEquals("kviesis", w.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    List<Wordform> formas = locītājs.generateInflections("kviesis", true);
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "kvie\u0161u");
  }

  @Test public void viesis() {
    Word w = locītājs.analyze("t\u0101lskatu");
    assertTrue(w.isRecognized());
    assertEquals("t\u0101lskatis", w.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    assertFalse(locītājs.analyze("t\u0101lska\u0161u").isRecognized());
    List<Wordform> formas = locītājs.generateInflections("viesis", true);
    assertNounInflection(formas, AttributeNames.v_Plural, AttributeNames.v_Genitive, "", "viesu");
  }

  @Test public void gljuki20140401() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("m\u0113ness", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "m\u0113ness");
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Vocative, "", "m\u0113nes");
  }

  @Test public void acronyms() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessAdjectives = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    List<Wordform> formas = locītājs.generateInflections("FMS", false);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Genitive, "", "FMS");
  }

  @Test public void rakiens() {
    Word w = locītājs.analyze("racis");
    assertTrue(w.isRecognized());
    assertEquals("rakt", w.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    w = locītājs.analyze("rakis");
    assertFalse(w.isRecognized());
    w = locītājs.analyze("veicis");
    assertTrue(w.isRecognized());
    assertEquals("veikt", w.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    List<Wordform> formas = locītājs.generateInflections("rakt", false);
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
    testset.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    assertInflection(formas, testset, "raciens");
  }

  @Test public void lecdams() {
    Word w = locītājs.analyze("l\u0113kdams");
    assertTrue(w.isRecognized());
    assertEquals("l\u0113kt", w.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    w = locītājs.analyze("l\u0113cdams");
    assertFalse(w.isRecognized());
    List<Wordform> formas = locītājs.generateInflections("l\u0113kt", false);
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    testset.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Participle);
    testset.addAttribute(AttributeNames.i_Lokaamiiba, AttributeNames.v_DaljeejiLokaams);
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
    testset.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    assertInflection(formas, testset, "l\u0113kdams");
  }

  @Test public void līstiiet() {
    Word w = locītājs.analyze("l\u012bstiet");
    assertTrue(w.isRecognized());
    assertEquals("l\u012bt", w.wordforms.get(0).getValue(AttributeNames.i_Lemma));
    w = locītājs.analyze("l\u012bstiiet");
    assertFalse(w.isRecognized());
    List<Wordform> formas = locītājs.generateInflections("l\u012bt", false);
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    testset.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Participle);
    testset.addAttribute(AttributeNames.i_Lokaamiiba, AttributeNames.v_DaljeejiLokaams);
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Nominative);
    testset.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    assertInflection(formas, testset, "l\u012bdams");
  }

  @Test public void apvidvārdi() {
    Word w = locītājs.analyze("\u012bst\u0101is");
    assertFalse(w.isRecognized());
  }

  @Test public void retie() {
    Word w = locītājs.analyze("ar\u0161ana");
    assertTrue(w.isRecognized());
    w = locītājs.analyze("ar");
    for (Wordform wf : w.wordforms) {
      assertFalse(wf.isMatchingStrong(AttributeNames.i_Lemma, "art"));
    }
  }

  @Test public void turlais() {
    locītājs.enableGuessing = true;
    locītājs.enableVocative = true;
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    locītājs.guessInflexibleNouns = true;
    locītājs.enableAllGuesses = true;
    Word w = locītājs.guessByEnding("turlais", "Turlais");
    assertTrue(w.isRecognized());
    for (Wordform wf : w.wordforms) {
      assertFalse(wf.isMatchingStrong(AttributeNames.i_Lemma, "art"));
    }
  }

  @Test public void apstākļa_vārdu_ģenerēšana() {
    List<Wordform> formas = locītājs.generateInflections("labi");
    assertEquals(1, formas.size());
  }

  @Test public void rozā() {
    List<Wordform> formas = locītājs.generateInflections("roz\u0101");
    assertEquals(1, formas.size());
    assertTrue(formas.get(0).isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adjective));
  }

  @Test public void reflexive_nouns() {
    Word klausītājies = locītājs.analyze("klaus\u012bt\u0101jies");
    assertTrue(klausītājies.isRecognized());
    Word vēlējumies = locītājs.analyze("v\u0113l\u0113jumies");
    assertTrue(vēlējumies.isRecognized());
    Word acīsskatīšanās = locītājs.analyze("ac\u012bsskat\u012b\u0161an\u0101s");
    assertTrue(acīsskatīšanās.isRecognized());
    Word pakaļdzinējies = locītājs.analyze("paka\u013cdzin\u0113jies");
    assertTrue(pakaļdzinējies.isRecognized());
  }

  @Test public void mijas_3_konj() {
    Word test = locītājs.analyze("m\u012bcu");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("m\u012bku");
    assertFalse(test.isRecognized());
    test = locītājs.analyze("m\u0101cu");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("m\u0101ku");
    assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("m\u0101c\u012bt"));
    test = locītājs.analyze("t\u016bcu");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("t\u016bku");
    assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("t\u016bc\u012bt"));
    test = locītājs.analyze("sacu");
    assertFalse(test.isRecognized());
    test = locītājs.analyze("saku");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("izsacos");
    assertFalse(test.isRecognized());
    test = locītājs.analyze("izsakos");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("slaucu");
    assertTrue(test.isRecognized());
    assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("slauc\u012bt"));
    assertTrue(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("slaukt"));
    test = locītājs.analyze("slauku");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("braucu");
    assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("brauc\u012bt"));
    test = locītājs.analyze("brauku");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("uzbraucu");
    assertFalse(test.getBestWordform().getValue(AttributeNames.i_Lemma).equalsIgnoreCase("uzbrauc\u012bt"));
    test = locītājs.analyze("uzbrauku");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("\u0146urcu");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("\u0146urku");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("murcu");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("murku");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("mocu");
    assertTrue(test.isRecognized());
    test = locītājs.analyze("moku");
    assertTrue(test.isRecognized());
  }

  @Test public void sēdošs() {
    Word sēdošs = locītājs.analyze("s\u0113do\u0161s");
    assertTrue(sēdošs.isRecognized());
  }

  @Test public void vajadzībasatstāstījuma() {
    Word jārokot = locītājs.analyze("j\u0101rokot");
    assertTrue(jārokot.isRecognized());
    assertEquals(AttributeNames.v_VajadziibasAtstaastiijuma, jārokot.wordforms.get(0).getValue(AttributeNames.i_Izteiksme));
  }

  @Test public void nelocīt() throws UnsupportedEncodingException {
    List<Wordform> formas = locītājs.generateInflections("xxx");
    assertEquals(0, formas.size());
    locītājs.guessVerbs = false;
    locītājs.guessParticiples = false;
    formas = locītājs.generateInflections("pav\u0101r\u0101ms");
    assertEquals(0, formas.size());
    formas = locītājs.generateInflections("nav");
    assertEquals(0, formas.size());
  }

  @Test public void locīt_ar_sliktu_paradigmu() {
    List<Wordform> formas = locītājs.generateInflectionsFromParadigm("v\u0101r\u0101ms", 16);
    assertTrue(true);
  }

  @Test public void adjektīviskā_deklinācija() {
    List<Wordform> formas = locītājs.generateInflections("m\u0113ness\u0113rdz\u012bgais", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "m\u0113ness\u0113rdz\u012bgajam");
    formas = locītājs.generateInflections("m\u0113ness\u0113rdz\u012bg\u0101", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "m\u0113ness\u0113rdz\u012bgajai");
    formas = locītājs.generateInflections("cietu\u0161ais", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "cietu\u0161ajam");
    formas = locītājs.generateInflections("cietus\u012b", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "cietu\u0161ajai");
    formas = locītājs.generateInflections("dzeramais", true);
    assertNounInflection(formas, AttributeNames.v_Singular, AttributeNames.v_Dative, "", "dzeramajam");
  }

  @Test public void inflect_hardcoded() {
    List<Wordform> formas = locītājs.generateInflections("b\u016bt");
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_Person, "3");
    testset.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Iisteniibas);
    testset.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Tagadne);
    assertInflection(formas, testset, "ir");
    formas = locītājs.generateInflections("vi\u0146\u0161");
    testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
    assertInflection(formas, testset, "vi\u0146am");
  }

  @Test public void simtas() {
    List<Wordform> formas = locītājs.generateInflections("simts");
    for (Wordform forma : formas) {
      if (forma.getToken().equalsIgnoreCase("simtas")) {
        forma.describe();
      }
      assertNotEquals("simtas", forma.getToken());
    }
    Word simtas = locītājs.analyze("simtas");
    assertFalse(simtas.isRecognized());
  }

  @Test public void krāties() {
    List<Wordform> formas = locītājs.generateInflections("kr\u0101ties");
    for (Wordform forma : formas) {
      assertNotEquals("kr\u0101os", forma.getToken());
    }
    Word krāos = locītājs.analyze("kr\u0101os");
    assertFalse(krāos.isRecognized());
  }

  @Test public void multistem_generateinflections() {
    List<Wordform> sairšana = locītājs.generateInflectionsFromParadigm("irt", 15, "ir", "irst", "ir");
    List<Wordform> laivas_iršana = locītājs.generateInflectionsFromParadigm("irt", 15, "ir", "ir", "\u012br");
    AttributeValues pagaatne = new AttributeValues();
    pagaatne.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    pagaatne.addAttribute(AttributeNames.i_Person, "3");
    pagaatne.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Iisteniibas);
    pagaatne.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Pagaatne);
    assertInflection(sairšana, pagaatne, "ira");
    assertInflection(laivas_iršana, pagaatne, "\u012bra");
    AttributeValues tagadne = new AttributeValues();
    tagadne.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    tagadne.addAttribute(AttributeNames.i_Person, "1");
    tagadne.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Iisteniibas);
    tagadne.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Tagadne);
    assertInflection(sairšana, tagadne, "irstu");
    assertInflection(laivas_iršana, tagadne, "iru");
  }

  @Test public void ļaudis() {
    AttributeValues attrs = new AttributeValues();
    attrs.addAttribute(AttributeNames.i_NumberSpecial, AttributeNames.v_PlurareTantum);
    attrs.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
    List<Wordform> formas = locītājs.generateInflectionsFromParadigm("\u013caudis", 11, attrs);
    for (Wordform forma : formas) {
      assertNotEquals("\u013caudiij", forma.getToken());
      assertFalse(forma.isMatchingStrong(AttributeNames.i_Number, AttributeNames.v_Singular));
      assertTrue(forma.isMatchingStrong(AttributeNames.i_Gender, AttributeNames.v_Masculine));
    }
    Word ļaudiij = locītājs.analyze("\u013caudiij");
    if (ļaudiij.isRecognized()) {
      ļaudiij.describe(System.out);
    }
    assertFalse(ļaudiij.isRecognized());
  }

  @Test public void griedt() {
    List<Wordform> formas = locītājs.generateInflections("griezt");
    for (Wordform forma : formas) {
      assertNotEquals("gried", forma.getToken());
      assertNotEquals("griediet", forma.getToken());
    }
    assertFalse(locītājs.analyze("gried").isRecognized());
    assertFalse(locītājs.analyze("griediet").isRecognized());
    assertTrue(locītājs.analyze("griez").isRecognized());
    assertTrue(locītājs.analyze("grieziet").isRecognized());
  }

  @Test public void lemmas2017mar() {
    assertLemma("izpau\u017eas", "izpausties");
    assertLemma("finan\u0161u", "finanses");
    assertLemma("t\u016bkstotim", "t\u016bkstotis");
    assertLemma("sl\u0113pjas", "sl\u0113pties");
    locītājs.enableGuessing = true;
    assertLemma("P\u0113tera", "P\u0113teris");
    assertLemma("NATO", "NATO");
    Word lībiešu = locītājs.analyze("l\u012bbie\u0161u");
    assertTrue(lībiešu.isRecognized());
    boolean foundLemma = false;
    for (Wordform wf : lībiešu.wordforms) {
      if (wf.isMatchingStrong(AttributeNames.i_Lemma, "l\u012bbietis")) {
        foundLemma = true;
      }
    }
    assertTrue(foundLemma);
  }

  @Test public void turpms() {
    locītājs.enableGuessing = true;
    Word turpmākiem = locītājs.analyze("turpm\u0101kiem");
    assertTrue(turpmākiem.isRecognized());
    assertLemma("turpm\u0101kiem", "turpm\u0101ks");
  }

  @Test public void pēdējajam() {
    List<Wordform> formas = locītājs.generateInflections("p\u0113d\u0113js");
    for (Wordform forma : formas) {
      if (forma.getToken().equalsIgnoreCase("p\u0113d\u0113jajam")) {
        describe(new LinkedList<Wordform>(Arrays.asList(forma)));
      }
      assertNotEquals("p\u0113d\u0113jajam", forma.getToken());
    }
    assertLemma("p\u0113d\u0113jam", "p\u0113d\u0113js");
    assertLemma("p\u0113d\u0113jajam", "p\u0113d\u0113js");
    assertLemma("visp\u0113d\u0113j\u0101kais", "p\u0113d\u0113js");
    assertLemma("visp\u0113d\u0113j\u0101kajam", "p\u0113d\u0113js");
  }

  @Test public void pase() {
    List<Wordform> pase = locītājs.generateInflections("pase");
    List<Wordform> kase = locītājs.generateInflections("kase");
    List<Wordform> rase = locītājs.generateInflections("rase");
    AttributeValues dskg = new AttributeValues();
    dskg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
    dskg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
    dskg.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
    assertInflection(pase, dskg, "pasu");
    assertInflection(kase, dskg, "kasu");
    assertInflection(rase, dskg, "rasu");
  }

  @Test public void frequencies() {
    assertTrue(locītājs.analyze("Kaspars").isRecognized());
    assertFalse(locītājs.analyze("Induls").isRecognized());
  }

  @Test public void divdabju_pakāpe() {
    Word ziedošs = locītājs.analyze("ziedo\u0161s");
    assertTrue(ziedošs.isRecognized());
    assertEquals(AttributeNames.v_Positive, ziedošs.getBestWordform().getValue(AttributeNames.i_Degree));
    assertEquals("vmnpdmsnapnpn", ziedošs.wordforms.get(0).getTag());
    Word ziedošāks = locītājs.analyze("ziedo\u0161\u0101ks");
    assertTrue(ziedošāks.isRecognized());
    assertEquals(AttributeNames.v_Comparative, ziedošāks.getBestWordform().getValue(AttributeNames.i_Degree));
    assertEquals("vmnpdmsnapncn", ziedošāks.wordforms.get(0).getTag());
    Word visziedošākais = locītājs.analyze("visziedo\u0161\u0101kais");
    assertTrue(visziedošākais.isRecognized());
    assertEquals(AttributeNames.v_Superlative, visziedošākais.getBestWordform().getValue(AttributeNames.i_Degree));
    assertEquals("vmnpdmsnapysn", visziedošākais.wordforms.get(0).getTag());
  }

  @Test public void balamute() {
    AttributeValues filter = new AttributeValues();
    filter.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
    List<Wordform> balamute = locītājs.generateInflections("balamute", false, filter);
    AttributeValues dskg = new AttributeValues();
    dskg.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
    dskg.addAttribute(AttributeNames.i_Number, AttributeNames.v_Plural);
    dskg.addAttribute(AttributeNames.i_Case, AttributeNames.v_Genitive);
    assertInflection(balamute, dskg, "balamutu");
    AttributeValues vskd = new AttributeValues();
    vskd.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun);
    vskd.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    vskd.addAttribute(AttributeNames.i_Case, AttributeNames.v_Dative);
    assertInflection(balamute, vskd, "balamutem");
  }

  @Test public void žirafe() {
    Word w = locītājs.analyze("\u017eirafu");
    assertTrue(w.isRecognized());
    w = locītājs.analyze("\u017eirafju");
    assertTrue(w.isRecognized());
  }

  @Test public void viszaļāk() {
    Word w = locītājs.analyze("visza\u013c\u0101k");
    assertTrue(w.isRecognized());
    List<Wordform> zaļš = locītājs.generateInflections("za\u013c\u0161");
    AttributeValues visp = new AttributeValues();
    visp.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Adverb);
    visp.addAttribute(AttributeNames.i_Degree, AttributeNames.v_Superlative);
    assertInflection(zaļš, visp, "visza\u013c\u0101k");
  }

  @Test public void iekosties() {
    List<Wordform> kost = locītājs.generateInflections("kost");
    AttributeValues tu = new AttributeValues();
    tu.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    tu.addAttribute(AttributeNames.i_Person, "2");
    tu.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Tagadne);
    tu.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    assertInflection(kost, tu, "kod");
    List<Wordform> izpausties = locītājs.generateInflections("izpausties");
    assertInflection(izpausties, tu, "izpaudies");
    List<Wordform> izlauzties = locītājs.generateInflections("izlauzties");
    assertInflection(izlauzties, tu, "izlauzies");
    Word w = locītājs.analyze("kod");
    assertTrue(w.isRecognized());
    w = locītājs.analyze("koz");
    assertFalse(w.isRecognized());
  }

  @Test public void aizkost() {
    List<Wordform> aizkost = locītājs.generateInflections("aizkost");
    AttributeValues tu = new AttributeValues();
    tu.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    tu.addAttribute(AttributeNames.i_Person, "2");
    tu.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Tagadne);
    tu.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    assertInflection(aizkost, tu, "aizkod");
    Word w = locītājs.analyze("aizkod");
    assertTrue(w.isRecognized());
    w = locītājs.analyze("aizkoz");
    assertFalse(w.isRecognized());
  }

  @Test public void jaundzimušākais() {
    Word w = locītājs.analyze("jaundzimu\u0161ais");
    assertTrue(w.isRecognized());
    w = locītājs.analyze("jaundzimu\u0161\u0101kais");
    assertFalse(w.isRecognized());
    w = locītājs.analyze("jaundzimu\u0161ajam");
    assertTrue(w.isRecognized());
    w = locītājs.analyze("jaundzimu\u0161\u0101kajam");
    assertFalse(w.isRecognized());
  }

  @Test public void guessAbbreviation() {
    Word w = locītājs.analyze("PZLK");
    assertFalse(w.isRecognized());
    locītājs.enableGuessing = true;
    w = locītājs.analyze("PZLK");
    assertTrue(w.isRecognized());
    boolean found = false;
    for (Wordform wf : w.wordforms) {
      if (wf.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Abbreviation)) {
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test public void guessInflexive() {
    Word w = locītājs.analyze("pluto");
    assertFalse(w.isRecognized());
    locītājs.enableGuessing = true;
    w = locītājs.analyze("pluto");
    assertTrue(w.isRecognized());
    boolean found = false;
    for (Wordform wf : w.wordforms) {
      if (wf.isMatchingStrong(AttributeNames.i_PartOfSpeech, AttributeNames.v_Noun)) {
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test public void zaļoksnējajā() {
    Word w = locītājs.analyze("za\u013coksn\u0113jaj\u0101");
    assertTrue(w.isRecognized());
  }

  @Test public void plāns_B() {
    Word w = locītājs.analyze("B");
    assertTrue(w.isRecognized());
    assertEquals("xx", w.getBestWordform().getTag());
  }

  @Test public void pelus() {
    AttributeValues attrs = new AttributeValues();
    attrs.addAttribute(AttributeNames.i_NumberSpecial, AttributeNames.v_PlurareTantum);
    attrs.addAttribute(AttributeNames.i_Gender, AttributeNames.v_Masculine);
    List<Wordform> pelus = locītājs.generateInflectionsFromParadigm("pelus", 31, attrs);
    assertNotEquals(0, pelus.size());
  }

  @Test public void sēžu() {
    AttributeValues testset = new AttributeValues();
    testset.addAttribute(AttributeNames.i_PartOfSpeech, AttributeNames.v_Verb);
    testset.addAttribute(AttributeNames.i_Izteiksme, AttributeNames.v_Iisteniibas);
    testset.addAttribute(AttributeNames.i_Laiks, AttributeNames.v_Tagadne);
    testset.addAttribute(AttributeNames.i_Number, AttributeNames.v_Singular);
    testset.addAttribute(AttributeNames.i_Person, "1");
    List<Wordform> sēdu = locītājs.generateInflectionsFromParadigm("s\u0113d\u0113t", 17);
    assertInflection(sēdu, testset, "s\u0113du");
    List<Wordform> sēžu = locītājs.generateInflectionsFromParadigm("s\u0113d\u0113t", 45);
    assertInflection(sēžu, testset, "s\u0113\u017eu");
    List<Wordform> aizsēdēties = locītājs.generateInflectionsFromParadigm("aizs\u0113d\u0113ties", 46);
    assertInflection(aizsēdēties, testset, "aizs\u0113\u017eos");
    testset.addAttribute(AttributeNames.i_Person, "2");
    assertInflection(aizsēdēties, testset, "aizs\u0113dies");
  }

  @Test public void roberts_20171110() {
    Word w = locītājs.analyze("!!!!");
    assertTrue(w.isRecognized());
    assertEquals("zs", w.getBestWordform().getTag());
    w = locītājs.analyze("!!!");
    assertTrue(w.isRecognized());
    assertEquals("zs", w.getBestWordform().getTag());
  }

  @Test public void manīm() {
    Word w = locītājs.analyze("man\u012bm");
    assertTrue(w.isRecognized());
    List<Wordform> formas = locītājs.generateInflections("es");
    for (Wordform forma : formas) {
      if (forma.getToken().equalsIgnoreCase("man\u012bm")) {
        describe(new LinkedList<Wordform>(Arrays.asList(forma)));
      }
      assertNotEquals("man\u012bm", forma.getToken());
    }
    formas = locītājs.generateInflections("tu");
    for (Wordform forma : formas) {
      if (forma.getToken().equalsIgnoreCase("tev\u012bm")) {
        describe(new LinkedList<Wordform>(Arrays.asList(forma)));
      }
      assertNotEquals("tev\u012bm", forma.getToken());
    }
  }

  @Test public void laura_20180614() {
    Word w = locītājs.analyze("ka");
    assertTrue(w.isRecognized());
    assertEquals("cs", w.getBestWordform().getTag());
    w = locītājs.analyze("ar\u012b");
    assertTrue(w.isRecognized());
    boolean found = false;
    for (Wordform f : w.wordforms) {
      if (f.getTag().equalsIgnoreCase("q")) {
        found = true;
      }
    }
    assertTrue("Nav \'ar\u012b\' k\u0101 partikula ar \'q\' tagu", found);
    w = locītājs.analyze("var");
    assertTrue(w.isRecognized());
    found = false;
    for (Wordform f : w.wordforms) {
      if (f.getTag().startsWith("vo")) {
        found = true;
      }
    }
    assertTrue("Nav \'var\' varianta ar \'vo...\' tagu", found);
    w = locītājs.analyze("nor\u0101d\u012bju\u0161i");
    assertTrue(w.isRecognized());
    assertTrue(w.getBestWordform().getTag() + " needs to end with pn", w.getBestWordform().getTag().endsWith("pn"));
  }
}