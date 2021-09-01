package graphic;

import base.Edge;
import base.Graph;
import base.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import base.Component;
import java.util.UUID;

public class GraphDisplay extends JComponent {

    private static final int RADIUS = 10;
    private static final int DIAMETER = 2 * RADIUS;
    private static final int ARROW_MAG = 3;
    private static final int MULTIPLIER = 55;
    
    private static final int PIXELS_PER_WEIGHT = 10;
    private static final int NODE_WEIGHT = 600;
    private static final int MAG_CAP = 1000;
    private static final double DAMPING = 0.09;

    private static final double STEP = 0.17;
    private static final double ZOOM_CONSTANT = 1.5;

    private static final Color EDGE_PROCESSING_COLOR = Color.RED;
    private static final Color EDGE_TRAVERSED_COLOR = Color.BLUE;
    private static final Color EDGE_NEUTRAL_COLOR = Color.BLACK;

    private static final Color NODE_PROCESSING_COLOR = Color.RED;
    private static final Color NODE_VISITED_COLOR = Color.GREEN;
    private static final Color NODE_NEUTRAL_COLOR = Color.BLACK;

    private Graph draw;
    private HashSet<EdgeDisplay> edges;
    private HashSet<NodeDisplay> nodes;
    private HashSet<NodeSpacer> nodeSpacers;
    private HashSet<ComponentDisplay> components;
    private HashSet<ComponentSpacer> componentSpacers;

    private GraphInteraction mouse;

    private double height, width, offsetX, offsetY, maxX, minX, maxY, minY, zoom = 1.0;

