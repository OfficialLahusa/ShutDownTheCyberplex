import java.util.*;

/**
 * Ein räumlich eingegrenzter Abschnitt einer Gridmap, innerhalb dessen Simulationen durchgeführt werden
 * 
 * @author Lasse Huber-Saffer
 * @version 13.12.2021
 */
public class Room
{
    private HashSet<Vector2i> _tiles;
    private ArrayList<IGameObject> _geometry;
    private ArrayList<Vector2i> _doorLocations;
    private ArrayList<DoorGameObject> _doors;
    
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
        _doorLocations = new ArrayList<Vector2i>();
        _doors = new ArrayList<DoorGameObject>();
    }
    
    /**
     * Verarbeitet die Tile-Werte und erstellt die spielbare Map
     * @param tileProviders Hashmap der TileProvider. Der Key entspricht der Tile-ID
     * @param tileLayer Mapdaten
     * @param mirrorZAxis soll eine Spiegelung entlang der z-Achse stattfinden, sodass die Map im Spiel wie im Editor angezeigt wird?
     */
    public void populate(HashMap<Integer, ITileProvider> tileProviders, ArrayList<ArrayList<Integer>> tileLayer)
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
                }
            }
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
    
    public void addDoor(DoorGameObject door)
    {
        if(door != null)
        {
            _doors.add(door);
        }
    }
    
    public boolean contains(int x, int z)
    {
        return _tiles.contains(new Vector2i(x, z));
    }
    
    public void addTile(Vector2i pos)
    {
        addTile(pos.getX(), pos.getY());
    }
    
    public boolean contains(Vector2i pos)
    {
        return contains(pos.getX(), pos.getY());
    }
    
    public ArrayList<DoorGameObject> getDoors()
    {
        return _doors;
    }
}
