package com.technosophos.sinciput.types;

/**
 * A SinciputType is an Enum that describes a type of sinciput document.
 * 
 * Items in a sinciput type should always have keys and printable names.
 * @author mbutcher
 *
 */
public interface SinciputType {
	
	public final int DEFAULT_FIELD_LENGTH = 512;

	
	/**
	 * Return the name of the item in the enum.
	 * @return
	 */
	public String getKey();
	
	/**
	 * Return the printable name for the item in the enum.
	 * @return
	 */
	public String getPrintableName();
	
	/**
	 * Get the field description for this Enum.
	 * @return
	 */
	public FieldDescription getFieldDescription();
	
	/**
	 * Returns true if the type defined by this Enum should have a body.
	 * Method on the entire Enum.
	 * @return true if the document should have a body, false otherwise.
	 * @see com.technosophos.rhizome.document.RhizomeDocument
	 */
	public boolean typeHasBody();
	
}
