package de.is24.deadcode4j.analyzer;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public final class A_SpringWebXmlAnalyzer extends AFinalAnalyzer<SpringWebXmlAnalyzer> {

    @Override
    protected SpringWebXmlAnalyzer createAnalyzer() {
        return new SpringWebXmlAnalyzer();
    }

    @Test
    public void shouldParseWebXmlFiles() {
        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/spring.web.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the web.xml file!", codeDependencies.size(), greaterThan(1));

        assertThatDependenciesAreReported(
                "servlet.contextClass",
                "servlet.initializerClass",
                "root.contextClass",
                "root.initializerClass",
                "root.secondInitializerClass");
    }

}
