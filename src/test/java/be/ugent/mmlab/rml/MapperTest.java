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
     * @param testName name of the test case
     */
    public MapperTest(String testName) {
        super(testName);
<<<<<<< LEFT

        RMLEngine.getFileMap().put("example.xml", getClass().getResource("/example1/example.xml").getFile());
        RMLEngine.getFileMap().put("Airport.csv", getClass().getResource("/example3/Airport.csv").getFile());
        RMLEngine.getFileMap().put("Venue.json", getClass().getResource("/example3/Venue.json").getFile());
        RMLEngine.getFileMap().put("Transport.xml", getClass().getResource("/example3/Transport.xml").getFile());
        RMLEngine.getFileMap().put("Venue4.json", getClass().getResource("/example4/Venue.json").getFile());
        RMLEngine.getFileMap().put("moon-walkers.csv", getClass().getResource("/example5/moon-walkers.csv").getFile());
        RMLEngine.getFileMap().put("museum.json", getClass().getResource("/example5/museum.json").getFile());
        RMLEngine.getFileMap().put("moon-walkers7.csv", getClass().getResource("/example7/moon-walkers7.csv").getFile());
=======
>>>>>>> RIGHT
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
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    public void testExample2() {
        URL fileToRMLFile = getClass().getResource("/example2/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example2/example.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    public void testExample3() {
        URL fileToRMLFile = getClass().getResource("/example3/example3.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example3/example3.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    public void testExample4() {
        URL fileToRMLFile = getClass().getResource("/example4/example4_Venue.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example4/example4_Venue.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    /*public void testExample5() {
        URL fileToRMLFile = getClass().getResource("/example5/museum-model.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example5/museum.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }*/
    public void testExample6() {
        URL fileToRMLFile = getClass().getResource("/example6/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example6/example.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    public void testExample7() {
        URL fileToRMLFile = getClass().getResource("/example7/moon-walkers.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example7/moon-walkers.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    private boolean assertMap(URL mappingURL, URL outputURL) {
        try {
            RMLMapping mapping = RMLMappingFactory.extractRMLMapping(mappingURL.getFile());

            RMLEngine engine = new RMLEngine();
            
            SesameDataSet output = engine.runRMLMapping(mapping, "http://example.com");

            output.dumpRDF(System.out, RDFFormat.TURTLE);

            SesameDataSet desiredOutput = new SesameDataSet();
            desiredOutput.addFile(outputURL.getFile(), RDFFormat.TURTLE);

            return desiredOutput.isEqualTo(output);
        } catch (SQLException | InvalidR2RMLStructureException | InvalidR2RMLSyntaxException | R2RMLDataError | RepositoryException | RDFParseException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }
}