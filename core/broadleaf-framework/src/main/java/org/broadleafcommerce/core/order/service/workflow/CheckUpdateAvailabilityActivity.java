  package      org . broadleafcommerce . core . order . service . workflow ;   import     org . apache . commons . logging . Log ;  import     org . apache . commons . logging . LogFactory ;  import      org . broadleafcommerce . core . catalog . domain . Sku ;  import      org . broadleafcommerce . core . catalog . service . CatalogService ;  import      org . broadleafcommerce . core . order . domain . BundleOrderItem ;  import      org . broadleafcommerce . core . order . domain . DiscreteOrderItem ;  import      org . broadleafcommerce . core . order . domain . Order ;  import      org . broadleafcommerce . core . order . domain . OrderItem ;  import      org . broadleafcommerce . core . order . service . OrderItemService ;  import       org . broadleafcommerce . core . order . service . call . NonDiscreteOrderItemRequestDTO ;  import       org . broadleafcommerce . core . order . service . call . OrderItemRequestDTO ;  import     org . broadleafcommerce . core . workflow . ProcessContext ;  import    org . springframework . stereotype . Component ;  import   java . util . HashMap ;  import   java . util . Map ;  import   javax . annotation . Resource ;  import      org . broadleafcommerce . core . catalog . domain . ProductSkuUsage ;  import      org . springframework . beans . factory . annotation . Value ;    @ Component  ( "blCheckUpdateAvailabilityActivity" ) public class CheckUpdateAvailabilityActivity  extends AbstractCheckAvailabilityActivity  {   private static final Log  LOG =  LogFactory . getLog  (  CheckUpdateAvailabilityActivity . class ) ;   public static final  int  ORDER = 2000 ;    @ Resource  (  name = "blCatalogService" ) protected CatalogService  catalogService ;    @ Resource  (  name = "blOrderItemService" ) protected OrderItemService  orderItemService ;   public CheckUpdateAvailabilityActivity  ( )  {   setOrder  ( ORDER ) ; }    @ Override public  ProcessContext  < CartOperationRequest > execute  (   ProcessContext  < CartOperationRequest > context )  throws Exception  {  CartOperationRequest  request =  context . getSeedData  ( ) ;  OrderItemRequestDTO  orderItemRequestDTO =  request . getItemRequest  ( ) ;  if  (  orderItemRequestDTO instanceof NonDiscreteOrderItemRequestDTO )  {  return context ; }  Sku  sku ;  Long  orderItemId =   request . getItemRequest  ( ) . getOrderItemId  ( ) ;  OrderItem  orderItem =  orderItemService . readOrderItemById  ( orderItemId ) ;  if  (  orderItem instanceof DiscreteOrderItem )  {   sku =   (  ( DiscreteOrderItem ) orderItem ) . getSku  ( ) ; } else  if  (  orderItem instanceof BundleOrderItem )  {   sku =   (  ( BundleOrderItem ) orderItem ) . getSku  ( ) ; } else  {   LOG . warn  (  "Could not check availability; did not recognize passed-in item " +   orderItem . getClass  ( ) . getName  ( ) ) ;  return context ; }  if  ( 
<<<<<<<
  sku . getProduct  ( ) . getEnableDefaultSkuInInventory  ( )
=======
 enableUseDefaultSkuInventory &&   (  ( ProductSkuUsage )  sku . getProduct  ( ) ) . getUseDefaultSkuInInventory  ( )
>>>>>>>
 )  {   sku =   sku . getProduct  ( ) . getDefaultSku  ( ) ; }  Order  order =   context . getSeedData  ( ) . getOrder  ( ) ;  Integer  requestedQuantity =   request . getItemRequest  ( ) . getQuantity  ( ) ;   Map  < Sku , Integer >  skuItems =  new  HashMap  < >  ( ) ;  for ( OrderItem orderItemFromOrder :  order . getOrderItems  ( ) )  {  Sku  skuFromOrder = null ;  if  (  orderItemFromOrder instanceof DiscreteOrderItem )  {   skuFromOrder =   (  ( DiscreteOrderItem ) orderItemFromOrder ) . getSku  ( ) ; } else  if  (  orderItemFromOrder instanceof BundleOrderItem )  {   skuFromOrder =   (  ( BundleOrderItem ) orderItemFromOrder ) . getSku  ( ) ; }  if  (   
<<<<<<<
skuFromOrder
=======
 skuFromOrder != null
>>>>>>>
 
<<<<<<<
!=
=======
&&
>>>>>>>
 
<<<<<<<
null
=======
enableUseDefaultSkuInventory
>>>>>>>
 &&  
<<<<<<<
 skuFromOrder . getProduct  ( )
=======
 (  ( ProductSkuUsage )  skuFromOrder . getProduct  ( ) )
>>>>>>>
 . 
<<<<<<<
getEnableDefaultSkuInInventory
=======
getUseDefaultSkuInInventory
>>>>>>>
  ( ) )  {   skuFromOrder =   skuFromOrder . getProduct  ( ) . getDefaultSku  ( ) ; }  if  (    skuFromOrder != null &&  skuFromOrder . equals  ( sku ) &&  !  orderItemFromOrder . equals  ( orderItem ) )  {   skuItems . merge  ( sku ,  orderItemFromOrder . getQuantity  ( ) ,   ( oldVal , newVal ) ->  oldVal + newVal ) ; } }   skuItems . merge  ( sku , requestedQuantity ,   ( oldVal , newVal ) ->  oldVal + newVal ) ;  for (   Map . Entry  < Sku , Integer > entry :  skuItems . entrySet  ( ) )  {   checkSkuAvailability  ( order ,  entry . getKey  ( ) ,  entry . getValue  ( ) ) ; }  Integer  previousQty =  orderItem . getQuantity  ( ) ;  for ( OrderItem child :  orderItem . getChildOrderItems  ( ) )  {  Sku  childSku =   (  ( DiscreteOrderItem ) child ) . getSku  ( ) ;  if  ( 
<<<<<<<
  childSku . getProduct  ( ) . getEnableDefaultSkuInInventory  ( )
=======
 enableUseDefaultSkuInventory &&   (  ( ProductSkuUsage )  childSku . getProduct  ( ) ) . getUseDefaultSkuInInventory  ( )
>>>>>>>
 )  {   childSku =   childSku . getProduct  ( ) . getDefaultSku  ( ) ; }  Integer  childQuantity =  child . getQuantity  ( ) ;   childQuantity =  childQuantity / previousQty ;   checkSkuAvailability  ( order , childSku ,  childQuantity * requestedQuantity ) ; }  return context ; }    @ Value  ( "${enable.weave.use.default.sku.inventory:false}" ) protected boolean  enableUseDefaultSkuInventory = false ; }