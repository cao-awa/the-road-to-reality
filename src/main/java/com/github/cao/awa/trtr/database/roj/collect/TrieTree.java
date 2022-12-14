package com.github.cao.awa.trtr.database.roj.collect;

import com.github.cao.awa.trtr.database.roj.text.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Roj234
 * @since 2021/2/21 10:39
 */
public class TrieTree<V> implements Map<CharSequence, V> {
	static final int COMPRESS_START_DEPTH = 1, MIN_REMOVE_ARRAY_SIZE = 5;
	private static final Object UNDEFINED = new Object();

	static class Entry<V> extends TrieEntry {
		V value;

		@SuppressWarnings("unchecked")
		Entry(char c) {
			super(c);
			this.value = (V) UNDEFINED;
		}

		Entry(char c, Entry<V> entry) {
			super(c);
			this.length = entry.length;
			this.size = entry.size;
			this.entries = entry.entries;
			this.value = entry.value;
		}

		@Override
		boolean isValid() {
			return value != UNDEFINED;
		}

		@SuppressWarnings("unchecked")
		public int copyFrom(TrieEntry x) {
			Entry<?> node = (Entry<?>) x;
			int v = 0;
			if (node.value != UNDEFINED && value == UNDEFINED) {
				this.value = (V) node.value;
				v = 1;
			}

			for (TrieEntry entry : node) {
				TrieEntry sub = getChild(entry.c);
				if (sub == null) putChild(sub = entry.clone());
				v += sub.copyFrom(entry);
			}
			return v;
		}

		public V getValue() {
			if (value == UNDEFINED) throw new UnsupportedOperationException();
			return value;
		}

		public V setValue(V value) {
			if (value == UNDEFINED || this.value == UNDEFINED) throw new UnsupportedOperationException();
			V ov = this.value;
			this.value = value;
			return ov;
		}
	}

	static final class PEntry<V> extends Entry<V> {
		CharSequence val;

		PEntry(CharSequence val) {
			super(val.charAt(0));
			this.val = val;
		}

		PEntry(CharSequence val, Entry<V> entry) {
			super(val.charAt(0), entry);
			this.val = val;
		}

		CharSequence text() {
			return val;
		}

		@Override
		void append(StringBuilder sb) {
			sb.append(val);
		}

		@Override
		int length() {
			return val.length();
		}

		@Override
		public String toString() {
			return "PE{" + val + '}';
		}
	}

	Entry<V> root = new Entry<>((char) 0);
	int size = 0;

	public TrieTree() {
	}

	public TrieTree(Map<CharSequence, V> map) {
		putAll(map);
	}

	public void addTrieTree(TrieTree<? extends V> m) {
		size += root.copyFrom(cast(m.root));
	}

	public static <T> T cast(Object o) {
		return (T)o;
	}

	@SuppressWarnings("unchecked")
	Entry<V> entryForPut(CharSequence s, int i, int len) {
		if (len - i < 0) throw new IllegalArgumentException("??length < 0");

		Entry<V> entry = root;
		Entry<V> prev;
		for (; i < len; i++) {
			char c = s.charAt(i);
			prev = entry;
			entry = (Entry<V>) entry.getChild(c);
			if (entry == null) {
				// ???COMPRESS_START_DEPTH?????????????????????????????????????????????
				if (len - i == 1 || i < COMPRESS_START_DEPTH) {
					prev.putChild(entry = new Entry<>(c));
				} else {
					prev.putChild(entry = new PEntry<>(s.subSequence(i, len)));
					break;
				}
			} else if (entry.text() != null) {
				final CharSequence text = entry.text();

				int lastMatch = TextUtil.lastMatches(text, 0, s, i, len - i);
				if (lastMatch == text.length()) {
					// ??????match
					i += lastMatch - 1;
				} else {
					// ??????P1: ????????????[0, lastMatch)
					if (lastMatch == 1) {
						prev.putChild(new Entry<>(entry.c));
					} else {
						((PEntry<V>) entry).val = text.subSequence(0, lastMatch);
					}

					// ??????P2: ????????????[lastMatch, text.length)
					Entry<V> child;
					if (text.length() - 1 == lastMatch) {
						child = new Entry<>(text.charAt(lastMatch), entry);
					} else {
						child = new PEntry<>(text.subSequence(lastMatch, text.length()), entry);
					}

					// entry????????????????????????child
					entry.clear();
					entry.value = (V) UNDEFINED;

					// ???????????????????????????entry??????
					(entry = (Entry<V>) prev.getChild(entry.c)).putChild(child);

					// ????????????entry
					lastMatch += i;
					if (len == lastMatch + 1) {
						// ??????1
						// ?????? abcde ?????? abcdf
						// ????????????
						child = new Entry<>(s.charAt(len - 1));
					} else {
						if (len == lastMatch) {
							// ??????2
							// ?????? abcde ?????? abcd
							// ?????????P1??????child
							break;
						} else {
							// ??????2
							// ?????? abcde ?????? abcdef
							child = new PEntry<>(s.subSequence(lastMatch, len));
						}
					}

					entry.putChild(child);
					entry = child;

					break;
				}
			}
		}
		if (entry.value == UNDEFINED) {
			size++;
			entry.value = null;
		}
		return entry;
	}

