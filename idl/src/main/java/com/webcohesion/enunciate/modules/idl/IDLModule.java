/**
 * Copyright © 2006-2016 Web Cohesion (info@webcohesion.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webcohesion.enunciate.modules.idl;

import com.webcohesion.enunciate.EnunciateContext;
import com.webcohesion.enunciate.EnunciateException;
import com.webcohesion.enunciate.facets.FacetFilter;
import com.webcohesion.enunciate.module.ApiFeatureProviderModule;
import com.webcohesion.enunciate.module.BasicGeneratingModule;
import com.webcohesion.enunciate.module.DependencySpec;
import com.webcohesion.enunciate.module.EnunciateModule;
import com.webcohesion.enunciate.modules.jaxb.JaxbModule;
import com.webcohesion.enunciate.modules.jaxb.model.SchemaInfo;
import com.webcohesion.enunciate.modules.jaxrs.JaxrsModule;
import com.webcohesion.enunciate.modules.jaxws.JaxwsModule;
import com.webcohesion.enunciate.modules.jaxws.WsdlInfo;
import com.webcohesion.enunciate.util.StaticInterfaceDescriptionFile;
import org.apache.commons.configuration2.HierarchicalConfiguration;

import java.io.File;
import java.util.*;

/**
 * @author Ryan Heaton
 */
public class IDLModule extends BasicGeneratingModule implements ApiFeatureProviderModule {

  JaxbModule jaxbModule;
  JaxwsModule jaxwsModule;
  JaxrsModule jaxrsModule;

  @Override
  public String getName() {
    return "idl";
  }

  @Override
  public List<DependencySpec> getDependencySpecifications() {
    return List.of(new DependencySpec() {
      @Override
      public boolean accept(EnunciateModule module) {
        if (module instanceof JaxbModule) {
          jaxbModule = (JaxbModule) module;
          return true;
        }
        else if (module instanceof JaxwsModule) {
          jaxwsModule = (JaxwsModule) module;
          return true;
        }
        else if (module instanceof JaxrsModule) {
          jaxrsModule = (JaxrsModule) module;
          return true;
        }

        return false;
      }

      @Override
      public boolean isFulfilled() {
        return true;
      }

      @Override
      public String toString() {
        return "optional jaxb, optional jaxws, optional jaxrs";
      }
    });
  }

  public Map<String, SchemaConfig> getSchemaConfigs() {
    HashMap<String, SchemaConfig> configs = new HashMap<>();

    List<HierarchicalConfiguration> schemas = this.config.configurationsAt("schema");
    for (HierarchicalConfiguration schema : schemas) {
      SchemaConfig schemaConfig = new SchemaConfig();
      schemaConfig.setAppinfo(schema.getString("[@appinfo]", null));
      schemaConfig.setFilename(schema.getString("[@filename]", null));
      schemaConfig.setJaxbBindingVersion(schema.getString("[@jaxbBindingVersion]", null));
      schemaConfig.setLocation(schema.getString("[@location]", null));
      String ns = schema.getString("[@namespace]", null);
      if ("".equals(ns)) {
        //default namspace to be represented as null.
        ns = null;
      }
      schemaConfig.setNamespace(ns);
      String useFile = schema.getString("[@useFile]", null);
      if (useFile != null) {
        File file = resolveFile(useFile);
        if (!file.exists()) {
          throw new EnunciateException(String.format("Invalid schema config: file %s does not exist.", useFile));
        }
        schemaConfig.setUseFile(file);
      }
      configs.put(schemaConfig.getNamespace(), schemaConfig);
    }

    return configs;
  }

  public Map<String, WsdlConfig> getWsdlConfigs() {
    HashMap<String, WsdlConfig> configs = new HashMap<>();
    List<HierarchicalConfiguration> wsdls = this.config.configurationsAt("wsdl");
    for (HierarchicalConfiguration wsdl : wsdls) {
      WsdlConfig wsdlConfig = new WsdlConfig();
      wsdlConfig.setFilename(wsdl.getString("[@filename]", null));
      wsdlConfig.setNamespace(wsdl.getString("[@namespace]", null));
      wsdlConfig.setInlineSchema(wsdl.getBoolean("[@inlineSchema]", true));
      String useFile = wsdl.getString("[@useFile]", null);
      if (useFile != null) {
        File file = resolveFile(useFile);
        if (!file.exists()) {
          throw new EnunciateException(String.format("Invalid wsdl config: file %s does not exist.", useFile));
        }
        wsdlConfig.setUseFile(file);
      }
      configs.put(wsdlConfig.getNamespace(), wsdlConfig);
    }

    return configs;
  }

