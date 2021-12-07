import java.util.*;

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
    
    public static final double TILE_WIDTH = 8.0;
    
    private Vector3 _playerSpawn;

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
        _playerSpawn = new Vector3(0.0, 2.0, 0.0);
    }
    
    /**
     * Verarbeitet die Tile-Werte und erstellt die spielbare Map
     * @param tileProviders Liste der TileProvider. Der Index in der Liste entspricht der Tile-ID
     * @param mirrorZAxis
     */
    public void populate(ArrayList<ITileProvider> tileProviders, boolean mirrorZAxis)
    {
        // Geometrieebene
        for(int z = 0; z < _tileLayer.size(); z++)
        {
            for(int x = 0; x < _tileLayer.get(z).size(); x++)
            {
                int value = _tileLayer.get(z).get(x);
                if(value > -1)
                {
                    if(value >= tileProviders.size())
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
                        
                        _mapGeometry.addAll(provider.getTileObjects(env, x, z, TILE_WIDTH, mirrorZAxis));
                    }
                }
            }
        }
        
        // Funktionsebene
        for(int z = 0; z < _functionLayer.size(); z++)
        {
            for(int x = 0; x < _functionLayer.get(z).size(); x++)
            {
                int value = _functionLayer.get(z).get(x);
                if(value == 20)
                {
                    _playerSpawn = new Vector3((x + 0.5) * TILE_WIDTH, 2.0, (mirrorZAxis ? -1 : 1) * (z + 0.5) * TILE_WIDTH);
                }
            }
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
     * Zeichnet die Mapgeometrie mit einem gegebenen Renderer in das Sichtfeld einer gegebenen Kamera
     * @param renderer zu nutzender Renderer
     * @param camera zu benutzende Kamera
     */
    public void draw(Renderer renderer, Camera camera)
    {
        for(int i = 0; i < _mapGeometry.size(); i++)
        {
            _mapGeometry.get(i).draw(renderer, camera);
        }
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
