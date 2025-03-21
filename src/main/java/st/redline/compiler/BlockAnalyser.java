package st.redline.compiler;

public class BlockAnalyser extends MethodAnalyser {
  public BlockAnalyser(String className, String packageName, int countOfArguments, boolean isClassMethod, Analyser containingAnalyser) {
    super(className, packageName, countOfArguments, isClassMethod, containingAnalyser);

<<<<<<< /usr/src/app/output/redline-smalltalk/redline-smalltalk/8d475d7ea4bcf0001b0486803b76c29ef6ba8698/src/main/java/st/redline/compiler/BlockAnalyser.java/left.java
    this.containingAnalyser = containingAnalyser;
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/redline-smalltalk/redline-smalltalk/8d475d7ea4bcf0001b0486803b76c29ef6ba8698/src/main/java/st/redline/compiler/BlockAnalyser.java/left.java
    this.blockSequence(containingAnalyser.blockSequence() + 1);
=======
>>>>>>> Unknown file: This is a bug in JDime.
  }

  protected void initialize() {
    classBytecodeWriter = new BlockBytecodeWriter(className, packageName, countOfArguments);
  }

  public boolean continueBlockVisit() {
    return true;
  }

  public void visit(Block block) {
    classBytecodeWriter.openClass();
  }

  public void visitEnd(Block block) {
    if (!block.hasStatements()) {
      classBytecodeWriter.stackPushNil(block.line());
    }
    classBytecodeWriter.closeClass();
  }

  public void visit(BlockVariableName blockVariableName, String value, int line) {
    registerMethodArgument(blockVariableName);
  }
}