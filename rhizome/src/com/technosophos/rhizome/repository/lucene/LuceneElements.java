package com.technosophos.rhizome.repository.lucene;

/**
 * Basic Lucene elements.
 * Since multiple classes need access to the same fields, these have been abstracted
 * out into this class. This should be imported with the <code>static</code> modifier.
 * @author mbutcher
 *
 */
public class LuceneElements {
	public static String LUCENE_BODY_FIELD = "body";
	public static String METADATA_FIELD_PREFIX = "metadatum:";
	public static String RELATION_FIELD_PREFIX = "relation:";
}
