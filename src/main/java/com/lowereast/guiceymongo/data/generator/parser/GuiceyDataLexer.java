  package      com . lowereast . guiceymongo . data . generator . parser ;   import   org . antlr . runtime .  * ;  import   java . util . Stack ;  import   java . util . List ;  import   java . util . ArrayList ;   public class GuiceyDataLexer  extends Lexer  {   public static final  int  EXPONENT = 
<<<<<<<
21
=======
20
>>>>>>>
 ;   public static final  int  T__29 = 29 ;   public static final  int  OPTION = 11 ;   public static final  int  T__28 = 28 ;   public static final  int  T__27 = 27 ;   public static final  int  T__26 = 26 ;   public static final  int  OCTAL_ESC = 
<<<<<<<
25
=======
24
>>>>>>>
 ;   public static final  int  FLOAT = 
<<<<<<<
17
=======
16
>>>>>>>
 ;   public static final  int  TYPE_LIST = 7 ;   public static final  int  ID = 14 ;   public static final  int  EOF =  - 1 ;   public static final  int  TYPE_PRIMITIVE = 9 ;   public static final  int  TYPE = 
<<<<<<<
19
=======
18
>>>>>>>
 ;   public static final  int  ESC_SEQ = 
<<<<<<<
22
=======
21
>>>>>>>
 ;   public static final  int  TYPE_MAP = 5 ;   public static final  int  PAIR = 12 ;   public static final  int  COMMENT = 15 ;   public static final  int  T__42 = 42 ;   public static final  int  T__43 = 43 ;   public static final  int  T__40 = 40 ;   public static final  int  T__41 = 41 ;   public static final  int  UNICODE_ESC = 
<<<<<<<
24
=======
23
>>>>>>>
 ;   public static final  int  HEX_DIGIT = 
<<<<<<<
23
=======
22
>>>>>>>
 ;   public static final  int  INT = 
<<<<<<<
16
=======
15
>>>>>>>
 ;   public static final  int  T__30 = 30 ;   public static final  int  T__31 = 31 ;   public static final  int  T__32 = 32 ;   public static final  int  PARAMETERIZED_TYPE = 8 ;   public static final  int  T__33 = 33 ;   public static final  int  WS = 
<<<<<<<
20
=======
19
>>>>>>>
 ;   public static final  int  T__34 = 34 ;   public static final  int  ENUM = 13 ;   public static final  int  T__35 = 35 ;   public static final  int  T__36 = 36 ;   public static final  int  T__37 = 37 ;   public static final  int  PROPERTY = 10 ;   public static final  int  T__38 = 38 ;   public static final  int  T__39 = 39 ;   public static final  int  TYPE_SET = 6 ;   public static final  int  DATA = 4 ;   public static final  int  STRING = 
<<<<<<<
18
=======
17
>>>>>>>
 ;   public GuiceyDataLexer  ( )  { ; }   public GuiceyDataLexer  (  CharStream input )  {  this  ( input ,  new RecognizerSharedState  ( ) ) ; }   public GuiceyDataLexer  (  CharStream input ,  RecognizerSharedState state )  {  super  ( input , state ) ; }   public String getGrammarFileName  ( )  {  return "com\\lowereast\\guiceymongo\\data\\generator\\parser\\GuiceyData.g" ; }   public final void mDATA  ( )  throws RecognitionException  {  try  {   int  _type = DATA ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "data" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mTYPE_MAP  ( )  throws RecognitionException  {  try  {   int  _type = TYPE_MAP ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "map" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mTYPE_SET  ( )  throws RecognitionException  {  try  {   int  _type = TYPE_SET ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "set" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mTYPE_LIST  ( )  throws RecognitionException  {  try  {   int  _type = TYPE_LIST ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "list" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mPARAMETERIZED_TYPE  ( )  throws RecognitionException  {  try  {   int  _type = PARAMETERIZED_TYPE ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "parameterized-type" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mTYPE_PRIMITIVE  ( )  throws RecognitionException  {  try  {   int  _type = TYPE_PRIMITIVE ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "primitive" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mPROPERTY  ( )  throws RecognitionException  {  try  {   int  _type = PROPERTY ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "property" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mOPTION  ( )  throws RecognitionException  {  try  {   int  _type = OPTION ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "option" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mPAIR  ( )  throws RecognitionException  {  try  {   int  _type = PAIR ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "pair" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mENUM  ( )  throws RecognitionException  {  try  {   int  _type = ENUM ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "enum" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__26  ( )  throws RecognitionException  {  try  {   int  _type = T__26 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
'{'
=======
'}'
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__27  ( )  throws RecognitionException  {  try  {   int  _type = T__27 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
'}'
=======
"{}"
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__28  ( )  throws RecognitionException  {  try  {   int  _type = T__28 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
"{}"
=======
','
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__29  ( )  throws RecognitionException  {  try  {   int  _type = T__29 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
','
=======
'['
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__30  ( )  throws RecognitionException  {  try  {   int  _type = T__30 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
"/*"
=======
']'
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__31  ( )  throws RecognitionException  {  try  {   int  _type = T__31 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
"*/"
=======
'('
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__32  ( )  throws RecognitionException  {  try  {   int  _type = T__32 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
'['
=======
")]"
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__33  ( )  throws RecognitionException  {  try  {   int  _type = T__33 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
']'
=======
'='
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__34  ( )  throws RecognitionException  {  try  {   int  _type = T__34 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
'('
=======
"data;"
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__35  ( )  throws RecognitionException  {  try  {   int  _type = T__35 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
")]"
=======
';'
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__36  ( )  throws RecognitionException  {  try  {   int  _type = T__36 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
'='
=======
"map<"
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__37  ( )  throws RecognitionException  {  try  {   int  _type = T__37 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
"data;"
=======
'>'
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__38  ( )  throws RecognitionException  {  try  {   int  _type = T__38 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
';'
=======
"set<"
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__39  ( )  throws RecognitionException  {  try  {   int  _type = T__39 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
"map<"
=======
"list<"
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__40  ( )  throws RecognitionException  {  try  {   int  _type = T__40 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( 
<<<<<<<
'>'
=======
'<'
>>>>>>>
 ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__41  ( )  throws RecognitionException  {  try  {   int  _type = T__41 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "set<" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__42  ( )  throws RecognitionException  {  try  {   int  _type = T__42 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( "list<" ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mT__43  ( )  throws RecognitionException  {  try  {   int  _type = T__43 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( '<' ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mCOMMENT  ( )  throws RecognitionException  {  try  {   int  _type = COMMENT ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   int  cnt1 = 0 ;  loop1 :  do  {   int  alt1 = 2 ;   int  LA1_0 =  input . LA  ( 1 ) ;  if  (  (  (   LA1_0 >= 'a' &&  LA1_0 <= 'z' ) ) )  {   alt1 = 1 ; }  switch  ( alt1 )  {   case 1 :  {   matchRange  ( 'a' , 'z' ) ; }  break ;   default :  if  (  cnt1 >= 1 )  break loop1 ;  EarlyExitException  eee =  new EarlyExitException  ( 1 , input ) ;  throw eee ; }   cnt1 ++ ; } while  ( true ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mTYPE  ( )  throws RecognitionException  {  try  {   int  _type = TYPE ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   mID  ( ) ;   int  cnt2 = 0 ;  loop2 :  do  { 
<<<<<<<
  int  alt2 = 2 ;
=======
>>>>>>>
   int  LA2_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (  LA2_0 == '.' ) )  {   alt2 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '.' :  {   alt1 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt2 )  {   case 1 :  {   match  ( '.' ) ;   mID  ( ) ; }  break ;   default :  if  (  cnt2 >= 1 )  break loop2 ;  EarlyExitException  eee =  new EarlyExitException  ( 2 , input ) ;  throw eee ; }   cnt2 ++ ; } while  ( true ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mID  ( )  throws RecognitionException  {  try  {   int  _type = ID ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {  if  (   (    input . LA  ( 1 ) >= 'A' &&   input . LA  ( 1 ) <= 'Z' ) ||  (    input . LA  ( 1 ) >= 'a' &&   input . LA  ( 1 ) <= 'z' ) )  {   input . consume  ( ) ; } else  {  MismatchedSetException  mse =  new MismatchedSetException  ( null , input ) ;   recover  ( mse ) ;  throw mse ; }  loop3 :  do  { 
<<<<<<<
  int  alt3 = 2 ;
=======
>>>>>>>
   int  LA3_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (     (   LA3_0 >= '0' &&  LA3_0 <= '9' ) ||  (   LA3_0 >= 'A' &&  LA3_0 <= 'Z' ) ||  LA3_0 == '_' ||  (   LA3_0 >= 'a' &&  LA3_0 <= 'z' ) ) )  {   alt3 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :   case '8' :   case '9' :   case 'A' :   case 'B' :   case 'C' :   case 'D' :   case 'E' :   case 'F' :   case 'G' :   case 'H' :   case 'I' :   case 'J' :   case 'K' :   case 'L' :   case 'M' :   case 'N' :   case 'O' :   case 'P' :   case 'Q' :   case 'R' :   case 'S' :   case 'T' :   case 'U' :   case 'V' :   case 'W' :   case 'X' :   case 'Y' :   case 'Z' :   case '_' :   case 'a' :   case 'b' :   case 'c' :   case 'd' :   case 'e' :   case 'f' :   case 'g' :   case 'h' :   case 'i' :   case 'j' :   case 'k' :   case 'l' :   case 'm' :   case 'n' :   case 'o' :   case 'p' :   case 'q' :   case 'r' :   case 's' :   case 't' :   case 'u' :   case 'v' :   case 'w' :   case 'x' :   case 'y' :   case 'z' :  {   alt2 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt3 )  {   case 1 :  {  if  (     (    input . LA  ( 1 ) >= '0' &&   input . LA  ( 1 ) <= '9' ) ||  (    input . LA  ( 1 ) >= 'A' &&   input . LA  ( 1 ) <= 'Z' ) ||   input . LA  ( 1 ) == '_' ||  (    input . LA  ( 1 ) >= 'a' &&   input . LA  ( 1 ) <= 'z' ) )  {   input . consume  ( ) ; } else  {  MismatchedSetException  mse =  new MismatchedSetException  ( null , input ) ;   recover  ( mse ) ;  throw mse ; } }  break ;   default :  break loop3 ; } } while  ( true ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mWS  ( )  throws RecognitionException  {  try  {   int  _type = WS ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {  if  (    (    input . LA  ( 1 ) >= '\t' &&   input . LA  ( 1 ) <= '\n' ) ||   input . LA  ( 1 ) == '\r' ||   input . LA  ( 1 ) == ' ' )  {   input . consume  ( ) ; } else  {  MismatchedSetException  mse =  new MismatchedSetException  ( null , input ) ;   recover  ( mse ) ;  throw mse ; }   _channel = HIDDEN ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mINT  ( )  throws RecognitionException  {  try  {   int  _type = INT ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   int  cnt4 = 0 ;  loop4 :  do  { 
<<<<<<<
  int  alt4 = 2 ;
=======
>>>>>>>
   int  LA4_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (  (   LA4_0 >= '0' &&  LA4_0 <= '9' ) ) )  {   alt4 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :   case '8' :   case '9' :  {   alt3 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt4 )  {   case 1 :  {   matchRange  ( '0' , '9' ) ; }  break ;   default :  if  (  cnt4 >= 1 )  break loop4 ;  EarlyExitException  eee =  new EarlyExitException  ( 4 , input ) ;  throw eee ; }   cnt4 ++ ; } while  ( true ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mFLOAT  ( )  throws RecognitionException  {  try  {   int  _type = FLOAT ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;   int  alt11 = 3 ;   alt11 =  dfa11 . predict  ( input ) ;  switch  ( alt11 )  {   case 1 :  {   int  cnt5 = 0 ;  loop5 :  do  { 
<<<<<<<
  int  alt5 = 2 ;
=======
>>>>>>>
   int  LA5_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (  (   LA5_0 >= '0' &&  LA5_0 <= '9' ) ) )  {   alt5 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :   case '8' :   case '9' :  {   alt4 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt5 )  {   case 1 :  {   matchRange  ( '0' , '9' ) ; }  break ;   default :  if  (  cnt5 >= 1 )  break loop5 ;  EarlyExitException  eee =  new EarlyExitException  ( 5 , input ) ;  throw eee ; }   cnt5 ++ ; } while  ( true ) ;   match  ( '.' ) ;  loop6 :  do  { 
<<<<<<<
  int  alt6 = 2 ;
=======
>>>>>>>
   int  LA6_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (  (   LA6_0 >= '0' &&  LA6_0 <= '9' ) ) )  {   alt6 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :   case '8' :   case '9' :  {   alt5 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt6 )  {   case 1 :  {   matchRange  ( '0' , '9' ) ; }  break ;   default :  break loop6 ; } } while  ( true ) ; 
<<<<<<<
  int  alt7 = 2 ;
=======
>>>>>>>
   int  LA7_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (   LA7_0 == 'E' ||  LA7_0 == 'e' ) )  {   alt7 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case 'E' :   case 'e' :  {   alt6 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt7 )  {   case 1 :  {   mEXPONENT  ( ) ; }  break ; } }  break ;   case 2 :  {   match  ( '.' ) ;   int  cnt8 = 0 ;  loop8 :  do  { 
<<<<<<<
  int  alt8 = 2 ;
=======
>>>>>>>
   int  LA8_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (  (   LA8_0 >= '0' &&  LA8_0 <= '9' ) ) )  {   alt8 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :   case '8' :   case '9' :  {   alt7 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt8 )  {   case 1 :  {   matchRange  ( '0' , '9' ) ; }  break ;   default :  if  (  cnt8 >= 1 )  break loop8 ;  EarlyExitException  eee =  new EarlyExitException  ( 8 , input ) ;  throw eee ; }   cnt8 ++ ; } while  ( true ) ; 
<<<<<<<
  int  alt9 = 2 ;
=======
>>>>>>>
   int  LA9_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (   LA9_0 == 'E' ||  LA9_0 == 'e' ) )  {   alt9 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case 'E' :   case 'e' :  {   alt8 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt9 )  {   case 1 :  {   mEXPONENT  ( ) ; }  break ; } }  break ;   case 3 :  {   int  cnt10 = 0 ;  loop10 :  do  { 
<<<<<<<
  int  alt10 = 2 ;
=======
>>>>>>>
   int  LA10_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (  (   LA10_0 >= '0' &&  LA10_0 <= '9' ) ) )  {   alt10 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :   case '8' :   case '9' :  {   alt9 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt10 )  {   case 1 :  {   matchRange  ( '0' , '9' ) ; }  break ;   default :  if  (  cnt10 >= 1 )  break loop10 ;  EarlyExitException  eee =  new EarlyExitException  ( 10 , input ) ;  throw eee ; }   cnt10 ++ ; } while  ( true ) ;   mEXPONENT  ( ) ; }  break ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mSTRING  ( )  throws RecognitionException  {  try  {   int  _type = STRING ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( '\'' ) ;  loop12 :  do  {   int  alt12 = 3 ;   int  LA12_0 =  input . LA  ( 1 ) ;  if  (  (  LA12_0 == '\\' ) )  {   alt12 = 1 ; } else  if  (  (    (   LA12_0 >= '\u0000' &&  LA12_0 <= '&' ) ||  (   LA12_0 >= '(' &&  LA12_0 <= '[' ) ||  (   LA12_0 >= ']' &&  LA12_0 <= '\uFFFF' ) ) )  {   alt12 = 2 ; }  switch  ( alt12 )  {   case 1 :  {   mESC_SEQ  ( ) ; }  break ;   case 2 :  {  if  (    (    input . LA  ( 1 ) >= '\u0000' &&   input . LA  ( 1 ) <= '&' ) ||  (    input . LA  ( 1 ) >= '(' &&   input . LA  ( 1 ) <= '[' ) ||  (    input . LA  ( 1 ) >= ']' &&   input . LA  ( 1 ) <= '\uFFFF' ) )  {   input . consume  ( ) ; } else  {  MismatchedSetException  mse =  new MismatchedSetException  ( null , input ) ;   recover  ( mse ) ;  throw mse ; } }  break ;   default :  break loop12 ; } } while  ( true ) ;   match  ( '\'' ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }   public final void mEXPONENT  ( )  throws RecognitionException  {  try  {  {  if  (    input . LA  ( 1 ) == 'E' ||   input . LA  ( 1 ) == 'e' )  {   input . consume  ( ) ; } else  {  MismatchedSetException  mse =  new MismatchedSetException  ( null , input ) ;   recover  ( mse ) ;  throw mse ; } 
<<<<<<<
  int  alt13 = 2 ;
=======
>>>>>>>
   int  LA13_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (   LA13_0 == '+' ||  LA13_0 == '-' ) )  {   alt13 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '+' :   case '-' :  {   alt12 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt13 )  {   case 1 :  {  if  (    input . LA  ( 1 ) == '+' ||   input . LA  ( 1 ) == '-' )  {   input . consume  ( ) ; } else  {  MismatchedSetException  mse =  new MismatchedSetException  ( null , input ) ;   recover  ( mse ) ;  throw mse ; } }  break ; }   int  cnt14 = 0 ;  loop14 :  do  { 
<<<<<<<
  int  alt14 = 2 ;
=======
>>>>>>>
   int  LA14_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (  (   LA14_0 >= '0' &&  LA14_0 <= '9' ) ) )  {   alt14 = 1 ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :   case '8' :   case '9' :  {   alt13 = 1 ; }  break ; }
>>>>>>>
  switch  ( alt14 )  {   case 1 :  {   matchRange  ( '0' , '9' ) ; }  break ;   default :  if  (  cnt14 >= 1 )  break loop14 ;  EarlyExitException  eee =  new EarlyExitException  ( 14 , input ) ;  throw eee ; }   cnt14 ++ ; } while  ( true ) ; } }  finally  { } }   public final void mHEX_DIGIT  ( )  throws RecognitionException  {  try  {  {  if  (    (    input . LA  ( 1 ) >= '0' &&   input . LA  ( 1 ) <= '9' ) ||  (    input . LA  ( 1 ) >= 'A' &&   input . LA  ( 1 ) <= 'F' ) ||  (    input . LA  ( 1 ) >= 'a' &&   input . LA  ( 1 ) <= 'f' ) )  {   input . consume  ( ) ; } else  {  MismatchedSetException  mse =  new MismatchedSetException  ( null , input ) ;   recover  ( mse ) ;  throw mse ; } } }  finally  { } }   public final void mESC_SEQ  ( )  throws RecognitionException  {  try  { 
<<<<<<<
  int  alt15 = 3 ;
=======
>>>>>>>
   int  LA15_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (  LA15_0 == '\\' ) )  {  switch  (  input . LA  ( 2 ) )  {   case '\"' :   case '\'' :   case '\\' :   case 'b' :   case 'f' :   case 'n' :   case 'r' :   case 't' :  {   alt15 = 1 ; }  break ;   case 'u' :  {   alt15 = 2 ; }  break ;   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :  {   alt15 = 3 ; }  break ;   default :  NoViableAltException  nvae =  new NoViableAltException  ( "" , 15 , 1 , input ) ;  throw nvae ; } } else  {  NoViableAltException  nvae =  new NoViableAltException  ( "" , 15 , 0 , input ) ;  throw nvae ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '\\' :  {  switch  (  input . LA  ( 2 ) )  {   case '\"' :   case '\'' :   case '\\' :   case 'b' :   case 'f' :   case 'n' :   case 'r' :   case 't' :  {   alt14 = 1 ; }  break ;   case 'u' :  {   alt14 = 2 ; }  break ;   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :  {   alt14 = 3 ; }  break ;   default :  NoViableAltException  nvae =  new NoViableAltException  ( "" , 14 , 1 , input ) ;  throw nvae ; } }  break ;   default :  NoViableAltException  nvae =  new NoViableAltException  ( "" , 14 , 0 , input ) ;  throw nvae ; }
>>>>>>>
  switch  ( alt15 )  {   case 1 :  {   match  ( '\\' ) ;  if  (          input . LA  ( 1 ) == '\"' ||   input . LA  ( 1 ) == '\'' ||   input . LA  ( 1 ) == '\\' ||   input . LA  ( 1 ) == 'b' ||   input . LA  ( 1 ) == 'f' ||   input . LA  ( 1 ) == 'n' ||   input . LA  ( 1 ) == 'r' ||   input . LA  ( 1 ) == 't' )  {   input . consume  ( ) ; } else  {  MismatchedSetException  mse =  new MismatchedSetException  ( null , input ) ;   recover  ( mse ) ;  throw mse ; } }  break ;   case 2 :  {   mUNICODE_ESC  ( ) ; }  break ;   case 3 :  {   mOCTAL_ESC  ( ) ; }  break ; } }  finally  { } }   public final void mOCTAL_ESC  ( )  throws RecognitionException  {  try  { 
<<<<<<<
  int  alt16 = 3 ;
=======
>>>>>>>
   int  LA16_0 =  input . LA  ( 1 ) ; 
<<<<<<<
 if  (  (  LA16_0 == '\\' ) )  {   int  LA16_1 =  input . LA  ( 2 ) ;  if  (  (  (   LA16_1 >= '0' &&  LA16_1 <= '3' ) ) )  {   int  LA16_2 =  input . LA  ( 3 ) ;  if  (  (  (   LA16_2 >= '0' &&  LA16_2 <= '7' ) ) )  {   int  LA16_4 =  input . LA  ( 4 ) ;  if  (  (  (   LA16_4 >= '0' &&  LA16_4 <= '7' ) ) )  {   alt16 = 1 ; } else  {   alt16 = 2 ; } } else  {   alt16 = 3 ; } } else  if  (  (  (   LA16_1 >= '4' &&  LA16_1 <= '7' ) ) )  {   int  LA16_3 =  input . LA  ( 3 ) ;  if  (  (  (   LA16_3 >= '0' &&  LA16_3 <= '7' ) ) )  {   alt16 = 2 ; } else  {   alt16 = 3 ; } } else  {  NoViableAltException  nvae =  new NoViableAltException  ( "" , 16 , 1 , input ) ;  throw nvae ; } } else  {  NoViableAltException  nvae =  new NoViableAltException  ( "" , 16 , 0 , input ) ;  throw nvae ; }
=======
 switch  (  input . LA  ( 1 ) )  {   case '\\' :  {  switch  (  input . LA  ( 2 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :  {  switch  (  input . LA  ( 3 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :  {  switch  (  input . LA  ( 4 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :  {   alt15 = 1 ; }  break ;   default :   alt15 = 2 ; } }  break ;   default :   alt15 = 3 ; } }  break ;   case '4' :   case '5' :   case '6' :   case '7' :  {  switch  (  input . LA  ( 3 ) )  {   case '0' :   case '1' :   case '2' :   case '3' :   case '4' :   case '5' :   case '6' :   case '7' :  {   alt15 = 2 ; }  break ;   default :   alt15 = 3 ; } }  break ;   default :  NoViableAltException  nvae =  new NoViableAltException  ( "" , 15 , 1 , input ) ;  throw nvae ; } }  break ;   default :  NoViableAltException  nvae =  new NoViableAltException  ( "" , 15 , 0 , input ) ;  throw nvae ; }
>>>>>>>
  switch  ( alt16 )  {   case 1 :  {   match  ( '\\' ) ;  {   matchRange  ( '0' , '3' ) ; }  {   matchRange  ( '0' , '7' ) ; }  {   matchRange  ( '0' , '7' ) ; } }  break ;   case 2 :  {   match  ( '\\' ) ;  {   matchRange  ( '0' , '7' ) ; }  {   matchRange  ( '0' , '7' ) ; } }  break ;   case 3 :  {   match  ( '\\' ) ;  {   matchRange  ( '0' , '7' ) ; } }  break ; } }  finally  { } }   public final void mUNICODE_ESC  ( )  throws RecognitionException  {  try  {  {   match  ( '\\' ) ;   match  ( 'u' ) ;   mHEX_DIGIT  ( ) ;   mHEX_DIGIT  ( ) ;   mHEX_DIGIT  ( ) ;   mHEX_DIGIT  ( ) ; } }  finally  { } }   public void mTokens  ( )  throws RecognitionException  {   int 
<<<<<<<
 alt17 = 35
=======
 alt16 = 32
>>>>>>>
 ;   alt17 =  dfa17 . predict  ( input ) ;  switch  ( alt17 )  {   case 1 :  {   mDATA  ( ) ; }  break ;   case 2 :  {   mTYPE_MAP  ( ) ; }  break ;   case 3 :  {   mTYPE_SET  ( ) ; }  break ;   case 4 :  {   mTYPE_LIST  ( ) ; }  break ;   case 5 :  {   mPARAMETERIZED_TYPE  ( ) ; }  break ;   case 6 :  {   mTYPE_PRIMITIVE  ( ) ; }  break ;   case 7 :  {   mPROPERTY  ( ) ; }  break ;   case 8 :  {   mOPTION  ( ) ; }  break ;   case 9 :  {   mPAIR  ( ) ; }  break ;   case 10 :  {   mENUM  ( ) ; }  break ;   case 11 :  {   mT__26  ( ) ; }  break ;   case 12 :  {   mT__27  ( ) ; }  break ;   case 13 :  {   mT__28  ( ) ; }  break ;   case 14 :  {   mT__29  ( ) ; }  break ;   case 15 :  {   mT__30  ( ) ; }  break ;   case 16 :  {   mT__31  ( ) ; }  break ;   case 17 :  {   mT__32  ( ) ; }  break ;   case 18 :  {   mT__33  ( ) ; }  break ;   case 19 :  {   mT__34  ( ) ; }  break ;   case 20 :  {   mT__35  ( ) ; }  break ;   case 21 :  {   mT__36  ( ) ; }  break ;   case 22 :  {   mT__37  ( ) ; }  break ;   case 23 :  {   mT__38  ( ) ; }  break ;   case 24 :  {   mT__39  ( ) ; }  break ;   case 25 :  {   
<<<<<<<
mT__40
=======
mT__39
>>>>>>>
  ( ) ; }  break ;   case 26 :  {   
<<<<<<<
mT__41
=======
mT__40
>>>>>>>
  ( ) ; }  break ;   case 27 :  {   mT__42  ( ) ; }  break ;   case 28 :  {   mT__43  ( ) ; }  break ;   case 29 :  {   mCOMMENT  ( ) ; }  break ;   case 
<<<<<<<
30
=======
27
>>>>>>>
 :  {   mTYPE  ( ) ; }  break ;   case 
<<<<<<<
31
=======
28
>>>>>>>
 :  {   mID  ( ) ; }  break ;   case 
<<<<<<<
32
=======
29
>>>>>>>
 :  {   mWS  ( ) ; }  break ;   case 
<<<<<<<
33
=======
30
>>>>>>>
 :  {   mINT  ( ) ; }  break ;   case 
<<<<<<<
34
=======
31
>>>>>>>
 :  {   mFLOAT  ( ) ; }  break ;   case 
<<<<<<<
35
=======
32
>>>>>>>
 :  {   mSTRING  ( ) ; }  break ; } }   protected DFA11  dfa11 =  new DFA11  ( this ) ;   protected DFA17  dfa17 =  new DFA17  ( this ) ;   static final String  DFA11_eotS = "\5\uffff" ;   static final String  DFA11_eofS = "\5\uffff" ;   static final String  DFA11_minS = "\2\56\3\uffff" ;   static final String  DFA11_maxS = "\1\71\1\145\3\uffff" ;   static final String  DFA11_acceptS = "\2\uffff\1\2\1\1\1\3" ;   static final String  DFA11_specialS = "\5\uffff}>" ;   static final  String  [ ]  DFA11_transitionS =  { "\1\2\1\uffff\12\1" , "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4" , "" , "" , "" } ;   static final   short  [ ]  DFA11_eot =  DFA . unpackEncodedString  ( DFA11_eotS ) ;   static final   short  [ ]  DFA11_eof =  DFA . unpackEncodedString  ( DFA11_eofS ) ;   static final   char  [ ]  DFA11_min =  DFA . unpackEncodedStringToUnsignedChars  ( DFA11_minS ) ;   static final   char  [ ]  DFA11_max =  DFA . unpackEncodedStringToUnsignedChars  ( DFA11_maxS ) ;   static final   short  [ ]  DFA11_accept =  DFA . unpackEncodedString  ( DFA11_acceptS ) ;   static final   short  [ ]  DFA11_special =  DFA . unpackEncodedString  ( DFA11_specialS ) ;   static final   short  [ ] [ ]  DFA11_transition ;  static  {   int  numStates =  DFA11_transitionS . length ;   DFA11_transition =  new  short  [ numStates ]  [ ] ;  for (   int  i = 0 ;  i < numStates ;  i ++ )  {    DFA11_transition [ i ] =  DFA . unpackEncodedString  (  DFA11_transitionS [ i ] ) ; } }  class DFA11  extends DFA  {   public DFA11  (  BaseRecognizer recognizer )  {    this . recognizer = recognizer ;    this . decisionNumber = 11 ;    this . eot = DFA11_eot ;    this . eof = DFA11_eof ;    this . min = DFA11_min ;    this . max = DFA11_max ;    this . accept = DFA11_accept ;    this . special = DFA11_special ;    this . transition = DFA11_transition ; }   public String getDescription  ( )  {  return "125:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );" ; } }   static final String  DFA17_eotS =     "\1\uffff\7\35\1\50\14\uffff\1\35\1\51\1\uffff\1\52\2\uffff\2\35" + "\2\uffff\1\51\7\35\4\uffff\1\35\1\67\1\71\7\35\1\102\4\uffff\1\104" + "\1\35\1\106\3\35\1\112\4\uffff\1\35\1\uffff\3\35\1\uffff\3\35\1" + "\122\3\35\1\uffff\2\35\1\130\1\35\1\132\1\uffff\1\35\1\uffff\3\35" + "\1\uffff" ;   static final String  DFA17_eofS = "\137\uffff" ;   static final String  DFA17_minS =   "\1\11\7\56\1\175\14\uffff\2\56\1\uffff\1\56\2\uffff\2\56\2\uffff" + "\10\56\4\uffff\13\56\4\uffff\7\56\4\uffff\1\56\1\uffff\3\56\1\uffff" + "\7\56\1\uffff\5\56\1\uffff\1\56\1\uffff\2\56\1\55\1\uffff" ;   static final String  DFA17_maxS =    "\1\175\7\172\1\175\14\uffff\2\172\1\uffff\1\145\2\uffff\2\172\2" + "\uffff\10\172\4\uffff\13\172\4\uffff\7\172\4\uffff\1\172\1\uffff" + "\3\172\1\uffff\7\172\1\uffff\5\172\1\uffff\1\172\1\uffff\3\172\1" + "\uffff" ;   static final String  DFA17_acceptS =     "\11\uffff\1\14\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\27\1\31" + "\1\34\2\uffff\1\40\1\uffff\1\42\1\43\2\uffff\1\35\1\36\10\uffff" + "\1\15\1\13\1\37\1\41\13\uffff\1\30\1\2\1\32\1\3\7\uffff\1\26\1\1" + "\1\33\1\4\1\uffff\1\11\3\uffff\1\12\7\uffff\1\10\5\uffff\1\7\1\uffff" + "\1\6\3\uffff\1\5" ;   static final String  DFA17_specialS = "\137\uffff}>" ;   static final  String  [ ]  DFA17_transitionS =  {    "\2\27\2\uffff\1\27\22\uffff\1\27\6\uffff\1\32\1\17\1\20\1\14" + "\1\uffff\1\12\1\uffff\1\31\1\13\12\30\1\uffff\1\22\1\24\1\21" + "\1\23\2\uffff\32\26\1\15\1\uffff\1\16\3\uffff\3\25\1\1\1\7\6" + "\25\1\4\1\2\1\25\1\6\1\5\2\25\1\3\7\25\1\10\1\uffff\1\11" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\1\33" + "\31\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\1\40" + "\31\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\4\34" + "\1\41\25\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\10\34" + "\1\42\21\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\1\43" + "\20\34\1\44\10\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\17\34" + "\1\45\12\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\15\34" + "\1\46\14\34" , "\1\47" , "" , "" , "" , "" , "" , "" , "" , "" , "" , "" , "" , "" , "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\34" , "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37" , "" , "\1\31\1\uffff\12\30\13\uffff\1\31\37\uffff\1\31" , "" , "" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\23\34" + "\1\53\6\34" , "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\34" , "" , "" , "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\17\34" + "\1\54\12\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\23\34" + "\1\55\6\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\22\34" + "\1\56\7\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\10\34" + "\1\60\10\34\1\57\10\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\10\34" + "\1\61\5\34\1\62\13\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\23\34" + "\1\63\6\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\24\34" + "\1\64\5\34" , "" , "" , "" , "" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\1\65" + "\31\34" ,  "\1\36\1\uffff\12\37\2\uffff\1\66\4\uffff\32\37\4\uffff\1\37" + "\1\uffff\32\34" ,  "\1\36\1\uffff\12\37\2\uffff\1\70\4\uffff\32\37\4\uffff\1\37" + "\1\uffff\32\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\23\34" + "\1\72\6\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\1\73" + "\31\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\21\34" + "\1\74\10\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\14\34" + "\1\75\15\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\17\34" + "\1\76\12\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\10\34" + "\1\77\21\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\14\34" + "\1\100\15\34" ,  "\1\36\1\uffff\12\37\1\uffff\1\101\5\uffff\32\37\4\uffff\1\37" + "\1\uffff\32\34" , "" , "" , "" , "" ,  "\1\36\1\uffff\12\37\2\uffff\1\103\4\uffff\32\37\4\uffff\1\37" + "\1\uffff\32\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\14\34" + "\1\105\15\34" , "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\10\34" + "\1\107\21\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\4\34" + "\1\110\25\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\16\34" + "\1\111\13\34" , "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\34" , "" , "" , "" , "" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\4\34" + "\1\113\25\34" , "" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\23\34" + "\1\114\6\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\21\34" + "\1\115\10\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\15\34" + "\1\116\14\34" , "" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\23\34" + "\1\117\6\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\10\34" + "\1\120\21\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\23\34" + "\1\121\6\34" , "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\4\34" + "\1\123\25\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\25\34" + "\1\124\4\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\30\34" + "\1\125\1\34" , "" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\21\34" + "\1\126\10\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\4\34" + "\1\127\25\34" , "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\10\34" + "\1\131\21\34" , "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\34" , "" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\31\34" + "\1\133" , "" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\4\34" + "\1\134\25\34" ,  "\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\3\34" + "\1\135\26\34" ,  "\1\136\1\36\1\uffff\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff" + "\32\34" , "" } ;   static final   short  [ ]  DFA17_eot =  DFA . unpackEncodedString  ( DFA17_eotS ) ;   static final   short  [ ]  DFA17_eof =  DFA . unpackEncodedString  ( DFA17_eofS ) ;   static final   char  [ ]  DFA17_min =  DFA . unpackEncodedStringToUnsignedChars  ( DFA17_minS ) ;   static final   char  [ ]  DFA17_max =  DFA . unpackEncodedStringToUnsignedChars  ( DFA17_maxS ) ;   static final   short  [ ]  DFA17_accept =  DFA . unpackEncodedString  ( DFA17_acceptS ) ;   static final   short  [ ]  DFA17_special =  DFA . unpackEncodedString  ( DFA17_specialS ) ;   static final   short  [ ] [ ]  DFA17_transition ;  static  {   int  numStates =  DFA17_transitionS . length ;   DFA17_transition =  new  short  [ numStates ]  [ ] ;  for (   int  i = 0 ;  i < numStates ;  i ++ )  {    DFA17_transition [ i ] =  DFA . unpackEncodedString  (  DFA17_transitionS [ i ] ) ; } }  class DFA17  extends DFA  {   public DFA17  (  BaseRecognizer recognizer )  {    this . recognizer = recognizer ;    this . decisionNumber = 17 ;    this . eot = DFA17_eot ;    this . eof = DFA17_eof ;    this . min = DFA17_min ;    this . max = DFA17_max ;    this . accept = DFA17_accept ;    this . special = DFA17_special ;    this . transition = DFA17_transition ; }   public String getDescription  ( )  {  return "1:1: Tokens : ( DATA | TYPE_MAP | TYPE_SET | TYPE_LIST | PARAMETERIZED_TYPE | TYPE_PRIMITIVE | PROPERTY | OPTION | PAIR | ENUM | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | COMMENT | TYPE | ID | WS | INT | FLOAT | STRING );" ; } } 
<<<<<<<
=======
  public final void mT__25  ( )  throws RecognitionException  {  try  {   int  _type = T__25 ;   int  _channel = DEFAULT_TOKEN_CHANNEL ;  {   match  ( '{' ) ; }    state . type = _type ;    state . channel = _channel ; }  finally  { } }
>>>>>>>
 
<<<<<<<
=======
  static final String  DFA10_acceptS = "\2\uffff\1\2\1\1\1\3" ;
>>>>>>>
 
<<<<<<<
=======
  static final  String  [ ]  DFA10_transitionS =  { "\1\2\1\uffff\12\1" , "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4" , "" , "" , "" } ;
>>>>>>>
 
<<<<<<<
=======
 class DFA10  extends DFA  {   public DFA10  (  BaseRecognizer recognizer )  {    this . recognizer = recognizer ;    this . decisionNumber = 10 ;    this . eot = DFA10_eot ;    this . eof = DFA10_eof ;    this . min = DFA10_min ;    this . max = DFA10_max ;    this . accept = DFA10_accept ;    this . special = DFA10_special ;    this . transition = DFA10_transition ; }   public String getDescription  ( )  {  return "116:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );" ; } }
>>>>>>>
 
<<<<<<<
=======
  static final String  DFA16_eotS =    "\1\uffff\7\32\1\44\12\uffff\1\32\1\uffff\1\45\2\uffff\2\32\2\uffff" + "\7\32\3\uffff\1\32\1\62\1\64\7\32\1\75\4\uffff\1\77\1\32\1\101\3" + "\32\1\105\4\uffff\1\32\1\uffff\3\32\1\uffff\3\32\1\115\3\32\1\uffff" + "\2\32\1\123\1\32\1\125\1\uffff\1\32\1\uffff\3\32\1\uffff" ;
>>>>>>>
 
<<<<<<<
=======
  static final String  DFA16_eofS = "\132\uffff" ;
>>>>>>>
 
<<<<<<<
=======
  static final String  DFA16_minS =   "\1\11\7\56\1\175\12\uffff\1\56\1\uffff\1\56\2\uffff\2\56\2\uffff" + "\7\56\3\uffff\13\56\4\uffff\7\56\4\uffff\1\56\1\uffff\3\56\1\uffff" + "\7\56\1\uffff\5\56\1\uffff\1\56\1\uffff\2\56\1\55\1\uffff" ;
>>>>>>>
 
<<<<<<<
=======
  static final String  DFA16_maxS =    "\1\175\7\172\1\175\12\uffff\1\172\1\uffff\1\145\2\uffff\2\172\2" + "\uffff\7\172\3\uffff\13\172\4\uffff\7\172\4\uffff\1\172\1\uffff" + "\3\172\1\uffff\7\172\1\uffff\5\172\1\uffff\1\172\1\uffff\3\172\1" + "\uffff" ;
>>>>>>>
 
<<<<<<<
=======
  static final String  DFA16_acceptS =     "\11\uffff\1\14\1\16\1\17\1\20\1\21\1\22\1\23\1\25\1\27\1\32\1\uffff" + "\1\35\1\uffff\1\37\1\40\2\uffff\1\34\1\33\7\uffff\1\15\1\13\1\36" + "\13\uffff\1\26\1\2\1\30\1\3\7\uffff\1\24\1\1\1\31\1\4\1\uffff\1" + "\11\3\uffff\1\12\7\uffff\1\10\5\uffff\1\7\1\uffff\1\6\3\uffff\1" + "\5" ;
>>>>>>>
 
<<<<<<<
=======
  static final String  DFA16_specialS = "\132\uffff}>" ;
>>>>>>>
 
<<<<<<<
=======
  static final  String  [ ]  DFA16_transitionS =  {    "\2\24\2\uffff\1\24\22\uffff\1\24\6\uffff\1\27\1\15\1\16\2\uffff" + "\1\12\1\uffff\1\26\1\uffff\12\25\1\uffff\1\20\1\22\1\17\1\21" + "\2\uffff\32\23\1\13\1\uffff\1\14\3\uffff\3\23\1\1\1\7\6\23\1" + "\4\1\2\1\23\1\6\1\5\2\23\1\3\7\23\1\10\1\uffff\1\11" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\1\30" + "\31\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\1\34" + "\31\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\4\31" + "\1\35\25\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\10" + "\31\1\36\21\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\1\37" + "\20\31\1\40\10\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\17" + "\31\1\41\12\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\15" + "\31\1\42\14\31" , "\1\43" , "" , "" , "" , "" , "" , "" , "" , "" , "" , "" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32" + "\31" , "" , "\1\26\1\uffff\12\25\13\uffff\1\26\37\uffff\1\26" , "" , "" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\23" + "\31\1\46\6\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32" + "\31" , "" , "" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\17" + "\31\1\47\12\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\23" + "\31\1\50\6\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\22" + "\31\1\51\7\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\10" + "\31\1\53\10\31\1\52\10\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\10" + "\31\1\54\5\31\1\55\13\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\23" + "\31\1\56\6\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\24" + "\31\1\57\5\31" , "" , "" , "" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\1\60" + "\31\31" ,  "\1\33\1\uffff\12\31\2\uffff\1\61\4\uffff\32\31\4\uffff\1\31" + "\1\uffff\32\31" ,  "\1\33\1\uffff\12\31\2\uffff\1\63\4\uffff\32\31\4\uffff\1\31" + "\1\uffff\32\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\23" + "\31\1\65\6\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\1\66" + "\31\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\21" + "\31\1\67\10\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\14" + "\31\1\70\15\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\17" + "\31\1\71\12\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\10" + "\31\1\72\21\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\14" + "\31\1\73\15\31" ,  "\1\33\1\uffff\12\31\1\uffff\1\74\5\uffff\32\31\4\uffff\1\31" + "\1\uffff\32\31" , "" , "" , "" , "" ,  "\1\33\1\uffff\12\31\2\uffff\1\76\4\uffff\32\31\4\uffff\1\31" + "\1\uffff\32\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\14" + "\31\1\100\15\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32" + "\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\10" + "\31\1\102\21\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\4\31" + "\1\103\25\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\16" + "\31\1\104\13\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32" + "\31" , "" , "" , "" , "" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\4\31" + "\1\106\25\31" , "" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\23" + "\31\1\107\6\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\21" + "\31\1\110\10\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\15" + "\31\1\111\14\31" , "" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\23" + "\31\1\112\6\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\10" + "\31\1\113\21\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\23" + "\31\1\114\6\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32" + "\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\4\31" + "\1\116\25\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\25" + "\31\1\117\4\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\30" + "\31\1\120\1\31" , "" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\21" + "\31\1\121\10\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\4\31" + "\1\122\25\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32" + "\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\10" + "\31\1\124\21\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32" + "\31" , "" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\31" + "\31\1\126" , "" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\4\31" + "\1\127\25\31" ,  "\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\3\31" + "\1\130\26\31" ,  "\1\131\1\33\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff" + "\32\31" , "" } ;
>>>>>>>
 
<<<<<<<
=======
 class DFA16  extends DFA  {   public DFA16  (  BaseRecognizer recognizer )  {    this . recognizer = recognizer ;    this . decisionNumber = 16 ;    this . eot = DFA16_eot ;    this . eof = DFA16_eof ;    this . min = DFA16_min ;    this . max = DFA16_max ;    this . accept = DFA16_accept ;    this . special = DFA16_special ;    this . transition = DFA16_transition ; }   public String getDescription  ( )  {  return "1:1: Tokens : ( DATA | TYPE_MAP | TYPE_SET | TYPE_LIST | PARAMETERIZED_TYPE | TYPE_PRIMITIVE | PROPERTY | OPTION | PAIR | ENUM | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | TYPE | ID | WS | INT | FLOAT | STRING );" ; } }
>>>>>>>
 }