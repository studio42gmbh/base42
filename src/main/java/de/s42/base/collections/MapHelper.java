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
package de.s42.base.collections;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author Benjamin.Schiller
 */
public final class MapHelper
{

	private MapHelper()
	{
		// never instantiated
	}

	static final MapN<?, ?> EMPTY_MAP;

	static {
		EMPTY_MAP = new MapN<>();
	}

	@SuppressWarnings("unchecked")
	public static <KeyType, ValueType> Map<KeyType, ValueType> of()
	{
		return (Map<KeyType, ValueType>) EMPTY_MAP;
	}

	public static <KeyType, ValueType> Map<KeyType, ValueType> of(
		KeyType k1, ValueType v1
	)
	{
		return new Map1<>(k1, v1);
	}

	public static <KeyType, ValueType> Map<KeyType, ValueType> of(
		KeyType k1, ValueType v1,
		KeyType k2, ValueType v2
	)
	{
		return new MapN<>(k1, v1, k2, v2);
	}

	public static <KeyType, ValueType> Map<KeyType, ValueType> of(
		KeyType k1, ValueType v1,
		KeyType k2, ValueType v2,
		KeyType k3, ValueType v3
	)
	{
		return new MapN<>(k1, v1, k2, v2, k3, v3);
	}

	public static <KeyType, ValueType> Map<KeyType, ValueType> of(
		KeyType k1, ValueType v1,
		KeyType k2, ValueType v2,
		KeyType k3, ValueType v3,
		KeyType k4, ValueType v4
	)
	{
		return new MapN<>(k1, v1, k2, v2, k3, v3, k4, v4);
	}

	public static <KeyType, ValueType> Map<KeyType, ValueType> of(
		KeyType k1, ValueType v1,
		KeyType k2, ValueType v2,
		KeyType k3, ValueType v3,
		KeyType k4, ValueType v4,
		KeyType k5, ValueType v5
	)
	{
		return new MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
	}

