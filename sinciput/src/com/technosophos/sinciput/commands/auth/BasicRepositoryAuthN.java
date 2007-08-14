package com.technosophos.sinciput.commands.auth;

import java.util.List;
import java.util.Map;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.ReRouteRequest;
import static com.technosophos.sinciput.servlet.ServletConstants.*;

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
	 * Name of next request.
	 * If auth succeeds, get next request from
	 * {@link CommandConfiguration.getDirective(String)}
	 * using this string: "next_request".
	 */
	public static final String PARAM_NEXT_REQUEST = "next_request";

	/**
	 * Perform authentication.
	 */
	public void doCommand(Map<String, Object> params,
			List<CommandResult> results) throws ReRouteRequest {
		
		String uid = this.getFirstParam(params, PARAM_AUTH_UID).toString();
		String pw = this.getFirstParam(params, PARAM_AUTH_PASSWD).toString();
		String[] rrc = this.comConf.getDirective(DIR_AUTH_FAILED_REQ);
		String errShell = "Authentication Failed: %s";
		
		// Make sure we have a command to forward to.
		if(rrc == null || rrc.length == 0) {
			String errMsg = String.format("Not found in command configuration: %s", DIR_AUTH_FAILED_REQ);
			String friendlyErrMsg = "Authentication Failed: The server is misconfigured.";
			this.createErrorCommandResult(errMsg, friendlyErrMsg);
		}
		
		if( uid == null || pw == null )
			throw new ReRouteRequest(rrc[0], String.format(errShell, "username and password cannot be null."));
		
		/*
		 * Do login stuff here:
		 */
		if( this.doAuthN(uid, pw)) {
			// If there is a place to forward, do the forward.
			String next = DEFAULT_REQUEST;
			if(this.hasParam(params, PARAM_NEXT_REQUEST))
				next = this.getFirstParam(params, PARAM_NEXT_REQUEST).toString();
			
			// FIXME: Before re-routing, check to make sure 'next' is a command.
			throw new ReRouteRequest(next, "You are logged in.");
		} else 
			throw new ReRouteRequest(rrc[0], String.format(errShell, "username/password combination is incorrect."));
	}
	
	protected boolean doAuthN(String uid, String pw) {
		// TODO: Finish Me!
		/*
		 * This should look up an entry in the default repository. 
		 */
		return false;
	}

}
