
/**
 * Tragen Sie hier eine Beschreibung des Interface ILivingEntity ein.
 * 
 * @author Lasse Huber-Saffer
 * @version 23.12.2021
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
     * @param source Bezeichung der Schadensquelle (z.B. "turret shot")
     */
    public void damage(int amount, String source);
    
    /**
     * Heilt den Spieler.
     * Seine Lebenspunkte k�nnen seine maximalen Lebenspunkte nicht �berschreiten.
     * @param amount Menge an Lebenspunkten
     * @param source Bezeichnung der Heilungsursache (z.B. "health powerup")
     */
    public void heal(int amount, String source);
}
