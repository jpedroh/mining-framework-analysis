package com.pablissimo.sonar;

import java.util.Map;
import java.util.Set;

import org.sonar.api.batch.BatchSide;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;

<<<<<<< /usr/src/app/output/pablissimo/sonartsplugin/5985fb7361c4b21cf789a9f5d89276afaa49674a/src/main/java/com/pablissimo/sonar/TsCoverageSensor.java/left.java
    private static final Logger LOG = LoggerFactory.getLogger(TsCoverageSensor.class);

    private final FileSystem moduleFileSystem;
    private final FilePredicates filePredicates;
    private final Settings settings;

    public TsCoverageSensor(FileSystem moduleFileSystem, Settings settings) {
        this.moduleFileSystem = moduleFileSystem;
        this.filePredicates = moduleFileSystem.predicates();
        this.settings = settings;
    }

    public boolean shouldExecuteOnProject(Project project) {
        return moduleFileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_KEY)).iterator().hasNext();
    }

    public void analyse(Project project, SensorContext context) {
        if (isLCOVReportProvided()) {
            saveMeasureFromLCOVFile(project, context);

        } else if (isForceZeroCoverageActivated()) {
            saveZeroValueForAllFiles(project, context);
        }

        // Else, nothing to do, there will be no coverage information for JavaScript files.
    }

    protected void saveZeroValueForAllFiles(Project project, SensorContext context) {
        for (File file : moduleFileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_KEY))) {
            saveZeroValueForResource(this.fileFromIoFile(file, project), context);
        }
    }

    protected void saveMeasureFromLCOVFile(Project project, SensorContext context) {
        String providedPath = settings.getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH);
        File lcovFile = getIOFile(moduleFileSystem.baseDir(), providedPath);

        if (!lcovFile.isFile()) {
            LOG.warn("No coverage information will be saved because LCOV file cannot be analysed. Provided LCOV file path: {}", providedPath);
            return;
        }

        LOG.info("Analysing {}", lcovFile);

        LCOVParser parser = getParser(moduleFileSystem.baseDir());
        Map<String, CoverageMeasuresBuilder> coveredFiles = parser.parseFile(lcovFile);

        for (File file : moduleFileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_KEY))) {
            try {
                CoverageMeasuresBuilder fileCoverage = coveredFiles.get(file.getAbsolutePath());
                org.sonar.api.resources.File resource = this.fileFromIoFile(file, project);

                if (fileCoverage != null) {
                    for (Measure measure : fileCoverage.createMeasures()) {
                        context.saveMeasure(resource, measure);
                    }
                } else {
                    // colour all lines as not executed
                    saveZeroValueForResource(resource, context);
                }
            } catch (Exception e) {
                LOG.error("Problem while calculating coverage for " + file.getAbsolutePath(), e);
            }
        }
    }

    protected org.sonar.api.resources.File fileFromIoFile(java.io.File file, Project project) {
        return org.sonar.api.resources.File.fromIOFile(file, project);
    }

    protected LCOVParser getParser(File baseDirectory) {
        return new LCOVParserImpl(baseDirectory);
    }

    private void saveZeroValueForResource(org.sonar.api.resources.File resource, SensorContext context) {
        PropertiesBuilder<Integer, Integer> lineHitsData = new PropertiesBuilder<>(CoreMetrics.COVERAGE_LINE_HITS_DATA);

        Measure<Integer> measure = context.getMeasure(resource, CoreMetrics.LINES);

        if(measure == null){
            LOG.info("measure == null for {}", resource.getPath());
            return;
        }

        for (int x = 1; x < measure.getIntValue(); x++) {
            lineHitsData.add(x, 0);
        }

        // use non comment lines of code for coverage calculation
        Measure ncloc = context.getMeasure(resource, CoreMetrics.NCLOC);
        context.saveMeasure(resource, lineHitsData.build());
        context.saveMeasure(resource, CoreMetrics.LINES_TO_COVER, ncloc.getValue());
        context.saveMeasure(resource, CoreMetrics.UNCOVERED_LINES, ncloc.getValue());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private boolean isForceZeroCoverageActivated() {
        return settings.getBoolean(TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE);
    }

    private boolean isLCOVReportProvided() {
        return StringUtils.isNotBlank(settings.getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH));
    }

    /**
     * Returns a java.io.File for the given path.
     * If path is not absolute, returns a File with module base directory as parent path.
     */
    public File getIOFile(File baseDir, String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(baseDir, path);
        }

        return file;
    }
