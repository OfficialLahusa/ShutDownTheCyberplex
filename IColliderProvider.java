import java.util.*;

/**
 * Interface für ColliderProvider-Typen, die in Abhängigkeit einer Tile-Umgebung Collider bereitstellen
 * 
 * @author Lasse Huber-Saffer
 * @version 20.12.2021
 */

public interface IColliderProvider
{
    /**
     * Gibt die Collider zurück, die der ColliderProvider in der gegebenen Umgebung generiert
     * @param env Umgebung der Tile
     * @param x x-Position der Tile
     * @param y y-Position der Tile
     * @return Liste an Collidern, die von der Tile platziert werden
     */
    public ArrayList<ICollider> getColliders(TileEnvironment env, int x, int z);
    
    /**
     * Gibt zurück, ob der ColliderProvider ein TileEnvironment als Parameter der Funktion getColliders bekommen soll, oder nicht, da nicht jeder TileProvider-Typ diesen Parametertyp benötigt
     * @return true, wenn dieser ColliderProvider als Parameter ein TileEnvironment ungleich null benötigt, sonst false
     */
    public boolean requiresEnvironment();
}
