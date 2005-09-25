/*
 * Copyright (c) Rafael Steil
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
 * This file creation date: 19/03/2004 - 18:54:27
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.generic.AutoKeys;
import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.repository.RolesRepository;
import net.jforum.security.Role;
import net.jforum.security.RoleCollection;
import net.jforum.security.RoleValueCollection;
import net.jforum.security.UserSecurityHelper;

/**
 * @author Rafael Steil
 * $Id: GenericUserSecurityDAO.java,v 1.9 2005/09/25 02:18:35 rafaelsteil Exp $
 */
public class GenericUserSecurityDAO extends AutoKeys implements net.jforum.dao.security.UserSecurityDAO, Serializable 
{
	/** 
	 * @see net.jforum.dao.security.SecurityDAO#deleteAllRoles(int)
	 */
	public void deleteAllRoles(int id) throws Exception 
	{
		throw new OperationNotSupportedException();
	}

	/** 
	 * @see net.jforum.dao.security.SecurityDAO#deleteRole(int, java.lang.String)
	 */
	public void deleteRole(int id, String roleName) throws Exception 
	{
		throw new OperationNotSupportedException();
	}

	/** 
	 * @see net.jforum.dao.security.SecurityDAO#addRole(int, net.jforum.security.Role)
	 */
	public void addRole(int id, Role role) throws Exception 
	{
		throw new OperationNotSupportedException();	
	}

	/** 
	 * @see net.jforum.dao.security.SecurityDAO#addRole(int, net.jforum.security.Role, net.jforum.security.RoleValueCollection)
	 */
	public void addRole(int id, Role role, RoleValueCollection roleValues) throws Exception 
	{
		throw new OperationNotSupportedException();
	}

	/** 
	 * @see net.jforum.dao.security.SecurityDAO#loadRoles(int)
	 */
	public RoleCollection loadRoles(int id) throws Exception 
	{
		User u = DataAccessDriver.getInstance().newUserDAO().selectById(id);

		return this.loadRoles(u);
	}

	/** 
	 * @see net.jforum.dao.security.UserSecurityDAO#loadRoles(net.jforum.entities.User)
	 */
	public RoleCollection loadRoles(User user) throws Exception 
	{
		List groups = user.getGroupsList();
		
		// For single group, we don't  need to check for merged roles
		if (groups.size() == 1) {
			return (RoleCollection)this.loadGroupRoles(groups).get(0);
		}
		
		// When the user is associated to more than one group, we
		// should check the merged roles
		int[] groupIds = this.getSortedGroupIds(groups);
		
		RoleCollection groupRoles = RolesRepository.getGroupRoles(groupIds);
		
		// Not cached yet? then do it now
		if (groupRoles == null) {
			List l = this.loadGroupRoles(groups);
			
			groupRoles = new RoleCollection();
			UserSecurityHelper.mergeUserGroupRoles(groupRoles, l);
			
			RolesRepository.addMergedGroupRoles(groupIds, groupRoles);
		}
		
		return groupRoles;
	}
	
	/**
	 * Load roles from the groups.
	 * @param groups The groups to load the roles
	 * @throws Exception
	 */
	private List loadGroupRoles(List groups) throws Exception
	{
		List groupRolesList = new ArrayList();
		GenericGroupSecurityDAO gmodel = new GenericGroupSecurityDAO();
		
		for (Iterator iter = groups.iterator(); iter.hasNext(); ) {
			Group g = (Group)iter.next();
			
			RoleCollection roles = RolesRepository.getGroupRoles(g.getId());
			
			if (roles == null) {
				roles = gmodel.loadRoles(g.getId());
				RolesRepository.addGroupRoles(g.getId(), roles);
			}
			
			groupRolesList.add(roles);
		}
		
		return groupRolesList;
	}
	
	private int[] getSortedGroupIds(List groups)
	{
		int[] groupsIds = new int[groups.size()];
		int i = 0;
		
		for (Iterator iter = groups.iterator(); iter.hasNext(); ) {
			groupsIds[i++] = ((Group)iter.next()).getId(); 
		}
		
		Arrays.sort(groupsIds);
		
		return groupsIds;
	}

	/**
	 * @see net.jforum.dao.security.SecurityDAO#addRoleValue(int, Role, RoleValueCollection)
	 */
	public void addRoleValue(int id, Role role, RoleValueCollection rvc) throws Exception
	{
		throw new UnsupportedOperationException();
	}
}
