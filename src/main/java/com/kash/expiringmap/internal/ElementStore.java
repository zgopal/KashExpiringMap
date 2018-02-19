package com.kash.expiringmap.internal;

public interface ElementStore<K,V> {
	void store(K key,V value);
	V retrieve(K key);
	V delete(K key);
	void update(K key, V value);
}
