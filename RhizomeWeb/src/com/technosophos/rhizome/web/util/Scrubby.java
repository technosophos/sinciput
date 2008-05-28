package com.technosophos.rhizome.web.util;

//import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.util.Arrays;

//import org.xml.sax.SAXException;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.web.xml.rhtml.RHTML;

/**
 * This class provides a number of input cleaning tools.
 * 
 * Some of the methods here are just wrappers around other Java functions (like URL encoding
 * and decoding). These are gathered here for the sake of convenience.
 * @author mbutcher
 *
 */
public class Scrubby {
	
	/**
	 * According to HTML/XML/XHTML specs, these MUST be encoded as entities.
	 */
	public final static int[] HTML_NECESSARY_ENTITIES = {
		34, // "
		38, // &
		39, // '
		60, // <
		62  // >
	};
	
	/**
	 * These are ISO 8859-1 symbols and chars
	 */
	public final static int[] HTML_OPTIONAL_ENTITIES = {
		// Symbols
		160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175,
		176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191,
		// chars
		192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 
		208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 220, 221, 222, 223, 224,
		225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240,
		241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255,
		//misc
		338, 339, 352, 353, 376, 710, 732, 8149, 8195, 8201, 8204, 8205, 8206, 8207, 8211, 
		8212, 8216, 8217, 8218, 8220,8221, 8222, 8224, 8225, 8230, 8240, 8249, 8250, 8364, 
		8482
	};
	
	/**
	 * Character set used for encoding and decoding URLs.
	 */
	public final static String URL_CHARSET = "UTF-8";

