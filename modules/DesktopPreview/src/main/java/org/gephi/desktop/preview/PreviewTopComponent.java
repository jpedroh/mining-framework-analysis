  package    org . gephi . desktop . preview ;   import   java . awt . BorderLayout ;  import   java . awt . CardLayout ;  import   java . awt . Color ;  import   java . awt . Dimension ;  import   java . awt . GraphicsDevice ;  import   java . awt . GraphicsEnvironment ;  import    java . awt . event . ActionEvent ;  import    java . awt . event . ActionListener ;  import   java . beans . PropertyChangeEvent ;  import   java . beans . PropertyChangeListener ;  import    java . lang . reflect . Field ;  import   javax . swing . BorderFactory ;  import   javax . swing . SwingUtilities ;  import   javax . swing . UIManager ;  import      org . gephi . desktop . preview . api . PreviewUIController ;  import      org . gephi . desktop . preview . api . PreviewUIModel ;  import     org . gephi . preview . api . G2DTarget ;  import     org . gephi . preview . api . PreviewController ;  import     org . gephi . preview . api . PreviewModel ;  import     org . gephi . preview . api . PreviewProperty ;  import     org . gephi . preview . api . RenderTarget ;  import     org . gephi . ui . components . JColorButton ;  import     org . gephi . ui . utils . UIUtils ;  import    org . jdesktop . swingx . JXBusyLabel ;  import     org . netbeans . api . settings . ConvertAsProperties ;  import    org . openide . awt . ActionID ;  import    org . openide . awt . ActionReference ;  import    org . openide . util . ImageUtilities ;  import    org . openide . util . Lookup ;  import    org . openide . util . NbBundle ;  import    org . openide . windows . TopComponent ;  import     org . gephi . preview . api . PreviewProperties ;    @ ConvertAsProperties  (  dtd = "-//org.gephi.desktop.preview//Preview//EN" ,  autostore = false )  @  TopComponent . Description  (  preferredID = "PreviewTopComponent" ,  iconBase = "org/gephi/desktop/preview/resources/preview.png" ,  persistenceType =  TopComponent . PERSISTENCE_ALWAYS )  @  TopComponent . Registration  (  mode = "editor" ,  openAtStartup = true ,  roles =  { "preview" } )  @ ActionID  (  category = "Window" ,  id = "org.gephi.desktop.preview.PreviewTopComponent" )  @ ActionReference  (  path = "Menu/Window" ,  position = 900 )  @  TopComponent . OpenActionRegistration  (  displayName = "#CTL_PreviewTopComponent" ,  preferredID = "PreviewTopComponent" ) public final class PreviewTopComponent  extends TopComponent  implements  PropertyChangeListener  {   private transient PreviewUIModel  model ;   private transient G2DTarget  target ;   private transient PreviewSketch  sketch ;   private   javax . swing . JButton  backgroundButton ;   private   javax . swing . JLabel  bannerLabel ;   private   javax . swing . JPanel  bannerPanel ;   private   javax . swing . JLabel  busyLabel ;   private   javax . swing . JButton  minusButton ;   private   javax . swing . JButton  plusButton ;   private   javax . swing . JPanel  previewPanel ;   private   javax . swing . JButton  refreshButton ;   private   javax . swing . JPanel  refreshPanel ;   private   javax . swing . JButton  resetZoomButton ;   private   javax . swing . JPanel  sketchPanel ;   private   javax . swing . JLabel  southBusyLabel ;   private   javax . swing . JToolBar  southToolbar ;   public PreviewTopComponent  ( )  {   initComponents  ( ) ;   setName  (  NbBundle . getMessage  (  PreviewTopComponent . class , "CTL_PreviewTopComponent" ) ) ;  if  (  UIUtils . isAquaLookAndFeel  ( ) )  {   previewPanel . setBackground  (  UIManager . getColor  ( "NbExplorerView.background" ) ) ; }  if  (  UIUtils . isAquaLookAndFeel  ( ) )  {   southToolbar . setBackground  (  UIManager . getColor  ( "NbExplorerView.background" ) ) ; }   bannerPanel . setVisible  ( false ) ;   backgroundButton . addPropertyChangeListener  (  JColorButton . EVENT_COLOR ,  new PropertyChangeListener  ( )  {    @ Override public void propertyChange  (  PropertyChangeEvent evt )  {  PreviewController  previewController =   Lookup . getDefault  ( ) . lookup  (  PreviewController . class ) ;  PreviewProperties  properties =   previewController . getModel  ( ) . getProperties  ( ) ;  Color  oldColor =  properties . getColorValue  (  PreviewProperty . BACKGROUND_COLOR ) ;  if  (   oldColor == null ||  !  oldColor . equals  (  evt . getNewValue  ( ) ) )  {   properties . putValue  (  PreviewProperty . BACKGROUND_COLOR ,  evt . getNewValue  ( ) ) ;  PreviewUIController  previewUIController =   Lookup . getDefault  ( ) . lookup  (  PreviewUIController . class ) ;   previewUIController . refreshPreview  ( ) ; } } } ) ;   southBusyLabel . setVisible  ( false ) ;   resetZoomButton . addActionListener  (  new ActionListener  ( )  {    @ Override public void actionPerformed  (  ActionEvent e )  {   sketch . resetZoom  ( ) ; } } ) ;   plusButton . addActionListener  (  new ActionListener  ( )  {    @ Override public void actionPerformed  (  ActionEvent e )  {   sketch . zoomPlus  ( ) ; } } ) ;   minusButton . addActionListener  (  new ActionListener  ( )  {    @ Override public void actionPerformed  (  ActionEvent e )  {   sketch . zoomMinus  ( ) ; } } ) ;  PreviewUIController  controller =   Lookup . getDefault  ( ) . lookup  (  PreviewUIController . class ) ;   controller . addPropertyChangeListener  ( this ) ;  PreviewUIModel  m =  controller . getModel  ( ) ;    this . model = m ;   initTarget  ( model ) ; }   protected static  float getScaleFactor  ( )  {  try  {  GraphicsDevice  graphicsDevice =   GraphicsEnvironment . getLocalGraphicsEnvironment  ( ) . getDefaultScreenDevice  ( ) ;  Field  field =  retrieveField  ( graphicsDevice , "scale" ) ;  if  (  field == null )  {   field =  retrieveField  ( graphicsDevice , "scaleX" ) ; }  if  (  field != null )  {   field . setAccessible  ( true ) ;  Object  scale =  field . get  ( graphicsDevice ) ;  if  (  scale instanceof Number )  {  return   (  ( Number ) scale ) . floatValue  ( ) ; } } }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }  return 1f ; }   protected static Field retrieveField  (  GraphicsDevice graphicsDevice ,  String name )  {  try  {  return   graphicsDevice . getClass  ( ) . getDeclaredField  ( name ) ; }  catch (   Exception e )  { }  return null ; }    @ Override public void propertyChange  (  PropertyChangeEvent evt )  {  if  (   evt . getPropertyName  ( ) . equals  (  PreviewUIController . SELECT ) )  {    this . model =  ( PreviewUIModel )  evt . getNewValue  ( ) ;   initTarget  ( model ) ; } else  if  (   evt . getPropertyName  ( ) . equals  (  PreviewUIController . REFRESHED ) )  {   SwingUtilities . invokeLater  (  new Runnable  ( )  {    @ Override public void run  ( )  {   target . refresh  ( ) ; } } ) ; } else  if  (   evt . getPropertyName  ( ) . equals  (  PreviewUIController . REFRESHING ) )  {   setRefresh  (  ( Boolean )  evt . getNewValue  ( ) ) ; } else  if  (   evt . getPropertyName  ( ) . equals  (  PreviewUIController . GRAPH_CHANGED ) )  {  if  (  ( Boolean )  evt . getNewValue  ( ) )  {   showBannerPanel  ( ) ; } else  {   hideBannerPanel  ( ) ; } } }   public void setRefresh  (   final boolean refresh )  {   SwingUtilities . invokeLater  (  new Runnable  ( )  {    @ Override public void run  ( )  {  CardLayout  cl =  ( CardLayout )  previewPanel . getLayout  ( ) ;   cl . show  ( previewPanel ,  refresh ? "refreshCard" : "previewCard" ) ;    (  ( JXBusyLabel ) busyLabel ) . setBusy  ( refresh ) ; } } ) ; }   protected Dimension getSketchDimensions  ( )  {   int  width =  sketchPanel . getWidth  ( ) ;   int  height =  sketchPanel . getHeight  ( ) ;  if  (   width > 1 &&  height > 1 )  {   float  scaleFactor =  getScaleFactor  ( ) ;  if  (  scaleFactor > 1f )  {   width =  (  int )  (  width * scaleFactor ) ;   height =  (  int )  (  height * scaleFactor ) ; }  return  new Dimension  ( width , height ) ; }  return  new Dimension  ( 1 , 1 ) ; }   public void initTarget  (  PreviewUIModel previewUIModel )  {  if  (  previewUIModel != null )  {  PreviewController  previewController =   Lookup . getDefault  ( ) . lookup  (  PreviewController . class ) ;  PreviewModel  previewModel =  previewUIModel . getPreviewModel  ( ) ;  Color  background =   previewModel . getProperties  ( ) . getColorValue  (  PreviewProperty . BACKGROUND_COLOR ) ;  if  (  background != null )  {   setBackgroundColor  ( background ) ; }  Dimension  dimensions =  getSketchDimensions  ( ) ;    previewModel . getProperties  ( ) . putValue  ( "width" ,  (  int )  dimensions . getWidth  ( ) ) ;    previewModel . getProperties  ( ) . putValue  ( "height" ,  (  int )  dimensions . getHeight  ( ) ) ;  if  (  sketch != null )  {   sketchPanel . remove  ( sketch ) ;   sketch = null ; }   target =  ( G2DTarget )  previewController . getRenderTarget  (  RenderTarget . G2D_TARGET ) ;  if  (  target != null )  {   sketch =  new PreviewSketch  ( target ) ;   sketchPanel . add  ( sketch ,  BorderLayout . CENTER ) ; }   plusButton . setEnabled  ( true ) ;   minusButton . setEnabled  ( true ) ;   backgroundButton . setEnabled  ( true ) ;   resetZoomButton . setEnabled  ( true ) ; } else  if  (  previewUIModel == null )  {  if  (  sketch != null )  {   sketchPanel . remove  ( sketch ) ;   sketch = null ; }   target = null ;   plusButton . setEnabled  ( false ) ;   minusButton . setEnabled  ( false ) ;   backgroundButton . setEnabled  ( false ) ;   resetZoomButton . setEnabled  ( false ) ; } }   public void showBannerPanel  ( )  {   SwingUtilities . invokeLater  (  new Runnable  ( )  {    @ Override public void run  ( )  {   bannerPanel . setVisible  ( true ) ; } } ) ; }   public void setBackgroundColor  (  Color color )  {    (  ( JColorButton ) backgroundButton ) . setColor  ( color ) ; }   public void hideBannerPanel  ( )  {   SwingUtilities . invokeLater  (  new Runnable  ( )  {    @ Override public void run  ( )  {   bannerPanel . setVisible  ( false ) ; } } ) ; }   private void initComponents  ( )  {    java . awt . GridBagConstraints  gridBagConstraints ;   southBusyLabel =  new JXBusyLabel  (  new Dimension  ( 14 , 14 ) ) ;   bannerPanel =  new   javax . swing . JPanel  ( ) ;   bannerLabel =  new   javax . swing . JLabel  ( ) ;   refreshButton =  new   javax . swing . JButton  ( ) ;   previewPanel =  new   javax . swing . JPanel  ( ) ;   sketchPanel =  new   javax . swing . JPanel  ( ) ;   refreshPanel =  new   javax . swing . JPanel  ( ) ;   busyLabel =  new JXBusyLabel  (  new Dimension  ( 20 , 20 ) ) ;   southToolbar =  new   javax . swing . JToolBar  ( ) ;   backgroundButton =  new JColorButton  (  Color . WHITE ) ;   resetZoomButton =  new   javax . swing . JButton  ( ) ;   minusButton =  new   javax . swing . JButton  ( ) ;   plusButton =  new   javax . swing . JButton  ( ) ;   setLayout  (  new   java . awt . GridBagLayout  ( ) ) ;   gridBagConstraints =  new   java . awt . GridBagConstraints  ( ) ;    gridBagConstraints . gridx = 0 ;    gridBagConstraints . gridy = 0 ;    gridBagConstraints . anchor =    java . awt . GridBagConstraints . FIRST_LINE_START ;    gridBagConstraints . insets =  new   java . awt . Insets  ( 5 , 5 , 0 , 0 ) ;   add  ( southBusyLabel , gridBagConstraints ) ;   bannerPanel . setBackground  (  new   java . awt . Color  ( 178 , 223 , 240 ) ) ;   bannerPanel . setBorder  (  BorderFactory . createMatteBorder  ( 0 , 0 , 1 , 0 ,  Color . BLACK ) ) ;   bannerPanel . setLayout  (  new   java . awt . GridBagLayout  ( ) ) ;   bannerLabel . setIcon  (  ImageUtilities . loadImageIcon  ( "org/gephi/desktop/preview/resources/info.png" , false ) ) ;      org . openide . awt . Mnemonics . setLocalizedText  ( bannerLabel ,     org . openide . util . NbBundle . getMessage  (  PreviewTopComponent . class , "PreviewTopComponent.bannerLabel.text" ) ) ;   gridBagConstraints =  new   java . awt . GridBagConstraints  ( ) ;    gridBagConstraints . gridx = 0 ;    gridBagConstraints . gridy = 0 ;    gridBagConstraints . anchor =    java . awt . GridBagConstraints . WEST ;    gridBagConstraints . weightx = 1.0 ;    gridBagConstraints . insets =  new   java . awt . Insets  ( 2 , 5 , 2 , 5 ) ;   bannerPanel . add  ( bannerLabel , gridBagConstraints ) ;      org . openide . awt . Mnemonics . setLocalizedText  ( refreshButton ,     org . openide . util . NbBundle . getMessage  (  PreviewTopComponent . class , "PreviewTopComponent.refreshButton.text" ) ) ;   refreshButton . addActionListener  (  new    java . awt . event . ActionListener  ( )  {    @ Override public void actionPerformed  (     java . awt . event . ActionEvent evt )  {   refreshButtonActionPerformed  ( evt ) ; } } ) ;   gridBagConstraints =  new   java . awt . GridBagConstraints  ( ) ;    gridBagConstraints . gridx = 1 ;    gridBagConstraints . gridy = 0 ;    gridBagConstraints . anchor =    java . awt . GridBagConstraints . EAST ;    gridBagConstraints . weightx = 1.0 ;    gridBagConstraints . insets =  new   java . awt . Insets  ( 1 , 0 , 1 , 1 ) ;   bannerPanel . add  ( refreshButton , gridBagConstraints ) ;   gridBagConstraints =  new   java . awt . GridBagConstraints  ( ) ;    gridBagConstraints . gridx = 0 ;    gridBagConstraints . gridy = 0 ;    gridBagConstraints . fill =    java . awt . GridBagConstraints . HORIZONTAL ;    gridBagConstraints . anchor =    java . awt . GridBagConstraints . NORTH ;   add  ( bannerPanel , gridBagConstraints ) ;   previewPanel . setLayout  (  new   java . awt . CardLayout  ( ) ) ;   sketchPanel . setBackground  (  new   java . awt . Color  ( 255 , 255 , 255 ) ) ;   sketchPanel . setPreferredSize  (  new   java . awt . Dimension  ( 500 , 500 ) ) ;   sketchPanel . setLayout  (  new   java . awt . BorderLayout  ( ) ) ;   previewPanel . add  ( sketchPanel , "previewCard" ) ;   refreshPanel . setOpaque  ( false ) ;   refreshPanel . setLayout  (  new   java . awt . GridBagLayout  ( ) ) ;   busyLabel . setHorizontalAlignment  (    javax . swing . SwingConstants . CENTER ) ;      org . openide . awt . Mnemonics . setLocalizedText  ( busyLabel ,     org . openide . util . NbBundle . getMessage  (  PreviewTopComponent . class , "PreviewTopComponent.busyLabel.text" ) ) ;   gridBagConstraints =  new   java . awt . GridBagConstraints  ( ) ;    gridBagConstraints . fill =    java . awt . GridBagConstraints . BOTH ;    gridBagConstraints . weightx = 1.0 ;    gridBagConstraints . weighty = 1.0 ;   refreshPanel . add  ( busyLabel , gridBagConstraints ) ;   previewPanel . add  ( refreshPanel , "refreshCard" ) ;   gridBagConstraints =  new   java . awt . GridBagConstraints  ( ) ;    gridBagConstraints . gridx = 0 ;    gridBagConstraints . gridy = 0 ;    gridBagConstraints . fill =    java . awt . GridBagConstraints . BOTH ;    gridBagConstraints . weightx = 1.0 ;    gridBagConstraints . weighty = 1.0 ;   add  ( previewPanel , gridBagConstraints ) ;   southToolbar . setFloatable  ( false ) ;   southToolbar . setRollover  ( true ) ;      org . openide . awt . Mnemonics . setLocalizedText  ( backgroundButton ,     org . openide . util . NbBundle . getMessage  (  PreviewTopComponent . class , "PreviewTopComponent.backgroundButton.text" ) ) ;   backgroundButton . setFocusable  ( false ) ;   southToolbar . add  ( backgroundButton ) ;      org . openide . awt . Mnemonics . setLocalizedText  ( resetZoomButton ,     org . openide . util . NbBundle . getMessage  (  PreviewTopComponent . class , "PreviewTopComponent.resetZoomButton.text" ) ) ;   resetZoomButton . setFocusable  ( false ) ;   resetZoomButton . setHorizontalTextPosition  (    javax . swing . SwingConstants . CENTER ) ;   resetZoomButton . setVerticalTextPosition  (    javax . swing . SwingConstants . BOTTOM ) ;   southToolbar . add  ( resetZoomButton ) ;      org . openide . awt . Mnemonics . setLocalizedText  ( minusButton , "-" ) ;   minusButton . setToolTipText  (     org . openide . util . NbBundle . getMessage  (  PreviewTopComponent . class , "PreviewTopComponent.minusButton.toolTipText" ) ) ;   minusButton . setFocusable  ( false ) ;   minusButton . setHorizontalTextPosition  (    javax . swing . SwingConstants . CENTER ) ;   minusButton . setVerticalTextPosition  (    javax . swing . SwingConstants . BOTTOM ) ;   southToolbar . add  ( minusButton ) ;      org . openide . awt . Mnemonics . setLocalizedText  ( plusButton , "+" ) ;   plusButton . setToolTipText  (     org . openide . util . NbBundle . getMessage  (  PreviewTopComponent . class , "PreviewTopComponent.plusButton.toolTipText" ) ) ;   plusButton . setFocusable  ( false ) ;   plusButton . setHorizontalTextPosition  (    javax . swing . SwingConstants . CENTER ) ;   plusButton . setVerticalTextPosition  (    javax . swing . SwingConstants . BOTTOM ) ;   southToolbar . add  ( plusButton ) ;   gridBagConstraints =  new   java . awt . GridBagConstraints  ( ) ;    gridBagConstraints . gridx = 0 ;    gridBagConstraints . gridy = 1 ;    gridBagConstraints . fill =    java . awt . GridBagConstraints . HORIZONTAL ;    gridBagConstraints . weightx = 1.0 ;   add  ( southToolbar , gridBagConstraints ) ; }   private void refreshButtonActionPerformed  (     java . awt . event . ActionEvent evt )  {     Lookup . getDefault  ( ) . lookup  (  PreviewUIController . class ) . refreshPreview  ( ) ; }  void writeProperties  (    java . util . Properties p )  {   p . setProperty  ( "version" , "1.0" ) ; }  void readProperties  (    java . util . Properties p )  {  String  version =  p . getProperty  ( "version" ) ; } }