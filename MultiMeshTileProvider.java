import java.util.*;
import javafx.util.*;

/**
 * TileProvider, der für die Tile immer die selben Meshes platziert
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
 */
public class MultiMeshTileProvider implements ITileProvider
{
    private ArrayList<Pair<Mesh, TurtleColor>> _coloredMeshes;

    /**
     * Konstruktor für Objekte der Klasse MultiMeshTileProvider
     * @param coloredMeshes Mesh-Farb-Paare, die vom Provider verwendet werden
     * @param color Farbe, die die Vorlage haben soll
     */
    public MultiMeshTileProvider(ArrayList<Pair<Mesh, TurtleColor>> coloredMeshes)
    {
        _coloredMeshes = coloredMeshes;
    }
    
        
    /**
     * @see ITileProvider#getTileObjects()
     */
    public ArrayList<IGameObject> getTileObjects(TileEnvironment env, int x, int z)
    {
        ArrayList<IGameObject> result = new ArrayList<IGameObject>();
        for(Pair<Mesh, TurtleColor> coloredMesh : _coloredMeshes)
        {
            result.add(
                new StaticGameObject(
                    coloredMesh.getKey(), coloredMesh.getValue(),
                    new Vector3((x + 0.5) * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 0.5) * MapHandler.TILE_WIDTH),
                    new Vector3(), new Vector3(1.0, 1.0, 1.0)
                )
            );
        }
        return result;
    }
    
    /**
     * @see ITileProvider#requiresEnvironment()
     */
    public boolean requiresEnvironment()
    {
        return false;
    }
    
    /**
     * Gibt eine Referenz zur Liste der Mesh-Farb-Paare des Providers zurück
     * @return Referenz zur Liste der Mesh-Farb-Paare
     */
    public ArrayList<Pair<Mesh, TurtleColor>> getColoredMeshes()
    {
        return _coloredMeshes;
    }
}
