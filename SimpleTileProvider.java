import java.util.*;

/**
 * Einfacher TileProvider, der für die Tile immer das selbe Mesh platziert
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
 */
public class SimpleTileProvider implements ITileProvider
{
    private Mesh _mesh;
    private TurtleColor _color;

    /**
     * Konstruktor für Objekte der Klasse SimpleTileProvider
     * @param mesh Mesh, das für die Vorlage verwendet werden soll
     * @param color Farbe, die die Vorlage haben soll
     */
    public SimpleTileProvider(Mesh mesh, TurtleColor color)
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
        result.add(
            new StaticGameObject(
                getMesh(), getColor(),
                new Vector3((x + 0.5) * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 0.5) * MapHandler.TILE_WIDTH),
                new Vector3(), new Vector3(1.0, 1.0, 1.0)
            )
        );
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
    public TurtleColor getColor()
    {
        return _color;
    }
}
