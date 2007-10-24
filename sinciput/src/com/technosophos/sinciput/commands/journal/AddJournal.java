package com.technosophos.sinciput.commands.journal;

import static com.technosophos.sinciput.servlet.ServletConstants.SETTINGS_REPO;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.DocumentID;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.sinciput.SinciputException;
import com.technosophos.sinciput.commands.SinciputCommand;
import com.technosophos.sinciput.types.JournalEnum;
import com.technosophos.sinciput.util.Scrubby;

/**
 * Add a journal to a given repository.
 * 
 * <p>What this object expects in parameters:</p>
 * <ul>
 * <li>title: Title of the journal</li>
 * <li>description: Description of the journal</li>
 * <li>tag(s): Zero or more tags</li>
 * </ul> 
 * <p>Additionally, it expects to be able to fetch a username from the session.</p>
 * @author mbutcher
 *
 */
public class AddJournal extends SinciputCommand {

	protected void execute() throws ReRouteRequest {
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
		String title = this.getFirstParam(JournalEnum.TITLE.getKey(), "Untitled").toString();
		String description = this.getFirstParam(JournalEnum.DESCRIPTION.getKey(), "").toString();
		Object tags = this.getParam(JournalEnum.TAG.getKey(), null);
		
		// - find out how tags are stored
		String[] ta;
		if( tags == null) {
			ta = new String[0];
		} else if( tags instanceof String[] ) {
			 ta = (String[])tags;
			
		} else ta = new String[]{ tags.toString() };
		
		// - clean fields
		title = Scrubby.cleanText(title);
		description = Scrubby.cleanText(description);
		int i, j = ta.length;
		for( i = 0; i < j; ++i) {
			ta[i] = ta[i] != null ? Scrubby.cleanText(ta[i]) : "Empty";
		}
		// - put em in metadata objects:
		doc.addMetadatum(new Metadatum(JournalEnum.TITLE.getKey(), title));
		doc.addMetadatum(new Metadatum(JournalEnum.DESCRIPTION.getKey(), description));
		doc.addMetadatum(new Metadatum(JournalEnum.TAG.getKey(), ta));
		
		// - set automatic fields
		String time = com.technosophos.rhizome.util.Timestamp.now();
		doc.addMetadatum(new Metadatum(JournalEnum.TYPE.getKey(), 
				JournalEnum.TYPE.getFieldDescription().getDefaultValue()));
		doc.addMetadatum(new Metadatum(JournalEnum.CREATED_ON.getKey(), time ));
		doc.addMetadatum(new Metadatum(JournalEnum.LAST_MODIFIED.getKey(), time));
		doc.addMetadatum(new Metadatum(JournalEnum.CREATED_BY.getKey(), uname ));
		doc.addMetadatum(new Metadatum(JournalEnum.MODIFIED_BY.getKey(), uname ));

		// Do the body:
		/*
		String body = this.getFirstParam(JOURNAL_BODY, "").toString();
		
		try {
			this.prepareBody(body, doc);
		} catch (SinciputException se) {
			String err = String.format("HTML in \"%s\" was bad: %s", title, se.getMessage());
			String ferr = String.format("We encountered a problem when safety-checking %s.", title);
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		*/
		
		// - Store in repo
		try {
			this.repoman.storeDocument(repoName, doc);
		} catch (RhizomeException e) {
			String err = String.format("Could not store \"%s\" in %s.", title, repoName);
			String ferr = String.format("We could not store \"%s\" in your repository.", title);
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Might as well pass the doc on
		this.results.add(this.createCommandResult(doc));
	}

}
