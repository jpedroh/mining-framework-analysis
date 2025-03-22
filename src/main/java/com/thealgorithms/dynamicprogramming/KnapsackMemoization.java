  package   com . thealgorithms . dynamicprogramming ;   public class KnapsackMemoization  {   int knapSack  (   int 
<<<<<<<
W
=======
capacity
>>>>>>>
 ,    int  [ ] 
<<<<<<<
wt
=======
weights
>>>>>>>
 ,    int  [ ] 
<<<<<<<
val
=======
profits
>>>>>>>
 ,   int 
<<<<<<<
N
=======
numOfItems
>>>>>>>
 )  {    int  [ ] [ ] 
<<<<<<<
 dp =  new  int  [  N + 1 ]  [  W + 1 ]
=======
 dpTable =  new  int  [  numOfItems + 1 ]  [  capacity + 1 ]
>>>>>>>
 ;  for (   int  i = 0 ;  i <  
<<<<<<<
N
=======
numOfItems
>>>>>>>
 + 1 ;  i ++ )  {  for (   int  j = 0 ;  j <  
<<<<<<<
W
=======
capacity
>>>>>>>
 + 1 ;  j ++ )  {     
<<<<<<<
dp
=======
dpTable
>>>>>>>
 [ i ] [ j ] =  - 1 ; } }  return  
<<<<<<<
knapSackRec
=======
solveKnapsackRecursive
>>>>>>>
  ( 
<<<<<<<
W
=======
capacity
>>>>>>>
 , 
<<<<<<<
wt
=======
weights
>>>>>>>
 , 
<<<<<<<
val
=======
profits
>>>>>>>
 , 
<<<<<<<
N
=======
numOfItems
>>>>>>>
 , 
<<<<<<<
dp
=======
dpTable
>>>>>>>
 ) ; }   int knapSackRec  (   int W ,    int  [ ] wt ,    int  [ ] val ,   int n ,    int  [ ] [ ] dp )  {  if  (   n == 0 ||  W == 0 )  {  return 0 ; }  if  (    dp [ n ] [ W ] !=  - 1 )  {  return   dp [ n ] [ W ] ; }  if  (   wt [  n - 1 ] > W )  {     dp [ n ] [ W ] =  knapSackRec  ( W , wt , val ,  n - 1 , dp ) ;  return   dp [ n ] [ W ] ; } else  {  return    dp [ n ] [ W ] =  Math . max  (  (   val [  n - 1 ] +  knapSackRec  (  W -  wt [  n - 1 ] , wt , val ,  n - 1 , dp ) ) ,  knapSackRec  ( W , wt , val ,  n - 1 , dp ) ) ; } }   int solveKnapsackRecursive  (   int capacity ,    int  [ ] weights ,    int  [ ] profits ,   int numOfItems ,    int  [ ] [ ] dpTable )  {  if  (   numOfItems == 0 ||  capacity == 0 )  {  return 0 ; }  if  (    dpTable [ numOfItems ] [ capacity ] !=  - 1 )  {  return   dpTable [ numOfItems ] [ capacity ] ; }  if  (   weights [  numOfItems - 1 ] > capacity )  {     dpTable [ numOfItems ] [ capacity ] =  solveKnapsackRecursive  ( capacity , weights , profits ,  numOfItems - 1 , dpTable ) ;  return   dpTable [ numOfItems ] [ capacity ] ; } else  {  return    dpTable [ numOfItems ] [ capacity ] =  Math . max  (  (   profits [  numOfItems - 1 ] +  solveKnapsackRecursive  (  capacity -  weights [  numOfItems - 1 ] , weights , profits ,  numOfItems - 1 , dpTable ) ) ,  solveKnapsackRecursive  ( capacity , weights , profits ,  numOfItems - 1 , dpTable ) ) ; } } }