import java.awt.*;
import java.util.*;
import java.util.List;

public class searchAlgorithm {

    Board board;
    Hashtable<String, stateNode> FrontierTable= new Hashtable<>(), Explored = new Hashtable<>();

    public searchAlgorithm(Board board) {
        this.board = board;
    }

    public void BFS(){
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
        LinkedList<stateNode> Frontier = new LinkedList<>();
        Frontier.add(start);
        Hashtable<String, stateNode> FrontierTable = new Hashtable<>();

        // 2.  C  make_hash_table
        Hashtable<String, stateNode> Explored = new Hashtable<>();

        //  3. While L not empty loop
        while (!Frontier.isEmpty()) {

            //  1.  n  L.remove_front()
            stateNode n = Frontier.remove();
//            System.out.println("++++++++++++++++ FATHER ++++++++++++++++");
//            n.printState();

            // 2. C  n
            Explored.put(n.key(), n);
            ArrayList<stateNode> children = n.getChildren();

            // 3. For each allowed operator on n
            for(stateNode operator :  children){
                // 1. g  operator(n)

//                System.out.println("----------- child "+ i++ +" ------------");
//                operator.printState();
                // 2. If g not in C and not in L
                if(!Explored.containsKey(operator.key()) && !FrontierTable.containsKey(operator.key())){
                    // 1. If goal(g) return path(g)
                    if(operator.equals(goal)){
                        System.out.println("found it. cost " + operator.getCost() + " path is....");
                        pathHandler(operator) ;
                        return;
                    }

                    // 2. L.insert(g)
                    Frontier.add(operator);
                    FrontierTable.put(operator.key(), operator);
                }
            }

        }

    }

    public void AStar(){
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
        // 2. C  make_hash_table
        Hashtable<String, stateNode> Explored = new Hashtable<>();

        PriorityQueue<stateNode> Frontier= new PriorityQueue<>();
        Hashtable<String, stateNode> FrontierTable = new Hashtable<>();
        Frontier.add(start);
        FrontierTable.put(start.toString(), start);

        // 3. While L not empty loop
        while(!Frontier.isEmpty()){

            // 1. n  L.remove_front()
            stateNode currState = Frontier.poll();
            currState.Heuristic(manhattan(currState));

            // 2. If goal(n) return path(n)
            if(currState.equals(goal)){
                pathHandler(currState);
                return;
            }

            // 3. C  n
            Explored.put(currState.key(), currState);

            // 4. For each allowed operator on n
            for(stateNode operator :  currState.getChildren()){
                // 1. x  operator(n)
                currState.Heuristic(manhattan(currState));

                // 2. If x not in C and not in L
                if(!Explored.containsKey(operator.key()) && !FrontierTable.containsKey(operator.key())){

                    // 1. L.insert(x)
                    Frontier.add(currState);
                    FrontierTable.put(currState.key(), currState);

                    // 3. Else if x in L with higher path cost
                }else if(FrontierTable.containsKey(currState.key())){
                    // 1. Replace the node in L with xL
                    if( currState.isGreaterThan( FrontierTable.get(currState.key()) ) ){
                        Frontier.remove(currState);
                        Frontier.add(currState);
                    }
                }
            }
        }
    }

    public void DFID(){
        System.out.println("DFID");
    }

