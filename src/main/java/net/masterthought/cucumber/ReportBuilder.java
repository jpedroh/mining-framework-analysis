package net.masterthought.cucumber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;

import net.masterthought.cucumber.generators.ErrorPage;
import net.masterthought.cucumber.generators.FailuresOverviewPage;
import net.masterthought.cucumber.generators.FeatureReportPage;
import net.masterthought.cucumber.generators.FeaturesOverviewPage;
import net.masterthought.cucumber.generators.StepsOverviewPage;
import net.masterthought.cucumber.generators.TagReportPage;
import net.masterthought.cucumber.generators.TagsOverviewPage;
import net.masterthought.cucumber.generators.TrendsOverviewPage;
import net.masterthought.cucumber.json.Feature;
import net.masterthought.cucumber.json.support.TagObject;

public class ReportBuilder {

<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/left.java
    private static final Logger LOG = LogManager.getLogger(ReportBuilder.class);
||||||| /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/base.java
    private static final Logger LOG = LogManager.getLogger(ReportBuilder.class);
=======
    private static final Logger LOG = Logger.getLogger(ReportBuilder.class.getName());
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/right.java

    /**
     * Page that should be displayed when the reports is generated. Shared between {@link FeaturesOverviewPage} and
     * {@link ErrorPage}.
     */

    public static final String HOME_PAGE = "overview-features.html";

    /**
     * Subdirectory where the report will be created.
     */

    public static final String BASE_DIRECTORY = "cucumber-html-reports";

    private static final ObjectMapper mapper = new ObjectMapper();

    private ReportResult reportResult;

    private final ReportParser reportParser;

    private Configuration configuration;

    private List<String> jsonFiles;

    /**
     * Flag used to detect if the file with updated trends is saved.
     * If the report crashes and the trends was not saved then it tries to save trends again with empty data
     * to mark that the build crashed.
     */

    private boolean wasTrendsFileSaved = false;

    public ReportBuilder(List<String> jsonFiles, Configuration configuration) {
        this.jsonFiles = jsonFiles;
        this.configuration = configuration;
        reportParser = new ReportParser(configuration);
    }

    public Reportable generateReports() {
        Trends trends = null;

        try {
            // first copy static resources so ErrorPage is displayed properly
            copyStaticResources();

            // create directory for embeddings before files are generated
            createEmbeddingsDirectory();

<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/left.java
            // parse json files for results
||||||| /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/base.java
=======
            // add metadata info sourced from files
            reportParser.parseClassificationsFiles(configuration.getClassificationFiles());

            // parse json files for results
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/right.java
            List<Feature> features = reportParser.parseJsonFiles(jsonFiles);
<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/left.java
            reportResult = new ReportResult(features);
            Reportable reportable = reportResult.getFeatureReport();
||||||| /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/base.java
            reportResult = new ReportResult(features);
=======
            reportResult = new ReportResult(features, configuration.getSortingMethod());
            Reportable reportable = reportResult.getFeatureReport();
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/right.java

<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/left.java
            if (configuration.isTrendsStatsFile()) {
                // prepare data required by generators, collect generators and generate pages
                trends = updateAndSaveTrends(reportable);
            }

            List<AbstractPage> pages = collectPages(trends);
            generatePages(pages);
||||||| /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/base.java
            List<AbstractPage> pages = collectPages();
            generatePages(pages);
=======
            if (configuration.isTrendsStatsFile()) {
                // prepare data required by generators, collect generators and generate pages
                trends = updateAndSaveTrends(reportable);
            }
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/right.java

<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/left.java
            return reportable;
||||||| /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/base.java
            return reportResult.getFeatureReport();
=======
            // Collect and generate pages in a single pass
            generatePages(trends);
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/right.java

<<<<<<< /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/left.java
            // whatever happens we want to provide at least error page instead of incomplete report or exception
||||||| /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/base.java
            // whatever happens we want to provide at least error page instead of empty report
=======
            return reportable;

            // whatever happens we want to provide at least error page instead of incomplete report or exception
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/ReportBuilder.java/right.java
        } catch (Exception e) {
            generateErrorPage(e);
            // update trends so there is information in history that the build failed

            // if trends was not created then something went wrong
            // and information about build failure should be saved
            if (!wasTrendsFileSaved && configuration.isTrendsStatsFile()) {
                Reportable reportable = new EmptyReportable();
                updateAndSaveTrends(reportable);
            }

            // something went wrong, don't pass result that might be incomplete
            return null;
        }
    }

