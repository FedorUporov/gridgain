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

package org.apache.ignite.internal.processors.query.h2.opt;

import java.io.IOException;
import org.apache.ignite.internal.util.GridConcurrentHashSet;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.LockObtainFailedException;

/**
 * Lucene {@link LockFactory} implementation.
 */
public class GridLuceneLockFactory extends LockFactory {
    /** */
    private final GridConcurrentHashSet<String> locks = new GridConcurrentHashSet<>();

    /** {@inheritDoc} */
    @Override public Lock obtainLock(Directory dir, String lockName) throws IOException {
        if (locks.add(lockName))
            return new LockImpl(lockName);
        else
            throw new LockObtainFailedException("lock instance already obtained: (dir=" + dir + ", lockName=" + lockName + ")");
    }

    /**
     * {@link Lock} Implementation.
     */
    private class LockImpl extends Lock {
        /** */
        private final String lockName;

        /** */
        private volatile boolean closed;

        /**
         * @param lockName Lock name.
         */
        private LockImpl(String lockName) {
            this.lockName = lockName;
        }

        /** {@inheritDoc} */
        @Override public void ensureValid() throws IOException {
            if (closed)
                throw new AlreadyClosedException("Lock instance already released: " + this);

            // check we are still in the locks map (some debugger or something crazy didn't remove us)
            if (!locks.contains(lockName))
                throw new AlreadyClosedException("Lock instance was invalidated from map: " + this);
        }

        /** {@inheritDoc} */
        @Override public void close() throws IOException {
            if (closed)
                return;

            try {
                if (!locks.remove(lockName))
                    throw new AlreadyClosedException("Lock was already released: " + this);
            }
            finally {
                closed = true;
            }
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return super.toString() + ": " + lockName;
        }
    }
}