import java.util.*;

/**
 * Geschützturm, der sich zum Spieler ausrichtet und ihn beschießt
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
 */
public class Turret implements ILivingEntity, ICollisionListener, IDynamicGameObject
{
    // Parent-Raum
    private Room _room;
    
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
    
    // Physik
    private CircleCollider _collider;
    
    // Positionierung
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private Vector3 _muzzlePos;
    
    // Rendering
    private Mesh _activeMesh;
    private Mesh _inactiveMesh;
    private String _color;
    private boolean _recalculateModelMatrix;
    private Matrix4 _model;
    private Vector3 _lastShotPos1;
    private Vector3 _lastShotPos2;
    
    // Konstanten
    private static final double COLLIDER_RADIUS = 1.5;
    private static final double MAX_RANGE = 500.0;
    private static final int MAGAZINE_CAPACITY = 15;
    private static final double FIRING_COOLDOWN = 0.065;
    private static final double RELOAD_TIME = 3.25;
    private static final double SHOT_VISIBILITY_TIME = 0.065;
    private static final int BULLET_DAMAGE = 2;
    private static final double MAXIMUM_INACCURACY_ANGLE = 6.0;
    private static final double FIRING_ANGLE_TOLERANCE = 2.0;
    private static final double TRACKING_SLOWNESS = 15.0;
    private static final double RELOAD_VOICELINE_DELAY = 0.25;
    private static final double VOICELINE_VOLUME = 0.8;
    
    /**
     * Konstruktor für Turret mit Position und Meshes
     * @param position Position
     * @param active Ob das Turret aktiv sein soll
     * @param room umgebender Raum
     * @param entityMeshes Register der EntityMeshes, aus dem die Meshes bezogen werden
     * @param soundEngine Sound Engine, aus der die Sounds der Entity bezogen werden
     */
    public Turret(Vector3 position, boolean active, Room room, HashMap<String, Mesh> entityMeshes, SoundEngine soundEngine)
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
        
