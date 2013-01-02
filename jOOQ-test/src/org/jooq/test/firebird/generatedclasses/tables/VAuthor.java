/**
 * This class is generated by jOOQ
 */
package org.jooq.test.firebird.generatedclasses.tables;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings("all")
public class VAuthor extends org.jooq.impl.TableImpl<org.jooq.test.firebird.generatedclasses.tables.records.VAuthorRecord> {

	private static final long serialVersionUID = 1903880075;

	/**
	 * The singleton instance of <code>V_AUTHOR</code>
	 */
	public static final org.jooq.test.firebird.generatedclasses.tables.VAuthor V_AUTHOR = new org.jooq.test.firebird.generatedclasses.tables.VAuthor();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.jooq.test.firebird.generatedclasses.tables.records.VAuthorRecord> getRecordType() {
		return org.jooq.test.firebird.generatedclasses.tables.records.VAuthorRecord.class;
	}

	/**
	 * The column <code>V_AUTHOR.ID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.VAuthorRecord, java.lang.Integer> ID = createField("ID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>V_AUTHOR.FIRST_NAME</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.VAuthorRecord, java.lang.String> FIRST_NAME = createField("FIRST_NAME", org.jooq.impl.SQLDataType.VARCHAR.length(50), this);

	/**
	 * The column <code>V_AUTHOR.LAST_NAME</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.VAuthorRecord, java.lang.String> LAST_NAME = createField("LAST_NAME", org.jooq.impl.SQLDataType.VARCHAR.length(50), this);

	/**
	 * The column <code>V_AUTHOR.DATE_OF_BIRTH</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.VAuthorRecord, java.sql.Date> DATE_OF_BIRTH = createField("DATE_OF_BIRTH", org.jooq.impl.SQLDataType.DATE, this);

	/**
	 * The column <code>V_AUTHOR.YEAR_OF_BIRTH</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.VAuthorRecord, java.lang.Integer> YEAR_OF_BIRTH = createField("YEAR_OF_BIRTH", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>V_AUTHOR.ADDRESS</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.VAuthorRecord, java.lang.String> ADDRESS = createField("ADDRESS", org.jooq.impl.SQLDataType.VARCHAR.length(50), this);

	/**
	 * Create a <code>V_AUTHOR</code> table reference
	 */
	public VAuthor() {
		super("V_AUTHOR");
	}

	/**
	 * Create an aliased <code>V_AUTHOR</code> table reference
	 */
	public VAuthor(java.lang.String alias) {
		super(alias, (org.jooq.Schema) null, org.jooq.test.firebird.generatedclasses.tables.VAuthor.V_AUTHOR);
	}

	/**
	 * Create an aliased <code>V_AUTHOR</code> table reference
	 */
	public VAuthor(java.lang.String alias, java.lang.String... fieldAliases) {
		super(alias, fieldAliases, (org.jooq.Schema) null, org.jooq.test.firebird.generatedclasses.tables.VAuthor.V_AUTHOR);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.test.firebird.generatedclasses.tables.VAuthor as(java.lang.String alias) {
		return new org.jooq.test.firebird.generatedclasses.tables.VAuthor(alias);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.test.firebird.generatedclasses.tables.VAuthor as(java.lang.String alias, java.lang.String... fieldAliases) {
		return new org.jooq.test.firebird.generatedclasses.tables.VAuthor(alias, fieldAliases);
	}
}
