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

package org.apache.ignite.internal.processors.rest.request;

import java.util.List;
import org.apache.ignite.internal.util.typedef.internal.S;

/**
 * Grid task command request.
 */
public class GridRestTaskRequest extends GridRestRequest {
    /** Task name. */
    private String taskName;

    /** Task Id. */
    private String taskId;

    /** Parameters. */
    private List<Object> params;

    /** Asynchronous execution flag. */
    private boolean async;

    /** Timeout. */
    private long timeout;

    /**
     * @return Task name, if specified, {@code null} otherwise.
     */
    public String taskName() {
        return taskName;
    }

    /**
     * @param taskName Name of task for execution.
     */
    public void taskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * @return Task identifier, if specified, {@code null} otherwise.
     */
    public String taskId() {
        return taskId;
    }

    /**
     * @param taskId Task identifier.
     */
    public void taskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return Asynchronous execution flag.
     */
    public boolean async() {
        return async;
    }

    /**
     * @param async Asynchronous execution flag.
     */
    public void async(boolean async) {
        this.async = async;
    }

    /**
     * @return Task execution parameters.
     */
    public List<Object> params() {
        return params;
    }

    /**
     * @param params Task execution parameters.
     */
    public void params(List<Object> params) {
        this.params = params;
    }

    /**
     * @return Timeout.
     */
    public long timeout() {
        return timeout;
    }

    /**
     * @param timeout Timeout.
     */
    public void timeout(long timeout) {
        this.timeout = timeout;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(GridRestTaskRequest.class, this, super.toString());
    }
}