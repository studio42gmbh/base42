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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Benjamin Schiller
 */
public class UUID58Test
{

	public final static int TEST_COUNT = 10;

	@DataProvider(name = "randomUUIDs")
	public Object[][] randomUUIDs()
	{
		List<UUID[]> uuids = new ArrayList<>(TEST_COUNT);
		for (int i = 0; i < TEST_COUNT; ++i) {

			UUID[] uuid = new UUID[]{UUID.randomUUID()};
			uuids.add(uuid);
		}

		return uuids.toArray(UUID[][]::new);
	}

	@Test(dataProvider = "randomUUIDs")
	public void createUUID58String(UUID uuid)
	{
		String uuid58 = UUID58.toString(uuid);

		UUID uuidRestored = UUID58.fromString(uuid58);

		assertEquals(uuid, uuidRestored);
	}
}
