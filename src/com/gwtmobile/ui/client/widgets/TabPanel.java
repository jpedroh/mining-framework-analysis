  package     com . gwtmobile . ui . client . widgets ;   import       com . google . gwt . event . dom . client . ClickEvent ;  import       com . google . gwt . event . dom . client . ClickHandler ;  import       com . google . gwt . event . logical . shared . HasSelectionHandlers ;  import       com . google . gwt . event . logical . shared . SelectionEvent ;  import       com . google . gwt . event . logical . shared . SelectionHandler ;  import      com . google . gwt . event . shared . HandlerRegistration ;  import       com . google . gwt . user . client . ui . Widget ;  import   java . beans . Beans ;  import       com . gwtmobile . ui . client . CSS . StyleNames . Primary ;  import       com . gwtmobile . ui . client . CSS . StyleNames . Secondary ;   public class TabPanel  extends PanelBase  implements   HasSelectionHandlers  < Integer > , ClickHandler  {   private TabHeaderPanel  _tabHeaderPanel =  new FlowPanel  ( ) ;   private TabContentPanel  _tabContentPanel =  new FlowPanel  ( ) ;   private  int  _selectedTabIndex =  - 1 ;   private  int  _defaultTab = 0 ;   public TabPanel  ( )  {   setStyleName  (  Primary . TabPanel ) ;   addStyleName  (  _tabPosition . toString  ( ) ) ; }    @ Override public void add  (  Widget w )  {  if  (  w instanceof TabHeaderPanel )  {  if  (  _tabHeaderPanel == null )  {   _tabHeaderPanel =  ( TabHeaderPanel ) w ;   _tabHeaderPanel . addDomHandler  ( this ,  ClickEvent . getType  ( ) ) ;   super . add  ( _tabHeaderPanel ) ;  return ; } else  {  assert false : "The TabPanel can only contain one TabHeaderPanel" ; } }  if  (  w instanceof TabContentPanel )  {  if  (  _tabContentPanel == null )  {   _tabContentPanel =  ( TabContentPanel ) w ;   super . add  ( _tabContentPanel ) ;  return ; } else  {  assert false : "The TabPanel can only contain one TabContentPanel" ; } }  if  (  isDesignTimeEmptyLabel  ( w ) )  {   super . add  ( w ) ;  return ; }  assert false :   "TabPanel can only contains a TabHeaderPanel and a TabContentPanel. (" +   w . getClass  ( ) . getName  ( ) + ")" ; }    @ Override public void onInitialLoad  ( )  {   int  widgetCount =  _tabHeaderPanel . getWidgetCount  ( ) ;  if  (  
<<<<<<<
widgetCount
=======
 _tabHeaderPanel != null
>>>>>>>
 &&   _tabHeaderPanel . getWidgetCount  ( ) > 0 )  {  if  (  
<<<<<<<
_defaultTab
=======
 Beans . isDesignTime  ( )
>>>>>>>
 
<<<<<<<
<
=======
&&
>>>>>>>
 
<<<<<<<
widgetCount
=======
 _selectedTabIndex >  - 1
>>>>>>>
 ) 
<<<<<<<
  selectTab  ( _defaultTab ) ;
=======
 {   selectTab  ( _selectedTabIndex ) ; }
>>>>>>>
 else 
<<<<<<<
  selectTab  ( 0 ) ;
=======
 {   selectTab  ( _defaultTabIndex ) ; }
>>>>>>>
 } }   public void selectTab  (   int index )  {  if  (  _selectedTabIndex == index )  {  return ; }  if  (  _selectedTabIndex !=  - 1 )  {   _tabHeaderPanel . unSelectHeader  ( _selectedTabIndex ) ; }   _tabHeaderPanel . selectHeader  ( index ) ;  if  (  _tabContentPanel != null )   _tabContentPanel . selectTab  ( _selectedTabIndex , index ) ;   _selectedTabIndex = index ;   SelectionEvent . fire  ( this , _selectedTabIndex ) ; }   public  int getSelectedTabIndex  ( )  {  return _selectedTabIndex ; }   public TabHeader getSelectedTab  ( )  {  return  ( TabHeader )  _tabHeaderPanel . getWidget  ( _selectedTabIndex ) ; }    @ Override public void onClick  (  ClickEvent event )  {   int  index =  _tabHeaderPanel . getClickedTabHeaderIndex  ( event ) ;  if  (  index !=  - 1 )  {   selectTab  ( index ) ; } }    @ Override public HandlerRegistration addSelectionHandler  (   SelectionHandler  < Integer > handler )  {  return  this . addHandler  ( handler ,  SelectionEvent . getType  ( ) ) ; }   public void setTabBarPanel  (  boolean isTabBarPanel )  {  if  ( isTabBarPanel )  {   addStyleName  (  Primary . TabBarPanel ) ; } else  {   removeStyleName  (  Primary . TabBarPanel ) ; } }   public void setDefaultTab  (   int defaultTab )  {   _defaultTab = defaultTab ; }   public enum TabPosition  {  Top ,  Bottom } ;   private  int  _defaultTabIndex = 0 ;   private boolean  _fullHeight = false ;   private TabPosition  _tabPosition =  TabPosition . Top ;   public void setSelectedTabIndex  (   int index )  {   selectTab  ( index ) ; }   public TabContent getSelectedTabContent  ( )  {  return  _tabContentPanel . getSelectedTabContent  ( ) ; }   public TabPosition getTabPosition  ( )  {  return _tabPosition ; }   public void setTabPosition  (  TabPosition tabsPosition )  {    this . _tabPosition = tabsPosition ;  if  (   tabsPosition ==  TabPosition . Bottom &&   getWidget  ( 0 ) == _tabHeaderPanel )  {   super . clear  ( ) ;   super . add  ( _tabContentPanel ) ;   super . add  ( _tabHeaderPanel ) ;   addStyleName  (  Secondary . Bottom ) ;   removeStyleName  (  Secondary . Top ) ; } else  if  (   tabsPosition ==  TabPosition . Top &&   getWidget  ( 0 ) == _tabContentPanel )  {   super . clear  ( ) ;   super . add  ( _tabHeaderPanel ) ;   super . add  ( _tabContentPanel ) ;   addStyleName  (  Secondary . Top ) ;   removeStyleName  (  Secondary . Bottom ) ; } }   public  int getDefaultTabIndex  ( )  {  return _defaultTabIndex ; }   public void setDefaultTabIndex  (   int defaultTabIndex )  {    this . _defaultTabIndex = defaultTabIndex ; }   public boolean isFullHeight  ( )  {  return _fullHeight ; }   public void setFullHeight  (  boolean fullHeight )  {    this . _fullHeight = fullHeight ;  if  ( fullHeight )  {   addStyleName  (  Secondary . FullHeight ) ; } else  {   removeStyleName  (  Secondary . FullHeight ) ; } }    @ Override protected String getDesignTimeMessage  ( )  {  return "Add a TabHeaderPanel and a TabContentPanel." ; } }