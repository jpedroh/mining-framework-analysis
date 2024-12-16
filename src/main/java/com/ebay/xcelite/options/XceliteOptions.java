  package    com . ebay . xcelite . options ;   import     com . ebay . xcelite . policies . MissingCellPolicy ;  import     com . ebay . xcelite . policies . MissingRowPolicy ;  import     com . ebay . xcelite . policies . TrailingEmptyRowPolicy ;  import  lombok . EqualsAndHashCode ;    @ EqualsAndHashCode public class XceliteOptions  {   private Boolean  hasHeaderRow = null ;   private boolean  headerParsingIsCaseSensitive = true ;   private Integer  headerRowIndex = 0 ;   private Integer  firstDataRowIndex = 0 ;   private MissingCellPolicy  missingCellPolicy =  MissingCellPolicy . RETURN_BLANK_AS_NULL ;   private MissingRowPolicy  missingRowPolicy =  MissingRowPolicy . NULL ;   private TrailingEmptyRowPolicy  trailingEmptyRowPolicy =  TrailingEmptyRowPolicy . SKIP ;   private boolean  anyColumnCreatesCollection = false ;   public XceliteOptions  ( )  { }   public XceliteOptions  (  XceliteOptions other )  {    this . hasHeaderRow =  other . hasHeaderRow ;    this . headerParsingIsCaseSensitive =  other . headerParsingIsCaseSensitive ;    this . headerRowIndex =  other . headerRowIndex ;    this . firstDataRowIndex =  other . firstDataRowIndex ;    this . missingCellPolicy =  other . missingCellPolicy ;    this . missingRowPolicy =  other . missingRowPolicy ;    this . trailingEmptyRowPolicy =  other . trailingEmptyRowPolicy ;    this . anyColumnCreatesCollection =  other . anyColumnCreatesCollection ; }   public Integer getHeaderRowIndex  ( )  {  return headerRowIndex ; }   public void setHeaderRowIndex  (  Integer headerRowIndex )  {    this . headerRowIndex = headerRowIndex ; }   public Integer getFirstDataRowIndex  ( )  {  return firstDataRowIndex ; }   public void setFirstDataRowIndex  (  Integer firstDataRowIndex )  {    this . firstDataRowIndex = firstDataRowIndex ; }   public MissingCellPolicy getMissingCellPolicy  ( )  {  return missingCellPolicy ; }   public void setMissingCellPolicy  (  MissingCellPolicy missingCellPolicy )  {    this . missingCellPolicy = missingCellPolicy ; }   public MissingRowPolicy getMissingRowPolicy  ( )  {  return missingRowPolicy ; }   public void setMissingRowPolicy  (  MissingRowPolicy missingRowPolicy )  {    this . missingRowPolicy = missingRowPolicy ; }   public Boolean isHasHeaderRow  ( )  {  return hasHeaderRow ; }   public void setHasHeaderRow  (  boolean hasHeaderRow )  {    this . hasHeaderRow = hasHeaderRow ; }   public boolean isHeaderParsingIsCaseSensitive  ( )  {  return headerParsingIsCaseSensitive ; }   public void setHeaderParsingIsCaseSensitive  (  boolean headerParsingIsCaseSensitive )  {    this . headerParsingIsCaseSensitive = headerParsingIsCaseSensitive ; }   public TrailingEmptyRowPolicy getTrailingEmptyRowPolicy  ( )  {  return trailingEmptyRowPolicy ; }   public void setTrailingEmptyRowPolicy  (  TrailingEmptyRowPolicy trailingEmptyRowPolicy )  {    this . trailingEmptyRowPolicy = trailingEmptyRowPolicy ; }   public boolean isAnyColumnCreatesCollection  ( )  {  return anyColumnCreatesCollection ; }   public void setAnyColumnCreatesCollection  (  boolean anyColumnCreatesCollection )  {    this . anyColumnCreatesCollection = anyColumnCreatesCollection ; } }