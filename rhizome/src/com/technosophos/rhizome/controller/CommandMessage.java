package com.technosophos.rhizome.controller;

/**
 * Basic command results container.
 * This is a basic object -- something like a 3-tuple or triple -- that 
 * Commands can choose to return.
 * <p>A command name is the name of the command that created this.</p>
 * <p>A message name is a name that external objects may use to distinguish this
 * from another command message.</p>
 * <p>A message value is the message intended to be displayed to a user.</p>
 * @author mbutcher
 * @version $id$
 */
public class CommandMessage {
	protected String name = null;
	protected String value = null;
	protected String command = null;
	
	public CommandMessage(String commandName, String msgName, String msgValue) {
		this.name = msgName;
		this.value = msgValue;
		this.command = commandName;
	}
	
	public String getCommandName() {
		return this.command;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Convenience function for getting a command result with a command message wrapped inside.
	 * @param cc
	 * @param msgName
	 * @param msgValue
	 * @return
	 */
	public static CommandResult messageInCommandResult( CommandConfiguration cc, 
			String msgName, 
			String msgValue) {
		CommandResult cr = new CommandResult(cc);
		cr.setResult(new CommandMessage(cc.getName(), msgName, msgValue));
		return cr;
		
	}
	
}
