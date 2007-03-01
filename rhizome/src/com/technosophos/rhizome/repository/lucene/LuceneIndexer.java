package com.technosophos.rhizome.repository.lucene;

import static com.technosophos.rhizome.repository.lucene.LuceneElements.*;

import com.technosophos.rhizome.document.*;
import com.technosophos.rhizome.repository.DocumentIndexer;
import com.technosophos.rhizome.repository.RepositoryContext;

//import java.util.ArrayList;
import java.util.Iterator;
import org.apache.lucene.document.*;

public class LuceneIndexer implements DocumentIndexer {

	RepositoryContext context;
	
	public LuceneIndexer() {
		this.context = new RepositoryContext();
	}
	
	public boolean isReusable() {
		// TODO Auto-generated method stub
		return false;
	}

	public long reindex() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updateIndex(RhizomeDocument doc) {
		// TODO Auto-generated method stub

	}

	public void updateIndex(String docID) {
		// TODO Auto-generated method stub

	}
	
	public void indexDocument(RhizomeDocument doc) {
		/*
		 * What to do:
		 * Store each metadatum in a separate field.
		 * Store relations?
		 * Store body in a field.
		 * Pass the whole thing into an indexer.
		 */
		Document ldoc = new Document();
		
		// Add document ID
		Field docID_field = new Field(LUCENE_DOCID_FIELD, 
									doc.getDocumentID(), 
									Field.Store.YES, 
									Field.Index.UN_TOKENIZED);
		ldoc.add(docID_field);
		
		// Add Metadata
		Iterator<Metadatum> md = doc.getMetadata().iterator();
		Field mfield;
		Metadatum m;
		while(md.hasNext()) {
			m = md.next();
			Iterator<String> vals = m.getValues().iterator();
			while(vals.hasNext()) {
				mfield = new Field(
						m.getName(), 
						vals.next(), 
						Field.Store.YES, 
						Field.Index.TOKENIZED);
				ldoc.add(mfield);
			}
		}
		
		// Add relations
		Iterator<Relation> relations = doc.getRelations().iterator();
		Relation r;
		StringBuffer sb;
		while(relations.hasNext()) {
			sb = new StringBuffer();
			r = relations.next();
			sb.append(r.getDocID());
			sb.append(RELATION_SEPARATOR);
			if(r.hasRelationType())
				sb.append(r.getRelationType());
			mfield = new Field(
					LUCENE_RELATION_FIELD,
					sb.toString(),
					Field.Store.YES, 
					Field.Index.TOKENIZED); 
					// Check tokenizer to see how SEPARATOR will be parsed
			ldoc.add(mfield);
		}
		
		// Add body
		Field bodyField;
		RhizomeData data = doc.getData();
		/*
		 * TODO: This should all be replaced with isIndexible() method in RhizomeData.
		 */
		String mimetype = data.getMimeType();
		if(RhizomeData.MIME_PLAINTEXT.equals(mimetype)) {
			bodyField = new Field(
					LUCENE_BODY_FIELD, 
					data.toString(),
					Field.Store.NO, 
					Field.Index.TOKENIZED );
			ldoc.add(bodyField);
		} else if (RhizomeData.MIME_HTML.equals(mimetype) ||
				RhizomeData.MIME_XML.equals(mimetype)) {
			// FIXME: Remove tags from HTML/XML
			bodyField = new Field(
					LUCENE_BODY_FIELD, 
					data.toString(),
					Field.Store.NO, 
					Field.Index.TOKENIZED );
			ldoc.add(bodyField);
		//} else {
			// DO NOT ADD FIELD
		}
		
		// Add Extensions
		Iterator<Extension> exts = doc.getExtensions().iterator();
		Extension ext;
		while(exts.hasNext()) {
			ext = exts.next();
			if(ext.isIndexible()) {
				mfield = new Field(
					LUCENE_EXTENSION_FIELD,
					ext.toIndexibleString(),
					Field.Store.NO, 
					Field.Index.TOKENIZED); 
				ldoc.add(mfield);
			}
		}
	}
	
	public RepositoryContext getConfiguration() {
		return this.context;
	}
	
	public void setConfiguration(RepositoryContext context) {
		this.context = context;
	}

}
