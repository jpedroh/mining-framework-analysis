package org.reficio.p2.utils;
import aQute.lib.osgi.Analyzer;
import aQute.lib.osgi.FileResource;
import aQute.lib.osgi.Jar;
import aQute.lib.osgi.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import clover.retrotranslator.edu.emory.mathcs.backport.java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import javax.xml.transform.Result;
import java.io.IOException;
import javax.xml.transform.Source;
import java.io.InputStream;
import javax.xml.transform.Transformer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import javax.xml.transform.TransformerFactory;
import java.util.Date;
import javax.xml.transform.dom.DOMSource;
import java.util.Enumeration;
import java.util.List;
import javax.xml.transform.stream.StreamResult;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author Tom Bujok (tom.bujok@gmail.com)<br>
 *         Reficio (TM) - Reestablish your software!<br>
 *         http://www.reficio.org
 * @since 1.0.0
 */
public class JarUtils {
  static Comparator<File> fileComparator = new Comparator<File>() {
    @Override public int compare(File arg0, File arg1) {
      return arg0.getName().compareTo(arg1.getName());
    }
  };

  public static void adjustSnapshotOutputVersion(File inputFile, File outputFile, String version) {
    Jar jar = null;
    try {
      jar = new Jar(inputFile);
      Manifest manifest = jar.getManifest();
      Attributes attributes = manifest.getMainAttributes();
      attributes.putValue(Analyzer.BUNDLE_VERSION, version);
      jar.write(outputFile);
    } catch (Exception e) {
      throw new RuntimeException("Cannot open jar " + outputFile, e);
    } finally {
      if (jar != null) {
        jar.close();
      }
    }
  }

  public static void adjustFeatureXml(File inputFile, File outputFile, File pluginDir, Log log, String timestamp) {
    Jar jar = null;
    File newXml = null;
    try {
      jar = new Jar(inputFile);
      Resource res = jar.getResource("feature.xml");
      Document featureSpec = XmlUtils.parseXml(res.openInputStream());
      adjustFeatureQualifierVersionWithTimestamp(featureSpec, timestamp);
      adjustFeaturePluginData(featureSpec, pluginDir, log);
      File temp = new File(outputFile.getParentFile(), "temp");
      temp.mkdir();
      newXml = new File(temp, "feature.xml");
      XmlUtils.writeXml(featureSpec, newXml);
      FileResource newRes = new FileResource(newXml);
      jar.putResource("feature.xml", newRes, true);
      jar.write(outputFile);
    } catch (IOException e) {
      throw new RuntimeException("Cannot open jar " + outputFile);
    } catch (Exception e) {
      throw new RuntimeException("Cannot open jar " + outputFile);
    } finally {
      if (jar != null) {
        jar.close();
      }
      if (null != newXml) {
        newXml.delete();
      }
    }
  }

