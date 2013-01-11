package com.ebay.app.raptor.cache.redis.client.util;

public abstract class ShardInfo<T> {
    private int weight;

    public ShardInfo() {
    }

    public ShardInfo(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }

    protected abstract T createResource();
    
    public abstract String getName();
}
