package com.kash.expiringmap.expirer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.kash.expiringmap.internal.ExpiryFiredListener;
import com.kash.expiringmap.internal.KeyExpirer;

public class InMemScheduledExpirer<K> implements KeyExpirer<K> {

	private ScheduledExecutorService executor;
	private Map<K, ExpiryProfileHolder> keyFutureMap;

	public InMemScheduledExpirer() {
		executor = Executors.newScheduledThreadPool(5);
		executor = Executors.newScheduledThreadPool(5, new ThreadFactory() {

			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setDaemon(false);
				return thread;
			}
		});
		keyFutureMap = new ConcurrentHashMap<K, ExpiryProfileHolder>();
	}

	public void registerExpiry(final K key, long duration, TimeUnit timeUnit, final ExpiryFiredListener<K> listener) {
		ScheduledFuture<?> schedule = executor.schedule(new Runnable() {

			public void run() {
				listener.onExpiry(key);
				keyFutureMap.remove(key);
			}
		}, duration, timeUnit);
		keyFutureMap.put(key, new ExpiryProfileHolder(duration, timeUnit, schedule, listener));
	}

	public void resetExpiry(K key) {
		ExpiryProfileHolder expiry = keyFutureMap.get(key);
		if (expiry==null)
			return;
		cancelExpiry(key);
		registerExpiry(key, expiry.duration, expiry.timeUnit, expiry.listener);
	}

	public void cancelExpiry(K key) {
		keyFutureMap.get(key).schedule.cancel(true);
		keyFutureMap.remove(key);
	}

	class ExpiryProfileHolder {
		long duration;
		TimeUnit timeUnit;
		ScheduledFuture<?> schedule;
		ExpiryFiredListener<K> listener;

		public ExpiryProfileHolder(long duration, TimeUnit timeUnit, ScheduledFuture<?> schedule,
				ExpiryFiredListener<K> listener) {
			this.duration = duration;
			this.timeUnit = timeUnit;
			this.schedule = schedule;
			this.listener = listener;

		}

	}

}
