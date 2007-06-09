package com.technosophos.rhizome.controller;

import com.technosophos.rhizome.repository.RepositoryManager;
//import com.technosophos.sinciput.commands.install.VerifyEnvironment;
/*
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
*/

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
			throws CommandNotFoundException, CommandInitializationException {
		RhizomeCommand command = null;
		String classname = cconf.getCommandClassname();
		
		if(classname == null) 
			throw new CommandNotFoundException("Command " + cconf.getName()
					+ " has no associated class.");
		
		try {
			Class<?> comClass = Class.forName(classname);
			//Class<?> comClass = altClassLoader(classname);
			command = (RhizomeCommand)comClass.newInstance();
			
		} catch (ClassNotFoundException e) {
			String cpath = System.getProperty("java.class.path");
			String errmsg = String.format("Cannot load class: %s. Class not found in %s.", classname, cpath);
			throw new CommandNotFoundException(errmsg, e);
		} catch (Exception e) {
			String errmsg = String.format("Cannot create object of class %s (%s)", 
					classname, 
					e.getMessage());
			throw new CommandNotFoundException(errmsg, e);
		}
		
		command.init(cconf, rm);
		return command;
	}
	/*
	private static Class<?> altClassLoader(String cname) throws ClassNotFoundException {
		String classpath = System.getProperty("java.class.path");
		String[] paths = classpath.split(":");
		URL [] urls = new URL[paths.length];
		
		for(int i = 0; i < paths.length; ++i) {
			try {
				urls[i] = new URL("file", null, paths[i]);
			} catch (MalformedURLException e) {}	
		}
		URLClassLoader loader = new URLClassLoader(urls);
		
		return loader.loadClass(cname);
	}
	*/
}
