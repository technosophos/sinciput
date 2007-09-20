package com.technosophos.sinciput.commands.install;

import java.util.HashMap;
import java.io.File;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.CommandResult;
//import com.technosophos.rhizome.controller.CommandMessage;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryContext;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

import static com.technosophos.sinciput.servlet.ServletConstants.*;

public class InstallBase extends AbstractCommand {

	/**
	 * Param that indicates where the install path is.
	 * "install_path"
	 */
	public static final String PARAM_INSTALL_PATH = "install_path";
	
	/**
	 * Key for message about creating the repository.
	 * This is placed into the CommandResult's info.
	 * "repo_create"
	 */
	public static final String INFO_REPO_CREATE = "repo_create";
	/**
	 * Key for a message about creating the index.
	 * This is placed into the CommandResult's info.
	 * "index_create"
	 */
	public static final String INFO_INDEX_CREATE = "index_create";
	
	public static final String INFO_SETTINGS_REPO_CREATE = "settings_repo_create";
	
	/**
	 * Perform base installation procedures.
	 */
	public void execute() throws ReRouteRequest {
		
		HashMap<String, String> info = new HashMap<String, String>();
		RepositoryContext cxt = this.repoman.getContext();
		String fs_repo_path = cxt.getParam("fs_repo_path");
		String index_path = cxt.getParam("index_path");
		
		// Do some checking:
		if(fs_repo_path == null) {
			String errMsg = "The fs_repo_path parameter does not exist in the context. Try adding it to your servlet init params in web.xml.";
			results.add(this.createErrorCommandResult(errMsg, errMsg));
			return;
		}
		if(index_path == null) {
			String errMsg = "The index_path parameter does not exist in the context. Try adding it to your servlet init params in web.xml.";
			results.add(this.createErrorCommandResult(errMsg, errMsg));
			return;
		}
		
		File repo_dir = new File(fs_repo_path);
		File index_dir = new File(index_path);
		
		String msg = "";
		
		if(repo_dir.exists()) {
			if(!repo_dir.isDirectory()) {
				String errMsg = String.format("Cannot use %s. The location exists, but is not a directory.", repo_dir.getAbsolutePath());
				results.add(this.createErrorCommandResult(errMsg, errMsg));
				return;
			}
			if(!repo_dir.canRead() || !repo_dir.canWrite()) {
				String errMsg = String.format("Cannot use %s. The location exists, but does not allow reading and writing.", repo_dir.getAbsolutePath());
				results.add(this.createErrorCommandResult(errMsg, errMsg));
				return;
			}
			// ELSE ok
			msg = String.format(
					"Directory %s will be used for storing the repository.", 
					repo_dir.getAbsolutePath());
		} else {
			// Create repo dir:
			boolean b = repo_dir.mkdirs();
			if( b == false ) {
				String errMsg = String.format("Could not create %s. This may be due to file system permissions.", repo_dir.getAbsolutePath());
				results.add(this.createErrorCommandResult(errMsg, errMsg));
				return;
			}
			msg = String.format(
					"Directory %s has been created, and the repository will be stored there.", 
					repo_dir.getAbsolutePath());
		}
		
		info.put(INFO_REPO_CREATE,msg);
		
		if(index_dir.exists()) {
			if(!index_dir.isDirectory()) {
				String errMsg = String.format("Cannot use %s for an index. The location exists, but is not a directory.", index_dir.getAbsolutePath());
				results.add(this.createErrorCommandResult(errMsg, errMsg));
				return;
			}
			if(!index_dir.canRead() || !index_dir.canWrite()) {
				String errMsg = String.format("Cannot use %s for an index. The location exists, but does not allow reading and writing.", index_dir.getAbsolutePath());
				results.add(this.createErrorCommandResult(errMsg, errMsg));
				return;
			}
			// ELSE ok
			msg = String.format(
					"Directory %s will be used for storing the index.", 
					index_dir.getAbsolutePath());
		} else {
			// Create repo dir:
			boolean b = index_dir.mkdirs();
			if( b == false ) {
				String errMsg = String.format("Could not create %s. This may be due to file system permissions.", index_dir.getAbsolutePath());
				results.add(this.createErrorCommandResult(errMsg, errMsg));
				return;
			}
			msg = String.format(
					"Directory %s has been created, and the index will be stored there.", 
					repo_dir.getAbsolutePath());
		}
		
		info.put(INFO_INDEX_CREATE, msg);
		
		
		/*
		 * Next, we need to actually create the basic repository.
		 */
		
		if( this.repoman.hasRepository(SETTINGS_REPO) ) {
			String errMsg = String.format("Failed Install: A repository named %s already exists in %s or %s.", 
					SETTINGS_REPO, 
					fs_repo_path, 
					index_path);
			results.add(this.createErrorCommandResult(errMsg, errMsg));
			return;
		}
		
		try {
			this.repoman.createRepository(SETTINGS_REPO);
		} catch ( RepositoryAccessException e) {
			String errMsg = String.format("Access problem when creating %s: %s", SETTINGS_REPO, e.getMessage());
			results.add(this.createErrorCommandResult(errMsg, errMsg, e));
			return;
		} catch (RhizomeInitializationException e) {
			String errMsg = String.format("Problem initializing repository when creating %s: %s", SETTINGS_REPO, e.getMessage() );
			results.add(this.createErrorCommandResult(errMsg, errMsg, e));
			return;
		}
		
		info.put(INFO_SETTINGS_REPO_CREATE, "Settings have been created.");
		
		CommandResult cr = new CommandResult(this.comConf);
		cr.setInfoMap(info);
		
		results.add(cr);

	}

}
