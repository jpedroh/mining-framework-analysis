package org.hdiv.components;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.hdiv.util.ConstantsJsf;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.UtilsJsf;

/**
 * <p>
 * UIParameter component extension
 * </p>
 * <p>
 * This component is used to define the parameters of CommandLink and outputLink. It stores the real values as
 * component's attributes, storing them in the state.
 * </p>
 * <p>
 * This data will be used to validate that it matches the data received in the next request.
 * </p>
 * 
 * @author Gotzon Illarramendi
 * 
 */
public class UIParameterExtension extends UIParameter {
  /**
	 * Returns the value of the parameter for the requested row in the dataTable
	 * 
	 * @param parentClientId Parent ClientId
	 * @return parameter value
	 */
  @SuppressWarnings(value = { "unchecked" }) public Object getValue(String parentClientId) {
    Object val = this.getValue();
    Map<String, Object> values = (Map<String, Object>) this.getAttributes().get(ConstantsJsf.HDIV_ATTRIBUTE_KEY);
    if (values != null) {
      val = values.get(parentClientId);
    }
    return val;
  }

  @SuppressWarnings(value = { "unchecked" }) public void encodeBegin(FacesContext context) throws IOException {
    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
    String hdivParameter = HDIVUtil.getHdivStateParameterName(request);
    String name = this.getName();
    if (name != null && name.equals(hdivParameter)) {
    } else {
      UIComponent parent = this.getParent();
      String parentClientId = parent.getClientId(context);
      Map<String, Object> values = (Map<String, Object>) this.getAttributes().get(ConstantsJsf.HDIV_ATTRIBUTE_KEY);
      if (values == null) {
        values = new HashMap<String, Object>();
      }
      UIData uiDataComp = UtilsJsf.findParentUIData(this);
      if (uiDataComp != null) {
        int rowIndex = uiDataComp.getRowIndex();
        if (rowIndex < 0) {
          rowIndex = 0;
        }
      }
      Object val = this.getValue();
      values.put(parentClientId, val);
      this.getAttributes().put(ConstantsJsf.HDIV_ATTRIBUTE_KEY, values);
    }
    super.encodeBegin(context);
  }
}