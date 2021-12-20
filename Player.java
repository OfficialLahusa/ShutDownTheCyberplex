/**
 * Die Player Klasse beschreibt den Zustand des Spielers.
 * Der Spieler ist ein DynamicGameObject, das kein Rendering durchführt und die Rotation repurposed.
 * 
 * @author Sven Schreiber, Lasse Huber-Saffer
 * @version 13.12.2021
 */
public class Player implements IDynamicGameObject, ICollisionListener
{
    private int _health;
    private Camera _camera;
    private CircleCollider _collider;
    private Vector3 _position;
    /**
     * Wird NICHT für Rendering verwendet: x-Rotation =^= Camera Pitch, y-Rotation =^= Camera Yaw
     */ 
    private Vector3 _rotation;
    
    private static final double COLLIDER_RADIUS = 1.5;
    private static final double ASPECT_RATIO = 1.0;
    private static final double FOV = 90.0;
    private static final double CAMERA_HEIGHT = 2.0;
    
    /**
     * Konstruktor für Player mit Position
     * @param position Position
     */
    public Player(Vector3 position)
    {
        this(position, new Vector3());
    }
    
    /**
     * Konstruktor für Player mit Position und Rotation
     * @param position Position
     * @param rotation Rotationswerte für die Kamera (pitch, yaw, *)
     */
    public Player(Vector3 position, Vector3 rotation)
    {
        _health = 100;
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        
        _camera = new Camera(new Vector3(_position.getX(), CAMERA_HEIGHT, _position.getZ()), ASPECT_RATIO, FOV);
        _camera.setPitch(_rotation.getX());
        _camera.setYaw(_rotation.getY());
        
        _collider = new CircleCollider(new Vector2(_position.getX(), _position.getZ()), COLLIDER_RADIUS, PhysicsLayer.PLAYER);
        _collider.setListener(this);
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
        return;
    }
    
    /**
     * @see ICollisionListener#onCollision()
     */
    public void onCollision(ICollider self, ICollider other)
    {
        if(other instanceof LineCollider || other instanceof CircleCollider)
        {
            ((CircleCollider)self).resolveCollision(other);
        }
    }
    
    /**
     * @see ICollisionListener#onResolution()
     */
    public void onResolution(ICollider self, ICollider other)
    {
        // Positionen von Spieler und Kamera dem Collider angleichen
        Vector2 colliderPos = _collider.getPosition();
        _position = new Vector3(colliderPos.getX(), _position.getY(), colliderPos.getY());
        _camera.setPosition(new Vector3(_position.getX(), CAMERA_HEIGHT, _position.getZ()));
    }
    
    /**
     * @see IDynamicGameObject#move()
     */
    public void move(Vector3 delta)
    {
        _position = _position.add(delta);
        _camera.setPosition(new Vector3(_position.getX(), CAMERA_HEIGHT, _position.getZ()));
        _collider.move(new Vector2(delta.getX(), delta.getZ()));
    }
    
    /**
     * {@inheritDoc}, Rotation wird wie folgt interpretiert: (pitch, yaw, *)
     */
    public void rotate(Vector3 rotation)
    {
        _rotation = _rotation.add(rotation);
        _camera.setPitch(_rotation.getX());
        _camera.setYaw(_rotation.getY());
    }
    
    /**
     * @see IDynamicGameObject#scale()
     */
    public void scale(Vector3 scale)
    {
        return;
    }
    
    /**
     * Generiert die Modelmatrix aller Transformationen auf dem SimpleDynamicGameObject
     * @return Modelmatrix
     */
    private Matrix4 getModelMatrix()
    {
        return null;
    }
    
    /**
     * Gibt die Kamera des Spieler als Referenz zurück
     * @return Kamera des Spielers
     */
    public Camera getCamera()
    {
        return _camera;
    }
    
    /**
     * Gibt den CircleCollider des Spielers als Referenz zurück
     * @return CircleCollider des Spielers
     */
    public CircleCollider getCollider()
    {
        return _collider;
    }
    
    /**
     * Gibt die Lebenspunkte des Spielers zurück
     * @return die Lebenspunkte
     */
    public int getHealth()
    {
        return _health;
    }
    
    /**
     * Gibt eine Kopie des Meshs (3D-Modell) zurück
     * @return Mesh (3D-Modell)
     */
    public Mesh getMesh()
    {
        return null;
    }
    
    /**
     * Gibt eine Referenz auf das Mesh (3D-Modell) zurück
     * @return Mesh (3D-Modell)
     */
    public Mesh getMeshRef()
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
        return new Vector3(1.0, 1.0, 1.0);
    }
    
    /**
     * @see IGameObject#getColor()
     */
    public String getColor()
    {
        return null;
    }
    
    /**
     * Setzt die Leben des Spielers
     * @param amount Menge an Lebenspunkten
     */
    public void setHealth(int amount)
    {
        _health = amount;
    }
    
    /**
     * Fügt dem Spieler Schaden zu
     * @param amount Menge an Lebenspunkten
     */
    public void damage(int amount)
    {
        _health -= amount;
        if(_health < 0) _health = 0;
    }
    
    /**
     * Heilt den Spieler
     * @param amount Menge an Lebenspunkten
     */
    public void heal(int amount)
    {
        _health += amount;
        if(_health > 100) _health = 100;
    }
    
    /**
     * @see IGameObject#setColor()
     */
    public void setColor(String color)
    {
        return;
    }
    
    /**
     * Setzt das Mesh (3D-Modell)
     * @param mesh Mesh (3D-Modell)
     */
    public void setMesh(Mesh mesh)
    {
        return;
    }
    
    /**
     * @see IDynamicGameObject#setPosition()
     */
    public void setPosition(Vector3 position)
    {
        _position = new Vector3(position);
        _collider.setPosition(new Vector2(position.getX(), position.getZ()));
        _camera.setPosition(new Vector3(_position.getX(), CAMERA_HEIGHT, _position.getZ()));
    }
    
    /**
     * {@inheritDoc}, Rotation wird wie folgt interpretiert: (pitch, yaw, *)
     */
    public void setRotation(Vector3 rotation)
    {
        _rotation = new Vector3(rotation);
        _camera.setPitch(_rotation.getX());
        _camera.setYaw(_rotation.getY());
    }
    
    /**
     * @see IDynamicGameObject#setScale()
     */
    public void setScale(Vector3 scale)
    {
        return;
    }
}
