package com.technosophos.rhizome.document;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.repository.RepositoryManager;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Contains a list of document IDs and (optional) metadata.
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
public class DocumentCollection implements Comparator<String> {

	private HashMap<String, List<Metadatum>> entries;
	private String[] md_hints;
	private String sortKey = null;
	
	/**
	 * Create a document collection with metadata hints.
	 * 
	 * @param the names of the metadata that elements in this collection may contain.
	 */
	public DocumentCollection(String [] md_hints) {
		this.md_hints = md_hints;
		this.entries = new HashMap<String, List<Metadatum>>(); 
	}
	
	/**
	 * Create a document collection with metadata hints.
	 * 
	 * @param the names of the metadata that elements in this collection may contain.
	 */
	public DocumentCollection(List<String> md_hints) {
		this(md_hints.toArray(new String[md_hints.size()]));
	}
	
	/**
	 * Create a document collection that tries to retrieve only one metadatum type.
	 * @param md_hint the name of the metadatum to try to retrieve.
	 */
	public DocumentCollection(String md_hint) {
		this(new String []{md_hint});
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
	 * However, not all entries will contain a Metadatum entry for each hint. If a given 
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
	 * Get the RhizomeDocument representation of a particular document.
	 * This is a convenience method for getting a RhizomeDocument for an item in this
	 * document collection. It is lazy in the sense that the document in its entirety is
	 * not fetched until this method is called. For that reason, it is possible that in 
	 * some repositories, the document can be deleted after the metadata is returned, 
	 * resulting in a {@link DocumentNotFoundException} being thrown.
	 * @param repoman An initialized Repository Manager.
	 * @param repoName The name of the repository to get the doc from.
	 * @param docID The ID of the document to fetch.
	 * @return A Rhizome Document
	 * @throws RhizomeException
	 */
	public RhizomeDocument getRhizomeDocument(RepositoryManager repoman, String repoName, String docID) 
			throws RhizomeException {
		return repoman.getDocument(repoName, docID);
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
	 * Comparator function.
	 * <p>This class can also act as a comparator for comparing items in a DocumentCollection.</p>
	 */
	public int compare(String keyA, String keyB) {
		if(this.sortKey == null) this.sortKey = this.md_hints[0];
		try {
			String vA = this.getMetadatum(keyA, sortKey).getFirstValue();
			String vB = this.getMetadatum(keyB, sortKey).getFirstValue();
			//return vA.compareTo(vB);
			return vA.compareToIgnoreCase(vB);
		} catch (NameNotStoredException e) {
			return 0;
		}
	}
	
	public String[] getSortedDocumentIDs(String metadatumName) {
		this.sortKey = metadatumName;
		String[] s = this.getDocumentIDs();
		Arrays.sort( s, (Comparator<String>)this );
		return s;
	}
	
	/**
	 * Get the DocumentList as a {@code java.util.HashMap}.
	 * Note that the hints are not preserved in the hash map.
	 * <b>WARNING:</b> This method may be deprecated or removed.
	 * @return hash map representation of String: List<Metadatum>
	 * @deprecated
	 */
	protected Map<String, List<Metadatum>> getItemsMap() {
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


