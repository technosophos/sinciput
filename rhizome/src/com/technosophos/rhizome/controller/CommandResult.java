package com.technosophos.rhizome.controller;

/**
 * This contains the result of a particular command.
 * <p>When a command (a class implementing {@link RhizomeCommand} and called during processing
 * of a command queue) completes its task, it returns a CommandResult. The CommandResult is
 * a convenient wrapper for return information, including whether the command executed successfully,
 * and what information was returned by the command.</p>
 * <p>The main item of interest in a CommandResult is the object stored within
 * it. The object should
 * contain the information that is to be returned to the requesting client.</p>
 * <p>Generally, it is up to the implementing server to decide what to do with the
 * CommandResult and its object. Clients may introspect the object and call methods, or
 * they may simply use the object's toString() method.</p>
 * @author mbutcher
 * @see RhizomeCommand
 * @see RhizomeController.doRequest(String, Map)
 *
 */
public class CommandResult {

	private Object result = null;
	private boolean err = false;
	private String errMsg = "";
	private String friendlyErrMsg = "An error occured while processing this request.";
	private Exception e = null;
	private String cmdName = null;
	
	/**
	 * This will set the command name to the String value.
	 * This method should only be used when the command configuration cannot be found.
	 * @param commandName Command name (might have prefix)
	 * @see #CommandResult(CommandConfiguration)
	 */
	public CommandResult(String commandName) {
		this.cmdName = commandName;
	}
	
	/**
	 * This will set the CommandResult name to cc.getPrefix() + cc.getName().
	 * @param cc
	 * @see #CommandResult(CommandConfiguration, Object)
	 */
	public CommandResult(CommandConfiguration cc) {
		if( cc.hasPrefix() )
			this.cmdName = cc.getPrefix() + cc.getName();
		else
			this.cmdName = cc.getName();
	}
	
	/**
	 * Create a new class in which results are stored.
	 * <b>Use this only when you must.</b>
	 * The object passed in is treated as the results. Different applications will deal
	 * with this object in their own ways, but a primitive one may simply call 
	 * Object.toString().
	 * @param commandName name of the command that produced this result
	 * @param o an object.
	 * @deprecated Use a form that takes a CommandConfiguration.
	 * @see #CommandResult(CommandConfiguration, Object)
	 */
	public CommandResult(String commandName, Object o) {
		this.cmdName = commandName;
		this.result = o;
	}
	
	/**
	 * This is the preferred way of creating a CommandResult.
	 * This will use the CommandConfiguration to get the prefix and the command
	 * name, which will be used together to create the name of this result. The object will
	 * be stored here, for retrieval by other commands, or by the controller.
	 * @param cc The CommandConfiguration object for the invoking command.
	 * @param o The object to be wrapped.
	 */
	public CommandResult(CommandConfiguration cc, Object o) {
		if( cc.hasPrefix() )
			this.cmdName = cc.getPrefix() + cc.getName();
		else
			this.cmdName = cc.getName();
		this.result = o;
	}
	
	/**
	 * Return the result from the command.
	 * @return An object.
	 */
	public Object getResult() {
		return this.result;
	}
	
	/**
	 * Get the name of this command.
	 * @return
	 * @deprecated use {@link #getName()}
	 */
	public String getCommandName() {
		return this.getName();
	}
	
	public String getName() {
		return this.cmdName;
	}
	
	/**
	 * Set the result object.
	 * Different applications will treat this object differently. A primitive one may 
	 * simply call the object's <code>toString()</code> method.
	 * @param o
	 */
	public void setResult(Object o) {
		this.result = o;
	}
	
	/**
	 * Indicates whether an error has occured while processing.
	 * @return true if an error occurs
	 */
	public boolean hasError() {
		return this.err;
	}
	
	/**
	 * Set an error.
	 * Call this if an error occurs while processing a command.
	 * @param errMsg Technical error message for debugging.
	 * @param friendlyErrMsg User-friendly error message.
	 * @param e Exception thrown that caused the error.
	 */
	public void setError(String errMsg, String friendlyErrMsg, Exception e) {
		this.err = true;
		this.errMsg = errMsg;
		this.friendlyErrMsg = friendlyErrMsg;
		this.e = e;
	}
	
	/**
	 * Set an error.
	 * This form may be used if the error is not a result of an exception.
	 * @see setError(String, String, Exception)
	 * @param errMsg
	 * @param friendlyErrMsg
	 */
	public void setError(String errMsg, String friendlyErrMsg) {
		this.setError(errMsg, friendlyErrMsg, null);
	}
	
	/**
	 * Return an error message with technical information.
	 * @return error string
	 */
	public String getErrorMessage() {
		return this.errMsg;
	}
	
	
	/**
	 * Return a friendly error message, for viewing by end user.
	 * 
	 * @return error string
	 */
	public String getFriendlyErrorMessage() {
		return this.friendlyErrMsg;
	}
	
	/**
	 * Return the exception (if any) that occured.
	 * If hasError is true, this might have an Exception object.
	 * @return the exception object, or null if no exception was thrown.
	 */
	public Exception getException() {
		return this.e;
	}
	
	//inherit javadoc
	public String toString() {
		if(this.hasError()) return this.getErrorMessage();
		return this.result.toString();
	}
}
