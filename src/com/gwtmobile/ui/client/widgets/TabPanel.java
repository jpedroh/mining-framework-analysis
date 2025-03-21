package com.gwtmobile.ui.client.widgets;
import java.beans.Beans;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.CSS.StyleNames.Primary;
import com.gwtmobile.ui.client.CSS.StyleNames.Secondary;

public class TabPanel extends PanelBase implements HasSelectionHandlers<Integer>, ClickHandler {
  public enum TabPosition {
    Top,
    Bottom
  }



  private TabPosition _tabPosition = TabPosition.Top;

  private TabHeaderPanel _tabHeaderPanel;

  private TabContentPanel _tabContentPanel;

  private int _selectedTabIndex = -1;

  private boolean _fullHeight = false;

  private int 
<<<<<<< /usr/src/app/output/dennisjzh/gwtmobile-ui/b1d29b7602f643ed5c7dd04acaa1d63631910a61/src/com/gwtmobile/ui/client/widgets/TabPanel.java/left.java
  _defaultTab = 0
=======
  _defaultTabIndex = 0
>>>>>>> /usr/src/app/output/dennisjzh/gwtmobile-ui/b1d29b7602f643ed5c7dd04acaa1d63631910a61/src/com/gwtmobile/ui/client/widgets/TabPanel.java/right.java
  ;

  public TabPanel() {
    setStyleName(Primary.TabPanel);
    addStyleName(_tabPosition.toString());
  }

  @Override public void add(Widget w) {
    if (w instanceof TabHeaderPanel) {
      if (_tabHeaderPanel == null) {
        _tabHeaderPanel = (TabHeaderPanel) w;
        _tabHeaderPanel.addDomHandler(this, ClickEvent.getType());
        super.add(_tabHeaderPanel);
        return;
      } else {
        assert false : "The TabPanel can only contain one TabHeaderPanel";
      }
    }
    if (w instanceof TabContentPanel) {
      if (_tabContentPanel == null) {
        _tabContentPanel = (TabContentPanel) w;
        super.add(_tabContentPanel);
        return;
      } else {
        assert false : "The TabPanel can only contain one TabContentPanel";
      }
    }
    if (isDesignTimeEmptyLabel(w)) {
      super.add(w);
      return;
    }
    assert false : "TabPanel can only contains a TabHeaderPanel and a TabContentPanel. (" + w.getClass().getName() + ")";
  }

  @Override public void onInitialLoad() {
    int widgetCount = _tabHeaderPanel.getWidgetCount();
    if (
<<<<<<< /usr/src/app/output/dennisjzh/gwtmobile-ui/b1d29b7602f643ed5c7dd04acaa1d63631910a61/src/com/gwtmobile/ui/client/widgets/TabPanel.java/left.java
    widgetCount > 0
=======
    _tabHeaderPanel != null && _tabHeaderPanel.getWidgetCount() > 0
>>>>>>> /usr/src/app/output/dennisjzh/gwtmobile-ui/b1d29b7602f643ed5c7dd04acaa1d63631910a61/src/com/gwtmobile/ui/client/widgets/TabPanel.java/right.java
    ) {

<<<<<<< /usr/src/app/output/dennisjzh/gwtmobile-ui/b1d29b7602f643ed5c7dd04acaa1d63631910a61/src/com/gwtmobile/ui/client/widgets/TabPanel.java/left.java
      if (_defaultTab < widgetCount) {
        selectTab(_defaultTab);
      } else {
        selectTab(0);
      }
=======
      if (Beans.isDesignTime() && _selectedTabIndex > -1) {
        selectTab(_selectedTabIndex);
      } else {
        selectTab(_defaultTabIndex);
      }
>>>>>>> /usr/src/app/output/dennisjzh/gwtmobile-ui/b1d29b7602f643ed5c7dd04acaa1d63631910a61/src/com/gwtmobile/ui/client/widgets/TabPanel.java/right.java
    }
  }

  public void selectTab(int index) {
    if (_selectedTabIndex == index) {
      return;
    }
    if (_selectedTabIndex != -1) {
      _tabHeaderPanel.unSelectHeader(_selectedTabIndex);
    }
    _tabHeaderPanel.selectHeader(index);
    if (_tabContentPanel != null) {
      _tabContentPanel.selectTab(_selectedTabIndex, index);
    }
    _selectedTabIndex = index;
    SelectionEvent.fire(this, _selectedTabIndex);
  }

  public int getSelectedTabIndex() {
    return _selectedTabIndex;
  }

  public void setSelectedTabIndex(int index) {
    selectTab(index);
  }

  public TabHeader getSelectedTab() {
    return (TabHeader) _tabHeaderPanel.getWidget(_selectedTabIndex);
  }

  public TabContent getSelectedTabContent() {
    return _tabContentPanel.getSelectedTabContent();
  }

  @Override public void onClick(ClickEvent event) {
    int index = _tabHeaderPanel.getClickedTabHeaderIndex(event);
    if (index != -1) {
      selectTab(index);
    }
  }

  @Override public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
    return this.addHandler(handler, SelectionEvent.getType());
  }

  public TabPosition getTabPosition() {
    return _tabPosition;
  }

  public void setTabPosition(TabPosition tabsPosition) {
    this._tabPosition = tabsPosition;
    if (tabsPosition == TabPosition.Bottom && getWidget(0) == _tabHeaderPanel) {
      super.clear();
      super.add(_tabContentPanel);
      super.add(_tabHeaderPanel);
      addStyleName(Secondary.Bottom);
      removeStyleName(Secondary.Top);
    } else {
      if (tabsPosition == TabPosition.Top && getWidget(0) == _tabContentPanel) {
        super.clear();
        super.add(_tabHeaderPanel);
        super.add(_tabContentPanel);
        addStyleName(Secondary.Top);
        removeStyleName(Secondary.Bottom);
      }
    }
  }

  public int getDefaultTabIndex() {
    return _defaultTabIndex;
  }

  public void setDefaultTabIndex(int defaultTabIndex) {
    this._defaultTabIndex = defaultTabIndex;
  }

  public boolean isFullHeight() {
    return _fullHeight;
  }

  public void setFullHeight(boolean fullHeight) {
    this._fullHeight = fullHeight;
    if (fullHeight) {
      addStyleName(Secondary.FullHeight);
    } else {
      removeStyleName(Secondary.FullHeight);
    }
  }

  public void setTabBarPanel(boolean isTabBarPanel) {
    if (isTabBarPanel) {
      addStyleName(Primary.TabBarPanel);
    } else {
      removeStyleName(Primary.TabBarPanel);
    }
  }

  /**
	 * Sets the default tab.
	 *
	 * @param defaultTab the new default tab
	 * 
	 * Frank Mena
	 */
  public void setDefaultTab(int defaultTab) {
    _defaultTab = defaultTab;
  }

  @Override protected String getDesignTimeMessage() {
    return "Add a TabHeaderPanel and a TabContentPanel.";
  }
}