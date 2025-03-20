package org.assertj.core.util.diff;
import org.assertj.core.util.diff.myers.MyersDiff;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copy from https://code.google.com/p/java-diff-utils/.
 * <p>
 * Implements the difference and patching engine
 * 
 * @author <a href="dm.naumenko@gmail.com">Dmitry Naumenko</a>
 */
public class DiffUtils {
  private static Pattern unifiedDiffChunkRe = Pattern.compile("^@@\\s+-(?:(\\d+)(?:,(\\d+))?)\\s+\\+(?:(\\d+)(?:,(\\d+))?)\\s+@@$");

  /**
   * Computes the difference between the original and revised list of elements
   * with default diff algorithm
   * 
   * @param original
   *            The original text. Must not be {@code null}.
   * @param revised
   *            The revised text. Must not be {@code null}.
   * @return The patch describing the difference between the original and
   *         revised sequences. Never {@code null}.
   */
  public static <T extends java.lang.Object> Patch<T> diff(List<T> original, List<T> revised) {
    return DiffUtils.diff(original, revised, new MyersDiff<T>());
  }

  /**
   * Computes the difference between the original and revised list of elements
   * with default diff algorithm
   *
   * @param original
   *            The original text. Must not be {@code null}.
   * @param revised
   *            The revised text. Must not be {@code null}.
   * @param algorithm
   *            The diff algorithm. Must not be {@code null}.
   * @return The patch describing the difference between the original and
   *         revised sequences. Never {@code null}.
   */
  public static <T extends java.lang.Object> Patch<T> diff(List<T> original, List<T> revised, DiffAlgorithm<T> algorithm) {
    if (original == null) {
      throw new IllegalArgumentException("original must not be null");
    }
    if (revised == null) {
      throw new IllegalArgumentException("revised must not be null");
    }
    if (algorithm == null) {
      throw new IllegalArgumentException("algorithm must not be null");
    }
    return algorithm.diff(original, revised);
  }

  /**
   * Patch the original text with given patch
   *
   * @param original
   *            the original text
   * @param patch
   *            the given patch
   * @return the revised text
   * @throws IllegalStateException
   *             if can't apply patch
   */
  public static <T extends java.lang.Object> List<T> patch(List<T> original, Patch<T> patch) throws IllegalStateException {
    return patch.applyTo(original);
  }

  /**
   * Parse the given text in unified format and creates the list of deltas for
   * it.
   *
   * @param diff
   *            the text in unified format
   * @return the patch with deltas.
   */
  public static Patch<String> parseUnifiedDiff(List<String> diff) {
    boolean inPrelude = true;
    List<String[]> rawChunk = new ArrayList<>();
    Patch<String> patch = new Patch<>();
    int old_ln = 0, new_ln = 0;
    String tag;
    String rest;
    for (String line : diff) {
      if (inPrelude) {
        if (line.startsWith("+++")) {
          inPrelude = false;
        }
        continue;
      }
      Matcher m = unifiedDiffChunkRe.matcher(line);
      if (m.find()) {
        if (rawChunk.size() != 0) {
          List<String> oldChunkLines = new ArrayList<>();
          List<String> newChunkLines = new ArrayList<>();
          for (String[] raw_line : rawChunk) {
            tag = raw_line[0];
            rest = raw_line[1];
            if (tag.equals(" ") || tag.equals("-")) {
              oldChunkLines.add(rest);
            }
            if (tag.equals(" ") || tag.equals("+")) {
              newChunkLines.add(rest);
            }
          }
          patch.addDelta(new ChangeDelta<>(new Chunk<>(old_ln - 1, oldChunkLines), new Chunk<>(new_ln - 1, newChunkLines)));
          rawChunk.clear();
        }
        old_ln = m.group(1) == null ? 1 : Integer.parseInt(m.group(1));
        new_ln = m.group(3) == null ? 1 : Integer.parseInt(m.group(3));
        if (old_ln == 0) {
          old_ln += 1;
        }
        if (new_ln == 0) {
          new_ln += 1;
        }
      } else {
        if (line.length() > 0) {
          tag = line.substring(0, 1);
          rest = line.substring(1);
          if (tag.equals(" ") || tag.equals("+") || tag.equals("-")) {
            rawChunk.add(new String[] { tag, rest });
          }
        } else {
          rawChunk.add(new String[] { " ", "" });
        }
      }
    }
    if (rawChunk.size() != 0) {
      List<String> oldChunkLines = new ArrayList<>();
      List<String> newChunkLines = new ArrayList<>();
      for (String[] raw_line : rawChunk) {
        tag = raw_line[0];
        rest = raw_line[1];
        if (tag.equals(" ") || tag.equals("-")) {
          oldChunkLines.add(rest);
        }
        if (tag.equals(" ") || tag.equals("+")) {
          newChunkLines.add(rest);
        }
      }
      patch.addDelta(new ChangeDelta<>(new Chunk<>(old_ln - 1, oldChunkLines), new Chunk<>(new_ln - 1, newChunkLines)));
      rawChunk.clear();
    }
    return patch;
  }

