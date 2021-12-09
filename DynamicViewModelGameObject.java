
/**
 * Ein dreidimensionales dynamisch bewegbares Objekt in der Spielwelt, das als ViewModel für die Kamera gerendert wird, also nicht mit der ViewMatrix verschoben wird
 * 
 * @author Lasse Huber-Saffer
 * @version 02.12.2021
 */
public class DynamicViewModelGameObject implements IDynamicGameObject
{
    private Mesh _mesh;
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private String _color;

    /**
     * Parameterloser Konstruktor
     */
    public DynamicViewModelGameObject()
    {
        this(new Mesh(), "gruen", new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für DynamicViewModelGameObject mit gegebenem Mesh
     * @param mesh Mesh (3D-Modell)
     */
    public DynamicViewModelGameObject(Mesh mesh)
    {
        this(mesh, "gruen", new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für DynamicViewModelGameObject mit gegebenem Mesh
     * @param mesh Mesh (3D-Modell)
     * @param color Farbe des Meshs
     */
    public DynamicViewModelGameObject(Mesh mesh, String color)
    {
        this(mesh, color, new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für DynamicViewModelGameObject mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param mesh Mesh (3D-Modell)
     * @param color Farbe des Meshs
     * @param position Position
     */
    public DynamicViewModelGameObject(Mesh mesh, String color, Vector3 position)
    {
        this(mesh, color, position, new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für DynamicViewModelGameObject mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param mesh Mesh (3D-Modell)
     * @param color Farbe des Meshs
     * @param position Position
     * @param rotation Rotation
     */
    public DynamicViewModelGameObject(Mesh mesh, String color, Vector3 position, Vector3 rotation)
    {
        this(mesh, color, position, rotation, new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für DynamicViewModelGameObject mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param mesh Mesh (3D-Modell)
     * @param color Farbe des Meshs
     * @param position Position
     * @param rotation Rotation
     * @param scale Skalierung
     */
    public DynamicViewModelGameObject(Mesh mesh, String color, Vector3 position, Vector3 rotation, Vector3 scale)
    {
        _mesh = new Mesh(mesh);
        _color = color;
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
    }
    
    /**
     * Zeichnet das DynamicViewModelGameObject mit dem gegebenen Renderer aus der Perspektive der gegebenen Kamera
     * @param renderer Renderer, der zum Zeichnen benutzt wird
     * @param camera Kamera, aus deren Perspektive gerendert wird
     */
    public void draw(Renderer renderer, Camera camera)
    {
        renderer.drawMesh(_mesh, getModelMatrix(), _color, camera, true);
    }
    
    /**
     * Bewegt das Objekt um einen Vektor
     * @param delta Bewegungsvektor
     */
    public void move(Vector3 delta)
    {
        _position = _position.add(delta);   
    }
    
    /**
     * Rotiert das Objekt
     * @param rotation Vektor aus drei Rotationswinkeln in Grad
     */
    public void rotate(Vector3 rotation)
    {
        _rotation = _rotation.add(rotation);
    }
    
    /**
     * Skaliert das Object relativ zu seiner vorherigen Größe
     * @param scale Vektor der Skalierung
     */
    public void scale(Vector3 scale)
    {
        _scale = new Vector3(_scale.getX() * scale.getX(), _scale.getY() * scale.getY(), _scale.getZ() * scale.getZ());
    }
    
    /**
     * Generiert die Modelmatrix aller Transformationen auf dem DynamicViewModelGameObject
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
     * Gibt eine Kopie des Meshs (3D-Modell) zurück
     * @return Mesh (3D-Modell)
     */
    public Mesh getMesh()
    {
        return new Mesh(_mesh);
    }
    
    /**
     * Gibt eine Referenz auf das Mesh (3D-Modell) zurück
     * @return Mesh (3D-Modell)
     */
    public Mesh getMeshRef()
    {
        return _mesh;
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
    
    /**
     * Setzt das Mesh (3D-Modell)
     * @param mesh Mesh (3D-Modell)
     */
    public void setMesh(Mesh mesh)
    {
        _mesh = new Mesh(mesh);
    }
    
    /**
     * Setzt die Position
     * @param position Position
     */
    public void setPosition(Vector3 position)
    {
        _position = new Vector3(position);
    }
    
    /**
     * Setzt die Rotation
     * @param rotation Rotation
     */
    public void setRotation(Vector3 rotation)
    {
        _rotation = new Vector3(rotation);
    }
    
    /**
     * Setzt die Skalierung
     * @param scale Skalierung
     */
    public void setScale(Vector3 scale)
    {
        _scale = new Vector3(scale);
    }
}
