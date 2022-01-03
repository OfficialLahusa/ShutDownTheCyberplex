package game;

import maths.*;
import core.*;
import physics.*;
import util.*;
import java.util.*;
import javafx.scene.media.*;

/**
 * Drohne, der sich zum Spieler ausrichtet, ihn verfolgt und beschießt
 * 
 * @author Lasse Huber-Saffer, Sven Schreiber
 * @version 03.01.2022
 */
public class Drone extends Enemy implements ILivingEntity, ICollisionListener, IDynamicGameObject
{
    // Zufallsgenerator
    private Random _random;
    
    // Functionality
    private DroneAIState _state;
    private int _nextPatrolPoint;
    private Vector2i _previousPathTarget;
    private LinkedList<PathNode> _path;
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
    
    // Sound
    private SoundEngine _soundEngine;
    private Sound _hoverSound;
    
    // Partikel
    private HashMap<String, Mesh> _particleMeshes;
    
    // Positionierung
    private Vector3 _scale;
    private Vector3 _muzzlePos1;
    private Vector3 _muzzlePos2;
    
    // Rendering
    private Mesh _activeMesh;
    private Mesh _inactiveMesh;
    private TurtleColor _color;
    private Matrix4 _model;
    private Vector3 _lastShotPos1;
    private Vector3 _lastShotPos2;
    
    // Konstanten
    // Bewegung
    private static final double FLYING_HEIGHT = 5.0;
    private static final double MIN_FLYING_HEIGHT = 0.69;
    private static final double FLYING_SPEED = 12.0;
    private static final double FALLING_SPEED = 6.0;
    
    // Physik
    private static final double COLLIDER_RADIUS = 0.6;
    
    // Spielerverfolgung
    private static final double TRACKING_INERTIA = 4.0;
    private static final double MAX_SIGHT_RANGE = 500.0;
    private static final double MAX_GUN_RANGE = 20.0;
    private static final double IDEAL_PLAYER_DISTANCE = 14.0;
    
    // Schießen
    private static final int MAGAZINE_CAPACITY = 6;
    private static final double FIRING_COOLDOWN = 0.5;
    private static final double RELOAD_TIME = 4.0;
    private static final double SHOT_VISIBILITY_TIME = 0.065;
    private static final int BULLET_DAMAGE = 3;
    private static final double MAXIMUM_INACCURACY_ANGLE = 6.0;
    private static final double FIRING_ANGLE_TOLERANCE = 4.0;
    
    // Spielerentdeckung
    private static final double PLAYER_DISCOVERY_ANGLE = 35.0;
    private static final double PLAYER_DISCOVERY_DISTANCE = 4.0;
    
    // Audio
    private static final double RELOAD_VOICELINE_DELAY = 0.25;
    private static final double VOICELINE_VOLUME = 0.6;
    private static final double HOVER_SOUND_FADE_DIST = 40.0;
    