	/**
	 * Encode a URL.
	 * This uses the UTF-8 character set.
	 * @param url URL to be encoded.
	 * @return URL encoded
	 * @see java.net.URLEncoder
	 */	
	public static String URLEncode(String url) {
		
		try {
			return URLEncoder.encode(url, URL_CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new Error("UTF-8 isn't supported?");
		}
	}
	
	/**
	 * Decode a URL.
	 * @param url
	 * @return
	 */
	public static String URLDecode(String url) {
		try {
			return URLDecoder.decode(url, URL_CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new Error("UTF-8 isn't supported?");
		}
	}
	
	/**
	 * Clean text.
	 * Make sure that the text has only valid UTF-8 printable characters.
	 * @param text
	 * @return
	 */
	public static String cleanText(String text) {
		//FIXME: !!!! This needs work.
		return encodeHTMLChars( text, false );
	}
	
	/**
	 * Check to make sure that an email address is valid.
	 * @param addr
	 * @return
	 */
	public static boolean isValidEmail(String addr) {
		// Got this regex from: http://regexlib.com/
		String regex = "^(([A-Za-z0-9]+_+)|([A-Za-z0-9]+\\-+)|([A-Za-z0-9]+\\.+)|([A-Za-z0-9]+\\++))*[A-Za-z0-9]+@((\\w+\\-+)|(\\w+\\.))*\\w{1,63}\\.[a-zA-Z]{2,6}$";
		return addr.matches(regex);
	}
	
	/**
	 * Clean an HTML string.
	 * Make sure that the HTML (a) is well-formed, (b) is properly encoded.
	 * @param html
	 * @return
	 */
	/*
	public static String cleanHTML(String html) {
		return html;
	}
	*/
	
	
	/**
	 * Make a clean and safe HTML fragment.
	 * This does deXSS stuff as well as basic HTML cleaning. Troublesome stuff may be removed.
	 * @param html Dirty HTML string.
	 * @return Clean HTML string.
	 * @throws SinciputException Thrown when HTML cannot be parsed (or if a parser cannot be created).
	 */
	public static String cleanSafeHTML( String html) throws RhizomeException {
		html = "<span>" + html + "</span>"; // Ensure that we have a wrapper tag.
		RHTML r = new RHTML(html);
		
		String clean = null;
		try {
			clean =  r.getRHTMLString();
		} catch (Exception e) {
			throw new RhizomeException("Failed to parse HTML: " + e.getMessage(), e);
		}
		//trim added span tag.
		return clean.substring(6, clean.length() - 7);
		//return clean;
	}
	
	/**
	 * Encode HTML in XML entities.
	 * @param cdata
	 * @param only_necessary
	 * @return
	 */
	public static String encodeHTMLChars( String cdata, boolean only_necessary) {
		StringBuilder sb = new StringBuilder(cdata.length() * 5);
		char[] chars = cdata.toCharArray();
		int i, j = chars.length;
		for( i = 0; i < j; ++i) {
			if(Arrays.binarySearch(HTML_NECESSARY_ENTITIES, chars[i]) >= 0) {
			// Stopped here
				sb.append("&#");
				sb.append((int)chars[i]);
				sb.append(';');
			}else if(!only_necessary && Arrays.binarySearch(HTML_OPTIONAL_ENTITIES, chars[i]) >= 0) {
			// Stopped here
				sb.append("&#");
				sb.append((int)chars[i]);
				sb.append(';');
			} else sb.append(chars[i]);
		}
		return sb.toString();
	}
	
	/**
	 * Attempt to encode HTML, or escape markup if necessary.
	 * Try to clean the HTML with {@link Scrubby.cleanSafeHTML(String)}, 
	 * but if that fails (perhaps because HTML is malformed),
	 * fallback to plain text conversion with {@link Scrubby.cleanText(String)}.
	 * @param html Some string with (possibly) HTML/XML markup.
	 * @return Cleaned string.
	 */
	public static String trySafeButFallbackToPlain(String html) {
		try {
			html= Scrubby.cleanSafeHTML(html);
		} catch (RhizomeException e1) {
			html = Scrubby.cleanText(html);
		}
		return html;
	}
	
	/**
	 * Make sure a string consists only of digits.
	 * @param str String to check
	 * @return True if only digits, false otherwise.
	 */
	public static boolean checkDigits(String str) {
		char[] chars = str.toCharArray();
		for( int i = 0; i < chars.length; ++i) {
			if(! Character.isDigit(chars[i])) return false;
		}
		return true;
	}
	
	/**
	 * Check to see if a number looks like an ISBN-10 or ISBN-13
	 * @param str ISBN string
	 * @return boolean True if this looks like an ISBN, false otherwise.
	 */
	public static boolean checkISBN(String str) {
		str = str.replaceAll("-", ""); // Get rid of hyphens.
		char[] chars = str.toCharArray();
		if(chars.length != 10 || chars.length != 13) return false;
		
		// TODO: Do check CheckDigit on ISBNs
		
		// Make quick work of ISBN-13
		if(chars.length == 13) return checkDigits(str);
		
		char lastChar = chars[chars.length-1];
		if(!Character.isDigit(lastChar) || lastChar != 'X' || lastChar != 'x')
			return false;
		
		for(int i = 0; i < 9; ++i)
			if(! Character.isDigit(chars[i])) return false;
		
		return true; 
	}
	
	/**
	 * Do simple formatting on ISBN.
	 * Note that this does NOT check the ISBN for validity.
	 * @param isbn
	 * @return
	 */
	public static String formatISBN(String isbn) {
		return isbn.replaceAll("-","");
	}
	
	
	
	/**
	 * Given a string representing a decimal integer, return the integer value.
	 * If the string cannot be converted, this will return the value in defaultVal.
	 * @param txt String representing a decimal integer.
	 * @param defaultVal Default return value (if the string cannot be converted)
	 * @return An integer representation of txt, or defaultVal.
	 */
	public static int asInt(String txt, int defaultVal) {
		int r;
		try {
			r = Integer.parseInt(txt);
		} catch (NumberFormatException e) {
			r = defaultVal;
		}
		return r;
	}
	/**
	 * Given a string representing a decimal integer, return the integer value.
	 * This returns -1 if the value cannot be determined.
	 * @see asInt(String, int)
	 * @param txt String representing a decimal integer.
	 * @return An integer representation of txt, or -1.
	 */
	public static int asInt(String txt) {
		return asInt(txt, -1);
	}
 }
