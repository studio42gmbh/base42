// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2023 Studio 42 GmbH ( https://www.s42m.de ).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//</editor-fold>
package de.s42.base.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

/**
 *
 * @author Benjamin Schiller
 */
public class WebBuilder
{

	protected final Web web;

	protected WebBuilder()
	{
		web = new Web();
	}

	public static WebBuilder build()
	{
		return new WebBuilder();
	}

	public static WebBuilder delete(String... urlParts) throws MalformedURLException
	{
		return build().delete().url(String.join("/", urlParts));
	}

	public static WebBuilder get(String... urlParts) throws MalformedURLException
	{
		return build().get().url(String.join("/", urlParts));
	}

	public static WebBuilder post(String... urlParts) throws MalformedURLException
	{
		return build().post().url(String.join("/", urlParts));
	}

	public static WebBuilder postJSON(String... urlParts) throws MalformedURLException
	{
		return build().postJSON().url(String.join("/", urlParts));
	}

	public Web create()
	{
		return web;
	}

	public WebResult perform() throws IOException
	{
		return web.perform();
	}

	public WebBuilder url(String url) throws MalformedURLException
	{
		web.setUrl(URI.create(url).toURL());
		return this;
	}

	public WebBuilder url(URL url)
	{
		web.setUrl(url);
		return this;
	}

	public WebBuilder delete()
	{
		web.setMethod(WebMethod.DELETE);
		return this;
	}

	public WebBuilder get()
	{
		web.setMethod(WebMethod.GET);
		return this;
	}

	public WebBuilder post()
	{
		return postMultipart();
	}

	public WebBuilder postJSON()
	{
		web.setMethod(WebMethod.POST);
		web.setPostType(WebPostType.JSON);
		return this;
	}

	public WebBuilder postMultipart()
	{
		web.setMethod(WebMethod.POST);
		web.setPostType(WebPostType.MULTIPART);
		return this;
	}

	public WebBuilder parameter(String key, Object value)
	{
		Objects.requireNonNull(value);

		web.setParameter(key, value);
		return this;
	}

	public WebBuilder optParameter(String key, Object value)
	{
		if (value == null) {
			return this;
		}

		web.setParameter(key, value);
		return this;
	}

	public WebBuilder header(String key, String value)
	{
		Objects.requireNonNull(value);

		web.setHeader(key, value);
		return this;
	}

	public WebBuilder optHeader(String key, String value)
	{
		if (value == null) {
			return this;
		}

		web.setHeader(key, value);
		return this;
	}

	public WebBuilder headerAccept(String value)
	{
		web.setHeader("Accept", value);
		return this;
	}

	public WebBuilder headerBearerToken(String token)
	{
		web.setHeader("Authorization", "Bearer " + token);
		return this;
	}

	public WebBuilder headerCacheControl(String control)
	{
		web.setHeader("Cache-Control", control);
		return this;
	}

	public WebBuilder headerCacheControlNoCache()
	{
		web.setHeader("Cache-Control", "no-cache");
		return this;
	}

	public WebBuilder headerContentType(String contentType)
	{
		web.setHeader("Content-Type", contentType);
		return this;
	}
}
