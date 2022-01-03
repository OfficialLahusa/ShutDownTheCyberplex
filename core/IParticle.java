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
     * Gibt zur�ck, ob der Partikel noch lebendig ist
     * @return true, wenn Partikel noch lebendig ist, sonst false
     */
    public boolean isAlive();
    
    /**
     * Gibt die bisher abgelaufene Zeit seit Erstellung des Partikels zur�ck
     * @return bisher abgelaufene Zeit seit Erstellung des Partikels
     */
    public double getElapsedTime();
    
    /**
     * T�tet diesen Partikel
     */
    public void kill();
}
