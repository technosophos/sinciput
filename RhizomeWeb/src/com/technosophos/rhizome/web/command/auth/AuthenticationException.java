package com.technosophos.rhizome.web.command.auth;

import com.technosophos.rhizome.RhizomeException;

/**
 * This exception indicates that there is an authentication problem.
 * @author mbutcher
 *
 */
public class AuthenticationException extends RhizomeException {

	private static final long serialVersionUID = 1L;
	
	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(String message, Throwable thr) {
		super(message, thr);
	}

}
