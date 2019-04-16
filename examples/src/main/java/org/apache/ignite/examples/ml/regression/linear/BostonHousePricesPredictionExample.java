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

package org.apache.ignite.examples.ml.regression.linear;

import java.io.FileNotFoundException;
import java.util.function.BiFunction;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.ml.dataset.feature.extractor.Vectorizer;
import org.apache.ignite.ml.dataset.feature.extractor.impl.DummyVectorizer;
import org.apache.ignite.ml.environment.LearningEnvironmentBuilder;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.regressions.linear.LinearRegressionLSQRTrainer;
import org.apache.ignite.ml.regressions.linear.LinearRegressionModel;
import org.apache.ignite.ml.selection.scoring.evaluator.Evaluator;
import org.apache.ignite.ml.selection.scoring.metric.regression.RegressionMetricValues;
import org.apache.ignite.ml.selection.scoring.metric.regression.RegressionMetrics;
import org.apache.ignite.ml.selection.split.TrainTestDatasetSplitter;
import org.apache.ignite.ml.selection.split.TrainTestSplit;
import org.apache.ignite.ml.trainers.DatasetTrainer;
import org.apache.ignite.ml.util.MLSandboxDatasets;
import org.apache.ignite.ml.util.SandboxMLCache;

/**
 * Example of using Linear Regression model in Apache Ignite for house prices prediction.
 *
 * Description of model can be found in: https://en.wikipedia.org/wiki/Linear_regression .
 * Original dataset can be downloaded from: https://archive.ics.uci.edu/ml/machine-learning-databases/housing/ .
 * Copy of dataset are stored in: modules/ml/src/main/resources/datasets/boston_housing_dataset.txt .
 * Score for regression estimation: R^2 (coefficient of determination).
 * Description of score evaluation can be found in: https://stattrek.com/statistics/dictionary.aspx?definition=coefficient_of_determination .
 */
public class BostonHousePricesPredictionExample {
    /** Runs example. */
    public static void main(String[] args) throws FileNotFoundException {
        try (Ignite ignite = Ignition.start("examples/config/example-ignite.xml")) {
            System.out.println(">>> Ignite grid started.");

            IgniteCache<Integer, Vector> dataCache = null;
            try {
                System.out.println(">>> Fill dataset cache.");
                dataCache = new SandboxMLCache(ignite).fillCacheWith(MLSandboxDatasets.BOSTON_HOUSE_PRICES);
                DatasetTrainer<LinearRegressionModel, Double> trainer = new LinearRegressionLSQRTrainer()
                    .withEnvironmentBuilder(LearningEnvironmentBuilder.defaultBuilder().withRNGSeed(0));

                // This vectorizer works with values in cache of Vector class.
                Vectorizer<Integer, Vector, Integer, Double> vectorizer = new DummyVectorizer<Integer>()
                    .labeled(Vectorizer.LabelCoordinate.FIRST); // FIRST means "label are stored at first coordinate of vector"

                // Splits dataset to train and test samples with 80/20 proportion.
                TrainTestSplit<Integer, Vector> split = new TrainTestDatasetSplitter<Integer, Vector>().split(0.8);

                System.out.println(">>> Start traininig.");
                LinearRegressionModel model = trainer.fit(
                    ignite, dataCache,
                    split.getTrainFilter(),
                    vectorizer
                );

                System.out.println(">>> Perform scoring.");
                double score = Evaluator.evaluate(
                    dataCache, split.getTestFilter(),
                    model, vectorizer,
                    new RegressionMetrics().withMetric(RegressionMetricValues::r2)
                );

                System.out.println(">>> Model: " + toString(model));
                System.out.println(">>> R^2 score: " + score);
            } finally {
                dataCache.destroy();
            }
        }
    }

    /**
     * Prepare pretty string for model.
     * @param model Model.
     * @return String representation of model.
     */
    private static String toString(LinearRegressionModel model) {
        BiFunction<Integer, Double, String> formatter = (idx, val) -> String.format("%.2f*f%d", val, idx);

        Vector weights = model.getWeights();
        StringBuilder sb = new StringBuilder(formatter.apply(0, weights.get(0)));

        for (int fid = 1; fid < weights.size(); fid++) {
            double w = weights.get(fid);
            sb.append(" ").append(w > 0 ? "+" : "-").append(" ")
                .append(formatter.apply(fid, Math.abs(w)));
        }

        double intercept = model.getIntercept();
        sb.append(" ").append(intercept > 0 ? "+" : "-").append(" ")
            .append(String.format("%.2f", Math.abs(intercept)));
        return sb.toString();
    }
}
