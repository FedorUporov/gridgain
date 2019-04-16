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

package org.apache.ignite.ml.genetic;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.ml.genetic.parameter.GAGridConstants;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.LoggerResource;

/**
 * Responsible for performing Roulette Wheel selection
 */
public class RouletteWheelSelectionJob extends ComputeJobAdapter {
    /** Ignite instance */
    @IgniteInstanceResource
    private Ignite ignite = null;

    /** Ignite logger */
    @LoggerResource
    private IgniteLogger log = null;

    /** Total Fitness score */
    Double totalFitnessScore = null;

    /** Chromosome key/fitness score pair */
    LinkedHashMap<Long, Double> map = null;

    /**
     * @param totalFitnessScore Total fitness score
     * @param map Chromosome key / fitness score map
     */
    public RouletteWheelSelectionJob(Double totalFitnessScore, LinkedHashMap<Long, Double> map) {
        this.totalFitnessScore = totalFitnessScore;
        this.map = map;
    }

    /**
     * Perform Roulette Wheel selection
     *
     * @return Chromosome parent chosen after 'spinning' the wheel.
     */
    @Override public Chromosome execute() throws IgniteException {

        IgniteCache<Long, Chromosome> populationCache = ignite.cache(GAGridConstants.POPULATION_CACHE);

        int value = spintheWheel(this.totalFitnessScore);

        double partialSum = 0;
        boolean notFound = true;

        //sort map in ascending order by fitness score
        Map<Long, Double> sortedAscendingMap = map.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        Iterator<Entry<Long, Double>> entries = sortedAscendingMap.entrySet().iterator();

        Long chromosomeKey = (long)-1;

        while (entries.hasNext() && notFound) {
            Entry<Long, Double> entry = entries.next();
            Long key = entry.getKey();
            Double fitnessScore = entry.getValue();
            partialSum = partialSum + fitnessScore;

            if (partialSum >= value) {
                notFound = false;
                chromosomeKey = key;
            }
        }

        return populationCache.get(chromosomeKey);
    }

    /**
     * Spin the wheel.
     *
     * @param fitnessScore Size of Gene pool
     * @return value
     */
    private int spintheWheel(Double fitnessScore) {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(fitnessScore.intValue());
    }

}
