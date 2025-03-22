package de.uni_koblenz.jgralab.schema;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Base class of the basic domains Boolean, Double, Integer, Long, and String.
 * 
 * @author ist@uni-koblenz.de
 */
public interface BasicDomain extends Domain {
  /**
	 * List of names of the basic domains.
	 */
  public static final Set<String> BASIC_DOMAINS = new TreeSet<String>(Arrays.asList(new String[] { BooleanDomain.BOOLEANDOMAIN_NAME, DoubleDomain.DOUBLEDOMAIN_NAME, IntegerDomain.INTDOMAIN_NAME, LongDomain.LONGDOMAIN_NAME, StringDomain.STRINGDOMAIN_NAME }));
}