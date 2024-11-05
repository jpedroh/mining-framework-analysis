package org.omnifaces.resourcehandler;
import static org.omnifaces.util.Events.subscribeToApplicationEvent;
import static org.omnifaces.util.Faces.evaluateExpressionGet;
import static org.omnifaces.util.Faces.getInitParameter;
import static org.omnifaces.util.Faces.isDevelopment;
import static org.omnifaces.util.Utils.isNumber;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.omnifaces.component.output.cache.Cache;
import org.omnifaces.component.script.DeferredScript;
import org.omnifaces.renderer.DeferredScriptRenderer;
import org.omnifaces.renderer.InlineScriptRenderer;
import org.omnifaces.renderer.InlineStylesheetRenderer;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Hacks;
import static org.omnifaces.util.Renderers.RENDERER_TYPE_CSS;
import static org.omnifaces.util.Renderers.RENDERER_TYPE_JS;

public class CombinedResourceHandler extends DefaultResourceHandler implements SystemEventListener {
  public static final String LIBRARY_NAME = "omnifaces.combined";

  public static final String PARAM_NAME_DISABLED = "org.omnifaces.COMBINED_RESOURCE_HANDLER_DISABLED";

  public static final String PARAM_NAME_EXCLUDED_RESOURCES = "org.omnifaces.COMBINED_RESOURCE_HANDLER_EXCLUDED_RESOURCES";

  public static final String PARAM_NAME_SUPPRESSED_RESOURCES = "org.omnifaces.COMBINED_RESOURCE_HANDLER_SUPPRESSED_RESOURCES";

  public static final String PARAM_NAME_INLINE_CSS = "org.omnifaces.COMBINED_RESOURCE_HANDLER_INLINE_CSS";

  public static final String PARAM_NAME_INLINE_JS = "org.omnifaces.COMBINED_RESOURCE_HANDLER_INLINE_JS";

  public static final String PARAM_NAME_CACHE_TTL = "org.omnifaces.COMBINED_RESOURCE_HANDLER_CACHE_TTL";

  private static final String ERROR_INVALID_CACHE_TTL_PARAM = "Context parameter \'" + PARAM_NAME_CACHE_TTL + "\' is in invalid syntax." + " It must represent a valid time in seconds between 0 and " + Integer.MAX_VALUE + "." + " Encountered an invalid value of \'%s\'.";

  private static final String TARGET_HEAD = "head";

  private static final String TARGET_BODY = "body";

  private String disabledParam;

  private Set<ResourceIdentifier> excludedResources;

  private Set<ResourceIdentifier> suppressedResources;

  private boolean inlineCSS;

  private boolean inlineJS;

  private Integer cacheTTL;

  public CombinedResourceHandler(ResourceHandler wrapped) {
    super(wrapped);
    disabledParam = getInitParameter(PARAM_NAME_DISABLED);
    excludedResources = initResources(PARAM_NAME_EXCLUDED_RESOURCES);
    excludedResources.addAll(initCDNResources());
    suppressedResources = initResources(PARAM_NAME_SUPPRESSED_RESOURCES);
    excludedResources.addAll(suppressedResources);
    inlineCSS = Boolean.valueOf(getInitParameter(PARAM_NAME_INLINE_CSS));
    inlineJS = Boolean.valueOf(getInitParameter(PARAM_NAME_INLINE_JS));
    cacheTTL = initCacheTTL(getInitParameter(PARAM_NAME_CACHE_TTL));
    subscribeToApplicationEvent(PreRenderViewEvent.class, this);
  }

  @Override public boolean isListenerForSource(Object source) {
    return (source instanceof UIViewRoot);
  }

  @Override public void processEvent(SystemEvent event) throws AbortProcessingException {
    if (disabledParam != null && Boolean.valueOf(String.valueOf(evaluateExpressionGet(disabledParam)))) {
      return;
    }
    FacesContext context = FacesContext.getCurrentInstance();
    UIViewRoot view = context.getViewRoot();
    CombinedResourceBuilder builder = new CombinedResourceBuilder();
    for (UIComponent component : view.getComponentResources(context, TARGET_HEAD)) {
      if (component.getAttributes().get("name") == null) {
        continue;
      }
      builder.add(context, component, component.getRendererType(), new ResourceIdentifier(component), TARGET_HEAD);
    }
    for (UIComponent component : view.getComponentResources(context, TARGET_BODY)) {
      if (!(component instanceof DeferredScript)) {
        continue;
      }
      builder.add(context, component, component.getRendererType(), new ResourceIdentifier(component), TARGET_BODY);
    }
    builder.create(context);
  }

