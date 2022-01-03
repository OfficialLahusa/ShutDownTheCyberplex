import java.util.*;
import javafx.util.*;

/**
 * Verwaltet die GridMap und stellt zusätzliche Funktionalität bereit, insbesondere zum Map-Loading und zur Performance-Optimierung
 * 
 * @author Lasse Huber-Saffer
 * @version 02.01.2022
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
    // Namentliches Register von Meshes, die von Partikeln benutzt werden
    private HashMap<String, Mesh> _particleMeshes;
    
    // File-Loading
    private static final String TILE_LAYER_SUFFIX = "_tile.csv";
    private static final String FUNCTION_LAYER_SUFFIX = "_function.csv";
    
    // Generierungskonstanten
    public static final boolean MIRROR_Z_AXIS = true;
    public static final double TILE_WIDTH = 8.0;
    
    /**
     * Konstruktor für Objekte der Klasse MapHandler
     * @param tileMeshes HashMap von namentlich aufgeführten Meshes für zu erstellende TileProvider
     * @param entityMeshes Register, aus dem die Entity-Meshes bezogen werden
     * @param particleMeshes Register, aus dem die Particle-Meshes bezogen werden
     * @param soundEngine SoundEngine für Tile-Sounds
     */
    public MapHandler(HashMap<String, Mesh> tileMeshes, HashMap<String, Mesh> entityMeshes, HashMap<String, Mesh> particleMeshes, SoundEngine soundEngine)
    {
        _map = null;
        
        _soundEngine = soundEngine;
        
        _csvLoader = new CSVMapLoader();
        _lodGenerator = new LODGenerator();
        
        _tileProviders = new HashMap<Integer, ITileProvider>();
        _colliderProviders = new HashMap<Integer, IColliderProvider>();
        
        _entityMeshes = entityMeshes;
        _particleMeshes = particleMeshes;
        
        // Initialisierung der TileProvider
        // Dirt floor
        ArrayList<Pair<Double, Mesh>> dirtFloorLODs = new ArrayList<Pair<Double, Mesh>>();
        dirtFloorLODs.add(new Pair<Double, Mesh>(0.0, tileMeshes.get("dirt_floor_borderless")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(30.0, tileMeshes.get("dirt_floor_borderless_lod1")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(40.0, tileMeshes.get("dirt_floor_borderless_lod2")));
        dirtFloorLODs.add(new Pair<Double, Mesh>(50.0, tileMeshes.get("dirt_floor_borderless_lod3")));
        _tileProviders.put(Tile.DIRT_FLOOR, new SimpleLODTileProvider(dirtFloorLODs, TurtleColor.ORANGE));
        
        // Brick wall
        _tileProviders.put(Tile.BRICK_WALL, new WallTileProvider(
            tileMeshes.get("brick_wall"), TurtleColor.GRAY, tileMeshes.get("simple_wall_pillar"), TurtleColor.GRAY)
        );
        
        // Wooden door
        ArrayList<Pair<Mesh, TurtleColor>> woodenDoorClosed = new ArrayList<Pair<Mesh, TurtleColor>>();
        woodenDoorClosed.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("wooden_door"), TurtleColor.ORANGE));
        woodenDoorClosed.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("wooden_door_handle"), TurtleColor.YELLOW));
        ArrayList<Pair<Mesh, TurtleColor>> woodenDoorOpen = new ArrayList<Pair<Mesh, TurtleColor>>();
        woodenDoorOpen.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("wooden_door_open"), TurtleColor.ORANGE));
        woodenDoorOpen.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("wooden_door_handle_open"), TurtleColor.YELLOW));
        _tileProviders.put(Tile.WOODEN_DOOR, new DoorTileProvider(
            woodenDoorClosed, woodenDoorOpen, false,
            new BlockedTunnelColliderProvider(), new TunnelColliderProvider(),
            tileMeshes.get("door_lock"), _tileProviders.get(Tile.DIRT_FLOOR), (WallTileProvider)_tileProviders.get(Tile.BRICK_WALL),
            _soundEngine, "wooden_door_open", "wooden_door_close", 0.2
        ));
        
        // Cyber floor
        _tileProviders.put(Tile.CYBER_FLOOR, new SimpleTileProvider(tileMeshes.get("cyber_floor"), TurtleColor.DARK_GRAY));
        
        // Dirt floor grass
        ArrayList<Pair<Mesh, TurtleColor>> dirtFloorGrass = new ArrayList<Pair<Mesh, TurtleColor>>();
        dirtFloorGrass.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("dirt_floor_borderless"), TurtleColor.ORANGE));
        dirtFloorGrass.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("dirt_floor_grassdetail"), TurtleColor.GREEN));
        _tileProviders.put(Tile.DIRT_FLOOR_GRASS, new MultiMeshTileProvider(dirtFloorGrass));
        
        // Cracked brick wall
        ArrayList<Pair<Mesh, TurtleColor>> secretDoorClosed = new ArrayList<Pair<Mesh, TurtleColor>>();
        secretDoorClosed.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("wooden_door"), TurtleColor.RED));
        secretDoorClosed.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("wooden_door_handle"), TurtleColor.CYAN));
        ArrayList<Pair<Mesh, TurtleColor>> secretDoorOpen = new ArrayList<Pair<Mesh, TurtleColor>>();
        secretDoorOpen.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("wooden_door_open"), TurtleColor.RED));
        secretDoorOpen.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("wooden_door_handle_open"), TurtleColor.CYAN));
        _tileProviders.put(Tile.CRACKED_BRICK_WALL_DOOR, new DoorTileProvider(
            secretDoorClosed, secretDoorOpen, false,
            new BlockedTunnelColliderProvider(), new TunnelColliderProvider(),
            tileMeshes.get("door_lock"), _tileProviders.get(Tile.DIRT_FLOOR), (WallTileProvider)_tileProviders.get(Tile.BRICK_WALL),
            _soundEngine, "wooden_door_open", "wooden_door_close", 0.2
        ));
        
        // Road Markings X
        _tileProviders.put(Tile.ROAD_MARKINGS_X, new SimpleTileProvider(tileMeshes.get("road_markings_x"), TurtleColor.YELLOW));
        
        // Dirt floor grass 2
        ArrayList<Pair<Mesh, TurtleColor>> dirtFloorGrass2 = new ArrayList<Pair<Mesh, TurtleColor>>();
        dirtFloorGrass2.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("dirt_floor_borderless"), TurtleColor.ORANGE));
        dirtFloorGrass2.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("dirt_floor_grassdetail2"), TurtleColor.GREEN));
        dirtFloorGrass2.add(new Pair<Mesh, TurtleColor>(tileMeshes.get("dirt_floor_stonedetail"), TurtleColor.DARK_GRAY));
        _tileProviders.put(Tile.DIRT_FLOOR_GRASS2, new MultiMeshTileProvider(dirtFloorGrass2));
        
        // Road Markings Z
        _tileProviders.put(Tile.ROAD_MARKINGS_Z, new SimpleTileProvider(tileMeshes.get("road_markings_z"), TurtleColor.YELLOW));
        
        
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
        _map.populate(_tileProviders, _colliderProviders, _entityMeshes, _particleMeshes, _soundEngine);
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
