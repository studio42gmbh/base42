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
package de.s42.base.compile;

import java.lang.reflect.InvocationTargetException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class CompileHelperTest
{

	public static interface TestAction
	{

		public String doAction();
	}

	protected final static String validClassName = "MyAction";
	protected final static String validClassCode = "public class MyAction implements de.s42.base.compile.CompileHelperTest.TestAction { public String doAction() { return \"YAY\"; } }";

	@Test
	public void validJitClassInstance() throws InvalidCompilation
	{
		TestAction testAction = CompileHelper.getCompiledInstance(validClassCode, validClassName);

		Assert.assertEquals("YAY", testAction.doAction());
	}

	@Test(expectedExceptions = InvalidCompilation.class)
	public void invalidJitClass() throws InvalidCompilation
	{
		String classCode = "public class MyAction implements de.s42.dl.util.compile.CompileHelperTest.TestAction {}";

		TestAction testAction = CompileHelper.getCompiledInstance(classCode, validClassName);
	}

	@Test
	public void validJitClassManualInstance() throws InvalidCompilation, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Class testActionClass = CompileHelper.getCompiledClass(validClassCode, validClassName);

		TestAction testAction = (TestAction) testActionClass.getConstructor().newInstance();

		Assert.assertEquals("YAY", testAction.doAction());
	}

}
