package com.technosophos.rhizome.repository;

import com.technosophos.rhizome.RhizomeException;

public class RepositoryAccessException extends RhizomeException {
	private static final long serialVersionUID = 1L;
	
	public RepositoryAccessException(){
		super("Cannot access document repository.");
	}
	
	public RepositoryAccessException(String str){
		super(str);
	}
}
