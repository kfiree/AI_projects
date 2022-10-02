import java.awt.*;
import java.util.HashMap;

public class Board {

    stateNode goal;
    stateNode curr;
    boolean time, open, reverse;
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
    public Board(boolean time, boolean open, int rowLen, int colLen, stateNode start, stateNode goal, boolean reverse) {
        this.reverse = reverse;
        if(reverse) {
            this.goal = start;
            this.curr = goal;
        }else {
            this.goal = goal;
            this.curr = start;
        }
        updateGoalMap(this.goal);

        this.time = time;
        this.open = open;
        this.rowLen = rowLen;
        this.colLen = colLen;
    }

    public void updateGoalMap(stateNode goal){
        for (int row = 0; row < goal.rowLen(); row++) {
            for (int col = 0; col < goal.colLen(); col++) {
                int tileNum = goal.getTile(row, col);
                if(tileNum!=-1)
                    goalMap.put(tileNum, new Point(row,col));
            }
        }
    }

    public boolean isReverse() {
        return reverse;
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

    public boolean showTime() {
        return time;
    }

    public stateNode getCurr() {
        return curr;
    }

    public stateNode getGoal() {
        return goal;
    }
}
