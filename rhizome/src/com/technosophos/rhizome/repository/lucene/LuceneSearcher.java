package com.technosophos.rhizome.repository.lucene;

import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RepositoryContext;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.document.DocumentCollection;
import com.technosophos.rhizome.document.Metadatum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.document.SetBasedFieldSelector;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
//import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
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
	public String [] getDocIDsByMetadataValue(String name, String value) throws RepositoryAccessException {
		
		ArrayList<String> docIDs = new ArrayList<String>();
		String [] fields = {name, LUCENE_DOCID_FIELD};
		
		MapFieldSelector fieldSelector = new MapFieldSelector(fields);
		IndexReader lreader;
		
		try {
			lreader = this.getIndexReader();
			int last = lreader.maxDoc();
			Document d;
			for(int i = 0; i < last; ++i) {
				if(!lreader.isDeleted(i)) {
					d = lreader.document(i, fieldSelector);
					if(this.checkFieldValueMatches(name, value, d))
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
	 * Returns a DocumentCollection of document IDs and metadata.
	 * <p>Given a metadata name and an array of document IDs, 
	 * this returns a DocumentCollection where the key is the document
	 * ID for a document with that metdatum name, and the value is the list of
	 * values (as a <code>String []</code>) for that metadata.</p>
	 * <p>This search ONLY checks for metdata in the document IDs given in the 
	 * <code>docs[]</code> array.</p>
	 * @see com.technosophos.rhizome.document.DocumentCollection
	 * @param name metadatum name to search for
	 * @param docs array of document IDs to search
	 * @return DocumentCollection with entries for docs, each with a Metadatum for name.
	 */
	public DocumentCollection getMetadataByName(String name, String[] docs)  
			throws RepositoryAccessException {
		
		String[] names = {name};
		return this.getDocCollection(names, docs);
		
		/*
		HashMap<String, String[]> vals = new HashMap<String, String[]>();
		
		HashSet<String> activeFields = new HashSet<String>();
		HashSet<String> lazyFields = new HashSet<String>();
		
		activeFields.add(LUCENE_DOCID_FIELD);
		lazyFields.add(name);
		
		SetBasedFieldSelector fsel = new SetBasedFieldSelector(activeFields, lazyFields);
		IndexReader lreader;
		
		try {
			lreader = this.getIndexReader();
			int last = lreader.maxDoc();
			Document d;
			String docID;
			for(int i = 0; i < last; ++i) {
				if(!lreader.isDeleted(i)) {
					d = lreader.document(i, fsel);
					docID = d.get(LUCENE_DOCID_FIELD);
					for(String did: docs)
						if(did.equals(docID)) vals.put(docID, d.getValues(name));
				}
				
			}
			lreader.close();
		} catch (java.io.IOException ioe) {
			throw new RepositoryAccessException("IOException: " + ioe.getMessage());
		}
		
		return vals;
		*/
	}
	
	/**
	 * This retrieves a DocumentCollection.
	 * <p>The collection will have an entry for every member of docIDs that exists in 
	 * the directory. An entry in the list will have a Metadatum item for every item
	 * in the names array.</p>
	 * <p>This method is used to grab a subset of available metadata for a select
	 * batch of document IDs.</p>
	 * @param names
	 * @param docIDs
	 * @return a DocumentCollection containing docs from docIDs, each with metadata.
	 */
	public DocumentCollection getDocCollection(String[] names, String[] docIDs) 
			throws RepositoryAccessException {
		DocumentCollection dc = new DocumentCollection(names);
		
		HashSet<String> activeFields = new HashSet<String>();
		HashSet<String> lazyFields = new HashSet<String>();
		
		activeFields.add(LUCENE_DOCID_FIELD);
		lazyFields.addAll(Arrays.asList(names));
		SetBasedFieldSelector fsel = new SetBasedFieldSelector(activeFields, lazyFields);
		IndexReader lreader;
		
		try {
			lreader = this.getIndexReader();
			int last = lreader.maxDoc();
			Document d;
			String docID;
			for(int i = 0; i < last; ++i) {
				if(!lreader.isDeleted(i)) {
					d = lreader.document(i, fsel);
					docID = d.get(LUCENE_DOCID_FIELD);
					// This should be optimized:
					for(String did: docIDs)
						if(did.equals(docID)) 
							dc.put(docID, this.fetchMetadata(d, names));
				}
				
			}
			lreader.close();
		} catch (java.io.IOException ioe) {
			throw new RepositoryAccessException("IOException: " + ioe.getMessage());
		}
		
		return dc;
		
	}
	
	/**
	 * Search for matching documents, and return them in a DocumentCollection.
	 * <p>This method performs a narrowing (AND) search, returning a DocumentCollection
	 * that contains docIDs for all docs that matched everything in the narrower.</p>
	 * <p>What metadata is in the DocumentCollection? This tries to retrieve all of the
	 * metadata items in the narrower, plus all of the metadata in the additional_md
	 * array.</p>
	 * <p>So, if there are two items in the narrower, and three items in the additional_md:
	 * <ul>
	 * <li>The DocumentCollection will contain an entry for every document that matched 
	 * both items in the narrower.</li>
	 * <li>Each item will have up to five Metadatum objects: one for each narrower name/value,
	 * and one for each additional_md entry.</li>
	 * </ul>
	 * </p>
	 * <p>This sort of thing is the same operation that could be achived running 
	 * {@see narrowingSearch(Map)}, and using getDocCollection() on the results.
	 * This, however, is far more efficient.</p>
	 * 
	 * 
	 * @param narrower
	 * @param additional_md
	 * @return DocumentCollection with all docs that match the narrower.
	 * @throws RepositoryAccessException
	 */
	public DocumentCollection narrowingSearch(Map<String, String> narrower, String[] additional_md)
			throws RepositoryAccessException {
		
		/*
		 * Welcome to your worst collections nightmare....
		 * fields: Those that require value matching
		 * all_fields: Those that should be included in collection, but need no matching.
		 */
		String [] all_fields;
		Set<String> narrower_keys = narrower.keySet();
		String [] fields = narrower_keys.toArray(new String[narrower_keys.size()]);
		HashSet<String> activeFields = new HashSet<String>();
		HashSet<String> lazyFields = new HashSet<String>();
		
		activeFields.addAll(narrower_keys);
		activeFields.add(LUCENE_DOCID_FIELD);
		
		if (additional_md.length > 0) {
			// If there are additional fields, we need to add those to all_fields.
			List<String> add_md = Arrays.asList(additional_md);
			ArrayList<String> allFields = new ArrayList<String>();
			allFields.addAll(narrower_keys);
			allFields.addAll(add_md);
			
			all_fields = allFields.toArray(new String[allFields.size()]);
			
			// Additional fields should be lazily loaded.
			lazyFields.addAll(add_md);
		} else {
			all_fields = fields;
		}
		
		DocumentCollection dc = new DocumentCollection(all_fields);
		SetBasedFieldSelector fsel = new SetBasedFieldSelector(activeFields, lazyFields);
		IndexReader lreader;
		
		// Do the work....
		try {
			lreader = this.getIndexReader();
			int last = lreader.maxDoc();
			Document d;
			String docID;
			for(int i = 0; i < last; ++i) {
				if(!lreader.isDeleted(i)) {
					d = lreader.document(i, fsel);
					docID = d.get(LUCENE_DOCID_FIELD);
					if(this.checkANDFieldMatches(fields, narrower, d))
						dc.put(docID, this.fetchMetadata(d, all_fields));
				}
				
			}
			lreader.close();
		} catch (java.io.IOException ioe) {
			throw new RepositoryAccessException("IOException: " + ioe.getMessage());
		}
		
		return dc;
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
	/*
	private boolean checkANDFieldMatches(Map<String, String> match, Document doc) {
		int l = match.size();
		String [] fields = match.keySet().toArray(new String[l]);
		return this.checkANDFieldMatches(fields, match, doc);
	}
	*/
	
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
	
	private ArrayList<Metadatum> fetchMetadata(Document d, String[] names) {
		ArrayList<Metadatum> md = new ArrayList<Metadatum>(names.length);
		
		for(String name: names)	md.add(new Metadatum(name, d.getValues(name)));
		
		return md;
	}
	
	public boolean isReusable() {
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
