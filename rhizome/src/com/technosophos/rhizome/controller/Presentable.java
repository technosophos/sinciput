package com.technosophos.rhizome.controller;

/**
 * Presentation-ready object.
 * Presentable classes are those that can readily represent their data. Applications, such
 * as Rhizome, which need a general interface to the data can use Presentable objects to 
 * pass them to an implementing system without determining in advance exactly how they will
 * look (or in what medium they will be displayed).
 * @author mbutcher
 *
 */
public interface Presentable {

	/**
	 * Return a pretty string that can be displayed as-is.
	 * @return A string representation (suitable for display) of the object.
	 */
	public String toPresentation();
	
	/**
	 * Return a list of strings.
	 * An implementing application can then loop through the list and print items as they
	 * appear. This may return nothing more than a list containing one string, but it 
	 * should never return null.
	 * @return List of strings that can be printed.
	 */
	public java.util.List<String> toPresentationList();
	
}
