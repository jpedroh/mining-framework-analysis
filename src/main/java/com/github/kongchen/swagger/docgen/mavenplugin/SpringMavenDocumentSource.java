package com.github.kongchen.swagger.docgen.mavenplugin;

import java.util.Set;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.web.bind.annotation.RestController;

import com.github.kongchen.swagger.docgen.AbstractDocumentSource;
import com.github.kongchen.swagger.docgen.GenerateException;
import com.github.kongchen.swagger.docgen.reader.ClassSwaggerReader;
import com.github.kongchen.swagger.docgen.reader.SpringMvcApiReader;
import com.google.common.collect.Sets;

/**
 * @author tedleman
 *         01/21/15
 * @author chekong
 * 05/13/2013
 */
public class SpringMavenDocumentSource extends AbstractDocumentSource {

    public SpringMavenDocumentSource(ApiSource apiSource, Log log, String encoding) throws MojoFailureException {
        super(log, apiSource);
        if(encoding !=null) {
            this.encoding = encoding;
        }
    }

<<<<<<< /usr/src/app/output/kongchen/swagger-maven-plugin/d2e1b0994d1d9cfdfc412842519319406128f268/src/main/java/com/github/kongchen/swagger/docgen/mavenplugin/SpringMavenDocumentSource.java/left.java
    protected Set<Class<?>> getValidClasses() {
||||||| /usr/src/app/output/kongchen/swagger-maven-plugin/d2e1b0994d1d9cfdfc412842519319406128f268/src/main/java/com/github/kongchen/swagger/docgen/mavenplugin/SpringMavenDocumentSource.java/base.java
     Set<Class<?>> getValidClasses() {
=======
    @Override
    public Set<Class<?>> getValidClasses() {
>>>>>>> /usr/src/app/output/kongchen/swagger-maven-plugin/d2e1b0994d1d9cfdfc412842519319406128f268/src/main/java/com/github/kongchen/swagger/docgen/mavenplugin/SpringMavenDocumentSource.java/right.java
        return Sets.union(
            super.getValidClasses(),
            apiSource.getValidClasses(RestController.class));
    }

    @Override
    protected ClassSwaggerReader resolveApiReader() throws GenerateException {
        String customReaderClassName = apiSource.getSwaggerApiReader();
        if (customReaderClassName == null) {
            SpringMvcApiReader reader = new SpringMvcApiReader(swagger, LOG);
            reader.setTypesToSkip(this.typesToSkip);
            return reader;
        } else {
            return getCustomApiReader(customReaderClassName);
        }
    }
}
