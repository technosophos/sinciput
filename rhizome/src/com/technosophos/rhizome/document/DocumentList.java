package com.technosophos.rhizome.document;

import static com.technosophos.rhizome.document.XMLElements.RHIZOME_DOC_XMLNS;

import java.io.CharArrayWriter;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

//import com.technosophos.rhizome.repository.RepositoryManager;
import static com.technosophos.rhizome.document.XMLElements.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Maintains a list of {@link RhizomeDocument}s.
 * @author mbutcher
 *
 */
public class DocumentList extends ArrayList<RhizomeDocument> {
	public static final long serialVersionUID = 11L;
	private String[] fields = null;
	
	public DocumentList(String[] fields) {
		this.fields = fields;
	}
	public DocumentList(String[] fields, int initialCapacity) {
		super(initialCapacity);
		this.fields = fields;
	}
	public DocumentList(String[] fields, Collection<? extends RhizomeDocument> collection) {
		super(collection);
	}
	
	public DocumentList() { super(); }
	
	public String[] getFields() {
		return this.fields;
	}
	
	public Document toDOM() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db; 
		db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element rhizome_ele = 
			doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOCLIST_ROOT);
		doc.appendChild(rhizome_ele);
		for(RhizomeDocument d: this) d.getDOM(doc, rhizome_ele);
		return doc;
	}
	
	/**
	 * Get an XML String.
	 * 
	 * This converts the present object to an XML string.
	 * @return
	 * @throws ParserConfigurationException
	 */
	public String toXML() throws ParserConfigurationException {
		CharArrayWriter output = new CharArrayWriter();
		this.toXML(output);
		return output.toString();
	}
	
	/**
	 * Transform the object to XML and write it to the given output stream.
	 * @param output
	 * @throws ParserConfigurationException
	 */
	public void toXML(OutputStream output) throws ParserConfigurationException {
		Document d = this.toDOM();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.transform(new DOMSource(d), new StreamResult(output));
		} catch (Exception e) {
			throw new ParserConfigurationException("Could not create Transformer: " + 
					e.getMessage());
		}
	}
	
	/**
	 * Transform the object to XML and write it to the given Writer.
	 * @param output
	 * @throws ParserConfigurationException
	 */
	public void toXML(Writer output) throws ParserConfigurationException {
		Document d = this.toDOM();
		
		if(d == null) System.err.println("WARNING: RhizomeDocument.toXML() doc is null.");
		if(output == null) System.err.println("WARNING: RhizomeDocument.toXML() output is null.");
		
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			//System.err.println("Transformer created.");
			t.transform(new DOMSource(d), new StreamResult(output));
		} catch (Exception e) {
			throw new ParserConfigurationException("Could not create Transformer: " + 
					e.getMessage());
		}
		
	}
	
	/**
	 * Get a String representation of the object.
	 * 
	 * This attempts to convert the object to XML, but if that fails, it uses
	 * the generic toString() method from Object.
	 * @see java.lang.Object
	 */
	public String toString() {
		try {
			return this.toXML();
		} catch(ParserConfigurationException pce) {
			return super.toString() + " (Parser Not Found)";
		}
	}
}
