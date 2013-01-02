/**
 * This class is generated by jOOQ
 */
package org.jooq.test.firebird.generatedclasses.tables;

/**
 * This class is generated by jOOQ.
 */
@java.lang.SuppressWarnings("all")
public class TBook extends org.jooq.impl.UpdatableTableImpl<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord> {

	private static final long serialVersionUID = 1619620142;

	/**
	 * The singleton instance of <code>T_BOOK</code>
	 */
	public static final org.jooq.test.firebird.generatedclasses.tables.TBook T_BOOK = new org.jooq.test.firebird.generatedclasses.tables.TBook();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord> getRecordType() {
		return org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord.class;
	}

	/**
	 * The column <code>T_BOOK.ID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, java.lang.Integer> ID = createField("ID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>T_BOOK.AUTHOR_ID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, java.lang.Integer> AUTHOR_ID = createField("AUTHOR_ID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>T_BOOK.CO_AUTHOR_ID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, java.lang.Integer> CO_AUTHOR_ID = createField("CO_AUTHOR_ID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>T_BOOK.DETAILS_ID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, java.lang.Integer> DETAILS_ID = createField("DETAILS_ID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>T_BOOK.TITLE</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, java.lang.String> TITLE = createField("TITLE", org.jooq.impl.SQLDataType.VARCHAR.length(400), this);

	/**
	 * The column <code>T_BOOK.PUBLISHED_IN</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, java.lang.Integer> PUBLISHED_IN = createField("PUBLISHED_IN", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>T_BOOK.LANGUAGE_ID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, java.lang.Integer> LANGUAGE_ID = createField("LANGUAGE_ID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>T_BOOK.CONTENT_TEXT</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, java.lang.String> CONTENT_TEXT = createField("CONTENT_TEXT", org.jooq.impl.SQLDataType.CLOB.length(8), this);

	/**
	 * The column <code>T_BOOK.CONTENT_PDF</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, byte[]> CONTENT_PDF = createField("CONTENT_PDF", org.jooq.impl.SQLDataType.BLOB.length(8), this);

	/**
	 * The column <code>T_BOOK.REC_VERSION</code>. 
	 */
	public final org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, java.lang.Integer> REC_VERSION = createField("REC_VERSION", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * Create a <code>T_BOOK</code> table reference
	 */
	public TBook() {
		super("T_BOOK");
	}

	/**
	 * Create an aliased <code>T_BOOK</code> table reference
	 */
	public TBook(java.lang.String alias) {
		super(alias, (org.jooq.Schema) null, org.jooq.test.firebird.generatedclasses.tables.TBook.T_BOOK);
	}

	/**
	 * Create an aliased <code>T_BOOK</code> table reference
	 */
	public TBook(java.lang.String alias, java.lang.String... fieldAliases) {
		super(alias, fieldAliases, (org.jooq.Schema) null, org.jooq.test.firebird.generatedclasses.tables.TBook.T_BOOK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord> getMainKey() {
		return org.jooq.test.firebird.generatedclasses.Keys.PK_T_BOOK;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord>>asList(org.jooq.test.firebird.generatedclasses.Keys.PK_T_BOOK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, ?>>asList(org.jooq.test.firebird.generatedclasses.Keys.FK_T_BOOK_AUTHOR_ID, org.jooq.test.firebird.generatedclasses.Keys.FK_T_BOOK_CO_AUTHOR_ID, org.jooq.test.firebird.generatedclasses.Keys.FK_T_BOOK_LANGUAGE_ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.TableField<org.jooq.test.firebird.generatedclasses.tables.records.TBookRecord, java.lang.Integer> getRecordVersion() {
		return org.jooq.test.firebird.generatedclasses.tables.TBook.T_BOOK.REC_VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.test.firebird.generatedclasses.tables.TBook as(java.lang.String alias) {
		return new org.jooq.test.firebird.generatedclasses.tables.TBook(alias);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.test.firebird.generatedclasses.tables.TBook as(java.lang.String alias, java.lang.String... fieldAliases) {
		return new org.jooq.test.firebird.generatedclasses.tables.TBook(alias, fieldAliases);
	}
}
