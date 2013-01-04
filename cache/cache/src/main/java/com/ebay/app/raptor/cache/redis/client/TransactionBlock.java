package com.ebay.app.raptor.cache.redis.client;

import com.ebay.app.raptor.cache.redis.exceptions.JedisException;

public abstract class TransactionBlock extends Transaction {
    public TransactionBlock(Client client) {
	super(client);
    }

    public TransactionBlock() {
    }

    public abstract void execute() throws JedisException;

    public void setClient(Client client) {
	this.client = client;
    }
}
