import java.util.*;
import javafx.util.*;

/**
 * TileProvider für Türen, die in Abhängigkeit von der Umgebung entlang der X- oder Z-Achse ausgerichtet sein können
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */
public class DoorTileProvider implements ITileProvider
{
    private ArrayList<Pair<Mesh, String>> _coloredMeshes;

    /**
     * Konstruktor für Objekte der Klasse DoorTileProvider
     * @param mesh Mesh, das für die Vorlage verwendet werden soll
     * @param color Farbe, die die Vorlage haben soll
     */
    public DoorTileProvider(ArrayList<Pair<Mesh, String>> coloredMeshes)
    {
        _coloredMeshes = coloredMeshes;
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
        
        boolean facingZ = (MapHandler.isTileSolidOrNone(env.px) && MapHandler.isTileSolidOrNone(env.nx));
        
        for(Pair<Mesh, String> coloredMesh : _coloredMeshes)
        {
            result.add(new StaticGameObject(coloredMesh.getKey(), coloredMesh.getValue(), new Vector3((x + 0.5) * tileWidth, 0.0, (mirrorZAxis ? -1 : 1) * (z + 0.5) * tileWidth), new Vector3(0.0, (facingZ)? 90.0 : 0.0, 0.0), new Vector3(1.0, 1.0, 1.0)));
        }
        return result;
    }
    
    /**
     * Gibt zurück, ob der TileProvider ein TileEnvironment als Parameter der Funktion getStaticTileObject bekommen soll, oder nicht, da nicht jeder TileProvider-Typ diesen Parametertyp benötigt
     * @return Wahrheitswert der Aussage "Dieser TileProvider benötigt als Parameter ein TileEnvironment ungleich null"
     */
    public boolean requiresEnvironment()
    {
        return true;
    }
    
    /**
     * Gibt eine Referenz zur Liste der Mesh-Farb-Paare des Providers zurück
     * @return Referenz zur Liste der Mesh-Farb-Paare
     */
    public ArrayList<Pair<Mesh, String>> getColoredMeshes()
    {
        return _coloredMeshes;
    }
}
