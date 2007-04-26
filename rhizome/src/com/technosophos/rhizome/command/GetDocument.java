package com.technosophos.rhizome.command;

import java.util.List;
import java.util.Map;

//import com.technosophos.rhizome.controller.CommandConfiguration;
//import com.technosophos.rhizome.controller.RhizomeCommand;
//import com.technosophos.rhizome.repository.RepositoryManager;

import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentNotFoundException;
import com.technosophos.rhizome.RhizomeException;

public class GetDocument extends AbstractCommand {

	/**
	 * This command looks for the parameter "docid" (or [prefix]docid if prefix is
	 * set in the command configuration).
	 */
	public static String PARAM_DOCID = "docid";
	
	/**
	 * Retrieve a document.
	 * <p>This gets a document id ({@link PARAM_DOCID}) from the Map of params. It then retrieves
	 * the document from the repository, and places the result in the results list.</p>
	 * <p><b>Error Conditions:</p> If no document ID is found in the params, or if the
	 * repository encounters an error (including Document Not Found), then the resulting
	 * {@link CommandResult} will have the error flag set, and error information embedded.</p>
	 */
	public void doCommand(Map<String, Object> params, List<CommandResult> results) {
		
		CommandResult res;
		RhizomeDocument doc;
		if(!params.containsKey(this.getPrefixedParamName(PARAM_DOCID))) {
			res = new CommandResult();
			String errMsg = "\"docid\" param is not set. Nothing to retrieve.";
			String friendlyErrMsg = "A document was requested, but no document name was given. We don't know what to look for.";
			res.setError(errMsg, friendlyErrMsg);
			results.add(res);
			return;
		}
		String docID = this.getParam(params, PARAM_DOCID).toString();
		try {
			doc = this.fetchDocument(docID);
			results.add(new CommandResult(doc));
		} catch (RhizomeException re) {
			res = new CommandResult();
			String errMsg = "Failed to retrieve document: " + re.getMessage();
			String friendlyErrMsg = "The document you are looking for cannot be found right now.";
			res.setError(errMsg, friendlyErrMsg, re);
			results.add(res);
			return;
		}
	}
	
	/**
	 * This method retrieves a document. Subclasses may wish to override or call this.
	 * @param docID
	 * @return A RhizomeDocument retrieved from the repository.
	 * @throws DocumentNotFoundException if the document is not found.
	 * @throws RhizomeException if there is an error accessing the respository.
	 */
	protected RhizomeDocument fetchDocument(String docID) throws DocumentNotFoundException, RhizomeException {
		return this.repoman.getDocument(docID);
	}

}
