package com.technosophos.sinciput;

import com.technosophos.rhizome.RhizomeException;

public class SinciputException extends RhizomeException {
	private static final long serialVersionUID = 1L;
	
	public SinciputException(String message) {
		super(message);
	}

	public SinciputException(String message, Throwable thr) {
		super(message, thr);
	}

}
