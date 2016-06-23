package kr.kimwz.sample.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class SimpleCaffeineCacheAspect {
	static final Logger logger	= LoggerFactory.getLogger(SimpleCaffeineCacheAspect.class);

	ConcurrentHashMap<String, LoadingCache> caches = new ConcurrentHashMap<>();

	@Around("@annotation(SimpleCaffeineCache) && @annotation(annotation)")
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
							logger.error("Failed LoadingCache", throwable);
						}
						return null;
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
			Assert.notNull(elements, "Elements must not be null");
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
			return getClass().getSimpleName() + " [" + StringUtils.arrayToCommaDelimitedString(this.params) + "]";
		}

	}
}
