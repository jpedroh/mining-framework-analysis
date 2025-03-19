  package      org . fenixedu . academic . domain . accounting . postingRules ;   import   java . math . BigDecimal ;  import   java . util . Map ;  import      org . fenixedu . academic . domain . accounting . EntryType ;  import      org . fenixedu . academic . domain . accounting . Event ;  import      org . fenixedu . academic . domain . accounting . EventType ;  import      org . fenixedu . academic . domain . accounting . ResidenceEvent ;  import      org . fenixedu . academic . domain . accounting . ServiceAgreementTemplate ;  import     org . fenixedu . academic . util . Money ;  import    org . joda . time . DateTime ;  import    org . joda . time . Days ;  import    org . joda . time . LocalDate ;  import       edu . emory . mathcs . backport . java . util . Collections ;   public class ResidencePR  extends ResidencePR_Base  {   public ResidencePR  (   final DateTime startDate ,   final DateTime endDate ,   final ServiceAgreementTemplate serviceAgreementTemplate ,  Money penaltyPerDay )  {   super . init  (  EntryType . RESIDENCE_FEE ,  EventType . RESIDENCE_PAYMENT , startDate , endDate , serviceAgreementTemplate ) ;   setPenaltyPerDay  ( penaltyPerDay ) ; }    @ Override public  Map  < LocalDate , Money > getDueDatePenaltyAmountMap  (  Event event ,  DateTime when )  {  ResidenceEvent  residenceEvent =  ( ResidenceEvent ) event ;  if  (   residenceEvent . getPaymentLimitDate  ( ) . isAfter  ( when ) )  {  return  Collections . emptyMap  ( ) ; }   final BigDecimal  daysBetween =  BigDecimal . valueOf  (   Days . daysBetween  (  
<<<<<<<
 residenceEvent . getPaymentLimitDate  ( )
=======
residenceEvent
>>>>>>>
 . 
<<<<<<<
toLocalDate
=======
getPaymentLimitDate
>>>>>>>
  ( ) , 
<<<<<<<
 when . toLocalDate  ( )
=======
when
>>>>>>>
 ) . getDays  ( ) ) ;   final Money  amount =   getPenaltyPerDay  ( ) . multiply  ( daysBetween ) ;  return  Collections . singletonMap  (   residenceEvent . getPaymentLimitDate  ( ) . toLocalDate  ( ) , amount ) ; }    @ Override protected Money doCalculationForAmountToPay  (  Event event ,  DateTime when )  {  return   (  ( ResidenceEvent ) event ) . getRoomValue  ( ) ; } }