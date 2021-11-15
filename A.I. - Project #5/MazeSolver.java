import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/*
 * 
 * This class utilizes BFS, DFS, and A* algorithms to solve the given maze of dimensions 59x59
 *
 */
public class MazeSolver {

    private static final int WIDTH = 59; // intended width of maze
    private static final int HEIGHT = 59; // intended height of maze
    private static final String SVGFILEPATH = "59 by 59 orthogonal maze.svg";

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

        SVGParser svgParser = new SVGParser(SVGFILEPATH, WIDTH, HEIGHT);

        svgParser.parseXML();

        Cell[] startAndFinishCells = svgParser.generateMaze();

        // define start and finish cells
        Cell start = startAndFinishCells[0];
        Cell finish = startAndFinishCells[1];
        
        System.out.println("\nPrinting Successor Matrix:");
        svgParser.printSuccessorMatrix();
        
        System.out.println("\nSolve using BFS Algorithm");
        
        BFS(start, finish);
        
        System.out.println("\nSolve using DFS Algorithm");
        
        DFS(start, finish);

        System.out.println("\nSolve using A* Search Algorithm");

        // call A* search on this maze 
        aStarSearch(start, finish);
        
        System.out.println("\nSolve using A* Search Algorithm with Euclidean distance");

        // call A* search method with Euclidean distance on this maze 
        aStarSearchEuclidean(start, finish);
        
