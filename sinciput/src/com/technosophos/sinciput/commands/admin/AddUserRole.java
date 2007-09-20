package com.technosophos.sinciput.commands.admin;

import static com.technosophos.sinciput.servlet.ServletConstants.SETTINGS_REPO;

//import java.util.List;
//import java.util.Map;

import com.technosophos.rhizome.command.AbstractCommand;
//import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.repository.DocumentNotFoundException;
import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.sinciput.types.admin.UserEnum;

/**
 * Command for adding a role to a user record.
 * 
 * TODO: As with other user-level classes, this needs to be refactored, separating user logic from
 * command itself.
 * @author mbutcher
 *
 */
public class AddUserRole extends AbstractCommand {

	/**
	 * Add a role to a user record.
	 */
	public void execute() throws ReRouteRequest {
		/*
		 * 1. Check to see if right params exist.
		 * 2. Check to see if user exists.
		 * 3. Set role on user.
		 */
		String uname_field = UserEnum.USERNAME.getKey();
		
		// See if role and user exist in params.
		if(! this.hasParam(UserEnum.ROLE.getKey()) ) {
			String err = String.format("No %s found in params.", UserEnum.USERNAME.getKey());
			String ferr = "You must specify a role name.";
			results.add( this.createErrorCommandResult(err, ferr));
			return;
		}
		if(! this.hasParam(uname_field) ) {
			String err = String.format("No %s found in params.", UserEnum.USERNAME.getKey());
			String ferr = "You must specify a user name.";
			results.add( this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Get params
		String user = this.getFirstParam(uname_field, null).toString();
		String role = this.getFirstParam(UserEnum.ROLE.getKey(), null).toString();
		
		// Check if user exists. If not, we abort.
		
		RepositorySearcher search;
		try {
			search = this.repoman.getSearcher(SETTINGS_REPO); //RhizomeInitializationException
			String [] docids = search.getDocIDsByMetadataValue(uname_field, user); // RepositoryAccessException
			
			// docids should never be null... search should always return an array.
			if( !(docids == null) && docids.length == 0 ) {
				String err = String.format("User %s does not exist in %s repository.", 
						user, SETTINGS_REPO);
				String ferr = String.format("The user %s does not exist. You can only add roles to existing users.", user);
				results.add( this.createErrorCommandResult(err, ferr));
				return;
			} else if( docids.length > 1 ) { // This should never happen.
				String err = String.format("SEVERE ERROR: User %s exisst multiple times in %s repository.", 
						user, SETTINGS_REPO);
				String ferr = "The server encountered a severe problem with the users database. Try again later.";
				results.add( this.createErrorCommandResult(err, ferr));
				return;
			}
			DocumentRepository repo = this.repoman.getRepository(SETTINGS_REPO);
			RhizomeDocument doc = repo.getDocument(docids[0]);
			boolean no_md = true;
			for( Metadatum m: doc.getMetadata()) {
				if( m.getName().equalsIgnoreCase(UserEnum.ROLE.getKey())) {
					// do we need to append?
					if( !m.hasValue(role)) m.addValue(role); // Add role only if necessary
					no_md = false;
				}
			}
			
			if( no_md ) doc.addMetadatum(new Metadatum(UserEnum.ROLE.getKey(), role));
			
			// Do timestamp:
			String time = com.technosophos.rhizome.util.Timestamp.now();
			doc.replaceMetadatum(new Metadatum(UserEnum.LAST_MODIFIED.getKey(), time));
			
			// Now let's store the document, catching all of the exceptions just to be careful:
			try {
				this.repoman.storeDocument(SETTINGS_REPO, doc);
			} catch (RhizomeInitializationException rie) {
				String err = String.format("Failed to get a repository to store document %s (user: %s) in %s: %s."
						, doc.getDocID()
						, user
						, SETTINGS_REPO
						, rie.getMessage());
				String ferr = "The server could store the new user entry. Try again later.";
				results.add( this.createErrorCommandResult(err, ferr,rie ));
				return;
			} catch (RepositoryAccessException rae) {
				String err = String.format("Failed to store document %s (user: %s) in %s: %s."
						, doc.getDocID()
						, user
						, SETTINGS_REPO
						, rae.getMessage());
				String ferr = "The server could store the new user entry. Try again later.";
				results.add( this.createErrorCommandResult(err, ferr,rae ));
				return;
			} catch (RhizomeException re) {
				String err = String.format("Failed to store document %s (user: %s) in %s: %s."
						, doc.getDocID()
						, user
						, SETTINGS_REPO
						, re.getMessage());
				String ferr = "The server could store the new user entry. Try again later.";
				results.add( this.createErrorCommandResult(err, ferr,re ));
				return;
			}
			
		} catch (RhizomeInitializationException e) {
			// Thrown creating search
			String err = String.format("Failed to get repository searcher: %s.", e.getMessage());
			String ferr = "The server could not make sure that this user name existx. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,e ));
			return;
		} catch (DocumentNotFoundException e) {
			// thrown retrieving doc
			String err = String.format("Retrieving document for %s failed: %s.", user.toString(), e.getMessage());
			String ferr = "The server could not this user's record. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,e ));
			return;
		} catch (RepositoryAccessException e) {
			// Thrown when searching or retrieving doc
			String err = String.format("Searching for %s failed: %s.", user.toString(), e.getMessage());
			String ferr = "The server could not make sure that this user name exists. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,e ));
			return;
		} catch (RhizomeException e) {
			// Thrown retrieving doc
			String err = String.format("Retrieving document for %s failed: %s.", user.toString(), e.getMessage());
			String ferr = "The server could not this user's record. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,e ));
		}
	}

}
