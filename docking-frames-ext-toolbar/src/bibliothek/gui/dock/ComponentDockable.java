  package   bibliothek . gui . dock ;   import   java . awt . CardLayout ;  import   java . awt . Component ;  import   java . awt . Container ;  import   java . awt . Dimension ;  import   java . util . ArrayList ;  import   java . util . List ;  import   javax . swing . Icon ;  import   javax . swing . JComponent ;  import   javax . swing . JPanel ;  import    javax . swing . event . MouseInputListener ;  import   bibliothek . gui . DockController ;  import   bibliothek . gui . DockStation ;  import   bibliothek . gui . Dockable ;  import   bibliothek . gui . ToolbarInterface ;  import   bibliothek . gui . ToolbarElementInterface ;  import     bibliothek . gui . dock . dockable . AbstractDockable ;  import     bibliothek . gui . dock . dockable . DockableIcon ;  import      bibliothek . gui . dock . station . toolbar . ToolbarPartDockFactory ;  import      bibliothek . gui . dock . station . toolbar . ToolbarStrategy ;  import      bibliothek . gui . dock . toolbar . expand . ExpandableStateController ;  import      bibliothek . gui . dock . toolbar . expand . ExpandableToolbarItem ;  import      bibliothek . gui . dock . toolbar . expand . ExpandableToolbarItemListener ;  import      bibliothek . gui . dock . toolbar . expand . ExpandedState ;  import     bibliothek . gui . dock . util . PropertyKey ;  import     bibliothek . gui . dock . util . SilentPropertyValue ;  import      bibliothek . gui . dock . util . icon . DockIcon ;   public class ComponentDockable  extends AbstractDockable  implements  ToolbarElementInterface , ExpandableToolbarItem  {   private JPanel  content ;   private CardLayout  contentLayout ;   private final  List  < ExpandableToolbarItemListener >  expandableListeners =  new  ArrayList  < ExpandableToolbarItemListener >  ( ) ;   private ExpandedState  state =  ExpandedState . SHRUNK ;   private final  Component  [ ]  components =  new Component  [   ExpandedState . values  ( ) . length ] ;   private final  List  < MouseInputListener >  mouseListeners =  new  ArrayList  < MouseInputListener >  ( ) ;   public ComponentDockable  ( )  {  this  ( null , null , null ) ; }   public ComponentDockable  (  Icon icon )  {  this  ( null , null , icon ) ; }   public ComponentDockable  (  String title )  {  this  ( null , title , null ) ; }   public ComponentDockable  (  Component component )  {  this  ( component , null , null ) ; }   public ComponentDockable  (  Component component ,  Icon icon )  {  this  ( component , null , icon ) ; }   public ComponentDockable  (  Component component ,  String title )  {  this  ( component , title , null ) ; }   public ComponentDockable  (  Component component ,  String title ,  Icon icon )  {  super  (  PropertyKey . DOCKABLE_TITLE ,  PropertyKey . DOCKABLE_TOOLTIP ) ;   contentLayout =  new CardLayout  ( )  {    @ Override public Dimension preferredLayoutSize  (  Container parent )  {  synchronized  (  parent . getTreeLock  ( ) )  {   final Component  current =  getNearestComponent  ( state ) ;  if  (  current == null )  {  return  new Dimension  ( 10 , 10 ) ; }  return  current . getPreferredSize  ( ) ; } }    @ Override public Dimension minimumLayoutSize  (  Container parent )  {  synchronized  (  parent . getTreeLock  ( ) )  {   final Component  current =  getNearestComponent  ( state ) ;  if  (  current == null )  {  return  new Dimension  ( 10 , 10 ) ; }  return  current . getMinimumSize  ( ) ; } }    @ Override public Dimension maximumLayoutSize  (  Container parent )  {  synchronized  (  parent . getTreeLock  ( ) )  {   final Component  current =  getNearestComponent  ( state ) ;  if  (  current == null )  {  return  new Dimension  ( 10 , 10 ) ; }  return  current . getMaximumSize  ( ) ; } } } ;   content =  new JPanel  ( contentLayout ) ;   new ExpandableStateController  ( this ) ;  if  (  component != null )  {   setComponent  ( component ,  ExpandedState . SHRUNK ) ; }   setTitleIcon  ( icon ) ;   setTitleText  ( title ) ; }   private Component getNearestComponent  (  ExpandedState state )  {   int  index =  state . ordinal  ( ) ;  while  (  index >= 0 )  {  if  (   components [ index ] != null )  {  return  components [ index ] ; }   index -- ; }   index =   state . ordinal  ( ) + 1 ;  while  (  index <  components . length )  {  if  (   components [ index ] != null )  {  return  components [ index ] ; }   index ++ ; }  return null ; }   private ExpandedState getNearestState  (  ExpandedState state )  {   final Component  nearest =  getNearestComponent  ( state ) ;  if  (  nearest == null )  {  return null ; }  for (  final ExpandedState next :  ExpandedState . values  ( ) )  {  if  (   components [  next . ordinal  ( ) ] == nearest )  {  return next ; } }  return null ; }    @ Override public void addMouseInputListener  (  MouseInputListener listener )  {   super . addMouseInputListener  ( listener ) ;   mouseListeners . add  ( listener ) ;  for (  final Component component : components )  {  if  (  component != null )  {   component . addMouseListener  ( listener ) ;   component . addMouseMotionListener  ( listener ) ; } } }    @ Override public void removeMouseInputListener  (  MouseInputListener listener )  {   super . removeMouseInputListener  ( listener ) ;   mouseListeners . remove  ( listener ) ;  for (  final Component component : components )  {  if  (  component != null )  {   component . removeMouseListener  ( listener ) ;   component . removeMouseMotionListener  ( listener ) ; } } }   public void setComponent  (  Component component ,  ExpandedState state )  {   final Component  previous =  components [  state . ordinal  ( ) ] ;  if  (  previous != component )  {  if  (  previous != null )  {   content . remove  ( previous ) ;  for (  final MouseInputListener listener : mouseListeners )  {   previous . removeMouseListener  ( listener ) ;   previous . removeMouseMotionListener  ( listener ) ; } }    components [  state . ordinal  ( ) ] = component ;  if  (  component != null )  {   content . add  ( component ,  state . toString  ( ) ) ;  for (  final MouseInputListener listener : mouseListeners )  {   component . addMouseListener  ( listener ) ;   component . addMouseMotionListener  ( listener ) ; } }   final ExpandedState  nearest =  getNearestState  (  this . state ) ;  if  (  nearest != null )  {   contentLayout . show  ( content ,  nearest . toString  ( ) ) ;   content . revalidate  ( ) ; } } }    @ Override public void setExpandedState  (  ExpandedState state )  {  if  (   this . state != state )  {   final ExpandedState  oldState =  this . state ;    this . state = state ;   final ExpandedState  nearest =  getNearestState  ( state ) ;  if  (  nearest != null )  {   contentLayout . show  ( content ,  nearest . toString  ( ) ) ; }   content . revalidate  ( ) ;  for (  final ExpandableToolbarItemListener listener :  expandableListeners . toArray  (  new ExpandableToolbarItemListener  [  expandableListeners . size  ( ) ] ) )  {   listener . changed  ( this , oldState , state ) ; } } }    @ Override public ExpandedState getExpandedState  ( )  {  return state ; }    @ Override public Component getComponent  ( )  {  return content ; }    @ Override public void addExpandableListener  (  ExpandableToolbarItemListener listener )  {  if  (  listener == null )  {  throw  new IllegalArgumentException  ( "listener must not be null" ) ; }   expandableListeners . add  ( listener ) ; }    @ Override public void removeExpandableListener  (  ExpandableToolbarItemListener listener )  {   expandableListeners . remove  ( listener ) ; }    @ Override public DockStation asDockStation  ( )  {  return null ; }    @ Override public String getFactoryID  ( )  {  return  ToolbarPartDockFactory . ID ; }    @ Override protected DockIcon createTitleIcon  ( )  {  return  new DockableIcon  ( "dockable.default" , this )  {    @ Override protected void changed  (  Icon oldValue ,  Icon newValue )  {   fireTitleIconChanged  ( oldValue , newValue ) ; } } ; }    @ Override public boolean accept  (  DockStation station )  {   final  SilentPropertyValue  < ToolbarStrategy >  value =  new  SilentPropertyValue  < ToolbarStrategy >  (  ToolbarStrategy . STRATEGY ,  getController  ( ) ) ;   final ToolbarStrategy  strategy =  value . getValue  ( ) ;   value . setProperties  (  ( DockController ) null ) ;  return  strategy . isToolbarGroupPartParent  ( station , this , false ) ; }    @ Override public boolean accept  (  DockStation base ,  Dockable neighbour )  {  return false ; }    @ Override public String toString  ( )  {  return     this . getClass  ( ) . getSimpleName  ( ) + '@' +  Integer . toHexString  (  hashCode  ( ) ) ; } }