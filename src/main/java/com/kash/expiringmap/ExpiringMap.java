package com.kash.expiringmap;

public interface ExpiringMap<K,V> {
	void put(K key, V value);
	void put(K key, V value, ExpiryProfile profile);
	V get(K key);
	void remove(K key);
}
