import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.lang.Math;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class NeuralNetwork {

    private static final String COMMA_DELIMITER = ",";
    private static final String TEST_FILE = "mnist_test.csv";
    private static final String NEW_TEST = "test.txt";
    private static final int MAX_EPOCHS = 100;
    static Double learning_rate = 0.01;

    static Double[][] weightsInputHidden = new Double[784][28];
    static Double[] weightsHiddenOutput = new Double[28];
    static Double[] biasInputHidden = new Double[28];
    static Double biasHiddenOutput;
    static Double[][] activationValsInputHidden;
    static Double[] activationValsHiddenOutput;
    

    static Random rand = new Random();


    public static double[][] parseRecords(String filePath) throws FileNotFoundException, IOException {
        double[][] records = new double[20000][785];
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;
            int k = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] string_values = line.split(COMMA_DELIMITER);
                if (string_values[0].equals("8")) records[k][0] = 0.0; // label 0
                else if (string_values[0].equals("9")) records[k][0] = 1.0; // label 1
                else continue;
                for (int i = 1; i < string_values.length; i++) {
                    records[k][i] = Double.parseDouble(string_values[i]) / 255.0; // features
                }
                k += 1;
            }

            double[][] res = new double[k][785];
            for (int i= 0; i < k; i ++){
                System.arraycopy(records[i], 0, res[i], 0, 785);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public static double[][] NewTest(String filePath) throws FileNotFoundException, IOException {
        double[][] records = new double[20000][785];
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;
            int k = 0;
            while ((line = bufferedReader.readLine()) != null) {

                String[] string_values = line.split(COMMA_DELIMITER);
                for (int i = 0; i < string_values.length; i++) {
                    records[k][i] = Double.parseDouble(string_values[i]) / 255.0; // features
                }
                k += 1;
            }

            double[][] res = new double[k][785];
            for (int i= 0; i < k; i ++){
                System.arraycopy(records[i], 0, res[i], 0, 785);
            }
            return res;
        } catch (Exception e) {
            // if any exception, print the trace
            e.printStackTrace();
            return null;
        }

    }


    public static double logistic(double x, double b){
        return 1.0 / (1.0 + Math.exp(-1.0 * (x + b)));
    }

    //creds: https://stackoverflow.com/questions/20190110/2d-int-array-shuffle
    public static double[][] shuffle(double[][] a) {
        Random random = new Random();

        for (int i = a.length - 1; i > 0; i--) {
            for (int j = a[i].length - 1; j > 0; j--) {
                int m = random.nextInt(i + 1);
                int n = random.nextInt(j + 1);

                double temp = a[i][j];
                a[i][j] = a[m][n];
                a[m][n] = temp;
            }
        }
        
        return a;
    }
    
    public static void main(String[] args) throws IOException {
        double[][] train = parseRecords(TRAIN_FILE);
        double[][] test = parseRecords(TEST_FILE);
        double[][] new_test = NewTest(NEW_TEST);

        double accBest = 0.0;
        Double[] aHidOutBest = new Double[train.length];
        Double[][] wihBest = new Double[784][28];
        Double[] whoBest = new Double[28];
        Double[] bihBest = new Double[28];
        Double bhoBest = 0.0;
        
        
        int num_train = train.length;
        int num_test = test.length;

        for (int i = 0; i < biasInputHidden.length; i++) biasInputHidden[i] = rand.nextDouble();
        biasHiddenOutput = rand.nextDouble();
        
        for(int i = 0; i < weightsInputHidden.length; i++){
            for (int j = 0; j < weightsInputHidden[0].length; j++){
                weightsInputHidden[i][j] = 2 * rand.nextDouble() - 1;
            }
        }
        
        for(int i = 0; i < weightsHiddenOutput.length; i++){
            weightsHiddenOutput[i] = 2 * rand.nextDouble() - 1;
        }


        for(int epoch = 1; epoch <= MAX_EPOCHS; epoch ++ ){
            activationValsHiddenOutput = new Double[num_train];
            activationValsInputHidden = new Double[num_train][28];
            
            train = shuffle(train);
            train = shuffle(train);
            for(int i = 0; i < num_train; i++){
               // calc activationValsInputHidden[i][j]
                for(int k = 0; k < 784; k++) {
                    double sum_xw = 0.0;
                    for (int j = 0; j < 28; j++) {
                        sum_xw += weightsInputHidden[k][j] * train[i][k+1];
                    }
                    for (int j = 0; j < 28; j++) {
                    activationValsInputHidden[i][j] = 1.0 / (1.0 + Math.exp(-1.0 * (sum_xw + biasInputHidden[j])));
                    }
                }
                
                // calc activationValsHiddenOutput[i]
                double sum_xw = 0.0;
                for(int j = 0; j < 28; ++ j) {
                    sum_xw += activationValsInputHidden[i][j] * weightsHiddenOutput[j];
                }
                activationValsHiddenOutput[i] = 1.0 / (1.0 + Math.exp(-1.0 * (sum_xw + biasHiddenOutput)));
            }
            
         // update weightsInputHidden
            for(int j = 0; j < 784; j++) {
                for(int k = 0; k < 28; k++) {
                    double w_temp = 0.0;
                    for(int i = 0; i < num_train; i++){
                        double a2 = activationValsHiddenOutput[i];
                        double a1 = activationValsInputHidden[i][k];
                        double x = train[i][j+1];
                        double y = train[i][0];
                        double w2 = weightsHiddenOutput[k];
                        w_temp += ((a2 - y) * (a2) * (1 - a2) * w2 * a1 * (1 - a1) * x);
                    }
                        weightsInputHidden[j][k] = weightsInputHidden[j][k] - learning_rate * w_temp;
                }
            }
            
         // update weightsHiddenOutput
            for(int k = 0; k < 28; k++) {
                double w_temp = 0.0;
                    for(int i = 0; i < num_train; i++){
                        double a2 = activationValsHiddenOutput[i];
                        double a1 = activationValsInputHidden[i][k];
                        double y = train[i][0];
                        w_temp += (a2 - y) * (a2) * (1 - a2) * a1;
                    }
                weightsHiddenOutput[k] = weightsHiddenOutput[k] - learning_rate * w_temp;
            }
            
         // update biasInputHidden
            for(int k = 0; k < 28; k++) {
                double b_temp = 0.0;
                    for(int i = 0; i < num_train; i++){
                        double a2 = activationValsHiddenOutput[i];
                        double a1 = activationValsInputHidden[i][k];
                        double y = train[i][0];
                        double w2 = weightsHiddenOutput[k];
                        b_temp += (a2 - y) * (a2) * (1 - a2) * w2 * a1 * (1 - a1);
                    }
                    biasInputHidden[k] = biasInputHidden[k] - learning_rate * b_temp;
            }
            
         // update biasHiddenOutput
            double bho_temp = 0.0;
            for(int i = 0; i < num_train; i++){
                double a2 = activationValsHiddenOutput[i];
                double y = train[i][0];
                bho_temp += (a2 - y) * (a2) * (1 - a2);
            }
            biasHiddenOutput = biasHiddenOutput - learning_rate * bho_temp;
            
            //calc cost
            double cost = 0.0;
            double cost_temp = 0.0;
            for(int i = 0; i < num_train; i++){
                cost_temp += (train[i][0] - activationValsHiddenOutput[i]) * (train[i][0] - activationValsHiddenOutput[i]);
            }
            cost = (1/2) * cost_temp;
            
            //calc error
            double error = 0;
            for(int ind = 0; ind < num_train; ind ++){
                double[] row = train[ind];
                error += -row[0] * Math.log(activationValsHiddenOutput[ind]) - (1-row[0]) * Math.log(1- activationValsHiddenOutput[ind]);
            }

            //correct
            double correct = 0.0;
            for(int ind = 0; ind < num_train; ind ++){
                if ((train[ind][0] == 1.0 && activationValsHiddenOutput[ind] >=0.5) || (train[ind][0] == 0.0 && activationValsHiddenOutput[ind] < 0.5) )
                    correct += 1.0;
            }
            double acc = correct / num_train;
            
            System.out.println("Epoch: " + epoch + ", error: " + error + ", acc: " + acc + ", cost: " + cost);
        }
        
        
        // save weightsInputHidden and biasInputHidden in text file
        try {
            File myObj = new File("weightsInputHidden.txt");
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
            } else {
              System.out.println("File already exists.");
            }
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        
        try {
            FileWriter myWriter = new FileWriter("weightsInputHidden.txt");
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);
            for (int iter = 0; iter < weightsInputHidden.length; iter++) {
                for (Number n : weightsInputHidden[iter]) {
                    Double d = n.doubleValue();
                    myWriter.write(df.format(d));
                    if (n.doubleValue() != weightsInputHidden[iter][weightsInputHidden[iter].length - 1]) {
                        myWriter.write(", ");
                    }
                }
                myWriter.write("\n");
            }
            
            for (Number n : biasInputHidden) {
                Double d = n.doubleValue();
                myWriter.write(df.format(d)+ ", ");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        
     // save weightsHiddenOutput and biasHiddenOutput in text file
        try {
            File myObj = new File("weightsHiddenOutput.txt");
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
            } else {
              System.out.println("File already exists.");
            }
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        
        try {
            FileWriter myWriter = new FileWriter("weightsHiddenOutput.txt");
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);
                for (Number n : weightsHiddenOutput) {
                    Double d = n.doubleValue();
                    myWriter.write(df.format(d));
                    myWriter.write(", ");
            }
            
            Double d = biasHiddenOutput;
            myWriter.write(df.format(d));
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        
        
            int num_newtest = new_test.length;
            
            Double[] activationValsHiddenOutputTest = new Double[num_newtest];
            Double[][] activationValsInputHiddenTest = new Double[num_newtest][28];

            for(int i = 0; i < 200; i++){

                // calc activationValsInputHidden[i][j]
                for(int j = 0; j < 28; j++) {
                    double sum_xw = 0.0;
                    for (int k = 0; k < 784; k++) {
                        sum_xw += weightsInputHidden[k][j] * test[i][k];
                    }
                    activationValsInputHiddenTest[i][j] = 1.0 / (1.0 + Math.exp(-1.0 * (sum_xw + biasInputHidden[j])));
                }

                // calc activationValsHiddenOutput[i]
                double sum_xw = 0.0;
                for(int j = 0; j < 28; ++ j) {
                    sum_xw += activationValsInputHiddenTest[i][j] * weightsHiddenOutput[j];
                }
                activationValsHiddenOutputTest[i] = 1.0 / (1.0 + Math.exp(-1.0 * (sum_xw + biasHiddenOutput))); 
            }
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
                for (Number n : activationValsHiddenOutputTest) {
                    Double d = n.doubleValue();
                    System.out.print(df.format(d));
                    System.out.print(", ");
                }
  }
}

