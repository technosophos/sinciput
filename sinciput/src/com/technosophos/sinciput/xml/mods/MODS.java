package com.technosophos.sinciput.xml.mods;

import org.betterxml.xelement.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;

/**
 * MODS document.
 * <p>MODS (Metadata Object Description Schema) is a Library of Congress standard format
 * for expressing metadata about objects -- typically objects collected in reference
 * units like libraries.</p>
 * 
 * <p>This implementation provides read-only access to a large subset of a MODS document.</p>
 * 
 * <p><b>This class is EXPERIMENTAL</b>, and it may change considerably.</p>
 * 
 * <h2>Skipped Parts:</h2>
 * <ul>
 * <li>physicalDescription is skipped in its entirety.</li>
 * <li>extension is skipped</li>
 * <li>accessCondition is skipped</li>
 * <li>location/physicalLocation is skipped</li>
 * <li></li>
 * </ul>
 * 
 * @author mbutcher
 * @see http://www.loc.gov/standards/mods/
 */
public class MODS {

	//private XElement mxml = null;
	private Element root = null;
	private XPath xpath = null;
	
	private MODS() {}
	
	/**
	 * Create a new MODS object from a string.
	 * @param data
	 * @throws SAXException If there is an error creating the parser.
	 * @throws XElementException If parsing fails.
	 */
	public MODS( String data ) throws org.xml.sax.SAXException, ParserConfigurationException, IOException, MODSException {
		this(new InputSource(new StringReader(data)));
	}
	
	/**
	 * This takes a &lt;mods/&gt; or &lt;relatedItem/&gt; element.
	 * @param e
	 * @throws MODSException if the element name is not correct.
	 */
	public MODS(Element e) throws MODSException {
		if(!"mods".equalsIgnoreCase(e.getTagName())
				&& !"relatedItem".equals(e.getTagName())) 
			throw new MODSException("Root element is not a MODS element: " + e.getTagName());
		this.root = e;
	}
	/**
	 * Create a new MODS object from an input source
	 * @param in
	 * @throws SAXException Parse error
	 * @throws ParserConfigurationException Error creating parser
	 * @throws IOException Error accessing input source
	 * @throws MODSException This is not a MODS document/construct.
	 */
	public MODS( InputSource in ) throws SAXException, ParserConfigurationException, IOException, MODSException {
		
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		/*
		f.setSchema(schema);
		f.setIgnoringElementContentWhitespace(whitespace);
		*/
		DocumentBuilder builder = f.newDocumentBuilder();
		Document d = builder.parse(in);
		Element e = d.getDocumentElement();
		if(!"mods".equalsIgnoreCase(e.getTagName())) 
			throw new MODSException("Root element is not a MODS element: " + e.getTagName());
		
		this.root = e;
		
		this.xpath = XPathFactory.newInstance().newXPath();
		/*
		XParser parser = new XParser();
		XDocument doc = parser.parse(in);
		XElement ele  = doc.getRootElement();
		if( ! "mods".equalsIgnoreCase(ele.getName()))
			throw new MODSException("Root element is not a MODS element");
		this.mxml = ele;
		*/
	}
	
	
	/**
	 * Get the names of creators, editors, translators, etc.
	 * <p>The results are returned as a list of Name objects, where each name 
	 * will have the name of the responisble party plus (possibly) the role they
	 * played.</p>
	 * @return List of Name objects.
	 */
	public List<Name> getNames() {
		
		NodeList l = xEvalNL("name");//root.getElementsByTagName("name");
		ArrayList<Name> nameList = new ArrayList<Name>();
		int i, j = l.getLength();
		for(i = 0; i < j; ++i)
			nameList.add(new Name( (Element)l.item(i) ));
		
		return nameList;
	}
	
