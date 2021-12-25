import java.util.*;

/**
 * Pathfinding-Solver mit dem A*-Algorithmus
 * 
 * @author Lasse Huber-Saffer
 * @version 25.12.2021
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
            
            // px
            Vector2i neighborPx = nodePos.add(new Vector2i(1, 0));
            if(!passed.containsKey(neighborPx) && isPassable(neighborPx, room))
            {
                if(!open.containsKey(neighborPx))
                {
                    open.put(neighborPx, new PathNode(neighborPx, currentNode.getG() + STRAIGHT_COST, getHeuristic(neighborPx, end), currentNode));
                }
                else
                {
                    double g = open.get(neighborPx).getG();
                    
                    if(g > currentNode.getG())
                    {
                        open.put(neighborPx, new PathNode(neighborPx, currentNode.getG() + STRAIGHT_COST, getHeuristic(neighborPx, end), currentNode));
                    }
                }
            }
            
            // nx
            Vector2i neighborNx = nodePos.add(new Vector2i(-1, 0));
            if(!passed.containsKey(neighborNx) && isPassable(neighborNx, room))
            {
                if(!open.containsKey(neighborNx))
                {
                    open.put(neighborNx, new PathNode(neighborNx, currentNode.getG() + STRAIGHT_COST, getHeuristic(neighborNx, end), currentNode));
                }
                else
                {
                    double g = open.get(neighborNx).getG();
                    
                    if(g > currentNode.getG())
                    {
                        open.put(neighborNx, new PathNode(neighborNx, currentNode.getG() + STRAIGHT_COST, getHeuristic(neighborNx, end), currentNode));
                    }
                }
            }
            
            
            // pz
            Vector2i neighborPz = nodePos.add(new Vector2i(0, 1));
            if(!passed.containsKey(neighborPz) && isPassable(neighborPz, room))
            {
                if(!open.containsKey(neighborPz))
                {
                    open.put(neighborPz, new PathNode(neighborPz, currentNode.getG() + STRAIGHT_COST, getHeuristic(neighborPz, end), currentNode));
                }
                else
                {
                    double g = open.get(neighborPz).getG();
                    
                    if(g > currentNode.getG())
                    {
                        open.put(neighborPz, new PathNode(neighborPz, currentNode.getG() + STRAIGHT_COST, getHeuristic(neighborPz, end), currentNode));
                    }
                }
            }
            
            // nz
            Vector2i neighborNz = nodePos.add(new Vector2i(0, -1));
            if(!passed.containsKey(neighborNz) && isPassable(neighborNz, room))
            {
                if(!open.containsKey(neighborNz))
                {
                    open.put(neighborNz, new PathNode(neighborNz, currentNode.getG() + STRAIGHT_COST, getHeuristic(neighborNz, end), currentNode));
                }
                else
                {
                    double g = open.get(neighborNz).getG();
                    
                    if(g > currentNode.getG())
                    {
                        open.put(neighborNz, new PathNode(neighborNz, currentNode.getG() + STRAIGHT_COST, getHeuristic(neighborNz, end), currentNode));
                    }
                }
            }
            
            // pxpz
            Vector2i neighborPxpz = nodePos.add(new Vector2i(1, 1));
            if(!passed.containsKey(neighborPxpz) && isPassable(neighborPxpz, room))
            {
                if(!open.containsKey(neighborPxpz))
                {
                    open.put(neighborPxpz, new PathNode(neighborPxpz, currentNode.getG() + DIAGONAL_COST, getHeuristic(neighborPxpz, end), currentNode));
                }
                else
                {
                    double g = open.get(neighborPxpz).getG();
                    
                    if(g > currentNode.getG())
                    {
                        open.put(neighborPxpz, new PathNode(neighborPxpz, currentNode.getG() + DIAGONAL_COST, getHeuristic(neighborPxpz, end), currentNode));
                    }
                }
            }
            
            // pxnz
            Vector2i neighborPxnz = nodePos.add(new Vector2i(1, -1));
            if(!passed.containsKey(neighborPxnz) && isPassable(neighborPxnz, room))
            {
                if(!open.containsKey(neighborPxnz))
                {
                    open.put(neighborPxnz, new PathNode(neighborPxnz, currentNode.getG() + DIAGONAL_COST, getHeuristic(neighborPxnz, end), currentNode));
                }
                else
                {
                    double g = open.get(neighborPxnz).getG();
                    
                    if(g > currentNode.getG())
                    {
                        open.put(neighborPxnz, new PathNode(neighborPxnz, currentNode.getG() + DIAGONAL_COST, getHeuristic(neighborPxnz, end), currentNode));
                    }
                }
            }
            
            // pxpz
            Vector2i neighborNxpz = nodePos.add(new Vector2i(-1, 1));
            if(!passed.containsKey(neighborNxpz) && isPassable(neighborNxpz, room))
            {
                if(!open.containsKey(neighborNxpz))
                {
                    open.put(neighborNxpz, new PathNode(neighborNxpz, currentNode.getG() + DIAGONAL_COST, getHeuristic(neighborNxpz, end), currentNode));
                }
                else
                {
                    double g = open.get(neighborNxpz).getG();
                    
                    if(g > currentNode.getG())
                    {
                        open.put(neighborNxpz, new PathNode(neighborNxpz, currentNode.getG() + DIAGONAL_COST, getHeuristic(neighborNxpz, end), currentNode));
                    }
                }
            }
            
            // pxnz
            Vector2i neighborNxnz = nodePos.add(new Vector2i(-1, -1));
            if(!passed.containsKey(neighborNxnz) && isPassable(neighborNxnz, room))
            {
                if(!open.containsKey(neighborNxnz))
                {
                    open.put(neighborNxnz, new PathNode(neighborNxnz, currentNode.getG() + DIAGONAL_COST, getHeuristic(neighborNxnz, end), currentNode));
                }
                else
                {
                    double g = open.get(neighborNxnz).getG();
                    
                    if(g > currentNode.getG())
                    {
                        open.put(neighborNxnz, new PathNode(neighborNxnz, currentNode.getG() + DIAGONAL_COST, getHeuristic(neighborNxnz, end), currentNode));
                    }
                }
            }
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
        for(int i = 0; i < reversedPath.size(); i++)
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
