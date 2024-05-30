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

import de.s42.base.validation.ValidationHelper;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Either is an implementation of the either monad. You can encode either 1 of 2 values (first, second) to be contained.
 * It provides many powerful ways to interact with it.
 *
 * @param <FirstType> Type of the first value
 * @param <SecondType> Type of the second value
 *
 * @author Benjamin Schiller
 */
public final class Either<FirstType, SecondType>
{

	/**
	 * Create an Either with one of the given values being non null.
	 *
	 * @param <FirstType> Type of the first value
	 * @param <SecondType> Type of the second value
	 * @param first First value
	 * @param second Second value
	 * @return Either of type &lt;FirstType, SecondType&gt;
	 * @throws IllegalArgumentException if both or none is null (first != null ^ second != null)
	 */
	public static <FirstType, SecondType> Either<FirstType, SecondType> ofEither(FirstType first, SecondType second) throws IllegalArgumentException
	{
		ValidationHelper.isValid(first != null ^ second != null, "Either first or(exclusive) second has to be non null");

		return new Either<>(first, second);
	}

	/**
	 * Create an Either with first.
	 *
	 * @param <FirstType> Type of the first value
	 * @param <SecondType> Type of the second value
	 * @param first First value
	 * @return An Either of first value
	 * @throws NullPointerException if first is null
	 */
	public static <FirstType, SecondType> Either<FirstType, SecondType> ofFirst(FirstType first) throws NullPointerException
	{
		Objects.requireNonNull(first, "first may not be null");

		return new Either<>(first, null);
	}

	/**
	 * Create an Either with second.
	 *
	 * @param <FirstType> Type of the first value
	 * @param <SecondType> Type of the second value
	 * @param second Second value
	 * @return An Either of second value
	 * @throws NullPointerException if second is null
	 */
	public static <FirstType, SecondType> Either<FirstType, SecondType> ofSecond(SecondType second) throws NullPointerException
	{
		Objects.requireNonNull(second, "second may not be null");

		return new Either<>(null, second);
	}

	/**
	 * Stores the first final.
	 */
	protected final FirstType first;

	/**
	 * Stores the second final.
	 */
	protected final SecondType second;

	/**
	 * Internal constructor for an Either.
	 *
	 * @param first First value
	 * @param second Second value
	 */
	private Either(FirstType first, SecondType second)
	{
		assert first != null ^ second != null : "first != null ^ second != null";

		this.first = first;
		this.second = second;
	}

	/**
	 * Calls the action if first is not null with first as parameter.
	 *
	 * @param action Called if first is not null
	 */
	public void ifFirst(Consumer<FirstType> action)
	{
		assert action != null : "action != null";

		if (isFirst()) {
			action.accept(first);
		}
	}

	/**
	 * Calls the action if second is not null with second as parameter.
	 *
	 * @param action Called if second is not null
	 */
	public void ifSecond(Consumer<SecondType> action)
	{
		assert action != null : "action != null";

		if (isSecond()) {
			action.accept(second);
		}
	}

	/**
	 * Calls on of the actions. Either actionFirst with first as parameter if first is not null or actionSecond with
	 * second as parameter.
	 *
	 * @param actionFirst Called if first is not null
	 * @param actionSecond Called if second is not null
	 */
	public void either(Consumer<FirstType> actionFirst, Consumer<SecondType> actionSecond)
	{
		assert actionFirst != null : "actionFirst != null";
		assert actionSecond != null : "actionSecond != null";

		if (isFirst()) {
			actionFirst.accept(first);
		} else {
			actionSecond.accept(second);
		}
	}

	/**
	 * Returns true if first is not null.
	 *
	 * @return True if first is not null
	 */
	public boolean isFirst()
	{
		return first != null;
	}

	/**
	 * Returns true if second is not null.
	 *
	 * @return True if second is not null
	 */
	public boolean isSecond()
	{
		return second != null;
	}

	/**
	 * Returns a nullable Optional of first.
	 *
	 * @return A nullable Optional of first
	 */
	public Optional<FirstType> first()
	{
		return Optional.ofNullable(first);
	}

	/**
	 * Returns a Optional of first or throws a exception if first is null.
	 *
	 * @return Optional of first or throws a exception if first is null
	 * @throws NullPointerException If first is null
	 */
	public FirstType firstOrThrow() throws NullPointerException
	{
		if (!isFirst()) {
			throw new NullPointerException("first is null");
		}

		return first;
	}

	/**
	 * Returns first if first is not null or other if first is null.
	 *
	 * @param other Value that will be returned if first is null
	 * @return first if first is not null or other if first is null
	 */
	public FirstType firstOrElse(FirstType other)
	{
		if (!isFirst()) {
			return other;
		}

		return first;
	}

	/**
	 * Returns a nullable Optional of second.
	 *
	 * @return A nullable Optional of second
	 */
	public Optional<SecondType> second()
	{
		return Optional.ofNullable(second);
	}

	/**
	 * Returns a Optional of second or throws a exception if second is null.
	 *
	 * @return Optional of second or throws a exception if second is null
	 * @throws NullPointerException If second is null
	 */
	public SecondType secondOrThrow() throws NullPointerException
	{
		if (!isSecond()) {
			throw new NullPointerException("second is null");
		}

		return second;
	}

	/**
	 * Returns second if second is not null or other if second is null.
	 *
	 * @param other Value that will be returned if second is null
	 * @return Second if second is not null or other if second is null
	 */
	public SecondType secondOrElse(SecondType other)
	{
		if (!isSecond()) {
			return other;
		}

		return second;
	}

	/**
	 * Returns either the first or the second value. Allows to unwrap easily.
	 *
	 * @return The first if not null or second
	 */
	public Object firstOrSecond()
	{
		if (isFirst()) {
			return first;
		}

		return second;
	}

	/**
	 * The combined hashcode of first and second.
	 *
	 * @return Combined hashcode of first and second
	 */
	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(this.first);
		hash = 79 * hash + Objects.hashCode(this.second);
		return hash;
	}

	/**
	 * Either is equal if first and second are equal.
	 *
	 * @param obj Other Either
	 * @return True if first and second are equal
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Either<?, ?> other = (Either<?, ?>) obj;
		if (!Objects.equals(this.first, other.first)) {
			return false;
		}
		return Objects.equals(this.second, other.second);
	}
}
