package com.technosophos.rhizome.command;

import java.util.List;
import java.util.Map;

import com.technosophos.rhizome.controller.CommandConfiguration;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.RhizomeCommand;
import com.technosophos.rhizome.repository.RepositoryManager;

public abstract class AbstractCommand implements RhizomeCommand {

	/** the CommandConfiguration */
	protected CommandConfiguration comConf = null;
	/** the RepositoryManager instance */
	protected RepositoryManager repoman = null;
	
	//inherit javadoc
	public abstract void doCommand(Map<String, Object> params, List<CommandResult> results);

	/**
	 * This simply stores the {@link CommandConfiguration} and {@link RepositoryManager} locally.
	 * Override this if you need any special initialization done.
	 * @param CommandConfiguration command configuration
	 * @param RepositoryManager initialized repository manager
	 */
	public void init(CommandConfiguration comConf, RepositoryManager rm) {
		this.comConf = comConf;
		this.repoman = rm;
	}
	
	/**
	 * Get the correctly-prefixed parameter name.
	 * <p>You <i>ought</i> to use this method for getting a paramter name.</p>
	 * <p>
	 * If this command should use a prefix, this will return the prefixed parameter name.
	 * A command may or may not need to use a prefixed name. This is determined primarily
	 * by the {@link CommandConfiguration} object.
	 * </p>
	 * @param param
	 * @return
	 */
	protected String getPrefixedParamName(String param) {
		if(this.comConf.hasPrefix()) return this.comConf.getPrefix() + param;
		return param;
	}
	
	/**
	 * Get a parameter. Automatically use the prefix if present.
	 * @param params the parameters hash.
	 * @param name
	 * @return the object value in the map, or null if the value isn't found.
	 */
	protected Object getParam(Map<String, Object> params, String name) {
		String pname = this.getPrefixedParamName(name);
		if(!params.containsKey(pname)) return null;
		return params.get(pname);
	}

}
