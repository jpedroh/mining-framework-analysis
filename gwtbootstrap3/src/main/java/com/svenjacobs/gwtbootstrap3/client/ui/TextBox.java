  package     com . svenjacobs . gwtbootstrap3 . client . ui ;   import      com . google . gwt . dom . client . Element ;  import      com . google . gwt . user . client . DOM ;  import       com . svenjacobs . gwtbootstrap3 . client . ui . base . TextBoxBase ;  import       com . svenjacobs . gwtbootstrap3 . client . ui . constants . Styles ;   public class TextBox  extends TextBoxBase  {   public TextBox  ( )  {  this  (  DOM . createInputText  ( ) ) ; }   public TextBox  (   final Element element )  {  super  ( element ) ;   setStyleName  (  Styles . FORM_CONTROL ) ; }   public void clear  ( )  {   super . setValue  ( null ) ; } 
<<<<<<<
=======
   @ Override public void setVisibleOn  (   final String deviceSizeString )  {   StyleHelper . setVisibleOn  ( this , deviceSizeString ) ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public void setHiddenOn  (   final String deviceSizeString )  {   StyleHelper . setHiddenOn  ( this , deviceSizeString ) ; }
>>>>>>>
 }