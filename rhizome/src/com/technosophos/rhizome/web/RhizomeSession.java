package com.technosophos.rhizome.web;

/**
 * Session for a Rhizome web application.
 * The members of this object are those that Rhizome web applications may make use of.
 * It is heavily influenced by {@link javax.servlet.http.HttpSession}, sans deprecated 
 * methods. In most cases, a RhizomeSession is a simple wrapper around an HttpSession from
 * a servlet.
 * @author mbutcher
 *
 */
public interface RhizomeSession {

	/**
	 * Get the UUID (DocID) for the user.
	 * This can be used to retrieve the user record from the repository.
	 * @return User's UUID.
	 */
	public abstract String getUserUUID();

	/**
	 * Get the user name.
	 * @return The name of the user.
	 */
	public abstract String getUserName();

	/**
	 * Get the name of the current repository.
	 * If no repository is currently selected, this will return null. With this name, you should
	 * be able to directly access the repository. NOTE: for a human readable name (a.k.a. a
	 * repository <i>title</i>, you should use {@link getActiveRepositoryUUID} to get the
	 * document ID, and then access the document to get title information.
	 * @return Name of the repository.
	 */
	public abstract String getActiveRepositoryName();

	/**
	 * Get the UUID (document ID) for the currently active repository.
	 * The UUID can be used to get the repo description from the repository.
	 * @return UUID for the repository.
	 */
	public abstract String getActiveRepositoryUUID();

	/**
	 * Get an attribute.
	 * @param name
	 * @return
	 */
	public abstract Object getAttribute(String name);

	/**
	 * Get an enumeration of attribute names.
	 * @return
	 */
	public abstract java.util.Enumeration getAttributeNames();

	/**
	 * Get the creation time in sec-since-epoch.
	 * @return
	 */
	public abstract long getCreationTime();

	/**
	 * Get last accessed time in sec-since-epoch.
	 * @return
	 */
	public abstract long getLastAccessedTime();

	/**
	 * Get max time this will wait before invalidating session.
	 * @return
	 */
	public abstract int getMaxInactiveInterval();

	/**
	 * Returns true if the session is new or rejected by browser.
	 * @return
	 */
	public abstract boolean isNew();

	/**
	 * invalidates the session.
	 *
	 */
	public abstract void invalidate();
	/**
	 * Returns true if there is session information for the user.
	 * Session info for a user means that the user is logged in.
	 * @return True if user is logged in, false otherwise.
	 */
	public boolean userLoggedIn();
	
	/**
	 * Set max time this will wait before invalidating session.
	 * @param i
	 */
	public void setMaxInactiveInterval(int i);
	
	/**
	 * Set the name of the currently selected repository.
	 * This repo will be treated as the default repository. A <b>repoName</b> is the 
	 * name of the repository (not necessarily human readable). The UUID is the 
	 * document ID for the RepositoryDescription.
	 * @see com.technosophos.lantern.types.admin.RepositoryDescriptionEnum
	 * @param repoName The name of the repository
	 * @param repoName The name of the repository
	 */
	public void setActiveRepository( String repoName, String repoUUID );
	
	/**
	 * Set an attribute name, value.
	 * @param name
	 * @param val
	 */
	public void setAttribute(String name, Object val);
	
	/**
	 * Store user information.
	 * A convenience method for putting user information into the session.
	 * @param uuid DocID for the user's unique record.
	 * @param username User's (displayable) login name.
	 */
	public void setUser(String uuid, String username);
	
	/**
	 * Remove an attribute.
	 * @param name
	 */
	public void removeAttribute(String name);
	
	
}