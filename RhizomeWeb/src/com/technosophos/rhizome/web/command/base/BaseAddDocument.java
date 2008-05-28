package com.technosophos.rhizome.web.command.base;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.*;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.DocumentNotFoundException;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.RhizomeException;

import java.util.Set;
import java.util.Map;

/**
 * This is a base class designed to make it easier to add new documents.
 * 
 * You can also subclass this to build commands that modify existing documents.
 * @author mbutcher
 *
 */
public abstract class BaseAddDocument extends AbstractCommand {

	/**
	 * Add a document to the repository.
	 * 
	 * This first builds a document (by calling {@link BaseAddDocument.buildDocument()},
	 * and then it attempts to add the document using 
	 * {@link BaseAddDocument.writeDocument()}.
	 * 
	 * If the addition fails, one or more errors are packed into the CommandResults list.
	 * 
	 * If the addition is successful, the document is packed into the CommandResults list,
	 * making it available for subsequent commands.
	 */
	protected void execute() throws ReRouteRequest {
		RhizomeDocument doc;
		try {
			doc = this.buildDocument();
		} catch (RhizomeException e) {
			String err = "Failed to build document: " + e.getMessage();
			String ferr = "The document was not saved.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		}
		
		try {
			this.writeDocument(doc);
		} catch(RhizomeException e) {
			String err = "Failed to write document: " + e.getMessage();
			String ferr = 
				"We could not write your document to the repository. Please try again later";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		}
		
		this.results.add(this.createCommandResult(doc));
	}
	
	/**
	 * This method is responsible for building a document.
	 * 
	 * It takes parameters from the local param map, does any checking on
	 * the parameters, and then creates a {@link RhizomeDocument} and returns
	 * it.
	 * 
	 * If an error occurs while building a document, the method should throw a 
	 * RhizomeException. However, if specific information should be returned to the user,
	 * that information needs to be loaded into the CommandResults list by the 
	 * buildDocument() method.
	 * 
	 * @return Initialized and populated RhizomeDocument.
	 */
	protected abstract RhizomeDocument buildDocument() throws RhizomeException;
	
	/**
	 * Write the document to the repository.
	 * @param doc
	 */
	protected abstract void writeDocument(RhizomeDocument doc) throws RhizomeException;
	
	/**
	 * Checks to make sure all required fields are in the params.
	 * 
	 * This should be called from buildDocument().
	 * 
	 * The {@link Map} should be composed as follows: The key is the paramter name,
	 * the value is the human-readable field name. In the event that the required
	 * parameter is not found, the user will be returned a message like this:
	 * <pre>The field '%s' is required.</pre>
	 * The %s will be replaced with the value from the map.
	 * @param fields
	 * @return
	 */
	protected boolean requireFields(Map<String, String> fields) {
		Set<String> keys = fields.keySet();
		boolean ret = true;
		for(String k: keys) {
			if(!this.params.containsKey(k)) {
				ret = false;
				String err = "Could not find param: " + k;
				String ferr = String.format("The field '%s' is required.", fields.get(k));
				this.results.add(this.createErrorCommandResult(err, ferr));
			}
		}
		return ret;
	}
	
	/**
	 * This is a utility method for adding a relation for this document.
	 * 
	 * It should be called from within buildDocument().
	 * 
	 * It does NOT add a {@link Relation} to the given {@link RhizomeDocument}. Instead,
	 * it opens the document for targetDocID and adds a new {@link Relation} to <i>that</i>
	 * object, writing it immediately to the database.
	 * 
	 * This can be called before the new document is written to the repository.
	 * 
	 * @param targetDocID Document ID for the document that this should be related to.
	 * @param doc The document we are adding.
	 * @param RelationName A string identifying the relationship type, e.g. 'parentOf'
	 * @throws DocumentNotFoundException if the targetDocID is not found.
	 * @throws RepositoryAccessException If the document cannot be retrieved.
	 * @throws RhizomeException If the target document cannot be written.
	 * @deprecated Use the {@link RelatedDocumentsHelper} class instead.
	 */
	protected void addRelation(String targetDocID, RhizomeDocument doc, String relationName, DocumentRepository repo) 
		throws DocumentNotFoundException, RepositoryAccessException, RhizomeException
	{
		if(!repo.hasDocument(targetDocID)) {
			throw new DocumentNotFoundException("AddRelation: Could not find " + targetDocID);
		}
		
		RhizomeDocument relDoc = repo.getDocument(targetDocID);
		relDoc.addRelation(relationName, doc.getDocID());
		this.repoman.storeDocument(repo.getRepositoryName(), relDoc);
	}
	
	/**
	 * Add a relationship to the target document.
	 * This does the same thing as the 
	 * {@link BaseAddDocument.addRelation(String, RhizomeDocument, String, DocumentRepository)}
	 * method, but it performs one additional test. It checks to make sure that the 
	 * target document has the "type" metadatum set, and that the first value for that
	 * metadatum is equal to targetDocType. This is a generic way of ensuring that the
	 * destination document is an appropriate document for relating.
	 * 
	 * @param targetDocID Document ID for the document that this should be related to.
	 * @param doc The document we are adding.
	 * @param RelationName A string identifying the relationship type, e.g. 'parentOf'
	 * @param targetDocType Type of destination document.
	 * @throws DocumentNotFoundException if the targetDocID is not found.
	 * @throws RepositoryAccessException If the document cannot be retrieved.
	 * @throws RhizomeException If the target document cannot be written or if target document
	 * is of the wrong type.
	 * @todo Should this use {@link RelatedDocumentsHelper}?
	 * @deprecated Use the {@link RelatedDocumentsHelper} class instead.
	 */
	protected void addRelation(String targetDocID, RhizomeDocument doc, String relationName, DocumentRepository repo, String targetDocType) 
		throws DocumentNotFoundException, RepositoryAccessException, RhizomeException
	{
		if(!repo.hasDocument(targetDocID)) {
			throw new DocumentNotFoundException("AddRelation: Could not find " + targetDocID);
		}
		
		RhizomeDocument relDoc = repo.getDocument(targetDocID);
		if(!targetDocType.equals(relDoc.getMetadatum("type").getFirstValue())) {
			throw new RhizomeException("Target RhizomeDocument is of the wrong type.");
		}
		relDoc.addRelation(relationName, doc.getDocID());
		this.repoman.storeDocument(repo.getRepositoryName(), relDoc);
	}

}
