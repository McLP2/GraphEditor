import nrw.graph.Graph;
import nrw.graph.GraphNode;
import nrw.list.List;

public class ImportExport
{
    static Graph tempGraph() {
        Graph einGraph = new Graph();

        GraphNode dm = new GraphNode("Dortmund");
        GraphNode gs = new GraphNode("Gelsenkirchen");
        GraphNode wi = new GraphNode("Witten");
        GraphNode ss = new GraphNode("Essen");
        GraphNode du = new GraphNode("Duisburg");
        GraphNode wp = new GraphNode("Wuppertal");
        GraphNode rh = new GraphNode("Recklinghausen");

        einGraph.addNode(dm);
        einGraph.addNode(gs);
        einGraph.addNode(wi);
        einGraph.addNode(ss);
        einGraph.addNode(du);
        einGraph.addNode(wp);
        einGraph.addNode(rh);

        einGraph.addEdge(dm, gs, 236);
        einGraph.addEdge(dm, wi, 109);
        einGraph.addEdge(dm, ss, 298);
        einGraph.addEdge(dm, du, 471);
        einGraph.addEdge(dm, wp, 357);
        einGraph.addEdge(dm, rh, 206);

        einGraph.addEdge(gs, dm, 236);
        einGraph.addEdge(gs, wi, 207);
        einGraph.addEdge(gs, ss, 76);
        einGraph.addEdge(gs, du, 231);
        einGraph.addEdge(gs, wp, 295);
        einGraph.addEdge(gs, rh, 132);

        einGraph.addEdge(wi, dm, 109);
        einGraph.addEdge(wi, gs, 207);
        einGraph.addEdge(wi, ss, 238);
        einGraph.addEdge(wi, du, 409);
        einGraph.addEdge(wi, wp, 255);
        einGraph.addEdge(wi, rh, 220);

        einGraph.addEdge(ss, dm, 298);
        einGraph.addEdge(ss, gs, 76);
        einGraph.addEdge(ss, wi, 238);
        einGraph.addEdge(ss, du, 165);
        einGraph.addEdge(ss, wp, 246);
        einGraph.addEdge(ss, rh, 220);

        einGraph.addEdge(du, dm, 471);
        einGraph.addEdge(du, gs, 231);
        einGraph.addEdge(du, wi, 409);
        einGraph.addEdge(du, ss, 165);
        einGraph.addEdge(du, wp, 355);
        einGraph.addEdge(du, rh, 355);

        einGraph.addEdge(wp, dm, 357);
        einGraph.addEdge(wp, gs, 295);
        einGraph.addEdge(wp, wi, 255);
        einGraph.addEdge(wp, ss, 246);
        einGraph.addEdge(wp, du, 355);
        einGraph.addEdge(wp, rh, 399);

        einGraph.addEdge(rh, dm, 206);
        einGraph.addEdge(rh, gs, 132);
        einGraph.addEdge(rh, wi, 220);
        einGraph.addEdge(rh, ss, 220);
        einGraph.addEdge(rh, du, 355);
        einGraph.addEdge(rh, wp, 399);

        return einGraph;
    }

    static String graphToCSV(Graph derGraph) {
        String s = "";
        // zweil Listen für verschachtelte Iterationsmöglichkeiten
        List<GraphNode> graphList = derGraph.getNodes();
        List<GraphNode> graphListCopy = derGraph.getNodes();
        // Erste Zeile Schreiben (Erstes Feld leer, Knotennamen kommagetrennt)
        graphList.toFirst();
        while (graphList.hasAccess()) {
            s+=","+graphList.getContent().getName();
            graphList.next();
        }
        // Gewichte Schreiben (Erstes Feld Knotenname, dann Gewichte kommagetrennt)
        graphList.toFirst();
        while (graphList.hasAccess()) {
            // Für jeden Knoten eine neue Zeile
            s+="\n";
            s+=graphList.getContent().getName();
            // alle Kanten von dem Knoten aus finden
            graphListCopy.toFirst();
            while (graphListCopy.hasAccess()) {
                s+=","; // immer ein neues Feld schreiben
                GraphNode n1 = graphList.getContent();
                GraphNode n2 = graphListCopy.getContent();
                if (derGraph.hasEdge(n1, n2)) // wenn Kante vorhanden, Gewicht schreiben
                {s+=derGraph.getEdgeWeight(n1, n2);}
                graphListCopy.next();
            }
            graphList.next();
        }
        return s;
    }

