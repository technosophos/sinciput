package com.technosophos.rhizome.repository;

/**
 * The main entry point to the Rhizome backend.
 * 
 * The repository manager provides the necessary mechanisms to get 
 * instances of the backend repository, index, and searcher.
 * @author mbutcher
 *
 */
public class RepositoryManager {
	
	/**
	 * Default indexer class. 
	 * If no alternate indexer is given, this one will be used.
	 */
	public static String DEFAULT_INDEXER_CLASS_NAME = 
		"com.technosophos.repository.foo";
	/**
	 * Default repository class name.
	 * If no other repository class is given, this one will be used.
	 */
	public static String DEFAULT_REPOSITORY_CLASS_NAME = 
		"com.technosophos.repository.foo";
	
	private String indexerClassName = null;
	private String repositoryClassName = null;
	private DocumentRepository repoInstance = null;
	private DocumentIndexer indexerInstance = null;

	/**
	 * The main constructor.
	 * This builds a new Repository Manager and initializes it.
	 * 
	 * RepositoryManager uses a lazy instantiation model, so the indexers 
	 * and repository classes will not be loaded until first requested.
	 * However, once one is loaded, the same objects may be served repeatedly
	 * unless they are flagged as non-reusable (via the canBeReused() method).
	 * 
	 * The practical import of this is that you may set the indexer and 
	 * repository classes at any time before the first call to getIndexer() or 
	 * getRepository() and predict the results. But if you set the classname
	 * after these two methods has been called, you can get unpredictable 
	 * results. 
	 * @see DocumentIndexer
	 * @see DocumentRepository
	 */
	public RepositoryManager() {
		this.indexerClassName = RepositoryManager.DEFAULT_INDEXER_CLASS_NAME;
		this.repositoryClassName = RepositoryManager.DEFAULT_REPOSITORY_CLASS_NAME;
	}
	
	/**
	 * Set the name of the class to be used for indexing.
	 * 
	 * The RepositoryManager will load this class on demand for indexing
	 * services.
	 * 
	 * An indexer must implement the DocumentIndexer interface
	 * @param classname
	 * @see com.technosophos.rhizome.repository.DocumentIndexer
	 */
	public void setIndexerClassName(String classname) {
		this.indexerClassName = classname;	
	}
	
	/**
	 * Set the name of the class to be used for indexing.
	 * 
	 * The RepositoryManager will load this class on demand for 
	 * repository access requests.
	 * 
	 * A document repository must implement the DocumentRepository interface.
	 * @param classname
	 * @see DocumentRepository
	 */
	public void setRepositoryClassName(String classname) {
		this.repositoryClassName = classname;
	}
	
	/**
	 * Return the class name of the currently set indexer.
	 * @return class name
	 */
	public String getIndexerClassName() {
		return this.indexerClassName;
	}
	
	/**
	 * Return the class name of the currently set repository class.
	 * @return class name
	 */
	public String getRepositoryClassName() {
		return this.repositoryClassName;
	}
	
	/**
	 * Get a document repository.
	 * 
	 * Depending on the repository class's isReusable() method, this may
	 * return a fresh instance or a cached copy.
	 * 
	 * @return
	 */
	public DocumentRepository getRepository() 
			throws RhizomeInitializationException {
		// If we already have one, return it.
		if(this.repoInstance != null) return this.repoInstance;
		DocumentRepository repoInst;
		try {
			Class<?> repoClass = Class.forName(this.repositoryClassName);
			repoInst = (DocumentRepository)repoClass.newInstance();
		} catch (ClassNotFoundException e) {
			String errmsg = "Cannot load class: " + this.repositoryClassName;
			throw new RhizomeInitializationException(errmsg);
		} catch (Exception e) {
			String errmsg = "Cannot create object of class " 
				+ this.repositoryClassName
				+ "(Reason: " + e.getMessage() + ")";
			throw new RhizomeInitializationException(errmsg);
		}
		// If reusable, cache.
		if(repoInst.isReusable()) this.repoInstance = repoInst;
		
		return repoInst;
	}
	
	/**
	 * Get a document indexer.
	 * 
	 * Depending on the indexer class's isReusable() method, this may
	 * return a fresh instance or a cached copy.
	 * 
	 * @return
	 */
	public DocumentIndexer getIndexer() 
			throws RhizomeInitializationException {
		// If we already have one, return it.
		if(this.indexerInstance != null) return this.indexerInstance;
		
		DocumentIndexer indInst;
		try {
			Class<?> indClass = Class.forName(this.indexerClassName);
			indInst = (DocumentIndexer)indClass.newInstance();
		} catch (ClassNotFoundException e) {
			String errmsg = "Cannot load class: " + this.indexerClassName;
			throw new RhizomeInitializationException(errmsg);
		} catch (Exception e) {
			String errmsg = "Cannot create object of class " 
				+ this.indexerClassName
				+ "(Reason: " + e.getMessage() + ")";
			throw new RhizomeInitializationException(errmsg);
		}
		// If reusable, cache.
		if(indInst.isReusable()) this.indexerInstance = indInst;
		
		return indInst;
	}
}
