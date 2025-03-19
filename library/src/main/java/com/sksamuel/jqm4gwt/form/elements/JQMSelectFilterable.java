package com.sksamuel.jqm4gwt.form.elements;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.OptionElement;
import com.sksamuel.jqm4gwt.JQMCommon;

/**
 * See <a href="http://demos.jquerymobile.com/1.4.5/selectmenu-custom-filter/">Filterable inside custom select</a>
 * <br> See also <a href="https://github.com/jquery/jquery-mobile/blob/9cb1040f1da9309c30b70eccbbfb54a8ddf253aa/demos/selectmenu-custom-filter/index.php">Github Demos</a>
 *
 * @author SlavaP
 */
public class JQMSelectFilterable extends JQMSelect {
  private static final String SELECT_FILTERABLE_STYLENAME = "jqm4gwt-select-filterable";

  private String menuStyleNames;

  private Element listView;

  public JQMSelectFilterable() {
    super();
    addStyleName(SELECT_FILTERABLE_STYLENAME);
    setNative(false);
  }

  public String getMenuStyleNames() {
    return menuStyleNames;
  }

  /**
     * There is predefined ui-select-filterable CSS class added to menu dialog/popup.
     * <br> You can style listview by defining rule .ui-selectmenu.ui-select-filterable .ui-selectmenu-list { ... }
     * <br> For additional flexibility you can specify custom classes to be added together with ui-select-filterable
     * @param dialogStyleNames - space separated custom classes
     */
  public void setMenuStyleNames(String dialogStyleNames) {
    this.menuStyleNames = dialogStyleNames;
  }

  @Override public Boolean doFiltering(Element elt, Integer index, String searchValue) {
    String s = JQMCommon.getFilterText(elt);
    if (s == null || s.isEmpty()) {
      s = JQMCommon.getAttribute(elt, "data-option-index");
      if (s != null && !s.isEmpty()) {
        try {
          int i = Integer.parseInt(s);
          OptionElement opt = getOption(i);
          if (opt != null) {
            s = JQMCommon.getFilterText(opt);
            if (s != null && !s.isEmpty()) {
              JQMCommon.setFilterText(elt, s);
            }
          }
        } catch (NumberFormatException ex) {
        }
      }
    }
    return super.doFiltering(elt, index, searchValue);
  }

  private void setListView(Element listViewElt) {
    listView = listViewElt;
    if (listView != null && JQMCommon.isFilterableReady(listView)) {
      JavaScriptObject origFilter = JQMCommon.getFilterCallback(listView);
      JQMCommon.bindFilterCallback(this, listView, origFilter);
    }
  }

  @Override protected void onLoad() {
    super.onLoad();
    bindLifecycleEvents(select.getElement().getId(), this);
  }

  @Override protected void onUnload() {
    unbindLifecycleEvents(select.getElement().getId());
    super.onUnload();
  }

  private static native void bindLifecycleEvents(String id, JQMSelectFilterable combo);

  private static native void unbindLifecycleEvents(String id);
}