
/**
 * Ein dreidimensionales Objekt in der Spielwelt
 * 
 * @author Lasse Huber-Saffer
 * @version 02.12.2021
 */
public class GameObject
{
    private Mesh _mesh;
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;

    /**
     * Parameterloser Konstruktor
     */
    public GameObject()
    {
        _mesh = new Mesh();
        _position = new Vector3();
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh
     * @param mesh Mesh (3D-Modell)
     */
    public GameObject(Mesh mesh)
    {
        _mesh = new Mesh(mesh);
        _position = new Vector3();
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param mesh Mesh (3D-Modell)
     * @param position Position
     * @param rotation Rotation
     * @param scale Skalierung
     */
    public GameObject(Mesh mesh, Vector3 position, Vector3 rotation, Vector3 scale)
    {
        _mesh = new Mesh(mesh);
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
    }
    
    /**
     * Generiert die Modelmatrix aller Transformationen auf dem GameObject
     * @return Modelmatrix
     */
    public Matrix4 getModelMatrix()
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
