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
package de.s42.base.conversion;

import de.s42.base.uuid.UUIDHelper;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Benjamin Schiller
 */
public final class ConversionHelper
{

	private ConversionHelper()
	{
	}

	private final static Map<Class, Map<Class, Function<?, ?>>> converters = Collections.synchronizedMap(new HashMap());
	private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	static {

		//byte[] -> UUID
		addConverter(byte[].class, UUID.class, (byte[] value) -> {
			return UUIDHelper.toUuid(value);
		});

		//String -> int
		addConverter(String.class, int.class, (String value) -> {
			return Integer.parseInt(value);
		});

		//String -> Integer
		addConverter(String.class, Integer.class, (String value) -> {
			return Integer.valueOf(value);
		});

		//String -> float
		addConverter(String.class, float.class, (String value) -> {
			return Float.parseFloat(value);
		});

		//String -> Float
		addConverter(String.class, Float.class, (String value) -> {
			return Float.valueOf(value);
		});

		//String -> double
		addConverter(String.class, double.class, (String value) -> {
			return Double.parseDouble(value);
		});

		//String -> Double
		addConverter(String.class, Double.class, (String value) -> {
			return Double.valueOf(value);
		});

		//String -> long
		addConverter(String.class, long.class, (String value) -> {
			return Long.parseLong(value);
		});

		//String -> Long
		addConverter(String.class, Long.class, (String value) -> {
			return Long.valueOf(value);
		});

		//String -> boolean
		addConverter(String.class, boolean.class, (String value) -> {
			return Boolean.parseBoolean(value);
		});

		//String -> Boolean
		addConverter(String.class, Boolean.class, (String value) -> {
			return Boolean.valueOf(value);
		});

		//String -> Class
		addConverter(String.class, Class.class, (String value) -> {
			try {
				return Class.forName(value);
			} catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException(ex.getMessage(), ex);
			}
		});

		//String -> UUID
		addConverter(String.class, UUID.class, (String value) -> {
			return UUID.fromString(value);
		});

		//String -> JSONObject
		addConverter(String.class, JSONObject.class, (String value) -> {
			try {
				return new JSONObject(value);
			} catch (JSONException ex) {
				throw new IllegalArgumentException(ex.getMessage(), ex);
			}
		});

		//String -> JSONArray
		addConverter(String.class, JSONArray.class, (String value) -> {
			try {
				return new JSONArray(value);
			} catch (JSONException ex) {
				throw new IllegalArgumentException(ex.getMessage(), ex);
			}
		});

		//String -> Pattern
		addConverter(String.class, Pattern.class, (String value) -> {
			return Pattern.compile(value);
		});

		//String -> Date
		addConverter(String.class, Date.class, (String value) -> {
			return new Date(Long.parseLong(value));
		});

		//String -> URL
		addConverter(String.class, URL.class, (String value) -> {
			try {
				return new URL(value);
			} catch (MalformedURLException ex) {
				throw new IllegalArgumentException(ex.getMessage(), ex);
			}
		});

		//String -> Path
		addConverter(String.class, Path.class, (String value) -> {
			return Path.of(value);
		});

		//Integer -> int
		addConverter(Integer.class, int.class, (Integer value) -> {
			return (int) value;
		});

		//Integer -> float
		addConverter(Integer.class, float.class, (Integer value) -> {
			return (float) value;
		});

		//Integer -> Float
		addConverter(Integer.class, Float.class, (Integer value) -> {
			return (float) value;
		});

		//Integer -> double
		addConverter(Integer.class, double.class, (Integer value) -> {
			return (double) value;
		});

		//Integer -> Double
		addConverter(Integer.class, Double.class, (Integer value) -> {
			return (double) value;
		});

		//Integer -> Date
		addConverter(Integer.class, Date.class, (Integer value) -> {
			return new Date((long) value);
		});

		//int -> String
		addConverter(int.class, String.class, (Integer value) -> {
			return value.toString();
		});

