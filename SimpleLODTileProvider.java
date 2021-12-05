import java.util.*;
import javafx.util.*;

/**
 * Einfacher TileProvider, der f�r die Tile immer das selbe Mesh platziert
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */
public class SimpleLODTileProvider implements ITileProvider
{
    private ArrayList<Pair<Double, Mesh>> _lodLevels;
    private String _color;

    /**
     * Konstruktor f�r Objekte der Klasse SimpleLODTileProvider
     * @param mesh Mesh, das f�r die Vorlage verwendet werden soll
     * @param color Farbe, die die Vorlage haben soll
     */
    public SimpleLODTileProvider(ArrayList<Pair<Double, Mesh>> lodLevels, String color)
    {
        _lodLevels = lodLevels;
        _color = color;
    }
    
        
    /**
     * Gibt die GameObjects zur�ck, die der TileProvider in der gegebenen Umgebung generiert
     * @param env Umgebung der Tile
     * @param x x-Position der Tile
     * @param y y-Position der Tile
     * @param mirrorZAxis gibt an, ob z-Achse der generierten Objekte gespiegelt sein soll
     * @return Liste an GameObjects, die von der Tile platziert werden
     */
    public ArrayList<IGameObject> getTileObjects(TileEnvironment env, int x, int z, double tileWidth, boolean mirrorZAxis)
    {
        ArrayList<IGameObject> result = new ArrayList<IGameObject>();
        result.add(new StaticLODGameObject(getLODLevels(), getColor(), new Vector3((x + 0.5) * tileWidth, 0.0, (mirrorZAxis ? -1 : 1) * (z + 0.5) * tileWidth)));
        return result;
    }
    
    /**
     * Gibt zur�ck, ob der TileProvider ein TileEnvironment als Parameter der Funktion getStaticTileObject bekommen soll, oder nicht, da nicht jeder TileProvider-Typ diesen Parametertyp ben�tigt
     * @return Wahrheitswert der Aussage "Dieser TileProvider ben�tigt als Parameter ein TileEnvironment ungleich null"
     */
    public boolean requiresEnvironment()
    {
        return false;
    }
    
    /**
     * Gibt eine neue Instanz des Meshs der Vorlage zur�ck
     * @return neue Instanz des Meshs der Vorlage
     */
    public ArrayList<Pair<Double, Mesh>> getLODLevels()
    {
        return _lodLevels;
    }
    
    /**
     * Gibt die Farbe der Vorlage zur�ck
     * @return Farbe der Vorlage
     */
    public String getColor()
    {
        return _color;
    }
}
