/*
 * Copyright (c) 2003, Rafael Steil
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * This file creation date: 12/03/2004 - 18:47:26
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.jforum.cache.CacheEngine;
import net.jforum.cache.Cacheable;
import net.jforum.dao.DataAccessDriver;
import net.jforum.entities.UserSession;
import net.jforum.repository.SecurityRepository;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 * @version $Id: SessionFacade.java,v 1.18 2005/03/26 04:10:37 rafaelsteil Exp $
 */
public class SessionFacade implements Cacheable
{
	private static final Logger logger = Logger.getLogger(SessionFacade.class);
	private static final String FQN = "sessions";
	private static CacheEngine cache;

	/**
	 * @see net.jforum.cache.Cacheable#setCacheEngine(net.jforum.cache.CacheEngine)
	 */
	public void setCacheEngine(CacheEngine engine)
	{
		cache = engine;
	}
	
	/**
	 * Add a new <code>UserSession</code> entry to the session
	 * 
	 * @param us The user session objetc to add
	 */
	public static void add(UserSession us)
	{
		add(us, JForum.getRequest().getSession().getId());
	}

	public static void add(UserSession us, String sessionId)
	{
		if (us.getSessionId() == null || us.getSessionId().equals("")) {
			us.setSessionId(sessionId);
		}
		
		sessionId = isUserInSession(us.getUsername());
		if (sessionId != null) {
			remove(sessionId);
		}

		cache.add(FQN, us.getSessionId(), us);
	}
	
	/**
	 * Add a new entry to the user's session
	 * 
	 * @param name The attribute name
	 * @param value The attribute value
	 */
	public static void setAttribute(String name, Object value)
	{
		JForum.getRequest().getSession().setAttribute(name, value);
	}
	
	/**
	 * Removes an attribute from the session
	 * 
	 * @param name The key associated to the the attribute to remove
	 */
	public static void removeAttribute(String name)
	{
		JForum.getRequest().getSession().removeAttribute(name);
	}
	
	/**
	 * Gets an attribute value given its name
	 * 
	 * @param name The attribute name to retrieve the value
	 * @return The value as an Object, or null if no entry was found
	 */
	public static Object getAttribute(String name)
	{
		return JForum.getRequest().getSession().getAttribute(name);
	}

	/**
	 * Remove an entry fro the session map
	 * 
	 * @param sessionId The session id to remove
	 */
	public static void remove(String sessionId)
	{
		logger.info("Removing session " + sessionId);
		cache.remove(FQN, sessionId);
	}
	
	/**
	 * Get all registered sessions
	 * 
	 * @return <code>ArrayList</code> with the sessions. Each entry
	 * is an <code>UserSession</code> object.
	 */
	public static List getAllSessions()
	{
		return new ArrayList(cache.getValues(FQN));
	}
	
	public static void clear()
	{
		cache.remove(FQN);
		cache.add(FQN, new HashMap());
	}
	
	/**
	 * Gets the user's <code>UserSession</code> object
	 * 
	 * @return The <code>UserSession</code> associated to the user's session
	 */
	public static UserSession getUserSession()
	{
		return getUserSession(JForum.getRequest().getSession().getId());
	}
	
	public static UserSession getUserSession(String sessionId)
	{
		return (UserSession)cache.get(FQN, sessionId);
	}

	/**
	 * Gets the number of session elements
	 * 
	 * @return The number of session elements currently registered
	 */
	public static int size()
	{
		return cache.getValues(FQN).size();
	}
	
	/**
	 * Verify if the user in already loaded
	 * 
	 * @param username The username to check
	 * @return The session id if the user is already registered into the session, 
	 * or <code>null</code> if it is not.
	 */
	public static String isUserInSession(String username)
	{
		int aid = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);
		
		for (Iterator iter = cache.getValues(FQN).iterator(); iter.hasNext(); ) {
			UserSession us = (UserSession)iter.next();
			String thisUsername = us.getUsername();
			
			if (thisUsername == null) {
				continue;
			}
			
			if (us.getUserId() != aid && thisUsername.equals(username)) {
				return us.getSessionId();
			}
		}
		
		return null;
	}
	
	/**
	 * Verify if there is an user in the session with the 
	 * user id passed as parameter.
	 * 
	 * @param userId The user id to check for existance in the session
	 * @return The session id if the user is already registered into the session, 
	 * or <code>null</code> if it is not.
	 */
	public static String isUserInSession(int userId)
	{
		int aid = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);
		
		for (Iterator iter = cache.getValues(FQN).iterator(); iter.hasNext(); ) {
			UserSession us = (UserSession)iter.next();
			
			if (us.getUserId() != aid && us.getUserId() == userId) {
				return us.getSessionId();
			}
		}
		
		return null;
	}
	
	/**
	 * Verify is the user is logged in.
	 * 
	 * @return <code>true</code> if the user is logged, or <code>false</code> if is 
	 * an anonymous user.
	 */
	public static boolean isLogged()
	{
		return "1".equals(SessionFacade.getAttribute("logged"));
	}

	/**
	 * Persists user session information.
	 * This method will get a <code>Connection</code> making a call to
	 * <code>DBConnection.getImplementation().getConnection()</code>, and
	 * then releasing the connection after the method is processed.   
	 * 
	 * @param sessionId The session which we're going to persist information
	 * @throws Exception
	 * @see #storeSessionData(String, Connection)
	 */
	public static void storeSessionData(String sessionId) throws Exception
	{
		Connection conn = null;
		try {
			conn = DBConnection.getImplementation().getConnection();
			SessionFacade.storeSessionData(sessionId, conn);
		}
		finally {
			if (conn != null) {
				try {
					DBConnection.getImplementation().releaseConnection(conn);
				}
				catch (Exception e) {
					logger.warn("Error while releasing a connection: " + e);
				}
			}
		}
	}

	/**
	 * Persists user session information.
	 * 
	 * @param sessionId The session which we're going to persist
	 * @param conn A <code>Connection</code> to be used to connect to
	 * the database. 
	 * @throws Exception
	 * @see #storeSessionData(String)
	 */
	public static void storeSessionData(String sessionId, Connection conn) throws Exception
	{
		UserSession us = SessionFacade.getUserSession(sessionId);
		if (us != null) {
			try {
				if (us.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
					DataAccessDriver.getInstance().newUserSessionDAO().update(us, conn);
				}
				
				SecurityRepository.remove(us.getUserId());
			}
			catch (Exception e) {
				logger.warn("Error storing user session data: " + e);
			}
		}
	}
}
