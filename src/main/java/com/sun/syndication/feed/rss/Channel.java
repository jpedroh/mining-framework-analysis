  package     com . sun . syndication . feed . rss ;   import   java . util . ArrayList ;  import   java . util . Collections ;  import   java . util . Date ;  import   java . util . HashSet ;  import   java . util . List ;  import   java . util . Set ;  import     com . sun . syndication . feed . WireFeed ;  import      com . sun . syndication . feed . module . Module ;  import       com . sun . syndication . feed . module . impl . ModuleUtils ;   public class Channel  extends WireFeed  {   private static final  long  serialVersionUID = 745207486449728472L ;   public static final String  SUNDAY = "sunday" ;   public static final String  MONDAY = "monday" ;   public static final String  TUESDAY = "tuesday" ;   public static final String  WEDNESDAY = "wednesday" ;   public static final String  THURSDAY = "thursday" ;   public static final String  FRIDAY = "friday" ;   public static final String  SATURDAY = "saturday" ;   private static final  Set  < String >  DAYS ;  static  {   final  HashSet  < String >  days =  new  HashSet  < String >  ( ) ;   days . add  ( SUNDAY ) ;   days . add  ( MONDAY ) ;   days . add  ( TUESDAY ) ;   days . add  ( WEDNESDAY ) ;   days . add  ( THURSDAY ) ;   days . add  ( FRIDAY ) ;   days . add  ( SATURDAY ) ;   DAYS =  Collections . unmodifiableSet  ( days ) ; }   private String  title ;   private String  description ;   private String  link ;   private String  uri ;   private Image  image ;   private  List  < Item >  items ;   private TextInput  textInput ;   private String  language ;   private String  rating ;   private String  copyright ;   private Date  pubDate ;   private Date  lastBuildDate ;   private String  docs ;   private String  managingEditor ;   private String  webMaster ;   private  List  < Integer >  skipHours ;   private  List  < String >  skipDays ;   private Cloud  cloud ;   private  List  < Category >  categories ;   private String  generator ;   private  int  ttl =  - 1 ;   private  List  < Module >  modules ;   public Channel  ( )  { }   public Channel  (   final String type )  {  super  ( type ) ; }   public String getTitle  ( )  {  return title ; }   public void setTitle  (   final String title )  {    this . title = title ; }   public String getDescription  ( )  {  return description ; }   public void setDescription  (   final String description )  {    this . description = description ; }   public String getLink  ( )  {  return link ; }   public void setLink  (   final String link )  {    this . link = link ; }   public String getUri  ( )  {  return uri ; }   public void setUri  (   final String uri )  {    this . uri = uri ; }   public Image getImage  ( )  {  return image ; }   public void setImage  (   final Image image )  {    this . image = image ; }   public  List  < Item > getItems  ( )  {  if  (  items == null )  {   items =  new  ArrayList  < Item >  ( ) ; }  return items ; }   public void setItems  (   final  List  < Item > items )  {    this . items = items ; }   public TextInput getTextInput  ( )  {  return textInput ; }   public void setTextInput  (   final TextInput textInput )  {    this . textInput = textInput ; }   public String getLanguage  ( )  {  return language ; }   public void setLanguage  (   final String language )  {    this . language = language ; }   public String getRating  ( )  {  return rating ; }   public void setRating  (   final String rating )  {    this . rating = rating ; }   public String getCopyright  ( )  {  return copyright ; }   public void setCopyright  (   final String copyright )  {    this . copyright = copyright ; }   public Date getPubDate  ( )  {  if  (  pubDate == null )  {  return null ; } else  {  return  new Date  (  pubDate . getTime  ( ) ) ; } }   public void setPubDate  (   final Date pubDate )  {  if  (  pubDate == null )  {    this . pubDate = null ; } else  {    this . pubDate =  new Date  (  pubDate . getTime  ( ) ) ; } }   public Date getLastBuildDate  ( )  {  if  (  lastBuildDate == null )  {  return null ; } else  {  return  new Date  (  lastBuildDate . getTime  ( ) ) ; } }   public void setLastBuildDate  (   final Date lastBuildDate )  {  if  (  lastBuildDate == null )  {    this . lastBuildDate = null ; } else  {    this . lastBuildDate =  new Date  (  lastBuildDate . getTime  ( ) ) ; } }   public String getDocs  ( )  {  return docs ; }   public void setDocs  (   final String docs )  {    this . docs = docs ; }   public String getManagingEditor  ( )  {  return managingEditor ; }   public void setManagingEditor  (   final String managingEditor )  {    this . managingEditor = managingEditor ; }   public String getWebMaster  ( )  {  return webMaster ; }   public void setWebMaster  (   final String webMaster )  {    this . webMaster = webMaster ; }   public  List  < Integer > getSkipHours  ( )  {  if  (  skipHours != null )  {  return skipHours ; } else  {  return  new  ArrayList  < Integer >  ( ) ; } }   public void setSkipHours  (   final  List  < Integer > skipHours )  {  if  (  skipHours != null )  {  for (   int  i = 0 ;  i <  skipHours . size  ( ) ;  i ++ )  {   final Integer  iHour =  skipHours . get  ( i ) ;  if  (  iHour != null )  {   final  int  hour =  iHour . intValue  ( ) ;  if  (   hour < 0 ||  hour > 24 )  {  throw  new IllegalArgumentException  (   "Invalid hour [" + hour + "]" ) ; } } else  {  throw  new IllegalArgumentException  ( "Invalid hour [null]" ) ; } } }    this . skipHours = skipHours ; }   public  List  < String > getSkipDays  ( )  {  if  (  skipDays != null )  {  return skipDays ; } else  {  return  new  ArrayList  < String >  ( ) ; } }   public void setSkipDays  (   final  List  < String > skipDays )  {  if  (  skipDays != null )  {  for (   int  i = 0 ;  i <  skipDays . size  ( ) ;  i ++ )  {  String  day =  skipDays . get  ( i ) ;  if  (  day != null )  {   day =  day . toLowerCase  ( ) ;  if  (  !  DAYS . contains  ( day ) )  {  throw  new IllegalArgumentException  (   "Invalid day [" + day + "]" ) ; }   skipDays . set  ( i , day ) ; } else  {  throw  new IllegalArgumentException  ( "Invalid day [null]" ) ; } } }    this . skipDays = skipDays ; }   public Cloud getCloud  ( )  {  return cloud ; }   public void setCloud  (   final Cloud cloud )  {    this . cloud = cloud ; }   public  List  < Category > getCategories  ( )  {  if  (  categories == null )  {   categories =  new  ArrayList  < Category >  ( ) ; }  return categories ; }   public void setCategories  (   final  List  < Category > categories )  {    this . categories = categories ; }   public String getGenerator  ( )  {  return generator ; }   public void setGenerator  (   final String generator )  {    this . generator = generator ; }   public  int getTtl  ( )  {  return ttl ; }   public void setTtl  (   final  int ttl )  {    this . ttl = ttl ; }    @ Override public  List  < Module > getModules  ( )  {  if  (  modules == null )  {   modules =  new  ArrayList  < Module >  ( ) ; }  return modules ; }    @ Override public void setModules  (   final  List  < Module > modules )  {    this . modules = modules ; }    @ Override public Module getModule  (   final String uri )  {  return  ModuleUtils . getModule  ( modules , uri ) ; } }