    /**
     * Konstruktor für Drohne mit Position und Meshes
     * @param position Position
     * @param active Ob das Drohne aktiv sein soll
     * @param room umgebender Raum
     * @param entityMeshes Register, aus dem die Entity-Meshes bezogen werden
     * @param particleMeshes Register, aus dem die Particle-Meshes bezogen werden
     * @param soundEngine Sound Engine, aus der die Sounds der Entity bezogen werden
     */
    public Drone(Vector3 position, boolean active, Room room, HashMap<String, Mesh> entityMeshes, HashMap<String, Mesh> particleMeshes, SoundEngine soundEngine)
    {
        _room = room;
        
        _random = new Random();
        
        _isActive = active;
        _currentAmmo = MAGAZINE_CAPACITY;
        _timeSinceLastShot = 0.0;
        _health = 50;
        _maxHealth = 50;
        _reloadingVoiceLineTriggered = true;
        
        _soundEngine = soundEngine;
        
        _particleMeshes = particleMeshes;
        
        _muzzleFlash = new SimpleDynamicGameObject(entityMeshes.get("turret_muzzle_flash"), TurtleColor.RED, new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _rotorLeft = new SimpleDynamicGameObject(entityMeshes.get("drone_rotor"), TurtleColor.LIGHT_GRAY, new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _rotorRight = new SimpleDynamicGameObject(entityMeshes.get("drone_rotor"), TurtleColor.LIGHT_GRAY, new Vector3(), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        
        _position = new Vector3(position.getX(), _isActive ? FLYING_HEIGHT : MIN_FLYING_HEIGHT, position.getZ());
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);
        _muzzlePos1 = new Vector3(0.987184, -0.782814, -0.1238);
        _muzzlePos2 = new Vector3(0.987184, -0.782814, 0.1238);
        
        // Startzustand setzen
        if(_room.getPatrolRoute() != null)
        {
            _state = DroneAIState.PATROL;
            
            // Nächsten Patrouillenpunkt berechnen
            double lowestDistance = Double.POSITIVE_INFINITY;
            
            for(int i = 0; i < _room.getPatrolRouteLength(); i++)
            {
                // 2D-projizierten Abstand zum jeweiligen Punkt berechnen
                Vector3 patrolPoint = MapHandler.tilePosToWorldPos(_room.getPatrolRoute()[i]);
                double dist = new Vector2(patrolPoint.getX(), patrolPoint.getZ()).subtract(new Vector2(_position.getX(), _position.getZ())).getLength();
                
                // Wenn die Distanz kleiner als die bisher niedrigste Distanz ist, nächsten Patrouillenpunkt setzen
                if(dist < lowestDistance)
                {
                    lowestDistance = dist;
                    _nextPatrolPoint = i;
                }
            }
        }
        else
        {
            _state = DroneAIState.WANDERING;
            _nextPatrolPoint = -1;
        }
        
        _activeMesh = entityMeshes.get("drone_active");
        _inactiveMesh = entityMeshes.get("drone_inactive");
        _color = TurtleColor.LIGHT_GRAY;
        
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
        
        // Aktivitätsstatus des Geschützes prüfen
        if(!_isActive)
        {
            // Flughöhe verringern
            if(_position.getY() > MIN_FLYING_HEIGHT)
            {
                _position.setY(Math.max(MIN_FLYING_HEIGHT, _position.getY() - FALLING_SPEED * deltaTime));
                _recalculateModelMatrix = true;
            }
            
            return;
        }
        
        // Mündungsfeuer positionieren und skalieren
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
        
        // Hover-Sound-Lautstärke berechnen
        Vector2 playerPos2D = new Vector2(cameraPosition.getX(), cameraPosition.getZ());
        Vector2 currentPos2D = new Vector2(_position.getX(), _position.getZ());
        double dist = playerPos2D.subtract(currentPos2D).getLength();
        // Sound linear attenuieren
        double volume = 1.0 - dist / HOVER_SOUND_FADE_DIST;
        
        if(volume > 0.0)
        {
            // Hover-Sound neu erstellen, wenn nonexistent oder fertig abgespielt
            if(_hoverSound == null || _hoverSound.getStatus() == MediaPlayer.Status.STOPPED || _hoverSound.getStatus() == MediaPlayer.Status.DISPOSED)
            {
                _hoverSound = _soundEngine.playSound("drone_hover", volume, false);
            }
            // Sonst Lautstärke des bestehenden Sounds setzen
            else
            {
                _hoverSound.setVolume(volume);
            }
        }
        
        // Waffe Laden
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
        
        // Verschiedene AI-Zustände unterschiedlich behandeln
        // Zufälliges umherfliegen
        if(_state == DroneAIState.WANDERING)
        {
            // Spieler entdecken und angreifen
            double playerAngle = getAngleTo(_room.getMap().getPlayer().getPosition()) - _rotation.getY();
            if(getDistanceTo(_room.getMap().getPlayer().getPosition()) <= PLAYER_DISCOVERY_DISTANCE || (hasLineOfSight(MAX_SIGHT_RANGE) && (playerAngle <= PLAYER_DISCOVERY_ANGLE) || (playerAngle >= 360.0 - PLAYER_DISCOVERY_ANGLE)))
            {
                _state = DroneAIState.ATTACKING;
                _soundEngine.playSound("drone_enemy_detected", VOICELINE_VOLUME, false);
            }
            
            // Wenn Pfad leer ist
            if(_path == null || _path.size() == 0)
            {
                // Zufällige Ziel-Tile auswählen
                HashSet<Vector2i> filteredTiles = _room.getFilteredTiles(new ITileFilter()
                    {
                        @Override
                        public boolean evaluate(int tileValue)
                        {
                            return !Tile.isSolidOrNone(tileValue) && !Tile.isSemiSolid(tileValue);
                        }
                    }
                );
                int randomIndex = _random.nextInt(filteredTiles.size());
                Iterator<Vector2i> iterator = filteredTiles.iterator();
                for(int i = 0; i < randomIndex; i++)
                {
                    iterator.next();
                }
                Vector2i pathTarget = iterator.next();
                
                // Pfad generieren
                if(_previousPathTarget == null || !pathTarget.equals(_previousPathTarget))
                {
                    _path = AStarPathSolver.solvePath(MapHandler.worldPosToTilePos(_position), pathTarget, _room);
                    
                    // Ersten Knoten entfernen, wenn er ein Rückschritt ist, die Drohne also bereits näher am 2. Punkt ist
                    if(_path != null && _path.size() > 1)
                    {
                        Vector3 first = MapHandler.tilePosToWorldPos(_path.get(0).getPosition());
                        Vector3 second = MapHandler.tilePosToWorldPos(_path.get(1).getPosition());
                        
                        double firstNodeDist = new Vector2(second.getX(), second.getZ()).subtract(new Vector2(first.getX(), first.getZ())).getLength();
                        double currentDist = new Vector2(second.getX(), second.getZ()).subtract(new Vector2(_position.getX(), _position.getZ())).getLength();
                        
                        if(firstNodeDist >= currentDist)
                        {
                            _path.remove();
                        }
                    }
                    
                    _previousPathTarget = new Vector2i(pathTarget);
                }                
            }
            
            if(_path != null && _path.size() > 0)
            {
                // Zum nächsten Pfadknoten navigieren
                PathNode nextTarget = _path.getFirst();            
                Vector3 nextTargetPos = MapHandler.tilePosToWorldPos(nextTarget.getPosition());
                
                // Ausrichten
                lookAtFade(nextTargetPos, TRACKING_INERTIA);
                
                // Bewegen
                Vector2 nextTargetPos2D = new Vector2(nextTargetPos.getX(), nextTargetPos.getZ());
                Vector2 pos2D = new Vector2(_position.getX(), _position.getZ());
                Vector2 dir = nextTargetPos2D.subtract(pos2D).normalize();
                Vector2 movement = dir.multiply(Math.min(getDistanceTo(nextTargetPos), FLYING_SPEED * deltaTime));
                move(new Vector3(movement.getX(), 0.0, movement.getY()));
                
                // Aktuellen Knoten fertigstellen
                if(getDistanceTo(nextTargetPos) < 0.05)
                {
                    _path.remove();
                }
            }
        }
        // Patrouillenroute abfliegen
        else if(_state == DroneAIState.PATROL)
        {            
            // Spieler entdecken und angreifen
            double playerAngle = getAngleTo(_room.getMap().getPlayer().getPosition()) - _rotation.getY();
            if(getDistanceTo(_room.getMap().getPlayer().getPosition()) <= PLAYER_DISCOVERY_DISTANCE || (hasLineOfSight(MAX_SIGHT_RANGE) && (playerAngle <= PLAYER_DISCOVERY_ANGLE) || (playerAngle >= 360.0 - PLAYER_DISCOVERY_ANGLE)))
            {
                _state = DroneAIState.ATTACKING;
                _soundEngine.playSound("drone_enemy_detected", VOICELINE_VOLUME, false);
            }
            
            // Wenn Pfad leer ist
            if(_path == null || _path.size() == 0)
            {
                // Patrouillenpunkt auswählen
                Vector2i pathTarget = _room.getPatrolRoute()[_nextPatrolPoint];
                
                // Pfad generieren
                if(_previousPathTarget == null || !pathTarget.equals(_previousPathTarget))
                {
                    _path = AStarPathSolver.solvePath(MapHandler.worldPosToTilePos(_position), pathTarget, _room);
                    
                    // Ersten Knoten entfernen, wenn er ein Rückschritt ist, die Drohne also bereits näher am 2. Punkt ist
                    if(_path != null && _path.size() > 1)
                    {
                        Vector3 first = MapHandler.tilePosToWorldPos(_path.get(0).getPosition());
                        Vector3 second = MapHandler.tilePosToWorldPos(_path.get(1).getPosition());
                        
                        double firstNodeDist = new Vector2(second.getX(), second.getZ()).subtract(new Vector2(first.getX(), first.getZ())).getLength();
                        double currentDist = new Vector2(second.getX(), second.getZ()).subtract(new Vector2(_position.getX(), _position.getZ())).getLength();
                        
                        if(firstNodeDist >= currentDist)
                        {
                            _path.remove();
                        }
                    }
                    
                    _previousPathTarget = new Vector2i(pathTarget);
                }                
            }
            
            if(_path != null && _path.size() > 0)
            {
                // Zum nächsten Pfadknoten navigieren
                PathNode nextTarget = _path.getFirst();            
                Vector3 nextTargetPos = MapHandler.tilePosToWorldPos(nextTarget.getPosition());
                
                // Ausrichten
                lookAtFade(nextTargetPos, TRACKING_INERTIA);
                
                // Bewegen
                Vector2 nextTargetPos2D = new Vector2(nextTargetPos.getX(), nextTargetPos.getZ());
                Vector2 pos2D = new Vector2(_position.getX(), _position.getZ());
                Vector2 dir = nextTargetPos2D.subtract(pos2D).normalize();
                Vector2 movement = dir.multiply(Math.min(getDistanceTo(nextTargetPos), FLYING_SPEED * deltaTime));
                move(new Vector3(movement.getX(), 0.0, movement.getY()));
                
                // Aktuellen Knoten fertigstellen
                if(getDistanceTo(nextTargetPos) < 0.05)
                {
                    _path.remove();
                    
                    // Nächsten Patrouillenknoten ansteuern, wenn Pfad zuende ist
                    if(_path.size() == 0)
                    {
                        _nextPatrolPoint = (_nextPatrolPoint + 1) % _room.getPatrolRouteLength();
                    }
                }
            }
        }
        // Spieler verfolgen
        else if(_state == DroneAIState.CHASING)
        {
            // Spieler angreifen, wenn er sichtbar ist
            if(hasLineOfSight(MAX_SIGHT_RANGE))
            {
                _state = DroneAIState.ATTACKING;
            }
            // Nachfolgendes nur ausführen, wenn Spieler nicht sichtbar ist
            else
            {
                // Pfad generieren
                Vector2i pathTarget = MapHandler.worldPosToTilePos(_room.getMap().getPlayer().getPosition());
                if(_previousPathTarget == null || !pathTarget.equals(_previousPathTarget))
                {
                    _path = AStarPathSolver.solvePath(MapHandler.worldPosToTilePos(_position), pathTarget, _room);
                    
                    // Ersten Knoten entfernen, wenn er ein Rückschritt ist, die Drohne also bereits näher am 2. Punkt ist
                    if(_path != null && _path.size() > 1)
                    {
                        Vector3 first = MapHandler.tilePosToWorldPos(_path.get(0).getPosition());
                        Vector3 second = MapHandler.tilePosToWorldPos(_path.get(1).getPosition());
                        
                        double firstNodeDist = new Vector2(second.getX(), second.getZ()).subtract(new Vector2(first.getX(), first.getZ())).getLength();
                        double currentDist = new Vector2(second.getX(), second.getZ()).subtract(new Vector2(_position.getX(), _position.getZ())).getLength();
                        
                        if(firstNodeDist >= currentDist)
                        {
                            _path.remove();
                        }
                    }
                    
                    _previousPathTarget = new Vector2i(pathTarget);
                }
                
                if(_path != null && _path.size() > 0)
                {
                    // Zum nächsten Pfadknoten navigieren
                    PathNode nextTarget = _path.getFirst();            
                    Vector3 nextTargetPos = MapHandler.tilePosToWorldPos(nextTarget.getPosition());
                    
                    // Ausrichten
                    lookAtFade(nextTargetPos, TRACKING_INERTIA);
                    
                    // Bewegen
                    Vector2 nextTargetPos2D = new Vector2(nextTargetPos.getX(), nextTargetPos.getZ());
                    Vector2 pos2D = new Vector2(_position.getX(), _position.getZ());
                    Vector2 dir = nextTargetPos2D.subtract(pos2D).normalize();
                    Vector2 movement = dir.multiply(Math.min(getDistanceTo(nextTargetPos), FLYING_SPEED * deltaTime));
                    move(new Vector3(movement.getX(), 0.0, movement.getY()));
                    
                    // Aktuellen Knoten fertigstellen
                    if(getDistanceTo(nextTargetPos) < 0.05)
                    {
                        _path.remove();
                    }
                }
            }
        }
        else if(_state == DroneAIState.ATTACKING)
        {
            // Spieler verfolgen, wenn er nicht sichtbar ist
            if(!hasLineOfSight(MAX_SIGHT_RANGE))
            {
                _state = DroneAIState.CHASING;
            }
            // Nachfolgendes nur ausführen, wenn Spieler sichtbar ist
            else
            {
                // Drohne zum Spieler ausrichten
                Player player = _room.getMap().getPlayer();
                lookAtFade(player.getPosition(), TRACKING_INERTIA);
                
                // Schießen, wenn Munition vorhanden, Spieler nah genug, Spieler genau genug anvisiert, und genug Zeit vergangen ist
                double inaccuracyAngle = getSightAngleTo(player.getPosition());
                double playerDist = getDistanceTo(player.getPosition());
                
                if(_currentAmmo > 0 && playerDist <= MAX_GUN_RANGE && inaccuracyAngle <= FIRING_ANGLE_TOLERANCE && _timeSinceLastShot > FIRING_COOLDOWN)
                {
                    // Munition abziehen und Timer zurücksetzen
                    _currentAmmo--;
                    _timeSinceLastShot = 0.0;
                    
                    // Reload-Voiceline zurücksetzen
                    _reloadingVoiceLineTriggered = false;
                    
                    // Raycast vorbereiten
                    Vector2 source = new Vector2(_position.getX(), _position.getZ());
                    Vector2 target = new Vector2(player.getPosition().getX(), player.getPosition().getZ());
                    Vector2 direction = target.subtract(source).normalize();                
                    EnumSet<PhysicsLayer> terminationFilter = EnumSet.of(PhysicsLayer.PLAYER, PhysicsLayer.SOLID);
                    EnumSet<PhysicsLayer>exclusionFilter = EnumSet.of(PhysicsLayer.ENEMY);
                    
                    // Ungenauigkeit zum Schuss hinzufügen
                    direction = direction.rotateAroundOrigin((_random.nextDouble()*2.0-1.0) * MAXIMUM_INACCURACY_ANGLE);
                    
                    // Raycast durchführen
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
                                
                                // Spieler Schaden hinzufügen
                                victim.damage(BULLET_DAMAGE, "drone shot");
                            }
                        }
                    }
                    
                    // Zufälligen Schusssound abspielen
                    _soundEngine.playSoundFromGroup("heavy_shot", 0.65, false);
                }
                
                // Distanz zum Spieler durch Vorwärts-/Rückwärtsflug korrigieren
                double deltaDist = playerDist - IDEAL_PLAYER_DISTANCE;
                playerPos2D = new Vector2(player.getPosition().getX(), player.getPosition().getZ());
                Vector2 pos2D = new Vector2(_position.getX(), _position.getZ());
                Vector2 dir = playerPos2D.subtract(pos2D).normalize();
                // Bewegung auf FLYING_SPEED beschränken
                Vector2 movement = dir.multiply(Math.max(-FLYING_SPEED * deltaTime, Math.min(deltaDist, FLYING_SPEED * deltaTime)));
                move(new Vector3(movement.getX(), 0.0, movement.getY()));
            }
        }
        
        //TODO:
        // Feuer-/Rauchpartikel aktivieren
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
     * @see ICollisionListener#onCollision()
     */
    public void onCollision(ICollider self, ICollider other)
    {
        EnumSet<PhysicsLayer> ignoredLayers = EnumSet.of(PhysicsLayer.ITEM, PhysicsLayer.RAYCAST);
        if(!ignoredLayers.contains(other.getLayer()))
        {
            ((CircleCollider)self).resolveCollision(other);
        }
    }
    
    /**
     * @see ICollisionListener#onResolution()
     */
    public void onResolution(ICollider self, ICollider other)
    {
        // Positionen von Drohne dem Collider angleichen
        Vector2 colliderPos = _collider.getPosition();
        _position = new Vector3(colliderPos.getX(), _position.getY(), colliderPos.getY());
    }
    
    /**
     * Gibt die transformierte Position der Laufmündung im World Space zurück
     * @return Position der Laufmündung im World Space
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
        
        // Spieler bemerken und angreifen
        _state = DroneAIState.ATTACKING;
        
        // Partikelsystem erzeugen
        _room.addParticleSystem(new DroneHitParticleSystem(_position, _room, _particleMeshes));
        
        // Neue Leben berechnen
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
