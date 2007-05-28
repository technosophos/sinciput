package com.technosophos.rhizome.repository;

/**
 * Acquire and manipulate searchers ({@link RepositorySearcher} instances).
 * @author mbutcher
 *
 */
public interface RepositorySearcherDepot {

	/**
	 * Get an instance of a searcher.
	 * Searcher is created and initialized. The searcher is set to search the named
	 * repository.
	 * @param name
	 * @param cxt
	 * @return initialized searcher.
	 * @throws RhizomeInitializationException
	 */
	public RepositorySearcher getSearcher(String name, RepositoryContext cxt) 
			throws RhizomeInitializationException ;
	
}
