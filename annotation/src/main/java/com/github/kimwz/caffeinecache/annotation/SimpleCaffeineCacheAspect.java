package com.github.kimwz.caffeinecache.annotation;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.sun.tools.javac.util.Assert;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.Serializable;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
public class SimpleCaffeineCacheAspect {
	ConcurrentHashMap<String, LoadingCache> caches = new ConcurrentHashMap<>();

	@Around("@annotation(com.github.kimwz.caffeinecache.annotation.SimpleCaffeineCache) && @annotation(annotation)")
	public Object autoRefreshCache(ProceedingJoinPoint joinPoint, SimpleCaffeineCache annotation) throws Throwable {
		if (!caches.containsKey(annotation.name())) {
			Caffeine cache = Caffeine.newBuilder();
			if (annotation.maximumSize() != -1) {
				cache = cache.maximumSize(annotation.maximumSize());
			}
			if (annotation.expireAfterWrite() != -1) {
				cache = cache.expireAfterWrite(annotation.expireAfterWrite(), TimeUnit.SECONDS);
			}
			if (annotation.expireAfterAccess() != -1) {
				cache = cache.expireAfterAccess(annotation.expireAfterAccess(), TimeUnit.SECONDS);
			}
			if (annotation.refreshAfterWrite() != -1) {
				cache = cache.refreshAfterWrite(annotation.refreshAfterWrite(), TimeUnit.SECONDS);
			}

			caches.put(annotation.name(), cache.build(
				key -> {
					try {
						return joinPoint.proceed(((CacheKey)key).getParams());
					} catch (Throwable throwable) {
						throw new RuntimeException("Fail to refresh cache", throwable);
					}
				}
			));
		}

		LoadingCache cache = caches.get(annotation.name());
		Object key = new CacheKey(joinPoint.getArgs());
		Object result = cache.get(key);

		if (result != null) {
			return result;
		}

		result = joinPoint.proceed();
		cache.put(key, result);
		return result;
	}

	private static class CacheKey implements Serializable {

		public static final CacheKey EMPTY = new CacheKey();

		private final Object[] params;
		private final int hashCode;


		public CacheKey(Object... elements) {
			Assert.checkNonNull(elements, "Elements must not be null");
			this.params = new Object[elements.length];
			System.arraycopy(elements, 0, this.params, 0, elements.length);
			this.hashCode = Arrays.deepHashCode(this.params);
		}

		public Object[] getParams() {
			return this.params;
		}

		@Override
		public boolean equals(Object obj) {
			return (this == obj || (obj instanceof CacheKey
					&& Arrays.deepEquals(this.params, ((CacheKey) obj).params)));
		}

		@Override
		public final int hashCode() {
			return hashCode;
		}

		@Override
		public String toString() {
			StringJoiner joiner = new StringJoiner(",", "[", "]");
			for (Object param : this.params) {
				joiner.add(param.toString());
			}
			return getClass().getSimpleName() + joiner.toString();
		}

	}
}
