package com.technosophos.rhizome.repository.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.binary.Base64;

/**
 * Utilities for creating and managing repositories.
 * @author mbutcher
 *
 */
public class RepositoryUtils {

	/**
	 * Generate a sutable file name for a repository.
	 * This returns a base64-encoded name.
	 * * <p>This takes the title and creates a String representation of
	 * this title's 32-bit SHA1 hash. (It is bas64 encoded)</p>
	 * <p>Apparently, there is a 2<sup>63</sup> possibility of an unintended
	 * collision. This, I think, is OK for our purposes. A UUID generator or something of
	 * that ilk might be another candidate, here.</p> 
	 * @param title
	 * @return
	 */
	/*
	 * <p><b>WARNING:</b> In the future, a different ID algo may be substituted here
	 * (such as SHA1 or UUID). Do not assume that the repo ID's value can be decoded.</p>
	 * <p>I am currently using base64 for a few simple reasons: (a) it is compact, (b) it 
	 * generates strings that can be used as filenames, (c) we can make some simplifying
	 * assumptions about the existence of an archive.</p>
	 * <p>There are some downsides to using this method, though -- not the least of which is 
	 * the fact that there is no fixed length.</p>  	 
	 */
	public static String generateRepoID( String title ) {
		//String  DigestUtils.shaHex(title);
		byte [] b64 = Base64.encodeBase64(DigestUtils.sha(title.getBytes()));
		// FIXME: Do we want to chop off the last byte?
		return new String(b64);
	}
	
}
