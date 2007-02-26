package com.technosophos.rhizome.repository.fs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This object tracks lock status of files in the file system.
 * 
 * It simply <i>tracks</i> locks. Actual maintenance of the locks
 * is left to other objects. The point of this class is to allow multiple
 * objects to all access (more or less independently) the files on the 
 * file system, without one write operation disrupting a bunch of read
 * operations.
 * 
 * <p>
 * Locks are stored in a synchronized collection. This should, overall,
 * reduce the number of objects that must be synchronized.
 * </p>
 * 
 * <p>In order to avoid the risk of multiple lock objects,
 * this is implemented as a singleton.</p>
 * @author mbutcher
 *
 */
public class FileSystemLocks {
	
	private static FileSystemLocks inst = null;
	
	private List<String> locks;
	private FileSystemLocks() {
		locks = Collections.synchronizedList(new ArrayList<String>());
	}
	
	/**
	 * Get an instance of the FileSystemLocks class
	 * @return
	 */
	public static FileSystemLocks getInstance() {
		if(FileSystemLocks.inst == null) FileSystemLocks.inst = new FileSystemLocks();
		return FileSystemLocks.inst;
	}
	
	/**
	 * Check to see if a file is locked.
	 * @param fileID
	 * @return
	 */
	public boolean isLocked(String fileID) {
		return this.locks.contains(fileID);
	}
	
	/**
	 * Add a new lock to the list.
	 * @param fileID
	 */
	public void lock(String fileID) {
		this.locks.add(fileID);
	}
	
	/**
	 * Remove a lock.
	 * @param fileID
	 */
	public void removeLock(String fileID) {
		if(this.isLocked(fileID)) 
			this.locks.remove(this.locks.indexOf(fileID));
	}
	
	/**
	 * Get an iterator of the list of locks.
	 * @return
	 */
	public java.util.Iterator getLockIterator() {
		return this.locks.iterator();
	}
}