  @Override
  public void call(EnunciateContext context) {
    Map<String, SchemaInfo> ns2schema = Collections.emptyMap();
    Map<String, String> ns2prefix = Collections.emptyMap();
    if (this.jaxbModule != null) {
      ns2schema = this.jaxbModule.getJaxbContext().getSchemas();
      ns2prefix = this.jaxbModule.getJaxbContext().getNamespacePrefixes();
    }

    Map<String, WsdlInfo> ns2wsdl = Collections.emptyMap();
    if (this.jaxwsModule != null) {
      ns2wsdl = this.jaxwsModule.getJaxwsContext().getWsdls();
    }

    Set<String> facetIncludes = new TreeSet<>(this.enunciate.getConfiguration().getFacetIncludes());
    facetIncludes.addAll(getFacetIncludes());
    Set<String> facetExcludes = new TreeSet<>(this.enunciate.getConfiguration().getFacetExcludes());
    facetExcludes.addAll(getFacetExcludes());
    FacetFilter facetFilter = new FacetFilter(facetIncludes, facetExcludes);

    Map<String, SchemaConfig> schemaConfigs = getSchemaConfigs();
    for (SchemaInfo schemaInfo : ns2schema.values()) {
      String defaultFilename = ns2prefix.get(schemaInfo.getNamespace()) + ".xsd";
      SchemaConfig explicitConfig = schemaConfigs.get(schemaInfo.getNamespace());

      if (explicitConfig != null && explicitConfig.getUseFile() != null) {
        schemaInfo.setFilename(explicitConfig.getUseFile().getName());
        schemaInfo.setSchemaFile(new StaticInterfaceDescriptionFile(explicitConfig.getUseFile(), this.enunciate));
      }
      else if (explicitConfig != null) {
        schemaInfo.setAppinfo(explicitConfig.getAppinfo());
        schemaInfo.setFilename(explicitConfig.getFilename() != null ? explicitConfig.getFilename() : defaultFilename);
        schemaInfo.setExplicitLocation(explicitConfig.getLocation());
        schemaInfo.setJaxbBindingVersion(explicitConfig.getJaxbBindingVersion());
        schemaInfo.setSchemaFile(new JaxbSchemaFile(this.enunciate, defaultFilename, this.jaxbModule.getJaxbContext(), schemaInfo, facetFilter, ns2prefix));
      }
      else {
        schemaInfo.setFilename(defaultFilename);
        schemaInfo.setSchemaFile(new JaxbSchemaFile(this.enunciate, defaultFilename, this.jaxbModule.getJaxbContext(), schemaInfo, facetFilter, ns2prefix));
      }
    }

    String baseUri = this.enunciate.getConfiguration().getApplicationRoot();

    Map<String, WsdlConfig> wsdlConfigs = getWsdlConfigs();
    for (WsdlInfo wsdlInfo : ns2wsdl.values()) {
      String defaultFilename = ns2prefix.get(wsdlInfo.getTargetNamespace()) + ".wsdl";
      WsdlConfig explicitConfig = wsdlConfigs.get(wsdlInfo.getTargetNamespace());

      if (explicitConfig != null && explicitConfig.getUseFile() != null) {
        wsdlInfo.setFilename(explicitConfig.getUseFile().getName());
        wsdlInfo.setWsdlFile(new StaticInterfaceDescriptionFile(explicitConfig.getUseFile(), this.enunciate));
      }
      else if (explicitConfig != null) {
        wsdlInfo.setFilename(explicitConfig.getFilename() != null ? explicitConfig.getFilename() : defaultFilename);
        wsdlInfo.setInlineSchema(explicitConfig.isInlineSchema());
        wsdlInfo.setWsdlFile(new JaxwsWsdlFile(this.enunciate, defaultFilename, wsdlInfo, this.jaxbModule.getJaxbContext(), baseUri, ns2prefix, facetFilter));
      }
      else {
        wsdlInfo.setFilename(defaultFilename);
        wsdlInfo.setWsdlFile(new JaxwsWsdlFile(this.enunciate, defaultFilename, wsdlInfo, this.jaxbModule.getJaxbContext(), baseUri, ns2prefix, facetFilter));
      }
    }

    if (this.jaxrsModule != null && this.jaxbModule != null && !isDisableWadl()) {
      this.jaxrsModule.getJaxrsContext().setWadlFile(new JaxrsWadlFile(this.enunciate, "application.wadl", this.jaxrsModule.getJaxrsContext(), this.jaxbModule.getJaxbContext(), new ArrayList<>(ns2schema.values()), getWadlStylesheetUri(), baseUri, ns2prefix, facetFilter, isLinkJsonToXml()));
    }
  }

  public boolean isDisableWadl() {
    return this.config.getBoolean("[@disableWadl]", false);
  }

  public boolean isLinkJsonToXml() {
    return this.config.getBoolean("[@linkJsonToXml]", false);
  }

  public String getWadlStylesheetUri() {
    return this.config.getString("[@wadlStylesheetUri]", null);
  }

  public Set<String> getFacetIncludes() {
    List<Object> includes = this.config.getList("facets.include[@name]");
    Set<String> facetIncludes = new TreeSet<>();
    for (Object include : includes) {
      facetIncludes.add(String.valueOf(include));
    }
    return facetIncludes;
  }

  public Set<String> getFacetExcludes() {
    List<Object> excludes = this.config.getList("facets.exclude[@name]");
    Set<String> facetExcludes = new TreeSet<>();
    for (Object exclude : excludes) {
      facetExcludes.add(String.valueOf(exclude));
    }
    return facetExcludes;
  }
}
