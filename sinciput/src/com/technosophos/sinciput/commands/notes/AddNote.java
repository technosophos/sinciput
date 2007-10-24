package com.technosophos.sinciput.commands.notes;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.*;
import com.technosophos.rhizome.repository.DocumentNotFoundException;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.sinciput.SinciputException;
//import com.technosophos.sinciput.types.CourseEnum;
import com.technosophos.sinciput.types.NotesEnum;
import com.technosophos.sinciput.commands.SinciputCommand;
import com.technosophos.sinciput.util.Scrubby;

import static com.technosophos.sinciput.servlet.ServletConstants.*;
/*
import com.technosophos.rhizome.repository.util.RepositoryUtils;
import com.technosophos.sinciput.SinciputException;
import com.technosophos.sinciput.servlet.SinciputSession;
import com.technosophos.sinciput.types.admin.RepoDescriptionEnum;
import com.technosophos.rhizome.command.AbstractCommand;
//import com.technosophos.rhizome.repository.DocumentNotFoundException;
//import com.technosophos.sinciput.types.NotesEnum;
 */

/**
 * Add a note to a given repository.
 * <p>At the heart of Sinciput is the note. This command is used for taking user input
 * and creating a new note in a given repository.</p>
 * <p>This default addNote class assumes that the note is in HTML. For other body types, you
 * can simply override {@link prepareBody(RhizomeDocument)}.</p>
 * <p>What this object expects in parameters:</p>
 * <ul>
 * <li>title: Title of the note</li>
 * <li>subtitle: Subtitle of the note</li>
 * <li>tag(s): Zero or more tags</li>
 * <li>body: The text of the note</li>
 * </ul> 
 * <p>Additionally, it expects to be able to fetch a username from the session.</p>
 * @author mbutcher
 *
 */
public class AddNote extends SinciputCommand {
	
	public final static String NOTE_BODY = "body";
	public final static String NOTE_PARENT_DOCID = "parent";
	public final static String SINCIPUT_PARENT_RELATION = "parentOf";

