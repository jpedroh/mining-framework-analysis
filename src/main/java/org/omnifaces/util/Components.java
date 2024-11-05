package org.omnifaces.util;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.quote;
import static javax.faces.component.visit.VisitContext.createVisitContext;
import static javax.faces.component.visit.VisitResult.ACCEPT;
import static org.omnifaces.util.Faces.getContext;
import static org.omnifaces.util.Faces.getELContext;
import static org.omnifaces.util.Faces.getFaceletContext;
import static org.omnifaces.util.Faces.getViewRoot;
import static org.omnifaces.util.Utils.isEmpty;
import static org.omnifaces.util.Utils.isOneInstanceOf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.view.facelets.FaceletContext;
import org.omnifaces.component.ParamHolder;
import org.omnifaces.component.SimpleParam;
import static org.omnifaces.util.Faces.setContext;
import static org.omnifaces.util.FacesLocal.getRenderKit;
import static org.omnifaces.util.FacesLocal.normalizeViewId;
import static org.omnifaces.util.Renderers.RENDERER_TYPE_JS;
import java.io.StringWriter;
import java.util.HashMap;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContextWrapper;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.view.ViewDeclarationLanguage;

public final class Components {
  private static final String ERROR_INVALID_PARENT = "Component \'%s\' must have a parent of type \'%s\', but it cannot be found.";

  private static final String ERROR_INVALID_DIRECT_PARENT = "Component \'%s\' must have a direct parent of type \'%s\', but it cannot be found.";

  private static final String ERROR_CHILDREN_DISALLOWED = "Component \'%s\' must have no children. Encountered children of types \'%s\'.";

  private Components() {
  }

