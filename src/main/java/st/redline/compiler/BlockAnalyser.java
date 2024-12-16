/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution */
package st.redline.compiler;



public class BlockAnalyser extends MethodAnalyser {
	public BlockAnalyser(String className, String packageName, int countOfArguments, boolean isClassMethod, Analyser containingAnalyser) {
<<<<<<< LEFT
		super(className, packageName, countOfArguments, isClassMethod);
		this.containingAnalyser = containingAnalyser;
		this.blockSequence(containingAnalyser.blockSequence() + 1);
=======
		super(className, packageName, countOfArguments, isClassMethod, containingAnalyser);
>>>>>>> RIGHT
	}

	protected void initialize() {
		classBytecodeWriter = new BlockBytecodeWriter(className, packageName, countOfArguments);
	}

	public boolean continueBlockVisit() {
		return true;
	}

	public void visit(Block block) {
	//		System.out.println("Block() Analysis begin " + block);
		classBytecodeWriter.openClass();
	}

	public void visitEnd(Block block) {
		if (!block.hasStatements())
			classBytecodeWriter.stackPushNil(block.line());
		classBytecodeWriter.closeClass();
	}

	public void visit(BlockVariableName blockVariableName, String value, int line) {
	//		System.out.println("visit(BlockVariable) " + value);
		registerMethodArgument(blockVariableName);
	}
}