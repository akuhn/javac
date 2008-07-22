//  Copyright (c) 2008 Adrian Kuhn <akuhn(a)iam.unibe.ch>
//  
//  This file is part of "fa".
//  
//  "fa" is free software: you can redistribute it and/or modify it under the
//  terms of the GNU Lesser General Public License as published by the Free
//  Software Foundation, either version 3 of the License, or (at your option)
//  any later version.
//  
//  "fa" is distributed in the hope that it will be useful, but WITHOUT ANY
//  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//  more details.
//  
//  You should have received a copy of the GNU Lesser General Public License
//  along with "fa". If not, see <http://www.gnu.org/licenses/>.
//  

package ch.akuhn.util;

import java.io.File;

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import numeral.RomanNumeralProcessor;


import com.sun.tools.javac.util.List;

public class ProcRunner implements Runnable {


	private Processor processor;


	private Iterable<File> getFiles() {
		return Files.all("test", "*.java");
	}

	public ProcRunner(Processor processor) {
		this.processor = processor;
	}


	public void run() {

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager fileman = compiler.getStandardFileManager(null,
				null, null);
		Iterable<? extends JavaFileObject> units = fileman
				.getJavaFileObjectsFromFiles(this.getFiles());

		CompilationTask task = compiler.getTask(null, // out
				fileman, // fileManager
				null, // diagnosticsListener
				//List.of("-printsource", "-d", "out"), // options
				List.of("-d", "bin"), // options
				null, // classes
				units);

		task.setProcessors(List.of(processor));

		task.call();

	}

	public static void main(String[] args) {
		Runnable runner = new ProcRunner(new RomanNumeralProcessor());
		runner.run();
	}


}

