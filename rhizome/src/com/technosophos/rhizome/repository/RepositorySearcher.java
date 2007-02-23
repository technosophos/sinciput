package com.technosophos.rhizome.repository;

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
 * @author mbutcher
 *
 */
public interface RepositorySearcher {
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
}
