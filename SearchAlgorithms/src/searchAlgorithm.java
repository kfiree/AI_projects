import java.awt.*;
import java.util.*;
import java.util.List;

enum Result{
    GOAL(null),
    FAIL(null),
    CUTOFF(null);

    private stateNode goal;

    public stateNode getGoal(){
        return this.goal;
    }

    public Result setGoal(stateNode goal){
        this.goal = goal;
        return this;
    }

    Result(stateNode resultGoal){
        this.goal = resultGoal;
    }

}

public class searchAlgorithm {

    Board board;
    private int nodesCtr = 1;
    private double time = 0;
//    Hashtable<String, stateNode> FrontierTable= new Hashtable<>(), Explored = new Hashtable<>();

    Result result;

    /**
     * CONSTRUCTOR
     * @param board game board
     */
    public searchAlgorithm(Board board) {
        this.board = board;
    }

    /**
     *                    --------------------------------
     *                   |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/|
     *                   |/\/\/\ SEARCH ALGORITHMS \/\/\/|
     *                   |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/|
     *                   --------------------------------
     */


    /**
     * Recursive DFID with loop avoidance
     * @return path from start to goal
     */
    public List<stateNode> BFS(){

        // get goal and start
        stateNode start = this.board.getCurr();
        stateNode goal = this.board.getGoal();

        // set Frontier (open list)
        Queue<stateNode> Frontier = new LinkedList<>();
        Hashtable<String, stateNode> FrontierTable = new Hashtable<>();

        //  set Explored (close list)
        Hashtable<String, stateNode> Explored = new Hashtable<>();

        // Frontier  start
        Frontier.add(start);
        FrontierTable.put(start.key(), start);


        //  While L ≠  ∅
        while (!Frontier.isEmpty()) {
            printOpenList(Frontier);

            // curr  L.remove_front()
            stateNode curr = Frontier.poll();

            // Explored  curr
            Explored.put(curr.key(), curr);

            // For each allowed operator on curr
            for(stateNode child :  curr.getChildren()){

                // if curr ∉ Explored, Frontier
                if(!Explored.containsKey(child.key()) && !FrontierTable.containsKey(child.key())){

                    // If child == goal
                    if(child.equals(goal))
                        return pathHandler(child) ;


                    // Frontier  curr
                    Frontier.add(child);
                    FrontierTable.put(child.key(), child);
                }
            }
        }
        // fail to find solution
        return null;
    }

    /**
     * uniform cost search with heuristic function
     * @return path from start to goal
     */
    public List<stateNode> AStar(){
        // get goal and start
        stateNode start = this.board.getCurr();
        stateNode goal = this.board.getGoal();

        //  set Frontier (open list)
        PriorityQueue<stateNode> Frontier= new PriorityQueue<>();
        Hashtable<String, stateNode> FrontierTable = new Hashtable<>();

        // set Explored (close list)
        Hashtable<String, stateNode> Explored = new Hashtable<>();

        // Frontier  start
        Frontier.add(start);
        FrontierTable.put(start.key(), start);

        //  While Frontier ≠  ∅
        while(!Frontier.isEmpty()){
            printOpenList(Frontier);

            // curr  L.remove_front()
            stateNode curr = Frontier.poll();
            FrontierTable.remove(curr.key());

            // set curr heuristic
            curr.Heuristic(setHeuristic(curr));

            // If child == goal
            if(curr.equals(goal))
                return pathHandler(curr);

            // Explored  curr
            Explored.put(curr.key(), curr);

            // For each allowed operator on curr
            ArrayList<stateNode> children = curr.getChildren();
            for(stateNode child :children){

                // set child heuristic
                child.Heuristic(setHeuristic(child));

                // if child ∉ Explored, Frontier
                if(!Explored.containsKey(child.key()) && !FrontierTable.containsKey(child.key())){

                    // Frontier  child
                    Frontier.add(child);
                    FrontierTable.put(child.key(), child);

                // else if child ∈ Frontier
                }else if(FrontierTable.containsKey(child.key())){
                    stateNode olderChild = FrontierTable.get(child.key());

                    //if child.f() > olderChild.f()
                    if( olderChild.isGreaterThan(child) ){
                        // replace child and olderChild
                        Frontier.remove(olderChild);
                        Frontier.add(child);
                        FrontierTable.put(child.key(), child);
                    }
                }
            }
        }
        // FAIL
        return null;
    }

