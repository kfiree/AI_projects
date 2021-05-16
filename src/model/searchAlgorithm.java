package model;

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
    private int nodesCtr = 0;
    private double time = 0;
//    Hashtable<String, stateNode> FrontierTable= new Hashtable<>(), Explored = new Hashtable<>();

    Result result;

    HashMap<Integer, Integer> duplicate = new HashMap<>();

    public searchAlgorithm(Board board) {
        this.board = board;
    }

    public List<stateNode> BFS(){
        System.out.println("running BFS algorithm...");
        stateNode start = this.board.getCurr();
        stateNode goal = this.board.getGoal();

/*
         BFS(Node start, Vector Goals)
         1. L  make_queue(start) and make_hash_table
         2. C  make_hash_table
         3. While L not empty loop
             1. n  L.remove_front()
             2. C  n
             3. For each allowed operator on n
                 1. g  operator(n)
                 2. If g not in C and not in L
                     1. If goal(g) return path(g)
                     2. L.insert(g)

         4. Return false
 */

        //  1. L  start
        Queue<stateNode> Frontier = new LinkedList<>();
        Frontier.add(start);
        Hashtable<String, stateNode> FrontierTable = new Hashtable<>();

        // 2.  C  make_hash_table
        Hashtable<String, stateNode> Explored = new Hashtable<>();

        //  3. While L not empty loop
        while (!Frontier.isEmpty()) {

            //  1.  n  L.remove_front()
            stateNode n = Frontier.poll();

            // 2. C  n
            Explored.put(n.key(), n);

            // 3. For each allowed operator on n
            ArrayList<stateNode> children = n.getChildren();
            for(stateNode operator :  children){
                // 1. g  operator(n)

                // 2. If g not in C and not in L
                if(!Explored.containsKey(operator.key()) && !FrontierTable.containsKey(operator.key())){
//                    System.out.println("DOPE ="+ ++nope+ ", node := "+ operator.toString());
//                    System.out.println(operator.toString());
                    // 1. If goal(g) return path(g)
                    if(operator.equals(goal)){
                        return pathHandler(operator) ;
                    }

                    // 2. L.insert(g)
                    Frontier.add(operator);
                    FrontierTable.put(operator.key(), operator);
                }
                else{

                }
            }

        }
        return null;
    }

    public List<stateNode> AStar(){
        System.out.println("running A* algorithm...");

        /*
             A*(Node start, Vector Goals)
                 1. L  make_priority_queue(start) and make_hash_table
                 2. C  make_hash_table
                 3. While L not empty loop
                     1. n  L.remove_front()
                     2. If goal(n) return path(n)
                     3. C  n
                     4. For each allowed operator on n
                         1. x  operator(n)
                         2. If x not in C and not in L
                            1. L.insert(x)
                         3. Else if x in L with higher path cost
                            1. Replace the node in L with x
                4. Return false
         */

        stateNode start = this.board.getCurr();
        stateNode goal = this.board.getGoal();

        // 1. L  make_priority_queue(start) and make_hash_table
        PriorityQueue<stateNode> L= new PriorityQueue<>();
        Hashtable<String, stateNode> LH = new Hashtable<>();

        L.add(start);
        LH.put(start.key(), start);

        // 2. C  make_hash_table
        Hashtable<String, stateNode> C = new Hashtable<>();


        // 3. While L not empty loop
        while(!L.isEmpty()){

            // 1. n  L.remove_front()
            stateNode currState = L.poll();
            LH.remove(currState.key());
//            LH.remove(currState.key());
            currState.Heuristic(setHeuristic(currState));

            // 2. If goal(n) return path(n)
            if(currState.equals(goal)){
                return pathHandler(currState);
            }
            int a[][] = {{1,3,4},
                        {-1,-1,6},
                        {2,5,7}};
            boolean b= true;
            for (int i = 0; i < currState.rowLen(); i++) {
                if (!Arrays.equals(a[i], currState.getTiles()[i])) {

                    b=false;
                }

            }
            if(b)
                System.out.println("");
            if(currState.getLastOperation()!= null)
                if(currState.getLastOperation().equals("3U") )
                    System.out.println("");

            if(currState.getId() == 31)
                System.out.println("");
            // 3. C  n
            C.put(currState.key(), currState);

            // 4. For each allowed operator on n
            ArrayList<stateNode> children = currState.getChildren();
            for(stateNode operator : children){
                // 1. x  operator(n)
                operator.Heuristic(setHeuristic(operator));

                // 2. If x not in C and not in L
                if(!C.containsKey(operator.key()) && !LH.containsKey(operator.key())){

                    // 1. L.insert(x)
                    L.add(operator);
                    LH.put(operator.key(), operator);

                // 3. Else if x in L with higher path cost
                }else if(LH.containsKey(operator.key())){
                    stateNode x = LH.get(operator.key());

                    // 1. Replace the node in L with xL
                    if( x.isGreaterThan(operator) ){
                        L.remove(x);
                        L.add(operator);
//                        LH.put(currState.key(), currState);
                    }
                }
            }
        }
        return null;
    }

    public List<stateNode> DFID(){
        System.out.println("running DFID algorithm...");

    /*
        DFID(Node start, Vector Goals)
            1. For depth=1 to ∞
                1. H  make_hash_table
                2. result  Limited_DFS(start,Goals,depth,H)
                3. If result ≠ cutoff then return result
     */

        stateNode start = this.board.getCurr();
        stateNode goal = this.board.getGoal();

        // 1. For depth=1 to ∞
        int depth = 1;
        while(depth<Integer.MAX_VALUE){
            depth++;

            // 1. H  make_hash_table
            Hashtable<String, stateNode> H = new Hashtable<>();

            // 2. result  Limited_DFS(start,Goals,depth,H)
            result = Limited_DFS(start, goal, depth, H);

            // 3. If result ≠ cutoff then return result
            if(result != Result.CUTOFF) {
                if(result == Result.GOAL){
                    return pathHandler(result.getGoal());
                }
            }
        }
        return null;
    }

    public Result Limited_DFS(stateNode curr, stateNode goal, int limit, Hashtable<String, stateNode> H){
        /*
        Limited_DFS(Node n, Vector Goals, int limit, hash H)
            1. If goal(n) then return path(n) //use the back pointers or the recursion tail
            2. Else if limit = 0 then return cutoff
            3. Else
                1. H.insert(n)
                2. isCutoff  false
                3. For each allowed operator on n
                    1. g  operator(n)
                    2. If H contains g
                        1. continue with the next operator
                    3. result  Limited_DFS(g,Goals,limit-1,H)
                    4. If result = cutoff
                        1. isCutoff  true
                    5. Else if result ≠ fail
                        1. return result

                4. H.remove(n) //the memory for n should be also released
                5. If isCutoff = true
                    1. return cutoff
                6. Else
                    1. return fail

         */

        //1. If goal(n) then return path(n) //use the back pointers or the recursion tail
        if(curr.equals(goal))
            return Result.GOAL.setGoal(curr);

        //2. Else if limit = 0 then return cutoff
        else if(limit == 0)
            return Result.CUTOFF;
        //3. Else
        else{
            // 1. H.insert(n)
            H.put(curr.key(), curr);

            // 2. isCutoff  false
            boolean isCutoff = false;

            // 3. For each allowed operator on n
            ArrayList<stateNode> children = curr.getChildren();
            for(stateNode state: children){
                // 1. g  operator(n)

                // 2. If H contains g
                if(H.containsKey(state.key())) {
                    // 1. continue with the next operator
                    continue;
                }

                // 3. result  Limited_DFS(g,Goals,limit-1,H)
                result = Limited_DFS(state, goal, limit-1, H);

                // 4. If result = cutoff
                if(result == Result.CUTOFF) {
                    // 1. isCutoff  true
                    isCutoff = true;
                // 5. Else if result ≠ fail
                }else if(result != Result.FAIL) {
                    // 1. return result
                    return result;
                }
            }

            // 4. H.remove(n) //the memory for n should be also released
            H.remove(curr);

            /*
             5. If isCutoff = true
                 1. return cutoff
             6. Else
                 1. return fail
            */
            return isCutoff? Result.CUTOFF: Result.FAIL;
        }
    }

    public List<stateNode> IDAStar(){
        System.out.println("running IDA* algorithm...");
    /*
     IDA*(Node start, Vector Goals)
         1. L  make_stack and H  make_hash_table
         2. t  h(start)
         3. While t ≠ ∞
             1. minF  ∞
             2. L.insert(start) and H.insert(start)
             3. While L is not empty
                 1. n  L.remove_front()
                 2. If n is marked as “out”
                     1. H.remove(n)
                 2. Else
                     2. mark n as “out” and L.insert(n)
                     3. For each allowed operator on n
                     4. g  operator(n)
                        1. If f(g) > t
                            1. minF  min(minF, f(g))
                            2. continue with the next operator
                        2. If H contains g’=g and g’ marked “out”
                            1. continue with the next operator
                        3. If H contains g’=g and g’ not marked “out”
                            1. If f(g’)>f(g)
                                1. remove g’ from L and H
                            2. Else
                                1. continue with the next operator
                        4. If goal(g) then return path(g) //all the “out” nodes in L
                        5. L.insert(g) and H.insert(g)
             4. t  minF
         4. Return false
     */

        stateNode start = this.board.getCurr();
        stateNode goal = this.board.getGoal();

        // 1. L  make_stack and H  make_hash_table
        Stack<stateNode> L = new Stack<>();
        Hashtable<String, stateNode> H = new Hashtable<>();

        // 2. t  h(start)
        start.Heuristic(setHeuristic(start));
        int minF, threshold = start.Heuristic();

        // 3. While t ≠ ∞
        while(threshold != Integer.MAX_VALUE) {
            // 1. minF  ∞
            minF = Integer.MAX_VALUE;

            // 2. L.insert(start) and H.insert(start)
            H.put(start.key(), start);
            L.push(start);

            // 3. While L is not empty
            while (!L.isEmpty()){

                // 1. n  L.remove_front()
                stateNode n = L.pop();
//                H.remove(pop.key()); //TODO ?

                // 2. If n is marked as “out”
                if(n.isOut()){
                    //1. H.remove(n)
                    H.remove(n.key());
                //2. Else
                }else {
                    //2. mark n as “out” and L.insert(n)
                    n.markOut(true);
                    L.push(n);

                    //3. For each allowed operator on n
                    ArrayList<stateNode> children = n.getChildren();
                    for (stateNode operator : children) {
                        operator.Heuristic(setHeuristic(operator));
                        // 1. If f(g) > t
                        if (operator.f() > threshold) {
                            //1. minF  min(minF, f(g))
                            minF = Math.min(minF, operator.f());
                            continue;
                            //2. continue with the next operator
                        }

                        stateNode g2 = H.get(operator.key());
//                        if (g2 != null) {

                        // 2. If H contains g’=g and g’ marked “out”
                        if (g2 != null && g2.isOut()) {
                            // 1. continue with the next operator
                            continue;
                        }

                        // 3. If H contains g’=g and g’ not marked “out”
                        if(g2 != null && !g2.isOut()){
                            g2.Heuristic(setHeuristic(g2));

                            // 1. If f(g’)>f(g)
                            if (g2.f() > operator.f()) {
                                // 1. remove g’ from L and H
                                H.remove(g2.key());
                                L.remove(g2);

                            // 2. Else
                            } else {
                                // 1. continue with the next operator
                                continue;
                            }
                        }
                        // 4. If goal(g) then return path(g) //all the “out” nodes in L
                        if (operator.equals(goal)) {
                            return pathHandler(operator);
                        }
                        // 5. L.insert(g) and H.insert(g)
                        L.push(operator);
                        H.put(operator.key(), operator);
                    }
                }
            }
            start.markOut(false);
            // 4. t  minF}
            threshold = minF;
        }
        // 4. Return false
        return null;
    }

    public List<stateNode> DFBnB(){
        System.out.println("running DFBnB algorithm...");
/*

DFBnB(Node start, Vector Goals)
    1. L  make_stack(start) and H  make_hash_table(start)
    2. result  null, t  ∞ // should be set to a strict upper bound in an infinite graph
    3. While L is not empty
        1. n  L.remove_front()
        2. If n is marked as “out”
            1. H.remove(n)
        3. Else
            1. mark n as “out” and L.insert(n)
            2. N  apply all of the allowed operators on n
            3. sort the nodes in N according to their f values (increasing order)
            4. For each node g from N according to the order of N
                1. If f(g) >= t
                    1. remove g and all the nodes after it from N
                2. Else If H contains g’=g and g’ is marked as “out”
                    1. remove g from N
                3. Else If H contains g’=g and g’ is not marked as “out”
                    1. If f(g’)<=f(g)
                        1. remove g from N
                    2. Else
                        1. remove g’ from L and from H
                4. Else If goal(g) // if we reached here, f(g) < t
                    1. t  f(g)
                    2. result  path(g) // all the “out” nodes in L
                    3. remove g and all the nodes after it from N
            5. insert N in a reverse order to L and H
        4. Return result
*/
        //set start and goal
        stateNode start = this.board.getCurr();
        stateNode goal = this.board.getGoal();

        // 1. L  make_stack(start) and H  make_hash_table(start)
        Stack<stateNode> L = new Stack<>();
        L.push(start);

        Hashtable<String, stateNode> H = new Hashtable<>();
        H.put(start.key(), start);

        // 2. result  null, t  ∞ // should be set to a strict upper bound in an infinite graph
        int t = Integer.MAX_VALUE;
        List<stateNode> result = null;

        // 3. While L is not empty
        while(!L.isEmpty()){
            // 1. n  L.remove_front()
            stateNode state = L.pop();

            //2. If n is marked as “out”
            if(state.isOut()) {
                //1. H.remove(n)
                H.remove(state.key());
            //3. Else
            }else{
                //1. mark n as “out” and L.insert(n)
                state.markOut(true);
                L.push(state);

                if(state.getId() == 14){
                    duplicate.put(0,0);
                }
                //2. N  apply all of the allowed operators on n
                ArrayList<stateNode> children = state.getChildren();

                // 3. sort the nodes in N according to their f values (increasing order)
                for(stateNode child :  children)
                    child.Heuristic(setHeuristic(child));
                Collections.sort(children);

                // 4. For each node g from N according to the order of N
                Iterator<stateNode> itr = children.iterator();
                while(itr.hasNext()){
                    stateNode child = itr.next();
                    // 1. If f(g) >= t
                    if(child.f() >= t){
                        // 1. remove g and all the nodes after it from N
                        itr.remove();
                        while(itr.hasNext()){
                            itr.next();
                            itr.remove();
                        }

                    // 2. Else If H contains g’=g and g’ is marked as “out”
                    }else if (H.containsKey(child.key()) && H.get(child.key()).isOut()){
                        // 1. remove g from N
                        itr.remove();
                    // 3. Else If H contains g’=g and g’ is not marked as “out”
                    }else if (H.containsKey(child.key()) && !H.get(child.key()).isOut()){
                        stateNode g2 = H.get(child.key());

                        // 1. If f(g’)<=f(g)  remove g from N
                        if(g2.f()<=child.f()) {
                            itr.remove();
                        }else {
                            // 1. remove g’ from L and from H
                            L.remove(g2);
                            H.remove(g2.key());
                        }
                    }
                    // 4. Else If goal(g) // if we reached here, f(g) < t
                    else if(child.equals(goal)){
                        // 1. t  f(g)
                        t = child.f();

                        // 2. result  path(g) // all the “out” nodes in L
                        result = pathHandler(child);

                        // 3. remove g and all the nodes after it from N
                        while(itr.hasNext()){
                            itr.next();
                            itr.remove();
                        }
                    }
                }

                //   5. insert N in a reverse order to L and H
                for (int i = children.size() - 1; i >= 0; i--) {
                    stateNode child = children.get(i);
                    L.push(child);
                    H.put(child.key(), child);
                }

            }

        }
        // 4. Return result
        printPath(result);
        return result;
    }

    private List<stateNode> pathHandler(stateNode target){
        stateNode curr = target;
        ArrayList<stateNode> path = new ArrayList<>();

        while(curr.getPrev() != null){
            path.add(curr);
            curr = curr.getPrev();
        }

        Collections.reverse(path);

//        printPath(path);
        return path;
    }

    private void printPath(List<stateNode> path){

        for(stateNode state: path){
            System.out.print(" " + state.getLastOperation()+" ");
//            state.printState();
        }

        stateNode target = path.get(path.size() - 1);
        System.out.println("\ncost := " + target.getCost()+", depth:= " + target.getDepth() + ", nodes created := " + target.stateNodeNumber());
    }

    private int setHeuristic(stateNode curr){
//        return manhattan(curr)*Operator.onePrice();
        return (manhattan(curr)+linearConflict(curr)*2)* Operator.onePrice();
    }

    private int manhattan(stateNode curr) {
        int cost = 0, tileNum;
        int[][] tiles = curr.getTiles();

        for (int row = 0; row < board.height(); row++) {
            for (int col = 0; col < board.width(); col++) {
                tileNum = tiles[row][col];
                if(tileNum!=-1){
                    Point targetLocation = board.tileLocation(tileNum);
                    cost += (Math.abs(row - targetLocation.x) + Math.abs(col - targetLocation.y));
                }
            }
        }
        return cost;
    }

    private int linearConflict(stateNode curr){
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
        return conflicts;
    }

    public int getNodesNumber() {
        return nodesCtr;
    }

    public double getAlgoTime() {
        return time;
    }
}
