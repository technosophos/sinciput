package com.technosophos.rhizome;

public class RhizomeException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private Throwable thr = null;
	
	public RhizomeException(String message) {
		super(message);
	}
	
	public RhizomeException(String message, Throwable thr) {
		super(message);
		this.thr = thr;
	}
	
	public Throwable getNestedThrowable() {
		return this.thr;
	}
}
