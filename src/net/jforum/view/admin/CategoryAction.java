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
 * This file creation date: Mar 10, 2003 / 8:49:51 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.admin;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.jforum.ActionServletRequest;
import net.jforum.Command;
import net.jforum.entities.Category;
import net.jforum.model.CategoryModel;
import net.jforum.model.DataAccessDriver;
import net.jforum.model.GroupModel;
import net.jforum.model.security.GroupSecurityModel;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.SecurityRepository;
import net.jforum.security.PermissionControl;
import net.jforum.security.Role;
import net.jforum.security.RoleValue;
import net.jforum.security.RoleValueCollection;
import net.jforum.security.SecurityConstants;
import net.jforum.util.I18n;
import net.jforum.util.TreeGroup;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * ViewHelper for category administration.
 * 
 * @author Rafael Steil
 * @version $Id: CategoryAction.java,v 1.10 2004/12/27 00:30:50 rafaelsteil Exp $
 */
public class CategoryAction extends Command 
{
	private CategoryModel cm;
	
	// Listing
	public void list() throws Exception
	{
		this.context.put("categories", DataAccessDriver.getInstance().newCategoryModel().selectAll());
		this.context.put("repository", new ForumRepository());
		this.context.put("moduleAction", "category_list.htm");
	}
	
	// One more, one more
	public void insert() throws Exception
	{
		this.context.put("groups", new TreeGroup().getNodes());
		this.context.put("selectedList", new ArrayList());
		this.context.put("moduleAction", "category_form.htm");
		this.context.put("action", "insertSave");
	}
	
	// Edit
	public void edit() throws Exception
	{
		this.context.put("category", this.cm.selectById(Integer.parseInt(this.request.getParameter("category_id"))));
		this.context.put("moduleAction", "category_form.htm");
		this.context.put("action", "editSave");
	}
	
	//  Save information
	public void editSave() throws Exception
	{
		Category c = new Category();
		c.setName(this.request.getParameter("category_name"));
		c.setId(Integer.parseInt(this.request.getParameter("categories_id")));
			
		this.cm.update(c);
		ForumRepository.reloadCategory(c);
			
		this.list();
	}
	
	// Delete
	public void delete() throws Exception
	{
		String ids[] = this.request.getParameterValues("categories_id");
		List errors = new ArrayList();
		
		if (ids != null) {						
			for (int i = 0; i < ids.length; i++){
				if (this.cm.canDelete(Integer.parseInt(ids[i]))) {
					int categoryId = Integer.parseInt(ids[i]);
					this.cm.delete(categoryId);
					
					Category c = new Category();
					c.setId(categoryId);
					
					ForumRepository.removeCategory(c);
				}
				else {
					errors.add(I18n.getMessage(I18n.CANNOT_DELETE_CATEGORY, new Object[] {new Integer(ids[i])}));
				}
			}
		}

		if (errors.size() > 0) {
			this.context.put("errorMessage", errors);
		}
		
		this.list();
	}
	
	// A new one
	public void insertSave() throws Exception
	{
		Category c = new Category();
		c.setName(this.request.getParameter("category_name"));
			
		int categoryId = this.cm.addNew(c);
		c.setId(categoryId);

		ForumRepository.addCategory(c);
		
		String[] groups = this.request.getParameterValues("groups");
		if (groups != null) {
			GroupModel gm = DataAccessDriver.getInstance().newGroupModel();
			GroupSecurityModel gmodel = DataAccessDriver.getInstance().newGroupSecurityModel();
			PermissionControl pc = new PermissionControl();
			pc.setSecurityModel(gmodel);

			Role role = new Role();
			role.setName(SecurityConstants.PERM_CATEGORY);

			for (int i = 0; i < groups.length; i++) {
				int groupId = Integer.parseInt(groups[i]);
				RoleValueCollection roleValues = new RoleValueCollection();
				
				RoleValue rv = new RoleValue();
				rv.setType(PermissionControl.ROLE_ALLOW);
				rv.setValue(Integer.toString(categoryId));
				
				roleValues.add(rv);
				
				pc.addRole(groupId, role, roleValues);
				
				Iterator iter = gm.selectUsersIds(groupId).iterator();
				while (iter.hasNext()) {
					SecurityRepository.remove(Integer.parseInt(iter.next().toString()));
				}
			}
		}
			
		this.list();
	}
	
	public void up() throws Exception
	{
		this.processOrdering(true);
	}
	
	public void down() throws Exception
	{
		this.processOrdering(false);
	}
	
	private void processOrdering(boolean up) throws Exception
	{
		Category toChange = new Category(ForumRepository.getCategory(Integer.parseInt(
				this.request.getParameter("category_id"))));
		
		List categories = ForumRepository.getAllCategories();
		
		int index = categories.indexOf(toChange);
		if (index == -1 || (up && index == 0) || (!up && index + 1 == categories.size())) {
			this.list();
			return;
		}
		
		if (up) {
			// Get the category which comes *before* the category we want to change
			Category otherCategory = new Category((Category)categories.get(index - 1));
			this.cm.setOrderUp(toChange, otherCategory);
		}
		else {
			// Get the category which comes *after* the category we want to change
			Category otherCategory = new Category((Category)categories.get(index + 1));
			this.cm.setOrderDown(toChange, otherCategory);
		}
		
		ForumRepository.reloadCategory(toChange);
		this.list();
	}
	
	/** 
	 * @see net.jforum.Command#process()
	 */
	public Template process(ActionServletRequest request, 
			HttpServletResponse response, 
			Connection conn, SimpleHash context) throws Exception 
	{
		if (AdminAction.isAdmin()) {
			this.cm = DataAccessDriver.getInstance().newCategoryModel();
			super.process(request, response, conn, context);
		}

		return AdminAction.adminBaseTemplate();
	}

}
