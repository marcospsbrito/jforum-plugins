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
 * Created on 23/07/2007 15:14:27
 * 
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.admin;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import net.jforum.JForumExecutionContext;
import net.jforum.context.RequestContext;
import net.jforum.context.ResponseContext;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.LuceneDAO;
import net.jforum.entities.Post;
import net.jforum.exceptions.ForumException;
import net.jforum.repository.ForumRepository;
import net.jforum.search.LuceneManager;
import net.jforum.search.LuceneReindexArgs;
import net.jforum.search.LuceneSettings;
import net.jforum.search.SearchFacade;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.TemplateKeys;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.IndexReader;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * @author Rafael Steil
 * @version $Id: LuceneStatsAction.java,v 1.17 2007/08/06 15:38:01 rafaelsteil Exp $
 */
public class LuceneStatsAction extends AdminCommand
{
	/**
	 * @see net.jforum.Command#list()
	 */
	public void list()
	{
		IndexReader reader = null;
		
		try {
			File indexDir = new File(SystemGlobals.getValue(ConfigKeys.LUCENE_INDEX_WRITE_PATH));
			
			this.setTemplateName(TemplateKeys.SEARCH_STATS_LIST);
			
			reader = IndexReader.open(indexDir);
			
			this.context.put("indexExists", IndexReader.indexExists(indexDir));
			this.context.put("isLocked", IndexReader.isLocked(indexDir.getAbsolutePath()));
			this.context.put("lastModified", new Date(IndexReader.lastModified(indexDir)));
			this.context.put("indexLocation", indexDir.getAbsolutePath());
			this.context.put("totalMessages", new Integer(ForumRepository.getTotalMessages()));
			this.context.put("indexVersion", new Long(reader.getVersion()));
			this.context.put("numberOfDocs", new Integer(reader.numDocs()));
		}
		catch (IOException e) {
			throw new ForumException(e);
		}
		finally {
			if (reader != null) {
				try { reader.close(); }
				catch (Exception e) {}
			}
		}
	}
	
	public void createIndexDirectory() throws Exception
	{
		this.settings().createIndexDirectory(
			SystemGlobals.getValue(ConfigKeys.LUCENE_INDEX_WRITE_PATH));
		this.list();
	}
	
	public void reconstructIndexFromScratch()
	{
		final LuceneReindexArgs args = this.buildReindexArgs();
		String indexOperationType = this.request.getParameter("indexOperationType");
		
		try {
			if ("recreate".equals(indexOperationType)) {
				this.settings().createIndexDirectory(SystemGlobals.getValue(ConfigKeys.LUCENE_INDEX_WRITE_PATH));
			}
		}
		catch (IOException e) {
			throw new ForumException(e);
		}
		
		Runnable indexingJob = new Runnable() {		
			public void run()
			{
				LuceneDAO dao = DataAccessDriver.getInstance().newLuceneDAO();
				int startPosition = 0;
				int howMany = 20;
				boolean hasMorePosts = true;
				
				while (hasMorePosts) {
					try {
						JForumExecutionContext ex = JForumExecutionContext.get();
						JForumExecutionContext.set(ex);
						
						List l = dao.getPostsToIndex(args, startPosition, howMany);
						
						for (Iterator iter = l.iterator(); iter.hasNext(); ) {
							Post p = (Post)iter.next();
							
							if (args.avoidDuplicatedRecords()) {
								//SearchFacade.delete(p);
							}
							
							SearchFacade.create(p);
						}
						
						startPosition += howMany;
						hasMorePosts = l.size() > 0;
					}
					finally {
						JForumExecutionContext.finish();
					}
				}
			}
		};
		
		Thread thread = new Thread(indexingJob);
		thread.start();
		
		this.list();
	}
	
	public void luceneNotEnabled()
	{
		this.setTemplateName(TemplateKeys.SEARCH_STATS_NOT_ENABLED);
	}
	
	public Template process(RequestContext request, ResponseContext response, SimpleHash context)
	{
		if (!this.isSearchEngineLucene()) {
			this.ignoreAction();
			this.luceneNotEnabled();
		}
		
		return super.process(request, response, context);
	}
	
	private boolean isSearchEngineLucene()
	{
		return LuceneManager.class.getName()
			.equals(SystemGlobals.getValue(ConfigKeys.SEARCH_INDEXER_IMPLEMENTATION))
			|| this.settings() == null;
	}
	
	private LuceneSettings settings()
	{
		return (LuceneSettings)SystemGlobals.getObjectValue(ConfigKeys.LUCENE_SETTINGS);
	}
	
	private LuceneReindexArgs buildReindexArgs()
	{
		Date fromDate = this.buildDateFromRequest("from");
		Date toDate = this.buildDateFromRequest("to");
		
		int firstPostId = 0;
		int lastPostId = 0;
		
		if (this.request.getParameter("firstPostId") != null) {
			firstPostId = this.request.getIntParameter("firstPostId");
		}
		
		if (this.request.getParameter("lastPostId") != null) {
			lastPostId = this.request.getIntParameter("lastPostId");
		}
		
		return new LuceneReindexArgs(fromDate, toDate, firstPostId, 
			lastPostId, "yes".equals(this.request.getParameter("avoidDuplicatedRecords")), 
			this.request.getIntParameter("type"));
	}
	
	private Date buildDateFromRequest(String prefix)
	{
		String day = this.request.getParameter(prefix + "Day");
		String month = this.request.getParameter(prefix + "Month");
		String year = this.request.getParameter(prefix + "Year");
		
		String hour = this.request.getParameter(prefix + "Hour");
		String minutes = this.request.getParameter(prefix + "Minutes");
		
		Date date = null;
		
		if (!StringUtils.isEmpty(day) 
			&& !StringUtils.isEmpty(month) 
			&& !StringUtils.isEmpty(year) 
			&& !StringUtils.isEmpty(hour) 
			&& !StringUtils.isEmpty(minutes))
		{
			date = new GregorianCalendar(Integer.parseInt(year), 
				Integer.parseInt(month) - 1, 
				Integer.parseInt(year), 
				Integer.parseInt(hour), 
				Integer.parseInt(minutes), 0).getTime();
		}
		
		return date;
	}
}