    /**
     * Recursive DFID with loop avoidance
     * using limited DFS algorithm with increased limit until reached goal
     * @return path from start to goal
     */
    public List<stateNode> DFID(){

        // get goal and start
        stateNode start = this.board.getCurr();
        stateNode goal = this.board.getGoal();

        // For depth=1 to ∞
        for(int depth = 0; depth < Integer.MAX_VALUE; depth++){
            //  set Explored (close list)
            Hashtable<String, stateNode> Explored = new Hashtable<>();

            // start recursive iterative deepening dfs search
            result = Limited_DFS(start, goal, depth, Explored);

            // handle result
            if(result != Result.CUTOFF) {
                if(result == Result.GOAL){
                    return pathHandler(result.getGoal());
                }
            }
        }
        return null;
    }

    /**
     * DFS algorithm with limited max depth
     * @param curr sub tree root
     * @param goal
     * @param limit max depth
     * @param Explored closed list
     * @return
     */
    public Result Limited_DFS(stateNode curr, stateNode goal, int limit, Hashtable<String, stateNode> Explored){

        //  check if reached goal or limit
        if(curr.equals(goal))
            return Result.GOAL.setGoal(curr);
        else if(limit == 0)
            return Result.CUTOFF;
        else{
            // Explored  curr
            Explored.put(curr.key(), curr);

            //set cutOff
            boolean isCutoff = false;

            // For each allowed operator on curr
            for(stateNode child: curr.getChildren()){

                nodesCtr++;

                //if in closed list
                if(Explored.containsKey(child.key())) {
                    if (Explored.get(child.key()).getDepth() > child.getDepth()) {
                        Explored.remove(child.key());
                    } else {
                        continue;
                    }
                }

                //recursive call
                result = Limited_DFS(child, goal, limit-1, Explored);

                //handle result
                if(result == Result.CUTOFF) {
                    isCutoff = true;
                }else if(result != Result.FAIL) {
                    return result;
                }
            }
            // finish with
            Explored.remove(curr);
            return isCutoff? Result.CUTOFF: Result.FAIL;
        }
    }

    /**
     * Iterative deepening A*
     * @return path from start to goal
     */
    public List<stateNode> IDAStar(){

        // get goal and start
        stateNode start = this.board.getCurr();
        stateNode goal = this.board.getGoal();

        //  set Frontier (open list)
        Stack<stateNode> Frontier = new Stack<>();
        Hashtable<String, stateNode> Explored = new Hashtable<>();

        // threshold  start.heuristic()
        start.Heuristic(setHeuristic(start));
        double minF, threshold = start.Heuristic();

        // While threshold ≠ ∞
        while(threshold != Double.MAX_VALUE) {
            printOpenList(Frontier);

            // minF  ∞
            minF = Double.MAX_VALUE;

            Explored.put(start.key(), start);
            Frontier.push(start);

            //  While Frontier ≠  ∅
            while (!Frontier.isEmpty()){

                printOpenList(Frontier);
                stateNode curr = Frontier.pop();
//                H.remove(pop.key()); //TODO ?

                if(curr.isOut()){
                    Explored.remove(curr.key());
                }else {
                    curr.markOut(true);
                    Frontier.push(curr);

                    // For each allowed operator on n
                    ArrayList<stateNode> children = curr.getChildren();
                    for (stateNode operator : children) {
                        nodesCtr++;
                        operator.Heuristic(setHeuristic(operator));
                        if (operator.f() > threshold) {
                            minF = Math.min(minF, operator.f());
                            continue;
                        }

                        stateNode olderChild = Explored.get(operator.key());

                        if (olderChild != null && olderChild.isOut()) {
                            continue;
                        }

                        if(olderChild != null && !olderChild.isOut()){
                            olderChild.Heuristic(setHeuristic(olderChild));

                            if (olderChild.f() > operator.f()) {
                                Explored.remove(olderChild.key());
                                Frontier.remove(olderChild);
                            } else {
                                continue;
                            }
                        }
                        if (operator.equals(goal)) {
                            return pathHandler(operator);
                        }
                        Frontier.push(operator);
                        Explored.put(operator.key(), operator);
                    }
                }
            }
            start.markOut(false);
            threshold = minF;
        }
        return null;
    }

