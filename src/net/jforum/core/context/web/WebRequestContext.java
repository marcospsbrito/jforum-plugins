/*
 * Copyright (c) JForum Team
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
 * This file creation date: Mar 16, 2003 / 1:31:30 AM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.core.context.web;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.jforum.core.UrlPattern;
import net.jforum.core.UrlPatternCollection;
import net.jforum.core.context.JForumContext;
import net.jforum.core.context.RequestContext;
import net.jforum.core.context.SessionContext;
import net.jforum.exceptions.MultipartHandlingException;
import net.jforum.util.legacy.commons.fileupload.FileItem;
import net.jforum.util.legacy.commons.fileupload.FileUploadException;
import net.jforum.util.legacy.commons.fileupload.disk.DiskFileItemFactory;
import net.jforum.util.legacy.commons.fileupload.servlet.ServletFileUpload;
import net.jforum.util.legacy.commons.fileupload.servlet.ServletRequestContext;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: WebRequestContext.java,v 1.1 2006/08/20 22:47:33 rafaelsteil Exp $
 */
public class WebRequestContext extends HttpServletRequestWrapper implements RequestContext
{
	private Map query;
	private JForumContext jforumContext;
	
	/**
	 * Default constructor.
	 * 
	 * @param superRequest Original <code>HttpServletRequest</code> instance
	 * @throws IOException
	 */
	public WebRequestContext(HttpServletRequest superRequest) throws IOException
	{
		super(superRequest);

		this.query = new HashMap();
		boolean isMultipart = false;
		
		String requestType = (superRequest.getMethod()).toUpperCase();
		String contextPath = superRequest.getContextPath();
		String requestUri = this.extractRequestUri(superRequest.getRequestURI(), contextPath);
		String encoding = SystemGlobals.getValue(ConfigKeys.ENCODING);
		String servletExtension = SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);
		
		boolean isPost = "POST".equals(requestType);
		boolean isGet = !isPost;
		
		boolean isQueryStringEmpty = (superRequest.getQueryString() == null 
			|| superRequest.getQueryString().length() == 0);
		
		if (isGet && isQueryStringEmpty && requestUri.endsWith(servletExtension)) {
			superRequest.setCharacterEncoding(encoding); 
			
			requestUri = requestUri.substring(0, requestUri.length() - servletExtension.length());
			String[] urlModel = requestUri.split("/");
			
			int moduleIndex = 1;
			int actionIndex = 2;
			int baseLen = 3;
			
			UrlPattern url = null;
			
			if (urlModel.length >= baseLen) {
				// <moduleName>.<actionName>.<numberOfParameters>
				StringBuffer sb = new StringBuffer(64)
					.append(urlModel[moduleIndex])
					.append('.')
					.append(urlModel[actionIndex])
					.append('.')
					.append(urlModel.length - baseLen);
				
				url = UrlPatternCollection.findPattern(sb.toString());
			}

			if (url != null) {
				// We have parameters? 
				if (url.getSize() >= urlModel.length - baseLen) {
					for (int i = 0; i < url.getSize(); i++) {
						this.addParameter(url.getVars()[i], urlModel[i + baseLen]);
					}
				}
				
				this.addParameter("module", urlModel[moduleIndex]);
				this.addParameter("action", urlModel[actionIndex]);
			}
			else {
				this.addParameter("module", null);
				this.addParameter("action", null);
			}
		}
		else if (isPost) {
			isMultipart = ServletFileUpload.isMultipartContent(new ServletRequestContext(superRequest));
			
			if (isMultipart) {
			    this.handleMultipart(superRequest, encoding);
			}
		}
		
