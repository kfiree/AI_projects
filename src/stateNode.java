import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class stateNode implements Comparable<stateNode> {
    static int ID_Generator = 0;
    private final int id, depth;
    private final int[][] tiles;
    private String lastOperation;
    private final stateNode prev;
    private final int colLen, rowLen;
    private int cost=0, heuristic;
    private final ArrayList<Point> empties;
    private final LinkedHashMap<String, stateNode> children = new LinkedHashMap<>();
    Hashtable<String, stateNode> Frontier, Explored;

    // Constructor
    public stateNode(int[][] tiles, ArrayList<Point> emptyTiles) {
        this.depth = 0;
        this.id = ++ID_Generator;
        this.tiles = tiles;
        this.rowLen = tiles.length;
        this.colLen = tiles[0].length;
        this.prev = null;
        this.empties = getEmpties(emptyTiles);
    }

    // Copy Constructor
    public stateNode(stateNode prev) {
        this.depth = prev.getDepth()+1;
        this.id = ++ID_Generator;
        this.rowLen = prev.getRowLen();
        this.colLen = prev.getColLen();
        this.tiles = prev.getTiles();
        this.prev = prev;
        this.empties = prev.getEmpties();
    }

    public LinkedHashMap<String, stateNode> getChildren(searchAlgorithm algo) {
        Frontier = algo.getFrontier();
        Explored = algo.getExplored();

        fixEmptyTileOrder();

        if(empties.size() == 2)
            moveTwo();
        else
            moveOne(empties.get(0));

        return this.children;
    }

    private void moveTwo(){
        Point empty = empties.get(0);
        int x = empty.x, y = empty.y;
        int TwoHorizontal_price = Prices.twoSides.getValue();
        int TwoVertical_price = Prices.twoUpAndDown.getValue();

        // two left:
        if (isColEmpty() && y > 0)
            updateOpenList(left(), TwoHorizontal_price);

        // two up:
        if (isRowEmpty() && x > 0)
            updateOpenList(up(), TwoVertical_price);

        // two right:
        if (isColEmpty() && y < colLen - 1)
            updateOpenList(right(), TwoHorizontal_price);


        // two down:
        if (isRowEmpty() && x < rowLen - 1)
            updateOpenList(down(), TwoVertical_price);

        moveOne(empties.get(0));
        moveOne(empties.get(1));

    }

    void updateOpenList(stateNode child, int cost){
        String childKey = child.key();
        if(!Frontier.containsKey(childKey) && !Explored.containsKey(childKey)) {
            child.updateCost(cost);
            this.children.put(child.key(), child);
        }
    }

    private void moveOne(Point empty){
        int x = empty.x, y = empty.y;
        int moveOne_price = Prices.moveOne.getValue();

        // left:
        if(y > 0)
            updateOpenList(left(empty), moveOne_price);

        // up:
        if(x > 0)
            updateOpenList(up(empty), moveOne_price);

        // right:
        if(y < colLen - 1)
            updateOpenList(right(empty), moveOne_price);

        // down:
        if(x < rowLen - 1)
            updateOpenList(down(empty), moveOne_price);
    }

    public stateNode left(){
        stateNode childState = new stateNode(this);
        for(Point empty: empties){
            childState.swapLeft(empty);
        }

        childState.setLastOperation("R", true);
        return childState;
    }
    public stateNode left(Point empty){
        stateNode childState = new stateNode(this);

        childState.swapLeft(empty);
        childState.setLastOperation("R", false);


        return childState;
    }
    public void swapLeft(Point empty){
//        System.out.println("===============left===============");
//        printState();
        empty = getEmpty(empty);
        int tile = this.tiles[empty.x][empty.y - 1];

        this.tiles[empty.x][empty.y] = tile;
        this.tiles[empty.x][empty.y-1] = -1;

        getEmpty(empty).y-=1;
//        printState();
    }

    public stateNode up(){
        stateNode childState = new stateNode(this);
        for(Point e: empties) {
            childState.swapUp(e);
        }

        childState.setLastOperation("D", true);
        return childState;
    }
    public stateNode up(Point empty){
        stateNode childState = new stateNode(this);
        if(empty != null)
            childState.swapUp(empty);

        childState.setLastOperation("D", false);
        return childState;
    }
    public void swapUp(Point empty){
//        System.out.println("===============up===============");
//        printState();
        empty = getEmpty(empty);
        int tile = this.tiles[empty.x-1][empty.y];

        this.tiles[empty.x][empty.y] = tile;
        this.tiles[empty.x-1][empty.y] = -1;
        getEmpty(empty).x-=1;
//        printState();
    }

    public stateNode right(){
        stateNode childState = new stateNode(this);
        for(Point e: empties) {
            childState.swapRight(e);
        }
        childState.setLastOperation("L", true);
        return childState;
    }
    public stateNode right(Point empty){
        stateNode childState = new stateNode(this);

        childState.swapRight(empty);

        childState.setLastOperation("L", false);
        return childState;
    }
    public void swapRight(Point empty){
//        System.out.println("===============right===============");
//        printState();
        int tile = this.tiles[empty.x][empty.y + 1];

        this.tiles[empty.x][empty.y] = tile;
        this.tiles[empty.x][empty.y+1] = -1;
        getEmpty(empty).y+=1;
//        printState();
    }

    public stateNode down(){
        stateNode childState = new stateNode(this);
        for(Point e: empties) {
            childState.swapDown(e);
        }
        childState.setLastOperation("U", true);
        return childState;
    }
    public stateNode down(Point empty){
        stateNode childState = new stateNode(this);
        if(empty != null)
            childState.swapDown(empty);

        childState.setLastOperation("U", false);
        return childState;
    }
    public void swapDown(Point empty){
//        System.out.println("===============down===============");
//        printState();
        empty = getEmpty(empty);
        int tile = this.tiles[empty.x+1][empty.y];

        this.tiles[empty.x][empty.y] = tile;
        this.tiles[empty.x+1][empty.y] = -1;
        getEmpty(empty).x+=1;
//        printState();
    }

    public void updateCost(int cost) {
        this.cost += cost;
    }

    public int getCost() {
        return cost;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isRowEmpty(){
        Point empty1 = empties.get(0),  empty2 = empties.get(1);
        return empty1.x == empty2.x && Math.abs(empty1.y-empty2.y) <= 1;
    }

    public boolean isColEmpty(){
        Point empty1 = empties.get(0),  empty2 = empties.get(1);
        return empty1.y == empty2.y && Math.abs(empty1.x-empty2.x) <= 1;
    }

    private void fixEmptyTileOrder(){
        Point point1 = this.empties.get(0);
        Point point2 = this.empties.get(1);
        if(point1.y != point2.y)
            if(point1.y > point2.y)
                Collections.swap(empties, 0, 1);
        else
            if(point1.x > point2.x)
                Collections.swap(empties, 0, 1);
    }

    public ArrayList<Point> getEmpties() {
        return getEmpties(this.empties);
    }
    public ArrayList<Point> getEmpties(ArrayList<Point> emptyTiles) {
        ArrayList<Point> newEmpties = new ArrayList<>();
        for(Point p: emptyTiles){
            newEmpties.add(new Point(p.x, p.y));
        }
        return newEmpties;
    }

    public int Id() {
        return id;
    }

    public int[][] getTiles() {
        int[][] newTiles = new int[rowLen][colLen];
        for(int i=0; i<this.rowLen; i++)
            System.arraycopy(this.tiles[i], 0, newTiles[i], 0, this.colLen);
        return newTiles;
    }

    public void setLastOperation(String lastOperation, boolean twoMoved){

        Point p1 = prev.getEmpties().get(0);
        int firstTile = getTile(p1);

        if(firstTile == -1)
            p1 = prev.getEmpties().get(1);

        String t1 = String.valueOf(getTile(p1));

        if(twoMoved){
            Point p2 = prev.getEmpties().get(1);
            String t2 = String.valueOf(getTile(p2));

            this.lastOperation =t1 +"&"+ t2 + lastOperation;
        }else{
            this.lastOperation = t1+lastOperation;
        }
    }


    public String getLastOperation() {
        return lastOperation;
    }

    public int getRowLen() {
        return rowLen;
    }

    public int getColLen() {
        return colLen;
    }

    private Point getEmpty(Point prev){
        Point curr = null;
        for(Point p : empties) {
            if (p.x == prev.x && p.y == prev.y) {
                curr = p;
            }
        }
        return curr;
    }

    public stateNode getPrev() {
        return prev;
    }

    public String key(){
        return Arrays.deepToString(this.tiles);
    }

    public void printState(){
        for (int[] x : tiles)
        {
            for (int y : x)
            {
                System.out.print(y + " ");
            }
            System.out.println();
        }
        if((empties.get(0).x == 2 && empties.get(0).y == 0) && (empties.get(1).x == 2 && empties.get(1).y == 2))
            System.out.println("here is the problem");
        System.out.println("empty1 = ("+empties.get(0).x+","+empties.get(0).y+")");
        System.out.println("empty2 = ("+empties.get(1).x+","+empties.get(1).y+")");
    }

    public int getTile(Point p){
        return getTile(p.x,p.y);
    }
    public int getTile(int x, int y){
        return this.tiles[x][y];
    }

    public int Heuristic() {
        return heuristic;
    }

    public void Heuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof stateNode other)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members

        // Compare the data members and return accordingly
        int[][] thisTiles = this.getTiles();
        int[][] otherTiles = other.getTiles();

        for (int i = 0; i < thisTiles.length; i++) {
            if (!Arrays.equals(otherTiles[i], thisTiles[i])) {
                return false;
            }

        }

        return true;
    }

    @Override
    public int compareTo(@NotNull stateNode other) {
        int other_heuristic = other.Heuristic();
        int this_heuristic = heuristic;
        return Integer.compare(this_heuristic, other_heuristic);
    }

    public boolean isGreaterThan(stateNode other){
        return compareTo(other) == 0;
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
