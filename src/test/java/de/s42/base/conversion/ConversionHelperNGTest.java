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
package de.s42.base.conversion;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class ConversionHelperNGTest
{

	@Test
	public void validConvertArray()
	{
		Object[] source = {1.1f, 2.2f, 3.3f};
		Float[] target = (Float[]) ConversionHelper.convert(source, Float[].class);
		float sum = 0.0f;
		for (Float v : target) {
			sum += v;
		}
		//epsilon tets
		Assert.assertTrue(Math.abs(sum - 6.6f) < 0.0001);
	}

	@Test
	public void validArrayConvert()
	{
		String values = "1,2,3";

		Integer[] ints = ConversionHelper.convertArray(values.split(","), Integer.class);

		Assert.assertEquals(ints, new Integer[]{1, 2, 3});
	}

	@Test
	public void validStringToDate() throws ParseException
	{
		Assert.assertEquals(ConversionHelper.convert("12222222222", Date.class), new Date(12222222222L));
		Assert.assertEquals(
			ConversionHelper.convert("2022-04-14 10:12:56:000", Date.class),
			ConversionHelper.DATE_FORMAT.parse("2022-04-14 10:12:56:000"));
	}

	@Test
	public void validConvertList()
	{
		Object[] source = {1.1f, 2.2f, 3.3f, 1.1f};
		List<Float> target = (List<Float>) ConversionHelper.convertList(source, Float.class);
		float sum = 0.0f;
		for (Float v : target) {
			sum += v;
		}
		//epsilon tets
		Assert.assertTrue(Math.abs(sum - 7.7f) < 0.0001);
	}

	@Test
	public void validConvertSet()
	{
		Object[] source = {1.1f, 2.2f, 3.3f};
		Set<Float> target = (Set<Float>) ConversionHelper.convertSet(source, Float.class);
		float sum = 0.0f;
		for (Float v : target) {
			sum += v;
		}
		//epsilon tets
		Assert.assertTrue(Math.abs(sum - 6.6f) < 0.0001);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void invalidConvertSet()
	{
		Object[] source = {1.1f, 2.2f, 1.1f};
		ConversionHelper.convertSet(source, Float.class);
	}
}
