package com.technosophos.rhizome.controller;

import com.technosophos.rhizome.RhizomeException;

/**
 * Thrown when a {@link RhizomeCommand} fails to initialize.
 * That is, if the init() fails for a RhizomeCommand, it should throw this exception.
 * @author mbutcher
 * @see RhizomeCommand
 */
public class CommandInitializationException extends RhizomeException {
	private static final long serialVersionUID = 1L;
	public CommandInitializationException(String message) {
		super(message);
	}

	public CommandInitializationException(String message, Throwable thr) {
		super(message, thr);
	}

}
