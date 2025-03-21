package com.technophobia.substeps.runner.syntax;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.parser.FileContents;
import com.technophobia.substeps.model.exception.DuplicatePatternException;

/**
 * @author ian
 * 
 */
public class SubStepDefinitionParser {
  private final Logger log = LoggerFactory.getLogger(SubStepDefinitionParser.class);

  private ParentStep currentParentStep;

  private final PatternMap<ParentStep> parentMap = new PatternMap<ParentStep>();

  private final boolean failOnDuplicateSubsteps;

  private final SyntaxErrorReporter syntaxErrorReporter;

  private FileContents currentFileContents;

  public SubStepDefinitionParser(final SyntaxErrorReporter syntaxErrorReporter) {
    this(true, syntaxErrorReporter);
  }

  public SubStepDefinitionParser(final boolean failOnDuplicateSubsteps, final SyntaxErrorReporter syntaxErrorReporter) {
    this.failOnDuplicateSubsteps = failOnDuplicateSubsteps;
    this.syntaxErrorReporter = syntaxErrorReporter;
  }

  void parseSubStepFile(final File substepFile) {
    this.currentFileContents = new FileContents();
    try {
      this.currentFileContents.readFile(substepFile);
      final List<String> lines = this.currentFileContents.getLines();
      for (int i = 0; i < this.currentFileContents.getNumberOfLines(); i++) {
        processLine(i);
      }
      if (this.currentParentStep != null) {
        if (this.currentParentStep.getSteps() != null && !this.currentParentStep.getSteps().isEmpty()) {
          try {
            storeForPatternOrThrowException(this.currentParentStep.getParent().getPattern(), this.currentParentStep);
          } catch (final DuplicatePatternException ex) {
            syntaxErrorReporter.reportSubstepsError(ex);
            if (failOnDuplicateSubsteps) {
              throw ex;
            }
          }
        } else {
          this.log.warn("Ignoring substep definition [" + this.currentParentStep.getParent().getLine() + "] as it has no steps");
        }
        this.currentParentStep = null;
      }
    } catch (final FileNotFoundException e) {
      this.log.error(e.getMessage(), e);
    } catch (final IOException e) {
      this.log.error(e.getMessage(), e);
    }
  }

  public PatternMap<ParentStep> loadSubSteps(final File definitions) {
    final List<File> substepsFiles = FileUtils.getFiles(definitions, ".substeps");
    for (final File f : substepsFiles) {
      parseSubStepFile(f);
    }
    return this.parentMap;
  }

  private void processLine(final int lineNumberIdx) {
    final String line = this.currentFileContents.getLineAt(lineNumberIdx);
    if (this.log.isTraceEnabled()) {
      this.log.trace("substep line[" + line + "] @ " + lineNumberIdx + ":" + this.currentFileContents.getFile().getName());
    }
    if (line != null && line.length() > 0) {
      final String trimmed = line.trim();
      if (trimmed.length() > 0 && !trimmed.startsWith("#")) {
        processTrimmedLine(trimmed, lineNumberIdx);
      }
    }
  }

  private void processTrimmedLine(final String trimmed, final int lineNumberIdx) {
    final int scolon = trimmed.indexOf(':');
    boolean lineProcessed = false;
    if (scolon > 0) {
      final String word = trimmed.substring(0, scolon);
      final String remainder = trimmed.substring(scolon + 1);
      final Directive d = isDirective(word);
      if (d != null) {
        final String trimmedRemainder = remainder.trim();
        if (!Strings.isNullOrEmpty(trimmedRemainder)) {
          processDirective(d, remainder, lineNumberIdx);
          lineProcessed = true;
        }
      }
    }
    if (!lineProcessed) {
      if (this.currentParentStep != null) {
        final int sourceOffset = this.currentFileContents.getSourceStartOffsetForLineIndex(lineNumberIdx);
        this.currentParentStep.addStep(new Step(trimmed, true, this.currentFileContents.getFile(), lineNumberIdx + 1, sourceOffset));
      }
    }
  }

  private void processDirective(final Directive d, final String remainder, final int lineNumberIdx) {
    this.currentDirective = d;
    switch (this.currentDirective) {
      case DEFINITION:
      {
        final int sourceOffset = this.currentFileContents.getSourceStartOffsetForLineIndex(lineNumberIdx);
        final Step parent = new Step(remainder, true, this.currentFileContents.getFile(), lineNumberIdx + 1, sourceOffset);
        if (this.currentParentStep != null) {
          final String newPattern = this.currentParentStep.getParent().getPattern();
          try {
            storeForPatternOrThrowException(newPattern, this.currentParentStep);
          } catch (final DuplicatePatternException ex) {
            syntaxErrorReporter.reportSubstepsError(ex);
            if (failOnDuplicateSubsteps) {
              throw ex;
            }
          }

<<<<<<< /usr/src/app/output/technophobia/substeps-core/f51e19b36c2a13d3d11fe9744381a0c910d61c6f/src/main/java/com/technophobia/substeps/runner/syntax/SubStepDefinitionParser.java/left.java
          storeForPatternOrReportFailure(this.currentFileContents.getFile(), newPattern, this.currentParentStep);
=======
>>>>>>> Unknown file: This is a bug in JDime.
        }
        this.currentParentStep = new ParentStep(parent);
        break;
      }
      default:
    }
  }


<<<<<<< /usr/src/app/output/technophobia/substeps-core/f51e19b36c2a13d3d11fe9744381a0c910d61c6f/src/main/java/com/technophobia/substeps/runner/syntax/SubStepDefinitionParser.java/left.java
  private void storeForPatternOrReportFailure(final File source, final String newPattern, final ParentStep parentStep) {
    try {
      storeParentStepForPattern(newPattern, parentStep);
    } catch (final RuntimeException ex) {
      this.syntaxErrorReporter.reportSubstepsError(source, parentStep.getParent().getLine(), parentStep.getParent().getSourceLineNumber(), ex.getMessage(), ex);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  private void storeForPatternOrThrowException(final String newPattern, final ParentStep parentStep) throws DuplicatePatternException {
    if (!this.parentMap.containsPattern(newPattern)) {
      this.parentMap.put(newPattern, parentStep);
    } else {
      throw new DuplicatePatternException(newPattern, parentMap.getValueForPattern(newPattern), parentStep);
    }
  }

  private static enum Directive {
    DEFINITION("Define")
    ;

    Directive(final String name) {
      this.name = name;
    }

    private final String name;
  }

  private Directive currentDirective = null;

  private Directive isDirective(final String word) {
    Directive rtn = null;
    for (final Directive d : Directive.values()) {
      if (word.equalsIgnoreCase(d.name)) {
        rtn = d;
        break;
      }
    }
    return rtn;
  }
}