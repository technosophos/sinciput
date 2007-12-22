package com.technosophos.sinciput.sru;

import org.w3c.dom.*;

import java.util.List;
import java.util.ArrayList;

import static com.technosophos.sinciput.xml.Helper.*;

/**
 * Describes a response from an SRU server.
 * <p>SRU servers respond with an XML message. This captures the response and handles the results.</p>
 * @author mbutcher
 *
 */
public class SRUResponse {
	
	/** ZING SRU/W namespace */
	public static final String ZS_NAMESPACE = "http://www.loc.gov/zing/srw/";
	public static final String ZS_EXPLAIN_RESPO = "explainResponse";
	public static final String ZS_RESPO = "searchRetrieveResponse";
	
	public static final int MAX_RECORDS = 256;
	
	public String version = "";
	private Integer numberOfRecords = 0;
	private ArrayList<Record> records;
	private boolean err = false;
	private Diagnostic diag;
	
	/*
	public static SRUResponse parseResponse(InputSource in) {
		
	}
	*/
	
	
	public SRUResponse(Document d) throws SRUException {
		records = new ArrayList<Record>();
		
		Element r = d.getDocumentElement();
		String [] qname = r.getTagName().split(":",2);
		String lname;//, pre=null;
		if(qname.length == 2) {
			//pre = qname[0];//r.getPrefix();
			lname = qname[1];//r.getLocalName();
		} else lname = qname[0];
		
		if( !ZS_RESPO.equals(lname) && !ZS_EXPLAIN_RESPO.equals(lname))
			throw new SRUException("Not a valid SRU root element: " + lname);
		
		NodeList version_nl, numrec_nl, rec_nl, diag_nl;
		
		//diag_nl = r.getElementsByTagNameNS(ZS_NAMESPACE, "diagnostics");
		diag_nl = r.getElementsByTagName("zs:diagnostics");
		
		/*
		 * We are assuming here that the XPATH lib does not support namespaces, which seems
		 * to be the case for whatever backend ships with JSDK 1.5/1.6
		 */
		version_nl = matchNodes("version", r);
		numrec_nl = matchNodes("numberOfRecords", r);
		rec_nl = matchNodes("records/record", r);
		
		if( version_nl == null || numrec_nl == null || rec_nl == null )
			throw new SRUException("This record does not appear to be an SRU record.");
		
		//System.out.println(r.getTagName());
		if( diag_nl.getLength() != 0 ) {
			System.out.println("Found error.");
			this.err = true;
			this.diag = new Diagnostic((Element)diag_nl.item(0));
		}
		
		if(version_nl.getLength() > 0) this.version = version_nl.item(0).getTextContent().trim();
		
		if(numrec_nl.getLength() > 0) {
			
			String s = numrec_nl.item(0).getTextContent().trim();
			//System.out.format("Found %s records...\n", s);
			try {
				this.numberOfRecords = Integer.parseInt(s);
				//System.out.format("Found %d records...\n", this.numberOfRecords);
			} catch (NumberFormatException e) {
				e.printStackTrace(System.err);
				this.numberOfRecords = 0;
			}
			// Set upper limit on number of results:
			/* THIS IS ALL WRONG:
			if( this.numberOfRecords > 256) this.numberOfRecords = 256;
			
			if(this.numberOfRecords > 0 ) {

				int nlLength = rec_nl.getLength();
				//if(nlLength > this.numberOfRecords) nlLength = this.numberOfRecords;
				//else if(nlLength != this.numberOfRecords) numberOfRecords = nlLength; // Need this for getNumRecords to be accurate
				Element e;
				for(int i = 0; i < nlLength; ++i) {
					e = (Element)rec_nl.item(i);
					records.add(new SRUResponseRecord(e));
				}
			}*/
			int nlLength = rec_nl.getLength();
			if(nlLength > MAX_RECORDS) nlLength = MAX_RECORDS; // Provide a cap on # of records.
			Element e;
			for(int i = 0; i < nlLength; ++i) {
				e = (Element)rec_nl.item(i);
				records.add(new Record(e));
			}
		}
	}
	
