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
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Benjamin Schiller
 */
public class WebResult
{

	protected int statusCode;
	protected Object response;

	public WebResult()
	{

	}

	public WebResult(int statusCode, Object response)
	{
		assert response != null : "response != null";
		assert statusCode >= 100 : "statusCode >= 100";

		this.statusCode = statusCode;
		this.response = response;
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(int statusCode)
	{
		assert statusCode >= 100 : "statusCode >= 100";

		this.statusCode = statusCode;
	}

	public Object getResponse()
	{
		return response;
	}

	public JSONObject getResponseAsObject()
	{
		return (JSONObject) response;
	}

	public JSONArray getResponseAsArray()
	{
		return (JSONArray) response;
	}

	public void setResponse(Object response)
	{
		assert response != null : "response != null";

		this.response = response;
	}

	public boolean isSuccess()
	{
		return !isError();
	}

	public boolean isError()
	{
		return statusCode > 399;
	}

	@Override
	public String toString()
	{
		return StringHelper.toString(this, Set.of("responseAsObject", "responseAsArray"));
	}
}
