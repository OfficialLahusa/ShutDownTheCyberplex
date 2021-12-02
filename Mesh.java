/**
 * Eine Speicherklasse f�r ein dreidimensionales Modell.
 * Das Mesh besteht aus einer Liste an Vertices, also Punkten im dreidimensionalen Raum,
 * die �ber eine Liste an Indexpaaren zu dreidimensionalen Linien verkn�pft werden.
 * 
 * @author Lasse Huber-Saffer
 * @version 02.12.2021
 */
public class Mesh
{
    public java.util.ArrayList<Vector3> vertices;
    public java.util.ArrayList<javafx.util.Pair<Integer, Integer>> lineIndices;

    /**
     * Konstruktor f�r Objekte der Klasse Mesh
     */
    public Mesh()
    {
        vertices = new java.util.ArrayList<Vector3>();
        lineIndices = new java.util.ArrayList<javafx.util.Pair<Integer, Integer>>();
    }
    
    /**
     * Kopie-Konstruktor
     * @param copy zu kopierendes Mesh
     */
    public Mesh(Mesh copy)
    {
        vertices = new java.util.ArrayList<Vector3>();
        lineIndices = new java.util.ArrayList<javafx.util.Pair<Integer, Integer>>();
        
        for(int i = 0; i < copy.vertices.size(); i++)
        {
            vertices.add(new Vector3(copy.vertices.get(i)));
        }
        
        for(int i = 0; i < copy.lineIndices.size(); i++)
        {
            javafx.util.Pair<Integer, Integer> linePair = copy.lineIndices.get(i);
            lineIndices.add(new javafx.util.Pair<Integer, Integer>(linePair.getKey(), linePair.getValue()));
        }
        
    }
}
