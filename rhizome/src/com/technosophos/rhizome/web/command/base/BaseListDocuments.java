package com.technosophos.rhizome.web.command.base;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.RhizomeException;

/**
 * Get a list of documents.
 * @author mbutcher
 *
 */
public abstract class BaseListDocuments extends AbstractCommand {

	/**
	 * Retrieve a list of documents.
	 * Connect to the repository, get the documents, and return a DocumentList.
	 * @return List of documents.
	 * @throws RhizomeException If anything goes wrong.
	 */
	protected abstract DocumentList getDocumentList() throws RhizomeException;
	
	protected void execute() throws ReRouteRequest {
		try {
			DocumentList docs = this.getDocumentList();
			this.results.add(this.createCommandResult(docs));
		} catch (RhizomeInitializationException e) {
			String err = "Failed to Initialize Storage";
			String ferr = "Could not find projects.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		} catch (RepositoryAccessException e) {
			String err = "Failed to access documents";
			String ferr = "An error occured while getting your documents.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		} catch(RhizomeException e) {
			String err = "Failed to getDocumentList(): " + e.getMessage();
			String ferr = "We could not retrieve a listing at this time. Please try again later.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		}
	}

}
