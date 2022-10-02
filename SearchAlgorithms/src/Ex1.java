import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

//enum ALGO  {BFS,
//        DFID,
//        AStar,
//        IDAStar,
//        DFBnB}

public class Ex1 {
    static Board _board;
    static long _duration;
    static searchAlgorithm _puzzleSolver;
    static stateNode _goal, _start;
    static int rowLen, colLen, startEdges, goalEdges;
    static boolean reverse=false;

    public static void main(String[] args){
    readFile();

    }


    private static void readFile() {
        String algoType;
        boolean timeInstruction, openInstruction;
        Scanner file = null;

        //      ==== OPEN FILE ====
        try{
            file = new Scanner(new File("input.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            e.printStackTrace();
        }

        //      ==== GET PUZZLE INSTRUCTION ====
        assert file != null;
        algoType = file.nextLine(); // types := BFS, DFID, A*, IDA*, DFBnB
        timeInstruction =  readTime(file.nextLine()); // print file
        openInstruction = readOpen(file.nextLine()); // print openList
        String[] dimensions = file.nextLine().split("x");
        rowLen = Integer.parseInt(dimensions[0]);
        colLen = Integer.parseInt(dimensions[1]);

        //       ==== READ START AND GOAL NODE ====
        int[][] startTiles = readTiles(file, rowLen, colLen, true);
        file.nextLine();
        int[][] goalTiles = readTiles(file, rowLen, colLen, false);
        file.close();


        reverse = goalEdges > startEdges ? true:false;
        _start = new stateNode(startTiles, reverse);
        _goal = new stateNode(goalTiles, reverse);

        //      ==== SET BOARD ====
        _board = new Board(timeInstruction, openInstruction, rowLen, colLen, _start, _goal, reverse);
        
        //      ==== RUN ALGORITHM ====
        runAlgo(algoType, _board);

    }

    private static void runAlgo(String algo, Board board){
        // BFS, DFID, A*, IDA*, DFBnB
        _puzzleSolver = new searchAlgorithm(board);
        java.util.List<stateNode> path;
        long startTime = System.currentTimeMillis();
        switch (algo) {
            case "BFS" ->  path = _puzzleSolver.BFS();
            case "DFID" -> path = _puzzleSolver.DFID();
            case "A*" -> path = _puzzleSolver.AStar();
            case "IDA*" -> path = _puzzleSolver.IDAStar();
            case "DFBnB" -> path = _puzzleSolver.DFBnB();

            default -> throw new IllegalArgumentException("Wrong algorithm instruction format - \"" + algo + "\".");
        }
        _duration = (System.currentTimeMillis() - startTime);
        pathHandler(path, _puzzleSolver);
    }

    private static void pathHandler(java.util.List<stateNode> path, searchAlgorithm algo){
        try {
            String fileName = "output.txt";
            File outPut = new File(fileName);
            FileWriter myWriter = new FileWriter(outPut, true);
            StringBuilder sb = new StringBuilder();

//            if(!_board.isReverse())
//            Collections.reverse(path);

            if(path == null){
                sb.append("No path\nNum:");
                myWriter.write(sb.toString());
                myWriter.close();
            }
            for(stateNode state: path){
                sb.append(state.getLastOperation()+'-');
            }
            sb.setLength(sb.length() - 1);

            sb.append('\n');
            sb.append("Num: ");
            sb.append(path.get(0).stateNodeNumber());

            sb.append('\n');
            sb.append("Cost: ");
            sb.append(Math.max(path.get(0).getCost(),path.get(path.size()-1).getCost()));

            if(_board.showTime() ){
                sb.append('\n');
                double timeInSeconds = _duration / 1000.0;
                sb.append(timeInSeconds);
                sb.append(" seconds");
            }

            myWriter.write(sb.toString());
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    private static int[][] readTiles(Scanner file, int rowLen, int colLen, boolean isFirst) {
        int[][] tiles = new int[rowLen][colLen];

        for (int i = 0; i < rowLen; i++) {
            String[] numbers = file.nextLine().split(",");

            for (int j = 0; j < colLen; j++) {
                int tile = readTile(numbers[j]);
                
                
                if (tile == -1 && isFirst)
                    startEdges += countEdges(i, j, rowLen, colLen);
                else if(tile == -1)
                    goalEdges += countEdges(i, j, rowLen, colLen);
                
                tiles[i][j] = tile;
            }
        }

        return tiles;
    }

    private static boolean readTime(String Instruction) {
        if(Instruction.equals("with time")){
            return true;
        }else if(Instruction.equals("no time")){
            return false;
        }
        throw new IllegalArgumentException("Wrong time instruction format - \""+Instruction+"\".");
    }

    private static boolean readOpen(String Instruction) {
        if(Instruction.equals("with open")){
            return true;
        }else if(Instruction.equals("no open")){
            return false;
        }
        throw new IllegalArgumentException("Wrong open instruction format - \""+Instruction+"\".");
    }

    private static int countEdges(int x, int y, int row, int col){
        int counter = 0;
        if(x == row-1)
            counter++;
        if(y == col-1)
            counter++;
        if(x == 0)
            counter++;
        if(y == 0)
            counter++;
        
        return counter;
    }
    
    private static String getReversedOperation(stateNode state){
        String operation = state.getLastOperation();
        String reversedOpp;
        reversedOpp = operation.replace('U', 'D');
        if(reversedOpp.equals(operation))
            reversedOpp = operation.replace('R', 'L');
        if(reversedOpp.equals(operation))
            reversedOpp = operation.replace('D', 'U');
        if(reversedOpp.equals(operation))
            reversedOpp = operation.replace('L', 'R');
        return reversedOpp;
    }

    private static int readTile(String tileStr) {
        if(tileStr.equals("_"))
            return -1;

        try{
            return Integer.parseInt(tileStr);
        } catch (Exception  e) { throw new IllegalArgumentException("Wrong tile format - \""+ tileStr +"\"."); }
    }

}
