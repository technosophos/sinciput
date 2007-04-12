package com.technosophos.rhizome.controller;

import java.util.Map;

/**
 * A Container for Command Configuration Information.
 * @author mbutcher
 *
 */
public class CommandConfiguration {

	protected String name = null;
	protected String classname = null;
	private String prefix = "";
	protected Map<String, String> params;
	private boolean failFast = false;
	
	private CommandConfiguration() {
		
	}
	
	/**
	 * Construct a new command configuration object. 
	 * <p>
	 * Use this constructor if you may need
	 * to set the command to fail on error.
	 * </p>
	 * <p>
	 * Name is used to determine which command should be run. (A request may have multiple
	 * command names).
	 * </p>
	 * <p>
	 * Classname is used to determine what class to load. The class must be an instance of 
	 * RhizomeCommand.
	 * </p>
	 * <p>
	 * Params are configuration parameters to be passed to the command at initialization.
	 * More information is sent to the command when the command is executed.
	 * </p>
	 * <p>The "fail on error" flag is used to indicate that if this command fails, the 
	 * entire request should fail.</p>
	 * 
	 * @param name name of the command.
	 * @param classname class for the command.
	 * @param params paramters for initializing the command.
	 * @param fail whether the entire request should fail when this command fails. False by default.
	 */
	public CommandConfiguration(String name, String classname, 
								Map<String, String> params, boolean fail) {
		this.name = name;
		this.classname = classname;
		this.params = params;
		this.failFast = fail;
	}
	
	public CommandConfiguration(String name, String classname) {
		this.name = name;
		this.classname = classname;
	}
	
	/**
	 * Construct a new command configuration object. This is the preferred constructor.
	 * @param name name of the command
	 * @param classname name of the command class, to be loaded by a classloader
	 * @param params name/value parameters
	 * @see CommandConfiguration(String, String, Map, boolean)
	 */
	public CommandConfiguration(String name, String classname, Map<String, String> params) {
		this(name, classname, params, false);
	}
	
	/**
	 * Returns true if the command processor should fail when an error occurs.
	 * A command processor (like RhizomeController) usually executes all of the commands
	 * required to complete a request, even if some return errors. But if this returns 
	 * true, the command processor should fail immediately, and return a general failure
	 * message to the client.
	 * @return true if the command processor should fail fast.
	 */
	public boolean failOnError() {
		return this.failFast;
	}
	
	/**
	 * Set fail on error flag.
	 * If a fail on error flag is set to true, then an error here results in an immediate
	 * (error-state) return to the client.
	 * @param b flag - true means an error will stop all command processing
	 */
	public void setFailOnError(boolean b) {
		this.failFast = b;
	}
	
	/**
	 * Get the name of this command.
	 * @return command name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Set the command name.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the classname of the command.
	 * This classname should be loadable by a class loader.
	 * @return class name (as a string).
	 */
	public String getCommandClassname() {
		return this.classname;
	}
	
	/**
	 * Set the class name for this command.
	 * @param classname
	 */
	public void setCommandClassname(String classname) {
		this.classname = classname;
	}
	
	/**
	 * Return the input prefix.
	 * A prefix is a string prepended to keys before the keys are grabbed out of the 
	 * input context.
	 * @return The prefix, or an empty string if none is set.
	 */
	public String getPrefix() {
		return this.prefix;
	}
	
	/**
	 * Set a prefix.
	 * The prefix will be prepended to the keys used to fetch key/value pairs out of
	 * the input context.
	 * @param prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Returns true if there is a prefix.
	 * @return
	 */
	public boolean hasPrefix() {
		if(this.prefix == null || this.prefix.length() == 0) return false;
		return true;
	}
	
	/**
	 * Get a map of paramter name-> value entries.
	 * @return Map of name/val pairs
	 */
	public Map<String, String> getParameters() {
		return this.params;
	}
	
	/**
	 * Get the value of a specific paramter.
	 * @param paramName the name of the paramter
	 * @return the value
	 */
	public String getParameter(String paramName) {
		return this.params.get(paramName);
	}
	
	/**
	 * Set the map of name/val pair-based parameters.
	 * @param m
	 * @return
	 */
	public void setParameters(Map<String, String> m) {
		this.params = m;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{Command: ");
		sb.append(this.name);
		sb.append("(");
		sb.append(this.classname);
		if (this.failOnError()) sb.append(" failfast is on ");
		if(this.prefix.length() > 0) {
			sb.append(", prefix is \"");
			sb.append(this.prefix);
			sb.append("\"");
		}
		sb.append(") {Parameters: ");
		for(String pname: this.params.keySet()) {
			sb.append("{");
			sb.append(pname);
			sb.append(": ");
			sb.append(this.params.get(pname));
			sb.append("}");
		}
		sb.append(" }}");
		return sb.toString();
	}
}
