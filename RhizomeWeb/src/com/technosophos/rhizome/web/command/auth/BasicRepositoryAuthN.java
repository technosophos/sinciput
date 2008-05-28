package com.technosophos.rhizome.web.command.auth;

//import java.util.List;
//import java.util.Map;
import java.util.HashMap;

import com.technosophos.rhizome.web.BaseRhizomeSession;
import com.technosophos.rhizome.web.types.UserEnum;
import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

import static com.technosophos.rhizome.web.ServletConstants.*;

/**
 * Implements basic repository authentication.
 * <p>Directives:</p>
 * <ul>
 *   <li>auth_failed: The command to reroute to if the login fails. (default: "default")</li>
 *   <li>passthru: Whether this should passthru. Boolean. (default: false)</li>
 * </ul>
 * <p>Params:</p>
 * <ul>
 *   <li>uid: User name</li>
 *   <li>passwd: User's password.</li>
 *   <li>next_request: Next command to reroute to if AuthN is successful (only works when 
 *   passthru is false).</li>
 * </ul>
 * @author mbutcher
 *
 */
public class BasicRepositoryAuthN extends AbstractCommand {
	
	/**
	 * Name of the parameter that contains the user ID.
	 * "uid"
	 */
	public static final String PARAM_AUTH_UID = "uid";
	/**
	 * Name of the parameter that contains the password.
	 * "password"
	 */
	public static final String PARAM_AUTH_PASSWD = "passwd";
	
	/**
	 * Name of request reroute directive.
	 * If auth fails, get next request from {@link CommandConfiguration.getDirective(String)}
	 * using this string.
	 * "auth_failed"
	 */
	public static final String DIR_AUTH_FAILED_REQ = "auth_failed";
	
	/**
	 * Directiev for enabling passthru mode.
	 * In passthru mode, reather than doing a rereoutrequest, this command silently returns
	 * after a successful login. This makes it possible to chain this command along with
	 * others.
	 */
	public static final String DIR_AUTH_PASSTHRU = "passthru";
	
	/**
	 * Name of next request.
	 * If auth succeeds, get next request from
	 * {@link CommandConfiguration.getDirective(String)}
	 * using this string: "next_request".
	 */
	public static final String PARAM_NEXT_REQUEST = "next_request";

	/**
	 * Perform authentication.
	 * 
	 * There are two modes of authentication: stand-alone (the default) and passthru.
	 * <p>In <em>Stand Alone</em> mode, when authentication succeeds, the command will
	 * reroute the request elsewhere.</p>
	 * <p>In <em>Passthru</em> mode, when AuthN suceeds, this will silently return. Use passthru
	 * mode to chain this command with other commands.</p>
	 * <p>To set passthru mode, use the "passthru" configuration directive in commands.xml.</p>
	 */
	public void execute() throws ReRouteRequest {
		boolean passthru_mode = false;
		String[] rrc = this.comConf.getDirective(DIR_AUTH_FAILED_REQ);
		String[] passthru = this.comConf.getDirective(DIR_AUTH_PASSTHRU);
		String errShell = "Authentication Failed: %s";
		
		// Why use params.get()? Because we don't want to use a prefixed value.
		BaseRhizomeSession sess = (BaseRhizomeSession) params.get(REQ_PARAM_SESSION);
		if( sess == null ) throw new Error("Session is null!");
		
		if( passthru != null && passthru.length > 0 && passthru[0].equalsIgnoreCase("true")) {
			passthru_mode = true;
			
			// If user is already logged in, just go on...
			if(sess.userLoggedIn()) return;
		}
		
		// Make sure we have a command to forward to.
		String rrr_cmd = DEFAULT_REQUEST;
		if(rrc != null || rrc.length > 0) {
			rrr_cmd = rrc[0];
		}
		
		// If no login/password, redirect.
		if( !this.hasParam(PARAM_AUTH_UID) || !this.hasParam(PARAM_AUTH_PASSWD) )
			throw new ReRouteRequest(rrr_cmd, String.format(errShell, "username and password cannot be null."));
		
		String uid = this.getFirstParam(PARAM_AUTH_UID, null).toString();
		String pw = this.getFirstParam(PARAM_AUTH_PASSWD, null).toString();
		System.err.format("Username: %s, Password: %s\n", uid, pw);
		
		/*
		 * Do login stuff here:
		 */
		String u_docid = null;
		try {
			u_docid = this.doAuthN(uid, pw);
			
		} catch (RhizomeInitializationException e) {
			String err = "Error trying to create a searcher:" + e.getMessage();
			String ferr = "We can not verify the user ID at this time.";
			results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} catch (RepositoryAccessException e) {
			String err = "Error trying to search index:" + e.getMessage();
			String ferr = "We can not verify the user ID at this time.";
			results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} 
		
		if( u_docid != null ) {
			
			
			sess.setUser(u_docid, uid);
			
			if( passthru_mode ) {
				results.add(new CommandResult("Logged In."));
				return;
			}
			
			// If there is a place to forward, do the forward.
			String next = DEFAULT_REQUEST;
			if(this.hasParam(PARAM_NEXT_REQUEST))
				next = this.getFirstParam(PARAM_NEXT_REQUEST, null).toString();
			
			// If next isn't a valid command, default will be used, anyway.
			throw new ReRouteRequest(next, "You are logged in.");
		}
			
		throw new ReRouteRequest(rrr_cmd, String.format(errShell, "username/password combination is incorrect."));
	}
	
