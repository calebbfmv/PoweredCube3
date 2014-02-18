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

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger extends java.util.logging.Logger {
	private PrintStream output;

	public Logger(PrintStream out) {
		super("PoweredCube", null);
		this.output = out;
	}

	public void info(String string) {
		print("INFO", string);
	}

    public void error(String string) {
        print("ERROR", string);
    }

    public void WARNING(String string) {
        print("WARNING", string);
    }

    private void print(String type, String message) {
        for (String s: message.split("\n")) {
            printLine(type, s);
        }
    }

    private void printLine(String type, String message) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

		output.println("[" + sdf.format(cal.getTime()) + " "
				+ type.toUpperCase() + "]: " + message);
	}

	OutputStream getStream() {
		return output;
	}
}
