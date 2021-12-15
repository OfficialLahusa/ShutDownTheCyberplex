import java.util.*;
import javafx.util.*;

/**
 * TileProvider, der für die Tile immer die selben LOD-Meshes platziert
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */
public class MultiMeshLODTileProvider implements ITileProvider
{
    private ArrayList<Pair<ArrayList<Pair<Double, Mesh>>, String>> _coloredLODMeshes;

    /**
     * Konstruktor für Objekte der Klasse MultiMeshLODTileProvider
     * @param coloredLODMeshes Liste an Paaren von jeweils Mesh-LOD-Stufen und Farben
     * @param color Farbe, die die Vorlage haben soll
     */
    public MultiMeshLODTileProvider(ArrayList<Pair<ArrayList<Pair<Double, Mesh>>, String>> coloredLODMeshes)
    {
        _coloredLODMeshes = coloredLODMeshes;
    }
    
        
    /**
     * @see ITileProvider#getTileObjects()
     */
    public ArrayList<IGameObject> getTileObjects(TileEnvironment env, int x, int z)
    {
        ArrayList<IGameObject> result = new ArrayList<IGameObject>();
        for(Pair<ArrayList<Pair<Double, Mesh>>, String> coloredLODMesh : _coloredLODMeshes)
        {
            result.add(new StaticLODGameObject(coloredLODMesh.getKey(), coloredLODMesh.getValue(), new Vector3((x + 0.5) * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 0.5) * MapHandler.TILE_WIDTH)));
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
     * Gibt eine Referenz zur Liste an Paaren von jeweils Mesh-LOD-Stufen und Farben des Providers zurück
     * @return Referenz zur Liste an Paaren von jeweils Mesh-LOD-Stufen und Farben
     */
    public ArrayList<Pair<ArrayList<Pair<Double, Mesh>>, String>> getColoredLODMeshes()
    {
        return _coloredLODMeshes;
    }
}
