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
 * This file creation date: 03/08/2003 / 05:28:03
 * net.jforum.util.bbcode.BBCollection.java
 * The JForum Project
 * http://www.jforum.net
 * 
 * $Id: BBCodeHandler.java,v 1.2 2004/04/21 23:57:41 rafaelsteil Exp $
 */
package net.jforum.util.bbcode;

 import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.jforum.util.SystemGlobals;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Rafael Steil
 */
public class BBCodeHandler extends DefaultHandler
{
	private ArrayList bbList = new ArrayList();
	private boolean matchOpen = false;
	private String tagName = "";
	private StringBuffer sb;	
	private BBCode bb;
	
	public BBCodeHandler() { }
	
	public BBCodeHandler parse() throws Exception
	{
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		InputSource input = new InputSource(SystemGlobals.getApplicationResourceDir() +"/config/bb_config.xml");
		BBCodeHandler bbParser = new BBCodeHandler();
		parser.parse(input, bbParser);
		
		return bbParser;  
	}
	
	public void addBb(BBCode bb)
	{
		this.bbList.add(bb);
	}
	
	public ArrayList getBbList()
	{
		return this.bbList;
	}
	
	public void startElement(String uri, String localName, String tag, Attributes attrs)
	{
		if (tag.equals("match")) {
			this.matchOpen = true;
			this.sb = new StringBuffer();
			this.bb = new BBCode();
		}
		else if (tag.equals("before")) {
			this.bb.setBeforeReplace(attrs.getValue("replace"));
			this.bb.setBeforeReplaceWith(attrs.getValue("with"));
			this.bb.setBeforeUseRegex(attrs.getValue("useRegex"));
		}
	
		this.tagName = tag;
	}

	public void endElement(String uri, String localName, String tag)
	{	
		if (tag.equals("match")) {
			this.matchOpen = false;
			this.bbList.add(this.bb);
		}
		else if (this.tagName.equals("replace")) {
			this.bb.setReplace(this.sb.toString().trim());
			this.sb.delete(0, this.sb.length());
		}
		else if (this.tagName.equals("regex")) {
			this.bb.setRegex(this.sb.toString().trim());
			this.sb.delete(0, this.sb.length());
		}
	
		this.tagName = "";
	}

	public void characters(char ch[], int start, int length)
	{
		if (this.tagName.equals("replace") || this.tagName.equals("regex"))
			this.sb.append(ch, start, length);
	}

	public void error(SAXParseException exception) throws SAXException 
	{
		throw exception;
	}
}