package org.jboss.seam.render.template.resolver;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import org.jboss.seam.render.spi.TemplateResolver;
import org.jboss.seam.render.spi.TemplateResource;
import org.jboss.seam.render.util.Assert;
import org.jboss.seam.solder.util.service.ServiceLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Singleton @SuppressWarnings(value = { "rawtypes" }) public class TemplateResolverFactory implements TemplateResolver<Object> {
  ServiceLoader<TemplateResolver> resolvers = null;

  Set<TemplateResolver> addedResolvers = new HashSet<TemplateResolver>();

  public void addResolver(final TemplateResolver resolver) {
    addedResolvers.add(resolver);
  }

  @Override @SuppressWarnings(value = { "unchecked" }) public TemplateResource resolve(final String target) throws TemplateResolutionException {
    Assert.notNull(target, "Target resource must not be null.");
    loadResolvers();
    TemplateResource<?> resource = null;
    for (TemplateResolver<?> resolver : addedResolvers) {
      resource = resolver.resolve(target);
      if (resource != null) {
        break;
      }
    }
    if (resource == null) {
      for (TemplateResolver<?> resolver : resolvers) {
        resource = resolver.resolve(target);
        if (resource != null) {
          break;
        }
      }
    }
    if (resource == null) {
      throw new TemplateResolutionException("Could not load requested resource: [" + target + "] with any configured resolvers:" + resolvers + ", " + addedResolvers);
    }
    return resource;
  }

  @Override @SuppressWarnings(value = { "unchecked" }) public TemplateResource resolveRelative(final TemplateResource origin, final String relativePath) throws TemplateResolutionException {
    Assert.notNull(origin, "Origin resource was null when attempting to resolve [" + relativePath + "]");
    Assert.notNull(relativePath, "Relative resource path must not be null when attempting to resolve from base resource [" + origin.getPath() + "]");
    TemplateResource result = null;
    TemplateResolver resolver = origin.getResolvedBy();
    if (resolver != null) {
      result = resolver.resolveRelative(origin, relativePath);
    }
    if (result == null) {
      result = resolve(relativePath);
    }
    if (result == null) {
      throw new TemplateResolutionException("Could not load requested resource: [" + relativePath + "] using origin resolver [" + origin.getResolvedBy() + "] from resource [" + origin.getPath() + "]");
    }
    return result;
  }

  private void loadResolvers() {
    if (resolvers == null) {
      resolvers = ServiceLoader.load(TemplateResolver.class);
    }
  }
}