	/**
	 * Get the record type.
	 * The following types are known to exist:
	 * <ul>
	 *  <li>text</li>
	 *  <li>cartograph</li>
	 *  <li>notated music</li>
	 *  <li> sound recording-musical</li>
	 *  <li>sound recording-nonmusical</li>
	 *  <li>sound recording</li>
	 *  <li>still image</li>
	 *  <li>moving image</li>
	 *  <li>three dimensional object</li>
	 *  <li>software</li>
	 *  <li>multimedia</li>
	 *  <li>mixed material</li>
	 * </ul>
	 * @return Type, if set, or null.
	 */
	public String getTypeOfResource() {
		return this.firstEContents("typeOfResource");
	}
	
	/**
	 * Get the genre.
	 * @return
	 */
	public String getGenre() {
		return this.firstEContents("genre");
	}
	
	/**
	 * Get the first reported language.
	 * Note that at this time, we don't try to figure out whether this is ISO or RFC3066
	 * @return Language code (i.e. en or eng)
	 */
	public String getLanguage() {
		return this.firstEContents("language/languageTerm");
	}
	/**
	 * Get the abstract.
	 * @return
	 */
	public String getAbstract() {
		return this.firstEContents("abstract");
	}
	/**
	 * Get the table of contents.
	 * @return
	 */
	public String getTableOfContents() {
		return this.firstEContents("tableOfContents");
	}
	/**
	 * Get target Audience.
	 * @return
	 */
	public String getTargetAudience() {
		return this.firstEContents("targetAudience");
	}
	/**
	 * Get notes.
	 * @return
	 */
	public String getNote() {
		return this.firstEContents("note");
	}
	/**
	 * Get info about the origin of the source.
	 * <p>Location, publisher, and publishing data re items in the Origin info.</p>
	 * @return A populated OriginInfo document.
	 */
	public OriginInfo getOriginInfo() {
		return new OriginInfo();
	}
	
	/**
	 * Get a list of subjects.
	 * @return
	 */
	public List<Subject> getSubjects() {
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		
		NodeList nl = xEvalNL("subject");//root.getElementsByTagName("subject");
		int i, j = nl.getLength();
		for(i=0; i<j; ++i) { subjects.add(new Subject(nl.item(i))); }
		
		return subjects;
	}
	/**
	 * Get a list of classifications.
	 * <p>Example classifications: LOC number, Dewey Decimal number, maybe ISBN....</p>
	 * @return List of Classification objects.
	 */
	public List<Classification> getClassifications() {
		ArrayList<Classification> clss = new ArrayList<Classification>();
		
		NodeList nl = xEvalNL("classification");//root.getElementsByTagName("classification");
		int i, j = nl.getLength();
		for(i=0; i<j; ++i) { clss.add(new Classification((Element)nl.item(i))); }
		
		return clss;
	}
	
	public TitleInfo getTitleInfo() {
		return new TitleInfo();
	}
	
	/**
	 * Collect parts.
	 * These should only occur journals and other serials.
	 * @return List of parts of this work.
	 */
	public List<Part> getParts() {
		ArrayList<Part> p = new ArrayList<Part>();
		NodeList nl = xEvalNL("part");
		int i, j = nl.getLength();
		for(i=0; i<j; ++i) { p.add(new Part((Element)nl.item(i))); }
		
		return p;
	}
	
	/**
	 * Get Identifiers (like ISBN, ASSN, and so on).
	 * <h2>Known identifiers</h2>
	 * <pre>
	 * doi (Digital Objects Identifier)
     * hdl (Handle)
     * isbn (International Standard Book Number)
     * ismn (International Standard Music Number)
     * isrc (International Standard Recording Code)
     * issn (International Standard Serials Number)
     * issue number
     * istc (International Standard Text Code)
     * lccn (Library of Congress Control Number)
     * local
     * matrix number
     * music plate
     * music publisher
     * sici (Serial Item and Contribution Identifier)
     * stock number
     * upc (Universal Product Code)
     * uri (Uniform Resource Identifier)
     * videorecording identifier 
     * </pre>
	 * @return
	 * @see http://www.loc.gov/standards/mods/v3/mods-userguide-elements.html#location
	 */
	public List<Identifier> getIdentifiers() {
		ArrayList<Identifier> p = new ArrayList<Identifier>();
		NodeList nl = xEvalNL("identifier");
		int i, j = nl.getLength();
		for(i=0; i<j; ++i) { p.add(new Identifier((Element)nl.item(i))); }
		
		return p;
	}
	
