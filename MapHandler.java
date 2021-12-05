import java.util.*;

/**
 * Verwaltet verschiedene GridMaps und stellt zusätzliche Funktionalität bereit
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */
public class MapHandler
{
    private GridMap _map;
    private CSVMapLoader _csvLoader;
    private WavefrontObjectLoader _objLoader;
    private ArrayList<ITileProvider> _tileProviders;
    
    private static final String MAP_DIRECTORY = "./res/maps/";
    private static final String TILE_LAYER_SUFFIX = "_tile.csv";
    private static final String FUNCTION_LAYER_SUFFIX = "_function.csv";
    
    /**
     * Konstruktor für Objekte der Klasse MapHandler
     */
    public MapHandler(WavefrontObjectLoader objLoader)
    {
        _map = null;
        _csvLoader = new CSVMapLoader();
        _objLoader = objLoader;
        
        _tileProviders = new ArrayList<ITileProvider>();
        _tileProviders.add(new SimpleTileProvider(_objLoader.loadFromFile("./res/models/dirt_floor.obj"), "orange"));
        _tileProviders.add(new WallTileProvider(_objLoader.loadFromFile("./res/models/brick_wall.obj"), "grau", _objLoader.loadFromFile("./res/models/simple_wall_pillar.obj"), "gruen"));
    }
    
    public void load(String mapName)
    {
        _map = _csvLoader.loadFromFile(MAP_DIRECTORY + mapName + TILE_LAYER_SUFFIX, MAP_DIRECTORY + mapName + FUNCTION_LAYER_SUFFIX);
        _map.populate(_tileProviders);
    }
    
    public GridMap getMap()
    {
        return _map;
    }

    public static boolean isTileSolid(int tileType)
    {
        return tileType == 1;    
    }
    
    public static boolean isTileNone(int tileType)
    {
        return tileType == -1;
    }
    
    public static boolean isTileSolidOrNone(int tileType)
    {
        return isTileSolid(tileType) || isTileNone(tileType);
    }
    
    public static boolean isTilePassableOrNone(int tileType)
    {
        return !isTileSolid(tileType) || isTileNone(tileType);
    }
}
