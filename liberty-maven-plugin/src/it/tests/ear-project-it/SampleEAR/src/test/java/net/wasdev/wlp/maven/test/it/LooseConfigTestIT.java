package net.wasdev.wlp.maven.test.it;
import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import static junit.framework.Assert.*;

public class LooseConfigTestIT {
  public final String LOOSE_APP = "liberty/wlp/usr/servers/test/apps/SampleEAR.ear.xml";

  @Test public void testLooseApplicationFileExist() throws Exception {
    File f = new File(LOOSE_APP);
    assertTrue(f.getCanonicalFile() + " doesn\'t exist", f.exists());
  }

  @Test public void testLooseApplicationFileContent() throws Exception {
    File f = new File(LOOSE_APP);
    FileInputStream input = new FileInputStream(f);
    DocumentBuilderFactory inputBuilderFactory = DocumentBuilderFactory.newInstance();
    inputBuilderFactory.setIgnoringComments(true);
    inputBuilderFactory.setCoalescing(true);
    inputBuilderFactory.setIgnoringElementContentWhitespace(true);
    inputBuilderFactory.setValidating(false);
    DocumentBuilder inputBuilder = inputBuilderFactory.newDocumentBuilder();
    Document inputDoc = inputBuilder.parse(input);
    XPath xPath = XPathFactory.newInstance().newXPath();
    String expression = "/archive/file";
    NodeList nodes = (NodeList) xPath.compile(expression).evaluate(inputDoc, XPathConstants.NODESET);
    assertEquals("Number of <file/> element ==>", 2, nodes.getLength());
    assertEquals("file targetInArchive attribute value", "/META-INF/application.xml", nodes.item(0).getAttributes().getNamedItem("targetInArchive").getNodeValue());
    assertEquals("file targetInArchive attribute value", "/META-INF/MANIFEST.MF", nodes.item(1).getAttributes().getNamedItem("targetInArchive").getNodeValue());
    expression = "/archive/dir";
    nodes = (NodeList) xPath.compile(expression).evaluate(inputDoc, XPathConstants.NODESET);
    assertEquals("Number of <dir/> element ==>", 1, nodes.getLength());
    expression = "/archive/archive";
    nodes = (NodeList) xPath.compile(expression).evaluate(inputDoc, XPathConstants.NODESET);
    assertEquals("Number of <archive/> element ==>", 3, nodes.getLength());
    assertEquals("archive targetInArchive attribute value", "/SampleEJB.jar", nodes.item(0).getAttributes().getNamedItem("targetInArchive").getNodeValue());
    assertEquals("archive targetInArchive attribute value", "/modules/web.war", nodes.item(1).getAttributes().getNamedItem("targetInArchive").getNodeValue());
    expression = "/archive/archive/file";
    nodes = (NodeList) xPath.compile(expression).evaluate(inputDoc, XPathConstants.NODESET);
    assertEquals("Number of <archive/> element ==>", 3, nodes.getLength());
    assertEquals(
<<<<<<< /usr/src/app/output/wasdev/ci.maven/b7c37eab73d658f90da6b0a6e9149b26a00bb332/liberty-maven-plugin/src/it/tests/ear-project-it/SampleEAR/src/test/java/net/wasdev/wlp/maven/test/it/LooseConfigTestIT.java/left.java
    "archive targetInArchive attribute value"
=======
    "file targetInArchive attribute value"
>>>>>>> /usr/src/app/output/wasdev/ci.maven/b7c37eab73d658f90da6b0a6e9149b26a00bb332/liberty-maven-plugin/src/it/tests/ear-project-it/SampleEAR/src/test/java/net/wasdev/wlp/maven/test/it/LooseConfigTestIT.java/right.java
    , 
<<<<<<< /usr/src/app/output/wasdev/ci.maven/b7c37eab73d658f90da6b0a6e9149b26a00bb332/liberty-maven-plugin/src/it/tests/ear-project-it/SampleEAR/src/test/java/net/wasdev/wlp/maven/test/it/LooseConfigTestIT.java/left.java
    "/SampleWAR2.war"
=======
    "/WEB-INF/lib/log4j-1.2.17.jar"
>>>>>>> /usr/src/app/output/wasdev/ci.maven/b7c37eab73d658f90da6b0a6e9149b26a00bb332/liberty-maven-plugin/src/it/tests/ear-project-it/SampleEAR/src/test/java/net/wasdev/wlp/maven/test/it/LooseConfigTestIT.java/right.java
    , nodes.item(2).getAttributes().getNamedItem("targetInArchive").getNodeValue());
  }
}