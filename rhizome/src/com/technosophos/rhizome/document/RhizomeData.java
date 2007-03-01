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
	 * <p>FIXME: Currently, only "application/xml" is recognized as a valid XML
	 * content type. Others, like application/xhtml+xml and image/svg should
	 * probably be added.</p>
	 * @param mimetype
	 * @return
	 */
	public boolean isXMLMimeType() {
		if (MIME_XML.equalsIgnoreCase(this.mimeType)) return true;
		return false;
	}
	
	/** Returns this.getData() 
	 * @see getData()
	 */
	public String toString() {
		return this.getData();
	}
}