		//Integer -> String
		addConverter(Integer.class, String.class, (Integer value) -> {
			return value.toString();
		});

		//Long -> long
		addConverter(Long.class, long.class, (Long value) -> {
			return (long) value;
		});

		//Long -> int
		addConverter(Long.class, int.class, (Long value) -> {
			return value.intValue();
		});

		//Long -> Date
		addConverter(Long.class, Date.class, (Long value) -> {
			return new Date(value);
		});

		//Long -> float
		addConverter(Long.class, float.class, (Long value) -> {
			return value.floatValue();
		});

		//BigDecimal -> float
		addConverter(BigDecimal.class, float.class, (BigDecimal value) -> {
			return value.floatValue();
		});

		//Long -> Float
		addConverter(Long.class, Float.class, (Long value) -> {
			return value.floatValue();
		});

		//Long -> double
		addConverter(Long.class, double.class, (Long value) -> {
			return value.doubleValue();
		});

		//BigDecimal -> double
		addConverter(BigDecimal.class, double.class, (BigDecimal value) -> {
			return value.doubleValue();
		});

		//Long -> Double
		addConverter(Long.class, Double.class, (Long value) -> {
			return (double) value;
		});

		//long -> String
		addConverter(long.class, String.class, (Long value) -> {
			return value.toString();
		});

		//Long -> String
		addConverter(Long.class, String.class, (Long value) -> {
			return value.toString();
		});

		//float -> String
		addConverter(float.class, String.class, (Float value) -> {
			return value.toString();
		});

		//Float -> String
		addConverter(Float.class, String.class, (Float value) -> {
			return value.toString();
		});

		//Float -> float
		addConverter(Float.class, float.class, (Float value) -> {
			return (float) value;
		});

		//Float -> double
		addConverter(Float.class, double.class, (Float value) -> {
			return (double) value;
		});

		//Float -> Double
		addConverter(Float.class, Double.class, (Float value) -> {
			return (double) value;
		});

		//Boolean -> boolean
		addConverter(Boolean.class, boolean.class, (Boolean value) -> {
			return (boolean) value;
		});

		//Boolean -> boolean
		addConverter(boolean.class, Boolean.class, (Boolean value) -> {
			return value;
		});

		//Double -> double
		addConverter(Double.class, double.class, (Double value) -> {
			return (double) value;
		});

		//Double -> double
		addConverter(double.class, Double.class, (Double value) -> {
			return value;
		});

		//Double -> float
		addConverter(Double.class, float.class, (Double value) -> {
			return (float) (double) value;
		});

		//Double -> Float
		addConverter(Double.class, Float.class, (Double value) -> {
			return (float) (double) value;
		});

		//Double -> int
		addConverter(Double.class, int.class, (Double value) -> {
			return value.intValue();
		});

		//Double -> Integer
		addConverter(Double.class, Integer.class, (Double value) -> {
			return value.intValue();
		});

		//double -> int
		addConverter(double.class, int.class, (Double value) -> {
			return value.intValue();
		});

		//double -> String
		addConverter(double.class, String.class, (Double value) -> {
			return value.toString();
		});

		//Double -> String
		addConverter(Double.class, String.class, (Double value) -> {
			return value.toString();
		});

		//Date -> Long
		addConverter(Date.class, Long.class, (Date value) -> {
			if (value == null) {
				return null;
			}
			return value.getTime();
		});

		//Class -> String
		addConverter(Class.class, String.class, (Class value) -> {
			return value.getName();
		});

		//List -> Object[]
		addConverter(List.class, Object[].class, (List value) -> {
			return value.toArray();
		});

		//Collection -> Object[]
		addConverter(Collection.class, Object[].class, (Collection value) -> {
			return value.toArray();
		});

