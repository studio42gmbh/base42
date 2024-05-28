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
package de.s42.base.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Benjamin Schiller
 */
public class Now
{

	private final Calendar now;

	public static Now create()
	{
		return new Now();
	}

	public Now()
	{
		now = Calendar.getInstance();
	}

	public Now(Date date)
	{
		assert date != null : "date != null";

		now = Calendar.getInstance();
		now.setTime(date);
	}

	public int getYear()
	{
		return now.get(Calendar.YEAR);
	}

	public String getYearAsString()
	{
		return Integer.toString(getYear());
	}

	public int getMonth()
	{
		return now.get(Calendar.MONTH) + 1;
	}

	public String getMonthTwoDigit()
	{
		return twoDigit(getMonth());
	}

	public int getDay()
	{
		return now.get(Calendar.DAY_OF_MONTH);
	}

	public int getDayOfWeek()
	{
		return LocalDate.ofInstant(now.toInstant(), ZoneId.systemDefault()).getDayOfWeek().getValue();
	}

	public String getDayOfWeekAsString()
	{
		return getDayOfWeekAsString(Locale.getDefault());
	}

	public String getDayOfWeekAsString(Locale locale)
	{
		assert locale != null : "locale != null";

		return DayOfWeek.of(getDayOfWeek()).getDisplayName(TextStyle.FULL, locale);
	}

	public String getDayTwoDigit()
	{
		return twoDigit(getDay());
	}

	public int getHour()
	{
		return now.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute()
	{
		return now.get(Calendar.MINUTE);
	}

	public int getSecond()
	{
		return now.get(Calendar.SECOND);
	}

	public int getMilliSecond()
	{
		return now.get(Calendar.MILLISECOND);
	}

	public long getUTC()
	{
		return now.getTimeInMillis();
	}

	protected String twoDigit(int v)
	{
		if (v < 10) {
			return "0" + v;
		}

		return "" + v;
	}

	protected String threeDigit(int v)
	{
		if (v < 10) {
			return "00" + v;
		} else if (v < 100) {
			return "0" + v;
		}

		return "" + v;
	}

	public String getIso()
	{
		return getIsoDateTime();
	}

	public String getIsoDateTime()
	{
		return getIsoDate() + " " + getIsoTime();
	}

	public String getIsoDate()
	{
		return "" + getYear() + "-" + twoDigit(getMonth()) + "-" + twoDigit(getDay());
	}

	public String getIsoTime()
	{
		return twoDigit(getHour()) + ":" + twoDigit(getMinute()) + ":" + twoDigit(getSecond()) + ":" + threeDigit(getMilliSecond());
	}
}
