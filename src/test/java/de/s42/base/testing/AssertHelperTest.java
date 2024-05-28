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
package de.s42.base.testing;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class AssertHelperTest
{

	@Test
	public void validDoubleEquals()
	{
		AssertHelper.assertEpsilonEquals(1.0, 1.0);
		AssertHelper.assertEpsilonEquals(1.1111111115, 1.111111111);
		AssertHelper.assertEpsilonEquals(1.00, 1.0 + AssertHelper.DOUBLE_EPSILON * 0.5);

		assertEquals(AssertHelper.epsilonEquals(1.0, 1.0), true);
		assertEquals(AssertHelper.epsilonEquals(1.1111111115, 1.111111111), true);
		assertEquals(AssertHelper.epsilonEquals(1.00, 1.0 + AssertHelper.DOUBLE_EPSILON * 0.5), true);
	}

	@Test(expectedExceptions = AssertionError.class)
	public void invalidDoubleEquals()
	{
		AssertHelper.assertEpsilonEquals(2.0, 1.0);
	}

	@Test
	public void validFloatEquals()
	{
		AssertHelper.assertEpsilonEquals(1.0f, 1.0f);
		AssertHelper.assertEpsilonEquals(1.11111115f, 1.111111f);
		AssertHelper.assertEpsilonEquals(1.00f, 1.0f + AssertHelper.FLOAT_EPSILON * 0.5f);

		assertEquals(AssertHelper.epsilonEquals(1.0f, 1.0f), true);
		assertEquals(AssertHelper.epsilonEquals(1.11111115f, 1.111111f), true);
		assertEquals(AssertHelper.epsilonEquals(1.00f, 1.0f + AssertHelper.FLOAT_EPSILON * 0.5f), true);
	}

	@Test(expectedExceptions = AssertionError.class)
	public void invalidFloatEquals()
	{
		AssertHelper.assertEpsilonEquals(2.0f, 1.0f);
	}
}
