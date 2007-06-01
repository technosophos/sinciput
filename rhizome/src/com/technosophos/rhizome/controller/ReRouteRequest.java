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
		// TODO Auto-generated constructor stub
	}

	public ReRouteRequest(String requestName) {
		this.requestName = requestName;
	}
	
	public ReRouteRequest(String request, String explanation) {
		
	}
	
	public String getRequestName() {
		return this.requestName;
	}
	
	public String getExplanation() {
		return this.explanation;
	}


}
