/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.remoting3;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * The version of Remoting.
 *
 * @apiviz.exclude
 */
@SuppressWarnings("deprecation")
public final class Version {

    private Version() {
    }

    private static final String JAR_NAME;
    /**
     * The version string.
     *
     * @deprecated Use {@link #getVersionString()} instead.
     */
    @Deprecated
    public static final String VERSION;

    static {
        Properties versionProps = new Properties();
        String jarName = "(unknown)";
        String versionString = "(unknown)";
<<<<<<< /usr/src/app/output/jboss-remoting/jboss-remoting/dd7f776d66ed721f4e2f6237137d69faa3d1b703/src/main/java/org/jboss/remoting3/Version.java/left.java
        try (InputStream stream = Version.class.getResourceAsStream("Version.properties")) {
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                versionProps.load(reader);
                jarName = versionProps.getProperty("jarName", jarName);
                versionString = versionProps.getProperty("version", versionString);
||||||| /usr/src/app/output/jboss-remoting/jboss-remoting/dd7f776d66ed721f4e2f6237137d69faa3d1b703/src/main/java/org/jboss/remoting3/Version.java/base.java
        final ClassLoader classLoader = Version.class.getClassLoader();
        try {
            resources = classLoader == null ? ClassLoader.getSystemResources("META-INF/MANIFEST.MF") : classLoader.getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                try {
                    final InputStream stream = url.openStream();
                    if (stream != null) try {
                        final Manifest manifest = new Manifest(stream);
                        final Attributes mainAttributes = manifest.getMainAttributes();
                        if (mainAttributes != null && "JBoss Remoting".equals(mainAttributes.getValue("Specification-Title"))) {
                            jarName = mainAttributes.getValue("Jar-Name");
                            versionString = mainAttributes.getValue("Jar-Version");
                        }
                    } finally {
                        safeClose(stream);
                    }
                } catch (IOException ignored) {
                }
=======
        try {
            final InputStream stream = Version.class.getResourceAsStream("Version.properties");
            try {
                final InputStreamReader reader = new InputStreamReader(stream);
                try {
                    versionProps.load(reader);
                    jarName = versionProps.getProperty("jarName", jarName);
                    versionString = versionProps.getProperty("version", versionString);
                } finally {
                    safeClose(reader);
                }
            } finally {
                safeClose(stream);
>>>>>>> /usr/src/app/output/jboss-remoting/jboss-remoting/dd7f776d66ed721f4e2f6237137d69faa3d1b703/src/main/java/org/jboss/remoting3/Version.java/right.java
            }
        } catch (IOException ignored) {
        }
        JAR_NAME = jarName;
        VERSION = versionString;
    }

    /**
     * Get the name of the JBoss Remoting JAR.
     *
     * @return the name
     */
    public static String getJarName() {
        return JAR_NAME;
    }

    /**
     * Get the version string of JBoss Remoting.
     *
     * @return the version string
     */
    public static String getVersionString() {
        return VERSION;
    }

    /**
     * Print out the current version on {@code System.out}.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        System.out.printf("JBoss Remoting version %s\n", getVersionString());
    }
}
