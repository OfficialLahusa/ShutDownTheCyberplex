import java.util.*;

/**
 * Ein TileProvider, der Mauern und optional Säulen in Abhängigkeit von der Umgebung platziert
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2021
 */
public class WallTileProvider implements ITileProvider
{
    private Mesh _wallMesh;
    private TurtleColor _wallColor;
    private Mesh _pillarMesh;
    private TurtleColor _pillarColor;
    
    /**
     * Konstruktor für Objekte der Klasse WallTileProvider
     * @param wallMesh Mesh, das für die Vorlage verwendet werden soll (Eine Seite auf der X-Achse, der Mittelpunkt auf der Y-Achse)
     * @param wallColor Farbe, die die Vorlage haben soll
     */
    public WallTileProvider(Mesh wallMesh, TurtleColor wallColor)
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
    public WallTileProvider(Mesh wallMesh, TurtleColor wallColor, Mesh pillarMesh, TurtleColor pillarColor)
    {
        _wallMesh = wallMesh;
        _wallColor = wallColor;
        _pillarMesh = pillarMesh;
        _pillarColor = pillarColor;
    }
    
    /**
     * Gibt ein GameObject zurück, das für eine bestimmte Tile eine Mauer in eine bestimmte Richtung darstellt
     * @param variant Seite/Richtung der Mauer. px: 0, pz: 1, nx: 2, nz: 3 [0,3]
     * @param x x-Position der Tile
     * @param y y-Position der Tile
     * @return neue Instanz eines GameObjects der Mauer
     */
    public IGameObject getWallVariant(int variant, int x, int z)
    {
        int xOffFac = 0, zOffFac = 0;
        if(variant == 0) xOffFac = 1;
        else if(variant == 1) zOffFac = 1;
        else if(variant == 2) xOffFac = -1;
        else if(variant == 3) zOffFac = -1;
        return new StaticGameObject(
            getWallMesh(), getWallColor(), new Vector3((x + (1 + xOffFac) * 0.5) * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + (1 + zOffFac) * 0.5) * MapHandler.TILE_WIDTH),
            new Vector3(0.0, (variant == 0 || variant == 2) ? 90.0 : 0.0, 0.0), new Vector3(1.0, 1.0, 1.0)
        );
    }
        
    /**
     * @see ITileProvider#getTileObjects()
     */
    public ArrayList<IGameObject> getTileObjects(TileEnvironment env, int x, int z)
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
        if(!Tile.isSolidOrNone(env.px))
        {
            result.add(getWallVariant(0, x, z));
        }
        // Wand auf +Z-Seite
        if(!Tile.isSolidOrNone(env.pz))
        {
            result.add(getWallVariant(1, x, z));
        }
        // Wand auf -X-Seite
        if(!Tile.isSolidOrNone(env.nx))
        {
            result.add(getWallVariant(2, x, z));
        }
        // Wand auf -Z-Seite
        if(!Tile.isSolidOrNone(env.nz))
        {
            result.add(getWallVariant(3, x, z));
        }
        
        // Säulen
        // Säule an Ecke -X-Z
        if(!Tile.isSolidOrNone(env.nxnz) && !Tile.isSolid(env.nx) && !Tile.isSolid(env.nz))
        {
            result.add(
                new StaticGameObject(
                    getPillarMesh(), getPillarColor(),
                    new Vector3(x * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * z * MapHandler.TILE_WIDTH),
                    new Vector3(), new Vector3(1.0, 1.0, 1.0)
                )
            );
        }
        // Säule an Ecke +X-Z
        if(!Tile.isSolidOrNone(env.pxnz) && !Tile.isSolid(env.px) && !Tile.isSolid(env.nz))
        {
            result.add(
                new StaticGameObject(
                    getPillarMesh(), getPillarColor(),
                    new Vector3((x + 1.0) * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * z * MapHandler.TILE_WIDTH),
                    new Vector3(), new Vector3(1.0, 1.0, 1.0)
                )
            );
        }
        // Säule an Ecke +X+Z
        if(!Tile.isSolidOrNone(env.pxpz) && !Tile.isSolid(env.px) && !Tile.isSolid(env.pz))
        {
            result.add(
                new StaticGameObject(
                    getPillarMesh(), getPillarColor(),
                    new Vector3((x + 1.0) * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 1.0) * MapHandler.TILE_WIDTH),
                    new Vector3(), new Vector3(1.0, 1.0, 1.0)
                )
            );
        }
        // Säule an Ecke -X+Z
        if(!Tile.isSolidOrNone(env.nxpz) && !Tile.isSolid(env.nx) && !Tile.isSolid(env.pz))
        {
            result.add(
                new StaticGameObject(
                    getPillarMesh(), getPillarColor(),
                    new Vector3(x * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 1.0) * MapHandler.TILE_WIDTH),
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
    public TurtleColor getWallColor()
    {
        return _wallColor;
    }
    
    /**
     * Gibt die Farbe der Vorlage zurück
     * @return Farbe der Vorlage
     */
    public TurtleColor getPillarColor()
    {
        return _pillarColor;
    }
}
