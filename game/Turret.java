package game;

import maths.*;
import core.*;
import physics.*;
import java.util.*;

/**
 * Gesch�tzturm, der sich zum Spieler ausrichtet und ihn beschie�t
 * 
 * @author Lasse Huber-Saffer, Sven Schreiber
 * @version 03.01.2022
 */
public class Turret extends Enemy implements ILivingEntity, ICollisionListener, IDynamicGameObject
{
    // Zufallsgenerator
    private Random _random;
    
    // Sound
    private SoundEngine _soundEngine;
    
    // Partikel
    private HashMap<String, Mesh> _particleMeshes;
    
    // Functionality
    private boolean _isActive;
    private int _currentAmmo;
    private double _timeSinceLastShot;
    private int _health;
    private int _maxHealth;
    private boolean _reloadingVoiceLineTriggered;
    
    // Child-Objekte
    private SimpleDynamicGameObject _muzzleFlash;
    
    // Physik
    private CircleCollider _collider;
    
    // Positionierung
    private Vector3 _scale;
    private Vector3 _muzzlePos;
    
    // Rendering
    private Mesh _activeMesh;
    private Mesh _inactiveMesh;
    private TurtleColor _color;
    private Matrix4 _model;
    private Vector3 _lastShotPos1;
    private Vector3 _lastShotPos2;
    
    // Konstanten
    private static final double COLLIDER_RADIUS = 1.5;
    private static final double MAX_SIGHT_RANGE = 500.0;
    private static final double MAX_GUN_RANGE = 100.0;
    private static final int MAGAZINE_CAPACITY = 15;
    private static final double FIRING_COOLDOWN = 0.065;
    private static final double RELOAD_TIME = 3.25;
    private static final double SHOT_VISIBILITY_TIME = 0.065;
    private static final int BULLET_DAMAGE = 2;
    private static final double MAXIMUM_INACCURACY_ANGLE = 6.0;
    private static final double FIRING_ANGLE_TOLERANCE = 2.0;
    private static final double TRACKING_INERTIA = 15.0;
    private static final double RELOAD_VOICELINE_DELAY = 0.25;
    private static final double VOICELINE_VOLUME = 0.6;
    
    /**
     * Konstruktor f�r Turret mit Position und Meshes
     * @param position Position
     * @param active Ob das Turret aktiv sein soll
     * @param room umgebender Raum
     * @param entityMeshes Register der EntityMeshes, aus dem die Meshes bezogen werden
     * @param soundEngine Sound Engine, aus der die Sounds der Entity bezogen werden
     */
    public Turret(Vector3 position, boolean active, Room room, HashMap<String, Mesh> entityMeshes, HashMap<String, Mesh> particleMeshes, SoundEngine soundEngine)
    {
        _room = room;
        
        _random = new Random();
        
        _soundEngine = soundEngine;
        
        _particleMeshes = particleMeshes;
        
        _isActive = active;
        _currentAmmo = MAGAZINE_CAPACITY;
        _timeSinceLastShot = 0.0;
        _health = 50;
        _maxHealth = 50;
        _reloadingVoiceLineTriggered = true;
        
        _position = new Vector3(position);
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);
        _muzzlePos = new Vector3(3.27203, 1.99736, 0.0);
        
        _activeMesh = entityMeshes.get("turret_active");
        _inactiveMesh = entityMeshes.get("turret_inactive");
        _color = TurtleColor.LIGHT_GRAY;
        _muzzleFlash = new SimpleDynamicGameObject(entityMeshes.get("turret_muzzle_flash"), TurtleColor.RED, new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        
        _recalculateModelMatrix = true;
        _model = null;
        _lastShotPos1 = null;
        _lastShotPos2 = null;
        
        _collider = new CircleCollider(new Vector2(_position.getX(), _position.getZ()), COLLIDER_RADIUS, PhysicsLayer.ENEMY);
        _collider.setListener(this);
        
        // Auf n�chsten Fokuspunkt ausrichten, wenn es einen gibt
        aimAtFocusPoint();
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
                            victim.damage(BULLET_DAMAGE, "turret shot");
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
        // Aktives und inaktives Turret unterschiedlich zeichnen
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
     * Richtet das Gesch�tz zum n�chsten Fokuspunkt aus, falls einer vorhanden ist
     */
    private void aimAtFocusPoint()
    {
        ArrayList<Vector2i> focusPoints = (ArrayList<Vector2i>)_room.getFocusPoints().clone();
        
        // Nur fortfahren, wenn es mindestens einen Fokuspunkt gibt
        if(focusPoints != null && focusPoints.size() > 0)
        {
            // N�chsten Fokuspunkt berechnen
            // Fokuspunkte nach Distanz aufsteigend sortieren
            Collections.sort(focusPoints,new Comparator<Vector2i>(){
                @Override
                public int compare(Vector2i firstTile, Vector2i secondTile) {
                    Vector3 firstFocusPoint = MapHandler.tilePosToWorldPos(firstTile);
                    Double firstDistance = firstFocusPoint.subtract(_position).getLength();
                    Vector3 secondFocusPoint = MapHandler.tilePosToWorldPos(secondTile);
                    Double secondDistance = secondFocusPoint.subtract(_position).getLength();
                    return firstDistance.compareTo(secondDistance);
                }
            });
            
            // Auf Punkt fokussieren
            Vector3 nearestFocusPoint = MapHandler.tilePosToWorldPos(focusPoints.get(0));
            if(nearestFocusPoint != null)
            {
                double angleToPoint = getAngleTo(nearestFocusPoint);
                setAngle(angleToPoint);
            }
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
        return getModelMatrix().multiply(new Vector4(_muzzlePos, 1.0)).getXYZ();
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
        _room.addParticleSystem(new DroneHitParticleSystem(_position.add(new Vector3(0.0f, 2.0f, 0.0f)), _room, _particleMeshes));
        
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
