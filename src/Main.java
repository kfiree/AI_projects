import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        readFile();
    }

    private static void readFile() {
        String algoType;
        int rowLen, colLen;;
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
        algoType = file.nextLine(); // types := BFS, DFID, A*, IDA*, DFBnB
        timeInstruction =  readTime(file.nextLine()); // print file
        openInstruction = readOpen(file.nextLine()); // print openList
        String[] dimensions = file.nextLine().split("x"); //
        rowLen = Integer.parseInt(dimensions[0]);
        colLen = Integer.parseInt(dimensions[1]);

        //       ==== READ START AND GOAL NODE ====
        stateNode start = readNode(file, rowLen, colLen);
        file.nextLine();
        stateNode goal  = readNode(file, rowLen, colLen);

        file.close();

        //      ==== SET BOARD ====
        Board board = new Board(timeInstruction, openInstruction, rowLen, colLen, start, goal);

        //      ==== RUN ALGORITHM ====
        runAlgo(algoType, board);

    }

    private static void runAlgo(String algo, Board board){
//        BFS, DFID, A*, IDA*, DFBnB
        searchAlgorithm puzzleSolver = new searchAlgorithm(board);

        switch(algo)
        {
            case "BFS":
                puzzleSolver.BFS();
                break;
            case "DFID":
                puzzleSolver.DFID();
                break;
            case "A*":
                puzzleSolver.AStar();
                break;
            case "IDA*":
                puzzleSolver.IDAStar();
                break;
            case "DFBnB":
                puzzleSolver.DFBnB();
                break;
            default:
                throw new IllegalArgumentException("Wrong algorithm instruction format - \""+algo+"\".");
        }
    }

    private static stateNode readNode(Scanner file, int rowLen, int colLen) {
        int[][] tiles = new int[rowLen][colLen];

        for (int i = 0; i < rowLen; i++) {
            String[] numbers = file.nextLine().split(",");

            for (int j = 0; j < colLen; j++) {
                tiles[i][j] = readTile(numbers[j]);
            }
        }

        return new stateNode(tiles, null);
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
            return Integer.valueOf(tileStr);
        } catch (Exception  e) { throw new IllegalArgumentException("Wrong tile format - \""+ tileStr +"\"."); }
    }

}
