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
package de.s42.base.testing;

/**
 *
 * @author Benjamin Schiller
 */
public class AssertHelper
{

	public final static float FLOAT_EPSILON = 0.1E-5f;
	public final static double DOUBLE_EPSILON = 0.1E-7;

	public static void assertEpsilonEquals(double actual, double expected) throws AssertionError
	{
		assertEpsilonEquals(actual, expected, "");
	}

	public static void assertEpsilonEquals(float actual, float expected) throws AssertionError
	{
		assertEpsilonEquals(actual, expected, "");
	}

	public static void assertEpsilonEquals(double actual, double expected, String message) throws AssertionError
	{
		assert message != null;

		if (!epsilonEquals(actual, expected)) {
			throw new AssertionError(message + " - expected almost [" + expected + "] but found [" + actual + "] (EPSILON : " + DOUBLE_EPSILON + ")");
		}
	}

	public static void assertEpsilonEquals(float actual, float expected, String message) throws AssertionError
	{
		if (!epsilonEquals(actual, expected)) {
			throw new AssertionError(message + " - expected almost [" + expected + "] but found [" + actual + "] (EPSILON : " + FLOAT_EPSILON + ")");
		}
	}

	public static boolean epsilonEquals(float actual, float expected)
	{
		return Math.abs(actual - expected) < FLOAT_EPSILON;
	}

	public static boolean epsilonEquals(double actual, double expected)
	{
		return Math.abs(actual - expected) < DOUBLE_EPSILON;
	}

	public static void assertStartsWith(String actual, String expectedStart)
	{
		assertStartsWith(actual, expectedStart, "");
	}

	public static void assertStartsWith(String actual, String expectedStart, String message)
	{
		assert actual != null;
		assert expectedStart != null;
		assert message != null;

		if (!actual.startsWith(expectedStart)) {
			throw new AssertionError(message + " - expected to start with '" + expectedStart + "' but found '" + actual + "'");
		}
	}
}
