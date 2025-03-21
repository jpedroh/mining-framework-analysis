package org.reficio.p2.utils;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Jar;
import org.apache.felix.bundleplugin.BundlePlugin;
import org.apache.maven.artifact.DefaultArtifact;
import org.reficio.p2.resolver.maven.Artifact;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Tom Bujok (tom.bujok@gmail.com)<br>
 *         Reficio (TM) - Reestablish your software!<br>
 *         http://www.reficio.org
 * @since 1.0.0
 */
public class BundleUtils extends BundlePlugin {
  public static final BundleUtils INSTANCE = new BundleUtils();

  private static final String BUNDLE_SYMBOLIC_NAME_ATTR_NAME = "Bundle-SymbolicName";

  private static final String BUNDLE_VERSION = "Bundle-Version";

  private static final String BUNDLE_NAME = "Bundle-Name";

  private boolean reuseSnapshotVersionFromArtifact = true;

  public boolean reportErrors(Analyzer analyzer) {
    return super.reportErrors("", analyzer);
  }

  public static org.apache.maven.artifact.Artifact aetherToMavenArtifactBasic(Artifact artifact) {
    DefaultArtifact mavenArtifact = new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), "compile", artifact.getExtension(), artifact.getClassifier(), null);
    return mavenArtifact;
  }

  public String calculateBundleSymbolicName(Artifact artifact) {
    return super.getMaven2OsgiConverter().getBundleSymbolicName(aetherToMavenArtifactBasic(artifact));
  }

  public String calculateBundleVersion(Artifact artifact) {
    return super.getMaven2OsgiConverter().getVersion(aetherToMavenArtifactBasic(artifact));
  }

  public String cleanupVersion(String version) {
    return super.getMaven2OsgiConverter().getVersion(version);
  }

  public boolean isBundle(File file) {
    Jar inputJar = null;
    try {
      inputJar = new Jar(file);
      return isBundle(inputJar);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      if (inputJar != null) {
        inputJar.close();
      }
    }
  }

  public boolean isBundle(Jar jar) {
    return getBundleSymbolicName(jar) != null;
  }

  public String getBundleSymbolicName(Jar jar) {
    return getManifestValue(jar, BUNDLE_SYMBOLIC_NAME_ATTR_NAME);
  }

  public String getBundleVersion(Jar jar) {
    return getManifestValue(jar, BUNDLE_VERSION);
  }

  public String getBundleName(Jar jar) {
    return getManifestValue(jar, BUNDLE_NAME);
  }

  private String getManifestValue(Jar jar, String attributeName) {
    try {
      Manifest manifest = jar.getManifest();
      if (manifest == null) {
        return null;
      }
      Attributes.Name symbolicName = new Attributes.Name(attributeName);
      Attributes attributes = manifest.getMainAttributes();
      if (attributes == null) {
        return null;
      }
      return attributes.getValue(symbolicName);
    } catch (Exception e) {
      return null;
    }
  }

  public static Properties transformDirectivesToProperties(Map<String, Object> instructions) {
    Properties properties = new Properties();
    properties.putAll(BundlePlugin.transformDirectives(instructions));
    return properties;
  }

  public void setReuseSnapshotVersionFromArtifact(boolean in) {
    reuseSnapshotVersionFromArtifact = in;
  }

  public boolean isReuseSnapshotVersionFromArtifact() {
    return reuseSnapshotVersionFromArtifact;
  }
}