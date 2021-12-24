import java.util.*;

/**
 * Physik-Utilityklasse
 * 
 * @author Lasse Huber-Saffer
 * @version 21.12.2021
 */
public class Physics
{
    /**
     * Führt eine Strecke von einem Startpunkt zu einem Zielpunkt innerhalb einer Map, und gibt geordnet alle Kollisionen zurück, die den gesetzten Filtern entsprechen
     * @param source Quelle des Strahls
     * @param target Ziel des Strahls
     * @param map GridMap, innerhalb derer die Kollisionen stattfinden (beschränkt auf aktiven Raum)
     * @param terminationFilter Set aller PhysicsLayer, die bei Kollision zum Abbruch des Raycasts führen
     * @param exclusionFilter Set aller PhysicsLayer, die vom Strahl ignoriert werden
     * @return geordnete Liste aller Raycast-Treffer in der Reihenfolge des Auftreffens von der Quelle aus
     */
    public static ArrayList<RaycastHit> raycast(Vector2 source, Vector2 target, GridMap map, EnumSet<PhysicsLayer> terminationFilter, EnumSet<PhysicsLayer> exclusionFilter)
    {
        // Filter null-checken
        if(terminationFilter == null) terminationFilter = EnumSet.noneOf(PhysicsLayer.class);
        if(exclusionFilter == null) exclusionFilter = EnumSet.noneOf(PhysicsLayer.class);
        
        // Collider des Raumes beschaffen
        Room room = map.rooms.get(map.activeRoom);
        ArrayList<ICollider> mapColliders = new ArrayList<ICollider>();
        mapColliders.addAll(room.getStaticColliders());
        mapColliders.addAll(room.getEntityColliders());
        mapColliders.addAll(map.globalColliders);
        
        // Tür-Collider zur Liste hinzufügen
        for(IDoorGameObject door : room.getDoors())
        {
            ICollider doorCollider = door.getCollider();
            if(doorCollider != null)
            {
                mapColliders.add(doorCollider);
            }
        }
        
        // Ray konstruieren
        LineCollider ray = new LineCollider(source, target, PhysicsLayer.RAYCAST);
        
        // Rückgabeliste erstellen
        ArrayList<RaycastHit> result = new ArrayList<RaycastHit>();
        
        // Determine Intersection points
        for(ICollider collider : mapColliders)
        {
            // Exklusionsfilter anwenden
            if(exclusionFilter.contains(collider.getLayer())) continue;
            
            // Auf Kollision prüfen
            if(ray.intersects(collider))
            {
                Vector2 hitPos = null;
                
                // Collider-Typen unterschiedlich behandeln:
                if(collider instanceof LineCollider)
                {
                    hitPos = new Vector2(ray.getLineIntersection((LineCollider)collider));
                }
                // Für CircleCollider: Nur den nächsten Schnittpunkt des Kreises mit dem Ray als Hit speichern
                else if(collider instanceof CircleCollider)
                {
                    LineCircleIntersection intersection = ray.getCircleIntersection((CircleCollider)collider);
                    
                    if(intersection.type == LineCircleIntersectionType.TANGENT || intersection.type == LineCircleIntersectionType.HALF_INTERSECTION)
                    {
                        hitPos = new Vector2(intersection.pos1);
                    }
                    else if(intersection.type == LineCircleIntersectionType.FULL_INTERSECTION)
                    {
                        double dist1 = intersection.pos1.subtract(source).getLength();
                        double dist2 = intersection.pos2.subtract(source).getLength();
                        
                        // Anhand der Distanz entscheiden, welcher der zwei Schnittpunkte für den Hit gewählt wird
                        if(dist1 == dist2 || dist1 < dist2)
                        {
                            hitPos = new Vector2(intersection.pos1);
                        }
                        else
                        {
                            hitPos = new Vector2(intersection.pos2);
                        }
                    }
                }
                
                // Wenn es einen Hit gibt: Zur Liste hinzufügen
                if(hitPos != null)
                {
                    result.add(new RaycastHit(collider, hitPos));
                }
            }
        }
        
        // Rückgabeliste aufsteigend nach Distanz sortieren
        Collections.sort(result,
            // Inline-Distanzkomparator
            new Comparator<RaycastHit>()
            {
                @Override
                public int compare(RaycastHit a, RaycastHit b)
                {
                    Double distA = a.position.subtract(source).getLength();
                    Double distB = b.position.subtract(source).getLength();
                    
                    return distA.compareTo(distB);
                }
            }
        );
        
        // Terminationsfilter anwenden
        int terminationIndex = result.size();
        for(int i = 0; i < result.size(); i++)
        {
            if(terminationFilter.contains(result.get(i).collider.getLayer()))
            {
                terminationIndex = i+1;
                break;
            }
        }
        
        // Subliste aus Termination bilden
        result = new ArrayList<RaycastHit>(result.subList(0, terminationIndex));
        
        return result;
    }
    
    /**
     * Führt eine Strecke von einem Startpunkt eine bestimmte Länge entlang eines Richtungsvektors, und gibt geordnet alle Kollisionen zurück, die den gesetzten Filtern entsprechen
     * @param source Quelle des Strahls
     * @param direction Richtung des Strahls
     * @param maxDistance
     * @param map GridMap, innerhalb derer die Kollisionen stattfinden (beschränkt auf aktiven Raum)
     * @param terminationFilter Set aller PhysicsLayer, die bei Kollision zum Abbruch des Raycasts führen
     * @param exclusionFilter Set aller PhysicsLayer, die vom Strahl ignoriert werden
     * @return geordnete Liste aller Raycast-Treffer in der Reihenfolge des Auftreffens von der Quelle aus
     */
    public static ArrayList<RaycastHit> raycast(Vector2 source, Vector2 direction, double maxDistance, GridMap map, EnumSet<PhysicsLayer> terminationFilter, EnumSet<PhysicsLayer> exclusionFilter)
    {
        Vector2 target = source.add(direction.normalize().multiply(maxDistance));
        return raycast(source, target, map, terminationFilter, exclusionFilter);
    }
}
