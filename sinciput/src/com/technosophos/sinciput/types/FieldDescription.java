package com.technosophos.sinciput.types;

// TODO: Add support for required fields.
/**
 * Describes one of the fields in some type enum.
 * 
 * @see NotesEnum for an example.
 * @see SinciputType
 * @author mbutcher
 *
 */
public class FieldDescription {
	
	/**
	 * By default, display is 20.
	 */
	public static final int DEFAULT_DISPLAY_SIZE = 20;
	
	/**
	 * This marks the field as uneditable. 
	 * A "no_edit" field should never be shown in an editable
	 * state in a user application.
	 */
	public static final String FIELD_NO_EDIT = "no_edit";
	
	/**
	 * Field should be hidden from user.
	 */
	public static final String FIELD_HIDDEN = "hidden";
	/**
	 * Field should be text edit.
	 */
	public static final String FIELD_TEXT = "text";
	/**
	 * Field should be text edit, where output is not echoed back directly.
	 */
	public static final String FIELD_PASSWORD = "password";
	/**
	 * Field should be radio buttons.
	 */
	public static final String FIELD_RADIO = "radio";
	/**
	 * Field should be checkbox.
	 */
	public static final String FIELD_CHECKBOX = "checkbox";
	/**
	 * Field should be single-choice select box.
	 */
	public static final String FIELD_SELECT = "select";
	/**
	 * Field should be multi-choice select box.
	 */
	public static final String FIELD_MULTISELECT = "multiselect";
	/**
	 * Field should be combo box.
	 */
	public static final String FIELD_COMBOBOX = "combobox";
	/**
	 * Field should be large text editing area.
	 */
	public static final String FIELD_TEXTAREA = "textarea";
	/**
	 * Field should be rich text editor (HTML?).
	 */
	public static final String FIELD_RICHTEXTAREA = "richtextarea";
	/**
	 * EXPERIMENTAL: Field should be wiki text editor.
	 */
	public static final String FIELD_WIKITEXTAREA = "wikitextarea";

	private String fieldType = FIELD_TEXT;
	private int fieldLength = 20;
	private String [] values = null;
	private String defaultValue = "";
	private boolean required = false;
	
	/**
	 * Create a new FieldDescription.
	 * <p>A field description describes the various attributes of an editable field. This
	 * is used to automatically generate a UI for this particular field.</p>
	 * <p>Use this or the other constructors to create a new field description.</p>
	 * @param fieldType type of field (See TYPE constants above)
	 * @param fieldLength length of field (default: DEFAULT_DISPLAY_SIZE)
	 * @param defaultValue  value to show by default
	 * @param values array of values for fields that support multiple potential values.
	 * @param required Is this field required?
	 */
	public FieldDescription(String fieldType, int fieldLength, String defaultValue,String[] values, boolean required) {
		this.fieldType = fieldType;
		this.fieldLength = fieldLength;
		this.values = values;
		this.defaultValue = defaultValue;
		this.required = required;
	}
	
	public FieldDescription(String fieldType, int fieldLength, String defaultValue,String[] values) {
		this(fieldType, fieldLength, defaultValue, null, false);
	}
	
	public FieldDescription(String fieldType, int fieldLength, String defaultValue) {
		this(fieldType, fieldLength, defaultValue, null);
	}
	
	public FieldDescription(String fieldType, String defaultValue) {
		this(fieldType, DEFAULT_DISPLAY_SIZE, defaultValue, null);
	}
	
	public FieldDescription(String fieldType, int fieldLength) {
		this(fieldType, fieldLength, "", null);
	}
	
	public FieldDescription(String fieldType) {
		this(fieldType, DEFAULT_DISPLAY_SIZE, "", null);
	}
	
	/**
	 * Get default value
	 * @return
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}
	/**
	 * Set the value that will be used as a default value.
	 * Default: "".
	 * @param default_value
	 */
	public void setDefaultValue(String default_value) {
		this.defaultValue = default_value;
	}
	/**
	 * Get field length
	 * @return
	 */
	public int getFieldLength() {
		return fieldLength;
	}
	/**
	 * Set the maximum field length (for display).
	 * Default is {@link DEFAULT_DISPLAY_SIZE}.
	 * @param fieldLength
	 */
	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}
	/**
	 * Get field type (See TYPE constants... custom type can be specified)
	 * @return
	 */
	public String getFieldType() {
		return fieldType;
	}
	/**
	 * Field type.
	 * One of the FIELD_* constants for this class.
	 * @param fieldType
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	/**
	 * Get values.
	 * @return
	 */
	public String[] getValues() {
		return values;
	}
	/**
	 * An optional list of values.
	 * For FIELD types that take multiple items, these will be displayed as options.
	 * @param values
	 */
	public void setValues(String[] values) {
		this.values = values;
	}
	
	public boolean getRequired() {
		return this.required;
	}
	
	public void setRequired(boolean b) {
		this.required = b;
	}
}
