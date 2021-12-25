import java.util.*;

/**
 * Drohne, der sich zum Spieler ausrichtet, ihn verfolgt und beschie�t
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
 */
public class Drone extends Enemy implements ILivingEntity, ICollisionListener, IDynamicGameObject
{
    // Zufallsgenerator
    private Random _random;
    
    // Sound
    private SoundEngine _soundEngine;
    
    // Functionality
    private boolean _isActive;
    private int _currentAmmo;
    private double _timeSinceLastShot;
    private int _health;
    private int _maxHealth;
    private boolean _reloadingVoiceLineTriggered;
    
    // Child-Objekte
    private SimpleDynamicGameObject _muzzleFlash;
    private SimpleDynamicGameObject _rotorLeft;
    private SimpleDynamicGameObject _rotorRight;
    
    // Physik
    private CircleCollider _collider;
    
    // Positionierung
    private Vector3 _scale;
    private Vector3 _muzzlePos1;
    private Vector3 _muzzlePos2;
    
    // Rendering
    private Mesh _activeMesh;
    private Mesh _inactiveMesh;
    private String _color;
    private Matrix4 _model;
    private Vector3 _lastShotPos1;
    private Vector3 _lastShotPos2;
    
    // Konstanten
    private static final double FLYING_HEIGHT = 5.0;
    private static final double COLLIDER_RADIUS = 0.6;
    private static final double MAX_SIGHT_RANGE = 500.0;
    private static final double MAX_GUN_RANGE = 20.0;
    private static final int MAGAZINE_CAPACITY = 6;
    private static final double FIRING_COOLDOWN = 0.5;
    private static final double RELOAD_TIME = 4.0;
    private static final double SHOT_VISIBILITY_TIME = 0.065;
    private static final int BULLET_DAMAGE = 3;
    private static final double MAXIMUM_INACCURACY_ANGLE = 6.0;
    private static final double FIRING_ANGLE_TOLERANCE = 4.0;
    private static final double TRACKING_INERTIA = 4.0;
    private static final double RELOAD_VOICELINE_DELAY = 0.25;
    private static final double VOICELINE_VOLUME = 0.8;
    
    /**
     * Konstruktor f�r Drohne mit Position und Meshes
     * @param position Position
     * @param active Ob das Drohne aktiv sein soll
     * @param room umgebender Raum
     * @param entityMeshes Register der EntityMeshes, aus dem die Meshes bezogen werden
     * @param soundEngine Sound Engine, aus der die Sounds der Entity bezogen werden
     */
    public Drone(Vector3 position, boolean active, Room room, HashMap<String, Mesh> entityMeshes, SoundEngine soundEngine)
    {
        _room = room;
        
        _random = new Random();
        
        _soundEngine = soundEngine;
        
        _isActive = active;
        _currentAmmo = MAGAZINE_CAPACITY;
        _timeSinceLastShot = 0.0;
        _health = 50;
        _maxHealth = 50;
        _reloadingVoiceLineTriggered = true;
        
        _muzzleFlash = new SimpleDynamicGameObject(entityMeshes.get("turret_muzzle_flash"), "rot");
        _rotorLeft = new SimpleDynamicGameObject(entityMeshes.get("drone_rotor"), "hellgrau");
        _rotorRight = new SimpleDynamicGameObject(entityMeshes.get("drone_rotor"), "hellgrau");
        
        _position = new Vector3(position.getX(), FLYING_HEIGHT, position.getZ());
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);
        _muzzlePos1 = new Vector3(0.987184, -0.782814, -0.1238);
        _muzzlePos2 = new Vector3(0.987184, -0.782814, 0.1238);
        
        _activeMesh = entityMeshes.get("drone_active");
        _inactiveMesh = entityMeshes.get("drone_active");
        _color = "hellgrau";
        
        _recalculateModelMatrix = true;
        _model = null;
        _lastShotPos1 = null;
        _lastShotPos2 = null;
        
        _collider = new CircleCollider(new Vector2(_position.getX(), _position.getZ()), COLLIDER_RADIUS, PhysicsLayer.ENEMY);
        _collider.setListener(this);
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        _timeSinceLastShot += deltaTime;
        
        // 1. Aktivit�tsstatus des Gesch�tzes pr�fen
        if(!_isActive) return;
        
        // 2. M�ndungsfeuer positionieren und skalieren
        _muzzleFlash.setRotation(_rotation);
        _muzzleFlash.setPosition(getCurrentMuzzlePosition());
        double scaleValue;
        
        // Zeitabh�ngig skalieren
        if(_timeSinceLastShot <= SHOT_VISIBILITY_TIME)
        {
            scaleValue = (SHOT_VISIBILITY_TIME - _timeSinceLastShot) / SHOT_VISIBILITY_TIME;
        }
        else
        {
            scaleValue = 1.0;
        }
        _muzzleFlash.setScale(new Vector3(scaleValue, scaleValue, scaleValue));
        
