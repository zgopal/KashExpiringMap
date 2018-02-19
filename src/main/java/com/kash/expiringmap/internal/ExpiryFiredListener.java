package com.kash.expiringmap.internal;

public interface ExpiryFiredListener<K> {
	void onExpiry(K key);
}
