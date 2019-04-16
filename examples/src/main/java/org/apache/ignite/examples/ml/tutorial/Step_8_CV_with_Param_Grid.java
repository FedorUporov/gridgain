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

package org.apache.ignite.examples.ml.tutorial;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.ml.dataset.feature.extractor.impl.FeatureLabelExtractorWrapper;
import org.apache.ignite.ml.math.functions.IgniteBiFunction;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.preprocessing.encoding.EncoderTrainer;
import org.apache.ignite.ml.preprocessing.encoding.EncoderType;
import org.apache.ignite.ml.preprocessing.imputing.ImputerTrainer;
import org.apache.ignite.ml.preprocessing.minmaxscaling.MinMaxScalerTrainer;
import org.apache.ignite.ml.preprocessing.normalization.NormalizationTrainer;
import org.apache.ignite.ml.selection.cv.CrossValidation;
import org.apache.ignite.ml.selection.cv.CrossValidationResult;
import org.apache.ignite.ml.selection.paramgrid.ParamGrid;
import org.apache.ignite.ml.selection.scoring.evaluator.Evaluator;
import org.apache.ignite.ml.selection.scoring.metric.classification.Accuracy;
import org.apache.ignite.ml.selection.split.TrainTestDatasetSplitter;
import org.apache.ignite.ml.selection.split.TrainTestSplit;
import org.apache.ignite.ml.tree.DecisionTreeClassificationTrainer;
import org.apache.ignite.ml.tree.DecisionTreeNode;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * To choose the best hyperparameters the cross-validation with {@link ParamGrid} will be used in this example.
 * <p>
 * Code in this example launches Ignite grid and fills the cache with test data (based on Titanic passengers data).</p>
 * <p>
 * After that it defines how to split the data to train and test sets and configures preprocessors that extract
 * features from an upstream data and perform other desired changes over the extracted data.</p>
 * <p>
 * Then, it tunes hyperparams with K-fold Cross-Validation on the split training set and trains the model based on
 * the processed data using decision tree classification and the obtained hyperparams.</p>
 * <p>
 * Finally, this example uses {@link Evaluator} functionality to compute metrics from predictions.</p>
 * <p>
 * The purpose of cross-validation is model checking, not model building.</p>
 * <p>
 * We train {@code k} different models.</p>
 * <p>
 * They differ in that {@code 1/(k-1)}th of the training data is exchanged against other cases.</p>
 * <p>
 * These models are sometimes called surrogate models because the (average) performance measured for these models
 * is taken as a surrogate of the performance of the model trained on all cases.</p>
 * <p>
 * All scenarios are described there: https://sebastianraschka.com/faq/docs/evaluate-a-model.html</p>
 */
public class Step_8_CV_with_Param_Grid {
    /** Run example. */
    public static void main(String[] args) {
        System.out.println();
        System.out.println(">>> Tutorial step 8 (cross-validation with param grid) example started.");

        try (Ignite ignite = Ignition.start("examples/config/example-ignite.xml")) {
            try {
                IgniteCache<Integer, Object[]> dataCache = TitanicUtils.readPassengers(ignite);

                // Defines first preprocessor that extracts features from an upstream data.
                // Extracts "pclass", "sibsp", "parch", "sex", "embarked", "age", "fare" .
                IgniteBiFunction<Integer, Object[], Object[]> featureExtractor
                    = (k, v) -> new Object[]{v[0], v[3], v[4], v[5], v[6], v[8], v[10]};

                IgniteBiFunction<Integer, Object[], Double> lbExtractor = (k, v) -> (double) v[1];

                TrainTestSplit<Integer, Object[]> split = new TrainTestDatasetSplitter<Integer, Object[]>()
                    .split(0.75);

                IgniteBiFunction<Integer, Object[], Vector> strEncoderPreprocessor = new EncoderTrainer<Integer, Object[]>()
                    .withEncoderType(EncoderType.STRING_ENCODER)
                    .withEncodedFeature(1)
                    .withEncodedFeature(6) // <--- Changed index here.
                    .fit(ignite,
                        dataCache,
                        featureExtractor
                    );

                IgniteBiFunction<Integer, Object[], Vector> imputingPreprocessor = new ImputerTrainer<Integer, Object[]>()
                    .fit(ignite,
                        dataCache,
                        strEncoderPreprocessor
                    );

                IgniteBiFunction<Integer, Object[], Vector> minMaxScalerPreprocessor = new MinMaxScalerTrainer<Integer, Object[]>()
                    .fit(
                        ignite,
                        dataCache,
                        imputingPreprocessor
                    );

                IgniteBiFunction<Integer, Object[], Vector> normalizationPreprocessor = new NormalizationTrainer<Integer, Object[]>()
                    .withP(2)
                    .fit(
                        ignite,
                        dataCache,
                        minMaxScalerPreprocessor
                    );

                // Tune hyperparams with K-fold Cross-Validation on the split training set.

                DecisionTreeClassificationTrainer trainerCV = new DecisionTreeClassificationTrainer();

                CrossValidation<DecisionTreeNode, Double, Integer, Object[]> scoreCalculator
                    = new CrossValidation<>();

                ParamGrid paramGrid = new ParamGrid()
                    .addHyperParam("maxDeep", new Double[]{1.0, 2.0, 3.0, 4.0, 5.0, 10.0, 10.0})
                    .addHyperParam("minImpurityDecrease", new Double[]{0.0, 0.25, 0.5});

                CrossValidationResult crossValidationRes = scoreCalculator.score(
                    trainerCV,
                    new Accuracy<>(),
                    ignite,
                    dataCache,
                    split.getTrainFilter(),
                    normalizationPreprocessor,
                    lbExtractor,
                    3,
                    paramGrid
                );

                System.out.println("Train with maxDeep: " + crossValidationRes.getBest("maxDeep")
                    + " and minImpurityDecrease: " + crossValidationRes.getBest("minImpurityDecrease"));

                DecisionTreeClassificationTrainer trainer = new DecisionTreeClassificationTrainer()
                    .withMaxDeep(crossValidationRes.getBest("maxDeep"))
                    .withMinImpurityDecrease(crossValidationRes.getBest("minImpurityDecrease"));

                System.out.println(crossValidationRes);

                System.out.println("Best score: " + Arrays.toString(crossValidationRes.getBestScore()));
                System.out.println("Best hyper params: " + crossValidationRes.getBestHyperParams());
                System.out.println("Best average score: " + crossValidationRes.getBestAvgScore());

                crossValidationRes.getScoringBoard().forEach((hyperParams, score)
                    -> System.out.println("Score " + Arrays.toString(score) + " for hyper params " + hyperParams));

                // Train decision tree model.
                DecisionTreeNode bestMdl = trainer.fit(
                    ignite,
                    dataCache,
                    split.getTrainFilter(),
                    FeatureLabelExtractorWrapper.wrap(normalizationPreprocessor, lbExtractor) //TODO: IGNITE-11581
                );

                System.out.println("\n>>> Trained model: " + bestMdl);

                double accuracy = Evaluator.evaluate(
                    dataCache,
                    split.getTestFilter(),
                    bestMdl,
                    normalizationPreprocessor,
                    lbExtractor,
                    new Accuracy<>()
                );

                System.out.println("\n>>> Accuracy " + accuracy);
                System.out.println("\n>>> Test Error " + (1 - accuracy));

                System.out.println(">>> Tutorial step 8 (cross-validation with param grid) example started.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
