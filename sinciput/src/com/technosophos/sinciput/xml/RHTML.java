package com.technosophos.sinciput.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
//import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.technosophos.sinciput.xml.RHTMLHandler;
import java.io.StringReader;

/**
 * This class take well-formed (and hopefully valid) (X)HTML and removes undesirable elements and attributes.
 *  
 * @author mbutcher
 * @see RHTMLHandler
 */
public class RHTML {
	private RHTMLHandler h;
	private InputSource is;
	
	private RHTML(){}
	
	/**
	 * Construct a new RHTML object.
	 * <p>An RHTML object is responsible for taking some well-formed HTML and converting it
	 * to a restricted subsset of HTML. Tags that are deemed dangerous are removed, as are 
	 * attributes that could cause problems.</p>
	 * <p>Note that no validation is done on the HTML. You will need to use jTidy or some other 
	 * HTML tool to do that.</p>
	 * @param html HTML in well-formed XML format
	 */
	public RHTML(String html) {
		this.h = new RHTMLHandler();
		this.is = new InputSource(new StringReader(html));
	}
	
	/**
	 * Get the RHTML string.
	 * This processes the HTML passed to the RHTML constructor and returns a cleaned string
	 * with illegal elements removed.
	 * @return A String with elements and attributes cleaned.
	 * @throws SAXException
	 * @throws java.io.IOException
	 */
	public String getRHTMLString() throws SAXException, java.io.IOException {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(is, h);
		} catch (ParserConfigurationException e) {
			throw new SAXException(e);
		}
		return h.getRHTMLString();
	}
	
	/**
	 * Simple testing method.
	 * @param argv
	 */
	public static void main( String[] argv) {
		String html1 = "<html><head></head><body><p>This is a test</p>" +
				"<script>alert();</script><br/>" +
				"<div>Hello &amp; world</div>" +
				"<a href=\"javascript:test()\">test</a>" +
				"<a href=\"http://test\">test</a>" +
				"<a href=\"/test\">test</a>" +
				"<p onload=\"doSomething()\">test</p>" +
				"<html:script xmlns:html=\"urn:test\">gooz()</html:script>"+
				"<EMBED><Applet/></EMBED>" +
				"<div ONLOAD=\"foo()\"/>" +
				"&gt;"+
				"<XHTML:DIV>TEST</XHTML:DIV>"+
				"&lt;&lt;&lt;"+
				"</body></html>";
		RHTML t = new RHTML(html1);
		System.out.println("Original:");
		System.out.println(html1);
		System.out.println("==============");
		System.out.println("Fixed:");
		try {
		System.out.println(t.getRHTMLString());
		} catch(Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
}
