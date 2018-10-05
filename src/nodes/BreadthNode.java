package nodes;

import nrw.list.List;

public class BreadthNode {
    // data
    private DrawNode representingNode;
    // values
    private boolean isVisited = false;

    /**
     * Konstruktor für Objekte der Klasse nodes.DrawNode
     */
    public BreadthNode(DrawNode node) {
        representingNode = node;
    }

    public DrawNode getNode() {
        return representingNode;
    }

    public boolean isUnVisited() {
        return !isVisited;
    }

    public void makeVisited() {
        isVisited = true;
    }
}
