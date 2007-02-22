package com.technosophos.rhizome.document;

import java.util.ArrayList;
//import org.betterxml.xelement.XDocument;
import org.w3c.dom.*;
import javax.xml.parsers.*;

import com.technosophos.rhizome.document.DocumentID;
import com.technosophos.rhizome.document.Extension;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.Relation;
import com.technosophos.rhizome.document.RhizomeData;

import static com.technosophos.rhizome.document.XMLElements.*;

/**
 * A document is a foundational piece of the Rhizome architecture.
 * Each document in Rhizome is identified by a unique ID (called a 
 * Document ID or docID). The RhizomeDocument stores all parts of a document
 * that Rhizome cares about.
 * <p>
 * A document is composed of four parts: metadata, relations, the body of
 * the document, and extensions.</p>
 * <p><b>Metadata:</b> metadata is data about the contents of the document.
 * Typically, thigs like title, modification and creation dates, author
 * information, and the like are considered metadata. A metadata section 
 * contains zero or more metadatum items, where each metadatum has one name, 
 * and any number of values.</p>
 * <p><b>Relations:</b> Relations are ties between this document and other
 * documents contained in Rhizome's content repo. Typically, a relation 
 * contains two pieces of data. The first is the type of relation (in hierarchical 
 * structures, we might talk about parents, children, etc. No hierarchy is
 * imposed by Rhizome, though.). The second is the ID of the document to
 * which this document is related. While any given relation can point to
 * only one document, there is no constraint to prohibit multiple 
 * relationships, all of the same relationship type.</p>
 * <p><b>The Body:</b> The body of the document contains whatever data is
 * considered the document itself. This might be HTML, XML, or text. (To
 * store binary information, use some encoding type, such as Base64, that
 * outputs text.) XML and XHTML content may be parsed, but HTML and Text
 * data are not parsed by Rhizome.</p>
 * <p><b>Extensions:</b> The extension mechanism provides a way to add on 
 * additional components to the Rhizome document structure. Generally, the 
 * extension is treated as structured content (XML), and is parsed.</p>
 * @see com.technosophos.rhizome.document.Metadatum
 * @see com.technosophos.rhizome.document.Relation
 * @see com.technosophos.rhizome.document.RhizomeData
 * @see com.technosophos.rhizome.document.Extension 
 * @author mbutcher
 *
 */
public class RhizomeDocument {
	
	
	
	private ArrayList<Metadatum> metadata = null;
	private RhizomeData body = null;
	private ArrayList<Extension> extensions = null;
	private ArrayList<Relation> relations = null;
	private String docID = null;

	/**
	 * Construct an empty document.
	 * This will generate a document ID automatically, using the 
	 * DocumentID class.
	 * @see com.technosophos.rhizome.DocumentID
	 */	
	private RhizomeDocument() {
		this(DocumentID.generateDocumentID());
	}
	
	/** 
	 * Create an empty document with an ID.
	 * @param docID
	 */
	public RhizomeDocument(String docID) {
		this(docID, 
				new ArrayList<Metadatum>(), 
				new ArrayList<Relation>(), 
				new RhizomeData(), 
				new ArrayList<Extension>());
	}
	
	/**
	 * This is a convenience constructor for quickly creating a document
	 * with a document ID and with a body. The body will be assigned the 
	 * MIME type that is set as default for RhizomeDocument (usually 
	 * text/html), and it will be stored in a new RhizomeData object.
	 * @param docID
	 * @param data
	 * @see RhizomeData
	 */
	public RhizomeDocument(String docID, String data) {
		this(docID, 
				new ArrayList<Metadatum>(), 
				new ArrayList<Relation>(), 
				new RhizomeData(data), 
				new ArrayList<Extension>());
	}

	/**
	 * This is the full constructor.
	 * @param docID The String unique document ID.
	 * @param metadata
	 * @param relations
	 * @param body
	 * @param extensions
	 */
	public RhizomeDocument(String docID, 
			ArrayList<Metadatum> metadata, 
			ArrayList<Relation> relations, 
			RhizomeData body, 
			ArrayList<Extension> extensions) 
	{
		this.docID = docID;
		this.metadata = metadata;
		this.relations = relations;
		this.extensions = extensions;
		this.body = body;
	}
	
	/**
	 * Get the document ID for this document.
	 * @return The document ID.
	 */
	public String getDocumentID() {
		return this.docID;
	}
	/**
	 * Get the document ID for this document.
	 * A convenience call for getDocumentID() (Since getDocID was the 
	 * method name in Pilaster).
	 * @return The document ID
	 */
	public String getDocID() {
		return this.getDocumentID();
	}

	/**
	 * Get a list of Metadatum items.
	 * 
	 * @return ArrayList of Metadatum items.
	 */
	public ArrayList<Metadatum> getMetadata() {
		return this.metadata;
	}
	
