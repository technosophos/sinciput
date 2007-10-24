package com.technosophos.sinciput.xml;

import java.io.StringWriter;

import org.w3c.dom.*;

import javax.xml.xpath.*;
import javax.xml.transform.*;
//import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

/**
 * Tools to ease use of XML.
 * @author mbutcher
 *
 */
public class Helper {

	/**
	 * Return a NodeList of nodes that matched the given expression.
	 * @param xpathExpr Expression in the XPath expression language.
	 * @param start Element to treat as the root.
	 * @return matching nodes
	 * @see http://www.w3.org/TR/xpath
	 */
	public static NodeList matchNodes(String xpathExpr, Node start) {
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		//if(start == null)System.err.println("Node is null: " + start.getNodeName());
		//if(expr == null)System.err.println("Expression is null!" + expr);
		NodeList nl = null;
		try {
			Object o = xpath.evaluate(xpathExpr, start, XPathConstants.NODESET);
			if(o==null) return null;
			nl = (NodeList)o;
			xpath.reset();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nl;
	}
	
	/**
	 * Get the text contents (CDATA) from the first Node that matches the given XPath expression.
	 * <p>Effectively, this is like evaluating an XPath expression, getting the first child, and 
	 * calling the DOM method {@link Node.getTextContents()} on it. </p>
	 * @param xpathExpr A string representation of an XPath expression
	 * @param start Node to start the XPath evaluation from.
	 * @return The found text, or an empty String.
	 */
	public static String textContents(String xpathExpr, Node start) {
		NodeList l = Helper.matchNodes(xpathExpr, start);
		return l.getLength() > 0 ? l.item(0).getTextContent().trim():"";
	}
	
	/**
	 * Take any arbitrary node of a DOM tree, and serialize it to a String.
	 * @param n Some DOM node
	 * @return A string representation of the Node and its children.
	 */
	public static String domToString(Node n) {
		Source s = new DOMSource(n);
		StringWriter w = new StringWriter();
		Result r = new StreamResult(w);
		
		try {
			TransformerFactory.newInstance().newTransformer().transform(s, r);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return w.toString();
	}
}
