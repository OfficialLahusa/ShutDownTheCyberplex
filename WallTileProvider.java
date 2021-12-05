import java.util.*;

/**
 * Ein TileProvider, der Mauern und optional Säulen in Abhängigkeit von der Umgebung platziert
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */
public class WallTileProvider implements ITileProvider
{
    private Mesh _wallMesh;
    private String _wallColor;
    private Mesh _pillarMesh;
    private String _pillarColor;
    
    /**
     * Konstruktor für Objekte der Klasse WallTileProvider
     * @param wallMesh Mesh, das für die Vorlage verwendet werden soll (Eine Seite auf der X-Achse, der Mittelpunkt auf der Y-Achse)
     * @param wallColor Farbe, die die Vorlage haben soll
     */
    public WallTileProvider(Mesh wallMesh, String wallColor)
    {
        this(wallMesh, wallColor, null, null);
    }
    
    /**
     * Konstruktor für Objekte der Klasse WallTileProvider
     * @param wallMesh Mesh, das für die Vorlage verwendet werden soll (Eine Seite auf der X-Achse, der Mittelpunkt auf der Y-Achse)
     * @param wallColor Farbe, die die Vorlage haben soll
     * @param pillarMesh Mesh, das für die Vorlage verwendet werden soll (Liegt auf der Y-Achse), darf null sein
     * @param pillarColor Farbe, die die Vorlage haben soll, darf null sein
     */
    public WallTileProvider(Mesh wallMesh, String wallColor, Mesh pillarMesh, String pillarColor)
    {
        _wallMesh = wallMesh;
        _wallColor = wallColor;
        _pillarMesh = pillarMesh;
        _pillarColor = pillarColor;
    }
    
        
    /**
     * Gibt die GameObjects zurück, die der TileProvider in der gegebenen Umgebung generiert
     * @param env Umgebung der Tile
     * @param x x-Position der Tile
     * @param y y-Position der Tile
     * @return Liste an GameObjects, die von der Tile platziert werden
     */
    public ArrayList<IGameObject> getTileObjects(TileEnvironment env, int x, int z, double tileWidth)
    {
        // Environment muss bei diesem Typ != null sein (siehe ITileProvider.requiresEnvironment())
        if(env == null)
        {
            throw new IllegalArgumentException("TileEnvironment was null when providing wall tiles");
        }
        
        // Rückgabe-ArrayList
        ArrayList<IGameObject> result = new ArrayList<IGameObject>();
        
        // Wände
        // Wand auf +X-Seite
        if(!MapHandler.isTileSolidOrNone(env.px))
        {
            result.add(new StaticGameObject(getWallMesh(), getWallColor(), new Vector3((x + 1.0) * tileWidth, 0.0, (z + 0.5) * tileWidth), new Vector3(0.0, 90.0, 0.0), new Vector3(1.0, 1.0, 1.0)));
        }
        // Wand auf +Z-Seite
        if(!MapHandler.isTileSolidOrNone(env.pz))
        {
            result.add(new StaticGameObject(getWallMesh(), getWallColor(), new Vector3((x + 0.5) * tileWidth, 0.0, (z + 1.0) * tileWidth)));
        }
        // Wand auf -X-Seite
        if(!MapHandler.isTileSolidOrNone(env.nx))
        {
            result.add(new StaticGameObject(getWallMesh(), getWallColor(), new Vector3((x      ) * tileWidth, 0.0, (z + 0.5) * tileWidth), new Vector3(0.0, 90.0, 0.0), new Vector3(1.0, 1.0, 1.0)));
        }
        // Wand auf -Z-Seite
        if(!MapHandler.isTileSolidOrNone(env.nz))
        {
            result.add(new StaticGameObject(getWallMesh(), getWallColor(), new Vector3((x + 0.5) * tileWidth, 0.0, (z      ) * tileWidth)));
        }
        
        // Säulen
        // Säule an Ecke -X-Z
        if(!MapHandler.isTileSolidOrNone(env.nxnz) && !MapHandler.isTileSolidOrNone(env.nx) && !MapHandler.isTileSolidOrNone(env.nz))
        {
            result.add(new StaticGameObject(getPillarMesh(), getPillarColor(), new Vector3(x * tileWidth, 0.0, z * tileWidth)));
        }
        // Säule an Ecke +X-Z
        if(!MapHandler.isTileSolidOrNone(env.pxnz) && !MapHandler.isTileSolidOrNone(env.px) && !MapHandler.isTileSolidOrNone(env.nz))
        {
            result.add(new StaticGameObject(getPillarMesh(), getPillarColor(), new Vector3((x + 1.0) * tileWidth, 0.0, z * tileWidth)));
        }
        // Säule an Ecke +X+Z
        if(!MapHandler.isTileSolidOrNone(env.pxpz) && !MapHandler.isTileSolidOrNone(env.px) && !MapHandler.isTileSolidOrNone(env.pz))
        {
            result.add(new StaticGameObject(getPillarMesh(), getPillarColor(), new Vector3((x + 1.0) * tileWidth, 0.0, (z + 1.0) * tileWidth)));
        }
        // Säule an Ecke -X+Z
        if(!MapHandler.isTileSolidOrNone(env.nxpz) && !MapHandler.isTileSolidOrNone(env.nx) && !MapHandler.isTileSolidOrNone(env.pz))
        {
            result.add(new StaticGameObject(getPillarMesh(), getPillarColor(), new Vector3(x * tileWidth, 0.0, (z + 1.0) * tileWidth)));
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
     * Gibt eine neue Instanz des Wandmeshs der Vorlage zurück
     * @return neue Instanz des Wandmeshs der Vorlage
     */
    public Mesh getWallMesh()
    {
        return new Mesh(_wallMesh);
    }
    
    /**
     * Gibt eine neue Instanz des Säulenmeshs der Vorlage zurück
     * @return neue Instanz des Säulenmeshs der Vorlage
     */
    public Mesh getPillarMesh()
    {
        return new Mesh(_pillarMesh);
    }
    
    /**
     * Gibt die Farbe der Vorlage zurück
     * @return Farbe der Vorlage
     */
    public String getWallColor()
    {
        return _wallColor;
    }
    
    /**
     * Gibt die Farbe der Vorlage zurück
     * @return Farbe der Vorlage
     */
    public String getPillarColor()
    {
        return _pillarColor;
    }
}
