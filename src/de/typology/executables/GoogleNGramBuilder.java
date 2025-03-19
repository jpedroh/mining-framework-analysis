package de.typology.executables;
import java.io.File;
import java.io.IOException;
import de.typology.nGramBuilder.NGramFromGoogleBuilder;
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
    File dir = new File(Config.get().googleInputDirectory);
    new File(Config.get().outputDirectory).mkdirs();
    for (File f : dir.listFiles()) {
      IOHelper.log(f.getAbsolutePath() + ":");
      String googleTyp = f.getName();
      NGramMergerMain.run(f.getAbsolutePath(), mergedGoogle);

<<<<<<< /usr/src/app/output/renepickhardt/generalized-language-modeling-toolkit/b2b90c58b5c20fc9e7565e4336c575ab6f27684d/src/de/typology/executables/GoogleNGramBuilder.java/left.java
      NGramParserMain
=======
      NGramFromGoogleBuilder
>>>>>>> /usr/src/app/output/renepickhardt/generalized-language-modeling-toolkit/b2b90c58b5c20fc9e7565e4336c575ab6f27684d/src/de/typology/executables/GoogleNGramBuilder.java/right.java
      .run(
<<<<<<< /usr/src/app/output/renepickhardt/generalized-language-modeling-toolkit/b2b90c58b5c20fc9e7565e4336c575ab6f27684d/src/de/typology/executables/GoogleNGramBuilder.java/left.java
      mergedGoogle
=======
      Config.get().outputDirectory + "/google/" + googleTyp + "/"
>>>>>>> /usr/src/app/output/renepickhardt/generalized-language-modeling-toolkit/b2b90c58b5c20fc9e7565e4336c575ab6f27684d/src/de/typology/executables/GoogleNGramBuilder.java/right.java
      , 
<<<<<<< /usr/src/app/output/renepickhardt/generalized-language-modeling-toolkit/b2b90c58b5c20fc9e7565e4336c575ab6f27684d/src/de/typology/executables/GoogleNGramBuilder.java/left.java
      outPath
=======
      Config.get().outputDirectory + "/google/" + googleTyp + "/" + "normalized/1/1gram-normalized.txt"
>>>>>>> /usr/src/app/output/renepickhardt/generalized-language-modeling-toolkit/b2b90c58b5c20fc9e7565e4336c575ab6f27684d/src/de/typology/executables/GoogleNGramBuilder.java/right.java
      );
    }
  }
}