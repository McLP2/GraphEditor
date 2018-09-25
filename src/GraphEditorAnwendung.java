import sum.komponenten.*;
import sum.ereignis.*;

import javax.swing.JOptionPane;

/**
 * Bedienung:
 * <p>
 * Laden läd eine Karte des Ruhrgebiets mit zufälligen Positionen. In einer späteren Version soll das CSV geladen werden.
 * Kräfte simuliert Kanten als Federn und stellt so die richtigen Entfernungen dar.
 * Speichern konvertiert den aktuellen Graphen in ein CSV.
 * <p>
 * Shift schaltet um zwischen Kanten, und Konten- und Pfadmodus.
 * Im Knotenmodus lassen sich Knoten durch Doppelklick hinzufügen oder entfernen.
 * Außerdem ist es möglich, die Knoten per Drag and Drop zu verschieben.
 * Im Kantenmodus lassen sich Kanten zwischen Knoten per Drag and Drop erstellen.
 * In Djikstra's Modus kann der kürzeste Pfad zwischen zwei Koten gefunden werden.
 */

public class GraphEditorAnwendung extends Ereignisanwendung {
    // Objekte
    private Etikett hatEtikettGraphEditor;
    private Zeilenbereich hatZeilenbereichAdjazenzMatrixCSV;
    private Buntstift hatStift;
    private Graph derGraph;
    private DrawNode[] graphArray;
    private DrawNode activeNode;
    private boolean drawPath;
    private Mode mode = Mode.EDIT_NODES;
    private int sX;
    private int sY;
    private boolean creatingEdge;
    private boolean findingPath;
    // physics
    private static final double k = 0.0001; // spring strength
    private static final double r = 20; // radius for nodes
    private static final double scale = 0.5; // scale for edges
    // canvas
    private int x = 60;
    private int y = 100;
    private int w = 980;
    private int h = 250;
    private List<DrawNode> derPfad;
    private DrawNode derStart;

    // Attribute

    /**
     * Konstruktor
     */
    public GraphEditorAnwendung() {
        //Initialisierung der Oberklasse
        super(1181, 703, true);
        bildschirm().zeichneDich();
        hatStift = new Buntstift();

        derGraph = ImportExport.tempGraph();
        init();

        hatEtikettGraphEditor = new Etikett(500, 50, 200, 25, "Graph Editor (Node Mode)");
        hatEtikettGraphEditor.setzeAusrichtung(Ausrichtung.MITTE);
        hatZeilenbereichAdjazenzMatrixCSV = new
                Zeilenbereich(60, 375, 950, 300,
                ImportExport.graphToCSV(derGraph));
        Knopf hatKnopfBeenden = new
                Knopf(1040, 650, 100, 25, "Beenden");
        hatKnopfBeenden.setzeBearbeiterGeklickt("hatKnopfBeendenGeklickt");
        Knopf hatKnopfZeichnen = new
                Knopf(1040, 475, 100, 25, "Kräfte");
        hatKnopfZeichnen.setzeBearbeiterGeklickt("hatKnopfZeichnenGeklickt");
        Knopf hatKnopfSpeichern = new
                Knopf(1040, 415, 100, 25, "Speichern");
        hatKnopfSpeichern.setzeBearbeiterGeklickt("hatKnopfSpeichernGeklickt");
        Knopf hatKnopfLaden = new
                Knopf(1040, 375, 100, 25, "Laden");
        hatKnopfLaden.setzeBearbeiterGeklickt("hatKnopfLadenGeklickt");
        bildschirm().zeichneDich();
        fuehreAus();
        bildschirm().zeichneDich();
    }

    /**
     * Vorher: Ereignis GeklicktvonhatKnopfBeenden fand statt.
     * Nachher: (schreiben Sie, was in dieser Methode ausgefuehrt wird)
     */
    public void hatKnopfBeendenGeklickt() {
        beenden();
    }

