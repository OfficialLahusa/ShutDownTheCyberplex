
/**
 * Interface für zweidimensionale Physik-Collider
 * 
 * @author Lasse Huber-Saffer
 * @version 16.12.2021
 */

public interface ICollider
{
    /**
     * Prüft, ob dieser Collider und ein anderer sich überschneiden
     * @param other Collider, zu dem die Überschneidung geprüft werden soll
     * @return true, wenn die Collider sich überschneiden, sonst false
     */
    public boolean intersects(ICollider other);
    
    /**
     * Setzt die Physik-Ebene, auf der dieser Collider agiert
     * @param layer Physik-Ebene, auf der dieser Collider agiert
     */
    public void setLayer(PhysicsLayer layer);
    
    /**
     * Gibt die Physik-Ebene, auf der dieser Collider agiert, zurück
     * @return Physik-Ebene, auf der dieser Collider agiert
     */
    public PhysicsLayer getLayer();
}
