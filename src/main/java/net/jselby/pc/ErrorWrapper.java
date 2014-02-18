/*
 * PoweredCube3
 * Copyright (C) 2014 James
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jselby.pc;

import java.io.PrintStream;

public class ErrorWrapper extends PrintStream {
	private PrintStream wrappedOut;
	private Logger logger;

	public ErrorWrapper(Logger logger) {
		super(logger.getStream());
		wrappedOut = (PrintStream) logger.getStream();
		this.logger = logger;
	}

	public boolean checkError() {
		return wrappedOut.checkError();
	}

	public void close() {
		wrappedOut.close();
	}

	public void flush() {
		wrappedOut.flush();
	}

	public void print(boolean x) {
		logger.error("" + x);
	}

	public void print(char x) {
		logger.error("" + x);
	}

	public void print(char[] x) {
		logger.error("" + new String(x));
	}

	public void print(double x) {
		logger.error("" + x);
	}

	public void print(float x) {
		logger.error("" + x);
	}

	public void print(int x) {
		logger.error("" + x);
	}

	public void print(long x) {
		logger.error("" + x);
	}

	public void print(Object x) {
		logger.error("" + x);
	}

	public void print(String x) {
		logger.error("" + x);
	}

	public void println() {
		logger.error("");
	}

	public void println(boolean x) {
		logger.error("" + x);
	}

	public void println(char x) {
		logger.error("" + x);
	}

	public void println(char[] x) {
		logger.error("" + new String(x));
	}

	public void println(double x) {
		logger.error("" + x);
	}

	public void println(float x) {
		logger.error("" + x);
	}

	public void println(int x) {
		logger.error("" + x);
	}

	public void println(long x) {
		logger.error("" + x);
	}

	public void println(Object x) {
		logger.error("" + x);
	}

	public void println(String x) {
		logger.error("" + x);
	}

	public void write(byte[] x, int y, int z) {
		byte[] newArray = new byte[z];
		for (int i = y; i < y + z; i++) {
			newArray[i - y] = x[i];
		}
		logger.error("" + new String(newArray));
	}

	public void write(int x) {
		logger.error("" + new String(new byte[]{(byte) x}));
	}
}