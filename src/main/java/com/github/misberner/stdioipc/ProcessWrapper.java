/* Copyright (C) 2014 Malte Isberner
 * This file is part of stdioipc, https://github.com/misberner/stdioipc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.misberner.stdioipc;

import java.io.*;
import java.util.concurrent.*;

/**
 * Created by malte on 30.01.14.
 */
public class ProcessWrapper {

	private class ReadOutput implements Runnable {

		private final Reader reader;
		private final StringBuilder buffer = new StringBuilder();
		private final Channel channel;

		private IOException streamException = null;
		private boolean endOfStream = false;

		public ReadOutput(InputStream is, Channel channel) {
			this(new InputStreamReader(is), channel);
		}

		public ReadOutput(Reader reader, Channel channel) {
			this.reader = reader;
			this.channel = channel;
		}

		public void close() throws IOException {
			this.reader.close();
		}

		public void run() {
			char[] buf = new char[4096];

			int len;

			try {
				while((len = reader.read(buf)) != -1) {
					processContents(buf, len);
				}
				endOfStream = true;
			}
			catch(IOException ex) {
				streamException = ex;
			}
			finally {
				try {
					sendOutput(null);
					reader.close();
				}
				catch(IOException ex) {}
			}
		}

		private void processContents(char[] buf, int len) {
			int i = 0;
			int lastDelim = 0;
			while(i < len) {
				char c = buf[i++];
				if(c == '\n') {
					buffer.append(buf, lastDelim, i - lastDelim - 1);
					sendOutput(buffer.toString());
					buffer.setLength(0);
					lastDelim = i;
				}
			}
			buffer.append(buf, lastDelim, len - lastDelim);
		}

		private void sendOutput(String output) {
			outputs.offer(new Output(output, channel));
		}
	}

	private final Process process;
	private final ExecutorService executor;
	private BlockingQueue<Output> outputs = new LinkedBlockingQueue<Output>();

	int openStreams;
	private final Writer stdinWriter;

	public ProcessWrapper(Process process) {
		this.process = process;
		this.executor = Executors.newFixedThreadPool(2);

		ReadOutput readStdout = new ReadOutput(process.getInputStream(), Channel.STDOUT);
		ReadOutput readStderr = new ReadOutput(process.getErrorStream(), Channel.STDERR);
		openStreams = 2;

		executor.execute(readStdout);
		executor.execute(readStderr);

		this.stdinWriter = new OutputStreamWriter(process.getOutputStream());
	}

	public Output read(long timeout, TimeUnit unit) throws InterruptedException, StreamsClosedException {
		while(outputs != null) {
			Output out = outputs.poll(timeout, unit);
			if(out == null) {
				return null;
			}
			if(out.getLine() != null) {
				return out;
			}
			// Stream closed!
			streamClosed(out.getChannel());
		}
		throw new StreamsClosedException();
	}

	public void write(String line) throws IOException {
		stdinWriter.write(line);
		stdinWriter.write('\n');
		stdinWriter.flush();
	}


	private void streamClosed(Channel channel) {
		if(--openStreams == 0) {
			executor.shutdownNow();
			outputs = null;
			try {
				process.exitValue();
			}
			catch(IllegalStateException ex) {
				throw new IllegalStateException("Both streams closed, but program still running", ex);
			}
		}
	}
}
