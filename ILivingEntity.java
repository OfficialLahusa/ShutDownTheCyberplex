
/**
 * Tragen Sie hier eine Beschreibung des Interface ILivingEntity ein.
 * 
 * @author Lasse Huber-Saffer
 * @version (eine Versionsnummer oder ein Datum)
 */

public interface ILivingEntity
{
    /**
     * Gibt die Lebenspunkte der Entität zurück.
     * @return die Lebenspunkte
     */
    public int getHealth();
    
    /**
     * Setzt die Lebenspunkte der Entität.
     * @param amount Menge an Lebenspunkten (>= 0)
     */
    public void setHealth(int amount);
    
    /**
     * Gibt die maximalen Lebenspunkte der Entität zurück.
     * @return maximale Lebenspunkte
     */
    public int getMaxHealth();
    
    /**
     * Setzt die maximalen Lebenspunkte der Entität.
     * @param amount maximale Menge an Lebenspunkten (>= 0)
     */
    public void setMaxHealth(int amount);
    
    /**
     * Fügt dem Spieler Schaden zu.
     * Seine Lebenspunkte können 0 nicht unterschreiten
     * @param amount Menge an Lebenspunkten
     */
    public void damage(int amount);
    
    /**
     * Heilt den Spieler.
     * Seine Lebenspunkte können seine maximalen Lebenspunkte nicht überschreiten.
     * @param amount Menge an Lebenspunkten
     */
    public void heal(int amount);
}
