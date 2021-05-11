import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class stateNode implements Comparable<stateNode> {
    static int ID_Generator = 0;
    private final int id, _leafDepth;
    private final int[][] tiles;
    private Operator _operator;
    private String lastOperation;
    private final stateNode prev;
    private int cost=0, heuristic;
    private ArrayList<Point> empties = new ArrayList<>();
    private boolean isOut;

    // Constructor
    public stateNode(int[][] tiles) {
        this._leafDepth = 0;
        this.id = ++ID_Generator;
        this.tiles = tiles;
        this.prev = null;
        setEmptyTiles();
    }

    // Copy Constructor
    public stateNode(stateNode prev, Operator operator){
        // set simple variables
        this.id = ++ID_Generator; //id
        this._leafDepth = prev.getDepth()+1; // leaf depth
        this.prev = prev;

        //deep copy prev's data
        this.tiles = prev.copyTiles();
        this.empties = prev.getEmpties();

        setOperation(operator);
        swapOne(empties.get(0), operator);
        swapOne(empties.get(1), operator);

        this.cost = prev.getCost() + operator.price();
    }

    public stateNode(stateNode prev, Operator operator, Point empty){
        // set simple variables
        this.id = ++ID_Generator; //id
        this._leafDepth = prev.getDepth()+1; // leaf depth
        this.prev = prev;
        this.cost = prev.getCost() + operator.price();

        //deep copy prev's data
        this.tiles = prev.copyTiles();
        this.empties = prev.getEmpties();

        setOperation(operator);
        swapOne(empty, operator);
    }

    private void doOperation(){

    }

    public ArrayList<stateNode> getChildren() {

        ArrayList<stateNode> children = new ArrayList<>();

        if(empties.size() == 2) {
            // sort empty tiles
            sortEmpty();

            // move adjacent tiles
            for (Operator operation : getAdjacentOperators())
                children.add(new stateNode(this, operation));

            //move single tile
            Point empty1 = empties.get(0);
            for (Operator operation : getSingleOperators(empty1))
                children.add(new stateNode(this, operation, empty1));

            Point empty2 = empties.get(1);
            for (Operator operation : getSingleOperators(empty2))
                children.add(new stateNode(this, operation, empty2));

        }else{
            for (Operator operation : getSingleOperators(empties.get(0)))
                children.add(new stateNode(this, operation));
        }

        return children;
    }

    public void swapOne(Point empty, Operator operator){

        // get prev's coordinates
        int prev_x = empty.x;
        int prev_y = empty.y;

        int x = prev_x +  _operator.tile().x;
        int y = prev_y +  _operator.tile().y;

        int temp = this.tiles[x][y];
        this.tiles[prev_x][prev_y] = temp;
        this.tiles[x][y] = -1;

    }

    public void updateCost(int cost) {
        this.cost += cost;
    }

    public int getCost() {
        return cost;
    }

    public int getDepth() {
        return _leafDepth;
    }

    private ArrayList<Operator> getSingleOperators(Point empty){
        ArrayList<Operator> legalOperators = new ArrayList<>();
        int x, y;

        Operator[] operators = Operator.values();

        for(int i = 4; i < 8; i++){
            Operator operator = operators[i];
            if(inBound(empty, operator))
                legalOperators.add(operator);
        }
        return legalOperators;
    }

    private ArrayList<Operator> getAdjacentOperators(){
        ArrayList<Operator> legalOperators = new ArrayList<>();
        int x, y;

        Point empty1 = empties.get(0),  empty2 = empties.get(1);
        Operator[] operators = Operator.values();

        if(!adjacent()) {
            for(int i = 0; i < 4; i++){
                Operator operator = operators[i];
                if(inBound(empty1, operator) && inBound(empty2, operator))
                    legalOperators.add(operator);
                //TODO check adj wrong size
            }
        }
        return legalOperators;
    }

    private boolean inBound(Point empty, Operator operator){
        int x, y;
        x = operator.tile().x + empty.x;
        y = operator.tile().y + empty.y;

        return x >= 0 && x < colLen() && y >= 0  && y < rowLen();
    }
    private boolean adjacent(){
        Point empty1 = empties.get(0),  empty2 = empties.get(1);

        boolean Horizontally = (empty1.x == empty2.x && Math.abs(empty1.y-empty2.y) <= 1);
        boolean Vertically = (empty1.y == empty2.y && Math.abs(empty1.x-empty2.x) <= 1);

        return Horizontally && Vertically;
    }

    private void sortEmpty(){
        Point point1 = this.empties.get(0);
        Point point2 = this.empties.get(1);
        if(point1.y != point2.y)
            if(point1.y > point2.y)
                Collections.swap(empties, 0, 1);
        else
            if(point1.x > point2.x)
                Collections.swap(empties, 0, 1);
    }

    private ArrayList<Point> getEmpties() {
        setEmpties(this.empties);
        return empties;
    }
    public void setEmpties(ArrayList<Point> emptyTiles) {
        for(Point p: emptyTiles){
            empties.remove(p);
            empties.add(new Point(p.x, p.y));
        }
    }

    public int[][] copyTiles() {
        int[][] newTiles = new int[rowLen()][colLen()];
        for(int i=0; i< rowLen(); i++)
            System.arraycopy(this.tiles[i], 0, newTiles[i], 0, colLen());
        return newTiles;
    }

    public int[][] getTiles() {
        return tiles;
    }

    public void setOperation(Operator operation){
        this._operator = operation;

        int tile1 = getTile(prev.getEmpties().get(0));
        int tile2 = getTile(prev.getEmpties().get(1));
        //TODO check on 1 empty tile

        //if two tiles was moved
        if(_operator.ordinal()<4){
            this.lastOperation =tile1 + "&" + tile2 + operation.symbol();
        }else{
            int tile = tile1 == -1 ? tile1 :tile2;
            this.lastOperation = tile + operation.symbol();
        }

    }


    public String getLastOperation() {
        return lastOperation;
    }

    public int rowLen() {
        return tiles[0].length;
    }

    public int colLen() {
        return tiles.length;
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

    public int f(){
        return heuristic + cost;
    }

    public void Heuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public boolean isOut() {
        return isOut;
    }

    public void markOut() {
        isOut = true;
    }

    private void setEmptyTiles(){
        for (int row = 0; row < rowLen(); row++)
            for (int col = 0; col < colLen(); col++)
                if(tiles[row][col] == -1)
                    empties.add(new Point(row, col));
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
        int[][] otherTiles = other.getTiles();

        for (int i = 0; i < rowLen(); i++) {
            if (!Arrays.equals(otherTiles[i], tiles[i])) {
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