	/**
	 * Get the number of Metadatum items.
	 * @return
	 */
	public int metadataSize(){
		return this.metadata.size();
	}

	/**
	 * Get relations list
	 * @return list of Relation objects.
	 */
	public ArrayList<Relation> getRelations() {
		return this.relations;
	}

	/**
	 * Get the body (the data) of the document.
	 * @return the body as a RhizomeData object.
	 */
	public RhizomeData getData() {
		return this.body;
	}

	/**
	 * Get a list of extensions
	 * @return a list of Extension objects.
	 */
	public ArrayList<Extension> getExtensions() {
		return this.extensions;
	}
	
	/**
	 * Gets extension by name.
	 * @param name
	 */
	public Extension getExtensionByName(String name) {
		Extension ext;
		for(int i = 0; i < this.extensions.size(); ++i) {
			ext = this.extensions.get(i);
			if(ext.getName().equals(name)) return ext;
		}
		// This ought to throw an exception.
		return null;
	}
	
	/**
	 * Checks to see if extension exists.
	 * @param name
	 * @return true if the extension is found.
	 */
	public boolean hasExtension(String name) {
		Extension ext;
		for(int i = 0; i < this.extensions.size(); ++i) {
			ext = this.extensions.get(i);
			if(ext.getName().equals(name)) return true;
		}
		return false;
	}
	
	/**
	 * Set the body text for the object.
	 * This string gets wrapped in a RhizomeData object.
	 * Generally, you should use either setBody(String, String)
	 * to set the content type, or you should use setBody(RhizomeData).
	 * @param txt
	 */
	public void setBody(String txt) {
		this.setBody(new RhizomeData(txt));
	}
	
	/**
	 * Set the body text for the object.
	 * Creates a new RhizomeData object with the correct
	 * MIME type, and then sets the text.
	 * @param mimeType
	 * @param txt
	 */
	public void setBody(String mimeType, String txt) {
		this.setBody(new RhizomeData(mimeType, txt));
	}
	
	/**
	 * Set the body text for the object.
	 * @param rd
	 */
	public void setBody(RhizomeData rd) {
		this.body = rd;
	}
	
	/**
	 * Add a relation to the existing list.
	 * @param rel
	 * 
	 */
	public void addRelation(Relation rel) {
		this.relations.add(rel);
	}
	
	/**
	 * Add a new relation.
	 * This is a convenience method to quickly add a new relation.
	 * @param relType The type of relation (sive "the relation name")
	 * @param relDocID The docID of the related document.
	 */
	public void addRelation(String relType, String relDocID) {
		this.relations.add(new Relation(relType, relDocID));
	}
	
	/**
	 * Convenience method to add a relation.
	 * Note that this will use the default relation type.
	 * @param relDocID The document ID of the related document.
	 * @see Relation(String)
	 */
	public void addRelation(String relDocID) {
		this.relations.add(new Relation(relDocID));
	}
	
	/**
	 * Add an extension to the existing list.
	 * @param ext
	 */
	public void addExtension(Extension ext) {
		this.extensions.add(ext);
	}
	/**
	 * Add a metadatum item to the existing list.
	 * @param meta
	 */
	public void addMetadatum(Metadatum meta) {
		this.metadata.add(meta);
	}
	
	/**
	 * Convenience method for quickly adding metadata.
	 * @param name
	 * @param values
	 */
	public void addMetadatum(String name, ArrayList<String> values) {
		this.metadata.add(new Metadatum(name, values));
	}
	
	/**
	 * Convenience method for quickly adding metdata. Note that this
	 * one also takes a dataType.
	 * @param name
	 * @param values
	 * @param dataType
	 * @see Metdatum#getDataType()
	 */
	public void addMetadatum(String name, 
			ArrayList<String> values, 
			String dataType) 
	{
		Metadatum md = new Metadatum(name, values);
		md.setDataType(dataType);
		this.metadata.add(md);
	}
	
