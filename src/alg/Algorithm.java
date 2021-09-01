package alg;

import base.Graph;
import graphic.GraphDisplay;

public abstract class Algorithm {

    protected Graph operate;
    protected GraphDisplay display;

    protected long delay, start, end;

    protected boolean complete;

    public Algorithm(GraphDisplay display){
        this.display = display;
        this.operate = display.getGraph();
        this.delay = 1000;
        complete = false;
    }

    public Graph getOperate() {
        return operate;
    }

    public void setOperate(Graph operate) {
        this.operate = operate;
    }

    public GraphDisplay getDisplay() {
        return display;
    }

    public void setDisplay(GraphDisplay display) {
        this.display = display;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean isComplete() {
        return complete;
    }

    protected void complete(){
        this.complete = true;
    }

    public void operate(){
        operate.prepareTraversal();
    }


    public void step(){

        start = System.currentTimeMillis();

        while(end - start < delay) {
            display.update();
            display.repaint();

            try {
                Thread.sleep(5);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }

            end = System.currentTimeMillis();
        }

    }
}
