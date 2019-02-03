package com.example.amine.learn2sign;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVMHelper {

    private double[][] xtrain = new double[100][];
    private double[][] xtest = new double[10][];
    private double[][] ytrain = new double[100][];
    private double[][] ytest = new double[10][];
    private svm_model model;

    public SVMHelper() {
    }

    public SVMHelper(double[][] xtrain, double[][] xtest, double[][] ytrain, double[][] ytest) {
        this.xtrain = xtrain;
        this.xtest = xtest;
        this.ytrain = ytrain;
        this.ytest = ytest;
    }

    public double[][] getXtrain() {
        return xtrain;
    }

    public void setXtrain(double[][] xtrain) {
        this.xtrain = xtrain;
    }

    public double[][] getXtest() {
        return xtest;
    }

    public void setXtest(double[][] xtest) {
        this.xtest = xtest;
    }

    public double[][] getYtrain() {
        return ytrain;
    }

    public void setYtrain(double[][] ytrain) {
        this.ytrain = ytrain;
    }

    public double[][] getYtest() {
        return ytest;
    }

    public void setYtest(double[][] ytest) {
        this.ytest = ytest;
    }

    public void svmTrain() {
        svm_problem prob = new svm_problem();
        int recordCount = xtrain.length;
        int featureCount = xtrain[0].length;
        prob.y = new double[recordCount];
        prob.l = recordCount;
        prob.x = new svm_node[recordCount][featureCount];

        for (int i = 0; i < recordCount; i++) {
            double[] features = xtrain[i];
            prob.x[i] = new svm_node[features.length];
            for (int j = 0; j < features.length; j++) {
                svm_node node = new svm_node();
                node.index = j;
                node.value = features[j];
                prob.x[i][j] = node;
            }
            prob.y[i] = ytrain[i][0];
        }

        svm_parameter param = new svm_parameter();
        param.degree = 1;
        param.C = 3;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
//        param.probability = 1;
//        param.gamma = 0.5;
//        param.nu = 0.5;
//        param.cache_size = 20000;
//        param.eps = 0.001;

        model = svm.svm_train(prob, param);
    }

    public double[] svmPredict() {

        double[] yPred = new double[xtest.length];

        for (int k = 0; k < xtest.length; k++) {

            double[] fVector = xtest[k];

            svm_node[] nodes = new svm_node[fVector.length];
            for (int i = 0; i < fVector.length; i++) {
                svm_node node = new svm_node();
                node.index = i;
                node.value = fVector[i];
                nodes[i] = node;
            }

            int totalClasses = 2;
            int[] labels = new int[totalClasses];
            svm.svm_get_labels(model, labels);

            double[] prob_estimates = new double[totalClasses];
            yPred[k] = svm.svm_predict_probability(model, nodes, prob_estimates);

        }

        return yPred;
    }

}
