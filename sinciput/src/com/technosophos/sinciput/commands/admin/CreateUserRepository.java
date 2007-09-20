package com.technosophos.sinciput.commands.admin;

import static com.technosophos.sinciput.servlet.ServletConstants.SETTINGS_REPO;

//import java.util.List;
//import java.util.Map;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.command.AbstractCommand;
//import com.technosophos.rhizome.controller.CommandResult;
//import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.DocumentID;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.RepositoryAccessException;
//import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.repository.util.RepositoryUtils;

import com.technosophos.sinciput.types.admin.RepoDescriptionEnum;
import com.technosophos.sinciput.types.admin.UserEnum;

/**
 * Command to create a new user repository.
 * A user repository is a user's default repository. There is nothing special about this
 * repository, other than the fact that it is set as the user's default repository. This 
 * setting is stored in the user's account information.
 * 
 * The machine ID for a repository shuold be its SHA1 value, I think. This will require extra
 * collision checking, but is both secure and compact.
 * @author mbutcher
 *
 */
public class CreateUserRepository extends AbstractCommand {

	/**
	 * Create a repository for a user.
	 * <p>This should be as automated as possible, so that the user does not have to 
	 * enter any information.</p>
	 * <p>Params:</p>
	 * <ul>
	 * <li>Username: This will become the owner, and will be used for generating other fields.</li>
	 * </ul>
	 */
	public void execute() {
		/*
		 * Check for the existence of required params.
		 * Note that we are looking for teh USERNAME field from UserEnum, and then we
		 * are going to use this value in other places.
		 */
		if(! this.hasParam(UserEnum.USERNAME.getKey()) ) {
			String err = String.format("No %s found in params.", UserEnum.USERNAME.getKey());
			String ferr = "You must specify a user name.";
			results.add( this.createErrorCommandResult(err, ferr));
			return;
		}
		
		Object o = this.getFirstParam(UserEnum.USERNAME.getKey(),null);
		String owner = o.toString();
		String title = "Personal notes of " + owner + ".";
		
		// Why the '<' char? Because it is not allowed in repository names. Thus, we don't
		// have to worry about collisions with non-user repositories named after the owner.
		//String repoName = RepositoryUtils.generateRepoID("<" + owner);
		
		String repoName = RepositoryUtils.generateRepoID(owner);
		
		// Does this repository already exist?
		// Rather than make assumptions about where this info might be stored (e.g. searching
		// the _sinciput repo), we just ask:
		if( this.repoman.hasRepository(repoName) ) {
			// We assume a naming collision from generateRepoID, and we try again:
			String err = String.format("A repository already exists for %s", owner);
			String ferr = String.format("A repository already exists for %s.", owner);
			results.add( this.createErrorCommandResult(err, ferr));
			return;	
		}
		// Create repository
		try {
			this.repoman.createRepository(repoName);
		} catch ( RepositoryAccessException e) {
			String errMsg = String.format("Access problem when creating %s: %s", repoName, e.getMessage());
			String ferr = "We could not create the repository for " + repoName;
			results.add(this.createErrorCommandResult(errMsg, ferr, e));
			return;
		} catch (RhizomeInitializationException e) {
			String errMsg = String.format("Problem initializing repository when creating %s: %s", repoName, e.getMessage() );
			String ferr = "We encountered a problem while trying to set up your repository. Try again later.";
			results.add(this.createErrorCommandResult(errMsg, ferr, e));
			return;
		}
		// If we made it this far, the repository has been created!
		
		//Create the new repository description
		RhizomeDocument doc = new RhizomeDocument(DocumentID.generateDocumentID());
		
		// Add Type first:
		doc.addMetadatum(new Metadatum(RepoDescriptionEnum.TYPE.getKey(), 
				RepoDescriptionEnum.TYPE.getFieldDescription().getDefaultValue()));
		
		// Add fields:
		doc.addMetadatum(new Metadatum(RepoDescriptionEnum.OWNER.getKey(), owner) );
		doc.addMetadatum(new Metadatum(RepoDescriptionEnum.TITLE.getKey(), title) );
		doc.addMetadatum(new Metadatum(RepoDescriptionEnum.REPO_NAME.getKey(), repoName) );
		
		// Do protected fields:
		String time = com.technosophos.rhizome.util.Timestamp.now();
		doc.addMetadatum(new Metadatum(RepoDescriptionEnum.CREATED_ON.getKey(), time ));
		doc.addMetadatum(new Metadatum(RepoDescriptionEnum.LAST_MODIFIED.getKey(), time));
		
		// Store the description in the main repository:
		try {
			this.repoman.storeDocument(SETTINGS_REPO, doc);
		} catch (RhizomeInitializationException rie) {
			String err = String.format("Failed to get a repository to store document %s (title: %s) in %s: %s."
					, doc.getDocID()
					, title
					, SETTINGS_REPO
					, rie.getMessage());
			String ferr = "The server could not store the repository description. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,rie ));
			return;
		} catch (RepositoryAccessException rae) {
			String err = String.format("Failed to store document %s title: %s) in %s: %s."
					, doc.getDocID()
					, title
					, SETTINGS_REPO
					, rae.getMessage());
			String ferr = "The server could not store the new repository description. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,rae ));
			return;
		} catch (RhizomeException re) {
			String err = String.format("Failed to store document %s (title: %s) in %s: %s."
					, doc.getDocID()
					, title
					, SETTINGS_REPO
					, re.getMessage());
			String ferr = "The server could not store the new repository description. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,re ));
			return;
		}
		
	}

}
