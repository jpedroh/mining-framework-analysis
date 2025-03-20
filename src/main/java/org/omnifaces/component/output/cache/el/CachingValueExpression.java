package org.omnifaces.component.output.cache.el;
import static org.omnifaces.util.Faces.getContext;
import java.util.Objects;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.omnifaces.component.output.Cache;
import org.omnifaces.el.ValueExpressionWrapper;

/**
 * A value expression implementation that caches its main value at the moment it's evaluated and uses
 * this cache value in future evaluations.
 *
 * @author Arjan Tijms
 *
 */
public class CachingValueExpression extends ValueExpressionWrapper {
  private static final long serialVersionUID = -3172741983469325940L;

  private final String name;

  private final Cache cache;

  public CachingValueExpression(String name, ValueExpression valueExpression, Cache cache) {
    super(valueExpression);
    this.name = name;
    this.cache = cache;
  }

  @Override public Object getValue(ELContext elContext) {
    FacesContext facesContext = getContext(elContext);
    Object value = cache.getCacheAttribute(facesContext, name);
    if (value == null) {
      value = super.getValue(elContext);
      cache.setCacheAttribute(facesContext, name, value);
    }
    return value;
  }

  @Override public boolean equals(Object object) {
    return super.equals(object) && Objects.equals(name, ((CachingValueExpression) object).name);
  }

  @Override public int hashCode() {
    return super.hashCode() + Objects.hashCode(name);
  }
}