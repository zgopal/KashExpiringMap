package com.kash.expiringmap.expirer;

import com.kash.expiringmap.internal.KeyExpirer;

public class ExpirerFactory {
	public static <K> KeyExpirer<K> getExpirer(ExpirerType expirerType) {
		KeyExpirer<K> expirer = new InMemScheduledExpirer<K>();
		switch (expirerType) {
		case SCHEDULED_IN_MEM:
			expirer = new InMemScheduledExpirer<K>();
			break;
		default:
			break;
		}
		return expirer;
	}
}
