import java.util.*;
import javafx.util.*;

/**
 * Beschreiben Sie hier die Klasse GridMap.
 * 
 * @author Lasse Huber-Saffer 
 * @version 03.12.2021
 */
public class GridMap
{
    private ArrayList<ArrayList<Integer>> _tileLayer;
    private ArrayList<ArrayList<Integer>> _functionLayer;
    private ArrayList<IGameObject> _mapGeometry;
    
    // Map-Layout
    private ArrayList<Vector2i> _doorLocations;
    private ArrayList<DoorGameObject> _doors;
    private ArrayList<Room> _rooms;
    
    public int activeRoom = 0;
    
    private Vector3 _playerSpawn;
    
    private static final boolean DEBUG_FULLDRAW = false;

    /**
     * Konstruktor für Objekte der Klasse GridMap
     * @param tileLayer zweidimensionale Liste der Tile-Werte
     * @param functionLayer zweidimensionale Liste der funktionalen Tiles
     */
    public GridMap(ArrayList<ArrayList<Integer>> tileLayer, ArrayList<ArrayList<Integer>> functionLayer)
    {
        _tileLayer = tileLayer;
        _functionLayer = functionLayer;
        _mapGeometry = new ArrayList<IGameObject>();
        _doorLocations = new ArrayList<Vector2i>();
        _doors = new ArrayList<DoorGameObject>();
        _rooms = new ArrayList<Room>();
        _playerSpawn = new Vector3(0.0, 2.0, 0.0);
    }
    
    /**
     * Zeichnet die Mapgeometrie mit einem gegebenen Renderer in das Sichtfeld einer gegebenen Kamera
     * @param renderer zu nutzender Renderer
     * @param camera zu benutzende Kamera
     */
    public void draw(Renderer renderer, Camera camera)
    {
        if(DEBUG_FULLDRAW)
        {
            for(int i = 0; i < _mapGeometry.size(); i++)
            {
                _mapGeometry.get(i).draw(renderer, camera);
            }
        }
        else
        {
            HashSet<Integer> completedRooms = new HashSet<Integer>();
            HashSet<Vector2i> completedDoors = new HashSet<Vector2i>();
            
            // Zeichnet rekursiv alle Räume, vom aktuellen Raum aus
            drawConnectedRooms(activeRoom, completedRooms, completedDoors, renderer, camera);
        }
    }
    
    /**
     * Zeichnet einen gegebenen Raum und alle damit verknüpften Räume und Türen
     * @param roomID ID des Raums in der GridMap-Raumliste
     * @param completedRooms HashSet der bereits gezeichneten RaumIDs
     * @param completedDoors HashSet der bereits gezeichneten Türpositionen
     * @param renderer Renderer, der zum Zeichnen verwendet werden soll
     * @param camera Kamera, in deren Sichtfeld gerendert werden soll
     */
    private void drawConnectedRooms(int roomID, HashSet<Integer> completedRooms, HashSet<Vector2i> completedDoors, Renderer renderer, Camera camera)
    {
        // Aktuellen Raum bekommen und rendern
        Room room = _rooms.get(roomID);
        room.draw(renderer, camera);
        
        for(DoorGameObject door : room.getDoors())
        {
            // Tür nur zeichnen, wenn sie im selben Frame nicht zuvor gezeichnet wurde
            if(!completedDoors.contains(door.getTilePosition()))
            {
                door.draw(renderer, camera);
                completedDoors.add(door.getTilePosition());
            }
            
            // Raum dahinter nur behandeln, wenn die Tür offen ist
            if(door.isOpen())
            {
                // ID des Raums am anderen Ende der Tür feststellen
                Integer otherRoom = null;
                Pair<Integer, Integer> doorConnection = door.getAttachedRoomIDs();
                if(roomID == doorConnection.getKey())
                {
                    otherRoom = doorConnection.getValue();
                }
                else if(roomID == doorConnection.getValue())
                {
                    otherRoom = doorConnection.getKey();
                }
                
                // Nächsten Raum nur zeichnen, wenn er existiert und nicht bereits gerendert wurde
                if(otherRoom != null && !completedRooms.contains(otherRoom))
                {
                    completedRooms.add(otherRoom);
                    drawConnectedRooms(otherRoom, completedRooms, completedDoors, renderer, camera);
                }
            }
        }
    }
    
