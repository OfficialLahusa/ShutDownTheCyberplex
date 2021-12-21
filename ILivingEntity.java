
/**
 * Tragen Sie hier eine Beschreibung des Interface ILivingEntity ein.
 * 
 * @author Lasse Huber-Saffer
 * @version (eine Versionsnummer oder ein Datum)
 */

public interface ILivingEntity
{
    /**
     * Gibt die Lebenspunkte der Entit�t zur�ck.
     * @return die Lebenspunkte
     */
    public int getHealth();
    
    /**
     * Setzt die Lebenspunkte der Entit�t.
     * @param amount Menge an Lebenspunkten (>= 0)
     */
    public void setHealth(int amount);
    
    /**
     * Gibt die maximalen Lebenspunkte der Entit�t zur�ck.
     * @return maximale Lebenspunkte
     */
    public int getMaxHealth();
    
    /**
     * Setzt die maximalen Lebenspunkte der Entit�t.
     * @param amount maximale Menge an Lebenspunkten (>= 0)
     */
    public void setMaxHealth(int amount);
    
    /**
     * F�gt dem Spieler Schaden zu.
     * Seine Lebenspunkte k�nnen 0 nicht unterschreiten
     * @param amount Menge an Lebenspunkten
     */
    public void damage(int amount);
    
    /**
     * Heilt den Spieler.
     * Seine Lebenspunkte k�nnen seine maximalen Lebenspunkte nicht �berschreiten.
     * @param amount Menge an Lebenspunkten
     */
    public void heal(int amount);
}
