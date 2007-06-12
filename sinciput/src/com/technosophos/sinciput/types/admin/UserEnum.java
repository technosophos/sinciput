package com.technosophos.sinciput.types.admin;

import com.technosophos.sinciput.types.SinciputType;
import com.technosophos.sinciput.types.FieldDescription;

public enum UserEnum implements SinciputType {
	
	TYPE (
			"type", 
			"Type", 
			new FieldDescription(FieldDescription.FIELD_NO_EDIT, "user")),
	USERNAME (
			"username", 
			"User Name", 
			new FieldDescription(FieldDescription.FIELD_TEXT, 128)),
	PASSWORD (
			"password", 
			"Password", 
			new FieldDescription(FieldDescription.FIELD_PASSWORD, 128)),
	PASSWORD_VERIFY (
			"password_verify", 
			"Verify Password", 
			new FieldDescription(FieldDescription.FIELD_PASSWORD, 128)),
	GIVENNAME (
			"gn", 
			"First Name", 
			new FieldDescription(FieldDescription.FIELD_TEXT, 128)),
	SURNAME (
			"sn", 
			"Last Name", 
			new FieldDescription(FieldDescription.FIELD_TEXT, 128)),
	DESCRIPTION (
			"description", 
			"Description", 
			new FieldDescription(FieldDescription.FIELD_TEXT, 1024)),
	ROLE (
			"role", 
			"Role", 
			new FieldDescription(FieldDescription.FIELD_TEXT, 64)),
	CREATED_ON (
			"created_on", 
			"Created on", 
			new FieldDescription(FieldDescription.FIELD_NO_EDIT)),
	LAST_MODIFIED (
			"last_modified", 
			"Last modified", 
			new FieldDescription(FieldDescription.FIELD_NO_EDIT));
	
	private final String key;
	private final String pretty_name;
	private final FieldDescription desc;
	
	UserEnum(String key, String pretty_name, FieldDescription desc) {
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
