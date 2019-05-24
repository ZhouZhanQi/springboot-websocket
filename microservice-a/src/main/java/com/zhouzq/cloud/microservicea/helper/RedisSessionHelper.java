package com.zhouzq.cloud.microservicea.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouzq
 * @date 2019/5/24
 * @desc
 *
 * 将用户名称和websocket sessionId的关系存储到redis，提供添加、删除、查询
 */
@Service
public class RedisSessionHelper implements IRedisSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSessionHelper.class);

    private RedisTemplate<String, String> template;

    // key = 登录用户名称， value=websocket的sessionId
    private ConcurrentHashMap<String,String> redisHashMap = new ConcurrentHashMap<>(32);

    @Override
    public void add(String name, String wsSessionId) {
        BoundValueOperations<String,String> boundValueOperations = template.boundValueOps(name);
        boundValueOperations.set(wsSessionId,24 * 3600, TimeUnit.SECONDS);
    }

    @Override
    public boolean del(String name) {
        return template.execute(connection -> {
            byte[] rawKey = template.getStringSerializer().serialize(name);
            return connection.del(rawKey) > 0;
        }, true);
    }

    @Override
    public String get(String name) {
        BoundValueOperations<String,String> boundValueOperations = template.boundValueOps(name);
        return boundValueOperations.get();
    }
}
