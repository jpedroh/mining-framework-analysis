  package      org . telegram . telegrambots . api . objects . replykeyboard ;   import     com . fasterxml . jackson . annotation . JsonProperty ;  import     com . fasterxml . jackson . core . JsonGenerator ;  import     com . fasterxml . jackson . databind . SerializerProvider ;  import      com . fasterxml . jackson . databind . jsontype . TypeSerializer ;  import   org . json . JSONArray ;  import   org . json . JSONObject ;  import        org . telegram . telegrambots . api . objects . replykeyboard . buttons . InlineKeyboardButton ;  import   java . io . IOException ;  import   java . util . ArrayList ;  import   java . util . List ;   public class InlineKeyboardMarkup  implements  ReplyKeyboard  {   private static final String  KEYBOARD_FIELD = "inline_keyboard" ;    @ JsonProperty  ( KEYBOARD_FIELD ) private  List  <  List  < InlineKeyboardButton > >  keyboard ;   public InlineKeyboardMarkup  ( )  {  super  ( ) ;   keyboard =  new  ArrayList  < >  ( ) ; }   public  List  <  List  < InlineKeyboardButton > > getKeyboard  ( )  {  return keyboard ; }   public void setKeyboard  (   List  <  List  < InlineKeyboardButton > > keyboard )  {    this . keyboard = keyboard ; }    @ Override public JSONObject toJson  ( )  {  JSONObject  jsonObject =  new JSONObject  ( ) ;  JSONArray  jsonkeyboard =  new JSONArray  ( ) ;  for (  List  < InlineKeyboardButton > innerRow :  this . keyboard )  {  JSONArray  innerJSONKeyboard =  new JSONArray  ( ) ;  for ( InlineKeyboardButton element : innerRow )  {   innerJSONKeyboard . put  (  element . toJson  ( ) ) ; }   jsonkeyboard . put  ( innerJSONKeyboard ) ; }   jsonObject . put  (  InlineKeyboardMarkup . KEYBOARD_FIELD , jsonkeyboard ) ;  return jsonObject ; }    @ Override public void serialize  (  JsonGenerator gen ,  SerializerProvider serializers )  throws IOException  {   gen . writeStartObject  ( ) ;   gen . writeArrayFieldStart  ( KEYBOARD_FIELD ) ;  for (  List  < InlineKeyboardButton > innerRow : keyboard )  {   gen . writeStartArray  ( ) ;  for ( InlineKeyboardButton element : innerRow )  {   gen . writeObject  ( element ) ; }   gen . writeEndArray  ( ) ; }   gen . writeEndArray  ( ) ;   gen . writeEndObject  ( ) ;   gen . flush  ( ) ; }    @ Override public void serializeWithType  (  JsonGenerator gen ,  SerializerProvider serializers ,  TypeSerializer typeSer )  throws IOException  {   serialize  ( gen , serializers ) ; }    @ Override public String toString  ( )  {  return    "InlineKeyboardMarkup{" + "inline_keyboard=" + keyboard + '}' ; } 
<<<<<<<
=======
  public InlineKeyboardMarkup setKeyboard  (   List  <  List  < String > > keyboard )  {    this . keyboard = keyboard ;  return this ; }
>>>>>>>
 
<<<<<<<
=======
  public InlineKeyboardMarkup setResizeKeyboard  (  Boolean resizeKeyboard )  {    this . resizeKeyboard = resizeKeyboard ;  return this ; }
>>>>>>>
 
<<<<<<<
=======
  public InlineKeyboardMarkup setOneTimeKeyboad  (  Boolean oneTimeKeyboad )  {    this . oneTimeKeyboad = oneTimeKeyboad ;  return this ; }
>>>>>>>
 
<<<<<<<
=======
  public InlineKeyboardMarkup setSelective  (  Boolean selective )  {    this . selective = selective ;  return this ; }
>>>>>>>
 }