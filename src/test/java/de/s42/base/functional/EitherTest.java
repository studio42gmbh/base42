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
public class EitherTest
{

	@Test
	public void useFirst()
	{
		Either<String, Double> either = Either.ofFirst("Test");

		assertEquals(either.first().orElseThrow(), "Test");
		assertTrue(either.isFirst());
		assertFalse(either.isSecond());

		either.ifFirst((first) -> {
			assertEquals(first, "Test");
		});

		either.ifSecond((second) -> {
			fail();
		});

		either.either(
			(first) -> {
				assertEquals(first, "Test");
			},
			(second) -> {
				fail();
			}
		);

		assertEquals(either.firstOrThrow(), "Test");

		assertEquals(either.firstOrElse("Not"), "Test");

		assertEquals(either.secondOrElse(42.0), 42.0);

		assertThrows(NullPointerException.class, () -> {
			either.secondOrThrow();
		});

		assertEquals(either.firstOrSecond(), "Test");

		either.stream().forEach((element) -> {
			assertEquals(element, "Test");
		});
	}

	@Test
	public void useSecond()
	{
		Either<String, Double> either = Either.ofSecond(42.0);

		assertEquals(either.second().orElseThrow(), 42.0);
		assertFalse(either.isFirst());
		assertTrue(either.isSecond());

		either.ifFirst((first) -> {
			fail();
		});

		either.ifSecond((second) -> {
			assertEquals(second, 42.0);
		});

		either.either(
			(first) -> {
				fail();
			},
			(second) -> {
				assertEquals(second, 42.0);
			}
		);

		assertEquals(either.secondOrThrow(), 42.0);

		assertEquals(either.secondOrElse(21.0), 42.0);

		assertEquals(either.firstOrElse("Test"), "Test");

		assertThrows(NullPointerException.class, () -> {
			either.firstOrThrow();
		});

		assertEquals(either.firstOrSecond(), 42.0);

		either.stream().forEach((element) -> {
			assertEquals(element, 42.0);
		});
	}

	@Test
	public void equalitiesSecond()
	{
		Either<String, Double> either = Either.ofSecond(42.0);

		Either<String, Double> either2 = Either.ofEither(null, 42.0);

		assertTrue(either.equals(either2));

		Either<String, Double> either3 = Either.ofEither(null, 41.0);

		assertFalse(either.equals(either3));

		Either<String, Double> either4 = Either.ofEither("Test", null);

		assertFalse(either.equals(either4));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void invalidUseOfEitherBothNull()
	{
		Either.ofEither(null, null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void invalidUseOfEitherBothNotNull()
	{
		Either.ofEither("Test", 42.0);
	}
}
