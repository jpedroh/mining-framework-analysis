package org.omnifaces.resourcehandler;
import static org.omnifaces.util.Faces.evaluateExpressionGet;
import static org.omnifaces.util.Faces.getInitParameter;
import static org.omnifaces.util.Utils.isEmpty;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.Resource;
import javax.faces.application.ResourceDependency;
import javax.faces.application.ResourceHandler;

public class CDNResourceHandler extends DefaultResourceHandler {
  public static final String PARAM_NAME_CDN_RESOURCES = "org.omnifaces.CDN_RESOURCE_HANDLER_URLS";

  public static final String PARAM_NAME_CDN_DISABLED = "org.omnifaces.CDN_RESOURCE_HANDLER_DISABLED";

  private static final String ERROR_MISSING_INIT_PARAM = "Context parameter \'" + PARAM_NAME_CDN_RESOURCES + "\' is missing in web.xml or web-fragment.xml.";

  private static final String ERROR_INVALID_INIT_PARAM = "Context parameter \'" + PARAM_NAME_CDN_RESOURCES + "\' is in invalid syntax." + " It must follow \'resourceId=URL,resourceId=URL,resourceId=URL\' syntax.";

  private static final String ERROR_INVALID_WILDCARD = "Context parameter \'" + PARAM_NAME_CDN_RESOURCES + "\' is in invalid syntax." + " Wildcard can only represent entire resource name \'*\' and URL suffix \'/*\' as in" + " \'libraryName:*=http://cdn.example.com/*\'.";

  private String disabledParam;

  private Map<ResourceIdentifier, String> cdnResources;

  public CDNResourceHandler(ResourceHandler wrapped) {
    super(wrapped);
    disabledParam = getInitParameter(PARAM_NAME_CDN_DISABLED);
    cdnResources = initCDNResources();
    if (cdnResources == null) {
      throw new IllegalArgumentException(ERROR_MISSING_INIT_PARAM);
    }
  }

  @Override public Resource decorateResource(Resource resource) {
    if (resource == null || (disabledParam != null && Boolean.valueOf(String.valueOf(evaluateExpressionGet(disabledParam))))) {
      return resource;
    }
    String requestPath = null;
    if (cdnResources != null) {
      String libraryName = resource.getLibraryName();
      String resourceName = resource.getResourceName();
      requestPath = cdnResources.get(new ResourceIdentifier(libraryName, resourceName));
      if (requestPath == null) {
        requestPath = cdnResources.get(new ResourceIdentifier(libraryName, "*"));
        if (requestPath != null) {
          requestPath = requestPath.substring(0, requestPath.length() - 1) + resourceName;
        }
      }
    }
    if (requestPath == null) {
      return resource;
    }
    String evaluatedRequestPath = evaluateExpressionGet(requestPath);
    return new RemappedResource(resource, evaluatedRequestPath);
  }

  static Map<ResourceIdentifier, String> initCDNResources() {
    String cdnResourcesParam = getInitParameter(PARAM_NAME_CDN_RESOURCES);
    if (isEmpty(cdnResourcesParam)) {
      return null;
    }
    Map<ResourceIdentifier, String> cdnResources = new HashMap<ResourceIdentifier, String>();
    for (String cdnResource : cdnResourcesParam.split("\\s*,\\s*")) {
      String[] cdnResourceIdAndURL = cdnResource.split("\\s*=\\s*", 2);
      if (cdnResourceIdAndURL.length != 2) {
        throw new IllegalArgumentException(ERROR_INVALID_INIT_PARAM);
      }
      ResourceIdentifier id = new ResourceIdentifier(cdnResourceIdAndURL[0]);
      if (id.getName().contains("*") && (!id.getName().equals("*") || !cdnResourceIdAndURL[1].endsWith("/*"))) {
        throw new IllegalArgumentException(ERROR_INVALID_WILDCARD);
      }
      cdnResources.put(id, cdnResourceIdAndURL[1]);
    }
    return cdnResources;
  }
}