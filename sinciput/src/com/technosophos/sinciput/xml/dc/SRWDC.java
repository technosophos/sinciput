package com.technosophos.sinciput.xml.dc;

import org.w3c.dom.*;
import com.technosophos.sinciput.xml.Helper;
/*
 * <zs:recordData><srw_dc:dc xmlns="http://purl.org/dc/elements/1.1/" xmlns:srw_dc="info:srw/schema/1/dc-schema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="info:srw/schema/1/dc-schema http://www.loc.gov/standards/sru/dc-schema.xsd">
  <title>Lilith; a romance,</title>
  <creator>MacDonald, George, 1824-1905.</creator>
  <creator>MacDonald, Greville, 1856-1944, ed.</creator>
  <type>text</type>
  <publisher>London, G. Allen &amp; Unwin, ltd.</publisher>
  <date>[1924]</date>
  <language>eng</language>
</srw_dc:dc></zs:recordData>
 */
/**
 * Provides access to SRW_DC XML data.
 * <p>This XML schema is based on Dublin Core.</p> 
 */
public class SRWDC {
	
	private Element root = null;
	private String title, creator, type, date, publisher, language;

	public SRWDC(Document doc) {
		this(doc.getDocumentElement());
	}
	
	public SRWDC(Element ele) {
		this.root = ele;
		
		this.title = Helper.textContents("title", this.root);
		this.creator = Helper.textContents("creator", this.root);
		this.type = Helper.textContents("type", this.root);
		this.date = Helper.textContents("date", this.root);
		this.publisher = Helper.textContents("publisher", this.root);
		this.language = Helper.textContents("language", this.root);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getCreator() {
		return this.creator;
		//return Helper.textContents("creator", this.root);
	}
	
	public String getType() {
		return this.type;
		//return Helper.textContents("type", this.root);
	}
	
	public String getDate() {
		return this.date;
		//return Helper.textContents("date", this.root);
	}
	
	public String getPublisher() {
		return this.publisher;
		//return Helper.textContents("publisher", this.root);
	}
	
	public String getLanguage() {
		return this.language;
		//return Helper.textContents("language", this.root);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Title: ").append(this.title).append('\n');
		sb.append("Creator: ").append(this.creator).append('\n');
		sb.append("Type: ").append(this.type).append('\n');
		sb.append("Date: ").append(this.date).append('\n');
		sb.append("Publisher: ").append(this.publisher).append('\n');
		sb.append("Language: ").append(this.language);
		return sb.toString();
	}
	
}