    public void IDAStar(){
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
        start.Heuristic(manhattan(start));
        int minF, threshold = start.Heuristic();

        // 3. While t ≠ ∞
        while(threshold != Integer.MAX_VALUE) {
            // 1. minF  ∞
            minF = Integer.MAX_VALUE;
            // 2. L.insert(start) and H.insert(start)
            H.put(start.key(), start);
            L.add(start);
            // 3. While L is not empty
            while (!L.isEmpty()){
                // 1. n  L.remove_front()
                stateNode pop = L.pop();
                H.remove(pop.key());

                // 2. If n is marked as “out”
                if(pop.isOut()){
                    //1. H.remove(n)
                    H.remove(pop.key());
                //2. Else
                }else {
                    //2. mark n as “out” and L.insert(n)
                    pop.markOut();
                    H.put(pop.key(), pop);
                    //3. For each allowed operator on n
                    for (stateNode operator : pop.getChildren()) {
                        // 1. If f(g) > t
                        int f = operator.Heuristic() + operator.getCost();
                        if (f > threshold) {
                            //1. minF  min(minF, f(g))
                            minF = Math.min(minF, f);

                            //2. continue with the next operator
                        } else {
                            // 2. If H contains g’=g and g’ marked “out”
                            stateNode g2 = H.get(operator.key());
                            if (g2 != null) {
                                if (!g2.isOut()) {
                                    // 1. continue with the next operator
                                    continue;
                                // 3. If H contains g’=g and g’ not marked “out”
                                } else {
                                    int f2 = g2.Heuristic() + g2.getCost();

                                    // 1. If f(g’)>f(g)
                                    if (f2 > f) {
                                        // 1. remove g’ from L and H
                                        H.remove(g2.key());
                                        // 2. Else
                                    } else {
                                        // 1. continue with the next operator
                                        continue;
                                    }
                                }
                                // 4. If goal(g) then return path(g) //all the “out” nodes in L
                                if (operator.equals(goal)) {
                                    this.pathHandler(operator);
                                    return;
                                }
                                // 5. L.insert(g) and H.insert(g)
                                L.add(operator);
                                H.put(operator.key(), operator);
                            }
                        }

                    }
                }
            // 4. t  minF}
            threshold = minF;
            }
        // 4. Return false
        }
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
        L.add(start);

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
                state.markOut();
                L.push(state);

                //2. N  apply all of the allowed operators on n
                ArrayList<stateNode> children = state.getChildren();

                // 3. sort the nodes in N according to their f values (increasing order)
                for(stateNode child :  children)
                    child.Heuristic(manhattan(child));

                Collections.sort(children);

                // 4. For each node g from N according to the order of N
                Iterator<stateNode> itr = children.iterator();
                while(itr.hasNext()){
                    stateNode child = itr.next();
                    // 1. If f(g) >= t
                    if(child.f() >= t){
                        // 1. remove g and all the nodes after it from N
                        children.subList(children.indexOf(child), children.size()).clear();

                    // 2. Else If H contains g’=g and g’ is marked as “out”
                    }else{
                        stateNode g2 = H.get(child.key());
                        if(g2!=null){
                            if(g2.isOut()){
                                // 1. remove g from N
                                children.remove(g2);
                            // 3. Else If H contains g’=g and g’ is not marked as “out”
                            }else{
                                // 1. If f(g’)<=f(g)  remove g from N
                                if(g2.f()<=child.f()) {
                                    L.remove(child);
                                }else {
                                    L.remove(g2);
                                    H.remove(g2.key());
                                }
                            }
                            // 4. Else If goal(g) // if we reached here, f(g) < t
                            if(child.equals(goal)){
                                // 1. t  f(g)
                                t = child.f();

                                // 2. result  path(g) // all the “out” nodes in L
                                result = pathHandler(child);

                                // 3. remove g and all the nodes after it from N
                                children.subList(children.indexOf(child), children.size()).clear();
                            }

                        }
                    }
                }
                //   5. insert N in a reverse order to L and H
                for (int i = children.size() - 1; i >= 0; i--) {
                    stateNode child = children.get(i);
                    L.add(child);
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
        }

        Collections.reverse(path);

//        printPath(path);
        return path;
    }

    private void printPath(List<stateNode> path){
        System.out.println("--- START ---");
        for(stateNode state: path){
            System.out.println("---"+state.getLastOperation()+"---");
            state.printState();
        }
        System.out.println("--- FINISHED ---");
        stateNode target = path.get(path.size() - 1);
        System.out.println("cost := " + target.getCost()+", depth:= " + target.getDepth());
    }

    private int manhattan(stateNode curr) {
        int cost = 0, tileNum;
        int[][] tiles = curr.getTiles();

        for (int row = 0; row < board.height(); row++) {
            for (int col = 0; col < board.width(); col++) {
                tileNum = tiles[row][col];
                Point targetLocation = board.tileLocation(tileNum);

                cost += Math.abs(row - targetLocation.x) + Math.abs(col - targetLocation.y);
            }
        }
        return cost;
    }

    public Hashtable<String, stateNode> getFrontier() {
        return FrontierTable;
    }

    public Hashtable<String, stateNode> getExplored() {
        return Explored;
    }

}
