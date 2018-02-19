package com.kash.expiringmap.inmem;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.kash.expiringmap.BaseExpiringMap;
import com.kash.expiringmap.EntryLoader;
import com.kash.expiringmap.ExpiringMap;
import com.kash.expiringmap.ExpiryListener;
import com.kash.expiringmap.ExpiryProfile;
import com.kash.expiringmap.ExpiryStrategy;
import com.kash.expiringmap.LoadMode;
import com.kash.expiringmap.expirer.ExpirerType;
import com.kash.expiringmap.store.StoreType;

public class InMemExpiringMapTest {

	@Test
	public void testInMemExpiringMap() throws InterruptedException {
		ExpiringMap<String, String> myMap = BaseExpiringMap
											.<String, String> builder()
											.withprofile(ExpiryProfile
														.builder()
														.duration(10)
														.timeUnit(TimeUnit.SECONDS)
														.strategy(ExpiryStrategy.LAST_ACCESSES)
														.build())
											.withLoadMode(LoadMode.SYNC)
											.withLoader
											(new EntryLoader<String, String>() {
												public String load(String key) {
													System.out.println("Loading for key: " + key);
													return key + " reloaded";
												}
											})
											.withExpiryListener
											(new ExpiryListener<String, String>() {
												public void expired(String key, String value) {
													System.out.println("Expiry Listener: key: " + key + " value: " + value);
												}
											})
											.withStore(StoreType.CONCURRENTMAP)
											.withExpirer(ExpirerType.SCHEDULED_IN_MEM)
											.build();
		myMap.put("first", "first");
		Thread.sleep(5000);
		System.out.println(myMap.get("first"));
		Thread.sleep(9000);
		System.out.println(myMap.get("first"));
		Thread.sleep(11000);
		System.out.println(myMap.get("first"));
		Thread.sleep(1000);
		System.out.println(myMap.get("first"));
	}
}
