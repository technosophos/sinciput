package com.technosophos.rhizome.controller;

import com.technosophos.rhizome.RhizomeException;

/**
 * Indicates that a fatal error has occured during command processing.
 * @author mbutcher
 *
 */
public class FatalCommandException extends RhizomeException {
	private static final long serialVersionUID = 1L;
	public FatalCommandException(String message) {
		super(message);
	}

	public FatalCommandException(String message, Throwable thr) {
		super(message, thr);
	}

}