    /**
     * Vorher: Ereignis GeklicktvonhatKnopfZeichnen fand statt.
     * Nachher: (schreiben Sie, was in dieser Methode ausgefuehrt wird)
     */
    public void hatKnopfZeichnenGeklickt() {
        graphZeichnen();
    }

    public void hatKnopfSpeichernGeklickt() {
        hatZeilenbereichAdjazenzMatrixCSV.setzeInhalt(ImportExport.graphToCSV(derGraph));
    }

    /**
     * Wandelt eine Liste von GraphNodes in ein Array um.
     */
    private DrawNode[] graphListToDrawArray(List<GraphNode> graphListe) {
        // get length
        int i = 0;
        graphListe.toFirst();
        while (graphListe.hasAccess()) {
            graphListe.next();
            i++;
        }
        DrawNode[] graphArray = new DrawNode[i];
        // iterate over array and list at once
        i = 0;
        graphListe.toFirst();
        while (graphListe.hasAccess()) {
            int xpos = random(x, w);
            int ypos = random(y, h);
            graphArray[i] = new DrawNode(xpos, ypos, graphListe.getContent());
            graphListe.next();
            i++;
        }
        return graphArray;
    }

    private int random(int min, int range) {
        return (int) ((Math.random() * range) + min);
    }

    /**
     * Zeichnet den Graph
     */
    private void graphZeichnen() {
        boolean equalibrium;
        do {
            equalibrium = true;
            for (DrawNode aGraphArray : graphArray) {
                // calculate spring forces
                for (DrawNode aGraphArray1 : graphArray) {
                    if (derGraph.hasEdge(aGraphArray.getNode(), aGraphArray1.getNode())) {
                        double dx = aGraphArray1.getPosX() - aGraphArray.getPosX();
                        double dy = aGraphArray1.getPosY() - aGraphArray.getPosY();
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        double toLength = derGraph.getEdgeWeight(aGraphArray.getNode(), aGraphArray1.getNode()) * scale;
                        double fx = (distance - toLength) * dx * k;
                        double fy = (distance - toLength) * dy * k;
                        aGraphArray.applyForce(fx, fy);
                    }
                    // force in both directions (apply edge from j to i too)
                    if (derGraph.hasEdge(aGraphArray1.getNode(), aGraphArray.getNode())) {
                        double dx = aGraphArray1.getPosX() - aGraphArray.getPosX();
                        double dy = aGraphArray1.getPosY() - aGraphArray.getPosY();
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        double toLength = derGraph.getEdgeWeight(aGraphArray1.getNode(), aGraphArray.getNode()) * scale;
                        double fx = (distance - toLength) * dx * k;
                        double fy = (distance - toLength) * dy * k;
                        aGraphArray.applyForce(fx, fy);
                    }
                }
                // calculate outside forces
                double pX = aGraphArray.getPosX();
                double pY = aGraphArray.getPosY();
                double aX = aGraphArray.getAccelerationX();
                double aY = aGraphArray.getAccelerationY();
                if (pX < x) { // left wall
                    aGraphArray.applyForce(Math.max(-aX, 0), 0);
                }
                if (pX > x + w) { // right wall
                    aGraphArray.applyForce(Math.min(-aX, 0), 0);
                }
                if (pY < y) { // top wall
                    aGraphArray.applyForce(0, Math.max(-aY, 0));
                }
                if (pY > y + h) { // bottom wall
                    aGraphArray.applyForce(0, Math.min(-aY, 0));
                }

                // apply physics
                equalibrium = aGraphArray.hasEqualibrium() && equalibrium;
                aGraphArray.simulate();
            }
        } while (!equalibrium);
        // draw graph
        draw();
    }

