import java.awt.*;
import java.util.HashMap;

public class Board {

    stateNode goal;
    stateNode curr;
    boolean time, open;
    int rowLen, colLen;
    HashMap<Integer, Point> goalMap = new HashMap<>();

    /**
         ___________________________________
        |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
        |\/\/\/\/  FORMAT EXAMPLE /\/\/\/\/|
        |\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/|
        ------------------------------------
                BFS
                with time
                no open
                3x4
                1  2  3  4
                5  6  11 7
                9  10 8  _
                Goal state:
                1  2  3  4
                5  6  7  8
                9  10 11 _
     */
    public Board(boolean time, boolean open, int rowLen, int colLen, stateNode start, stateNode goal) {
        this.goal= goal;
        this.curr = start;
        this.time = time;
        this.open = open;
        this.rowLen = rowLen;
        this.colLen = colLen;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setTime(boolean time) {
        this.time = time;
    }

    public void setCurr(stateNode curr) {
        this.curr = curr;
    }

    public void setGoal(stateNode goal) {
        this.goal = goal;
    }

    public Point tileLocation(int tileNum){
        return goalMap.get(tileNum);
    }

    public int width() {
        return colLen;
    }

    public int height() {
        return rowLen;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isTime() {
        return time;
    }

    public stateNode getCurr() {
        return curr;
    }

    public stateNode getGoal() {
        return goal;
    }
}
