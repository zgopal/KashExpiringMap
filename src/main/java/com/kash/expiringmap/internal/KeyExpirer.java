package com.kash.expiringmap.internal;

import java.util.concurrent.TimeUnit;

public interface KeyExpirer<K> {
	void registerExpiry(K key, long duration, TimeUnit timeUnit, ExpiryFiredListener<K> listener);

	void resetExpiry(K key);
	void cancelExpiry(K key);
}
