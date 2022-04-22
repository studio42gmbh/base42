/*
// <editor-fold desc="The MIT License" defaultstate="collapsed">
 * The MIT License
 * 
 * Copyright 2022 Studio 42 GmbH (https://www.s42m.de).
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
package de.s42.base.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Benjamin Schiller
 * @param <KeyType>
 * @param <DataType>
 */
public class MappedList<KeyType, DataType>
{

	protected final List<DataType> list = new ArrayList<>();
	protected final Set<DataType> set = new HashSet<>();
	protected final Map<KeyType, DataType> map = new HashMap<>();

	public MappedList()
	{
	}

	public int size()
	{
		return list.size();
	}

	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	public void clear()
	{
		list.clear();
		set.clear();
		map.clear();
	}

	public void addAll(MappedList<KeyType, DataType> other)
	{
		for (Map.Entry<KeyType, DataType> entry : other.map().entrySet()) {
			add(entry.getKey(), entry.getValue());
		}
	}

	public void add(KeyType key, DataType value)
	{
		assert key != null;
		assert value != null;

		DataType oldValue = map.put(key, value);
		if (oldValue != null) {
			list.remove(oldValue);
		}
		list.add(value);
		set.add(value);
	}

	public void remove(KeyType key)
	{
		assert key != null;

		DataType value = map.remove(key);
		if (value != null) {
			list.remove(value);
			set.remove(value);
		}
	}

	public void removeByData(DataType value)
	{
		assert value != null;

		Set<Map.Entry<KeyType, DataType>> entries = map.entrySet();
		for (Map.Entry<KeyType, DataType> entry : entries) {
			if (value.equals(entry.getValue())) {
				entries.remove(entry);
				list.remove(value);
				set.remove(value);
				return;
			}
		}
	}

	public Optional<DataType> get(KeyType key)
	{
		assert key != null;

		return Optional.ofNullable(map.get(key));
	}

	public DataType get(int index)
	{
		assert index >= 0;
		assert index < list.size();

		return list.get(index);
	}

	public boolean contains(KeyType key)
	{
		assert key != null;

		return map.containsKey(key);
	}

	public Set<KeyType> keys()
	{
		return Collections.unmodifiableSet(map.keySet());
	}

	public Set<DataType> values()
	{
		return Collections.unmodifiableSet(set);
	}

	public List<DataType> list()
	{
		return Collections.unmodifiableList(list);
	}

	public Map<KeyType, DataType> map()
	{
		return Collections.unmodifiableMap(map);
	}
}
