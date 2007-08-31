package com.technosophos.sinciput.xml;

import java.util.Arrays;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.Attributes;

/**
 * A SAX2 (ext) handler for a restricted subset of XHTML.
 * <p>This handler is used by RHTML, and probably never needs direct calling otherwise.</p>
 * 
 * <p>A document must be well-formed. Use jTidy or another cleaner if you are worried about this.</p>
 * <p>This is a very simple HTML restricting handler. It restricts which tags will be allowed,
 * and which attributes are allowed.</p>
 * <p>Why not use deXSS? Two reasons: (a) license is incompatible with GPL, and (b) it requires
 * tagSoup, which doesn't work on JDK > 1.4.x.</p>
 * @author mbutcher
 * 
 */
public class RHTMLHandler extends DefaultHandler2 {
	
	/**
	 * Elements disallowed in RHTML.
	 * <p>Elements not allowed in an RHTML document:</p>
	 * <code>
	 * "meta",
		"link",
		"bgsound",
		"param",
		"applet",
		"xml",
		"script",
		"embed",
		"server",
		"frame",
		"frameset"
	 * </code>
	 * <p>Note that not all of these are even standard HTML/XHTML tags... but they might be
	 * used in "real world" HTML. Blach.</p>
	 */
	public final static String[] ELEMENTS_BLACKLIST = {
		"meta",
		"link",
		"bgsound",
		"param",
		"applet",
		"xml",
		"script",
		"embed",
		"server",
		"frame",
		"frameset"
	};
	
	/**
	 * Elements that should be replaced by a span tag.
	 * <p>The elements in this category are:</p>
	 * <code>
	 * "iframe",
		"html",
		"body",
		"head",
		"layer",
		"ilayer",
		"object",
		"noscript"
	 * </code>
	 */
	public final static String[] ELEMENTS_REPLACE = {
		"iframe",
		"html",
		"body",
		"head",
		"layer",
		"ilayer",
		"object",
		"noscript"
	};
	
	/**
	 * Attributes to be omitted outright.
	 * <code>
	 * "dynsrc",
		"datasrc",
		"datafld",
		"dataformatas",
		"action",
		"method",
		"accesskey",
		"enctype",
		"method",
		"onblur",
		"onchange",
		"onclick",
		"ondblclick",
		"onfocus",
		"onkeydown",
		"onkeypress",
		"onkeyup",
		"onload",
		"onmousedown",
		"onmousemove",
		"onmouseout",
		"onmouseover",
		"onmouseup",
		"onreset",
		"onselect",
		"onsubmit",
		"onselect",
		"onsubmit",
		"onunload"
	 * </code>
	 */
	public final static String[] ATTRIBUTES_BLACKLIST = {
		//"on*",
		"dynsrc",
		"datasrc",
		"datafld",
		"dataformatas",
		"action",
		"method",
		"accesskey",
		"enctype",
		"method",
		"onblur",
		"onchange",
		"onclick",
		"ondblclick",
		"onfocus",
		"onkeydown",
		"onkeypress",
		"onkeyup",
		"onload",
		"onmousedown",
		"onmousemove",
		"onmouseout",
		"onmouseover",
		"onmouseup",
		"onreset",
		"onselect",
		"onsubmit",
		"onselect",
		"onsubmit",
		"onunload"
	};
	
	/**
	 * Attributes to be scanned for javascript/other script.
	 * <code>
	 * "src", "lowsrc", "href"
	 * </code>
	 */
	public final static String[] ATTRIBUTES_GREYLIST = {
		"src", "lowsrc", "href"
	};
	
	/**
	 * In greylist attributes, only allow these protocols. (Also
	 * allows relative and absolute paths.)
	 * <code>"http", "https", "ftp"
	 * </code>
	 */
	public final static String[] PROTOCOL_WHITELIST = {
		"http", "https", "ftp"
	};

	private StringBuilder sb = null;
	//private boolean skip_chars = false;
	//private int depth = 0;
	private int skip_children = 0;
	
	private String[] sorted_elements_blacklist = ELEMENTS_BLACKLIST.clone();
	private String[] sorted_elements_replace = ELEMENTS_REPLACE.clone();
	private String[] sorted_attributes_blacklist = ATTRIBUTES_BLACKLIST.clone();
	private String[] sorted_attributes_greylist = ATTRIBUTES_GREYLIST.clone();
	private String[] sorted_protocol_whitelist = PROTOCOL_WHITELIST.clone();
	
	//private String prevElement;
	//private String prevAttrs;
	//private int prevLevel = 0;
	
	public RHTMLHandler() {
		//sb = new StringBuilder();
		
		//this.prevElement = null;
		//this.prevAttrs = "";
		//this.prevLevel = 0;
		
		// TODO: test whether using a hashSet might be faster than sorting and 
		// binary search.
		
		// Prepare these for binary searching:
		Arrays.sort(this.sorted_elements_blacklist);
		Arrays.sort(this.sorted_elements_replace);
		Arrays.sort(this.sorted_attributes_blacklist);
		Arrays.sort(this.sorted_attributes_greylist);
		Arrays.sort(this.sorted_protocol_whitelist);
		
	}
	
	/**
	 * Once parsing is finished, use this to get the results.
	 * @return Cleaned RHTML string.
	 */
	public String getRHTMLString() {
		return this.sb.toString();
	}
	
	// // // // // // SAX overrides // // // // // // // // //
	
	public void startDocument() {
		this.sb = new StringBuilder();
		//this.depth = 0;
		this.skip_children = 0;
	}
	