    /**
     * Depth First Branch and Bound
     *
     * @return path from start to goal
     */
    public List<stateNode> DFBnB(){
        //set start and goal
        stateNode start = this.board.getCurr();
        stateNode goal = this.board.getGoal();

        Stack<stateNode> Frontier = new Stack<>();
        Hashtable<String, stateNode> Explored = new Hashtable<>();

        Frontier.push(start);
        Explored.put(start.key(), start);

        // result  null, t  ∞
        double t = Integer.MAX_VALUE;
        List<stateNode> result = null;

        // While L is not empty
        while(!Frontier.isEmpty()){
            printOpenList(Frontier);

            // 1. n  L.remove_front()
            stateNode curr = Frontier.pop();

            if(curr.isOut()) {
                Explored.remove(curr.key());
            }else{
                curr.markOut(true);
                Frontier.push(curr);

                ArrayList<stateNode> children = curr.getChildren();

                // sort the children according to their f values (increasing order)
                for(stateNode child :  children) {
                    nodesCtr++;
                    child.Heuristic(setHeuristic(child));
                }

                Collections.sort(children);

                // For each node g from N according to the order of N
                Iterator<stateNode> itr = children.iterator();
                while(itr.hasNext()){
                    stateNode child = itr.next();
                    if(child.f() >= t){
                        itr.remove();
                        while(itr.hasNext()&&children.contains(itr.next())){
                            itr.remove();
                        }
                    }else if (Explored.containsKey(child.key()) && Explored.get(child.key()).isOut()){
                        itr.remove();
                    }else if (Explored.containsKey(child.key()) && !Explored.get(child.key()).isOut()){
                        stateNode g2 = Explored.get(child.key());
                        if(g2.f()<=child.f()) {
                            itr.remove();
                        }else {
                            Frontier.remove(g2);
                            Explored.remove(g2.key());
                        }
                    }
                    else if(child.equals(goal)){

                        t = child.f();

                        result = pathHandler(child);

                        while(itr.hasNext() && children.contains(itr.next())){
                            itr.remove();
                        }
                    }
                }
                for (int i = children.size() - 1; i >= 0; i--) {
                    stateNode child = children.get(i);
                    Frontier.push(child);
                    Explored.put(child.key(), child);
                }

            }

        }
        return result;
    }

/**
 *                    --------------------------------
 *                   |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/|
 *                   |/\/\ Assistance Functions /\/\/|
 *                   |/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/|
 *                   --------------------------------
 */

    private double setHeuristic(stateNode curr){
        return manhattan(curr)* Operator.onePrice();
//        return (manhattan(curr)+linearConflict(curr));
    }

    private double manhattan(stateNode curr) {
        double cost = 0;
        int tileNum;
        int[][] tiles = curr.getTiles();

        for (int row = 0; row < board.height(); row++) {
            for (int col = 0; col < board.width(); col++) {
                tileNum = tiles[row][col];
                if(tileNum!=-1){
                    Point targetLocation = board.tileLocation(tileNum);
                    cost += (Math.abs(row - targetLocation.x) + Math.abs(col - targetLocation.y));
//                    cost += (Math.abs(row - targetLocation.x)*3 + Math.abs(col - targetLocation.y)*3.5);
                }
            }
        }
        return cost;
    }

    private double linearConflict(stateNode curr){
        int conflicts = 0, tileNum, tileNum_t;
        int[][] tiles = curr.getTiles();
        for (int row = 0; row < board.height(); row++) {
            for (int col = 0; col < board.width(); col++) {
                tileNum = tiles[row][col];
                Point targetLocation = board.tileLocation(tileNum);
                if(tileNum==-1) {
                    continue;
                }

                for(int row_t=0; row_t< board.height(); row_t++) {
                    tileNum_t = tiles[row_t][col];
                    if(tileNum_t==-1) {
                        continue;
                    }
                    Point targetLocation_t = board.tileLocation(tileNum);

                    boolean inRightCol = col == targetLocation_t.y;
                    if(inRightCol &&  targetLocation.x == targetLocation_t.x && row_t > row && targetLocation.y > targetLocation_t.y){
                        conflicts++;
                    }
                }

                for(int col_t=0; col_t< board.height(); col_t++) {
                    tileNum_t = tiles[row][col_t];
                    if(tileNum_t==-1) {
                        continue;
                    }
                    Point targetLocation_t = board.tileLocation(tileNum);

                    boolean inRightRow = row == targetLocation_t.x;
                    if(inRightRow &&  targetLocation.y == targetLocation_t.y && col_t > col && targetLocation.x > targetLocation_t.x){
                        conflicts++;
                    }
                }
            }
        }
//        return conflicts*3.5;
        return conflicts;
    }

    private List<stateNode> pathHandler(stateNode target){
        stateNode curr = target;
        ArrayList<stateNode> path = new ArrayList<>();

        while(curr.getPrev() != null){
            path.add(curr);
            curr = curr.getPrev();
        }

//        Collections.reverse(path);
        return path;
    }

    private void printOpenList(Object a){
        if(board.isOpen()){
            System.out.println(a);
        }
    }

    private void printPath(List<stateNode> path){

        for(stateNode state: path){
            System.out.print(" " + state.getLastOperation()+" ");
        }

        stateNode target = path.get(path.size() - 1);
        System.out.println("\ncost := " + target.getCost()+", depth:= " + target.getDepth() + ", nodes created := " + target.stateNodeNumber());
    }

    public double getAlgoTime() {
        return time;
    }
}
