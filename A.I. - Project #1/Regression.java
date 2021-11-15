import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class Regression {
    private static final String COMMA_DELIMITER = ",";
    private static final String TRAIN_CSV_FILE = "mnist_train.csv";
    private static final String TEST_CSV_FILE = "mnist_test.csv";
    private static final String NEW_TEST = "test.txt";
    private static final int max_num_iterations = 20;
    static double learning_rate = 0.01;

    public static List<List<Double>> parseRecords(String filePath) throws FileNotFoundException, IOException {
        // List to be returned containing all the labels and their corresponding features
        List<List<Double>> records = new ArrayList<>();
        
        // read every line of file to populate records 2D List
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                
                // string array of current line of values being read
                String[] currLine = line.split(COMMA_DELIMITER);
                
                // double array containing the labels of values we're interested in along with the value's features
                Double[] label_features = new Double[currLine.length]; 
                
                // if current line read has its first read value equal to 8 or 9, we assign a label (no label otherwise)
                if (currLine[0].equals("8")) 
                    label_features[0] = 0.0; // if the value is 8, the label value is 0
                else if (currLine[0].equals("9")) 
                    label_features[0] = 1.0; // if the value is 9, the label value is 1
                else continue; // value isn't 8 or 9 so we continue iterating the file
                
                // if value is 8 or 9, we compute its feature values
                for (int i = 1; i < currLine.length; i++) {
                   Double currFeatureVal = Double.parseDouble(currLine[i]);
                    label_features[i] = currFeatureVal / 255.0;
                }
                
                // add newly read array of one label with its features to the records list
                records.add(Arrays.asList(label_features));
            }
        } catch (Exception e) {
            // if any exception, print the trace
            e.printStackTrace();
            return null;
        }
        return records;
    }

    public static List<List<Double>> parseNewRecords(String filePath) throws FileNotFoundException, IOException {
        // List to be returned containing all the labels and their corresponding features
        List<List<Double>> records = new ArrayList<>();
        
        // read every line of file to populate records 2D List
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                
                // string array of current line of values being read
                String[] currLine = line.split(COMMA_DELIMITER);
                
                // double array containing the labels of values we're interested in along with the value's features
                Double[] label_features = new Double[currLine.length]; 
                
                // compute the feature values on every line
                for (int i = 0; i < currLine.length; i++) {
                   Double currFeatureVal = Double.parseDouble(currLine[i]);
                    label_features[i] = currFeatureVal / 255.0;
                }
                // add newly read array of one label with its features to the records list
                records.add(Arrays.asList(label_features));
            }
        } catch (Exception e) {
            // if any exception, print the trace
            e.printStackTrace();
            return null;
        }
        return records;
    }



    public static void main(String[] args) throws IOException {
        // Read and parse csv files we're about to perform regressions on
        List<List<Double>> records = parseRecords(TRAIN_CSV_FILE);
        List<List<Double>> test_records = parseRecords(TEST_CSV_FILE);
        List<List<Double>> new_test = parseNewRecords(NEW_TEST);

        // create bias variable as well as array of weights, initialized with random double values
        Random rand = new Random();
        Double bias = rand.nextDouble();
        Double[] weights = new Double[784];
        for (int i = 0; i < weights.length; i++) weights[i] = rand.nextDouble();
        
        // create cost variables that represent cost of current read value and that of the previous value
        Double prevCost = 0.0;
        Double currCost = 0.0;

        for (int iteration = 0; ; iteration++) {
            // array of the values of the activation function at every feature xi for a certain label y
            Double[] activationVals = new Double[records.size()];
            for (int i = 0; i < records.size(); i++) {
                double sum_wx = 0; // used to calculate the summation part of the activation function
                for (int j = 0; j < weights.length; j++) {
                    sum_wx += weights[j] * records.get(i).get(j+1);
                }
                activationVals[i] = 1.0 / (1 + Math.exp(-1 * (sum_wx + bias))); // value from activation function for a certain xi
            }

            // Update weights
            for (int j = 0; j < weights.length; j++) {
                double w_temp = 0;
                for (int i = 0; i < records.size(); i++) {
                    w_temp += (activationVals[i] - records.get(i).get(0)) * records.get(i).get(j+1);
                }
                weights[j] = weights[j] - learning_rate * w_temp; // update weights
            }

            // Update bias
            double b_temp = 0;
            for (int i = 0; i < records.size(); i++) {
                b_temp += (activationVals[i] - records.get(i).get(0));
            }
            bias = bias - learning_rate * b_temp;

            // Calculate cost function
            prevCost = currCost;
            currCost = 0.0;
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i).get(0) == 0.0) {
                    if (activationVals[i] > 0.9999) currCost += 100.0; // something large
                    else currCost -= Math.log(1 - activationVals[i]);
                }
                else if (records.get(i).get(0) == 1.0) {
                    if (activationVals[i] < 0.0001) currCost += 100.0;
                    else currCost -= Math.log(activationVals[i]);
                }
            }

            
            
            // Check for convergence
            if (Math.abs(currCost - prevCost) < 0.0001) break;
            else if (iteration > max_num_iterations) { // termination condition
                System.out.println("Reached the maximum number of iterations. "
                        + "Maybe try a different learning rate?");
                break;
            }
            System.out.println("weights and bias from values in mnist_train.csv file: ");
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);
            for (Number n : weights) {
                Double d = n.doubleValue();
                System.out.print(df.format(d)+ ", ");
            }
            System.out.println(df.format(bias));
            double correct = 0.0;
            for(int ind = 0; ind < records.size(); ind ++){
                if ((records.get(ind).get(0) == 1.0 && activationVals[ind] >=0.5) || (records.get(ind).get(0) == 0.0 && activationVals[ind] < 0.5) )
                    correct += 1.0;
            }
            double acc = correct / records.size();
            System.out.println("iteration: " + iteration + ", acc: " + acc);
        }

        // everything under here uses data from the test.txt file
        
        // array of the values of the activation function at every feature xi 
        
        
        
            Double[] activationValsTest = new Double[new_test.size()];
            for (int i = 0; i < new_test.size(); i++) {
                double sum_wx = 0; // used to calculate the summation part of the activation function
                for (int j = 0; j < weights.length; j++) {
                    sum_wx += weights[j] * new_test.get(i).get(j);
                }
                activationValsTest[i] = 1.0 / (1 + Math.exp(-1 * (sum_wx + bias))); // value from activation function for a certain xi
            }

            System.out.println("activation values from data in test.txt file: ");
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            for (Number n : activationValsTest) {
                Double d = n.doubleValue();
                System.out.print(df.format(d)+ ", ");
            }

    }
}