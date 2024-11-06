// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2024 Studio 42 GmbH ( https://www.s42m.de ).
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
package de.s42.base.math;

/**
 *
 * @author Benjamin.Schiller
 */
public final class MathHelper
{

	public final static double SQRT2 = Math.sqrt(2.0);

	public final static double SQRT3 = Math.sqrt(3.0);

	public final static double TAU = Math.PI * 2.0;

	public static final long[] POWERS_OF_TEN_LONG = {
		1L,
		10L,
		100L,
		1_000L,
		10_000L,
		100_000L,
		1_000_000L,
		10_000_000L,
		100_000_000L,
		1_000_000_000L,
		10_000_000_000L,
		100_000_000_000L,
		1_000_000_000_000L,
		10_000_000_000_000L,
		100_000_000_000_000L,
		1_000_000_000_000_000L};

	public static final double[] POWERS_OF_TEN_DOUBLE = {
		1.0,
		10.0,
		100.0,
		1_000.0,
		10_000.0,
		100_000.0,
		1_000_000.0,
		10_000_000.0,
		100_000_000.0,
		1_000_000_000.0,
		10_000_000_000.0,
		100_000_000_000.0,
		1_000_000_000_000.0,
		10_000_000_000_000.0,
		100_000_000_000_000.0,
		1_000_000_000_000_000.0};

	private MathHelper()
	{
		// do nothing
	}

	public static final double pow10Double(int exp)
	{
		if (exp < 0 || exp > 15) {
			throw new IllegalArgumentException("exp has to be 0 - 15");
		}

		return POWERS_OF_TEN_DOUBLE[exp];
	}

	public static final double pow10(double number, int exp)
	{
		if (exp < 0 || exp > 15) {
			throw new IllegalArgumentException("exp has to be 0 - 15");
		}

		return number * POWERS_OF_TEN_DOUBLE[exp];
	}

	public static final long pow10Long(int exp)
	{
		if (exp < 0 || exp > 15) {
			throw new IllegalArgumentException("exp has to be 0 - 15");
		}

		return POWERS_OF_TEN_LONG[exp];
	}

	public static final long pow10(long number, int exp)
	{
		if (exp < 0 || exp > 15) {
			throw new IllegalArgumentException("exp has to be 0 - 15");
		}

		return number * POWERS_OF_TEN_LONG[exp];
	}

	public static final double saturate(double value)
	{
		return clamp(value, 0.0, 1.0);
	}

	public static final double saturate(Number value)
	{
		assert value != null : "value != null";

		return clamp(value.doubleValue(), 0.0, 1.0);
	}

	public static final double clamp(double value, double min, double max)
	{
		return Math.max(Math.min(value, max), min);
	}

	public static final double clamp(Number value, Number min, Number max)
	{
		assert value != null : "value != null";
		assert min != null : "min != null";
		assert max != null : "max != null";

		return Math.max(Math.min(value.doubleValue(), max.doubleValue()), min.doubleValue());
	}

	public static final double fract(double value)
	{
		return value - Math.floor(value);
	}

	public static final float fract(float value)
	{
		return value - (float) Math.floor(value);
	}
}
