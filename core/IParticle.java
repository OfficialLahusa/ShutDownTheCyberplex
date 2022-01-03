package core;

/**
 * Einzelpartikel eines Partikelsystems
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
 */

public interface IParticle extends IDynamicGameObject
{
    /**
     * Gibt zurück, ob der Partikel noch lebendig ist
     * @return true, wenn Partikel noch lebendig ist, sonst false
     */
    public boolean isAlive();
    
    /**
     * Gibt die bisher abgelaufene Zeit seit Erstellung des Partikels zurück
     * @return bisher abgelaufene Zeit seit Erstellung des Partikels
     */
    public double getElapsedTime();
    
    /**
     * Tötet diesen Partikel
     */
    public void kill();
}
