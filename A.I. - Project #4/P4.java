import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * 
 * This program classifies US States according to their covid-19 deaths time series data through clustering.
 * Methods of clustering are:
 * - Hierarchical Clustering with single-linkage and Euclidean distance
 * - Hierarchical Clustering with complete-linkage and Euclidean distance
 * - K-Means Clustering with Euclidean distance
 *
 */
public class P4 {

    private static final int K = 8;               // number of clusters to generate
    private static final int FEATURE_LENGTH = 5;  // number of parameters used in the algorithms

    public static void main(String[] args) {

        Map<String, Double[]> USData = preprocessData("time_series_covid19_deaths_US.txt");

        Set<String> states = USData.keySet();
        List<String> sortedListStates = new ArrayList<>(states);
        Collections.sort(sortedListStates);
        
        List<Double[]> state_data = new ArrayList<Double[]>();
        for (String state: sortedListStates){
            state_data.add(USData.get(state));
        }

        // Hierarchical Clustering with single-linkage and Euclidean distance
        List<List<String>> clusters = new ArrayList<List<String>>();

        // each country appears as a separate cluster at the beginning
        for (int i=0; i<sortedListStates.size(); i++){
            List<String> cluster = new ArrayList<String>();
            cluster.add(sortedListStates.get(i));
            clusters.add(cluster);
        }

        while (clusters.size() != K ){

            double minimumDistance = Double.MAX_VALUE;
            List<String> clusterToMerge1 = null, clusterToMerge2 = null;
            int indexCluster1 = -1, indexCluster2 = -1;

            for (int i=0; i<clusters.size(); i++){
                List<String> cluster1 = clusters.get(i);
                for (int j=i+1; j<clusters.size(); j++){ // make sure it's not the same cluster though, otherwise dist is 0.

                    List<String> cluster2 = clusters.get(j);
                    // compute single linkage distance between given two clusters
                    double currDist = singleLinkageDistance(USData, cluster1, cluster2); 

                    if (currDist < minimumDistance) {
                        minimumDistance = currDist;
                        clusterToMerge1 = cluster1;
                        clusterToMerge2 = cluster2; 
                        indexCluster1 = i;
                        indexCluster2 = j;
                    }
                }
            }

            // need to merge those two clusters with min distance
            if (clusterToMerge1!=null && clusterToMerge2!=null){

                clusters.remove(indexCluster2);
                clusters.remove(indexCluster1);

                clusterToMerge1.addAll(clusterToMerge2);
                clusters.add(clusterToMerge1);
            } else{
                System.out.println("Something is incorrect, one cluster might be null.");
            }
        }

        // Print out final clusters
        System.out.println("Hierarchical Clustering (Single-Linkage) Results: ");
        int cluster_num = 0;
        HashMap<String, Integer> clusterHier = new HashMap<String, Integer>();
        for (List<String> l : clusters){
            System.out.println(cluster_num + " " + l);

            for (int i=0; i<l.size(); i++){
                String state = l.get(i);
                clusterHier.put(state, cluster_num);
            }
            cluster_num++;
        }
        System.out.println("=============================================================");
        
        
     // Hierarchical Clustering with complete-linkage and Euclidean distance
        List<List<String>> clusters2 = new ArrayList<List<String>>();

        // each country appears as a separate cluster at the beginning
        for (int i=0; i<sortedListStates.size(); i++){
            List<String> cluster = new ArrayList<String>();
            cluster.add(sortedListStates.get(i));
            clusters2.add(cluster);
        }

        while (clusters2.size() != K ){

            double minimumDistance = Double.MAX_VALUE;
            List<String> clusterToMerge1 = null, clusterToMerge2 = null;
            int indexCluster1 = -1, indexCluster2 = -1;

            for (int i=0; i<clusters2.size(); i++){
                List<String> cluster1 = clusters2.get(i);
                for (int j=i+1; j<clusters2.size(); j++){ // make sure it's not the same cluster though, otherwise dist is 0.

                    List<String> cluster2 = clusters2.get(j);
                    // compute complete linkage distance between given two clusters
                    double currDist = completeLinkageDistance(USData, cluster1, cluster2); 

                    if (currDist < minimumDistance) {
                        minimumDistance = currDist;
                        clusterToMerge1 = cluster1;
                        clusterToMerge2 = cluster2; 
                        indexCluster1 = i;
                        indexCluster2 = j;
                    }
                }
            }

            // need to merge those two clusters with min distance
            if (clusterToMerge1!=null && clusterToMerge2!=null){

                clusters2.remove(indexCluster2);
                clusters2.remove(indexCluster1);

                clusterToMerge1.addAll(clusterToMerge2);
                clusters2.add(clusterToMerge1);
            } else{
                System.out.println("Something is incorrect, one cluster might be null.");
            }
        }

        // Print out final clusters
        System.out.println("Hierarchical Clustering (Complete-Linkage) Results: ");
        int cluster_num2 = 0;
        HashMap<String, Integer> clusterHier2 = new HashMap<String, Integer>();
        for (List<String> l : clusters2){
            System.out.println(cluster_num2 + " " + l);

            for (int i=0; i<l.size(); i++){
                String state = l.get(i);
                clusterHier2.put(state, cluster_num2);
            }
            cluster_num2++;
        }
        System.out.println("=============================================================");
        

        int[] cluster_info = new int[sortedListStates.size()]; // cluster to which state is assigned to

        // K-Means Clustering with Euclidean distance

        // K vectors of length FEATURE_LENGTH, those will hold our cluster centers
        List<Double[]> means = new ArrayList<Double[]>(); 
        // Choose K random countries as our initial means for cluster centers
        for (int i=0; i < K; i++){
            Random r = new Random();
            int rand = r.nextInt(sortedListStates.size());
            Double[] vector = state_data.get(rand); 
            means.add(vector);
        }

        // find initial cluster assignment for all countries
        findClusterForStates(state_data, means, cluster_info);

        // start updating means for clusters until clusters do not change
        boolean keepUpdatingMeans = true;

        List<Double[]> previous_iteration_means = means;

        while (keepUpdatingMeans){

            List<Double[]> recomputed_means = recomputeMeans(state_data, cluster_info);

            // need to make sure that all K means are not changing for stopping learning algorithm
            int checker = 0;
            for(int cl_index=0; cl_index<K; cl_index++){

                if (Arrays.equals(recomputed_means.get(cl_index), previous_iteration_means.get(cl_index))){
                    checker+=1;
                }
            }
            if (checker == K){
                // all K vectors are still the same after last iteration of updates to the means
                // therefore stop the algorithm
                keepUpdatingMeans = false;
                DecimalFormat df = new DecimalFormat("#.####");
                df.setRoundingMode(RoundingMode.HALF_UP);
                System.out.println("\nObtained K-means cluster Centers:\n");
                for(int clusterIndex = 0; clusterIndex < K; clusterIndex++) {
                    for (int i = 0; i < 5; i++) 
                        System.out.print(df.format(recomputed_means.get(clusterIndex)[i]) + ", ");
                    System.out.print("\n");
                }
                System.out.print("\n\n");
                System.out.print("Total distortion of the clustering: ");
                double sumAll = 0.0;
                double euclidDist = 0.0;
                for(int clusterIndex = 0; clusterIndex < K; clusterIndex++) {
                    for (int j=0; j<sortedListStates.size(); j++){
                        if (cluster_info[j] == clusterIndex + 1) {
                            euclidDist = 0.0;
                            for (int i = 0; i < 5; i++)
                                euclidDist += (USData.get(sortedListStates.get(j))[i] - recomputed_means.get(clusterIndex)[i]) * (USData.get(sortedListStates.get(j))[i] - recomputed_means.get(clusterIndex)[i]);
                            euclidDist = Math.sqrt(euclidDist);
                            sumAll += euclidDist * euclidDist;
                        }
                    }
                 }
                System.out.print(sumAll + "\n\n");
                break;
            }
            findClusterForStates(state_data, recomputed_means, cluster_info);
            previous_iteration_means = recomputed_means;
        }

        // Print out list of countries in each cluster for K-Means
        System.out.println("K-Means Clustering Results: ");
        for (int i=1; i<K+1; i++){
            System.out.print(i-1 + " [");
            StringBuilder sb = new StringBuilder();
            for (int j=0; j<sortedListStates.size(); j++){
                if (cluster_info[j] == i){
                    sb.append(sortedListStates.get(j) + ", ");
                }
            }
            if (sb.length() != 0)System.out.print(sb.toString().substring(0,sb.length()-2));
            else System.out.print(sb.toString());
            System.out.print("]");
            System.out.println();
        }

    }

