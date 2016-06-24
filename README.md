A sample code using [CaffeineCache](https://github.com/ben-manes/caffeine) through Annotation in Spring

- Available [asynchronously refresh](https://github.com/ben-manes/caffeine/wiki/Refresh) setting in annotation.

### Usage

```java
@Service
public class TimeService {

	@SimpleCaffeineCache(name = "getNowCache", expireAfterWrite = 20, refreshAfterWrite = 5)
	public String getNow(String name) throws InterruptedException {
		Thread.sleep(3000);
		return name + " / " + DateTime.now().toString();
	}
	
}
```


### Setting

build.gradle
```java
repositories {
    maven {
        url "https://github.com/kimwz/caffeine-cache-annotation/raw/master/release/"
    }
}

compile('com.github.kimwz.caffeinecache:caffeinecache-annotation:1.0')

// default dependencies
compile('org.aspectj:aspectjweaver:1.8.6')
compile("com.github.ben-manes.caffeine:caffeine:2.3.1")
```

CacheConfig.java
```java
@Configuration
@EnableAspectJAutoProxy
public class CacheConfig {
	@Bean
	public SimpleCaffeineCacheAspect simpleCaffeineCache() {
		return new SimpleCaffeineCacheAspect();
	}
}
```


