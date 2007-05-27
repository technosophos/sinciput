package com.technosophos.sinciput.servlet;

import java.io.IOException;
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
 * @web.servlet-init-param
 *   name="command_config"
 *   value="commands.xml"
 *   description="Path to command.xml file"
 * 
 * @web.servlet-init-param
 *   name="base_path"
 *   value="$SERVLET/var"
 *   description="Base path for the application. $SERVLET is the servlet base."
 *   
 * @web.servlet-init-param
 *   name="debug"
 *   value="false"
 *   description="If the value is true, debug info will be written to the servlet output."
 */
 public class SinciputServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 
	 static final long serialVersionUID = 1; 
	 /** The servlet's copy of the {@link RhizomeController} object. This 
	  * controller is used for all do* methods.
	  */
	 protected RhizomeController rc = null;
	 /**
	  * The path to the main configuration files for this servlet.
	  */
	 protected String configPath = "";
	 protected boolean debug = false;
	 
	 /** The param in Get/Post/Cookie that contains the request ("r") */
	 public static final String GPC_PARAM_REQUEST = "r";
	 /** The default request to be invoked if no request is specified ("default")*/
	 public static final String DEFAULT_REQUEST = "default";
	 /** The name used to store the request object in the params for the request process 
	  * ("_request"). */
	 public static final String REQ_PARAM_REQUEST_OBJ = "_request";
	 
	 /**
	  * Name of base path param: "base_path".
	  * A Base path can be passed into Sinciput as a servlet init param. 
	  * If relative, it will
	  * be prepended with the servlet path. If absolute, it will be left alone.
	  */
	 public static final String P_BASE_PATH = "base_path";
	 
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
		String request_name = DEFAULT_REQUEST;
		Map orig_params = request.getParameterMap();
		
		//String req_param = request.getParameter(GPC_PARAM_REQUEST);
		
		// Get param from path, or get it from params, otherwise, just use default.
		if(path_info != null && !"/".equals(path_info) ) {
			String[] path_items = path_info.split("/", 2);
			if(path_items.length > 0 && this.rc.hasRequest(path_items[0]))
				request_name = path_items[0];
		} else if(orig_params.containsKey(GPC_PARAM_REQUEST)) {
			String [] tt = (String [])orig_params.get(GPC_PARAM_REQUEST);
			String t = tt[0];
			System.out.println("Request: " + t);
			if(t != null && this.rc.hasRequest(t)) request_name = t;	
		}
		
		// Configure parameters for doRequest:
		Map<String, Object> params = new java.util.HashMap<String, Object>(orig_params);
		params.put(REQ_PARAM_REQUEST_OBJ, request);
		
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
		//response.getWriter().write("<html><head></head><body>"+request.getPathInfo()+"</body></html>");
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
		String debug_str = this.getInitParameter("debug");
		if("false".equalsIgnoreCase(debug_str)) this.debug = true;
		
		String basePath = this.getServletContext().getRealPath("/");
		this.configPath = basePath + "WEB-INF/";
		String command_config = this.getInitParameter("command_config");

		// If the command config is empty, set it to WEB-INF/commands.xml
		// Else if the path is not absolute, prepend servlet container's real path.
		if(command_config == null || command_config.length() == 0) {
			command_config = this.configPath + "commands.xml";
		}else if(!command_config.startsWith("/")) {
			command_config = this.configPath + command_config;
		}
		
		Map<String, String> paths = new java.util.HashMap<String, String>();
		paths.put(XMLRequestConfigurationReader.BASE_PATH, basePath);
		paths.put(XMLRequestConfigurationReader.RESOURCE_PATH, basePath + "resources/");
		paths.put(XMLRequestConfigurationReader.CONFIG_PATH, this.configPath);
		//paths.put("url", this.getServletContext().)
		
		RepositoryContext rcxt = this.buildRepositoryContext();
		XMLRequestConfigurationReader r = new XMLRequestConfigurationReader(paths);
		Map<String, RequestConfiguration> cconf = null;
		try {
			cconf = r.get(new java.io.File(command_config));
		} catch (RhizomeException e) {
			throw new ServletException("Failed to parse command configuration file: " +e.getMessage(), e);
		}
		this.rc = new RhizomeController();
		
		try {
			this.rc.init(cconf, rcxt);
		} catch (RhizomeException re ) {
			String err = "Fatal error initializing Rhizome Controller: ";
			throw new ServletException( err + re.getMessage(), re);
		}
	}
	
	/**
	 * This creates the RepositoryContext.
	 * <p>The {@link RepositoryContext} contains the configuration that the {@link RepositoryManager}
	 * and the commands (loaded instances of {@RhizomeCommand}s) use. To build this information,
	 * this method dumps the initialization parameters into the repository context 
	 * object.</p>
	 * <p>Of note, if no "base_path" init param is found, one is supplied, pointing to 
	 * the WEB-INF directory for this servlet.(See {@link this.configPath})</p>
	 * @return
	 */
	protected RepositoryContext buildRepositoryContext() {
		Enumeration e = this.getInitParameterNames();
		RepositoryContext c = new RepositoryContext();
		String n;
		while(e.hasMoreElements()) {
			n = e.nextElement().toString();
			c.addParam(n, this.getInitParameter(n));
		}
		if(c.hasKey("base_path")) {
			/*
			 * If provided base path is not absolute, then prepend config path.
			 */
			String path = c.getParam("base_path");
			if(!path.startsWith(System.getProperty("file.separator"))) c.addParam("base_path", this.configPath + path);
				
		} else {
			c.addParam("base_path", this.configPath);
		}
		return c;
	}

}