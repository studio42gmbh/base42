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
package de.s42.base.strings;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class StringHelperTest
{

	@Test
	public void simpleInserts()
	{
		// Empty string
		assertEquals(StringHelper.insert("", "Hallo", 0), "Hallo");

		// Empty insert
		assertEquals(StringHelper.insert("Hallo", "", 3), "Hallo");

		// Insert middle
		assertEquals(StringHelper.insert("Hallo", "lila", 2), "Halilallo");

		// Insert end
		assertEquals(StringHelper.insert("Hallo", "li", 5), "Halloli");

		// Insert start
		assertEquals(StringHelper.insert("Hallo", "HoHo", 0), "HoHoHallo");
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void invalidInsertOffsetNegative()
	{
		StringHelper.insert("Hallo", "a", -1);
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void invalidInsertOffsetBehind()
	{
		StringHelper.insert("Hallo", "a", 17);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void invalidNullString()
	{
		StringHelper.insert(null, "a", 1);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void invalidNullInsert()
	{
		StringHelper.insert("Hallo", null, 1);
	}

	@Test
	public void simpleRemoves()
	{
		// Null remove beginning
		assertEquals(StringHelper.remove("Hallo", 0, 0), "Hallo");

		// Remove 1 beginning
		assertEquals(StringHelper.remove("Hallo", 0, 1), "allo");

		// Remove all beginning
		assertEquals(StringHelper.remove("Hallo", 0, 5), "");

		// Remove 1 mid
		assertEquals(StringHelper.remove("Hallo", 2, 1), "Halo");

		// Remove all mid to end
		assertEquals(StringHelper.remove("Hallo", 2, 3), "Ha");

		// Remove all mid to behind end
		assertEquals(StringHelper.remove("Hallo", 3, 99), "Hal");

		// Remove end behind end
		assertEquals(StringHelper.remove("Hallo", 5, 99), "Hallo");
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void invalidNullRemove()
	{
		StringHelper.remove(null, 1, 1);
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void invalidIndesRemove()
	{
		StringHelper.remove("Hallo", 9, 1);
	}
}
