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

import de.s42.base.strings.StringHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Benjamin Schiller
 */
public class Web
{

	protected WebMethod method = WebMethod.GET;

	protected WebPostType postType = WebPostType.MULTIPART;

	protected URL url;

	protected final Map<String, Object> parameters = new HashMap<>();

	protected final Map<String, String> headers = new HashMap<>();

	// See https://stackoverflow.com/questions/16150089/how-to-handle-cookies-in-httpurlconnection-using-cookiemanager
	static {
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public Web()
	{

	}

	public Web(String url) throws MalformedURLException
	{
		assert url != null : "url != null";

		this.url = new URL(url);
	}

	public Web(URL url)
	{
		assert url != null : "url != null";

		this.url = url;
	}

	public Web(String url, Map<String, Object> parameters) throws MalformedURLException
	{
		assert parameters != null : "parameters != null";
		assert url != null : "url != null";

		this.url = new URL(url);
		this.parameters.putAll(parameters);
	}

	public Web(URL url, Map<String, Object> parameters)
	{
		assert parameters != null : "parameters != null";
		assert url != null : "url != null";

		this.url = url;
		this.parameters.putAll(parameters);
	}

	public static void activateGlobalCookieManager()
	{
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public static void deactivateGlobalCookieManager()
	{
		CookieHandler.setDefault(null);
	}

	protected WebResult post() throws ProtocolException, IOException
	{
		if (postType == WebPostType.MULTIPART) {
			return postMultiPart();
		} else if (postType == WebPostType.JSON) {
			return postJSON();
		}

		throw new IllegalArgumentException("Unhandled method " + method);
	}

	protected WebResult postJSON() throws ProtocolException, IOException
	{
		assert url != null : "url != null";

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");

		for (Map.Entry<String, String> header : headers.entrySet()) {
			conn.setRequestProperty(header.getKey(), header.getValue());
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		JSONObject data = new JSONObject(parameters);

		os.write(data.toString().getBytes());

		byte[] dataToSend = os.toByteArray();

		conn.setRequestProperty("Content-Length", String.valueOf(dataToSend.length));

		try (OutputStream out = conn.getOutputStream()) {
			out.write(dataToSend);
			out.flush();
		}

		return getRepsonse(conn);
	}

	protected WebResult postMultiPart() throws ProtocolException, IOException
	{
		assert url != null : "url != null";

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		String boundary = UUID.randomUUID().toString();

		String type = "multipart/form-data; boundary=" + boundary;

		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", type);

		for (Map.Entry<String, String> header : headers.entrySet()) {
			conn.setRequestProperty(header.getKey(), header.getValue());
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		for (Map.Entry<String, Object> entry : parameters.entrySet()) {

			os.write("\r\n--".getBytes());
			os.write(boundary.getBytes());
			os.write(("\r\nContent-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes());

			if (entry.getValue() instanceof byte[] bs) {
				os.write(bs);
			} else {
				os.write(entry.getValue().toString().getBytes(StandardCharsets.UTF_8));
			}
		}

		os.write("\r\n--".getBytes());
		os.write(boundary.getBytes());
		os.write("--\r\n\r\n".getBytes());

		byte[] dataToSend = os.toByteArray();

		conn.setRequestProperty("Content-Length", String.valueOf(dataToSend.length));

		try (OutputStream out = conn.getOutputStream()) {
			out.write(dataToSend);
			out.flush();
		}

		return getRepsonse(conn);
	}

	protected WebResult read() throws ProtocolException, IOException
	{
		assert method != null : "method != null";
		assert url != null : "url != null";
		assert method != null : "method != null";

		URL getUrl;

		if (!parameters.isEmpty()) {

			StringBuilder getUrlString = new StringBuilder(url.toExternalForm());
			getUrlString.append("?");

			boolean first = true;
			for (Map.Entry<String, Object> parameter : parameters.entrySet()) {

				if (!first) {
					getUrlString.append("&");
				}

				getUrlString.append(parameter.getKey()).append("=").append(parameter.getValue().toString());

				first = false;
			}

			getUrl = new URL(getUrlString.toString());
		} else {
			getUrl = url;
		}

		HttpURLConnection conn = (HttpURLConnection) getUrl.openConnection();

		conn.setRequestMethod(method.toString());

		//conn.setDefaultUseCaches(true);
		//conn.setUseCaches(true);
		for (Map.Entry<String, String> header : headers.entrySet()) {
			conn.setRequestProperty(header.getKey(), header.getValue());
		}

		return getRepsonse(conn);
	}

	protected WebResult getRepsonse(HttpURLConnection conn) throws IOException
	{
		assert conn != null : "conn != null";

		int statusCode = conn.getResponseCode();

		String callResult;
		InputStream in;

		if (statusCode >= 100 && statusCode <= 399) {
			in = conn.getInputStream();
		} else {
			in = conn.getErrorStream();
		}

		if (in != null) {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			for (int length; (length = in.read(buffer)) != -1;) {
				result.write(buffer, 0, length);
			}
			callResult = result.toString(StandardCharsets.UTF_8);
			in.close();
		} else {
			callResult = "";
		}

		conn.disconnect();

		// Allow an empty return -> leads to an enpty object
		if (callResult == null || callResult.isBlank()) {
			return new WebResult(statusCode, new JSONObject());
		}

		// Check if it is an JSON array
		if (callResult.trim().startsWith("[")) {
			return new WebResult(statusCode, new JSONArray(callResult));
		}

		// Return a JSON object
		if (callResult.trim().startsWith("{")) {
			return new WebResult(statusCode, new JSONObject(callResult));
		}

		return new WebResult(statusCode, callResult);
	}

	public WebResult perform() throws IOException
	{
		if (null != method) {
			switch (method) {
				case POST -> {
					return post();
				}
				case GET -> {
					return read();
				}
				case DELETE -> {
					return read();
				}
				case OPTIONS -> {
					return read();
				}
			}
		}

		throw new IllegalArgumentException("Unhandled method " + method);
	}

	public void setParameter(String key, Object value)
	{
		// Ignore null parameters
		if (value == null) {
			return;
		}

		assert key != null : "key != null";

		parameters.put(key, value);
	}

	public void removeParameter(String key)
	{
		assert key != null : "key != null";

		parameters.remove(key);
	}

	public void setHeader(String key, String value)
	{
		// Ignore null parameters
		if (value == null) {
			return;
		}

		assert key != null : "key != null";

		headers.put(key, value);
	}

	public void removeHeader(String key)
	{
		assert key != null : "key != null";

		headers.remove(key);
	}

	public URL getUrl()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		assert url != null : "url != null";

		this.url = url;
	}

	public WebMethod getMethod()
	{
		return method;
	}

	public void setMethod(WebMethod method)
	{
		assert method != null : "method != null";

		this.method = method;
	}

	public WebPostType getPostType()
	{
		return postType;
	}

	public void setPostType(WebPostType postType)
	{
		assert postType != null : "postType != null";

		this.postType = postType;
	}

	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}

	public Map<String, Object> getParameters()
	{
		return Collections.unmodifiableMap(parameters);
	}

	public Map<String, String> getHeaders()
	{
		return Collections.unmodifiableMap(headers);
	}
}
