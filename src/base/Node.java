package base;

import java.util.HashSet;

public class Node {

    private int id;
    private double weight;
    private boolean visited;
    private boolean processing;

    private Component comp;
    private Graph ref;

    private HashSet<Edge> outgoing;

    public Node(int id){
        this.id = id;
        this.weight = 1;
        this.visited = false;
        this.processing = false;
        outgoing = new HashSet<>();
    }

    public Node(int id, double weight){
        this.id = id;
        this.weight = weight;
        this.visited = false;
        this.processing = false;
        outgoing = new HashSet<>();
    }

    public int getId(){
        return id;
    }

    public boolean equals(Object other){
        if(other instanceof Node){
            return ((Node) other).getId() == this.getId();
        }

        return false;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void addEdge(Edge e){
        outgoing.add(e);
    }

    public void removeEdge(Edge e){
        outgoing.remove(e);
    }

    public boolean containsEdge(Edge e){
        for(Edge check: outgoing){
            if(check.equals(e)){
                return true;
            }
        }

        return false;
    }

    public HashSet<Edge> getEdges(){
        return outgoing;
    }

    public Component getComponent(){
        return comp;
    }

    public void setComponent(Component comp){
        this.comp = comp;
    }

    public void setGraph(Graph ref){
        this.ref = ref;
    }

    public Graph getGraph(){
        return ref;
    }

    public boolean visited(){
        if(!visited){
            visited = true;
            return false;
        }

        return true;
    }

    public boolean processing(){
        if(!processing){
            processing = true;
            return false;
        }

        return true;
    }

    public boolean isVisited() {
        return visited;
    }

    public boolean isProcessing(){
        return processing;
    }

    public void resetNode(){
        visited = false;
        processing = false;

        for(Edge e:outgoing){
            e.resetEdge();
        }
    }

    public int degree(){
        return outgoing.size();
    }

    public String toString(){

        String ret = "Node " + id + ": (" + weight + ", [";

        for(Edge e : getEdges()){
            ret += e.toString();
            ret += ", ";
        }

        return  ret  + "])";
    }
}
