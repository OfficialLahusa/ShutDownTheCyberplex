import java.util.*;

/**
 * Ein räumlich eingegrenzter Abschnitt einer Gridmap, innerhalb dessen Simulationen durchgeführt werden
 * 
 * @author Lasse Huber-Saffer
 * @version 02.01.2022
 */
public class Room
{
    // Parent
    private GridMap _map;
    
    // Map Geometry & Physics
    private HashSet<Vector2i> _tiles;
    private ArrayList<IGameObject> _geometry;
    private ArrayList<ICollider> _staticColliders;
    
    // Functionality
    private ArrayList<Vector2i> _doorLocations;
    private ArrayList<IDoorGameObject> _doors;
    private ArrayList<Vector2i> _focusPoints;
    private Vector2i[] _patrolRoute;
    private int _patrolRouteLength;
    
    // Entities im Raum
    private ArrayList<IGameObject> _entities;
    private ArrayList<IParticleSystem> _particleSystems;
    
    // Bounds im Grid
    private int _minX;
    private int _minZ;
    private int _maxX;
    private int _maxZ;

    /**
     * Konstruktor für Objekte der Klasse Room
     * @param map GridMap, in der der Raum sich befindet
     */
    public Room(GridMap map)
    {
        if(map == null) throw new IllegalArgumentException("Room parent GridMap must not be null");
        _map = map;
        
        _tiles = new HashSet<Vector2i>();
        _geometry = new ArrayList<IGameObject>();
        _staticColliders = new ArrayList<ICollider>();
        
        _doorLocations = new ArrayList<Vector2i>();
        _doors = new ArrayList<IDoorGameObject>();
        _focusPoints = new ArrayList<Vector2i>();
        _patrolRoute = null;
        _patrolRouteLength = 0;
        
        _entities = new ArrayList<IGameObject>();
        _particleSystems = new ArrayList<IParticleSystem>();
    }
    
