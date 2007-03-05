package com.technosophos.rhizome.repository;

import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentNotFoundException;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.RhizomeException;

/**
 * The main entry point to the Rhizome backend.
 * <p>
 * The repository manager provides the necessary mechanisms to get 
 * instances of the backend repository, index, and searcher. Also, it 
 * has a handful of convenience methods that make it much easier to 
 * perform simple tasks like getting and storing documents.
 * </p><p>
 * There should be only one instance of RepositoryManager per repository.
 * Multiple instances may have undefined behavior (depending on the 
 * backend implementation).
 * </p><p>
 * The backend repository, indexer, and searcher interfaces can have 
 * different implementations. The default implementation is loaded 
 * automatically. To use alternative implementations, set the 
 * class names with the set*ClassName() methods. You must do this
 * before one of the accessors for those objects is called, or else 
 * you might get unpredictable results.
 * </p>
 * @author mbutcher
 *
 */
public class RepositoryManager {
	
	/**
	 * Default indexer class. 
	 * If no alternate indexer is given, this one will be used.
	 */
	public static String DEFAULT_INDEXER_CLASS_NAME = 
		"com.technosophos.rhizome.repository.lucene.LuceneIndexer";
	/**
	 * Default repository class name.
	 * If no other repository class is given, this one will be used.
	 */
	public static String DEFAULT_REPOSITORY_CLASS_NAME = 
		"com.technosophos.rhizome.repository.fs.FileSystemRepository";
	/**
	 * Default repository searcher class name.
	 * If no other repository searcher class is given, this one will be used.
	 */
	public static String DEFAULT_REPOSITORY_SEARCHER_CLASS_NAME = 
		"com.technosophos.rhizome.repository.lucene.LuceneSearcher";
	
	private String indexerClassName = null;
	private String repositoryClassName = null;
	private String searcherClassName = null;
	
	private RepositoryContext context = null;
	
	private DocumentRepository repoInstance = null;
	private DocumentIndexer indexerInstance = null;
	private RepositorySearcher searchInstance = null;
	
	private boolean indexerInstanceCreated = false;
	private boolean searcherInstanceCreated = false;
	private boolean repoInstanceCreated = false;

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
	 * @param context The configuration information for this RepositoryManager.
	 * @see DocumentIndexer
	 * @see DocumentRepository
	 */
	public RepositoryManager(RepositoryContext context) {
		this.context = context;
		this.indexerClassName = RepositoryManager.DEFAULT_INDEXER_CLASS_NAME;
		this.repositoryClassName = RepositoryManager.DEFAULT_REPOSITORY_CLASS_NAME;
		this.searcherClassName = RepositoryManager.DEFAULT_REPOSITORY_SEARCHER_CLASS_NAME;
	}
	
	/**
	 * Get the current configuration context
	 * @return Returns the RepositoryContext, which could be null.
	 */
	public RepositoryContext getContext() {
		return this.context;
	}
	
	/*===============================================
	 * Convenience Methods
	 *===============================================*/
	
	/**
	 * Put a document into Rhizome.
	 * <p>
	 * This puts a document into the repository. If a document with the same
	 * document ID already exists, then this document will replace the other
	 * (in other words, it acts as a modification operation). If no other document
	 * exists, this will be added to the repository.</p>
	 * <p>Adding a document this way automatically puts it into the search
	 * index, so there is no reason to interact directly with the indexer.</p>
	 * @param document to add to repository. 
	 */
	public void storeDocument(RhizomeDocument doc) throws RhizomeException {
		DocumentRepository repo = this.getRepository();
		DocumentIndexer indexer = this.getIndexer();
		
		repo.setConfiguration(this.context);
		indexer.setConfiguration(this.context);
		
		repo.storeDocument(doc, true);
		indexer.updateIndex(doc);
	}
	
	/**
	 * This attempts to remove a document from the index (first) and then the 
	 * repository.
	 * <p>If a document cannot be removed from the index, then it will not be removed from
	 * the repository. Otherwise, there would be the possiblity for much misleading
	 * information.</p>
	 * <p>On the other hand, if a document is successfully removed from the index, 
	 * and then cannot be removed from the repository, the transaction is not rolled
	 * back. The document is left in the repository, and omitted from the index.
	 * <b>This behavior may change in future versions.</b></p>
	 * <p>In both cases, if a delete fails, a 
	 * <code>RepositoryAccessException</code> will be thrown.</p>
	 * 
	 * @param docID document to delete
	 * @throws RepositoryAccessException, RhizomeException
	 */
	public void removeDocument(String docID) throws RhizomeException {
		DocumentRepository repo = this.getRepository();
		DocumentIndexer indexer = this.getIndexer();
		
		if (!indexer.deleteFromIndex(docID)) 
			throw new RepositoryAccessException(
				"Could not remove document from index. Document is still available.");
		if(!repo.removeDocument(docID))
			throw new RepositoryAccessException(
				"Could not remove document from repository. Document is not in index.");
	}
	
