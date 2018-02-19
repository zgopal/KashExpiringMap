package com.kash.expiringmap.store;

import java.util.concurrent.ConcurrentHashMap;

import com.kash.expiringmap.internal.ElementStore;

public class InMemElementStore<K, V> implements ElementStore<K, V> {

	private ConcurrentHashMap<K, V> store;

	public InMemElementStore() {
		store = new ConcurrentHashMap<K, V>();
	}

	public void store(K key, V value) {
		store.put(key, value);

	}

	public V retrieve(K key) {
		return store.get(key);
	}

	public V delete(K key) {
		return store.remove(key);
	}

	public void update(K key, V value) {
		store.put(key, value);
	}

}
