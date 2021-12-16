
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
     * @
     */
    public boolean intersect(ICollider other);
}
