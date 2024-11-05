package st.redline.compiler;

public class BlockAnalyser extends MethodAnalyser {
  public BlockAnalyser(String className, String packageName, int countOfArguments, boolean isClassMethod, Analyser containingAnalyser) {
    super(className, packageName, countOfArguments, isClassMethod, containingAnalyser);
    this.blockSequence(containingAnalyser.blockSequence() + 1);
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