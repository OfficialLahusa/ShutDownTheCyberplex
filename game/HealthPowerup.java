package game;

import maths.*;
import core.*;
import physics.*;
import java.util.*;

/**
 * Einsammelbares Powerup, das den Spieler heilt
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
 */
public class HealthPowerup implements IDynamicGameObject, ICollisionListener
{
    // Parent-Raum
    private Room _room;
    
    // Sound
    private SoundEngine _soundEngine;
    
    // Child-Objekte
    private SimpleDynamicGameObject _muzzleFlash;
    
    // Physik
    private CircleCollider _collider;
    
    // Funktionaliät
    private double _elapsedTime;
    
    // Positionierung
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    
    // Rendering
    private Mesh _mesh;
    private TurtleColor _color;
    private boolean _recalculateModelMatrix;
    private Matrix4 _model;
    
    // Konstanten
    private static final double COLLIDER_RADIUS = 0.4;
    private static final double ROTATION_SPEED = 120.0;
    private static final double HOVERING_HEIGHT_MIN = 1.5;
    private static final double HOVERING_HEIGHT_MAX = 2.5;
    private static final double HOVERING_PERIOD = 2.0;
    public static final int HEALING_AMOUNT = 25;
    
    /**
     * Konstruktor für HealthPowerup mit Position und Meshes
     * @param position Position
     * @param room umgebender Raum
     * @param entityMeshes Register der EntityMeshes, aus dem die Meshes bezogen werden
     * @param soundEngine Sound Engine, aus der die Sounds der Entity bezogen werden
     */
    public HealthPowerup(Vector3 position, Room room, HashMap<String, Mesh> entityMeshes, SoundEngine soundEngine)
    {
        _room = room;
        
        _soundEngine = soundEngine;
        
        _elapsedTime = 0.0;
        
        _position = new Vector3(position.getX(), HOVERING_HEIGHT_MIN, position.getZ());
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);
        
        _mesh = entityMeshes.get("health_powerup");
        _color = TurtleColor.GREEN;
        
        _recalculateModelMatrix = true;
        _model = null;
        
        _collider = new CircleCollider(new Vector2(_position.getX(), _position.getZ()), COLLIDER_RADIUS, PhysicsLayer.ITEM);
        _collider.setListener(this);
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        _elapsedTime += deltaTime;
        
        // Hüpfende Hover-Animation
        _position.setY(HOVERING_HEIGHT_MIN + (HOVERING_HEIGHT_MAX - HOVERING_HEIGHT_MIN) * Math.sin(Math.toRadians(_elapsedTime * (360 / HOVERING_PERIOD))));
        
        // Drehung
        _rotation.setY((ROTATION_SPEED * _elapsedTime) % 360.0);
        
        _recalculateModelMatrix = true;
        
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
     * @see ICollisionListener#onCollision()
     */
    public void onCollision(ICollider self, ICollider other)
    {
        // Überprüfen, ob anderer Collider der Spieler ist
        if(other.getLayer() == PhysicsLayer.PLAYER)
        {
            // Spielerobjekt erhalten
            Player player = (Player)other.getListener();
            
            // Nur aufsammeln, wenn der Spieler nicht bereits volle Leben hat
            if(player.getHealth() < player.getMaxHealth())
            {
                // Heilung wirken
                player.heal(HEALING_AMOUNT, "health powerup");
                
                // Sound abspielen
                _soundEngine.playSound("health_powerup_collected", 1.0, false);
                
                // Entity aus Map entfernen
                int index = -1;
                ArrayList<IGameObject> roomEntities = _room.getEntities();
                
                for(int i = 0; i < roomEntities.size(); i++)
                {
                    if(roomEntities.get(i) == this)
                    {
                        index = i;
                        break;
                    }
                }
                
                if(index != -1)
                {
                    roomEntities.remove(index);
                }
            }
        }
        
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
