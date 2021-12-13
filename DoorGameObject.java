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
    
    private boolean _isOpen;
    
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private Matrix4 _model;
    
    /**
     * Konstruktor für Türen mit gegebenem Mesh, Position, Rotation und Skalierung
     * @param coloredMeshesClosed Liste von Mesh-Farb-Paaren, die im geschlossenen Zustand gerendert werden
     * @param coloredMeshesOpen Liste von Mesh-Farb-Paaren, die im offenen Zustand gerendert werden
     * @param isOpen Offenheit der Tür
     * @param position Position
     * @param rotation Rotation
     * @param scale Skalierung
     */
    public DoorGameObject(ArrayList<Pair<Mesh, String>> coloredMeshesClosed, ArrayList<Pair<Mesh, String>> coloredMeshesOpen, boolean isOpen, Vector3 position, Vector3 rotation, Vector3 scale)
    {
        _coloredMeshesClosed = coloredMeshesClosed;
        _coloredMeshesOpen = coloredMeshesOpen;
        _isOpen = isOpen;
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
        _model = null;
    }
    
    /**
     * Zeichnet das GameObject mit dem gegebenen Renderer aus der Perspektive der gegebenen Kamera
     * @param renderer Renderer, der zum Zeichnen benutzt wird
     * @param camera Kamera, aus deren Perspektive gerendert wird
     */
    public void draw(Renderer renderer, Camera camera)
    {
        ArrayList<Pair<Mesh, String>> coloredMeshList = (_isOpen)? _coloredMeshesOpen : _coloredMeshesClosed;
        
        for(Pair<Mesh, String> coloredMesh : coloredMeshList)
        {
            renderer.drawMesh(coloredMesh.getKey(), getModelMatrix(), coloredMesh.getValue(), camera);
        }
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
