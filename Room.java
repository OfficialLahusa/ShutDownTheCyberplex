import java.util.*;

/**
 * Ein räumlich eingegrenzter Abschnitt einer Gridmap, innerhalb dessen Simulationen durchgeführt werden
 * 
 * @author Lasse Huber-Saffer
 * @version 21.12.2021
 */
public class Room
{
    private HashSet<Vector2i> _tiles;
    private ArrayList<IGameObject> _geometry;
    private ArrayList<ICollider> _colliders;
    private ArrayList<Vector2i> _doorLocations;
    private ArrayList<IDoorGameObject> _doors;
    
    // Bounds im Grid
    private int _minX;
    private int _minZ;
    private int _maxX;
    private int _maxZ;

    /**
     * Konstruktor für Objekte der Klasse Room
     */
    public Room()
    {
        _tiles = new HashSet<Vector2i>();
        _geometry = new ArrayList<IGameObject>();
        _colliders = new ArrayList<ICollider>();
        _doorLocations = new ArrayList<Vector2i>();
        _doors = new ArrayList<IDoorGameObject>();
    }
    
    /**
     * Verarbeitet die Tile-Werte und erstellt die spielbare Map
     * @param tileProviders Hashmap der TileProvider. Der Key entspricht der Tile-ID
     * @param colliderProviders Hashmap der ColliderProvider. Der Key entspricht der Tile-ID
     * @param tileLayer Mapdaten
     */
    public void populate(HashMap<Integer, ITileProvider> tileProviders, HashMap<Integer, IColliderProvider> colliderProviders, ArrayList<ArrayList<Integer>> tileLayer)
    {
        // Geometrieebene (Iteriert nur innerhalb der Bounds)
        for(int z = _minZ; z <= _maxZ; z++)
        {
            for(int x = _minX; x <= _maxX; x++)
            {
                int value = tileLayer.get(z).get(x);
                if(value != -1 && this.contains(x, z))
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
                        
                        _colliders.addAll(provider.getColliders(env, x, z));
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
        for(ICollider col : _colliders)
        {
            collider.detectCollision(col);
        }
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        for(IGameObject obj : _geometry)
        {
            obj.draw(renderer, camera);
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
     * Gibt die Türobjekte des Raums zurück
     * @return Liste der Türobjekte des Raums
     */
    public ArrayList<IDoorGameObject> getDoors()
    {
        return _doors;
    }
    
    /**
     * Gibt die Collider des Raums zurück
     * @return Liste der Collider des Raums
     */
    public ArrayList<ICollider> getColliders()
    {
        return _colliders;
    }
}
