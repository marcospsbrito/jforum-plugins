/*
 * Copyright (c) 2005 Rafael Steil
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
 * Created on Feb 22, 2005 4:24:18 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.drivers.generic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.jforum.entities.Post;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: SearchIndexerModel.java,v 1.1 2005/02/22 20:32:38 rafaelsteil Exp $
 */
public class SearchIndexerModel extends AutoKeys implements net.jforum.model.SearchIndexerModel
{
	private Connection conn;
	
	/**
	 * @see net.jforum.model.SearchIndexerModel#setConnection(java.sql.Connection)
	 */
	public void setConnection(Connection conn)
	{
		this.conn = conn;
	}
	
	/**
	 * @see net.jforum.model.SearchIndexerModel#insertSearchWords(net.jforum.entities.Post)
	 */
	public void insertSearchWords(Post post) throws Exception
	{
		PreparedStatement insert = this.getStatementForAutoKeys("SearchModel.insertWords", this.conn);
		PreparedStatement existing = this.conn.prepareStatement(
						SystemGlobals.getSql("SearchModel.searchExistingWord"));
		
		PreparedStatement existingAssociation = this.conn.prepareStatement(
						SystemGlobals.getSql("SearchModel.searchExistingAssociation"));
		existingAssociation.setInt(2, post.getId());
		
		PreparedStatement wordToPost = this.conn.prepareStatement(
						SystemGlobals.getSql("SearchModel.associateWordToPost"));
		wordToPost.setInt(1, post.getId());
		
		String str = post.getText() +" "+ post.getSubject();
		String[] words = str.toLowerCase().replaceAll("[\\.\\\\\\/~^&\\(\\)-_+=!@#$%\"\'\\[\\]\\{\\}?<:>,*�A�B�C�D�E�F�G�H�I�J�K�L�U�Z�]�^�a�b�e�f�i�j�m�n�q�r�u�v�y�z��������������������\n\r\t]", " ").split(" ");
						
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].trim();
			// Skip words less than 3 chars
			if (words[i].length() < 3) {
				continue;
			}
			
			// Trucate words longer than 100 chars
			if (words[i].length() > 100) {
				words[i] = words[i].substring(0, 100); 
			}
			
			// Verify if the current word is not in the database before proceeding
			int hash = words[i].hashCode();
			existing.setInt(1, hash);
			ResultSet rs = existing.executeQuery();

			if (!rs.next()) {
				// The word is not in the database. Insert it now
				insert.setInt(1, hash);
				insert.setString(2, words[i]);
				int wordId = this.executeAutoKeysQuery(insert, this.conn);

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
}
