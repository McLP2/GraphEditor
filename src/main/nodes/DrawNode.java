package main.nodes;

import nrw.graph.GraphNode;

public class DrawNode
{
    // data
    private GraphNode representingNode;
    // physics
    private double posx;
    private double posy;
    private double velx = 0;
    private double vely = 0;
    private double accx = 0;
    private double accy = 0;
    private static final double mass = 1;
    private static final double damp = 0.001;

    /**
     * Konstruktor für Objekte der Klasse main.nodes.DrawNode
     */
    public DrawNode(double x, double y, GraphNode node) {
        representingNode = node;
        posx = x;
        posy = y;
    }

    public void simulate() {
        velx += accx;
        vely += accy;
        posx += velx;
        posy += vely;
        velx *= damp;
        vely *= damp;
        accx = 0;
        accy = 0;
    }

    public void applyForce(double x, double y) {
        accx += x/mass;
        accy += y/mass;
    }

    public GraphNode getNode() {
        return representingNode;
    }

    public double getPosX() {
        return posx;
    }

    public double getPosY() {
        return posy;
    }

    public double getAccelerationX() {
        return accx;
    }

    public double getAccelerationY() {
        return accy;
    }

    public boolean hasEqualibrium() {
        return Math.abs(accx + accy) < 0.00001;
    }

    public void setPos(int x, int y){
        posx = x;
        posy = y;
    }
}
