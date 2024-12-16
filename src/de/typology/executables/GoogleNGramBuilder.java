package de.typology.executables;

import de.typology.nGramBuilder.NGramFromGoogleBuilder;
import de.typology.nGramBuilder.NGramNormalizer;
import de.typology.utils.Config;
import de.typology.utils.IOHelper;
import java.io.File;
import java.io.IOException;


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
		// parse and normalize google ngram data:
		IOHelper.log("start building ngrams");
		NGramNormalizer ngn = new NGramNormalizer();
		File dir = new File(Config.get().googleInputDirectory);
		new File(Config.get().outputDirectory).mkdirs();
		for (File f : dir.listFiles()) {
			IOHelper.log(f.getAbsolutePath() + ":");
			// PARSE NGRAMS!
			String googleTyp = f.getName();
			//			String outPath = Config.get().outputDirectory + "google/"
			//					+ googleTyp + "/";
			//			String mergedGoogle = outPath+ "merged/";
			//			new File(mergedGoogle).mkdirs();
			//			if (Config.get().parseData) {
			//				NGramMergerMain.run(f.getAbsolutePath(), mergedGoogle);
			//				NGramParserMain.run(mergedGoogle,
			//						outPath);
			//			}
			//"/home/martin/out/google/ger/","/home/martin/out/google/ger/normalized/1/1gram-normalized.txt"
			NGramFromGoogleBuilder.run(((Config.get().outputDirectory + "/google/") + googleTyp) + "/", (((Config.get().outputDirectory + "/google/") + googleTyp) + "/") + "normalized/1/1gram-normalized.txt");
		}
	}
}