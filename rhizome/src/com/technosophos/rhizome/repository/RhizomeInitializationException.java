package com.technosophos.rhizome.repository;

import com.technosophos.rhizome.RhizomeException;

public class RhizomeInitializationException extends RhizomeException {
	private static final long serialVersionUID = 1L;
	
	public RhizomeInitializationException(){
		super("Failed to initialize unknown Rhizome component.");
	}
	
	public RhizomeInitializationException(String str){
		super(str);
	}
	
	public RhizomeInitializationException(String str, Throwable thr){
		super(str, thr);
	}
}
