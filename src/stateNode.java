import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class stateNode {
    private int[][] tiles;
    private stateNode prev;
    private int rowLen, colLen;
    private Point empty1, empty2;
    private LinkedHashMap<Integer, stateNode> children = new LinkedHashMap<>();
//    private enum operator{
//        twoLeft,
//        twoUp,
//        twoRight,
//        twoDown,
//        oneLeft,
//        oneUp,
//        oneRight,
//        oneDown
//    }

    // Constructor
    public stateNode(int[][] tiles, Point p1, Point p2) {
        this.tiles = tiles;
        this.rowLen = tiles.length;
        this.colLen = tiles[0].length;
        this.prev = null;
        this.empty1 = p1;
        this.empty2 = p2;
    }

    // Copy Constructor
    public stateNode(stateNode prev) {
        this.rowLen = prev.getRowLen();
        this.colLen = prev.getColLen();
        this.tiles = prev.getTiles();
        this.prev = prev;
    }



    public int getRowLen() {
        return rowLen;
    }

    public int getColLen() {
        return colLen;
    }

    public void setTiles(int[][] tiles) {
        this.tiles = tiles;
    }

    public int[][] getTiles() {
        int[][] newTiles = new int[rowLen][colLen];
        for(int i=0; i<this.rowLen; i++)
            for(int j=0; j<this.colLen; j++)
                newTiles[i][j] = this.tiles[i][j];
        return newTiles;
    }

    public LinkedHashMap<Integer, stateNode> getChildren(stateNode father) {
        stateNode childState;
        Point[] empties = emptyTiles();

        if(empty2 != null) {

            // two left:
            if (colEmpty() && empty1.x > 1)
                this.children.put(0, left(empties));

            // two up:
            if (rowEmpty() && empty1.y > 1)
                this.children.put(0, up(empties));

            // two right:
            if (colEmpty() && empty1.y < colLen)
                this.children.put(0, right(empties));

            // two down:
            if (rowEmpty() && empty1.x < rowLen)
                this.children.put(0, down(empties));

            Point secondEmpty = empties[1];
            empties[1] = null;
            this.children.put(0, left(empties));
            this.children.put(0, up(empties));
            this.children.put(0, right(empties));
            this.children.put(0, down(empties));

            empties[0] = secondEmpty;
            this.children.put(0, left(empties));
            this.children.put(0, up(empties));
            this.children.put(0, right(empties));
            this.children.put(0, down(empties));
        }else {
            empties[0] = empty1;
            // one left:
            if (empty1.y > 0)
                this.children.put(0, left(empties));

            // one up:
            if (empty1.x > 0)
                this.children.put(0, up(empties));

            // one right:
            if (empty1.y < colLen)
                this.children.put(0, right(empties));

            // one down:
            if (empty1.y < rowLen)
                this.children.put(0, down(empties));

        }
        return this.children;
    }

    public void twoLeft(){
        new stateNode(this);
        if(colEmpty()){

        }
    }

    public stateNode left(Point[] empties){
        stateNode newState = new stateNode(this);
        for(Point e: empties) {
            if(e != null)
                newState.switchLeft(e);
        }
        return newState;
    }
    public void switchLeft(Point empty){
        this.tiles[empty.x][empty.y] = this.tiles[empty.x][empty.y-1];
        this.tiles[empty.x][empty.y-1] = -1;
    }

    public stateNode right(Point[] empties){
        stateNode newState = new stateNode(this);
        for(Point e: empties) {
            newState.switchRight(e);
        }
        return newState;
    }
    public void switchRight(Point empty){
        this.tiles[empty.x][empty.y] = this.tiles[empty.x][empty.y+1];
        this.tiles[empty.x][empty.y+1] = -1;
    }

    public stateNode up(Point[] empties){
        stateNode newState = new stateNode(this);
        for(Point e: empties) {
            newState.switchUp(e);
        }
        return newState;
    }
    public void switchUp(Point empty){
        this.tiles[empty.x][empty.y] = this.tiles[empty.x+1][empty.y];
        this.tiles[empty.x+1][empty.y] = -1;
    }

    public stateNode down(Point[] empties){
        stateNode newState = new stateNode(this);
        for(Point e: empties) {
            newState.switchDown(e);
        }
        return newState;
    }
    public void switchDown(Point empty){
        this.tiles[empty.x][empty.y] = this.tiles[empty.x-1][empty.y];
        this.tiles[empty.x-1][empty.y] = -1;
    }

    public boolean colEmpty(){
        return empty1.x == empty2.x && Math.abs(empty1.y-empty2.y) <= 1;
    }

    public boolean rowEmpty(){
        return empty1.y == empty2.y && Math.abs(empty1.x-empty2.x) <= 1;
    }

    private Point[] emptyTiles(){
        Point[] empties = new Point[2];
        if(empty1.y != empty2.y){
            if(empty1.y > empty2.y) {
                empties[0] = empty2;
                empties[1] = empty1;
            }else{
                empties[0] = empty1;
                empties[1] = empty2;
            }
        }else{
            if(empty1.x > empty2.x) {
                empties[0] = empty2;
                empties[1] = empty1;
            }else{
                empties[0] = empty1;
                empties[1] = empty2;
            }
        }
        return empties;
    }

}


//        switch (operator.values()[child]) {
//                case twoLeft:
//                // empty2 != null &&  empty1.x == empty2.x && Math.abs(empty1.y-empty2.y) <= 1;
//                if (colEmpty() && empty1.x > 1) {
//                left();
//                }
//                break;
//                case twoUp:
//                if (rowEmpty() && empty1.x > 1) {
//                up();
//                }
//                break;
//                case twoRight:
//                if (colEmpty() && empty1.y < colLen) {
//        right();
//        }
//        break;
//        case twoDown:
//        if (rowEmpty() && empty1.x < rowLen) {
//        down();
//        }
//        break;
//        case oneLeft:
//        if (empty1.y > 0) {
//        left(empty1);
//        }
//        break;
//        case oneUp:
//        if (empty1.x > 0) {
//        up(empty1);
//        }
//        break;
//        case oneRight:
//        if (empty1.y < colLen) {
//        right(empty1);
//        }
//        break;
//        case oneDown:
//        if (empty1.y < rowLen) {
//        down(empty1);
//        }
//        break;
//        }
//        return childState;