        _position = new Vector3(position);
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);
        _muzzlePos = new Vector3(3.27203, 1.99736, 0.0);
        
        _activeMesh = entityMeshes.get("turret_active");
        _inactiveMesh = entityMeshes.get("turret_inactive");
        _color = "hellgrau";
        _muzzleFlash = new SimpleDynamicGameObject(entityMeshes.get("turret_muzzle_flash"), "rot");
        
        _recalculateModelMatrix = true;
        _model = null;
        _lastShotPos1 = null;
        _lastShotPos2 = null;
        
        _collider = new CircleCollider(new Vector2(_position.getX(), _position.getZ()), COLLIDER_RADIUS, PhysicsLayer.ENEMY);
        _collider.setListener(this);
        
        // Auf nächsten Fokuspunkt ausrichten, wenn es einen gibt
        aimAtFocusPoint();
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        _timeSinceLastShot += deltaTime;
        
        // 1. Aktivitätsstatus des Geschützes prüfen
        if(!_isActive) return;
        
        // 2. Mündungsfeuer positionieren und skalieren
        _muzzleFlash.setRotation(_rotation);
        _muzzleFlash.setPosition(getCurrentMuzzlePosition());
        double scaleValue;
        
        // Zeitabhängig skalieren
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
            
            // Magazin füllen
            if(_timeSinceLastShot >= RELOAD_TIME)
            {
                _currentAmmo = MAGAZINE_CAPACITY;
            }
        }
        
        // 4. Line-of-sight-Check mit Spieler machen
        Player player = _room.getMap().getPlayer();
        
        // Raycast vorbereiten
        Vector2 source = new Vector2(_position.getX(), _position.getZ());
        Vector2 target = new Vector2(player.getPosition().getX(), player.getPosition().getZ());
        Vector2 direction = target.subtract(source).normalize();
        EnumSet<PhysicsLayer> terminationFilter = EnumSet.of(PhysicsLayer.PLAYER, PhysicsLayer.SOLID);
        EnumSet<PhysicsLayer> exclusionFilter = EnumSet.of(PhysicsLayer.ENEMY);
        ArrayList<RaycastHit> raycast = Physics.raycast(source, direction, MAX_RANGE, _room.getMap(), terminationFilter, exclusionFilter);
        
        // Sichtbarkeit berechnen (Bdg.: der letzte Treffer des Raycasts muss der Spieler sein)
        boolean isPlayerVisible = (raycast.size() > 0 && raycast.get(raycast.size() - 1).collider.getLayer() == PhysicsLayer.PLAYER);
        
        // Nachfolgendes nur ausführen, wenn Spieler sichtbar ist
        if(isPlayerVisible)
        {
            // 5. Geschütz zum Spieler ausrichten
            double angleToPlayer = getAngleTo(player.getPosition());
            double prevAngle = _rotation.getY();
            
            // 360°-Flip bei 360° zu 0°-Transition und umgekehrt verhindern
            if(prevAngle > 270.0 && angleToPlayer < 90.0)       angleToPlayer += 360.0;
            else if(angleToPlayer > 270.0 && prevAngle < 90.0)  prevAngle += 360.0;
            
            // Neuen Winkel setzen (Langsamer Übergang)
            double newAngle = ((angleToPlayer + TRACKING_SLOWNESS*prevAngle) / (TRACKING_SLOWNESS + 1.0)) % 360.0;
            setAngle(newAngle);
            
            // 6. Schießen, wenn Munition vorhanden ist, Spieler genau genug anvisiert ist, und genug Zeit vergangen ist
            if(_currentAmmo > 0 && _timeSinceLastShot > FIRING_COOLDOWN)
            {
                // Anvisieren überprüfen
                Vector2 currentDirection = new Vector2(Math.cos(Math.toRadians(-_rotation.getY())), Math.sin(Math.toRadians(-_rotation.getY())));
                Vector2 idealDirection = new Vector2(player.getPosition().getX() - _position.getX(), player.getPosition().getZ() - _position.getZ());
                double inaccuracyAngle = idealDirection.getAngleBetween(currentDirection);
                
                // Nur schießen, wenn korrekt anvisiert wurde
                if(inaccuracyAngle <= FIRING_ANGLE_TOLERANCE)
                {
                    // Munition abziehen und Timer zurücksetzen
                    _currentAmmo--;
                    _timeSinceLastShot = 0.0;
                    
                    // Reload-Voiceline zurücksetzen
                    _reloadingVoiceLineTriggered = false;
                    
                    // Raycast vorbereiten
                    source = new Vector2(_position.getX(), _position.getZ());
                    target = new Vector2(player.getPosition().getX(), player.getPosition().getZ());
                    direction = target.subtract(source).normalize();                
                    terminationFilter = EnumSet.of(PhysicsLayer.PLAYER, PhysicsLayer.SOLID);
                    exclusionFilter = EnumSet.of(PhysicsLayer.ENEMY);
                    
                    // Ungenauigkeit zum Schuss hinzufügen
                    direction = direction.rotateAroundOrigin((_random.nextDouble()*2.0-1.0) * MAXIMUM_INACCURACY_ANGLE);
                    
                    // Raycast durchführen
                    raycast = Physics.raycast(source, direction, MAX_RANGE, _room.getMap(), terminationFilter, exclusionFilter);
                    
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
                                
                                // Spieler Schaden hinzufügen
                                victim.damage(BULLET_DAMAGE, "turret shot");
                            }
                        }
                    }
                    
                    // Zufälligen Schusssound abspielen
                    _soundEngine.playSoundFromGroup("heavy_shot", 0.65, false);
                }
            }
        }
        
        //TODO:
        // 7. Feuer-/Rauchpartikel aktivieren
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
            
            // Für eine bestimmte Zeit nach Abfeuern des Schusses den Trace und das Mündungsfeuer rendern
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
     * Richtet das Geschütz zum nächsten Fokuspunkt aus, falls einer vorhanden ist
     */
    private void aimAtFocusPoint()
    {
        ArrayList<Vector2i> focusPoints = (ArrayList<Vector2i>)_room.getFocusPoints().clone();
        
        // Nur fortfahren, wenn es mindestens einen Fokuspunkt gibt
        if(focusPoints != null && focusPoints.size() > 0)
        {
            // Nächsten Fokuspunkt berechnen
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
            double angleToPoint = getAngleTo(nearestFocusPoint);
            setAngle(angleToPoint);
        }
    }
    
        /**
     * Gibt den Winkel zu einem Zielpunkt zurück
     * @param target Zielpunkt
     * @return Ausrichtungswinkel, in dem der Geschützturm genau auf den Zielpunkt ausgerichtet ist
     */
    private double getAngleTo(Vector3 target)
    {
        Vector2 baseDirection = new Vector2(1.0, 0.0);
        Vector2 toTarget2D = new Vector2(target.getX(), target.getZ()).subtract(new Vector2(_position.getX(), _position.getZ()));
        
        // Kleinstmöglicher Winkel zwischen baseDirection und toTarget
        double resultingAngle = baseDirection.getAngleBetween(toTarget2D);
        
        // Winkel umkehren, wenn z-Koordinate des Zielpunkts größer ist, als die des Turrets
        if(target.getZ() > _position.getZ())
        {
            resultingAngle = 360.0 - resultingAngle;
        }
        
        return resultingAngle;
    }
    
    /**
     * Setzt den Winkel, in dem der Geschützturm ausgerichtet ist
     * @param angle neuer Ausrichtungswinkel
     */
    private void setAngle(double angle)
    {
        _rotation.setY(angle);
        _recalculateModelMatrix = true;
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
     * Gibt die transformierte Position der Laufmündung im World Space zurück
     * @return Position der Laufmündung im World Space
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
