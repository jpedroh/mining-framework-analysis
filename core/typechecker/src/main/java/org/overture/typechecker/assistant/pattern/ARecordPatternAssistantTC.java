package org.overture.typechecker.assistant.pattern;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class ARecordPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ARecordPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

<<<<<<< HEAD
//	public static void typeResolve(ARecordPattern pattern,
//			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question) throws AnalysisException
//	{
//		if (pattern.getResolved())
//			return;
//		else
//		{
//			pattern.setResolved(true);
//		}
//
//		try
//		{
//			PPatternListAssistantTC.typeResolve(pattern.getPlist(), rootVisitor, question);
//			pattern.setType(af.createPTypeAssistant().typeResolve(pattern.getType(), null, rootVisitor, question));
//		} catch (TypeCheckException e)
//		{
//			unResolve(pattern);
//			throw e;
//		}
//
//	}
=======
	public static void typeResolve(ARecordPattern pattern,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		if (pattern.getResolved())
		{
			return;
		} else
		{
			pattern.setResolved(true);
		}

		try
		{
			PPatternListAssistantTC.typeResolve(pattern.getPlist(), rootVisitor, question);
			pattern.setType(af.createPTypeAssistant().typeResolve(pattern.getType(), null, rootVisitor, question));
		} catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}

	}
>>>>>>> origin/pvj/main

//	public static void unResolve(ARecordPattern pattern)
//	{
//		PTypeAssistantTC.unResolve(pattern.getType());
//		pattern.setResolved(false);
//	}

	public static List<PDefinition> getAllDefinitions(ARecordPattern rp,
			PType exptype, NameScope scope)
	{

		List<PDefinition> defs = new Vector<PDefinition>();

		PType type = rp.getType();

		if (!PTypeAssistantTC.isTag(type))
		{
			TypeCheckerErrors.report(3200, "Mk_ expression is not a record type", rp.getLocation(), rp);
			TypeCheckerErrors.detail("Type", type);
			return defs;
		}

		ARecordInvariantType pattype = PTypeAssistantTC.getRecord(type);
		PType using = PTypeAssistantTC.isType(exptype, pattype.getName().getFullName());

		if (using == null || !(using instanceof ARecordInvariantType))
		{
			TypeCheckerErrors.report(3201, "Matching expression is not a compatible record type", rp.getLocation(), rp);
			TypeCheckerErrors.detail2("Pattern type", type, "Expression type", exptype);
			return defs;
		}

		// RecordType usingrec = (RecordType)using;

		if (pattype.getFields().size() != rp.getPlist().size())
		{
			TypeCheckerErrors.report(3202, "Record pattern argument/field count mismatch", rp.getLocation(), rp);
		} else
		{
			Iterator<AFieldField> patfi = pattype.getFields().iterator();

			for (PPattern p : rp.getPlist())
			{
				AFieldField pf = patfi.next();
				// defs.addAll(p.getDefinitions(usingrec.findField(pf.tag).type, scope));
				defs.addAll(PPatternAssistantTC.getDefinitions(p, pf.getType(), scope));
			}
		}

		return defs;
	}

}
