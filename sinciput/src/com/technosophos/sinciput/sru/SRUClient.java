package com.technosophos.sinciput.sru;

//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.IOException;

import javax.xml.parsers.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
//import org.betterxml.xelement.*;

import com.technosophos.sinciput.xml.mods.MODS;
import com.technosophos.sinciput.xml.dc.SRWDC;

import static com.technosophos.sinciput.xml.Helper.*;

/**
 * This class implements an SRU client.
 * <p>SRU is a REST-type method for performing Search/Retrieval against ZING-like 
 * online databases. SRU is standardized by the U.S. Library of Congress.</p>
 * @author mbutcher
 * @see http://www.loc.gov/standards/sru/
 */
public class SRUClient {
	
	public final static String AGENT_STRING="Sinciput/2.0 (+http://technosophos.com)";
	
	public final static String SRU_VERSION = "1.1";
	//public final static int DEFAULT_MAX_RECORDS = 25;
	
	/**
	 * Record packing: Use XML.
	 */
	public final static String RECORD_PACKING_XML = "xml";
	
	/**
	 * Record packing: Use string (escaped XML).
	 */
	public final static String RECORD_PACKING_STRING = "string";
	
	/**
	 * Record Schema: MODS. This uses the short version (mods), not the URI.
	 * @see http://www.loc.gov/standards/sru/resources/schemas.html
	 */
	public final static String RECORD_SCHEMA_MODS = "mods";
	/**
	 * Record Schema: Dublin Core (DC). This uses the short version (dc), not the URI.
	 * @see http://www.loc.gov/standards/sru/resources/schemas.html
	 */
	public final static String RECORD_SCHEMA_DC = "dc";
	
	private String cql = null;
	/* Essentially, this is the offset. */
	private int startRecord = 0;
	private int maximumRecords = 25;
	private String recordPacking = RECORD_PACKING_XML;
	private String recordSchema = RECORD_SCHEMA_DC;
	
	private String baseURL;
	
	//private int resultSetTTL = 30;
	//private String extraRequestData="";
	//private String stylesheet="";

	/**
	 * Construct a new SRUClient.
	 * Note that URL is not checked until query() is run.
	 */
	public SRUClient( String baseURL ) {
		this.baseURL = baseURL;
	}
	
	public SRUClient( String baseURL, String cql ) throws java.net.MalformedURLException {
		this.baseURL = baseURL;
		this.cql = cql;
	}
	
	public SRUClient( String baseURL, CQL cql ) throws java.net.MalformedURLException {
		this.baseURL = baseURL;
		this.cql = cql.toString();
	}
	
	public void setCQL( String cql ) { this.cql = cql; }
	/**
	 * Set the CQL query for this SRU request.
	 * @param cql
	 * @see CQL.query()
	 */
	public void setCQL( CQL cql ) { this.cql = cql.toString(); }
	public String getCQL() { return this.cql; }
	
	public void setURL(String url){this.baseURL = url;}
	public String getURL(){return this.baseURL;}
	
	/**
	 * Specify record packing.
	 * @param recordPacking
	 */
	public void setRecordPacking( String recordPacking ) { this.recordPacking = recordPacking; }
	public String getRecordPacking() { return this.recordPacking; }
	
	/**
	 * Specify the encoding schema that data should be returned using.
	 * See the SCHEMA_* constants. (dc is default)
	 * @param recordSchema
	 */
	public void setRecordSchema( String recordSchema ) { this.recordSchema = recordSchema; }
	public String getRecordSchema() { return this.recordSchema; }
	
	/**
	 * Set the maximum number of records that should be returned by the server.
	 * @param max
	 */
	public void setMaximumRecords( int max ){ this.maximumRecords = max; }
	public int getMaximumRecords() { return this.maximumRecords; }
	
	/**
	 * Set the offset that the server will use to decide what record it should first return.
	 * @param start
	 */
	public void setStartRecord(int start){this.startRecord=start;}
	public int getStartRecord(){return this.startRecord;}
	
