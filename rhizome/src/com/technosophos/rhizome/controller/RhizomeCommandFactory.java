package com.technosophos.rhizome.controller;

import com.technosophos.rhizome.repository.RepositoryManager;

/**
 * Factory for creating RhizomeCommand objects.
 * @author mbutcher
 *
 */
public class RhizomeCommandFactory {
	
	/**
	 * Using a command configuration, get a new command object.
	 * This returns an initialized RhizomeCommand object, ready to have doCommand() called.
	 * @param commandName the name of the command.
	 */
	public static RhizomeCommand getCommand(CommandConfiguration cconf, RepositoryManager rm)
			throws CommandNotFoundException {
		RhizomeCommand command = null;
		String classname = cconf.getCommandClassname();
		
		if(classname == null) 
			throw new CommandNotFoundException("Command " + cconf.getName()
					+ " has no associated class.");
		
		try {
			Class<?> comClass = Class.forName(classname);
			command = (RhizomeCommand)comClass.newInstance();
			
		} catch (ClassNotFoundException e) {
			String errmsg = "Cannot load class: " + classname;
			throw new CommandNotFoundException(errmsg);
		} catch (Exception e) {
			String errmsg = "Cannot create object of class " 
				+ classname
				+ "(Reason: " + e.getMessage() + ")";
			throw new CommandNotFoundException(errmsg);
		}
		
		command.init(cconf, rm);
		return command;
	}
}
