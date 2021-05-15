import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class stateNode implements Comparable<stateNode> {
    //  === NODE'S VARIABLE ===
    static int ID_Generator = 0;
    private final int _id, _leafDepth;
    private final int[][] _tiles;
    private ArrayList<Point> _empties = new ArrayList<>();
    private  static int nodeCounter;
    private boolean twoEmpty;

    // === ALGORITHM'S VARIABLES ===
    private Operator _operator;
    private String _operationStr;
    private final stateNode _prev;
    private int _cost, _heuristic;
    private boolean _out;


    /**
         _______________________________
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         |/\/\/\/\ CONSTRUCTORS /\/\/\/\|
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         ------------------------------
     */

    /**
     * default constructor
     * @param tiles
     */
    public stateNode(int[][] tiles) {
        this._leafDepth = 0;
        this._id = ++ID_Generator;
        this._tiles = tiles;
        this._prev = null;
        setEmptyTiles();

        checkForErrors();
//        printState();
    }

    /**
     * copy constructor for two tiles operations
     * @param prev
     * @param operator
     */
    public stateNode(stateNode prev, Operator operator){
        if(ID_Generator == 36)
            System.out.println("");
        // set simple variables
        nodeCounter++;
        this._id = ++ID_Generator; //id
        this._leafDepth = prev.getDepth()+1; // leaf depth
        this._prev = prev;
        this.twoEmpty = prev.isTwoEmpty();

        //deep copy prev's data
        this._tiles = prev.copyTiles();
        this._empties = prev.copyEmpties();

        setOperation(operator);
        Point empty1 = _empties.get(0);
        Point empty2 = _empties.get(1);
        swapOne(empty1);
        swapOne(empty2);

        this._cost = prev.getCost() + operator.price();
        checkForErrors();
//        printState();
    }

    /**
     * copy constructor for one tiles operations
     * @param prev
     * @param operator
     * @param empty
     */
    public stateNode(stateNode prev, Operator operator, Point empty){
        nodeCounter++;

        // set simple variables
        this._id = ++ID_Generator; //id
        this._leafDepth = prev.getDepth()+1; // leaf depth
        this._prev = prev;
        this._cost = prev.getCost() + operator.price();
        this.twoEmpty = prev.isTwoEmpty();

        //deep copy prev's data
        this._tiles = prev.copyTiles();
        this._empties = prev.copyEmpties();


        setOperation(empty, operator);
        swapOne(empty);

        checkForErrors();
//        printState();
    }


    /**
         _______________________________
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         |/\  ALGORITHM'S FUNCTIONS /\/\|
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         ------------------------------
     */

    /**
     * get all node's legal child leaf
     * @return arraylist of nodes
     */
    public ArrayList<stateNode> getChildren() {

        ArrayList<stateNode> children = new ArrayList<>();

        if(twoEmpty) {
            // sort empty tiles
            sortEmpty();
        }

        // move adjacent tiles
        ArrayList<Operator> adjacentOperators = getAdjacentOperators();
        for (Operator operation : adjacentOperators)
            children.add(new stateNode(this, operation));

        //move single tile
        for(Point empty : _empties) {
            for (Operator operation : getSingleOperators(empty))
                children.add(new stateNode(this, operation, empty));
        }

        return children;
    }

    /**
     * swap empty tile with a adjacent tile (determent by node's operator)
     * @param empty tile to be swapped
     */
    public void swapOne(Point empty){

        // get prev's coordinates
        int prev_x = empty.x;
        int prev_y = empty.y;

        int x = prev_x +  _operator.tile().x;
        int y = prev_y +  _operator.tile().y;

        int temp = this._tiles[x][y];
        this._tiles[prev_x][prev_y] = temp;
        this._tiles[x][y] = -1;

        updateEmpty(empty, x, y);
    }

    /**
     * if empty in empties:
     *      switch empty point with a deep copy
     * else:
     *      add new point (x,y) to empties
     *
     * @param empty original point
     * @param x x coordinate
     * @param y y coordinate
     */
    private void updateEmpty(Point empty, int x, int y){
        Point temp = new Point();
        for(Point p : _empties){
            if(p.x == empty.x && p.y == empty.y){
                temp = p;
            }
        }

        _empties.remove(temp);
        _empties.add(new Point(x,y));
    }

    /**
     * get all operators that can be done on given empty tile
     *
     * @param empty
     * @return arraylist of operators
     */
    private ArrayList<Operator> getSingleOperators(Point empty){
        ArrayList<Operator> legalOperators = new ArrayList<>();

        Operator[] operators = Operator.values();

        // operators 4 -> 8 are single operators
        for(int i = 4; i < 8; i++){
            Operator operator = operators[i];
            if(inBound(empty, operator))
                legalOperators.add(operator);
        }
        return legalOperators;
    }

    /**
     * get all operators that can be done on two empty tiles in parallel
     * @return arraylist of operators
     */
    private ArrayList<Operator> getAdjacentOperators(){
        ArrayList<Operator> legalOperators = new ArrayList<>();
        if(!twoEmpty)
            return legalOperators;



        Point empty = _empties.get(0);

        boolean upAndDown = adjacentHorizontally();
        boolean leftAndRight = adjacentVertically();

        if(leftAndRight && inBound(empty,Operator.TWO_LEFT))
            legalOperators.add(Operator.TWO_LEFT);

        if(upAndDown && inBound(empty,Operator.TWO_DOWN))
                legalOperators.add(Operator.TWO_DOWN);

        if(leftAndRight && inBound(empty,Operator.TWO_RIGHT))
                legalOperators.add(Operator.TWO_RIGHT);

        if(upAndDown && inBound(empty,Operator.TWO_UP))
                legalOperators.add(Operator.TWO_UP);

        return legalOperators;
    }

    /**
     * sort empty tiles by there x and then by there y
     * the tile closest to (0,0) should be first
     */
    private void sortEmpty(){
        if(!twoEmpty)
            return;
        //TODO check sort
        Point point1 = this._empties.get(0);
        Point point2 = this._empties.get(1);
        if(point1.x != point2.x)
            if(point1.y > point2.y)
                Collections.swap(_empties, 0, 1);
        else
            if(point1.x > point2.x)
                Collections.swap(_empties, 0, 1);
    }


    //                                   ++++++++ operators legality check ++++++++

    /**
     * check if operation is legal (prevent outOfBound exception)
     * @param empty
     * @param operator
     * @return true if operation is legal
     */
    private boolean inBound(Point empty, Operator operator){
        int x, y;
        x = operator.tile().x + empty.x;
        y = operator.tile().y + empty.y;

        return (x >= 0 && x < rowLen() && y >= 0  && y < colLen()) && (getTile(x,y) != -1);
    }

    /**
     * check operations legality
     * @return true if tiles could move horizontally
     */
    private boolean adjacentHorizontally(){
        Point empty1 = _empties.get(0),  empty2 = _empties.get(1);

        return empty1.x == empty2.x && Math.abs(empty1.y-empty2.y) <= 1;
    }

    /**
     * check operations legality
     * @return true if tiles could move vertically
     */
    private boolean adjacentVertically(){
        Point empty1 = _empties.get(0),  empty2 = _empties.get(1);
        return empty1.y == empty2.y && Math.abs(empty1.x-empty2.x) <= 1;
    }


    /**
          _______________________________
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         |/\/\/\/\/  GETTERS \/\/\/\/\/\|
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
          ------------------------------
     */

    //                                   ++++++++ stateNode's variables ++++++++

    /**
     * @return node's unique id
     */
    public int getId() {
        return _id;
    }

    /**
     * @return node's depth
     */
    public int getDepth() {
        return _leafDepth;
    }

    /**
     * @return node's tiles
     */
    public int[][] getTiles() {
        return _tiles;
    }

    /**
     * @return empty tiles
     */
    private ArrayList<Point> getEmpties() {
        return _empties;
    }

    public boolean isTwoEmpty() {
        return twoEmpty;
    }

    /**
     * @return node's operator
     */
    public Operator get_operator() {
        return _operator;
    }

    /**
     * @return operation as string
     */
    public String getLastOperation() {
        //TODO change to getOperatorStr or build in each call
         return _operationStr;
    }

    /**
     * @return get node's father
     */
    public stateNode getPrev() {
        return _prev;
    }

    /**
     * cost := total cost (cumulative cost of all operators in node's branch)
     * @return path-to-node cost
     */
    public int getCost() {
        return _cost;
    }

    /**
     * getter
     * @return node's heuristic value
     */
    public int Heuristic() {
        return _heuristic;
    }

    /**
     * getter
     * @return true if node was marked out
     */
    public boolean isOut() {
        return _out;
    }


    //                                       ++++++++ compute ++++++++

    /**
     * @return tiles row length
     */
    public int rowLen() {
        return _tiles.length;
    }

    /**
     * @return tiles column length
     */
    public int colLen() {
        return _tiles[0].length;
    }

    /**
     * @return node's state as string (non unique)
     */
    public String key(){
        //TODO check if called more then once
        return Arrays.deepToString(this._tiles);
    }

    public String toString(){
        String strID = String.valueOf(_id);
        return strID + " \uF0DF "+ Arrays.deepToString(this._tiles)+"  $"+(_cost+_heuristic);
    }

    /**
     *  get point with same coordinates as Point prev
     * @param empty father's empty
     * @return point from curr's empties
     */
    private Point getEmpty(Point empty){
        //TODO check if needed
        Point curr = null;
        for(Point p : _empties) {
            if (p.x == empty.x && p.y == empty.y) {
                curr = p;
            }
        }
        return curr;
    }

    /**
     * get tile value from given Point's coordinates
     * @param p Point
     * @return tile value
     */
    public int getTile(Point p){
        //TODO check if used
        return getTile(p.x,p.y);
    }

    /**
     * get tile value from given coordinates (x,y)
     * @param x coordinate
     * @param y coordinate
     * @return tile value
     */
    public int getTile(int x, int y){
        return this._tiles[x][y];
    }

    /**
     * getter
     * @return node's value  f() = h() + g()
     */
    public int f(){
        return _heuristic + _cost;
    }


    //                               ++++++++ deep copy stateNode's var ++++++++

    /**
     * @return deep copy of node's tiles
     */
    public int[][] copyTiles() {
        int[][] newTiles = new int[rowLen()][colLen()];
        for(int i=0; i< rowLen(); i++)
            System.arraycopy(this._tiles[i], 0, newTiles[i], 0, colLen());
        return newTiles;
    }

    /**
     * @return deep copy of empty tiles list
     */
    public ArrayList<Point> copyEmpties(){
        ArrayList<Point> newEmpties = new ArrayList<>();
        for(Point p: this._empties){
            newEmpties.add(new Point(p.x, p.y));
        }
        return newEmpties;
    }


    /**
         _______________________________
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         |/\/\/\/\/  SETTERS \/\/\/\/\/\|
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         ------------------------------
     */

    /**
     * initialize _empties
     * empties <-  empty points in tiles
     */
    private void setEmptyTiles(){
        for (int row = 0; row < rowLen(); row++)
            for (int col = 0; col < colLen(); col++)
                if(_tiles[row][col] == -1)
                    _empties.add(new Point(row, col));
        if(_empties.size()==2)
            twoEmpty = true;
    }

    /**
     * set one tile operation & operation str
     *
     * @param empty point that was moved
     * @param operation node's operator
     */
    public void setOperation(Point empty, Operator operation){
        // set state operator
        this._operator = operation;

        // set operator code
        int x = operation.tile().x;
        int y = operation.tile().y;
        this._operationStr = getTile(empty.x+x, empty.y+y) + operation.symbol();
    }

    /**
     * set two tiles operation & operation str
     *
     * @param operation node's operator
     */
    public void setOperation(Operator operation){
        // set state operator
        this._operator = operation;

        Point empty1 = _prev.getEmpties().get(0);
        Point empty2 = _prev.getEmpties().get(1);

        int x = operation.tile().x;
        int y = operation.tile().y;

        // set operator code
        int tile1 = getTile(empty1.x+x, empty1.y+y);
        int tile2 = getTile(empty2.x+x, empty2.y+y);
        this._operationStr =tile1 + "&" + tile2 + operation.symbol();
    }

    /**
     * @param heuristic
     */
    public void Heuristic(int heuristic) {
        this._heuristic = heuristic;
    }

    /**
     * set _out as true
     */
    public void markOut(boolean out) {
        _out = out;
    }




    /**
         _______________________________
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         |/\/  METHOD'S OVERRIDING \/\/\|
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         ------------------------------
     */

    /**
     * check if two stateNodes are same state
     * @param o
     * @return true if tiles are equal
     */
    @Override
    public boolean equals(Object o) {

        // o == this  -> true
        if (o == this) {
            return true;
        }

        // if o is a stateNode & do casting
        if (!(o instanceof stateNode other)) {
            return false;
        }


        // compare this's and other's tiles
        int[][] otherTiles = other.getTiles();

        for (int i = 0; i < rowLen(); i++) {
            if (!Arrays.equals(otherTiles[i], _tiles[i])) {
                return false;
            }

        }

        return true;
    }

    /**
     * compare nodes by there heuristic value
     * @param other
     * @return this > other ->  1
     *         this == other -> 0
     *         this < other -> -1
     */
    @Override
    public int compareTo(@NotNull stateNode other) {
        int other_f = other.f();
        int this_f = f();
        return Integer.compare(this_f, other_f);
    }

    /**
     * check who is bigger by there heuristic value
     * @param other
     * @return true if this bigger than other
     */
    public boolean isGreaterThan(stateNode other){
        return compareTo(other) == 1;
    }


    /**
         _______________________________
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         |/\/\/\  FOR DEBUGGING /\/\/\/\|
         |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\|
         ------------------------------
     */

    /**
     * check that empties are accurate
     */

    private void checkForErrors(){
        checkEmpties();
    }

    public static int stateNodeNumber(){
        return nodeCounter;
    }

    public void checkEmpties(){
        for(Point empty: _empties){
            int emptyVal = getTile(empty);
            if( emptyVal != -1){
                printState();
                throw new RuntimeException("ERROR! \nEmpty point ("+empty.x+","+empty.y+") is not empty."+", nodeId =="+ _id);
            }
            if(_empties.size()>2){
                throw new RuntimeException("ERROR! \nthere are currently "+ _empties.size()+"empty points"+", nodeId =="+ _id);
            }
            if(this._prev != null) {
                if (!checkAdjacentToFather(empty)) {
                    throw new RuntimeException("ERROR! \nPoint (" + empty.x + "," + empty.y + ") isnt adjacent to prev empties .\nnodeId =="+ _id);
                }
            }
        }
    }

    private boolean checkAdjacentToFather(Point empty){
        ArrayList<Point> prevEmpties = _prev.getEmpties();
        for(Point fatherEmpty : prevEmpties) {
            if(Math.abs((empty.x - fatherEmpty.x) + (empty.y - fatherEmpty.y))< 2)
                return true;
        }
        return false;
    }

    /**
     * print state for debug purposes
     */
    public void printState(){
        int prevID = _prev == null ? -1: _prev.getId();
        String operator = _operator == null ? "": _operator.name();

        System.out.println("=== father := "+ prevID + " , this ID := "+_id+", "+operator +" ===");
        for (int[] x : _tiles)
        {
            for (int y : x)
            {
                System.out.print(y + " ");
            }
            System.out.println();
        }
        for(Point empty: _empties)
            System.out.println("empty = ("+empty.x+","+empty.y+")");
    }
}
