package com.technosophos.rhizome.controller;

import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

/**
 * Controller for handling Rhizome requests.
 * <p>
 * This takes requests, unpacks them into queues of commands, 
 * and then processes the commands in order.
 * </p>
 * <p>
 * The request-to-command queue mapping is performed during 
 * initialization. Then, at each doRequest() method, the given information is unpacked
 * into a command queue, and each command is executed in sequence.
 * </p>
 * @author mbutcher
 *
 */
public class RhizomeController {

	private HashMap<String, Queue<CommandConfiguration>> cqMap = null;
	
	/**
	 * This constructor should only be used when necessary.
	 *
	 */
	public RhizomeController() {
		
	}
	
	/**
	 * THis is the preferred constructor.
	 * If you use this constructor, you do not need to initialize with {@code init()}.
	 * @param cqMap
	 */
	public RhizomeController(HashMap<String, Queue<CommandConfiguration>> cqMap) {
		this.cqMap = cqMap;
	}
	
	/**
	 * Initialize this controller.
	 * You <b>MUST</b> use int() if you used the empty constructor.
	 * @see isInitialized()
	 * @param cqMap
	 */
	public void init(HashMap<String, Queue<CommandConfiguration>> cqMap) {
		this.cqMap = cqMap;
	}
	
	/**
	 * Returns true if this object has been initialized.
	 * 
	 * @return
	 */
	public boolean isInitialized() {
		if(this.cqMap == null) return false;
		return true;
	}
	
	/**
	 * Handle a request.
	 * This handles a request, which is typically made up of one or more commands, and
	 * returns the results in a List. Exceptions thrown by commands are caught and stored
	 * in the CommandResult objects.
	 * @param requestName
	 * @param data
	 * @return
	 * @throws RequestNotFoundException
	 */
	public LinkedList<CommandResult> doRequest(String requestName, Map data) 
			throws RequestNotFoundException {
		
		LinkedList<CommandResult> results = new LinkedList<CommandResult>();
		
		if(!this.cqMap.containsKey(requestName))
			throw new RequestNotFoundException("No match for request \"" 
					+ requestName + "\" ");
		Iterator<CommandConfiguration> commands = this.cqMap.get(requestName).iterator();
		
		
		try {
			while (commands.hasNext()) {
				CommandConfiguration cconf = commands.next();
				this.doCommand(cconf, data, results);
			}
		} catch (FatalCommandException fce) {
			results.clear();
			CommandResult res = new CommandResult();
			String errMsg = "Fatal Error in " + requestName + ".";
			String ferrMsg = "The server experienced a severe error, and cannot complete this task.";
			res = new CommandResult();
			res.setError(errMsg, ferrMsg, fce);
			results.add(res);
		}		
		return results;
	}
	
	/**
	 * Run a command.
	 * Any errors are caught and wrapped in a CommandResult.
	 * @param cconf
	 * @param data
	 * @return
	 */
	protected void doCommand(CommandConfiguration cconf, Map data, List results) 
			throws FatalCommandException {
		try {
			RhizomeCommand command = RhizomeCommandFactory.getCommand(cconf);
			command.doCommand(data, results);
		} catch (CommandNotFoundException cnfe) {
			if (cconf.failOnError()) {
				String err = "Fatal error in " +cconf.getName() + ".";
				throw new FatalCommandException(err, cnfe);
			} else {
				String errMsg = "Command " + cconf.getName() + " not found.";
				String ferrMsg = "The server could not find the tools required to handle this request.";
				CommandResult res = new CommandResult();
				res.setError(errMsg, ferrMsg, cnfe);
			}			
		}
	}
	
}