  public static void adjustFeatureQualifierVersionWithTimestamp(Document featureSpec, String timestamp) {
    String version = featureSpec.getDocumentElement().getAttributeNode("version").getValue();
    String newVersion = Utils.eclipseQualifierToTimeStamp(version, timestamp);

<<<<<<< /usr/src/app/output/reficio/p2-maven-plugin/b02e8c2d7d3f758d6d44fb446854c713dcdb4d5f/src/main/java/org/reficio/p2/utils/JarUtils.java/left.java
    try {
      jar = new Jar(inputFile);
      Resource res = jar.getResource("feature.xml");
      Document featureSpec = parseXml(res.openInputStream());
      String version = featureSpec.getDocumentElement().getAttributeNode("version").getValue();
      String newVersion = replaceQualifierWithTimestamp(version);
      featureSpec.getDocumentElement().getAttributeNode("version").setValue(newVersion);
      File newXml = new File(inputFile.getParentFile(), "feature.xml");
      writeXml(featureSpec, newXml);
      FileResource newRes = new FileResource(newXml);
      jar.putResource("feature.xml", newRes, true);
      jar.write(outputFile);
    } catch (Exception e) {
      throw new RuntimeException("Cannot open jar " + outputFile, e);
    } finally {
      if (jar != null) {
        jar.close();
      }
    }
=======
    featureSpec.getDocumentElement().getAttributeNode("version").setValue(newVersion);
>>>>>>> /usr/src/app/output/reficio/p2-maven-plugin/b02e8c2d7d3f758d6d44fb446854c713dcdb4d5f/src/main/java/org/reficio/p2/utils/JarUtils.java/right.java
  }


<<<<<<< /usr/src/app/output/reficio/p2-maven-plugin/b02e8c2d7d3f758d6d44fb446854c713dcdb4d5f/src/main/java/org/reficio/p2/utils/JarUtils.java/left.java
  public static Document parseXml(InputStream input) {
    try {
      DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
      fac.setValidating(false);
      DocumentBuilder docBuilder = fac.newDocumentBuilder();
      Document doc = docBuilder.parse(input);
      return doc;
    } catch (Exception e) {
      throw new RuntimeException("Cannot parse XML input", e);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  public static void adjustFeaturePluginData(Document featureSpec, File pluginDir, Log log) throws IOException {
    NodeList plugins = featureSpec.getElementsByTagName("plugin");
    for (int i = 0; i < plugins.getLength(); ++i) {
      Node n = plugins.item(i);
      if (n instanceof Element) {
        Element el = (Element) n;
        String pluginId = el.getAttribute("id");
        File[] files = findFiles(pluginDir, pluginId);
        if (files.length < 0) {
          log.error("Cannot find plugin " + pluginId);
        } else {
          Arrays.sort(files, fileComparator);
          File lastFile = files[files.length - 1];
          String lastVersion = BundleUtils.INSTANCE.getBundleVersion(new Jar(lastFile));
          log.info("Adjusting version for plugin " + pluginId + " to " + lastVersion);
          el.setAttribute("version", lastVersion);
        }
      }
    }
  }


<<<<<<< /usr/src/app/output/reficio/p2-maven-plugin/b02e8c2d7d3f758d6d44fb446854c713dcdb4d5f/src/main/java/org/reficio/p2/utils/JarUtils.java/left.java
  public static void writeXml(Document doc, File outputFile) {
    try {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      Result output = new StreamResult(outputFile);
      Source input = new DOMSource(doc);
      transformer.transform(input, output);
    } catch (Exception e) {
      throw new RuntimeException("Cannot write XML document to file " + outputFile.getName(), e);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  static File[] findFiles(File pluginDir, final String pluginId) {
    return pluginDir.listFiles(new FilenameFilter() {
      @Override public boolean accept(File dir, String name) {
        return name.startsWith(pluginId) && name.endsWith(".jar");
      }
    });
  }

  public static void removeSignature(File jar) {
    File unsignedJar = new File(jar.getParent(), jar.getName() + ".tmp");
    try {
      if (unsignedJar.exists()) {
        FileUtils.deleteQuietly(unsignedJar);
        unsignedJar = new File(jar.getParent(), jar.getName() + ".tmp");
      }
      if (!unsignedJar.createNewFile()) {
        throw new RuntimeException("Cannot create file " + unsignedJar);
      }
      ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(unsignedJar));
      try {
        ZipFile zip = new ZipFile(jar);
        for (Enumeration list = zip.entries(); list.hasMoreElements(); ) {
          ZipEntry entry = (ZipEntry) list.nextElement();
          String name = entry.getName();
          if (entry.isDirectory()) {
            continue;
          } else {
            if (name.endsWith(".RSA") || name.endsWith(".DSA") || name.endsWith(".SF")) {
              continue;
            }
          }
          InputStream zipInputStream = zip.getInputStream(entry);
          zipOutputStream.putNextEntry(entry);
          try {
            IOUtils.copy(zipInputStream, zipOutputStream);
          }  finally {
            zipInputStream.close();
          }
        }
        IOUtils.closeQuietly(zipOutputStream);
        FileUtils.copyFile(unsignedJar, jar);
      }  finally {
        IOUtils.closeQuietly(zipOutputStream);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      FileUtils.deleteQuietly(unsignedJar);
    }
  }

  public static boolean containsSignature(File jarToUnsign) {
    try {
      ZipFile zip = new ZipFile(jarToUnsign);
      try {
        for (Enumeration list = zip.entries(); list.hasMoreElements(); ) {
          ZipEntry entry = (ZipEntry) list.nextElement();
          String name = entry.getName();
          if (!entry.isDirectory() && (name.endsWith(".RSA") || name.endsWith(".DSA") || name.endsWith(".SF"))) {
            return true;
          }
        }
        return false;
      }  finally {
        zip.close();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}