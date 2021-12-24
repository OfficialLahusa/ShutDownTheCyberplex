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
    
    // Sound-Engine
    private SoundEngine _soundEngine;
    
    // Utility
    private CSVMapLoader _csvLoader;
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
     * @param tileMeshes HashMap von namentlich aufgeführten Meshes für zu erstellende TileProvider
     * @param entityMeshes HashMap von namentlich aufgeführten Meshes für zu erstellende Entities
     * @param soundEngine SoundEngine für Tile-Sounds
     */
    public MapHandler(HashMap<String, Mesh> tileMeshes, HashMap<String, Mesh> entityMeshes, SoundEngine soundEngine)
    {
        _map = null;
        
        _soundEngine = soundEngine;
        
        _csvLoader = new CSVMapLoader();
        _lodGenerator = new LODGenerator();
        
        _tileProviders = new HashMap<Integer, ITileProvider>();
        _colliderProviders = new HashMap<Integer, IColliderProvider>();
        _entityMeshes = entityMeshes;
        
        // Initialisierung der TileProvider
        // Dirt floor
        ArrayList<Pair<Double, Mesh>> dirtFloorLODs = new ArrayList<Pair<Double, Mesh>>();
        dirtFloorLODs.add(new Pair<Double, Mesh>(0.0, tileMeshes.get("dirt_floor_borderless")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(30.0, tileMeshes.get("dirt_floor_borderless_lod1")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(40.0, tileMeshes.get("dirt_floor_borderless_lod2")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(50.0, tileMeshes.get("dirt_floor_borderless_lod3")));
        _tileProviders.put(Tile.DIRT_FLOOR, new SimpleLODTileProvider(dirtFloorLODs, "orange"));
        
        // Brick wall
        _tileProviders.put(Tile.BRICK_WALL, new WallTileProvider(
            tileMeshes.get("brick_wall"), "grau", tileMeshes.get("simple_wall_pillar"), "grau")
        );
        
        // Wooden door
        ArrayList<Pair<Mesh, String>> woodenDoorClosed = new ArrayList<Pair<Mesh, String>>();
        woodenDoorClosed.add(new Pair<Mesh, String>(tileMeshes.get("wooden_door"), "orange"));
        woodenDoorClosed.add(new Pair<Mesh, String>(tileMeshes.get("wooden_door_handle"), "gelb"));
        ArrayList<Pair<Mesh, String>> woodenDoorOpen = new ArrayList<Pair<Mesh, String>>();
        woodenDoorOpen.add(new Pair<Mesh, String>(tileMeshes.get("wooden_door_open"), "orange"));
        woodenDoorOpen.add(new Pair<Mesh, String>(tileMeshes.get("wooden_door_handle_open"), "gelb"));
        _tileProviders.put(Tile.WOODEN_DOOR, new DoorTileProvider(
            woodenDoorClosed, woodenDoorOpen, false,
            new BlockedTunnelColliderProvider(), new TunnelColliderProvider(),
            _tileProviders.get(Tile.DIRT_FLOOR), (WallTileProvider)_tileProviders.get(Tile.BRICK_WALL),
            _soundEngine, "wooden_door_open", "wooden_door_close"
        ));
        
        // Dirt floor grass
        ArrayList<Pair<Mesh, String>> dirtFloorGrass = new ArrayList<Pair<Mesh, String>>();
        dirtFloorGrass.add(new Pair<Mesh, String>(tileMeshes.get("dirt_floor_borderless"), "orange"));
        dirtFloorGrass.add(new Pair<Mesh, String>(tileMeshes.get("dirt_floor_grassdetail"), "gruen"));
        _tileProviders.put(Tile.DIRT_FLOOR_GRASS, new MultiMeshTileProvider(dirtFloorGrass));
        
        // Cracked brick wall
        ArrayList<Pair<Mesh, String>> secretDoorClosed = new ArrayList<Pair<Mesh, String>>();
        secretDoorClosed.add(new Pair<Mesh, String>(tileMeshes.get("wooden_door"), "rot"));
        secretDoorClosed.add(new Pair<Mesh, String>(tileMeshes.get("wooden_door_handle"), "cyan"));
        ArrayList<Pair<Mesh, String>> secretDoorOpen = new ArrayList<Pair<Mesh, String>>();
        secretDoorOpen.add(new Pair<Mesh, String>(tileMeshes.get("wooden_door_open"), "rot"));
        secretDoorOpen.add(new Pair<Mesh, String>(tileMeshes.get("wooden_door_handle_open"), "cyan"));
        _tileProviders.put(Tile.CRACKED_BRICK_WALL_DOOR, new DoorTileProvider(
            secretDoorClosed, secretDoorOpen, false,
            new BlockedTunnelColliderProvider(), new TunnelColliderProvider(),
            _tileProviders.get(Tile.DIRT_FLOOR), (WallTileProvider)_tileProviders.get(Tile.BRICK_WALL),
            _soundEngine, "wooden_door_open", "wooden_door_close"
        ));
        
        // Road Markings X
        _tileProviders.put(Tile.ROAD_MARKINGS_X, new SimpleTileProvider(tileMeshes.get("road_markings_x"), "gelb"));
        
        // Dirt floor grass 2
        ArrayList<Pair<Mesh, String>> dirtFloorGrass2 = new ArrayList<Pair<Mesh, String>>();
        dirtFloorGrass2.add(new Pair<Mesh, String>(tileMeshes.get("dirt_floor_borderless"), "orange"));
        dirtFloorGrass2.add(new Pair<Mesh, String>(tileMeshes.get("dirt_floor_grassdetail2"), "gruen"));
        dirtFloorGrass2.add(new Pair<Mesh, String>(tileMeshes.get("dirt_floor_stonedetail"), "dunkelgrau"));
        _tileProviders.put(Tile.DIRT_FLOOR_GRASS2, new MultiMeshTileProvider(dirtFloorGrass2));
        
        // Road Markings Z
        _tileProviders.put(Tile.ROAD_MARKINGS_Z, new SimpleTileProvider(tileMeshes.get("road_markings_z"), "gelb"));
        
        
        // Initialisierung der ColliderProvider
        _colliderProviders.put(Tile.BRICK_WALL, new WallColliderProvider());
    }
    
    /**
     * Lädt eine Map
     * @param mapName Name der Map (KEIN Pfad)
     */
    public void load(String mapName)
    {
        _map = _csvLoader.loadFromFile(Directory.MAP + mapName + TILE_LAYER_SUFFIX, Directory.MAP + mapName + FUNCTION_LAYER_SUFFIX);
        _map.populate(_tileProviders, _colliderProviders, _entityMeshes, _soundEngine);
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
