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
 * This file creation date: 31/01/2004 - 19:22:42
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.rss;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: ForumRSS.java,v 1.8 2004/10/21 03:26:04 rafaelsteil Exp $
 */
public class ForumRSS extends GenericRSS 
{
	private LinkedHashMap forums;
	private RSS rss;
	private String forumLink;
	
	public ForumRSS(String title, String description, LinkedHashMap forums)
	{
		this.forums = forums;
		
		this.forumLink = SystemGlobals.getValue(ConfigKeys.FORUM_LINK);
		if (!this.forumLink.endsWith("/")) { 
			this.forumLink += "/";
		}
		
		this.rss = new RSS(title, description, 
				SystemGlobals.getValue(ConfigKeys.ENCODING ),
				this.forumLink);
		
		this.prepareRSS();
	}

	private void prepareRSS()
	{
		for (Iterator iter = this.forums.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry entry = (Map.Entry)iter.next();
			Category category = (Category)entry.getKey();
			
			ArrayList forumsList = (ArrayList)entry.getValue();
			for (Iterator fIter = forumsList.iterator(); fIter.hasNext(); ) {
				Forum forum = (Forum)fIter.next();
				
				RSSItem item = new RSSItem();
				item.addCategory(category.getName());
				item.setTitle(forum.getName());
				item.setDescription(forum.getDescription());
				item.setContentType(RSSAware.CONTENT_HTML);
				item.setLink(this.forumLink
						+ "forums/list/" + forum.getId()
						+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
				
				this.rss.addItem(item);
			}
		}
		
		super.setRSS(this.rss);
	}
}
