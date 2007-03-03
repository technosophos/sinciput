package com.technosophos.rhizome.document;

import java.lang.StringBuffer;

public class RhizomeData {
	public static String RHIZOME_DATA_MIME_TYPE = "text/html";
	
	/** text/plain */
	public static String MIME_PLAINTEXT = "text/plain";

	/** text/html */
	public static String MIME_HTML = "text/html";
	
	/** application/xhtml+xml */
	public static String MIME_XHTML = "applciation/xhtml+xml";
	
	/** application/xml */
	public static String MIME_XML = "application/xml";
	
	/** application/octet-stream (Base64 encoded?) */
	public static String MIME_BINARY = "application/octet-stream";

	private String mimeType = RHIZOME_DATA_MIME_TYPE;
	private StringBuffer data = null;
	
	private boolean canBeIndexed = true;
	private boolean isParseable = false;
	/**
	 * Construct a new RhizomeData object.
	 * The MIME type *should* be formed according to the convention
	 * (Which RFC?). But, no enforcing is done on the MIME type string.
	 * By default, the MIME type is text/html.
	 * @param mimeType
	 * @param data
	 */
	public RhizomeData (String mimeType, String data) {
		this(mimeType, new StringBuffer(data));
	}
	
	/**
	 * Construct a RhizomeData with a String buffer and a mime type.
	 * @see #RhizomeData(String, String)
	 * @param mimeType
	 * @param data
	 */
	public RhizomeData (String mimeType, StringBuffer data) {
		this.data = data;
		this.mimeType = mimeType;
	}
	
	/**
	 * Construct a RhizomeData with the default MIME type.
	 * @param data
	 */
	public RhizomeData (String data) {
		this(RHIZOME_DATA_MIME_TYPE, new StringBuffer(data));
	}

	/**
	 * Construct a RhizomeData with the default MIME type.
	 * @param data
	 */
	public RhizomeData (StringBuffer data) {
		this(RHIZOME_DATA_MIME_TYPE, data);
	}
	
	/**
	 * Construct an empty RhizomeData object.
	 * This will set the MIME type to the default and create an empty
	 * body.
	 *
	 */
	public RhizomeData() {
		this.mimeType = RHIZOME_DATA_MIME_TYPE;
		this.data = new StringBuffer();
	}

	/**
	 * Set the data. This will overwrite the existing data, but will
	 * not change the MIME type.
	 * @param data
	 */
	public void setData(String data) {
		this.data = new StringBuffer(data);
	}

	/**
	 * Set the data. This will overwrite the existing data, but will
	 * not change the MIME type.
	 * @param data
	 */
	public void setData(StringBuffer data) {
		this.data = data;
	}
	
	/**
	 * Add more to the existing data.
	 * @param moreData
	 */
	public void appendData(String moreData) {
		this.data.append(moreData);
	}
	
	/**
	 * This clears the existing data.
	 */
	public void deleteData() {
		this.data = new StringBuffer();
	}
	
	/**
	 * Returns the number of chars currently stored in the data
	 * section of this object.
	 * @return Length in characters.
	 */
	public int getDataLength() {
		return this.data.length();
	}
	
	/**
	 * Get the data.
	 * @return The data.
	 */
	public String getData() {
		return this.data.toString();
	}
	
	/**
	 * Set the MIME type. Reminder: no checking is done of the string
	 * passed in here, though it is recommended that this be a valid MIME
	 * type.
	 * @param mimeType
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	/** Get the current MIME type. 
	 * @return The MIME type string.
	 */
	public String getMimeType() {
		return this.mimeType;
	}
	
	/**
	 * Indicates whether or not the MIME type for this data is recognized as
	 * an XML type.
	 * <p>This is used to determine whether or not an XML parser should parse this text.</p>
	 * @param mimetype
	 * @return
	 *  @deprecated Use isXMLParseable() to determine if the content is parseable, and isTaggedText() to find out if it is XML-like text.
	 */
	public boolean isXMLMimeType() {
		if (MIME_XML.equalsIgnoreCase(this.mimeType)) return true;
		return false;
	}
	
	/**
	 * Sets the XMLParseable flag.
	 * <p>If it is okay for Rhizome to parse the data into an internal 
	 * representation of the XML, then this should be true.
	 * </p>
	 * <p>When converting from XML to RhizomeDocument, this should be set
	 * to TRUE if the Data is not in a CDATA section, and false if it is.</p>
	 * @param isParseable
	 */
	public void setXMLParseable(boolean isParseable) {
		this.isParseable = isParseable;
	}
	
	/**
	 * Indicates if data ought to be parsed with an XML parser.
	 * <p>Data can be in a parseable format, but that factor alone does not mean
	 * that we want it parsed. This flag indicates whether or not we want it parsed.
	 * </p>
	 * @return true if it is okay to parse this document for the application.
	 * @see isTaggedText()
	 */
	public boolean isXMLParseable() {
		return this.isParseable;
	}
	
	/**
	 * This returns true if the MIME type for the text suggests the content
	 * is XML, HTML, or XHTML. 
	 * 
	 * <p>Any type that returns true should be parseable by an HTML/XML parser.
	 * Typically, this is used to 
	 * help indexing applications to guess how to treat this document.</p>
	 * @return true if this is an XML-like document (based on MIME type)
	 * @see isXMLParseable()
	 */
	public boolean isTaggedText() {
		if (MIME_XML.equalsIgnoreCase(this.mimeType)
				|| MIME_HTML.equalsIgnoreCase(this.mimeType)
				|| MIME_XHTML.equalsIgnoreCase(this.mimeType)) 
			return true;
		return false;
	}
	
	/**
	 * Check to see if text can be included in search index.
	 * @return true if this data should be included in search indexes
	 */
	public boolean isIndexible(){
		return this.canBeIndexed;
	}
	
	/**
	 * Turn on indexing for the body of this data.
	 * <p>This is used to tell the underlying indexing engine whether or not 
	 * this body can be parsed and indexed for searching. In general, 
	 * you probably want this on (the default), but if the body contains
	 * binary data or XML not intended to be displayed, then you might want to 
	 * turn this off.</p>
	 * <p>By default, all RhizomeData bodies are indexed.</p>
	 * @param flag
	 */
	public void setIndexible(boolean flag) {
		this.canBeIndexed = flag;
	}
	
	/** Returns this.getData() 
	 * @see getData()
	 */
	public String toString() {
		return this.getData();
	}
}
