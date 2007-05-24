package com.technosophos.sinciput.types;

public enum NotesEnum implements SinciputType {
	TYPE ("type", "Type"),
	TITLE ("title", "Title"),
	SUBTITLE ("subtitle", "Subtitle"),
	CREATED_ON ("created_on", "Created On"),
	MODIFIED_ON ("last_modified", "Last Modified"),
	CREATED_BY ("created_by","Created By"),
	MODIFIED_BY ("modified_by", "Modified By");
	
	private final String key;
	private final String pretty_name;
	
	NotesEnum(String key, String pretty_name) {
		this.key = key;
		this.pretty_name = pretty_name;
	}
	
	public String getPrintableName() {
		return this.pretty_name;
	}
	
	public String getKey() {
		return this.key;
	}
	
}
