package com.technosophos.sinciput.commands.auth;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.sinciput.servlet.SinciputSession;
import static com.technosophos.sinciput.servlet.ServletConstants.*;

/**
 * Command to check if authentication has been performed.
 * This inserts an exception into the results if the user has not yet authenticated.
 * If a user has authenticated, this exits silently.
 * @author mbutcher
 *
 */
public class BasicRepositoryRequireAuthN extends AbstractCommand {

	public void execute() {
		SinciputSession sess = (SinciputSession)params.get(REQ_PARAM_SESSION);
		if( sess.userLoggedIn()) return;
		
		//System.err.println("Not authenticated");
		
		results.add(this.createErrorCommandResult(
				"Not Authenticated.", 
				"You must login first."));
		
		//,
		//		new Exception("Must Authenticate.")));
	}
}
