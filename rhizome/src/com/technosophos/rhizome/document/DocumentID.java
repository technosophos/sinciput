package com.technosophos.rhizome.document;

/**
 * This is a utility class for dealing with document IDs.
 * For the sake of simplicity, document IDs are just strings.
 * But this class provides utilities for testing or generating document
 * IDs. Implementors may wish to override the generateDocumentID() method
 * to customize how new document IDs are created.
 * @author mbutcher
 *
 */
public class DocumentID {
	/**
	 * Generate a suitable document ID string.
	 */
	public static String generateDocumentID() {
		return "test-test-test";
	}
}
