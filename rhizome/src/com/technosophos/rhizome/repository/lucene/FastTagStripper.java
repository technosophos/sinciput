package com.technosophos.rhizome.repository.lucene;
/**
 * Quickly remove all HTML/XML/SGML tags.
 *  <p>This class implements a very fast tag stripper. It is not exactly 
 * careful about stripping tags... it just rips them all out.</p>
 * <p>This class is borrowed from my old OpenCms Lucene search code, which is still
 * available (albeit worthless) at http://aleph-null.tv
 * @author mbutcher
 */
public class FastTagStripper {

	public static void main (String [] argv) {
		String html = "<html><head><title>Title</title><body attr='bogus'>Test  test      test \ntest test. This is a test.</body></html>";
		System.out.println(strip(html.toCharArray()));

	}
	
	/**
	 * Ruthlessly strips all tags out of a string.
	 * This is a convenience method for strip(char[])
	 * @see strip(char[])
	 * @param doc
	 * @return
	 */
	public static String strip(String doc) {
		return strip(doc.toCharArray());
	}

	/**
	 * Ruthlessly strips all tags out of a char array. Not good if you are trying
	 * to capture data inside of the tags. Tags/Elements are considered
	 * anything that begins with a &gt;. Yes, this is pretty shallow, but it
	 * works on any valid HTML/XML doc, and it's fast. 
	 * For something more robust, see the
	 * JavaCC HTMLParser that comes with Lucene. 
	 * <p>Note that this will include contents of script or style tags that
	 * do not use comment tags to hide their contents.</p>
	 * <p>
	 * This will work for XML, too, but it will strip out the contents of 
	 * CDATA elements as well. 
	 * </p>
	 * @param char[] html
	 * @return String stripped of all tags/elements.
	 */
	public static String strip(char [] doc) {
		StringBuffer sb = new StringBuffer();
		char lastChar = ' '; // basically, prevents leading whitespace
		boolean write = true;
		for (int i=0; i < doc.length; ++i) {
			if(doc[i] == '<') write = false;
			else if (doc[i] == '>') {
				write = true;
				sb.append(' ');
			} else if (write && 
					!(isWhitespace(doc[i]) && isWhitespace(lastChar))) {
				sb.append(doc[i]);
			}
			lastChar = doc[i];
		}
		return sb.toString();
	}

	public static boolean isWhitespace(char c) {
		char [] ws = {' ','\n','\t'};
		for (int i = 0; i < ws.length; ++i) {
			if(ws[i] == c) return true;
		}
		return false;
	}
}

