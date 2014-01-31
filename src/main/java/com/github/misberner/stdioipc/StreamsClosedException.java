package com.github.misberner.stdioipc;

/**
 * Created by malte on 31.01.14.
 */
public class StreamsClosedException extends Exception {

	public StreamsClosedException() {
	}

	public StreamsClosedException(String message) {
		super(message);
	}

	public StreamsClosedException(String message, Throwable cause) {
		super(message, cause);
	}

	public StreamsClosedException(Throwable cause) {
		super(cause);
	}
}
