package com.technosophos.rhizome.controller;

import com.technosophos.rhizome.RhizomeException;
import org.betterxml.xelement.*;
import java.io.File;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;

/**
 * Read XML files containing request-to-command information.
 * <p>
 * The {@link RhizomeController} takes a string request and performs a sequence of commands,
 * based on the information passed in to as a HashMap of request-to-command mappings. This
 * class reads an XML file containing that information and converts it into an appropriately
 * formatted HashMap.</p>
 * @author mbutcher
 * @see RhizomeController
 * @see CommandConfiguration
 *
 */
public class XMLRequestConfigurationReader {

	public static String REQ_ROOT_ELE = "commands";
	public static String REQ_LOADCLASS_ELE = "loadclass";
	public static String REQ_REQUESTS_ELE = "requests";
	public static String REQ_REQUEST_ELE = "request";
	public static String REQ_GLOBAL_ELE = "global";
	public static String REQ_CMD_ELE = "cmd";
	public static String REQ_PARAM_ELE = "param";
	public static String REQ_VALUE_ELE = "value";
	public static String REQ_GROUP_ELE = "group";
	
	public static String REQ_NAME_ATTR = "name";
	public static String REQ_CLASS_ATTR = "class";
	public static String REQ_DO_ATTR = "do";
	public static String REQ_FATAL_ATTR = "fatal";
	public static String REQ_PREFIX_ATTR = "prefix";
	public static final String REQ_MIME_ATTR = "mimetype";
	//public static String REQ_DEFAULT_ATTR = "default";
	public static final String REQ_GROUP_ATTR = "group";
	
	/**
	 * Processor Instructions for this Target will be translated to path information.
	 */
	public static final String REQ_PATH_PI_TARGET = "path";
	
	
	public static final String BASE_PATH = "base_path";
	public static final String CONFIG_PATH = "config_path";
	public static final String RESOURCE_PATH = "resource_path";
	
	
	private XParser p = null;
	private XDocument doc = null;
	private HashMap<String, String> classes = new HashMap<String, String>();
	private HashMap<String, String[]> globals = new HashMap<String, String[]>();
	private Map<String, String> pathInfo = null;
	
	// This is used to store group info.
	private HashMap<String, RequestConfiguration> groups = null;
	
	public XMLRequestConfigurationReader(Map<String, String> pathInfo) {
		this.pathInfo = pathInfo;
	}
	
	/**
	 * Read an XML file and return a Map of requests and their respective command queues.
	 * @param xmlData
	 * @return
	 * @throws RhizomeException
	 */
	public Map get(String xmlData) throws RhizomeException {
		try {
			this.p = new XParser();
			this.doc = p.parse(xmlData);
		} catch (Exception e) {
			throw new RhizomeException("Error parsing configuration XML.", e);
		}
		return this.doMapping(doc.getRootElement());
	}
	
	/**
	 * Read an XML file and return a Map of requests and their respective command queues.
	 * @param file
	 * @return
	 * @throws RhizomeException
	 */
	public Map<String, RequestConfiguration> get(File file) throws RhizomeException {
		try {
			this.p = new XParser();
			this.doc = p.parse(file);
		} catch (Exception e) {
			throw new RhizomeException("Error parsing configuration XML: " + e.toString(), e);
		}
		return this.doMapping(doc.getRootElement());
	}
	
	/**
	 * Read an XML file and return a Map of requests and their respective command queues.
	 * @param in
	 * @return
	 * @throws RhizomeException
	 */
	public Map<String, RequestConfiguration> get(InputStream in) throws RhizomeException {
		try {
			this.p = new XParser();
			this.doc = p.parse(in);
		} catch (Exception e) {
			throw new RhizomeException("Error parsing configuration XML.", e);
		}
		return this.doMapping(doc.getRootElement());	
	}
	
	/**
	 * Returns a map of command names and their associated classnames.
	 * <p>Returns empty if some version of {@link #get(String)} has not been called first.</p>
	 * @return A map of command names and classnames. 
	 * @see #get(File)
	 * @see #get(InputStream)
	 * @see #get(String)
	 */
	public Map<String, String> getCommandMap() {
		return this.classes;
	}
	