    private void draw() {
        // clear
        hatStift.setzeFuellMuster(1);
        hatStift.setzeFarbe(Farbe.WEISS);
        hatStift.bewegeBis(0, y - r);
        hatStift.zeichneRechteck(bildschirm().breite(), h + 2 * r);
        // draw
        hatStift.setzeSchriftGroesse(10);
        // draw edges
        for (DrawNode aGraphArray : graphArray) {
            hatStift.setzeFuellMuster(0);
            hatStift.setzeFarbe(Farbe.SCHWARZ);
            for (DrawNode aGraphArray1 : graphArray) {
                if (derGraph.hasEdge(aGraphArray.getNode(), aGraphArray1.getNode())) {
                    hatStift.hoch();
                    hatStift.bewegeBis(aGraphArray.getPosX(), aGraphArray.getPosY());
                    hatStift.runter();
                    hatStift.bewegeBis(aGraphArray1.getPosX(), aGraphArray1.getPosY());
                }
            }
        }
        // draw nodes with background
        for (DrawNode aGraphArray : graphArray) {
            hatStift.hoch();
            hatStift.bewegeBis(aGraphArray.getPosX(), aGraphArray.getPosY());
            hatStift.setzeFuellMuster(1);
            hatStift.setzeFarbe(Farbe.WEISS);
            hatStift.zeichneKreis(r);
            hatStift.setzeFuellMuster(0);
            hatStift.setzeFarbe(Farbe.SCHWARZ);
            hatStift.zeichneKreis(r - 2);
            hatStift.bewegeBis( // text zentrieren
                    aGraphArray.getPosX() -
                            (hatStift.textbreite(aGraphArray.getNode().getName()) / 2.0 - 1),
                    aGraphArray.getPosY() + 4);
            hatStift.schreibeText(aGraphArray.getNode().getName());
        }
        if (drawPath) {
            if (!(derPfad.first == derPfad.last)) {
                // nodes
                derPfad.toFirst();
                while (derPfad.hasAccess()) {
                    double x = derPfad.getContent().getPosX();
                    double y = derPfad.getContent().getPosY();
                    hatStift.setzeFarbe(Farbe.GRUEN);
                    hatStift.bewegeBis(x, y);
                    hatStift.setzeFuellMuster(1);
                    hatStift.zeichneKreis(r / 2);
                    derPfad.next();
                }
                // edges
                derPfad.toFirst();
                hatStift.hoch();
                hatStift.setzeLinienBreite(2);
                do {
                    double x = derPfad.getContent().getPosX();
                    double y = derPfad.getContent().getPosY();
                    hatStift.bewegeBis(x, y);
                    hatStift.runter();
                    derPfad.next();
                } while (derPfad.hasAccess());
                hatStift.setzeLinienBreite(1);
                hatStift.hoch();
            } else {
                derPfad.toLast();
                double e_x = derPfad.getContent().getPosX();
                double e_y = derPfad.getContent().getPosY();
                double s_x = derStart.getPosX();
                double s_y = derStart.getPosY();
                hatStift.setzeFuellMuster(1);
                hatStift.setzeFarbe(Farbe.ROT);
                hatStift.bewegeBis(s_x, s_y);
                hatStift.zeichneKreis(r / 2);
                hatStift.bewegeBis(e_x, e_y);
                hatStift.zeichneKreis(r / 2);
            }
        }
        bildschirm().zeichneDich();
    }

    /**
     * Vorher: Ereignis GeklicktvonhatKnopfLaden fand statt.
     * Nachher: (schreiben Sie, was in dieser Methode ausgefuehrt wird)
     */
    public void hatKnopfLadenGeklickt() {
        derGraph = ImportExport.csvToGraph(hatZeilenbereichAdjazenzMatrixCSV.inhaltAlsText()); //Julian
        init();
    }

    private void init() {
        List<GraphNode> graphListe = derGraph.getNodes();
        // make circle objects with basic pyhsics and references to nodes
        graphArray = graphListToDrawArray(graphListe);
        draw();
    }

