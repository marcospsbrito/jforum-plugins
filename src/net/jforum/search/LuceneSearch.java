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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;

import net.jforum.dao.SearchDAO;
import net.jforum.dao.SearchData;
import net.jforum.entities.Forum;
import net.jforum.exceptions.SearchException;

/**
 * @author Rafael Steil
 * @version $Id: LuceneSearch.java,v 1.1 2007/07/19 01:38:03 rafaelsteil Exp $
 */
public class LuceneSearch implements SearchDAO
{
	private IndexSearcher search;
	private Directory directory;
	
	public void setDirectory(Directory directory) throws Exception
	{
		this.directory = directory;
		this.openSearch();
	}
	
	private void openSearch() throws Exception
	{
		this.search = new IndexSearcher(this.directory);
	}
	
	/**
	 * @see net.jforum.dao.SearchDAO#search(net.jforum.dao.SearchData)
	 */
	public List search(SearchData sd)
	{
		List l = new ArrayList();
		
		try {
			Hits hits = null;
			
			if (sd.getForumId() > 0) {
				hits = this.search.search(new TermQuery(
					new Term(SearchFields.Keyword.FORUM_ID, String.valueOf(sd.getForumId()))));
			}
			
			if (hits != null && hits.length() > 0) {
				int total = hits.length(); 
				
				for (int i = 0; i < total; i++) {
					Document doc = hits.doc(i);
					
					SearchResult result = new SearchResult(null, null, null, 
						new Forum(Integer.parseInt(doc.get(SearchFields.Keyword.FORUM_ID))), null);
				}
			}
		}
		catch (Exception e) {
			throw new SearchException(e);
		}
		
		return l;
	}
	
	public void cleanSearch() { }
}
