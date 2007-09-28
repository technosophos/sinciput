package com.technosophos.sinciput.commands.notes;

import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeParseException;
import com.technosophos.rhizome.repository.DocumentNotFoundException;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.sinciput.SinciputException;
import com.technosophos.sinciput.types.NotesEnum;
import com.technosophos.sinciput.commands.SinciputCommand;

/**
 * A command to display a note.
 * Givena  UUID for a note, this will attempt to retrieve the note object.
 * @author mbutcher
 *
 */
public class ViewNote extends SinciputCommand {

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
			String ferr = "You must supply a document identifier before we can retrieve the note.";
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
				String ferr = "You must supply a document identifier before we can retrieve the note.";
				String err = "No 'doc' parameter found.";
				this.results.add(this.createErrorCommandResult(err, ferr));
				return;
			}
			
			RhizomeDocument doc = repo.getDocument(docID);
			String type = NotesEnum.TYPE.getFieldDescription().getDefaultValue();
			Metadatum m = doc.getMetadatum(NotesEnum.TYPE.getKey());
			if( m == null || !m.hasValue(type)) {
				String ferr = "No matching note could be found.";
				String err = String.format("The document %s is not a note.", docID);
				this.results.add(this.createErrorCommandResult(err, ferr));
				return;
			}
			this.results.add(this.createCommandResult(doc));
		} catch (DocumentNotFoundException e) {
			String ferr = "No matching note could be found.";
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

}
