import javafx.util.*;
import java.util.*;

/**
 * Ein dreidimensionales Objekt in der Spielwelt
 * 
 * @author Lasse Huber-Saffer
 * @version 02.12.2021
 */
public class StaticLODGameObject implements IGameObject, ILODGameObject
{
    private ArrayList<Pair<Double, Mesh>> _lodLevels;
    private int _lodLevel;
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private String _color;

    /**
     * Parameterloser Konstruktor
     */
    public StaticLODGameObject()
    {
        this(new ArrayList<Pair<Double, Mesh>>(), "gruen", new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh
     * @param lodLevels Liste von mehreren LOD-Meshes (3D-Modellen) und sortierten Distanzen (aufsteigend), ab denen diese genutzt werden sollen
     */
    public StaticLODGameObject(ArrayList<Pair<Double, Mesh>> lodLevels)
    {
        this(lodLevels, "gruen", new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh
     * @param lodLevels Liste von mehreren LOD-Meshes (3D-Modellen) und sortierten Distanzen (aufsteigend), ab denen diese genutzt werden sollen
     * @param color Farbe des Meshs
     */
    public StaticLODGameObject(ArrayList<Pair<Double, Mesh>> lodLevels, String color)
    {
        this(lodLevels, color, new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param lodLevels Liste von mehreren LOD-Meshes (3D-Modellen) und sortierten Distanzen (aufsteigend), ab denen diese genutzt werden sollen
     * @param color Farbe des Meshs
     * @param position Position
     */
    public StaticLODGameObject(ArrayList<Pair<Double, Mesh>> lodLevels, String color, Vector3 position)
    {
        this(lodLevels, color, position, new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param lodLevels Liste von mehreren LOD-Meshes (3D-Modellen) und sortierten Distanzen (aufsteigend), ab denen diese genutzt werden sollen
     * @param color Farbe des Meshs
     * @param position Position
     * @param rotation Rotation
     */
    public StaticLODGameObject(ArrayList<Pair<Double, Mesh>> lodLevels, String color, Vector3 position, Vector3 rotation)
    {
        this(lodLevels, color, position, rotation, new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param lodLevels Liste von mehreren LOD-Meshes (3D-Modellen) und sortierten Distanzen (aufsteigend), ab denen diese genutzt werden sollen
     * @param color Farbe des Meshs
     * @param position Position
     * @param rotation Rotation
     * @param scale Skalierung
     */
    public StaticLODGameObject(ArrayList<Pair<Double, Mesh>> lodLevels, String color, Vector3 position, Vector3 rotation, Vector3 scale)
    {
        _lodLevels = new ArrayList<Pair<Double, Mesh>>();
        
        for(Pair<Double, Mesh> pair : lodLevels)
        {
            _lodLevels.add(new Pair<Double, Mesh>(pair.getKey(), new Mesh(pair.getValue())));
        }
        
        _color = color;
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
    }
    
    /**
     * Zeichnet das GameObject mit dem gegebenen Renderer aus der Perspektive der gegebenen Kamera
     * @param renderer Renderer, der zum Zeichnen benutzt wird
     * @param camera Kamera, aus deren Perspektive gerendert wird
     */
    public void draw(Renderer renderer, Camera camera)
    {
        renderer.drawMesh(_lodLevels.get(getLODLevel()).getValue(), getModelMatrix(), _color, camera);
    }
    
    /**
     * Updated den Levels-of-Detail des GameObjects im Verhältnis zu einem Bezugspunkt
     * @param cameraPosition Position der Kamera, die als Bezugspunkt der LOD-Berechnung genutzt wird
     */
    public void updateLOD(Vector3 cameraPosition)
    {
        double dist = cameraPosition.subtract(_position).getLength();
        
        for(int i = 0; i < _lodLevels.size(); i++)
        {
            if(_lodLevels.get(i).getKey() > dist)
            {
                break;
            }
            else
            {
                _lodLevel = i;
            }
        }
    }
    
    /**
     * Gibt das Level-of-Detail, das unter dem aktuellen Bezugspunkt genutzt wird, zurück
     * @return Level-of-Detail, das vom GameObject genutzt wird. Je höher, desto weiter ist das Mesh vom Originalmesh entfernt.
     */
    public int getLODLevel()
    {
        return _lodLevel;
    }
    
    /**
     * Generiert die Modelmatrix aller Transformationen auf dem GameObject
     * @return Modelmatrix
     */
    private Matrix4 getModelMatrix()
    {
        Matrix4 translation = MatrixGenerator.generateTranslationMatrix(_position);
        Matrix4 rotationX = MatrixGenerator.generateAxialRotationMatrix(new Vector3(1, 0, 0), _rotation.getX());
        Matrix4 rotationY = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 1, 0), _rotation.getY());
        Matrix4 rotationZ = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 0, 1), _rotation.getZ());
        Matrix4 scale = MatrixGenerator.generateScaleMatrix(_scale);
            
        Matrix4 transform = translation.multiply(scale.multiply(rotationZ.multiply(rotationY.multiply(rotationX))));
        return transform;
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
        return _color;
    }
    
    /**
     * Setzt die Farbe
     * @param color Farbe des Objekts
     */
    public void setColor(String color)
    {
        _color = color;
    }
}
