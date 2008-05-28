package com.technosophos.rhizome.web.xml.rhtml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
//import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
	 * <p>Note that no element validation is done on the HTML (though entity validation <i>is</i> done). 
	 * You will need to use jTidy or some other 
	 * HTML tool to do that.</p>
	 * @param html HTML in well-formed XML format
	 */
	public RHTML(String html) {
		this(html, true);
	}
	
	/**
	 * Construct a new RHTML object.
	 * <p>You probably want to use the other version of this constructor.</p>
	 * <p>An RHTML object is responsible for taking some well-formed HTML and converting it
	 * to a restricted subsset of HTML. Tags that are deemed dangerous are removed, as are 
	 * attributes that could cause problems.</p>
	 * <p>If you want to use HTML entities and the source document does not have a DTD, then
	 * you need to set useEntityDTD to true. Otherwise, any NON-XML entity (anything other than ampersand,
	 * greater-than, less-than, and double quotes) will result in a parse exception.</p>
	 * @param html HTML in well-formed XML format
	 * @param useEntityDTD If true, then this will automatically set up the entity DTD so that 
	 * standard XHTML entities can be used.
	 * @see RHTML(String)
	 */
	public RHTML(String html, boolean useEntityDTD) {
		if( useEntityDTD == true ) html = RHTMLEntities.getEntitiesDTD() + html;
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
		String html1 =
				/*" <!DOCTYPE doc [ "
				+ "<!ENTITY % HTMLlat1 PUBLIC \"-//W3C//ENTITIES Latin 1 for XHTML//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent\"> %HTMLlat1;"
				+ "<!ENTITY % HTMLspecial PUBLIC \"-//W3C//ENTITIES Special for XHTML//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent\"> %HTMLspecial;"
				+ "<!ENTITY % HTMLsymbol PUBLIC \"-//W3C//ENTITIES Symbols for XHTML//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent\"> %HTMLsymbol;"
				+ "]>"+
				*/
				//RHTMLEntities.getEntitiesDTD()+ "\n" +
				"<html><head></head><body><p>This is a test</p>" +
				"<script>alert();</script><br/>" +
				"<div>Hello &amp;&copy; world</div>" +
				"<a href=\"javascript:test()\">test</a>" +
				"<a href=\"http://test\">test</a>&Aacute;" +
				"<a href=\"/test\">test</a>" +
				"<p onload=\"doSomething()\">test</p>" +
				"<html:script xmlns:html=\"urn:test\">gooz()</html:script>"+
				"<EMBED><Applet/></EMBED>" +
				"<div ONLOAD=\"foo()\"/>" +
				"&gt;"+
				"<XHTML:DIV>TEST</XHTML:DIV>"+
				"&lt;&lt;&lt;"+
				"<span>a</span><span> </span>Test%a%b%c<span>%</span>"+
				"</body></html>";
		RHTML t = new RHTML(html1);
		System.out.println("Original:");
		System.out.println(html1);
		System.out.println("==============");
		System.out.println("Fixed:");
		try {
			String r = t.getRHTMLString();
			System.out.println(r);
			// Again to make sure we don't screw things up:
			System.out.println("Re-parsed:");
			t = new RHTML(r);
			r = t.getRHTMLString();
			System.out.println(r);
		} catch(Exception e) {
			e.printStackTrace(System.err);
		}
		//System.out.println(RHTMLEntities.summaryString());  
	}
	
}
