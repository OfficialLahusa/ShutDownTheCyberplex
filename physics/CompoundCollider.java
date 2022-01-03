package physics;


/**
 * Collider, der aus anderen Collidern zusammengesetzt wird
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
 */
public class CompoundCollider implements ICollider
{
    private ICollider[] _colliders;
    private ICollisionListener _listener;
    private PhysicsLayer _layer;

    /**
     * Konstruktor für Objekte der Klasse CompoundCollider
     * @param colliders Array an Collidern, aus denen der Collider zusammengesetzt werden soll
     * @param layer Physik-Ebene, die allen Unter-Collidern zugewiesen wird 
     */
    public CompoundCollider(ICollider[] colliders, PhysicsLayer layer)
    {
        _colliders = colliders.clone();
        setLayer(layer);
    }

    /**
     * @see ICollider#detectCollision()
     */
    public boolean detectCollision(ICollider other)
    {       
        boolean didIntersect = false;
        
        for(ICollider collider : _colliders)
        {
            if(collider.detectCollision(other))
            {
                didIntersect = true;
            }
        }
        
        return didIntersect;
    }
    
    /**
     * @see ICollider#intersects()
     */
    public boolean intersects(ICollider other)
    {
        for(ICollider collider : _colliders)
        {
            if(collider.intersects(other))
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * @see ICollider#setLayer()
     */
    public void setLayer(PhysicsLayer layer)
    {
        _layer = layer;
        for(ICollider collider : _colliders)
        {
            collider.setLayer(_layer);
        }
    }
    
    /**
     * @see ICollider#getLayer()
     */
    public PhysicsLayer getLayer()
    {
        return _layer;
    }
    
    /**
     * @see ICollider#getListener()
     */
    public ICollisionListener getListener()
    {
        return _listener;
    }
    
    /**
     * @see ICollider#setListener()
     */
    public void setListener(ICollisionListener listener)
    {
        _listener = listener;
        
        for(ICollider collider : _colliders)
        {
            collider.setListener(listener);
        }
    }
    
    /**
     * Gibt die Unter-Collider dieses CompoundColliders zurück
     * @return Array der Unter-Collider
     */
    public ICollider[] getColliders()
    {
        return _colliders;
    }
}