    public GraphDisplay(Graph draw){
        this.draw = draw;
        this.edges = new HashSet<>();
        this.nodes = new HashSet<>();
        this.nodeSpacers = new HashSet<>();
        this.components = new HashSet<>();
        this.componentSpacers = new HashSet<>();

        this.width = getWidth();
        this.height = getHeight();

        this.offsetX = 0;
        this.offsetY = 0;

        prepDraw();

        mouse = new GraphInteraction();

        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);

    }

    public void update(){

        for(ComponentDisplay cd: this.components){

        }


        for(NodeDisplay nd: this.nodes){
            HashSet<EdgeDisplay> edges = getEdgesFromNode(nd.node);
            HashSet<NodeSpacer> spacers = getSpacersFromNode(nd.node);

            double unitXSum = 0, unitYSum = 0;

            for(EdgeDisplay ed: edges){
                if(ed.length > 10) {
                    unitXSum += draw.isDirected() ? ed.unitX * ed.length * PIXELS_PER_WEIGHT : ed.unitX * ed.length * PIXELS_PER_WEIGHT / 2;
                    unitYSum += draw.isDirected() ? ed.unitY * ed.length * PIXELS_PER_WEIGHT : ed.unitY * ed.length * PIXELS_PER_WEIGHT / 2;
                }
            }

            for(NodeSpacer ns: spacers){

                double distance = ns.getDistance();

                if(distance > 10) {

                    unitXSum += -1 * ns.unitX * (NODE_WEIGHT / (distance)) * (NODE_WEIGHT / (distance));
                    unitYSum += -1 * ns.unitY * (NODE_WEIGHT / (distance)) * (NODE_WEIGHT / (distance));
                }
            }

            double sqrt = Math.sqrt(unitXSum * unitXSum + unitYSum * unitYSum);
            double mag = sqrt > MAG_CAP ? MAG_CAP : sqrt;

            if(unitXSum != 0){
                unitXSum /= mag;
            }

            if(unitYSum != 0){
                unitYSum /= mag;
            }

            //System.out.println(mag);

            //System.out.println("B(" + nd.xPos + "," + nd.yPos + ")");

            nd.xVel += unitXSum * STEP;
            nd.yVel += unitYSum * STEP;

            mag = Math.sqrt(nd.xVel * nd.xVel + nd.yVel * nd.yVel);

            nd.xVel -= nd.xVel/mag * DAMPING;
            nd.yVel -= nd.yVel/mag * DAMPING;

            nd.xPos += nd.xVel * STEP;
            nd.yPos += nd.yVel * STEP;


            //System.out.println("(" + nd.xPos + "," + nd.yPos + ") (" + unitXSum + "," + unitYSum + ")");


            //System.out.println("A(" + nd.xPos + "," + nd.yPos + ")\n");

        }

        for(EdgeDisplay ed: this.edges){
            ed.updateUnitVector();
        }

        for(NodeDisplay nd: this.nodes){
            nd.updateProps();
        }

    }

    public void setGraph(Graph g){
        draw = g;
        prepDraw();
   }

    private HashSet<NodeSpacer> getSpacersFromNode(Node node) {

        HashSet<NodeSpacer> checked = new HashSet<>();

        for(NodeSpacer spacer: nodeSpacers){
            if(spacer.one.node.equals(node)) {
                checked.add(spacer);
            }
        }

        return checked;
    }

    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        this.height = this.getHeight();
        this.width = this.getWidth();

        this.maxX = -9999;
        this.minX = 9999;
        this.maxY = -9999;
        this.minY = 9999;
        this.zoom = 0;

        for(NodeDisplay nd: this.nodes){
            offsetX += nd.xPos;
            offsetY += nd.yPos;

            if(nd.xPos > maxX){
                maxX = nd.xPos;
            }

            if(nd.xPos < minX){
                minX = nd.xPos;
            }

            if(nd.yPos > maxY){
                maxY = nd.yPos;
            }

            if(nd.yPos < minY){
                minY = nd.yPos;
            }
        }

        offsetX /= this.nodes.size();
        offsetY /= this.nodes.size();

        double xDist = maxX - minX;
        double yDist = maxY - minY;

        zoom = Math.min(this.width/xDist, this.height/yDist)/ZOOM_CONSTANT;

        paintMetrics(g2d);

        for(EdgeDisplay ed: this.edges){
            paintEdge(ed, g2d);
        }

        for(NodeDisplay nd: this.nodes){
            paintNode(nd, g2d);
        }

    }

    private void paintMetrics(Graphics2D g2d) {
        Rectangle rect = new Rectangle(0, 0, this.getWidth(), this.getHeight()/15);

        String write = "Graph " + draw.getUuid() + " = (" + draw.getSize() + ", " + draw.getEdges() + ")";

        drawCenteredString(g2d, write, rect, new Font("Courier", Font.PLAIN, 18));

        int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
        int y = (int) MouseInfo.getPointerInfo().getLocation().getY();

        if(mouse.tracked == null) {

            for (NodeDisplay nd : nodes) {
                boolean inRange = Math.sqrt((x - nd.getScreenCenterX()) * (x - nd.getScreenCenterX())
                        + (y - nd.getScreenCenterY()) * (y - nd.getScreenCenterY())) < nd.getScreenRadius();
                if (inRange) {
                    write = String.format("(%.2f, %.2f, %.2f, %.2f)", nd.xPos, nd.yPos, nd.xVel, nd.yVel);
                    rect = new Rectangle(0, this.getHeight()/15, this.getWidth(), this.getHeight() / 15);
                    drawCenteredString(g2d, write, rect, new Font("Courier", Font.PLAIN, 18));
                    break;
                }

            }
        } else {
            write = String.format("(%.2f, %.2f, %.2f, %.2f)", mouse.tracked.xPos,  mouse.tracked.yPos, mouse.tracked.xVel, mouse.tracked.yVel);
            rect = new Rectangle(0, this.getHeight()/15, this.getWidth(), this.getHeight() / 15);
            drawCenteredString(g2d, write, rect, new Font("Courier", Font.PLAIN, 18));
        }


    }

    public void paintNode(NodeDisplay nd, Graphics2D g2d){
        int cirX = nd.getScreenX();
        int cirY = nd.getScreenY();
        //System.out.println("(" + nd.xPos + "," + nd.yPos + ")");

        int trueDiameter = nd.getScreenRadius() * 2;

        g2d.setColor(nd.nodeColor.darker());
        g2d.fillOval((int)(cirX), (int)(cirY), trueDiameter, trueDiameter);
        g2d.setColor(nd.nodeColor);
        g2d.drawOval((int)(cirX), (int)(cirY), trueDiameter, trueDiameter);

    }

    public void paintEdge(EdgeDisplay e, Graphics2D g2d){
        if(e.visible) {
            g2d.setColor(e.edgeColor);

            double startTwoX = e.getTwoScreenX() - DIAMETER * zoom * e.unitX;
            double startTwoY = e.getTwoScreenY() - DIAMETER * zoom * e.unitY;

            double startOneX = e.getOneScreenX() + DIAMETER * zoom * e.unitX;
            double startOneY = e.getOneScreenY() + DIAMETER * zoom * e.unitY;

            double endX = e.getTwoScreenX() - RADIUS * zoom * e.unitX;
            double endY = e.getTwoScreenY() - RADIUS * zoom * e.unitY;

            double topX = startTwoX - ARROW_MAG * e.perpX * zoom;
            double topY = startTwoY - ARROW_MAG * e.perpY * zoom;

            double bottomX = startTwoX + ARROW_MAG * e.perpX * zoom;
            double bottomY = startTwoY + ARROW_MAG * e.perpY * zoom;

            if(draw.isDirected()) {
                g2d.drawLine(e.getOneScreenX(), e.getOneScreenY(), (int) startTwoX, (int) startTwoY);
            } else {
                g2d.drawLine((int) startOneX, (int) startOneY, (int) startTwoX, (int) startTwoY);
            }

            g2d.drawLine((int) topX, (int) topY, (int) endX, (int) endY);
            g2d.drawLine((int) bottomX, (int) bottomY, (int) endX, (int) endY);
            g2d.drawLine((int) topX, (int) topY, (int) bottomX, (int) bottomY);

        }

    }

    public void drawCenteredString(Graphics2D g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    private void prepDraw() {
        this.edges = new HashSet<>();
        this.nodes = new HashSet<>();
        this.nodeSpacers = new HashSet<>();
        this.components = new HashSet<>();
        this.componentSpacers = new HashSet<>();

        for(Node n: draw.getVertices()){

            NodeDisplay toAdd = new NodeDisplay(n);

            for(Edge e: n.getEdges()){
                EdgeDisplay next = new EdgeDisplay(e);
                next.visible = true;
                next.one = toAdd;
                this.edges.add(next);
            }

            this.nodes.add(toAdd);
        }

        HashSet<EdgeDisplay> reversed = new HashSet<>();

        for(EdgeDisplay ed: this.edges){
            ed.two = getNodeFromNode(ed.edge.getTo());
            EdgeDisplay rev = new EdgeDisplay(new Edge(ed.two.node, ed.one.node));
            rev.one = ed.two;
            rev.two = ed.one;
            rev.updateUnitVector();
            ed.updateUnitVector();
            rev.visible = false;
            reversed.add(rev);
        }

        if(draw.isDirected())
            this.edges.addAll(reversed);

        for(Node x: this.draw.getVertices()){
            for(Node y: this.draw.getVertices()) {
                if (!x.equals(y)){
                    NodeSpacer spacer = new NodeSpacer();
                    spacer.one = getNodeFromNode(x);
                    spacer.two = getNodeFromNode(y);
                    nodeSpacers.add(spacer);
                }
            }
        }

        for(Component disp: this.draw.getComponents()){
            ComponentDisplay cd = new ComponentDisplay(disp);
            this.components.add(cd);
        }

        for(ComponentDisplay x: this.components){
            for(ComponentDisplay y: this.components){
                if(x != y) {
                    ComponentSpacer cs = new ComponentSpacer(x, y);
                    this.componentSpacers.add(cs);
                }
            }
        }

    }


    private NodeDisplay getNodeFromNode(Node node){
        for(NodeDisplay nd: this.nodes){
            if(nd.node.equals(node)) {
                return nd;
            }
        }

        return null;
    }

    private HashSet<EdgeDisplay> getEdgesFromNode(Node node){

        HashSet<EdgeDisplay> edgeDisplays = new HashSet<>();

        for(EdgeDisplay e: this.edges){
            if(e.edge.getFrom().equals(node)){
                edgeDisplays.add(e);
            }
        }

        return edgeDisplays;
    }

    public Graph getGraph() {
        return draw;
    }

    class NodeDisplay{

        public Node node;
        public double xPos, yPos;
        public double xVel, yVel;
        public Color nodeColor;

        public boolean fixed;

        public NodeDisplay(Node node){
            this.node = node;
            this.xPos = (2 * (Math.random() - 0.5)) * MULTIPLIER;
            this.yPos = (2 * (Math.random() - 0.5)) * MULTIPLIER;
            this.xVel = 0;
            this.yVel = 0;
            this.nodeColor = NODE_NEUTRAL_COLOR;
            this.fixed = false;
        }

        public int getScreenX(){
            return (int)((xPos - RADIUS - offsetX) * zoom + width/2);
        }

        public int getScreenY(){
            return (int)((yPos - RADIUS - offsetY) * zoom + height/2);
        }

        public int getScreenCenterX(){
            return (int)((xPos - offsetX) * zoom + width/2);
        }

        public int getScreenCenterY(){
            return (int)((yPos - offsetY) * zoom + height/2);
        }

        public int getScreenRadius(){
            return (int)(RADIUS * zoom);
        }

        public void translateFromScreen(int x, int y) {
            xPos = ((x - width/2)/zoom) + offsetX;
            yPos = ((y - height/2)/zoom) + offsetY;
        }

        public void updateProps(){
            if(node.isProcessing()){
                this.nodeColor = NODE_PROCESSING_COLOR;
            }

            if(node.isVisited()){
                this.nodeColor = NODE_VISITED_COLOR;
            }

            if(!node.isVisited() && !node.isProcessing()){
                this.nodeColor = NODE_NEUTRAL_COLOR;
            }
        }
    }

    class EdgeDisplay{

        public Edge edge;
        public NodeDisplay one, two;
        public Color edgeColor;
        public double length;
        public double unitX, unitY;
        public double perpX, perpY;
        public HashSet<EdgeDisplay> crossed;
        public boolean visible;

        public EdgeDisplay(Edge e){
            this.edge = e;
            this.edgeColor = EDGE_NEUTRAL_COLOR;
            this.unitX = 0;
            this.unitY = 0;
            this.crossed = new HashSet<>();
            this.visible = false;
        }

        public void updateUnitVector(){
            double dx = two.xPos - one.xPos;
            double dy = two.yPos - one.yPos;

            double mag = Math.sqrt(dx * dx + dy * dy);

            unitX = dx/mag;
            unitY = dy/mag;

            perpX = -unitY;
            perpY = unitX;

            if(edge.isProcessing()){
                this.edgeColor = EDGE_PROCESSING_COLOR;
            }

            if(edge.traversed()){
                this.edgeColor = EDGE_TRAVERSED_COLOR;
            }

            if(!edge.isProcessing() && !edge.traversed()){
                this.edgeColor = EDGE_NEUTRAL_COLOR;
            }

            length = mag;

        }

        public int getOneScreenX(){
            return (int) ((one.xPos - offsetX) * zoom + width/2);
        }

        public int getTwoScreenX(){
            return (int) ((two.xPos - offsetX) * zoom + width/2);
        }

        public int getOneScreenY(){
            return (int) ((one.yPos - offsetY) * zoom + height/2);
        }

        public int getTwoScreenY(){
            return (int) ((two.yPos - offsetY) * zoom + height/2);
        }
    }

    class NodeSpacer{
        public NodeDisplay one, two;
        public double unitX, unitY;
        public boolean satisfied;

        public NodeSpacer(){
            satisfied = false;
            unitX = 0;
            unitY = 0;
        }

        public double getDistance(){
            double dx = two.xPos - one.xPos;
            double dy = two.yPos - one.yPos;

            double mag = Math.sqrt(dx * dx + dy * dy);

            unitX = dx/mag;
            unitY = dy/mag;

            return mag;
        }
    }

    class ComponentDisplay {
        public Component comp;
        public Color compColor;
        public NodeDisplay root;
        public HashSet<NodeDisplay> nodeDisplays;

        public ComponentDisplay(Component comp){
            this.comp = comp;
            this.compColor = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
            this.root = getNodeFromNode(comp.getRandomVertex());
            this.nodeDisplays = new HashSet<NodeDisplay>();
        }

    }

    class ComponentSpacer{
        public ComponentDisplay one, two;
        public double unitX, unitY;

        public ComponentSpacer(ComponentDisplay one, ComponentDisplay two){
            this.one = one;
            this.two = two;
        }

        public void updateUnitVector(){
            double dx = two.root.xPos - one.root.xPos;
            double dy = two.root.yPos - one.root.yPos;

            double mag = Math.sqrt(dx * dx + dy * dy);

            unitX = dx/mag;
            unitY = dy/mag;
        }
    }

    class GraphInteraction extends MouseAdapter {

        private NodeDisplay dragging = null;
        public NodeDisplay tracked = null;

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            for(NodeDisplay nd: nodes){
                boolean inRange = Math.sqrt((e.getX() - nd.getScreenCenterX()) * (e.getX() - nd.getScreenCenterX())
                        + (e.getY() - nd.getScreenCenterY()) * (e.getY() - nd.getScreenCenterY())) < nd.getScreenRadius();

                if(inRange){

                    if(tracked != null && nd.node.equals(tracked.node)){
                        tracked = null;
                    } else {
                        tracked = nd;
                    }
                    return;
                }
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            dragging = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);

            if(dragging != null){
                dragging.translateFromScreen(e.getX(), e.getY());
            }

            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            for(NodeDisplay nd: nodes){
                boolean inRange = Math.sqrt((e.getX() - nd.getScreenCenterX()) * (e.getX() - nd.getScreenCenterX())
                        + (e.getY() - nd.getScreenCenterY()) * (e.getY() - nd.getScreenCenterY())) < nd.getScreenRadius();

                if(inRange){
                    dragging = nd;
                    return;
                }
            }
        }
    }
}
