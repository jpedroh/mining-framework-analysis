  package     com . sksamuel . jqm4gwt . form . elements ;   import      com . google . gwt . dom . client . Document ;  import       com . google . gwt . event . dom . client . DomEvent ;  import      com . google . gwt . event . shared . GwtEvent ;  import      com . google . gwt . event . shared . HandlerRegistration ;  import    com . sksamuel . jqm4gwt . JQMCommon ;  import     com . sksamuel . jqm4gwt . events . JQMChangeHandler ;  import     com . sksamuel . jqm4gwt . events . JQMComponentEvents ;  import     com . sksamuel . jqm4gwt . events . JQMEvent ;  import     com . sksamuel . jqm4gwt . events . JQMEventFactory ;  import     com . sksamuel . jqm4gwt . events . JQMHandlerRegistration ;  import      com . sksamuel . jqm4gwt . events . JQMHandlerRegistration . WidgetHandlerCounter ;  import     com . sksamuel . jqm4gwt . events . JQMInputHandler ;   public class JQMSearch  extends JQMText  {   public JQMSearch  ( )  {  this  ( null ) ; }   public JQMSearch  (  String text )  {  super  ( text ) ;   setType  ( "search" ) ;   initChangeHandler  ( ) ; }    @ Override public String getValue  ( )  {  return  JQMCommon . getVal  (  getInputId  ( ) ) ; }    @ Override public void setValue  (  String value ,  boolean fireEvents )  {   JQMCommon . setVal  (  getInputId  ( ) , value ) ;  if  ( fireEvents )   DomEvent . fireNativeEvent  (   Document . get  ( ) . createChangeEvent  ( ) , input ) ; }   private void initChangeHandler  ( )  {  JQMChangeHandler  handler =  new JQMChangeHandler  ( )  {    @ Override public void onEvent  (   JQMEvent  <  ? > event )  {   DomEvent . fireNativeEvent  (   Document . get  ( ) . createChangeEvent  ( ) , input ) ; } } ;   JQMHandlerRegistration . registerJQueryHandler  (  new WidgetHandlerCounter  ( )  {    @ Override public  int getHandlerCountForWidget  (    GwtEvent . Type  <  ? > type )  {  return  getHandlerCount  ( type ) ; } } , this , handler ,  JQMComponentEvents . CHANGE ,  JQMEventFactory . getType  (  JQMComponentEvents . CHANGE ,  JQMChangeHandler . class ) ) ; }   public HandlerRegistration addInputHandler  (  JQMInputHandler handler )  {  if  (  handler == null )  return null ;  return  JQMHandlerRegistration . registerJQueryHandler  (  new WidgetHandlerCounter  ( )  {    @ Override public  int getHandlerCountForWidget  (    GwtEvent . Type  <  ? > type )  {  return  getHandlerCount  ( type ) ; } } , this , handler ,  JQMComponentEvents . INPUT ,  JQMEventFactory . getType  (  JQMComponentEvents . INPUT ,  JQMInputHandler . class ) ) ; } }