package com.technosophos.rhizome.repository.lucene;

import java.io.File;
import com.technosophos.rhizome.repository.DocumentIndexer;
import com.technosophos.rhizome.repository.DocumentIndexerDepot;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositoryContext;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

public class LuceneIndexerDepot implements DocumentIndexerDepot {

	public void createIndex(String name, RepositoryContext cxt)
			throws RhizomeInitializationException, RepositoryAccessException {
		// Quick check.
		String p = LuceneIndexer.getIndexPath("", cxt);
		if(p == null) throw new RhizomeInitializationException("Index directory does not exist.");
		
		if(LuceneIndexer.getIndexPath(name, cxt) != null)
			throw new RepositoryAccessException("Index already exists. Can't create.");
		
		File f = new File(p, name);
		boolean b = f.mkdir();
		if(!b) throw new RepositoryAccessException("Cannot create directory " + f.getAbsolutePath()); 

	}

	public void deleteIndex(String name, RepositoryContext cxt)
			throws RepositoryAccessException {
		// TODO Auto-generated method stub

	}

	public DocumentIndexer getIndexer(String name, RepositoryContext cxt)
			throws RhizomeInitializationException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Check if index exists and is usable.
	 * This makes sure that the index directory exists, has the right perms, and is 
	 * really a directory (where the index files can be stored).
	 * @return true if the index exists and is in good working order.
	 */
	public boolean hasIndex(String name, RepositoryContext cxt) {
		
		String p = LuceneIndexer.getIndexPath(name, cxt);
		if(p == null)return false;
		File f = new File(p);
		
		// We only want true if the index is in good working order:
		if(f.isDirectory() && f.canWrite() && f.canRead()) return true;
		
		return false;
	}

}
