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
	  * The SinciputSession object.
	  * @see com.technosophos.sinciput.servlet.SinciputSession.
	  */
	 public static final String REQ_PARAM_SESSION = "_session";
	 
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
	 
	 /**
	  * The base URL for this servlet.
	  * This is the path to the servlet currently being used.
	  */
	 public static final String APP_URL = "app_url";
	 
	 /**
	  * The absolute URI.
	  * <p>The full path from the server root to the servlet. E.g. if the URL is
	  * http://example.com/servlet/servlet, the absolute path is /servlet/servlet.</p>
	  */
	 public static final String ABSOLUTE_URI = "absolute_uri";
	 
	 /**
	  * The resource URI.
	  * <p>The full path from the server root the location of supporting files, such as
	  * js, images, and so on. 
	  * E.g. if the URL is
	  * http://example.com/servlet/js/, the absolute path is /servlet/js.</p>
	  */
	 public static final String RESOURCE_URI = "resource_uri";
	 
	 /**
	  * Servlet init parameter to get for FS repository path: fs_repo_path.
	  * @see com.technosophos.rhizome.repository.fs.FileSystemRepository
	  */
	 public static final String SERVPARAM_FS_REPO_PATH = "fs_repo_path";
	 /**
	  * Serlvet init parameter to get for FS index path: index_path
	  * @see com.technosophos.rhizome.repository.lucene.LuceneElements
	  */
	 public static final String SERVPARAM_INDEX_PATH = "index_path";
	 
	 
	 //public static final String CXT_REPO_PATH = "";
	 
	 /**
	  * The name of the settings repository.
	  * It is "__sinciput".
	  */
	 public static final String SETTINGS_REPO = "__sinciput";
}
