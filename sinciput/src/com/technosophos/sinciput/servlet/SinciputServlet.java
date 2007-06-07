package com.technosophos.sinciput.servlet;

import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.RequestConfiguration;
import com.technosophos.rhizome.controller.RequestNotFoundException;
import com.technosophos.rhizome.controller.RhizomeController;
import com.technosophos.rhizome.controller.XMLRequestConfigurationReader;
import com.technosophos.rhizome.repository.RepositoryContext;

// This fails with Xdoclet stuff
//import static com.technosophos.sinciput.servlet.ServletConstants.*;
import com.technosophos.sinciput.servlet.ServletConstants;

/**
 * Servlet implementation class for Servlet: SinciputServlet
 * @web.servlet
 *   name="Sinciput"
 *   display-name="Sinciput" 
 *   description="Servlet for Sinciput Web Interface"
 *   load-on-startup=1
 *
 * @web.servlet-mapping
 *   url-pattern="/Sinciput" 
 *   
 * @web.servlet-mapping
 *   url-pattern="/Sinciput/*"
 *   
 * @web.servlet-init-param
 *   name="command_config"
 *   value="commands.xml"
 *   description="Path to command.xml file"
 *   
 * @web.servlet-init-param
 *     name="fs_repo_path"
 *     value="repository/"
 *     description="Path to the file system repository. A relative path will be located inside of the WEB-INF dir."
 *     
 * @web.servlet-init-param
 *     name="index_path"
 *     value="index/"
 *     description="Path to the index. A relative path will be located inside of the WEB-INF dir."
 *        
 * @web.servlet-init-param
 *   name="debug"
 *   value="false"
 *   description="If the value is true, debug info will be written to the servlet output."
 */
 public class SinciputServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 
	 protected static final long serialVersionUID = 1; 
	 /** The servlet's copy of the {@link RhizomeController} object. This 
	  * controller is used for all do* methods.
	  */
	 protected RhizomeController rc = null;
	 /**
	  * The path to the main configuration files for this servlet.
	  */
	 protected String configPath = "";
	 protected String basePath = "";
	 protected String resourcePath = "";
	 protected boolean debug = false;
	 
	 /**
	  * Name of base path param: "base_path".
	  * A Base path can be passed into Sinciput as a servlet init param. 
	  * If relative, it will
	  * be prepended with the servlet path. If absolute, it will be left alone.
	  */
	 //private static final String P_BASE_PATH = "base_path";
	 
	 /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public SinciputServlet() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path_info = request.getPathInfo();
		String request_name = ServletConstants.DEFAULT_REQUEST;
		Map orig_params = request.getParameterMap();
		
		try {
			Class<?> c = Class.forName("com.technosophos.sinciput.commands.install.VerifyEnvironment");
			this.log("111 SUCCESS loading VerifyEnv.");
		} catch (Exception e) {
			this.log("111 Failed test load of class.", e);
		}
		
		//String req_param = request.getParameter(GPC_PARAM_REQUEST);
		
		//this.log(String.format("Extra path info: %s", path_info));
		
		// Get param from path, or get it from params, otherwise, just use default.
		if(path_info != null && !"/".equals(path_info) ) {
			String[] path_items = path_info.split("/", 4);
			//this.log("Path info 1:" + path_items[1]);
			if(path_items.length > 1 && this.rc.hasRequest(path_items[1]))
				request_name = path_items[1];
		} else if(orig_params.containsKey(ServletConstants.GPC_PARAM_REQUEST)) {
			String [] tt = (String [])orig_params.get(ServletConstants.GPC_PARAM_REQUEST);
			String t = tt[0];
			System.out.println("Request: " + t);
			if(t != null && this.rc.hasRequest(t)) request_name = t;
		}
		
		// Configure parameters for doRequest:
		Map<String, Object> params = new java.util.HashMap<String, Object>(orig_params);
		params.put(ServletConstants.REQ_PARAM_REQUEST_OBJ, request);
		params.put(ServletConstants.BASE_PATH, this.basePath);
		params.put(ServletConstants.CONFIG_PATH, this.configPath);
		params.put(ServletConstants.RESOURCE_PATH, this.resourcePath);
		params.put(ServletConstants.APP_URL, this.getBaseUrl(request));
		// Workaround for broken velocity:
		params.put("app_path", this.basePath);
		params.put(ServletConstants.ABSOLUTE_URI, request.getContextPath() + request.getServletPath());
		/*
		 * FIXME: Need to put in name of repository here?
		 */
		
		java.io.Writer out = response.getWriter();
		LinkedList<CommandResult> results;
		
		this.log("Doing request: " + request_name);
		
		try{
			results = this.rc.doRequest(request_name, params);
		} catch (RequestNotFoundException e) {
			// This should not happen.
			this.log("Failed to get request " + request_name, e);
			response.sendError(500, "Request Failed");
			return;
		}
		response.setContentType(this.rc.getMimeType(request_name));
		
		this.log(String.format("There are %d command results.",results.size()));
		for(CommandResult r: results) {
			if(r.hasError()) {
				out.write(r.getErrorMessage());
				if(this.debug) {
					Exception e = r.getException();
					if(e != null) {
						out.write("<p>" + r.getException().toString() + "</p>");
					    r.getException().printStackTrace(System.out);
					}
				}
			}
			else out.write(r.getResult().toString());
		}
		response.flushBuffer();
		
		
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	/**
	 * Initialize the Rhizome repository.
	 */
	public void init() throws ServletException {
		// TODO: Fix the file separator crap.
		this.log("Initializing Rhizome servlet...");
		super.init();
		
		// Debug mode?
		String debug_str = this.getInitParameter("debug");
		if("false".equalsIgnoreCase(debug_str)) this.debug = true;
		
		// BEGIN: Init vars
		this.basePath = this.getServletContext().getRealPath(File.separator);
		this.configPath = basePath + "WEB-INF" + File.separator;
		this.resourcePath = basePath + "resources" + File.separator;
		String command_config = this.getInitParameter("command_config");
		
		// If the command config is empty, set it to WEB-INF/commands.xml
		// Else if the path is not absolute, prepend servlet container's real path.
		if(command_config == null || command_config.length() == 0) {
			command_config = this.configPath + "commands.xml";
		}else if(!(new File(command_config).isAbsolute())) {
			command_config = this.configPath + command_config;
		}
		// END: Init vars
		
		// BEGIN: Get the request configuration from an XML file:
		// ## This stuff goes in the configuration reader to load the PI engine.
		Map<String, String> paths = new java.util.HashMap<String, String>();
		paths.put(XMLRequestConfigurationReader.BASE_PATH, this.basePath);
		paths.put(XMLRequestConfigurationReader.RESOURCE_PATH, this.basePath + "resources" + File.separator);
		paths.put(XMLRequestConfigurationReader.CONFIG_PATH, this.configPath);
		//paths.put("url", this.getServletContext().)
		XMLRequestConfigurationReader r = new XMLRequestConfigurationReader(paths);
		
		Map<String, RequestConfiguration> cconf = null;
		try {
			cconf = r.get(new java.io.File(command_config));
		} catch (RhizomeException e) {
			throw new ServletException("Failed to parse command configuration file: " +e.getMessage(), e);
		}
		
		// Preload the classes.
		Map<String, Class<?>> cmdMap = null;
		try {
			cmdMap = CommandClassPreloader.preloadClasses(r.getCommandMap());
		} catch (ClassNotFoundException e) {
			this.log("Classes are missing.", e);
			throw new ServletException("Classes are missing.", e);
		}
		// END: Get the request configuration
		
		// BEGIN: Create the controller.
		RepositoryContext rcxt = this.buildRepositoryContext();
		this.rc = new RhizomeController();
		this.rc.setPreloadedCommandMap(cmdMap);
		try {
			this.rc.init(cconf, rcxt);
		} catch (RhizomeException re ) {
			String err = "Fatal error initializing Rhizome Controller: ";
			throw new ServletException( err + re.getMessage(), re);
		}
		// ENd: Create the controller
	}
	
	/**
	 * This creates the RepositoryContext.
	 * <p>The {@link RepositoryContext} contains the configuration that the {@link RepositoryManager}
	 * and the commands (loaded instances of {@RhizomeCommand}s) use. To build this information,
	 * this method dumps the initialization parameters into the repository context 
	 * object.</p>
	 * <p>The following are added to the context by this method:</p>
	 * <ul>
	 * <li>{@link #CONFIG_PATH}: Path to the WEB-INF directory</li>
	 * <li>{@link #BASE_PATH}: Path to the wepapp base directory</li>
	 * </ul>
	 * @return Populated repository context
	 */
	protected RepositoryContext buildRepositoryContext() {
		Enumeration e = this.getInitParameterNames();
		RepositoryContext c = new RepositoryContext();
		String n;
		while(e.hasMoreElements()) {
			n = e.nextElement().toString();
			c.addParam(n, this.getInitParameter(n));
		}
		c.addParam(ServletConstants.CONFIG_PATH, this.configPath);
		c.addParam(ServletConstants.BASE_PATH, this.basePath);
		//c.addParam(ServletConstants.RESOURCE_PATH, this.resourcePath);
		/*
		 * We always want base path to be the servlet path. Repo path is set 
		 * elsewhere.
		 * Removed from init param:
		* @web.servlet-init-param
		*   name="base_path"
		*   value="$SERVLET/var"
		*   description="Base path for the application. $SERVLET is the servlet base."
		if(c.hasKey(BASE_PATH)) {
			// If provided base path is not absolute, then prepend config path.
			String path = c.getParam(BASE_PATH);
			File f = new File(path);
			if(!f.isAbsolute()) {
				File ff = new File(this.configPath, path);
				c.addParam(BASE_PATH, ff.getAbsolutePath());
			}
				
		} else {
			c.addParam(BASE_PATH, this.configPath);
		}
		*/
		return c;
	}
	
	protected String getBaseUrl(HttpServletRequest r) {
		String scheme = r.getScheme();
		int p = r.getServerPort();
		
		StringBuilder sb = new StringBuilder();
		sb.append(scheme);
		sb.append("://");
		sb.append(r.getServerName());
		// Port conditional:
		if( !("http".equalsIgnoreCase(scheme) && p == 80) 
				&& !("https".equalsIgnoreCase(scheme) && p == 443)) {
			sb.append(':');
			sb.append(p);
		}
		sb.append(r.getContextPath());
		sb.append(r.getServletPath());
		//this.log("BASE URL: " + sb.toString());
		return sb.toString();
	}

}