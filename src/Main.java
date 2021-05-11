import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Board board;
    public static void main(String[] args){
    readFile();

    }

    static stateNode goal, start;


    private static void readFile() {
        String algoType;
        int rowLen, colLen;
        boolean timeInstruction, openInstruction;
        Scanner file = null;
        System.out.println(new File("."));
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        //      ==== OPEN FILE ====
        try{
            file = new Scanner(new File("./src/data/input2.txt"));
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
        start = readState(file, rowLen, colLen);

        file.nextLine();
        goal  = readState(file, rowLen, colLen);

        file.close();

        //      ==== SET BOARD ====
        board = new Board(timeInstruction, openInstruction, rowLen, colLen, start, goal);


        //      ==== RUN ALGORITHM ====
        runAlgo(algoType, board);

    }

    private static void runAlgo(String algo, Board board){
//        BFS, DFID, A*, IDA*, DFBnB
        searchAlgorithm puzzleSolver = new searchAlgorithm(board);

        switch (algo) {
            case "BFS" -> puzzleSolver.BFS();
            case "DFID" -> puzzleSolver.DFID();
            case "A*" -> puzzleSolver.AStar();
            case "IDA*" -> puzzleSolver.IDAStar();
            case "DFBnB" -> puzzleSolver.DFBnB();
            default -> throw new IllegalArgumentException("Wrong algorithm instruction format - \"" + algo + "\".");
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
