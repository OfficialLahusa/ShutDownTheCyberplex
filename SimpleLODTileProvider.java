import java.util.*;
import javafx.util.*;

/**
 * Einfacher TileProvider, der für die Tile immer das selbe Mesh platziert
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
 */
public class SimpleLODTileProvider implements ITileProvider
{
    private ArrayList<Pair<Double, Mesh>> _lodLevels;
    private TurtleColor _color;

    /**
     * Konstruktor für Objekte der Klasse SimpleLODTileProvider
     * @param mesh Mesh, das für die Vorlage verwendet werden soll
     * @param color Farbe, die die Vorlage haben soll
     */
    public SimpleLODTileProvider(ArrayList<Pair<Double, Mesh>> lodLevels, TurtleColor color)
    {
        _lodLevels = lodLevels;
        _color = color;
    }
    
        
    /**
     * @see ITileProvider#getTileObjects()
     */
    public ArrayList<IGameObject> getTileObjects(TileEnvironment env, int x, int z)
    {
        ArrayList<IGameObject> result = new ArrayList<IGameObject>();
        result.add(
            new StaticLODGameObject(
                getLODLevels(), getColor(),
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
     * Gibt eine neue Instanz des Meshs der Vorlage zurück
     * @return neue Instanz des Meshs der Vorlage
     */
    public ArrayList<Pair<Double, Mesh>> getLODLevels()
    {
        return _lodLevels;
    }
    
    /**
     * Gibt die Farbe der Vorlage zurück
     * @return Farbe der Vorlage
     */
    public TurtleColor getColor()
    {
        return _color;
    }
}
