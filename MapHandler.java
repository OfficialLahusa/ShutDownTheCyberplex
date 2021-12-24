import java.util.*;
import javafx.util.*;

/**
 * Verwaltet die GridMap und stellt zusätzliche Funktionalität bereit, insbesondere zum Map-Loading und zur Performance-Optimierung
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
 */
public class MapHandler
{
    // Aktuelle Gridmap
    private GridMap _map;
    
    // Aktuelles GameState
    private GameState _state;
    
    // Utility
    private CSVMapLoader _csvLoader;
    private WavefrontObjectLoader _objLoader;
    private LODGenerator _lodGenerator;
    
    // Register der TileProvider für Erstellung der Mapgeometrie
    private HashMap<Integer, ITileProvider> _tileProviders;
    // Register der ColliderProvider für Bestimmung der Collider der Tiles
    private HashMap<Integer, IColliderProvider> _colliderProviders;
    // Namentliches Register von Meshes, die von Entities benutzt werden
    private HashMap<String, Mesh> _entityMeshes;
    
    // File-Loading
    private static final String TILE_LAYER_SUFFIX = "_tile.csv";
    private static final String FUNCTION_LAYER_SUFFIX = "_function.csv";
    
    // Generierungskonstanten
    public static final boolean MIRROR_Z_AXIS = true;
    public static final double TILE_WIDTH = 8.0;
    
    /**
     * Konstruktor für Objekte der Klasse MapHandler
     * @param state Referenz zum GameState
     */
    public MapHandler(GameState state)
    {
        _map = null;
        _state = state;
        
        _csvLoader = new CSVMapLoader();
        _objLoader = new WavefrontObjectLoader();
        _lodGenerator = new LODGenerator();
        
        _tileProviders = new HashMap<Integer, ITileProvider>();
        _colliderProviders = new HashMap<Integer, IColliderProvider>();
        _entityMeshes = new HashMap<String, Mesh>();
        
        // Initialisierung der TileProvider
        // Dirt floor
        ArrayList<Pair<Double, Mesh>> dirtFloorLODs = new ArrayList<Pair<Double, Mesh>>();
        dirtFloorLODs.add(new Pair<Double, Mesh>(0.0, _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_borderless.obj")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(30.0, _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_borderless_lod1.obj")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(40.0, _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_borderless_lod2.obj")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(50.0, _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_borderless_lod3.obj")));
        _tileProviders.put(Tile.DIRT_FLOOR, new SimpleLODTileProvider(dirtFloorLODs, "orange"));
        
        // Brick wall
        _tileProviders.put(Tile.BRICK_WALL, new WallTileProvider(_objLoader.loadFromFile("./res/models/brick_wall.obj"), "grau", _objLoader.loadFromFile("./res/models/simple_wall_pillar.obj"), "grau"));
        
