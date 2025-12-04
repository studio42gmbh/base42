// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2025 Studio 42 GmbH ( https://www.s42m.de ).
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

import de.s42.base.functional.Pair;
import java.io.IOException;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Allows to watch changes on a given single normal file.
 *
 * The easiest way to use it is:
 * <pre>
 * {@code
 * Pair<SingleFileChangeWatcher, Thread> watcher = SingleFileChangeWatcher.startWatching(file, (file) -> {
 *   // ... do something with changed file ...
 * });
 * }
 * </pre>
 *
 * @author Benjamin Schiller
 */
public class SingleFileChangeWatcher
{

	/**
	 * Creates and starts the watcher. The thread is a virtual thread.
	 *
	 * @param file
	 * @param handler
	 * @return
	 */
	public static Pair<SingleFileChangeWatcher, Thread> startWatching(Path file, Consumer<Path> handler)
	{
		assert file != null : "file != null";
		assert handler != null : "handler != null";

		SingleFileChangeWatcher watcher = new SingleFileChangeWatcher(file, handler);
		Thread thread = watcher.startWatching();

		return Pair.of(watcher, thread);
	}

	/**
	 * File to watch changes on.
	 */
	protected final Path file;

	/**
	 * handler that will be called back on changes of file.
	 */
	protected final Consumer<Path> handler;

	/**
	 * FS integration for watching changes
	 */
	protected WatchService watcher;

	/**
	 * will be set to true on cancel().
	 */
	protected boolean terminated;

	/**
	 * Create a watcher for the given file and given a callback that will be called whenever the file has changed in the
	 * filesystem
	 *
	 * @param file
	 * @param handler
	 */
	public SingleFileChangeWatcher(Path file, Consumer<Path> handler)
	{
		assert file != null : "file != null";
		assert handler != null : "handler != null";

		this.file = file;
		this.handler = handler;
	}

	/**
	 * Allows to cancel the running watcher
	 *
	 * @throws IOException
	 */
	public void cancel() throws IOException
	{
		if (watcher != null) {
			watcher.close();
			watcher = null;
		}
		terminated = true;
	}

	/**
	 * Starts this watcher creating a virtual thread.
	 *
	 * @return
	 * @throws RuntimeException
	 */
	public Thread startWatching() throws RuntimeException
	{
		return Thread.startVirtualThread(() -> {
			try {
				watchImmediate();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});
	}

	/**
	 * Can be called by a worker directly. ATTENTION this method will block until the watcher is cancellde or
	 * invalidated! It is intended to be used if you want to control the execution (own runners, thread, etc.).
	 *
	 * @throws Exception
	 */
	public void watchImmediate() throws Exception
	{
		if (watcher != null) {
			watcher.close();
		}

		watcher = FileSystems.getDefault().newWatchService();
		terminated = false;

		while (!terminated) {

			/* WatchKey regKey = */
			file.getParent().register(watcher,
				//ENTRY_CREATE,
				//ENTRY_DELETE,
				ENTRY_MODIFY);

			// Wait for key to be signaled - poll all 100 ms
			WatchKey key;
			try {
				key = watcher.poll(100, TimeUnit.MILLISECONDS);
			} catch (ClosedWatchServiceException | InterruptedException ex) {

				if (watcher != null) {
					watcher.close();
				}

				return;
			}

			if (key != null) {

				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();

					// There might be OVERFLOW events which we ignore
					if (kind == ENTRY_MODIFY) {

						@SuppressWarnings("unchecked")
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path changedEntryPathName = ev.context();
						Path changedEntryPath = file.getParent().resolve(changedEntryPathName);

						// Check if the change in the watched dir is about the given file
						if (changedEntryPath.equals(file)) {

							long fileLength = changedEntryPath.toFile().length();

							// Just inform on changes that provide a file with content
							if (fileLength > 0) {
								handler.accept(file);
							}
						}
					}
				}

				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		}
	}
}
