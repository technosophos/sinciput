package com.technosophos.rhizome.repository;

/**
 * Factory for creating document repository instances.
 * 
 * <p>A document repository is a named collection of documents. The factory is used
 * to get a named document repository, and return it as a {@link DocumentRepository} 
 * instance.</p>
 * <p>This class also includes methods for checking on the status of a repository, 
 * creating a repository, and deleting a repository.</p>
 * @author mbutcher
 *
 */
public interface DocumentRepositoryDepot {

	/**
	 * Report whether the name repository already exists.
	 * @param name
	 * @return true if the repository exists, false otherwise.
	 */
	public boolean hasNamedRepository(String name, RepositoryContext cxt);
	
	/**
	 * Create a new repository.
	 * <p>Create a new repository in Rhizome. Path information and so on will be retrieved 
	 * from the {@link RhizomeContext}. Hereafter, the repository can be fetched using
	 * the name.</p> 
	 * @param name
	 * @param cxt
	 */
	public void createNamedRepository(String name, RepositoryContext cxt)throws RhizomeInitializationException, RepositoryAccessException;
	
	/**
	 * Get an instance of the document repository.
	 * <p>This will return an initialized {@link DocumentRepository}.</p>
	 * @param name
	 * @param cxt initialized repository.
	 * @return
	 */
	public DocumentRepository getNamedRepository(String name, RepositoryContext cxt)
		throws RhizomeInitializationException;
	
	/**
	 * Remove a repository.
	 * @param name
	 * @param cxt
	 */
	public void deleteNamedRepository(String name, RepositoryContext cxt) throws RepositoryAccessException; 
	
}
