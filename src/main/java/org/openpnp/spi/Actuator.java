  package   org . openpnp . spi ;   import     org . openpnp . gui . support . Wizard ;  import    org . openpnp . model . AxesLocation ;  import     org . openpnp . spi . base . AbstractActuator ;   public interface Actuator  extends  HeadMountable , WizardConfigurable , PropertySheetHolder  {   public Driver getDriver  ( ) ;   public void setDriver  (  Driver driver ) ;   public void actuate  (  boolean on )  throws Exception ;   public void actuate  (   double value )  throws Exception ;   public void actuate  (  String value )  throws Exception ;   public Object getLastActuationValue  ( ) ;   public String read  ( )  throws Exception ;   public  <  T > String read  (  T value )  throws Exception ;  boolean isCoordinatedBeforeActuate  ( ) ;  boolean isCoordinatedAfterActuate  ( ) ;  boolean isCoordinatedBeforeRead  ( ) ;   public static interface InterlockMonitor  {  boolean interlockActuation  (  Actuator actuator ,  AxesLocation location0 ,  AxesLocation location1 ,  boolean beforeMove ,   double speed )  throws Exception ;  Wizard getConfigurationWizard  (  AbstractActuator actuator ) ; }   public InterlockMonitor getInterlockMonitor  ( ) ;   public enum ActuatorValueType  {  Double ,  Boolean ,  String ,  Profile }   public ActuatorValueType getValueType  ( ) ;   public  String  [ ] getProfileValues  ( ) ;   public Object getDefaultOnValue  ( ) ;   public Object getDefaultOffValue  ( ) ;   public void actuate  (  Object value )  throws Exception ;   abstract void actuateProfile  (  String name )  throws Exception ;   abstract void actuateProfile  (  boolean on )  throws Exception ;   public boolean isActuated  ( ) ; }