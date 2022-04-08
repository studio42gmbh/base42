/*
 * The MIT License
 * 
 * Copyright 2021 Studio 42 GmbH (https://www.s42m.de).
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
package de.s42.base.beans;

import de.s42.base.conversion.ConversionHelper;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.json.JSONObject;

/**
 *
 * @author Benjamin Schiller
 */
public final class BeanHelper
{

	private final static Map<Class, Map<String, Method>> readPropertyMap = new HashMap();
	private final static Map<Class, Map<String, Method>> writePropertyMap = new HashMap();

	private BeanHelper()
	{
		// never instantiated
	}

	public static Map<String, Method> getReadProperties(Object object) throws IntrospectionException
	{
		assert object != null;

		return getReadProperties(object.getClass());
	}

	public static Map<String, Method> getReadProperties(Class objectClass) throws IntrospectionException
	{
		assert objectClass != null;

		Map<String, Method> readProperties = readPropertyMap.get(objectClass);

		if (readProperties != null) {

			return readProperties;
		}

		synchronized (BeanHelper.class) {

			readProperties = new HashMap();
			BeanInfo beanInfo = Introspector.getBeanInfo(objectClass);
			for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {

				Method readMethod = desc.getReadMethod();
				if (readMethod != null) {
					readProperties.put(desc.getName(), readMethod);
				}
			}

			readPropertyMap.put(objectClass, readProperties);

			return readProperties;
		}
	}

	public static Map<String, Method> getWriteProperties(Object object) throws IntrospectionException
	{
		assert object != null;

		return getWriteProperties(object.getClass());
	}

	public static Map<String, Method> getWriteProperties(Class objectClass) throws IntrospectionException
	{
		assert objectClass != null;

		Map<String, Method> writeProperties = writePropertyMap.get(objectClass);

		if (writeProperties != null) {

			return writeProperties;
		}

		synchronized (BeanHelper.class) {

			writeProperties = new HashMap();
			BeanInfo beanInfo = Introspector.getBeanInfo(objectClass);
			for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {

				Method writeMethod = desc.getWriteMethod();
				if (writeMethod != null) {
					writeProperties.put(desc.getName(), writeMethod);
				}
			}

			writePropertyMap.put(objectClass, writeProperties);

			return writeProperties;
		}
	}

	public static boolean hasReadProperty(Object object, String property) throws IntrospectionException
	{
		assert object != null;
		assert property != null;

		return getReadProperties(object).containsKey(property);
	}

	public static Set<String> getReadPropertyNames(Object object) throws IntrospectionException
	{
		assert object != null;

		return getReadProperties(object).keySet();
	}

	public static <T extends Object> T readProperty(Object object, String property) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		assert object != null;
		assert property != null;

		Map<String, Method> readProperties = getReadProperties(object);

		Method readMethod = readProperties.get(property);

		if (readMethod == null) {
			throw new IllegalArgumentException("Read property " + property + " not found");
		}

		return (T) readMethod.invoke(object);
	}

	public static boolean hasWriteProperty(Object object, String property) throws IntrospectionException
	{
		assert object != null;
		assert property != null;

		return getWriteProperties(object).containsKey(property);
	}

	public static Set<String> getWritePropertyNames(Object object) throws IntrospectionException
	{
		assert object != null;

		return getWriteProperties(object).keySet();
	}

	public static void writeProperty(Object object, String property, Object value) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		assert object != null;
		assert property != null;

		Map<String, Method> writeProperties = getWriteProperties(object);

		Method writeMethod = writeProperties.get(property);

		if (writeMethod == null) {
			throw new IllegalArgumentException("Write property " + property + " not found in object of class " + object.getClass().getName());
		}

		try {
			writeMethod.invoke(object, (Object) ConversionHelper.convert(value, writeMethod.getParameterTypes()[0]));
		} catch (ClassCastException | IllegalArgumentException ex) {

			if (value != null) {
				throw new IllegalArgumentException(
					"Error writing " + property + " "
					+ writeMethod.getParameterTypes()[0] + " " + writeMethod.getParameterTypes()[0].hashCode() + " "
					+ value.getClass() + " " + value.getClass().hashCode(), ex);
			} else {
				throw new IllegalArgumentException(
					"Error writing " + property + " "
					+ writeMethod.getParameterTypes()[0] + " " + writeMethod.getParameterTypes()[0].hashCode() + " null", ex);
			}
		}
	}

	public static String toJSON(Object bean)
	{
		JSONObject obj = new JSONObject(bean);

		return obj.toString();
	}
}
