/**
 * Copyright 2018 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.transaction.operation.set;

import java.util.concurrent.TimeUnit;

import org.redisson.RedissonSetCache;
import org.redisson.api.RObject;
import org.redisson.api.RSetCache;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.transaction.operation.TransactionalOperation;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public class AddCacheOperation extends TransactionalOperation {

    final Object value;
    final long ttl;
    final TimeUnit timeUnit;
    
    public AddCacheOperation(RObject set, Object value) {
        this(set, value, 0, null);
    }
    
    public AddCacheOperation(RObject set, Object value, long ttl, TimeUnit timeUnit) {
        super(set.getName(), set.getCodec());
        this.value = value;
        this.timeUnit = timeUnit;
        this.ttl = ttl;
    }


    @Override
    public void commit(CommandAsyncExecutor commandExecutor) {
        RSetCache<Object> set = new RedissonSetCache<Object>(codec, null, commandExecutor, name, null);
        if (timeUnit != null) {
            set.addAsync(value, ttl, timeUnit);
        } else {
            set.addAsync(value);
        }
        set.getLock(value).unlockAsync();
    }

    @Override
    public void rollback(CommandAsyncExecutor commandExecutor) {
        RSetCache<Object> set = new RedissonSetCache<Object>(codec, null, commandExecutor, name, null);
        set.getLock(value).unlockAsync();
    }

}
