package com.technosophos.rhizome.document;

import org.w3c.dom.Document;

/**
 * Extensions for Rhizome Documents.
 * The Extension object provides a facility to extend the core structure
 * of a Rhizome document in order to allow additional structured components
 * to be added. (For example, a CMS system may use the extension mechanism
 * to implement a locking structure.)
 * <p>A document must be a DOM structure with its own namespace.</p>
 * @author mbutcher
 * @see RhizomeDocument
 */
public class Extension {
	private String name = null;
	private Document extDoc = null;
	private boolean indexible = false;
	
	public Extension (String name, Document extDoc) {
		this(name, extDoc, false);
	}
	
	public Extension(String name, Document extDoc, boolean indexible) {
		this.name = name;
		this.extDoc = extDoc;
		this.indexible = indexible;
	}
	
	private Extension () {
		this.name="";
		this.extDoc = null;
	}

	public String getName() {
		return this.name;
	}
	
	public Document getDOMDocument() {
		return this.extDoc;
	}
	
	public boolean isIndexible() {
		return this.indexible;
	}
	
	//FIXME: This is not implemented.
	public String toIndexibleString() {
		return this.toString();
	}
}
