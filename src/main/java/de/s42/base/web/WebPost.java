// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2022 Studio 42 GmbH ( https://www.s42m.de ).
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;

/**
 * Is deprecated Web and WebBuilder shoule be used instead
 *
 * @author Benjamin Schiller
 */
@Deprecated
public class WebPost
{

	protected URL url;

	protected final Map<String, Object> parameters = new HashMap<>();

	// See https://stackoverflow.com/questions/16150089/how-to-handle-cookies-in-httpurlconnection-using-cookiemanager
	static {
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public WebPost()
	{

	}

	public WebPost(String url) throws MalformedURLException
	{
		assert url != null;

		this.url = new URL(url);
	}

	public WebPost(URL url)
	{
		assert url != null;

		this.url = url;
	}

	public WebPost(String url, Map<String, Object> parameters) throws MalformedURLException
	{
		assert parameters != null;
		assert url != null;

		this.url = new URL(url);
		this.parameters.putAll(parameters);
	}

	public WebPost(URL url, Map<String, Object> parameters)
	{
		assert parameters != null;
		assert url != null;

		this.url = url;
		this.parameters.putAll(parameters);
	}

	public void addParameter(String key, Object value)
	{
		assert key != null;
		assert value != null;

		parameters.put(key, value);
	}

	public void removeParameter(String key)
	{
		assert key != null;

		parameters.remove(key);
	}

	public WebPostResult perform() throws IOException
	{
		String boundary = UUID.randomUUID().toString();

		String type = "multipart/form-data; boundary=" + boundary;

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//conn.setDefaultUseCaches(true);
		//conn.setUseCaches(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", type);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		for (Map.Entry<String, Object> entry : parameters.entrySet()) {

			os.write("\r\n--".getBytes());
			os.write(boundary.getBytes());
			os.write(("\r\nContent-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes());

			if (entry.getValue() instanceof byte[] bs) {
				os.write(bs);
			} else {
				os.write(entry.getValue().toString().getBytes("UTF-8"));
			}
		}

		os.write("\r\n--".getBytes());
		os.write(boundary.getBytes());
		os.write("--\r\n\r\n".getBytes());

		byte[] dataToSend = os.toByteArray();

		conn.setRequestProperty("Content-Length", String.valueOf(dataToSend.length));

		OutputStream out = conn.getOutputStream();
		out.write(dataToSend);

		int statusCode = conn.getResponseCode();

		String callResult;
		InputStream in;

		if (statusCode >= 100 && statusCode <= 399) {
			in = conn.getInputStream();
		} else {
			in = conn.getErrorStream();
		}

		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for (int length; (length = in.read(buffer)) != -1;) {
			result.write(buffer, 0, length);
		}
		callResult = result.toString("UTF-8");

		in.close();

		conn.disconnect();

		// Allow an empty return -> leads to an enpty object
		if (callResult == null || callResult.isBlank()) {
			return new WebPostResult(statusCode, new JSONObject());
		}

		return new WebPostResult(statusCode, new JSONObject(callResult));
	}

	public URL getUrl()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}
}
