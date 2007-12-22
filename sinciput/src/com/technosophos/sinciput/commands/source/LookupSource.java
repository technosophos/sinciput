package com.technosophos.sinciput.commands.source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.controller.CommandInitializationException;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryManager;
//import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.controller.CommandConfiguration;
import com.technosophos.rhizome.document.*;
//import com.technosophos.sinciput.SinciputException;
import com.technosophos.sinciput.commands.SinciputCommand;
import com.technosophos.sinciput.sru.*;
import com.technosophos.sinciput.xml.mods.*;
import com.technosophos.sinciput.types.SourceEnum;

/**
 * This command retrieves information from a ZING server and returns the results.
 * @author mbutcher
 *
 */
public class LookupSource extends SinciputCommand {
	
	public static final String DIR_SRU_SERVER = "sru_server";
	public static final String PARAM_LOOKUP_KEY = "lookup_key";
	public static final String PARAM_LOOKUP_VALUE = "lookup_value";
	public static final String DEFAULT_LOOKUP_KEY = "isbn";
	
	private Map<String, String> supportedIndexes;
	
	public void init(CommandConfiguration c, RepositoryManager r) 
			throws CommandInitializationException {
		super.init(c, r);
		
		this.supportedIndexes = new HashMap<String, String>();
		this.supportedIndexes.put("isbn", CQL.INDEX_ISBN);
		this.supportedIndexes.put("issn", CQL.INDEX_ISSN);
		this.supportedIndexes.put("lccn", CQL.INDEX_LCCN);
		
		this.supportedIndexes.put("default", CQL.INDEX_ISBN);
	}

	/**
	 * Do the actual lookup.
	 */
	protected void execute() throws ReRouteRequest {
		/*
		 * Steps:
		 * 1. Get info to lookup
		 * 2. Get info about where to look it up.
		 * 3. Perform the lookup
		 * 4. Check returned data
		 * 5. Create a new Sinciput document
		 * <OR>
		 * 5. Return info directly to client
		 */
		
		// Lookup:
		String lKey = this.getFirstParam(PARAM_LOOKUP_KEY, "isbn").toString().toLowerCase();
		String lVal = this.getFirstParam(PARAM_LOOKUP_VALUE, "").toString().toLowerCase();
		
		if(lVal.trim().length() == 0) {
			String ferr = "You must supply a value to lookup. Try an ISBN number.";
			this.results.add(this.createErrorCommandResult("No value", ferr));
			return;
		}
		
		if(!this.supportedIndexes.containsKey(lKey)) {
			//XXX: Should this throw exception?
			lKey = "default";//this.supportedIndexes.get("default");
		}
		
		String server = this.comConf.hasDirective(DIR_SRU_SERVER) 
			? this.comConf.getDirective(DIR_SRU_SERVER)[0]
			: "http://z3950.loc.gov:7090/voyager";
		
		this.doLookup(server, this.supportedIndexes.get(lKey), lVal);
			
		
	}
	
