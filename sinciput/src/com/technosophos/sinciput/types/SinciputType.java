package com.technosophos.sinciput.types;

/**
 * A SinciputType is an Enum that describes a type of sinciput document.
 * 
 * Items in a sinciput type should always have keys and printable names.
 * @author mbutcher
 *
 */
public interface SinciputType {

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
	
}
