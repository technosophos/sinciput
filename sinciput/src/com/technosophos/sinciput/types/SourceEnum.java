package com.technosophos.sinciput.types;

/**
 * Enumeration for handling sources.
 * <p>A source, in Sinciput, is any sort of resource that can be treated as a media source.
 * Typically, this is books, periodicals, journals, articles, and so on. However, musical
 * scores and recordings should also be (roughly) sources.</p>
 * <p>The structure of a source is largely influenced by two factors: (a) Bibliographic needs, 
 * and (b) the structure of MODS records, as defined by the US Library of Congress. MODS is 
 * not as comprehensive as MARC, but it provides just about all the information we care about,
 * plus some.</p>
 * @author mbutcher
 * @see http://www.loc.gov/standards/mods/
 *
 */
public enum SourceEnum implements SinciputType {
	/** 
	 * Internal type identifier: source
	 */
	TYPE (
			"type", 
			"Type", 
			new FieldDescription(FieldDescription.FIELD_NO_EDIT, "source")),
	/**
	 * The full title, including short title, subtitle, and non-indexing prefixes.
	 */
	TITLE (
			"title", 
			"Title", 
			new FieldDescription(FieldDescription.FIELD_TEXT, 512)),
	SORTABLE_TITLE (
			"sortable_title", 
			"Title", 
			new FieldDescription(FieldDescription.FIELD_TEXT,512)),
	/*
	 SUBTITLE (
			"subtitle",
			"Subtitle",
			new FieldDescription(FieldDescription.FIELD_TEXT, 50)),
	*/
	/**
	 * Sinciput tagging support.
	 */
	TAG (
			"tag", 
			"Tag",
			new FieldDescription(FieldDescription.FIELD_TEXT, 256)),
	/**
	 * The author or creator of a work.
	 */
	AUTHOR ( 
			"author",
			"Author",
			new FieldDescription(FieldDescription.FIELD_TEXT, 256)
			),
	/**
	 * Illustrator. (Possibly not used in MODS).
	 */
	ILLUSTRATOR(
			"illustrator",
			"Illustrator",
			new FieldDescription(FieldDescription.FIELD_TEXT, 256)),
	/**
	 * Editor of the source. E.g. collected works have editors.
	 */
	EDITOR (
			"editor",
			"Editor",
			new FieldDescription(FieldDescription.FIELD_TEXT, 256)),
	/**
	 * Translator of a work.
	 */
	TRANSLATOR (
			"translator",
			"Translator",
			new FieldDescription(FieldDescription.FIELD_TEXT, 256)),
	/**
	 * Organization responsible for publication.
	 */
	PUBLISHER (
			"publisher",
			"Publisher",
			new FieldDescription(FieldDescription.FIELD_TEXT, 256)),
	/**
	 * Date of publication.
	 */
	PUBLISH_DATE ("publish_date","Publication date",new FieldDescription(FieldDescription.FIELD_TEXT, 32)),
	/**
	 * Location of publication.
	 */
	PUBLISH_PLACE ("publish_place","Publication location",new FieldDescription(FieldDescription.FIELD_TEXT, 512)),
	/**
	 * Edition of work (e.g. 1st, 2nd, 11th...)
	 */
	EDITION ("edition","Edition",new FieldDescription(FieldDescription.FIELD_TEXT, 16)),
	/**
	 * Library of congress subject headings (collapsed form)
	 */
	SUBJECT ("subject","Subject",new FieldDescription(FieldDescription.FIELD_TEXT, 512)),
	/**
	 * What type of source this is -- text, audio, visual, etc.
	 */
	SOURCE_TYPE (
			"source_type",
			"Source type",
			new FieldDescription(FieldDescription.FIELD_TEXT, 32)),
	/**
	 * Genre of work.
	 */
	GENRE ("genre","Genre",new FieldDescription(FieldDescription.FIELD_TEXT, 32)),
	/**
	 * Language of work.
	 */
	LANGUAGE ("language","Language",new FieldDescription(FieldDescription.FIELD_TEXT, 64)),
	/**
	 * Abstract of the work.
	 */
	ABSTRACT ("abstract","Abstract",new FieldDescription(FieldDescription.FIELD_TEXT, 1024)),
	/**
	 * Work's table of contents.
	 */
	TABLE_OF_CONTENTS ("table_of_contents","Table of contents",new FieldDescription(FieldDescription.FIELD_TEXT, 1024)),
	/**
	 * Creator's intended audience.
	 */
	TARGET_AUDIENCE ("target_audience","Target audience",new FieldDescription(FieldDescription.FIELD_TEXT, 1024)),
	/**
	 * Additional notes from the library.
	 */
	NOTE ("note","Note",new FieldDescription(FieldDescription.FIELD_TEXT, 1024)),
	/**
	 * Unique IDs, such as ISBN, ISSN, and LCCN.
	 * Typical format: isbn:001234...
	 */
	IDENTIFIER ("identifier","Identifier",new FieldDescription(FieldDescription.FIELD_TEXT, 512)),
	/**
	 * Classification numbers, such as LCC, Dewey Decimal.
	 * Typical format: dds:F 123. B3
	 */
	CLASSIFICATION ("classification","Classification number",new FieldDescription(FieldDescription.FIELD_TEXT, 512)),
	/**
	 * URLs pertaining to this work. Typically, these are library record pages.
	 * Typical format: http://example.com/foo/mybook My Book
	 */
	LOCATION_URL ("location_url","Location URL",new FieldDescription(FieldDescription.FIELD_TEXT, 1024)),
	// INTERNAL fields
	CREATED_ON (
			"created_on", 
			"Created On",
			new FieldDescription(FieldDescription.FIELD_TEXT, 50)),
	LAST_MODIFIED (
			"last_modified", 
			"Last Modified",
			new FieldDescription(FieldDescription.FIELD_TEXT, 50)),
	CREATED_BY (
			"created_by",
			"Created By",
			new FieldDescription(FieldDescription.FIELD_TEXT, 50)),
	MODIFIED_BY (
			"modified_by", 
			"Modified By",
			new FieldDescription(FieldDescription.FIELD_TEXT, 50));
	
	private final String key;
	private final String pretty_name;
	private final FieldDescription desc;
	
	SourceEnum(String key, String pretty_name, FieldDescription desc) {
		this.key = key;
		this.pretty_name = pretty_name;
		this.desc = desc;
	}
	
	public String getPrintableName() {
		return this.pretty_name;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public FieldDescription getFieldDescription() {
		return this.desc;
	}
	
	public boolean typeHasBody() {
		return true;
	}
}