	protected void doLookup(String server, String index, String term) {
		
		//String indexName = this.getIndexName(index);
		
		// Setup query
		CQL cql = CQL.query().clause(index, term, CQL.REL_EQUALS);
		SRUClient client;
		try {
			System.err.println("CQL: " + cql.toString());
			client = new SRUClient(server, cql);
		} catch (MalformedURLException e) {
			String errMsg = "URL is invalid: " + e.getMessage();
			String friendlyErrMsg = "Lookup failed because we could not contact the library.";
			this.results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg, e));
			return;
		}
		
		client.setMaximumRecords(1);
		client.setRecordSchema(SRUClient.RECORD_SCHEMA_MODS);
		
		// Do query
		SRUResponse res;
		try {
			res = client.query();
		} catch (MalformedURLException e) {
			String errMsg = "URL is invalid (2): " + e.getMessage();
			String friendlyErrMsg = "Lookup failed because we could not contact the library.";
			this.results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg, e));
			return;
		} catch (IOException e) {
			String errMsg = String.format("IO error doing SRU.", e.getMessage());
			String friendlyErrMsg = "Lookup failed. We could not read the information that the library returned.";
			this.results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg, e));
			return;
		} catch (SRUException e) {
			String errMsg = String.format("SRU failed query: %s.", e.getMessage());
			String friendlyErrMsg = "Lookup failed; something bad happened while exchanging information with the library.";
			this.results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg, e));
			return;
		}
		
		if(res.hasError()) {
			System.err.println("SRU Diagnostic: " + res.getErrorDiagnostic().toString());
			String errMsg = String.format("SRU reports an error: %s.", res.getErrorDiagnostic().toString());
			String friendlyErrMsg = "Lookup failed; something bad happened while exchanging information with the library.";
			this.results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg));
		}
		
		// To do: what if server reports that there are more matches?
		//if(res.getNumberOfRecords() > 0) this.
		
		java.util.List<SRUResponse.Record> modsRecords = res.getRecords();
		if(modsRecords.size() != 1) {
			String errMsg = String.format("Server indicates that there are %d records. Expected 1.", res.getNumberOfRecords());
			String friendlyErrMsg = "No record was found that is an exact match.";
			this.results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg));
			return;
		}

		MODS mods;
		try {
			mods = new MODS(modsRecords.get(0).getRecordData());
		} catch (MODSException e) {
			String errMsg = String.format("MODS data is malformed: %s.", e.getMessage());
			String friendlyErrMsg = "Lookup failed; the library sent data we don't understand.";
			this.results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg, e));
			return;
		}
		
		RhizomeDocument doc = this.transformMODS(mods);
		
		// Get the doc, store it, and pack it into the response.
		try {
			this.repoman.storeDocument(this.getCurrentRepository(), doc);
		} catch (RhizomeException e) {
			String errMsg = String.format("Retrieved MODS, but failed to write it: %s.", e.getMessage());
			String friendlyErrMsg = "We could not write the results into your repository.";
			this.results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg, e));
			return;
		}
		
		this.results.add(this.createCommandResult(doc));
	}
	
	/**
	 * This method takes a MODS object and transforms it into a RhizomeDocument.
	 * <p>As with other types, this uses the {@link SourceEnum} to figure out what 
	 * data should be included. Unlike other types, though, it uses the MODS data
	 * to automatically populate the fields.</p>
	 * @param mods
	 * @return
	 */
	protected RhizomeDocument transformMODS(MODS mods) {
		RhizomeDocument doc = new RhizomeDocument(DocumentID.generateDocumentID());
		
		// Abstract:
		doc.addMetadatum(new Metadatum(SourceEnum.ABSTRACT.getKey(), mods.getAbstract()));
		
		
		// Authors, editors, and translators:
		ArrayList<String> authors = new ArrayList<String>();
		ArrayList<String> translators = new ArrayList<String>();
		ArrayList<String> editors = new ArrayList<String>();
		
		List<String> roles;
		
		List<MODS.Name> names = mods.getNames();
		for(MODS.Name name: names) {
			roles = name.getRoles();
			/* Reduce number of searches by 2, since we default to creator anyway.
			if(roles.contains("creator") || roles.contains("author"))
				authors.add(name.getNamePart());
			else
			*/ 
			if(roles.contains("ed.") || roles.contains("editor"))
				editors.add(name.getNamePart());
			else if(roles.contains("trans.") || roles.contains("translator"))
				translators.add(name.getNamePart());
			else {
				// Assume author?
				authors.add(name.getNamePart());
			}
		}
		
		doc.addMetadatum(new Metadatum(SourceEnum.AUTHOR.getKey(), authors));
		doc.addMetadatum(new Metadatum(SourceEnum.EDITOR.getKey(), editors));
		doc.addMetadatum(new Metadatum(SourceEnum.TRANSLATOR.getKey(), translators));
		

		// Classification:
		List<MODS.Classification> classifications = mods.getClassifications();
		Metadatum mClass = new Metadatum(SourceEnum.CLASSIFICATION.getKey());
		for(MODS.Classification ic: classifications) {
			mClass.addValue(ic.getAuthority() + ':' + ic.getValue());
		}
		doc.addMetadatum(mClass);
		
		// Genre:
		doc.addMetadatum(new Metadatum(SourceEnum.GENRE.getKey(), mods.getGenre()));
		
		// Identifiers:
		List<MODS.Identifier> ids = mods.getIdentifiers();
		Metadatum mIDs = new Metadatum(SourceEnum.IDENTIFIER.getKey());
		for(MODS.Identifier id: ids) mIDs.addValue(id.toURI());
		doc.addMetadatum(mIDs);
		
		// Language:
		doc.addMetadatum(new Metadatum(SourceEnum.LANGUAGE.getKey(), mods.getLanguage()));
		
		// Location URLs:
		List<MODS.LocationURL> locs = mods.getLocationURLs();
		Metadatum mLocs = new Metadatum(SourceEnum.LOCATION_URL.getKey());
		String labeledURI, label = null;
		for(MODS.LocationURL loc: locs) {
			// Labeled URI format:
			label = loc.getDisplayLabel();
			labeledURI = loc.getURL() + (label != null && label.length() >0  ? (' ' + label) : "");
			mLocs.addValue(labeledURI);
		}
		doc.addMetadatum(mLocs);
		
		// Note:
		doc.addMetadatum(new Metadatum(SourceEnum.NOTE.getKey(), mods.getNote()));
		
		// Publisher, Place, Date, and Edition:
		MODS.OriginInfo origin = mods.getOriginInfo();
		doc.addMetadatum(new Metadatum(SourceEnum.PUBLISHER.getKey(), origin.getPublisher()));
		doc.addMetadatum(new Metadatum(SourceEnum.PUBLISH_DATE.getKey(), origin.getDateIssued()));
		doc.addMetadatum(new Metadatum(SourceEnum.PUBLISH_PLACE.getKey(), origin.getPlace()));
		doc.addMetadatum(new Metadatum(SourceEnum.EDITION.getKey(), origin.getEdition()));
		
		// Source type (text, audio, etc.):
		doc.addMetadatum(new Metadatum(SourceEnum.SOURCE_TYPE.getKey(), mods.getTypeOfResource()));
		
		// Subjects:
		Metadatum mSubj = new Metadatum(SourceEnum.SUBJECT.getKey());
		List<MODS.Subject> subjs = mods.getSubjects();
		for(MODS.Subject subj: subjs) mSubj.addValue(subj.toString()); // toString should collapse subject trees into list format
		doc.addMetadatum(mSubj);
		
		// TOC:
		doc.addMetadatum(new Metadatum(SourceEnum.TABLE_OF_CONTENTS.getKey(), mods.getTableOfContents()));
		
		// Target Audience:
		doc.addMetadatum(new Metadatum(SourceEnum.TARGET_AUDIENCE.getKey(), mods.getTargetAudience()));
		
		// Title
		doc.addMetadatum(new Metadatum(SourceEnum.TITLE.getKey(), mods.getTitleInfo().getFullTitle()));
		doc.addMetadatum(new Metadatum(SourceEnum.SORTABLE_TITLE.getKey(), mods.getTitleInfo().getSortableTitle()));
		
		// Automatic fields
		String time = com.technosophos.rhizome.util.Timestamp.now();
		String uname = this.ses.getUserName();
		doc.addMetadatum(new Metadatum(SourceEnum.TYPE.getKey(), 
				SourceEnum.TYPE.getFieldDescription().getDefaultValue()));
		doc.addMetadatum(new Metadatum(SourceEnum.CREATED_ON.getKey(), time ));
		doc.addMetadatum(new Metadatum(SourceEnum.LAST_MODIFIED.getKey(), time));
		doc.addMetadatum(new Metadatum(SourceEnum.CREATED_BY.getKey(), uname ));
		doc.addMetadatum(new Metadatum(SourceEnum.MODIFIED_BY.getKey(), uname ));
		
		return doc;
	}

}
