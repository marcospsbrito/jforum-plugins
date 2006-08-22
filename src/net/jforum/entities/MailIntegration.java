/*
 * Created on 21/08/2006 21:23:29
 */
package net.jforum.entities;

/**
 * @author Rafael Steil
 * @version $Id: MailIntegration.java,v 1.1 2006/08/22 02:05:24 rafaelsteil Exp $
 */
public class MailIntegration
{
	private int forumId;
	private int popPort;
	private String popHost;
	private String popUsername;
	private String popPassword;
	
	/**
	 * @return the forumId
	 */
	public int getForumId()
	{
		return this.forumId;
	}
	
	/**
	 * @return the popHost
	 */
	public String getPopHost()
	{
		return this.popHost;
	}
	
	/**
	 * @return the popPassword
	 */
	public String getPopPassword()
	{
		return this.popPassword;
	}
	
	/**
	 * @return the popPort
	 */
	public int getPopPort()
	{
		return this.popPort;
	}
	
	/**
	 * @return the popUsername
	 */
	public String getPopUsername()
	{
		return this.popUsername;
	}
	
	/**
	 * @param forumId the forumId to set
	 */
	public void setForumId(int forumId)
	{
		this.forumId = forumId;
	}
	
	/**
	 * @param popHost the popHost to set
	 */
	public void setPopHost(String popHost)
	{
		this.popHost = popHost;
	}
	
	/**
	 * @param popPassword the popPassword to set
	 */
	public void setPopPassword(String popPassword)
	{
		this.popPassword = popPassword;
	}
	
	/**
	 * @param popPort the popPort to set
	 */
	public void setPopPort(int popPort)
	{
		this.popPort = popPort;
	}
	
	/**
	 * @param popUsername the popUsername to set
	 */
	public void setPopUsername(String popUsername)
	{
		this.popUsername = popUsername;
	}
}
