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
package de.s42.base.files;

import de.s42.base.conversion.ConversionHelper;
import jakarta.activation.MimetypesFileTypeMap;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.filechooser.FileSystemView;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Benjamin Schiller
 */
public final class FilesHelper
{

	private FilesHelper()
	{
		// never instantiated
	}

	// https://docs.oracle.com/javase/8/docs/api/index.html?javax/activation/MimetypesFileTypeMap.html
	private final static MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();

	public final static String getMimeType(Path file) throws IOException
	{
		assert file != null : "file != null";

		String extension = FilesHelper.getExtension(file);

		// Special handling for calendar attachments
		if (extension.equalsIgnoreCase("ics")) {
			return "text/calendar";
		}

		String mimeType = fileTypeMap.getContentType(file.toFile());

		if (mimeType != null) {
			return mimeType;
		}

		// Default to application/octet-stream instead of null
		return "application/octet-stream";
	}

	public final static String createMavenNetbeansFileConsoleLink(Path file)
	{
		assert file != null : "file != null";

		return createMavenNetbeansFileConsoleLink("", file.getFileName().toString(), file.toAbsolutePath().toString(), 1, 1, false);
	}

	public final static String createMavenNetbeansFileConsoleLink(String message, Path file)
	{
		assert file != null : "file != null";

		return createMavenNetbeansFileConsoleLink(message, file.getFileName().toString(), file.toAbsolutePath().toString(), 1, 1, false);
	}

	public final static String createMavenNetbeansFileConsoleLink(String message, String fileShort, String file, int line, int column)
	{
		return createMavenNetbeansFileConsoleLink(message, fileShort, file, line, column, false);
	}

	/**
	 * See
	 * https://github.com/apache/netbeans/blob/c084119009d2e0f736f225d706bc1827af283501/java/maven/src/org/netbeans/modules/maven/output/GlobalOutputProcessor.java
	 * Line 71 and 174 From https://maven.apache.org/ref/3.0.5/maven-model-builder/ From
	 * https://maven.apache.org/ref/3.0.5/maven-model-builder/apidocs/org/apache/maven/model/building/ModelProblemUtils.html#formatLocation(org.apache.maven.model.building.ModelProblem,%20java.lang.String)
	 *
	 * @param message human readyble message of this info
	 * @param fileShort short name of file without path
	 * @param file complete name of file with absolute path
	 * @param line has to be 1 or higher
	 * @param column has to be 1 or higher
	 * @param sanitize default is false - if true it ensures empty messages get sanitized to match the pattern
	 * @return the prepared string which can be print to console
	 */
	public final static String createMavenNetbeansFileConsoleLink(String message, String fileShort, String file, int line, int column, boolean sanitize)
	{
		assert message != null : "message != null";
		assert fileShort != null : "fileShort != null";
		assert file != null : "file != null";

		line = Math.max(1, line);
		column = Math.max(1, column);

		// needs at least 2 signs for the regex - see source above ref: .+ @ (?:\\S+, (.+), )?line (\\d+), column (\\d+)
		if (message.isEmpty() && sanitize) {
			message = " ";
		}

		StringBuilder msg = new StringBuilder();
		msg
			.append(message)
			.append(" @ ")
			.append(fileShort)
			.append(", ")
			.append(file)
			.append(", line ")
			.append(line)
			.append(", column ")
			.append(column);

		// needs a newline aftetr the column for the regex - see source above ref: .+ @ (?:\\S+, (.+), )?line (\\d+), column (\\d+)
		if (sanitize) {
			msg.append("\n"); // you have to add a newline directly after!
		}

		return msg.toString();
	}

	public final static String getExtension(Path path)
	{
		assert path != null : "path != null";

		return getExtension(path.getFileName().toString());
	}

	public final static String getExtension(String path)
	{
		assert path != null : "path != null";

		int index = path.lastIndexOf('.');

		// No dot return empty ending
		if (index == -1) {
			return "";
		}

		return path.substring(index + 1);
	}

