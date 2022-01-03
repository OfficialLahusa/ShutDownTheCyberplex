package game;

import maths.*;
import core.*;
import util.*;
import physics.*;
import java.util.*;
import javafx.util.*;

/**
 * TileProvider für Türen, die in Abhängigkeit von der Umgebung entlang der X- oder Z-Achse ausgerichtet sein können
 * 
 * @author Lasse Huber-Saffer
 * @version 03.01.2022
 */
public class DoorTileProvider implements ITileProvider
{
    private ArrayList<Pair<Mesh, TurtleColor>> _coloredMeshesClosed;
    private ArrayList<Pair<Mesh, TurtleColor>> _coloredMeshesOpen;
    private boolean _isOpen;
    
    private IColliderProvider _closedColliderProvider;
    private IColliderProvider _openColliderProvider;
    
    private Mesh _lockMesh;
    private ITileProvider _floorTileProvider;
    private WallTileProvider _wallTileProvider;
    
    private SoundEngine _soundEngine;
    private String _openSoundKey;
    private String _closeSoundKey;
    private Double _soundVolume;
    
    /**
     * Konstruktor für Objekte der Klasse DoorTileProvider
     * @param coloredMeshesClosed Liste von Mesh-Farb-Paaren, die im geschlossenen Zustand gerendert werden
     * @param coloredMeshesOpen Liste von Mesh-Farb-Paaren, die im offenen Zustand gerendert werden
     * @param isOpen Offenheit der Tür
     * @param closedColliderProvider (Optional) ColliderProvider, der im geschlossenen Zustand für Kollisionsberechnungen verwendet wird
     * @param openColliderProvider (Optional) ColliderProvider, der im offenen Zustand für Kollisionsberechnungen verwendet wird
     * @param lockMesh (Optional) Mesh, das gerendert werden soll, wenn die Tür abgeschlossen ist
     * @param floorTileProvider (Optional) TileProvider, der für den Boden unter der Tür verwendet werden soll
     * @param wallTileProvider (Optional) WallTileProvider, der für die Wände an den Seiten der Tür verwendet werden soll
     * @param soundEngine (Optional) SoundEngine, in der die nachfolgenden Sounds vorzufinden sind
     * @param openSoundKey (Optional) Soundquelle, die für den Sound beim Öffnen der Tür verwendet wird
     * @param closeSoundKey (Optional) Soundquelle, die für den Sound beim Schließen der Tür verwendet wird
     * @param soundVolume (Optional) Lautstärke der Sounds
     */
    public DoorTileProvider(
        ArrayList<Pair<Mesh, TurtleColor>> coloredMeshesClosed, ArrayList<Pair<Mesh, TurtleColor>> coloredMeshesOpen, boolean isOpen,
        IColliderProvider closedColliderProvider, IColliderProvider openColliderProvider,
        Mesh lockMesh, ITileProvider floorTileProvider, WallTileProvider wallTileProvider,
        SoundEngine soundEngine, String openSoundKey, String closeSoundKey, Double soundVolume
    )
    {
        _coloredMeshesClosed = coloredMeshesClosed;
        _coloredMeshesOpen = coloredMeshesOpen;
        _isOpen = isOpen;
        
        _closedColliderProvider = closedColliderProvider;
        _openColliderProvider = openColliderProvider;
        
        _lockMesh = lockMesh;
        _floorTileProvider = floorTileProvider;
        _wallTileProvider = wallTileProvider;
        
        _soundEngine = soundEngine;
        _openSoundKey = openSoundKey;
        _closeSoundKey = closeSoundKey;
        _soundVolume = soundVolume;
    }
    
        
    /**
     * @see ITileProvider#getTileObjects()
     */
    public ArrayList<IGameObject> getTileObjects(TileEnvironment env, int x, int z)
    {
        ArrayList<IGameObject> result = new ArrayList<IGameObject>();
        
        boolean facingZ = (Tile.isSolidOrNone(env.px) && Tile.isSolidOrNone(env.nx));
        
        ArrayList<ICollider> closedColliders = null;
        ArrayList<ICollider> openColliders = null;
        if(_closedColliderProvider != null)
        {
            closedColliders = _closedColliderProvider.getColliders(env, x, z);
        }
        if(_openColliderProvider != null)
        {
            openColliders = _openColliderProvider.getColliders(env, x, z);
        }
        
        ArrayList<IGameObject> floorGeometry = null, wallGeometry = null;
        if(_floorTileProvider != null)
        {
            floorGeometry = new ArrayList<IGameObject>();
            floorGeometry.addAll(_floorTileProvider.getTileObjects(env, x, z));
        }
        if(_wallTileProvider != null)
        {
            wallGeometry = new ArrayList<IGameObject>();
            if(!facingZ)
            {
                wallGeometry.add(_wallTileProvider.getWallVariant(1, x, z));
                wallGeometry.add(_wallTileProvider.getWallVariant(3, x, z));
            }
            else
            {
                wallGeometry.add(_wallTileProvider.getWallVariant(0, x, z));
                wallGeometry.add(_wallTileProvider.getWallVariant(2, x, z));
            }
        }
        
        ICollider[] closedCollidersArray = closedColliders.toArray(new ICollider[closedColliders.size()]);
        ICollider[] openCollidersArray = openColliders.toArray(new ICollider[openColliders.size()]);
        
        // Feststellen, ob die Tür mit einem Schloss einer bestimmten Farbe verriegelt werden soll
        TurtleColor lockColor = null;
        if(env.func == Tile.GENERIC_LOCK)
        {
            lockColor = TurtleColor.YELLOW;
        }
        else if(env.func == Tile.RED_LOCK)
        {
            lockColor = TurtleColor.RED;
        }
        else if(env.func == Tile.GREEN_LOCK)
        {
            lockColor = TurtleColor.GREEN;
        }
        else if(env.func == Tile.BLUE_LOCK)
        {
            lockColor = TurtleColor.BLUE;
        }
        
        DoorGameObject obj = new DoorGameObject(
            new Vector3((x + 0.5) * MapHandler.TILE_WIDTH, 0.0, (MapHandler.MIRROR_Z_AXIS ? -1 : 1) * (z + 0.5) * MapHandler.TILE_WIDTH),
            new Vector3(0.0, (facingZ)? 90.0 : 0.0, 0.0), new Vector3(1.0, 1.0, 1.0),
            facingZ, _isOpen,  new Vector2i(x, z),
            _coloredMeshesClosed, _coloredMeshesOpen,
            env.room, lockColor, _lockMesh,
            new CompoundCollider(closedCollidersArray, PhysicsLayer.SEMISOLID), new CompoundCollider(openCollidersArray, PhysicsLayer.SEMISOLID),
            floorGeometry, wallGeometry,
            _soundEngine, _openSoundKey, _closeSoundKey, _soundVolume
        );
        
        result.add(obj);

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
     * Gibt eine Referenz zur Liste der Mesh-Farb-Paare des Providers zurück, die im geschlossenen Zustand verwendet werden
     * @return Referenz zur Liste der Mesh-Farb-Paare, die im geschlossenen Zustand verwendet werden
     */
    public ArrayList<Pair<Mesh, TurtleColor>> getColoredMeshesClosed()
    {
        return _coloredMeshesClosed;
    }
    
    /**
     * Gibt eine Referenz zur Liste der Mesh-Farb-Paare des Providers zurück, die im offenen Zustand verwendet werden
     * @return Referenz zur Liste der Mesh-Farb-Paare, die im offenen Zustand verwendet werden
     */
    public ArrayList<Pair<Mesh, TurtleColor>> getColoredMeshesOpen()
    {
        return _coloredMeshesOpen;
    }
}
