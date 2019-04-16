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

package org.apache.ignite.ml.math.primitives.vector;

import java.io.Externalizable;
import java.io.Serializable;
import org.apache.ignite.ml.math.Destroyable;
import org.apache.ignite.ml.math.StorageOpsMetrics;

/**
 * Data storage support for {@link Vector}.
 */
public interface VectorStorage extends Externalizable, StorageOpsMetrics, Destroyable {
    /**
     *
     *
     */
    public int size();

    /**
     * Gets element from vector by index and cast it to double.
     *
     * @param i Vector element index.
     * @return Value obtained for given element index.
     */
    public double get(int i);

    /**
     * @param i Vector element index.
     * @param <T> Type of stored element in vector.
     * @return Value obtained for given element index.
     */
    public <T extends Serializable> T getRaw(int i);

    /**
     * @param i Vector element index.
     * @param v Value to set at given index.
     */
    public void set(int i, double v);

    /**
     * @param i Vector element index.
     * @param v Value to set at given index.
     */
    public void setRaw(int i, Serializable v);

    /**
     * Gets underlying array if {@link StorageOpsMetrics#isArrayBased()} returns {@code true} and all values
     * in vector are Numbers.
     * Returns {@code null} if in other cases.
     *
     * @see StorageOpsMetrics#isArrayBased()
     */
    public default double[] data() {
        return null;
    }

    /**
     * @return Underlying array of serializable objects {@link StorageOpsMetrics#isArrayBased()} returns {@code true}.
     * Returns {@code null} if in other cases.
     */
    public default Serializable[] rawData() {
        return null;
    }
}