	/**
	 * Perform the authentication.
	 * Given a username and password, verify that we have a matching record.
	 * 
	 * <p>This version retrieves the information from the repository via a searcher, and verifies
	 * that the two match. If they do not match, this returns null. Otherwise, this returns
	 * the document ID of the matching document.</p>
	 * 
	 * <p>Note that the password will be "prepared" using the {@link preparePassword(String)} 
	 * method in this class.</p>
	 * @param uid User ID (username)
	 * @param pw Plain text password
	 * @return DocID for the document, or null of no document was found.
	 * @throws RhizomeInitializationException If the searcher could not be created.
	 * @throws RepositoryAccessException If the search failed abnormally.
	 */
	protected String doAuthN(String uid, String pw) 
				throws RhizomeInitializationException, RepositoryAccessException {
		
		// This throws an initialization exception if can't get searcher:
		RepositorySearcher rs = this.repoman.getSearcher(SETTINGS_REPO);
		String type = UserEnum.TYPE.getFieldDescription().getDefaultValue();
		
		HashMap<String, String> narrower = new HashMap<String, String>(4, (float)0.9);
		narrower.put(UserEnum.USERNAME.getKey(), uid);
		narrower.put(UserEnum.PASSWORD.getKey(), this.preparePassword(pw));
		narrower.put(UserEnum.TYPE.getKey(), type);
		
		//System.err.format( "User: %s, Password: %s, Type: %s\n", uid, pw, type);
		
		// This throws an access exception if error:
		String[] res = rs.narrowingSearch(narrower);
		
		//String[] names = rs.getMetadataNames();
		//System.err.format("There are %s names.", String.valueOf(names.length));
		
		// If length is > 0, we know that at least one user matches username, password, type.
		if( res.length > 0 ) return res[0];
		System.err.println("No users found in "+ SETTINGS_REPO +".");
		return null;
	}
	
	/**
	 * Prepare a password.
	 * The user agent may supply a password in clear text. But the value in the repository may
	 * be encrypted or hashed. This method prepares the password for matching against the
	 * repository.
	 * 
	 * For example, if the repository uses MD5 hashing, this method should hash the password
	 * accordingly.
	 * 
	 * The default version does nothing.
	 * @param pw Plain text password.
	 * @return prapared password.
	 */
	protected String preparePassword( String pw ) {
		return pw;
	}

}
