import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 
 * @author hassankheireddine
 *
 */
import java.util.ArrayList;
import java.util.Arrays;

public class Regression {

    // the learning step
    private double learnStep;

    // the regularization parameter
    private double R;

    // the number of epochs
    private final int EPOCHS = 1000;

    // the weights to be learned
    private double[] weights;


    public Regression(int n) {
        this.weights = new double[n];
        this.learnStep = 0.001;
        this.R = 0.001;
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public void setH(double h) {
        this.learnStep = h;
    }

    public void trainData(ArrayList<ArrayList<String>> fixtures) {
        // Epochs
        for (int e = 0; e <= EPOCHS; e++) {

            // initialize the weights with random values between 0.0 and 1.0
            for (int i = 0; i < this.weights.length; i++)
                this.weights[i] = Math.random();

            // the log likelihood
            double llh = 0;
            for (int i = 0; i < fixtures.size(); i++) {
                ArrayList<String> fixture = fixtures.get(i);
                ArrayList<String> attributes = new ArrayList<String>();
                if (fixtures.get(i).size() == 0) break;
                attributes.add(fixtures.get(i).get(1));
                attributes.add(fixtures.get(i).get(2));
                attributes.add(fixtures.get(i).get(3));
                attributes.add(fixtures.get(i).get(4));
                attributes.add(fixtures.get(i).get(5));
                attributes.add(fixtures.get(i).get(6));
                double output = findProbability(fixture); // the output of the sigmoid funcion
                String winner = fixtures.get(i).get(0); // 1 if home team wins, 0 otherwise
                double c;
                if (winner.equals("1")) {
                    c = 1;
                } else {
                    c = 0;
                }
                for (int j = 0; j < this.weights.length; j++) {
                    if (j >= fixture.size())
                        break;
                    this.weights[j] = (1 - 2 * this.R * this.learnStep) * this.weights[j]
                        + this.learnStep * (c - output) * Double.parseDouble(fixture.get(j));
                }
                llh += getLogLikelihood(c, fixture);
            }
            System.out.println("Epoch " + e + " weights " + Arrays.toString(this.weights)
                + " LogLikelihood " + llh);
        }
    }


    // Calculate the sum of w*x for each weight and attribute
    // call the sigmoid function with that s
    public double findProbability(ArrayList<String> fixture) {
        double s = 0;
        for (int i = 0; i < this.weights.length; i++) {
            if (i >= fixture.size())
                break;
            s += this.weights[i] * Double.parseDouble(fixture.get(i));
        }
        return sigmoid(s);
    }

    // Sigmoid with overflow check
    private double sigmoid(double s) {
        if (s > 20) {
            s = 20;
        } else if (s < -20) {
            s = -20;
        }
        double exp = Math.exp(s);
        return exp / (1 + exp);
    }

    // Calculate log likelihood on given data
    private double getLogLikelihood(double cat, ArrayList<String> fixture) {
        return cat * Math.log(findProbability(fixture)) + (1 - cat) * Math.log(1 - findProbability(fixture));
    }

}
