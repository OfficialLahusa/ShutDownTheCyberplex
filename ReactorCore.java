import java.util.*;

/**
 * Drehender Reaktorkern, der de-facto Endgegner des Spiels
 * 
 * @author Lasse Huber-Saffer
 * @version 03.01.2022
 */
public class ReactorCore extends Enemy implements ILivingEntity, ICollisionListener, IDynamicGameObject
{
    // Zufallsgenerator
    private Random _random;
    
    // Sound
    private SoundEngine _soundEngine;
    
    // Functionality
    private boolean _isActive;
    private int _health;
    private int _maxHealth;
    private double _coreBounceHeightSamplingPoint;
    private double _timeSinceDeath;
    
    // Child-Objekte
    private SimpleDynamicGameObject _core;
    private SimpleDynamicGameObject _pillar;
    private ReactorPillarParticleSystem _pillarParticleSystem;
    
    // Physik
    private CircleCollider _collider;
    
    // Partikel
    private HashMap<String, Mesh> _particleMeshes;
    
    // Positionierung
    private Vector3 _scale;
    
    // Rendering
    private TurtleColor _color;
    private Matrix4 _model;
    
    // Konstanten
    private static final int MAX_HP = 400;
    private static final double COLLIDER_RADIUS = 0.8;
    private static final double PILLAR_EMITTER_HEIGHT = 1.6;
    private static final double CORE_HOVER_HEIGHT = 3.5;
    private static final double MAX_HP_CORE_BOUNCING_SPEED = 180.0;
    private static final double MIN_HP_CORE_BOUNCING_SPEED = 540.0;
    private static final double CORE_BOUNCING_AMPLITUDE = 0.4;
    private static final double VOICELINE_VOLUME = 0.35;
    private static final double MAX_HP_CORE_ROTATION_SPEED = 90.0;
    private static final double MIN_HP_CORE_ROTATION_SPEED = 720.0;
    private static final double DEATH_GAME_COMPLETION_DELAY = 5.0;
    
    /**
     * Konstruktor für ReactorCore mit Position und Meshes
     * @param position Position
     * @param room umgebender Raum
     * @param entityMeshes Register, aus dem die Entity-Meshes bezogen werden
     * @param particleMeshes Register, aus dem die Partikel-Meshes bezogen werden
     * @param soundEngine Sound Engine, aus der die Sounds der Entity bezogen werden
     */
    public ReactorCore(Vector3 position, Room room, HashMap<String, Mesh> entityMeshes, HashMap<String, Mesh> particleMeshes, SoundEngine soundEngine)
    {
        _room = room;
        
        _random = new Random();
        
        _soundEngine = soundEngine;
        
        _isActive = true;
        _health = MAX_HP;
        _maxHealth = MAX_HP;
        _coreBounceHeightSamplingPoint = 0.0;
        _timeSinceDeath = 0.0;
        
        _particleMeshes = particleMeshes;
        
        _position = new Vector3(position);
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);

        _core = new SimpleDynamicGameObject(entityMeshes.get("reactor_core"), TurtleColor.CYAN, new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _pillar = new SimpleDynamicGameObject(entityMeshes.get("reactor_pillar"), TurtleColor.LIGHT_GRAY, new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _pillarParticleSystem = new ReactorPillarParticleSystem(new Vector3(_position.getX(), PILLAR_EMITTER_HEIGHT, _position.getZ()), _room, _particleMeshes);
        
        _color = TurtleColor.LIGHT_GRAY;
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
        if(!_isActive)
        {
            _timeSinceDeath += deltaTime;
            
            if (_timeSinceDeath % 0.2 < 0.1)
            {
                // Partikelsystem erzeugen
                _room.addParticleSystem(new ReactorExplosionParticleSystem(_position.add(new Vector3(0.0, 2.5, 0.0)), _room, _particleMeshes));
            }
            
            if(_timeSinceDeath > DEATH_GAME_COMPLETION_DELAY)
            {
                _room.getMap().isCompleted = true;
            }
        }
        
        // Partikelsystem positionieren und updaten
        _pillarParticleSystem.setPosition(new Vector3(_position.getX(), PILLAR_EMITTER_HEIGHT, _position.getZ()));
        _pillarParticleSystem.update(deltaTime, runTime, cameraPosition);
        
        // Kern auf Basis der Leben rotieren
        double speedScaling = (double)_health / (double)_maxHealth;
        _core.rotate(new Vector3(0.0, (speedScaling * (MAX_HP_CORE_ROTATION_SPEED - MIN_HP_CORE_ROTATION_SPEED) + MIN_HP_CORE_ROTATION_SPEED) * deltaTime, 0.0));
        
        // Kern richtig positionieren und animieren
        _coreBounceHeightSamplingPoint += (speedScaling * (MAX_HP_CORE_BOUNCING_SPEED - MIN_HP_CORE_BOUNCING_SPEED) + MIN_HP_CORE_BOUNCING_SPEED) * deltaTime;
        _core.setPosition(new Vector3(_position.getX(), CORE_HOVER_HEIGHT + CORE_BOUNCING_AMPLITUDE * Math.sin(Math.toRadians(_coreBounceHeightSamplingPoint)), _position.getZ()));
        
        // Säule richtig positionieren
        _pillar.setPosition(_position);
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        if(_isActive) _core.draw(renderer, camera);
        _pillar.draw(renderer, camera);
        _pillarParticleSystem.draw(renderer, camera);
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
     * Generiert die Modelmatrix aller Transformationen auf dem Geschï¿½tzturm
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
     * @see ILivingEntity#isAlive()
     */
    public boolean isAlive()
    {
        return _health > 0;
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
    public TurtleColor getColor()
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
        
        // Partikelsystem erzeugen
        _room.addParticleSystem(new ReactorHitParticleSystem(_position.add(new Vector3(0.0, 2.5, 0.0)), _room, _particleMeshes));
        
        _health = Math.max(0, Math.min(_health - amount, _maxHealth));
        
        if(_health > 0.66 * _maxHealth)
        {
            _soundEngine.playSoundFromGroup("reactor_hurt_light", VOICELINE_VOLUME, false);
        }
        else if(_health > 0.33 * _maxHealth)
        {
            _soundEngine.playSoundFromGroup("reactor_hurt_medium", VOICELINE_VOLUME, false);
        }
        else if(_health > 0)
        {
            _soundEngine.playSoundFromGroup("reactor_hurt_heavy", VOICELINE_VOLUME, false);
        }
        else if(_health == 0) 
        {
            _isActive = false;
            _soundEngine.playSound("reactor_explode", VOICELINE_VOLUME, false);
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
    public void setColor(TurtleColor color)
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
     * {@inheritDoc}, Rotation wird wie folgt interpretiert: (*, yaw, *)
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
