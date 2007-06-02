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
	  * The name of the settings repository.
	  * It is "__sinciput".
	  */
	 public static final String SETTINGS_REPO = "__sinciput";
}
