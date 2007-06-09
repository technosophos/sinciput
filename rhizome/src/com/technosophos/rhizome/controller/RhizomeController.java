package com.technosophos.rhizome.controller;

import com.technosophos.rhizome.repository.RepositoryManager;
import com.technosophos.rhizome.repository.RepositoryContext;
import com.technosophos.rhizome.RhizomeException;
import java.util.LinkedList;
//import java.util.Queue;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

/**
 * Controller for handling Rhizome requests.
 * <p>The main job of this class is to act as a controller (front controller pattern) for
 * the Rhizome backend. It works by first initializing a rhizome RepositoryManager, which 
 * will handle access to data storage and searching facilities, and then handling requests
 * from "clients" (any caller from outside).
 * The controller takes requests, unpacks them into queues of commands, 
 * and then processes the commands in order.
 * </p>
 * <p>
 * The request-to-command queue mapping is performed during 
 * initialization. Then, at each doRequest() method, the given information is unpacked
 * into a command queue, and each command is executed in sequence.
 * </p>
 * <hr/>
 * <p>About the request-to-command HashMap:</p>
 * <p>The RhizomeController maps requests (identified by String names) with a queue of 
 * commands. Each request should result (assuming no fatal error) in the processing of 
 * each command in sequence. So each entry in the request-to-command HashMap should have 
 * the string name of the request, followed by a Queue of CommandConfiguration objects. One
 * way of retrieving such mappings is with the XMLRequstConfigurationReader, in
 * which case an XML file containing mappings is read, and converted into an appropriately
 * formatted HashMap.</p>
 * @see XMLRequestConfigurationReader
 * @see CommandConfiguration
 * @author mbutcher
 * @since 0.1
 *
 */
public class RhizomeController {

	private Map<String, RequestConfiguration> cqMap = null;
	private Map<String, Class<?>> preloaded = null;
	private RepositoryManager repoman = null;
	protected RepositoryContext repocxt = null;
	
	/**
	 * This constructor should only be used when necessary.
	 * It must be followed by a call to init(), otherwise the repository may not get 
	 * correctly initialized, and no commands will exist.
	 */
	public RhizomeController() {
		this.cqMap = new HashMap<String, RequestConfiguration>();
		this.repocxt = new RepositoryContext();
		this.repoman = new RepositoryManager();
		//this.repoman.init(this.repocxt);
	}
	
	/**
	 * This is the preferred constructor.
	 * If you use this constructor, you do not need to initialize with {@code init()}.
	 * @param cqMap a command queue map of request names to a queue of command names.
	 * @param cxt A RepositoryContext with settings for operating the respository.
	 * @see RepositoryContext()
	 */
	public RhizomeController(Map<String, RequestConfiguration> cqMap, RepositoryContext cxt) 
			throws RhizomeException {
		this.cqMap = cqMap;
		this.repocxt = cxt;
		this.repoman = new RepositoryManager();
		this.repoman.init(cxt);
	}
	
	/**
	 * Initialize this controller.
	 * You <b>MUST</b> use init() if you used the empty constructor.
	 * @see isInitialized()
	 * @see RepositoryContext()
	 * @param cqMap a command queue map of request names to a queue of command names.
	 * @param cxt A RepositoryContext with settings for operating the repository.
	 */
	public void init(Map<String, RequestConfiguration> cqMap, RepositoryContext cxt) 
			throws RhizomeException {
		this.cqMap = cqMap;
		this.repocxt = cxt;
		this.repoman = new RepositoryManager();
		this.repoman.init(cxt);
	}
	
	/**
	 * Returns true if this object has been initialized.
	 * An uninitialized RhizomeController is one that has no requests in the command queue
	 * map.
	 * 
	 * @return true if it has at least one register request-to-command mapping.
	 */
	public boolean isInitialized() {
		if(this.cqMap == null || this.cqMap.isEmpty()) return false;
		return true;
	}
	
	/**
	 * Check whether this controller can process the given request.
	 * If this controller can process the given request, this will return true. Otherwise,
	 * it will return false.
	 * @param requestName
	 * @return true if it can process the given request
	 */
	public boolean hasRequest(String requestName) {
		if(this.cqMap.containsKey(requestName)) return true;
		return false;
	}
	
	/**
	 * Set the map of preloaded classes.
	 * <p>Before calling a classloader to load command classes, RhizomeController will
	 * check to see if any command classes have been preloaded. If they have, then
	 * the preloaded version will be used instead. This is more efficient than using
	 * a classloader each request.</p>
	 * @param cmds
	 */
	public void setPreloadedCommandMap(Map<String, Class<?>> cmds) {
		if(cmds == null || cmds.size() == 0) return;
		this.preloaded = cmds;
	}
	
