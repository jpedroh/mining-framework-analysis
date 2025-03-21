package edu.cmu.cs.lti.ark.fn.utils;
import edu.cmu.cs.lti.ark.util.nlp.Lemmatizer;
import edu.cmu.cs.lti.ark.util.nlp.MorphaLemmatizer;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.apache.commons.io.IOUtils.closeQuietly;

public class LemmatizeStuff {
  private static Lemmatizer lemmatizer = new MorphaLemmatizer();


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public static void main(String[] args) throws URISyntaxException, FileNotFoundException {
    CustomOptions options = new CustomOptions(args);
    if (options.isPresent(IN_FILE)) {
      infilename = options.get(IN_FILE);
    }
    if (options.isPresent(OUT_FILE)) {
      outfilename = options.get(OUT_FILE);
    }
    run();
  }
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/utils/LemmatizeStuff.java/right.java


  /**
	 * Reads sentences from infile, in the format
	 * n   word_1    ...   word_n    ...{other_stuff}...
	 * and writes them with their lemmatized versions appended to outfile in the format
	 * n   word_1    ...   word_n    ...{other_stuff}...   lemma_1   ...   lemma_n
	 *
	 * @param inFilename path to a file containing the input sentences
	 * @param outFilename path to file to which to write
	 */
  public static void lemmatize(String inFilename, String outFilename) throws FileNotFoundException {
    Scanner sc = new Scanner(new FileInputStream(inFilename));
    PrintStream ps = new PrintStream(new FileOutputStream(outFilename));
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      ps.print(line + "\t");
      String[] tokens = line.trim().split("\\s");
      int sentLen = Integer.parseInt(tokens[0]);
      for (int i = 0; i < sentLen; i++) {
        String lemma = lemmatizer.getLemma(tokens[i + 1], tokens[i + 1 + sentLen]);
        ps.print(lemma + "\t");
      }
      ps.println();
    }
    sc.close();
    closeQuietly(ps);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private static void run() throws FileNotFoundException {
    Scanner sc = new Scanner(new FileInputStream(infilename));
    PrintStream ps = new PrintStream(new FileOutputStream(outfilename));
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      ps.print(line + "\t");
      String[] toks = line.trim().split("\\s");
      int sentLen = Integer.parseInt(toks[0]);
      for (int i = 0; i < sentLen; i++) {
        String lemma = lemmatizer.getLemma(toks[i + 1].toLowerCase(), toks[i + 1 + sentLen]);
        ps.print(lemma + "\t");
      }
      ps.println();
    }
    sc.close();
    closeQuietly(ps);
  }
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/utils/LemmatizeStuff.java/right.java
}