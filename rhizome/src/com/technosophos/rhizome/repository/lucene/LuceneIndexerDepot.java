package com.technosophos.rhizome.repository.lucene;

import java.io.File;
import com.technosophos.rhizome.repository.DocumentIndexer;
import com.technosophos.rhizome.repository.DocumentIndexerDepot;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositoryContext;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.repository.util.FileUtils;

public class LuceneIndexerDepot implements DocumentIndexerDepot {

	public void createIndex(String name, RepositoryContext cxt)
			throws RhizomeInitializationException, RepositoryAccessException {
		
		// Quick check.
		if(name == null || name.length() == 0)
			throw new RhizomeInitializationException("Repository name must be specified. It cannot be empty or null.");
		String p = LuceneIndexer.getIndexPath("", cxt);
		// null == No key in context
		if(p == null) throw new RhizomeInitializationException("Index directive does not exist in context. Check your configuration file.");
		
		// Next: check if dir already exists. If not, create.
		File full_path = new File(LuceneIndexer.getIndexPath(name, cxt));
		if( full_path.exists())
			throw new RepositoryAccessException(String.format("Index %s already exists. Can't create.", full_path));
		
		
		boolean b = full_path.mkdir();
		if(!b) throw new RepositoryAccessException("Cannot create directory " + full_path.getAbsolutePath()); 

	}
	
	/**
	 * @deprecated This was just a bad idea to begin with.
	 */
	public void createIndex(String name, RepositoryContext cxt, boolean shareExisting) 
			throws RhizomeInitializationException, RepositoryAccessException {
		this.createIndex(name, cxt);
		return;
		/*
		//		 Quick check.
		String p = LuceneIndexer.getIndexPath("", cxt);
		if(p == null) throw new RhizomeInitializationException("Index directory does not exist.");
		
		String full_path = LuceneIndexer.getIndexPath(name, cxt);
		// First: check to see if this is in the same place as an existing repository:
		
		
		
		if( full_path != null) {
			File full_path_f = new File(full_path);
			if(shareExisting && full_path_f.isDirectory()) {
				System.out.println(full_path + " already exists.");
				return;
			} else throw new RepositoryAccessException(
					String.format("Index %s already exists. Can't create.", full_path));
		}
		
		File f = new File(p, name);
		boolean b = f.mkdir();
		if(!b) throw new RepositoryAccessException("Cannot create directory " + f.getAbsolutePath());
		*/ 
	}

	public void deleteIndex(String name, RepositoryContext cxt)
			throws RepositoryAccessException {
		
		String p = LuceneIndexer.getIndexPath(name, cxt);
		
		if( p == null) return;
		File f = new File(p);
		
		FileUtils.recursiveDirDelete(f);
	}

	public DocumentIndexer getIndexer(String name, RepositoryContext cxt)
			throws RhizomeInitializationException {
		LuceneIndexer li = new LuceneIndexer(name, cxt);
		
		return li;
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
