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
    private ArrayList<StaticGameObject> _mapGeometry;
    
    public static final double TILE_WIDTH = 8.0;
    
    private Vector3 _playerSpawn;

    /**
     * Konstruktor für Objekte der Klasse GridMap
     */
    public GridMap()
    {
        _tileLayer = new ArrayList<ArrayList<Integer>>();
        _functionLayer = new ArrayList<ArrayList<Integer>>();
        _mapGeometry = new ArrayList<StaticGameObject>();
        _playerSpawn = new Vector3(0.0, 2.0, 0.0);
    }
    
    public GridMap(ArrayList<ArrayList<Integer>> tileLayer, ArrayList<ArrayList<Integer>> functionLayer)
    {
        _tileLayer = tileLayer;
        _functionLayer = functionLayer;
        _mapGeometry = new ArrayList<StaticGameObject>();
        _playerSpawn = new Vector3(0.0, 2.0, 0.0);
    }
    
    public void populate(ArrayList<ITileProvider> tileProviders)
    {
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
                        
                        _mapGeometry.addAll(provider.getStaticTileObjects(env, x, z, TILE_WIDTH));
                    }
                }
            }
        }
        
        for(int z = 0; z < _functionLayer.size(); z++)
        {
            for(int x = 0; x < _functionLayer.get(z).size(); x++)
            {
                int value = _functionLayer.get(z).get(x);
                if(value == 20)
                {
                    _playerSpawn = new Vector3((x + 0.5) * TILE_WIDTH, 2.0, (z + 0.5) * TILE_WIDTH);
                }
            }
        }
    }
    
    public Vector3 getPlayerSpawn()
    {
        return _playerSpawn;
    }

    public void draw(Renderer renderer, Camera camera)
    {
        for(int i = 0; i < _mapGeometry.size(); i++)
        {
            _mapGeometry.get(i).draw(renderer, camera);
        }
    }
}
