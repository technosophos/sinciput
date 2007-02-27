package com.technosophos.rhizome.repository;

import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.RepositoryContext;

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
	public void updateIndex(RhizomeDocument doc);
	
	/**
	 * Update the index with a document already in the repository.
	 * 
	 * This will try to fetch a document from the repository and then index it.
	 * @see updateIndex(RhizomeDocument) 
	 * @param docID
	 */
	public void updateIndex(String docID);
	
	/**
	 * Re-create the entire index.
	 * 
	 * Re-create the index from scratch, indexing all of the documents in
	 * the repository.
	 * @return number of documents indexed.
	 */
	public long reindex(); 
	
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