    /**
     * Verarbeitet die Tile-Werte und erstellt die spielbare Map
     * @param tileProviders Hashmap der TileProvider. Der Key entspricht der Tile-ID
     * @param mirrorZAxis soll eine Spiegelung entlang der z-Achse stattfinden, sodass die Map im Spiel wie im Editor angezeigt wird?
     */
    public void populate(HashMap<Integer, ITileProvider> tileProviders)
    {
        // Geometrieebene
        for(int z = 0; z < _tileLayer.size(); z++)
        {
            for(int x = 0; x < _tileLayer.get(z).size(); x++)
            {
                int value = _tileLayer.get(z).get(x);
                if(value != -1)
                {
                    if(!tileProviders.containsKey(value))
                    {
                        System.out.println("[Error] Tile mesh not provided: " + value);
                    }
                    else
                    {
                        ITileProvider provider = tileProviders.get(value);
                        TileEnvironment env = null;
                        if(provider.requiresEnvironment())
                        {
                            env = new TileEnvironment(_tileLayer, x, z);
                        }
                        
                        _mapGeometry.addAll(provider.getTileObjects(env, x, z));
                    }
                }
            }
        }
        
        ArrayList<Vector2i> roomFloodFills = new ArrayList<Vector2i>();
        
        // Funktionsebene
        for(int z = 0; z < _functionLayer.size(); z++)
        {
            for(int x = 0; x < _functionLayer.get(z).size(); x++)
            {
                int value = _functionLayer.get(z).get(x);
                switch(value)
                {
                    case Tile.PLAYER_SPAWN_DOWN:
                    case Tile.PLAYER_SPAWN_RIGHT:
                    case Tile.PLAYER_SPAWN_UP:
                    case Tile.PLAYER_SPAWN_LEFT:
                        _playerSpawn = new Vector3((x + 0.5) * MapHandler.TILE_WIDTH, 2.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 0.5) * MapHandler.TILE_WIDTH);
                        break;
                    case Tile.ROOM_FLOODFILL:
                        roomFloodFills.add(new Vector2i(x, z));
                }
            }
        }
        
        System.out.println("FloodFills: " + roomFloodFills.size());
        
        for(int i = 0; i < roomFloodFills.size(); i++)
        {
            Vector2i source = roomFloodFills.get(i);
            Room room = floodFillFromSource(source);
            if(room != null)
            {
                room.populate(tileProviders, _tileLayer);
                _rooms.add(room);
            }
            else
            {
                System.out.println("Source #" + i + " at " + source.getX() + ", " + source.getY() + " is already contained in another room");
            }
        }
    