        System.out.print("\nSolution:");
        soln(finish);
    }
    
    /*
     * BFS Algorithm
     * This method returns the number of visited cells. 
     */
    public static int BFS(Cell start, Cell finish){
        
        HashSet<Cell> visited = new HashSet<Cell>();
        LinkedList<Cell> queue = new LinkedList<Cell>(); 
        boolean reachedFinish = false;
        
        int[][] mazeBFSVisited = new int[59][59];
        for (int i = 0; i < 59; i++) {
            for (int j = 0; j < 59; j++) {
                mazeBFSVisited[i][j] = 0;
            }
        }
        
        ArrayList<Cell> orderOfNodesVisited = new ArrayList<>();
        
        queue.add(start); 
        visited.add(start);
        orderOfNodesVisited.add(start);
        
        while(!queue.isEmpty() && !reachedFinish){
            Cell curr = queue.poll();
            orderOfNodesVisited.add(curr);
            
            if (curr.xCoord == finish.xCoord && curr.yCoord == finish.yCoord) reachedFinish=true;
                
            ArrayList<Cell> neighbors = curr.getNeighbors();
            for (Cell neighbor: neighbors){
                if (!visited.contains(neighbor)){
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        System.out.println("Number of Expanded Vertices = " + visited.size());

        for (int i = 0; i < orderOfNodesVisited.size(); i++) {
            System.out.print("(" + orderOfNodesVisited.get(i).yCoord + "," + orderOfNodesVisited.get(i).xCoord + "), ");
            mazeBFSVisited[orderOfNodesVisited.get(i).yCoord][orderOfNodesVisited.get(i).xCoord] = 1;
        }
        System.out.print("\n");
        for (int i = 0; i < 59; i++) {
            for (int j = 0; j < 59; j++) {
                if (j<58) System.out.print(mazeBFSVisited[i][j] + ", ");
                else System.out.print(mazeBFSVisited[i][j]);
            }
            System.out.print("\n");
        }

        return visited.size();
    }
    
    /*
     * DFS Algorithm
     * This method returns the number of visited cells. 
     */
    public static int DFS(Cell start, Cell finish){
        
        HashSet<Cell> visited = new HashSet<Cell>();
        Stack<Cell> stack = new Stack<Cell>(); 
        boolean reachedFinish = false;
        
        int[][] mazeBFSVisited = new int[59][59];
        for (int i = 0; i < 59; i++) {
            for (int j = 0; j < 59; j++) {
                mazeBFSVisited[i][j] = 0;
            }
        }
        
        ArrayList<Cell> orderOfNodesVisited = new ArrayList<>();
        
        stack.add(start); 
        visited.add(start);
        orderOfNodesVisited.add(start);
        
        while(!stack.isEmpty() && !reachedFinish){
            Cell curr = stack.pop();
            orderOfNodesVisited.add(curr);
            
            if (curr.xCoord == finish.xCoord && curr.yCoord == finish.yCoord) reachedFinish=true;
                
            ArrayList<Cell> neighbors = curr.getNeighbors();
            for (Cell neighbor: neighbors){
                if (!visited.contains(neighbor)){
                    visited.add(neighbor);
                    stack.push(neighbor);
                }
            }
        }
        System.out.println("Number of Expanded Vertices = " + visited.size());

        for (int i = 0; i < orderOfNodesVisited.size(); i++) {
            System.out.print("(" + orderOfNodesVisited.get(i).yCoord + "," + orderOfNodesVisited.get(i).xCoord + "), ");
            mazeBFSVisited[orderOfNodesVisited.get(i).yCoord][orderOfNodesVisited.get(i).xCoord] = 1;
        }
        System.out.print("\n");
        for (int i = 0; i < 59; i++) {
            for (int j = 0; j < 59; j++) {
                if (j<58) System.out.print(mazeBFSVisited[i][j] + ", ");
                else System.out.print(mazeBFSVisited[i][j]);
            }
            System.out.print("\n");
        }

        return visited.size();
    }
    
    /*
     * 
     * This method represents A* search algorithm. 
     * It returns the number of total expanded cells for reaching finish cell from start cell.
     * 
     */
    public static int aStarSearch(Cell start, Cell finish){

        HashSet<Cell> visited = new HashSet<Cell>();
        boolean reachedFinish = false;

        int[][] mazeAStarVisited = new int[59][59];
        for (int i = 0; i < 59; i++) {
            for (int j = 0; j < 59; j++) {
                mazeAStarVisited[i][j] = 0;
            }
        }
        
        ArrayList<Cell> orderOfNodesVisited = new ArrayList<>();
        
        // priority queue based on the f value 
        PriorityQueue<Cell> queue = new PriorityQueue<Cell>(WIDTH*HEIGHT, new Comparator<Cell>(){

            public int compare(Cell cell1, Cell cell2){
                if (cell1.f > cell2.f) return 1;
                if (cell1.f < cell2.f) return -1;
                return 0;
            }

        });

        // initialize g cost for the start cell
        start.g = 0;
        queue.add(start);
        orderOfNodesVisited.add(start);

        int counter = 1;
        
        while (!queue.isEmpty() && !reachedFinish){

            // retrieve cell with the lowest f value
            Cell current = queue.poll();

            orderOfNodesVisited.add(current);
            
            // to track cells that were expanded
            visited.add(current);

            // case when finish cell is dequeued
            if (current.xCoord == finish.xCoord && current.yCoord == finish.yCoord){
                reachedFinish = true;
            }

            ArrayList<Cell> neighbors = current.getNeighbors();

            // consider all the neighbors of the current cell
            for (Cell adjacentCell : neighbors){

                // since one step is needed to move from current cell to its neighbor, increment g value
                double g = current.g + 1; 

                // Manhattan distance is used to compute h from adjacent cell to the finish cell
                // f = g + h
                double f = g + Math.abs(adjacentCell.xCoord-finish.xCoord) + 
                        Math.abs(adjacentCell.yCoord-finish.yCoord);
                
                // if this cell was already expanded then do not add to the queue;
                // for our maze examples we do not need to worry about cases when there are 
                // multiple paths to the same cell and we could have several f costs for same cell (no loops)
                if (visited.contains(adjacentCell)){
                    continue;
                } else { 

                    // set f and g values for adjacent cell
                    adjacentCell.g = g;
                    adjacentCell.f = f;

                    // need this for backtracking purposes
                    adjacentCell.parent = current;

                    // add adjacent cell to the queue so that it's expanded later
                    queue.add(adjacentCell);
                }
            }
        }

        System.out.println("Number of Expanded Vertices = " + visited.size());
        
        for (int i = 0; i < orderOfNodesVisited.size(); i++) {
            System.out.print("(" + orderOfNodesVisited.get(i).yCoord + "," + orderOfNodesVisited.get(i).xCoord + "), ");
            mazeAStarVisited[orderOfNodesVisited.get(i).yCoord][orderOfNodesVisited.get(i).xCoord] = 1;
        }
        System.out.print("\n");
        for (int i = 0; i < 59; i++) {
            for (int j = 0; j < 59; j++) {
                if (j<58) System.out.print(mazeAStarVisited[i][j] + ", ");
                else System.out.print(mazeAStarVisited[i][j]);
            }
            System.out.print("\n");
        }
        
        return visited.size();

    }
    
    /*
     * 
     * This method represents A* search algorithm using Euclidean distance. 
     * It returns the number of total expanded cells for reaching finish cell from start cell.
     * 
     */
    public static int aStarSearchEuclidean(Cell start, Cell finish){

        HashSet<Cell> visited = new HashSet<Cell>();
        boolean reachedFinish = false;

        int[][] mazeAStarVisited = new int[59][59];
        for (int i = 0; i < 59; i++) {
            for (int j = 0; j < 59; j++) {
                mazeAStarVisited[i][j] = 0;
            }
        }
        
        ArrayList<Cell> orderOfNodesVisited = new ArrayList<>();
        
        // priority queue based on the f value 
        PriorityQueue<Cell> queue = new PriorityQueue<Cell>(WIDTH*HEIGHT, new Comparator<Cell>(){

            public int compare(Cell cell1, Cell cell2){
                if (cell1.f > cell2.f) return 1;
                if (cell1.f < cell2.f) return -1;
                return 0;
            }

        });

        // initialize g cost for the start cell
        start.g = 0;
        queue.add(start);
        orderOfNodesVisited.add(start);

        while (!queue.isEmpty() && !reachedFinish){

            // retrieve cell with the lowest f value
            Cell current = queue.poll();

            orderOfNodesVisited.add(current);
            
            // to track cells that were expanded
            visited.add(current);

            // case when finish cell is dequeued
            if (current.xCoord == finish.xCoord && current.yCoord == finish.yCoord){
                reachedFinish = true;
            }

            ArrayList<Cell> neighbors = current.getNeighbors();

            // consider all the neighbors of the current cell
            for (Cell adjacentCell : neighbors){

                // since one step is needed to move from current cell to its neighbor, increment g value
                double g = current.g + 1; 

                // Euclidean distance is used to compute h from adjacent cell to the finish cell
                // f = g + h
                double f = g + Math.sqrt(((adjacentCell.xCoord-finish.xCoord) * (adjacentCell.xCoord-finish.xCoord)) + 
                        ((adjacentCell.yCoord-finish.yCoord) * (adjacentCell.yCoord-finish.yCoord)));

                // if this cell was already expanded then do not add to the queue;
                // for our maze examples we do not need to worry about cases when there are 
                // multiple paths to the same cell and we could have several f costs for same cell (no loops)
                if (visited.contains(adjacentCell)){
                    continue;
                } else { 

                    // set f and g values for adjacent cell
                    adjacentCell.g = g;
                    adjacentCell.f = f;

                    // need this for backtracking purposes
                    adjacentCell.parent = current;

                    // add adjacent cell to the queue so that it's expanded later
                    queue.add(adjacentCell);
                }
            }
        }

        System.out.println("Number of Expanded Vertices = " + visited.size());
        
        for (int i = 0; i < orderOfNodesVisited.size(); i++) {
            System.out.print("(" + orderOfNodesVisited.get(i).yCoord + "," + orderOfNodesVisited.get(i).xCoord + "), ");
            mazeAStarVisited[orderOfNodesVisited.get(i).yCoord][orderOfNodesVisited.get(i).xCoord] = 1;
        }
        System.out.print("\n");
        for (int i = 0; i < 59; i++) {
            for (int j = 0; j < 59; j++) {
                if (j<58) System.out.print(mazeAStarVisited[i][j] + ", ");
                else System.out.print(mazeAStarVisited[i][j]);
            }
            System.out.print("\n");
        }
        
        return visited.size();

    }
    
    public static ArrayList<String> soln(Cell finish){
        
        Cell curr = finish;
        Cell prev = finish.parent;
        
        System.out.println();
        ArrayList<String> solutionPath = new ArrayList<String>();
        solutionPath.add(String.valueOf(finish.xCoord + "+" + finish.yCoord));
        
        StringBuilder sb = new StringBuilder();
    
        while(prev!=null){
            if (curr.xCoord < prev.xCoord) sb.append("L");
            if (curr.xCoord > prev.xCoord) sb.append("R");
            if (curr.yCoord > prev.yCoord) sb.append("D");
            if (curr.yCoord < prev.yCoord) sb.append("U");
            curr = prev;
            // add curr to the solution path
            solutionPath.add(String.valueOf(curr.xCoord + "+" + curr.yCoord));
            
            prev = curr.parent;
        }
        System.out.println(sb.reverse().toString());
        
        // reverse the solutionPath list 
        Collections.reverse(solutionPath);
        return solutionPath;
    }
}
