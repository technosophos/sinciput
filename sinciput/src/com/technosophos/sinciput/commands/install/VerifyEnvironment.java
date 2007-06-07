package com.technosophos.sinciput.commands.install;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.ReRouteRequest;
import static com.technosophos.sinciput.servlet.ServletConstants.*;

public class VerifyEnvironment extends AbstractCommand {

	/**
	 * Do some basic verification.
	 */
	public void doCommand(Map<String, Object> params,
			List<CommandResult> results) throws ReRouteRequest {
		List<String> l = new ArrayList<String>();

		if(this.comConf == null) {
			String errMsg = String.format("RhizomeCommand.init() was not called on %s", this.getClass().getCanonicalName());
			String friendlyErrMsg = "The server is misconfigured, and your request cannot be answered";
			results.add(this.createErrorCommandResult(errMsg, friendlyErrMsg));
		}
		
		String repoPath = this.repoman.getContext().getParam("fs_repo_path");
		String indexPath = this.repoman.getContext().getParam("index_path");
		
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
		
		//String [] repoPaths = this.comConf.getDirective("fs_repo_path");
		//String [] indexPaths = this.comConf.getDirective("index_path");
		
		//Map<String, String[]> d = this.comConf.getDirectives();
		
		l.add("The main path to the application is: " + params.get(BASE_PATH).toString());
		l.add("Configuration files are stored in: " + params.get(CONFIG_PATH).toString());
		l.add("Static resources are stored in: " + params.get(RESOURCE_PATH).toString());
		l.add("The repository is stored in: " + repoPath);
		l.add("The index files are stored in: " + indexPath);
		
		

		results.add(new CommandResult(this.comConf, l));
	}

}
