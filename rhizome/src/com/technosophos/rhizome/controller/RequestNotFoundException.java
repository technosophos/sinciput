package com.technosophos.rhizome.controller;

import com.technosophos.rhizome.RhizomeException;

/**
 * Thrown when a request cannot be found by the controller.
 * @author mbutcher
 *
 */
public class RequestNotFoundException extends RhizomeException {
	private static final long serialVersionUID = 1L;
	
	public RequestNotFoundException(String message) {
		super(message);
	}

	public RequestNotFoundException(String message, Throwable thr) {
		super(message, thr);
	}

}
