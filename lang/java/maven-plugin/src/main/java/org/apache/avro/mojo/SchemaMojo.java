/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avro.mojo;

import org.apache.avro.Schema;

import java.io.File;

import java.io.IOException;

import org.apache.avro.SchemaParseException;

import java.net.MalformedURLException;

import java.util.Arrays;

import java.util.List;

import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Generate Java classes from Avro schema files (.avsc)
 *
 * @goal schema
 * @phase generate-sources
 * @requiresDependencyResolution runtime+test
 * @threadSafe
 */
public class SchemaMojo extends AbstractAvroMojo {
  /**
   * A parser used to parse all schema files. Using a common parser will
   * facilitate the import of external schemas.
   */
  private Schema.Parser schemaParser = new Schema.Parser();

  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern <code>**&#47;*.avsc</code>
   * is used to select grammar files.
   *
   * @parameter
   */
  private String[] includes = new String[] { "**/*.avsc" };
  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern <code>**&#47;*.avsc</code>
   * is used to select grammar files.
   *
   * @parameter
   */
  private String[] testIncludes = new String[] { "**/*.avsc" };
  @Override
<<<<<<< /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/SchemaMojo.java/left.java
  protected void doCompile(String[] filesName, File sourceDirectory, File outputDirectory) throws IOException {
    File src = new File(sourceDirectory, filename);
    final Schema schema;

    // This is necessary to maintain backward-compatibility. If there are
    // no imported files then isolate the schemas from each other, otherwise
    // allow them to share a single schema so reuse and sharing of schema
    // is possible.
    if (imports == null) {
      schema = new Schema.Parser().parse(src);
    } else {
      schema = schemaParser.parse(src);
    }

    doCompile(src, schema, outputDirectory);
  }
||||||| /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/SchemaMojo.java/base.java
  protected void doCompile(String[] filesName, File sourceDirectory, File outputDirectory) throws IOException 
=======
  protected void doCompile(String[] filesName, File sourceDirectory, File outputDirectory) throws IOException {
    if (imports == null) {
      schemas = new Schema.Parser().parse(sourceFiles);
    } else {
      schemas = schemaParser.parse(sourceFiles);
    }
  }
>>>>>>> /usr/src/app/output/apache/avro/4bd07bf93c36c227704900824c78c291cbe24dc1/lang/java/maven-plugin/src/main/java/org/apache/avro/mojo/SchemaMojo.java/right.java
  @Override
  protected void doCompile(final String filename, final File sourceDirectory, final File outputDirectory)
      throws IOException {
    // Not call.
  }

  @Override
  protected String[] getIncludes() {
    return includes;
  }

  @Override
  protected String[] getTestIncludes() {
    return testIncludes;
  }
}
