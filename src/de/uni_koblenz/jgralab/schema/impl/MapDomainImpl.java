/**
 *
 */
package de.uni_koblenz.jgralab.schema.impl;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.MapDomain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.QualifiedName;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * @author Tassilo Horn <horn@uni-koblenz.de>
 *
 */
public class MapDomainImpl extends CompositeDomainImpl implements MapDomain {
	/**
	 * The domain of this MapDomain's keys.
	 */
	protected Domain keyDomain;

	/**
	 * The domain of this MapDomain's values.
	 */
	protected Domain valueDomain;

	public MapDomainImpl(Schema schema, QualifiedName qn, Domain aKeyDomain,
			Domain aValueDomain) {
		super(schema, qn);
		keyDomain = aKeyDomain;
		valueDomain = aValueDomain;
	}

	public MapDomainImpl(Schema schema, Domain aKeyDomain, Domain aValueDomain) {
		this(schema,
				new QualifiedName("Map<"
						+ aKeyDomain.getTGTypeName(schema.getDefaultPackage())
						+ ", "
						+ aValueDomain
								.getTGTypeName(schema.getDefaultPackage())
						+ ">"), aKeyDomain, aValueDomain);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni_koblenz.jgralab.schema.impl.CompositeDomainImpl#equals(java.lang
	 * .Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof MapDomain)) {
			return false;
		}

		MapDomain other = (MapDomain) o;
		return keyDomain.equals(other.getKeyDomain())
				&& valueDomain.equals(other.getValueDomain());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.uni_koblenz.jgralab.schema.MapDomain#getKeyDomain()
	 */
	@Override
	public Domain getKeyDomain() {
		return keyDomain;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.uni_koblenz.jgralab.schema.MapDomain#getValueDomain()
	 */
	@Override
	public Domain getValueDomain() {
		return valueDomain;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni_koblenz.jgralab.schema.CompositeDomain#getAllComponentDomains()
	 */
	@Override
	public Set<Domain> getAllComponentDomains() {
		HashSet<Domain> allComponentDomains = new HashSet<Domain>(2);
		allComponentDomains.add(keyDomain);
		allComponentDomains.add(valueDomain);
		return allComponentDomains;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni_koblenz.jgralab.schema.Domain#getJavaAttributeImplementationTypeName
	 * (java.lang.String)
	 */
	@Override
	public String getJavaAttributeImplementationTypeName(
			String schemaRootPackagePrefix) {
		return "java.util.Map<"
				+ keyDomain.getJavaClassName(schemaRootPackagePrefix) + ", "
				+ valueDomain.getJavaClassName(schemaRootPackagePrefix) + ">";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni_koblenz.jgralab.schema.Domain#getJavaClassName(java.lang.String)
	 */
	@Override
	public String getJavaClassName(String schemaRootPackagePrefix) {
		return getJavaAttributeImplementationTypeName(schemaRootPackagePrefix);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.uni_koblenz.jgralab.schema.Domain#getReadMethod(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public CodeBlock getReadMethod(String schemaPrefix, String variableName,
			String graphIoVariableName) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni_koblenz.jgralab.schema.Domain#getTGTypeName(de.uni_koblenz.jgralab
	 * .schema.Package)
	 */
	@Override
	public String getTGTypeName(Package pkg) {
		return "Map<" + keyDomain.getTGTypeName(pkg) + ", "
				+ valueDomain.getTGTypeName(pkg) + ">";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni_koblenz.jgralab.schema.Domain#getWriteMethod(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public CodeBlock getWriteMethod(String schemaRootPackagePrefix,
			String variableName, String graphIoVariableName) {
		// TODO Auto-generated method stub
		return null;
	}

}
