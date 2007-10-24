package com.technosophos.sinciput.sru;

import java.util.ArrayList;
//import java.util.List;

/**
 * Create Context-oriented Queries in CQL.
 * <p>Context Query Language is a simple syntax for describing a query.
 * This language is used by SRU/SRW.</p>
 * <p>This class provides a fluent CQL interface. It supports
 * construction of CQL 1.2, levels 0, 1, and 2 (though full level two may require more sophisticated use
 * of this API). These classes can be used with CQL 1.1, but be aware of the fact that many of the 
 * constants (and the {@link exactMatch(String, String)}) method are designed with CQL 1.2 in mind.</p>
 * <p>This release does not contain a syntax checker, though this is a feature that may be added in 
 * later releases.</p>
 * @author mbutcher
 * @see http://www.loc.gov/standards/sru/specs/cql.html
 */
public class CQL {
	/*
	 * The internals to this class are nothing fancy, and could probably use an overhaul.
	 * Basically, we store clauses and booleans as strings in an array list. This makes
	 * the process of building a query fairly easy (to implement), but perhaps it makes
	 * the query less mutable.
	 */
	
	/** 
	 * Known index, supported by DC (Dublin Core) servers. 
	 * <p>The Dublin Core index constants provided here are those used by the US Library 
	 * of Congress and commonly implemented on other SRU servers. Other constants are 
	 * listed at the URL below.</p>
	 * @see http://www.loc.gov/standards/sru/resources/dc-context-set.html
	 */
	public static final String INDEX_TITLE = "dc.title";
	/** Known index, supported by DC (Dublin Core) servers. */
	public static final String INDEX_SUBJECT = "dc.subject";
	/** Known index, supported by DC (Dublin Core) servers. */
	public static final String INDEX_CREATOR = "dc.creator";
	/** Known index, supported by DC (Dublin Core) servers. */
	public static final String INDEX_AUTHOR = "dc.author";
	/** Known index, supported by DC (Dublin Core) servers. */
	public static final String INDEX_EDITOR = "dc.editor";
	/** Known index, supported by DC (Dublin Core) servers. */
	public static final String INDEX_PUBLISHER = "dc.publisher";
	/** Known index, supported by DC (Dublin Core) servers. */
	public static final String INDEX_DESCRIPTION = "dc.description";
	/** Known index, supported by DC (Dublin Core) servers. */
	public static final String INDEX_DATE = "dc.date";
	
	/** Known index, supported by Bath (Library of Congress) servers. */
	public static final String INDEX_ISBN = "bath.isbn";
	/** Known index, supported by Bath (Library of Congress) servers. */
	public static final String INDEX_ISSN = "bath.issn";
	/** Known index, supported by Bath (Library of Congress) servers. */
	public static final String INDEX_NAME = "bath.name";
	/** Known index, supported by Bath (Library of Congress) servers. */
	public static final String INDEX_LCCN = "bath.lccn";
	/**
	 * Search keywords lists.
	 */
	public static final String INDEX_KEYWORD = "cql.keywords";
	/**
	 * Search any applicable index. Server decides what's applicable.
	 */
	public static final String INDEX_ANYINDEXES = "cql.anyIndexes";
	/**
	 * Search all indexes.
	 */
	public static final String INDEX_ALLINDEXES = "cql.allIndexes";
	
	/**
	 * Relation: If <i>any</i> of the terms match... (OR)
	 */
	public static final String REL_ANY = "any";
	/**
	 * Relation: Server-interpreted loose equality.
	 */
	public static final String REL_EQUALS = "=";
	/**
	 * Relation: Server choice. (1.1 only?)
	 */
	public static final String REL_SCR = "scr";
	/**
	 * Relation: Exact match
	 */
	public static final String REL_EXACT_MATCH = "==";
	/**
	 * Within a range, inclusively. Example "date within 2004 2006"
	 */
	public static final String REL_WITHIN = "within";
	/**
	 * Relation: Negation of exact match.
	 */
	public static final String REL_NOT_EQUAL = "<>";
	/**
	 * Relation: All words must be adjacent.
	 */
	public static final String REL_ADJACENT = "adj";
	/**
	 * All words must be present. (AND)
	 */
	public static final String REL_ALL = "all";
	/**
	 * Relation: Given boundaries enclose the given value
	 * @see REL_WITHIN
	 */
	public static final String REL_ENCLOSES = "encloses";
	
