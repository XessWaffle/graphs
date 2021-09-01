package util;

import base.Edge;
import base.Graph;
import base.Node;

public class GraphCreator {

    public static Graph generateGraph(int vertices, int maxDegree, boolean directed, boolean forceConnected, double maxVertexWeight, double maxEdgeWeight, double density){
        Graph g = new Graph();
        g.setDirected(directed);

        int maxEdges = (int)(vertices * (vertices - 1)/2.0);
        int edges = forceConnected && (maxEdges * density * density) < (vertices - 1) ? vertices - 1 : (int) (maxEdges * density * density);

        if(forceConnected){
            Node root = maxVertexWeight != 1 ? new Node(0, (2 * (Math.random() - 0.5) * maxVertexWeight)) : new Node(0);
            g.addVertex(root);
            developSpanningTree(root, maxDegree, vertices, maxVertexWeight, maxEdgeWeight, directed, g);

            edges -= (vertices - 1);
        } else {
            for(int i = 0; i < vertices; i++){
                Node next = maxVertexWeight!= 1 ? new Node(i, (2 * (Math.random() - 0.5) * maxVertexWeight)) : new Node(i);
                g.addVertex(next);
            }
        }

        Node one = null, two = null;

        while(edges > 0){
            one = g.randVertex();
            two = g.randVertex();

            while(one == null){
                one = g.randVertex();
            }

            while(two == null || one.equals(two)){
                two = g.randVertex();
            }

            //System.out.println(one + ":" + two);

            Edge rootEdge = maxEdgeWeight != 1 ? new Edge(one, two, 2 * (Math.random() - 0.5) * maxVertexWeight) : new Edge(one, two);

            while(one.containsEdge(rootEdge) || rootEdge.getTo() == null || rootEdge.getTo().equals(one)){
                rootEdge.setTo(g.randVertex());
            }

            one.addEdge(rootEdge);

            if(!directed){
                two.addEdge(rootEdge.reversed());
            }

            edges--;
        }

        g.componentize();

        return g;

    }
    public static Graph generateGraphCycle(int vertices, int maxDegree, boolean directed, boolean forceConnected, double maxVertexWeight, double maxEdgeWeight, int cycles){
        Graph g = new Graph();
        g.setDirected(directed);

        int maxEdges = (vertices - 1) * vertices / 2;
        int edges = vertices - 1 + cycles > maxEdges ? maxEdges : vertices - 1 + cycles;

        if(forceConnected){
            Node root = maxVertexWeight != 1 ? new Node(0, (2 * (Math.random() - 0.5) * maxVertexWeight)) : new Node(0);
            g.addVertex(root);
            developSpanningTree(root, maxDegree, vertices, maxVertexWeight, maxEdgeWeight, directed, g);

            edges -= (vertices - 1);
        } else {
            for(int i = 0; i < vertices; i++){
                Node next = maxVertexWeight!= 1 ? new Node(i, (2 * (Math.random() - 0.5) * maxVertexWeight)) : new Node(i);
                g.addVertex(next);
            }
        }

        Node one = null, two = null;

        while(edges > 0){
            one = g.randVertex();
            two = g.randVertex();

            while(one == null){
                one = g.randVertex();
            }

            while(two == null || one.equals(two)){
                two = g.randVertex();
            }

            //System.out.println(one + ":" + two);

            Edge rootEdge = maxEdgeWeight != 1 ? new Edge(one, two, 2 * (Math.random() - 0.5) * maxVertexWeight) : new Edge(one, two);

            while(one.containsEdge(rootEdge) || rootEdge.getTo() == null || rootEdge.getTo().equals(one) || two.containsEdge(new Edge(two, one))){
                two = g.randVertex();
                rootEdge.setTo(two);
            }

            one.addEdge(rootEdge);

            if(!directed){
                two.addEdge(rootEdge.reversed());
            }

            edges--;
        }

        g.componentize();

        return g;

    }


    private static void developSpanningTree(Node root, int maxDegree, int vertices, double maxVertexWeight, double maxEdgeWeight, boolean directed, Graph g) {

        if(g.getSize() >= vertices){
            return;
        }

        int rootDegree = (int)(Math.random() * (maxDegree - 1)) + 1;

        for(int i = 0; i < rootDegree; i++){
            if(root.degree() < maxDegree) {
                Node next = maxVertexWeight != 1 ? new Node(g.getSize(), (2 * (Math.random() - 0.5) * maxVertexWeight)) : new Node(g.getSize());
                Edge rootEdge = maxEdgeWeight != 1 ? new Edge(root, next, 2 * (Math.random() - 0.5) * maxVertexWeight) : new Edge(root, next);

                root.addEdge(rootEdge);

                if (!directed) {
                    next.addEdge(rootEdge.reversed());
                }

                g.addVertex(next);

                //System.out.println("Added Vertex: " + next);

                if (g.getSize() >= vertices) {
                    return;
                }

                if (Math.random() > 0.8) {
                    developSpanningTree(next, maxDegree, vertices, maxVertexWeight, maxEdgeWeight, directed, g);
                }
            } else {
                break;
            }

        }

        if(g.getSize() < vertices){
            Node expand = null;
            do{
                expand = g.randVertex();
            }while(expand == null);

            developSpanningTree(expand, maxDegree, vertices, maxVertexWeight, maxEdgeWeight, directed, g);
        }


    }
}
