/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution */
package st.redline.compiler;

public class BlockAnalyser extends MethodAnalyser {

	public BlockAnalyser(String className, String packageName, int countOfArguments, boolean isClassMethod, Analyser containingAnalyser) {
<<<<<<< /usr/src/app/output/redline-smalltalk/redline-smalltalk/8d475d7ea4bcf0001b0486803b76c29ef6ba8698/src/main/java/st/redline/compiler/BlockAnalyser.java/left.java
		super(className, packageName, countOfArguments, isClassMethod);
		this.containingAnalyser = containingAnalyser;
		this.blockSequence(containingAnalyser.blockSequence() + 1);
||||||| /usr/src/app/output/redline-smalltalk/redline-smalltalk/8d475d7ea4bcf0001b0486803b76c29ef6ba8698/src/main/java/st/redline/compiler/BlockAnalyser.java/base.java
		super(className, packageName, countOfArguments, isClassMethod);
		this.containingAnalyser = containingAnalyser;
=======
		super(className, packageName, countOfArguments, isClassMethod, containingAnalyser);
>>>>>>> /usr/src/app/output/redline-smalltalk/redline-smalltalk/8d475d7ea4bcf0001b0486803b76c29ef6ba8698/src/main/java/st/redline/compiler/BlockAnalyser.java/right.java
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
