package com.technosophos.rhizome.controller;

/**
 * This contains the result of a particular command.
 * @author mbutcher
 *
 */
public class CommandResult {

	private Presentable result = null;
	private boolean err = false;
	private String errMsg = "";
	private String friendlyErrMsg = "An error occured while processing this request.";
	private Exception e = null;
	
	public CommandResult() {
		
	}
	
	/**
	 * Create a new class in which results are stored.
	 * The object passed in is treated as the results. Different applications will deal
	 * with this object in their own ways, but a primitive one may simply call 
	 * Object.toPresentation().
	 * @param o a presentable object.
	 */
	public CommandResult(Presentable o) {
		this.result = o;
	}
	
	/**
	 * Return the result from the command.
	 * @return
	 */
	public Object getResult() {
		return null;
	}
	
	/**
	 * Set the result object.
	 * Different applications will treat this object differently. A primitive one may 
	 * simply call the object's <code>toPresentable()</code> method.
	 * @param o
	 */
	public void setResult(Presentable o) {
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