	public void startElement(String uri, String lname, String qname, Attributes attrs) {
		//++depth;
		
		String name;
		int c;
		if( (c = qname.indexOf(':')) >= 0)
			name = qname.substring(c+1).toLowerCase();
		else name = qname.toLowerCase();
		
		// Check on status of this element:
		System.out.println("Name: " + name);
		if( Arrays.binarySearch(this.sorted_elements_blacklist, name) >= 0 ) {
			// blacklist
			System.out.println("Skip children ++ for " + name);
			++this.skip_children;
		} else if(Arrays.binarySearch(this.sorted_elements_replace, name) >= 0) {
			// Replace element with span.
			//this.addElement("span", null); // Don't translate attributes
			if(this.skip_children == 0)
				this.sb.append("<span>");
		} else {
			if( this.skip_children > 0) return; // Skip adding this...
			// Element is okay, get attrs.
			int i, j = attrs.getLength();
			StringBuilder sbAttr = new StringBuilder();
			for(i = 0; i < j; ++i) {
				sbAttr.append( this.cleanAttr(attrs.getQName(i), attrs.getValue(i)));
			}
			this.addElement(qname, sbAttr.toString());
		}
		System.out.println(this.skip_children);
	}
	
	public void endElement(String uri, String lname, String qname ) {
		//--depth;
		
		String name;
		int j;
		if( (j = qname.indexOf(':')) >= 0)
			name = qname.substring(j+1).toLowerCase();
		else name = qname.toLowerCase();
		
		//System.out.format("QNAME: %s, NAME: %s\n", qname, name);
		// FIXME: With depth + element name, should be able to skip this.
		if( Arrays.binarySearch(this.sorted_elements_blacklist, name) >= 0 ) {
			// blacklist
			System.out.println("Skip children -- for " + name);
			--this.skip_children;
		} else if(Arrays.binarySearch(this.sorted_elements_replace, name) >= 0) {
			//this.addElement("span");
			this.sb.append("</span>");
		} else {
			this.sb.append("</");
			this.sb.append(qname);
			this.sb.append('>');
		}
		System.out.println(this.skip_children);
	}

	public void characters(char[] ch, int start, int len) {
		
		if(this.skip_children < 1) {
			String str = new String(ch, start, len);
			System.out.println("Characters was called with " + str);
			// According to XML spec, these chars have to be resolved by parser.
			// Now we need to reverse that:
			if( len == 1 ) { 
				// FIXME: Is this parser-specific behavior? Or do all parsers report decoded
				// entities as single char pcdata?
				switch(ch[start]) {
				case '<':
					//System.out.println("Got an LT");
					sb.append("&lt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				// MORE...
				}
			// else just append string:
			} else sb.append(ch, start, len);
		}
	}
	
	public void startCDATA() {
		++this.skip_children;
	}
	
	public void endCDATA() {
		--this.skip_children;
	}
	
	public void endDocument() {
		
	}
	
	public void startEntity(String n) {
		System.out.println("Starting entity.");
	}
	// // // // // // // // Utilities // // // // // // // // //
	protected String cleanAttr(String name, String val) {
		
		// Get rid of namespace prefix:
		String rname;
		int c = name.indexOf(':');
		if( c >= 0 ) rname = name.substring(c).toLowerCase();
		else rname = name.toLowerCase();
		
		StringBuilder attr = new StringBuilder( name.length() + val.length() + 5);
		if( Arrays.binarySearch(this.sorted_attributes_blacklist, rname) >= 0 ) {
			// Do nothing... attribute is blacklisted.
			attr.append("");
		} else if(Arrays.binarySearch(this.sorted_attributes_greylist, rname) >= 0) {
			// Find out if the protocol is correct:
			int colon = -1;
			if( (colon = val.indexOf(':')) >= 0) {
				String proto= val.substring(0, colon);
				if(Arrays.binarySearch(this.sorted_protocol_whitelist, proto) >= 0) {
					// Protocol is OK; write the attribute.
					attr.append(' ');
					attr.append(name);
					attr.append("=\"");
					attr.append(val);
					attr.append('"');
				}
			} else { 
				// This attribute is okay.
				// The item in the path is either a relative or absolute path.
				attr.append(' ');
				attr.append(name);
				attr.append("=\"");
				attr.append(val);
				attr.append('"');
			}
			
		} else {
			// This attribute is okay.
			// The attribute is not in a black or greylist
			attr.append(' ');
			attr.append(name);
			attr.append("=\"");
			attr.append(val);
			attr.append('"');
		}
		return attr.toString();
	}
	
	private void addElement(String name, String attributes) {
		
		sb.append('<');
		sb.append(name);
		if(attributes != null) sb.append(attributes);
		sb.append('>');
	}
	/*
	 * FIXME: Add level code in to calculate whether start and end element should
	 * be condensed, e.g. <br></br> => <br/>
	private void addStartElement(String name, String attributes) {
		if( this.prevElement != null) {
			sb.append('<');
			sb.append(prevElement);
			if(prevAttrs != null) sb.append(prevAttrs);
			sb.append('>');
		}
		this.prevElement = name;
		this.prevAttrs = attributes;
	}
	
	private void addEndElement(String name) {
		if( this.prevElement.equalsIgnoreCase(name)) {
			sb.append('<');
			sb.append(name);
			if(prevAttrs != null) sb.append(prevAttrs);
			sb.append("/>");
		}
	}
	*/
	
}
