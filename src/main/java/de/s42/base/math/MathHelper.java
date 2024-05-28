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

	private MathHelper()
	{
		// do nothing
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
}
