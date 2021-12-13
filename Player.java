
/**
 * Die Player Klasse beschreibt den Zustand des Spielers.
 * 
 * @author Sven Schreiber 
 * @version 13.12.2021
 */
public class Player
{
    private int _health;
    
    /**
     * Konstruktor f�r Player
     */
    public Player()
    {
        _health = 100;
    }
    
    /**
     * Setzt die Leben des Spielers
     * 
     * @param amount Die Menge an Lebenspunkten
     */
    public void setHealth(int amount)
    {
        _health = amount;
    }
    
    /**
     * Gibt die Lebenspunkte des Spielers zur�ck
     * 
     * @return die Lebenspunkte
     */
    public int getHealth()
    {
        return _health;
    }
    
}
