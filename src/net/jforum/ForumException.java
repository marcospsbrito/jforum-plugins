/*
 * Copyright (c) 2003, Rafael Steil
 * All rights reserved.

 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:

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
 * This file creation date: Mar 3, 2003 / 11:52:30 AM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.log4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author Rafael Steil
 * @version $Id: ForumException.java,v 1.5 2004/11/07 14:01:42 rafaelsteil Exp $
 */
public class ForumException 
{
	private static Logger logger = Logger.getLogger(ForumException.class);
	
	public ForumException(Throwable t, Writer w)
	{
		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		t.printStackTrace(writer);		
		writer.close();
		
		try {
			logger.error(strWriter);
			
			String message = t.getMessage();
			if (message == null) {
				message = t.getCause().getMessage();
				
				if (message == null) {
					message = t.getCause().toString();
				}
			}

			JForum.getContext().put("stackTrace", strWriter.toString());
			JForum.getContext().put("message", t.getClass().getName() +": "+  message);

			Template template = Configuration.getDefaultConfiguration().getTemplate("exception.html");
			template.process(JForum.getContext(), w);
		}
		catch (Exception e) {
			strWriter = new StringWriter();
			writer = new PrintWriter(strWriter);
			t.printStackTrace(writer);
			writer.close();
			logger.error(strWriter);
		}
	}
	
	public ForumException(Throwable t)
	{
		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		t.printStackTrace(writer);
		writer.close();
		logger.error(strWriter);
	}	
}
