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

package org.apache.ignite.ml.preprocessing.maxabsscaling;

import org.apache.ignite.ml.math.functions.IgniteBiFunction;
import org.apache.ignite.ml.math.primitives.vector.Vector;

/**
 * The preprocessing function that makes maxabsscaling, transforms features to the scale {@code [-1,+1]}. From
 * mathematical point of view it's the following function which is applied to every element in a dataset:
 *
 * {@code a_i = a_i / maxabs_i for all i},
 *
 * where {@code i} is a number of column, {@code maxabs_i} is the value of the absolute maximum element in this column.
 *
 * @param <K> Type of a key in {@code upstream} data.
 * @param <V> Type of a value in {@code upstream} data.
 */
public class MaxAbsScalerPreprocessor<K, V> implements IgniteBiFunction<K, V, Vector> {
    /** */
    private static final long serialVersionUID = 1L;

    /** Maximum absolute values. */
    private final double[] maxAbs;

    /** Base preprocessor. */
    private final IgniteBiFunction<K, V, Vector> basePreprocessor;

    /**
     * Constructs a new instance of maxabsscaling preprocessor.
     *
     * @param maxAbs Maximal absolute values.
     * @param basePreprocessor Base preprocessor.
     */
    public MaxAbsScalerPreprocessor(double[] maxAbs, IgniteBiFunction<K, V, Vector> basePreprocessor) {
        this.maxAbs = maxAbs;
        this.basePreprocessor = basePreprocessor;
    }

    /**
     * Applies this preprocessor.
     *
     * @param k Key.
     * @param v Value.
     * @return Preprocessed row.
     */
    @Override public Vector apply(K k, V v) {
        Vector res = basePreprocessor.apply(k, v);

        assert res.size() == maxAbs.length;

        for (int i = 0; i < res.size(); i++)
            res.set(i, res.get(i) / maxAbs[i]);

        return res;
    }

    /** */
    public double[] getMaxAbs() {
        return maxAbs;
    }
}
