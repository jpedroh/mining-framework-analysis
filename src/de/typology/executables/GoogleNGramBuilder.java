package de.typology.executables;
import java.io.File;
import java.io.IOException;
import de.typology.nGramBuilder.NGramFromGoogleBuilder;
import de.typology.nGramBuilder.NGramNormalizer;
import de.typology.utils.Config;
import de.typology.utils.IOHelper;

public class GoogleNGramBuilder {
  /**
	 * executes the following steps:
	 * <p>
	 * 1) parse and normalize google ngram data
	 * <p>
	 * 
	 * @author Rene Pickhardt, Martin Koerner
	 * @throws IOException
	 */
  public static void main(String[] args) throws IOException {
    IOHelper.log("start building ngrams");
    NGramNormalizer ngn = new NGramNormalizer();
    File dir = new File(Config.get().googleInputDirectory);
    new File(Config.get().outputDirectory).mkdirs();
    for (File f : dir.listFiles()) {
      IOHelper.log(f.getAbsolutePath() + ":");
      String googleTyp = f.getName();
      String finalGoogle = outPath + "final/";

<<<<<<< /usr/src/app/output/renepickhardt/generalized-language-modeling-toolkit/2a4775b0e660f3488de5b6d15f1647436d0a5efb/src/de/typology/executables/GoogleNGramBuilder.java/left.java
      NGramFromGoogleBuilder.run(Config.get().outputDirectory + "/google/" + googleTyp + "/", Config.get().outputDirectory + "/google/" + googleTyp + "/" + "normalized/1/1gram-normalized.txt")
=======
      new File(finalGoogle).mkdirs()
>>>>>>> /usr/src/app/output/renepickhardt/generalized-language-modeling-toolkit/2a4775b0e660f3488de5b6d15f1647436d0a5efb/src/de/typology/executables/GoogleNGramBuilder.java/right.java
      ;
    }
  }
}