  /**
   * generateUnifiedDiff takes a Patch and some other arguments, returning the
   * Unified Diff format text representing the Patch.
   * 
   * @param original Filename of the original (unrevised file)
   * @param revised Filename of the revised file
   * @param originalLines Lines of the original file
   * @param patch Patch created by the diff() function
   * @param contextSize number of lines of context output around each difference
   *                    in the file.
   * @return List of strings representing the Unified Diff representation of
   *         the Patch argument.
   */
  public static List<String> generateUnifiedDiff(String original, String revised, List<String> originalLines, Patch<String> patch, int contextSize) {
    if (!patch.getDeltas().isEmpty()) {
      List<String> ret = new ArrayList<>();
      ret.add("--- " + original);
      ret.add("+++ " + revised);
      List<Delta<String>> patchDeltas = new ArrayList<>(patch.getDeltas());
      List<Delta<String>> deltas = new ArrayList<>();
      Delta<String> delta = patchDeltas.get(0);
      deltas.add(delta);
      if (patchDeltas.size() > 1) {
        for (int i = 1; i < patchDeltas.size(); i++) {
          int position = delta.getOriginal().getPosition();
          Delta<String> nextDelta = patchDeltas.get(i);
          if ((position + delta.getOriginal().size() + contextSize) >= (nextDelta.getOriginal().getPosition() - contextSize)) {
            deltas.add(nextDelta);
          } else {
            List<String> curBlock = processDeltas(originalLines, deltas, contextSize);
            ret.addAll(curBlock);
            deltas.clear();
            deltas.add(nextDelta);
          }
          delta = nextDelta;
        }
      }
      List<String> curBlock = processDeltas(originalLines, deltas, contextSize);
      ret.addAll(curBlock);
      return ret;
    }
    return new ArrayList<>();
  }

  /**
   * processDeltas takes a list of Deltas and outputs them together in a
   * single block of Unified-Diff-format text.
   * 
   * @param origLines the lines of the original file
   * @param deltas the Deltas to be output as a single block
   * @param contextSize the number of lines of context to place around block
   */
  private static List<String> processDeltas(List<String> origLines, List<Delta<String>> deltas, int contextSize) {
    List<String> buffer = new ArrayList<>();
    int origTotal = 0;
    int revTotal = 0;
    int line;
    Delta<String> curDelta = deltas.get(0);
    int origStart = curDelta.getOriginal().getPosition() + 1 - contextSize;
    if (origStart < 1) {
      origStart = 1;
    }
    int revStart = curDelta.getRevised().getPosition() + 1 - contextSize;
    if (revStart < 1) {
      revStart = 1;
    }
    int contextStart = curDelta.getOriginal().getPosition() - contextSize;
    if (contextStart < 0) {
      contextStart = 0;
    }
    for (line = contextStart; line < curDelta.getOriginal().getPosition(); line++) {
      buffer.add(" " + origLines.get(line));
      origTotal++;
      revTotal++;
    }
    buffer.addAll(getDeltaText(curDelta));
    origTotal += curDelta.getOriginal().getLines().size();
    revTotal += curDelta.getRevised().getLines().size();
    int deltaIndex = 1;
    while (deltaIndex < deltas.size()) {
      Delta<String> nextDelta = deltas.get(deltaIndex);
      int intermediateStart = curDelta.getOriginal().getPosition() + curDelta.getOriginal().getLines().size();
      for (line = intermediateStart; line < nextDelta.getOriginal().getPosition(); line++) {
        buffer.add(" " + origLines.get(line));
        origTotal++;
        revTotal++;
      }
      buffer.addAll(getDeltaText(nextDelta));
      origTotal += nextDelta.getOriginal().getLines().size();
      revTotal += nextDelta.getRevised().getLines().size();
      curDelta = nextDelta;
      deltaIndex++;
    }
    contextStart = curDelta.getOriginal().getPosition() + curDelta.getOriginal().getLines().size();
    for (line = contextStart; (line < (contextStart + contextSize)) && (line < origLines.size()); line++) {
      buffer.add(" " + origLines.get(line));
      origTotal++;
      revTotal++;
    }
    String header = "@@ -" + origStart + "," + origTotal + " +" + revStart + "," + revTotal + " @@";
    buffer.add(0, header);
    return buffer;
  }

  /**
   * getDeltaText returns the lines to be added to the Unified Diff text from
   * the Delta parameter
   * 
   * @param delta the Delta to output
   * @return list of String lines of code.
   */
  private static List<String> getDeltaText(Delta<String> delta) {
    List<String> buffer = new ArrayList<>();
    for (String original : delta.getOriginal().getLines()) {
      buffer.add("-" + original);
    }
    for (String original : delta.getRevised().getLines()) {
      buffer.add("+" + original);
    }
    return buffer;
  }
}