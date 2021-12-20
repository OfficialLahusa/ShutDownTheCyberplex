import java.util.*;

/**
 * Physik-Utilityklasse
 * 
 * @author Lasse Huber-Saffer
 * @version 20.12.2021
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
        throw new UnsupportedOperationException("WIP");
    }
}
