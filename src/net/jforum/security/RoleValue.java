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
 * This file creation date: 08/01/2004 / 21:38:57
 * net.jforum.security.RoleValue.java
 * The JForum Project
 * http://www.jforum.net
 * 
 * $Id: RoleValue.java,v 1.2 2004/04/21 23:57:36 rafaelsteil Exp $
 */
package net.jforum.security;

/**
 * @author Rafael Steil
 */
public class RoleValue 
{
	private int roleId;
	private String value;
	private int type = PermissionControl.ROLE_DENY;
	
	public void setRoleId(int roleId)
	{
		this.roleId = roleId;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public int getRoleId()
	{
		return this.roleId;
	}
	
	public String getValue()
	{
		return this.value;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) 
	{
		if (!(o instanceof RoleValue)) {
			return false;
		}
		
		RoleValue rv = (RoleValue)o;
		return (rv.getValue().equals(this.value) && rv.getType() == this.type);
	}

	/* 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() 
	{
		return (this.value + this.type).hashCode();
	}

}
