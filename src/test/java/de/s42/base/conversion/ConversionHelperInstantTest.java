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

import java.time.Instant;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class ConversionHelperInstantTest
{

	@Test
	public void instantFromString()
	{
		Instant converted = ConversionHelper.convert("1717958547123", Instant.class);

		Instant expected = Instant.ofEpochMilli(1717958547123L);

		assertEquals(converted, expected);
	}

	@Test
	public void instantFromLong()
	{
		Instant converted = ConversionHelper.convert(Long.valueOf(1717958547123L), Instant.class);

		Instant expected = Instant.ofEpochMilli(1717958547123L);

		assertEquals(converted, expected);
	}

	@Test
	public void instantFromPrimitveLong()
	{
		Instant converted = ConversionHelper.convert(1717958547123L, Instant.class);

		Instant expected = Instant.ofEpochMilli(1717958547123L);

		assertEquals(converted, expected);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void invalidInstantFromString()
	{
		ConversionHelper.convert("invalid234234", Instant.class);
	}

	@Test
	public void instantToString()
	{
		String converted = ConversionHelper.convert(Instant.ofEpochMilli(1717958547123L), String.class);

		String expected = "1717958547123";

		assertEquals(converted, expected);
	}

	@Test
	public void instantToLong()
	{
		Long converted = ConversionHelper.convert(Instant.ofEpochMilli(1717958547123L), Long.class);

		Long expected = 1717958547123L;

		assertEquals(converted, expected);
	}

	@Test
	public void instantToPrimitiveLong()
	{
		long converted = ConversionHelper.convert(Instant.ofEpochMilli(1717958547123L), long.class);

		long expected = 1717958547123L;

		assertEquals(converted, expected);
	}
}
