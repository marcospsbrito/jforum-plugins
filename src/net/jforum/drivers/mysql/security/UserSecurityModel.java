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
 * This file creation date: 19/03/2004 - 18:54:27
 * net.jforum.drivers.mysql.security.UserSecurityModel.java
 * The JForum Project
 * http://www.jforum.net
 * 
 * $Id: UserSecurityModel.java,v 1.2 2004/04/21 23:57:39 rafaelsteil Exp $
 */
package net.jforum.drivers.mysql.security;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;

import net.jforum.JForum;
import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.model.DataAccessDriver;
import net.jforum.security.Role;
import net.jforum.security.RoleCollection;
import net.jforum.security.RoleValueCollection;
import net.jforum.security.UserSecurityHelper;
import net.jforum.util.SystemGlobals;

/**
 * @author Rafael Steil
 */
public class UserSecurityModel implements net.jforum.model.security.UserSecurityModel 
{
	/* 
	 * @see net.jforum.model.security.SecurityModel#deleteAllRoles(int)
	 */
	public void deleteAllRoles(int id) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PermissionControl.deleteAllUserRoles"));
		p.setInt(1, id);
		p.executeUpdate();
		p.close();
	}

	/* 
	 * @see net.jforum.model.security.SecurityModel#deleteRole(int, java.lang.String)
	 */
	public void deleteRole(int id, String roleName) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PermissionControl.deleteUserRole"));
		p.setString(1, roleName);
		p.setInt(2, id);
		p.executeUpdate();
		p.close();
	}

	/* 
	 * @see net.jforum.model.security.SecurityModel#addRole(int, net.jforum.security.Role)
	 */
	public void addRole(int id, Role role) throws Exception 
	{
		this.addRole(id, role, null);	
	}

	/* 
	 * @see net.jforum.model.security.SecurityModel#addRole(int, net.jforum.security.Role, net.jforum.security.RoleValueCollection)
	 */
	public void addRole(int id, Role role, RoleValueCollection roleValues) throws Exception 
	{
		SecurityCommon.executeAddRole(SystemGlobals.getSql("PermissionControl.addUserRole"), id, role, roleValues);
	}

	/* 
	 * @see net.jforum.model.security.SecurityModel#loadRoles(int)
	 */
	public RoleCollection loadRoles(int id) throws Exception 
	{
		User u = DataAccessDriver.getInstance().newUserModel().selectById(id);

		return this.loadRoles(u);
	}

	/* 
	 * @see net.jforum.model.security.UserSecurityModel#deleteUserRoleByGroup(int, java.lang.String)
	 */
	public void deleteUserRoleByGroup(int groupId, String roleName) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PermissionControl.deleteUserRoleByGroup"));
		p.setInt(1, groupId);
		p.setString(2, roleName);
		p.executeUpdate();
		
		p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PermissionControl.deleteUserRoleValuesByRoleName"));
		p.setInt(1, groupId);
		p.setString(2, roleName);
		p.executeUpdate();
		
		p.close();
	}

	/* 
	 * @see net.jforum.model.security.UserSecurityModel#deleteUserRoleValuesByGroup(int, java.lang.String, java.lang.String)
	 */
	public void deleteUserRoleValuesByGroup (int groupId, String roleName, String roleValue) throws Exception 
	{
		this.deleteUserRoleValuesByGroup(groupId, roleName, new String[] { roleValue });
	}

	/* 
	 * @see net.jforum.model.security.UserSecurityModel#deleteUserRoleValuesByGroup(int, java.lang.String, java.lang.String[])
	 */
	public void deleteUserRoleValuesByGroup(int groupId, String roleName, String[] roleValues) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PermissionControl.deleteUserRoleValueByGroup"));
		
		for (int i = 0; i < roleValues.length; i++) {
			p.setInt(1, groupId);
			p.setString(2, roleName);
			p.setString(3, roleValues[i]);
			
			p.executeUpdate();
		}
		
		p.close();
	}

	/* 
	 * @see net.jforum.model.security.UserSecurityModel#deleteUserRoleValuesByGroup(int, java.lang.String)
	 */
	public void deleteUserRoleValuesByGroup(int groupId, String roleName) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PermissionControl.deleteUserRoleByGroup"));
		p.setInt(1, groupId);
		p.setString(2, roleName);
		p.executeUpdate();
		
		p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PermissionControl.deleteUserRoleValuesByRoleName"));
		p.setInt(1, groupId);
		p.setString(2, roleName);
		p.executeUpdate();
		
		p.close();
	}

	/* 
	 * @see net.jforum.model.security.UserSecurityModel#loadRoles(net.jforum.entities.User)
	 */
	public RoleCollection loadRoles(User user) throws Exception 
	{
		RoleCollection roles = SecurityCommon.processLoadRoles(SystemGlobals.getSql("PermissionControl.loadUserRoles"), user.getId());
		
		ArrayList groupRolesList = this.loadGroupRoles(user.getGroupsList());
		UserSecurityHelper.mergeUserGroupRoles(roles, groupRolesList);

		return roles;
	}
	
	private ArrayList loadGroupRoles(ArrayList groups) throws Exception
	{
		ArrayList groupRolesList = new ArrayList();
		GroupSecurityModel gmodel = new GroupSecurityModel();
		
		for (Iterator iter = groups.iterator(); iter.hasNext(); ) {
			Group g = (Group)iter.next();
			
			groupRolesList.add(gmodel.loadRoles(g.getId()));
		}
		
		return groupRolesList;
	}

}
