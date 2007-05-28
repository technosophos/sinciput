package com.technosophos.rhizome.repository.fs;

import java.io.File;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.DocumentRepositoryDepot;
import com.technosophos.rhizome.repository.RepositoryContext;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.util.FileUtils;

/**
 * This implements a repository that resides in the file system.
 * @author mbutcher
 *
 */
public class FileSystemRepositoryDepot implements DocumentRepositoryDepot {

	/**
	 * Creates a new repository as a directory in the file system.
	 */
	public void createNamedRepository(String name, RepositoryContext cxt) 
			throws RhizomeInitializationException, RepositoryAccessException {
		
		String base = FileSystemRepository.getFullPath("", cxt);
		if(base == null) 
			throw new RhizomeInitializationException("Repository directory not found.");

		String newDirName = FileSystemRepository.getFullPath(name, cxt); // Could be null only if thread changed cxt.
		if( newDirName == null ||  hasRepositoryPath(newDirName) ) 
			throw new RepositoryAccessException("Create failed. Repository already exists.");
		
		// Create dir
		File newDir = new File(newDirName);
		newDir.mkdir();
	}

	/**
	 * Delete an entire repository. Note that this expunges files completely. They will
	 * be no more. Gone. For good.
	 * If there is a problem deleting a file or subdirectory from the repository, an
	 * exception will be thrown.
	 * If no such directory was found, this will return without having done anything.
	 */
	public void deleteNamedRepository(String name, RepositoryContext cxt) 
			throws RepositoryAccessException {
		
		String delDirName = FileSystemRepository.getFullPath(name, cxt);
		if(delDirName == null) return; // this should throw exception?
		
		FileUtils.recursiveDirDelete(new File(delDirName));

	}

	public DocumentRepository getNamedRepository(String name, RepositoryContext cxt) 
			throws RhizomeInitializationException {
		
		FileSystemRepository r =  new FileSystemRepository(name, cxt);
		
		//r.getRepoDir(); // To trigger an exception if the dir does not exist.
		
		return r;
	}

	public boolean hasNamedRepository(String name, RepositoryContext cxt) {
		
		String p = FileSystemRepository.getFullPath(name, cxt);
		if(p == null) return false;
		
		return hasRepositoryPath( p );
		
	}
	
	/**
	 * Check to see if there is a repo directory at this path.
	 * This takes a path name, and returns true if this path exists, and has all the
	 * attributes expected of a directory.
	 * @param pathToRepository
	 * @return
	 */
	public static boolean hasRepositoryPath(String pathToRepository) {
		File dir = new File( pathToRepository );
		if(!dir.exists() || !dir.isDirectory() || !(dir.canRead() && dir.canWrite()))
			return false;
		
		return true;
	}

}