  @Override public String getLibraryName() {
    return LIBRARY_NAME;
  }

  @Override public Resource createResourceFromLibrary(String resourceName, String contentType) {
    return new CombinedResource(resourceName, cacheTTL);
  }

  private static Set<ResourceIdentifier> initResources(String name) {
    Set<ResourceIdentifier> resources = new HashSet<ResourceIdentifier>(1);
    String configuredResources = getInitParameter(name);
    if (configuredResources != null) {
      for (String resourceIdentifier : configuredResources.split("\\s*,\\s*")) {
        resources.add(new ResourceIdentifier(resourceIdentifier));
      }
    }
    return resources;
  }

  private static Set<ResourceIdentifier> initCDNResources() {
    Map<ResourceIdentifier, String> cdnResources = CDNResourceHandler.initCDNResources();
    return (cdnResources != null) ? cdnResources.keySet() : Collections.<ResourceIdentifier>emptySet();
  }

  private static Integer initCacheTTL(String cacheTTLParam) {
    if (!isDevelopment() && cacheTTLParam != null) {
      if (isNumber(cacheTTLParam)) {
        int cacheTTL = Integer.valueOf(cacheTTLParam);
        if (cacheTTL > 0) {
          return cacheTTL;
        }
      }
      throw new IllegalArgumentException(String.format(ERROR_INVALID_CACHE_TTL_PARAM, cacheTTLParam));
    } else {
      return null;
    }
  }

  private final class CombinedResourceBuilder {
    private static final String EXTENSION_CSS = ".css";

    private static final String EXTENSION_JS = ".js";

    private CombinedResourceBuilder stylesheets;

    private CombinedResourceBuilder scripts;

    private Map<String, CombinedResourceBuilder> deferredScripts;

    private List<UIComponent> componentResourcesToRemove;

    public CombinedResourceBuilder() {
      stylesheets = new CombinedResourceBuilder(EXTENSION_CSS, TARGET_HEAD);
      scripts = new CombinedResourceBuilder(EXTENSION_JS, TARGET_HEAD);
      deferredScripts = new LinkedHashMap<String, CombinedResourceBuilder>();
      componentResourcesToRemove = new ArrayList<UIComponent>();
    }

    private void add(FacesContext context, UIComponent component, String rendererType, ResourceIdentifier id, String target) {
      if (LIBRARY_NAME.equals(id.getLibrary())) {
        addCombined(context, component, rendererType, id, target);
      } else {
        if (rendererType.equals(RENDERER_TYPE_CSS)) {
          addStylesheet(context, component, id);
        } else {
          if (rendererType.equals(RENDERER_TYPE_JS)) {
            addScript(context, component, id);
          } else {
            if (component instanceof DeferredScript) {
              addDeferredScript(component, id);
            } else {
              if (Hacks.isRichFacesResourceLibraryRenderer(rendererType)) {
                Set<ResourceIdentifier> resourceIdentifiers = Hacks.getRichFacesResourceLibraryResources(id);
                ResourceHandler handler = context.getApplication().getResourceHandler();
                for (ResourceIdentifier identifier : resourceIdentifiers) {
                  add(context, null, handler.getRendererTypeForResourceName(identifier.getName()), identifier, target);
                }
                componentResourcesToRemove.add(component);
              }
            }
          }
        }
      }
    }

    private void addCombined(FacesContext context, UIComponent component, String rendererType, ResourceIdentifier id, String target) {
      String[] resourcePathParts = id.getName().split("\\.", 2)[0].split("/");
      String resourceId = resourcePathParts[resourcePathParts.length - 1];
      CombinedResourceInfo info = CombinedResourceInfo.get(resourceId);
      boolean added = false;
      if (info != null) {
        for (ResourceIdentifier combinedId : info.getResourceIdentifiers()) {
          add(context, added ? null : component, rendererType, combinedId, target);
          added = true;
        }
      }
      if (!added) {
        componentResourcesToRemove.add(component);
      }
    }

    private void addStylesheet(FacesContext context, UIComponent component, ResourceIdentifier id) {
      if (stylesheets.add(component, id)) {
        Hacks.setStylesheetResourceRendered(context, id);
      }
    }

