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
 * This file creation date: 19/04/2004 - 21:11:42
 * net.jforum.util.mail.LostPasswordSpammer.java
 * The JForum Project
 * http://www.jforum.net
 * 
 * $Id: LostPasswordSpammer.java,v 1.3 2004/06/01 19:47:26 pieter2 Exp $
 */
package net.jforum.util.mail;

import java.util.ArrayList;

import freemarker.template.SimpleHash;
import net.jforum.util.I18n;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 */
public class LostPasswordSpammer extends Spammer
{
	public LostPasswordSpammer(String username, String email, String hash)
	{
		String url = SystemGlobals.getValue(ConfigKeys.FORUM_LINK) + "/user/recoverPassword/"+ hash + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);
		SimpleHash params = new SimpleHash();
		params.put("url", url);
		params.put("username", username);
		
		ArrayList recipients = new ArrayList();
		recipients.add(email);
		
		super.prepareMessage(recipients, params, I18n.getMessage("PasswordRecovery.mailTitle"), SystemGlobals.getValue(ConfigKeys.MAIL_LOST_PASSWORD_MESSAGE_FILE));
	}
}
