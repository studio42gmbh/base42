// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2023 Studio 42 GmbH ( https://www.s42m.de ).
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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A pair of 2 values. Both types can be null. It provides handy access.
 *
 * @param <FirstType>
 * @param <SecondType>
 *
 * @author Benjamin Schiller
 */
public final class Pair<FirstType, SecondType>
{

	/**
	 * Create an empty Pair with both of the given values being null.
	 *
	 * @param <FirstType> Type of the first value
	 * @param <SecondType> Type of the second value
	 * @return An empty Pair
	 */
	public static <FirstType, SecondType> Pair<FirstType, SecondType> empty()
	{
		return new Pair<>(null, null);
	}

	/**
	 * Create a Pair with both of the given values being non null.
	 *
	 * @param <FirstType> Type of the first value
	 * @param <SecondType> Type of the second value
	 * @param first First value
	 * @param second Second value
	 * @return Pair of type &lt;FirstType, SecondType&gt;
	 * @throws NullPointerException if either of first or second is null
	 */
	public static <FirstType, SecondType> Pair<FirstType, SecondType> of(FirstType first, SecondType second) throws NullPointerException
	{
		Objects.requireNonNull(first, "first may not be null");
		Objects.requireNonNull(second, "second may not be null");

		return new Pair<>(first, second);
	}

	/**
	 * Create a Pair with first set and second being null
	 *
	 * @param <FirstType> Type of the first value
	 * @param <SecondType> Type of the second value
	 * @param first First value
	 * @return Pair of type &lt;FirstType, SecondType&gt;
	 * @throws NullPointerException if first is null
	 */
	public static <FirstType, SecondType> Pair<FirstType, SecondType> ofFirst(FirstType first) throws NullPointerException
	{
		Objects.requireNonNull(first, "first may not be null");

		return new Pair<>(first, null);
	}

	/**
	 * Create a Pair with second set and first being null
	 *
	 * @param <FirstType> Type of the first value
	 * @param <SecondType> Type of the second value
	 * @param second Second value
	 * @return Pair of type &lt;FirstType, SecondType&gt;
	 * @throws NullPointerException if second is null
	 */
	public static <FirstType, SecondType> Pair<FirstType, SecondType> ofSecond(SecondType second) throws NullPointerException
	{
		Objects.requireNonNull(second, "second may not be null");

		return new Pair<>(null, second);
	}

	/**
	 * Create a Pair with both of the given values being null or non null.
	 *
	 * @param <FirstType> Type of the first value
	 * @param <SecondType> Type of the second value
	 * @param first First value
	 * @param second Second value
	 * @return Either of type &lt;FirstType, SecondType&gt;
	 */
	public static <FirstType, SecondType> Pair<FirstType, SecondType> ofNullable(FirstType first, SecondType second)
	{
		return new Pair<>(first, second);
	}

	/**
	 * Stores the first final.
	 */
	private final FirstType first;

	/**
	 * Stores the second final.
	 */
	private final SecondType second;

	/**
	 * Constructs a pair of first and second. Both values may be null
	 *
	 * @param first First value
	 * @param second Second value
	 */
	private Pair(FirstType first, SecondType second)
	{
		this.first = first;
		this.second = second;
	}

	/**
	 * Calls the action if first is not null with first as parameter.
	 *
	 * @param actionFirst Called if first is not null
	 * @param actionSecond Called if second is not null
	 */
	public void each(Consumer<FirstType> actionFirst, Consumer<SecondType> actionSecond)
	{
		assert actionFirst != null : "actionFirst != null";
		assert actionSecond != null : "actionSecond != null";

		if (isFirst()) {
			actionFirst.accept(first);
		}

		if (isSecond()) {
			actionSecond.accept(second);
		}
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
	 * Returns the optional of first value
	 *
	 * @return Optional of first value
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
	 * Returns a Optional of first or throws a customized exception if first is null.
	 *
	 * @param <ExceptionType>
	 * @param supplier
	 * @return Optional of first or throws a exception if first is null
	 * @throws ExceptionType generated if first is null
	 */
	public <ExceptionType extends Throwable> FirstType firstOrThrow(Supplier<ExceptionType> supplier) throws ExceptionType
	{
		if (!isFirst()) {
			throw supplier.get();
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
	 * Returns the optional of second value
	 *
	 * @return Optional of second value
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
	 * Returns a Optional of second or throws a customized exception if first is null.
	 *
	 * @param <ExceptionType>
	 * @param supplier
	 * @return Optional of second or throws a exception if second is null
	 * @throws ExceptionType generated if second is null
	 */
	public <ExceptionType extends Throwable> SecondType secondOrThrow(Supplier<ExceptionType> supplier) throws ExceptionType
	{
		if (!isSecond()) {
			throw supplier.get();
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
	 * Allows to get a stream to streaming first then second (only contains them if non null)
	 *
	 * @return A stream that streams first then second
	 */
	public Stream<?> stream()
	{
		if (first != null && second != null) {
			return Stream.of(first, second);
		}

		if (first != null) {
			return Stream.of(first);
		}

		if (second != null) {
			return Stream.of(second);
		}

		return Stream.empty();
	}

	/**
	 * Returns a string in the form '[first, second]'
	 *
	 * @return String of first then second
	 */
	@Override
	public String toString()
	{
		return "[" + first + ", " + second + "]";
	}

	/**
	 * The combined hashcode of first and second.
	 *
	 * @return Combined hashcode of first and second
	 */
	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.first);
		hash = 97 * hash + Objects.hashCode(this.second);
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
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		if (!Objects.equals(this.first, other.first)) {
			return false;
		}
		return Objects.equals(this.second, other.second);
	}
}
