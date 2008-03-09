//  Copyright (c) 1998-2008 Adrian Kuhn <akuhn(a)iam.unibe.ch>
//  
//  This file is part of "Adrian Kuhn's Utilities for Java".
//  
//  "Adrian Kuhn's Utilities for Java" is free software: you can redistribute
//  it and/or modify it under the terms of the GNU Lesser General Public License
//  as published by the Free Software Foundation, either version 3 of the
//  License, or (at your option) any later version.
//  
//  "Adrian Kuhn's Utilities for Java" is distributed in the hope that it will
//  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
//  General Public License for more details.
//  
//  You should have received a copy of the GNU Lesser General Public License
//  along with "Adrian Kuhn's Utilities for Java". If not, see
//  <http://www.gnu.org/licenses/>.
//  

package ch.akuhn.fa;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/** Static methods that operate on or return files.
 *
 */
public class Files {

	public static FileFilter endsWith(final String suffix) {
		return new FileFilter() {
			
			public boolean accept(File file) {
				return file.getName().endsWith(suffix);
			}
		};
	}

	public static Iterable<File> all(final File folder,
			final FileFilter filter) {
		return new Iterable<File>() {

			
			public Iterator<File> iterator() {
				return new Iterator<File>() {
					private LinkedList<File> queue = new LinkedList<File>();
					{
						queue.offer(folder);
						processDirectories();
					}

					
					public boolean hasNext() {
						this.processDirectories();
						return !queue.isEmpty();
					}

					
					public File next() {
						this.processDirectories();
						if (queue.isEmpty())
							throw new NoSuchElementException();
						return queue.poll();
					}

					private void processDirectories() {
						while (!queue.isEmpty()) {
							if (!queue.peek().isDirectory())
								break;
							File next = queue.poll();
							for (File each : next.listFiles()) {
								if (each.isDirectory() || filter == null
										|| filter.accept(each)) {
									queue.offer(each);
								}
							}
						}
					}

					
					public void remove() {
						throw new UnsupportedOperationException();
					}

				};
			}
		};
	}

	public static Iterable<File> all(String filename) {
		return all(new File(filename));
	}

	public static Iterable<File> all(File file) {
		return all(file, (FileFilter) null);
	}
	
	
	public static Iterable<File> all(String filename, FileFilter filter) {
		return all(new File(filename), filter);
	}
	
	public static Iterable<File> all(File file, String pattern) {
		if (pattern.equals("*")) return all(file);
		if (pattern.lastIndexOf('*') != 0) throw //TODO support other patterns
				new UnsupportedOperationException("Patterns other than \"*abc\" not yet supported.");
		return all(file, endsWith(pattern.substring(1)));
	}

	public static Iterable<File> all(String filename, String pattern) {
		return all(new File(filename), pattern);
	}

	
	
}
