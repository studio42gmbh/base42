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
module de.sft.base
{
	requires java.compiler;
	requires java.desktop;
	requires java.sql;
	requires org.json;
	requires jakarta.activation;

	exports de.s42.base.arrays;
	exports de.s42.base.assertion;
	exports de.s42.base.beans;
	exports de.s42.base.collections;
	exports de.s42.base.compile;
	exports de.s42.base.console;
	exports de.s42.base.conversion;
	exports de.s42.base.date;
	exports de.s42.base.files;
	exports de.s42.base.functional;
	exports de.s42.base.math;
	exports de.s42.base.modules;
	exports de.s42.base.resources;
	exports de.s42.base.sql;
	exports de.s42.base.strings;
	exports de.s42.base.swing;
	exports de.s42.base.system;
	exports de.s42.base.testing;
	exports de.s42.base.uuid;
	exports de.s42.base.validation;
	exports de.s42.base.web;
	exports de.s42.base.zip;
}
