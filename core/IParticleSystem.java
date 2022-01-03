package core;


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
    
    /**
     * Gibt zur�ck, ob das Partikelsystem fertig simuliert wurde
     * @return true, wenn die Simulation abgeschlossen ist, sonst false
     */
    public boolean isDone();
}
