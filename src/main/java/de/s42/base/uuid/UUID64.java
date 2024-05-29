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

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/**
 * UUID58 provides uuid base64 string encoding and decoding for UUIDs to shorten, improve readability and c&p support in
 * string form.
 *
 * uuid64 strings look like: , ...
 *
 * @author Benjamin Schiller
 */
public final class UUID64
{

	private UUID64()
	{
		// Never instantiated
	}

	/**
	 * Converts a uuid base64 string into a UUID.
	 *
	 * @param uuid64 A valid uuid base64 string
	 * @return Restored UUID from base64 string
	 * @throws IllegalArgumentException If the string is too long
	 * @throws IndexOutOfBoundsException If a character is contained which is not part of the Symbols
	 * @throws NullPointerException If the parameter uuid58 is null
	 */
	public static UUID fromString(String uuid64) throws IndexOutOfBoundsException, IllegalArgumentException, NullPointerException
	{
		Objects.requireNonNull(uuid64, "uuid64 must not be null");

		if (uuid64.length() != 22) {
			throw new IllegalArgumentException("UUID64 string must equal to 22 signs but is " + uuid64.length());
		}

		byte[] decodedBytes = Base64.getUrlDecoder().decode(uuid64);

		if (decodedBytes.length != 16) {
			throw new IllegalArgumentException("UUID64 string decode to 16 bytes");
		}

		// Wrap and build UUID
		ByteBuffer buf = ByteBuffer.wrap(decodedBytes);
		return new UUID(buf.getLong(), buf.getLong());
	}

	/**
	 * Converts a UUID into a uuid base58 string.
	 *
	 * @param uuid The UUID to be converted
	 * @return The uuid base64 string using
	 * @throws NullPointerException If the parameter uuid58 is null
	 */
	public static String toString(UUID uuid) throws NullPointerException
	{
		Objects.requireNonNull(uuid, "uuid must not be null");

		ByteBuffer buf = ByteBuffer.allocate(16);
		buf.putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits()).flip();

		return Base64.getUrlEncoder().withoutPadding().encodeToString(buf.array());
	}
}
