package com.technosophos.sinciput.commands;

import static com.technosophos.sinciput.servlet.ServletConstants.REQ_PARAM_SESSION;
import static com.technosophos.sinciput.servlet.ServletConstants.SETTINGS_REPO;

import java.util.List;
import java.util.Map;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.ReRouteRequest;
//import com.technosophos.rhizome.repository.DocumentNotFoundException;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.util.RepositoryUtils;
import com.technosophos.rhizome.document.*;
import com.technosophos.sinciput.SinciputException;
import com.technosophos.sinciput.commands.auth.AuthenticationException;
import com.technosophos.sinciput.servlet.SinciputSession;
import com.technosophos.sinciput.types.admin.RepoDescriptionEnum;

/**
 * Sinciput command abstraction.
 * <p>An abstract class that provides a variety of convenience methods and
 * helper functions used by commands in Sinciput.</p>
 * @author mbutcher
 *
 */
public abstract class SinciputCommand extends AbstractCommand {
	
	protected SinciputSession ses = null;
	
	/**
	 * Command configuration directive for authentication.
	 * If the no-auth directive is set to 'no', 'false', or 'off', then the {@link doCommand(Map, List)}
	 * will not require authentication before proceeding. By default, doCommand() requires authentication.
	 * Value: 'no-auth'
	 */
	public final static String DIR_NO_AUTH = "no-auth";

	protected abstract void execute() throws ReRouteRequest;

