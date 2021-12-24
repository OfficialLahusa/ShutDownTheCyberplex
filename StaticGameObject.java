
/**
 * Ein dreidimensionales Objekt in der Spielwelt
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
 */
public class StaticGameObject implements IGameObject
{
    private Mesh _mesh;
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private String _color;
    private Matrix4 _model;

    /**
     * Parameterloser Konstruktor
     */
    public StaticGameObject()
    {
        this(new Mesh(), "gruen", new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh
     * @param mesh Mesh (3D-Modell)
     */
    public StaticGameObject(Mesh mesh)
    {
        this(mesh, "gruen", new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh
     * @param mesh Mesh (3D-Modell)
     * @param color Farbe des Meshs
     */
    public StaticGameObject(Mesh mesh, String color)
    {
        this(mesh, color, new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param mesh Mesh (3D-Modell)
     * @param color Farbe des Meshs
     * @param position Position
     */
    public StaticGameObject(Mesh mesh, String color, Vector3 position)
    {
        this(mesh, color, position, new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param mesh Mesh (3D-Modell)
     * @param color Farbe des Meshs
     * @param position Position
     * @param rotation Rotation
     */
    public StaticGameObject(Mesh mesh, String color, Vector3 position, Vector3 rotation)
    {
        this(mesh, color, position, rotation, new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param mesh Mesh (3D-Modell)
     * @param color Farbe des Meshs
     * @param position Position
     * @param rotation Rotation
     * @param scale Skalierung
     */
    public StaticGameObject(Mesh mesh, String color, Vector3 position, Vector3 rotation, Vector3 scale)
    {
        _mesh = new Mesh(mesh);
        _color = color;
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
        _model = null;
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        return;
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        renderer.drawMesh(_mesh, getModelMatrix(), _color, camera);
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
     * @see IGameObject#getCollider()
     */
    public ICollider getCollider()
    {
        return null;
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
    public String getColor()
    {
        return _color;
    }
    
    /**
     * @see IGameObject#setColor()
     */
    public void setColor(String color)
    {
        _color = color;
    }
}
