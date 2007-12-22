package com.technosophos.sinciput.commands;

import java.util.Map;

import com.technosophos.rhizome.controller.ReRouteRequest;
//import com.technosophos.rhizome.document.DocumentCollection;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.sinciput.SinciputException;

public abstract class ListDocuments extends SinciputCommand {
	
	/**
	 * This method returns a Map that serves as a narrower.
	 * <p>A narrower is used by the {@link RepositorySearcher.narrowingSearch(Map<String, String>, String[]) }
	 * method to query the repository for a {@link DocumentCollection} of matching 
	 * documents.</p>
	 * <p>When this command is {@link execute()}ed, the repository query is composed of the fields in this
	 * narrower. For each item in the narrower, the key will be searched for, and the resulting document ID
	 * will be added to the document collection <i>only if</i> the values match.</p>
	 * <p>When multiple values are supplied in the returned {@link Map}, then an ANDing search will 
	 * be performed.</p> 
	 * 
	 * @return a {@link Map} of filter name/value pairs.
	 * @see RepositorySearcher.narrowingSearch(Map<String, String>, String[])
	 */
	protected abstract Map<String, String> narrower();
	
	/**
	 * This method returns a String[] of values that should be returned from a search.
	 * <p>In addition to the fields in the Map created by {@link narrower()} these fields
	 * will also be returned in the {@link DocumentCollection}.</p>
	 * 
	 * @see RepositorySearcher.narrowingSearch(Map<String, String>, String[])
	 * @return An array of additional fields that should be returned.
	 */
	protected String[] additionalMetadata() {
		return new String[0];
	}
	
	/**
	 * Sort the returned document collection.
	 * <p>By default this does nothing.</p>
	 * @param col
	 */
	protected void sortResults(DocumentList dl) {
		return;
	}
	
	/**
	 * List Documents.
	 * Fetch a {@link DocumentCollection} in the given repository.
	 */
	protected void execute() throws ReRouteRequest {
		
		// Setup initial data
		Map<String, String> narrower = this.narrower();
		
		String[] additional_md = this.additionalMetadata();
		
		RepositorySearcher s = null;
		//DocumentCollection col = null;
		DocumentList dl = null;
		
		// Create searcher and do search:
		try {
			s = this.repoman.getSearcher(this.getCurrentRepository());
			//col = s.narrowingSearch(narrower, additional_md);
			dl = s.fetchDocumentList(narrower, additional_md, this.repoman.getRepository(this.getCurrentRepository()));
		} catch (RhizomeInitializationException e) {
			String err = "Failed to initialize: " + e.getMessage();
			String ferr = "We could not retrieve your list. Try again later.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		} catch (SinciputException e) {
			String err = "Failed with SinciputException. That's odd. " + e.getMessage();
			String ferr = "We encountered a general error. Please try again later.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		} catch (RepositoryAccessException e) {
			String err = "Error accessing repository: " + e.getMessage();
			String ferr = "We could not access your repository. Please try again later.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		}
		
		// What did we get back?
		if( dl == null ) {
			// Handle empty collection...
			String err = "Search produced NULL return value. That was not expected.";
			String ferr = "No items were found.";
			this.results.add(this.createErrorCommandResult(err, ferr));
		}
		
		this.sortResults(dl);
		
		this.results.add(this.createCommandResult(dl));
	}
}
