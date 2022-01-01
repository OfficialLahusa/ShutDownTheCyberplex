import java.util.*;
import javafx.util.*;

/**
 * Beschreiben Sie hier die Klasse DoorGameObject.
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
 */
public class DoorGameObject implements IDoorGameObject, IGameObject, ILODGameObject
{
    // Transformation
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private Matrix4 _model;
    
    // Functionality
    private boolean _facingZ;
    private boolean _isOpen;
    private Vector2i _tilePosition;
    private HashSet<Vector2i> _doorTriggers;
    
    // Interpretation: null -> Kein Raum verbunden. >=0 -> Raum mit ID ist verbunden. <0 -> Illegaler Zustand
    private Pair<Integer, Integer> _connectedRoomIDs;
    
    // Rendering
    private ArrayList<Pair<Mesh, TurtleColor>> _coloredMeshesClosed;
    private ArrayList<Pair<Mesh, TurtleColor>> _coloredMeshesOpen;
    private ArrayList<IGameObject> _floor;
    private ArrayList<IGameObject> _walls;
    
    // Physics
    private ICollider _closedCollider;
    private ICollider _openCollider;
    
    // Sound
    private SoundEngine _soundEngine;
    private String _openSoundKey;
    private String _closeSoundKey;    
    
    /**
     * Konstruktor für Türen mit gegebenem Mesh, Position, Rotation, Skalierung und Boden- sowie Wand-GameObjects
     * @param position Position
     * @param rotation Rotation
     * @param scale Skalierung
     * @param facingZ Führt die z-Achse durch diese Tür hindurch?
     * @param isOpen Offenheit der Tür
     * @param tilePosition Position der Tür im Grid
     * @param coloredMeshesClosed Liste von Mesh-Farb-Paaren, die im geschlossenen Zustand gerendert werden
     * @param coloredMeshesOpen Liste von Mesh-Farb-Paaren, die im offenen Zustand gerendert werden
     * @param closedCollider (Optional) Collider, der im geschlossenen Zustand für die Kollisionserkennung verwendet wird
     * @param openCollider (Optional) Collider, der im offenen Zustand für die Kollisionserkennung verwendet wird
     * @param floor (Optional) Liste an GameObjects, die als Fußboden gerendert werden sollen
     * @param walls (Optional) Liste an GameObjects, die als Mauern gerendert werden sollen
     * @param soundEngine (Optional) Sound Engine, in der die nachfolgenden Schlüssel enthalten sind
     * @param openSoundKey (Optional) Schlüssel des Sounds für das Öffnen
     * @param closeSoundKey (Optional) Schlüssel des Sounds für das Schließen
     */
    public DoorGameObject(Vector3 position, Vector3 rotation, Vector3 scale,
        boolean facingZ, boolean isOpen, Vector2i tilePosition,
        ArrayList<Pair<Mesh, TurtleColor>> coloredMeshesClosed, ArrayList<Pair<Mesh, TurtleColor>> coloredMeshesOpen,
        ICollider closedCollider, ICollider openCollider,
        ArrayList<IGameObject> floor, ArrayList<IGameObject> walls,
        SoundEngine soundEngine, String openSoundKey, String closeSoundKey
    )
    {
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
        _facingZ = facingZ;
        _isOpen = isOpen;
        _tilePosition = new Vector2i(tilePosition);
        _coloredMeshesClosed = coloredMeshesClosed;
        _coloredMeshesOpen = coloredMeshesOpen;
        _closedCollider = closedCollider;
        _openCollider = openCollider;
        _floor = floor;
        _walls = walls;
        _soundEngine = soundEngine;
        _openSoundKey = openSoundKey;
        _closeSoundKey = closeSoundKey;
        
        _model = null;
        
        _doorTriggers = new HashSet<Vector2i>();
        _doorTriggers.add(_tilePosition);
        if(facingZ)
        {
            _doorTriggers.add(new Vector2i(_tilePosition.getX(), _tilePosition.getY() - 1));
            _doorTriggers.add(new Vector2i(_tilePosition.getX(), _tilePosition.getY() + 1));
        }
        else
        {
            _doorTriggers.add(new Vector2i(_tilePosition.getX() - 1, _tilePosition.getY()));
            _doorTriggers.add(new Vector2i(_tilePosition.getX() + 1, _tilePosition.getY()));
        }
        
        // Bei Initialisierung sind noch keine Räume zugewiesen
        _connectedRoomIDs = new Pair<Integer, Integer>(null, null);
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        Vector2i tilePos = MapHandler.worldPosToTilePos(cameraPosition);
        
        if(!_isOpen && _doorTriggers.contains(tilePos))
        {
            _isOpen = true;
            playSound(_openSoundKey);
        }
        else if(_isOpen && !_doorTriggers.contains(tilePos))
        {
            _isOpen = false;
            playSound(_closeSoundKey);
        }
    }
    
