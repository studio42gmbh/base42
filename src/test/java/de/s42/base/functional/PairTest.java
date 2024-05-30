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
package de.s42.base.functional;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class PairTest
{

	@Test
	public void pairBoth()
	{
		Pair<String, Double> pair = Pair.of("Test", 42.0);

		assertEquals(pair.first().orElseThrow(), "Test");
		assertEquals(pair.second().orElseThrow(), 42.0);
		assertTrue(pair.isFirst());
		assertTrue(pair.isSecond());

		pair.ifFirst((first) -> {
			assertEquals(first, "Test");
		});

		pair.ifSecond((second) -> {
			assertEquals(second, 42.0);
		});

		assertEquals(pair.firstOrThrow(), "Test");

		assertEquals(pair.firstOrElse("Not"), "Test");

		assertEquals(pair.secondOrThrow(), 42.0);

		assertEquals(pair.secondOrElse(41.0), 42.0);

		pair.each((first) -> {
			assertEquals(first, "Test");
		},
			(second) -> {
				assertEquals(second, 42.0);
			}
		);
	}

	@Test
	public void pairFirst()
	{
		Pair<String, Double> pair = Pair.ofFirst("Test");

		assertEquals(pair.first().orElseThrow(), "Test");
		assertTrue(pair.isFirst());
		assertFalse(pair.isSecond());

		pair.ifFirst((first) -> {
			assertEquals(first, "Test");
		});

		pair.ifSecond((second) -> {
			fail();
		});

		assertEquals(pair.firstOrThrow(), "Test");

		assertEquals(pair.firstOrElse("Not"), "Test");

		assertEquals(pair.secondOrElse(42.0), 42.0);

		assertThrows(NullPointerException.class, () -> {
			pair.secondOrThrow();
		});

		pair.stream().forEach((element) -> {
			assertEquals(element, "Test");
		});

		pair.each((first) -> {
			assertEquals(first, "Test");
		},
			(second) -> {
				fail();
			}
		);
	}

	@Test
	public void pairSecond()
	{
		Pair<Double, String> pair = Pair.ofSecond("Test");

		assertEquals(pair.second().orElseThrow(), "Test");
		assertFalse(pair.isFirst());
		assertTrue(pair.isSecond());

		pair.ifSecond((second) -> {
			assertEquals(second, "Test");
		});

		pair.ifFirst((first) -> {
			fail();
		});

		assertEquals(pair.secondOrThrow(), "Test");

		assertEquals(pair.secondOrElse("Not"), "Test");

		assertEquals(pair.firstOrElse(42.0), 42.0);

		assertThrows(NullPointerException.class, () -> {
			pair.firstOrThrow();
		});

		pair.stream().forEach((element) -> {
			assertEquals(element, "Test");
		});

		pair.each((first) -> {
			fail();
		},
			(second) -> {
				assertEquals(second, "Test");
			}
		);
	}

	@Test
	public void pairEmpty()
	{
		Pair<String, Double> pair = Pair.empty();

		assertFalse(pair.isFirst());
		assertFalse(pair.isSecond());

		pair.ifFirst((first) -> {
			fail();
		});

		pair.ifSecond((second) -> {
			fail();
		});

		assertEquals(pair.firstOrElse("Not"), "Not");

		assertEquals(pair.secondOrElse(41.0), 41.0);

		pair.each((first) -> {
			fail();
		},
			(second) -> {
				fail();
			}
		);
	}

	@Test
	public void equalities()
	{
		Pair<String, Double> pair = Pair.of("Test", 42.0);

		Pair<String, Double> pair2 = Pair.of("Test", 42.0);

		assertTrue(pair.equals(pair2));

		Pair<String, Double> pair3 = Pair.of("Test", 41.0);

		assertFalse(pair.equals(pair3));

		Pair<String, Double> pair4 = Pair.of("TestS", 42.0);

		assertFalse(pair.equals(pair4));
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void invalidUseOfPairBothNull()
	{
		Pair.of(null, null);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void invalidUseOfPairFirstNull()
	{
		Pair.of(null, "Test");
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void invalidUseOfPairSecondNull()
	{
		Pair.of("Test", null);
	}

}
