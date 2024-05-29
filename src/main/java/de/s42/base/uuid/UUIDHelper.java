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
package de.s42.base.uuid;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 *
 * @author Benjamin Schiller
 */
public final class UUIDHelper
{

	/**
	 * Validates a string to be a valid uuid pattern
	 */
	public final static Pattern UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");

	private UUIDHelper()
	{
		// never instantiated
	}

	public static boolean isUUID(String string)
	{
		if (string == null) {
			return false;
		}

		return UUID_PATTERN.matcher(string).matches();
	}

	public static byte[] toBytes(UUID uuid)
	{
		assert uuid != null : "uuid != null";

		ByteBuffer result = ByteBuffer.wrap(new byte[16]);
		result.putLong(uuid.getMostSignificantBits());
		result.putLong(uuid.getLeastSignificantBits());

		return result.array();
	}

	public static UUID toUuid(byte[] data)
	{
		assert data != null : "data != null";

		ByteBuffer buf = ByteBuffer.wrap(data);
		long firstLong = buf.getLong();
		long secondLong = buf.getLong();

		return new UUID(firstLong, secondLong);
	}

	/**
	 * Support converting UUID base16 and base58 Strings
	 *
	 * @param uuidLike
	 * @return
	 */
	public static UUID toUUID(String uuidLike)
	{
		assert uuidLike != null : "data != null";

		// Simple base16 UUID
		if (uuidLike.length() == 36) {
			return UUID.fromString(uuidLike);
		}

		// Alternative try base58 UUID
		return UUID58.fromString(uuidLike);
	}
}
