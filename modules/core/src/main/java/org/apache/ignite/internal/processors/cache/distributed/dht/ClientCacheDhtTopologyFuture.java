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

package org.apache.ignite.internal.processors.cache.distributed.dht;

import java.util.Collection;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.processors.affinity.AffinityTopologyVersion;
import org.apache.ignite.internal.processors.cache.CacheGroupContext;
import org.apache.ignite.internal.util.typedef.internal.U;

/**
 * Topology future created for client cache start.
 */
public class ClientCacheDhtTopologyFuture extends GridDhtTopologyFutureAdapter {
    /** */
    final AffinityTopologyVersion topVer;

    /**
     * @param topVer Topology version.
     */
    public ClientCacheDhtTopologyFuture(AffinityTopologyVersion topVer) {
        assert topVer != null;

        this.topVer = topVer;

        onDone(topVer);
    }

    /**
     * @param topVer Topology version.
     * @param e Error.
     */
    public ClientCacheDhtTopologyFuture(AffinityTopologyVersion topVer, IgniteCheckedException e) {
        assert e != null;
        assert topVer != null;

        this.topVer = topVer;

        onDone(e);
    }

    /**
     * @param grp Cache group.
     * @param topNodes Topology nodes.
     */
    public void validate(CacheGroupContext grp, Collection<ClusterNode> topNodes) {
        grpValidRes = U.newHashMap(1);

        grpValidRes.put(grp.groupId(), validateCacheGroup(grp,topNodes));
    }

    /** {@inheritDoc} */
    @Override public AffinityTopologyVersion initialVersion() {
        return topVer;
    }

    /** {@inheritDoc} */
    @Override public boolean exchangeDone() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public AffinityTopologyVersion topologyVersion() {
        return topVer;
    }

    /** {@inheritDoc} */
    @Override public boolean changedAffinity() {
        return true;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return "ClientCacheDhtTopologyFuture [topVer=" + topVer + ']';
    }
}
