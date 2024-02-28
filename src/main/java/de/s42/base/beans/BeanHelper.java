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
package de.s42.base.beans;

import java.util.*;
import org.json.JSONObject;

/**
 *
 * @author Benjamin Schiller
 */
public final class BeanHelper
{

	private final static Map<Class, BeanInfo> infos = Collections.synchronizedMap(new HashMap<>());

	private BeanHelper()
	{
		// never instantiated
	}

	@SuppressWarnings("unchecked")
	public static <BeanType> BeanInfo<BeanType> getBeanInfo(Class<? extends BeanType> beanClass) throws InvalidBean
	{
		assert beanClass != null;

		BeanInfo<BeanType> info = infos.get(beanClass);

		if (info != null) {
			return info;
		}

		info = new BeanInfo(beanClass);

		infos.put(beanClass, info);

		return info;
	}

	public static String toJSON(Object bean)
	{
		assert bean != null;

		JSONObject obj = new JSONObject(bean);

		return obj.toString();
	}
}
