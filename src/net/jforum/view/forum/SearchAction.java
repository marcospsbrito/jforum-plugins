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
 * This file creation date: 14/01/2004 / 22:02:56
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.forum;

import java.util.Date;

import net.jforum.Command;
import net.jforum.JForumExecutionContext;
import net.jforum.context.RequestContext;
import net.jforum.context.ResponseContext;
import net.jforum.exceptions.ForumException;
import net.jforum.repository.ForumRepository;
import net.jforum.search.ContentSearchOperation;
import net.jforum.search.NewMessagesSearchOperation;
import net.jforum.search.SearchArgs;
import net.jforum.search.SearchOperation;
import net.jforum.search.SearchResult;
import net.jforum.util.I18n;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.TemplateKeys;
import net.jforum.view.forum.common.TopicsCommon;
import net.jforum.view.forum.common.ViewCommon;
import freemarker.template.SimpleHash;

/**
 * @author Rafael Steil
 * @version $Id: SearchAction.java,v 1.54 2007/07/30 14:06:44 rafaelsteil Exp $
 */
public class SearchAction extends Command 
{
	public SearchAction() { }
	
	public SearchAction(RequestContext request, ResponseContext response, SimpleHash context) 
	{
		this.request = request;
		this.response = response;
		this.context = context;
	}
	
	public void filters()
	{
		this.setTemplateName(TemplateKeys.SEARCH_FILTERS);
		this.context.put("categories", ForumRepository.getAllCategories());
		this.context.put("pageTitle", I18n.getMessage("ForumBase.search"));
	}
	
	public void newMessages()
	{
		this.search(new NewMessagesSearchOperation());
	}
	
	public void search()
	{
		this.search(new ContentSearchOperation());
	}
	
	private void search(SearchOperation operation)
	{
		SearchArgs args = this.buildSearchArgs();
		
		int start = args.startFrom();
		int recordsPerPage = SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE);
		
		SearchResult searchResult = operation.performSearch(args);
		operation.prepareForDisplay();
		
		this.setTemplateName(operation.viewTemplate());
		
		this.context.put("results", operation.filterResults(operation.results()));
		this.context.put("categories", ForumRepository.getAllCategories());
		this.context.put("searchArgs", args);
		this.context.put("fr", new ForumRepository());
		this.context.put("pageTitle", I18n.getMessage("ForumBase.search"));
		this.context.put("openModeration", "1".equals(this.request.getParameter("openModeration")));
		this.context.put("postsPerPage", new Integer(SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE)));
		
		ViewCommon.contextToPagination(start, searchResult.numberOfHits(), recordsPerPage);
		TopicsCommon.topicListingBase();
	}
	
	private SearchArgs buildSearchArgs()
	{
		SearchArgs args = new SearchArgs();
		
		args.setKeywords(this.request.getParameter("search_keywords"));
		
		if (this.request.getParameter("search_author") != null) {
			args.setAuthor(this.request.getIntParameter("search_author"));
		}
		
		args.setOrderBy(this.request.getParameter("sort_by"));
		args.setOrderDir(this.request.getParameter("sort_dir"));
		args.startFetchingAtRecord(ViewCommon.getStartPage());
		args.setMatchType(this.request.getParameter("match_type"));
		
		if (this.request.getObjectParameter("from_date") != null
			&& this.request.getObjectParameter("to_date") != null) {
			args.setDateRange((Date)this.request.getObjectParameter("from_date"), 
				(Date)this.request.getObjectParameter("to_date"));		    
		}

		if ("all_terms".equals(args.getMatchType())) {
			args.matchAllKeywords();
		}
		
		if (this.request.getParameter("forum") != null) {
			args.setForumId(this.request.getIntParameter("forum"));
		}
		
		return args;
	}
	
	public void doModeration()
	{
		new ModerationHelper().doModeration(this.makeRedirect());
		
		if (JForumExecutionContext.getRequest().getParameter("topicMove") != null) {
			this.setTemplateName(TemplateKeys.MODERATION_MOVE_TOPICS);
		}
	}
	
	public void moveTopic()
	{
		new ModerationHelper().moveTopicsSave(this.makeRedirect());
	}
	
	public void moderationDone()
	{
		this.setTemplateName(new ModerationHelper().moderationDone(this.makeRedirect()));
	}
	
	private String makeRedirect()
	{
		throw new ForumException("Fix this");
		
		/*
		String persistData = this.request.getParameter("persistData");
		if (persistData == null) {
			this.getSearchFields();
		}
		else {
			String[] p = persistData.split("&");

			for (int i = 0; i < p.length; i++) {
				String[] v = p[i].split("=");

				String name = (String)fieldsMap.get(v[0]);
				if (name != null) {
					Field field;
					
					try {
						field = this.getClass().getDeclaredField(name);
						
						if (field != null && v[1] != null && !v[1].equals("")) {
							field.set(this, v[1]);
						}
					}
					catch (Exception e) {
						throw new ForumException(e);
					}
				}
			}
		}

		StringBuffer path = new StringBuffer(512)
			.append(this.request.getContextPath())
			.append("/jforum")
			.append(SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION))
			.append("?module=search&action=search&clean=1");

		if (this.forum != null) { 
			path.append("&search_forum=").append(this.forum); 
		}

		if (this.searchTerms != null) { 
			path.append("&search_terms=").append(this.searchTerms); 
		}

		if (this.sortDir != null) {
			path.append("&sort_dir=").append(this.sortDir);
		}

		if (this.sortBy != null) {
			path.append("&sort_by=").append(this.sortBy);
		}

		if (this.keywords != null) {
			path.append("&search_keywords=").append(this.keywords);
		}

		if (this.fromDate != null) {
			path.append("&post_time=").append(this.fromDate);
		}

		path.append("&start=").append(ViewCommon.getStartPage());

		return path.toString();
		*/
	}
	
	/** 
	 * @see net.jforum.Command#list()
	 */
	public void list()  
	{
		this.filters();
	}
}
