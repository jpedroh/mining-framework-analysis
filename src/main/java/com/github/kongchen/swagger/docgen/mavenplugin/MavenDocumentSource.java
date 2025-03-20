package com.github.kongchen.swagger.docgen.mavenplugin;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import com.github.kongchen.swagger.docgen.AbstractDocumentSource;
import com.github.kongchen.swagger.docgen.GenerateException;
import com.github.kongchen.swagger.docgen.reader.ClassSwaggerReader;
import com.github.kongchen.swagger.docgen.reader.JaxrsReader;
import com.google.common.collect.Sets;
import org.springframework.web.bind.annotation.RestController;
import javax.ws.rs.Path;

/**
 * @author chekong
 *         05/13/2013
 */
public class MavenDocumentSource extends AbstractDocumentSource {

    public MavenDocumentSource(ApiSource apiSource, Log log, String encoding) throws MojoFailureException {
        super(log, apiSource);
        if(encoding !=null) {
            this.encoding = encoding;
        }
    }

    @Override
    public void loadDocuments() throws GenerateException {
        if (apiSource.getSwaggerInternalFilter() != null) {
            try {
                LOG.info("Setting filter configuration: " + apiSource.getSwaggerInternalFilter());
                FilterFactory.setFilter((SwaggerSpecFilter) Class.forName(apiSource.getSwaggerInternalFilter()).newInstance());
            } catch (Exception e) {
                throw new GenerateException("Cannot load: " + apiSource.getSwaggerInternalFilter(), e);
            }
        }

        swagger = resolveApiReader().read(getValidClasses());

        if (apiSource.getSecurityDefinitions() != null) {
            for (SecurityDefinition sd : apiSource.getSecurityDefinitions()) {
                for (Map.Entry<String, SecuritySchemeDefinition> entry : sd.getDefinitions().entrySet()) {
                    swagger.addSecurityDefinition(entry.getKey(), entry.getValue());
                }
            }
        }

        // sort security defs to make output consistent
        Map<String, SecuritySchemeDefinition> defs = swagger.getSecurityDefinitions();
        if (defs != null) {
            Map<String, SecuritySchemeDefinition> sortedDefs = new TreeMap<String, SecuritySchemeDefinition>();
            sortedDefs.putAll(defs);
            swagger.setSecurityDefinitions(sortedDefs);
        }

        if (FilterFactory.getFilter() != null) {
            swagger = new SpecFilter().filter(swagger, FilterFactory.getFilter(),
                    new HashMap<String, List<String>>(), new HashMap<String, String>(),
                    new HashMap<String, List<String>>());
        }
    }

    protected Sets.SetView<Class<?>> getValidClasses()
    {
        return Sets.union(
                apiSource.getValidClasses(Api.class),
                apiSource.getValidClasses(Path.class));
    }

    protected ClassSwaggerReader resolveApiReader() throws GenerateException {
        String customReaderClassName = apiSource.getSwaggerApiReader();
        if (customReaderClassName == null) {
            JaxrsReader reader = new JaxrsReader(swagger, LOG);
            reader.setTypesToSkip(this.typesToSkip);
            return reader;
        } else {
            return getCustomApiReader(customReaderClassName);
        }
    }
}
