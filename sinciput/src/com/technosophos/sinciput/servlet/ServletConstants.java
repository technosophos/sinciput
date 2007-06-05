package com.technosophos.sinciput.servlet;

public class ServletConstants {
	/** The default request to be invoked if no request is specified ("default")*/
	public static final String DEFAULT_REQUEST = "default";
	
	/** 
	  * The param in Get/Post/Cookie that contains the request ("r")
	  * This should be used in Get/Post method as the param that specifies the name of
	  * the request. 
	  */
	 public static final String GPC_PARAM_REQUEST = "r";
	 /* The default request to be invoked if no request is specified ("default")*/
	 //public static final String DEFAULT_REQUEST = "default";
	 /** 
	  * The name used to store the request object in the params for the request process 
	  * ("_request"). 
	  */
	 public static final String REQ_PARAM_REQUEST_OBJ = "_request";
	 
	 /**
	  * The parameter with the name of the repository.
	  * "repository"
	  */
	 public static final String REQ_PARAM_REPO = "repository";
	 
	 /**
	  * The param name to retrieve webapp path from the params. (base_path) 
	  * The {@link SinciputServlet} servlet inserts the base path information 
	  * (the location of the servlet's webapp directory).
	  * This is <strong>NOT</strong> the path to the repository. It is the a path to configuration 
	  * information for the servlet.
	  */
	 public static final String BASE_PATH = "base_path";
	 
	 /**
	  * The param name to retrieve the configuration dir from the params. (config_path)
	  * Set by {@link SinciputServlet}. Retrieving the value from the context should
	  * provide you with the path to the configuration files, like command.xml and web.xml.
	  */
	 public static final String CONFIG_PATH = "config_path";
	 
	 /**
	  * The param name to retrieve path information for static resources.
	  * (resource_path) Static resources are files that will be served up unaltered
	  * by the servlet engine (perhaps not even managed by the servlet). These include
	  * images, CSS, and other such files.
	  */
	 public static final String RESOURCE_PATH = "resource_path";
	 
	 //public static final String CXT_REPO_PATH = "";
	 
	 /**
	  * The name of the settings repository.
	  * It is "__sinciput".
	  */
	 public static final String SETTINGS_REPO = "__sinciput";
}
