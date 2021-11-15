import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class P2 {

    // This code uses all 9 features to train the decision tree
    public static int numAttr = 9;

    public static void main(String[] args) {
        
        List<ArrayList<Integer>> trainData = cleanAndTransformData("breast-cancer-wisconsin.txt");
        
        int numTwos = 0; // # of benign
        
        int numFours = 0; // # of malignant
        
        // count number of 2s and 4s in the trainData 2D list
        for(int i = 0; i < trainData.size(); i++) {
            if (trainData.get(i).get(9) == 2) numTwos++;
            else numFours++;
        }
        
        
        System.out.println("# benign: " + numTwos + ", # malignant: " + numFours);
        
        DecTreeNode root = buildTree(trainData);
        
       double infoGain = informationGain(trainData, 1, 2);
       System.out.println(infoGain);
        
       List<ArrayList<Integer>> testData = cleanAndTransformData("test.txt");
       
       // final product
       int[] results = new int[200];
       for(int i = 0; i < testData.size(); i++) {
           if (testData.get(i).get(1) <= 2) {
               if (testData.get(i).get(2) <= 2) {
                   if (testData.get(i).get(4) <= 2) {
                    if (testData.get(i).get(6) <= 3) {
                     if (testData.get(i).get(7) <= 2) {
                      if (testData.get(i).get(8) <= 1) {
                          System.out.print("2");
                          results[i] = 2;
                      }
                      else {
                          System.out.print("4");
                          results[i] = 4;
                      }
                     } else {
                         System.out.print("4");
                         results[i] = 4;
                     }
                    } else {
                        System.out.print("4");
                        results[i] = 4;
                    }
                   } else {
                       System.out.print("4");
                       results[i] = 4;
                   }
               } else { 
                   System.out.print("4");
                   results[i] = 4;
               }
           } else {
               System.out.print("4");
               results[i] = 4;
           }
           System.out.print(", ");
       }
    }
    
    /*
     * This method cleans the train dataset, removes rows with missing values, 
     * and ignores first id column, since that's not used during training. 
     */
    public static List<ArrayList<Integer>> cleanAndTransformData(String file){
        
        List<ArrayList<Integer>> cleanData = new ArrayList<ArrayList<Integer>>();
        
        try {
            Scanner scan = new Scanner(new File(file));
            
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                // if the line doesn't contain '?', we should include it into our clean dataset
                if (line.indexOf('?') == -1) {
                    String[] split = line.split(",");
                    
                    ArrayList<Integer> instance = new ArrayList<Integer>(); 
                    // ignore id column, it's not used for training
                    // convert string values to integer values
                    for (int i = 1; i < split.length; i++) {              
                        instance.add(Integer.parseInt(split[i]));
                    }
                    cleanData.add(instance);
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("File with the name " + file + " cannot be read!");
        }
        return cleanData;
    }
    
    /*
     * Method for computing entropy. 
     */
    public static double entropy(double p0){ 
        if (p0 == 0 || p0 == 1) return 0;
        
        double p1 = 1 - p0;
        return -(p0*Math.log(p0)/Math.log(2) + p1*Math.log(p1)/Math.log(2));
    }
    
    /*
     * Method for computing information gain. 
     */
    public static double informationGain(List<ArrayList<Integer>> dataSet, int feature, int threshold){
        
        int dataSize = dataSet.size();
        // first computing H(Y)
        int count = 0;
        for (List<Integer> data: dataSet) {
            if (data.get(data.size()-1) == 2) { // if label equals 2, last column is label
                count++;
            }
        }
        double Hy = entropy((double) count / dataSize);
        
        // computing H(Y|X), conditional entropy
        double Hyx = 0;
        int countLess = 0;
        int countGreater = 0;
        int countLessAndPositive = 0;
        int countGreaterAndPositive = 0;
        for (List<Integer> data: dataSet){
            if (data.get(feature) <= threshold) {
                countLess++;
                if (data.get(data.size()-1) == 2) countLessAndPositive++; // last column is label
            } else {
                countGreater++;
                if (data.get(data.size()-1) == 2) countGreaterAndPositive++;
            }
        }
        double prob1 = (double)countLess/dataSize;
        double prob2 = (double)countGreater/dataSize;
        if (prob1>0){
            Hyx = Hyx + prob1 * entropy(((double)countLessAndPositive)/countLess);
        }
        if(prob2>0){
            Hyx = Hyx + prob2 * entropy(((double)countGreaterAndPositive)/countGreater);
        }
        // return difference between entropy and conditional entropy
        return Hy - Hyx;
    }
    
    /*
     * Method for building a decision tree using the given dataset. 
     * It returns a pointer to the root node. 
     */
    private static DecTreeNode buildTree(List<ArrayList<Integer>> dataSet)
    {
        int numData = dataSet.size();
        int bestAttr = -1;
        int bestThres = Integer.MIN_VALUE;
        double bestScore = Double.NEGATIVE_INFINITY;
        boolean leaf = false;
        DecTreeNode node = null;
        
        // if node isn't leaf node, compute the best split for it 
        if (!leaf) {
            for (int j = 0; j < numAttr; j++) {
                for (int i=1; i<11; i++){
                    double score = informationGain(dataSet, j, i); 
                    if (score > bestScore) {
                        bestScore = score;
                        bestAttr = j;
                        bestThres = i; 
                    }
                }
            }
            if (bestScore == 0) {
                leaf = true;
            }
            
            // split the entire # of instances into two groups based on the threshold value (<= and >)
            List<ArrayList<Integer>> leftList = new ArrayList<ArrayList<Integer>>();
            List<ArrayList<Integer>> rightList = new ArrayList<ArrayList<Integer>>();
            for (ArrayList<Integer> data : dataSet) {
                if (data.get(bestAttr) <= bestThres) {
                    leftList.add(data);
                }
                else {
                    rightList.add(data);
                }
            }
            
            if (leftList.size() == 0 || rightList.size() == 0) {
                leaf = true;
            }
            // if node is not leaf, create left and right children
            if (!leaf) {
                node = new DecTreeNode(-1, bestAttr, bestThres);
                node.left = buildTree(leftList);
                node.right = buildTree(rightList);
            }
        }
        // if node is leaf, need to count # of instances with labels 2 and 4
        if (leaf) {
            int count = 0;
            for(List<Integer> data: dataSet) {
                if (data.get(data.size()-1) == 2)
                count += 1;
            }
            
            if (count >= numData - count) {
                node = new DecTreeNode(2, -1, -1); // assign label 2 to the leaf node
            }
            else {
                node = new DecTreeNode(4, -1, -1); // assign label 4 to the leaf node
            }
        }
        return node;
    }
}

/*
 * This class represents a node in a decision tree. 
 * 
 * It has fields for:
 * - feature - the feature on which split at this node happens
 * - threshold - the value of the threshold for the feature chosen
 * - left - pointer to the left child node
 * - right - pointer to the right child node 
 * - classLabel - the value 2 or 4 which represents label assigned 
 *          to instances at this node if this is a leaf node
 */

class DecTreeNode {
    
    public int feature;
    public int threshold;
    public DecTreeNode left = null;
    public DecTreeNode right = null; 
    public int classLabel; 
    
    public DecTreeNode(int classLabel, int feature, int threshold) {
        
        this.classLabel = classLabel;
        this.feature = feature;
        this.threshold = threshold;
        
    }
    
    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }
}
