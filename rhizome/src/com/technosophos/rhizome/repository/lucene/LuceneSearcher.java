package com.technosophos.rhizome.repository.lucene;

import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RepositoryContext;

import java.util.Map;

public class LuceneSearcher implements RepositorySearcher {

	RepositoryContext context;
	
	public LuceneSearcher() {
		this.context = new RepositoryContext();
	}
	
	public LuceneSearcher(RepositoryContext cxt) {
		this.context = cxt;
	}
	
	/**
	 * Get an array containing metadata names.
	 * Metadata has two main parts: the name and the list of values. This method
	 * retrieves a complete list of unique names in the database.
	 * @return
	 */
	public String []  getMetadataNames() {
		return new String[0];
	}
	
	/**
	 * Get all metadata that have the specified name and value.
	 * Get an array of document IDs for documents that contain the metadatum
	 * with the name <code>name</code> and one of the values matches <code>value</code>.
	 * @param name metadatum name
	 * @param value value to search for in <code>name</code> metadata.
	 * @return array of matching document IDs.
	 */
	public String [] getMetadataByValue(String name, String value) {
		return null;
	}
	
	/**
	 * Get metadatum values.
	 * Given the name of a metadatum and the document ID for a document,
	 * this gets the associated values for that metadatum.
	 * @param name Name of metadata to get value for.
	 * @param docID Name of the document to fetch
	 * @return Array of metadata values
	 */
	public String [] getMetadatumByDocID(String name, String docID) {
		return null;
	}
	
	/**
	 * Returns a map of document IDs and values.
	 * Given a metadata name, this returns a map where the key is the document
	 * ID for a document with that metdatum name, and the value is the list of
	 * values (as a <code>String []</code>) for that metadata.
	 * @param name
	 * @return Map of documentID->String['val1','val2'...]
	 */
	public java.util.Map getMetadataByName(String name) {
		return null;
	}
	
	/**
	 * Returns a map of document IDs and values.
	 * <p>Given a metadata name and an array of document IDs, 
	 * this returns a map where the key is the document
	 * ID for a document with that metdatum name, and the value is the list of
	 * values (as a <code>String []</code>) for that metadata.</p>
	 * <p>This search ONLY checks for metdata in the document IDs given in the 
	 * <code>docs[]</code> array.</p>
	 * @param name metadatum name to search for
	 * @param docs array of document IDs to search
	 * @return Map of documentID->String['val1','val2'...]
	 */
	public java.util.Map getMetadataByName(String name, String[] docs) {
		return null;
	}
	
	/**
	 * Perform a search for documents with multiple metadata names.
	 * Given a <code>Map</code> of metadatum names and values, 
	 * this searches for documents that have *all* of
	 * the given metadata. This performs like a (short-circuit) AND-ing search.
	 * <p>For example, the map may contain <code>{'key1'=>'val1', 'key2'=>'val2'}</code>.
	 * A document ID will be returned in the String[] iff it has both keys, and it has 
	 * values that match the given values.</p> 
	 * @param narrower
	 * @return
	 */
	public String [] narrowingSearch(Map narrower) {
		return null;
	}
	
	public boolean isReusable() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public RepositoryContext getConfiguration() {
		return this.context;
	}
	
	public void setConfiguration(RepositoryContext context) {
		this.context = context;
	}
}