        // 3. Waffe Laden
        if(_currentAmmo == 0)
        {
            // Nachlade-Voiceline
            if(!_reloadingVoiceLineTriggered && _timeSinceLastShot >= RELOAD_VOICELINE_DELAY)
            {
                _soundEngine.playSoundFromGroup("turret_reload", VOICELINE_VOLUME, false);
                _reloadingVoiceLineTriggered = true;
            }
            
            // Magazin f�llen
            if(_timeSinceLastShot >= RELOAD_TIME)
            {
                _currentAmmo = MAGAZINE_CAPACITY;
            }
        }
        
        // Nachfolgendes nur ausf�hren, wenn Spieler sichtbar ist
        if(hasLineOfSight(MAX_SIGHT_RANGE))
        {
            // 4. Drohne zum Spieler ausrichten
            Player player = _room.getMap().getPlayer();
            lookAtFade(player.getPosition(), TRACKING_INERTIA);
            
            // 5. Schie�en, wenn Munition vorhanden, Spieler nah genug, Spieler genau genug anvisiert, und genug Zeit vergangen ist
            double inaccuracyAngle = getSightAngleTo(player.getPosition());
            double playerDist = getDistanceTo(player.getPosition());
            
            if(_currentAmmo > 0 && playerDist <= MAX_GUN_RANGE && inaccuracyAngle <= FIRING_ANGLE_TOLERANCE && _timeSinceLastShot > FIRING_COOLDOWN)
            {
                // Munition abziehen und Timer zur�cksetzen
                _currentAmmo--;
                _timeSinceLastShot = 0.0;
                
                // Reload-Voiceline zur�cksetzen
                _reloadingVoiceLineTriggered = false;
                
                // Raycast vorbereiten
                Vector2 source = new Vector2(_position.getX(), _position.getZ());
                Vector2 target = new Vector2(player.getPosition().getX(), player.getPosition().getZ());
                Vector2 direction = target.subtract(source).normalize();                
                EnumSet<PhysicsLayer> terminationFilter = EnumSet.of(PhysicsLayer.PLAYER, PhysicsLayer.SOLID);
                EnumSet<PhysicsLayer>exclusionFilter = EnumSet.of(PhysicsLayer.ENEMY);
                
                // Ungenauigkeit zum Schuss hinzuf�gen
                direction = direction.rotateAroundOrigin((_random.nextDouble()*2.0-1.0) * MAXIMUM_INACCURACY_ANGLE);
                
                // Raycast durchf�hren
                ArrayList<RaycastHit> raycast = Physics.raycast(source, direction, MAX_GUN_RANGE, _room.getMap(), terminationFilter, exclusionFilter);
                
                // Schussergebnis berechnen
                if(raycast.size() > 0)
                {
                    RaycastHit lastHit = raycast.get(raycast.size() - 1);
                    
                    // Schuss-Tracer setzen
                    _lastShotPos1 = new Vector3(getCurrentMuzzlePosition());
                    _lastShotPos2 = new Vector3(lastHit.position.getX(), 1.2, lastHit.position.getY());
                    
                    // Herausfinden, ob Spieler getroffen wurde
                    if(lastHit.collider.getLayer() == PhysicsLayer.PLAYER && lastHit.collider.getListener() != null)
                    {
                        if(lastHit.collider.getListener() instanceof ILivingEntity)
                        {
                            ILivingEntity victim = (ILivingEntity)lastHit.collider.getListener();
                            
                            // Spieler Schaden hinzuf�gen
                            victim.damage(BULLET_DAMAGE, "drone shot");
                        }
                    }
                }
                
                // Zuf�lligen Schusssound abspielen
                _soundEngine.playSoundFromGroup("heavy_shot", 0.65, false);
            }
        }
        
        //TODO:
        // 6. Feuer-/Rauchpartikel aktivieren
        return;
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        // Aktives und inaktives Drone unterschiedlich zeichnen
        if(_isActive)
        {
            renderer.drawMesh(_activeMesh, getModelMatrix(), _color, camera);
            
            // F�r eine bestimmte Zeit nach Abfeuern des Schusses den Trace und das M�ndungsfeuer rendern
            if(_timeSinceLastShot <= SHOT_VISIBILITY_TIME)
            {
                _muzzleFlash.draw(renderer, camera);
                
                // Schusstracer zeichnen
                if(_lastShotPos1 != null && _lastShotPos2 != null)
                {
                    renderer.drawLine3D(_lastShotPos1, _lastShotPos2, _muzzleFlash.getColor(), camera);
                }
            }
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
     * Gibt die transformierte Position der Laufm�ndung im World Space zur�ck
     * @return Position der Laufm�ndung im World Space
     */
    private Vector3 getCurrentMuzzlePosition()
    {
        return getModelMatrix().multiply(new Vector4(_muzzlePos1, 1.0)).getXYZ();
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
     * Generiert die Modelmatrix aller Transformationen auf dem Gesch�tzturm
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
    public void damage(int amount, String source)
    {
        if(_health == 0) return;
        
        _health = Math.max(0, Math.min(_health - amount, _maxHealth));
        
        if(_health == 0) 
        {
            _isActive = false;
            _soundEngine.playSoundFromGroup("turret_death", VOICELINE_VOLUME, false);
        }
    }
    
    /**
     * @see ILivingEntity#heal()
     */
    public void heal(int amount, String source)
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
