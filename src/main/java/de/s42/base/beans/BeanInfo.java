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

import de.s42.base.conversion.ConversionHelper;
import de.s42.base.strings.StringHelper;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 * @param <BeanClass>
 */
public final class BeanInfo<BeanClass>
{

	protected final Class<BeanClass> beanClass;
	protected final java.beans.BeanInfo beanInfo;
	protected final int modifiers;
	protected final Set<BeanProperty<BeanClass>> properties;
	protected final Set<BeanProperty<BeanClass>> ownProperties;
	protected final Set<BeanProperty<BeanClass>> readProperties;
	protected final Set<BeanProperty<BeanClass>> writeProperties;
	protected final Map<String, BeanProperty<BeanClass>> propertiesByName;

	//@SuppressWarnings("unchecked")
	public BeanInfo(Class<BeanClass> beanClass) throws InvalidBean
	{
		assert beanClass != null;

		try {

			this.beanClass = beanClass;

			modifiers = beanClass.getModifiers();

			beanInfo = Introspector.getBeanInfo(beanClass);

			properties = Collections.unmodifiableSet(createProperties(beanClass));

			// prepare filtered properties
			Set<BeanProperty<BeanClass>> ownProps = new HashSet<>();
			Set<BeanProperty<BeanClass>> rProps = new HashSet<>();
			Set<BeanProperty<BeanClass>> wProps = new HashSet<>();
			Map<String, BeanProperty<BeanClass>> propsByName = new HashMap<>();
			for (BeanProperty<BeanClass> property : properties) {
				if (property.isOwn()) {
					ownProps.add(property);
				}
				if (property.canRead()) {
					rProps.add(property);
				}
				if (property.canWrite()) {
					wProps.add(property);
				}
				propsByName.put(property.getName(), property);
			}
			ownProperties = Collections.unmodifiableSet(ownProps);
			readProperties = Collections.unmodifiableSet(rProps);
			writeProperties = Collections.unmodifiableSet(wProps);
			propertiesByName = Collections.unmodifiableMap(propsByName);
		} catch (IntrospectionException ex) {
			throw new InvalidBean("Error creating bean info - " + ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static <BeanClass> Set<BeanProperty<BeanClass>> createProperties(Class beanClass) throws InvalidBean
	{
		assert beanClass != null;

		try {

			Set<BeanProperty<BeanClass>> result = new HashSet<>();

			Map<String, Field> fields = new HashMap<>();
			for (Field field : beanClass.getDeclaredFields()) {
				fields.put(field.getName(), field);
			}

			Set<Method> declaredMethods = new HashSet<>(Arrays.asList(beanClass.getDeclaredMethods()));

			java.beans.BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
			for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {

				// make sure it is not an indexed property with no default access - see javadoc of the method getPropertyType
				if (desc.getPropertyType() != null) {

					result.add(createProperty(fields.get(desc.getName()), desc, declaredMethods));
				}

				// Make sure the field is removed to not show up twice later with field only properties
				fields.remove(desc.getName());
			}

			// Iterate the left over field -> public non static fields will get added as well
			for (Field field : fields.values()) {
				int modifiers = field.getModifiers();
				if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
					result.add(createProperty(field, null, declaredMethods));
				}
			}

			return result;
		} catch (IntrospectionException ex) {
			throw new InvalidBean("Error creating bean properties - " + ex.getMessage(), ex);
		}
	}

	protected static BeanProperty createProperty(Field field, PropertyDescriptor desc, Set<Method> declaredMethods) throws InvalidBean
	{
		assert (field != null) || (desc != null);

		// Get generic types
		List<Class> genericTypes = new ArrayList<>();
		if (field != null && field.getGenericType() instanceof ParameterizedType) {

			for (Type type : ((ParameterizedType) field.getGenericType()).getActualTypeArguments()) {

				if (type instanceof Class) {
					genericTypes.add((Class) type);
				}
			}
		}

		// field and desc
		if (desc != null) {

			// Determine is own
			boolean own
				= (field != null)
				|| declaredMethods.contains(desc.getReadMethod())
				|| declaredMethods.contains(desc.getWriteMethod());

			Class<?> type = (field != null) ? field.getType() : desc.getPropertyType();

			return new BeanProperty<>(
				desc.getName(),
				type,
				desc.getReadMethod(),
				desc.getWriteMethod(),
				field,
				genericTypes,
				own,
				desc);
		} // field only -> public field
		else if (field != null) {
			return new BeanProperty<>(
				field.getName(),
				field.getType(),
				null,
				null,
				field,
				genericTypes,
				true,
				desc);
		} else {
			throw new InvalidBean("Either field or desc has to be non null");
		}
	}

	public BeanClass newInstance() throws InvalidBean
	{
		try {
			return (BeanClass) beanClass.getConstructor().newInstance();
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			throw new InvalidBean("Error instantiating bean - " + ex.getMessage(), ex);
		}
	}

	public boolean isFinal()
	{
		return Modifier.isFinal(modifiers);
	}

	public boolean isInterface()
	{
		return Modifier.isInterface(modifiers);
	}

	public boolean isAbstract()
	{
		return Modifier.isAbstract(modifiers);
	}

	public <ReturnType> ReturnType read(BeanClass bean, String propertyName) throws InvalidBean
	{
		assert bean != null;
		assert propertyName != null;

		if (!bean.getClass().equals(beanClass)) {
			throw new RuntimeException("Bean class " + beanClass.getCanonicalName() + " does not match object class " + bean.getClass().getCanonicalName());
		}

		BeanProperty<BeanClass> prop = propertiesByName.get(propertyName);

		if (prop == null) {
			throw new RuntimeException("Property " + propertyName + " is not contained in bean " + beanClass.getCanonicalName());
		}

		return prop.read(bean);
	}

	public void write(BeanClass bean, String propertyName, Object value) throws InvalidBean
	{
		assert bean != null;
		assert propertyName != null;

		if (!bean.getClass().equals(beanClass)) {
			throw new RuntimeException("Bean class " + beanClass.getCanonicalName() + " does not match object class " + bean.getClass().getCanonicalName());
		}

		BeanProperty<BeanClass> prop = propertiesByName.get(propertyName);

		if (prop == null) {
			throw new RuntimeException("Property " + propertyName + " is not contained in bean " + beanClass.getCanonicalName());
		}

		prop.write(bean, value);
	}

	public void writeConverted(BeanClass bean, String propertyName, Object value) throws InvalidBean
	{
		assert bean != null;
		assert propertyName != null;

		if (!bean.getClass().equals(beanClass)) {
			throw new RuntimeException("Bean class " + beanClass.getCanonicalName() + " does not match object class " + bean.getClass().getCanonicalName());
		}

		BeanProperty<BeanClass> prop = propertiesByName.get(propertyName);

		if (prop == null) {
			throw new RuntimeException("Property " + propertyName + " is not contained in bean " + beanClass.getCanonicalName());
		}

		prop.write(bean, ConversionHelper.convert(value, prop.getPropertyClass()));
	}

	public boolean hasProperty(String name)
	{
		assert name != null;

		return propertiesByName.containsKey(name);
	}

	public boolean hasReadProperty(String name)
	{
		assert name != null;

		BeanProperty property = propertiesByName.get(name);

		return property != null && property.canRead();
	}

	public boolean hasWriteProperty(String name)
	{
		assert name != null;

		BeanProperty property = propertiesByName.get(name);

		return property != null && property.canWrite();
	}

	public Optional<BeanProperty<BeanClass>> getProperty(String name)
	{
		assert name != null;

		return Optional.ofNullable(propertiesByName.get(name));
	}

	public Class getBeanClass()
	{
		return beanClass;
	}

	public Set<BeanProperty<BeanClass>> getProperties()
	{
		return properties;
	}

	public Set<BeanProperty<BeanClass>> getOwnProperties()
	{
		return ownProperties;
	}

	public Set<BeanProperty<BeanClass>> getReadProperties()
	{
		return readProperties;
	}

	public Set<BeanProperty<BeanClass>> getWriteProperties()
	{
		return writeProperties;
	}

	public Map<String, BeanProperty<BeanClass>> getPropertiesByName()
	{
		return propertiesByName;
	}

	@Override
	public String toString()
	{
		return StringHelper.toString(getBeanClass(), null,
			new String[]{
				"properties",},
			new Object[]{
				getProperties()}
		);
	}
}
