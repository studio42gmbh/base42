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
package de.s42.base.uuid;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

/**
 * UUID58 provides uuid base58 string encoding and decoding for UUIDs to shorten, improve readability and c&p support in
 * string form.
 *
 * uuid58 strings look like: hxAGoWGvFPR35qxCG5fYuX, ouyeFFWJLjnNeJfjs9DcvY, cyAp8zexLqUQRGFxRVcBCF, ...
 *
 * @author Benjamin Schiller
 */
public final class UUID58
{

	/**
	 * This is the symbols used for the encodet string to improve readability avoiding similar chars like 0 and O or 1
	 * and I
	 */
	public static final String SYMBOLS = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
	public static final char[] SYMBOLS_CHARS = SYMBOLS.toCharArray();

	/**
	 * The radix 58 as BigInteger
	 */
	public static final BigInteger RADIX = BigInteger.valueOf(58L);

	private UUID58()
	{
		// Never instantiated
	}

	/**
	 * Converts a uuid base58 string into a UUID.
	 *
	 * ATTENTION: A uuid base58 string is often 22 signs, BUT can be as short as 18 chars if the uuid (i.e. v4) has many
	 * leading 0
	 *
	 * @param uuid58 A valid uuid base58 string
	 * @return Restored UUID from uuid58 string
	 * @throws IllegalArgumentException If the string is too long
	 * @throws IndexOutOfBoundsException If a character is contained which is not part of the Symbols
	 * @throws NullPointerException If the parameter uuid58 is null
	 */
	public static UUID fromString(String uuid58) throws IndexOutOfBoundsException, IllegalArgumentException, NullPointerException
	{
		Objects.requireNonNull(uuid58, "uuid58 must not be null");

		// Theoretically the string might be much shorter if the uuid (i.e. v4) has many leading 0
		if (uuid58.length() > 22) {
			throw new IllegalArgumentException("UUID58 string must below or equal to 22 signs but is " + uuid58.length());
		}

		BigInteger current = BigInteger.ZERO;

		for (char ch : uuid58.toCharArray()) {
			current = current.multiply(RADIX);
			current = current.add(BigInteger.valueOf(SYMBOLS.indexOf(ch)));
		}

		byte[] decodedArray = current.toByteArray();

		// Optionally add or remove bytes to bring it to 16
		if (decodedArray.length < 16) {
			byte[] tmp = new byte[16];
			System.arraycopy(decodedArray, 0, tmp, 16 - decodedArray.length, decodedArray.length);
			decodedArray = tmp;
		} else if (decodedArray.length > 16) {
			byte[] tmp = new byte[16];
			System.arraycopy(decodedArray, decodedArray.length - 16, tmp, 0, 16);
			decodedArray = tmp;
		}

		// Wrap and build UUID
		ByteBuffer buf = ByteBuffer.wrap(decodedArray);
		return new UUID(buf.getLong(), buf.getLong());
	}

	/**
	 * Converts a UUID into a uuid base58 string.
	 *
	 * @param uuid The UUID to be converted
	 * @return The uuid base58 string
	 * @throws NullPointerException If the parameter uuid58 is null
	 */
	public static String toString(UUID uuid) throws NullPointerException
	{
		Objects.requireNonNull(uuid, "uuid must not be null");

		// 17 bytes (2 x 8 + 1) to provide space for 2 long (= uuid) and 1 pad
		ByteBuffer buf = ByteBuffer.allocate(17);
		buf.put((byte) 0); // Padding to avoid negative numbers (2 - complement)
		buf.putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits()).flip();

		// Is always positive
		BigInteger current = new BigInteger(buf.array());

		// Will contain [Quot, Remainder] from BigInteger.divideAndRemainder
		BigInteger qr[];
		StringBuilder builder = new StringBuilder();

		// Divide current by 58 and add looked up char [remainder] to builder until nothing left
		// Hint: padding before made sure we always have a positive big int
		while (current.compareTo(BigInteger.ZERO) > 0) {

			qr = current.divideAndRemainder(RADIX);

			// Append the looked up symbol at index remainder
			builder.append(SYMBOLS_CHARS[qr[1].intValue()]);

			// Make current to be the quotient
			current = qr[0];
		}

		// Reverse to provide proper endianess
		return builder.reverse().toString();
	}
}
