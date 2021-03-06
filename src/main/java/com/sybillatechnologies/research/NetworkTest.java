package com.sybillatechnologies.research;

import org.ejml.simple.SimpleMatrix;

import java.io.*;

/**
 * Created by Dawid Pazik on 2014-11-11.
 */
public class NetworkTest
{
    private static String outputFilePath;

    public static void main(String[] args)
    {
        if(args.length != 1)
        {
            System.out.println("Invalid number of parameters. Please specify output file path");
            System.exit(1);
        }

        try
        {
            outputFilePath = args[0];

            String path = "UCI HAR Dataset/";
            SimpleMatrix trainingX = Utils.loadFile(path + "train/X_train.txt"), trainingY = Utils.loadFile(path + "train/y_train.txt");
            SimpleMatrix testX = Utils.loadFile(path + "test/X_test.txt"), testY = Utils.loadFile(path + "test/y_test.txt");

            int numberOfLabels = 6;
            double defaultLambda = 0;
            int defaultNumberOfIterations = 100;
            int defaultHiddenLayerSize = 25;

            double[] testedLambdas = {0, 0.1, 0.2, 0.3};
            int[] testedNumbersOfIterations = {50, 100, 150, 200};
            int[] testedHiddenLayerSizes = {15, 20, 25, 30};

            for(double lambda : testedLambdas)
            {
                testConfiguration(trainingX, trainingY, testX, testY, numberOfLabels, lambda, defaultNumberOfIterations, defaultHiddenLayerSize);
            }

            for(int numberOfIterations : testedNumbersOfIterations)
            {
                testConfiguration(trainingX, trainingY, testX, testY, numberOfLabels, defaultLambda, numberOfIterations, defaultHiddenLayerSize);
            }

            for(int hiddenLayerSize : testedHiddenLayerSizes)
            {
                testConfiguration(trainingX, trainingY, testX, testY, numberOfLabels, defaultLambda, defaultNumberOfIterations, hiddenLayerSize);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void testConfiguration(SimpleMatrix trainingX, SimpleMatrix trainingY, SimpleMatrix testX, SimpleMatrix testY, int numberOfLabels, double lambda, int numberOfIterations, int hiddenLayerSize)
            throws IOException
    {
        int inputLayerSize = trainingX.numCols();

        NeuralNetwork network = new NeuralNetwork(inputLayerSize, hiddenLayerSize, numberOfLabels);
        network.train(trainingX, trainingY, lambda,numberOfIterations);

        SimpleMatrix predictedY = network.predict(testX);
        SimpleMatrix matches = Utils.elementEquals(testY, predictedY);

        PrintWriter fileOutput = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePath, true)));
        fileOutput.println("Number of iterations: " + numberOfIterations);
        fileOutput.println("Size of hidden layer: " + hiddenLayerSize);
        fileOutput.println("Lambda: " + lambda);
        fileOutput.println("Accuracy: " + (matches.elementSum() / matches.getNumElements()));
        fileOutput.println();
        fileOutput.close();
    }
}
