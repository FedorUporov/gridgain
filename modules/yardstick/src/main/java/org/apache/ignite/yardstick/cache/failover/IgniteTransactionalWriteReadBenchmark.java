/*
 *                   GridGain Community Edition Licensing
 *                   Copyright 2019 GridGain Systems, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License") modified with Commons Clause
 * Restriction; you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * Commons Clause Restriction
 * 
 * The Software is provided to you by the Licensor under the License, as defined below, subject to
 * the following condition.
 * 
 * Without limiting other conditions in the License, the grant of rights under the License will not
 * include, and the License does not grant to you, the right to Sell the Software.
 * For purposes of the foregoing, “Sell” means practicing any or all of the rights granted to you
 * under the License to provide to third parties, for a fee or other consideration (including without
 * limitation fees for hosting or consulting/ support services related to the Software), a product or
 * service whose value derives, entirely or substantially, from the functionality of the Software.
 * Any license notice or attribution required by the License must also include this Commons Clause
 * License Condition notice.
 * 
 * For purposes of the clause above, the “Licensor” is Copyright 2019 GridGain Systems, Inc.,
 * the “License” is the Apache License, Version 2.0, and the Software is the GridGain Community
 * Edition software provided with this notice.
 */

package org.apache.ignite.yardstick.cache.failover;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.apache.ignite.transactions.TransactionConcurrency.PESSIMISTIC;
import static org.apache.ignite.transactions.TransactionIsolation.REPEATABLE_READ;
import static org.apache.ignite.yardstick.IgniteBenchmarkUtils.doInTransaction;
import static org.yardstickframework.BenchmarkUtils.println;

/**
 * Transactional write read failover benchmark.
 * <p>
 * Each client generates a random integer K in a limited range and creates keys in the form 'key-' + K + '-1',
 * 'key-' + K + '-2', ... Then client starts a pessimistic repeatable read transaction, reads value associated with
 * each key. Values must be equal. Client increments value by 1, commits the transaction.
 */
public class IgniteTransactionalWriteReadBenchmark extends IgniteFailoverAbstractBenchmark<String, Long> {
    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        final int k = nextRandom(args.range());

        assert args.keysCount() > 0 : "Count of keys: " + args.keysCount();

        final String[] keys = new String[args.keysCount()];

        for (int i = 0; i < keys.length; i++)
            keys[i] = "key-" + k + "-" + i;

        return doInTransaction(ignite().transactions(), PESSIMISTIC, REPEATABLE_READ, new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                Map<String, Long> map = new HashMap<>();

                final int timeout = args.cacheOperationTimeoutMillis();

                for (String key : keys) {
                    Long val = cache.getAsync(key).get(timeout);

                    map.put(key, val);
                }

                Set<Long> values = new HashSet<>(map.values());

                if (values.size() != 1) {
                    // Print all usefull information and finish.
                    println(cfg, "Got different values for keys [map=" + map + "]");

                    println(cfg, "Cache content:");

                    for (int k = 0; k < args.range(); k++) {
                        for (int i = 0; i < args.keysCount(); i++) {
                            String key = "key-" + k + "-" + i;

                            Long val = cache.getAsync(key).get(timeout);

                            if (val != null)
                                println(cfg, "Entry [key=" + key + ", val=" + val + "]");
                        }
                    }

                    throw new IgniteConsistencyException("Found different values for keys (see above information).");
                }

                final Long oldVal = map.get(keys[0]);

                final Long newVal = oldVal == null ? 0 : oldVal + 1;

                for (String key : keys)
                    cache.putAsync(key, newVal).get(timeout);

                return true;
            }
        });
    }

    /** {@inheritDoc} */
    @Override protected String cacheName() {
        return "tx-write-read";
    }
}
