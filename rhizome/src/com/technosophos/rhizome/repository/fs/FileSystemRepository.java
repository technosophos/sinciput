package com.technosophos.rhizome.repository.fs;

import java.io.InputStream;

import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositoryContext;

/**
 * File system-backed Document Repository.
 * <p>
 * This is an implementation of the DocumentRepository that uses 
 * the system's file system to store documents.
 * </p>
 * <p>
 * Documents are stored on the file system, where the document ID 
 * is the file name, and the document is stored as the file contents
 * (in XML, presumably).
 * </p>
 * @author mbutcher
 *
 */
public class FileSystemRepository implements DocumentRepository {
	
	/**
	 * The name of the value in the hash map that contains the
	 * path information for the file system path.
	 */
	public static String FILE_SYSTEM_PATH_NAME = "fileSystemPath";
	
	// The config for this repository
	private RepositoryContext cxt;
	private String fileSystemPath;
	
	/**
	 * Construct a new repository.
	 * <p>
	 * The context should contain all of the settings for the 
	 * repository. In particular, the respository's file 
	 * system path ought to be passed in as:
	 * </p>
	 * <pre>
	 * fileSystemPath
	 * </pre>
	 * <p>
	 * The value should be the full (absolute) path in the 
	 * file system of the repository directory.
	 * </p>
	 * @param cxt
	 */
	public FileSystemRepository(RepositoryContext cxt) {
		this.cxt = cxt;
		if(cxt.hasKey(FileSystemRepository.FILE_SYSTEM_PATH_NAME))
			this.fileSystemPath = cxt.getParam(FileSystemRepository.FILE_SYSTEM_PATH_NAME);
	}

	public long countDocumentIDs() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] getAllDocumentIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	public RepositoryContext getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	public RhizomeDocument getDocument(String docID) {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getRawDocument(String docID) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasDocument(String docID) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isReusable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setConfiguration(RepositoryContext ctx) {
		// TODO Auto-generated method stub

	}

	public synchronized String storeDocument(RhizomeDocument doc) {
		if(this.canAccess(doc.getDocumentID())) {
			FileSystemLocks.getInstance().lock(doc.getDocumentID());
			try {
//				 STOPPED HERE	
			} finally {
				FileSystemLocks.getInstance().removeLock(doc.getDocumentID());
			}
		} else {
			// What to do here?
		}
		return null;
	}
	
	private boolean canAccess(String docID) {
		return !FileSystemLocks.getInstance().isLocked(docID);
	}

}
