package com.technosophos.rhizome.repository;

import java.util.Map;

import com.technosophos.rhizome.document.DocumentCollection;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.document.Metadatum;

/**
 * A document searcher handles searching the repository.
 * 
 * Rhizome's repository backend has three main functional units:
 * <ul>
 * <li>
 * A storage mechanism (DocumentRepository)
 * </li>
 * <li>
 * A searching mechanism (RepositorySearcher)
 * </li>
 * <li>
 * And a document indexing tool (DocumentIndexer)
 * </li>
 * </ul>
 * This part performs the searching of the repository for a list
 * of documents.
 * 
 * <p>WARNING: The javadoc for these functions was copied from LucenSearcher.</p>
 * 
 * @author mbutcher
 *
 */
public interface RepositorySearcher {
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
	 * {@see narrowingSearch(Map<String, String>)}, and using getDocCollection() on the results.
	 * This, however, is far more efficient.</p>
	 * 
	 * 
	 * @param narrower
	 * @param additional_md
	 * @return DocumentCollection with all docs that match the narrower.
	 * @throws RepositoryAccessException
	 * @deprecated
	 */
	public DocumentCollection narrowingSearch(Map<String, String> narrower, String[] additional_md)
			throws RepositoryAccessException;
	
	/**
	 * Replacement for narrowingSearch().
	 * @param narrower
	 * @param additional_md
	 * @param r
	 * @return
	 * @throws RepositoryAccessException
	 */
	public DocumentList fetchDocumentList(Map<String, String> narrower, String[] additional_md, DocumentRepository r)
			throws RepositoryAccessException;
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
	public String [] narrowingSearch(Map<String, String> narrower) 
			throws RepositoryAccessException;
	
	/**
	 * This retrieves a DocumentList.
	 * <p>The list will have a document for every member of docIDs passed in docIDs. 
	 * A document in the list will have a Metadatum item for every item
	 * in the names array.</p>
	 * <p>Implementations of this command may returned proxied documents, which will 
	 * cache the metadata in <code>names</code>, but will proxy access to the rest of the metadata.
	 * Other implementations may return FULL document objects, with all metadata (effectively ignoring names)</p>
	 * @param names Metadata items that should definitely be returned
	 * @param docIDs Array of IDs to look for.
	 * @param repo An initialized repository used to get documents
	 * @return a DocumentList containing docs from docIDs, each with metadata.
	 * @see DocumentList
	 */
	//public DocumentCollection getDocCollection(String[] names, String[] docIDs) 
	//		throws RepositoryAccessException;
	public DocumentList getDocumentList(String[] names, String[] docIDs, DocumentRepository repo) throws RepositoryAccessException;
	
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
	//public DocumentCollection getMetadataByName(String name, String[] docs)  
	//		throws RepositoryAccessException;
	public DocumentList getMetadataByName(String name, String[] docs, DocumentRepository repo)  
	throws RepositoryAccessException;
	/**
	 * Returns a map of document IDs and values.
	 * Given a metadata name, this returns a map where the key is the document
	 * ID for a document with that metdatum name, and the value is the list of
	 * values (as a <code>String []</code>) for that metadata.
	 * @param name
	 * @return Map of documentID->String['val1','val2'...]
	 */
	public java.util.Map<String, String[]> getMetadataByName(String name) throws RepositoryAccessException;
	
	/**
	 * Get metadatum values.
	 * Given the name of a metadatum and the document ID for a document,
	 * this gets the associated values for that metadatum.
	 * @param name Name of metadata to get value for.
	 * @param docID Name of the document to fetch
	 * @return Metadatum containing metadata values.
	 * @throws RepositoryAccessException if there is an underlying IO issue.
	 */
	public Metadatum getMetadatumByDocID(String name, String docID) 
				throws RepositoryAccessException;
	
	public String[] getReverseRelatedDocuments(String docID) throws RepositoryAccessException;
	
	/**
	 * Get all of the docIDs that report having a relationship with this document.
	 * @param docID target document
	 * @param relationType The target relation type.
	 * @return Array of document IDs that point to the given document
	 * @throws RepositoryAccessException
	 */
	public String[] getReverseRelatedDocuments(String docID, String relationType)  throws RepositoryAccessException;
	
	/**
	 * Get all docIDs that have the specified name and value.
	 * Get an array of document IDs for documents that contain the metadatum
	 * with the name <code>name</code> and one of the values matches <code>value</code>.
	 * @param name metadatum name
	 * @param value value to search for in <code>name</code> metadata.
	 * @return array of matching document IDs.
	 */
	public String [] getDocIDsByMetadataValue(String name, String value) throws RepositoryAccessException;
	
	/**
	 * Get an array containing metadata names.
	 * Metadata has two main parts: the name and the list of values. This method
	 * retrieves a complete list of unique names in the database.
	 * @return
	 */
	public String []  getMetadataNames() throws RepositoryAccessException;
	
	/**
	 * This should provide a hint to the Repository Manager as to 
	 * whether this object can be reused indefinitely, or whether every
	 * thread/request needs its own copy.
	 * 
	 * If this returns true, then it is assumed that this component is 
	 * thread-safe and that its statefulness will not interfere with its 
	 * performance. (In other words, if state info is retained, it will be
	 * inconsequential for two different objects.)
	 * 
	 * If you don't understand this, you are best to set this to return
	 * false.
	 * @return true if this object is thread safe and can be reused indefinitely.
	 */
	public boolean isReusable();
	
	public RepositoryContext getConfiguration();
	public void setConfiguration(RepositoryContext context);
}