	/**
	 * Store the note as a document in the repository.
	 */
	public void execute() throws ReRouteRequest {

		// Get user from session.
		String uname = this.ses.getUserName();
		String userid = this.ses.getUserUUID();
		String repoName;
		RepositorySearcher s_search;
		DocumentRepository s_repo;
		
		if(uname == null || userid == null) {
			String err = "No user object!";
			String ferr = "We can not verify the user ID at this time. Are you logged in?";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}


		try {
			s_search = this.repoman.getSearcher(SETTINGS_REPO);
			s_repo = this.repoman.getRepository(SETTINGS_REPO);
		} catch (RhizomeInitializationException e1) {
			String err = "Failed to initialize searcher and repo: " + e1.getMessage();
			String ferr = "Our system cannot initialize your repository. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Get repository
		try {
			repoName = this.getCurrentRepository(s_search);
		} catch (SinciputException e) {
			String err = "Repository not found: " + e.getMessage();
			String ferr = "Our system cannot find your repository. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		if(repoName == null)  return; // This should be cleaner... fix SinciputCommand
		
		// Check write access to repo
		if( ! this.userCanWriteRepo(s_repo)) {
			String err = "No write permissions to " + repoName;
			String ferr = "You are not allowed to write notes in this repository.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Create required fields
		RhizomeDocument doc = new RhizomeDocument(DocumentID.generateDocumentID());
		
		// Fetch optional fields
		// - get all fields
		String title = this.getFirstParam(NotesEnum.TITLE.getKey(), "Untitled").toString();
		String subtitle = this.getFirstParam(NotesEnum.SUBTITLE.getKey(), "").toString();
		Object tags = this.getParam(NotesEnum.TAG.getKey(), null);
		
		// - find out how tags are stored
		String[] ta;
		if( tags == null) {
			ta = new String[0];
		} else if( tags instanceof String[] ) {
			 ta = (String[])tags;
			
		} else ta = new String[]{ tags.toString() };
		
		// - clean fields
		title = Scrubby.cleanText(title);
		subtitle = Scrubby.cleanText(subtitle);
		int i, j = ta.length;
		for( i = 0; i < j; ++i) {
			ta[i] = ta[i] != null ? Scrubby.cleanText(ta[i]) : "Empty";
		}
		// - put em in metadata objects:
		doc.addMetadatum(new Metadatum(NotesEnum.TITLE.getKey(), title));
		doc.addMetadatum(new Metadatum(NotesEnum.SUBTITLE.getKey(), subtitle));
		doc.addMetadatum(new Metadatum(NotesEnum.TAG.getKey(), ta));
		
		// - set automatic fields
		String time = com.technosophos.rhizome.util.Timestamp.now();
		doc.addMetadatum(new Metadatum(NotesEnum.TYPE.getKey(), 
				NotesEnum.TYPE.getFieldDescription().getDefaultValue()));
		doc.addMetadatum(new Metadatum(NotesEnum.CREATED_ON.getKey(), time ));
		doc.addMetadatum(new Metadatum(NotesEnum.LAST_MODIFIED.getKey(), time));
		doc.addMetadatum(new Metadatum(NotesEnum.CREATED_BY.getKey(), uname ));
		doc.addMetadatum(new Metadatum(NotesEnum.MODIFIED_BY.getKey(), uname ));

		// Do the body:
		String body = this.getFirstParam(NOTE_BODY, "").toString();
		
		try {
			this.prepareBody(body, doc);
		} catch (SinciputException se) {
			String err = String.format("HTML in \"%s\" was bad: %s", title, se.getMessage());
			String ferr = String.format("We encountered a problem when safety-checking %s.", title);
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// - Store in repo
		try {
			this.repoman.storeDocument(repoName, doc);
		} catch (RhizomeException e) {
			String err = String.format("Could not store \"%s\" in %s.", title, repoName);
			String ferr = String.format("We could not store \"%s\" in your repository.", title);
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// - Check for relations:
		this.addNoteToParent(doc, repoName);
		
		// Might as well pass the doc on
		this.results.add(this.createCommandResult(doc));
	}
	
	/**
	 * Insert the body into the document.
	 * This method assumes that the body is HTML (or, rather RHTML). It is safe to 
	 * override this method to perform other processing (including setting the text type)
	 * on the text, and then storing it in the document.
	 * @param body The body text, as extracted from params. No cleaning has been done, yet.
	 * @param doc The document that will hold the note.
	 */
	protected void prepareBody( String body, RhizomeDocument doc ) throws SinciputException {
		// 1. Do cleaning
		body = Scrubby.cleanSafeHTML(body);
		
		// 2. Set the type and store the document
		doc.setBody("text/html", body);
	}
	
	protected void addNoteToParent( RhizomeDocument doc, String repoName ) {
		String parentID = this.getFirstParam(NOTE_PARENT_DOCID, "").toString();
		if( parentID.length() > 0 ) {
			System.err.println("Adding note to parent.");
			try {
				// Every line here throws one or more exceptions:
				DocumentRepository repo = this.repoman.getRepository(repoName);
				RhizomeDocument parent = repo.getDocument(parentID);
				parent.addRelation(new Relation(SINCIPUT_PARENT_RELATION, doc.getDocID()));
				this.repoman.storeDocument(this.getCurrentRepository(), parent);
			} catch (DocumentNotFoundException e) {
				String  em = "Parent document not found: " + e.getMessage();
				String fem = "The document was created, but we can't relate it correctly.";
				this.results.add(this.createErrorCommandResult(em, fem, e));
				return;
			} catch (RepositoryAccessException e) {
				String  em = "Accessing repo to find parent failed: " + e.getMessage();
				String fem = "The document was created, but we can't relate it correctly.";
				this.results.add(this.createErrorCommandResult(em, fem, e));
				return;
			} catch (RhizomeParseException e) {
				String  em = "The parent document is not valid: " + e.getMessage();
				String fem = "The document was created, but we can't relate it correctly.";
				this.results.add(this.createErrorCommandResult(em, fem, e));
				return;
			} catch (RhizomeException e) {
				String  em = "The parent document could not be saved: " + e.getMessage();
				String fem = "The document was created, but we can't relate it correctly.";
				this.results.add(this.createErrorCommandResult(em, fem, e));
				return;
			}
			System.err.println("Done adding note to parent.");
		}
	}

}
