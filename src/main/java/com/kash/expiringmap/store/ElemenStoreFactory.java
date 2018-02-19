package com.kash.expiringmap.store;

import com.kash.expiringmap.internal.ElementStore;

public class ElemenStoreFactory {

	public static <K,V> ElementStore<K, V> getStore(StoreType storeType){
		ElementStore<K, V> store = new InMemElementStore<K, V>();
		switch (storeType) {
		case CONCURRENTMAP :
			store = new InMemElementStore<K, V>();
			break;
		case MYSQLMAP :
			break;
		case REDISMAP :
			break;
		}
		return store;
			
	}
}