    public static Map<String, Double[]> preprocessData(String file) {

        Map<String, Integer> numOfOccurrences = new HashMap<String,Integer>();
        Map<String,Double[]> USData = new HashMap<String, Double[]>();
        Map<String,Double[]> USExtraData = new HashMap<String, Double[]>();

        try {
            Scanner sc = new Scanner(new File(file));
            String firstLineColumnNames = sc.nextLine();
            int totalNumColumns = firstLineColumnNames.split(",").length;
            System.out.println(totalNumColumns);
            System.out.println(totalNumColumns-13);
            int[] cts = new int[totalNumColumns];
            for (int i = 0; i < cts.length; i++) {
                cts[i] = 0;
            }
            
            while (sc.hasNext()){
                String[] line = sc.nextLine().split(",");
                Double[] stateInfo = new Double[FEATURE_LENGTH];
                Double[] extraNeededStateInfo = new Double[559]; // extraNeededStateInfo[0] stores population, the rest are cumulative # deaths
                // get needed state info to calculate parameters
                for (int i = 13; i < totalNumColumns; i++) {
                    extraNeededStateInfo[i-13] = Double.valueOf(line[i]);
                }
                
                // parameters will be initialized with 0.0 because they will be calculated later
                stateInfo[0] = 0.0; //mean
                stateInfo[1] = 0.0; // standard deviation
                stateInfo[2] = 0.0; // time it took for deaths to increase by a factor of 5 by using the last day as the final value
                stateInfo[3] = 0.0; // maximum number of deaths in one day
                stateInfo[4] = 0.0; // % deaths with respect to state's population
                
                if (USData.containsKey(line[6])){
                    // get previous values
                    Double[] prevExtraNeeded = USExtraData.get(line[6]);
                    
                    // sum population and daily cumulative death for the same state
                    for (int j=0; j < prevExtraNeeded.length; j++){
                        extraNeededStateInfo[j] = extraNeededStateInfo[j] + prevExtraNeeded[j];
                    }
                    
                    numOfOccurrences.put(line[6], numOfOccurrences.get(line[6]) + 1); // increase counter
                    USData.put(line[6], stateInfo);
                    USExtraData.put(line[6], extraNeededStateInfo);

                } else {
                    USData.put(line[6], stateInfo);
                    USExtraData.put(line[6], extraNeededStateInfo);
                    numOfOccurrences.put(line[6], 1);
                }
            }
            
            // calculate parameters on state level, especially those with multiple occurrences
           for (String state: numOfOccurrences.keySet()){

                int count = numOfOccurrences.get(state);
                if (count >= 1){
                    Double[] list = USData.get(state);
                    Double[] extraDataList = USExtraData.get(state);
                    
                 // re-scaling data with respect to population
                    for (int i = 1; i < extraDataList.length; i++) {
                        extraDataList[i] = (extraDataList[i] / extraDataList[0]) * 1000000;
                    }
                    
                    list[0] = (extraDataList[extraDataList.length - 1] / 558); //calculating daily average of state
                    
                    double sum = 0;
                    double sd = 0; // variable to hold value of standard deviation
                    double maxVal = 0; // variable to hold maximum number of deaths in one day
                    for (int i = 1; i < extraDataList.length; i++) {
                     // sum to calculate the standard deviation
                        sum += (extraDataList[i] - list[0]) * (extraDataList[i] - list[0]);
                        if(i == 1) 
                            continue;
                        else if(extraDataList[i]-extraDataList[i-1] > maxVal) 
                            // assign maxVal to the new highest number of daily deaths recorded
                            maxVal = extraDataList[i]-extraDataList[i-1];
                    }
                    // calculate the standard deviation
                    sd = Math.sqrt(sum / (totalNumColumns - 14));
                    
                    // calculate time it took for deaths to go from n/10 to n
                    double tenHundredParam1 = 0; // variable to hold time it took for deaths to go from n/10 to n by using the last day as the final value
                    for (int i = extraDataList.length - 1; i > 0; i--) {
                        if(extraDataList[i] > extraDataList[extraDataList.length - 1] / 10) tenHundredParam1++;
                        else {
                            tenHundredParam1++;
                            break;
                        }
                    }
                    
                    // calculate time it took for deaths to go from n/100 to n/10
                    double tenHundredParam2 = 0; // variable to hold time it took for deaths to go from n/100 to n/10 by using the last day as the final value
                    for (int i = extraDataList.length - 1; i > 0; i--) {
                        if(extraDataList[i] > extraDataList[extraDataList.length - 1] / 10) continue;
                        if(extraDataList[i] > extraDataList[extraDataList.length - 1] / 100) tenHundredParam2++;
                        else {
                            tenHundredParam2++;
                            break;
                        }
                    }
                    
                    // parameters
                    list[1] = sd; // save standard deviation in state info
                    list[2] = tenHundredParam1; // save time it took for deaths to go from n/10 to n in state info
                    list[3] = maxVal; // save max one-day death toll in state info
                    list[4] = tenHundredParam2; //  save time it took for deaths to go from n/100 to n/10 in state info
                    
                    USData.put(state, list);
                } 
            }
            // re-scale by putting numbers in same [0,10] range
            double max1 = -Double.MAX_VALUE, min1 = Double.MAX_VALUE, max2 = -Double.MAX_VALUE, min2 = Double.MAX_VALUE, max3 = -Double.MAX_VALUE, min3 = Double.MAX_VALUE, max4 = -Double.MAX_VALUE, min4 = Double.MAX_VALUE, max5 = -Double.MAX_VALUE, min5 = Double.MAX_VALUE;
            for (String state: numOfOccurrences.keySet()) {
                
                Double[] list = USData.get(state);
                
                if(list[0] > max1) max1 = list[0];
                if(list[0] < min1) min1 = list[0];
                if(list[1] > max2) max2 = list[1];
                if(list[1] < min2) min2 = list[1];
                if(list[2] > max3) max3 = list[2];
                if(list[2] < min3) min3 = list[2];
                if(list[3] > max4) max4 = list[3];
                if(list[3] < min4) min4 = list[3];
                if(list[4] > max5) max5 = list[4];
                if(list[4] < min5) min5 = list[4];
            }
            
            for (String state: numOfOccurrences.keySet()) {
                
                Double[] list = USData.get(state);
                list[0] = ((list[0] - min1)/ (max1 - min1)) * 10;
                list[1] = ((list[1] - min2)/ (max2 - min2)) * 10;
                list[2] = ((list[2] - min3)/ (max3 - min3)) * 10;
                list[3] = ((list[3] - min4)/ (max4 - min4)) * 10;
                list[4] = ((list[4] - min5)/ (max5 - min5)) * 10;
                USData.put(state, list);
            }
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            for (String state: numOfOccurrences.keySet()) {
                Double[] list = USData.get(state);
                System.out.print(state + ", " + df.format(list[0]) + ", " + df.format(list[1]) + ", " + df.format(list[2]) + ", " + df.format(list[3]) + ", " + df.format(list[4]) + "\n");
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("The input file cannot be found! ");
        }

        return USData;
    }

    /*
     * This method computes single-linkage distance between two clusters. 
     * Makes use of Euclidean distance. 
     */
    public static double singleLinkageDistance( Map<String, Double[]> USData, List<String> cluster1, List<String> cluster2){

        double minDistance = Double.MAX_VALUE;
        double distance; 
        for (int i=0; i<cluster1.size(); i++){
            for (int j=0; j<cluster2.size(); j++){

                distance = 0;

                Double[] values1 = USData.get(cluster1.get(i));
                Double[] values2 = USData.get(cluster2.get(j));

                for (int m=0; m < values1.length; m++){
                    distance += (values1[m] - values2[m]) * (values1[m] - values2[m]);
                }
                distance = Math.sqrt(distance);
                
                if (distance < minDistance) minDistance = distance;
            }
        }
        return minDistance;
    }

    
    /*
     * This method computes complete-linkage distance between two clusters. 
     * Makes use of Euclidean distance. 
     */
    public static double completeLinkageDistance( Map<String, Double[]> USData, List<String> cluster1, List<String> cluster2){

        double maxDistance = -Double.MAX_VALUE;
        double distance; 
        for (int i=0; i<cluster1.size(); i++){
            for (int j=0; j<cluster2.size(); j++){

                distance = 0;

                Double[] values1 = USData.get(cluster1.get(i));
                Double[] values2 = USData.get(cluster2.get(j));

                for (int m=0; m < values1.length; m++){
                    distance += (values1[m] - values2[m]) * (values1[m] - values2[m]);
                }
                distance = Math.sqrt(distance);
                
                if (distance > maxDistance) maxDistance = distance;
            }
        }
        return maxDistance;
    }
    
    /*
     * This method given input on parameters for each country, and information about 
     * means for the clusters, assigns each country to the cluster and updates the 
     * array holding information about the assignment. 
     */
    private static void findClusterForStates(List<Double[]> data, List<Double[]> means, int[] cluster_info) {

        for (int i=0; i<data.size(); i++){
            Double[] countryInfo = data.get(i);

            int min_cluster = 0;
            double min_distance = Double.MAX_VALUE;

            for (int k = 0; k<K; k++){

                Double[] mean_vector = means.get(k);
                
                // Euclidean distance 
                double distance = 0;
                for (int j=0; j < countryInfo.length; j++){
                    distance += (countryInfo[j] - mean_vector[j]) * (countryInfo[j] - mean_vector[j]); 
                }
                distance = Math.sqrt(distance);
                
                if (distance<min_distance){
                    min_cluster = k+1;
                    min_distance = distance;
                }
            }
            cluster_info[i] = min_cluster;
        }
    }
    
    /*
     * This method given all countries' information and a new assignment of countries to the clusters, 
     * recomputes the means for clusters by finding the average.
     */
    private static List<Double[]> recomputeMeans(List<Double[]> countries, int[] cluster_info){
        
        List<Double[]> means = new ArrayList<Double[]>();
        
        // consider each cluster
        for (int i=0; i < K; i++){

            int cluster_size = 0;
            Double[] cluster_mean = new Double[FEATURE_LENGTH];
            
            // initialize values to 0, to avoid null pointer
            for (int k=0;k<cluster_mean.length; k++){
                cluster_mean[k] = 0.0;
            }

            for (int j=0; j< cluster_info.length; j++){

                if (cluster_info[j] == i+1){

                    cluster_size += 1;

                    Double[] countryInfo = countries.get(j);

                    // sum values for all countries in the cluster
                    for (int index = 0; index<countryInfo.length; index++){
                        cluster_mean[index] += countryInfo[index];
                    }
                }
            }

            // average all countries in the cluster 
            for (int index = 0; index<cluster_mean.length; index++){
                cluster_mean[index] = cluster_mean[index]/(double)cluster_size;
            }
            means.add(cluster_mean);
        }
        return means;
    }
}
