import java.util.*;

/**
 * Beschreiben Sie hier die Klasse TileEnvironment.
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */
public class TileEnvironment
{
    // Position
    public int x;
    public int z;
    
    // Umgebende TileTypes - p = positive (+1 offset), n = negative (-1 offset)
    public int px = -1;
    public int pxpz = -1;
    public int pz = -1;
    public int nxpz = -1;
    public int nx = -1;
    public int nxnz = -1;
    public int nz = -1;
    public int pxnz = -1;
    
    /**
     * Konstruktor für Objekte der Klasse TileEnvironment auf Basis einer Map und einer Position in dieser Map
     * @param tileValues Tile-Werte der Map (Zeilen mit jeweils gleicher Länge und Werten von nicht null)
     * @param x x-Position des Zentrums (Muss innerhalb der Karte liegen)
     * @param y y-Position des Zentrums (Muss innerhalb der Karte liegen)
     */
    public TileEnvironment(ArrayList<ArrayList<Integer>> tileValues, int x, int z)
    {
        if(tileValues == null) {
            throw new IllegalArgumentException("The value of tileValues was null when loading the TileEnvironment");
        }
        
        int height = tileValues.size();
        int width;
        
        ArrayList<Integer> firstRow = tileValues.get(0);
        if(firstRow == null)
        {
            throw new IllegalArgumentException("Row was null, could not read width");
        }
        else
        {
            width = firstRow.size();
        }
        
        // x- und z-Positionsüberprüfung
        if(x < 0 || x >= width || z < 0 || z >= height)
        {
            throw new IllegalArgumentException("TileEnvironment position out of bounds");
        }
        
        // Überprüft jede umgebende Position darauf, ob sie in der Map enthalten sind, und setzt sie dementsprechend
        px      = (x < width - 1)                   ? tileValues.get( z ).get(x+1) : -1;
        pxpz    = (x < width - 1 && z < height - 1) ? tileValues.get(z+1).get(x+1) : -1;
        pz      = (z < height - 1)                  ? tileValues.get(z+1).get( x ) : -1;
        nxpz    = (x > 0 && z < height - 1)         ? tileValues.get(z+1).get(x-1) : -1;
        nx      = (x > 0)                           ? tileValues.get( z ).get(x-1) : -1;
        nxnz    = (x > 0 && z > 0)                  ? tileValues.get(z-1).get(x-1) : -1;
        nz      = (z > 0)                           ? tileValues.get(z-1).get( x ) : -1;
        pxnz    = (x < width - 1 && z > 0)          ? tileValues.get(z-1).get(x+1) : -1;
    }

    
}
