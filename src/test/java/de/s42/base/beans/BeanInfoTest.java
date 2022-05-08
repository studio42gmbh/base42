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

import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class BeanInfoTest
{

	public static class TestBean
	{

		protected float protectedValue;
		public int publicValue;
		protected double doubleValue;
		protected String stringValue;
		protected String writeOnlyValue;
		protected List<String> stringValues;
		protected Map<String, Double> doubleValuesByName;

		public double getDoubleValue()
		{
			return doubleValue;
		}

		public void setDoubleValue(double doubleValue)
		{
			this.doubleValue = doubleValue;
		}

		public String getStringValue()
		{
			return stringValue;
		}

		public void setStringValue(String stringValue)
		{
			this.stringValue = stringValue;
		}

		public List<String> getStringValues()
		{
			return stringValues;
		}

		public void setStringValues(List<String> stringValues)
		{
			this.stringValues = stringValues;
		}

		public void setWriteOnlyValue(String writeOnlyValue)
		{
			this.writeOnlyValue = writeOnlyValue;
		}

		public Map<String, Double> getDoubleValuesByName()
		{
			return doubleValuesByName;
		}

		public void setDoubleValuesByName(Map<String, Double> doubleValuesByName)
		{
			this.doubleValuesByName = doubleValuesByName;
		}
	}

	@Test
	public void validCreateBeanInfo() throws InvalidBean
	{
		BeanInfo<TestBean> info = new BeanInfo<>(TestBean.class);

		/*for (BeanProperty property : info.getProperties()) {
			System.out.println(property);
		}*/
		// New instance
		TestBean bean = info.newInstance();
		Assert.assertEquals(bean.getClass(), TestBean.class);
	}

	@Test
	public void validWriteBeanProperty() throws InvalidBean
	{
		BeanInfo<TestBean> info = new BeanInfo<>(TestBean.class);
		TestBean bean = info.newInstance();

		// Test normal bean field
		info.write(bean, "stringValue", "Test");
		Assert.assertEquals(bean.getStringValue(), "Test");
	}

	@Test
	public void validWritePublicFieldOnlyProperty() throws InvalidBean
	{
		BeanInfo<TestBean> info = BeanHelper.getBeanInfo(TestBean.class);
		TestBean bean = info.newInstance();

		// Test public field
		info.getProperty("publicValue").orElseThrow().write(bean, 42);
		Assert.assertEquals(bean.publicValue, 42);
	}

	@Test
	public void validCheckPropertyGenericType() throws InvalidBean
	{
		BeanInfo<TestBean> info = BeanHelper.getBeanInfo(TestBean.class);

		// Retrieve generic field and test generic type
		BeanProperty property = info.getProperty("stringValues").orElseThrow();
		Assert.assertEquals(property.getGenericTypes().get(0), String.class);
	}
}
