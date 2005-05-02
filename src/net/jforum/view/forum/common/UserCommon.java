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
 * Created on 29/11/2004 23:07:10
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.forum.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import net.jforum.JForum;
import net.jforum.SessionFacade;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.UserDAO;
import net.jforum.entities.User;
import net.jforum.util.I18n;
import net.jforum.util.MD5;
import net.jforum.util.SafeHtml;
import net.jforum.util.image.ImageUtils;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 * @version $Id: UserCommon.java,v 1.4 2005/05/02 03:44:20 rafaelsteil Exp $
 */
public class UserCommon 
{
	private static final Logger logger = Logger.getLogger(UserCommon.class);

	/**
	 * Updates the user information
	 * 
	 * @param userId The user id we are saving
	 * @throws Exception
	 */
	public static List saveUser(int userId) throws Exception
	{
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		User u = um.selectById(userId);
		
		String username = JForum.getRequest().getParameter("username");
		if (username != null) {
			u.setUsername(username);
		}
		
		u.setId(userId);
		u.setEmail(SafeHtml.makeSafe(JForum.getRequest().getParameter("email")));
		u.setIcq(SafeHtml.makeSafe(JForum.getRequest().getParameter("icq")));
		u.setAim(SafeHtml.makeSafe(JForum.getRequest().getParameter("aim")));
		u.setMsnm(SafeHtml.makeSafe(JForum.getRequest().getParameter("msn")));
		u.setYim(SafeHtml.makeSafe(JForum.getRequest().getParameter("yim")));
		u.setFrom(SafeHtml.makeSafe(JForum.getRequest().getParameter("location")));
		u.setOccupation(SafeHtml.makeSafe(JForum.getRequest().getParameter("occupation")));
		u.setInterests(SafeHtml.makeSafe(JForum.getRequest().getParameter("interests")));
		u.setSignature(SafeHtml.makeSafe(JForum.getRequest().getParameter("signature")));
		u.setViewEmailEnabled(JForum.getRequest().getParameter("viewemail").equals("1"));
		u.setViewOnlineEnabled(JForum.getRequest().getParameter("hideonline").equals("0"));
		u.setNotifyPrivateMessagesEnabled(JForum.getRequest().getParameter("notifypm").equals("1"));
		u.setNotifyOnMessagesEnabled(JForum.getRequest().getParameter("notifyreply").equals("1"));
		u.setAttachSignatureEnabled(JForum.getRequest().getParameter("attachsig").equals("1"));
		u.setHtmlEnabled(JForum.getRequest().getParameter("allowhtml").equals("1"));
		u.setLang(JForum.getRequest().getParameter("language"));
		
		String website = SafeHtml.makeSafe(JForum.getRequest().getParameter("website"));
		if (website != null && !"".equals(website.trim()) && !website.toLowerCase().startsWith("http://")) {
			website = "http://" + website;
		}
	
		u.setWebSite(website);
		
		if (JForum.getRequest().getParameter("new_password") != null && JForum.getRequest().getParameter("new_password").length() > 0) {
			u.setPassword(MD5.crypt(JForum.getRequest().getParameter("new_password")));
		}
		
		if (JForum.getRequest().getParameter("avatardel") != null) {
			File f = new File(SystemGlobals.getApplicationPath() + "/images/avatar/"+ u.getAvatar());
			f.delete();
			
			u.setAvatar(null);
		}
	
		List warns = new ArrayList();
		if (JForum.getRequest().getObjectParameter("avatar") != null) {
			try {
				UserCommon.handleAvatar(u);
			}
			catch (Exception e) {
				UserCommon.logger.warn("Problems while uploading the avatar: " + e);
				warns.add(I18n.getMessage("User.avatarUploadError"));
			}
		}
		else {
			String avatarUrl = JForum.getRequest().getParameter("avatarUrl");
			if (avatarUrl != null && !"".equals(avatarUrl.trim())) {
				if (avatarUrl.toLowerCase().startsWith("http://")) {
					u.setAvatar(avatarUrl);
				}
				else {
					warns.add(I18n.getMessage("User.avatarUrlShouldHaveHttp"));
				}
			}
		}
		
		um.update(u);
		
		SessionFacade.getUserSession().setLang(u.getLang());
		
		return warns;
	}

	/**
	 * @param u
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void handleAvatar(User u) throws Exception 
	{
		String fileName = MD5.crypt(Integer.toString(u.getId()));
		FileItem item = (FileItem)JForum.getRequest().getObjectParameter("avatar");
		UploadUtils uploadUtils = new UploadUtils(item, JForum.getRequest());
		
		// Gets file extension
		String extension = uploadUtils.getExtension().toLowerCase();
		int type = -1;
		
		if (extension.equals("jpg") || extension.equals("jpeg")) {
			type = ImageUtils.IMAGE_JPEG;
		}
		else if (extension.equals("gif") || extension.equals("png")) {
			type = ImageUtils.IMAGE_PNG;
		}
		
		if (type != -1) {
			String avatarTmpFileName = SystemGlobals.getApplicationPath() 
				+ "/images/avatar/" + fileName + "_tmp." + extension;
			
			// We cannot handle gifs
			if (extension.toLowerCase().equals("gif")) {
				extension = "png";
			}
	
			String avatarFinalFileName = SystemGlobals.getApplicationPath() + 
				"/images/avatar/" + fileName + "." + extension;
	
			uploadUtils.saveUploadedFile(avatarTmpFileName);
			
			// OK, time to check and process the avatar size
			int maxWidth = SystemGlobals.getIntValue(ConfigKeys.AVATAR_MAX_WIDTH);
			int maxHeight = SystemGlobals.getIntValue(ConfigKeys.AVATAR_MAX_HEIGHT);
	
			BufferedImage image = ImageUtils.resizeImage(avatarTmpFileName, type, maxWidth, maxHeight);
			ImageUtils.saveImage(image, avatarFinalFileName, type);
	
			u.setAvatar(fileName + "." + extension);
			
			// Delete the temporary file
			new File(avatarTmpFileName).delete();
		}
	}

}
