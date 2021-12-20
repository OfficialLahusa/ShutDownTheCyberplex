import java.util.*;

/**
 * Ein ColliderProvider, der Collider für einen Tunnel mit einer Blockade in der Mitte in Abhängigkeit von der Umgebung platziert
 * 
 * @author Lasse Huber-Saffer
 * @version 20.12.2021
 */
public class BlockedTunnelColliderProvider implements IColliderProvider
{
    /**
     * Konstruktor für Objekte der Klasse BlockedTunnelColliderProvider
     */
    public BlockedTunnelColliderProvider()
    {
        
    }
        
    /**
     * @see IColliderProvider#getColliders()
     */
    public ArrayList<ICollider> getColliders(TileEnvironment env, int x, int z)
    {
        // Environment muss bei diesem Typ != null sein (siehe ITileProvider.requiresEnvironment())
        if(env == null)
        {
            throw new IllegalArgumentException("TileEnvironment was null when providing wall colliders");
        }
        
        // Rückgabe-ArrayList
        ArrayList<ICollider> result = new ArrayList<ICollider>();
        
        boolean facingZ = (Tile.isSolidOrNone(env.px) && Tile.isSolidOrNone(env.nx));
        
        
        // Ecken der Tile
        Vector2 nxnz = new Vector2(x       * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * z       * MapHandler.TILE_WIDTH);
        Vector2 nxpz = new Vector2(x       * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 1) * MapHandler.TILE_WIDTH);
        Vector2 pxnz = new Vector2((x + 1) * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * z       * MapHandler.TILE_WIDTH);
        Vector2 pxpz = new Vector2((x + 1) * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 1) * MapHandler.TILE_WIDTH);
        
        // Wände
        if(facingZ)
        {
            // Wand auf +X-Seite
            result.add(new LineCollider(pxnz, pxpz, PhysicsLayer.SOLID));
            // Wand auf -X-Seite
            result.add(new LineCollider(nxnz, nxpz, PhysicsLayer.SOLID));
            
            // Barriere
            result.add(
                new LineCollider(
                    new Vector2(x         * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 0.5) * MapHandler.TILE_WIDTH),
                    new Vector2((x + 1.0) * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 0.5) * MapHandler.TILE_WIDTH),
                    PhysicsLayer.SOLID
                )
            );
        }
        else
        {
            // Wand auf +Z-Seite
            result.add(new LineCollider(nxpz, pxpz, PhysicsLayer.SOLID));
            // Wand auf -Z-Seite
            result.add(new LineCollider(pxnz, nxnz, PhysicsLayer.SOLID));
            
            // Barriere
            result.add(
                new LineCollider(
                    new Vector2((x + 0.5) * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * z         * MapHandler.TILE_WIDTH),
                    new Vector2((x + 0.5) * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 1.0) * MapHandler.TILE_WIDTH),
                    PhysicsLayer.SOLID
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
}
