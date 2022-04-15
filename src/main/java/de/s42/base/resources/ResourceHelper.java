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
package de.s42.base.resources;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Benjamin Schiller
 */
public final class ResourceHelper
{

	private ResourceHelper()
	{
		// never instantiated
	}

	public static boolean hasResource(Class refClass, String relativeResourceName)
	{
		assert refClass != null;
		assert relativeResourceName != null;

		return (refClass.getResource(relativeResourceName) != null);
	}

	public static boolean hasResource(String resourceName)
	{
		assert resourceName != null;

		return (Thread.currentThread().getContextClassLoader().getResource(resourceName) != null);
	}

	public static Optional<String> getResourceAsString(String resourceName) throws IOException
	{
		assert resourceName != null;

		if (!hasResource(resourceName)) {
			return Optional.empty();
		}

		ByteArrayOutputStream result;
		try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
			result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
		}
		return Optional.of(result.toString(StandardCharsets.UTF_8.name()));
	}

	public static Optional<String> getResourceAsString(Class refClass, String relativeResourceName) throws IOException
	{
		assert refClass != null;
		assert relativeResourceName != null;

		if (!hasResource(refClass, relativeResourceName)) {
			return Optional.empty();
		}

		ByteArrayOutputStream result;
		try (InputStream inputStream = refClass.getResourceAsStream(relativeResourceName)) {
			result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
		}
		return Optional.of(result.toString(StandardCharsets.UTF_8.name()));
	}

	public final static String getZippedSingleFileResourceAsString(String resourceName) throws IOException
	{
		return new String(getZippedSingleFileResourceAsByteArray(resourceName), "UTF-8");
	}

	public final static byte[] getZippedSingleFileResourceAsByteArray(String resourceName) throws IOException
	{
		ByteArrayOutputStream baos;
		try (ZipInputStream zipStream = new ZipInputStream(ResourceHelper.class.getClassLoader().getResourceAsStream(resourceName))) {
			zipStream.getNextEntry();
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int read;
			while ((read = zipStream.read(buffer)) > 0) {
				baos.write(buffer, 0, read);
			}
		}
		return baos.toByteArray();
	}
	
	public final static InputStream getResourceAsStream(final Class moduleClass, final String relativeResourceName)
	{
		InputStream in = moduleClass.getResourceAsStream(relativeResourceName);

		if (in == null) {
			throw new RuntimeException("Mssing resource '" + relativeResourceName + "' in module " + moduleClass.getName());
		}

		return in;
	}

	public final static InputStream getResourceAsStream(final String resourceName)
	{
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);

		if (in == null) {
			throw new RuntimeException("Mssing resource '" + resourceName + "'");
		}

		return in;
	}	
	
	public final static Icon getResourceAsIcon(Class moduleClass, String relativeResourceName, int width, int height)
	{
		return new ImageIcon(getResourceAsImage(moduleClass, relativeResourceName).getScaledInstance(width, height, BufferedImage.SCALE_AREA_AVERAGING));
	}
	
	public final static BufferedImage getResourceAsImage(Class moduleClass, String relativeResourceName)
	{
		try {
			BufferedImage image;
			try (InputStream in = getResourceAsStream(moduleClass, relativeResourceName)) {
				image = ImageIO.read(in);
			}
			return image;
		}
		catch (IOException ex) {
			throw new RuntimeException("Unable to read module resource " + relativeResourceName + " - " + ex.getMessage(), ex);
		}
	}
	
	public final static BufferedImage getResourceAsImage(String relativeResourceName)
	{
		try {
			BufferedImage image;
			try (InputStream in = getResourceAsStream(relativeResourceName)) {
				image = ImageIO.read(in);
			}
			return image;
		}
		catch (IOException ex) {
			throw new RuntimeException("Unable to read module resource " + relativeResourceName + " - " + ex.getMessage(), ex);
		}
	}
	
}