    /**
     * Verarbeitet die Tile-Werte und erstellt die spielbare Map
     * @param tileProviders Hashmap der TileProvider. Der Key entspricht der Tile-ID
     * @param colliderProviders Hashmap der ColliderProvider. Der Key entspricht der Tile-ID
     * @param entityMeshes Register, aus dem die Entity-Meshes bezogen werden
     * @param particleMeshes Register, aus dem die Particle-Meshes bezogen werden
     * @param soundEngine Sound Engine
     * @param tileLayer rohe Geometrie-Mapdaten
     * @param functionLayer rohe Funktions-Mapdaten
     */
    public void populate(HashMap<Integer, ITileProvider> tileProviders, HashMap<Integer, IColliderProvider> colliderProviders, HashMap<String, Mesh> entityMeshes, HashMap<String, Mesh> particleMeshes, SoundEngine soundEngine, ArrayList<ArrayList<Integer>> tileLayer, ArrayList<ArrayList<Integer>> functionLayer)
    {
        // Null-Check der Parameter
        if(tileProviders == null)       throw new IllegalArgumentException("tileProviders was null when populating room");
        if(colliderProviders == null)   throw new IllegalArgumentException("colliderProviders was null when populating room");
        if(entityMeshes == null)        throw new IllegalArgumentException("entityMeshes was null when populating room");
        
        // Geometrieebene (Iteriert nur innerhalb der Bounds)
        for(int z = _minZ; z <= _maxZ; z++)
        {
            for(int x = _minX; x <= _maxX; x++)
            {
                int value = _map.getTileValue(new Vector2i(x, z));
                if(value != -1 && this.contains(x, z))
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
                            env = new TileEnvironment(tileLayer, this, x, z);
                        }
                        
                        _geometry.addAll(provider.getTileObjects(env, x, z));
                    }
                    
                    if(colliderProviders.containsKey(value))
                    {
                        IColliderProvider provider = colliderProviders.get(value);
                        TileEnvironment env = null;
                        if(provider.requiresEnvironment())
                        {
                            env = new TileEnvironment(tileLayer, this, x, z);
                        }
                        
                        _staticColliders.addAll(provider.getColliders(env, x, z));
                    }
                }
            }
        }
        
        // Funktionsebene (Nur im Kontext des Raumes wichtige Tiles, iteriert nur innerhalb der Bounds)
        // Erster Lauf:
        for(int z = _minZ; z <= _maxZ; z++)
        {
            for(int x = _minX; x <= _maxX; x++)
            {
                int value = functionLayer.get(z).get(x);
                if(value != -1 && this.contains(x, z))
                {
                    switch(value)
                    {
                        case Tile.TURRET_FOCUS_POINT:
                            _focusPoints.add(new Vector2i(x, z));
                            break;
                        case Tile.PATROL_1:
                            if(_patrolRoute == null) _patrolRoute = new Vector2i[8];
                            _patrolRoute[0] = new Vector2i(x, z);
                            break;
                        case Tile.PATROL_2:
                            if(_patrolRoute == null) _patrolRoute = new Vector2i[8];
                            _patrolRoute[1] = new Vector2i(x, z);
                            break;
                        case Tile.PATROL_3:
                            if(_patrolRoute == null) _patrolRoute = new Vector2i[8];
                            _patrolRoute[2] = new Vector2i(x, z);
                            break;
                        case Tile.PATROL_4:
                            if(_patrolRoute == null) _patrolRoute = new Vector2i[8];
                            _patrolRoute[3] = new Vector2i(x, z);
                            break;
                        case Tile.PATROL_5:
                            if(_patrolRoute == null) _patrolRoute = new Vector2i[8];
                            _patrolRoute[4] = new Vector2i(x, z);
                            break;
                        case Tile.PATROL_6:
                            if(_patrolRoute == null) _patrolRoute = new Vector2i[8];
                            _patrolRoute[5] = new Vector2i(x, z);
                            break;
                        case Tile.PATROL_7:
                            if(_patrolRoute == null) _patrolRoute = new Vector2i[8];
                            _patrolRoute[6] = new Vector2i(x, z);
                            break;
                        case Tile.PATROL_8:
                            if(_patrolRoute == null) _patrolRoute = new Vector2i[8];
                            _patrolRoute[7] = new Vector2i(x, z);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        
        // Patrouillenroutenlänge berechnen
        if(_patrolRoute != null)
        {
            for(int i = 0; i < _patrolRoute.length; i++)
            {
                if(_patrolRoute[i] == null)
                {
                    _patrolRouteLength = i;
                    break;
                }
            }
            
            if(_patrolRouteLength == 0 && _patrolRoute[7] != null)
            {
                _patrolRouteLength = 8;
            }
        }
        
        // Zweiter Lauf (Entities)
        for(int z = _minZ; z <= _maxZ; z++)
        {
            for(int x = _minX; x <= _maxX; x++)
            {
                int value = functionLayer.get(z).get(x);
                if(value != -1 && this.contains(x, z))
                {
                    switch(value)
                    {
                        case Tile.SPAWN_TURRET_INACTIVE:
                            Turret inactive_turret = new Turret(MapHandler.tilePosToWorldPos(new Vector2i(x, z)), false, this, entityMeshes, soundEngine);
                            _entities.add(inactive_turret);
                            break;
                        case Tile.SPAWN_TURRET_ACTIVE:
                            Turret active_turret = new Turret(MapHandler.tilePosToWorldPos(new Vector2i(x, z)), true, this, entityMeshes, soundEngine);
                            _entities.add(active_turret);
                            break;
                        case Tile.SPAWN_DRONE:
                            Drone drone = new Drone(MapHandler.tilePosToWorldPos(new Vector2i(x, z)), true, this, entityMeshes, particleMeshes, soundEngine);
                            _entities.add(drone);
                            break;
                        case Tile.SPAWN_HEALTH_POWERUP:
                            HealthPowerup healthPowerup = new HealthPowerup(MapHandler.tilePosToWorldPos(new Vector2i(x, z)), this, entityMeshes, soundEngine);
                            _entities.add(healthPowerup);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
    
    /**
     * Verarbeitet die Kollisionen eines Colliders mit den Collidern des Raums
     * @param collider Collider, dessen Kollisionen verarbeitet werden
     */
    public void handleCollisions(ICollider collider)
    {
        // Raum-Collider überprüfen
        for(ICollider col : _staticColliders)
        {
            collider.detectCollision(col);
        }
        
        // An den Raum angrenzende Türen überprüfen
        for(IDoorGameObject door : _doors)
        {
            ICollider col = door.getCollider();
            if(col != null)
            {
                collider.detectCollision(col);
            }
        }
        
        // Entity-Collider überprüfen
        for(ICollider col : getEntityColliders())
        {
            collider.detectCollision(col);
        }
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        // Raumgeometrie zeichnen
        for(IGameObject obj : _geometry)
        {
            obj.draw(renderer, camera);
        }
        
        // Entities zeichnen
        for(IGameObject entity : _entities)
        {
            entity.draw(renderer, camera);
        }
        
        // Partikelsysteme zeichnen
        for(IParticleSystem particleSystem : _particleSystems)
        {
            particleSystem.draw(renderer, camera);
        }
    }
    
    /**
     * Updated den Raum.
     * @param deltaTime Deltazeit des Frames in Sekunden
     * @param runTime Laufzeit des Programms in Sekunden
     * @param cameraPosition Position der Kamera im World Space
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        // Entities updaten
        for(IGameObject entity : _entities)
        {
            entity.update(deltaTime, runTime, cameraPosition);
        }
        
        // Entfernt iterativ die gestoppten Partikelsysteme
        for(Iterator<IParticleSystem> iter = _particleSystems.iterator(); iter.hasNext();)
        {
            IParticleSystem elem = iter.next();
            if(elem == null || elem.isDone())
            {
                iter.remove();
            }
        }
        
        // Partikelsysteme updaten
        for(IParticleSystem particleSystem : _particleSystems)
        {
            particleSystem.update(deltaTime, runTime, cameraPosition);
        }
    }
    
    /**
     * Updated die Levels-of-Detail der Raumgeometrie im Verhältnis zu einem Bezugspunkt
     * @param cameraPosition Position der Kamera, die als Bezugspunkt der LOD-Berechnung genutzt wird
     */
    public void updateLOD(Vector3 cameraPosition)
    {
        for(int i = 0; i < _geometry.size(); i++)
        {
            if(_geometry.get(i) instanceof ILODGameObject)
            {
                ((ILODGameObject)_geometry.get(i)).updateLOD(cameraPosition);
            }
        }
    }
    
    /**
     * Sortiert die GameObjects, die Teil der Raumgeometrie sind, neu, sodass sie mit aufsteigender Distanz zur Kamera sortiert sind.
     * Dies sorgt dafür, dass das Flackern, das durch das Fehlen des Back Buffers entsteht, möglichst entfernt von der Kamera stattfindet und somit weniger bemerkbar ist.
     * @param cameraPosition Position der Kamera, im Bezug zu der die GameObjects sortiert werden sollen
     */
    public void reorderAroundCamera(Vector3 cameraPosition)
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
        
        // Sortiert die Collection
        Collections.sort(_geometry,
             gameObjectDistanceComparator        
        );
    }
    
    /**
     * Fügt eine bestimmte Tile zum Gebiet dieses Raumes hinzu
     * @param x x-Koordinate der Tile
     * @param z z-Koordinate der Tile
     */
    public void addTile(int x, int z)
    {
        if(!_tiles.contains(new Vector2i(x, z)))
        {
            if(_tiles.size() == 0)
            {
                _minX = x;
                _maxX = x;
                _minZ = z;
                _maxZ = z;
            }
            else if(x < _minX)
            {
                _minX = x;
            }
            else if(x > _maxX)
            {
                _maxX = x;
            }
            else if(z < _minZ)
            {
                _minZ = z;
            }
            else if(z > _maxZ)
            {
                _maxZ = z;
            }
            _tiles.add(new Vector2i(x, z));
        }
        else
        {
            System.out.println("Already contains element");
        }
    }
    
    /**
     * Fügt ein Türobjekt zum Raum hinzu
     * @param door Türobjekt
     */
    public void addDoor(IDoorGameObject door)
    {
        if(door != null)
        {
            _doors.add(door);
        }
    }
    
    /**
     * Prüft, ob ein Koordinatenpaar im Raum enthalten ist
     * @param x x-Koordinate
     * @param z z-Koordinate
     * @return true, wenn Koordinatenpaar im Raum enthalten ist, sonst false
     */
    public boolean contains(int x, int z)
    {
        return _tiles.contains(new Vector2i(x, z));
    }
    
    /**
     * Fügt eine bestimmte Tile zum Gebiet dieses Raumes hinzu
     * @param pos Position der Tile
     */
    public void addTile(Vector2i pos)
    {
        addTile(pos.getX(), pos.getY());
    }
    
    /**
     * Prüft, ob ein Koordinatenpaar im Raum enthalten ist
     * @param pos Koordinatenpaar
     * @return true, wenn Koordinatenpaar im Raum enthalten ist, sonst false
     */
    public boolean contains(Vector2i pos)
    {
        return contains(pos.getX(), pos.getY());
    }
    
    /**
     * Fügt ein Partikelsystem zum Raum hinzu
     * @param particleSystem Partikelsystem
     */
    public void addParticleSystem(IParticleSystem particleSystem)
    {
        _particleSystems.add(particleSystem);
    }

    /**
     * Gibt eine Kopie des Sets der im Raum enthaltenen Tiles zurück
     * @return Kopie des Sets der im Raum enthaltenen Tiles
     */
    public HashSet<Vector2i> getTiles()
    {
        HashSet<Vector2i> result = new HashSet<Vector2i>();
        
        for(Vector2i tile : _tiles)
        {
            result.add(new Vector2i(tile));
        }
        
        return result;
    }
    
    /**
     * Gibt eine gefilterte Kopie des Sets der im Raum enthaltenen Tiles zurück
     * @param Filter, der jedem Tile-Wert einen booleschen Wert zuordnet. Nur Tiles mit dem Ergebnis true werden zurückgegeben.
     * @return Kopie des Sets der im Raum enthaltenen Tiles
     */
    public HashSet<Vector2i> getFilteredTiles(ITileFilter filter)
    {
        HashSet<Vector2i> result = new HashSet<Vector2i>();
        
        for(Vector2i tile : _tiles)
        {
            int tileValue = getMap().getTileValue(tile);
            
            if(filter.evaluate(tileValue))
            {
                result.add(new Vector2i(tile));
            }
        }
        
        return result;
    }
    
    /**
     * Gibt die Map, in der dieser Raum liegt, zurück
     * @return Referenz zur Map, in der dieser Raum liegt
     */
    public GridMap getMap()
    {
        return _map;
    }
    
    /**
     * Gibt die Liste der im Raum registrierten Turret-Fokuspunkte zurück
     * @return Liste der Turret-Fokuspunkte
     */
    public ArrayList<Vector2i> getFocusPoints()
    {
        return _focusPoints;
    }
    
    /**
     * Gibt die Patrouillenroute des Raums zurück.
     * Nicht existierende Patrouillenpunkte sind mit null belegt.
     * @return Array der Länge 8, das die registrierten Patrouillenpunkte enthält. Null, wenn es keine Route gibt.
     */
    public Vector2i[] getPatrolRoute()
    {
        return _patrolRoute;
    }
    
    /**
     * Gibt die Patrouillenroutenlänge zurück
     * @return Länge der Patrouillenroute
     */
    public int getPatrolRouteLength()
    {
        return _patrolRouteLength;
    }
        
    /**
     * Gibt die Türobjekte des Raums zurück
     * @return Liste der Türobjekte des Raums
     */
    public ArrayList<IDoorGameObject> getDoors()
    {
        return _doors;
    }
    
    /**
     * Gibt die statischen Collider des Raums zurück
     * @return Liste der Collider des Raums
     */
    public ArrayList<ICollider> getStaticColliders()
    {
        return _staticColliders;
    }
    
    /**
     * Gibt die Collider der Entities im Raum zurück.
     * Nicht jede Entity hat einen Collider.
     * @return Liste der Collider der dynamischen Entities im Raum
     */
    public ArrayList<ICollider> getEntityColliders()
    {
        ArrayList<ICollider> result = new ArrayList<ICollider>();
        
        for(IGameObject entity : _entities)
        {
            ICollider collider = entity.getCollider();
            
            if(collider != null)
            {
                result.add(collider);
            }
        }
        
        return result;
    }
    
    /**
     * Gibt die Entities innerhalb des Raums zurück
     * @return Liste der Entities innerhalb des Raums
     */
    public ArrayList<IGameObject> getEntities()
    {
        return _entities;
    }
    
    /**
     * Gibt den unteren inklusiven x-Wert der Bounding Box des Raums zurück
     * @return unterer inklusiver x-Wert der Bounding Box
     */
    public int getMinX()
    {
        return _minX;
    }
    
    /**
     * Gibt den oberen inklusiven x-Wert der Bounding Box des Raums zurück
     * @return oberer inklusiver x-Wert der Bounding Box
     */
    public int getMaxX()
    {
        return _maxX;
    }
    
    /**
     * Gibt den unteren inklusiven z-Wert der Bounding Box des Raums zurück
     * @return unterer inklusiver z-Wert der Bounding Box
     */
    public int getMinZ()
    {
        return _minZ;
    }
    
    /**
     * Gibt den oberen inklusiven z-Wert der Bounding Box des Raums zurück
     * @return oberer inklusiver z-Wert der Bounding Box
     */
    public int getMaxZ()
    {
        return _maxZ;
    }
}
