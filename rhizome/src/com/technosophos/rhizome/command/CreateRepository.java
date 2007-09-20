package com.technosophos.rhizome.command;

//import java.util.List;
//import java.util.Map;

import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
//import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.CommandMessage;

/**
 * Command for creating a repository.
 * <p>This class exposes a command for creating a named repository. It will create
 * the file storage repository, as well as an index.</p>
 * <p>Params:</p>
 * <ul>
 * 	<li>repository_name (the value of {@link AbstractCommand.PARAM_REPO_NAME}):
 *   the name of the repository to be created.</li>
 * </ul>
 * <p><string>Output</strong>: This puts a string</p>
 * 
 * @author mbutcher
 *
 */
public class CreateRepository extends AbstractCommand {

	/**
	 * Create a new repository.
	 * This will retrive params from the Map of params. See the class description.
	 */
	//public void doCommand(Map<String, Object> params,
	//		List<CommandResult> results) throws ReRouteRequest {
	public void execute() throws ReRouteRequest {
		String repoName = this.getCurrentRepositoryName(params);
		if(repoName == null) {
			String errMsg = String.format("No repository name (%s) specified in params or in configuration directives.", PARAM_REPO_NAME);
			String friendlyErrMsg = "The new repository could not be created because we don't know what it's name should be.";
			this.createErrorCommandResult(errMsg, friendlyErrMsg);
			return;
		}
		if(this.repoman.hasRepository(repoName)) {
			String errMsg = String.format("Repository %s already exists.", repoName);
			String friendlyErrMsg = "The new repository could not be created because one with the same name already exists.";
			this.createErrorCommandResult(errMsg, friendlyErrMsg);
			return;
		}
		try {
			this.repoman.createRepository(repoName);
		} catch (RhizomeInitializationException rie) {
			String errMsg = String.format("Repository %s could not be created: %s.", repoName, rie.getMessage());
			String friendlyErrMsg = "The new repository could not be created. There was a technical problem..";
			this.createErrorCommandResult(errMsg, friendlyErrMsg, rie);
			return;
		} catch (RepositoryAccessException rae) {
			String errMsg = String.format("Creation of %s failed due to access problems: %s", repoName ,rae.getMessage());
			String friendlyErrMsg = "The new repository could not be created. The was a permissions problem.";
			this.createErrorCommandResult(errMsg, friendlyErrMsg, rae);
			return;
		}
		
		CommandResult cr = CommandMessage.messageInCommandResult(this.comConf, 
				"repository_created", 
				String.format("Repository %s was created", repoName));
		results.add(cr);
	}

}
