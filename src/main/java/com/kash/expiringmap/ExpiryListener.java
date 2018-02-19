package com.kash.expiringmap;

public interface ExpiryListener<K, V> {

	void expired(K key, V value);
}
