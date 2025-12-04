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
 *
 * @author Benjamin Schiller
 */
public class SingleFileChangeWatcher
{

	public static Pair<SingleFileChangeWatcher, Thread> startWatching(Path file, Consumer<Path> handler)
	{
		assert file != null : "file != null";
		assert handler != null : "handler != null";

		SingleFileChangeWatcher watcher = new SingleFileChangeWatcher(file, handler);
		Thread thread = watcher.startWatching();

		return Pair.of(watcher, thread);
	}

	@SuppressWarnings("unchecked")
	protected static <EventType> WatchEvent<EventType> cast(WatchEvent<?> event)
	{
		return (WatchEvent<EventType>) event;
	}

	protected final Path file;
	protected final Consumer<Path> handler;
	protected WatchService watcher;
	protected boolean terminated;

	public SingleFileChangeWatcher(Path file, Consumer<Path> handler)
	{
		assert file != null : "file != null";
		assert handler != null : "handler != null";

		this.file = file;
		this.handler = handler;
	}

	public void cancel() throws IOException
	{
		if (watcher != null) {
			watcher.close();
			watcher = null;
		}
		terminated = true;
	}

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

	public void watchImmediate() throws Exception
	{
		if (watcher != null) {
			throw new RuntimeException("watcher != null");
		}

		watcher = FileSystems.getDefault().newWatchService();

		while (!terminated) {

			/* WatchKey regKey = */
			file.getParent().register(watcher,
				//ENTRY_CREATE,
				//ENTRY_DELETE,
				ENTRY_MODIFY);

			// Wait for key to be signaled
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

						WatchEvent<Path> ev = cast(event);
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