	/**
	 * Get the location URL if specified.
	 * <p>Todo: A label may come with a URL. Fetch the label, too.</p>
	 * @return
	 */
	public List<LocationURL> getLocationURLs() {
		ArrayList<LocationURL> p = new ArrayList<LocationURL>();
		NodeList nl = xEvalNL("location/url");
		int i, j = nl.getLength();
		for(i=0; i<j; ++i) { p.add(new LocationURL((Element)nl.item(i))); }
		return p;
	}
	
	/**
	 * Returns true if the record looks like it is a journal.
	 * <p>It guesses by examining the Issuance (and Part?).</p>
	 * @return true if this looks like a journal.
	 * @deprecated
	 */
	public boolean looksLikeJournal() {
		return "continuing".equalsIgnoreCase(this.getOriginInfo().getIssuance());
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Title: ").append(this.getTitleInfo().toString()).append("\n");
		sb.append("Names: ").append(this.listToString(this.getNames())).append("\n");
		sb.append("Origin Info: ").append(this.getOriginInfo()).append("\n");
		sb.append("Identifiers: ").append(this.listToString(this.getIdentifiers())).append("\n");
		sb.append("Classifications: ").append(this.listToString(this.getClassifications())).append("\n");
		sb.append("URLs: ").append(this.listToString(this.getLocationURLs())).append("\n");
		sb.append("Language: ").append(this.getLanguage()).append("\n");
		sb.append("Subjects: ").append(this.listToString(this.getSubjects())).append("\n");
		sb.append("Genre: ").append(this.getGenre()).append("\n");
		sb.append("Abstract: ").append(this.getAbstract()).append("\n");
		sb.append("Note: ").append(this.getNote()).append("\n");
		sb.append("Table of Contents: ").append(this.getTableOfContents()).append("\n");
		sb.append("Target Audience: ").append(this.getTargetAudience()).append("\n");
		sb.append("Part: ").append(this.listToString(this.getParts())).append("\n");
		
		
		List<MODS> rel = this.getRelatedItems();
		if(rel.size() > 0)
			sb.append("=== RELATED:===\n").append(this.listToString(rel)).append("=====\n");
		
		return sb.toString();
	}
	
	/**
	 * Get a related MODS item.
	 * <p>A MODS item may have one or more nested MODS items. Almost always, the nested item is the 
	 * parent of the given item.</p>
	 * @return
	 */
	public List<MODS> getRelatedItems() {
		ArrayList<MODS> reli = new ArrayList<MODS>();
		
		NodeList nl = xEvalNL("relatedItem");
		int i, j = nl.getLength();
		for(i=0; i<j; ++i) {
			try {
				reli.add(new MODS((Element)nl.item(i)));
			} catch (MODSException e) {System.err.println("BAD:"+e.getMessage());}
		}
		
		return reli;
	}
	
	
	/*
	 * BEGIN INNER CLASSES HERE
	 * We use some inner classes because originally this was quite small. Now, it's obviously much
	 * bigger, and a refactoring is probably a good idea.
	 */
	//=============================================================================================
	// // // // // // // CLASSES // // // // // //
	
	/**
	 * Title information from a MODS document.
	 * <p>To sort: you probably want getTitle().</p>
	 * <p>To get a full title: use getFullTitle()</p>
	 */
	public class TitleInfo {
		private String nonSort = null;
		private String title = null;
		private String subTitle = null;
		private String partNumber = null;
		private String partName = null;
		
