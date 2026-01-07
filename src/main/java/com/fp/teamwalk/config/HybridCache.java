package com.fp.teamwalk.config;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import java.util.concurrent.Callable;

public class HybridCache implements Cache {

    private final String name;
    private final Cache l1Cache; // Caffeine
    private final Cache l2Cache; // Redis

    public HybridCache(String name, Cache l1Cache, Cache l2Cache) {
        this.name = name;
        this.l1Cache = l1Cache;
        this.l2Cache = l2Cache;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    /**
     * Tiered Read:
     * 1. Check Caffeine (L1)
     * 2. If miss, check Redis (L2) and populate L1
     */
    @Override
    @Nullable
    public ValueWrapper get(Object key) {
        // Check L1
        ValueWrapper wrapper = l1Cache.get(key);
        if (wrapper != null) {
            return wrapper;
        }

        // Check L2
        wrapper = l2Cache.get(key);
        if (wrapper != null) {
            // Write back to L1 for next time
            l1Cache.put(key, wrapper.get());
        }
        return wrapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        T value = l1Cache.get(key, type);
        if (value != null) {
            return value;
        }

        value = l2Cache.get(key, type);
        if (value != null) {
            l1Cache.put(key, value);
        }
        return value;
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        return l1Cache.get(key, () -> {
            T value = l2Cache.get(key, valueLoader);
            return value;
        });
    }

    /**
     * Tiered Write: Populate both layers
     */
    @Override
    public void put(Object key, @Nullable Object value) {
        l1Cache.put(key, value);
        l2Cache.put(key, value);
    }

    /**
     * Tiered Evict: Clear from both layers
     */
    @Override
    public void evict(Object key) {
        l1Cache.evict(key);
        l2Cache.evict(key);
    }

    @Override
    public void clear() {
        l1Cache.clear();
        l2Cache.clear();
    }

    @Override
    @Nullable
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        l2Cache.putIfAbsent(key, value);
        return l1Cache.putIfAbsent(key, value);
    }
}
