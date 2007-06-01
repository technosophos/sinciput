package com.technosophos.rhizome.controller;

import com.technosophos.rhizome.RhizomeException;

public class CommandNotFoundException extends RhizomeException {
	private static final long serialVersionUID = 1L;
	
	public CommandNotFoundException(String message) {
		super(message);
	}

	public CommandNotFoundException(String message, Throwable thr) {
		super(message, thr);
	}

}
