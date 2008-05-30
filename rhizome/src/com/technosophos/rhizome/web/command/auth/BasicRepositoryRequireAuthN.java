package com.technosophos.rhizome.web.command.auth;

import com.technosophos.rhizome.web.RhizomeSession;
import com.technosophos.rhizome.command.AbstractCommand;

import static com.technosophos.rhizome.web.ServletConstants.*;

/**
 * Command to check if authentication has been performed.
 * This inserts an exception into the results if the user has not yet authenticated.
 * If a user has authenticated, this exits silently.
 * @author mbutcher
 *
 */
public class BasicRepositoryRequireAuthN extends AbstractCommand {

	public void execute() {
		RhizomeSession sess = (RhizomeSession)params.get(REQ_PARAM_SESSION);
		if(sess != null && sess.userLoggedIn()) return;
		
		//System.err.println("Not authenticated");
		
		results.add(this.createErrorCommandResult(
				"Not Authenticated.", 
				"You must login first."));
		
		//,
		//		new Exception("Must Authenticate.")));
	}
}
