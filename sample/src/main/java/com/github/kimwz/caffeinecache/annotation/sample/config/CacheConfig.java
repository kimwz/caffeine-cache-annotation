package com.github.kimwz.caffeinecache.annotation.sample.config;

import com.github.kimwz.caffeinecache.annotation.SimpleCaffeineCacheAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class CacheConfig {
	@Bean
	public SimpleCaffeineCacheAspect simpleCaffeineCache() {
		return new SimpleCaffeineCacheAspect();
	}
}
