import java.util.*;

/**
 * Verwaltet verschiedene GridMaps und stellt zusätzliche Funktionalität bereit, insbesondere zur Performance-Optimierung
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */
public class MapHandler
{
    private GridMap _map;
    private CSVMapLoader _csvLoader;
    private WavefrontObjectLoader _objLoader;
    private LODGenerator _lodGenerator;
    private HashMap<Integer, ITileProvider> _tileProviders;
    
    private static final String MAP_DIRECTORY = "./res/maps/";
    private static final String TILE_LAYER_SUFFIX = "_tile.csv";
    private static final String FUNCTION_LAYER_SUFFIX = "_function.csv";    
    private static final boolean MIRROR_Z_AXIS = true;
    
    /**
     * Konstruktor für Objekte der Klasse MapHandler
     */
    public MapHandler()
    {
        _map = null;
        _csvLoader = new CSVMapLoader();
        _objLoader = new WavefrontObjectLoader();
        _lodGenerator = new LODGenerator();
        
        _tileProviders = new HashMap<Integer, ITileProvider>();
        _tileProviders.put(0, new SimpleLODTileProvider(_lodGenerator.createBasicFloorTileLOD(_objLoader.loadFromFile("./res/models/dirt_floor.obj"), 40.0), "orange"));
        _tileProviders.put(1, new WallTileProvider(_objLoader.loadFromFile("./res/models/brick_wall.obj"), "grau", _objLoader.loadFromFile("./res/models/simple_wall_pillar.obj"), "grau"));
    }
    
    public void load(String mapName)
    {
        _map = _csvLoader.loadFromFile(MAP_DIRECTORY + mapName + TILE_LAYER_SUFFIX, MAP_DIRECTORY + mapName + FUNCTION_LAYER_SUFFIX);
        _map.populate(_tileProviders, MIRROR_Z_AXIS);
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
