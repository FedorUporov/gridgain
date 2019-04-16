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

package org.apache.ignite.ml.selection.scoring.metric.classification;

import org.apache.ignite.ml.selection.scoring.metric.MetricValues;

/**
 * Provides access to binary metric values.
 */
public class BinaryClassificationMetricValues implements MetricValues {
    /** True Positive (TP). */
    private double tp;

    /** True Negative (TN). */
    private double tn;

    /** False Positive (FP). */
    private double fp;

    /** False Negative (FN). */
    private double fn;

    /** Sensitivity or True Positive Rate (TPR). */
    private double recall;

    /** Specificity (SPC) or True Negative Rate (TNR). */
    private double specificity;

    /** Precision or Positive Predictive Value (PPV). */
    private double precision;

    /** Negative Predictive Value (NPV). */
    private double npv;

    /** Fall-out or False Positive Rate (FPR). */
    private double fallOut;

    /** False Discovery Rate (FDR). */
    private double fdr;

    /** Miss Rate or False Negative Rate (FNR). */
    private double missRate;

    /** Accuracy. */
    private double accuracy;

    /** Balanced accuracy. */
    private double balancedAccuracy;

    /** F1-Score is the harmonic mean of Precision and Sensitivity. */
    private double f1Score;

    /** ROC AUC. */
    private double rocauc;

    /**
     * Initialize an example by 4 metrics.
     *
     * @param tp True Positive (TP).
     * @param tn True Negative (TN).
     * @param fp False Positive (FP).
     * @param fn False Negative (FN).
     */
    public BinaryClassificationMetricValues(long tp, long tn, long fp, long fn, double rocauc) {
        this.tp = tp;
        this.tn = tn;
        this.fp = fp;
        this.fn = fn;
        this.rocauc = rocauc;

        long p = tp + fn;
        long n = tn + fp;
        long positivePredictions = tp + fp;
        long negativePredictions = tn + fn;

        // according to https://github.com/dice-group/gerbil/wiki/Precision,-Recall-and-F1-measure
        recall = p == 0 ? 1 : (double)tp / p;
        precision = positivePredictions == 0 ? 1 : (double)tp / positivePredictions;
        specificity = n == 0 ? 1 : (double)tn / n;
        npv = negativePredictions == 0 ? 1 : (double)tn / negativePredictions;
        fallOut = n == 0 ? 1 : (double)fp / n;
        fdr = positivePredictions == 0 ? 1 : (double)fp / positivePredictions;
        missRate = p == 0 ? 1 : (double)fn / p;

        f1Score = 2 * (recall * precision) / (recall + precision);

        accuracy = (p + n) == 0 ? 1 : (double)(tp + tn) / (p + n); // multiplication on 1.0 to make double
        balancedAccuracy = p == 0 && n == 0 ? 1 : ((double)tp / p + (double)tn / n) / 2;
    }

    /** */
    public double tp() {
        return tp;
    }

    /** */
    public double tn() {
        return tn;
    }

    /** */
    public double fp() {
        return fp;
    }

    /** */
    public double fn() {
        return fn;
    }

    /** Returns Sensitivity or True Positive Rate (TPR). */
    public double recall() {
        return recall;
    }

    /** Returns Specificity (SPC) or True Negative Rate (TNR). */
    public double specificity() {
        return specificity;
    }

    /** Returns Precision or Positive Predictive Value (PPV). */
    public double precision() {
        return precision;
    }

    /** Returns Negative Predictive Value (NPV). */
    public double npv() {
        return npv;
    }

    /** Returns Fall-out or False Positive Rate (FPR). */
    public double fallOut() {
        return fallOut;
    }

    /** Returns False Discovery Rate (FDR). */
    public double fdr() {
        return fdr;
    }

    /** Returns Miss Rate or False Negative Rate (FNR). */
    public double missRate() {
        return missRate;
    }

    /** Returns Accuracy. */
    public double accuracy() {
        return accuracy;
    }

    /** Returns Balanced accuracy. */
    public double balancedAccuracy() {
        return balancedAccuracy;
    }

    /** Returns F1-Score is the harmonic mean of Precision and Sensitivity. */
    public double f1Score() {
        return f1Score;
    }

    /** Returns ROCAUC value. */
    public double rocauc() {
        return rocauc;
    }

}
