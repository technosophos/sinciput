package com.technosophos.rhizome.repository.util;

import java.io.File;

import com.technosophos.rhizome.repository.RepositoryAccessException;

/**
 * Utilities for manipulating files.
 * @author mbutcher
 *
 */
public class FileUtils {
	/**
	 * Recursively deletes all dirs and files in baseDir.
	 * @param baseDir
	 * @throws RepositoryAccessException
	 */
	public static void recursiveDirDelete(File baseDir) throws RepositoryAccessException {
		if(!baseDir.exists() || !baseDir.isDirectory()) return;
		
		if(!baseDir.canWrite()) 
			throw new RepositoryAccessException("Cannot remove " + baseDir.getAbsolutePath());
		
		File[] subfiles = baseDir.listFiles();
		for(File file: subfiles) {
			if(file.isDirectory()) recursiveDirDelete(file);
			else {
				boolean whacked = file.delete();
				if(!whacked) throw new RepositoryAccessException("Cannot remove file " + file.getAbsolutePath());
			}
		}
	}
}
