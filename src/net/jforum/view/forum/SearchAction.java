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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.jforum.Command;
import net.jforum.JForumExecutionContext;
import net.jforum.context.RequestContext;
import net.jforum.context.ResponseContext;
import net.jforum.dao.SearchArgs;
import net.jforum.entities.Forum;
import net.jforum.exceptions.ForumException;
import net.jforum.repository.ForumRepository;
import net.jforum.search.SearchPost;
import net.jforum.util.I18n;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.TemplateKeys;
import net.jforum.view.forum.common.TopicsCommon;
import net.jforum.view.forum.common.ViewCommon;
import freemarker.template.SimpleHash;

/**
 * @author Rafael Steil
 * @version $Id: SearchAction.java,v 1.46 2007/07/28 14:17:09 rafaelsteil Exp $
 */
public class SearchAction extends Command 
{
	private String searchTerms;
	private String forum;
	private String sortBy;
	private String sortDir;
	private String kw;
	private String author;
	private String postTime;
	
	private static Map fieldsMap = new HashMap();
	private static Map sortByMap = new HashMap();
	
	static {
		fieldsMap.put("search_terms", "searchTerms");
		fieldsMap.put("search_forum", "forum");
		fieldsMap.put("sort_by", "sortBy");
		fieldsMap.put("sort_dir", "sortDir");
		fieldsMap.put("search_keywords", "kw");
		fieldsMap.put("search_author", "author");
		fieldsMap.put("post_time", "postTime");
		
		sortByMap.put("time", "p.post_time");
		sortByMap.put("title", "t.topic_title");
		sortByMap.put("username", "u.username");
		sortByMap.put("forum", "t.forum_id");
	}
	
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
	
	private void getSearchFields()
	{
		this.searchTerms = this.addSlashes(this.request.getParameter("search_terms"));
		this.forum = this.addSlashes(this.request.getParameter("search_forum"));
		
		this.sortBy = (String)sortByMap.get(this.addSlashes(this.request.getParameter("sort_by")));
		
		if (this.sortBy == null) {
			this.sortBy = (String)sortByMap.get("time");
		}
		
		this.sortDir = this.addSlashes(this.request.getParameter("sort_dir"));
		
		if (!"ASC".equals(this.sortDir) && !"DESC".equals(this.sortDir)) {
			this.sortDir = "DESC";
		}
		
		this.kw = this.addSlashes(this.request.getParameter("search_keywords"));
		this.author = this.addSlashes(this.request.getParameter("search_author"));
		this.postTime = this.addSlashes(this.request.getParameter("post_time"));
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
		this.getSearchFields();

		SearchArgs args = this.buildSearchArgs();
		
		int start = ViewCommon.getStartPage();
		int recordsPerPage = SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE);
		
		operation.performSearch(args);
		
		int totalRecords = operation.totalRecords();
		
		int sublistLimit = recordsPerPage + start > totalRecords 
			? totalRecords 
			: recordsPerPage + start;
		
		operation.prepareForDisplay(start, sublistLimit);
		
		this.setTemplateName(operation.viewTemplate());
		
		this.context.put("results", operation.results());
		this.context.put("categories", ForumRepository.getAllCategories());
		this.context.put("kw", kw != null ? kw.trim() : "");
		this.context.put("fr", new ForumRepository());
		this.context.put("terms", searchTerms);
		this.context.put("forum", forum);
		this.context.put("orderField", sortBy);
		this.context.put("orderBy", sortDir);
		this.context.put("author", author);
		this.context.put("postTime", postTime);
		this.context.put("pageTitle", I18n.getMessage("ForumBase.search"));
		this.context.put("openModeration", "1".equals(this.request.getParameter("openModeration")));
		this.context.put("postsPerPage", new Integer(SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE)));
		
		ViewCommon.contextToPagination(start, totalRecords, recordsPerPage);
		TopicsCommon.topicListingBase();
	}

	private SearchArgs buildSearchArgs()
	{
		SearchArgs args = new SearchArgs();
		
		args.setKeywords(kw != null ? kw.trim() : "");
		args.setAuthor(author);
		args.setOrderByField(sortBy);
		args.setOrderBy(sortDir);
		
		if (postTime != null) {
			args.setTime(new Date(Long.parseLong(postTime)));		    
		}
		
		if (searchTerms != null && !searchTerms.equals("any")) {
			args.matchAllKeywords();
		}
		
		if (forum != null && !forum.equals("")) {
			args.setForumId(Integer.parseInt(forum));
		}
		
		args.setSearchStarted(this.request.getParameter("clean") == null);
		
		return args;
	}
	
	private List filterResults(List results)
	{
		List l = new ArrayList();
		
		Map forums = new HashMap();
		
		for (Iterator iter = results.iterator(); iter.hasNext(); ) {
			SearchPost post = (SearchPost)iter.next();
			
			Integer forumId = new Integer(post.getForumId());
			ForumFilterResult status = (ForumFilterResult)forums.get(forumId);
			
			if (status == null) {
				Forum f = ForumRepository.getForum(post.getForumId());
				status = new ForumFilterResult(f);
				forums.put(forumId, status);
			}
			
			if (status.isValid()) {
				post.setForum(status.getForum());
				l.add(post);
			}
		}
		
		return l;
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

		if (this.kw != null) {
			path.append("&search_keywords=").append(this.kw);
		}

		if (this.postTime != null) {
			path.append("&post_time=").append(this.postTime);
		}

		path.append("&start=").append(ViewCommon.getStartPage());

		return path.toString();
	}
	
	private String addSlashes(String s)
	{
		if (s != null) {
			s = s.replaceAll("'", "\\'");
			s = s.replaceAll("\"", "\\\"");
		}
		
		return s;
	}
	
	/** 
	 * @see net.jforum.Command#list()
	 */
	public void list()  
	{
		this.filters();
	}
	
	private static class ForumFilterResult
	{
		private Forum forum;
		
		public ForumFilterResult(Forum forum)
		{
			this.forum = forum;
		}
		
		public Forum getForum() 
		{
			return this.forum;
		}
		
		public boolean isValid()
		{
			return this.forum != null;
		}
	}
}