		if (!isMultipart) {
			superRequest.setCharacterEncoding(encoding);
			String containerEncoding = SystemGlobals.getValue(ConfigKeys.DEFAULT_CONTAINER_ENCODING);
			
			if (isPost) { 
				containerEncoding = encoding;
			}
			
			for (Enumeration e = superRequest.getParameterNames(); e.hasMoreElements(); ) {
				String name = (String)e.nextElement();
				this.query.put(name, new String(superRequest.getParameter(name).getBytes(containerEncoding), encoding));
			}
		}
	}

    public SessionContext getWebSession(boolean create) {
        return new WebSessionContext(this.getSession(create));
    }

    public SessionContext getWebSession() {
        return new WebSessionContext(this.getSession());
    }

    /**
	 * @param superRequest HttpServletRequest
	 * @param encoding String
	 * @throws UnsupportedEncodingException
	 */
	private void handleMultipart(HttpServletRequest superRequest, String encoding) throws UnsupportedEncodingException
	{
		String tmpPath = new StringBuffer(256)
		    .append(SystemGlobals.getApplicationPath())
		    .append('/')
		    .append(SystemGlobals.getValue(ConfigKeys.TMP_DIR))
		    .toString();
		
		File tmpDir = new File(tmpPath);
		boolean success = false;

		try {
			if (!tmpDir.exists()) {
				tmpDir.mkdirs();
				success = true;
			}
		}
		catch (Exception e) {
			// We won't log it because the directory
			// creation failed for some reason - a SecurityException
			// or something else. We don't care about it, as the
			// code below tries to use java.io.tmpdir
		}
		
		if (!success) {
			tmpPath = System.getProperty("java.io.tmpdir");
			tmpDir = new File(tmpPath);
		}
		
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(100 * 1024, tmpDir));
		upload.setHeaderEncoding(encoding);

		try {
			List items = upload.parseRequest(superRequest);
			
			for (Iterator iter = items.iterator(); iter.hasNext(); ) {
				FileItem item = (FileItem)iter.next();
			
				if (item.isFormField()) {
					this.query.put(item.getFieldName(), item.getString(encoding));
				}
				else {
					if (item.getSize() > 0) {
						this.query.put(item.getFieldName(), item);
					}
				}
			}
		}
		catch (FileUploadException e) {
			throw new MultipartHandlingException("Error while processing multipart content: " + e);
		}
	}
	
	private String extractRequestUri(String requestUri, String contextPath)
	{
		// First, remove the context path from the requestUri, 
		// so we can work only with the important stuff
		if (contextPath != null && contextPath.length() > 0) {
			requestUri = requestUri.substring(contextPath.length(), requestUri.length());
		}
		
		// Remove the "jsessionid" (or similar) from the URI
		// Probably this is not the right way to go, since we're
		// discarting the value...
		int index = requestUri.indexOf(';');
		
		if (index > -1) {
			int lastIndex = requestUri.indexOf('?', index);
			
			if (lastIndex == -1) {
				lastIndex = requestUri.indexOf('&', index);
			}
			
			if (lastIndex == -1) {
				requestUri = requestUri.substring(0, index);
			}
			else {
				String part1 = requestUri.substring(0, index);
				requestUri = part1 + requestUri.substring(lastIndex);
			}
		}
		
		return requestUri;
	}

	/**
	 * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String parameter) 
	{
		return (String)this.query.get(parameter);
	}

	/**
	 * Gets an parameter that is a number.
	 * A call to <code>Integer#parseInt(String)</code> is made
	 * to do the conversion
	 * @param parameter The parameter name to get the value
	 * @return int
	 */
	public int getIntParameter(String parameter)
	{
		return Integer.parseInt(this.getParameter(parameter));
	}
	
	/**
	 * Gets all parameters of the current request. 
	 * 
	 * @return <code>java.util.Map</code> with all request
	 * data.
	 */
	public Map dumpRequest()
	{
		return this.query;
	}
	
	/**
	 * Restores a request "dump".
	 * 
	 * @param query A <code>java.util.Map</code> with all request
	 * data. Usually it will be the result of a previous call
	 * to @link #dumpRequest() 
	 */
	public void restoreDump(Map query)
	{
		this.query = query;
	}
	
	/**
	 * Gets some request parameter as <code>Object</code>.
	 * This method may be used when you have to get some value
	 * of a <i>multipart/form-data</i> request, like a image
	 * of file. <br>
	 * 
	 * @param parameter String
	 * @return Object
	 */
	public Object getObjectParameter(String parameter)
	{
		return this.query.get(parameter);
	}
	
	/**
	 * Adds a new parameter to the request.
	 * 
	 * If you want to have one more, or to modify an existing one parameter,
	 * you should use this method to the job. 
	 * 
	 * @param name Parameter name
	 * @param value Parameter value
	 */
	public void addParameter(String name, Object value)
	{
		this.query.put(name, value);
	}
	
	/**
	 * Gets the <i>action</i> of the current request.
	 * 
	 * An <i>Action</i> is the parameter name which specifies
	 * what next action should be done by the system. It may be
	 * add or edit a post, editing the groups, whatever. In the URL, the
	 * Action can the represented in two forms:
	 * <p>
	 * <blockquote>
	 * <code>
	 * http://www.host.com/webapp/servletName?module=groups&action=list
	 * </code>
	 * </blockquote>
	 * <p>
	 * or
	 * <p>
	 * <blockquote>
	 * <code>
	 * http://www.host.com/webapp/servletName/groups/list
	 * </code>
	 * </blockquote>
	 * <p>
	 * In both situations, the action's name is "list".
	 * 
	 * @return String representing the action name
	 */
	public String getAction()
	{
		return this.getParameter("action");
	}
	
	/**
	 * Gets the <i>module</i> of the current request.
	 * 
	 * A <i>Module</i> is the parameter name which specifies
	 * what module the user is requesting. It may be the group
	 * administration, the topics or anything else configured module.
	 *In the URL, the Module can the represented in two forms:
	 * <p>
	 * <blockquote>
	 * <code>
	 * http://www.host.com/webapp/servletName?module=groups&action=list
	 * </code>
	 * </blockquote>
	 * <p>
	 * or
	 * <p>
	 * <blockquote>
	 * <code>
	 * http://www.host.com/webapp/servletName/groups/list
	 * </code>
	 * </blockquote>
	 * <p>
	 * In both situations, the module's name is "groups".
	 * 
	 * @return String representing the module name
	 */
	public String getModule()
	{
		return this.getParameter("module");
	}
	
	public Object getObjectRequestParameter(String parameter)
	{
		return this.query.get(parameter);
	}
	
	public JForumContext getJForumContext()
	{
		return this.jforumContext;
	}
	
	public void setJForumContext(JForumContext jforumcontext)
	{
		this.jforumContext = jforumcontext;
	}
}