    public void bearbeiteMausBewegt(int px, int py) {
        if (activeNode != null &&
                !(px < x ||
                        py < y ||
                        px > x + w ||
                        py > y + h)
        ) {
            switch (mode) {
                case EDIT_NODES:
                    activeNode.setPos(px, py);
                    draw();
                    break;
                case CREATE_EDGES:
                    if (!creatingEdge) {
                        // save first pos for drawing mouse-line
                        sX = px;
                        sY = py;
                    }
                    creatingEdge = true;
                    draw();
                    hatStift.setzeFuellMuster(0);
                    hatStift.setzeFarbe(Farbe.rgb(50, 50, 255));
                    hatStift.bewegeBis(sX, sY);
                    hatStift.runter();
                    hatStift.bewegeBis(px, py);
                    hatStift.hoch();
                    bildschirm().zeichneDich();
                    break;
                case DJIKSTRA:
                    if (!findingPath) {
                        // save first pos for drawing mouse-line
                        sX = px;
                        sY = py;
                    }
                    findingPath = true;
                    draw();
                    hatStift.setzeFuellMuster(0);
                    hatStift.setzeFarbe(Farbe.rgb(255, 127, 50));
                    hatStift.bewegeBis(sX, sY);
                    hatStift.runter();
                    hatStift.bewegeBis(px, py);
                    hatStift.hoch();
                    bildschirm().zeichneDich();
                    break;
            }
        }
    }

    public void bearbeiteMausDruck(int px, int py) {
        for (DrawNode aGraphArray : graphArray) {
            double dx = aGraphArray.getPosX() - px;
            double dy = aGraphArray.getPosY() - py;
            if (dx * dx + dy * dy < r * r) {
                activeNode = aGraphArray;
            }
        }
    }

    public void bearbeiteDoppelKlick(int px, int py) {
        if (mode != Mode.EDIT_NODES) {
            return;
        }
        if (px < x ||
                py < y ||
                px > x + w ||
                py > y + h) {
            return;
        }
        int k = 0;
        for (int i = 0; i < graphArray.length; i++) {
            double dx = graphArray[i].getPosX() - px;
            double dy = graphArray[i].getPosY() - py;
            if (dx * dx + dy * dy < r * r) {
                derGraph.removeNode(graphArray[i].getNode());
                graphArray[i] = null;
                k--;
            }
        }
        GraphNode newNode = new GraphNode("Error");
        if (k == 0) {
            String newNodeName = holeText("Name:");
            if (newNodeName != null) {
                newNode = new GraphNode(newNodeName);
                derGraph.addNode(newNode);
                k++;
            }
        }
        DrawNode[] gArr = new DrawNode[graphArray.length + k];
        int j = 0;
        if (k > 0) {
            System.arraycopy(graphArray, 0, gArr, 0, graphArray.length);
            gArr[gArr.length - 1] = new DrawNode(px, py, newNode);
        } else {
            for (DrawNode aGraphArray : graphArray) {
                if (aGraphArray != null) {
                    gArr[j] = aGraphArray;
                    j++;
                }
            }
        }
        graphArray = gArr;
        activeNode = null;
        draw();
    }

    private String holeText(String msg) {
        return JOptionPane.showInputDialog(msg);
    }

    private double holeZahl(String msg) {
        try {
            String ans = JOptionPane.showInputDialog(msg);
            if (ans == null) {
                return Double.NaN;
            }
            return Double.parseDouble(ans);
        } catch (Exception e) {
            return holeZahl("Keine Zahl. " + msg);
        }
    }

