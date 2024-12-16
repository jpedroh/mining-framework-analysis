  package     net . md_5 . bungee . api . chat ;   import     com . google . common . base . Preconditions ;  import     net . md_5 . bungee . api . ChatColor ;  import   java . util . ArrayList ;  import   java . util . List ;   public class ComponentBuilder  {   private BaseComponent  current ;   private final  List  < BaseComponent >  parts =  new  ArrayList  < BaseComponent >  ( ) ;   public ComponentBuilder  (  ComponentBuilder original )  {   current =   original . current . duplicate  ( ) ;  for ( BaseComponent baseComponent :  original . parts )  {   parts . add  (  baseComponent . duplicate  ( ) ) ; } }   public ComponentBuilder  (  String text )  {   current =  new TextComponent  ( text ) ; }   public ComponentBuilder  (  BaseComponent component )  {   current =  component . duplicate  ( ) ; }   public ComponentBuilder append  (  BaseComponent component )  {  return  append  ( component ,  FormatRetention . ALL ) ; }   public ComponentBuilder append  (  BaseComponent component ,  FormatRetention retention )  {   parts . add  ( current ) ;  BaseComponent  previous = current ;   current =  component . duplicate  ( ) ;   current . copyFormatting  ( previous , retention , false ) ;  return this ; }   public ComponentBuilder append  (   BaseComponent  [ ] components )  {  return  append  ( components ,  FormatRetention . ALL ) ; }   public ComponentBuilder append  (   BaseComponent  [ ] components ,  FormatRetention retention )  {   Preconditions . checkArgument  (   components . length != 0 , "No components to append" ) ;  BaseComponent  previous = current ;  for ( BaseComponent component : components )  {   parts . add  ( current ) ;   current =  component . duplicate  ( ) ;   current . copyFormatting  ( previous , retention , false ) ; }  return this ; }   public ComponentBuilder append  (  String text )  {  return  append  ( text ,  FormatRetention . ALL ) ; }   public ComponentBuilder append  (  String text ,  FormatRetention retention )  {   parts . add  ( current ) ;  BaseComponent  old = current ;   current =  new TextComponent  ( text ) ;   current . copyFormatting  ( old , retention , false ) ;  return this ; }   public ComponentBuilder color  (  ChatColor color )  {   current . setColor  ( color ) ;  return this ; }   public ComponentBuilder bold  (  boolean bold )  {   current . setBold  ( bold ) ;  return this ; }   public ComponentBuilder italic  (  boolean italic )  {   current . setItalic  ( italic ) ;  return this ; }   public ComponentBuilder underlined  (  boolean underlined )  {   current . setUnderlined  ( underlined ) ;  return this ; }   public ComponentBuilder strikethrough  (  boolean strikethrough )  {   current . setStrikethrough  ( strikethrough ) ;  return this ; }   public ComponentBuilder obfuscated  (  boolean obfuscated )  {   current . setObfuscated  ( obfuscated ) ;  return this ; }   public ComponentBuilder insertion  (  String insertion )  {   current . setInsertion  ( insertion ) ;  return this ; }   public ComponentBuilder event  (  ClickEvent clickEvent )  {   current . setClickEvent  ( clickEvent ) ;  return this ; }   public ComponentBuilder event  (  HoverEvent hoverEvent )  {   current . setHoverEvent  ( hoverEvent ) ;  return this ; }   public ComponentBuilder reset  ( )  {  return  retain  (  FormatRetention . NONE ) ; }   public ComponentBuilder retain  (  FormatRetention retention )  {   current . retain  ( retention ) ;  return this ; }   public  BaseComponent  [ ] create  ( )  {   BaseComponent  [ ]  result =  parts . toArray  (  new BaseComponent  [   parts . size  ( ) + 1 ] ) ;    result [  parts . size  ( ) ] = current ;  return result ; }   public static enum FormatRetention  {  NONE ,  FORMATTING ,  EVENTS ,  ALL }   public ComponentBuilder append  (  Joiner joiner )  {  return  joiner . join  ( this ,  FormatRetention . ALL ) ; }   public ComponentBuilder append  (  Joiner joiner ,  FormatRetention retention )  {  return  joiner . join  ( this , retention ) ; }   public interface Joiner  {  ComponentBuilder join  (  ComponentBuilder componentBuilder ,  FormatRetention retention ) ; } }