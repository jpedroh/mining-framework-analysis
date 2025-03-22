  package      org . broadleafcommerce . core . order . service . workflow ;   import     org . apache . commons . logging . Log ;  import     org . apache . commons . logging . LogFactory ;  import      org . broadleafcommerce . core . catalog . domain . Sku ;  import      org . broadleafcommerce . core . catalog . service . CatalogService ;  import      org . broadleafcommerce . core . order . domain . BundleOrderItem ;  import      org . broadleafcommerce . core . order . domain . DiscreteOrderItem ;  import      org . broadleafcommerce . core . order . domain . Order ;  import      org . broadleafcommerce . core . order . domain . OrderItem ;  import      org . broadleafcommerce . core . order . service . OrderItemService ;  import       org . broadleafcommerce . core . order . service . call . NonDiscreteOrderItemRequestDTO ;  import       org . broadleafcommerce . core . order . service . call . OrderItemRequestDTO ;  import     org . broadleafcommerce . core . workflow . ProcessContext ;  import    org . springframework . stereotype . Component ;  import   java . util . HashMap ;  import   java . util . Map ;  import   javax . annotation . Resource ;  import      org . broadleafcommerce . core . catalog . domain . ProductSkuUsage ;  import      org . springframework . beans . factory . annotation . Value ;    @ Component  ( "blCheckAddAvailabilityActivity" ) public class CheckAddAvailabilityActivity  extends AbstractCheckAvailabilityActivity  {   private static final Log  LOG =  LogFactory . getLog  (  CheckAddAvailabilityActivity . class ) ;   public static final  int  ORDER = 2000 ;    @ Resource  (  name = "blCatalogService" ) protected CatalogService  catalogService ;    @ Resource  (  name = "blOrderItemService" ) protected OrderItemService  orderItemService ;   public CheckAddAvailabilityActivity  ( )  {   setOrder  ( ORDER ) ; }    @ Override public  ProcessContext  < CartOperationRequest > execute  (   ProcessContext  < CartOperationRequest > context )  throws Exception  {  CartOperationRequest  request =  context . getSeedData  ( ) ;  OrderItemRequestDTO  orderItemRequestDTO =  request . getItemRequest  ( ) ;  if  (  orderItemRequestDTO instanceof NonDiscreteOrderItemRequestDTO )  {  return context ; }  Long  skuId =   request . getItemRequest  ( ) . getSkuId  ( ) ;  Sku  sku =  catalogService . findSkuById  ( skuId ) ;  if  ( 
<<<<<<<
  sku . getProduct  ( ) . getEnableDefaultSkuInInventory  ( )
=======
 enableUseDefaultSkuInventory &&   (  ( ProductSkuUsage )  sku . getProduct  ( ) ) . getUseDefaultSkuInInventory  ( )
>>>>>>>
 )  {   sku =   sku . getProduct  ( ) . getDefaultSku  ( ) ; }  Order  order =   context . getSeedData  ( ) . getOrder  ( ) ;  Integer  requestedQuantity =   request . getItemRequest  ( ) . getQuantity  ( ) ;   Map  < Sku , Integer >  skuItems =  new  HashMap  < >  ( ) ;  for ( OrderItem orderItem :  order . getOrderItems  ( ) )  {  Sku  skuFromOrder = null ;  if  (  orderItem instanceof DiscreteOrderItem )  {   skuFromOrder =   (  ( DiscreteOrderItem ) orderItem ) . getSku  ( ) ; } else  if  (  orderItem instanceof BundleOrderItem )  {   skuFromOrder =   (  ( BundleOrderItem ) orderItem ) . getSku  ( ) ; }  if  (   
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
  ( ) )  {   skuFromOrder =   skuFromOrder . getProduct  ( ) . getDefaultSku  ( ) ; }  if  (   skuFromOrder != null &&  skuFromOrder . equals  ( sku ) )  {   skuItems . merge  ( sku ,  orderItem . getQuantity  ( ) ,   ( oldVal , newVal ) ->  oldVal + newVal ) ; } }   skuItems . merge  ( sku , requestedQuantity ,   ( oldVal , newVal ) ->  oldVal + newVal ) ;  for (   Map . Entry  < Sku , Integer > entry :  skuItems . entrySet  ( ) )  {   checkSkuAvailability  ( order ,  entry . getKey  ( ) ,  entry . getValue  ( ) ) ; }  return context ; }    @ Value  ( "${enable.weave.use.default.sku.inventory:false}" ) protected boolean  enableUseDefaultSkuInventory = false ; }