package main;

import base.Graph;
import util.GraphCreator;

public class GraphTester {
    public static void main(String[] args){
        Graph g = GraphCreator.generateGraph(1000, 2, true, true,1, 1, 0);
        g.printGraph();
    }
}
