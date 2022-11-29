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
import de.s42.base.beans.BeanInfo;
import de.s42.base.beans.BeanProperty;
import de.s42.base.beans.InvalidBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 *
 * @author Benjamin Schiller
 */
public final class StringHelper
{

	private final static Map<Class, BiConsumer<?, StringBuilder>> writers = Collections.synchronizedMap(new HashMap<>());

	static {

		// UUID
		addWriter(UUID.class, (UUID value, StringBuilder builder) -> {
			builder.append("\"").append(value).append("\"");
		});

		// String
		addWriter(String.class, (String value, StringBuilder builder) -> {
			builder.append("\"").append(value).append("\"");
		});

		// String[]
		addWriter(String[].class, (String[] value, StringBuilder builder) -> {
			write(value, builder);
		});

		// Class
		addWriter(Class.class, (Class value, StringBuilder builder) -> {
			builder.append(value.getName());
		});

		// List
		addWriter(List.class, (List value, StringBuilder builder) -> {
			write(value, builder);
		});

		// ArrayList
		addWriter(ArrayList.class, (List value, StringBuilder builder) -> {
			write(value, builder);
		});

		// Collections.unmodifiableList
		addWriter(Collections.unmodifiableList(List.of()).getClass(), (List value, StringBuilder builder) -> {
			write(value, builder);
		});

		// Set
		addWriter(Set.class, (Set value, StringBuilder builder) -> {
			write(value, builder);
		});

		// HashSet
		addWriter(HashSet.class, (Set value, StringBuilder builder) -> {
			write(value, builder);
		});

		// Collections.unmodifiableSet
		addWriter(Collections.unmodifiableSet(Set.of()).getClass(), (Set value, StringBuilder builder) -> {
			write(value, builder);
		});
	}

	private StringHelper()
	{
		// never instantiated
	}

	public static void write(Object[] array, StringBuilder builder)
	{
		assert builder != null;

		if (array == null || array.length == 0) {
			builder.append("null");
			return;
		}

		for (int i = 0; i < array.length; ++i) {
			if (i > 0) {
				builder.append(", ");
			}
			write(array[i], builder);
		}
	}

	public static void write(Collection collection, StringBuilder builder)
	{
		assert builder != null;

		if (collection == null || collection.isEmpty()) {
			builder.append("null");
			return;
		}

		Iterator it = collection.iterator();
		boolean first = true;
		while (it.hasNext()) {
			if (!first) {
				builder.append(", ");
			}
			first = false;
			write(it.next(), builder);
		}
	}

	@SuppressWarnings("unchecked")
	public static void write(Object value, StringBuilder builder)
	{
		assert builder != null;

		if (value == null) {
			builder.append("null");
			return;
		}

		BiConsumer writer = getWriter(value.getClass());

		if (writer != null) {
			writer.accept(value, builder);
		} else {
			builder
				//.append("\"")
				.append(value);
			//.append("\"");
		}
	}

	public static BiConsumer<?, StringBuilder> getWriter(Class sourceClass)
	{
		assert sourceClass != null;

		return writers.get(sourceClass);
	}

	public synchronized static void addWriter(Class sourceClass, BiConsumer<?, StringBuilder> writer) throws RuntimeException
	{
		assert sourceClass != null;
		assert writer != null;

		if (writers.putIfAbsent(sourceClass, writer) != null) {
			throw new RuntimeException("Writer for class " + sourceClass.getName() + " is already mapped");
		}
	}

	public static String lowerCaseFirst(String transform)
	{
		assert transform != null;

		return transform.substring(0, 1).toLowerCase() + transform.substring(1);
	}

	public static String upperCaseFirst(String transform)
	{
		assert transform != null;

		return transform.substring(0, 1).toUpperCase() + transform.substring(1);
	}

	public static String toString(Class type, String name, String[] attributeNames, Object[] attributeValues)
	{
		assert type != null;
		assert attributeNames != null;
		assert attributeValues != null;
		assert attributeNames.length == attributeValues.length;

		if (attributeNames.length != attributeValues.length) {
			throw new RuntimeException("attributeNames.length != attributeValues.length");
		}

		StringBuilder builder = new StringBuilder();

		builder.append(type.getName());

		if (name != null) {
			builder.append(" ").append(name);
		}

		builder.append(" {");

		for (int i = 0; i < attributeNames.length; ++i) {

			String attributeName = attributeNames[i];
			Object attributeValue = attributeValues[i];

			builder
				.append(" ")
				.append(attributeName)
				.append(" : ");

			write(attributeValue, builder);

			builder.append(";");
		}

		if (attributeNames.length > 0) {
			builder.append(" }");
		} else {
			builder.append("}");
		}

		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	public static String toString(Object object)
	{
		return toString(object, Collections.EMPTY_SET);
	}

	@SuppressWarnings("unchecked")
	public static String toString(Object object, Set<String> ignoredProperties)
	{
		if (object == null) {
			return "null";
		}

		try {
			BeanInfo<?> info = BeanHelper.getBeanInfo(object.getClass());

			// @todo Optimize this quick and not so optimal copying solution for read properties
			List<BeanProperty> readProperties = new ArrayList<>(info.getReadProperties());
			List<String> attributeNames = new ArrayList<>(readProperties.size() - 1);
			List<Object> attributeValues = new ArrayList<>(readProperties.size() - 1);
			for (BeanProperty property : readProperties) {
				String propertyName = property.getName();
				if ("class".equals(propertyName)
					|| ignoredProperties.contains(propertyName)) {
					continue;
				}
				attributeNames.add(propertyName);
				attributeValues.add(property.read(object));
			}

			// @todo implement smart to string
			return toString(object.getClass(), null, attributeNames.toArray(String[]::new), attributeValues.toArray());
			//return toString(info.getBeanClass(), null, info.getReadPropertyNames(), attributeValues);
			//return object.getClass().getName() + "@" + object.hashCode();

		} catch (InvalidBean ex) {
			throw new RuntimeException("Error to string - " + ex.getMessage(), ex);
		}
	}
}
