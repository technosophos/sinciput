package com.technosophos.rhizome.web.command;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
//import com.technosophos.rhizome.repository.DocumentRepository;
import static com.technosophos.rhizome.web.ServletConstants.*;

public class InstallRedirect extends AbstractCommand {

	/**
	 * Redirects to installer if no config repository exists.
	 */
	public void execute() throws ReRouteRequest {
		//System.err.println("Checking on installer.");
		if(! this.repoman.hasRepository(SETTINGS_REPO) ) {
			System.err.println("Re-Routing to install.");
			throw new ReRouteRequest("install", "Install has not comlpeted.");
		}
	}

}
