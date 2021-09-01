package alg;

import base.Edge;
import base.Node;
import graphic.GraphDisplay;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class BFS extends Algorithm{

    public BFS(GraphDisplay g){
        super(g);
    }

    @Override
    public void operate() {

        super.operate();

        for(Node v: this.operate.getVertices()){

            if(!v.isVisited()) {
                Queue<Node> queue = new LinkedList<>();

                queue.add(v);

                while(!queue.isEmpty()){
                    Node next = queue.remove();

                    if(!next.visited()) {
                        step();
                        for(Edge e: next.getEdges()){
                            e.processing();
                            step();

                            queue.add(e.traverse());
                            e.getTo().processing();
                            step();
                        }
                    }
                }
            }
        }



    }
}
