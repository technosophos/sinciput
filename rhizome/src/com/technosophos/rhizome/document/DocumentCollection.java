package com.technosophos.rhizome.document;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Contains a list of document IDs and (otional) metadata.
 * <p>A DocumentCollection is a representation of a collection of document information,
 * including the document id (docID) and a {@code java.util.List<Metadatum>} of Metadatum objects.</p>
 *
 * <p>A DocumentCollection contains one entry per document ID. Each entry has a document ID
 * and one or more Metadatum objects: (docID: List<Metadatum>).</p>
 * 
 * <p>Which Metadatum items appear in a list is determined by the population process.
 * Typically, the process that created the collection and populated it did so by 
 * using a consistent set of metadata types. BEcause of the indeterminacy of this process,
 * DocumentCollection has <i>hints</i> about what metadata (by name) might be in entries
 * in this collection.</p>
 *
 * 
 * @author mbutcher
 *
 */
public class DocumentCollection {

	private HashMap<String, List<Metadatum>> entries;
	private String[] md_hints;
	
	/**
	 * Create a document collection with metadata hints.
	 * 
	 * @param the names of the metadata that elements in this collection may contain.
	 */
	public DocumentCollection(String [] md_hints) {
		this.md_hints = md_hints;
		this.entries = new HashMap<String, List<Metadatum>>(); 
	}
	
	/*
	public DocumentCollection() {
		this(new String[0]);
	}
	*/
	
	/**
	 * Get an array of metadata hints.
	 * <p>
	 * A metadata hint is a hint as to what metdata fields you can expect to find in
	 * the Metadatum objects contained in this collection. That is, if the hints array 
	 * contains <code>title</code> and <code>subtitle</code>, then it means that the 
	 * underlying system tried to retrieve title and subtitle information for this 
	 * document.
	 * </p><p>
	 * However, not entries will contain a Metadatum entry for each hint. If a given 
	 * document (docID) does not have the requested Metdatum -- for instance, if a document
	 * does not have a title -- no title Metadatum is added.
	 * </p><p>
	 * Hints, then, should not be treated as required fields, but possible fields.
	 * </p>
	 * @return
	 */
	public String[] getMetadataHints() {
		return this.md_hints;
	}
	
	/**
	 * Returns the number of metadata hints that this collection has.
	 * @see getMetadataHints()
	 * @return maximum number of metadata items
	 */
	public int countMetadataHints() {
		return this.md_hints.length;
	}
	
	/**
	 * Get the Metadatum object for the given document ID.
	 * @param docID
	 * @param name
	 * @return the metadatum object containing the metadata name/values[]
	 * @throws NameNotStoredException if the metadata name is not stored here.
	 */
	public Metadatum getMetadatum(String docID, String name) throws NameNotStoredException {
		if(this.entries.containsKey(docID)) {
			List<Metadatum> vals = this.entries.get(docID);
			for(Metadatum md: vals) {
				if(md.getName().equals(name)) return md;
			}
		}
		return null;
	}
	
	/**
	 * Get a list of all of the metadatum objects for this docID.
	 * Given a document ID, this retrieves all of the metadatum objects for the 
	 * metadata contained in this list. 
	 * @param docID
	 * @return A List of Metadatum objects, or null if the DocID is not found.
	 */
	public List<Metadatum> getMetadata(String docID) {
		if(this.entries.containsKey(docID)) return this.entries.get(docID);
		return null;
	}
	
	/**
	 * Get a set of all document IDs in this object.
	 * @return
	 */
	public String[] getDocumentIDs() {
		Set<String> keys = this.entries.keySet();
		return keys.toArray(new String[keys.size()]);
	}
	
	/**
	 * Get the DocumentList as a {@code java.util.HashMap}.
	 * Note that the hints are not preserved in the hash map.
	 * <b>WARNING:</b> This method may be deprecated or removed.
	 * @return hash map representation of String: List<Metadatum>
	 */
	protected HashMap<String, List<Metadatum>> getHashMap() {
		return this.entries;
	}
	
	/**
	 * Return the number of docs in this collection.
	 * @return
	 */
	public int size() {
		return this.entries.size();
	}
	
	/**
	 * Add a new document to the collection.
	 * If the document already exists, its metadata will be replaced with the items in the 
	 * list.
	 * @param docID
	 * @param md
	 */
	public void put(String docID, List<Metadatum> md) {
		this.entries.put(docID, md);	
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{DocumentCollection: ");
		java.util.Set<String> keys = this.entries.keySet();
		for(String docID: keys) {
			sb.append("{docID: ");
			sb.append(docID);
			sb.append("; ");
			sb.append(this.entries.get(docID).toString());
			sb.append("} ");
		}
		
		sb.append("}");
		return sb.toString();
	}
	
}


