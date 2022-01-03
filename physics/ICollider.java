package physics;


/**
 * Interface f�r zweidimensionale Physik-Collider
 * 
 * @author Lasse Huber-Saffer
 * @version 20.12.2021
 */

public interface ICollider
{
    /**
     * Pr�ft, ob dieser Collider und ein anderer sich �berschneiden.
     * Im Falle einer �berschneidung wird die Methode onCollision beider Listener ausgef�hrt.
     * @param other Collider, zu dem die �berschneidung gepr�ft werden soll
     * @return true, wenn die Collider sich �berschneiden, sonst false
     */
    public boolean detectCollision(ICollider other);
    
    /**
     * Pr�ft, ob dieser Collider und ein anderer sich �berschneiden.
     * @param other Collider, zu dem die �berschneidung gepr�ft werden soll
     * @return true, wenn die Collider sich �berschneiden, sonst false
     */
    public boolean intersects(ICollider other);
    
    /**
     * Setzt die Physik-Ebene, auf der dieser Collider agiert
     * @param layer Physik-Ebene, auf der dieser Collider agiert
     */
    public void setLayer(PhysicsLayer layer);
    
    /**
     * Gibt die Physik-Ebene, auf der dieser Collider agiert, zur�ck
     * @return Physik-Ebene, auf der dieser Collider agiert
     */
    public PhysicsLayer getLayer();
    
    /**
     * Gibt den Kollisions-Listener dieses Colliders zur�ck.
     * Der Kollisionslistener bietet Callback-Methoden f�r Kollisionen und Kollisionsaufl�sungen.
     * @return Instanz des Kollisionslisteners, null, wenn keiner gesetzt ist
     */
    public ICollisionListener getListener();
    
    /**
     * Setzt den Kollisions-Listener dieses Colliders.
     * Der Kollisionslistener bietet Callback-Methoden f�r Kollisionen und Kollisionsaufl�sungen.
     * @param listener zu verwendender Listener
     */
    public void setListener(ICollisionListener listener);
}