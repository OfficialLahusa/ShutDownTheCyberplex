
/**
 * Interface für Levels-Of-Detail GameObjects, also GameObjects, die ihr Mesh in Abhängigkeit von der Distanz ändern
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */

public interface ILODGameObject
{
    /**
     * Updated den Levels-of-Detail-Bezugspunkt des GameObjects
     * @param cameraPosition Position der Kamera, die als Bezugspunkt der LOD-Berechnung genutzt wird
     */
    public void updateLOD(Vector3 cameraPosition);
    
    /**
     * Gibt das Level-of-Detail, das unter dem aktuellen Bezugspunkt genutzt wird, zurück
     * @return Level-of-Detail, das vom GameObject genutzt wird. Je höher, desto weiter ist das Mesh vom Originalmesh entfernt.
     */
    public int getLODLevel();
}
