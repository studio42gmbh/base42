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

import de.s42.base.strings.StringHelper;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 * @param <BeanClass>
 * @param <PropertyClass>
 */
public final class BeanProperty<BeanClass, PropertyClass>
{

	protected final String name;
	protected final Class<PropertyClass> propertyClass;
	protected final Method readMethod;
	protected final Method writeMethod;
	protected final Field field;
	protected final List<Class> genericTypes;
	protected final boolean own;
	protected final boolean write;
	protected final boolean read;
	protected final boolean hasGenericTypes;
	protected final boolean publicField;
	protected final PropertyDescriptor descriptor;

	public BeanProperty(
		String name,
		Class<PropertyClass> propertyClass,
		Method readMethod,
		Method writeMethod,
		Field field,
		List<Class> genericTypes,
		boolean own,
		PropertyDescriptor descriptor
	)
	{
		assert name != null;
		assert propertyClass != null : "propertyClass != null in property " + name;

		int modifiers = (field != null) ? field.getModifiers() : 0;

		this.name = name;
		this.propertyClass = propertyClass;
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
		this.field = field;
		if (genericTypes != null) {
			this.genericTypes = Collections.unmodifiableList(genericTypes);
		} else {
			this.genericTypes = Collections.emptyList();
		}
		this.own = own;
		this.descriptor = descriptor;
		publicField = (descriptor == null && Modifier.isPublic(modifiers));
		read = readMethod != null || publicField;
		write = writeMethod != null || publicField;
		hasGenericTypes = !this.genericTypes.isEmpty();
	}

	public void write(BeanClass object, Object value) throws InvalidBean
	{
		assert object != null;

		try {
			if (writeMethod != null) {
				writeMethod.invoke(object, (Object) value);
			} else {
				field.set(object, value);
			}
		} catch (InvocationTargetException ex) {
			throw new InvalidBean("Error writing value " + value + " to property " + getName() + " from bean " + object + " - " + ex.getCause().getMessage(), ex);
		} catch (IllegalAccessException | IllegalArgumentException ex) {
			throw new InvalidBean("Error writing value " + value + " to property " + getName() + " from bean " + object + " - " + ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unchecked")
	public <ReturnType> ReturnType read(BeanClass object) throws InvalidBean
	{
		assert object != null;

		try {
			if (readMethod != null) {
				return (ReturnType) readMethod.invoke(object);
			} else {
				return (ReturnType) field.get(object);
			}
		} catch (InvocationTargetException ex) {
			throw new InvalidBean("Error reading property from bean - " + ex.getCause().getMessage(), ex);
		} catch (IllegalAccessException ex) {
			throw new InvalidBean("Error reading property from bean - " + ex.getMessage(), ex);
		}
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass)
	{
		assert annotationClass != null;

		boolean annotation;

		if (field != null) {

			annotation = field.isAnnotationPresent(annotationClass);

			if (annotation) {
				return true;
			}
		}

		if (readMethod != null) {

			annotation = readMethod.isAnnotationPresent(annotationClass);

			if (annotation) {
				return true;
			}
		}

		if (writeMethod != null) {

			return writeMethod.isAnnotationPresent(annotationClass);
		}

		return false;
	}

	public <AnnotationType extends Annotation> Optional<AnnotationType> getAnnotation(Class<? extends AnnotationType> annotationClass)
	{
		assert annotationClass != null;

		AnnotationType annotation;

		if (field != null) {

			annotation = field.getAnnotation(annotationClass);

			if (annotation != null) {
				return Optional.of(annotation);
			}
		}

		if (readMethod != null) {

			annotation = readMethod.getAnnotation(annotationClass);

			if (annotation != null) {
				return Optional.of(annotation);
			}
		}

		if (writeMethod != null) {
			return Optional.ofNullable(writeMethod.getAnnotation(annotationClass));
		}

		return Optional.empty();
	}

	public boolean isPublicField()
	{
		return publicField;
	}

	public boolean isReadOnly()
	{
		return read & !write;
	}

	public boolean canRead()
	{
		return read;
	}

	public boolean isWriteOnly()
	{
		return !read & write;
	}

	public boolean canWrite()
	{
		return write;
	}

	public boolean isOwn()
	{
		return own;
	}

	public boolean hasGenericTypes()
	{
		return hasGenericTypes;
	}

	public String getName()
	{
		return name;
	}

	public Method getReadMethod()
	{
		return readMethod;
	}

	public Method getWriteMethod()
	{
		return writeMethod;
	}

	public Field getField()
	{
		return field;
	}

	public List<Class> getGenericTypes()
	{
		return genericTypes;
	}

	public PropertyDescriptor getDescriptor()
	{
		return descriptor;
	}

	public Class<PropertyClass> getPropertyClass()
	{
		return propertyClass;
	}

	@Override
	public String toString()
	{
		return StringHelper.toString(getClass(), getName(),
			new String[]{
				"propertyClass",
				"canRead",
				"canWrite",
				"own",
				"genericTypes"},
			new Object[]{
				getPropertyClass(),
				canRead(),
				canWrite(),
				isOwn(),
				genericTypes}
		);
	}
}
