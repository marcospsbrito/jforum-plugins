/*
 * Copyright (c) 2003, 2004 Rafael Steil
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
 * This file creation date: 25/02/2004 - 19:32:42
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.drivers.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.jforum.JForum;
import net.jforum.SessionFacade;
import net.jforum.entities.Post;
import net.jforum.model.SearchData;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: SearchModel.java,v 1.13 2004/11/11 17:44:51 rafaelsteil Exp $
 */
public class SearchModel extends AutoKeys implements net.jforum.model.SearchModel	
{
	/** 
	 * @see net.jforum.model.SearchModel#search(net.jforum.model.SearchData)
	 */
	public List search(SearchData sd) throws Exception 
	{
		List l = new ArrayList();
		List topics = new ArrayList();
		
		// Check for the search cache
		if (!sd.getSearchStarted()) {
			if (sd.getTime() == null) {
				this.topicsByKeyword(sd);
			}
			else {
				this.topicsByTime(sd);
			}
		}
		
		StringBuffer criterias = new StringBuffer(256);
		if (sd.getForumId() != 0) {
			criterias.append(" AND t.forum_id = "+ sd.getForumId());
		}
		
		if (sd.getCategoryId() != 0) {
			criterias.append(" AND f.categories_id = "+ sd.getCategoryId());
		}
		
		if (sd.getOrderByField() == null || sd.getOrderByField().equals("")) {
			sd.setOrderByField("p.post_time");
		}
		
		String sql = SystemGlobals.getSql("SearchModel.searchBase");
		// Prepare the query
		sql = sql.replaceAll(":orderByField:", sd.getOrderByField());
		sql = sql.replaceAll(":orderBy:", sd.getOrderBy());
		sql = sql.replaceAll(":criterias:", criterias.toString());
		
		PreparedStatement p = JForum.getConnection().prepareStatement(sql);
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.setString(2, SessionFacade.getUserSession().getSessionId());

		ResultSet rs = p.executeQuery();
		
		l = new TopicModel().fillTopicsData(rs);
		
		rs.close();
		p.close();
		
		return l;
	}
	