		public TitleInfo() {
			
			//NON-SORT
			String q = "titleInfo/nonSort";
			NodeList l = xEvalNL(q);
			nonSort = l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
			
			//TITLE
			q = "titleInfo/title";
			l = xEvalNL(q);
			title = l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
			
			//SUBTITLE
			q = "titleInfo/subTitle";
			l = xEvalNL(q);
			subTitle = l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
			
			//PART NUMBER & NAME
			q = "titleInfo/partNumber";
			l = xEvalNL(q);
			partNumber = l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
			
			q = "titleInfo/partName";
			l = xEvalNL(q);
			partName = l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
		}
		/**
		 * Get the full title.
		 * This is non-sort plus title plus subtitle.
		 */
		public String getFullTitle() {
			StringBuilder sb = new StringBuilder();
			if( nonSort.length() > 0 ) sb.append(nonSort).append(" ");
			sb.append(title);
			if( subTitle.length() > 0 ) sb.append(": ").append(subTitle);
			if( partNumber.length() > 0 ) sb.append(" (").append(partNumber).append(") ");
			if( partName.length() > 0 ) sb.append("--").append(partName).append(" ");
			
			return sb.toString();
		}
		
		/**
		 * Title suitable for natural sorting.
		 * <p>Leading articles such as 'A' and 'The' are appended after subtitle, instead of prefixed
		 * to the front. The accuracy of this is dependent on the categorization in the MODS
		 * document. If articles are not categorized as non-sort, then this will not catche them.</p>
		 * @return
		 */
		public String getSortableTitle() {
			StringBuilder sb = new StringBuilder();
			sb.append(title);
			if( subTitle.length() > 0 ) sb.append(": ").append(subTitle);
			if( nonSort.length() > 0 ) sb.append(", ").append(nonSort);
			if( partNumber.length() > 0l ) sb.append(" (").append(partNumber).append(") ");
			if( partName.length() > 0 ) sb.append(" -- ").append(partName).append(" ");
			
			return sb.toString();
		}
		
		/**
		 * Return nonsortable title prefixe (e.g. 'the')
		 * @return
		 */
		public String getTitleNonSort() {return nonSort;}
		
		/**
		 * Get the short title (title sans non-sort and subtitle)
		 * @return
		 */
		public String getTitle() {return title;}
		
		/**
		 * Get the subtitle only.
		 * @return
		 */
		public String getSubTitle() {return subTitle;}
		
		public String toString(){ return this.getFullTitle();}
	}
	
	/**
	 * Describes a name attached to this work.
	 * <p>Usually, a name is an author (creator), editor, or translator.</p>
	 * @author mbutcher
	 *
	 */
	public class Name {
		
		String nname = null;
		ArrayList<String> nlist = null;
		String gname = null, sname=null, dates=null;
		
