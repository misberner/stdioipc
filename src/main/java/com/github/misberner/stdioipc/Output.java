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
