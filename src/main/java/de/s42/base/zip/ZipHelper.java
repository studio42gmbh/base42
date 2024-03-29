// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2022 Studio 42 GmbH (https://www.s42m.de).
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
package de.s42.base.zip;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Benjamin Schiller
 */
public final class ZipHelper
{

	private ZipHelper()
	{
		// never instantiated
	}

	/**
	 * Test if the given file is a ZIP archive. See
	 * https://stackoverflow.com/questions/33934178/how-to-identify-a-zip-file-in-java
	 *
	 * @param file this file will be tested if it is an archive
	 * @return true if the file contains the according file signature (fileSignature == 0x504B0304 || fileSignature ==
	 * 0x504B0506 || fileSignature == 0x504B0708)
	 */
	public static boolean isArchive(Path file)
	{
		assert file != null;
		assert Files.isRegularFile(file);

		int fileSignature = 0;
		try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
			fileSignature = raf.readInt();
		} catch (IOException e) {
			// handle if you like
		}

		return isArchive(fileSignature);
	}

	/**
	 * Tests if the file signature is a valid ZIP - see https://en.wikipedia.org/wiki/List_of_file_signatures
	 *
	 * @param fileSignature
	 * @return
	 */
	public static boolean isArchive(int fileSignature)
	{
		return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
	}
}