	@SuppressWarnings("unchecked")
	public Entry<V> getEntry(CharSequence s, int i, int len) {
		if (len - i < 0) throw new IllegalArgumentException("??length < 0");

		Entry<V> entry = root;
		for (; i < len; i++) {
			entry = (Entry<V>) entry.getChild(s.charAt(i));
			if (entry == null) return null;
			final CharSequence text = entry.text();
			if (text != null) {
				int lastMatch = TextUtil.lastMatches(text, 0, s, i, len - i);
				if (lastMatch != text.length()) return null;
				i += text.length() - 1;
			}
		}
		return entry.value != UNDEFINED ? entry : null;
	}

	public Entry<V> getRoot() {
		return root;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public V put(CharSequence key, V e) {
		return put(key, 0, key.length(), e);
	}

	public V put(CharSequence key, int len, V e) {
		return put(key, 0, len, e);
	}

	public V put(CharSequence key, int off, int len, V e) {
		Entry<V> entry = entryForPut(key, off, len);
		V v = entry.value;
		entry.value = e;

		return v;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void putAll(@NotNull Map<? extends CharSequence, ? extends V> m) {
		if (m instanceof TrieTree) {
			addTrieTree((TrieTree<? extends V>) m);
			return;
		}
		for (Map.Entry<? extends CharSequence, ? extends V> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V remove(Object k) {
		CharSequence s = (CharSequence) k;
		return remove(s, 0, s.length(), UNDEFINED);
	}

	public V remove(CharSequence s, int len) {
		return remove(s, 0, len, UNDEFINED);
	}

	public V remove(CharSequence s, int i, int len) {
		return remove(s, i, len, UNDEFINED);
	}

	@SuppressWarnings("unchecked")
	V remove(CharSequence s, int i, int len, Object tc) {
		if (len - i < 0) throw new IllegalArgumentException("??length < 0");

		List<Entry<V>> list = new ObjectArrayList<>();

		Entry<V> entry = root;
		for (; i < len; i++) {
			list.add(entry);

			entry = (Entry<V>) entry.getChild(s.charAt(i));
			if (entry == null) return null;

			final CharSequence text = entry.text();
			if (text != null) {
				int lastMatch = TextUtil.lastMatches(text, 0, s, i, len - i);
				if (lastMatch != text.length()) return null;
				i += text.length() - 1;
			}
		}
		if (entry.value == UNDEFINED) return null;
		if (!Objects.equals(entry.value, tc) && tc != UNDEFINED) return null;

		size--;

		i = list.size() - 1;

		Entry<V> prev = entry;
		while (i >= 0) {
			Entry<V> curr = list.get(i);

			// ??????????????????:
			// root <== a <== b <== cd <== efg
			if (curr.size > 1 || curr.isValid()) {
				curr.removeChild(prev);

				// ???????????????entry
				// root <== a <== b (curr) <== def <== ghi
				if (curr.size == 1 && curr.value == UNDEFINED && i >= COMPRESS_START_DEPTH) {
					StringBuilder sb = new StringBuilder(16);

					do {
						curr.append(sb);

						TrieEntry[] entries = curr.entries;
						for (TrieEntry trieEntry : entries) {
							if (trieEntry != null) {
								curr = (Entry<V>) trieEntry;
								break;
							}
						}
					} while (curr.size == 1);
					curr.append(sb);
					// sb = "bdefghi"

					// sb.length ???????????? 1
					// ?????????????????? curr ??? curr.next
					list.get(i - 1).putChild(new PEntry<>(sb.toString(), curr));
				}
				return entry.value;
			}
			prev = curr;
			i--;
		}
		throw new AssertionError("Entry list chain size");
	}

	@Override
	public boolean containsKey(Object i) {
		final CharSequence s = (CharSequence) i;
		return containsKey(s, 0, s.length());
	}

	public boolean containsKey(CharSequence s, int len) {
		return containsKey(s, 0, len);
	}

	public boolean containsKey(CharSequence s, int off, int len) {
		return getEntry(s, off, len) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		Iterator<Entry<V>> itr = mapItr();
		while (itr.hasNext()) {
			if (Objects.equals(itr.next().value, value)) {
				return true;
			}
		}
		return false;
	}

	public Map.Entry<Integer, V> longestMatches(CharSequence s) {
		return longestMatches(s, 0, s.length());
	}

	public Map.Entry<Integer, V> longestMatches(CharSequence s, int len) {
		return longestMatches(s, 0, len);
	}

	@SuppressWarnings("unchecked")
	public Map.Entry<Integer, V> longestMatches(CharSequence s, int i, int len) {
		int d = 0;

		Entry<V> entry = root, next;
		for (; i < len; i++) {
			next = (Entry<V>) entry.getChild(s.charAt(i));
			if (next == null) break;

			CharSequence text = next.text();
			if (text != null) {
				int lastMatch = TextUtil.lastMatches(text, 0, s, i, len - i);
				d += lastMatch;

				if (lastMatch < text.length()) break;
				i += text.length() - 1;
			} else {
				d++;
			}

			entry = next;
		}

		return entry.value == UNDEFINED ? null : new AbstractMap.SimpleImmutableEntry<>(d, entry.value);
	}

	public List<V> valueMatches(CharSequence s, int limit) {
		return valueMatches(s, 0, s.length(), limit);
	}

	public List<V> valueMatches(CharSequence s, int len, int limit) {
		return valueMatches(s, 0, len, limit);
	}

	@SuppressWarnings("unchecked")
	public List<V> valueMatches(CharSequence s, int i, int len, int limit) {
		StringBuilder base = new StringBuilder();
		Entry<V> entry = matches(s, i, len, base);
		if (entry == null) return Collections.emptyList();

		List<V> values = new ObjectArrayList<>();
		base = new StringBuilder();
		TrieEntry.KeyItr itr = new TrieEntry.KeyItr(entry, base);
		while (itr.hasNext()) {
			if (values.size() >= limit) break;

			itr.next();
			values.add(((Entry<V>) itr.ent).value);
		}
		return values;
	}

	public List<Map.Entry<String, V>> entryMatches(CharSequence s, int limit) {
		return entryMatches(s, 0, s.length(), limit);
	}

	public List<Map.Entry<String, V>> entryMatches(CharSequence s, int len, int limit) {
		return entryMatches(s, 0, len, limit);
	}

	@SuppressWarnings("unchecked")
	public List<Map.Entry<String, V>> entryMatches(CharSequence s, int i, int len, int limit) {
		StringBuilder base = new StringBuilder();
		Entry<V> entry = matches(s, i, len, base);
		if (entry == null) return Collections.emptyList();

		ArrayList<Map.Entry<String, V>> entries = new ArrayList<>();
		TrieEntry.KeyItr itr = new TrieEntry.KeyItr(entry, base);
		while (itr.hasNext()) {
			if (entries.size() >= limit) {
				break;
			}
			entries.add(new AbstractMap.SimpleImmutableEntry<>(itr.next().toString(), ((Entry<V>) itr.ent).value));
		}
		return entries;
	}

	@SuppressWarnings("unchecked")
	private Entry<V> matches(CharSequence s, int i, int len, StringBuilder sb) {
		Entry<V> entry = root;
		for (; i < len; i++) {
			entry = (Entry<V>) entry.getChild(s.charAt(i));
			if (entry == null) {
				return null;
			}
			final CharSequence text = entry.text();
			if (text != null) {
				int lastMatch = TextUtil.lastMatches(text, 0, s, i, len - i);
				if (lastMatch != text.length()) {
					if (lastMatch < len - i) // ?????????????????????
					{return null;}

					sb.append(text);
					break;
				}
				i += text.length() - 1;
				sb.append(text);
			} else {
				sb.append(entry.c);
			}
		}

		return entry;
	}

	/**
	 * internal: nodes.forEach(if (s.startsWith(node)))
	 * s: abcdef
	 * node: abcdef / abcde / abcd... true
	 * node: abcdefg  ... false
	 */
	public boolean startsWith(CharSequence s) {
		return startsWith(s, 0, s.length());
	}

	public boolean startsWith(CharSequence s, int len) {
		return startsWith(s, 0, len);
	}

	@SuppressWarnings("unchecked")
	public boolean startsWith(CharSequence s, int i, int len) {
		Entry<V> entry = root;
		for (; i < len; i++) {
			entry = (Entry<V>) entry.getChild(s.charAt(i));
			if (entry == null) return false;
			final CharSequence text = entry.text();
			if (text != null) {
				int lastMatch = TextUtil.lastMatches(text, 0, s, i, len - i);
				if (lastMatch != text.length()) {
					return false;
					// ???????????????: entry.value != NOT_USING && lastMatch == len - i;
				}
				i += text.length() - 1;
			}

			if (entry.value != UNDEFINED) return true;
		}
		return entry.value != UNDEFINED;
	}

	@Override
	public V get(Object id) {
		CharSequence s = (CharSequence) id;
		return get(s, 0, s.length());
	}

	public V get(CharSequence s, int length) {
		return get(s, 0, length);
	}

	public V get(CharSequence s, int offset, int length) {
		Entry<V> entry = getEntry(s, offset, length);
		return entry == null ? null : entry.value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TrieTree").append('{');
		if (!isEmpty()) {
			forEach((k, v) -> sb.append(k).append('=').append(v).append(','));
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.append('}').toString();
	}

	@Override
	public void clear() {
		size = 0;
		root.clear();
	}

	/**
	 * @see #forEach(BiConsumer)
	 */
	@NotNull
	@Override
	public Set<CharSequence> keySet() {
		return new KeySet(this);
	}

	@NotNull
	@Override
	public Collection<V> values() {
		return new Values<>(this);
	}

	public Iterator<Entry<V>> mapItr() {
		return new TrieEntry.Itr<Entry<V>, Entry<V>>() {
			{
				super.setupDepthFirst(root);
			}

			@Override
			public boolean computeNext() {
				boolean v = super._computeNextDepthFirst();
				if (v) result = ent;
				return v;
			}
		};
	}

	static final class KeySet extends AbstractSet<CharSequence> {
		private final TrieTree<?> map;

		public KeySet(TrieTree<?> map) {
			this.map = map;
		}

		public final int size() {
			return map.size();
		}

		public final void clear() {
			map.clear();
		}

		public final Iterator<CharSequence> iterator() {
			return isEmpty() ? Collections.emptyIterator() : new TrieEntry.KeyItr(map.root);
		}

		public final boolean contains(Object o) {
			return map.containsKey(o);
		}

		public final boolean remove(Object o) {
			return map.remove(o) != null;
		}
	}

	static final class Values<V> extends AbstractCollection<V> {
		private final TrieTree<V> map;

		public Values(TrieTree<V> map) {
			this.map = map;
		}

		public final int size() {
			return map.size();
		}

		public final void clear() {
			map.clear();
		}

		public final Iterator<V> iterator() {
			return isEmpty() ? Collections.emptyIterator() : new TrieEntry.Itr<V, Entry<V>>() {
				{
					super.setupDepthFirst(map.root);
				}

				@Override
				public boolean computeNext() {
					boolean v = super._computeNextDepthFirst();
					if (v) result = ent.value;
					return v;
				}
			};
		}

		public final boolean contains(Object o) {
			return map.containsValue(o);
		}

		public final boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		public final Spliterator<V> spliterator() {
			return Spliterators.spliterator(iterator(), size(), 0);
		}
	}

	/**
	 * @see #forEach(BiConsumer)
	 */
	@NotNull
	@Override
	@Deprecated
	public Set<Map.Entry<CharSequence, V>> entrySet() {
		Set<Map.Entry<CharSequence, V>> set = new ObjectOpenHashSet<>(size);
		forEach((k, v) -> set.add(new AbstractMap.SimpleImmutableEntry<>(k, v)));
		return Collections.unmodifiableSet(set);
	}

	public void forEachSince(CharSequence s, BiConsumer<? super CharSequence, ? super V> consumer) {
		forEachSince(s, 0, s.length(), consumer);
	}

	public void forEachSince(CharSequence s, int len, BiConsumer<? super CharSequence, ? super V> consumer) {
		forEachSince(s, 0, len, consumer);
	}

	public void forEachSince(CharSequence s, int i, int len, BiConsumer<? super CharSequence, ? super V> consumer) {
		StringBuilder base = new StringBuilder();
		Entry<V> entry = matches(s, i, len, base);
		if (entry == null) return;
		recursionEntry(root, consumer, base);
	}

	@Override
	public void forEach(BiConsumer<? super CharSequence, ? super V> consumer) {
		recursionEntry(root, consumer, new StringBuilder());
	}

	@SuppressWarnings("unchecked")
	private static <V> void recursionEntry(Entry<V> parent, BiConsumer<? super CharSequence, ? super V> consumer, StringBuilder sb) {
		if (parent.value != UNDEFINED) {
			consumer.accept(sb.toString(), parent.value);
		}
		for (TrieEntry entry : parent) {
			entry.append(sb);
			recursionEntry((Entry<V>) entry, consumer, sb);
			sb.setLength(sb.length() - entry.length());
		}
	}

	@Override
	public boolean remove(Object key, Object value) {
		int os = size;
		CharSequence cs = (CharSequence) key;
		remove(cs, 0, cs.length(), value);
		return os != size;
	}

	@Override
	public boolean replace(CharSequence key, V oldValue, V newValue) {
		Entry<V> entry = getEntry(key, 0, key.length());
		if (entry == null || entry.value == UNDEFINED) return false;
		if (Objects.equals(oldValue, entry.value)) {
			entry.value = newValue;
			return true;
		}
		return false;
	}

	@Override
	public V compute(CharSequence key, BiFunction<? super CharSequence, ? super V, ? extends V> remap) {
		Entry<V> entry = getEntry(key, 0, key.length());
		V newV = remap.apply(key, entry == null || entry.value == UNDEFINED ? null : entry.value);
		if (newV == null) {
			if (entry != null && entry.value != UNDEFINED) {
				remove(key, 0, key.length(), UNDEFINED);
			}
			return null;
		} else if (entry == null) {
			entry = entryForPut(key, 0, key.length());
		}

		if (entry.value == UNDEFINED) size++;
		entry.value = newV;

		return newV;
	}

	@Override
	public V computeIfAbsent(CharSequence key, Function<? super CharSequence, ? extends V> map) {
		Entry<V> entry = getEntry(key, 0, key.length());
		if (entry != null && entry.value != UNDEFINED) return entry.value;
		if (entry == null) {
			entry = entryForPut(key, 0, key.length());
		}
		if (entry.value == UNDEFINED) size++;
		return entry.value = map.apply(key);
	}

	@Override
	public V computeIfPresent(CharSequence key, BiFunction<? super CharSequence, ? super V, ? extends V> remap) {
		Entry<V> entry = getEntry(key, 0, key.length());
		if (entry == null || entry.value == UNDEFINED) return null;
		if (entry.value == null) return null; // default implement guarantee
		V newV = remap.apply(key, entry.value);
		if (newV == null) {
			remove(key, 0, key.length(), UNDEFINED);
			return null;
		}

		return entry.value = newV;
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		CharSequence cs = (CharSequence) key;
		Entry<V> entry = getEntry(cs, 0, cs.length());
		if (entry == null || entry.value == UNDEFINED) return defaultValue;
		return entry.value;
	}

	@Override
	public V putIfAbsent(CharSequence key, V value) {
		int os = size;
		Entry<V> entry = entryForPut(key, 0, key.length());
		if (os != size) {
			return entry.value = value;
		}
		return entry.value;
	}

	@Override
	public V replace(CharSequence key, V value) {
		Entry<V> entry = getEntry(key, 0, key.length());
		if (entry == null) return null;

		V v = entry.value;
		if (v == UNDEFINED) v = null;

		entry.value = value;
		return v;
	}
}