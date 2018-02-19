package com.kash.expiringmap;

public interface EntryLoader<K,V> {
	V load(K key);
}
