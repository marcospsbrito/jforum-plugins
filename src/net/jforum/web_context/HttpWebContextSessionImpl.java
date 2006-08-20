/*
 * Copyright (c) 2006, Rafael Steil
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
 *
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.web_context;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * User: SergeMaslyukov
 * Date: 20.08.2006
 * Time: 18:42:11
 * <p/>
 * $Id: HttpWebContextSessionImpl.java,v 1.1 2006/08/20 15:30:29 sergemaslyukov Exp $
 */
public class HttpWebContextSessionImpl implements WebContextSession
{
    private HttpSession httpSession=null;

    public HttpWebContextSessionImpl(HttpSession httpSession)
    {
        this.httpSession = httpSession;
    }

    public void setAttribute(String name, Object value)
    {
        httpSession.setAttribute(name, value);
    }

    public void removeAttribute(String name)
    {
        httpSession.removeAttribute(name);
    }

    public Object getAttribute(String name)
    {
        return httpSession.getAttribute(name);
    }

    public String getId()
    {
        return httpSession.getId();
    }

    public Enumeration getAttributeNames()
    {
        return httpSession.getAttributeNames();
    }

    public void invalidate()
    {
        httpSession.invalidate();
    }
}
