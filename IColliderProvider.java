import java.util.*;

/**
 * Interface f�r ColliderProvider-Typen, die in Abh�ngigkeit einer Tile-Umgebung Collider bereitstellen
 * 
 * @author Lasse Huber-Saffer
 * @version 20.12.2021
 */

public interface IColliderProvider
{
    /**
     * Gibt die Collider zur�ck, die der ColliderProvider in der gegebenen Umgebung generiert
     * @param env Umgebung der Tile
     * @param x x-Position der Tile
     * @param y y-Position der Tile
     * @return Liste an Collidern, die von der Tile platziert werden
     */
    public ArrayList<ICollider> getColliders(TileEnvironment env, int x, int z);
    
    /**
     * Gibt zur�ck, ob der ColliderProvider ein TileEnvironment als Parameter der Funktion getColliders bekommen soll, oder nicht, da nicht jeder TileProvider-Typ diesen Parametertyp ben�tigt
     * @return true, wenn dieser ColliderProvider als Parameter ein TileEnvironment ungleich null ben�tigt, sonst false
     */
    public boolean requiresEnvironment();
}
