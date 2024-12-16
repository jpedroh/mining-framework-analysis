package be.ugent.mmlab.rml;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.model.RMLMapping;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import static junit.framework.TestCase.assertTrue;


/**
 * Unit test for simple App.
 */
public class MapperTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     * 		name of the test case
     */
    public MapperTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(MapperTest.class);
    }

    /**
     * Tests
     */
    public void testExample1() {
        URL fileToRMLFile = getClass().getResource("/example1/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example1/example.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile)));
    }

    public void testExample2() {
        URL fileToRMLFile = getClass().getResource("/example2/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example2/example.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile)));
    }

    public void testExample3() {
        URL fileToRMLFile = getClass().getResource("/example3/example3.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example3/example3.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile)));
    }

    public void testExample4() {
        URL fileToRMLFile = getClass().getResource("/example4/example4_Venue.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example4/example4_Venue.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile)));
    }

    /*public void testExample5() {
        URL fileToRMLFile = getClass().getResource("/example5/museum-model.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example5/museum.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }*/
    public void testExample6() {
        URL fileToRMLFile = getClass().getResource("/example6/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example6/example.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile)));
    }

    public void testExample7() {
        URL fileToRMLFile = getClass().getResource("/example7/example7.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example7/example7.output.ttl");
        assertTrue(desiredContextOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile)));
    }

    private SesameDataSet desiredOutput(URL outputURL) {
        SesameDataSet desiredOutput = new SesameDataSet();
        desiredOutput.addFile(outputURL.getFile(), RDFFormat.TURTLE);
        return desiredOutput;
    }

    private SesameDataSet desiredContextOutput (URL outputURL){
        SesameDataSet desiredOutput = new SesameDataSet();
        desiredOutput.addFile(outputURL.getFile(), RDFFormat.NQUADS);
        return desiredOutput;
    }

    private SesameDataSet assertMap(URL mappingURL) {
        try {
            RMLMapping mapping = RMLMappingFactory.extractRMLMapping(mappingURL.getFile());
            RMLEngine engine = new RMLEngine();
            SesameDataSet output = engine.runRMLMapping(mapping, "http://example.com");
            output.dumpRDF(System.out, RDFFormat.TURTLE);
            return output;
        } catch (SQLException | InvalidR2RMLStructureException | InvalidR2RMLSyntaxException | R2RMLDataError | RepositoryException | RDFParseException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}