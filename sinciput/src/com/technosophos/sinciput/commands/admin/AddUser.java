package com.technosophos.sinciput.commands.admin;

import java.util.List;
import java.util.Map;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.repository.RepositorySearcher;
//import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.document.*;
import com.technosophos.sinciput.types.admin.UserEnum;

import static com.technosophos.sinciput.servlet.ServletConstants.SETTINGS_REPO;

/**
 * This class adds a new user to the Sinciput repository.
 * <p>Sinciput has an internal database for internal information. This command adds
 * a new user to that database. This uses the fields from {@link UserEnum}.
 * </p>
 * <p>Required Params</p>
 * <ul>
 * <li>username</li>
 * <li>password</li>
 * <li>password_verify</li>
 * </li>
 * <p>Optional Params</p>
 * <ul>
 * <li>role</li>
 * <li>gn (given name)</li>
 * <li>sn (surname)</li>
 * <li>email</li>
 * </li>
 * <p>Directives</p>
 * <ul>
 * <li>default_role</li>
 * </ul>
 * @author mbutcher
 * @see UserEnum for the fields that this class uses.
 *
 */
public class AddUser extends AbstractCommand {
	
	public static final String DIR_DEFAULT_ROLE = "default_role";

	/**
	 * Add a new user.
	 * <p>This command adds a new user record to the internal Sinciput 
	 * repository. At minimum, a new user record should have a username and a 
	 * password, but other attributes are allowed, too.</p>
	 */
	public void doCommand(Map<String, Object> params,
			List<CommandResult> results) throws ReRouteRequest {
		/*
		 * 1. Check to see if right params exist.
		 * 2. Check to see if another user of this name exists.
		 * 3. Check password
		 * 4. Create record
		 */
		
		// TODO: This needs some refactoring.
		
		/*
		 * Next three conditionals check for the existence of required params.
		 */
		if(! this.hasParam(params, UserEnum.USERNAME.getKey()) ) {
			String err = String.format("No %s found in params.", UserEnum.USERNAME.getKey());
			String ferr = "You must specify a user name.";
			results.add( this.createErrorCommandResult(err, ferr));
			return;
		}
		if(! this.hasParam(params, UserEnum.PASSWORD.getKey()) ) {
			String err = String.format("No %s found in params.", UserEnum.PASSWORD.getKey());
			String ferr = "You must specify a password.";
			results.add( this.createErrorCommandResult(err, ferr));
			return;
		}
		if(! this.hasParam(params, UserEnum.PASSWORD_VERIFY.getKey()) ) {
			String err = String.format("No %s found in params.", UserEnum.PASSWORD_VERIFY.getKey());
			String ferr = "You must specify the password twice for verification.";
			results.add( this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Get params
		String user = this.getFirstParam(params, UserEnum.USERNAME.getKey()).toString();
		String pw = this.getFirstParam(params, UserEnum.PASSWORD.getKey()).toString();
		String pwa = this.getFirstParam(params, UserEnum.PASSWORD_VERIFY.getKey()).toString();
		
		// Does username already exist?
		String uname_field = UserEnum.USERNAME.getKey();
		RepositorySearcher search;
		try {
			search = this.repoman.getSearcher(SETTINGS_REPO); //RhizomeInitializationException
			String [] docids = search.getDocIDsByMetadataValue(uname_field, user); // RepositoryAccessException
			
			// docids should never be null... search should always return an array.
			if( !(docids == null) && docids.length > 0 ) {
				String err = String.format("User %s already exists in %s repository.", 
						user, SETTINGS_REPO);
				String ferr = String.format("The user %s already exists. You need to pick another user name.", user);
				results.add( this.createErrorCommandResult(err, ferr));
				return;
			}
		} catch (RhizomeInitializationException e) {
			String err = String.format("Failed to get repository searcher: %s.", e.getMessage());
			String ferr = "The server could not make sure that this user name does not already exist. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,e ));
			return;
		} catch (RepositoryAccessException e) {
			String err = String.format("Searching for %s failed: %s.", user.toString(), e.getMessage());
			String ferr = "The server could not make sure that this user name does not already exist. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,e ));
			return;
		}
		
		// Does password exist?
		if( pw == null || pw.length() == 0 ) {
			String err = String.format("%s from params is empty.", UserEnum.PASSWORD.getKey());
			String ferr = "You must specify a password.";
			results.add( this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Are passwords equal?
		if( ! pw.equals(pwa)) {
			String err = String.format("%s and %s from params did not match.", 
					UserEnum.PASSWORD.getKey(),
					UserEnum.PASSWORD_VERIFY.getKey());
			String ferr = "The passwords did not match.";
			results.add( this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Is password minimally strong?
		if(!this.passwordStrengthTest(pw)) {
			String err = String.format("%s is too weak.", UserEnum.PASSWORD.getKey());
			String ferr = "You must specify a password that is at least 7 characters long.";
			results.add( this.createErrorCommandResult(err, ferr));
			return;
		}
		
		
		
 
		RhizomeDocument doc = new RhizomeDocument(DocumentID.generateDocumentID());
		
		// Add Type first:
		doc.addMetadatum(new Metadatum(UserEnum.TYPE.getKey(), 
				UserEnum.TYPE.getFieldDescription().getDefaultValue()));
		
		// Add fields we know are there:
		doc.addMetadatum(new Metadatum(UserEnum.USERNAME.getKey(), user) );
		doc.addMetadatum(new Metadatum(UserEnum.PASSWORD.getKey(), pw) );
		
		// Check other fields:
		String[] e = { 
				UserEnum.GIVENNAME.getKey(), 
				UserEnum.SURNAME.getKey(), 
				UserEnum.EMAIL.getKey(),
				UserEnum.DESCRIPTION.getKey() 
			};
		for(String k: e ) {
			if(this.hasParam(params, k ))
				doc.addMetadatum(new Metadatum(k, this.getFirstParam(params, k).toString()));
		}
		
		// Assign role:
		String k = UserEnum.ROLE.getKey();
		// FIXME: This should be able to assign mulitple roles at once.
		if(this.hasParam(params, k)) {
			doc.addMetadatum(new Metadatum(k, this.getFirstParam(params, k).toString()));
		} else if( this.comConf.hasDirective(DIR_DEFAULT_ROLE)) {
			doc.addMetadatum(new Metadatum(k, this.comConf.getDirective(DIR_DEFAULT_ROLE)));
		}
		
		String time = com.technosophos.rhizome.util.Timestamp.now();
		
		// Do protected fields:
		doc.addMetadatum(new Metadatum(UserEnum.CREATED_ON.getKey(), time ));
		doc.addMetadatum(new Metadatum(UserEnum.LAST_MODIFIED.getKey(), time));
		
		try {
			//DocumentRepository repo = this.repoman.getRepository(SETTINGS_REPO);
			//repo.storeDocument(doc);
			this.repoman.storeDocument(SETTINGS_REPO, doc);
		} catch (RhizomeInitializationException rie) {
			String err = String.format("Failed to get a repository to store document %s (user: %s) in %s: %s."
					, doc.getDocID()
					, user
					, SETTINGS_REPO
					, rie.getMessage());
			String ferr = "The server could not store the new user entry. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,rie ));
			return;
		} catch (RepositoryAccessException rae) {
			String err = String.format("Failed to store document %s (user: %s) in %s: %s."
					, doc.getDocID()
					, user
					, SETTINGS_REPO
					, rae.getMessage());
			String ferr = "The server could not store the new user entry. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,rae ));
			return;
		} catch (RhizomeException re) {
			String err = String.format("Failed to store document %s (user: %s) in %s: %s."
					, doc.getDocID()
					, user
					, SETTINGS_REPO
					, re.getMessage());
			String ferr = "The server could not store the new user entry. Try again later.";
			results.add( this.createErrorCommandResult(err, ferr,re ));
			return;
		}

	}
	
	/**
	 * Test strength of password.
	 * Right now, this just requires that the password be 7 characters or more.
	 * @param pw password string
	 * @return true if password is strong enough, false otherwise.
	 */
	public boolean passwordStrengthTest(String pw) {
		if (pw.length() < 7) return false;
		return true;
	}
}
