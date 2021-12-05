import java.util.*;
import javafx.util.*;

/**
 * Generator-Klasse, die einige Standard-LOD-Erstellungen ermöglicht
 * 
 * @author Lasse Huber-Saffer
 * @version 05.12.2021
 */
public class LODGenerator
{
    private WavefrontObjectLoader _objLoader;
    private Mesh _basicFloorTile;
    
    public LODGenerator()
    {
        _objLoader = new WavefrontObjectLoader();
        _basicFloorTile = _objLoader.loadFromFile("./res/models/basic_floor.obj");
    }
    
    public ArrayList<Pair<Double, Mesh>> createBasicFloorTileLOD(Mesh fullResTile, double distance)
    {
        ArrayList<Pair<Double, Mesh>> result = new ArrayList<Pair<Double, Mesh>>();
        
        result.add(new Pair<Double, Mesh>(0.0, fullResTile));
        result.add(new Pair<Double, Mesh>(distance, _basicFloorTile));
        
        return result;
    }
}
