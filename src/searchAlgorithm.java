import java.awt.*;
import java.util.*;

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
            LinkedHashMap<String, stateNode> children = n.getChildren(this);

            // 3. For each allowed operator on n
            for(Map.Entry<String, stateNode> entry :  children.entrySet()){
                // 1. g  operator(n)
                stateNode operator = entry.getValue();
//                System.out.println("----------- child "+ i++ +" ------------");
//                operator.printState();
                // 2. If g not in C and not in L
                if(!Explored.containsKey(operator.key()) && !FrontierTable.containsKey(operator.key())){
                    // 1. If goal(g) return path(g)
                    if(operator.equals(goal)){
                        System.out.println("found it. cost " + operator.getCost() + " path is....");
                        HandlePath(operator) ;
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
                HandlePath(currState);
                return;
            }

            // 3. C  n
            Explored.put(currState.key(), currState);

            // 4. For each allowed operator on n
            LinkedHashMap<String, stateNode> children = currState.getChildren(this);
            for(Map.Entry<String, stateNode> entry :  children.entrySet()){
                // 1. x  operator(n)
                stateNode operator = entry.getValue();
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
        System.out.println("IDA*");

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


    }

    public void DFBnB(){
        System.out.println("DFBnB");
    }

    private void HandlePath(stateNode pathEnd){
        stateNode curr = pathEnd;
        ArrayList<String> path = new ArrayList<>();

        while(curr.getPrev() != null){
            path.add(curr.getLastOperation());

            curr.printState();
            System.out.println("---"+curr.getLastOperation()+"---");
            curr=curr.getPrev();
        }

        curr.printState();
        System.out.println("---"+curr.getLastOperation()+"---");

        Collections.reverse(path);


//        for(int i=0; i<path.size();i++){
//            System.out.println(path.get(i)+"-");
//            nodesPath.get(i).printState();
//        }
//        for(String s: path){
//            stateNode node = Explored.get(s);
//            if(node!= null)
//                node.printState();
//            System.out.println(s+"-");
//        }
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