  public static UIComponent getCurrentComponent() {
    return UIComponent.getCurrentComponent(getContext());
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getAttribute(UIComponent component, String name) {
    return (T) component.getAttributes().get(name);
  }

  public static boolean isRendered(UIComponent component) {
    for (UIComponent current = component; current.getParent() != null; current = current.getParent()) {
      if (!current.isRendered()) {
        return false;
      }
    }
    return true;
  }

  @SuppressWarnings(value = { "unchecked" }) public static <C extends UIComponent> C findComponent(String clientId) {
    return (C) getViewRoot().findComponent(clientId);
  }

  @SuppressWarnings(value = { "unchecked" }) public static <C extends UIComponent> C findComponentRelatively(UIComponent component, String clientId) {
    if (isEmpty(clientId)) {
      return null;
    }
    UIComponent result = findComponentInParents(component, clientId);
    if (result == null) {
      result = findComponentInChildren(getViewRoot(), clientId);
    }
    return (C) result;
  }

  @SuppressWarnings(value = { "unchecked" }) public static <C extends UIComponent> C findComponentInParents(UIComponent component, String clientId) {
    if (isEmpty(clientId)) {
      return null;
    }
    UIComponent parent = component;
    while (parent != null) {
      UIComponent result = null;
      if (parent instanceof NamingContainer) {
        try {
          result = parent.findComponent(clientId);
        } catch (IllegalArgumentException e) {
          continue;
        }
      }
      if (result != null) {
        return (C) result;
      }
      parent = parent.getParent();
    }
    return null;
  }

  @SuppressWarnings(value = { "unchecked" }) public static <C extends UIComponent> C findComponentInChildren(UIComponent component, String clientId) {
    if (isEmpty(clientId)) {
      return null;
    }
    for (UIComponent child : component.getChildren()) {
      UIComponent result = null;
      if (child instanceof NamingContainer) {
        try {
          result = child.findComponent(clientId);
        } catch (IllegalArgumentException e) {
          continue;
        }
      }
      if (result == null) {
        result = findComponentInChildren(child, clientId);
      }
      if (result != null) {
        return (C) result;
      }
    }
    return null;
  }

  public static <C extends UIComponent> List<C> findComponentsInChildren(UIComponent component, Class<C> type) {
    List<C> components = new ArrayList<C>();
    findComponentsInChildren(component, type, components);
    return components;
  }

  @SuppressWarnings(value = { "unchecked" }) private static <C extends UIComponent> void findComponentsInChildren(UIComponent component, Class<C> type, List<C> matches) {
    for (UIComponent child : component.getChildren()) {
      if (type.isInstance(child)) {
        matches.add((C) child);
      }
      findComponentsInChildren(child, type, matches);
    }
  }

  public static <C extends UIComponent> C getClosestParent(UIComponent component, Class<C> parentType) {
    UIComponent parent = component.getParent();
    while (parent != null && !parentType.isInstance(parent)) {
      parent = parent.getParent();
    }
    return parentType.cast(parent);
  }

  public static boolean shouldVisitSkipIteration(VisitContext context) {
    try {
      return context.getHints().contains(VisitHint.valueOf("SKIP_ITERATION"));
    } catch (IllegalArgumentException e) {
      return context.getFacesContext().getAttributes().get("javax.faces.visit.SKIP_ITERATION") == Boolean.TRUE;
    }
  }

  public static ForEach forEachComponent() {
    return new ForEach();
  }

  public static ForEach forEachComponent(FacesContext facesContext) {
    return new ForEach(facesContext);
  }

  public static class ForEach {
    private FacesContext facesContext;

    private UIComponent root;

    private Collection<String> ids;

    private Set<VisitHint> hints;

    private Class<?>[] types;

    public ForEach() {
      facesContext = Faces.getContext();
    }

    public ForEach(FacesContext facesContext) {
      this.facesContext = facesContext;
    }

    public ForEach fromRoot(UIComponent root) {
      this.root = root;
      return this;
    }

    public ForEach havingIds(Collection<String> ids) {
      this.ids = ids;
      return this;
    }

    public ForEach havingIds(String... ids) {
      this.ids = asList(ids);
      return this;
    }

    public ForEach withHints(Set<VisitHint> hints) {
      this.hints = hints;
      return this;
    }

    public ForEach withHints(VisitHint... hints) {
      if (hints.length > 0) {
        EnumSet<VisitHint> hintsSet = EnumSet.noneOf(hints[0].getDeclaringClass());
        for (VisitHint hint : hints) {
          hintsSet.add(hint);
        }
        this.hints = hintsSet;
      }
      return this;
    }

    public final ForEach ofTypes(Class<?>... types) {
      this.types = types;
      return this;
    }

    public void invoke(final Callback.WithArgument<UIComponent> operation) {
      invoke(new VisitCallback() {
        @Override public VisitResult visit(VisitContext context, UIComponent target) {
          operation.invoke(target);
          return ACCEPT;
        }
      });
    }

    public void invoke(final Callback.ReturningWithArgument<VisitResult, UIComponent> operation) {
      invoke(new VisitCallback() {
        @Override public VisitResult visit(VisitContext context, UIComponent target) {
          return operation.invoke(target);
        }
      });
    }

    public void invoke(final VisitCallback operation) {
      VisitCallback callback = operation;
      if (types != null) {
        callback = new TypesVisitCallback(types, callback);
      }
      getRoot().visitTree(createVisitContext(getContext(), getIds(), getHints()), callback);
    }

    protected FacesContext getFacesContext() {
      return facesContext;
    }

    protected UIComponent getRoot() {
      return root != null ? root : getFacesContext().getViewRoot();
    }

    protected Collection<String> getIds() {
      return ids;
    }

    protected Set<VisitHint> getHints() {
      return hints;
    }

    private static class TypesVisitCallback implements VisitCallback {
      private Class<?>[] types;

      private VisitCallback next;

      public TypesVisitCallback(Class<?>[] types, VisitCallback next) {
        this.types = types;
        this.next = next;
      }

      @Override public VisitResult visit(VisitContext context, UIComponent target) {
        if (isOneInstanceOf(target.getClass(), types)) {
          return next.visit(context, target);
        }
        return ACCEPT;
      }
    }
  }

  public static void includeFacelet(UIComponent parent, String path) throws IOException {
    getFaceletContext().includeFacelet(parent, path);
  }

  public static UIComponent includeCompositeComponent(UIComponent parent, String libraryName, String tagName, String id) {
    return includeCompositeComponent(parent, libraryName, tagName, id, null);
  }

  public static UIForm getCurrentForm() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.isPostback()) {
      return null;
    }
    UIViewRoot viewRoot = context.getViewRoot();
    for (String name : context.getExternalContext().getRequestParameterMap().keySet()) {
      if (name.startsWith("javax.faces.")) {
        continue;
      }
      UIComponent component = findComponentIgnoringIAE(viewRoot, stripIterationIndexFromClientId(name));
      if (component instanceof UIForm) {
        return (UIForm) component;
      } else {
        if (component != null) {
          UIForm form = getClosestParent(component, UIForm.class);
          if (form != null) {
            return form;
          }
        }
      }
    }
    return null;
  }

  public static UICommand getCurrentCommand() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.isPostback()) {
      return null;
    }
    UIViewRoot viewRoot = context.getViewRoot();
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();
    if (context.getPartialViewContext().isAjaxRequest()) {
      String source = params.get("javax.faces.source");
      if (source != null) {
        UIComponent component = findComponentIgnoringIAE(viewRoot, stripIterationIndexFromClientId(source));
        if (component instanceof UICommand) {
          return (UICommand) component;
        }
      }
    }
    for (String name : params.keySet()) {
      if (name.startsWith("javax.faces.")) {
        continue;
      }
      UIComponent component = findComponentIgnoringIAE(viewRoot, stripIterationIndexFromClientId(name));
      if (component instanceof UICommand) {
        return (UICommand) component;
      }
    }
    return null;
  }

  public static boolean isEditable(UIInput input) {
    return input.isRendered() && !Boolean.TRUE.equals(input.getAttributes().get("disabled")) && !Boolean.TRUE.equals(input.getAttributes().get("readonly"));
  }

  public static String getLabel(UIComponent input) {
    String label = getOptionalLabel(input);
    return (label != null) ? label : input.getClientId();
  }

  public static String getOptionalLabel(UIComponent input) {
    Object label = input.getAttributes().get("label");
    if (Utils.isEmpty(label)) {
      ValueExpression labelExpression = input.getValueExpression("label");
      if (labelExpression != null) {
        label = labelExpression.getValue(getELContext());
      }
    }
    return (label != null) ? label.toString() : null;
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getValue(EditableValueHolder component) {
    Object submittedValue = component.getSubmittedValue();
    return (T) ((submittedValue != null) ? submittedValue : component.getLocalValue());
  }

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> T getImmediateValue(UIInput input) {
    if (input.isValid() && input.getSubmittedValue() != null) {
      input.validate(getContext());
    }
    return input.isLocalValueSet() ? (T) input.getValue() : null;
  }

  public static boolean hasSubmittedValue(EditableValueHolder component) {
    return !Utils.isEmpty(component.getSubmittedValue());
  }

  public static boolean hasInvokedSubmit(UIComponent component) {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.isPostback()) {
      return false;
    }
    String clientId = stripIterationIndexFromClientId(component.getClientId(context));
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();
    if (context.getPartialViewContext().isAjaxRequest()) {
      String source = params.get("javax.faces.source");
      if (source != null) {
        return clientId.equals(stripIterationIndexFromClientId(source));
      }
    }
    if (component instanceof UICommand) {
      for (String name : params.keySet()) {
        if (name.startsWith("javax.faces.")) {
          continue;
        }
        if (clientId.equals(stripIterationIndexFromClientId(name))) {
          return true;
        }
      }
    }
    return false;
  }

  public static List<ParamHolder> getParams(UIComponent component) {
    if (component.getChildCount() > 0) {
      List<ParamHolder> params = new ArrayList<ParamHolder>(component.getChildCount());
      for (UIComponent child : component.getChildren()) {
        if (child instanceof UIParameter) {
          UIParameter param = (UIParameter) child;
          if (!isEmpty(param.getName()) && !param.isDisable()) {
            params.add(new SimpleParam(param));
          }
        }
      }
      return Collections.unmodifiableList(params);
    } else {
      return Collections.emptyList();
    }
  }

  public static ValueExpression createValueExpression(String expression, Class<?> type) {
    FacesContext context = FacesContext.getCurrentInstance();
    return context.getApplication().getExpressionFactory().createValueExpression(context.getELContext(), expression, type);
  }

  public static MethodExpression createMethodExpression(String expression, Class<?> returnType, Class<?>... parameterTypes) {
    FacesContext context = FacesContext.getCurrentInstance();
    return context.getApplication().getExpressionFactory().createMethodExpression(context.getELContext(), expression, returnType, parameterTypes);
  }

  public static MethodExpression createVoidMethodExpression(String expression, Class<?>... parameterTypes) {
    return createMethodExpression(expression, Void.class, parameterTypes);
  }

  public static MethodExpressionActionListener createActionListenerMethodExpression(String expression) {
    return new MethodExpressionActionListener(createVoidMethodExpression(expression, ActionEvent.class));
  }

  public static AjaxBehavior createAjaxBehavior(String expression) {
    FacesContext context = FacesContext.getCurrentInstance();
    AjaxBehavior behavior = (AjaxBehavior) context.getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
    final MethodExpression method = createVoidMethodExpression(expression, AjaxBehaviorEvent.class);
    behavior.addAjaxBehaviorListener(new AjaxBehaviorListener() {
      @Override public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
        method.invoke(getELContext(), new Object[] { event });
      }
    });
    return behavior;
  }

  public static <C extends UIComponent> void validateHasParent(UIComponent component, Class<C> parentType) throws IllegalArgumentException {
    if (getClosestParent(component, parentType) == null) {
      throw new IllegalArgumentException(String.format(ERROR_INVALID_PARENT, component.getClass().getSimpleName(), parentType));
    }
  }

  public static <C extends UIComponent> void validateHasDirectParent(UIComponent component, Class<C> parentType) throws IllegalArgumentException {
    if (!parentType.isInstance(component.getParent())) {
      throw new IllegalArgumentException(String.format(ERROR_INVALID_DIRECT_PARENT, component.getClass().getSimpleName(), parentType));
    }
  }

  public static void validateHasNoChildren(UIComponent component) throws IllegalArgumentException {
    if (component.getChildCount() > 0) {
      StringBuilder childClassNames = new StringBuilder();
      for (UIComponent child : component.getChildren()) {
        if (childClassNames.length() > 0) {
          childClassNames.append(", ");
        }
        childClassNames.append(child.getClass().getName());
      }
      throw new IllegalArgumentException(String.format(ERROR_CHILDREN_DISALLOWED, component.getClass().getSimpleName(), childClassNames));
    }
  }

  private static String stripIterationIndexFromClientId(String clientId) {
    String separatorChar = Character.toString(UINamingContainer.getSeparatorChar(getContext()));
    return clientId.replaceAll(quote(separatorChar) + "[0-9]+" + quote(separatorChar), separatorChar);
  }

  private static UIComponent findComponentIgnoringIAE(UIViewRoot viewRoot, String clientId) {
    try {
      return viewRoot.findComponent(clientId);
    } catch (IllegalArgumentException ignore) {
      return null;
    }
  }

  public static UIComponent includeCompositeComponent(UIComponent parent, String libraryName, String tagName, String id, Map<String, String> attributes) {
    String taglibURI = "http://xmlns.jcp.org/jsf/composite/" + libraryName;
    Map<String, Object> attrs = (attributes == null) ? null : new HashMap<String, Object>(attributes);
    FacesContext context = FacesContext.getCurrentInstance();
    UIComponent composite = context.getApplication().getViewHandler().getViewDeclarationLanguage(context, context.getViewRoot().getViewId()).createComponent(context, taglibURI, tagName, attrs);
    composite.setId(id);
    parent.getChildren().add(composite);
    return composite;
  }

  public static UIComponent addScriptToBody(String script) {
    UIOutput outputScript = createScriptResource();
    UIOutput content = new UIOutput();
    content.setValue(script);
    outputScript.getChildren().add(content);
    return addComponentResource(outputScript, "body");
  }

  public static UIComponent addScriptResourceToBody(String libraryName, String resourceName) {
    return addScriptResource(libraryName, resourceName, "body");
  }

  public static UIComponent addScriptResourceToHead(String libraryName, String resourceName) {
    return addScriptResource(libraryName, resourceName, "head");
  }

  private static UIOutput createScriptResource() {
    UIOutput outputScript = new UIOutput();
    outputScript.setRendererType(RENDERER_TYPE_JS);
    return outputScript;
  }

  private static UIComponent addScriptResource(String libraryName, String resourceName, String target) {
    UIOutput outputScript = createScriptResource();
    if (libraryName != null) {
      outputScript.getAttributes().put("library", libraryName);
    }
    outputScript.getAttributes().put("name", resourceName);
    return addComponentResource(outputScript, target);
  }

  private static UIComponent addComponentResource(UIComponent resource, String target) {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getViewRoot().addComponentResource(context, resource, target);
    return resource;
  }

  public static UIViewRoot buildView(String viewId) throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    String normalizedViewId = normalizeViewId(context, viewId);
    ViewHandler viewHandler = context.getApplication().getViewHandler();
    UIViewRoot view = viewHandler.createView(context, normalizedViewId);
    FacesContext temporaryContext = new TemporaryViewFacesContext(context, view);
    try {
      setContext(temporaryContext);
      viewHandler.getViewDeclarationLanguage(temporaryContext, normalizedViewId).buildView(temporaryContext, view);
    }  finally {
      setContext(context);
    }
    return view;
  }

  public static String encodeHtml(UIComponent component) throws IOException {
    FacesContext context = FacesContext.getCurrentInstance();
    ResponseWriter originalWriter = context.getResponseWriter();
    StringWriter output = new StringWriter();
    context.setResponseWriter(getRenderKit(context).createResponseWriter(output, "text/html", "UTF-8"));
    try {
      component.encodeAll(context);
    }  finally {
      if (originalWriter != null) {
        context.setResponseWriter(originalWriter);
      }
    }
    return output.toString();
  }

  private static class TemporaryViewFacesContext extends FacesContextWrapper {
    private FacesContext wrapped;

    private UIViewRoot temporaryView;

    public TemporaryViewFacesContext(FacesContext wrapped, UIViewRoot temporaryView) {
      this.wrapped = wrapped;
      this.temporaryView = temporaryView;
    }

    @Override public UIViewRoot getViewRoot() {
      return temporaryView;
    }

    @Override public RenderKit getRenderKit() {
      return FacesLocal.getRenderKit(this);
    }

    @Override public FacesContext getWrapped() {
      return wrapped;
    }
  }
}