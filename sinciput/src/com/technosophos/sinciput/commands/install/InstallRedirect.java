package com.technosophos.sinciput.commands.install;

import java.util.List;
import java.util.Map;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.ReRouteRequest;
//import com.technosophos.rhizome.repository.DocumentRepository;
import static com.technosophos.sinciput.servlet.ServletConstants.*;

public class InstallRedirect extends AbstractCommand {

	/**
	 * Redirects to installer if no config repository exists.
	 */
	public void doCommand(Map<String, Object> params,
			List<CommandResult> results) throws ReRouteRequest {
		//System.err.println("Checking on installer.");
		if(! this.repoman.hasRepository(SETTINGS_REPO) ) {
			System.err.println("Re-Routing to install.");
			throw new ReRouteRequest("install", "Install has not comlpeted.");
		}
	}

}
