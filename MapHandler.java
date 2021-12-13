import java.util.*;
import javafx.util.*;

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
        
        // Dirt floor
        ArrayList<Pair<Double, Mesh>> dirtFloorLODs = new ArrayList<Pair<Double, Mesh>>();
        dirtFloorLODs.add(new Pair<Double, Mesh>(0.0, _objLoader.loadFromFile("./res/models/dirt_floor_borderless.obj")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(30.0, _objLoader.loadFromFile("./res/models/dirt_floor_borderless_lod1.obj")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(40.0, _objLoader.loadFromFile("./res/models/dirt_floor_borderless_lod2.obj")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(50.0, _objLoader.loadFromFile("./res/models/dirt_floor_borderless_lod3.obj")));
        _tileProviders.put(0, new SimpleLODTileProvider(dirtFloorLODs, "orange"));
        // Brick wall
        _tileProviders.put(1, new WallTileProvider(_objLoader.loadFromFile("./res/models/brick_wall.obj"), "grau", _objLoader.loadFromFile("./res/models/simple_wall_pillar.obj"), "grau"));
        // Wooden door
        ArrayList<Pair<Mesh, String>> woodenDoor = new ArrayList<Pair<Mesh, String>>();
        woodenDoor.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/wooden_door.obj"), "orange"));
        woodenDoor.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/wooden_door_handle.obj"), "gelb"));
        _tileProviders.put(2, new DoorTileProvider(woodenDoor));
        // Dirt floor grass
        ArrayList<Pair<Mesh, String>> dirtFloorGrass = new ArrayList<Pair<Mesh, String>>();
        dirtFloorGrass.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/dirt_floor_borderless.obj"), "orange"));
        dirtFloorGrass.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/dirt_floor_grassdetail.obj"), "gruen"));
        _tileProviders.put(20, new MultiMeshTileProvider(dirtFloorGrass));
        // Dirt floor grass 2
        ArrayList<Pair<Mesh, String>> dirtFloorGrass2 = new ArrayList<Pair<Mesh, String>>();
        dirtFloorGrass2.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/dirt_floor_borderless.obj"), "orange"));
        dirtFloorGrass2.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/dirt_floor_grassdetail2.obj"), "gruen"));
        dirtFloorGrass2.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/dirt_floor_stonedetail.obj"), "dunkelgrau"));
        _tileProviders.put(40, new MultiMeshTileProvider(dirtFloorGrass2));
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
