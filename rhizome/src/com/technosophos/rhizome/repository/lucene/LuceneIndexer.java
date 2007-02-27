package com.technosophos.rhizome.repository.lucene;

import static com.technosophos.rhizome.repository.lucene.LuceneElements.*;

import com.technosophos.rhizome.document.*;
import com.technosophos.rhizome.repository.DocumentIndexer;
import com.technosophos.rhizome.repository.RepositoryContext;

import java.util.ArrayList;
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
		Iterator<Metadatum> md = doc.getMetadata().iterator();
		Field mfield;
		while(md.hasNext()) {
			Metadatum m = md.next();
			Iterator<String> vals = m.getValues().iterator();
			while(vals.hasNext()) {
				mfield = new Field(
						m.getName(), 
						vals.next(), 
						Field.Store.YES, 
						Field.Index.TOKENIZED);
			}
		}
		
		//ArrayList<Relation> relations = doc.getRelations()
		
		//Field bodyField = new Field(LUCENE_BODY_FIELD, doc.getData());
	}
	
	public RepositoryContext getConfiguration() {
		return this.context;
	}
	
	public void setConfiguration(RepositoryContext context) {
		this.context = context;
	}

}