	public static final String RELMOD_STEM = "stem";
	public static final String RELMOD_RELEVANT = "relevant";
	public static final String RELMOD_PHONETIC = "phonetic";
	public static final String RELMOD_FUZZY = "fuzzy";
	public static final String RELMOD_PARTIAL = "partial";
	public static final String RELMOD_IGNORECASE = "ignorecase";
	public static final String RELMOD_RESPECTCASE = "respectcase";
	public static final String RELMOD_IGNOREACCENT = "ignoreaccent";
	public static final String RELMOD_RESPECTACCENT = "respectaccent";
	//public static final String RELMOD_LOCALE = "locale=";
	
	public static final String TERMMOD_WORD = "word";
	public static final String TERMMOD_STRING = "string";
	public static final String TERMMOD_ISODATE = "isoDate";
	public static final String TERMMOD_NUMBER = "number";
	public static final String TERMMOD_URI = "uri";
	public static final String TERMMOD_OID = "oid";
	public static final String TERMMOD_UNMASKED = "unmasked";
	public static final String TERMMOD_REGEXP = "regexp";
	public static final String TERMMOD_SUBSTRING = "substring";
	
	/**
	 * Constant defining descending sort order.
	 * @see sortBy(String, String...)
	 */
	public static final String SORT_DESCENDING = "sort.descending";
	/**
	 * Constant defining ascending sort order.
	 * @see sortBy(String, String...)
	 */
	public static final String SORT_ASCENDING = "sort.ascending";
	
	private ArrayList<String> stack;
	
	/**
	 * Get a new instance of a CQL object.
	 * <p>This can be used as an alternative to a constructor. It better suites the 
	 * fluent model. Example:</p>
	 * <code>
	 * CQL c = CQL.query().x().y().z();
	 * </code>
	 * <p></p>
	 * @return Newly-created empty query.
	 */
	public static CQL query() {
		return new CQL();
	}
	/**
	 * Convenience method for constructing a new CQL query and assigning a clause.
	 * @see clause(String)
	 * @param term
	 * @return
	 */
	public static CQL query(String term) {
		CQL c = new CQL();
		return c.clause(term);
	}
	/**
	 * Convenience method for constructing a new CQL query and assigning a clause.
	 * @see clause(String, String)
	 * @param term
	 * @return
	 */
	public static CQL query(String index, String term) {
		CQL c = new CQL();
		return c.clause(index, term);
	}
	/**
	 * Convenience method for constructing a new CQL query and assigning a clause.
	 * @see clause(String, String, String)
	 * @param term
	 * @return
	 */
	public static CQL query(String index, String term, String relation) {
		CQL c = new CQL();
		return c.clause(index, term, relation);
	}
	
	/**
	 * Create a new CQL query object.
	 * <p>If you prefer a fluent feel, you may wish to use {@see query()} instead
	 * of a traditional constructor.</p>
	 */
	public CQL() {
		this.stack = new ArrayList<String>();
	}
	
	// SIMPLE TERM
	
	/**
	 * Add a term. Used on its own, this functions identically to the {@link clause(String term)}
	 * method.
	 */
	public CQL term(String str) {
		stack.add(this.prepareTerm(str));
		return this;
	}
	
	/*
	public CQL index(String index) {
		stack.add(index);
		return this;
	}
	
	public CQL relation(String relation) {
		stack.add(relation);
		return this;
	}
	*/
	
	/**
	 * Simple identity match. To match, a candidate must exactly equal the given term.
	 * <h2>Warning: This is a 1.2 Feature only!</h2>
	 * <p>While this is not specified in the CQL documentation, CQL servers running 1.1 or earlier will
	 * not recognize strong equality operators. At the time of this writing, US LOC does not support 1.2.</p>
	 * <hr/>
	 * <p>Since this is one of the most often used comparisons, it is provided here for convenience.
	 * <p>Constructs a string like "dc.title == Beowulf".</p>
	 * @param index Index to search (see INDEX_*)
	 * @param term Term to match
	 * @return
	 */
	public CQL exactMatch(String index, String term) {
		stack.add(String.format("%s == \"%s\"", index, term));
		return this;
	}
	
	// CLAUSES
	
	/**
	 * Add just a single term clause.
	 * <p>Example: "Lilith"</p>
	 */
	public CQL clause(String term) {
		return this.term(term);
	}
	
