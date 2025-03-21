package com.technophobia.substeps.runner.syntax.validation.fake;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.technophobia.substeps.runner.syntax.SyntaxErrorReporter;
import com.technophobia.substeps.model.exception.StepImplementationException;
import com.technophobia.substeps.model.exception.SubstepsParsingException;

public class FakeSyntaxErrorReporter implements SyntaxErrorReporter {


    public FakeSyntaxErrorReporter() {
        this.syntaxErrors = new ArrayList<FakeSyntaxErrorReporter.SyntaxErrorData>();
        this.stepErrors = new ArrayList<FakeSyntaxErrorReporter.StepImplErrorData>();
    }


<<<<<<< /usr/src/app/output/technophobia/substeps-core/f51e19b36c2a13d3d11fe9744381a0c910d61c6f/src/test/java/com/technophobia/substeps/runner/syntax/validation/fake/FakeSyntaxErrorReporter.java/left.java
    public void reportFeatureError(final File file, final String line,
            final int lineNumber, final String description)
            throws RuntimeException {
        this.errors.add(new SyntaxErrorData(true, file, line, lineNumber,
                description));
    }
||||||| /usr/src/app/output/technophobia/substeps-core/f51e19b36c2a13d3d11fe9744381a0c910d61c6f/src/test/java/com/technophobia/substeps/runner/syntax/validation/fake/FakeSyntaxErrorReporter.java/base.java
=======
    public void reportFeatureError(final File file, final String line, final int lineNumber, final String description)
            throws RuntimeException {
        syntaxErrors.add(new SyntaxErrorData(true, file, line, lineNumber, description));
    }
>>>>>>> /usr/src/app/output/technophobia/substeps-core/f51e19b36c2a13d3d11fe9744381a0c910d61c6f/src/test/java/com/technophobia/substeps/runner/syntax/validation/fake/FakeSyntaxErrorReporter.java/right.java


<<<<<<< /usr/src/app/output/technophobia/substeps-core/f51e19b36c2a13d3d11fe9744381a0c910d61c6f/src/test/java/com/technophobia/substeps/runner/syntax/validation/fake/FakeSyntaxErrorReporter.java/left.java
    public void reportFeatureError(final File file, final String line,
            final int lineNumber, final String description,
            final RuntimeException ex) throws RuntimeException {
        this.errors.add(new SyntaxErrorData(true, file, line, lineNumber,
                description));
    }
||||||| /usr/src/app/output/technophobia/substeps-core/f51e19b36c2a13d3d11fe9744381a0c910d61c6f/src/test/java/com/technophobia/substeps/runner/syntax/validation/fake/FakeSyntaxErrorReporter.java/base.java
=======
    public void reportFeatureError(final File file, final String line, final int lineNumber, final String description,
            final RuntimeException ex) throws RuntimeException {
        syntaxErrors.add(new SyntaxErrorData(true, file, line, lineNumber, description));
    }
>>>>>>> /usr/src/app/output/technophobia/substeps-core/f51e19b36c2a13d3d11fe9744381a0c910d61c6f/src/test/java/com/technophobia/substeps/runner/syntax/validation/fake/FakeSyntaxErrorReporter.java/right.java


    public void reportSubstepsError(final File file, final String line,
            final int lineNumber, final String description)
            throws RuntimeException {
        this.errors.add(new SyntaxErrorData(false, file, line, lineNumber,
                description));
    }


    public void reportSubstepsError(final File file, final String line,
            final int lineNumber, final String description,
            final RuntimeException ex) throws RuntimeException {
        this.errors.add(new SyntaxErrorData(false, file, line, lineNumber,
                description));
    }


    public List<SyntaxErrorData> errors() {
        Collections.sort(this.errors);
        return this.errors;
    }


    public static class SyntaxErrorData implements Comparable<SyntaxErrorData> {
        private final boolean isFeature;
        private final File file;
        private final String line;
        private final int lineNumber;
        private final String description;


        public SyntaxErrorData(final boolean isFeature, final File file,
                final String line, final int lineNumber,
                final String description) {
            this.isFeature = isFeature;
            this.file = file;
            this.line = line;
            this.lineNumber = lineNumber;
            this.description = description;
        }


        public boolean isFeature() {
            return this.isFeature;
        }


        public File getFile() {
            return this.file;
        }


        public String getLine() {
            return this.line;
        }


        public int getLineNumber() {
            return this.lineNumber;
        }


        public String getDescription() {
            return this.description;
        }


        public int compareTo(final SyntaxErrorData other) {
            return this.lineNumber - other.lineNumber;
        }
    }


    private final List<SyntaxErrorData> syntaxErrors;


    private final List<StepImplErrorData> stepErrors;


    public void reportSubstepsError(final SubstepsParsingException ex) {
        syntaxErrors.add(new SyntaxErrorData(false, ex.getFile(), ex.getLine(), ex.getLineNumber(), ex.getMessage()));
    }


    public void reportStepImplError(final StepImplementationException ex) {
        stepErrors.add(new StepImplErrorData(ex.getImplementingClass(), ex.getImplementingMethod().getName(), ex
                .getMessage()));
    }


    public List<SyntaxErrorData> syntaxErrors() {
        Collections.sort(syntaxErrors);
        return syntaxErrors;
    }


    public List<StepImplErrorData> stepImplErrors() {
        Collections.sort(stepImplErrors());
        return stepErrors;
    }


    private static class StepImplErrorData implements Comparable<StepImplErrorData> {

        private final Class<?> clazz;
        private final String methodName;
        private final String description;

        public StepImplErrorData(final Class<?> clazz, final String methodName, final String description) {
            this.clazz = clazz;
            this.methodName = methodName;
            this.description = description;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getDescription() {
            return description;
        }

        public int compareTo(final StepImplErrorData other) {
            final int result = clazz.getName().compareTo(other.clazz.getName());

            if (result != 0) {
                return result;
            }
            return methodName.compareTo(other.methodName);
        }
    }

}
