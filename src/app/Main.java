package app;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

//enum ALGO  {BFS,
//        DFID,
//        AStar,
//        IDAStar,
//        DFBnB}

public class Main {
    static Board _board;
    static long _duration;
    static searchAlgorithm _puzzleSolver;
    static stateNode _goal, _start;

    public static void main(String[] args){
    readFile();

    }


    private static void readFile() {
        String algoType;
        int rowLen, colLen;
        boolean timeInstruction, openInstruction;
        Scanner file = null;

        //      ==== OPEN FILE ====
        try{
            file = new Scanner(new File("./src/input.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            e.printStackTrace();
        }

        //      ==== GET PUZZLE INSTRUCTION ====
        assert file != null;
        algoType = file.nextLine(); // types := BFS, DFID, A*, IDA*, DFBnB
        timeInstruction =  readTime(file.nextLine()); // print file
        openInstruction = readOpen(file.nextLine()); // print openList
        String[] dimensions = file.nextLine().split("x"); //
        rowLen = Integer.parseInt(dimensions[0]);
        colLen = Integer.parseInt(dimensions[1]);

        //       ==== READ START AND GOAL NODE ====
        _start = readState(file, rowLen, colLen);

        file.nextLine();
        _goal  = readState(file, rowLen, colLen);

        file.close();

        //      ==== SET BOARD ====
        _board = new Board(timeInstruction, openInstruction, rowLen, colLen, _start, _goal);


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
            File outPut = new File("output.txt");
            FileWriter myWriter = new FileWriter(outPut);
            StringBuilder sb = new StringBuilder();

            for(stateNode state: path){
                sb.append(state.getLastOperation() + '-');
            }
            sb.setLength(sb.length() - 1);

            sb.append('\n');
            sb.append("Num: ");
            sb.append(algo.getNodesNumber());

            sb.append('\n');
            sb.append("Cost: ");
            sb.append(path.get(0).getCost());

            if(_board.showTime() ){
                sb.append('\n');
                double timeInSeconds = _duration / 1000.0;
                sb.append(timeInSeconds);
                sb.append(" seconds");
            }

            System.out.println(sb);
            System.out.println(path.get(0).getId());

            myWriter.write(sb.toString());
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    private static stateNode readState(Scanner file, int rowLen, int colLen) {
        int[][] tiles = new int[rowLen][colLen];

        for (int i = 0; i < rowLen; i++) {
            String[] numbers = file.nextLine().split(",");

            for (int j = 0; j < colLen; j++) {
                int tile = readTile(numbers[j]);
                tiles[i][j] = tile;
            }
        }

        return new stateNode(tiles);
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

    private static int readTile(String tileStr) {
        if(tileStr.equals("_"))
            return -1;

        try{
            return Integer.parseInt(tileStr);
        } catch (Exception  e) { throw new IllegalArgumentException("Wrong tile format - \""+ tileStr +"\"."); }
    }

}
