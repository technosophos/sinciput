package com.technosophos.rhizome.repository;

import com.technosophos.rhizome.RhizomeException;

public class DocumentExistsException extends RhizomeException {
	private static final long serialVersionUID = 1L;
	
	public DocumentExistsException(){
		super("Document already exists.");
	}
	
	public DocumentExistsException(String str){
		super(str);
	}
}