	public static <KeyType, ValueType> Map<KeyType, ValueType> of(
		KeyType k1, ValueType v1,
		KeyType k2, ValueType v2,
		KeyType k3, ValueType v3,
		KeyType k4, ValueType v4,
		KeyType k5, ValueType v5,
		KeyType k6, ValueType v6
	)
	{
		return new MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
			k6, v6);
	}

	public static <KeyType, ValueType> Map<KeyType, ValueType> of(
		KeyType k1, ValueType v1,
		KeyType k2, ValueType v2,
		KeyType k3, ValueType v3,
		KeyType k4, ValueType v4,
		KeyType k5, ValueType v5,
		KeyType k6, ValueType v6,
		KeyType k7, ValueType v7
	)
	{
		return new MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
			k6, v6, k7, v7);
	}

	public static <KeyType, ValueType> Map<KeyType, ValueType> of(
		KeyType k1, ValueType v1,
		KeyType k2, ValueType v2,
		KeyType k3, ValueType v3,
		KeyType k4, ValueType v4,
		KeyType k5, ValueType v5,
		KeyType k6, ValueType v6,
		KeyType k7, ValueType v7,
		KeyType k8, ValueType v8
	)
	{
		return new MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
			k6, v6, k7, v7, k8, v8);
	}

	public static <KeyType, ValueType> Map<KeyType, ValueType> of(
		KeyType k1, ValueType v1,
		KeyType k2, ValueType v2,
		KeyType k3, ValueType v3,
		KeyType k4, ValueType v4,
		KeyType k5, ValueType v5,
		KeyType k6, ValueType v6,
		KeyType k7, ValueType v7,
		KeyType k8, ValueType v8,
		KeyType k9, ValueType v9
	)
	{
		return new MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
			k6, v6, k7, v7, k8, v8, k9, v9);
	}

	public static <KeyType, ValueType> Map<KeyType, ValueType> of(
		KeyType k1, ValueType v1,
		KeyType k2, ValueType v2,
		KeyType k3, ValueType v3,
		KeyType k4, ValueType v4,
		KeyType k5, ValueType v5,
		KeyType k6, ValueType v6,
		KeyType k7, ValueType v7,
		KeyType k8, ValueType v8,
		KeyType k9, ValueType v9,
		KeyType k10, ValueType v10
	)
	{
		return new MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
			k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
	}

	final static class KeyValueHolder<K, V> implements Map.Entry<K, V>
	{

		final K key;
		final V value;

		KeyValueHolder(K k, V v)
		{
			key = Objects.requireNonNull(k);
			value = v;
		}

		@Override
		public K getKey()
		{
			return key;
		}

		@Override
		public V getValue()
		{
			return value;
		}

		@Override
		public V setValue(V value)
		{
			throw new UnsupportedOperationException("not supported");
		}

		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof Map.Entry<?, ?>)) {
				return false;
			}

			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;

			return key.equals(e.getKey()) && ((value != null) ? value.equals(e.getValue()) : (e.getValue() == null));
		}

		@Override
		public int hashCode()
		{
			return key.hashCode() ^ ((value != null) ? value.hashCode() : 0xFFFFFFFF);
		}

		@Override
		public String toString()
		{
			return key + "=" + value;
		}
	}

	private static final long SALT32L;
	private static final boolean REVERSE;

	static {
		long color = 0x243F_6A88_85A3_08D3L; // slice of pi

		long seed = (new Random()).nextLong();
		if (seed == 0) {
			seed = System.nanoTime();
		}
		SALT32L = (int) ((color * seed) >> 16) & 0xFFFF_FFFFL;
		// use the lowest bit to determine if we should reverse iteration
		REVERSE = (SALT32L & 1) == 0;
	}

	static final int EXPAND_FACTOR = 2;

	static UnsupportedOperationException uoe()
	{
		return new UnsupportedOperationException();
	}

	abstract static class AbstractImmutableMap<K, V> extends AbstractMap<K, V>
	{

		@Override
		public void clear()
		{
			throw uoe();
		}

		@Override
		public V compute(K key, BiFunction<? super K, ? super V, ? extends V> rf)
		{
			throw uoe();
		}

		@Override
		public V computeIfAbsent(K key, Function<? super K, ? extends V> mf)
		{
			throw uoe();
		}

		@Override
		public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> rf)
		{
			throw uoe();
		}

		@Override
		public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> rf)
		{
			throw uoe();
		}

		@Override
		public V put(K key, V value)
		{
			throw uoe();
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> m)
		{
			throw uoe();
		}

		@Override
		public V putIfAbsent(K key, V value)
		{
			throw uoe();
		}

		@Override
		public V remove(Object key)
		{
			throw uoe();
		}

		@Override
		public boolean remove(Object key, Object value)
		{
			throw uoe();
		}

		@Override
		public V replace(K key, V value)
		{
			throw uoe();
		}

		@Override
		public boolean replace(K key, V oldValue, V newValue)
		{
			throw uoe();
		}

		@Override
		public void replaceAll(BiFunction<? super K, ? super V, ? extends V> f)
		{
			throw uoe();
		}

		@Override
		public V getOrDefault(Object key, V defaultValue)
		{
			if (containsKey(key)) {
				return get(key);
			}

			return defaultValue;
		}
	}

	static final class Map1<K, V> extends AbstractImmutableMap<K, V>
	{

		private final K k0;
		private final V v0;

		Map1(K k0, V v0)
		{
			this.k0 = Objects.requireNonNull(k0);
			this.v0 = v0;
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet()
		{
			return Set.of(new KeyValueHolder<>(k0, v0));
		}

		@Override
		public V get(Object o)
		{
			return o.equals(k0) ? v0 : null; // implicit nullcheck of o
		}

		@Override
		public boolean containsKey(Object o)
		{
			return o.equals(k0); // implicit nullcheck of o
		}

		@Override
		public boolean containsValue(Object o)
		{
			return Objects.equals(o, v0);
		}

		@Override
		public int size()
		{
			return 1;
		}

		@Override
		public boolean isEmpty()
		{
			return false;
		}

		@Override
		public int hashCode()
		{
			return k0.hashCode() ^ ((v0 != null) ? v0.hashCode() : 0xFFFFFFFF);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Map1<?, ?> other = (Map1<?, ?>) obj;
			if (!Objects.equals(this.k0, other.k0)) {
				return false;
			}
			return Objects.equals(this.v0, other.v0);
		}
	}

	static final class MapN<K, V> extends AbstractImmutableMap<K, V>
	{

		final Object[] table; // pairs of key, value

		final int size; // number of pairs

		MapN(Object... input)
		{
			if ((input.length & 1) != 0) { // implicit nullcheck of input
				throw new InternalError("length is odd");
			}
			size = input.length >> 1;

			int len = EXPAND_FACTOR * input.length;
			len = (len + 1) & ~1; // ensure table is even length
			table = new Object[len];

			for (int i = 0; i < input.length; i += 2) {
				@SuppressWarnings("unchecked")
				K k = Objects.requireNonNull((K) input[i]);
				@SuppressWarnings("unchecked")
				V v = (V) input[i + 1];
				int idx = probe(k);
				if (idx >= 0) {
					throw new IllegalArgumentException("duplicate key: " + k);
				} else {
					int dest = -(idx + 1);
					table[dest] = k;
					table[dest + 1] = v;
				}
			}
		}

		@Override
		public boolean containsKey(Object o)
		{
			Objects.requireNonNull(o);
			return size > 0 && probe(o) >= 0;
		}

		@Override
		public boolean containsValue(Object o)
		{
			Objects.requireNonNull(o);
			for (int i = 1; i < table.length; i += 2) {
				Object v = table[i];
				if (v != null && o.equals(v)) {
					return true;
				}
			}
			return false;
		}
		
		/* @todo verify if not defining thos is sufficiently providing hashcode and equals as expected - was removed as equals with other maps didnt work properly
		@Override
		public int hashCode()
		{
			int hash = 0;
			for (int i = 0; i < table.length; i += 2) {
				Object k = table[i];
				if (k != null) {
					Object v = table[i + 1];
					hash += k.hashCode() ^ ((v != null) ? v.hashCode() : 0xFFFFFFFF);
				}
			}
			return hash;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final MapN<?, ?> other = (MapN<?, ?>) obj;
			return Arrays.deepEquals(this.table, other.table);
		}
*/

		@Override
		@SuppressWarnings("unchecked")
		public V get(Object o)
		{
			if (size == 0) {
				Objects.requireNonNull(o);
				return null;
			}
			int i = probe(o);
			if (i >= 0) {
				return (V) table[i + 1];
			} else {
				return null;
			}
		}

		@Override
		public int size()
		{
			return size;
		}

		@Override
		public boolean isEmpty()
		{
			return size == 0;
		}

		class MapNIterator implements Iterator<Map.Entry<K, V>>
		{

			private int remaining;

			private int idx;

			MapNIterator()
			{
				remaining = size;
				// pick an even starting index in the [0 .. table.length-1]
				// range randomly based on SALT32L
				idx = (int) ((SALT32L * (table.length >> 1)) >>> 32) << 1;
			}

			@Override
			public boolean hasNext()
			{
				return remaining > 0;
			}

			private int nextIndex()
			{
				int idx = this.idx;
				if (REVERSE) {
					if ((idx += 2) >= table.length) {
						idx = 0;
					}
				} else {
					if ((idx -= 2) < 0) {
						idx = table.length - 2;
					}
				}
				return this.idx = idx;
			}

			@Override
			public Map.Entry<K, V> next()
			{
				if (remaining > 0) {
					int idx;
					while (table[idx = nextIndex()] == null) {
					}
					@SuppressWarnings("unchecked")
					Map.Entry<K, V> e
						= new KeyValueHolder<>((K) table[idx], (V) table[idx + 1]);
					remaining--;
					return e;
				} else {
					throw new NoSuchElementException();
				}
			}
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet()
		{
			return new AbstractSet<>()
			{
				@Override
				public int size()
				{
					return MapN.this.size;
				}

				@Override
				public Iterator<Map.Entry<K, V>> iterator()
				{
					return new MapNIterator();
				}
			};
		}

		// returns index at which the probe key is present; or if absent,
		// (-i - 1) where i is location where element should be inserted.
		// Callers are relying on this method to perform an implicit nullcheck
		// of pk.
		private int probe(Object pk)
		{
			int idx = Math.floorMod(pk.hashCode(), table.length >> 1) << 1;
			while (true) {
				@SuppressWarnings("unchecked")
				K ek = (K) table[idx];
				if (ek == null) {
					return -idx - 1;
				} else if (pk.equals(ek)) {
					return idx;
				} else if ((idx += 2) == table.length) {
					idx = 0;
				}
			}
		}
	}
}
