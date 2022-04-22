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
package de.s42.base.compile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 *
 * @author Benjamin Schiller
 */
public final class CompileHelper
{

	private CompileHelper()
	{
		// never instantiated
	}

	static final class JavaFileObject extends SimpleJavaFileObject
	{

		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		JavaFileObject(String name, JavaFileObject.Kind kind)
		{
			super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
		}

		byte[] getBytes()
		{
			return os.toByteArray();
		}

		@Override
		public OutputStream openOutputStream()
		{
			return os;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors)
		{
			return new String(os.toByteArray(), StandardCharsets.UTF_8);
		}
	}

	static final class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager>
	{

		private final Map<String, JavaFileObject> fileObjectMap;
		private Map<String, byte[]> classes;

		ClassFileManager(StandardJavaFileManager standardManager)
		{
			super(standardManager);
			fileObjectMap = new HashMap<>();
		}

		@Override
		public ClassLoader getClassLoader(Location location)
		{
			return super.getClassLoader(location);
		}

		@Override
		public JavaFileObject getJavaFileForOutput(
			JavaFileManager.Location location,
			String className,
			JavaFileObject.Kind kind,
			FileObject sibling
		)
		{
			JavaFileObject result = new JavaFileObject(className, kind);
			fileObjectMap.put(className, result);
			return result;
		}

		boolean isEmpty()
		{
			return fileObjectMap.isEmpty();
		}

		Map<String, byte[]> getClasses()
		{
			if (classes == null) {
				classes = new HashMap<>();

				for (Map.Entry<String, JavaFileObject> entry : fileObjectMap.entrySet()) {
					classes.put(entry.getKey(), entry.getValue().getBytes());
				}
			}

			return classes;
		}

		Class<?> loadAndReturnMainClass(String mainClassName, ClassLoader classLoader) throws Exception
		{
			ByteClassLoader l = new ByteClassLoader(new URL[0], classLoader, getClasses());
			return l.findClass(mainClassName);
		}
	}

	public static class ByteClassLoader extends URLClassLoader
	{

		private final Map<String, byte[]> extraClassDefs;

		public ByteClassLoader(URL[] urls, ClassLoader parent, Map<String, byte[]> extraClassDefs)
		{
			super(urls, parent);
			this.extraClassDefs = new HashMap<>(extraClassDefs);
		}

		@Override
		protected Class<?> findClass(final String name) throws ClassNotFoundException
		{
			byte[] classBytes = this.extraClassDefs.remove(name);
			if (classBytes != null) {
				return defineClass(name, classBytes, 0, classBytes.length);
			}
			return super.findClass(name);
		}
	}

	static final class CharSequenceJavaFileObject extends SimpleJavaFileObject
	{

		final CharSequence content;

		public CharSequenceJavaFileObject(String className, CharSequence content)
		{
			super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
			this.content = content;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors)
		{
			return content;
		}
	}

	private static ClassFileManager compileInternal(String javaClassCode, String className, ClassLoader classLoader, String classPath) throws InvalidCompilation
	{
		assert classLoader != null;

		//log.trace("Code", javaClassCode);
		try {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

			// @improvement ugly fallback - do we really want to do this? actually solving ant task usage
			if (compiler == null) {

				compiler = (JavaCompiler) Class.forName("com.sun.tools.javac.api.JavacTool").getConstructor().newInstance();

				if (compiler == null) {
					throw new RuntimeException("Could not get ToolProvider.getSystemJavaCompiler()");
				}
			}

			ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(null, null, null));

			List<CharSequenceJavaFileObject> files = new ArrayList<>();
			files.add(new CharSequenceJavaFileObject(className, javaClassCode));
			StringWriter out = new StringWriter();

			List<String> options = new ArrayList<>();

			if (classPath == null) {

				StringBuilder classpath = new StringBuilder();
				String separator = System.getProperty("path.separator");
				String cp = System.getProperty("java.class.path");
				String mp = System.getProperty("jdk.module.path");

				if (cp != null && !"".equals(cp)) {
					classpath.append(cp);
				}
				if (mp != null && !"".equals(mp)) {
					classpath.append(mp);
				}

				if (classLoader instanceof URLClassLoader) {
					for (URL url : ((URLClassLoader) classLoader).getURLs()) {
						if (classpath.length() > 0) {
							classpath.append(separator);
						}

						if ("file".equals(url.getProtocol())) {
							classpath.append(new File(url.toURI()));
						}
					}
				}

				//log.trace("classPath", classpath.toString());
				options.addAll(Arrays.asList("-classpath", classpath.toString()));
			} else {

				//log.trace("classPath", classPath);
				options.addAll(Arrays.asList("-classpath", classPath));
			}

			JavaCompiler.CompilationTask task = compiler.getTask(out, fileManager, null, options, null, files);

			boolean runSuccess = task.call();

			if (!runSuccess) {
				throw new InvalidCompilation(out.toString());
			}

			return fileManager;
		} catch (InvalidCompilation | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | RuntimeException | InvocationTargetException | URISyntaxException ex) {
			throw new InvalidCompilation(ex.getMessage(), ex);
		}
	}

	public static byte[] getCompiledClassData(String javaClassCode, String className, ClassLoader classLoader, String classPath) throws InvalidCompilation
	{
		if (classLoader == null) {
			classLoader = CompileHelper.class.getClassLoader();
		}

		ClassFileManager fileManager = compileInternal(javaClassCode, className, classLoader, classPath);

		className = (String) fileManager.getClasses().keySet().toArray()[0];

		return fileManager.getClasses().get(className);
	}

	public static Class getCompiledClass(String javaClassCode, String className) throws InvalidCompilation
	{
		return getCompiledClass(javaClassCode, className, null, null);
	}

	public static Class getCompiledClass(String javaClassCode, String className, ClassLoader classLoader, String classPath) throws InvalidCompilation
	{
		if (classLoader == null) {
			classLoader = CompileHelper.class.getClassLoader();
		}

		ClassFileManager fileManager = compileInternal(javaClassCode, className, classLoader, classPath);

		className = (String) fileManager.getClasses().keySet().toArray()[0];

		try {
			return fileManager.loadAndReturnMainClass(className, classLoader);
		} catch (Exception ex) {
			throw new InvalidCompilation(ex.getMessage(), ex);
		}
	}

	public static <InstanceType> InstanceType getCompiledInstance(String javaClassCode, String className) throws InvalidCompilation
	{
		return getCompiledInstance(javaClassCode, className, null, null);
	}

	@SuppressWarnings("unchecked")
	public static <InstanceType> InstanceType getCompiledInstance(String javaClassCode, String className, ClassLoader classLoader, String classPath) throws InvalidCompilation
	{
		Class compiledClass = getCompiledClass(javaClassCode, className, classLoader, classPath);

		try {
			return (InstanceType) compiledClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
			throw new InvalidCompilation(ex.getMessage(), ex);
		}
	}
}
