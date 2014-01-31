package com.github.misberner.stdioipc;

import java.util.concurrent.TimeUnit;

/**
 * Created by malte on 31.01.14.
 */
public class ProcessWrapperDemo {
	public static void main(String[] args) throws Exception{
		Process p = new ProcessBuilder()
					.command("find", ".", "-name", "*.java")
				.start();

		ProcessWrapper pw = new ProcessWrapper(p);

		for(;;) {
			Output out = pw.read(5, TimeUnit.MILLISECONDS);
			if(out == null) {
				System.err.println("Timeout");
			}
			else {
				System.err.println("Output '" + out.getLine() + "' on " + out.getChannel());
			}
		}
	}
}
