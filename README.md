A sample code using [CaffeineCache](https://github.com/ben-manes/caffeine) through Annotation in Spring

- Available [asynchronously refresh](https://github.com/ben-manes/caffeine/wiki/Refresh) setting in annotation.

```java
@SimpleCaffeineCache(name = "getNowCache", expireAfterWrite = 20, refreshAfterWrite = 5)
public String getNow(String name) throws InterruptedException {
	Thread.sleep(3000);
	return name + " / " + DateTime.now().toString();
}
```