	public boolean hasError(){
		return this.err;
	}
	public Diagnostic getErrorDiagnostic() {return this.err ? this.diag :null;}
	
	/**
	 * Return the number of records found here.
	 * <p>Number of records is the number of records the remote server says 
	 * that the given query matches. This is not the same as the number of 
	 * records that were returned. Unfortunately, the SRU naming convention is
	 * confusing here.</p>
	 * @return
	 */
	public Integer getNumberOfRecords() {return this.numberOfRecords;}
	public Integer size() {return this.getNumberOfRecords();}
	
	/**
	 * Return the SRU response version string.
	 * @return
	 */
	public String getVersion(){ return this.version;}
	
	/**
	 * Return a list of records.
	 * @return
	 */
	public List<Record> getRecords() {return this.records;}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Matches on server: ").append(this.numberOfRecords);
		sb.append("; Local records: ").append(this.records.size()).append(' ');
		sb.append("(SRU v.").append(this.version).append(") ");
		if(this.err)sb.append(this.getErrorDiagnostic().toString());
		return sb.toString();
	}
	
	/**
	 * Represents a record embedded in an SRU response.
	 * @author mbutcher
	 *
	 */
	public class Record {
		private Element recordDataContent = null;
		private String recordSchema = "";
		private String recordPacking = "";
		
		public Record(Element e) {
			//NodeList l = e.getElementsByTagNameNS(ZS_NAMESPACE, "recordData");
			NodeList l = matchNodes("recordData", e);
			if(l.getLength() > 0) this.recordDataContent = (Element)l.item(0);
			
			// Generally, we want the first subelement. But if no subelements are found,
			// it is likely that we are dealing with CDATA-only content, so we keep 
			// the zs:recordData element.
			NodeList kids = this.recordDataContent.getChildNodes();
			int i, j = kids.getLength();
			for(i = 0; i < j; ++i) {
				//System.err.println("Checking..." + kids.item(i).getNodeName());
				if(Node.ELEMENT_NODE == kids.item(i).getNodeType()) {
					//System.err.format("Assigning %s to recordDataContent", kids.item(i).getNodeName());
					this.recordDataContent = (Element)kids.item(i);
					break;
				}
			}
			
			//l = e.getElementsByTagNameNS(ZS_NAMESPACE, "recordSchema");
			l = e.getElementsByTagName("zs:recordSchema");
			if(l.getLength() > 0) this.recordSchema = l.item(0).getTextContent().trim();
			
			//l = e.getElementsByTagNameNS(ZS_NAMESPACE, "recordPacking");
			l = e.getElementsByTagName("zs:recordPacking");
			if(l.getLength() > 0) this.recordPacking = l.item(0).getTextContent().trim();
		}
		public String getRecordSchema(){return this.recordSchema;}
		public String getRecordPacking(){return this.recordPacking;}
		public Element getRecordData(){return this.recordDataContent;}
		public String toString() {
			StringBuilder sb = new StringBuilder(); 
			sb.append(recordSchema).append('(').append(recordPacking).append(')');
			if(recordDataContent != null ) sb.append(recordDataContent.toString());
			return sb.toString();
		}
	}
	
	public class Diagnostic {
		private String uri, message, details;
		public Diagnostic(Element e) {
			NodeList l = e.getElementsByTagName("uri");
			if(l.getLength() > 0) this.uri = l.item(0).getTextContent().trim();
			
			l = e.getElementsByTagName("message");
			if(l.getLength() > 0) this.message = l.item(0).getTextContent().trim();
			
			l = e.getElementsByTagName("details");
			if(l.getLength() > 0) this.details = l.item(0).getTextContent().trim();
		}
		public String getURI(){return this.uri;}
		public String getMessage(){return this.message;}
		public String getDetails(){return this.details;}
		public String toString(){return String.format("%s: %s (%s)", message, details, uri);}
	}
}
