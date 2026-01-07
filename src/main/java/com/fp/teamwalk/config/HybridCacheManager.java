package com.fp.teamwalk.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;

public class HybridCacheManager implements CacheManager {
    private final CaffeineCacheManager l1;
    private final RedisCacheManager l2;

    public HybridCacheManager(CaffeineCacheManager l1, RedisCacheManager l2) {
        this.l1 = l1;
        this.l2 = l2;
    }

    @Override
    public Cache getCache(String name) {
        Cache caffeineCache = l1.getCache(name);
        Cache redisCache = l2.getCache(name);
        return new HybridCache(name, caffeineCache, redisCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return l1.getCacheNames();
    }
}
