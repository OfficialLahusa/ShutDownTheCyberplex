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
    private ArrayList<DoorGameObject> _doors;
    
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
        _doors = new ArrayList<DoorGameObject>();
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
    
    public boolean contains(int x, int z)
    {
        return _tiles.contains(new Vector2i(x, z));
    }
}