    private void copyStaticResources() {
        copyResources("css", "cucumber.css", "bootstrap.min.css", "font-awesome.min.css");
        copyResources("js", "jquery.min.js", "jquery.tablesorter.min.js", "bootstrap.min.js", "Chart.min.js",
                "moment.min.js");
        copyResources("fonts", "FontAwesome.otf", "fontawesome-webfont.svg", "fontawesome-webfont.woff",
                "fontawesome-webfont.eot", "fontawesome-webfont.ttf", "fontawesome-webfont.woff2",
                "glyphicons-halflings-regular.eot", "glyphicons-halflings-regular.eot",
                "glyphicons-halflings-regular.woff2", "glyphicons-halflings-regular.woff",
                "glyphicons-halflings-regular.ttf", "glyphicons-halflings-regular.svg");
        copyResources("images", "favicon.png");
    }

    private void createEmbeddingsDirectory() {
        configuration.getEmbeddingDirectory().mkdirs();
    }

    private void copyResources(String resourceLocation, String... resources) {
        for (String resource : resources) {
            File tempFile = new File(configuration.getReportDirectory().getAbsoluteFile(),
                    BASE_DIRECTORY + File.separatorChar + resourceLocation + File.separatorChar + resource);
            // don't change this implementation unless you verified it works on Jenkins
            try {
                FileUtils.copyInputStreamToFile(
                        this.getClass().getResourceAsStream("/" + resourceLocation + "/" + resource), tempFile);
            } catch (IOException e) {
                // based on FileUtils implementation, should never happen even is declared
                throw new ValidationException(e);
            }
        }
    }

    private void generatePages(Trends trends) {
    	new FeaturesOverviewPage(reportResult, configuration).generatePage();
    	
    	for (Feature feature : reportResult.getAllFeatures()) {
    		new FeatureReportPage(reportResult, configuration, feature).generatePage();
    	}
    	
    	new TagsOverviewPage(reportResult, configuration).generatePage();
    	
    	for (TagObject tagObject : reportResult.getAllTags()) {
    		new TagReportPage(reportResult, configuration, tagObject).generatePage();
    	}

    	new StepsOverviewPage(reportResult, configuration).generatePage();
    	new FailuresOverviewPage(reportResult, configuration).generatePage();

    	if (configuration.isTrendsStatsFile()) {
    		new TrendsOverviewPage(reportResult, configuration, trends).generatePage();
    	}
    }

    private Trends updateAndSaveTrends(Reportable reportable) {
        Trends trends = loadOrCreateTrends();
        appendToTrends(trends, reportable);

        // save updated trends so it contains all history
        saveTrends(trends, configuration.getTrendsStatsFile());

        // display only last n items - don't skip items if limit is not defined
        if (configuration.getTrendsLimit() > 0) {
            trends.limitItems(configuration.getTrendsLimit());
        }

        return trends;
    }

    private void appendToTrends(Trends trends, Reportable result) {
        trends.addBuild(configuration.getBuildNumber(), result);
    }

    private Trends loadOrCreateTrends() {
        File trendsFile = configuration.getTrendsStatsFile();
        if (trendsFile != null && trendsFile.exists()) {
            return loadTrends(trendsFile);
        } else {
            return new Trends();
        }
    }

    /**
     * Flag used to detect if the file with updated trends is saved.
     * If the report crashes and the trends was not saved then it tries to save trends again with empty data
     * to mark that the build crashed.
     */

    /**
     * Parses provided files and generates the report. When generating process fails
     * report with information about error is provided.
     * @return stats for the generated report
     */

    private static Trends loadTrends(File file) {
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return mapper.readValue(reader, Trends.class);
        } catch (JsonMappingException e) {
            throw new ValidationException(String.format("File '%s' could not be parsed as file with trends!", file), e);
        } catch (IOException e) {
            // IO problem - stop generating and re-throw the problem
            throw new ValidationException(e);
        }
    }

    private void saveTrends(Trends trends, File file) {
        ObjectWriter objectWriter = mapper.writer().with(SerializationFeature.INDENT_OUTPUT);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            objectWriter.writeValue(writer, trends);
            wasTrendsFileSaved = true;
        } catch (IOException e) {
            wasTrendsFileSaved = false;
            throw new ValidationException("Could not save updated trends in file: " + file.getAbsolutePath(), e);
        }
    }

    private void generateErrorPage(Exception exception) {
        LOG.log(Level.INFO, "Unexpected error", exception);
        ErrorPage errorPage = new ErrorPage(reportResult, configuration, exception, jsonFiles);
        errorPage.generatePage();
    }
}
