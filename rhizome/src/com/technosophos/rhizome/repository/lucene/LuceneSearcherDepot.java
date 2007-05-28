package com.technosophos.rhizome.repository.lucene;

import com.technosophos.rhizome.repository.RepositoryContext;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RepositorySearcherDepot;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

public class LuceneSearcherDepot implements RepositorySearcherDepot {

	public RepositorySearcher getSearcher(String name, RepositoryContext cxt)
			throws RhizomeInitializationException {
		
		return new LuceneSearcher(name, cxt);
	}

}
