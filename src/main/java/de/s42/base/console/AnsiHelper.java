// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2022 Studio 42 GmbH ( https://www.s42m.de ).
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
package de.s42.base.console;

/**
 * See https://www.lihaoyi.com/post/BuildyourownCommandLinewithANSIescapecodes.html
 *
 * @author Benjamin Schiller
 */
public final class AnsiHelper
{

	public enum TerminalColor
	{
		Black("\u001b[30m"),
		Red("\u001b[31m"),
		Green("\u001b[32m"),
		Yellow("\u001b[33m"),
		Blue("\u001b[34m"),
		Magenta("\u001b[35m"),
		Cyan("\u001b[36m"),
		White("\u001b[37m"),
		BrightBlack("\u001b[30;1m"),
		BrightRed("\u001b[31;1m"),
		BrightGreen("\u001b[32;1m"),
		BrightYellow("\u001b[33;1m"),
		BrightBlue("\u001b[34;1m"),
		BrightMagenta("\u001b[35;1m"),
		BrightCyan("\u001b[36;1m"),
		BrightWhite("\u001b[37;1m"),
		BackgroundBlack("\u001b[40m"),
		BackgroundRed("\u001b[41m"),
		BackgroundGreen("\u001b[42m"),
		BackgroundYellow("\u001b[43m"),
		BackgroundBlue("\u001b[44m"),
		BackgroundMagenta("\u001b[45m"),
		BackgroundCyan("\u001b[46m"),
		BackgroundWhite("\u001b[47m"),
		Reset("\u001b[0m");

		public final String ansiCode;

		private TerminalColor(String ansiCode)
		{
			this.ansiCode = ansiCode;
		}
	}

	public enum TerminalBackgroundColor
	{
		BackgroundBlack("\u001b[40m"),
		BackgroundRed("\u001b[41m"),
		BackgroundGreen("\u001b[42m"),
		BackgroundYellow("\u001b[43m"),
		BackgroundBlue("\u001b[44m"),
		BackgroundMagenta("\u001b[45m"),
		BackgroundCyan("\u001b[46m"),
		BackgroundWhite("\u001b[47m"),
		BackgroundBrightBlack("\u001b[40;1m"),
		BackgroundBrightRed("\u001b[41;1m"),
		BackgroundBrightGreen("\u001b[42;1m"),
		BackgroundBrightYellow("\u001b[43;1m"),
		BackgroundBrightBlue("\u001b[44;1m"),
		BackgroundBrightMagenta("\u001b[45;1m"),
		BackgroundBrightCyan("\u001b[46;1m"),
		BackgroundBrightWhite("\u001b[47;1m");

		public final String ansiCode;

		private TerminalBackgroundColor(String ansiCode)
		{
			this.ansiCode = ansiCode;
		}
	}

	private AnsiHelper()
	{
		// never instantiated
	}

	public static String coloredString(String text, TerminalColor color)
	{
		return coloredString(text, color, true);
	}

	public static String coloredString(String text, TerminalColor color, boolean ansiEnabled)
	{
		assert text != null;
		assert color != null;
		
		if (!ansiEnabled) {
			return text;
		}

		return color.ansiCode + text + TerminalColor.Reset.ansiCode;
	}
	
	public static String coloredString(String text, TerminalColor color, TerminalBackgroundColor background)
	{
		return coloredString(text, color, background, true);
	}
	
	public static String coloredString(String text, TerminalColor color, TerminalBackgroundColor background, boolean ansiEnabled)
	{
		assert text != null;
		assert color != null;
		assert background != null;

		if (!ansiEnabled) {
			return text;
		}
		
		return color.ansiCode + background.ansiCode + text + TerminalColor.Reset.ansiCode;
	}
	
}
