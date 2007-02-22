package com.technosophos.rhizome.document;

/**
 * Basic XML information.
 * Since lots of classes need access to the same strings, this class
 * defines constants for accessing these. This should be imported with 
 * the static modifier.
 * @author mbutcher
 *
 */
public class XMLElements {
	public static String RHIZOME_DOC_XMLNS = "http://technosophos.com/xml/rhizome/document";
	public static String RHIZOME_DOC_QUALIFIER = "rhizome";
	public static String RHIZOME_DOC_ROOT = "rhizome";
	public static String RHIZOME_DOC_METADATA = "metadata";
	public static String RHIZOME_DOC_METADATUM = "metadatum";
	public static String RHIZOME_DOC_VALUE = "value";
	public static String RHIZOME_DOC_RELATIONS = "relations";
	public static String RHIZOME_DOC_RELATION = "relation";
	public static String RHIZOME_DOC_DATA = "data";
	public static String RHIZOME_DOC_EXTENSIONS = "extensions";
	public static String RHIZOME_DOC_EXTENSION = "extension";
	public static String RHIZOME_DOC_ATTR_DOCID = "docid";
	public static String RHIZOME_DOC_ATTR_NAME = "name";
	public static String RHIZOME_DOC_ATTR_DATATYPE = "datatype";
	public static String RHIZOME_DOC_ATTR_RELATIONTYPE = "relationtype";
	public static String RHIZOME_DOC_ATTR_MIMETYPE = "mimetype";	
}
