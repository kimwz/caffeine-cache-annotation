package kr.kimwz.sample.cache;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleCaffeineCache {
	String name() default "";
	int maximumSize() default -1;
	int expireAfterWrite() default -1;
	int expireAfterAccess() default -1;
	int refreshAfterWrite() default -1;
}
