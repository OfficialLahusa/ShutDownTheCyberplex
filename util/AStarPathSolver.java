package util;

import maths.*;
import game.*;
import java.util.*;

/**
 * Pathfinding-Solver mit dem A*-Algorithmus
 * 
 * @author Lasse Huber-Saffer
 * @version 26.12.2021
 */
public class AStarPathSolver
{
    private static final double STRAIGHT_COST = 1.0;
    private static final double DIAGONAL_COST = 1.41421;
    
    /**
     * Berechnet den kürzesten Pfad zwischen zwei Tiles innerhalb eines Raumes
     * @param start Anfangstile
     * @param end Endtile
     * @param room Raum, in dem der Pfad berechnet wird
     * @return LinkedList der Pfad-Knotenpunkte vom Startpunkt aus, oder null, wenn es keinen validen Pfad gibt
     */
    public static LinkedList<PathNode> solvePath(Vector2i start, Vector2i end, Room room)
    {
        if(start == null)
        {
            throw new IllegalArgumentException("path start position was null");
        }
        if(end == null)
        {
            throw new IllegalArgumentException("path end position was null");
        }
        if(room == null)
        {
            throw new IllegalArgumentException("path room was null");
        }
        
        // Ungültige Start- und Endpunkte ausschließen
        if(!room.contains(start) || !room.contains(end))
        {
            return null;
        }
        
        
        
        // Map der bereits passierten Knoten
        HashMap<Vector2i, PathNode> passed = new HashMap<Vector2i, PathNode>();
        // Geordnete Map der noch offenen Knoten
        LinkedHashMap<Vector2i, PathNode> open = new LinkedHashMap<Vector2i, PathNode>();
        
        // Startknoten erstellen
        open.put(start, new PathNode(start, 0, getHeuristic(start, end), null));
        
        while(true)
        {
            open = sortOpenNodes(open);
            
            // Abbruchbedingung: Kein valider Pfad verfügbar
            if(open.size() == 0)
            {
                return null;
            }
            
            // Erstes Element erhalten und aus open entfernen
            Iterator<Vector2i> iterator = open.keySet().iterator();
            PathNode currentNode = open.get(iterator.next());
            iterator.remove();
            
            // Element in passed speichern
            Vector2i nodePos = currentNode.getPosition();
            passed.put(nodePos, currentNode);
            
            // Abbruchbedingung: Ende des Pfades gefunden
            if(nodePos.equals(end))
            {
                break;
            }
            
            //Nachbarsknoten hinzufügen
            addNeighborToOpen(new Vector2i(1, 0),   currentNode, passed, open, end, room);
            addNeighborToOpen(new Vector2i(-1, 0),  currentNode, passed, open, end, room);            
            addNeighborToOpen(new Vector2i(0, 1),   currentNode, passed, open, end, room);
            addNeighborToOpen(new Vector2i(0, -1),  currentNode, passed, open, end, room);
            addNeighborToOpen(new Vector2i(1, 1),   currentNode, passed, open, end, room);
            addNeighborToOpen(new Vector2i(1, -1),  currentNode, passed, open, end, room);
            addNeighborToOpen(new Vector2i(-1, 1),  currentNode, passed, open, end, room);
            addNeighborToOpen(new Vector2i(-1, -1), currentNode, passed, open, end, room);
        }
        
        // Pfad rückwärts ermitteln
        ArrayList<PathNode> reversedPath = new ArrayList<PathNode>();
        PathNode lastNode = passed.get(end);
        reversedPath.add(lastNode);
        while(lastNode.getPreviousNode() != null)
        {
            lastNode = lastNode.getPreviousNode();
            reversedPath.add(lastNode);
        }
        
        // Rückgabe-LinkedList
        LinkedList<PathNode> result = new LinkedList<PathNode>(); 
        
        // Pfad umkehren
        for(int i = reversedPath.size() - 1; i >= 0; i--)
        {
            result.add(reversedPath.get(i));
        }
        
        return result;
    }
    
    /**
     * Gibt den Heuristikwert (Geschätzte Distanz von diesem Knoten zum Ziel) einer gegebenen Tile zu einem Endpunkt zurück
     * @param tile Tile, deren Heuristikwert berechnet werden soll
     * @param end Endpunkt
     * @return Heuristikwert (Geschätzte Distanz von diesem Knoten zum Ziel)
     */
    private static double getHeuristic(Vector2i tile, Vector2i end)
    {
        if(tile == null)
        {
            throw new IllegalArgumentException("tile was null when calculating heuristic");
        }
        if(end == null)
        {
            throw new IllegalArgumentException("end was null when calculating heuristic");
        }
        
        // NOTE (Lasse): Anstatt die Distanz sqrt(dx^2 + dy^2) als Heuristik zu benutzen, kann man die Radizierung auch weglassen und mit weniger Rechenaufwand gleichwertige Ergebnisse erhalten
        Vector2i dist = end.subtract(tile);
        return dist.getLength();
    }
    
