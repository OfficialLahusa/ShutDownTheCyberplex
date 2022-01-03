package game;


/**
 * Interface f�r Tile-Filter, die jedem Tile-Wert einen booleschen Wert zuordnet
 * 
 * @author Lasse Huber-Saffer
 * @version 26.12.2021
 */

public interface ITileFilter
{
    /**
     * Wertet den Filter f�r einen gegebenen Tile-Wert aus
     * @param tileValue Wert der Tile
     * @return boolesches Ergebnis des Filters
     */
    public boolean evaluate(int tileValue);
}
