package com.technosophos.sinciput.commands.install;

//import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryContext;
//import static com.technosophos.sinciput.servlet.ServletConstants.*;

public class VerifyEnvironment extends AbstractCommand {

	/**
	 * Do some basic verification.
	 */
	public void execute() throws ReRouteRequest {

		if(this.comConf == null) {
			String errMsg = String.format("RhizomeCommand.init() was not called on %s", this.getClass().getCanonicalName());
			String friendlyErrMsg = "The server is misconfigured, and your request cannot be answered";
			results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg));
		}
		
		RepositoryContext cxt = this.repoman.getContext();
		String repoPath = cxt.getParam("fs_repo_path");
		String indexPath = cxt.getParam("index_path");
		String cmdFileName = cxt.getParam("command_config");
		
		// Do some checking:
		if(repoPath == null) {
			String errMsg = "The fs_repo_path parameter does not exist in the context. Try adding it to your servlet init params in web.xml.";
			results.add(this.createErrorCommandResult(errMsg, errMsg));
			return;
		}
		if(indexPath == null) {
			String errMsg = "The index_path parameter does not exist in the context. Try adding it to your servlet init params in web.xml.";
			results.add(this.createErrorCommandResult(errMsg, errMsg));
			return;
		}
		
		// Create command result and populate it with data.
		CommandResult cr = new CommandResult(this.comConf);
		
		Map<String, String> d = new HashMap<String, String>();
		d.put("fs_repo_path", repoPath);
		d.put("index_path", indexPath);
		d.put("command_config", cmdFileName);
		
		cr.setInfoMap(d);
		
		results.add(cr);
	}

}
