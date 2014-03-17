package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public abstract class CompStrategy extends AbstractIterationStrategy
{
	protected PExpCG predicate;
	protected String var;
	protected PTypeCG compType;
	
	public abstract String getClassName();
	public abstract String getMemberName();
	public abstract PTypeCG getCollectionType() throws AnalysisException;
	
	public CompStrategy(TransformationAssistantCG transformationAssistant, PExpCG predicate, String var, PTypeCG compType)
	{
		super(transformationAssistant);
		this.predicate = predicate;
		this.var = var;
		this.compType = compType;
	}
	
	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		String className = getClassName();
		String memberName = getMemberName();
		PTypeCG collectionType = getCollectionType();
		
		return packDecl(transformationAssistant.consCompResultDecl(collectionType, var, className, memberName));
	}

	@Override
	public PExpCG getForLoopCond(String iteratorName)
			throws AnalysisException
	{
		AClassTypeCG iteratorType = transformationAssistant.consIteratorType();
		
		return transformationAssistant.consInstanceCall(iteratorType, iteratorName, new ABoolBasicTypeCG(), IJavaCodeGenConstants.HAS_NEXT_ELEMENT_ITERATOR, null);
	}

	@Override
	public ABlockStmCG getForLoopBody(PTypeCG setElementType, AIdentifierPatternCG id, String iteratorName) throws AnalysisException
	{
		return transformationAssistant.consForBodyNextElementDeclared(setElementType, id.getName(), iteratorName);
	}
	
	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return null;//Indicates that there are no extra statements to be added to the outer block
	}
}
