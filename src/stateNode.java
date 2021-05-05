import java.util.ArrayList;

public class stateNode {
    int[][] tiles;
    stateNode prev;

    public stateNode(int[][] tiles, stateNode prev) {
        this.tiles = tiles;
        this.prev = prev;
    }
}