	public Document getDOM() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db; 
		db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		return this.getDOM(doc);
	}
	
	/**
	 * Get the object as an XML DOM.
	 * This is one way of converting a RhizomeDocument into
	 * an XML document.
	 * <p>Currently, the XML conversion of the document is
	 * monolithic -- that is, this one method does all of
	 * the traversal of the child objects and creates XML
	 * DOM representations. This is done for two reasons:
	 * (1). I may switch to BetterXML instead of DOM, and
	 * (2). I may make a second encoding besides DOM.</p>
	 * @return The document in the form of a DOM.
	 */
	public Document getDOM(Document doc) {
		
		Element rhizome_ele = 
			doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_ROOT);
		rhizome_ele.setAttribute(RHIZOME_DOC_ATTR_DOCID, this.docID);
		doc.appendChild(rhizome_ele);
		this.addRhizomeElements(doc, rhizome_ele);
		return doc;
	}
	
	public Document getDOM(Document doc, Element parent_ele) {
		Element rhizome_ele = 
			doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_ROOT);
		rhizome_ele.setAttribute(RHIZOME_DOC_ATTR_DOCID, this.docID);
		parent_ele.appendChild(rhizome_ele);
		this.addRhizomeElements(doc, rhizome_ele);
		return doc;
	}
		
	/**
	 * Add the requisite body elements, filled out from object data.
	 * @param doc
	 * @param rhizome_ele
	 */
	private void addRhizomeElements(Document doc, Element rhizome_ele) {
		//Add the main document sections:
		Element meta_ele = doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_METADATA);
		Element relations_ele = doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_RELATIONS);
		Element data_ele = doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_DATA);
		Element extensions_ele = doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_EXTENSIONS);
		rhizome_ele.appendChild(meta_ele);
		rhizome_ele.appendChild(relations_ele);
		rhizome_ele.appendChild(data_ele);
		rhizome_ele.appendChild(extensions_ele);
		
		//Add metadata information
		Element md_ele, val_ele;
		Text val_pcd;
		for (Metadatum m : this.getMetadata()) {
			md_ele = doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_METADATUM);
			md_ele.setAttributeNS(RHIZOME_DOC_XMLNS, 
					RHIZOME_DOC_ATTR_NAME, 
					m.getName());
			md_ele.setAttributeNS(RHIZOME_DOC_XMLNS, 
					RHIZOME_DOC_ATTR_DATATYPE, 
					m.getDataType());
			meta_ele.appendChild(md_ele);
			if(m.hasValues()) {
				for(String txt : m.getValues()) {
					val_ele = doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_VALUE);
					val_pcd = doc.createTextNode(txt);
					val_ele.appendChild(val_pcd);
					md_ele.appendChild(val_ele);
				}
			}
		}
		
		//Add relations
		if(this.relations.size() > 0) {
			Element rel_ele; 
			Text rel_txt;
			for(Relation r : this.getRelations()) {
				rel_ele = doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_VALUE);
				rel_txt = doc.createTextNode(r.getDocID());
				rel_ele.appendChild(rel_txt);
				if(r.hasRelationType())
					rel_ele.setAttributeNS(RHIZOME_DOC_XMLNS, 
							RHIZOME_DOC_ATTR_RELATIONTYPE, 
							r.getRelationType());
			}
		}
		
		//Add data
		if(this.body.getDataLength() > 0) {
			data_ele.setAttributeNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_ATTR_MIMETYPE, this.body.getMimeType());
			if(this.body.isXMLMimeType()) {
				// FIXME: Do XML processing...
				/*
				 * Need to get a new parser, parse the document, and insert
				 * the results into the DOM. Need to check namespace issues
				 * as well, since we don't want to insert a doc into our
				 * own namespace.
				 * 
				 * In the event that a parser cannot be retrieved, should
				 * definitely catch the exception an insert the document
				 * in a CDATA section.
				 * 
				 * Update: Partially done.
				 */
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
				
					Document tempD = db.parse(new java.io.ByteArrayInputStream(this.body.getData().getBytes()));
					Element rn = tempD.getDocumentElement();
					data_ele.appendChild(doc.adoptNode(rn));
				} catch (Exception e ) {
				    CDATASection cdata = doc.createCDATASection(this.body.getData());
				    data_ele.appendChild(cdata);
				}
			} else {
				CDATASection cdata = doc.createCDATASection(this.body.getData());
				data_ele.appendChild(cdata);
			}
		}
		
		//Add extensions
		if(this.extensions.size() > 0 ) {
			Element ext_ele;
			for(Extension ext : this.getExtensions()) {
				ext_ele = doc.createElementNS(RHIZOME_DOC_XMLNS, RHIZOME_DOC_EXTENSION);
				ext_ele.setAttributeNS(RHIZOME_DOC_XMLNS, 
						RHIZOME_DOC_ATTR_NAME, 
						ext.getName());
				extensions_ele.appendChild(ext_ele);
				Node extroot_ele = doc.importNode(
						ext.getDOMDocument().getDocumentElement(), 
						true);
				ext_ele.appendChild(extroot_ele);
			}
		}
	}
	
	public String toXML() throws ParserConfigurationException {
		return this.getDOM().toString();
	}
	/*
	public XDocument getXDocument() {
		XElement rhizome = new XElement();
		XAttributes rhi_attrs = new XAttributes()
		rhi_attrs.setAttribute("xmlns",RHIZOME_XMLNS)
		XDocument xdoc = new XDocument();
		return xdoc;
		
	}
	*/
}