		public Name(Element ele) {
			nlist = new ArrayList<String>();
			
			// Get name part
			NodeList nl = ele.getElementsByTagName("namePart");
			if(nl.getLength() > 0 ) {
				
				/*
				 * Unfortunate situation:
				 * Some names come as given name, surname pairs.
				 * Others come preformatted as <sname>, <gname>
				 * Others are (seemingly) arbitrary.
				 * 
				 * We just take a good shot...
				 */
				int i, j = nl.getLength();
				//String gname = null, sname = null;
				StringBuilder sb = new StringBuilder();
				for(i=0;i<j;++i) {
					Element ee = (Element)nl.item(i);
					if(ee.hasAttribute("type")) {
						String t = ee.getAttribute("type"); 
						if(t.equals("family")) this.sname = ee.getTextContent().trim();
						else if(t.equals("given")) this.gname = ee.getTextContent().trim();
						else if(t.equals("date")) this.dates = ee.getTextContent().trim();
					}
					if(sb.length() > 0) sb.append(' ');
					sb.append(ee.getTextContent().trim());
				}
				nname = (gname != null && sname != null) 
					? (dates == null ? String.format("%s, %s", sname, gname): String.format("%s, %s %s", sname, gname, dates))
					: sb.toString();
			}
			
			// Get roleTerms
			nl = xEvalNL("role/roleTerm", ele);
			if( nl.getLength() > 0) {
				
				Node n;
				for(int i = 0; i < nl.getLength(); ++i) {
					n = nl.item(i);
					nlist.add(((Element)n).getTextContent().trim());
				}
			}
		}
		public String getNamePart() {return nname;}
		public String getFamilyName(){return sname;}
		public String getGivenName(){return gname;}
		public String getDates(){return dates;}
		public List<String> getRoles() {return nlist;}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			return sb.append(nname).append(" (").append(listToString(nlist)).append(") ").toString();
		}
	}
	
	/**
	 * Information about a resource's origin.
	 * <p>Much of the extraneous date material has been left out.</p>
	 */
	public class OriginInfo {
		
		String place;
		String publisher;
		String dateIssued;
		String edition;
		String issuance;
		String frequency;
		
		public OriginInfo() {
			
			// PLACE
			String q = "originInfo/place/placeTerm[@type='text']";
			NodeList l = xEvalNL(q);
			if(l.getLength() > 0 ) {
				Element e = (Element)l.item(0);
				place = e.getTextContent().trim();
			} else {
				// Try again...
				q = "originInfo/place/placeTerm";
				l = xEvalNL(q);
				place =  l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
			}
			
			// PUBLISHER
			q = "originInfo/publisher";
			l = xEvalNL(q);
			publisher = l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
			
			// DATE ISSUED
			q = "originInfo/dateIssued";
			l = xEvalNL(q);
			dateIssued = l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
			
			// ISSUENCE
			q = "originInfo/issuance";
			l = xEvalNL(q);
			issuance = l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
			if(issuance.length() > 0) {
				q = "originInfo/frequency";
				l = xEvalNL(q);
				frequency = l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
			}
			// Edition
			q = "originInfo/edition";
			l = xEvalNL(q);
			edition = l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "" ;
		}
		
		/**
		 * Get the most sensible place term.
		 * @return
		 */
		public String getPlace() {return place;}
		public String getPublisher() {return publisher;}
		public String getDateIssued() {return dateIssued;}
		public String getEdition() {return edition;}
		public String getIssuance() {return issuance;}
		public String getFrequency() {return frequency;}
		public String toString() {return String.format("%s: %s, %s", place, publisher, dateIssued);}

	}
	
	/**
	 * Get subject information.
	 * This gives access to basic subject information, but it does not currently handle hierarchic 
	 * content.
	 * @author mbutcher
	 *
	 */
	public class Subject {
		ArrayList<SubjectTerm> terms = null;
		public Subject(Node n) {
			terms = new ArrayList<SubjectTerm>();
			
			NodeList nl2 = n.getChildNodes();
			Node n2;
			int ii, jj = nl2.getLength();
			for(ii=0;ii<jj;++ii) {
				n2 = nl2.item(ii);
				if(Node.ELEMENT_NODE == n2.getNodeType()) {
					Element ee = (Element)n2;
					terms.add(new SubjectTerm( ee.getTextContent().trim() , ee.getTagName()));
				}
			}
		}
		public List<SubjectTerm> getTerms() {return terms;}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(SubjectTerm t: terms) {
				if(sb.length() > 0) sb.append("::");
				sb.append(t.toString());
			}
			return sb.toString();
		}
	}
	
	/**
	 * Describes a component part of a Subject.
	 * @author mbutcher
	 *
	 */
	public class SubjectTerm {
		private String svalue = null;
		private String stype = null;
		public SubjectTerm(String value, String type) {
			svalue = value;
			stype = type;
		}
		public String getValue(){return svalue;}
		public String getType(){return stype;}
		public String toString() {return svalue;} //+ "(" + stype + ")";}
	}
	
	/**
	 * Provides information on the classification system used.
	 * <p>The classification provides source identification information. E.g. Dewey Decimal Number
	 * or Library of Congress Code (LCC, not LCCN).</p>
	 * @author mbutcher
	 *
	 */
	public class Classification {
		String cvalue = null;
		String cauth = null;
		public Classification(Element e) {
			String authstr = "authority";
			cauth = e.hasAttribute(authstr) ? e.getAttribute(authstr) : "";
			cvalue = e.getTextContent().trim();
		}
		public String getValue(){return cvalue;}
		public String getAuthority(){return cauth;}
		public String toString(){ return String.format("%s:%s", cauth, cvalue);}
	}
	
	/**
	 * Describes a part.
	 * <p>In general, a part object contains information about what PART of a larger whole this
	 * particular MODS element describes. Example: An article is a part of a journal.</p>
	 * @author mbutcher
	 *
	 */
	public class Part {
		
		private String date, text, ID, type, order;
		private PartDetail partDetail = null;
		private PartExtent partExtent = null;
		
		public Part(Element e) {
			
			// Get attribtues
			ID = e.hasAttribute("ID") ? e.getAttribute("ID") : "";
			type = e.hasAttribute("type") ? e.getAttribute("type") : "";
			order = e.hasAttribute("order") ? e.getAttribute("order") : "";
			
			// Get subelements
			NodeList nl = e.getElementsByTagName("text");
			text = nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
			
			nl = e.getElementsByTagName("date");
			date = nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
			
			nl = e.getElementsByTagName("detail");
			if(nl.getLength() > 0) {
				partDetail = new PartDetail((Element)nl.item(0));
			}
			nl = e.getElementsByTagName("extent");
			if(nl.getLength() > 0) {
				partExtent = new PartExtent((Element)nl.item(0));
			}
			return;
		}
		public String getDate(){return this.date;}
		public String getID(){return this.ID;}
		public String getType(){return this.type;}
		public String getOrder(){return this.order;}
		public String getText(){return this.text;}
		public PartDetail getPartDetail(){return this.partDetail;}
		public PartExtent getPartExtent(){return this.partExtent;}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(type.length() > 0) sb.append("Type:").append(this.type).append(" ");
			if(partDetail != null) sb.append(partDetail.toString());
			if(partExtent != null) sb.append(partExtent.toString());
			return sb.toString();
		}
	}
	
	/**
	 * The detail section of a part.
	 * @author mbutcher
	 *
	 */
	public class PartDetail {
		private String type, order, number, caption, title;
		public PartDetail(Element e) {
			type = e.hasAttribute("type") ? e.getAttribute("type") : "";
			order = e.hasAttribute("order") ? e.getAttribute("order") : "";
			
			NodeList nl = e.getElementsByTagName("number");
			number = nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
			nl = e.getElementsByTagName("caption");
			caption = nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
			nl = e.getElementsByTagName("title");
			title = nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
		}
		
		public String getType(){return this.type;}
		public String getOrder(){return this.order;}
		public String getNumber(){return this.number;}
		public String getCaption(){return this.caption;}
		public String getTitle(){return this.title;}
		public String toString(){ return String.format("Detail %s (%s)", title, type);};
	}
	
	/**
	 * Describes a part extent.
	 * @author mbutcher
	 *
	 */
	public class PartExtent {
		private String unit, start, end, total, list;
		public PartExtent(Element e) {
			unit = e.hasAttribute("unit") ? e.getAttribute("unit") : "";
			NodeList nl = e.getElementsByTagName("start");
			start = nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
			nl = e.getElementsByTagName("end");
			end = nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
			nl = e.getElementsByTagName("total");
			total = nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
			nl = e.getElementsByTagName("list");
			list = nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
		}
		public String getUnit(){return this.unit;}
		public String getStart(){return this.start;}
		public String getEnd(){return this.end;}
		public String getTotal(){return this.total;}
		public String getList(){return this.list;}
		public String toString(){ return String.format("Extent: %s", this.list); }
	}
	
	// Finish me!
	/**
	 * Class for Unique Identifiers.
	 * <p>A Unique Identifier covers things like ISBN, LCCN, ASSN, and similar fields.
	 * The spec if very vague.</p>
	 * @see http://www.loc.gov/standards/mods/v3/mods-userguide-elements.html#identifier
	 */
	public class Identifier {
		private static final String TYPE_NAME = "type"; 
		private static final String LABEL_NAME = "displayLabel";
		private String label = null;
		private String type = null;
		private String value = null;
		
		public Identifier(Element e) {
			type = e.hasAttribute(TYPE_NAME) ? e.getAttribute(TYPE_NAME) : ""; 
			label = e.hasAttribute(LABEL_NAME) ? e.getAttribute(LABEL_NAME) : "";
			value = e.getTextContent().trim();
		}
		public String getType(){return this.type;}
		public String getDisplayLable(){return this.label;}
		public String getValue(){return this.value;}
		public String toURI() { return String.format("%s:%s", this.type, this.value); }
		public String toString(){return this.toURI();}
	}
	
	/**
	 * Describes a URL stored in a Location field.
	 * 
	 * @author mbutcher
	 */
	public class LocationURL {
		private String displayLabel = null;
		private String urlString = null;
		public LocationURL(Element e) {
			displayLabel = e.hasAttribute("displayLabel") ? e.getAttribute("displayLabel") : "";
			urlString = e.getTextContent().trim();
		}
		public String getURL(){return this.urlString;}
		public String getDisplayLabel(){return this.displayLabel;}
		public String toString(){return this.getURL();}
	}

	//=============================================================================================
	// // // // // // // PRIVATE METHODS // // // // // //
	
	private String firstEContents(String expr) { return this.firstEContents(expr, this.root); }
	
	private String firstEContents(String expr, Node start) {
		NodeList l = this.xEvalNL(expr, start);
		return l.getLength() > 0 ? ((Element)l.item(0)).getTextContent().trim() : "";
	}
	
	private NodeList xEvalNL(String expr) {
		return this.xEvalNL(expr, this.root);
	}
	
	private NodeList xEvalNL(String expr, Node start) {
		if( this.xpath == null) {
			this.xpath = XPathFactory.newInstance().newXPath();
		}
		
		//if(start == null)System.err.println("Node is null: " + start.getNodeName());
		//if(expr == null)System.err.println("Expression is null!" + expr);
		NodeList nl = null;
		try {
			Object o = this.xpath.evaluate(expr, start, XPathConstants.NODESET);
			if(o==null) return null;
			nl = (NodeList)o;
			this.xpath.reset();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nl;
	}
	
	private <T> String listToString(List<T> l) {
		StringBuilder sb = new StringBuilder();
		for(T t: l) {
			if(sb.length() > 0 ) sb.append("; ");
			sb.append(t.toString());
		}
		return sb.toString();
	}
	
	// Testing:
	public static void main (String [] args) {
		try {
			String str = "http://www.loc.gov/standards/mods/v3/mods86646620.xml";
			//String str = "http://www.loc.gov/standards/mods/v3/mods99042030.xml";
			//String str = "http://www.loc.gov/standards/mods/v3/modsbook-chapter.xml";
			//String str = "http://www.loc.gov/standards/mods/v3/modsjournal.xml";
			java.net.URL url = new java.net.URL(str);
			Object o = url.getContent();
			//System.err.println(o.toString());
			if(o instanceof java.io.InputStream ) {
				MODS m = new MODS(new InputSource((java.io.InputStream)o));
				System.out.print(m.toString());
			} else {
				System.err.println("No idea what kind of source this is!");
			}
				
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e ) {
			e.printStackTrace();
		}
		return;
	}
}
