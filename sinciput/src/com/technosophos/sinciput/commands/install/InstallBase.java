package com.technosophos.sinciput.commands.install;

import java.util.List;
import java.util.Map;
import java.io.File;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.CommandMessage;
import com.technosophos.rhizome.controller.ReRouteRequest;

public class InstallBase extends AbstractCommand {

	/**
	 * Param that indicates where the install path is.
	 * "install_path"
	 */
	public static final String PARAM_INSTALL_PATH = "install_path";
	
	/**
	 * Perform base installation procedures.
	 */
	public void doCommand(Map<String, Object> params,
			List<CommandResult> results) throws ReRouteRequest {
		String fs_repo_path;
		String index_path;
		try {
			fs_repo_path = this.comConf.getDirective("fs_repo_path")[0];
			index_path = this.comConf.getDirective("index_path")[0];
		} catch (Exception e) {
			String errMsg = "The directives fs_repo_path and index_path must be specified in the servlet's init parameters (web.xml). Failed to create repository.";
			results.add(this.createErrorCommandResult(errMsg, errMsg, e));
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
		
		/*
		 * Store msg somewhere...
		 */
		
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
		
		results.add(CommandMessage.messageInCommandResult(this.comConf, 
				"success", msg));

	}

}