	// Find topics by time
	private void topicsByTime(SearchData sd) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("SearchModel.searchByTime"));
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.setTimestamp(2, new Timestamp(sd.getTime().getTime()));
		p.executeUpdate();
		p.close();
		
		this.selectTopicData();
	}
	
	// Given a set of keywords, find the topics
	private void topicsByKeyword(SearchData sd) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("SearchModel.searchByWord"));

		HashMap eachWordMap = new HashMap();

		// Get the post ids to which the words are associated to
		for (int i = 0; i < sd.getKeywords().length; i++) {
			p.setString(1, sd.getKeywords()[i]);
			
			HashSet postsIds = new HashSet();
			ResultSet rs = p.executeQuery();
			while (rs.next()) {
				postsIds.add(new Integer(rs.getInt("post_id")));
			}
			
			if (postsIds.size() > 0) {
				eachWordMap.put(sd.getKeywords()[i], postsIds);
			}
		}
		
		// [wordName] = { each, post, id }
		
		// If seach type is OR, then get all words
		// If it is AND, then we want only the ids common to all words
		// ( oooohhh.. really? that's soooo unlogic )
		HashSet postsIds = null;
		
		if (sd.getUseAllWords()) {
			for (Iterator iter = eachWordMap.values().iterator(); iter.hasNext(); ) {
				if (postsIds == null) {
					postsIds = new HashSet(eachWordMap.values().size());
					postsIds.addAll((HashSet)iter.next());
				}
				else {
					postsIds.retainAll((HashSet)iter.next());
				}
			}
		}
		else {
			postsIds = new HashSet();
			
			for (Iterator iter = eachWordMap.values().iterator(); iter.hasNext(); ) {
				postsIds.addAll((HashSet)iter.next());
			}
		}
		
		if (postsIds == null || postsIds.size() == 0) {
			return;
		}
		
		// Time to get ready to search for the topics ids 
		StringBuffer sb = new StringBuffer(1024);
		for (Iterator iter = postsIds.iterator(); iter.hasNext(); ) {
			sb.append(iter.next()).append(",");
		}
		sb.delete(sb.length() - 1, sb.length());

		// Search for the ids, inserting them in the helper table 
		String sql = SystemGlobals.getSql("SearchModel.insertTopicsIds");
		sql = sql.replaceAll(":posts:", sb.toString());
		p = JForum.getConnection().prepareStatement(sql);
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.executeUpdate();
		
		// Now that we have the topics ids, it's time to make a copy from the 
		// topics table, to make the search faster ( damn, next version I'll 
		// remove the search functionality. Look for this code's size )
		this.selectTopicData();
		
		p.close();
	}
	
	private void selectTopicData() throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("SearchModel.selectTopicData"));
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.setString(2, SessionFacade.getUserSession().getSessionId());
		p.executeUpdate();
		
		p.close();
	}
	
	public void insertSearchWords(Post post) throws Exception
	{
		PreparedStatement insert = this.getStatementForAutoKeys("SearchModel.insertWords");
		PreparedStatement existing = JForum.getConnection().prepareStatement(
						SystemGlobals.getSql("SearchModel.searchExistingWord"));
		
		PreparedStatement existingAssociation = JForum.getConnection().prepareStatement(
						SystemGlobals.getSql("SearchModel.searchExistingAssociation"));
		existingAssociation.setInt(2, post.getId());
		
		PreparedStatement wordToPost = JForum.getConnection().prepareStatement(
						SystemGlobals.getSql("SearchModel.associateWordToPost"));
		wordToPost.setInt(1, post.getId());
		
		String str = post.getText() +" "+ post.getSubject();
		String[] words = str.toLowerCase().replaceAll("[\\.\\\\\\/~^&\\(\\)-_+=!@#$%\"\'\\[\\]\\{\\}?<:>,*]", " ").split(" ");
						
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].trim();
			// Skip words less than 3 chars
			if (words[i].length() < 3) {
				continue;
			}
			
			// Verify if the current word is not in the database before proceeding
			int hash = words[i].hashCode();
			existing.setInt(1, hash);
			ResultSet rs = existing.executeQuery();

			if (!rs.next()) {
				// The word is not in the database. Insert it now
				insert.setInt(1, hash);
				insert.setString(2, words[i]);
				int wordId = this.executeAutoKeysQuery(insert);

				// Associate the current word to the post
				this.associateWordToPost(wordToPost, words[i], wordId, post);
			}
			else {
				// The word is already in the database ( jforum_search_words )
				// Check then if the current post is not already associated to the word
				int wordId = rs.getInt("word_id");
				existingAssociation.setInt(1, wordId);
				
				ResultSet rsa = existingAssociation.executeQuery();
				if (!rsa.next()) {
					// Assoacite the post to the word
					this.associateWordToPost(wordToPost, words[i], wordId, post);
				}
				rsa.close();
			}
			
			rs.close();
		}
		
		insert.close();
		existing.close();
		wordToPost.close();
	}
	
	private void associateWordToPost(PreparedStatement p, String word, int wordId, Post post) throws Exception
	{
		p.setInt(2, wordId);
		
		String subject = post.getSubject();
		int inSubject = 0;
		if (subject != null && !subject.equals("")) {
			inSubject = subject.indexOf(word) > -1 ? 1 : 0;
		}
		
		p.setInt(3, inSubject);
		p.executeUpdate();
	}

	/** 
	 * @see net.jforum.model.SearchModel#cleanSearch()
	 */
	public void cleanSearch() throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("SearchModel.cleanSearchTopics"));
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.executeUpdate();
		
		p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("SearchModel.cleanSearchResults"));
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.executeUpdate();
		p.close();
	}
}
