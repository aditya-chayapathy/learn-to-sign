package com.example.amine.learn2sign;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ClassifierService {

    private SVMHelper svmHelper;

    public void initializeClassifier() throws Exception {
        double[][] xTrain = new double[350][];
        double[][] yTrain = {{0}, {0}, {1}, {0}, {0}, {1}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {1}, {1}, {0}, {1}, {1}, {1}, {0}, {0}, {1}, {1}, {0}, {0}, {0}, {1}, {1}, {0}, {0}, {1}, {1}, {0}, {0}, {1}, {0}, {0}, {0}, {0}, {0}, {0}, {1}, {0}, {0}, {0}, {1}, {0}, {1}, {1}, {0}, {1}, {0}, {0}, {0}, {0}, {1}, {1}, {0}, {1}, {1}, {1}, {1}, {1}, {1}, {0}, {0}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {1}, {1}, {0}, {1}, {1}, {1}, {0}, {0}, {0}, {1}, {0}, {0}, {0}, {0}, {0}, {0}, {1}, {1}, {0}, {0}, {1}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {0}, {1}, {0}, {1}, {0}, {1}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {1}, {0}, {0}, {0}, {0}, {1}, {0}, {0}, {0}, {1}, {0}, {0}, {0}, {0}, {0}, {0}, {1}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {1}, {1}, {1}, {0}, {0}, {1}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {1}, {0}, {1}, {1}, {1}, {0}, {0}, {1}, {1}, {1}, {0}, {1}, {0}, {1}, {0}, {0}, {0}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {1}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {0}, {1}, {1}, {1}, {0}, {0}, {1}, {1}, {1}, {1}, {1}, {0}, {1}, {1}, {1}, {0}, {0}, {1}, {0}, {0}, {0}, {0}, {1}, {0}, {0}, {0}, {1}, {0}, {0}, {1}, {0}, {0}, {1}, {0}, {0}, {0}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {0}, {1}, {0}, {1}, {0}, {1}, {0}, {1}, {1}, {0}, {1}, {1}, {0}, {1}, {0}, {1}, {1}, {1}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {1}, {1}, {0}, {1}, {0}, {0}, {0}, {1}, {1}, {1}, {1}, {1}, {0}, {1}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {1}, {1}, {1}, {0}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {0}, {1}, {0}, {0}, {1}, {0}, {1}, {1}, {0}, {1}, {1}, {0}, {1}, {1}, {0}, {0}, {0}, {0}, {0}, {0}, {1}, {0}, {1}, {0}, {0}, {1}, {0}, {1}, {1}, {0}, {1}, {1}, {1}, {1}, {1}, {0}, {1}, {0}, {0}, {0}, {0}, {1}, {1}, {1}, {0}, {0}, {1}, {0}};

        File sdCardRoot = Environment.getExternalStorageDirectory();
        File file = new File(sdCardRoot + "/Learn2Sign/final_feature_matrix.csv");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st = br.readLine();
        int i = 0;
        while ((st = br.readLine()) != null) {
            String[] vals = st.split(",");
            double[] record = new double[22];
            int j = 0;
            for (String val : vals) {
                record[j] = Double.valueOf(val);
                j++;
            }
            xTrain[i] = record;
            i++;
        }

        svmHelper = new SVMHelper();
        svmHelper.setXtrain(xTrain);
        svmHelper.setYtrain(yTrain);
        svmHelper.svmTrain();
    }

    public List<String> getPredictionForFiles(List<String> inputFiles) throws Exception {
        double[][] xTest = new double[inputFiles.size()][];
        double[][] yTest = new double[inputFiles.size()][];
        int[] indexPos = {3, 4, 6, 7, 9, 10, 12, 13, 15, 16, 18, 19, 21, 22, 24, 25, 27, 28, 30, 31, 33, 34};

        int i = 0;
        for (String inputFile : inputFiles) {
            System.out.println(inputFile);
            if (inputFile.toLowerCase().contains("father")) {
                double[] temp = {1};
                yTest[i] = temp;
            } else {
                double[] temp = {0};
                yTest[i] = temp;
            }

            File file = new File(inputFile);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st = br.readLine();

            double[] finalRecord = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            int count = 0;
            while ((st = br.readLine()) != null) {
                String[] vals = st.replace("\"", "").split(",");

                int j = 0;
                for (int l = 0; l < indexPos.length; l++) {
                    finalRecord[j] += Double.valueOf(vals[indexPos[l]]);
                    j++;
                }

                count++;
            }

            for (int k = 0; k < finalRecord.length; k++) {
                finalRecord[k] /= count;
            }

            xTest[i] = finalRecord;
            i++;
        }

        svmHelper.setXtest(xTest);
        svmHelper.setYtest(yTest);
        double[] results = svmHelper.svmPredict();

        List<String> predictions = new ArrayList<String>();
        for (double result : results) {
            if (result == 1.0) {
                predictions.add("FATHER");
            } else {
                predictions.add("ABOUT");
            }
        }

        return predictions;
    }

}

