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

package org.apache.ignite.internal.visor.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ignite.IgniteException;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.internal.processors.task.GridInternal;
import org.apache.ignite.internal.visor.VisorOneNodeTask;
import org.jetbrains.annotations.Nullable;

/**
 * Task that collect cache metrics from all nodes.
 */
@GridInternal
public class VisorCacheConfigurationCollectorTask
    extends VisorOneNodeTask<VisorCacheConfigurationCollectorTaskArg, Map<String, VisorCacheConfiguration>> {
    /** */
    private static final long serialVersionUID = 0L;

    /** {@inheritDoc} */
    @Override protected VisorCacheConfigurationCollectorJob job(VisorCacheConfigurationCollectorTaskArg arg) {
        return new VisorCacheConfigurationCollectorJob(arg, debug);
    }

    /** {@inheritDoc} */
    @Override protected @Nullable Map<String, VisorCacheConfiguration> reduce0(
        List<ComputeJobResult> results
    ) throws IgniteException {
        if (results == null)
            return null;

        Map<String, VisorCacheConfiguration> map = new HashMap<>();

        List<Exception> resultsExceptions = null;

        for (ComputeJobResult res : results) {
            if (res.getException() == null)
                map.putAll(res.getData());
            else {
                if (resultsExceptions == null)
                    resultsExceptions = new ArrayList<>(results.size());

                resultsExceptions.add(new IgniteException("Job failed on node: " + res.getNode().id(), res.getException()));
            }
        }

        if (resultsExceptions != null) {
            IgniteException e = new IgniteException("Reduce failed because of job failed on some nodes");

            for (Exception ex : resultsExceptions)
                e.addSuppressed(ex);

            throw e;
        }

        return map;
    }
}
