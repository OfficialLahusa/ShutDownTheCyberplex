import java.util.*;

/**
 * Geschützturm, der sich zum Spieler ausrichtet und ihn beschießt
 * 
 * @author Lasse Huber-Saffer
 * @version 22.12.2021
 */
public class Turret implements ILivingEntity, ICollisionListener, IDynamicGameObject
{
    private boolean _isActive;
    private int _health;
    private int _maxHealth;
    private CircleCollider _collider;
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    
    private Mesh _activeMesh;
    private Mesh _inactiveMesh;
    private String _color;
    
    private boolean _recalculateModelMatrix;
    private Matrix4 _model;
    
    private static final double COLLIDER_RADIUS = 1.5;
    
    /**
     * Konstruktor für Turret mit Position und Meshes
     * @param position Position
     * @param active Ob das Turret aktiv sein soll
     * @param activeMesh Mesh des Turrets im aktiven Zustand
     * @param inactiveMesh Mesh des Turrets im inaktiven Zustand
     * @param color Farbe des Turrets
     * @param room umgebender Raum
     */
    public Turret(Vector3 position, boolean active, Mesh activeMesh, Mesh inactiveMesh, String color, Room room)
    {
        _isActive = active;
        _health = 100;
        _maxHealth = 100;
        _position = new Vector3(position);
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);
        
        _activeMesh = new Mesh(activeMesh);
        _inactiveMesh = new Mesh(inactiveMesh);
        _color = color;
        
        _recalculateModelMatrix = true;
        _model = null;
        
        _collider = new CircleCollider(new Vector2(_position.getX(), _position.getZ()), COLLIDER_RADIUS, PhysicsLayer.ENEMY);
        _collider.setListener(this);
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        // Aktivitätsstatus des Geschützes prüfen
        
        // Line-of-sight-Check mit Spieler machen
        
        // Geschütz zum Spieler ausrichten
        
        // Schießen, wenn Cooldown niedrig ist
        
        // Feuer-/Rauchpartikel aktivieren?
        
        //_recalculateModelMatrix = true;
        
        return;
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        if(_isActive)
        {
            renderer.drawMesh(_activeMesh, getModelMatrix(), _color, camera);
        }
        else
        {
            renderer.drawMesh(_inactiveMesh, getModelMatrix(), _color, camera);
        }
    }
    
    /**
     * @see ICollisionListener#onCollision()
     */
    public void onCollision(ICollider self, ICollider other)
    {
        return;
    }
    
    /**
     * @see ICollisionListener#onResolution()
     */
    public void onResolution(ICollider self, ICollider other)
    {
        return;
    }
    
    /**
     * @see IDynamicGameObject#move()
     */
    public void move(Vector3 delta)
    {
        _position = _position.add(delta);
        _collider.move(new Vector2(delta.getX(), delta.getZ()));
        _recalculateModelMatrix = true;
    }
    
    /**
     * @see IDynamicGameObject#rotate()
     */
    public void rotate(Vector3 rotation)
    {
        _rotation = _rotation.add(rotation);
        _recalculateModelMatrix = true;
    }
    
    /**
     * @see IDynamicGameObject#scale()
     */
    public void scale(Vector3 scale)
    {
        _scale = new Vector3(_scale.getX() * scale.getX(), _scale.getY() * scale.getY(), _scale.getZ() * scale.getZ());
        _recalculateModelMatrix = true;
    }
    
    /**
     * Generiert die Modelmatrix aller Transformationen auf dem Geschützturm
     * @return Modelmatrix
     */
    private Matrix4 getModelMatrix()
    {
        if(_recalculateModelMatrix || _model == null)
        {
            Matrix4 translation = MatrixGenerator.generateTranslationMatrix(_position);
            Matrix4 rotationX = MatrixGenerator.generateAxialRotationMatrix(new Vector3(1, 0, 0), _rotation.getX());
            Matrix4 rotationY = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 1, 0), _rotation.getY());
            Matrix4 rotationZ = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 0, 1), _rotation.getZ());
            Matrix4 scale = MatrixGenerator.generateScaleMatrix(_scale);
                
            Matrix4 transform = translation.multiply(scale.multiply(rotationZ.multiply(rotationY.multiply(rotationX))));
            _model = transform;
            
            _recalculateModelMatrix = false;
        }
        
        return _model;
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
        _recalculateModelMatrix = true;
    }
    
    /**
     * {@inheritDoc}, Rotation wird wie folgt interpretiert: (pitch, yaw, *)
     */
    public void setRotation(Vector3 rotation)
    {
        _rotation = new Vector3(rotation);
        _recalculateModelMatrix = true;
    }
    
    /**
     * @see IDynamicGameObject#setScale()
     */
    public void setScale(Vector3 scale)
    {
        _scale = new Vector3(scale);
        _recalculateModelMatrix = true;
    }
}
