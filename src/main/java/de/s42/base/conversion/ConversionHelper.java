// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2022 Studio 42 GmbH (https://www.s42m.de).
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
package de.s42.base.conversion;

import de.s42.base.uuid.UUIDHelper;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
@SuppressWarnings("unchecked")
public final class ConversionHelper
{

	private ConversionHelper()
	{
	}

	private final static Map<Class, Map<Class, Function<?, ?>>> converters = Collections.synchronizedMap(new HashMap<>());
	private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

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

		//String -> Number
		addConverter(String.class, Number.class, (String value) -> {
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

			// date as timestamp
			if (value.matches("[0-9]+")) {
				return new Date(Long.parseLong(value));
			} else {
				try {
					return DATE_FORMAT.parse(value);
				} catch (ParseException ex) {

					// Try Timestamp
					try {
						return java.sql.Timestamp.valueOf(value);
					} catch (IllegalArgumentException ex2) {
						throw new RuntimeException("Error converting to date - " + ex2.getMessage(), ex2);
					}
				}
			}
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

		//String (as JSON String) -> Map
		addConverter(String.class, Map.class, (String value) -> {

			JSONObject data = new JSONObject(value);

			return data.toMap();
		});

		//String (as JSON String) -> List
		addConverter(String.class, List.class, (String value) -> {

			JSONArray data = new JSONArray(value);

			return data.toList();
		});

		//String -> Character
		addConverter(String.class, Character.class, (String value) -> {
			return value.charAt(0);
		});

		//String -> char
		addConverter(String.class, char.class, (String value) -> {
			return value.charAt(0);
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

		//int -> Date
		addConverter(int.class, Date.class, (Integer value) -> {
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

		//Long -> Integer
		addConverter(Long.class, Integer.class, (Long value) -> {
			return value.intValue();
		});

		//Long -> Date
		addConverter(Long.class, Date.class, (Long value) -> {
			return new Date(value);
		});

		//long -> Date
		addConverter(long.class, Date.class, (Long value) -> {
			return new Date(value);
		});

		//Long -> float
		addConverter(Long.class, float.class, (Long value) -> {
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

		//BigDecimal -> Double
		addConverter(BigDecimal.class, Double.class, (BigDecimal value) -> {
			return value.doubleValue();
		});

		//BigDecimal -> double
		addConverter(BigDecimal.class, double.class, (BigDecimal value) -> {
			return value.doubleValue();
		});

		//BigDecimal -> Float
		addConverter(BigDecimal.class, Float.class, (BigDecimal value) -> {
			return value.floatValue();
		});

		//BigDecimal -> float
		addConverter(BigDecimal.class, float.class, (BigDecimal value) -> {
			return value.floatValue();
		});

		//BigDecimal -> Long
		addConverter(BigDecimal.class, Long.class, (BigDecimal value) -> {
			return value.longValue();
		});

		//BigDecimal -> long
		addConverter(BigDecimal.class, long.class, (BigDecimal value) -> {
			return value.longValue();
		});

		//BigDecimal -> Integer
		addConverter(BigDecimal.class, Integer.class, (BigDecimal value) -> {
			return value.intValue();
		});

		//BigDecimal -> int
		addConverter(BigDecimal.class, int.class, (BigDecimal value) -> {
			return value.intValue();
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

					result.add(value.get(i));
				}
				return result.toArray();
			} catch (JSONException ex) {
				throw new IllegalArgumentException(ex.getMessage(), ex);
			}
		});

		//UUID -> String
		addConverter(UUID.class, String.class, (UUID value) -> {
			return value.toString();
		});

		//Long -> Byte
		addConverter(Long.class, Byte.class, (Long value) -> {
			return value.byteValue();
		});

		//Long -> byte
		addConverter(Long.class, byte.class, (Long value) -> {
			return value.byteValue();
		});

		//long -> Byte
		addConverter(long.class, Byte.class, (Long value) -> {
			return value.byteValue();
		});

		//long -> byte
		addConverter(long.class, byte.class, (Long value) -> {
			return value.byteValue();
		});

		//Integer -> Byte
		addConverter(Integer.class, Byte.class, (Integer value) -> {
			return value.byteValue();
		});

		//Integer -> byte
		addConverter(Integer.class, byte.class, (Integer value) -> {
			return value.byteValue();
		});

		//int -> Byte
		addConverter(int.class, Byte.class, (Integer value) -> {
			return value.byteValue();
		});

		//int -> byte
		addConverter(int.class, byte.class, (Integer value) -> {
			return value.byteValue();
		});

		//Short -> Byte
		addConverter(Short.class, Byte.class, (Short value) -> {
			return value.byteValue();
		});

		//Short -> byte
		addConverter(Short.class, byte.class, (Short value) -> {
			return value.byteValue();
		});

		//short -> Byte
		addConverter(short.class, Byte.class, (Short value) -> {
			return value.byteValue();
		});

		//short -> byte
		addConverter(short.class, byte.class, (Short value) -> {
			return value.byteValue();
		});

		//Long -> Short
		addConverter(Long.class, Short.class, (Long value) -> {
			return value.shortValue();
		});

		//Long -> short
		addConverter(Long.class, short.class, (Long value) -> {
			return value.shortValue();
		});

		//long -> Short
		addConverter(long.class, Short.class, (Long value) -> {
			return value.shortValue();
		});

		//long -> short
		addConverter(long.class, short.class, (Long value) -> {
			return value.shortValue();
		});

		//Integer -> Short
		addConverter(Integer.class, Short.class, (Integer value) -> {
			return value.shortValue();
		});

		//Integer -> short
		addConverter(Integer.class, short.class, (Integer value) -> {
			return value.shortValue();
		});

		//int -> Short
		addConverter(int.class, Short.class, (Integer value) -> {
			return value.shortValue();
		});

		//int -> short
		addConverter(int.class, short.class, (Integer value) -> {
			return value.shortValue();
		});

		//Byte -> Short
		addConverter(Byte.class, Short.class, (Integer value) -> {
			return value.shortValue();
		});

		//Byte -> short
		addConverter(Byte.class, short.class, (Integer value) -> {
			return value.shortValue();
		});

		//byte -> Short
		addConverter(byte.class, Short.class, (Integer value) -> {
			return value.shortValue();
		});

		//byte -> short
		addConverter(byte.class, short.class, (Integer value) -> {
			return value.shortValue();
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

	public static <ReturnType> ReturnType convert(Object value, Class<? extends ReturnType> targetClass, ReturnType defaultValue) throws RuntimeException
	{
		if (value == null) {

			if (defaultValue != null) {
				return convert(defaultValue, targetClass);
			}

			return null;
		}

		ReturnType result = convert(value, targetClass);

		if (result == null) {

			if (defaultValue != null) {
				return convert(defaultValue, targetClass);
			}

			return null;
		}

		return result;
	}

	public static <ReturnType> ReturnType convert(Object value, Class<? extends ReturnType> targetClass) throws RuntimeException
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
			return (ReturnType) value;
		}

		//equal class remains unchanged - its not an interceptor approach
		if (targetClass.isInstance(value)) {
			return (ReturnType) value;
		}

		//handling for all enums
		if (targetClass.isEnum()) {
			return (ReturnType) Enum.valueOf((Class<Enum>) targetClass, value.toString());
		}

		//convert array types and check if all entries are consistent
		if (value.getClass().isArray()) {

			if (targetClass.isArray()) {

				Object[] sourceArray = ((Object[]) (value));
				int length = sourceArray.length;
				Class targetComponentType = targetClass.getComponentType();
				Object[] targetArray = (Object[]) Array.newInstance(targetComponentType, length);

				for (int i = 0; i < length; ++i) {

					// convert each element of the array
					targetArray[i] = convert(sourceArray[i], targetComponentType);
				}

				return (ReturnType) targetArray;
			} // Convert array into string
			else if (String.class.isAssignableFrom(targetClass)) {

				Object[] sourceArray = ((Object[]) (value));
				int length = sourceArray.length;

				StringBuilder builder = new StringBuilder();

				for (int i = 0; i < length; ++i) {

					builder.append(convert(sourceArray[i], String.class));
					if (i < length - 1) {
						builder.append(", ");
					}
				}

				return (ReturnType) builder.toString();
			}
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

			return (ReturnType) array;
		}

		//handling for all enums to String
		if (String.class.equals(targetClass) && value.getClass().isEnum()) {
			return (ReturnType) value.toString();
		}

		//handle automatic conversion of implementations to their interfaces
		if (targetClass.isInterface() && targetClass.isAssignableFrom(value.getClass())) {
			return (ReturnType) value;
		}

		Map<Class, Function<?, ?>> targetMappings = converters.get(value.getClass());

		if (targetMappings == null) {

			// Default conversion to String using the given toString method of the value
			if (String.class.isAssignableFrom(targetClass)) {
				return (ReturnType) value.toString();
			}

			throw new RuntimeException("No source mappings for source class " + value.getClass().getName() + " to " + targetClass.getName());
		}

		Function converter = targetMappings.get(targetClass);

		// @improvement allow to check for converters of parent classes? if under what contract?
		if (converter == null) {

			// Default conversion to String using the given toString method of the value
			if (String.class.isAssignableFrom(targetClass)) {
				return (ReturnType) value.toString();
			}

			throw new RuntimeException("No target mappings for source class " + value.getClass().getName() + " to " + targetClass.getName());
		}

		try {
			return (ReturnType) converter.apply(value);
		} catch (RuntimeException ex) {
			throw new RuntimeException("Error converting from " + value.getClass().getName() + " to " + targetClass.getName() + " - " + ex.getMessage(), ex);
		}
	}

	public static <ReturnType> ReturnType[] convertArray(Object[] values, Class<? extends ReturnType> targetClass) throws RuntimeException
	{
		if (values == null) {
			return null;
		}

		ReturnType[] result = (ReturnType[]) Array.newInstance(targetClass, values.length);

		for (int i = 0; i < values.length; ++i) {
			result[i] = convert(values[i], targetClass);
		}

		return result;
	}

	public static <ReturnType> List<ReturnType> convertList(Object[] values, Class<? extends ReturnType> targetClass) throws RuntimeException
	{
		if (values == null) {
			return null;
		}

		List<ReturnType> result = new ArrayList<>();

		for (int i = 0; i < values.length; ++i) {
			result.add(convert(values[i], targetClass));
		}

		return result;
	}

	public static <ReturnType> List<ReturnType> convertList(List values, Class<? extends ReturnType> targetClass) throws RuntimeException
	{
		if (values == null) {
			return null;
		}

		List result = Collections.checkedList(new ArrayList<>(), targetClass);

		for (Object value : values) {
			result.add(convert(value, targetClass));
		}

		return result;
	}

	public static <ReturnType> Set<ReturnType> convertSet(Object[] values, Class<? extends ReturnType> targetClass) throws RuntimeException
	{
		if (values == null) {
			return null;
		}

		Set<ReturnType> result = new HashSet<>();

		for (int i = 0; i < values.length; ++i) {
			if (!result.add(convert(values[i], targetClass))) {
				throw new RuntimeException("Element " + i + " is already contained in Set");
			}
		}

		return result;
	}

	@SuppressWarnings("null")
	public static <ReturnType> ReturnType[] convertArray(ReturnType[] values, Class<? extends ReturnType>[] targetClasses) throws RuntimeException
	{
		if (values == null && targetClasses == null) {
			return null;
		}

		if ((values != null && targetClasses == null)
			|| (values == null && targetClasses != null)) {
			throw new RuntimeException("values and targetClasses have to be either both null or non null");
		}

		if (values.length == 0 && targetClasses.length == 0) {
			return values;
		}

		if (values.length != targetClasses.length) {
			throw new RuntimeException("values count not matching has to have " + targetClasses.length + " parameter(s) but has " + values.length);
		}

		ReturnType[] result = (ReturnType[]) Array.newInstance(values.getClass().componentType(), values.length);

		for (int i = 0; i < result.length; ++i) {
			result[i] = convert(values[i], targetClasses[i]);
		}

		return result;
	}

	public static boolean canConvert(Class sourceClass, Class targetClass)
	{
		Map<Class, Function<?, ?>> targetMappings = converters.get(sourceClass);

		if (targetMappings == null) {
			return false;
		}

		Function converter = targetMappings.get(targetClass);

		// @improvement allow to check for converters of parent classes? if under what contract?
		return (converter != null);
	}
}
