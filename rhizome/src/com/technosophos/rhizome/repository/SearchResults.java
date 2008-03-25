package com.technosophos.rhizome.repository;

import java.util.Map;

import com.technosophos.rhizome.document.DocumentList;


/**
 * Search results.
 * This utility class encapsulates search results.
 * @author mbutcher
 *
 */
public class SearchResults {
	protected String q;
	protected String[] names;
	protected Map<String,String> args;
	protected int maxResults, offset, numberOfResults;
	protected DocumentList docList;
	
	/**
	 * Use this whenever your search returns results.
	 * @param q Original query string
	 * @param names Names of metadata that may appear in returned documents
	 * @param args Arguments list passed to searcher
	 * @param maxResults Maximum number of results that can be returned at a time
	 * @param offset Offset from beginning of search
	 * @param numberOfResults Total number of results found by searcher
	 * @param docs The list of documents that was returned.
	 */
	public SearchResults(
			String q, 
			String[] names, 
			Map<String,String> args, 
			int maxResults, 
			int offset,
			int numberOfResults,
			DocumentList docs
	) {
		this.q = q;
		this.names = names;
		this.args = args;
		this.maxResults = maxResults;
		this.offset = offset;
		this.numberOfResults = numberOfResults;
		this.docList = docs;
	}
	
	/**
	 * ERROR CONSTRUCTOR. This is only used when no search results were found.
	 * @param q
	 * @param names
	 * @param args
	 */
	public SearchResults(String q, String[] names, Map<String,String> args, int maxResults, int offset) {		
		this(q, names, args, maxResults, offset, 0, new DocumentList());
	}
	
	protected SearchResults() {
		this("", new String[0], null, 25, 0, 0, new DocumentList());
	}
	
	// These are required for Velocity's introspection agent:
	public String getQuery() { return this.q; }
	public String[] getNames() {return this.names;}
	public Map<String, String> getArgs(){return this.args;}
	public int getMaxResults(){return this.maxResults;}
	public int getOffset(){return this.offset;}
	public int getTotalMatches(){return this.numberOfResults;}
	public DocumentList getDocumentList(){return this.docList;}
	
	
}