	/**
	 * Do the Query.
	 * This connects to the remote server and fetches the results.
	 * @return Contents as a string.
	 * @throws java.net.MalformedURLException If the URL is invalid.
	 * @throws java.io.IOException If the connection experienced trouble.
	 * @throws SRUException If the data that is returned is not what is expected.
	 */
	public SRUResponse query() throws java.net.MalformedURLException, IOException, SRUException {
		
		String url = this.constructQueryString();
		System.err.println(url);
		URL con = new URL(url);
		con.openConnection().addRequestProperty("User-Agent", AGENT_STRING);
		Object o = con.getContent();
		if(o instanceof InputStream ) {
			//MODS m = new MODS(new InputSource((java.io.InputStream)o));
			//System.out.print(m.toString());
			InputSource in = new InputSource((InputStream)o);
			
			SRUResponse response = null;
			
			try {
				Document doc = this.parseResults(in);
				response = new SRUResponse(doc);
				//System.out.println(domToString(doc));
			} catch (ParserConfigurationException e) {
				throw new SRUException("Could not configure parser for SRU response.", e);
			} catch (SAXException e) {
				throw new SRUException("Error parsing SRU response.", e);
			}
			
			return response;
			
		} else {
			throw new SRUException("Unknown content type from SRU connection");
		}
		//return "";
	}
	
	/**
	 * Construct a query string for SRU.
	 * <p>This method is called internally to take the values above and encode them for
	 * URL-based delivery.</p>
	 * @return URL as a string.
	 * @throws SRUException
	 */
	protected String constructQueryString() throws SRUException {
		String enc = "UTF-8";
		
		StringBuilder sb = new StringBuilder();
		sb.append(this.baseURL).append('?');
		sb.append("version=").append(SRU_VERSION).append('&');
		sb.append("operation=searchRetrieve&")
		  .append("maximumRecords=").append(this.getMaximumRecords()).append('&');
		if(this.startRecord > 0) sb.append("startRecord=").append(this.startRecord).append('&');
		try {
			sb.append("recordSchema=").append(URLEncoder.encode(this.getRecordSchema(), enc)).append('&');
			sb.append("recordPacking=").append(URLEncoder.encode(this.getRecordPacking(), enc)).append('&');
			sb.append("query=").append(URLEncoder.encode(this.getCQL(), enc));//.append('&');
		} catch(java.io.UnsupportedEncodingException e) {
			throw new SRUException("Could not encode query", e);
		}
		//return this.baseURL + "?" + this.cql;
		return sb.toString();
	}
	
	public static void main (String [] args) {
		String base = "http://z3950.loc.gov:7090/voyager";
		//String cql = "version=1.1&operation=searchRetrieve&query=beowulf&maximumRecords=1&recordSchema=mods";
		
		SRUClient c = new SRUClient(base);
		
		CQL cql = CQL.query(CQL.INDEX_TITLE, "lilith", CQL.REL_SCR)
		  //.and().clause("bath.lccn", "0802860613", "=")
		  .and().clause(CQL.INDEX_AUTHOR, "George Macdonald", CQL.REL_ALL);
		
		System.out.println(cql.toString());
		
		c.setMaximumRecords(10);
		c.setRecordSchema(SRUClient.RECORD_SCHEMA_MODS);
		//c.setCQL("beowulf");
		c.setCQL(cql);
		try {
			SRUResponse r = c.query();
			System.out.println("Records received. Formatting...");
			
			int k = 0;
			if (RECORD_SCHEMA_MODS.equalsIgnoreCase(c.getRecordSchema())) {
				MODS mods;
				Element mods_ele;
				
				for (SRUResponse.Record rec : r.getRecords()) {
					System.out.format("%d.\n========================\n",++k);
					mods_ele = rec.getRecordData();
					if (mods_ele != null) {
						mods = new MODS(mods_ele);
						System.out.println(mods.toString());
					}
				}
			} else {
				SRWDC dc;
				Element srw_ele;
				for (SRUResponse.Record rec : r.getRecords()) {
					System.out.format("%d.\n========================\n",++k);
					srw_ele = rec.getRecordData();
					if (srw_ele != null) {
						//System.out.println("Swoop!");
						dc = new SRWDC(srw_ele);
						System.out.println(dc.toString());
					} else
						System.err.println("No contents!");
				}
			}			
			System.out.println(r.toString());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		System.out.println("Done.");
		return;
	}
	
	/**
	 * Take an input source and try to parse it into a DOM tree.
	 * @param in
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document parseResults(InputSource in) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory bFac = DocumentBuilderFactory.newInstance();
		//System.out.format("Builder is %s.\n", bFac.isNamespaceAware() ? "Namespace aware" : "Brain dead");
		DocumentBuilder b = bFac.newDocumentBuilder();
		Document d = b.parse(in);
		return d;
	}
	
}
