  package    dk . frankbille . scoreboard . ratings ;   public class ELOCalculator  {   public static  double  DEFAULT_RATING = 1000 ;   private static  double  K_FACTOR = 50 ;   private static  double  RATING_FACTOR = 400 ;   private static  double  SCORE_PERCENT = 50 ;   public static  double calculate  (   double winnerRating ,   int winnerScore ,   double loserRating ,   int loserScore )  {   double  expected =  Math . pow  ( 10 ,  winnerRating / RATING_FACTOR ) ;   expected =  expected /  (  expected +  Math . pow  ( 10 ,  loserRating / RATING_FACTOR ) ) ;   final  double  winMargin =   (  (  double )  (  winnerScore - loserScore ) ) / winnerScore ;   final  double  maxRatingPoints =    winMargin * K_FACTOR *  (  SCORE_PERCENT / 100 ) +   K_FACTOR *  (  100 - SCORE_PERCENT ) / 100 ;  if  (  winnerScore > loserScore )  {   winMargin =   (  (  double )  (  winnerScore - loserScore ) ) / winnerScore ;   maxRatingPoints =    winMargin * K_FACTOR *  (  SCORE_PERCENT / 100 ) +   K_FACTOR *  (  100 - SCORE_PERCENT ) / 100 ; } else  {  if  (  winnerRating > loserRating )  {   maxRatingPoints =    K_FACTOR *  (  100 - SCORE_PERCENT ) / 100 / 2 ; } else  if  (  winnerRating < loserRating )  {   maxRatingPoints =     - K_FACTOR *  (  100 - SCORE_PERCENT ) / 100 / 2 ; } else  {   winMargin = 0 ;   maxRatingPoints = 0 ; } }  return  maxRatingPoints *  (  1 - expected ) ; } }