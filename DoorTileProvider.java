import java.util.*;
import javafx.util.*;

/**
 * TileProvider für Türen, die in Abhängigkeit von der Umgebung entlang der X- oder Z-Achse ausgerichtet sein können
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */
public class DoorTileProvider implements ITileProvider
{
    private ArrayList<Pair<Mesh, String>> _coloredMeshesClosed;
    private ArrayList<Pair<Mesh, String>> _coloredMeshesOpen;
    private ITileProvider _floorTileProvider;
    private WallTileProvider _wallTileProvider;
    private boolean _isOpen;
    
    /**
     * Konstruktor für Objekte der Klasse DoorTileProvider
     * @param coloredMeshesClosed Liste von Mesh-Farb-Paaren, die im geschlossenen Zustand gerendert werden
     * @param coloredMeshesOpen Liste von Mesh-Farb-Paaren, die im offenen Zustand gerendert werden
     * @param isOpen Offenheit der Tür
     */
    public DoorTileProvider(ArrayList<Pair<Mesh, String>> coloredMeshesClosed, ArrayList<Pair<Mesh, String>> coloredMeshesOpen, boolean isOpen)
    {
        this(coloredMeshesClosed, coloredMeshesOpen, null, null, isOpen);
    }
    
    /**
     * Konstruktor für Objekte der Klasse DoorTileProvider
     * @param coloredMeshesClosed Liste von Mesh-Farb-Paaren, die im geschlossenen Zustand gerendert werden
     * @param coloredMeshesOpen Liste von Mesh-Farb-Paaren, die im offenen Zustand gerendert werden
     * @param isOpen Offenheit der Tür
     */
    public DoorTileProvider(ArrayList<Pair<Mesh, String>> coloredMeshesClosed, ArrayList<Pair<Mesh, String>> coloredMeshesOpen, ITileProvider floorTileProvider, WallTileProvider wallTileProvider, boolean isOpen)
    {
        _coloredMeshesClosed = coloredMeshesClosed;
        _coloredMeshesOpen = coloredMeshesOpen;
        _floorTileProvider = floorTileProvider;
        _wallTileProvider = wallTileProvider;
        _isOpen = isOpen;
    }
    
        
    /**
     * @see ITileProvider#getTileObjects()
     */
    public ArrayList<IGameObject> getTileObjects(TileEnvironment env, int x, int z)
    {
        ArrayList<IGameObject> result = new ArrayList<IGameObject>();
        
        boolean facingZ = (Tile.isSolidOrNone(env.px) && Tile.isSolidOrNone(env.nx));
        
        ArrayList<IGameObject> floorGeometry = null, wallGeometry = null;
        if(_floorTileProvider != null)
        {
            floorGeometry = new ArrayList<IGameObject>();
            floorGeometry.addAll(_floorTileProvider.getTileObjects(env, x, z));
        }
        if(_wallTileProvider != null)
        {
            wallGeometry = new ArrayList<IGameObject>();
            if(!facingZ)
            {
                wallGeometry.add(_wallTileProvider.getWallVariant(1, x, z));
                wallGeometry.add(_wallTileProvider.getWallVariant(3, x, z));
            }
            else
            {
                wallGeometry.add(_wallTileProvider.getWallVariant(0, x, z));
                wallGeometry.add(_wallTileProvider.getWallVariant(2, x, z));
            }
        }
        
        result.add(new DoorGameObject(_coloredMeshesClosed, _coloredMeshesOpen,
            facingZ, _isOpen, floorGeometry, wallGeometry, new Vector2i(x, z),
            new Vector3((x + 0.5) * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 0.5) * MapHandler.TILE_WIDTH),
            new Vector3(0.0, (facingZ)? 90.0 : 0.0, 0.0), new Vector3(1.0, 1.0, 1.0))
        );
        
        return result;
    }
    
    /**
     * @see ITileProvider#requiresEnvironment()
     */
    public boolean requiresEnvironment()
    {
        return true;
    }
    
    /**
     * Gibt eine Referenz zur Liste der Mesh-Farb-Paare des Providers zurück, die im geschlossenen Zustand verwendet werden
     * @return Referenz zur Liste der Mesh-Farb-Paare, die im geschlossenen Zustand verwendet werden
     */
    public ArrayList<Pair<Mesh, String>> getColoredMeshesClosed()
    {
        return _coloredMeshesClosed;
    }
    
    /**
     * Gibt eine Referenz zur Liste der Mesh-Farb-Paare des Providers zurück, die im offenen Zustand verwendet werden
     * @return Referenz zur Liste der Mesh-Farb-Paare, die im offenen Zustand verwendet werden
     */
    public ArrayList<Pair<Mesh, String>> getColoredMeshesOpen()
    {
        return _coloredMeshesOpen;
    }
}
