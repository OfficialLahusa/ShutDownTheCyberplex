import java.util.*;
import javafx.util.*;

/**
 * Beschreiben Sie hier die Klasse GridMap.
 * 
 * @author Lasse Huber-Saffer 
 * @version 27.12.2021
 */
public class GridMap
{
    // Positionen von Türobjekten im Grid
    private ArrayList<Vector2i> _doorLocations;
    // Rohe Mapdaten
    private ArrayList<ArrayList<Integer>> _tileLayer;
    private ArrayList<ArrayList<Integer>> _functionLayer;
    // Debug Fulldraw-Daten
    private ArrayList<IGameObject> _mapGeometry;
    private static final boolean DEBUG_FULLDRAW = false;
    // Spieler-Spawnpunkt
    private Vector3 _playerSpawn;
    private Player _player;
    
    /**
     * Index des aktuell aktiven Raumes der GridMap
     */
    public int activeRoom = 0;
    /**
     * Liste aller Türobjekte der Map
     */ 
    public ArrayList<IDoorGameObject> doors;
    /**
     * Liste aller Räume
     */ 
    public ArrayList<Room> rooms;
    /**
     * Liste aller nicht-raumgebundenen Collider
     */
    public ArrayList<ICollider> globalColliders;

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
        doors = new ArrayList<IDoorGameObject>();
        rooms = new ArrayList<Room>();
        globalColliders = new ArrayList<ICollider>();
        _playerSpawn = new Vector3(0.0, 2.0, 0.0);
    }
    
    /**
     * Updated die GridMap.
     * Levels of Detail und Draw Order werden neu berechnet.
     * @param deltaTime Deltazeit des Frames in Sekunden
     * @param runTime Laufzeit des Programms in Sekunden
     * @param cameraPosition Position der Kamera im World Space
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        // LODs und DrawOrder updaten
        updateLOD(cameraPosition);
        reorderAroundCamera(cameraPosition);
        
        // Aktiven Raum updaten
        rooms.get(activeRoom).update(deltaTime, runTime, cameraPosition);
        
        // Türen updaten
        for(IDoorGameObject door : doors)
        {
            door.update(deltaTime, runTime, cameraPosition);
        }
        
        // ID des aktiven Raums neu berechnen
        Vector2i tilePos = MapHandler.worldPosToTilePos(cameraPosition);
        Integer roomID = getRoomID(tilePos);
        if(roomID != null && roomID != activeRoom)
        {
            activeRoom = roomID;
        }
    }
    
    /**
     * Verarbeitet die Kollisionen eines Colliders mit den Collidern der Map
     * @param collider Collider, dessen Kollisionen verarbeitet werden
     */
    public void handleCollisions(ICollider collider)
    {
        rooms.get(activeRoom).handleCollisions(collider);
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
        Room room = rooms.get(roomID);
        room.draw(renderer, camera);
        
        for(IDoorGameObject door : room.getDoors())
        {
            // Tür nur zeichnen, wenn sie im selben Frame nicht zuvor gezeichnet wurde
            if(!completedDoors.contains(door.getTilePosition()))
            {
                if(door instanceof IGameObject)
                {
                    ((IGameObject)door).draw(renderer, camera);
                }
                completedDoors.add(door.getTilePosition());
            }
            
            // Raum dahinter nur behandeln, wenn die Tür offen ist
            if(door.isOpen())
            {
                // ID des Raums am anderen Ende der Tür feststellen
                Integer otherRoom = null;
                Pair<Integer, Integer> doorConnection = door.getConnectedRoomIDs();
                if(doorConnection.getKey() != null && roomID == doorConnection.getKey())
                {
                    otherRoom = doorConnection.getValue();
                }
                else if(doorConnection.getValue() != null && roomID == doorConnection.getValue())
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
     * Updated die Levels-of-Detail der Mapgeometrie im Verhältnis zu einem Bezugspunkt
     * @param cameraPosition Position der Kamera, die als Bezugspunkt der LOD-Berechnung genutzt wird
     */
    private void updateLOD(Vector3 cameraPosition)
    {
        if(DEBUG_FULLDRAW)
        {
            for(int i = 0; i < _mapGeometry.size(); i++)
            {
                if(_mapGeometry.get(i) instanceof ILODGameObject)
                {
                    ((ILODGameObject)_mapGeometry.get(i)).updateLOD(cameraPosition);
                }
            }
        }
        else
        {
            HashSet<Integer> completedRooms = new HashSet<Integer>();
            HashSet<Vector2i> completedDoors = new HashSet<Vector2i>();
            
            // Updated rekursiv die LODs aller Räume, vom aktuellen Raum aus
            lodUpdateConnectedRooms(activeRoom, completedRooms, completedDoors, cameraPosition);
        }
    }
    
    /**
     * Updated die Levels-of-Detail für einen gegebenen Raum und alle damit verknüpften Räume und Türen
     * @param roomID ID des Raums in der GridMap-Raumliste
     * @param completedRooms HashSet der bereits gezeichneten RaumIDs
     * @param completedDoors HashSet der bereits gezeichneten Türpositionen
     * @param cameraPosition Position der Kamera, die als Bezugspunkt der LOD-Berechnung genutzt wird
     */
    private void lodUpdateConnectedRooms(int roomID, HashSet<Integer> completedRooms, HashSet<Vector2i> completedDoors, Vector3 cameraPosition)
    {
        // Aktuellen Raum bekommen und updaten
        Room room = rooms.get(roomID);
        room.updateLOD(cameraPosition);
        
        for(IDoorGameObject door : room.getDoors())
        {
            // Tür nur updaten, wenn sie im selben Frame nicht zuvor geupdated wurde
            if(!completedDoors.contains(door.getTilePosition()))
            {
                if(door instanceof ILODGameObject)
                {
                    ((ILODGameObject)door).updateLOD(cameraPosition);                    
                }
                completedDoors.add(door.getTilePosition());
            }
            
            // Raum dahinter nur behandeln, wenn die Tür offen ist
            if(door.isOpen())
            {
                // ID des Raums am anderen Ende der Tür feststellen
                Integer otherRoom = null;
                Pair<Integer, Integer> doorConnection = door.getConnectedRoomIDs();
                if(roomID == doorConnection.getKey())
                {
                    otherRoom = doorConnection.getValue();
                }
                else if(roomID == doorConnection.getValue())
                {
                    otherRoom = doorConnection.getKey();
                }
                
                // Nächsten Raum nur updaten, wenn er existiert und nicht bereits geupdated wurde
                if(otherRoom != null && !completedRooms.contains(otherRoom))
                {
                    completedRooms.add(otherRoom);
                    lodUpdateConnectedRooms(otherRoom, completedRooms, completedDoors, cameraPosition);
                }
            }
        }
    }
    
    /**
     * Sortiert die GameObjects, die Teil der Mapgeometrie sind, neu, sodass sie mit aufsteigender Distanz zur Kamera sortiert sind.
     * Dies sorgt dafür, dass das Flackern, das durch das Fehlen des Back Buffers entsteht, möglichst entfernt von der Kamera stattfindet und somit weniger bemerkbar ist.
     * @param cameraPosition Position der Kamera, im Bezug zu der die GameObjects sortiert werden sollen
     */
    private void reorderAroundCamera(Vector3 cameraPosition)
    {
        // Distanz-Komparator
        Comparator gameObjectDistanceComparator = new Comparator<IGameObject>() {
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
        };
        
        if(DEBUG_FULLDRAW)
        {
            // Sortiert die Collection
            Collections.sort(_mapGeometry,
                 gameObjectDistanceComparator        
            );
        }
        else
        {
            HashSet<Integer> completedRooms = new HashSet<Integer>();
            
            // Updated rekursiv die Draw-Reihenfolge aller Räume, vom aktuellen Raum aus
            reorderConnectedRooms(activeRoom, completedRooms, cameraPosition);
        }
    }
    
    /**
     * Updated den Draw Order für einen gegebenen Raum und alle damit verknüpften Räume
     * @param roomID ID des Raums in der GridMap-Raumliste
     * @param completedRooms HashSet der bereits gezeichneten RaumIDs
     * @param cameraPosition Position der Kamera, die als Bezugspunkt der LOD-Berechnung genutzt wird
     */
    private void reorderConnectedRooms(int roomID, HashSet<Integer> completedRooms, Vector3 cameraPosition)
    {
        // Aktuellen Raum bekommen und updaten
        Room room = rooms.get(roomID);
        room.reorderAroundCamera(cameraPosition);
        
        for(IDoorGameObject door : room.getDoors())
        {
            // Raum hinter Tür nur behandeln, wenn die Tür offen ist
            if(door.isOpen())
            {
                // ID des Raums am anderen Ende der Tür feststellen
                Integer otherRoom = null;
                Pair<Integer, Integer> doorConnection = door.getConnectedRoomIDs();
                if(roomID == doorConnection.getKey())
                {
                    otherRoom = doorConnection.getValue();
                }
                else if(roomID == doorConnection.getValue())
                {
                    otherRoom = doorConnection.getKey();
                }
                
                // Nächsten Raum nur updaten, wenn er existiert und nicht bereits geupdated wurde
                if(otherRoom != null && !completedRooms.contains(otherRoom))
                {
                    completedRooms.add(otherRoom);
                    reorderConnectedRooms(otherRoom, completedRooms, cameraPosition);
                }
            }
        }
    }
    
    /**
     * Verarbeitet die Tile-Werte und erstellt die spielbare Map.
     * @param tileProviders Hashmap der TileProvider. Der Key entspricht der Tile-ID.
     * @param colliderProviders Hashmap der ColliderProvider. Der Key entspricht der Tile-ID.
     * @param entityMeshes Hashmap der von Entities verwendeten Meshes.
     * @param soundEngine Sound Engine
     */
    public void populate(HashMap<Integer, ITileProvider> tileProviders, HashMap<Integer, IColliderProvider> colliderProviders, HashMap<String, Mesh> entityMeshes, SoundEngine soundEngine)
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
                        System.out.println("Tile mesh not provided: " + value);
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
        
        // Funktionsebene (Nur nicht-raumeigene Funktionstiles werden ausgelesen)
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
                        _playerSpawn = new Vector3((x + 0.5) * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 0.5) * MapHandler.TILE_WIDTH);
                        break;
                    case Tile.ROOM_FLOODFILL:
                        roomFloodFills.add(new Vector2i(x, z));
                }
            }
        }
        
        // Räume aus Floodfills füllen und populaten
        for(int i = 0; i < roomFloodFills.size(); i++)
        {
            Vector2i source = roomFloodFills.get(i);
            Room room = floodFillFromSource(source);
            if(room != null)
            {
                room.populate(tileProviders, colliderProviders, entityMeshes, soundEngine, _tileLayer, _functionLayer);
                rooms.add(room);
            }
            else
            {
                System.out.println("Source #" + i + " at " + source.getX() + ", " + source.getY() + " is already contained in another room");
            }
        }
    
        // Türobjekte (Verknüpfungen zwischen Räumen) generieren
        for(Vector2i doorLocation : _doorLocations)
        {
            int x = doorLocation.getX(), z = doorLocation.getY();
            int value = _tileLayer.get(z).get(x);
            if(Tile.isDoor(value))
            {
                if(!tileProviders.containsKey(value))
                {
                    System.out.println("[Error] Door TileProvider missing: " + value);
                }
                else
                {
                    ArrayList<IGameObject> doorObjList = tileProviders.get(value).getTileObjects(new TileEnvironment(_tileLayer, x, z), x, z);
                    for(IGameObject obj : doorObjList)
                    {
                        if(!(obj instanceof IDoorGameObject))
                        {
                            throw new RuntimeException("Tried adding a non-door GameObject to Door List");
                        }
                        else
                        {
                            IDoorGameObject doorObj = (IDoorGameObject)obj;
                            doors.add(doorObj);
                            connectDoor(doorObj);
                        }
                    }  
                }
            }
        }
    }
    
    /**
     * Richtet die Raum-Tür-Verbindungen eines Türobjekts für diese Map ein
     * @param door Tür, die verbunden werden soll
     */
    private void connectDoor(IDoorGameObject door)
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
                firstRoom = getRoomID(new Vector2i(x, z - 1));
            }
            if(z < _tileLayer.get(0).size()-1)
            {
                secondRoom = getRoomID(new Vector2i(x, z + 1));
            }
        }
        else
        {
            if(x > 0)
            {
                firstRoom = getRoomID(new Vector2i(x - 1, z));
            }
            if(x < _tileLayer.size()-1)
            {
                secondRoom = getRoomID(new Vector2i(x + 1, z));
            }
        }
        
        if(firstRoom != null || secondRoom != null) {
            // Verbindung bei der Tür registrieren
            door.setConnectedRoomIDs(firstRoom, secondRoom);
            
            // Verbindung bei den Räumen registrieren
            if(firstRoom != null)
            {
                rooms.get(firstRoom).addDoor(door);
            }
            if(secondRoom != null)
            {
                rooms.get(secondRoom).addDoor(door);
            }
        }
    }
    
    /**
     * Füllt von einer Quell-Tile aus eine Raumfläche auf
     * @param source Quell-Tile
     * @return entstandener Raum
     */
    private Room floodFillFromSource(Vector2i source)
    {
        if(source == null)
        {
            return null;
        }
        
        for(int i = 0; i < rooms.size(); i++)
        {
            if(rooms.get(i).contains(source.getX(), source.getY()))
            {
                System.out.println("Already contained in room" + i);
            }
        }
        
        Room result = new Room(this);
        
        floodFillStep(source, result);
        
        return result;
    }
    
    /**
     * Einzelschritt des Floodfill-Algorithmus
     * @param source Quell-Tile dieses Schrittes
     * @param room bisheriger Raum
     */
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
    
    /**
     * Einzelner direktionaler Schritt des Floodfill-Algorithmus
     * @param dx x-Verschiebung von der Quell-Tile
     * @param dz z-Verschiebung von der Quell-Tile
     * @param source Quell-Tile
     * @param room bisheriger Raum
     */
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
    public Integer getRoomID(Vector2i pos)
    {
        for(int i = 0; i < rooms.size(); i++)
        {
            if(rooms.get(i).contains(pos))
            {
                return i;
            }
        }
        return null;
    }
    
    /**
     * Gibt den Wert des Tile-Layers an einer gegebenen Position zurück
     * @param pos Position im Grid
     * @return Wert des Tile-Layers an der Position, Tile.NONE für Out-of-Bounds-Werte
     */
    public int getTileValue(Vector2i pos)
    {
        if(pos.getX() < 0 || pos.getY() < 0 || pos.getX() >= _tileLayer.get(0).size() || pos.getY() >= _tileLayer.size())
        {
            return Tile.NONE;
        }
        else
        {
            return _tileLayer.get(pos.getY()).get(pos.getX());
        }
    }
    
    /**
     * Gibt den Wert des Function-Layers an einer gegebenen Position zurück
     * @param pos Position im Grid
     * @return Wert des Function-Layers an der Position, Tile.NONE für Out-of-Bounds-Werte
     */
    public int getFunctionValue(Vector2i pos)
    {
        if(pos.getX() < 0 || pos.getY() < 0 || pos.getX() >= _functionLayer.get(0).size() || pos.getY() >= _functionLayer.size())
        {
            return Tile.NONE;
        }
        else
        {
            return _functionLayer.get(pos.getY()).get(pos.getX());
        }
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
        return (rooms == null)? 0 : rooms.size();
    }
    
    /**
     * Gibt eine Referenz zum Spieler zurück, wenn diese zuvor gesetzt wurde
     * @return Referenz zum Spieler, null, falls nicht gesetzt
     */
    public Player getPlayer()
    {
        return _player;
    }
    
    /**
     * Setzt die Spielerreferenz der Map
     * @param player Spieler
     */
    public void setPlayer(Player player)
    {
        _player = player;
    }
}
