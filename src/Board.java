public class Board {

    stateNode goal;
    stateNode curr;
    boolean time, open;
    int rowLen, colLen;

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
}
