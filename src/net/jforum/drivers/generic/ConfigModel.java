/*
 * Copyright (c) 2003, Rafael Steil
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
 * Created on Dec 29, 2004 1:29:52 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.drivers.generic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.jforum.JForum;
import net.jforum.entities.Config;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: ConfigModel.java,v 1.1 2004/12/29 17:18:40 rafaelsteil Exp $
 */
public class ConfigModel implements net.jforum.model.ConfigModel
{
	private Connection conn;
	
	public ConfigModel()
	{
		this.conn = JForum.getConnection();
	}
	
	public ConfigModel(Connection conn)
	{
		this.conn = conn;
	}
	
	/**
	 * @see net.jforum.model.ConfigModel#insert(net.jforum.entities.Config)
	 */
	public void insert(Config config) throws Exception
	{
		PreparedStatement p = this.conn.prepareStatement(SystemGlobals.getSql("ConfigModel.insert"));
		p.setString(1, config.getName());
		p.setString(2, config.getValue());
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.model.ConfigModel#update(net.jforum.entities.Config)
	 */
	public void update(Config config) throws Exception
	{
		PreparedStatement p = this.conn.prepareStatement(SystemGlobals.getSql("ConfigModel.update"));
		p.setString(1, config.getValue());
		p.setString(2, config.getName());
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.model.ConfigModel#delete(net.jforum.entities.Config)
	 */
	public void delete(Config config) throws Exception
	{
		PreparedStatement p = this.conn.prepareStatement(SystemGlobals.getSql("ConfigModel.delete"));
		p.setInt(1, config.getId());
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.model.ConfigModel#selectAll()
	 */
	public List selectAll() throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = this.conn.prepareStatement(SystemGlobals.getSql("ConfigModel.selectAll"));
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.makeConfig(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}

	/**
	 * @see net.jforum.model.ConfigModel#selectByName(java.lang.String)
	 */
	public Config selectByName(String name) throws Exception
	{
		PreparedStatement p = this.conn.prepareStatement(SystemGlobals.getSql("ConfigModel.selectByName"));
		p.setString(1, name);
		ResultSet rs = p.executeQuery();
		Config c = null;
		
		if (rs.next()) {
			c = this.makeConfig(rs);
		}
		
		rs.close();
		p.close();
		
		return c;
	}
	
	protected Config makeConfig(ResultSet rs) throws Exception
	{
		Config c = new Config();
		c.setId(rs.getInt("config_id"));
		c.setName(rs.getString("config_name"));
		c.setValue(rs.getString("config_value"));
		
		return c;
	}
}