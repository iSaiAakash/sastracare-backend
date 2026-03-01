package com.academic.sastracare.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AudioCacheService {

    private final RedisTemplate<String, byte[]> redisTemplate;

    public AudioCacheService(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheAudio(String key, byte[] audio) {
        redisTemplate.opsForValue()
                .set(key, audio, 1, TimeUnit.HOURS); // 1 hour TTL
    }

    public byte[] getAudio(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}