    public void bearbeiteMausLos(int px, int py) {
        if (creatingEdge) {
            GraphNode n2 = null;
            GraphNode n1 = activeNode.getNode();
            for (DrawNode aGraphArray : graphArray) {
                double dx = aGraphArray.getPosX() - px;
                double dy = aGraphArray.getPosY() - py;
                if (dx * dx + dy * dy < r * r) {
                    n2 = aGraphArray.getNode();
                }
            }
            if (n2 != null && n1 != n2) {
                double w = holeZahl("Gewicht:");
                if (!Double.isNaN(w)) {
                    derGraph.addEdge(n1, n2, w);
                }
            }
        } else if (findingPath) {
            GraphNode n2 = null;
            GraphNode n1 = activeNode.getNode();
            for (DrawNode aGraphArray : graphArray) {
                double dx = aGraphArray.getPosX() - px;
                double dy = aGraphArray.getPosY() - py;
                if (dx * dx + dy * dy < r * r) {
                    n2 = aGraphArray.getNode();
                }
            }
            if (n2 != null && n1 != n2) {
                djikstra(n1, n2);
            }
        }
        activeNode = null;
        creatingEdge = false;
        findingPath = false;
        draw();
    }

    private void djikstra(GraphNode n1, GraphNode n2) {
        DjikstraNode start = new DjikstraNode(new DrawNode(0, 0, n1)); // temp
        DjikstraNode end = new DjikstraNode(new DrawNode(0, 0, n2)); // temp
        DjikstraNode currentNode;
        DjikstraNode[] djikstraNodes = new DjikstraNode[graphArray.length];
        for (int i = 0; i < djikstraNodes.length; i++) {
            DjikstraNode node = new DjikstraNode(graphArray[i]);
            if (graphArray[i].getNode() == n1) {
                start = node;
            } else if (graphArray[i].getNode() == n2) {
                end = node;
            }
            djikstraNodes[i] = node;
        }

        start.makeFirst();
        currentNode = start;
        while (currentNode != null) {
            for (DjikstraNode djikstraNode : djikstraNodes) {
                GraphNode currentGraphNode = currentNode.getNode().getNode();
                GraphNode djikstraGraphNode = djikstraNode.getNode().getNode();
                // every non marked neighbour of current
                if (djikstraNode.isUnVisited() && derGraph.hasEdge(currentGraphNode, djikstraGraphNode)) {
                    // setPath
                    double len = currentNode.getPathLength() +
                            derGraph.getEdgeWeight(currentGraphNode, djikstraGraphNode);
                    djikstraNode.addPath(currentNode, len);
                }
            }
            // mark current
            currentNode.makeVisited();
            // current = smallest non marked non infinite
            currentNode = null;
            double minlen = Integer.MAX_VALUE;
            for (DjikstraNode djikstraNode : djikstraNodes) {
                if (djikstraNode.isUnVisited() &&
                        djikstraNode.getPathLength() < minlen &&
                        djikstraNode.getPathLength() != Integer.MAX_VALUE) {
                    currentNode = djikstraNode;
                    minlen = currentNode.getPathLength();
                }
            }
            if (currentNode != null && end.getPathLength() <= currentNode.getPathLength()) {
                break;
            }
        }
        currentNode = end;
        List<DrawNode> path = new List<DrawNode>();
        while (currentNode.hasPrev()) {
            path.append(currentNode.getNode());
            currentNode = currentNode.getPrev();
        }
        path.append(currentNode.getNode());

        derPfad = path;
        derStart = start.getNode();
        drawPath = true;
        draw();
    }

    public void bearbeiteTaste(char c) {
        if (c == 516) {
            drawPath = false;
            switch (mode) {
                case EDIT_NODES:
                    mode = Mode.CREATE_EDGES;
                    hatEtikettGraphEditor.setzeInhalt("Graph Editor (Edge Mode)");
                    break;
                case CREATE_EDGES:
                    mode = Mode.DJIKSTRA;
                    hatEtikettGraphEditor.setzeInhalt("Graph Editor (Djikstra's Mode)");
                    break;
                case DJIKSTRA:
                    mode = Mode.EDIT_NODES;
                    hatEtikettGraphEditor.setzeInhalt("Graph Editor (Node Mode)");
                    break;
            }
            draw();
        }
    }
}
