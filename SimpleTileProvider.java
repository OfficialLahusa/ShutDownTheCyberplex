import java.util.*;

/**
 * Einfacher TileProvider, der für die Tile immer das selbe Mesh platziert
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */
public class SimpleTileProvider implements ITileProvider
{
    private Mesh _mesh;
    private String _color;

    /**
     * Konstruktor für Objekte der Klasse SimpleTileProvider
     * @param mesh Mesh, das für die Vorlage verwendet werden soll
     * @param color Farbe, die die Vorlage haben soll
     */
    public SimpleTileProvider(Mesh mesh, String color)
    {
        _mesh = mesh;
        _color = color;
    }
    
        
    /**
     * Gibt die StaticGameObjects zurück, die der TileProvider in der gegebenen Umgebung generiert
     * @param env Umgebung der Tile
     * @param x x-Position der Tile
     * @param y y-Position der Tile
     * @return Liste an statischen GameObjects, die von der Tile platziert werden
     */
    public ArrayList<StaticGameObject> getStaticTileObjects(TileEnvironment env, int x, int z, double tileWidth)
    {
        ArrayList<StaticGameObject> result = new ArrayList<StaticGameObject>();
        result.add(new StaticGameObject(getMesh(), getColor(), new Vector3((x + 0.5) * tileWidth, 0.0, (z + 0.5) * tileWidth)));
        return result;
    }
    
    /**
     * Gibt zurück, ob der TileProvider ein TileEnvironment als Parameter der Funktion getStaticTileObject bekommen soll, oder nicht, da nicht jeder TileProvider-Typ diesen Parametertyp benötigt
     * @return Wahrheitswert der Aussage "Dieser TileProvider benötigt als Parameter ein TileEnvironment ungleich null"
     */
    public boolean requiresEnvironment()
    {
        return false;
    }
    
    /**
     * Gibt eine neue Instanz des Meshs der Vorlage zurück
     * @return neue Instanz des Meshs der Vorlage
     */
    public Mesh getMesh()
    {
        return new Mesh(_mesh);
    }
    
    /**
     * Gibt die Farbe der Vorlage zurück
     * @return Farbe der Vorlage
     */
    public String getColor()
    {
        return _color;
    }
}
