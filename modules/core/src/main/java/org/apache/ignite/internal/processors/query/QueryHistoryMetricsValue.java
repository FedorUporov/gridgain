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

package org.apache.ignite.internal.processors.query;

/**
 * Immutable query metrics.
 */
class QueryHistoryMetricsValue {
    /** Number of executions. */
    private final long execs;

    /** Number of failures. */
    private final long failures;

    /** Minimum time of execution. */
    private final long minTime;

    /** Maximum time of execution. */
    private final long maxTime;

    /** Last start time of execution. */
    private final long lastStartTime;

    /**
     * @param execs Number of executions.
     * @param failures Number of failure.
     * @param minTime Min time of execution.
     * @param maxTime Max time of execution.
     * @param lastStartTime Last start time of execution.
     */
    public QueryHistoryMetricsValue(long execs, long failures, long minTime, long maxTime, long lastStartTime) {
        this.execs = execs;
        this.failures = failures;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.lastStartTime = lastStartTime;
    }

   /**
     * Gets total number execution of query.
     *
     * @return Number of executions.
     */
    public long execs() {
        return execs;
    }

    /**
     * Gets number of times a query execution failed.
     *
     * @return Number of times a query execution failed.
     */
    public long failures() {
        return failures;
    }

    /**
     * Gets minimum execution time of query.
     *
     * @return Minimum execution time of query.
     */
    public long minTime() {
        return minTime;
    }

    /**
     * Gets maximum execution time of query.
     *
     * @return Maximum execution time of query.
     */
    public long maxTime() {
        return maxTime;
    }

    /**
     * Gets latest query start time.
     *
     * @return Latest time query was stared.
     */
    public long lastStartTime() {
        return lastStartTime;
    }
}
