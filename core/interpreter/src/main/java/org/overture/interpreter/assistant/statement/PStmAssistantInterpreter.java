package org.overture.interpreter.assistant.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.typechecker.assistant.statement.PStmAssistantTC;

public class PStmAssistantInterpreter extends PStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static PExp findExpression(PStm stm, int lineno)
	{
		try
		{
			return stm.apply(af.getStatementExpressionFinder(),lineno);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null; // Most have none
		}
//		if (stm instanceof AAlwaysStm)
//		{
//			return AAlwaysStmAssistantInterpreter.findExpression((AAlwaysStm) stm, lineno);
//		} else if (stm instanceof AAssignmentStm)
//		{
//			return AAssignmentStmAssistantInterpreter.findExpression((AAssignmentStm) stm, lineno);
//		} else if (stm instanceof AAtomicStm)
//		{
//			return AAtomicStmAssistantInterpreter.findExpression((AAtomicStm) stm, lineno);
//		} else if (stm instanceof ACallStm)
//		{
//			return ACallStmAssistantInterpreter.findExpression((ACallStm) stm, lineno);
//		} else if (stm instanceof ACallObjectStm)
//		{
//			return ACallObjectStatementAssistantInterpreter.findExpression((ACallObjectStm) stm, lineno);
//		} else if (stm instanceof ACasesStm)
//		{
//			return ACasesStmAssistantInterpreter.findExpression((ACasesStm) stm, lineno);
//		} else if (stm instanceof ACyclesStm)
//		{
//			return ACyclesStmAssistantInterpreter.findExpression((ACyclesStm) stm, lineno);
//		} else if (stm instanceof ADurationStm)
//		{
//			return ADurationStmAssistantInterpreter.findExpression((ADurationStm) stm, lineno);
//		} else if (stm instanceof AElseIfStm)
//		{
//			return AElseIfStmAssistantInterpreter.findExpression((AElseIfStm) stm, lineno);
//		} else if (stm instanceof AExitStm)
//		{
//			return AExitStmAssistantInterpreter.findExpression((AExitStm) stm, lineno);
//		} else if (stm instanceof AForAllStm)
//		{
//			return AForAllStmAssistantInterpreter.findExpression((AForAllStm) stm, lineno);
//		} else if (stm instanceof AForIndexStm)
//		{
//			return AForIndexStmAssistantInterpreter.findExpression((AForIndexStm) stm, lineno);
//		} else if (stm instanceof AForPatternBindStm)
//		{
//			return AForPatternBindStmAssitantInterpreter.findExpression((AForPatternBindStm) stm, lineno);
//		} else if (stm instanceof AIfStm)
//		{
//			return AIfStmAssistantInterpreter.findExpression((AIfStm) stm, lineno);
//		} else if (stm instanceof ALetBeStStm)
//		{
//			return ALetBeStStmAssistantInterpreter.findExpression((ALetBeStStm) stm, lineno);
//		} else if (stm instanceof ALetStm)
//		{
//			return SLetDefStmAssistantInterpreter.findExpression((ALetStm) stm, lineno);
//		} else if (stm instanceof AReturnStm)
//		{
//			return AReturnStmAssistantInterpreter.findExpression((AReturnStm) stm, lineno);
//		} else if (stm instanceof SSimpleBlockStm)
//		{
//			return SSimpleBlockStmAssistantInterpreter.findExpression((SSimpleBlockStm) stm, lineno);
//		} else if (stm instanceof AStartStm)
//		{
//			return AStartStmAssistantInterpreter.findExpression((AStartStm) stm, lineno);
//		} else if (stm instanceof AStopStm)
//		{
//			return AStartStmAssistantInterpreter.findExpression((AStopStm) stm, lineno);
//		} else if (stm instanceof ATixeStm)
//		{
//			return ATixeStmAssistantInterpreter.findExpression((ATixeStm) stm, lineno);
//		} else if (stm instanceof ATrapStm)
//		{
//			return ATrapStmAssistantInterpreter.findExpression((ATrapStm) stm, lineno);
//		} else if (stm instanceof AWhileStm)
//		{
//			return AWhileStmAssistantInterpreter.findExpression((AWhileStm) stm, lineno);
//		} else
//		{
//			return null;
//		}
	}

	/**
	 * Find a statement starting on the given line. Single statements just compare their location to lineno, but block
	 * statements and statements with sub-statements iterate over their branches.
	 * 
	 * @param lineno
	 *            The line number to locate.
	 * @return A statement starting on the line, or null.
	 */
	public static PStm findStatement(PStm stm, int lineno)
	{
		try
		{
			return stm.apply(af.getStatementFinder(),lineno);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null; // Most have none
		}
//		if (stm instanceof AAlwaysStm)
//		{
//			return AAlwaysStmAssistantInterpreter.findStatement((AAlwaysStm) stm, lineno);
//		} else if (stm instanceof AAtomicStm)
//		{
//			return AAtomicStmAssistantInterpreter.findStatement((AAtomicStm) stm, lineno);
//		} else if (stm instanceof ACasesStm)
//		{
//			return ACasesStmAssistantInterpreter.findStatement((ACasesStm) stm, lineno);
//		} else if (stm instanceof ACyclesStm)
//		{
//			return ACyclesStmAssistantInterpreter.findStatement((ACyclesStm) stm, lineno);
//		} else if (stm instanceof ADurationStm)
//		{
//			return ADurationStmAssistantInterpreter.findStatement((ADurationStm) stm, lineno);
//		} else if (stm instanceof AElseIfStm)
//		{
//			return AElseIfStmAssistantInterpreter.findStatement((AElseIfStm) stm, lineno);
//		} else if (stm instanceof AForAllStm)
//		{
//			return AForAllStmAssistantInterpreter.findStatement((AForAllStm) stm, lineno);
//		} else if (stm instanceof AForIndexStm)
//		{
//			return AForIndexStmAssistantInterpreter.findStatement((AForIndexStm) stm, lineno);
//		} else if (stm instanceof AForPatternBindStm)
//		{
//			return AForPatternBindStmAssitantInterpreter.findStatement((AForPatternBindStm) stm, lineno);
//		} else if (stm instanceof AIfStm)
//		{
//			return AIfStmAssistantInterpreter.findStatement((AIfStm) stm, lineno);
//		} else if (stm instanceof ALetBeStStm)
//		{
//			return ALetBeStStmAssistantInterpreter.findStatement((ALetBeStStm) stm, lineno);
//		} else if (stm instanceof ALetStm)
//		{
//			return SLetDefStmAssistantInterpreter.findStatement((ALetStm) stm, lineno);
//		} else if (stm instanceof SSimpleBlockStm)
//		{
//			return SSimpleBlockStmAssistantInterpreter.findStatement((SSimpleBlockStm) stm, lineno);
//		} else if (stm instanceof ATixeStm)
//		{
//			return ATixeStmAssistantInterpreter.findStatement((ATixeStm) stm, lineno);
//		} else if (stm instanceof ATrapStm)
//		{
//			return ATrapStmAssistantInterpreter.findStatement((ATrapStm) stm, lineno);
//		} else if (stm instanceof AWhileStm)
//		{
//			return AWhileStmAssistantInterpreter.findStatement((AWhileStm) stm, lineno);
//		} else
//		{
//			return findStatementBaseCase(stm, lineno);
//		}
	}

	
}