	/**
	 * Get the MIME type for the request name.
	 * <p>If no request by this name exists, the method will return <Code>null</code>. Use
	 * the {@link #hasRequest(String)} method to verify that such a request exists.</p>
	 * <p>Information of the default MIME type returned can be found in 
	 * {@link RequestConfiguration.DEFAULT_MIME_TYPE}.
	 * @param requestName the name of the request
	 * @return The intended MIME type for the output of this request.
	 */
	public String getMimeType(String requestName) {
		if(!this.cqMap.containsKey(requestName)) return null;
		return this.cqMap.get(requestName).getMimeType();
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
	public LinkedList<CommandResult> doRequest(String requestName, Map<String, Object> data) 
			throws RequestNotFoundException {
		
		LinkedList<CommandResult> results = new LinkedList<CommandResult>();
		
		if(!this.cqMap.containsKey(requestName))
			throw new RequestNotFoundException(String.format("No match for request \"%s\".", requestName));
		RequestConfiguration rconf = this.cqMap.get(requestName);
		Iterator<CommandConfiguration> commands = rconf.getQueue().iterator();
		
		CommandConfiguration cconf = null;
		try {
			while (commands.hasNext()) {
				cconf = commands.next();
				System.out.format("Doing command %s.\n", cconf.getName());
				this.doCommand(cconf, data, results);
				System.out.format("Command: There are %d results.\n", results.size());
			}
		} catch (ReRouteRequest rrr) {
			// Reroute a request to a new request and begin processing again.
			results.clear();
			String rrreqname = rrr.getRequestName();
			if(!this.cqMap.containsKey(rrreqname)) {
				//results.clear();
				CommandResult res;
				String errMsg = "Error forwarding " + requestName + " to "+rrreqname+".";
				String ferrMsg = "The server cannot find a necessary component, and so cannot complete this task.";
				if(cconf == null) cconf = new CommandConfiguration("FatalCommandException","");
				res = new CommandResult(cconf);
				res.setError(errMsg, ferrMsg);
				results.add(res);
			}
			data.put("rerouterequest", rrr);
			//rconf = this.cqMap.get(rrreqname);
			//commands = rconf.getQueue().iterator();
			return this.doRequest(rrreqname, data);
			
		} catch (FatalCommandException fce) {
			results.clear();
			CommandResult res;
			String errMsg = String.format("Fatal Error in %s: %s.", requestName, fce.getMessage());
			String ferrMsg = "The server experienced a severe error, and cannot complete this task.";
			if(cconf == null) cconf = new CommandConfiguration("FatalCommandException","");
			res = new CommandResult(cconf);
			res.setError(errMsg, ferrMsg, fce);
			results.add(res);
		}		
		return results;
	}
	
	/**
	 * Run a command.
	 * <p>This method is used internally. As a request is processed, each command is called 
	 * in turn.</p>
	 * <p>Any errors are caught and wrapped in a CommandResult.</p>
	 * @param cconf
	 * @param data
	 * @return
	 */
	protected void doCommand(CommandConfiguration cconf, Map<String, Object> data, List<CommandResult> results) 
			throws FatalCommandException, ReRouteRequest {
		try {
			RhizomeCommand command;
			if(this.preloaded != null && this.preloaded.containsKey(cconf.getName())) {
				Class<?> c = this.preloaded.get(cconf.getName());
				command = (RhizomeCommand)c.newInstance();
				command.init(cconf, this.repoman);
			} else
				command = RhizomeCommandFactory.getCommand(cconf, this.repoman);
			command.doCommand(data, results);
		} catch (IllegalAccessException e) {
			if (cconf.failOnError()) {
				String err = "Fatal error in " +cconf.getName() + ".";
				throw new FatalCommandException(err, e);
			} else {
				String errMsg = "Cannot access command " + cconf.getName() + ".";
				String ferrMsg = "The server could not find the tools required to handle this request.";
				CommandResult res = new CommandResult(cconf.getName());
				res.setError(errMsg, ferrMsg, e);
				results.add(res);
			}		
		} catch (InstantiationException e) {
			if (cconf.failOnError()) {
				String err = "Fatal error in " +cconf.getName() + ".";
				throw new FatalCommandException(err, e);
			} else {
				String errMsg = "Command " + cconf.getName() + " initiation failure.";
				String ferrMsg = "The server could not find the tools required to handle this request.";
				CommandResult res = new CommandResult(cconf.getName());
				res.setError(errMsg, ferrMsg, e);
				results.add(res);
			}		
		} catch (RhizomeException re) {
			if (cconf.failOnError()) {
				String err = "Fatal error in " +cconf.getName() + ".";
				throw new FatalCommandException(err, re);
			} else {
				String errMsg = "Command " + cconf.getName() + " not found.";
				String ferrMsg = "The server could not find the tools required to handle this request.";
				CommandResult res = new CommandResult(cconf.getName());
				res.setError(errMsg, ferrMsg, re);
				results.add(res);
			}			
		}
	}
	
}