||||||| /usr/src/app/output/pablissimo/sonartsplugin/5985fb7361c4b21cf789a9f5d89276afaa49674a/src/main/java/com/pablissimo/sonar/TsCoverageSensor.java/base.java
    private static final Logger LOG = LoggerFactory.getLogger(TsCoverageSensor.class);

    private final FileSystem moduleFileSystem;
    private final FilePredicates filePredicates;
    private final Settings settings;

    public TsCoverageSensor(FileSystem moduleFileSystem, Settings settings) {
        this.moduleFileSystem = moduleFileSystem;
        this.filePredicates = moduleFileSystem.predicates();
        this.settings = settings;
    }

    public boolean shouldExecuteOnProject(Project project) {
        return moduleFileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_KEY)).iterator().hasNext();
    }

    public void analyse(Project project, SensorContext context) {
        if (isLCOVReportProvided()) {
            saveMeasureFromLCOVFile(project, context);

        } else if (isForceZeroCoverageActivated()) {
            saveZeroValueForAllFiles(project, context);
        }

        // Else, nothing to do, there will be no coverage information for JavaScript files.
    }

    protected void saveZeroValueForAllFiles(Project project, SensorContext context) {
        for (File file : moduleFileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_KEY))) {
            saveZeroValueForResource(this.fileFromIoFile(file, project), context);
        }
    }

    protected void saveMeasureFromLCOVFile(Project project, SensorContext context) {
        String providedPath = settings.getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH);
        File lcovFile = getIOFile(moduleFileSystem.baseDir(), providedPath);

        if (!lcovFile.isFile()) {
            LOG.warn("No coverage information will be saved because LCOV file cannot be analysed. Provided LCOV file path: {}", providedPath);
            return;
        }

        LOG.info("Analysing {}", lcovFile);

        LCOVParser parser = getParser(moduleFileSystem.baseDir());
        Map<String, CoverageMeasuresBuilder> coveredFiles = parser.parseFile(lcovFile);

        for (File file : moduleFileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_KEY))) {
            try {
                CoverageMeasuresBuilder fileCoverage = coveredFiles.get(file.getAbsolutePath());
                org.sonar.api.resources.File resource = this.fileFromIoFile(file, project);

                if (fileCoverage != null) {
                    for (Measure measure : fileCoverage.createMeasures()) {
                        context.saveMeasure(resource, measure);
                    }
                } else {
                    // colour all lines as not executed
                    saveZeroValueForResource(resource, context);
                }
            } catch (Exception e) {
                LOG.error("Problem while calculating coverage for " + file.getAbsolutePath(), e);
            }
        }
    }

    protected org.sonar.api.resources.File fileFromIoFile(java.io.File file, Project project) {
        return org.sonar.api.resources.File.fromIOFile(file, project);
    }

    protected LCOVParser getParser(File baseDirectory) {
        return new LCOVParserImpl(baseDirectory);
    }

    private void saveZeroValueForResource(org.sonar.api.resources.File resource, SensorContext context) {
        PropertiesBuilder<Integer, Integer> lineHitsData = new PropertiesBuilder<>(CoreMetrics.COVERAGE_LINE_HITS_DATA);

        for (int x = 1; x < context.getMeasure(resource, CoreMetrics.LINES).getIntValue(); x++) {
            lineHitsData.add(x, 0);
        }

        // use non comment lines of code for coverage calculation
        Measure ncloc = context.getMeasure(resource, CoreMetrics.NCLOC);
        context.saveMeasure(resource, lineHitsData.build());
        context.saveMeasure(resource, CoreMetrics.LINES_TO_COVER, ncloc.getValue());
        context.saveMeasure(resource, CoreMetrics.UNCOVERED_LINES, ncloc.getValue());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private boolean isForceZeroCoverageActivated() {
        return settings.getBoolean(TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE);
    }

    private boolean isLCOVReportProvided() {
        return StringUtils.isNotBlank(settings.getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH));
    }

    /**
     * Returns a java.io.File for the given path.
     * If path is not absolute, returns a File with module base directory as parent path.
     */
    public File getIOFile(File baseDir, String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(baseDir, path);
        }

        return file;
    }
=======
@BatchSide
public interface TsCoverageSensor {
    public abstract void execute(SensorContext ctx, Map<InputFile, Set<Integer>> nonCommentLineNumbersByFile);
>>>>>>> /usr/src/app/output/pablissimo/sonartsplugin/5985fb7361c4b21cf789a9f5d89276afaa49674a/src/main/java/com/pablissimo/sonar/TsCoverageSensor.java/right.java
}
