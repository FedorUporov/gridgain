/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.websession;

import org.apache.ignite.testsuites.IgniteIgnore;

/**
 * Tests web sessions with TRANSACTIONAL cache.
 */
public class WebSessionTransactionalSelfTest extends WebSessionSelfTest {
    /** {@inheritDoc} */
    @Override protected String getCacheName() {
        return "partitioned_tx";
    }

    /** {@inheritDoc} */
    @IgniteIgnore(value = "https://issues.apache.org/jira/browse/IGNITE-810", forceFailure = true)
    @Override public void testRestarts() throws Exception {
        // No-op.
    }

    /** {@inheritDoc} */
    @IgniteIgnore(value = "https://issues.apache.org/jira/browse/IGNITE-810", forceFailure = true)
    @Override public void testInvalidatedSession() throws Exception {
        // No-op.
    }

    /** {@inheritDoc} */
    @IgniteIgnore(value = "https://issues.apache.org/jira/browse/IGNITE-810", forceFailure = true)
    @Override public void testClientReconnectRequest() throws Exception {
        // No-op.
    }
}