        // Wooden door
        ArrayList<Pair<Mesh, String>> woodenDoorClosed = new ArrayList<Pair<Mesh, String>>();
        woodenDoorClosed.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "wooden_door.obj"), "orange"));
        woodenDoorClosed.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "wooden_door_handle.obj"), "gelb"));
        ArrayList<Pair<Mesh, String>> woodenDoorOpen = new ArrayList<Pair<Mesh, String>>();
        woodenDoorOpen.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "wooden_door_open.obj"), "orange"));
        woodenDoorOpen.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "wooden_door_handle_open.obj"), "gelb"));
        _tileProviders.put(Tile.WOODEN_DOOR, new DoorTileProvider(
            woodenDoorClosed, woodenDoorOpen, false,
            new BlockedTunnelColliderProvider(), new TunnelColliderProvider(),
            _tileProviders.get(Tile.DIRT_FLOOR), (WallTileProvider)_tileProviders.get(Tile.BRICK_WALL),
            _state.soundEngine, "wooden_door_open", "wooden_door_close"
        ));
        
        // Dirt floor grass
        ArrayList<Pair<Mesh, String>> dirtFloorGrass = new ArrayList<Pair<Mesh, String>>();
        dirtFloorGrass.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "dirt_floor_borderless.obj"), "orange"));
        dirtFloorGrass.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "dirt_floor_grassdetail.obj"), "gruen"));
        _tileProviders.put(Tile.DIRT_FLOOR_GRASS, new MultiMeshTileProvider(dirtFloorGrass));
        
        // Cracked brick wall
        ArrayList<Pair<Mesh, String>> secretDoorClosed = new ArrayList<Pair<Mesh, String>>();
        secretDoorClosed.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "wooden_door.obj"), "rot"));
        secretDoorClosed.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "wooden_door_handle.obj"), "cyan"));
        ArrayList<Pair<Mesh, String>> secretDoorOpen = new ArrayList<Pair<Mesh, String>>();
        secretDoorOpen.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "wooden_door_open.obj"), "rot"));
        secretDoorOpen.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "wooden_door_handle_open.obj"), "cyan"));
        _tileProviders.put(Tile.CRACKED_BRICK_WALL_DOOR, new DoorTileProvider(
            secretDoorClosed, secretDoorOpen, false,
            new BlockedTunnelColliderProvider(), new TunnelColliderProvider(),
            _tileProviders.get(Tile.DIRT_FLOOR), (WallTileProvider)_tileProviders.get(Tile.BRICK_WALL),
            _state.soundEngine, "wooden_door_open", "wooden_door_close"
        ));
        
        // Road Markings X
        _tileProviders.put(Tile.ROAD_MARKINGS_X, new SimpleTileProvider(_objLoader.loadFromFile(Directory.MODEL + "road_markings_x.obj"), "gelb"));
        
        // Dirt floor grass 2
        ArrayList<Pair<Mesh, String>> dirtFloorGrass2 = new ArrayList<Pair<Mesh, String>>();
        dirtFloorGrass2.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "dirt_floor_borderless.obj"), "orange"));
        dirtFloorGrass2.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "dirt_floor_grassdetail2.obj"), "gruen"));
        dirtFloorGrass2.add(new Pair<Mesh, String>(_objLoader.loadFromFile(Directory.MODEL + "dirt_floor_stonedetail.obj"), "dunkelgrau"));
        _tileProviders.put(Tile.DIRT_FLOOR_GRASS2, new MultiMeshTileProvider(dirtFloorGrass2));
        
        // Road Markings Z
        _tileProviders.put(Tile.ROAD_MARKINGS_Z, new SimpleTileProvider(_objLoader.loadFromFile(Directory.MODEL + "road_markings_z.obj"), "gelb"));
        
        
        // Initialisierung der ColliderProvider
        _colliderProviders.put(Tile.BRICK_WALL, new WallColliderProvider());
        
        
        // Initialisierung der Entity Meshes
        _entityMeshes.put("turret_inactive", _objLoader.loadFromFile(Directory.MODEL + "turret/turret_inactive.obj"));
        _entityMeshes.put("turret_active", _objLoader.loadFromFile(Directory.MODEL + "turret/turret_active.obj"));
        _entityMeshes.put("turret_muzzle_flash", _objLoader.loadFromFile(Directory.MODEL + "turret/turret_muzzle_flash.obj"));
        _entityMeshes.put("drone_active", _objLoader.loadFromFile(Directory.MODEL + "drone/drone.obj"));
        _entityMeshes.put("drone_inactive", _objLoader.loadFromFile(Directory.MODEL + "drone/drone.obj"));
        _entityMeshes.put("drone_rotor", _objLoader.loadFromFile(Directory.MODEL + "drone/drone_rotor.obj"));
    }
    
    /**
     * Lädt eine Map
     * @param mapName Name der Map (KEIN Pfad)
     */
    public void load(String mapName)
    {
        _map = _csvLoader.loadFromFile(Directory.MAP + mapName + TILE_LAYER_SUFFIX, Directory.MAP + mapName + FUNCTION_LAYER_SUFFIX);
        _map.populate(_tileProviders, _colliderProviders, _entityMeshes, _state.soundEngine);
    }
    
    /**
     * Gibt eine Referenz zur aktuellen Map zurück
     * @return Referenz zur aktuellen Map
     */
    public GridMap getMap()
    {
        return _map;
    }
    
    /**
     * Konvertiert eine Position in World Space zu einer Tile-Position
     * @param worldPos dreidimensionale Position im World Space
     * @return zweidimensionale Koordinaten der umschließenden Tile im Grid
     */
    public static Vector2i worldPosToTilePos(Vector3 worldPos)
    {
        return new Vector2i((int)Math.round(worldPos.getX() / TILE_WIDTH - 0.5), (int)Math.round((MIRROR_Z_AXIS ? -1 : 1) * worldPos.getZ() / TILE_WIDTH - 0.5));
    }
    
    /**
     * Konvertiert eine Position im Tile Grid ins World Space
     * @param tilePos zweidimensionale Position im Grid
     * @return dreidimensionale Position des Mittelpunktes der Tile im World Space
     */
    public static Vector3 tilePosToWorldPos(Vector2i tilePos)
    {
        return new Vector3((tilePos.getX() + 0.5) * TILE_WIDTH, 0.0, (MIRROR_Z_AXIS ? -1 : 1) * (tilePos.getY() + 0.5) * TILE_WIDTH);
    }
}
