package com.technosophos.sinciput.types;

public enum NotesEnum implements SinciputType {
	TYPE (
			"type", 
			"Type", 
			new FieldDescription(FieldDescription.FIELD_NO_EDIT, "user")),
	TITLE (
			"title", 
			"Title", 
			new FieldDescription(FieldDescription.FIELD_TEXT, 50)),
	SUBTITLE (
			"subtitle",
			"Subtitle",
			new FieldDescription(FieldDescription.FIELD_TEXT, 50)),
	TAG (
			"tag", 
			"Tag",
			new FieldDescription(FieldDescription.FIELD_TEXT, 256)),
	CREATED_ON (
			"created_on", 
			"Created On",
			new FieldDescription(FieldDescription.FIELD_TEXT, 50)),
	MODIFIED_ON (
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
	
	NotesEnum(String key, String pretty_name, FieldDescription desc) {
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
