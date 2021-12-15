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
    public static final boolean MIRROR_Z_AXIS = true;
    public static final double TILE_WIDTH = 8.0;
    
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
        _tileProviders.put(Tile.DIRT_FLOOR, new SimpleLODTileProvider(dirtFloorLODs, "orange"));
        
        // Brick wall
        _tileProviders.put(Tile.BRICK_WALL, new WallTileProvider(_objLoader.loadFromFile("./res/models/brick_wall.obj"), "grau", _objLoader.loadFromFile("./res/models/simple_wall_pillar.obj"), "grau"));
        
        // Wooden door
        ArrayList<Pair<Mesh, String>> woodenDoorClosed = new ArrayList<Pair<Mesh, String>>();
        woodenDoorClosed.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/wooden_door.obj"), "orange"));
        woodenDoorClosed.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/wooden_door_handle.obj"), "gelb"));
        ArrayList<Pair<Mesh, String>> woodenDoorOpen = new ArrayList<Pair<Mesh, String>>();
        woodenDoorOpen.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/wooden_door_open.obj"), "orange"));
        woodenDoorOpen.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/wooden_door_handle_open.obj"), "gelb"));
        _tileProviders.put(Tile.WOODEN_DOOR, new DoorTileProvider(woodenDoorClosed, woodenDoorOpen, _tileProviders.get(Tile.DIRT_FLOOR), (WallTileProvider)_tileProviders.get(Tile.BRICK_WALL), true));
        
        // Dirt floor grass
        ArrayList<Pair<Mesh, String>> dirtFloorGrass = new ArrayList<Pair<Mesh, String>>();
        dirtFloorGrass.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/dirt_floor_borderless.obj"), "orange"));
        dirtFloorGrass.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/dirt_floor_grassdetail.obj"), "gruen"));
        _tileProviders.put(Tile.DIRT_FLOOR_GRASS, new MultiMeshTileProvider(dirtFloorGrass));
        
        // Dirt floor grass 2
        ArrayList<Pair<Mesh, String>> dirtFloorGrass2 = new ArrayList<Pair<Mesh, String>>();
        dirtFloorGrass2.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/dirt_floor_borderless.obj"), "orange"));
        dirtFloorGrass2.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/dirt_floor_grassdetail2.obj"), "gruen"));
        dirtFloorGrass2.add(new Pair<Mesh, String>(_objLoader.loadFromFile("./res/models/dirt_floor_stonedetail.obj"), "dunkelgrau"));
        _tileProviders.put(Tile.DIRT_FLOOR_GRASS2, new MultiMeshTileProvider(dirtFloorGrass2));
    }
    
    /**
     * Lädt eine Map
     * @param mapName Name der Map (KEIN Pfad)
     */
    public void load(String mapName)
    {
        _map = _csvLoader.loadFromFile(MAP_DIRECTORY + mapName + TILE_LAYER_SUFFIX, MAP_DIRECTORY + mapName + FUNCTION_LAYER_SUFFIX);
        _map.populate(_tileProviders);
    }
    
    public GridMap getMap()
    {
        return _map;
    }
    
    public static Vector2i worldPosToTilePos(Vector3 worldPos)
    {
        //new Vector3((x + 0.5) * tileWidth, 0.0, (mirrorZAxis ? -1 : 1) * (z + 0.5) * tileWidth)
        return new Vector2i((int)Math.round(worldPos.getX() / TILE_WIDTH - 0.5), (int)Math.round((MIRROR_Z_AXIS ? -1 : 1) * worldPos.getZ() / TILE_WIDTH - 0.5));
    }
}
