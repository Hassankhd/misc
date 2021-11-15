
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.ArrayList;

/*
 * This file reads the given CSV
 */
public class CSVReader {

private String filePath;
protected ArrayList<ArrayList<Double>> x;
protected ArrayList<Integer> y;

public CSVReader(String filePath) {
    this.filePath = filePath;
    this.y = new ArrayList<Integer>();
    this.x = new ArrayList<ArrayList<Double>>(20000);
    for (int i = 0; i < 20000; i++) {
        x.add(new ArrayList<>());
    }
}

public ArrayList<ArrayList<Double>> getx() {
    return this.x;
}

public ArrayList<Integer> gety() {
    return this.y;
}

public boolean loadData() {
    try {
        BufferedReader bufferedReader = 
                        new BufferedReader(new FileReader(filePath));
        bufferedReader.readLine();
        String eachLine = "";
        int i = 0;
        
        // read each line of the data file
        while ((eachLine = bufferedReader.readLine()) != null) {
            // split the data and set each field
            String[] line = eachLine.split(",");
            if (Double.parseDouble(line[0]) == 8) {
                y.add(0);
                for(int j = 1; j < line.length; j++) {
                    int parsedLine = Integer.parseInt(line[j]);
                    x.get(i).add(parsedLine/255.0);
                }
                i++;
            }
            else if (Double.parseDouble(line[0]) == 9) {
                y.add(1);
                for(int j = 1; j < line.length; j++) {
                    double parsedLine = (Double.parseDouble(line[j]))/255.0;
                    x.get(i).add(parsedLine);
                }
                i++;
            }
        }
    }
    catch (Exception e) {
        // if any exception, print the trace
        e.printStackTrace();
        return false;
    }
    return true;
  }

public void prepareAllData() {
    this.loadData();
}


}
