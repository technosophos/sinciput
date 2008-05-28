package com.technosophos.rhizome.web.helper;

import java.util.ArrayList;
import java.util.List;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.document.Relation;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RepositoryManager;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

public class RelatedDocumentsHelper {
	
	private RepositoryManager repoman;
	private String relationFilter = null;
	
	public RelatedDocumentsHelper(RepositoryManager repoman) {
		this.repoman = repoman;
	}
	
	public RelatedDocumentsHelper(RepositoryManager repoman, String relationType) {
		this.repoman = repoman;
		this.relationFilter = relationType;
	}
	
	/**
	 * Open the target document and add a relation to the relatedToID document.
	 * @param targetID ID of the target document
	 * @param relatedToID ID of the document that will be added as a relation of the target.
	 * @throws RhizomeException If there is an error adding the relationship.
	 */
	public void relateDocuments(String targetID, String relatedToID, String repoName) 
			throws RhizomeException
	{
		
		DocumentRepository repo = this.repoman.getRepository(repoName);
		
		RhizomeDocument parent = repo.getDocument(targetID);
		parent.addRelation(new Relation(this.relationFilter, relatedToID));
		this.repoman.storeDocument(repoName, parent);
		
	}
	
	/**
	 * Set this object to filter results to only look for the given relation.
	 * @param onlyThisRelation
	 */
	public void setRelationTypeFilter(String onlyThisRelation) {
		this.relationFilter = onlyThisRelation;
	}
	
	public String getRelationTypeFilter() {
		return this.relationFilter;
	}
	
	/**
	 * Get reverse-related documents.
	 * This searches all other documents to see if any of them are related to the given
	 * document.
	 * 
	 * This uses the relation type filter set in setRelationFilter or the constructor. If
	 * no filter is found, it will search for any relationship.
	 * 
	 * Is this slow? Not as slow as you'd think, since reverse-related documents are stored
	 * in an index.
	 * @param docID
	 * @param metadataNames
	 * @param repoName
	 * @return
	 * @throws RhizomeException
	 */
	public DocumentList getReverseRelatedDocuments(String docID, 
			String[] metadataNames, String repoName )
			throws RhizomeException {
		DocumentRepository repo = this.repoman.getRepository(repoName);
		RepositorySearcher search = this.repoman.getSearcher(repoName);
		
		// It's best to use a filter... but if none is found....
		String [] docIDs = (this.relationFilter == null) 
				? search.getReverseRelatedDocuments(docID)
				: search.getReverseRelatedDocuments(docID, this.relationFilter);
			
		if(docIDs.length == 0) {
			return new DocumentList();
		}
		
		return search.getDocumentList(metadataNames, docIDs, repo);
	}
	
	/**
	 * Get the documents that this document indicates that it is related to.
	 * 
	 * This will not search for other documents that say they are related to this one.
	 * @param docID
	 * @param metadataNames
	 * @param repoName
	 * @return
	 * @throws RhizomeException
	 * @see getReverseRelatedDocuments(String, String[], String)
	 */
	public DocumentList getRelatedDocuments(String docID, 
			String[] metadataNames, String repoName)
			throws RhizomeException
		{
		
		DocumentRepository repo = this.repoman.getRepository(repoName);
		RepositorySearcher search = this.repoman.getSearcher(repoName);
		
		if(!repo.hasDocument(docID)) {
			// Do what?
			return new DocumentList();
		}
		
		RhizomeDocument doc = repo.getDocument(docID);
		List<Relation> relations = doc.getRelations();
		
		List<String> docIDs = this.getRelatedDocIDs(relations);
		
		if(docIDs.size() > 0 ) {
			String[] idArray = docIDs.toArray(new String[docIDs.size()-1]);
			DocumentList docs = search.getDocumentList(metadataNames, idArray, repo);
			
			// Return:
			return docs;
		}
		return new DocumentList();
		
	}
	
	protected List<String> getRelatedDocIDs(List<Relation> relations) {
		ArrayList<String> docIDs = new ArrayList<String>();
		
		boolean hasFilter = this.relationFilter != null;
		
		for(Relation rel: relations) {
			if(hasFilter && !this.relationFilter.equals(rel.getRelationType()))
				continue;
			
			docIDs.add(rel.getDocID());
		}
		
		return docIDs;
	}
}
