package base;

public class Edge {

    protected Node from, to;
    protected double weight;
    protected boolean traversed;
    protected boolean processing;

    public Edge(Node from, Node to){
        this.from = from;
        this.to = to;

        this.weight = 1;
        traversed = false;
        processing = false;
    }

    public Edge(Node from, Node to, double weight){
        this.from = from;
        this.to = to;
        this.weight = weight;
        traversed = false;
        processing = false;
    }

    public Node traverse(){

        if(traversed){
            System.out.println("Retraversal of edge (" + from + ", " + to + ")");
        }

        traversed = true;
        return to;
    }

    public boolean traversed(){
        return traversed;
    }

    public boolean processing(){
        if(!processing){
            processing = true;
            return false;
        }

        return true;
    }

    public boolean isProcessing(){
        return processing;
    }

    public void resetEdge(){
        traversed = false;
        processing = false;
    }

    public void reverse(){
        Node temp = from;
        from = to;
        to = temp;
    }

    public Edge reversed(){
        return new Edge(to, from, weight);
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public double getWeight(){
        return weight;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public boolean equals(Object other){

        Edge o = (Edge) other;
            //System.out.println("EDGE " + o.from.equals(this.from) + ":" + o.to.equals(this.to));
        return o.from.getId() == this.from.getId() && o.to.getId() == this.to.getId();

    }

    public String toString(){
        return from.getId() + "->" + to.getId();
    }
}
