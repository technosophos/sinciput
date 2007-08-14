package com.technosophos.sinciput.servlet;

import javax.servlet.http.HttpSession;

/**
 * This is a basic wrapper around a Servlet HttpSession.
 * It removes all deprecated method signatures, and adds a few utility functions. It
 * also masks methods that should not be used within Sinciput.
 * @author mbutcher
 *
 */
public class SinciputSession {
	
	/**
	 * Default number of seconds that this session will wait until invalidating.
	 * Default is 3600.
	 */
	public static final int DEFAULT_INACTIVE_SECONDS = 3600;
	
	private static final String SESSION_USER_UUID = "user_uuid";
	private static final String SESSION_USER_NAME = "user_name";

	HttpSession ses = null;
	
	private SinciputSession() {}
	
	/**
	 * Create a new SinciputSession.
	 * Wraps an HttpSession.
	 * @param session
	 */
	public SinciputSession(HttpSession session) {
		this.ses = session;
		this.ses.setMaxInactiveInterval(DEFAULT_INACTIVE_SECONDS);
	}
	
	/**
	 * Store user information.
	 * A convenience method for putting user information into the session.
	 * @param uuid DocID for the user's unique record.
	 * @param username User's (displayable) login name.
	 */
	public void setUser(String uuid, String username) {
		this.ses.setAttribute(SESSION_USER_UUID, uuid);
		this.ses.setAttribute(SESSION_USER_NAME, username);
	}
	
	/**
	 * Get the UUID (DocID) for the user.
	 * This can be used to retrieve the user record from the repository.
	 * @return User's UUID.
	 */
	public String getUserUUID() {
		return this.ses.getAttribute(SESSION_USER_UUID).toString();
	}
	
	/**
	 * Get the user name.
	 * @return The name of the user.
	 */
	public String getUserName() {
		return this.ses.getAttribute(SESSION_USER_NAME).toString();
	}
	
	/**
	 * Returns true if there is session information for the user.
	 * Session info for a user means that the user is logged in.
	 * @return True if user is logged in, false otherwise.
	 */
	public boolean userLoggedIn() {
		if( this.ses.getAttribute(SESSION_USER_NAME) == null) return false;
		return true;
	}
	/*
	public RhizomeDocument getUserInfo() {
		
	}
	*/
	
	/**
	 * Get an attribute.
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name) {
		return this.ses.getAttribute(name);
	}
	
	/**
	 * Set an attribute name, value.
	 * @param name
	 * @param val
	 */
	public void setAttribute(String name, Object val) {
		this.ses.setAttribute(name, val);
	}
	
	/**
	 * Remove an attribute.
	 * @param name
	 */
	public void removeAttribute(String name) {
		this.ses.removeAttribute(name);
	}
	
	/**
	 * Get an enumeration of attribute names.
	 * @return
	 */
	public java.util.Enumeration getAttributeNames(){
		return this.ses.getAttributeNames();
	}
	
	/**
	 * Get the creation time in sec-since-epoch.
	 * @return
	 */
	public long getCreationTime() {
		return this.ses.getCreationTime();
	}
	
	/**
	 * Get last accessed time in sec-since-epoch.
	 * @return
	 */
	public long getLastAccessedTime() {
		return this.ses.getLastAccessedTime();
	}
	/**
	 * Get max time this will wait before invalidating session.
	 * @return
	 */
	public int getMaxInactiveInterval() {
		return this.ses.getMaxInactiveInterval();
	}
	/**
	 * Set max time this will wait before invalidating session.
	 * @param i
	 */
	public void setMaxInactiveInterval(int i) {
		this.ses.setMaxInactiveInterval(i);
	}
	/**
	 * Returns true if the session is new or rejected by browser.
	 * @return
	 */
	public boolean isNew() { return this.ses.isNew(); }
	/**
	 * invalidates the session.
	 *
	 */
	public void invalidate() { this.ses.invalidate(); }
	
}
