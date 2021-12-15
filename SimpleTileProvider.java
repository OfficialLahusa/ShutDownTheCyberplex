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
     * @see ITileProvider#getTileObjects()
     */
    public ArrayList<IGameObject> getTileObjects(TileEnvironment env, int x, int z)
    {
        ArrayList<IGameObject> result = new ArrayList<IGameObject>();
        result.add(new StaticGameObject(getMesh(), getColor(), new Vector3((x + 0.5) * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 0.5) * MapHandler.TILE_WIDTH)));
        return result;
    }
    
    /**
     * @see ITileProvider#requiresEnvironment()
     */
    public boolean requiresEnvironment()
    {
        return false;
    }
    
    /**
     * Gibt eine neue Instanz des Meshs des Providers zurück
     * @return neue Instanz des Meshs des Providers
     */
    public Mesh getMesh()
    {
        return new Mesh(_mesh);
    }
    
    /**
     * Gibt die Farbe des Providers zurück
     * @return Farbe des Providers
     */
    public String getColor()
    {
        return _color;
    }
}
