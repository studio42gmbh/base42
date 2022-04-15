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
package de.s42.base.strings;

import de.s42.base.beans.BeanHelper;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Benjamin Schiller
 */
public final class StringHelper
{

	private StringHelper()
	{
		// never instantiated
	}

	public static String lowerCaseFirst(String transform)
	{
		assert transform != null;

		return transform.substring(0, 1).toLowerCase() + transform.substring(1);
	}

	public static String toString(Object object)
	{
		try {
			assert object != null;

			//return BeanHelper.toJSON(bean);
			StringBuilder builder = new StringBuilder();

			builder.append(object.getClass().getName());

			if (BeanHelper.hasReadProperty(object, "name")) {
				builder
					.append(" ")
					.append((Object) BeanHelper.readProperty(object, "name"));
			}

			builder.append(" {");

			for (String name : BeanHelper.getReadPropertyNames(object)) {

				if (name.equals("name")
					|| name.equals("class")) {
					continue;
				}

				Object val = BeanHelper.readProperty(object, name);
				
				if (val == null) {
					continue;
				}
				
				builder
					.append(" ")
					.append(name)
					.append(" : ");

				if (val instanceof String) {
					builder
						.append("\"")
						.append(val)
						.append("\"");
				} else {
					builder
						.append(val);
				}

				builder
					.append(";");
			}

			builder.append(" }");

			return builder.toString();
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException("Error stringifying - " + ex.getMessage(), ex);
		}
	}
}
