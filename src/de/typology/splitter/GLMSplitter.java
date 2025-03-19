package de.typology.splitter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import de.typology.utils.Config;
import de.typology.utils.IOHelper;
import de.typology.utils.SystemHelper;

/**
 * 
 * @author Martin Koerner
 * 
 */
public class GLMSplitter extends Splitter {
  protected String extension;

  public GLMSplitter(String directory, String indexName, String statsName, String inputName) {
    super(directory, indexName, statsName, inputName, "glm");
  }

  /**
	 * @param args
	 */
  public static void main(String[] args) {
    String outputDirectory = Config.get().outputDirectory + Config.get().inputDataSet;
    GLMSplitter ts = new GLMSplitter(outputDirectory, "index.txt", "stats.txt", "training.txt");
    ts.split(5);
  }

  @Override public void split(int maxSequenceLength) {
    for (int sequenceDecimal = 1; sequenceDecimal < Math.pow(2, maxSequenceLength); sequenceDecimal++) {
      String sequenceBinary = Integer.toBinaryString(sequenceDecimal);
      this.extension = sequenceBinary;
      IOHelper.strongLog("splitting into " + this.extension);
      this.initialize(this.extension);
      while (this.getNextSequence(sequenceBinary.length())) {
        String[] sequenceCut = new String[Integer.bitCount(sequenceDecimal)];
        char[] sequenceChars = sequenceBinary.toCharArray();
        int sequencePointer = 0;
        for (int i = 0; i < sequenceChars.length; i++) {
          if (Character.getNumericValue(sequenceChars[i]) == 1) {
            sequenceCut[sequencePointer] = this.sequence[i];
            sequencePointer++;
          }
        }
        BufferedWriter writer = this.getWriter(sequenceCut[0]);
        try {
          for (String sequenceCutWord : sequenceCut) {
            writer.write(sequenceCutWord + "\t");
          }
          writer.write(this.sequenceCount + "\n");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      this.reset();
      this.sortAndAggregate(this.outputDirectory.getAbsolutePath() + "/" + this.extension);
    }
  }

  @Override protected void mergeSmallestType(String inputPath) {
    File inputFile = new File(inputPath);
    if (Integer.bitCount(Integer.parseInt(inputFile.getName(), 2)) == 1) {
      File[] files = inputFile.listFiles();
      String fileExtension = inputFile.getName();
      IOHelper.log("merge all " + fileExtension);
      SystemHelper.runUnixCommand("cat " + files[0].getParent() + "/* > " + inputPath + "/all." + fileExtension);
      for (File file : files) {
        if (!file.getName().equals("all." + fileExtension)) {
          file.delete();
        }
      }
    }
  }
}