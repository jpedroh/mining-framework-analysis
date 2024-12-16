  package     org . wicketopia . example . web . page ;   import      org . apache . wicket . markup . html . IHeaderContributor ;  import      org . apache . wicket . markup . html . IHeaderResponse ;  import      org . apache . wicket . markup . html . WebPage ;  import     org . apache . wicket . model . IModel ;  import       org . apache . wicket . request . mapper . parameter . PageParameters ;  import      org . apache . wicket . request . resource . PackageResourceReference ;  import      org . apache . wicket . request . resource . ResourceReference ;  import     org . apache . wicket . ajax . AjaxRequestTarget ;  import       org . apache . wicket . ajax . markup . html . AjaxLink ;  import      org . apache . wicket . markup . html . WebMarkupContainer ;  import       org . apache . wicket . markup . html . basic . Label ;  import       org . apache . wicket . markup . html . link . BookmarkablePageLink ;  import       org . apache . wicket . markup . html . link . Link ;  import       org . apache . wicket . markup . html . panel . FeedbackPanel ;  import     org . apache . wicket . model . ResourceModel ;  import     org . apache . wicket . model . StringResourceModel ;  import       org . apache . wicket . spring . injection . annot . SpringBean ;  import     org . springframework . security . authentication . AuthenticationManager ;  import     org . springframework . security . authentication . UsernamePasswordAuthenticationToken ;  import      org . springframework . security . core . context . SecurityContextHolder ;  import   java . util . Calendar ;  import   java . util . GregorianCalendar ;   public class BasePage  extends WebPage  implements  IHeaderContributor  {   private static final  long  serialVersionUID = 1L ;   public BasePage  ( )  {   init  ( ) ; }   public BasePage  (   IModel  <  ? > model )  {  super  ( model ) ;   init  ( ) ; }   public BasePage  (  PageParameters parameters )  {  super  ( parameters ) ;   init  ( ) ; }   public void renderHead  (  IHeaderResponse header )  {   header . renderCSSReference  ( 
<<<<<<<
 new PackageResourceReference  (  getClass  ( ) , "style.css" )
=======
 new ResourceReference  (  BasePage . class , "style.css" )
>>>>>>>
 ) ; }    @ SpringBean private AuthenticationManager  authenticationManager ;   private void init  ( )  {   setOutputMarkupId  ( true ) ;   add  (   new Label  ( "titleLabel" ,  getTitleModel  ( ) ) . setRenderBodyOnly  ( true ) ) ;   add  (   new Label  ( "captionLabel" ,  getCaptionModel  ( ) ) . setRenderBodyOnly  ( true ) ) ;   add  (   new Label  ( "copyrightLabel" ,  resourceModel  ( "page.copyright" ,   new GregorianCalendar  ( ) . get  (  Calendar . YEAR ) ) ) . setEscapeModelStrings  ( false ) ) ;   add  (  new StyleSheetReference  ( "stylesheet" ,  BasePage . class , "style.css" ) ) ;   add  (   new FeedbackPanel  ( "feedback" ) . setOutputMarkupPlaceholderTag  ( true ) ) ;   add  (  new  BookmarkablePageLink  < Void >  ( "homeLink" ,  HomePage . class ) ) ;   add  (  new Link  ( "login" )  {    @ Override public void onClick  ( )  {   final UsernamePasswordAuthenticationToken  tok =  new UsernamePasswordAuthenticationToken  ( "admin" , "admin" ) ;    SecurityContextHolder . getContext  ( ) . setAuthentication  (  authenticationManager . authenticate  ( tok ) ) ;   setResponsePage  (   BasePage . this . getClass  ( ) ) ;   setRedirect  ( true ) ; }    @ Override public boolean isVisible  ( )  {  return    SecurityContextHolder . getContext  ( ) . getAuthentication  ( ) == null ; } } ) ;   add  (  new Link  ( "logout" )  {    @ Override public void onClick  ( )  {   SecurityContextHolder . clearContext  ( ) ;   setResponsePage  (   BasePage . this . getClass  ( ) ) ;   setRedirect  ( true ) ; }    @ Override public boolean isVisible  ( )  {  return    SecurityContextHolder . getContext  ( ) . getAuthentication  ( ) != null ; } } ) ; } 
<<<<<<<
=======
  public BasePage  (  IPageMap pageMap )  {  super  ( pageMap ) ;   init  ( ) ; }
>>>>>>>
 
<<<<<<<
=======
  public BasePage  (  IPageMap pageMap ,   IModel  <  ? > model )  {  super  ( pageMap , model ) ;   init  ( ) ; }
>>>>>>>
 
<<<<<<<
=======
  public BasePage  (  IPageMap pageMap ,  PageParameters parameters )  {  super  ( pageMap , parameters ) ;   init  ( ) ; }
>>>>>>>
   protected  IModel  < String > getCaptionModel  ( )  {  return  resourceModel  ( "page.caption" ) ; }   protected  IModel  < String > getTitleModel  ( )  {  return  resourceModel  ( "page.title" ) ; }   protected  IModel  < String > resourceModel  (  String key ,  Object ...  params )  {  if  (   params == null ||   params . length == 0 )  {  return  new ResourceModel  ( key ,   "[" + key + "]" ) ; } else  {  return  new StringResourceModel  ( key , this , null , params ,   "[" + key + "]" ) ; } } }