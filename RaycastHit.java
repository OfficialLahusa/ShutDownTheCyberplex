
/**
 * Einzeltreffer eines Raycasts
 * 
 * @author Lasse Huber-Saffer
 * @version 21.12.2021
 */
public class RaycastHit
{
    /**
     * Collider, der getroffen wurde
     */
    public ICollider collider;
    /**
     * Ort, an dem der Treffer stattfand
     */
    public Vector2 position;
    
    /**
     * Expliziter parameterloser Konstruktor
     */
    public RaycastHit()
    {
        collider = null;
        position = null;
    }
    
    /**
     * Vollständiger Konstruktor
     * @param collider Collider, der getroffen wurde
     * @param position Ort, an dem der Treffer stattfand
     */
    public RaycastHit(ICollider collider, Vector2 position)
    {
        this.collider = collider;
        this.position = position;
    }
}
