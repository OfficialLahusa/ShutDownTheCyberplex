import java.util.*;

/**
 * Die Player Klasse beschreibt den Zustand des Spielers.
 * Der Spieler ist ein DynamicGameObject, das kein Rendering durchf�hrt und die Rotation repurposed.
 * 
 * @author Sven Schreiber, Lasse Huber-Saffer
 * @version 21.12.2021
 */
public class Player implements ILivingEntity, IDynamicGameObject, ICollisionListener
{
    private int _health;
    private int _maxHealth;
    private Camera _camera;
    private CircleCollider _collider;
    private Vector3 _position;
    /**
     * Wird NICHT f�r Rendering verwendet: x-Rotation =^= Camera Pitch, y-Rotation =^= Camera Yaw
     */ 
    private Vector3 _rotation;
    
    private static final double COLLIDER_RADIUS = 1.5;
    private static final double ASPECT_RATIO = 1.0;
    private static final double FOV = 90.0;
    private static final double CAMERA_HEIGHT = 2.0;
    
    /**
     * Konstruktor f�r Player mit Position
     * @param position Position
     */
    public Player(Vector3 position)
    {
        this(position, new Vector3());
    }
    
    /**
     * Konstruktor f�r Player mit Position und Rotation
     * @param position Position
     * @param rotation Rotationswerte f�r die Kamera (pitch, yaw, *)
     */
    public Player(Vector3 position, Vector3 rotation)
    {
        _health = 100;
        _maxHealth = 100;
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
        EnumSet<PhysicsLayer> ignoredLayers = EnumSet.of(PhysicsLayer.ITEM, PhysicsLayer.RAYCAST, PhysicsLayer.PLAYER);
        if(!ignoredLayers.contains(other.getLayer()))
        {
            if(other instanceof LineCollider || other instanceof CircleCollider)
            {
                ((CircleCollider)self).resolveCollision(other);
            }
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
     * Gibt die Kamera des Spieler als Referenz zur�ck
     * @return Kamera des Spielers
     */
    public Camera getCamera()
    {
        return _camera;
    }
    
    /**
     * Gibt den CircleCollider des Spielers als Referenz zur�ck
     * @return CircleCollider des Spielers
     */
    public CircleCollider getCollider()
    {
        return _collider;
    }
    
    /**
     * @see ILivingEntity#getHealth()
     */
    public int getHealth()
    {
        return _health;
    }
    
    /**
     * @see ILivingEntity#getMaxHealth()
     */
    public int getMaxHealth()
    {
        return _maxHealth;
    }
    
    /**
     * Gibt eine Kopie des Meshs (3D-Modell) zur�ck
     * @return Mesh (3D-Modell)
     */
    public Mesh getMesh()
    {
        return null;
    }
    
    /**
     * Gibt eine Referenz auf das Mesh (3D-Modell) zur�ck
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
     * @see ILivingEntity#setHealth()
     */
    public void setHealth(int amount)
    {
        if(amount < 0) throw new IllegalArgumentException("Health has to be >= 0");
        
        _health = amount;
        if(_health > _maxHealth) _health = _maxHealth;
    }
    
    /**
     * @see ILivingEntity#setMaxHealth()
     */
    public void setMaxHealth(int amount)
    {
        if(amount < 0) throw new IllegalArgumentException("Max health has to be >= 0");
        
        _maxHealth = amount;
        if(_health > _maxHealth) _health = _maxHealth;
    }
    
    /**
     * @see ILivingEntity#damage()
     */
    public void damage(int amount)
    {
        _health -= amount;
        if(_health < 0) _health = 0;
        if(_health > _maxHealth) _health = _maxHealth;
    }
    
    /**
     * @see ILivingEntity#heal()
     */
    public void heal(int amount)
    {
        _health += amount;
        if(_health < 0) _health = 0;
        if(_health > _maxHealth) _health = _maxHealth;
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
