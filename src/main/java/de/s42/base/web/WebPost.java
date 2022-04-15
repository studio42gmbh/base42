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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Benjamin Schiller
 */
public class WebPost
{

	protected URL url;

	protected final Map<String, Object> parameters = new HashMap<>();

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

	public WebPost(Map<String, Object> parameters)
	{
		assert parameters != null;

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

	public String perform() throws IOException
	{
		String boundary = UUID.randomUUID().toString();

		String type = "multipart/form-data; boundary=" + boundary;

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", type);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		for (Map.Entry<String, Object> entry : parameters.entrySet()) {

			os.write("\r\n--".getBytes());
			os.write(boundary.getBytes());
			os.write(("\r\nContent-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes());

			if (entry.getValue() instanceof byte[]) {
				os.write((byte[]) entry.getValue());
			} else {
				os.write(entry.getValue().toString().getBytes("UTF-8"));
			}
		}

		os.write("\r\n--".getBytes());
		os.write(boundary.getBytes());
		os.write("--\r\n\r\n".getBytes());

		byte[] dataToSend = os.toByteArray();

		conn.setRequestProperty("Content-Length", String.valueOf(dataToSend.length));

		OutputStream web = conn.getOutputStream();

		web.write(dataToSend);

		String callResult;
		try ( //retrieve result
			InputStream in = conn.getInputStream()) {
			BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			callResult = "";
			String line = r.readLine();
			while (line != null) {
				if (line.length() > 0) {
					callResult += line + "\n";
				}
				line = r.readLine();
			}
		}

		conn.disconnect();

		return callResult;
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
