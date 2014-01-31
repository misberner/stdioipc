package com.github.misberner.stdioipc;

/**
* Created by malte on 31.01.14.
*/
public class Output {
	private final String line;
	private final Channel channel;

	public Output(String line, Channel channel) {
		this.line = line;
		this.channel = channel;
	}

	public String getLine() {
		return line;
	}

	public Channel getChannel() {
		return channel;
	}
}
