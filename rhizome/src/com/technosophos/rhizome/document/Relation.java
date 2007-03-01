package com.technosophos.rhizome.document;
/**
 * A Relation is one of the parts of a RhizomeDocument.
 * This part traces the relationship of this RhizomeDocument to a 
 * related document.
 * <p>For an explantion of how this fits in with the rest of the 
 * RhizomeDocument structure, see RhizomeDoucment.</p>
 * @author mbutcher
 * @see RhizomeDocument
 *
 */
public class Relation {
	
	/** The default relation name ("peer"). */
	public static String RHIZOME_RELATION_DEFAULT="peer";

	private String relationType = null;
	private String docID = null;
	
	/**
	 * Create a relationship of the named relation type to the give 
	 * document. For example, to indicate that one document is 
	 * related (in a weak way), you might use 
	 * <code>new Relation("peer", "1234.xml")</code>.
	 * @param relationType
	 * @param docID
	 */
	public Relation(String relationType, String docID) {
		this.relationType = relationType;
		this.docID = docID;
	}
	
	/**
	 * Create a relationship to another document.
	 * The relation type will be left null.
	 * @param docID
	 * @see #RHIZOME_RELATION_DEFAULT
	 */
	public Relation(String docID) {
		this.docID = docID;
		//this.relationType = RHIZOME_RELATION_DEFAULT;
	}

	/**
	 * Get the relation type.
	 * If no relation type is specified, this will return null.
	 * @return the string representation of the relation.
	 */
	public String getRelationType() {
		return this.relationType;
	}
	
	/**
	 * Indicates whether or not this relation has type info.
	 * Not all relations must have types, though they ought to.
	 * @return true if the relation has type information.
	 */
	public boolean hasRelationType() {
		if(this.relationType == null) return false;
		return true;
	}
	
	/**
	 * Set the relation type.
	 * @param typeName Name of the relation type (e.g. "peer")
	 */
	public void setRelationType(String typeName) {
		this.relationType = typeName;
	}

	/**
	 * Get the document ID for the related document.
	 * @return document ID of the related document.
	 */
	public String getDocID() {
		return this.docID;
	}
	
	/**
	 * Set the docID that this document relates to.
	 * @param docID document ID of related document.
	 */
	public void setDocID(String docID) {
		this.docID = docID;
	}
}
