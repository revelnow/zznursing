package com.zzyl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void test() {
        System.out.println("redis测试");
        System.out.println(redisTemplate);

    }
    @Test
    public void testString() {
        //
        redisTemplate.opsForValue().set("name", "张三");
        System.out.println(redisTemplate.opsForValue().get("name"));

        redisTemplate.opsForValue().set("age", "18");
        redisTemplate.opsForValue().set("age", "17");
        System.out.println(redisTemplate.opsForValue().get("age"));

        // 设置带有过期时间的key
        redisTemplate.opsForValue().set("token", "123qweasd",20, TimeUnit.SECONDS);
        System.out.println(redisTemplate.opsForValue().get("token"));

        // setnx 当指定的键key不存在时,会将key的值设置为value，返回true，否则返回false(不能覆盖原有值)
        System.out.println(redisTemplate.opsForValue().setIfAbsent("lock", "09876", 5, TimeUnit.MINUTES));
        System.out.println(redisTemplate.opsForValue().setIfAbsent("lock", "34567", 5, TimeUnit.MINUTES));
    }

    @Test
    public void testHash() {
        redisTemplate.opsForHash().put("user", "name", "张三");
        System.out.println(redisTemplate.opsForHash().get("user", "name"));

        redisTemplate.opsForHash().put("user", "age", "18");

        System.out.println(redisTemplate.opsForHash().keys("user"));
        System.out.println(redisTemplate.opsForHash().values("user"));
        redisTemplate.opsForHash().delete("user", "name");
    }

    @Test
    public void testList() {

        redisTemplate.opsForList().leftPushAll("list", "b", "c", "d");
        redisTemplate.opsForList().leftPush("list", "a");

        System.out.println(redisTemplate.opsForList().leftPop("list"));
        System.out.println(redisTemplate.opsForList().rightPop("list"));

        System.out.println(redisTemplate.opsForList().range("list", 0, -1));
        System.out.println(redisTemplate.opsForList().size("list"));

    }

    @Test
    public void testSet() {
        redisTemplate.opsForSet().add("set1", "a", "b", "c", "d");
        redisTemplate.opsForSet().add("set2", "a", "b", "x", "y");

        System.out.println(redisTemplate.opsForSet().intersect("set1", "set2"));
        System.out.println(redisTemplate.opsForSet().union("set1", "set2"));
    }
}