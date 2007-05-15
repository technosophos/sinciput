package com.technosophos.rhizome.controller;

import java.util.Queue;

/**
 * Contains a request description.
 * <p>In Rhizome, a request configuration is composed of a series (a queue) of command 
 * configurations. When the {@link RhizomeController} receives a request, in the form
 * of a String name, it retrieves the correct request configuration and processes each
 * command in the queue of commands.</p>
 * <p>While a RequestConfiguration is basically just a name attached to a queue, there
 * are certain pieces of information that apply to an entire request, rather than just one
 * of the {@link CommandConfiguration} items. Thus, this object contains the queue, and
 * also makes available certain pieces of request-level information.</p>
 * @author mbutcher
 *
 */
public class RequestConfiguration {

	/** Default MIME type, which is text/html. */
	public static final String DEFAULT_MIME_TYPE = "text/html";
	
	protected String name = null;
	protected Queue<CommandConfiguration> commands = null;
	protected String mimeType = DEFAULT_MIME_TYPE;
	
	/**
	 * Create a new RequestConfiguration.
	 * This leaves the MIME type as the default: text/html.
	 * @see #RequestConfiguration(String, Queue, String)
	 * @param name
	 * @param commands
	 */
	public RequestConfiguration(String name, Queue<CommandConfiguration> commands) {
		this(name, commands, null);
	}
	
	/**
	 * Create a new RequestConfiguration.
	 * <p>This takes a request name, a Queue of CommandConfiguration objects, and a MIME type.
	 * </p>
	 * <p>
	 * The MIME type is used as a suggestion to the implementing application about how 
	 * the responses -- as a whole -- should be treated. While applications can ignore this
	 * at will, it is helpful to those, such as Servlets, that treat different content types 
	 * very differently.</p>
	 * @param name
	 * @param commands
	 * @param mimeType
	 */
	public RequestConfiguration(String name, Queue<CommandConfiguration> commands, String mimeType) {
		this.name = name;
		this.commands = commands;
		if(mimeType != null) this.mimeType = mimeType;
	}
	
	public Queue<CommandConfiguration> getQueue() {
		return this.commands;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getMimeType() {
		return this.mimeType;
	}
	
	/**
	 * Set the MIME type.
	 * @see #RequestConfiguration(String, Queue, String)
	 * @param mime
	 */
	public void setMimeType(String mime) {
		this.mimeType = mime;
	}
	
}