	/**
	 * Traverse the XML and configure the new Map.
	 * @param root
	 * @return
	 */
	protected Map<String, RequestConfiguration> doMapping(XElement root) {
		HashMap<String, RequestConfiguration> map = 
			new HashMap<String, RequestConfiguration>();
		
		// FIRST: Load name->classname hash.
		LinkedList<XElement> lc = root.getChildrenElements(REQ_LOADCLASS_ELE);
		XAttributes xattrs;
		for(XElement ele: lc) {
			xattrs = ele.getAttributes();
			if(xattrs.containsAttribute(REQ_NAME_ATTR) 
					&& xattrs.containsAttribute(REQ_CLASS_ATTR)) {
				this.classes.put( xattrs.getAttributeValue(REQ_NAME_ATTR), 
						   xattrs.getAttributeValue(REQ_CLASS_ATTR));
			}
		}
		
		// SECOND: Get Global Params
		XElement reqs_ele = root.getChildrenElements(REQ_REQUESTS_ELE).getFirst();
		XElement globals_ele = reqs_ele.getChildrenElements(REQ_GLOBAL_ELE).getFirst();
		this.piReplace(globals_ele); // Replace processing instructions.
		LinkedList<XElement> param_l = globals_ele.getChildrenElements(REQ_PARAM_ELE);
		String txt[];
		LinkedList<XElement> value_l;
		for(XElement param_ele: param_l) {
			value_l = param_ele.getChildrenElements(REQ_VALUE_ELE);
			if(value_l == null || value_l.size() == 0) {
				txt = new String [1];
				txt[0] = this.catPCData(param_ele.getPCData());
			} else {
				txt = new String [value_l.size()];
				XElement val_ele;
				for(int iii = 0; iii < value_l.size(); ++iii) {
					val_ele = value_l.get(iii);
					txt[iii] = this.catPCData(val_ele.getPCData());
				}
			}
			xattrs = param_ele.getAttributes();
			if(xattrs.containsAttribute(REQ_NAME_ATTR))
				this.globals.put(xattrs.getAttributeValue(REQ_NAME_ATTR), txt);
		}
		
		// THIRD: Get Command Groups
		LinkedList<XElement> group_l = reqs_ele.getChildrenElements(REQ_GROUP_ELE);
		this.groups = new HashMap();
		if(group_l.size() > 0) {
			RequestConfiguration reqC;
			for(XElement req_ele: group_l) {
				xattrs = req_ele.getAttributes();
				if(xattrs.containsAttribute(REQ_NAME_ATTR)) {
					reqC = new RequestConfiguration(
							xattrs.getAttributeValue(REQ_NAME_ATTR), //Name
							this.getCommands(req_ele) // Queue
					);
					groups.put(xattrs.getAttributeValue(REQ_NAME_ATTR), reqC);
				}
			}
		}
		
		// FOURTH: Get Request Queues 
		LinkedList<XElement> req_l = reqs_ele.getChildrenElements(REQ_REQUEST_ELE);
		String mimeType = null;
		RequestConfiguration reqC;
		for(XElement req_ele: req_l) {
			xattrs = req_ele.getAttributes();
			if(xattrs.containsAttribute(REQ_NAME_ATTR)) {
				if(xattrs.containsAttribute(REQ_MIME_ATTR))
					mimeType = xattrs.getAttributeValue(REQ_MIME_ATTR);
				reqC = new RequestConfiguration(
						xattrs.getAttributeValue(REQ_NAME_ATTR), //Name
						this.getCommands(req_ele), // Queue
						mimeType); // MIME type
				//System.out.println("DEBUG: Request Name loaded: " + reqC.getName());
				// Ugly, but faster than iterating through a list of reqC objs
				map.put(reqC.getName(), reqC);
			}
			//else System.err.println("Skipping element: "  + req_ele.getName());
		}
		
		return map;
	}
	
