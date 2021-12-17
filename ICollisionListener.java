/**
 * Interface für Objekte, die auf Kollisionen reagieren
 * 
 * @author Lasse Huber-Saffer
 * @version 16.12.2021
 */

public interface ICollisionListener
{
    /**
     * Callback-Methode, die im Fall einer Kollision ausgeführt wird
     * @param self Collider, auf dem dieser Listener registriert wird
     * @param other Collider, mit dem self Kollidiert
     */
    public void onCollision(ICollider self, ICollider other);
    
    /**
     * Callback-Methode, die im Fall einer Kollisionsauflösung ausgeführt wird
     * @param self Collider, auf dem dieser Listener registriert wird
     * @param other Collider, mit dem self Kollidiert
     */
    public void onResolution(ICollider self, ICollider other);
}
