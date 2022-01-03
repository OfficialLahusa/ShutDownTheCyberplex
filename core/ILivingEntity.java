package core;


/**
 * Tragen Sie hier eine Beschreibung des Interface ILivingEntity ein.
 * 
 * @author Lasse Huber-Saffer
 * @version 23.12.2021
 */

public interface ILivingEntity
{
    /**
     * Fügt der Entity Schaden zu.
     * Seine Lebenspunkte können 0 nicht unterschreiten
     * @param amount Menge an Lebenspunkten
     * @param source Bezeichung der Schadensquelle (z.B. "turret shot")
     */
    public void damage(int amount, String source);
    
    /**
     * Heilt die Entity.
     * Seine Lebenspunkte können seine maximalen Lebenspunkte nicht überschreiten.
     * @param amount Menge an Lebenspunkten
     * @param source Bezeichnung der Heilungsursache (z.B. "health powerup")
     */
    public void heal(int amount, String source);
    
    /**
     * Gibt zurück, ob die Entity aktuell lebt.
     * @return true, wenn Entity lebt, sonst false
     */
    public boolean isAlive();
    
    /**
     * Gibt die Lebenspunkte der Entity zurück.
     * @return die Lebenspunkte
     */
    public int getHealth();
    
    /**
     * Setzt die Lebenspunkte der Entity.
     * @param amount Menge an Lebenspunkten (>= 0)
     */
    public void setHealth(int amount);
    
    /**
     * Gibt die maximalen Lebenspunkte der Entity zurück.
     * @return maximale Lebenspunkte
     */
    public int getMaxHealth();
    
    /**
     * Setzt die maximalen Lebenspunkte der Entity.
     * @param amount maximale Menge an Lebenspunkten (>= 0)
     */
    public void setMaxHealth(int amount);
}