	/**
	 * This uses general equality to match the given string using the given index.
	 * @param index Index to searchg (See INDEX_*, though others are accepted.)
	 * @param term Term to search for.
	 * @return
	 */
	public CQL clause(String index, String term) {
		stack.add(String.format("%s = %s", index, this.prepareTerm(term)));
		return this;
	}
	/**
	 * Add a clause.
	 * Clauses here are of the form "index relation term". Example: "dc.title adj 'Mastering OpenLDAP'"
	 * @param index An index. For example, any of the INDEX_* constants should work here.
	 * @param term A search term
	 * @param relation Any relation. See REL_* and RELMOD_* above. Also, grater than, less, than, == and so on work.
	 * @return
	 */
	public CQL clause(String index, String term, String relation) {
		StringBuilder sb = new StringBuilder();
		sb.append(index).append(' ')
		  .append(relation).append(' ')
		  .append(this.prepareTerm(term));
		
		stack.add(sb.toString());
		return this;
	}
	
	/**
	 * Add a clause.
	 * <p>Clauses here are of the form "index relation term". Example: "dc.title adj 'Mastering OpenLDAP'"
	 * </p>
	 * <p><b>Relation Modifiers:</b> Any of the RELMOD_ and TERMMOD_ values are good candidates for
	 * a relation modifier. Additionally, servers may support other relation or term modifiers.</p>
	 * @param index An index. For example, any of the INDEX_* constants should work here.
	 * @param term A search term
	 * @param relation Any relation. See REL_* above. Also, greater than, less, than, == and so on work.
	 * @param relationModifier One or more relation modifiers. Example: "relevant"
	 * @return
	 */
	public CQL clause(String index, String term, String relation, String... relationModifier) {
		StringBuilder sb = new StringBuilder();
		sb.append(index).append(' ');
		sb.append(relation);
		for(String r: relationModifier) sb.append('/').append(r);
		sb.append(' ');
		sb.append(this.prepareTerm(term));
		
		stack.add(sb.toString());
		return this;
	}
	
	// SUBQUERIES
	/**
	 * Add an existing CQL query as a subquery.
	 * This will actually render the subquery and insert the resulting CQL string into this context.
	 * Parenthesis will surround the subquery. It should be used mainly for grouping.
	 * 
	 */
	public CQL subquery(CQL subq) {
		this.stack.add("(" + subq.toString() + ")");
		return this;
	}
	
	// OPERATORS
	
	public CQL and() {
		this.stack.add("and");
		return this;
	}
	
	public CQL andNot() {
		this.stack.add("not");
		return this;
	}
	
	public CQL or() {
		this.stack.add("or");
		return this;
	}
	
	public CQL prox() {
		this.stack.add("prox");
		return this;
	}
	
	/**
	 * A fully-specified proximity operator.
	 * <p>This gives you the ability to add a proximity context and unit keyword. For
	 * example, prox("xyz", "sentence") will generate CQL equivalent to "prox/xyz.unit=sentence".
	 * @param context The CQL-specific context.
	 * @param unit The unit (Often one of word, sentence, paragraph or line)
	 * @return
	 */
	public CQL prox(String context, String unit) {
		this.stack.add(String.format("prox/%s.unit=%s", context, unit));
		return this;
	}
	
	// SORTING
	/**
	 * Add one or more sorting criteria.
	 * <p>Example: sortBy(INDEX_TITLE, INDEX_AUTHOR) would result in CQL that looked
	 * something like this: "sortBy dc.title/sort.descending dc.author/sort.descending"</p>
	 */
	public CQL sortBy(String... sort) {
		this.stack.add("sortBy");
		for(String s: sort) this.stack.add(String.format("%s/sort.descending", s));
		return this;
	}
	/**
	 * 
	 * @param ordering The ordering. All CQL apps do at least sort.descending and sort.ascending.
	 * @param sort One or more indexes to sort.
	 * @return
	 */
	public CQL sortBy(String ordering, String... sort) {
		this.stack.add("sortBy");
		for(String s: sort) this.stack.add(String.format("%s/sort.%s", s, ordering));
		return this;
	}
	
	// PREFIXING
	public CQL prefix(String URI) {
		this.stack.add(String.format("> %s", URI));
		return this;
	}
	
	public CQL prefix(String URI, String shortname) {
		this.stack.add(String.format("> %s = %s", shortname, URI));
		return this;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		for(String s: this.stack) {
			sb.append(s).append(' ');
		}
		return sb.toString();
	}
	
	/**
	 * Prepare a string for inclusing in CQL.
	 * @param term
	 * @return
	 */
	protected String prepareTerm(String term) {
		StringBuilder sb = new StringBuilder();
		char[] cs = term.toCharArray();
		sb.append('"');
		for(char c: cs) {
			if(c == '"') sb.append('\\').append('"');
			else sb.append(c);
		}
		sb.append('"');
		return sb.toString();
	}
	
}
