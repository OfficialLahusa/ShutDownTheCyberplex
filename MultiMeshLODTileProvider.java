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
     * Gibt die GameObjects zurück, die der TileProvider in der gegebenen Umgebung generiert
     * @param env Umgebung der Tile
     * @param x x-Position der Tile
     * @param y y-Position der Tile
     * @param mirrorZAxis gibt an, ob z-Achse der generierten Objekte gespiegelt sein soll
     * @return Liste an GameObjects, die von der Tile platziert werden
     */
    public ArrayList<IGameObject> getTileObjects(TileEnvironment env, int x, int z, double tileWidth, boolean mirrorZAxis)
    {
        ArrayList<IGameObject> result = new ArrayList<IGameObject>();
        for(Pair<ArrayList<Pair<Double, Mesh>>, String> coloredLODMesh : _coloredLODMeshes)
        {
            result.add(new StaticLODGameObject(coloredLODMesh.getKey(), coloredLODMesh.getValue(), new Vector3((x + 0.5) * tileWidth, 0.0, (mirrorZAxis ? -1 : 1) * (z + 0.5) * tileWidth)));
        }
        return result;
    }
    
    /**
     * Gibt zurück, ob der TileProvider ein TileEnvironment als Parameter der Funktion getStaticTileObject bekommen soll, oder nicht, da nicht jeder TileProvider-Typ diesen Parametertyp benötigt
     * @return Wahrheitswert der Aussage "Dieser TileProvider benötigt als Parameter ein TileEnvironment ungleich null"
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