    private void addScript(FacesContext context, UIComponent component, ResourceIdentifier id) {
      if (Hacks.isScriptResourceRendered(context, id)) {
        componentResourcesToRemove.add(component);
      } else {
        if (scripts.add(component, id)) {
          Hacks.setScriptResourceRendered(context, id);
        }
      }
    }

    private void addDeferredScript(UIComponent component, ResourceIdentifier id) {
      String group = (String) component.getAttributes().get("group");
      CombinedResourceBuilder builder = deferredScripts.get(group);
      if (builder == null) {
        builder = new CombinedResourceBuilder(EXTENSION_JS, TARGET_BODY);
        deferredScripts.put(group, builder);
      }
      builder.add(component, id);
    }

    public void create(FacesContext context) {
      stylesheets.create(context, inlineCSS ? InlineStylesheetRenderer.RENDERER_TYPE : RENDERER_TYPE_CSS);
      scripts.create(context, inlineJS ? InlineScriptRenderer.RENDERER_TYPE : RENDERER_TYPE_JS);
      for (CombinedResourceBuilder builder : deferredScripts.values()) {
        builder.create(context, DeferredScriptRenderer.RENDERER_TYPE);
      }
      removeComponentResources(context, componentResourcesToRemove, TARGET_HEAD);
    }

    private String extension;

    private String target;

    private CombinedResourceInfo.Builder infoBuilder;

    private UIComponent componentResource;

    private CombinedResourceBuilder(String extension, String target) {
      this.extension = extension;
      this.target = target;
      infoBuilder = new CombinedResourceInfo.Builder();
      componentResourcesToRemove = new ArrayList<UIComponent>();
    }

    private boolean add(UIComponent componentResource, ResourceIdentifier resourceIdentifier) {
      if (componentResource != null && !componentResource.isRendered()) {
        componentResourcesToRemove.add(componentResource);
        return true;
      } else {
        if (!containsResourceIdentifier(excludedResources, resourceIdentifier)) {
          infoBuilder.add(resourceIdentifier);
          if (this.componentResource == null) {
            this.componentResource = componentResource;
          } else {
            if (componentResource instanceof DeferredScript) {
              mergeAttribute(this.componentResource, componentResource, "onbegin");
              mergeAttribute(this.componentResource, componentResource, "onsuccess");
              mergeAttribute(this.componentResource, componentResource, "onerror");
            }
            componentResourcesToRemove.add(componentResource);
          }
          return true;
        } else {
          if (containsResourceIdentifier(suppressedResources, resourceIdentifier)) {
            componentResourcesToRemove.add(componentResource);
            return true;
          }
        }
      }
      return false;
    }

    private void mergeAttribute(UIComponent originalComponent, UIComponent newComponent, String name) {
      String originalAttribute = getAttribute(originalComponent, name);
      String newAttribute = getAttribute(newComponent, name);
      String separator = (originalAttribute.isEmpty() || originalAttribute.endsWith(";") ? "" : ";");
      originalComponent.getAttributes().put(name, originalAttribute + separator + newAttribute);
    }

    private String getAttribute(UIComponent component, String name) {
      String attribute = (String) component.getAttributes().get(name);
      return (attribute == null) ? "" : attribute.trim();
    }

    private void create(FacesContext context, String rendererType) {
      if (!infoBuilder.isEmpty()) {
        if (componentResource == null) {
          componentResource = new UIOutput();
          context.getViewRoot().addComponentResource(context, componentResource, target);
        }
        componentResource.getAttributes().put("library", LIBRARY_NAME);
        componentResource.getAttributes().put("name", infoBuilder.create() + extension);
        componentResource.setRendererType(rendererType);
      }
      removeComponentResources(context, componentResourcesToRemove, target);
    }

    private boolean containsResourceIdentifier(Set<ResourceIdentifier> ids, ResourceIdentifier id) {
      return !ids.isEmpty() && (ids.contains(id) || ids.contains(new ResourceIdentifier(id.getLibrary(), "*")));
    }
  }

  private static void removeComponentResources(FacesContext context, List<UIComponent> componentResourcesToRemove, String target) {
    UIViewRoot view = context.getViewRoot();
    for (UIComponent resourceToRemove : componentResourcesToRemove) {
      if (resourceToRemove != null) {
        resourceToRemove.setInView(false);
        view.removeComponentResource(context, resourceToRemove, target);
      }
    }
  }
}