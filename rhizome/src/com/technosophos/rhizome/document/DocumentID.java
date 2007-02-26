package com.technosophos.rhizome.document;

import java.io.File;

/**
 * This is a utility class for dealing with document IDs.
 * For the sake of simplicity, document IDs are just strings.
 * But this class provides utilities for testing or generating document
 * IDs. Implementors may wish to override the generateDocumentID() method
 * to customize how new document IDs are created.
 * @author mbutcher
 *
 */
public class DocumentID implements java.io.FilenameFilter, java.io.FileFilter {
	/**
	 * Generate a suitable document ID string.
	 */
	public static String generateDocumentID() {
		return "test-test-test";
	}
	
	public boolean accept(java.io.File dir, String name) {
		if(name.startsWith(".")) return false;
		File f = new File(dir, name);
		if(f.isFile()) return true;
		return false;
	}
	
	public boolean accept(java.io.File file) {
		if(file.isFile() && !file.getName().startsWith(".")) return true;
		return false;
	}
}
