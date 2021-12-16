
/**
 * Interface f�r zweidimensionale Physik-Collider
 * 
 * @author Lasse Huber-Saffer
 * @version 16.12.2021
 */

public interface ICollider
{
    /**
     * Pr�ft, ob dieser Collider und ein anderer sich �berschneiden
     * @
     */
    public boolean intersect(ICollider other);
}