    static Graph csvToGraph(String befehlString) {
        //Neuer Graph wird erzeugt
        Graph lGraph = new Graph();

        //Neue Liste wird erzeugt
        List<String> Liste = new List<String>();

        //String zwischenspeicher
        String aktString = befehlString;//Der String, der Stück für Stück verkleinert wird
        String aktuellerKnoten;
        String aktuelleZahl;

        //Namen der ersten Zeile Speichern:
        for(int i =0; true; i++)//Gehe die Zeichen des aktStrings durch...
        {
            if(aktString.charAt(i)==',')//...bis zu einem Komma
            {
                if(i-1>0)//Wenn es inhalt gibt,
                {
                    aktuellerKnoten = aktString.substring(0,i);//Filter den Namen des Knoten raus
                    Liste.append(aktuellerKnoten);//Füge ihn in der Liste ein

                    lGraph.addNode(new GraphNode(aktuellerKnoten));//Füge den Knoten dem Graphen hinzu
                }
                aktString = aktString.substring(i+1);//Kürze den schon ausgewerteten Teil des Strings weg
                i=0;//Setze den Startwert der Zeichensuche zurück auf 0
            }
            if(aktString.charAt(i)==(char)10)//...bis zu einem Absatz
            {
                aktuellerKnoten = aktString.substring(0,i);//Filter den letzen Namen des Knoten aus diesem Absatz raus
                Liste.append(aktuellerKnoten);//Füge ihn in der Liste ein

                lGraph.addNode(new GraphNode(aktuellerKnoten));//Füge den Knoten dem Graphen hinzu

                aktString = aktString.substring(i+1);//Kürze den schon ausgewerteten Teil des Strings weg
                break;//Verlasse die Schleife
            }
        }


        //Kanten Festlegen
        try//Solange der String nicht zuende ist
        {
            for(int j = 0; true; j++) {
                if(aktString.charAt(j)==',')//Filtere den aktuellen Knoten der Aktuellen Zeile Heraus
                {
                    aktuellerKnoten = aktString.substring(0,j);//Zwischenspeichere diesen


                    aktString = aktString.substring(j+1);//Kürze den schon ausgewerteten Teil des Strings weg


                    Liste.toFirst();//Gehe die Spalten von Anfang an durch
                    for(j = 0; true; j++)//Filtere die einzelnen Gewichte der Kanten raus
                    {
                        if(aktString.charAt(j)==(char)10)//Bei einem Zeilenwechsel
                        {
                            if(j>0)//Überprüfe ob es noch eine Zahl ist(Falls eine Feld lehr ist : ,,)
                            {
                                aktuelleZahl = aktString.substring(0,j);//Speichere die letzte Zahl der Zeile
                                double zahlInDouble = Double.parseDouble(aktuelleZahl);//Konvertiere die Zahl

                                lGraph.addEdge(lGraph.getNode(aktuellerKnoten),lGraph.getNode(Liste.getContent()),zahlInDouble);//Bilde die Kante mit dem Aktuellen Knoten der Zeile, dem Aktuellen Knoten der Spalte (aus der Liste) und der aktuellen Zahl
                            }
                            aktString = aktString.substring(j+1);//Kürze den schon ausgewerteten Teil des Strings weg
                            break;//Zeilenwechsel
                        }
                        if(aktString.charAt(j)==',' )//Wenn die Zahl zuende ist
                        {
                            if(j>0)//Überprüfe ob es eine Zahl ist(Falls eine Feld lehr ist : ,,)
                            {
                                aktuelleZahl = aktString.substring(0,j);//Speichere die Zahl

                                double zahlInDouble = Double.parseDouble(aktuelleZahl);//Konvertiere die Zahl

                                lGraph.addEdge(lGraph.getNode(aktuellerKnoten),lGraph.getNode(Liste.getContent()),zahlInDouble);//Bilde die Kante mit dem Aktuellen Knoten der Zeile, dem Aktuellen Knoten der Spalte (aus der Liste) und der aktuellen Zahl
                            }

                            Liste.next();//Gehe eine Spalte weiter
                            aktString = aktString.substring(j+1);//Kürze den schon ausgewerteten Teil des Strings weg
                            j=-1;//Setze den Startwert der Zeichensuche zurück auf 0
                        }
                    }
                }
            }
        } catch(Exception e)//Wenn der String vollständig durchgegangen ist, sollte der Algorythmus abstürtzen,
        {

        }
        return lGraph;//Sodass der Graph hier returned wird
    }
}
