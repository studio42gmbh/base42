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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

		// Date
		addWriter(Date.class, (Date value, StringBuilder builder) -> {
			builder.append(value.getTime());
		});

		// String
		addWriter(String.class, (String value, StringBuilder builder) -> {
			builder.append("\"").append(escapeJavaString(value)).append("\"");
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

	public static String fromCamelCaseToWords(String camelCase)
	{
		assert camelCase != null : "camelCase != null";

		String human = camelCase.replaceAll("([A-Z])", " $0").trim();

		return StringHelper.upperCaseFirst(human);
	}

	public static boolean isLowerCaseFirst(String compare)
	{
		assert compare != null;

		if (compare.isBlank()) {
			return false;
		}

		return Character.isLowerCase(compare.charAt(0));
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
		if (object == null) {
			return "null";
		}

		if (object instanceof String string) {
			return "\"" + string + "\"";
		} else if (object instanceof Number number) {
			return number.toString();
		} else if (object.getClass().isPrimitive()) {
			return object.toString();
		} else if (object.getClass().isArray()) {
			return toString((Object[]) object);
		}

		return toString(object, Collections.EMPTY_SET);
	}

	public static String toString(Object[] array)
	{
		assert array != null : "array != null";

		StringBuilder builder = new StringBuilder();

		builder.append("[");

		for (int i = 0; i < array.length; ++i) {

			Object object = array[i];
			builder.append(toString(object));
			if (i < array.length - 1) {
				builder.append(",");
			}
		}

		builder.append("]");

		return builder.toString();
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

	@SuppressWarnings("unchecked")
	public static String toStringStrict(Object object, Set<String> properties)
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
					|| !properties.contains(propertyName)) {
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

	public static String escapeJavaString(String content)
	{
		assert content != null;

		// @todo This might get more sophisticated for edge cases with encoding for example but should do most of the cases
		return content.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"");
	}

	public static String unescapeJavaString(String content)
	{
		assert content != null;

		StringBuilder builder = new StringBuilder(content.length());

		for (int i = 0; i < content.length(); i++) {

			char ch = content.charAt(i);

			// Found escape back slash
			if (ch == '\\') {

				char nextChar = (i == content.length() - 1) ? '\\' : content
					.charAt(i + 1);

				// Octal escape
				if (nextChar >= '0' && nextChar <= '7') {
					String code = "" + nextChar;
					i++;
					if ((i < content.length() - 1) && content.charAt(i + 1) >= '0'
						&& content.charAt(i + 1) <= '7') {
						code += content.charAt(i + 1);
						i++;
						if ((i < content.length() - 1) && content.charAt(i + 1) >= '0'
							&& content.charAt(i + 1) <= '7') {
							code += content.charAt(i + 1);
							i++;
						}
					}
					builder.append((char) Integer.parseInt(code, 8));
					continue;
				}

				// Progress
				switch (nextChar) {
					case '\\' ->
						ch = '\\';
					case 'b' ->
						ch = '\b';
					case 'f' ->
						ch = '\f';
					case 'n' ->
						ch = '\n';
					case 'r' ->
						ch = '\r';
					case 't' ->
						ch = '\t';
					case '\"' ->
						ch = '\"';
					case '\'' ->
						ch = '\'';
					case 'u' -> {
						if (i >= content.length() - 5) {
							ch = 'u';
							break;
						}

						int code = Integer.parseInt(
							"" + content.charAt(i + 2) + content.charAt(i + 3)
							+ content.charAt(i + 4) + content.charAt(i + 5), 16);
						builder.append(Character.toChars(code));
						i += 5;
						continue;
					}
				}
				// Hex Unicode: u????
				i++;
			}
			builder.append(ch);
		}
		return builder.toString();
	}

	/**
	 * Inserts a string at the given index into another
	 *
	 * @param string
	 * @param insert
	 * @param index
	 * @return
	 */
	public static String insert(String string, String insert, int index)
	{
		Objects.requireNonNull(string, "string != null");
		Objects.requireNonNull(insert, "insert != null");
		Objects.checkIndex(index, string.length() + 1);

		if (string.isEmpty()) {
			return insert;
		}

		if (insert.isEmpty()) {
			return string;
		}

		StringBuilder combined = new StringBuilder(string);
		combined.insert(index, insert);
		return combined.toString();
	}

	/**
	 * Removes length characters beginning at index from string
	 *
	 * @param string
	 * @param index
	 * @param length
	 * @return
	 */
	public static String remove(String string, int index, int length)
	{
		Objects.requireNonNull(string, "string != null");
		Objects.checkIndex(index, string.length() + 1);

		if (string.isEmpty() || index == string.length()) {
			return string;
		}

		int end = index + length;

		// Just return first part as the end is equal or behind end segment
		if (end >= string.length()) {
			return string.substring(0, index);
		}

		StringBuilder combined = new StringBuilder(string.substring(0, index));
		combined.append(string.substring(end, string.length()));

		return combined.toString();
	}

	public static String formatNumber(float number, int numberPrecision)
	{
		return formatNumber((double) number, numberPrecision);
	}

	public static String formatNumber(double number, int numberPrecision)
	{
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator('.');
		symbols.setDecimalSeparator(',');

		DecimalFormat format = new DecimalFormat(
			"#,##0" + ((numberPrecision > 0) ? "." : "") + "0".repeat(numberPrecision),
			symbols
		);

		return format.format(number);
	}
}