		//JSONArray -> Object[]
		addConverter(JSONArray.class, Object[].class, (JSONArray value) -> {
			try {
				List result = new ArrayList();
				for (int i = 0; i < value.length(); ++i) {

					Object val = value.get(i);

					result.add(val);
				}
				return result.toArray();
			} catch (JSONException ex) {
				throw new IllegalArgumentException(ex.getMessage(), ex);
			}
		});
	}

	public static String bytesToHex(byte[] bytes)
	{
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	public synchronized static void addConverter(Class sourceClass, Class targetClass, Function<?, ?> converter) throws RuntimeException
	{
		Map<Class, Function<?, ?>> targetMappings = converters.get(sourceClass);

		if (targetMappings == null) {
			targetMappings = new HashMap<>();
			converters.put(sourceClass, targetMappings);
		}

		if (targetMappings.putIfAbsent(targetClass, converter) != null) {
			throw new RuntimeException("Converter from class " + sourceClass.getName() + " to " + targetClass.getName() + " is already mapped");
		}
	}

	public static <T extends Object> T convert(Object value, Class targetClass, T defaultValue) throws RuntimeException
	{
		if (value == null) {

			if (defaultValue != null) {
				return convert(defaultValue, targetClass);
			}

			return null;
		}

		T result = convert(value, targetClass);

		if (result == null) {

			if (defaultValue != null) {
				return convert(defaultValue, targetClass);
			}

			return null;
		}

		return result;
	}

	public static <T extends Object> T convert(Object value, Class targetClass) throws RuntimeException
	{
		assert targetClass != null;

		//null remains null
		if (value == null) {
			return null;
		}

		//cast JSONObject.NULL always to null
		if (value == JSONObject.NULL) {
			return null;
		}

		//cast to object return original
		if (targetClass.equals(Object.class)) {
			return (T) value;
		}

		//equal class remains unchanged - its not an interceptor approach
		if (targetClass.isInstance(value)) {
			return (T) value;
		}

		//handling for all enums
		if (targetClass.isEnum()) {
			return (T) Enum.valueOf(targetClass, value.toString());
		}

		//convert array types and check if all entries are consistent
		if (targetClass.isArray() && value.getClass().isArray()) {

			Object[] sourceArray = ((Object[]) (value));
			int length = sourceArray.length;
			Class targetComponentType = targetClass.getComponentType();
			Object targetArray = Array.newInstance(targetComponentType, length);

			for (int i = 0; i < length; ++i) {
				Array.set(targetArray, i, sourceArray[i]);
			}

			return (T) targetArray;
		}

		//handling arrays from string
		if (targetClass.isArray() && (value instanceof String)) {

			String[] parts = ((String) value).split(",");

			Object[] array = (Object[]) Array.newInstance(targetClass.getComponentType(), parts.length);

			int i = 0;
			for (String part : parts) {

				array[i] = convert(part.trim(), targetClass.getComponentType());
				++i;
			}

			return (T) array;
		}

		//handling for all enums to String
		if (String.class.equals(targetClass) && value.getClass().isEnum()) {
			return (T) value.toString();
		}

		//handle automatic conversion of implementations to their interfaces
		if (targetClass.isInterface() && targetClass.isAssignableFrom(value.getClass())) {
			return (T) value;
		}

		Map<Class, Function<?, ?>> targetMappings = converters.get(value.getClass());

		if (targetMappings == null) {
			throw new RuntimeException("No source mappings for source class " + value.getClass().getName() + " to " + targetClass.getName());
		}

		Function converter = targetMappings.get(targetClass);

		//@todo allow to check for converters of parent classes? if under what contract?
		if (converter == null) {
			throw new RuntimeException("No target mappings for source class " + value.getClass().getName() + " to " + targetClass.getName());
		}

		try {
			return (T) converter.apply(value);
		} catch (RuntimeException ex) {
			throw new RuntimeException("Error converting from " + value.getClass().getName() + " to " + targetClass.getName() + " - " + ex.getMessage(), ex);
		}
	}
}
