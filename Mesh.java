import java.util.*;
import javafx.util.*;

/**
 * Eine Speicherklasse für ein dreidimensionales Modell.
 * Das Mesh besteht aus einer Liste an Vertices, also Punkten im dreidimensionalen Raum,
 * die über eine Liste an Indexpaaren zu dreidimensionalen Linien verknüpft werden.
 * 
 * @author Lasse Huber-Saffer
 * @version 22.12.2021
 */
public class Mesh
{
    public ArrayList<Vector3> vertices;
    public ArrayList<Pair<Integer, Integer>> lineIndices;

    /**
     * Konstruktor für Objekte der Klasse Mesh
     */
    public Mesh()
    {
        vertices = new ArrayList<Vector3>();
        lineIndices = new ArrayList<Pair<Integer, Integer>>();
    }
    
    /**
     * Kopie-Konstruktor
     * @param copy zu kopierendes Mesh. Wenn null, wird ein leeres Mesh erzeugt.
     */
    public Mesh(Mesh copy)
    {        
        vertices = new ArrayList<Vector3>();
        lineIndices = new ArrayList<Pair<Integer, Integer>>();
        
        if(copy != null)
        {
            for(int i = 0; i < copy.vertices.size(); i++)
            {
                vertices.add(new Vector3(copy.vertices.get(i)));
            }
            
            for(int i = 0; i < copy.lineIndices.size(); i++)
            {
                Pair<Integer, Integer> linePair = copy.lineIndices.get(i);
                lineIndices.add(new Pair<Integer, Integer>(linePair.getKey(), linePair.getValue()));
            } 
        }
    }
}
