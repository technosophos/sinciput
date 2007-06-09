package com.technosophos.rhizome.controller;

/**
 * A ReRouteRequest is used to interrupt command queue processing.
 * <p>It is used to interrupt the processing of one request, and begin processing
 * another request. Practically speaking, this can be used in cases where, for 
 * example, an unauthenticated user performs one request, but must be authenticated,
 * and is re-routed to the authentication request.</p>
 * @author mbutcher
 *
 */
public class ReRouteRequest extends Throwable {

	private String requestName = null;
	private String explanation = null;
	static final long serialVersionUID = 12312L;
	
	private ReRouteRequest() {
		this("default");
	}

	public ReRouteRequest(String requestName) {
		this(requestName, null);
	}
	
	public ReRouteRequest(String request, String explanation) {
		this.requestName = request;
		this.explanation = explanation;
	}
	
	/**
	 * Get the name of the request to re-route to.
	 * @return
	 */
	public String getRequestName() {
		return this.requestName;
	}
	
	/**
	 * Get an explanation string.
	 * This might be null.
	 * @return
	 */
	public String getExplanation() {
		return this.explanation;
	}


}
