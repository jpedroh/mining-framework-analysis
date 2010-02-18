package org.overture.ide.vdmpp.ui.editor.syntax;

import java.util.ArrayList;
import java.util.List;

import org.overture.ide.ui.editor.syntax.IVdmKeywords;

public class  VdmPpKeywords implements IVdmKeywords {

	public static final String[] reservedwords = {
	      "#act", "#active", "#fin", "#req", "#waiting",
	      "abs", "all", "always", "and", "async", "atomic",
	      "be", "bool", "by", "card", "cases", "char",
	      "class", "comp", "compose", "conc",
	      "dcl", "def", "dinter", "div", "do", "dom", "dunion",
	      "elems", "else", "elseif", "end", "error", "errs",
	      "exists", "exists1", "exit", "ext", "false", "floor",
	      "for", "forall", "from", "functions", "hd", "if",
	      "in", "inds", "inmap", "instance", "int", "inter",
	      "inv", "inverse", "iota", "is", "isofbaseclass",
	      "isofclass", "lambda", "len", "let", "map", "measure",
	      "merge", "mod", "mu", "munion", "mutex", "nat", "nat1",
	      "new", "nil", "not", "of", "operations", "or", "others",
	      "per", "periodic", "post", "power", "pre", "private",
	      "protected", "psubset", "public", "rat", "rd", "real",
	      "rem", "responsibility", "return", "reverse", "rng",
	      "samebaseclass", "sameclass", "self", "seq", "seq1",
	      "set", "skip", "specified", "st", "static","start", "startlist",
	      "subclass", "subset", "sync",
	      "then", "thread", "threadid",
	      "tixe", "tl", "to", "token", "traces", "trap", "true",
	      "types", "undefined", "union", "values", "variables",
	      "while", "with", "wr", "yet", "RESULT",
		  };
	  
	  public static final String[] multipleKeywords = {
		  "is not yet specified", "for all", 
		  "in set", "be st", "not in set",
		  "is subclass of", "instance variables",
		  "is subclass responsibility"
	  };
	  
	  public static String[] historyCounters = {
		  "#act", "#fin", "#active", "#req", "#waiting"
	  };

	  public static final String[] binaryoperators = {
	      "comp", "and", "or", "in set", "not in set",
		  "union", "inter", "munion", "subset",
		  "psubset", "=>","<=>","=","<>","+",
	      "-","*","/","div","mod", "rem", "<",">","<=",">=", "<>",
	      "\\","++","<:","<-:",":>",":->",".#", "**"
	  };
	  
	  public static final String[] basictypes = {"bool", "int", "nat", "nat1", "real", "rat", "char", "token"};

	  public static final String[] textvalues = {"true", "false", "nil"};
	 
	  
	  public static final String[] unaryoperators = { 
	      "abs", "card", "floor",  	  
	      "hd", "tl", "len", "elems", "inds", "conc", "dom", 
	      "rng", "merge", "not", "inverse", 
	      "dunion", "dinter", "power", "isofbaseclass", "isofclass", 
		  "samebaseclass", "sameclass"};
	  
	  


	  public String[] getBasictypes()
		{
			return basictypes;
		}

		public String[] getBinaryoperators()
		{
			return binaryoperators;
		}

		public String[] getMultipleKeywords()
		{
			return multipleKeywords;
		}

		public String[] getReservedwords()
		{
			return reservedwords;
		}

		public String[] getTextvalues()
		{
			return textvalues;
		}

		public String[] getUnaryoperators()
		{
			return unaryoperators;
		}

		public String[] getAllKeywords() {
			int offset = 0;
			String[] all = new String[basictypes.length 
			                          + binaryoperators.length 
			                          + multipleKeywords.length 
			                          + reservedwords.length 
			                          + textvalues.length 
			                          + unaryoperators.length];
			
			System.arraycopy(basictypes, 0, all, offset, basictypes.length);
			offset = offset + basictypes.length;
			
			System.arraycopy(binaryoperators, 0, all, offset, binaryoperators.length);
			offset = offset + binaryoperators.length;
			
			System.arraycopy(multipleKeywords, 0, all, offset, multipleKeywords.length);
			offset = offset + multipleKeywords.length;
			
			System.arraycopy(reservedwords, 0, all, offset, reservedwords.length);
			offset = offset + reservedwords.length;
			
			System.arraycopy(textvalues, 0, all, offset, textvalues.length);
			offset = offset + textvalues.length;
			
			System.arraycopy(unaryoperators, 0, all, offset, unaryoperators.length);
			offset = offset + binaryoperators.length;
			
			return all;
		}

}
