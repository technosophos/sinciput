package com.technosophos.rhizome.repository;

import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.RepositoryContext;
import com.technosophos.rhizome.repository.RepositoryManager;
import com.technosophos.rhizome.document.RhizomeParseException;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.repository.RepositoryAccessException;

/**
 * An indexer handles breaking a document down into searchable and indexable
 * parts.
 * 
 * Rhizome integrates searching at a very low level. Any object in the repository
 * should be searchable by specific metadata elements, or by its entire contents.
 * 
 * All instances of Rhizome must provide implementations of an indexer, 
 * even if the implementation does nothing.
 * @author mbutcher
 *
 */
public interface DocumentIndexer {

	/**
	 * Given a document, updates the search index with this document's
	 * information.
	 * 
	 * If the document already exists in the index, this should remove 
	 * defunct info.
	 * @param doc
	 */
	public void updateIndex(RhizomeDocument doc) throws RhizomeInitializationException;
	
	/**
	 * Get the name of the index.
	 * @return the name of the index that this object uses.
	 */
	public String getIndexName();
	
	/**
	 * Update the index with a document already in the repository.
	 * 
	 * This will try to fetch a document from the repository and then index it.
	 * @see updateIndex(RhizomeDocument) 
	 * @param docID
	 */
	public void updateIndex(String docID, RepositoryManager repman) 
		throws RhizomeParseException, RhizomeInitializationException, RepositoryAccessException;
	
	/**
	 * Re-create the entire index.
	 * 
	 * Re-create the index from scratch, indexing all of the documents in
	 * the repository.
	 * @return number of documents indexed.
	 */
	public long reindex(RepositoryManager repman) 
		throws RepositoryAccessException, RhizomeInitializationException; 
	
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
	/**
	 * Delete a document from the index.
	 * 
	 * This does NOT throw an exception if the document is not found. It just returns
	 * false. Note that if it finds (somehow) multiple documents with the same ID, it will
	 * delete them all. Under normal operations, that should never happen, though.
	 * 
	 * @param docID
	 * @return true if one or more documents is deleted
	 * @throws RhizomeInitializationException if there was an error manipulating the index
	 */
	public boolean deleteFromIndex(String docID) throws RhizomeInitializationException;
	public RepositoryContext getConfiguration();
	public void setConfiguration(RepositoryContext context);
}
