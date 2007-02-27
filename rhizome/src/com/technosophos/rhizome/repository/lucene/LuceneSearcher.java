package com.technosophos.rhizome.repository.lucene;

import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RepositoryContext;

public class LuceneSearcher implements RepositorySearcher {

	RepositoryContext context;
	
	public LuceneSearcher() {
		this.context = new RepositoryContext();
	}
	
	public boolean isReusable() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public RepositoryContext getConfiguration() {
		return this.context;
	}
	
	public void setConfiguration(RepositoryContext context) {
		this.context = context;
	}
}
