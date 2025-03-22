  package   net . tridentsdk . meta ;   import     net . tridentsdk . meta . nbt . TagCompound ;  import   javax . annotation . Nullable ;  import    javax . annotation . concurrent . ThreadSafe ;   public  @ ThreadSafe class ItemMeta  {   public  @ Nullable TagCompound toNbt  ( )  {  if  (   this . nbt . isEmpty  ( ) )  {  return null ; }  return 
<<<<<<<
null
=======
 this . nbt
>>>>>>>
 ; }   private final TagCompound  nbt =  new TagCompound  ( ) ; }