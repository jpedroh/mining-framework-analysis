  package   org . gedcom4j . factory ;   import    org . gedcom4j . model . Family ;  import    org . gedcom4j . model . FamilyChild ;  import    org . gedcom4j . model . FamilySpouse ;  import    org . gedcom4j . model . Gedcom ;  import    org . gedcom4j . model . Individual ;  import    org . gedcom4j . model . IndividualReference ;  import   java . util . Arrays ;   public class FamilyFactory  {   public Family create  (  Gedcom g ,  Individual father ,  Individual mother ,  Individual ...  children )  {  if  (   father != null &&  !   g . getIndividuals  ( ) . containsKey  (  father . getXref  ( ) ) )  {  throw  new IllegalArgumentException  (  "Father could not be found by xref in supplied gedcom object: " +  father . getXref  ( ) ) ; }  if  (   mother != null &&  !   g . getIndividuals  ( ) . containsKey  (  mother . getXref  ( ) ) )  {  throw  new IllegalArgumentException  (  "Mother could not be found by xref in supplied gedcom object: " +  mother . getXref  ( ) ) ; }  if  (  children != null )  {  for ( Individual kid : children )  {  if  (  !   g . getIndividuals  ( ) . containsKey  (  kid . getXref  ( ) ) )  {  throw  new IllegalArgumentException  (  "Child could not be found by xref in supplied gedcom object: " +  kid . getXref  ( ) ) ; } } }  Family  result =  new Family  ( ) ;  for (   int  xref =   g . getFamilies  ( ) . size  ( ) ;   !   g . getFamilies  ( ) . containsKey  (   "@F" + xref + "@" ) &&   result . getXref  ( ) == null ;  xref ++ )  {   result . setXref  (   "@F" + xref + "@" ) ;    g . getFamilies  ( ) . put  (  result . getXref  ( ) , result ) ; }   result . setHusband  (  new IndividualReference  ( father ) ) ;   result . setWife  (  new IndividualReference  ( mother ) ) ;  if  (  
<<<<<<<
children
=======
 children != null
>>>>>>>
 
<<<<<<<
!=
=======
&&
>>>>>>>
 
<<<<<<<
null
=======
  children . length > 0
>>>>>>>
 )  { 
<<<<<<<
 for ( Individual child : children )  {    result . getChildren  ( true ) . add  (  new IndividualReference  ( child ) ) ; }
=======
   result . getChildren  ( true ) . addAll  (  Arrays . asList  ( children ) ) ;
>>>>>>>
 }  if  (  father != null )  {  FamilySpouse  fams =  new FamilySpouse  ( ) ;   fams . setFamily  ( result ) ;    father . getFamiliesWhereSpouse  ( true ) . add  ( fams ) ; }  if  (  mother != null )  {  FamilySpouse  fams =  new FamilySpouse  ( ) ;   fams . setFamily  ( result ) ;    mother . getFamiliesWhereSpouse  ( true ) . add  ( fams ) ; }  if  (  children != null )  {  for ( Individual kid : children )  {  FamilyChild  famc =  new FamilyChild  ( ) ;   famc . setFamily  ( result ) ;    kid . getFamiliesWhereChild  ( true ) . add  ( famc ) ; } }  return result ; } }