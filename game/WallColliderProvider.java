package game;

import maths.*;
import physics.*;
import java.util.*;

/**
 * Ein ColliderProvider, der Collider für Mauern in Abhängigkeit von der Umgebung platziert
 * 
 * @author Lasse Huber-Saffer
 * @version 20.12.2021
 */
public class WallColliderProvider implements IColliderProvider
{
    /**
     * Konstruktor für Objekte der Klasse WallColliderProvider
     */
    public WallColliderProvider()
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
        
        
        // Ecken der Tile
        Vector2 nxnz = new Vector2(x       * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * z       * MapHandler.TILE_WIDTH);
        Vector2 nxpz = new Vector2(x       * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 1) * MapHandler.TILE_WIDTH);
        Vector2 pxnz = new Vector2((x + 1) * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * z       * MapHandler.TILE_WIDTH);
        Vector2 pxpz = new Vector2((x + 1) * MapHandler.TILE_WIDTH, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 1) * MapHandler.TILE_WIDTH);
        
        // Wände
        // Wand auf +X-Seite
        if(!Tile.isSolidOrNone(env.px))
        {
            result.add(new LineCollider(pxnz, pxpz, PhysicsLayer.SOLID));
        }
        // Wand auf +Z-Seite
        if(!Tile.isSolidOrNone(env.pz))
        {
            result.add(new LineCollider(nxpz, pxpz, PhysicsLayer.SOLID));
        }
        // Wand auf -X-Seite
        if(!Tile.isSolidOrNone(env.nx))
        {
            result.add(new LineCollider(nxnz, nxpz, PhysicsLayer.SOLID));
        }
        // Wand auf -Z-Seite
        if(!Tile.isSolidOrNone(env.nz))
        {
            result.add(new LineCollider(pxnz, nxnz, PhysicsLayer.SOLID));
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
