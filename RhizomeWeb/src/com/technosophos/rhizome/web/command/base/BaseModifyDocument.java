package com.technosophos.rhizome.web.command.base;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.*;
//import com.technosophos.rhizome.repository.DocumentRepository;
//import com.technosophos.rhizome.repository.DocumentNotFoundException;
//import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.RhizomeException;

/**
 * Base class for document modifications.
 * 
 * Use this class as a basis for building commands that modify a document's contents.
 * 
 * This class is intentionally developed to be very flexible. With a little creative
 * engineering, you can use it to do "other things," too. Things like making a copy
 * of a document.
 * @author mbutcher
 *
 */
public abstract class BaseModifyDocument extends AbstractCommand {
	
	/**
	 * Modify the contents of a document.
	 * 
	 * This loads a document, modifies the content, and then writes the document
	 * to the repository.
	 * 
	 * In the event of an error, an error message is added to the CommandResults list.
	 * 
	 * If the modification succeeds, then the modified RhizomeDocument is added to the 
	 * CommandResults list, where it can be used by subsequent commands.
	 */
	protected void execute() throws ReRouteRequest {
	
		// Get the document -- need to know the doc param value
		
		RhizomeDocument doc;
		try {
			doc = this.getDocument();
		} catch (RhizomeException e) {
			String err = "Document could not be retrieved: " + e.getMessage();
			String ferr = "We could not find the document.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		}
		
		//String id = doc.getDocID();
		
		// Modify the document
		try {
			doc = this.modifyDocument(doc);
		} catch (RhizomeException e) {
			String err = "Document could not be modified: " + e.getMessage();
			String ferr = 
				"There was an error modifying your document. The document was not saved. Please try again.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		}
		
		// Store the document
		try {
			this.writeDocument(doc);
		} catch (RhizomeException e) {
			String err = "Document could not be written: " + e.getMessage();
			String ferr = 
				"There was an error saving your document. The document was not saved. Please try again.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		}
		
		// Add the doc to the command results.
		this.results.add(this.createCommandResult(doc));
	}
	
	/**
	 * This returns the document that will be modified.
	 * 
	 * This method should not modify the document's contents. Optionally, this method
	 * may check the document type to make sure it is correct.
	 * 
	 * If this method returns an empty document (one with a documentID that is not yet used
	 * in the repository), then the new document will be added instead of modified.
	 * 
	 * Optionally, this method may put an error in the CommandResults list, but this is
	 * not necessary. If this throws an exception, an error will be generated.
	 * 
	 * @return The document to be modified.
	 * @exception RhizomeException If the document ID cannot be found.
	 */
	protected abstract RhizomeDocument getDocument() throws RhizomeException;
	
	/**
	 * Write the modified document back to the repository.
	 * @param doc
	 * @throws RhizomeException
	 */
	protected abstract void writeDocument(RhizomeDocument doc) throws RhizomeException;
	
	/**
	 * Modify the document.
	 * 
	 * The primary use of this method is to make it possible to modify the 
	 * fields of the document.
	 * 
	 * But the document returned is the one written to the repository, which means you
	 * can do a few interesting things, copying a document by merely generating a different
	 * document ID and then returning that document.
	 * 
	 * @param doc
	 * @return Modified document. (The resulting document is the one written).
	 * @throws RhizomeException
	 */
	protected abstract RhizomeDocument modifyDocument(RhizomeDocument doc) throws RhizomeException;
	

}
