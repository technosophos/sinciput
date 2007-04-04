package com.technosophos.rhizome.document;

import com.technosophos.rhizome.RhizomeException;

/**
 * This exception is used to indicate that some Metadata container object does not
 * have information on this name.
 * @author mbutcher
 *
 */
public class NameNotStoredException extends RhizomeException {
	private static final long serialVersionUID = 1L;
	
	private Throwable thr = null;
	
	public NameNotStoredException(String message) {
		super(message);
	}
	
	public NameNotStoredException(String message, Throwable thr) {
		super(message);
		this.thr = thr;
	}
	
	public Throwable getNestedThrowable() {
		return this.thr;
	}
}