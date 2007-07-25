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
 * Created on 18/07/2007 22:05:37
 * 
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.search;

import java.util.ArrayList;
import java.util.List;

import net.jforum.dao.SearchArgs;
import net.jforum.dao.SearchDAO;
import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.exceptions.SearchException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

/**
 * @author Rafael Steil
 * @version $Id: LuceneSearch.java,v 1.14 2007/07/25 03:08:14 rafaelsteil Exp $
 */
public class LuceneSearch implements SearchDAO, NewDocumentAdded
{
	private static final Logger logger = Logger.getLogger(LuceneSearch.class);
	
	private IndexSearcher search;
	private LuceneSettings settings;
	
	public void setSettings(LuceneSettings settings) throws Exception
	{
		this.settings = settings;
		this.openSearch();
	}
	
	private void openSearch() throws Exception
	{
		this.search = new IndexSearcher(this.settings.directory());
	}
	
	/**
	 * @see net.jforum.search.NewDocumentAdded#newDocumentAdded()
	 */
	public void newDocumentAdded()
	{
		try {
			this.search.close();
			this.openSearch();
		}
		catch (Exception e) {
			throw new SearchException(e);
		}
	}
	
	/**
	 * @see net.jforum.dao.SearchDAO#search(net.jforum.dao.SearchArgs)
	 */
	public List search(SearchArgs args)
	{
		List l = new ArrayList();
		
		try {
			StringBuffer sb = new StringBuffer(256);
			
			this.filterByForum(args, sb);
			this.filterByKeywords(args, sb);
			
			Query query = new QueryParser("", new StandardAnalyzer()).parse(sb.toString());
			
			if (logger.isDebugEnabled()) {
				logger.debug("Generated query: " + query);
			}
			
			Hits hits = this.search.search(query);
			 			
			if (hits != null && hits.length() > 0) {
				int total = hits.length(); 
				
				for (int i = 0; i < total; i++) {
					Document doc = hits.doc(i);
					
					Forum forum = new Forum(Integer.parseInt(doc.get(SearchFields.Keyword.FORUM_ID)));
					Post post = new Post(Integer.parseInt(doc.get(SearchFields.Keyword.POST_ID)));
					Topic topic = new Topic(Integer.parseInt(doc.get(SearchFields.Keyword.TOPIC_ID)));
					User user = new User(Integer.parseInt(doc.get(SearchFields.Keyword.USER_ID)));
					
					SearchResult result = new SearchResult();
					
					result.setAuthor(user);
					result.setForum(forum);
					result.setTopic(topic);
					result.setPost(post);
					
					l.add(result);
				}
			}
		}
		catch (Exception e) {
			throw new SearchException(e);
		}
		
		return l;
	}

	private void filterByKeywords(SearchArgs sd, StringBuffer sb)
	{
		String[] keywords = sd.getKeywords();
		
		for (int i = 0; i < keywords.length; i++) {
			if (sd.shouldMatchAllKeywords()) {
				sb.append(" +");
			}
			
			sb.append('(')
			.append(SearchFields.Indexed.CONTENTS)
			.append(':')
			.append(keywords[i])
			.append(") ");
		}
	}

	private void filterByForum(SearchArgs sd, StringBuffer sb)
	{
		// TODO: Remove those forums that the current user doesn't
		// have permissions to see
		if (sd.getForumId() > 0) {
			sb.append("+(")
				.append(SearchFields.Keyword.FORUM_ID)
				.append(':')
				.append(sd.getForumId())
				.append(") ");
		}
	}
	
	public void cleanSearch() { }
}
