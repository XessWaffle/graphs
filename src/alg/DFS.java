package alg;

import base.Edge;
import base.Node;
import graphic.GraphDisplay;

import java.util.Stack;

public class DFS extends Algorithm{

    public DFS(GraphDisplay g){
        super(g);
    }

    @Override
    public void operate() {

        super.operate();

        for(Node v: this.operate.getVertices()){

            if(!v.isVisited()) {
                Stack<Node> stack = new Stack<>();

                stack.add(v);

                while(!stack.isEmpty()){
                    Node next = stack.pop();

                    if(!next.visited()) {
                        step();
                        for(Edge e: next.getEdges()){
                            e.processing();
                            step();

                            stack.push(e.traverse());
                            e.getTo().processing();
                            step();
                        }
                    }
                }
            }
        }



    }
}
