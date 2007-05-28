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
	/*
	 * TODO: Rewrite the configurations stuff so that the Repository Manager does
	 * all of the configuring of the indexer, searcher, and repository.
	 * 
	 * Make Interfaces for those three contain arrays of all necessary configuration
	 * parameters, and all optional configurations. That way, better reports can be
	 * generated from RepoMan.
	 */
	
	
	/**
	 * Default indexer class. 
	 * If no alternate indexer is given, this one will be used.
	 */
	public static final String DEFAULT_INDEXER_CLASS_NAME = 
		"com.technosophos.rhizome.repository.lucene.LuceneIndexer";
	/**
	 * Default repository class name.
	 * If no other repository class is given, this one will be used.
	 */
	public static final String DEFAULT_REPOSITORY_CLASS_NAME = 
		"com.technosophos.rhizome.repository.fs.FileSystemRepositoryDepot";
	/**
	 * Default repository searcher class name.
	 * If no other repository searcher class is given, this one will be used.
	 */
	public static final String DEFAULT_REPOSITORY_SEARCHER_CLASS_NAME = 
		"com.technosophos.rhizome.repository.lucene.LuceneSearcher";
	
	public static final String CXT_INDEXER_CLASS_NAME = "indexer_class";
	public static final String CXT_REPOSITORY_CLASS_NAME = "repository_class";
	public static final String CXT_REPOSITORY_SEARCHER_CLASS_NAME = "searcher_class";
	
	private RepositoryContext context = null;
	
	private DocumentRepository repoInstance = null;
	private DocumentIndexer indexerInstance = null;
	private RepositorySearcher searchInstance = null;
	
	private DocumentRepositoryDepot drFact = null;
	
	private Class<?> indexerClass = null;
	private Class<?> repositoryClass = null;
	private Class<?> searcherClass = null;
	
	/*
	 * The main constructor.
	 * This builds a new Repository Manager and initializes it.
	 * 
	 * RepositoryManager uses a lazy instantiation model, so the indexers 
	 * and repository classes will not be loaded until first requested.
	 * However, once one is loaded, the same objects may be served repeatedly
	 * unless they are flagged as non-reusable (via the canBeReused() method).
	 *
	 * @param context The configuration information for this RepositoryManager.
	 * @see DocumentIndexer
	 * @see DocumentRepository
	 *//*
	public RepositoryManager(RepositoryContext context) {
		
	}
	*/
	
	/**
	 * When a new Rhizome Manager is created, it must be initialized.
	 * <p>This method initializes a new Rhizome Manager. Along with some trivial 
	 * initialization, it performs all class loading needed for this manager, and
	 * failures to load classes will result in an exception.</p>
	 * @param context The new repository context.
	 * @throws
	 */
	public void init(RepositoryContext context) throws RhizomeException {
		this.context = context;
		
		try {
			Class tempClass;
			if(context.hasKey(CXT_INDEXER_CLASS_NAME))
				indexerClass = Class.forName(context.getParam(CXT_INDEXER_CLASS_NAME));
			else indexerClass = Class.forName(DEFAULT_INDEXER_CLASS_NAME);
			
			// Create new repository factory:
			if(context.hasKey(CXT_REPOSITORY_CLASS_NAME))
				tempClass = Class.forName(context.getParam(CXT_REPOSITORY_CLASS_NAME));
			else tempClass = Class.forName(DEFAULT_REPOSITORY_CLASS_NAME);
			this.drFact = (DocumentRepositoryDepot)tempClass.newInstance();
			
			if(context.hasKey(CXT_REPOSITORY_SEARCHER_CLASS_NAME))
				searcherClass = Class.forName(context.getParam(CXT_REPOSITORY_SEARCHER_CLASS_NAME));
			else searcherClass = Class.forName(DEFAULT_REPOSITORY_SEARCHER_CLASS_NAME);
			
		}catch (ClassNotFoundException cnfe) {
			throw new RhizomeInitializationException("Failed to load class: " + cnfe.getMessage(), cnfe);
		} catch (InstantiationException ie) {
			throw new RhizomeInitializationException("Failed to instantiate class: " + ie.getMessage(), ie);
		} catch (IllegalAccessException iae) {
			throw new RhizomeInitializationException("Class access problem: " + iae.getMessage(), iae);
		}

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
	 * 
	 * If instances of the repository, search, or indexer have been created, they will
	 * be re-configured. In other words, this will make every attempt to re-initialize
	 * all parts of the Rhizome backend with the new context.
	 * @param context
	 */
	public void setContext(RepositoryContext context) {
		
		try {
			if(this.repoInstance != null) this.repoInstance.setConfiguration(context);
			if(this.searchInstance != null) this.searchInstance.setConfiguration(context);
			if(this.indexerInstance != null) this.indexerInstance.setConfiguration(context);
		} catch (RhizomeInitializationException rie) {
			this.repoInstance = null;
			this.searchInstance = null;
			this.indexerInstance = null;
		}
			
		this.context = context;
	}

	
	/*===============================================
	 * Accessor Methods
	 *===============================================*/
	
	public boolean hasRepository(String name) {
		return false;
	}


	public DocumentRepository getRepository(String repoName) 
			throws RhizomeInitializationException {
		assert repoName != null;
		return this.drFact.getNamedRepository(repoName, this.context);

	}
	
	/**
	 * Get a document repository.
	 * 
	 * Depending on the repository class's isReusable() method, this may
	 * return a fresh instance or a cached copy.
	 * 
	 * @return initialized document repository.
	 * @deprecated
	 */
	public DocumentRepository getRepository() 
			throws RhizomeInitializationException {
		// If we already have one, return it.
		if(this.repoInstance != null) return this.repoInstance;
		DocumentRepository repoInst;
		try {
			repoInst = (DocumentRepository)this.repositoryClass.newInstance();
			repoInst.setConfiguration(this.context);
		} catch (InstantiationException e) {
			String errmsg = "Cannot create object of class " 
				+ this.repositoryClass.getCanonicalName()
				+ "(Reason: " + e.getMessage() + ")";
			throw new RhizomeInitializationException(errmsg, e);
		} catch (IllegalAccessException e) {
			String errmsg = "Cannot create object of class " 
				+ this.repositoryClass.getCanonicalName()
				+ "(Reason: Illegal access." + e.getMessage() + ")";
			throw new RhizomeInitializationException(errmsg, e);
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
			indInst = (DocumentIndexer)this.indexerClass.newInstance();
			indInst.setConfiguration(this.context);
		} catch (Exception e) {
			String errmsg = "Cannot create object of class " 
				+ this.indexerClass.getCanonicalName()
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
			searchInst = (RepositorySearcher)this.searcherClass.newInstance();
			searchInst.setConfiguration(this.context);
		} catch (Exception e) {
			String errmsg = "Cannot create object of class " 
				+ this.searcherClass.getCanonicalName()
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
		StringBuilder sb = new StringBuilder();
		sb.append("Search: ");
		sb.append(this.searcherClass.getCanonicalName());
		sb.append("\nIndex: ");
		sb.append(this.indexerClass.getCanonicalName());
		sb.append("\nRepository: ");
		sb.append(this.repositoryClass.getCanonicalName());
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