    /**
     * @see IGameObject#getCollider()
     */
    public ICollider getCollider()
    {
        if(_isOpen)
        {
            return _openCollider;    
        }
        else
        {
            return _closedCollider;       
        }
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        ArrayList<Pair<Mesh, TurtleColor>> coloredMeshList = (_isOpen)? _coloredMeshesOpen : _coloredMeshesClosed;
        
        if(_floor != null)
        {
            for(IGameObject obj : _floor)
            {
                obj.draw(renderer, camera);
            }
        }
        if(_walls != null)
        {
            for(IGameObject obj : _walls)
            {
                obj.draw(renderer, camera);
            }
        }
        
        if(coloredMeshList != null)
        {
            for(Pair<Mesh, TurtleColor> coloredMesh : coloredMeshList)
            {
                renderer.drawMesh(coloredMesh.getKey(), getModelMatrix(), coloredMesh.getValue(), camera);
            }
        }
    }
    
    private void playSound(String key)
    {
        if(_soundEngine != null)
        {
            if(key != null && _soundEngine.containsSource(key))
            {
                _soundEngine.playSound(key, 0.6, false);
            }
        }
    }
    
    /**
     * @see ILODGameObject#updateLOD()
     */
    public void updateLOD(Vector3 cameraPosition)
    {
        for(IGameObject obj : _floor)
        {
            if(obj instanceof ILODGameObject)
            {
                ((ILODGameObject)obj).updateLOD(cameraPosition);
            }
        }
        
        for(IGameObject obj : _floor)
        {
            if(obj instanceof ILODGameObject)
            {
                ((ILODGameObject)obj).updateLOD(cameraPosition);
            }
        }
    }
    
    /**
     * @see ILODGameObject#getLODLevel()
     */
    public int getLODLevel()
    {
        // Base Door Mesh is always on the most detailed LOD
        return 0;
    }
    
    /**
     * @see IDoorGameObject#getConnectedRoomIDs
     */
    public Pair<Integer, Integer> getConnectedRoomIDs()
    {
        return _connectedRoomIDs;
    }
    
    /**
     * Generiert die Modelmatrix aller Transformationen auf dem GameObject
     * @return Modelmatrix
     */
    private Matrix4 getModelMatrix()
    {
        if(_model == null)
        {
            Matrix4 translation = MatrixGenerator.generateTranslationMatrix(_position);
            Matrix4 rotationX = MatrixGenerator.generateAxialRotationMatrix(new Vector3(1, 0, 0), _rotation.getX());
            Matrix4 rotationY = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 1, 0), _rotation.getY());
            Matrix4 rotationZ = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 0, 1), _rotation.getZ());
            Matrix4 scale = MatrixGenerator.generateScaleMatrix(_scale);
                
            _model = translation.multiply(scale.multiply(rotationZ.multiply(rotationY.multiply(rotationX))));
        }

        return _model;
    }
    
    /**
     * @see IDoorGameObject#isOpen()
     */
    public boolean isOpen()
    {
        return _isOpen;
    }
    
    /**
     * @see IDoorGameObject#isFacingZ()
     */
    public boolean isFacingZ()
    {
        return _facingZ;
    }
    
    /**
     * @see IDoorGameObject#getTilePosition()
     */
    public Vector2i getTilePosition()
    {
        return new Vector2i(_tilePosition);
    }
    
    /**
     * @see IGameObject#getPosition()
     */
    public Vector3 getPosition()
    {
        return new Vector3(_position);
    }
    
    /**
     * @see IGameObject#getRotation()
     */
    public Vector3 getRotation()
    {
        return new Vector3(_rotation);
    }
    
    /**
     * @see IGameObject#getScale()
     */
    public Vector3 getScale()
    {
        return new Vector3(_scale);
    }
    
    /**
     * @see IGameObject#getColor()
     */
    public TurtleColor getColor()
    {
        return null;
    }
    
    /**
     * @see IDoorGameObject#setConnectedRoomIDs()
     */
    public void setConnectedRoomIDs(Integer first, Integer second)
    {
        _connectedRoomIDs = new Pair<Integer, Integer>(first, second);
    }
    
    /**
     * @see IDoorGameObject#setOpen()
     */
    public void setOpen(boolean isOpen)
    {
        _isOpen = isOpen;
    }
    
    /**
     * @see IGameObject#setColor()
     */
    public void setColor(TurtleColor color)
    {
        return;
    }
}
