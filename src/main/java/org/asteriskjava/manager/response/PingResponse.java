  package    org . asteriskjava . manager . response ;   public class PingResponse  extends ManagerResponse  {   private static final  long  serialVersionUID = 0L ;   private String  ping ;   private 
<<<<<<<
String
=======
Double
>>>>>>>
  timestamp ;   public String getPing  ( )  {  return ping ; }   public void setPing  (  String ping )  {    this . ping = ping ; }   public 
<<<<<<<
String
=======
Double
>>>>>>>
 getTimestamp  ( )  {  return timestamp ; }   public void setTimestamp  (  String timestamp )  {    this . timestamp = timestamp ; }   public void setTimestamp  (  Double timestamp )  {    this . timestamp = timestamp ; } }