        // Türobjekte generieren
        for(Vector2i doorLocation : _doorLocations)
        {
            int x = doorLocation.getX(), z = doorLocation.getY();
            int value = _tileLayer.get(z).get(x);
            if(!Tile.isDoor(value))
            {
                
            }
            else
            {
                if(!tileProviders.containsKey(value))
                {
                    System.out.println("[Error] Door TileProvider missing: " + value);
                }
                else
                {
                    ArrayList<IGameObject> doorObjList = tileProviders.get(value).getTileObjects(new TileEnvironment(_tileLayer, x, z), x, z);
                    for(IGameObject doorObj : doorObjList)
                    {
                        if(!(doorObj instanceof DoorGameObject))
                        {
                            throw new RuntimeException("Tried adding a non-door GameObject to Door List");
                        }
                        else
                        {
                            _doors.add((DoorGameObject)doorObj);
                            connectDoor((DoorGameObject)doorObj);
                        }
                    }  
                }
            }
        }
    }
    
    private void connectDoor(DoorGameObject door)
    {
        if(door == null)
        {
            throw new IllegalArgumentException("Tried to connect a door with value null");    
        }
        
        Vector2i pos = door.getTilePosition();
        int x = pos.getX(), z = pos.getY();
        Integer firstRoom = null, secondRoom = null;
        
        if(door.isFacingZ())
        {
            if(z > 0)
            {
                firstRoom = getRoom(new Vector2i(x, z - 1));
            }
            if(z < _tileLayer.get(0).size()-1)
            {
                secondRoom = getRoom(new Vector2i(x, z + 1));
            }
        }
        else
        {
            if(x > 0)
            {
                firstRoom = getRoom(new Vector2i(x - 1, z));
            }
            if(x < _tileLayer.size()-1)
            {
                secondRoom = getRoom(new Vector2i(x + 1, z));
            }
        }
        
        if(firstRoom != null || secondRoom != null) {
            // Verbindung bei der Tür registrieren
            door.setAttachedRoomIDs(firstRoom, secondRoom);
            
            // Verbindung bei den Räumen registrieren
            if(firstRoom != null)
            {
                _rooms.get(firstRoom).addDoor(door);
            }
            if(secondRoom != null)
            {
                _rooms.get(secondRoom).addDoor(door);
            }
        }
    }
    
    private Room floodFillFromSource(Vector2i source)
    {
        if(source == null)
        {
            return null;
        }
        
        System.out.println("Source: x:" + source.getX() + ", z:" + source.getY());
        
        for(int i = 0; i < _rooms.size(); i++)
        {
            if(_rooms.get(i).contains(source.getX(), source.getY()))
            {
                System.out.println("Already contained in room" + i);
            }
        }
        
        Room result = new Room();
        
        floodFillStep(source, result);
        
        return result;
    }
    
    private void floodFillStep(Vector2i source, Room room)
    {
        if(!room.contains(source))
        {
            room.addTile(source);
        }
        int x = source.getX(), z = source.getY();
        // neg x
        if(x > 0)
        {
            floodFillDirection(-1, 0, source, room);
        }
        // pos x
        if(x < _tileLayer.get(0).size() - 1)
        {
            floodFillDirection(1, 0, source, room);
        }
        // neg z
        if(z > 0)
        {
            floodFillDirection(0, -1, source, room);
        }
        // pos z
        if(z < _tileLayer.size() - 1)
        {
            floodFillDirection(0, 1, source, room);
        }
    }
    
    private void floodFillDirection(int dx, int dz, Vector2i source, Room room)
    {
        int x = source.getX(), z = source.getY();
        
        if(!room.contains(new Vector2i(x + dx, z + dz)))
        {
            int val = _tileLayer.get(z + dz).get(x + dx);
            if(!Tile.isDoor(val))
            {
                room.addTile(new Vector2i(x + dx, z + dz));
                if(!Tile.isSolid(val))
                {
                    floodFillStep(new Vector2i(x + dx, z + dz), room);
                }
            }
            else
            {
                _doorLocations.add(new Vector2i(x + dx, z + dz));
            }
        }
    }
    
    /**
     * Gibt zurück, ob, und in welchem Raum sich eine Tile befindet
     * @param pos Position der Tile im Grid
     * @return null, wenn die Tile in keinem Raum liegt, ansonsten ID des Raums
     */
    public Integer getRoom(Vector2i pos)
    {
        for(int i = 0; i < _rooms.size(); i++)
        {
            if(_rooms.get(i).contains(pos))
            {
                return i;
            }
        }
        return null;
    }
    
    /**
     * Gibt den Spawnpunkt des Spielers zurück
     * @return Spawnpunkt des Spielers in der Map
     */
    public Vector3 getPlayerSpawn()
    {
        return _playerSpawn;
    }
    
    /**
     * Gibt die Anzahl der registrierten Räume zurück
     * @return Anzahl registrierter Räume
     */    
    public int getRoomCount()
    {
        return (_rooms == null)? 0 : _rooms.size();
    }
    
    /**
     * Updated den Levels-of-Detail des GameObjects im Verhältnis zu einem Bezugspunkt
     * @param cameraPosition Position der Kamera, die als Bezugspunkt der LOD-Berechnung genutzt wird
     */
    public void updateLOD(Vector3 cameraPosition)
    {
        for(int i = 0; i < _mapGeometry.size(); i++)
        {
            if(_mapGeometry.get(i) instanceof ILODGameObject)
            {
                ((ILODGameObject)_mapGeometry.get(i)).updateLOD(cameraPosition);
            }
        }
    }
    
    /**
     * Sortiert die GameObjects, die Teil der Mapgeometrie sind neu, sodass sie mit aufsteigender Distanz zur Kamera sortiert sind.
     * Dies sorgt dafür, dass das Flackern, das durch das Fehlen des Back Buffers entsteht, möglichst entfernt von der Kamera stattfindet und somit weniger bemerkbar ist.
     * @param cameraPosition Position der Kamera, im Bezug zu der die GameObjects sortiert werden sollen
     */
    public void reorderAroundCamera(Vector3 cameraPosition)
    {
        // Sortiert die Collection
        Collections.sort(_mapGeometry,
            // Distanz-Komparator
            new Comparator<IGameObject>() {
                @Override
                public int compare(IGameObject obj1, IGameObject obj2)
                {
                    double dist1 = obj1.getPosition().subtract(cameraPosition).getLength();
                    double dist2 = obj2.getPosition().subtract(cameraPosition).getLength();
                    
                    if(dist1 > dist2)
                    {
                        return 1;
                    }
                    else if(dist1 < dist2)
                    {
                        return -1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        );
    }
}
