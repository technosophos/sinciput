package com.technosophos.sinciput.commands;

import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.document.RhizomeParseException;
import com.technosophos.rhizome.repository.DocumentNotFoundException;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.sinciput.SinciputException;

public abstract class ViewDocument extends SinciputCommand {

	/** 
	 * Retrieve a note.
	 * Params:
	 * <ul>
	 * <li>doc: Document UUID (aka doc ID)</li>
	 * </ul>
	 */
	protected void execute() throws ReRouteRequest {
		String docID = this.getParam("doc", null).toString();
		if( docID == null ) {
			String ferr = "You must supply a document identifier before we can retrieve the requested information.";
			String err = "No 'doc' parameter found.";
			this.results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		String repoName = null;
		try {
			repoName = this.getCurrentRepository();
		} catch (RhizomeInitializationException e) {
			String ferr = "We could not open your repository for viewing.";
			String err = "Repo error:" + e.getMessage() ;
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} catch (SinciputException e) {
			String ferr = "We could not find your default repository. Are you logged in?";
			String err = "Sinciput error getting repo: " + e.getMessage() ;
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		}
		
		DocumentRepository repo = null;
		try {
			repo = this.repoman.getRepository(repoName);
		} catch (RhizomeInitializationException e) {
			String ferr = "We could not open the repository for reading. Try again later.";
			String err = "Initialization error getting repo: " + e.getMessage() ;
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		}
		
		try {
			if( ! repo.hasDocument(docID)) {
				String ferr = "You must supply a valid document identifier before we can retrieve the requested information.";
				String err = String.format("No document matching %s was found.", docID);
				this.results.add(this.createErrorCommandResult(err, ferr));
				return;
			}
			
			RhizomeDocument doc = repo.getDocument(docID);
			if( doc == null) {
				String ferr = "You must supply a valid document identifier before we can retrieve the requested information.";
				String err = String.format("BAD THING: Document matching %s was found, but is null.", docID);
				this.results.add(this.createErrorCommandResult(err, ferr));
				return;
			}
			//String type = NotesEnum.TYPE.getFieldDescription().getDefaultValue();
			//Metadatum m = doc.getMetadatum(NotesEnum.TYPE.getKey());
			if(!this.verifyDocument(doc)) {
				String ferr = "No information could be found.";
				String err = String.format("The document %s failed verification.", docID);
				this.results.add(this.createErrorCommandResult(err, ferr));
				return;
			}
			this.prepareDocument(doc);
		} catch (DocumentNotFoundException e) {
			String ferr = "No information could be found. Please try again.";
			String err = String.format("The document %s is not in the repository.", docID);
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} catch (RepositoryAccessException e) {
			String ferr = "The requested note could not be retrieved. It may exist, but we can't find it right now.";
			String err = String.format("The document %s is not in the repository: %s.", docID, e.getMessage());
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} catch (RhizomeParseException e) {
			String ferr = "Your note could not be read, probably because of an error when entering the note.";
			String err = String.format("The document %s cannot be parsed: %s.", docID, e.getMessage());
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		}

	}
	
	/**
	 * Verifies that the returned document is okay to return to the client.
	 * <p>Implementing classes should perform checks on the document to make sure that it should
	 * be returned to the client. Minimally, this function should make sure that the <i>type</i>
	 * of the document is correct. For example, we want to make sure the user is not retrieving a course
	 * document in a method designed to handle a note document.</p>
	 * @param doc Document retrieved from the repository. This is a candidate for return to the client.
	 * @return True if the document should be returned, false otherwise.
	 */
	protected abstract boolean verifyDocument(RhizomeDocument doc);
	
	/**
	 * Prepare and deliver the document.
	 * <p>This method is responsible for determining what to do with the returned document.
	 * It may perform processing, and it may insert zero or more results into the {@link CommandResult} set.
	 * </p>
	 * <p>By default, this method simply puts the document into the results and returns.</p>
	 * <p>For more sophisticated behavior, override this method.</p>
	 * @param doc Document retrieved from the repository and verified.
	 * @see verifyDocument(RhizomeDocument)
	 */
	protected void prepareDocument(RhizomeDocument doc) {
		this.results.add(this.createCommandResult(doc));
	}

}
