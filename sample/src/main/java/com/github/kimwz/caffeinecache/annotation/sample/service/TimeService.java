package com.github.kimwz.caffeinecache.annotation.sample.service;

import com.github.kimwz.caffeinecache.annotation.SimpleCaffeineCache;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;


@Service
public class TimeService {

	@SimpleCaffeineCache(name = "now", expireAfterWrite = 20, refreshAfterWrite = 5)
	public String getNow(String name) throws InterruptedException {
		Thread.sleep(3000);
		return name + " / " + DateTime.now().toString();
	}
}