	/**
	 * Given an XML elemnt, get all of the commands in this element.
	 * This handles two kinds of commands. Commands with the do="command_name" attribute
	 * are added directly to the queue. Commands with the attribute group="group_name" 
	 * cause getCommands to examine this.groups to see if there is a group with that name.
	 * If there is, then the commands in that group are injected into the current request.
	 * 
	 * Using this group mechanism, it is possible to introduce a layer of abstraction onto
	 * the request object construction.
	 * @param req_ele
	 * @return
	 */
	private Queue<CommandConfiguration> getCommands(XElement req_ele) {
		this.piReplace(req_ele); // Replace processing instructions.
		LinkedList<XElement> cmd_l = req_ele.getChildrenElements(REQ_CMD_ELE);
		LinkedList<CommandConfiguration> cconf_l = new LinkedList<CommandConfiguration>();
		HashMap<String, String[]> ppp;
		CommandConfiguration cconf;
		String cmd_name;
		String [] txt;
		XAttributes xattrs, xattrs2;
		LinkedList<XElement> param_l;
		LinkedList<XElement> value_l;
		for(XElement cmd_ele: cmd_l) {
			ppp = new HashMap<String, String[]>(this.globals);
			xattrs = cmd_ele.getAttributes();
			if(xattrs.containsAttribute(REQ_DO_ATTR)) {
				cmd_name = xattrs.getAttributeValue(REQ_DO_ATTR);
				if(this.classes.containsKey(cmd_name)) {
					cconf = new CommandConfiguration(cmd_name, this.classes.get(cmd_name));
					param_l = cmd_ele.getChildrenElements(REQ_PARAM_ELE);
					for(XElement param_ele: param_l) {
						value_l = param_ele.getChildrenElements(REQ_VALUE_ELE);
						if(value_l == null || value_l.size() == 0) {
							txt = new String [1];
							txt[0] = this.catPCData(param_ele.getPCData());
						} else {
							txt = new String [value_l.size()];
							XElement val_ele;
							for(int iii = 0; iii < value_l.size(); ++iii) {
								val_ele = value_l.get(iii);
								txt[iii] = this.catPCData(val_ele.getPCData());
							}
						}
						xattrs2 = param_ele.getAttributes();
						//txt = this.catPCData(param_ele.getPCData());
						if(xattrs2.containsAttribute(REQ_NAME_ATTR)) {
							ppp.put(xattrs2.getAttributeValue(REQ_NAME_ATTR), txt);
						}
					}
					cconf.setDirective(ppp);
					if(xattrs.containsAttribute(REQ_FATAL_ATTR) 
							&& "true".equalsIgnoreCase(xattrs.getAttributeValue(REQ_FATAL_ATTR)))
						cconf.setFailOnError(true);
					else if (xattrs.containsAttribute(REQ_FATAL_ATTR)) System.err.println("Has fatal attr");
					if(xattrs.containsAttribute(REQ_PREFIX_ATTR)) 
						cconf.setPrefix(xattrs.getAttributeValue(REQ_PREFIX_ATTR));
					
					
					cconf_l.add(cconf);
				}
			} else if (xattrs.containsAttribute(REQ_GROUP_ATTR)) {
				// Here we handle groups.
				// A command can reference a group, which is a collection of commands.
				// In such a case, all of the commands in that group will be injected
				// into the current request.
				String groupName = xattrs.getAttributeValue(REQ_GROUP_ATTR);
				if(this.groups.containsKey(groupName)) {
					Queue<CommandConfiguration> groupCommands = 
						this.groups.get(groupName).getQueue();
					for(CommandConfiguration cc: groupCommands) {
					  System.err.println("Adding command from group.");
					  cconf_l.add(cc);	
					}
				}
				
			}
		}
		return cconf_l;
	}
	
	/*
	private XElement getGroup(String name, XElement parent) {
		LinkedList<XElement> groups = parent.getChildrenElements(REQ_GROUP_ELE);
		for(XElement group: groups) {
			XAttributes attr = group.getAttributes();
			if(attr.containsAttribute(REQ_GROUP_ATTR) 
					&& name.equalsIgnoreCase(attr.getAttributeValue(REQ_GROUP_ATTR))) {
				return group;
			}
		}
		return null;
	}
	*/

	private String catPCData(LinkedList<XPCData> pcd) {
		if(pcd == null || pcd.size() == 0 ) return "";
		if(pcd.size() == 1 ) return pcd.getFirst().getText(); 
		StringBuffer sb = new StringBuffer();
		for(XPCData d: pcd) {
			sb.append(d.getText());
		}
		return sb.toString();
	}
	
	//protected void replaceSpecials
	
	/**
	 * Replace processor instructions.
	 * This attempts to replace a PI with the target {@link REQ_PATH_PI_TARGET}. 
	 * This may return null.
	 * @see #XMLRequestConfigurationReader(Map)
	 */
	protected String translatePI(XProcessingInstruction p) {
		if(this.pathInfo.size() == 0) return null; // We know that we will never match.
		String k = p.getData();
		if( k == null) return null;
		k = k.trim();
		String d = null;
		if(REQ_PATH_PI_TARGET.equals(p.getTarget())
				&& this.pathInfo.containsKey(k)) {
			d = this.pathInfo.get(k);
			if("".equals(d)) return null;
		}
		return d;
	}
	
	/**
	 * Scan everything below the given element and replace PIs.
	 * @param top
	 */
	protected void piReplace(XElement top) {
		LinkedList<XNode> nodes = top.getChildren();
		XNode t;
		String rep = null;
		for(int i = 0; i < nodes.size(); ++i) {
			t = nodes.get(i);
			if(t instanceof XProcessingInstruction) {
				rep = this.translatePI((XProcessingInstruction)t);
				if(rep == null) nodes.remove(i);
				else nodes.set(i, new XPCData(rep));
			} else if (t instanceof XElement) {
				this.piReplace((XElement)t);
			}
		}
	}
}
