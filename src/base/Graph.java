package base;

import java.util.*;

public class Graph {
    private HashSet<Node> vertices;
    private HashSet<Component> components;
    private boolean directed;

    private UUID uuid;

    public Graph() {
        vertices = new HashSet<>();
        components = new HashSet<>();
        uuid = UUID.randomUUID();
    }

    public void addVertex(Node node){
        if (!vertices.contains(node)) {
            vertices.add(node);
            node.setGraph(this);
        }
    }

    public void removeNode(Node node){
        vertices.remove(node);
    }

    public HashSet<Node> getVertices() {
        return vertices;
    }

    public void setDirected(boolean directed){
        this.directed = directed;
    }

    public boolean isDirected(){
        return directed;
    }

    public int getSize(){
        return vertices.size();
    }

    public int getEdges(){
        int sum = 0;

        for(Node n : vertices){
            sum += n.degree();
        }

        if(isDirected()){
            return sum;
        } else {
            return sum/2;
        }
    }

    public Node randVertex() {

        int ind = (int) (Math.random() * getSize()), i = 0;

        for (Node n : vertices) {
            if (i == ind) {
                return n;
            }
            i++;
        }

        return null;

    }

    public void componentize(){

        HashMap<Node, Component> mapping = new HashMap<>();

        for(Node v: this.getVertices()){
            mapping.put(v, new Component(v.getId()));
        }

        int i = 1;

        for(Node v: this.getVertices()){

            Component current = mapping.get(v);

            if(!v.isVisited()) {
                Queue<Node> queue = new LinkedList<>();

                queue.add(v);

                while(!queue.isEmpty()){
                    Node next = queue.remove();

                    current.addNode(next);

                    if(mapping.get(next).getComponentId() != current.getComponentId()) {
                        current.merge(mapping.get(next));

                        mapping.put(next, current);
                    }



                    if(!next.visited()) {
                        for(Edge e: next.getEdges()){
                            queue.add(e.traverse());
                        }
                    }
                }
            }

            i++;

        }

        for(Node v: this.getVertices()){
            System.out.println(v.toString() + " " + mapping.get(v).getComponentId());
        }

        for(Node key: mapping.keySet()){
            components.add(mapping.get(key));
        }

    }

    public void prepareTraversal(){
        for(Node n: vertices){
            n.resetNode();
        }
    }

    public void printGraph(){

        System.out.println("G = (V[" + getSize() + "], E[" + getEdges() + "])");

        for(Node n: vertices){
            System.out.println(n);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public HashSet<Component> getComponents() {
        return components;
    }

    public void setComponents(HashSet<Component> components) {
        this.components = components;
    }
}
