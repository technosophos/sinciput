package com.technosophos.rhizome.controller;

import java.util.Map;
import java.util.List;

/**
 * A Command Object
 * <p>
 * This interface describes a command object. Command object handle the processing of
 * specific chunks during a request. Each request may be made up of one or more than one
 * command objects.
 * </p>
 * <p>A command object is created, initialized, and used once.</p>
 * @author mbutcher
 *
 */
public interface RhizomeCommand {
	
	/**
	 * Initialize the command. This may be called soon after the empty constructor.
	 * @param comConf
	 */
	public abstract void init(CommandConfiguration comConf);
	
	/**
	 * Execute a command.
	 * 
	 * The CommandParameters object may contain parameters that are not directly related
	 * to this object, since many commands may be executed during a single request.
	 * 
	 * It is this method's responsibility to add some output to the results list.
	 * 
	 * @param params The runtime parameters that this command will need to execute.
	 * @return The result of the command, wrapped as a CommandResult object.
	 */
	public abstract void doCommand(Map params, List results);
	
	
}
