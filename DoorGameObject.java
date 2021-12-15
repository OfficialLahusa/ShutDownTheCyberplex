import java.util.*;
import javafx.util.*;

/**
 * Beschreiben Sie hier die Klasse DoorGameObject.
 * 
 * @author Lasse Huber-Saffer
 * @version 13.12.2021
 */
public class DoorGameObject implements IGameObject
{
    private ArrayList<Pair<Mesh, String>> _coloredMeshesClosed;
    private ArrayList<Pair<Mesh, String>> _coloredMeshesOpen;
    
    private ArrayList<IGameObject> _floor;
    private ArrayList<IGameObject> _walls;
    
    private boolean _facingZ;
    private boolean _isOpen;
    private Vector2i _tilePosition;
    
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private Matrix4 _model;
    
    // Interpretation: null -> Kein Raum verbunden. >=0 -> Raum mit ID ist verbunden. <0 -> Illegaler Zustand
    private Pair<Integer, Integer> _connectedRoomIDs;
    
    /**
     * Konstruktor für Türen mit gegebenem Mesh, Position, Rotation und Skalierung
     * @param coloredMeshesClosed Liste von Mesh-Farb-Paaren, die im geschlossenen Zustand gerendert werden
     * @param coloredMeshesOpen Liste von Mesh-Farb-Paaren, die im offenen Zustand gerendert werden
     * @param facingZ Führt die z-Achse durch diese Tür hindurch?
     * @param isOpen Offenheit der Tür
     * @param tilePosition Position der Tür im Grid
     * @param position Position
     * @param rotation Rotation
     * @param scale Skalierung
     */
    public DoorGameObject(ArrayList<Pair<Mesh, String>> coloredMeshesClosed, ArrayList<Pair<Mesh, String>> coloredMeshesOpen, boolean facingZ, boolean isOpen, Vector2i tilePosition, Vector3 position, Vector3 rotation, Vector3 scale)
    {
        this(coloredMeshesClosed, coloredMeshesOpen, facingZ, isOpen, null, null, tilePosition, position, rotation, scale);
    }
    
    /**
     * Konstruktor für Türen mit gegebenem Mesh, Position, Rotation, Skalierung und Boden- sowie Wand-GameObjects
     * @param coloredMeshesClosed Liste von Mesh-Farb-Paaren, die im geschlossenen Zustand gerendert werden
     * @param coloredMeshesOpen Liste von Mesh-Farb-Paaren, die im offenen Zustand gerendert werden
     * @param facingZ Führt die z-Achse durch diese Tür hindurch?
     * @param isOpen Offenheit der Tür
     * @param floor(OPTIONAL) Liste an GameObjects, die als Fußboden gerendert werden sollen
     * @param walls (OPTIONAL) Liste an GameObjects, die als Mauern gerendert werden sollen
     * @param tilePosition Position der Tür im Grid
     * @param position Position
     * @param rotation Rotation
     * @param scale Skalierung
     */
    public DoorGameObject(ArrayList<Pair<Mesh, String>> coloredMeshesClosed, ArrayList<Pair<Mesh, String>> coloredMeshesOpen,
        boolean facingZ, boolean isOpen,
        ArrayList<IGameObject> floor, ArrayList<IGameObject> walls,
        Vector2i tilePosition, Vector3 position, Vector3 rotation, Vector3 scale)
    {
        _coloredMeshesClosed = coloredMeshesClosed;
        _coloredMeshesOpen = coloredMeshesOpen;
        _facingZ = facingZ;
        _isOpen = isOpen;
        _floor = floor;
        _walls = walls;
        _tilePosition = new Vector2i(tilePosition);
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
        _model = null;
        
        // Bei Initialisierung sind noch keine Räume zugewiesen
        _connectedRoomIDs = new Pair<Integer, Integer>(null, null);
    }
    
    /**
     * Zeichnet das GameObject mit dem gegebenen Renderer aus der Perspektive der gegebenen Kamera
     * @param renderer Renderer, der zum Zeichnen benutzt wird
     * @param camera Kamera, aus deren Perspektive gerendert wird
     */
    public void draw(Renderer renderer, Camera camera)
    {
        ArrayList<Pair<Mesh, String>> coloredMeshList = (_isOpen)? _coloredMeshesOpen : _coloredMeshesClosed;
        
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
        
        for(Pair<Mesh, String> coloredMesh : coloredMeshList)
        {
            renderer.drawMesh(coloredMesh.getKey(), getModelMatrix(), coloredMesh.getValue(), camera);
        }
    }
    
    /**
     * Setzt die IDs der verbundenen Räume der Tür
     * @param first erster verbundener Raum, null -> kein erster Raum (Reihenfolge irrelevant)
     * @param second zweiter verbundener Raum, null -> kein zweiter Raum (Reihenfolge irrelevant)
     */
    public void setAttachedRoomIDs(Integer first, Integer second)
    {
        _connectedRoomIDs = new Pair<Integer, Integer>(first, second);
    }
    
    /**
     * Gibt die IDs der verbundenen Räume der Tür zurück
     * @return Paar zweier Integer, die jeweils eine Raum-ID oder null, d.h. kein Raum, sind
     */
    public Pair<Integer, Integer> getAttachedRoomIDs()
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
     * Gibt zurück, ob die Tür offen ist
     * @return Offenheit der Tür
     */
    public boolean isOpen()
    {
        return _isOpen;
    }
    
    /**
     * Setzt die Offenheit der Tür
     * @param isOpen Offenheit der Tür
     */
    public void setOpen(boolean isOpen)
    {
        _isOpen = isOpen;
    }
    
    /**
     * Gibt zurück, ob die z-Achse durch die Öffnung dieser Tür hindurchgeht
     * @return Ob die z-Achse durch die Öffnung dieser Tür hindurchgeht
     */
    public boolean isFacingZ()
    {
        return _facingZ;
    }
    
    /**
     * Gibt die Position der Tür-Tile im Grid zurück
     * @return Position der Tür-Tile im Grid
     */
    public Vector2i getTilePosition()
    {
        return new Vector2i(_tilePosition);
    }
    
    /**
     * Gibt die Position zurück
     * @return Position
     */
    public Vector3 getPosition()
    {
        return new Vector3(_position);
    }
    
    /**
     * Gibt die Rotation zurück
     * @return Rotation
     */
    public Vector3 getRotation()
    {
        return new Vector3(_rotation);
    }
    
    /**
     * Gibt die Skalierung zurück
     * @return Skalierung
     */
    public Vector3 getScale()
    {
        return new Vector3(_scale);
    }
    
    /**
     * Gibt die Farbe zurück
     * @return Farbe des Objekts
     */
    public String getColor()
    {
        return null;
    }
    
    /**
     * Setzt die Farbe
     * @param color Farbe des Objekts
     */
    public void setColor(String color)
    {
        return;
    }
}