	public final static String sanitizeFileName(String name)
	{
		assert name != null : "name != null";

		// @todo What could be a general sinitization for filenames?
		return name;
	}

	public final static boolean fileExists(String path)
	{
		assert path != null : "path != null";

		return fileExists(Path.of(path));
	}

	public final static boolean fileExists(Path path)
	{
		assert path != null : "path != null";

		return Files.isRegularFile(path);
	}

	public final static boolean directoryExists(String path)
	{
		assert path != null : "path != null";

		return directoryExists(Path.of(path));
	}

	public final static boolean directoryExists(Path path)
	{
		assert path != null : "path != null";

		return Files.isDirectory(path);
	}

	public static String getFileMD5(String path) throws IOException, NoSuchAlgorithmException
	{
		assert path != null : "path != null";

		return getFileMD5(Path.of(path));
	}

	public static String getFileMD5(Path path) throws IOException, NoSuchAlgorithmException
	{
		assert path != null : "path != null";

		MessageDigest digest = MessageDigest.getInstance("MD5");
		InputStream fileInputStream = Files.newInputStream(path);
		DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);

		// Stream into digest
		byte[] bytes = new byte[1024];
		while (digestInputStream.read(bytes) > 0) {
		}

		return ConversionHelper.bytesToHex(digest.digest());
	}

	public static String getFileSHA256(String path) throws IOException, NoSuchAlgorithmException
	{
		assert path != null : "path != null";

		return getFileSHA256(Path.of(path));
	}

	public static String getFileSHA256(Path path) throws IOException, NoSuchAlgorithmException
	{
		assert path != null : "path != null";

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		InputStream fileInputStream = Files.newInputStream(path);
		DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);

		// Stream into digest
		byte[] bytes = new byte[1024];
		while (digestInputStream.read(bytes) > 0) {
		}

		return ConversionHelper.bytesToHex(digest.digest());
	}

	public final static JSONObject getFileAsJSON(String path) throws IOException, JSONException
	{
		assert path != null : "path != null";

		return getFileAsJSON(Path.of(path));
	}

	public final static JSONObject getFileAsJSON(Path path) throws IOException, JSONException
	{
		assert path != null : "path != null";

		return new JSONObject(getFileAsString(path));
	}

	public final static String getFileAsString(String path) throws IOException
	{
		assert path != null : "path != null";

		return getFileAsString(Path.of(path));
	}

	public final static String getFileAsString(Path path) throws IOException
	{
		assert path != null : "path != null";

		byte[] encoded = Files.readAllBytes(path);
		return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
	}

	public final static void writeStringToFile(String path, String content) throws IOException
	{
		assert path != null : "path != null";
		assert content != null : "content != null";

		writeStringToFile(Path.of(path), content);
	}

	public final static void writeStringToFile(Path path, String content) throws IOException
	{
		assert path != null : "path != null";
		assert content != null : "content != null";

		if (!Files.exists(path)) {
			Files.createDirectories(path.getParent());
		}

		Files.writeString(path, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	public final static void writeStringToZippedSingleFile(String path, String content, String entryName) throws IOException
	{
		assert path != null : "path != null";
		assert content != null : "content != null";
		assert entryName != null : "entryName != null";

		writeStringToZippedSingleFile(Path.of(path), content, entryName);
	}

	public final static void writeStringToZippedSingleFile(Path path, String content, String entryName) throws IOException
	{
		assert path != null : "path != null";
		assert content != null : "content != null";
		assert entryName != null : "entryName != null";

		if (!Files.exists(path)) {
			Files.createDirectories(path.getParent());
		}

		try (OutputStream out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {

			((ZipOutputStream) out).putNextEntry(new ZipEntry(entryName));

			out.write(content.getBytes(StandardCharsets.UTF_8));
			out.flush();
		}
	}

	public final static void writeByteArrayToFile(String path, byte[] content) throws IOException
	{
		assert path != null : "path != null";
		assert content != null : "content != null";

		writeByteArrayToFile(Path.of(path), content);
	}

	public final static void writeByteArrayToFile(Path path, byte[] content) throws IOException
	{
		assert path != null : "path != null";
		assert content != null : "content != null";

		if (!Files.exists(path)) {
			Files.createDirectories(path.getParent());
		}

		Files.write(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	public final static void moveFile(String sourcePath, String targetPath, boolean createDirectories, boolean overwrite) throws IOException
	{
		assert sourcePath != null : "sourcePath != null";
		assert targetPath != null : "targetPath != null";

		moveFile(Path.of(sourcePath), Path.of(targetPath), createDirectories, overwrite);
	}

	public final static void moveFile(Path sourcePath, Path targetPath, boolean createDirectories, boolean overwrite) throws IOException
	{
		assert sourcePath != null : "sourcePath != null";
		assert targetPath != null : "targetPath != null";

		if (createDirectories) {
			if (!Files.exists(targetPath)) {
				Files.createDirectories(targetPath.getParent());
			}
		}

		if (overwrite) {
			Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
		} else {
			Files.move(sourcePath, targetPath);
		}
	}

	public final static void copyFile(String sourcePath, String targetPath, boolean createDirectories, boolean overwrite) throws IOException
	{
		assert sourcePath != null : "sourcePath != null";
		assert targetPath != null : "targetPath != null";

		copyFile(Path.of(sourcePath), Path.of(targetPath), createDirectories, overwrite);
	}

	public final static void copyFile(Path sourcePath, Path targetPath, boolean createDirectories, boolean overwrite) throws IOException
	{
		assert sourcePath != null : "sourcePath != null";
		assert targetPath != null : "targetPath != null";

		if (createDirectories) {
			if (!Files.exists(targetPath)) {
				Files.createDirectories(targetPath.getParent());
			}
		}

		if (overwrite) {
			Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
		} else {
			Files.copy(sourcePath, targetPath);
		}
	}

	public final static void emptyDirectory(String path) throws IOException
	{
		assert path != null : "path != null";

		emptyDirectory(Path.of(path));
	}

	public final static void emptyDirectory(Path path) throws IOException
	{
		assert path != null : "path != null";

		try (Stream<Path> walk = Files.walk(path)) {
			walk.sorted(Comparator.reverseOrder())
				.filter((p) -> {
					return !p.equals(path);
				})
				.map(Path::toFile)
				//.peek(log::info)
				.forEach(File::delete);
		}
	}

	public static Path getUserHome()
	{
		return FileSystemView.getFileSystemView().getHomeDirectory().toPath();
	}

	public static Path getWorkingDirectory()
	{
		return Path.of(System.getProperty("user.dir")).toAbsolutePath();
	}

	public static Path pathOf(String fileName)
	{
		assert fileName != null;

		return Path.of(fileName).toAbsolutePath().normalize();
	}

	public static ByteBuffer getFileAsByteBuffer(String fileName) throws IOException
	{
		assert fileName != null : "fileName != null";

		return getFileAsByteBuffer(Path.of(fileName));
	}

	public static ByteBuffer getFileAsByteBuffer(Path filePath) throws IOException
	{
		assert filePath != null : "filePath != null";
		assert Files.isRegularFile(filePath) : "Is not a readable file " + FilesHelper.createMavenNetbeansFileConsoleLink(filePath);

		return ByteBuffer.wrap(Files.readAllBytes(filePath));
	}

	public static MappedByteBuffer getFileAsMappedByteBuffer(String fileName) throws IOException
	{
		assert fileName != null : "fileName != null";

		return getFileAsMappedByteBuffer(Path.of(fileName));
	}

	public static MappedByteBuffer getFileAsMappedByteBuffer(Path filePath) throws IOException
	{
		assert filePath != null : "filePath != null";
		assert Files.isRegularFile(filePath) : "Is not a readable file " + FilesHelper.createMavenNetbeansFileConsoleLink(filePath);

		MappedByteBuffer buffer;
		File file = filePath.toFile();
		if (!file.isFile()) {
			throw new IOException("File not found " + filePath);
		}

		try (FileInputStream fis = new FileInputStream(file); FileChannel fc = fis.getChannel()) {
			buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		}

		return buffer;
	}

	public final static String getZippedSingleFileAsString(String fileName) throws IOException
	{
		assert fileName != null : "fileName != null";

		return new String(getZippedSingleFileAsByteArray(fileName), StandardCharsets.UTF_8);
	}

	public final static String getZippedSingleFileAsString(Path filePath) throws IOException
	{
		assert filePath != null : "filePath != null";

		return new String(getZippedSingleFileAsByteArray(filePath), StandardCharsets.UTF_8);
	}

	public final static byte[] getZippedSingleFileAsByteArray(String fileName) throws IOException
	{
		assert fileName != null : "fileName != null";

		return getZippedSingleFileAsByteArray(Path.of(fileName));
	}

	public final static byte[] getZippedSingleFileAsByteArray(Path filePath) throws IOException
	{
		assert filePath != null : "filePath != null";
		assert Files.isRegularFile(filePath) : "Files.isRegularFile(filePath)";

		File file = filePath.toFile();
		if (!file.isFile()) {
			throw new IOException("File not found " + filePath);
		}

		ByteArrayOutputStream baos;
		try (ZipInputStream zipStream = new ZipInputStream(new FileInputStream(file))) {
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

	public static void saveImageAsFilePng(BufferedImage image, String outputFile) throws IOException
	{
		assert image != null : "image != null";
		assert outputFile != null : "outputFile != null";

		saveImageAsFilePng(image, Path.of(outputFile));
	}

	public static void saveImageAsFilePng(BufferedImage image, Path outputFile) throws IOException
	{
		assert image != null : "image != null";
		assert outputFile != null : "outputFile != null";

		ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
		ImageWriter writer = ImageIO.getImageWriters(type, "png").next();

		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(0.0f);

		try (ImageOutputStream out = new FileImageOutputStream(outputFile.toFile())) {
			writer.setOutput(out);
			writer.write(null, new IIOImage(image, null, null), param);
			writer.dispose();
		}
	}

	public static void saveImageAsFileJpg(BufferedImage image, String outputFile, float quality) throws IOException
	{
		assert image != null : "image != null";
		assert outputFile != null : "outputFile != null";

		saveImageAsFileJpg(image, Path.of(outputFile), quality);
	}

	public static void saveImageAsFileJpg(BufferedImage image, Path outputFile, float quality) throws IOException
	{
		assert image != null : "image != null";
		assert outputFile != null : "outputFile != null";
		assert quality >= 0.0f && quality <= 1.0f : "quality >= 0.0f && quality <= 1.0f";

		// @todo might want to check if the colormodel is able to be written into a jpg -> give proper error then (default error is rather weird)
		ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
		ImageWriter writer = ImageIO.getImageWriters(type, "jpg").next();

		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);

		try (ImageOutputStream out = new FileImageOutputStream(outputFile.toFile())) {
			writer.setOutput(out);
			writer.write(null, new IIOImage(image, null, null), param);
			writer.dispose();
		}
	}

	public static BufferedImage getFileAsScaledImage(Path file, int width, int height)
	{
		assert file != null : "file != null";
		assert width > 0 : "width > 0";
		assert height > 0 : "height > 0";

		BufferedImage original = getFileAsImage(file);
		Image scaled = original.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
		BufferedImage scaledBuffered = new BufferedImage(width, height, original.getType());
		Graphics g = scaledBuffered.getGraphics();
		g.drawImage(scaled, 0, 0, null);
		g.dispose();
		original.flush();
		scaled.flush();
		return scaledBuffered;
	}

	public static BufferedImage getFileAsImage(Path file)
	{
		assert file != null : "file != null";

		try {
			BufferedImage image;
			image = ImageIO.read(file.toFile());
			return image;
		} catch (IOException ex) {
			throw new RuntimeException("Unable to read file " + file.normalize().toAbsolutePath().toString() + " - " + ex.getMessage(), ex);
		}
	}
}
