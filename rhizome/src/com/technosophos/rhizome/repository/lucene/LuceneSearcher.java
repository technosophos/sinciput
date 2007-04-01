package com.technosophos.rhizome.repository.lucene;

import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RepositoryContext;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.MapFieldSelector;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.File;
import static com.technosophos.rhizome.repository.lucene.LuceneElements.*;

public class LuceneSearcher implements RepositorySearcher {

	//public static String LUCENE_INDEX_PATH_PARAM = "indexpath";
	
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
	public String []  getMetadataNames() throws RepositoryAccessException {
		String [] fields = null;
		try {
			File indexDir = new File(this.context.getParam(LUCENE_INDEX_PATH_PARAM));
			IndexReader lreader = IndexReader.open(indexDir);
			Collection c = lreader.getFieldNames(IndexReader.FieldOption.ALL);
			fields = new String[c.size()];
			Iterator it = c.iterator();
			for(int i = 0; i < c.size(); ++i) {
				fields[i] = it.next().toString();
			}
			lreader.close();
		} catch (java.io.IOException ioe) {

			throw new RepositoryAccessException("IOException: " + ioe.getMessage());
		}
		return fields;
	}
	
	/**
	 * Get all docIDs that have the specified name and value.
	 * Get an array of document IDs for documents that contain the metadatum
	 * with the name <code>name</code> and one of the values matches <code>value</code>.
	 * @param name metadatum name
	 * @param value value to search for in <code>name</code> metadata.
	 * @return array of matching document IDs.
	 */
	public String [] getDocIDsByMetadataValue(String name, String value) {
		return null;
	}
	
	/**
	 * Get metadatum values.
	 * Given the name of a metadatum and the document ID for a document,
	 * this gets the associated values for that metadatum.
	 * @param name Name of metadata to get value for.
	 * @param docID Name of the document to fetch
	 * @return Array of metadata values
	 * @throws RepositoryAccessException if there is an underlying IO issue.
	 */
	public String [] getMetadatumByDocID(String name, String docID) 
			throws RepositoryAccessException {
		String [] vals = null;
		String [] fields = {name};
		
		MapFieldSelector fsel = new MapFieldSelector(fields);
		IndexReader lreader;
		
		try {
			lreader = this.getIndexReader();
			TermDocs td = lreader.termDocs(new Term(LUCENE_DOCID_FIELD,docID));
			
			ArrayList<String> md_vals = new ArrayList<String>();
			while(td.next()) {
				Document d = lreader.document(td.doc(), fsel);
				md_vals.add(d.get(name));
			}
			vals = md_vals.toArray(new String[md_vals.size()]);
			td.close();
			lreader.close();
		} catch (java.io.IOException ioe) {
			throw new RepositoryAccessException("IOException: " + ioe.getMessage());
		}
		
		return vals;
	}
	
	/**
	 * Returns a map of document IDs and values.
	 * Given a metadata name, this returns a map where the key is the document
	 * ID for a document with that metdatum name, and the value is the list of
	 * values (as a <code>String []</code>) for that metadata.
	 * @param name
	 * @return Map of documentID->String['val1','val2'...]
	 */
	public java.util.Map<String, String[]> getMetadataByName(String name) throws RepositoryAccessException {
		HashMap<String,String[]> vals = new HashMap<String, String[]>();
		String [] fields = {LUCENE_DOCID_FIELD, name};
		
		MapFieldSelector fieldSelector = new MapFieldSelector(fields);
		IndexReader lreader;
		
		try {
			lreader = this.getIndexReader();
			int last = lreader.maxDoc();
			Document d;
			for(int i = 0; i < last; ++i) {
				if(!lreader.isDeleted(i)) {
					d = lreader.document(i, fieldSelector);
					if(d.getField(name) != null)
						vals.put(d.get(LUCENE_DOCID_FIELD), d.getValues(name));
				}
				
			}
			lreader.close();
		} catch (java.io.IOException ioe) {
			throw new RepositoryAccessException("IOException: " + ioe.getMessage());
		}
		
		return vals;
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
	public java.util.Map<String, String[]> getMetadataByName(String name, String[] docs) {
		/*
		 * Loop through all IDs, getting a document with only attribute DocID
		 * If docID matches, fetch name too?
		 */
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
	public String [] narrowingSearch(Map<String, String> narrower) throws RepositoryAccessException {
		
		ArrayList<String> docIDs = new ArrayList<String>();
		
		// Get String[] of names for MapFieldSelector
		Iterator keys = narrower.keySet().iterator();
		int l = narrower.size();
		String [] fields = new String[l];
		for(int i = 0; i < l; ++i) fields[i] = (String)keys.next();
		
		/*
		 * Irritating artifact of limitations of MapFieldSelector:
		 * Since there is no way to add last field to the map field selector,
		 * we need to copy the array and add the DocID field to this version.
		 * 
		 * The other array (fields[]) is used for scanning values.
		 */
		String [] fields_plus = new String[fields.length + 1];
		for(int j = 0; j < fields.length; ++ j)
			fields_plus[j] = fields[j];
		fields_plus[fields_plus.length -1] = LUCENE_DOCID_FIELD; 
		
		// Now we are ready to check for matches:
		MapFieldSelector fieldSelector = new MapFieldSelector(fields_plus);
		IndexReader lreader;
		try {
			lreader = this.getIndexReader();
			int last = lreader.maxDoc();
			Document d;
			for(int i = 0; i < last; ++i) {
				if(!lreader.isDeleted(i)) {
					d = lreader.document(i, fieldSelector);
					if(this.checkANDFieldMatches(fields, narrower, d))
						docIDs.add(d.get(LUCENE_DOCID_FIELD));
				}
			}
			lreader.close();
		} catch (java.io.IOException ioe) {
			throw new RepositoryAccessException("IOException: " + ioe.getMessage());
		}
		
		return docIDs.toArray(new String[docIDs.size()]);
	}
	
	/** 
	 * Helper function that checks that all fields in a list match for a document.
	 * Takes a Map of key, val pairs to match with field, vals in doc. A Document field may 
	 * have multiple values. This checks all values.
	 */
	private boolean checkANDFieldMatches(Map<String, String> match, Document doc) {
		Iterator keys = match.keySet().iterator();
		int l = match.size();
		String [] fields = new String[l];
		for(int i = 0; i < l; ++i) fields[i] = (String)keys.next();
		return this.checkANDFieldMatches(fields, match, doc);
	}
	
	/** 
	 * Helper function that checks that all fields in a list match for a document.
	 * Takes a Map of key, val pairs to match with field, vals in doc. A Document field may 
	 * have multiple values. This checks all values.
	 * @return true if the document matches *all* match criteria.
	 */
	private boolean checkANDFieldMatches(String[] keys, Map<String, String> match, Document doc) {
		for(String key: keys) {
			// Fail fast:
			if(!this.checkFieldValueMatches(key, match.get(key), doc))
				return false;
		}
		return true; // everything matches for this doc.
	}
	
	/** Helper function for checking field values. */
	private boolean checkFieldValueMatches(String key, String value, Document doc) {
		for( String val: doc.getValues(key)) {
			if(value.equals(val)) return true;
		}
		return false;
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
	
	private IndexReader getIndexReader() throws java.io.IOException {
		File indexDir = new File(this.context.getParam(LUCENE_INDEX_PATH_PARAM));
		return IndexReader.open(indexDir);
	}
}