    /**
     * Gibt zurück, ob eine Tile in einem Raum für den Pfad infrage kommt
     * @param tile Position der Tile
     * @param room Raum, in dem sich die Tile befindet
     * @return true, wenn Tile begehbar ist, sonst false
     */
    private static boolean isPassable(Vector2i tile, Room room)
    {
        if(!room.contains(tile))
        {
            return false;
        }
        
        // Tile-Wert von der Map erhalten
        int tileValue = room.getMap().getTileValue(tile);
        
        return !Tile.isSolidOrNone(tileValue) && !Tile.isSemiSolid(tileValue);
    }
    
    /**
     * Versucht, einen Nachbarn einer gewählten PathNode in die Liste der offenen Knoten einzufügen
     * @param delta relative Position des Nachbarn von der aktuellen Tile aus
     * @param currentNode aktueller Knoten
     * @param passed HashMap der passierten Knoten
     * @param open LinkedHashMap der offenen Knoten
     * @param end Position des Pfadendes
     * @param room umgebender Raum
     */
    private static void addNeighborToOpen(Vector2i delta, PathNode currentNode, HashMap<Vector2i, PathNode> passed, LinkedHashMap<Vector2i, PathNode> open, Vector2i end, Room room)
    {
        Vector2i neighbor = currentNode.getPosition().add(delta);
        
        if(!passed.containsKey(neighbor))
        {
            // Pfadkosten (g-Inkrement) des Nachbarsknotens
            double path_cost = STRAIGHT_COST;
            
            // Für direkte Nachbarn nur die Nachbartile überprüfen
            if(delta.getX() == 0 || delta.getY() == 0)
            {
                if(!isPassable(neighbor, room)) return;
            }
            // Für indirekte/diagonale Nachbarn alle drei angrenzenden Tiles überprüfen
            else
            {
                if(!isPassable(neighbor, room)) return;
                if(!isPassable(currentNode.getPosition().add(new Vector2i(delta.getX(), 0)), room)) return;
                if(!isPassable(currentNode.getPosition().add(new Vector2i(0, delta.getY())), room)) return;
                
                path_cost = DIAGONAL_COST;
            }
            
            // Wenn Nachbarsknoten noch nicht offen ist, hinzufügen
            if(!open.containsKey(neighbor))
            {
                open.put(neighbor, new PathNode(neighbor, currentNode.getG() + path_cost, getHeuristic(neighbor, end), currentNode));
            }
            // Wenn bereits vorhanden, nur hinzufügen, wenn g (Pfadkosten) vorher höher waren
            else
            {
                double g = open.get(neighbor).getG();
                
                // Bereits existierenden Knoteneintrag überschreiben
                if(g > currentNode.getG())
                {
                    open.put(neighbor, new PathNode(neighbor, currentNode.getG() + path_cost, getHeuristic(neighbor, end), currentNode));
                }
            }
        }
    }
    
    /**
     * Sortiert die geordnete Map der offenen Knoten anhand der Gesamtkosten
     * @param open unsortierte Map an offenen Knoten
     * @return anhand der Gesamtkosten sortierte Map an offenen Knoten
     */
    private static LinkedHashMap<Vector2i, PathNode> sortOpenNodes(LinkedHashMap<Vector2i, PathNode> open)
    {       
        if(open == null)
        {
            throw new IllegalArgumentException("open was null when sorting nodes");
        }
        
        // Liste der Einträge erhalten
        ArrayList<Map.Entry<Vector2i, PathNode>> entries = new ArrayList<Map.Entry<Vector2i, PathNode>>(open.entrySet());
        
        // Komparator für das Sortieren der Einträge von offenen Knoten
        Comparator<Map.Entry<Vector2i, PathNode>> openNodeComparator = new Comparator<Map.Entry<Vector2i, PathNode>>()
        {
            public int compare(Map.Entry<Vector2i, PathNode> first, Map.Entry<Vector2i, PathNode> second)
            {
                int comp = first.getValue().compareTo(second.getValue());
                if(comp == 0)
                {
                    return 1;
                }
                else
                {
                    return comp;
                }
            }
        };
        
        // Einträge sortieren
        Collections.sort(entries, openNodeComparator);
        
        // Sortierte Liste als Map formatieren
        LinkedHashMap<Vector2i, PathNode> sortedResult = new LinkedHashMap<Vector2i, PathNode>();
        for(Map.Entry<Vector2i, PathNode> entry : entries)
        {
            sortedResult.put(entry.getKey(), entry.getValue());
        }
        
        return sortedResult;
    }
}
