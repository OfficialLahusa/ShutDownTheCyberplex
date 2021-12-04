import java.util.*;

/**
 * Interface für TileProvider-Typen, die in Abhängigkeit einer Tile-Umgebung GameObjects bereitstellen
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */

public interface ITileProvider
{
    /**
     * Gibt die StaticGameObjects zurück, die der TileProvider in der gegebenen Umgebung generiert
     * @param env Umgebung der Tile
     * @param x x-Position der Tile
     * @param y y-Position der Tile
     * @return Liste an statischen GameObjects, die von der Tile platziert werden
     */
    public ArrayList<StaticGameObject> getStaticTileObjects(TileEnvironment env, int x, int z, double tileWidth);
    
    /**
     * Gibt zurück, ob der TileProvider ein TileEnvironment als Parameter der Funktion getStaticTileObject bekommen soll, oder nicht, da nicht jeder TileProvider-Typ diesen Parametertyp benötigt
     * @return Wahrheitswert der Aussage "Dieser TileProvider benötigt als Parameter ein TileEnvironment ungleich null"
     */
    public boolean requiresEnvironment();
}
