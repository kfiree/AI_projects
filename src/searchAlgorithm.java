import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class searchAlgorithm {

    Board board;
    int cost = 0;

    public searchAlgorithm(Board board) {
        this.board = board;
    }

    /**
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
     **/
    public void BFS(stateNode start, stateNode target){
        System.out.println("running BFS algorithm...");

        //   L  make_queue(start) and make_hash_table
        LinkedList<stateNode> L = new LinkedList<>();
        L.add(start);

        //   C  make_hash_table
        Hashtable<String, stateNode> C = new Hashtable<>(); //TODO check if hashMap possible

        //  For each allowed operator on n
        while (!L.isEmpty()) {

//            1. g  operator(n)
//            2. If g not in C and not in L
//                1. If goal(g) return path(g)
//                2. L.insert(g)

            //  g  operator(n)
            stateNode n = L.removeFirst();

            C.put(n.toString(), n);

//            Method m = node.class.getDeclaredMethod("allowedOperators");
//            List<node> allowed = (List<node>) m.invoke(n);
//            for (node node : allowed) {
//                counter++;
//                if (!C.containsKey(node.toString()) && !l.containsKey(node.toString())) {
//                    if (node.equals(goal)) {
//                        System.out.println("Num:  " + counter);
//                        return getPath(node);
//                    } else {
//                        L.add(node);
//                        l.put(node.toString(), node);
//                    }
//                }
//            }
        }

    }

    public void DFID(){
        System.out.println("DFID");
    }
    public void AStar(){
        System.out.println("A*");
    }
    public void IDAStar(){
        System.out.println("IDA*");
    }
    public void DFBnB(){
        System.out.println("DFBnB");
    }

}
