  package   net . tridentsdk . inventory ;   import    javax . annotation . concurrent . Immutable ;    @ Immutable public enum InventoryType  { 
<<<<<<<
=======
 PLAYER  ( "Player" )
>>>>>>>
 ,  CONTAINER ,  CHEST ,  CRAFTING_TABLE ,  FURNACE ,  DISPENSER ,  ENCHANTING_TABLE ,  BREWING_STAND ,  VILLAGER ,  BEACON ,  ANVIL ,  HOPPER ,  DROPPER ,  SHULKER_BOX ,  
<<<<<<<
ENTITY_HORSE
=======
HORSE
>>>>>>>
  ( "EntityHorse" ) ,  PLAYER  ( "player" )  ;   private final String  raw ;  InventoryType  ( )  {    this . 
<<<<<<<
raw
=======
name
>>>>>>>
 =  "minecraft:" +   this . name  ( ) . toLowerCase  ( ) ; }  InventoryType  (  String 
<<<<<<<
raw
=======
name
>>>>>>>
 )  {    this . 
<<<<<<<
raw
=======
name
>>>>>>>
 = 
<<<<<<<
raw
=======
name
>>>>>>>
 ; }    @ Override public String toString  ( )  {  return  this . 
<<<<<<<
raw
=======
name
>>>>>>>
 ; }   private final String  name ; }