package com.technosophos.sinciput.types.admin;

import com.technosophos.sinciput.types.FieldDescription;
import com.technosophos.sinciput.types.SinciputType;

public enum RepoDescriptionEnum implements SinciputType {
	TYPE (
			"type", 
			"Type", 
			new FieldDescription(FieldDescription.FIELD_NO_EDIT, "repodescription")),
	TITLE (
			"title",
			"Title",
			new FieldDescription(FieldDescription.FIELD_TEXT, 1024)),
	REPO_NAME(
			"repoName",
			"Repository Name",
			new FieldDescription(FieldDescription.FIELD_TEXT, 128)),
	OWNER (
			"owner",
			"Owner",
			new FieldDescription(FieldDescription.FIELD_SELECT)),
	// NOT SURE WHAT TO DO WITH MEMBERS AND GUESTS
	// These fields should allow multiple users.
	MEMBERS(
			"members",
			"Members",
			new FieldDescription(FieldDescription.FIELD_MULTISELECT, 128)),
	GUESTS (
			"guests",
			"Guests",
			new FieldDescription(FieldDescription.FIELD_MULTISELECT, 128)),
	DESCRIPTION (
			"description", 
			"Description", 
			new FieldDescription(FieldDescription.FIELD_TEXT, 1024)),
	CREATED_ON (
			"created_on", 
			"Created on", 
			new FieldDescription(FieldDescription.FIELD_NO_EDIT)),
	LAST_MODIFIED (
			"last_modified", 
			"Last modified", 
			new FieldDescription(FieldDescription.FIELD_NO_EDIT));
			
	;

	private final String key;
	private final String pretty_name;
	private final FieldDescription desc;
	
	RepoDescriptionEnum(String key, String pretty_name, FieldDescription desc) {
		this.key = key;
		this.pretty_name = pretty_name;
		this.desc = desc;
	}
	
	/**
	 * Return the name of the item in the enum.
	 * @return
	 */
	public String getKey() {
		return this.key;
	}
	
	/**
	 * Return the printable name for the item in the enum.
	 * @return
	 */
	public String getPrintableName() {
		return this.pretty_name;
	}
	
	public FieldDescription getFieldDescription() {
		return this.desc;
	}
	
	public boolean typeHasBody() {
		return false;
	}

}
