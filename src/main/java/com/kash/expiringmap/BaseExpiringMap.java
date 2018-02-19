package com.kash.expiringmap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.spi.StateFactory;

import com.kash.expiringmap.expirer.ExpirerFactory;
import com.kash.expiringmap.expirer.ExpirerType;
import com.kash.expiringmap.expirer.InMemScheduledExpirer;
import com.kash.expiringmap.inmem.InMemExpiringMap;
import com.kash.expiringmap.inmem.InMemExpiringMap.Builder;
import com.kash.expiringmap.internal.ElementStore;
import com.kash.expiringmap.internal.ExpiryFiredListener;
import com.kash.expiringmap.internal.KeyExpirer;
import com.kash.expiringmap.store.ElemenStoreFactory;
import com.kash.expiringmap.store.InMemElementStore;
import com.kash.expiringmap.store.StoreType;

public class BaseExpiringMap<K, V> implements ExpiringMap<K, V> {
	protected ElementStore<K, V> store;
	protected KeyExpirer<K> expirer;
	protected EntryLoader<K, V> loader;
	protected LoadMode mode = LoadMode.SYNC;
	protected ExpiryProfile expiryProfile;
	protected ExecutorService loaderService = Executors.newCachedThreadPool();
	protected ExpiryListener<K, V> expiryListener;
	
	private BaseExpiringMap(Builder<K, V> builder) {
		store = ElemenStoreFactory.getStore(builder.storeType);
		expirer = ExpirerFactory.getExpirer(builder.expirerType);
		expiryProfile = builder.profile;
		loader = builder.loader;
		mode = builder.mode;
		expiryListener = builder.listener;
	}

	public V get(K key) {
		V value = store.retrieve(key);
		if (value == null) {
			handleCacheMiss(key);
			if (mode.equals(LoadMode.SYNC)) {
				value = store.retrieve(key);
			}
		}

		if (expiryProfile.strategy.equals(ExpiryStrategy.LAST_ACCESSES)) {
			expirer.resetExpiry(key);
		}
		return value;
	}

	private void handleCacheMiss(final K key) {
		if (loader != null) {
			if (mode.equals(LoadMode.SYNC)) {
				V value = loadKey(key);
				put(key, value);
			} else if (mode.equals(LoadMode.ASYNC)) {
				loaderService.submit(new Runnable() {

					public void run() {
						V value = loader.load(key);
						put(key, value);
					}
				});
			}
		}

	}

	private V loadKey(K key) {
		return loader.load(key);
	}

	public void put(K key, V value) {
		put(key, value, expiryProfile);
	}

	public void put(K key, V value, ExpiryProfile profile) {
		store.store(key, value);
		expirer.registerExpiry(key, profile.duration, profile.timeunit, new ExpiryFiredListener<K>() {
			public void onExpiry(K key) {
				System.out.println("From AbstractExpiringMap: Expiring key: " + key);
				V value = store.delete(key);
				if (expiryListener != null) {
					expiryListener.expired(key, value);
				}
			}
		});
	}

	public void remove(K key) {
		store.delete(key);
		expirer.cancelExpiry(key);
	}

	public static <K, V> Builder<K, V> builder() {
		return new Builder<K, V>();
	}

	public static class Builder<K1, V1> {
		ExpiryProfile profile;
		EntryLoader<K1, V1> loader;
		LoadMode mode;
		ExpiryListener<K1, V1> listener;
		StoreType storeType;
		ExpirerType expirerType;

		public Builder() {
			// TODO Auto-generated constructor stub
		}

		public Builder<K1, V1> withStore(StoreType storeType) {
			this.storeType = storeType;
			return this;
		}
		
		public Builder<K1, V1> withExpirer(ExpirerType expirerType) {
			this.expirerType = expirerType;
			return this;
		}
		
		public Builder<K1, V1> withprofile(ExpiryProfile profile) {
			this.profile = profile;
			return this;
		}

		public Builder<K1, V1> withLoader(EntryLoader<K1, V1> loader) {
			this.loader = loader;
			return this;
		}

		public Builder<K1, V1> withLoadMode(LoadMode mode) {
			this.mode = mode;
			return this;
		}

		public Builder<K1, V1> withExpiryListener(ExpiryListener<K1, V1> listener) {
			this.listener = listener;
			return this;
		}

		public BaseExpiringMap<K1, V1> build() {
			return new BaseExpiringMap<K1, V1>(this);
		}
	}
}