	// Override the parent's doCommand object.
	/**
	 * Setup command.
	 * <p>This method overrides the default one in {@link AbstractCommand}. It does the following:</p>
	 * <ul>
	 * <li>It retrieves the session and does any necessary setup. See {@link SinciputSession}</li>
	 * <li>It checks to see if the user is authenticated. See {@link checkAuth()}.</li>
	 * <li>It calls the parent, which in turn calls the {@link execute()} method.</li>
	 * </ul>
	 * @see AbstractCommand
	 */
	public void doCommand(Map<String, Object> params, List<CommandResult> results) throws ReRouteRequest {

		// Not that session never has a prefix. Thus we can safely get it this way.
		this.ses = (SinciputSession)params.get(REQ_PARAM_SESSION);
		
		if( !this.checkAuth() ) {
			String friendlyErrMsg = "You must login first.";
			String errMsg = "SinciputCommand: Unauthenticated user access.";
			System.err.println("User not logged in.");
			results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg, new AuthenticationException("No Auth")));
			return;
		}
		
		// Do this last, since it calls execute();
		super.doCommand(params, results);
	}
	
	/**
	 * Set the current repository.
	 * <p>This will create a new repository searcher and then look for the given repository name.
	 * If it is found, it will be set as the current repository.</p>
	 * <p>If you have already created a repository searcher, it is more efficient to use the other
	 * form of this method.</p>
	 * @param repoName Name of the repository.
	 * @throws RhizomeInitializationException If there is an error creating the searcher.
	 * @throws RepositoryAccessException If there is an access violation creating the searcher.
	 * @see setCurrentRepository(String, RepositorySearcher)
	 * @see RepositorySearcher
	 */
	protected void setCurrentRepository(String repoName ) throws RhizomeInitializationException, RepositoryAccessException {
		RepositorySearcher s_search;
		s_search = this.repoman.getSearcher(SETTINGS_REPO);
		this.setCurrentRepository(repoName, s_search);
	}
	
	/**
	 * Set the current repository to repoName.
	 * <p>This sets the current repository, after performing some basic checks.</p>
	 * <h3>Caveats</h3>
	 * <p>Note that certain repositories (namely, those that begin with '__' (double underscore)
	 * cannot be set as the current repository.</p>
	 * @param repoName Name of the repository.
	 * @param search Initialized repository searcher.
	 * @throws RepositoryAccessException Thrown if the repository does not exist.
	 * @see RepositorySearcher
	 */
	protected void setCurrentRepository(String repoName, RepositorySearcher search) throws RepositoryAccessException {
		if( repoName.startsWith("__")) 
			throw new RepositoryAccessException("Illegal name: " + repoName);
		String[] matches = search.getDocIDsByMetadataValue(
				RepoDescriptionEnum.REPO_NAME.getKey(), 
				repoName);
		if( matches.length == 0) 
			throw new RepositoryAccessException(String.format("No repository named %s.", repoName));
		this.ses.setActiveRepository(repoName, matches[0]);
	}
	
	/**
	 * This creates a new searcher and then searches for info on current repository.
	 * If possible, use {@link getCurrentRepository(RepositorySearcher)} instead.
	 * @return Name of the current repository.
	 * @throws RhizomeInitializationException if an error occured creating the searcher.
	 * @throws SinciputException If it can't get user information from the session.
	 * @see getCurrentRepository(RepositorySearcher)
	 */
	protected String getCurrentRepository() throws RhizomeInitializationException, SinciputException {
		RepositorySearcher s_search;
		s_search = this.repoman.getSearcher(SETTINGS_REPO);
		return this.getCurrentRepository(s_search);
	}
	
	/**
	 * Get the name of the current repository.
	 * This returns the repository name (NOT the human-readable title). This can be passed on
	 * to a repository manager to retrieve the correct repository. 
	 * @param search Initialized repository searcher
	 * @return Name of the currently-select repository.
	 */
	protected String getCurrentRepository(RepositorySearcher search) throws SinciputException {
		
		// Get repository
		String repoName = ses.getActiveRepositoryName();
		
		if(repoName == null || repoName.startsWith("__")) {
			String username = ses.getUserName();
			if( username == null ) throw new SinciputException("User ID not found in session.");
			// Lookup the user's repository:
			
			// There should be a repository with this name:
			repoName = RepositoryUtils.generateRepoID(username);
			try {
				String[] matches = search.getDocIDsByMetadataValue(
								RepoDescriptionEnum.REPO_NAME.getKey(), 
								repoName);
				// If we get a match, we know that we have a repo by that name.
				if( matches == null || matches.length == 0) {
					String err = "No user repository for " + username;
					String ferr = "We cannot find your default repository. Try again later.";
					results.add(this.createErrorCommandResult(err, ferr));
					return null;
				}
				
				// Set this as the active repository.
				ses.setActiveRepository(repoName, matches[0]);

			} catch (RhizomeException e) {
				String err = "Exception trying to get user record: " + e.getMessage();
				String ferr = "Something went wrong while trying to get your user record. Maybe later...?";
				results.add(this.createErrorCommandResult(err, ferr, e));
				return null;
			}
			
		}
		return repoName;
	}
	
	/**
	 * Does user have perms to read and write to repository?
	 * User must be an owner or member.
	 * @return True if the user is allowed to read and write to this repo.
	 */
	protected boolean userCanWriteRepo(DocumentRepository r) {
		RhizomeDocument doc;
		String username = this.ses.getUserName();
		try {
			doc = r.getDocument(this.ses.getActiveRepositoryUUID());
		} catch (Exception e) {
			return false; // Can't read it if can't access it.
		}
		Metadatum owners =  doc.getMetadatum(RepoDescriptionEnum.OWNER.getKey() );
		Metadatum members = doc.getMetadatum(RepoDescriptionEnum.MEMBERS.getKey() );
		
		if( owners != null && owners.hasValue(username)) return true;
		else return (members != null && members.hasValue(username));
	}
	

	/**
	 * Does user have permission to read the repository?
	 * User must be owner, member, or guest of the repository.
	 * @return True if user is allowed acces to read from repo.
	 * @see RepoDescriptionEnum
	 */
	protected boolean userCanReadRepo( DocumentRepository r) {
		RhizomeDocument doc;
		String username = this.ses.getUserName();
		try {
			doc = r.getDocument(this.ses.getActiveRepositoryUUID());
		} catch (Exception e) {
			return false; // Can't read it if can't access it.
		}
		Metadatum owners =  doc.getMetadatum(RepoDescriptionEnum.OWNER.getKey() );
		Metadatum members = doc.getMetadatum(RepoDescriptionEnum.MEMBERS.getKey() );
		Metadatum guests = doc.getMetadatum(RepoDescriptionEnum.GUESTS.getKey() );

		if( owners != null && owners.hasValue(username)) return true;
		else if(guests != null && guests.hasValue(username)) return true;
		else return (members != null && members.hasValue(username));
	}
	
	/**
	 * Check to see if user is authenticated.
	 * <p>SinciputCommand automatically checks to see if a user is authenticated (unless the 
	 * {@link DIR_NO_AUTH} directive is set in the commnad configuration). This method performs the 
	 * check. By default, it simply calls {@link SinciputSession.userLoggedIn()} method.</p>
	 * <p>If this is not the desired behavior, you may override this method.</p>
	 * @return True if the user is authenticated, false otherwise.
	 * @see DIR_NO_AUTH
	 */
	protected boolean checkAuth() {
		if(this.comConf.hasDirective(DIR_NO_AUTH)) {
			String[] v = this.comConf.getDirective(DIR_NO_AUTH);
			for( String s: v ) {
				if ("no".equalsIgnoreCase(s) 
						|| "off".equalsIgnoreCase(s) 
						|| "false".equalsIgnoreCase(s)) 
					return true;
				// Otherwise, fall through
			}
		}
		return this.ses.userLoggedIn();
	}
	
	/**
	 * Get the current session.
	 * If no session is found (which means no one is logged in on this connection), then 
	 * this returns null.
	 * @return The current session, or null.
	 */
	protected SinciputSession getSession() {
		return this.ses;
	}

}
