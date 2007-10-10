package com.technosophos.sinciput.types;

/**
 * This Enum describes a course (a class or seminar).
 * @author mbutcher
 *
 */
public enum CourseEnum implements SinciputType {
	TYPE (
			"type", 
			"Type", 
			new FieldDescription(FieldDescription.FIELD_NO_EDIT, "coures")),
	TITLE (
			"title", 
			"Course Title", 
			new FieldDescription(FieldDescription.FIELD_TEXT, 50)),
	LOCATION (
			"location",
			"Course Location",
			new FieldDescription(FieldDescription.FIELD_TEXT, 512)),
	START_DATE (
			"start_date",
			"Start Date",
			new FieldDescription(FieldDescription.FIELD_TEXT, 64)),
	END_DATE (
			"end_date",
			"End Date",
			new FieldDescription(FieldDescription.FIELD_TEXT, 64)),
	INSTRUCTOR (
			"instructor",
			"Instructor(s)",
			new FieldDescription(FieldDescription.FIELD_TEXT, 512)),
	INSTRUCTOR_EMAIL (
			"instructor_email",
			"Instructor(s)'s Email Address",
			new FieldDescription(FieldDescription.FIELD_TEXT, 512)),
	COURSE_NUMBER (
			"course_number",
			"Course number or code",
			new FieldDescription(FieldDescription.FIELD_TEXT, 32)),
	COURSE_TIMES (
			"course_times",
			"Course Day/Time Information",
			new FieldDescription(FieldDescription.FIELD_TEXT, 256)),
	DESCRIPTION (
			"description",
			"Course Description or Notes About the Course",
			new FieldDescription(FieldDescription.FIELD_TEXT, 1024)),
	TAG (
			"tag", 
			"Tag",
			new FieldDescription(FieldDescription.FIELD_TEXT, 256)),
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
	
	CourseEnum(String key, String pretty_name, FieldDescription desc) {
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
