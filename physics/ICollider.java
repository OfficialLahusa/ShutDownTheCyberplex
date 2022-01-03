package physics;


/**
 * Interface für zweidimensionale Physik-Collider
 * 
 * @author Lasse Huber-Saffer
 * @version 20.12.2021
 */

public interface ICollider
{
    /**
     * Prüft, ob dieser Collider und ein anderer sich überschneiden.
     * Im Falle einer Überschneidung wird die Methode onCollision beider Listener ausgeführt.
     * @param other Collider, zu dem die Überschneidung geprüft werden soll
     * @return true, wenn die Collider sich überschneiden, sonst false
     */
    public boolean detectCollision(ICollider other);
    
    /**
     * Prüft, ob dieser Collider und ein anderer sich überschneiden.
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
    
    /**
     * Gibt den Kollisions-Listener dieses Colliders zurück.
     * Der Kollisionslistener bietet Callback-Methoden für Kollisionen und Kollisionsauflösungen.
     * @return Instanz des Kollisionslisteners, null, wenn keiner gesetzt ist
     */
    public ICollisionListener getListener();
    
    /**
     * Setzt den Kollisions-Listener dieses Colliders.
     * Der Kollisionslistener bietet Callback-Methoden für Kollisionen und Kollisionsauflösungen.
     * @param listener zu verwendender Listener
     */
    public void setListener(ICollisionListener listener);
}