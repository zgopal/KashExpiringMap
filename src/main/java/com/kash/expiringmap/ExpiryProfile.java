package com.kash.expiringmap;

import java.util.concurrent.TimeUnit;

public class ExpiryProfile {
	long duration;
	TimeUnit timeunit;
	ExpiryStrategy strategy;

	private ExpiryProfile(Builder builder) {
		this.duration = builder.duration;
		this.timeunit = builder.timeunit;
		this.strategy = builder.strategy;
	}
	
	public static Builder builder(){
		return new Builder();
	}

	public static class Builder {
		long duration;
		TimeUnit timeunit;
		ExpiryStrategy strategy;

		public Builder duration(long duration) {
			this.duration = duration;
			return this;
		}

		public Builder timeUnit(TimeUnit unit) {
			this.timeunit = unit;
			return this;
		}

		public Builder strategy(ExpiryStrategy strategy) {
			this.strategy = strategy;
			return this;
		}

		public ExpiryProfile build() {
			return new ExpiryProfile(this);
		}

	}
}
