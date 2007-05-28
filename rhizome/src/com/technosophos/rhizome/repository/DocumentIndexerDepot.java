package com.technosophos.rhizome.repository;

/**
 * These classes are designed to provide access to indexers.
 * @author mbutcher
 *
 */
public interface DocumentIndexerDepot {

	/**
	 * Report whether the index exists.
	 * @param name
	 * @return true if the repository exists, false otherwise.
	 */
	public boolean hasIndex(String name, RepositoryContext cxt);
	
	/**
	 * Create a new index.
	 * <p>Create a new repository in Rhizome. Path information and so on will be retrieved 
	 * from the {@link RhizomeContext}. Hereafter, the repository can be fetched using
	 * the name.</p> 
	 * @param name
	 * @param cxt
	 */
	public void createIndex(String name, RepositoryContext cxt)throws RhizomeInitializationException, RepositoryAccessException;
	
	/**
	 * Get an instance of the indexer.
	 * <p>This will return an initialized {@link DocumentIndexer}.</p>
	 * @param name
	 * @param cxt initialized repository.
	 * @return
	 */
	public DocumentIndexer getIndexer(String name, RepositoryContext cxt)
		throws RhizomeInitializationException;
	
	/**
	 * Remove an index.
	 * @param name
	 * @param cxt
	 */
	public void deleteIndex(String name, RepositoryContext cxt) throws RepositoryAccessException; 
	
	
}
