public class DjikstraNode {
    // data
    private DrawNode representingNode;
    // values
    private double shortestPath = Integer.MAX_VALUE;
    private DjikstraNode shortestPrev = null;
    private boolean isVisited = false;

    /**
     * Konstruktor für Objekte der Klasse DrawNode
     */
    public DjikstraNode(DrawNode node) {
        representingNode = node;
    }

    public void addPath(DjikstraNode prev, double length) {
        if (shortestPath > length) {
            shortestPrev = prev;
            shortestPath = length;
        }
    }

    public DjikstraNode getPrev() {
        return shortestPrev;
    }

    public double getPathLength() {
        return shortestPath;
    }

    public void makeFirst() {
        shortestPath = 0;
    }

    public DrawNode getNode() {
        return representingNode;
    }

    public boolean hasPrev() {
        return shortestPrev != null;
    }

    public boolean isUnVisited() {
        return !isVisited;
    }

    public void makeVisited() {
        isVisited = true;
    }
}
