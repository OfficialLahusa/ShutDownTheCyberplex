
/**
 * Interface f�r Partikelsysteme, also unsichtbare Objekte, die Partikel emittieren und verwalten.
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
 */

public interface IParticleSystem extends IDynamicGameObject
{
    /**
     * Gibt die Anzahl der aktuell simulierten Partikel zur�ck
     * @return Anzahl der aktuell simulierten Partikel
     */
    public int getParticleCount();
    
    /**
     * Entfernt alle Partikel
     */
    public void clear();
}
