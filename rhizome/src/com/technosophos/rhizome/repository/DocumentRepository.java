package com.technosophos.rhizome.repository;

import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.document.RhizomeParseException;

/**
 * A document repository provides access to document storage.
 * 
 * Documents in Rhizome are stored in a repository -- a backend storage
 * mechanism designed for retrieval of a document by name.
 * 
 * A document repository supports three basic operations:
 * 1. Document retrieval (given an ID, a RhizomeDocument is returned)
 * 2. Document Storage (given a RhizomeDocument, the document is stored)
 * 3. Modification  
 * 
 * This defines the public interface for a document repository. All
 * document repository implementations must implement this interface.
 * 
 * @author mbutcher
 *
 */
public interface DocumentRepository {
	
	/**
	 * Set the object that will contain configuration information.
	 * @param ctx
	 */
	public void setConfiguration(RepositoryContext ctx) 
		throws RhizomeInitializationException;
	
	/**
	 * Get the object that contains the configuration info that this 
	 * object is currently using.
	 * @return
	 */
	public RepositoryContext getConfiguration();
	
	/**
	 * Get a RhizomeDocument, given a document ID.
	 * @param docID
	 * @return
	 */
	public RhizomeDocument getDocument(String docID) 
			throws DocumentNotFoundException, RepositoryAccessException, RhizomeParseException;
	
	/**
	 * Get a document as a raw (unparsed) Input Stream
	 * @param docID
	 * @return
	 */
	public java.io.InputStream getRawDocument(String docID) 
			throws DocumentNotFoundException, RepositoryAccessException;
	
	/**
	 * Tests to see if a document exists.
	 * 
	 * Returns true if a document with docID exists.
	 * @param docID
	 * @return
	 */
	public boolean hasDocument(String docID) throws RepositoryAccessException;
	
	/**
	 * Stores a RhizomeDocument.
	 * <p>
	 * If the document exists, this will overwrite the document. If the 
	 * document does not exist, this will create a new entry in the 
	 * repository.
	 * </p><p>
	 * The document ID is extracted from doc.getDocumentID(). If no document ID is found,
	 * a new one is generated.
	 * </p><p>
	 * This should usually be synchronized.
	 * </p>
	 * @param doc document to store
	 * @return the document ID.
	 */
	public String storeDocument(RhizomeDocument doc) throws RepositoryAccessException;
	
	/**
	 * Stores a RhizomeDocument.
	 * <p>
	 * If <code>overwrite</code> is true and 
	 * the document exists, this will overwrite the document. If the 
	 * document does not exist, this will create a new entry in the 
	 * repository.
	 * </p><p>
	 * If <code>overwrite</code> is false and the document exists, a RepositoryDocumentExistsException
	 * will be thrown.
	 * </p><p>
	 * The document ID is extracted from doc.getDocumentID(). If no document ID is found,
	 * a new one is generated.
	 * </p><p>
	 * This should usually be synchronized.
	 * </p>
	 * 
	 * @param doc
	 * @param overwrite
	 * @return
	 * @throws RepositoryAccessException, DocumentExistsException
	 */
	public String storeDocument(RhizomeDocument doc, boolean overwrite) 
		throws RepositoryAccessException, DocumentExistsException;
	
	/**
	 * Returns the number of documents in the repository.
	 * @return
	 */
	public long countDocumentIDs() throws RepositoryAccessException;
	
	/**
	 * Get an array containing all document IDs.
	 * 
	 * This should be used to safely pick a point to do a backup.
	 * @return
	 */
	public String[] getAllDocumentIDs() throws RepositoryAccessException;
	
	/**
	 * Removes a document from the repository.
	 * 
	 * <p>The exact handling of this is implementation-depedant, but in general, 
	 * once a document is removed, it should no longer be returned by any of the 
	 * methods defined in this interface.</p>
	 * 
	 * @param docID
	 * @return
	 * @throws RepositoryAccessException
	 */
	public boolean removeDocument(String docID) throws RepositoryAccessException;
	
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