	/**
	 * Return a document.
	 * <p>Given a document ID, attempt to fetch the document from the repository.</p>
	 * <p>If the file is not found, a <code>repositoryAccessException</code> is thrown.</p>
	 * @param docID
	 * @return
	 * @throws RhizomeException
	 */
	public RhizomeDocument getDocument(String docID) 
			throws DocumentNotFoundException, RhizomeException {
		return this.getRepository().getDocument(docID);
	}
	/*===============================================
	 * Mutator Methods
	 *===============================================*/
	/**
	 * Set the repository context.
	 * This should not be called after getRepository, getIndexer, or getSearcher.
	 * Doing so may return objects in an inconsistent way.
	 * @param context
	 */
	public void setContext(RepositoryContext context) {
		this.context = context;
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
	 * @throws RhizomeClassInstanceException if the getIndexer() method has been called.
	 */
	public void setIndexerClassName(String classname) 
			throws RhizomeClassInstanceException {
		if(this.indexerInstanceCreated) 
			throw new RhizomeClassInstanceException(
					"Indexer already instantiated. Cannot change class name.");
		
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
	 * @throws RhizomeClassInstanceException if the getRepository() method has been called.
	 */
	public void setRepositoryClassName(String classname)
			throws RhizomeClassInstanceException {
		if(this.repoInstanceCreated) 
			throw new RhizomeClassInstanceException(
					"Repository already instantiated. Cannot change class name."); 
		this.repositoryClassName = classname;
	}
	/**
	 * Set the name of the class to be used for searching.
	 * 
	 * The RepositoryManager will load this class on demand for 
	 * repository search requests.
	 * 
	 * A repository searcher must implement the RepositorySearcher interface.
	 * @param classname
	 * @see RepositorySearcher
	 * @throws RhizomeClassInstanceException if the getRepositorySearcher() 
	 * method has been called.
	 */
	public void setRepositorySearcherClassName(String classname)
			throws RhizomeClassInstanceException {
		if(this.searcherInstanceCreated) 
			throw new RhizomeClassInstanceException(
					"Searcher already instantiated. Cannot change class name."); 
		this.searcherClassName = classname;
	}
	
	/*===============================================
	 * Accessor Methods
	 *===============================================*/
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
	 * @return initialized document repository.
	 */
	public DocumentRepository getRepository() 
			throws RhizomeInitializationException {
		// If we already have one, return it.
		if(this.repoInstance != null) return this.repoInstance;
		DocumentRepository repoInst;
		try {
			Class<?> repoClass = Class.forName(this.repositoryClassName);
			repoInst = (DocumentRepository)repoClass.newInstance();
			repoInst.setConfiguration(this.context);
			this.repoInstanceCreated = true;
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
	 * @return initialized indexer.
	 */
	public DocumentIndexer getIndexer() 
			throws RhizomeInitializationException {
		// If we already have one, return it.
		if(this.indexerInstance != null) return this.indexerInstance;
		
		DocumentIndexer indInst;
		try {
			Class<?> indClass = Class.forName(this.indexerClassName);
			indInst = (DocumentIndexer)indClass.newInstance();
			this.indexerInstanceCreated = true;
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
	
	/**
	 * Get a repository searcher.
	 * 
	 * Depending on the Searcher class's isReusable() method, this may
	 * return a fresh instance or a cached copy.
	 * 
	 * @return initialized repository searcher.
	 */
	public RepositorySearcher getRepositorySearcher() 
			throws RhizomeInitializationException {
		// If we already have one, return it.
		if(this.searchInstance != null) return this.searchInstance;
		
		RepositorySearcher searchInst;
		try {
			Class<?> searchClass = Class.forName(this.searcherClassName);
			searchInst = (RepositorySearcher)searchClass.newInstance();
			this.searcherInstanceCreated = true;
		} catch (ClassNotFoundException e) {
			String errmsg = "Cannot load class: " + this.searcherClassName;
			throw new RhizomeInitializationException(errmsg);
		} catch (Exception e) {
			String errmsg = "Cannot create object of class " 
				+ this.searcherClassName
				+ "(Reason: " + e.getMessage() + ")";
			throw new RhizomeInitializationException(errmsg);
		}
		// If reusable, cache.
		if(searchInst.isReusable()) this.searchInstance = searchInst;
		
		return searchInst;
	}
	
	/**
	 * Prints out information about what searcher, indexer and repository are being used.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Search: ");
		sb.append(this.searcherClassName);
		sb.append("\nIndex: ");
		sb.append(this.indexerClassName);
		sb.append("\nRepository: ");
		sb.append(this.repositoryClassName);
		sb.append("\n");
		return sb.toString();
	}
}

class RhizomeClassInstanceException extends RhizomeInitializationException {
	private static final long serialVersionUID = 1L;
	
	public RhizomeClassInstanceException(){
		super("Cannot change classes after the class has been instantiated.");
	}
	
	public RhizomeClassInstanceException(String str){
		super(str);
	}
}