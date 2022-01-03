package core;

import maths.*;
import physics.*;
import game.*;

/**
 * Stets auf die Kamera ausgerichteter Partikel mit Startgeschwindigkeit, auf den Schwerkraft wirkt
 * 
 * @author Lasse Huber-Saffer 
 * @version 01.01.2022
 */
public class BillboardParticle implements IParticle
{
    // Parent
    private Room _room;
    
    // Funktionalität
    private boolean _isAlive;
    private double _elapsedTime;
    private Vector3 _velocity;
    private Vector3 _gravity;
    private Double _lifeTime;
    
    // Rendering
    private Mesh _mesh;
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private TurtleColor _color;
    
    /**
     * Konstruktor für Objekte der Klasse BillboardParticle
     * @param room umschließender Raum
     * @param mesh Mesh des Partikels
     * @param color Farbe
     * @param position Position
     * @param rotation Rotation
     * @param scale Skalierung
     * @param velocity Startgeschwindigkeitsvektor des Partikels
     * @param gravity Beschleunigungsvektor des Partikels
     * @param lifeTime (Optional) maximale Lebenszeit des Partikels
     */
    public BillboardParticle(Room room, Mesh mesh, TurtleColor color, Vector3 position, Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 gravity, Double lifeTime)
    {
        _room = room;
        _mesh = new Mesh(mesh);
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
        _color = color;
        
        _isAlive = true;
        _elapsedTime = 0.0;
        _velocity = new Vector3(velocity);
        _gravity = new Vector3(gravity);
        _lifeTime = lifeTime;
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        renderer.drawMesh(_mesh, getModelMatrix(), _color, camera);
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        _elapsedTime += deltaTime;
        
        _rotation.setY(_room.getMap().getPlayer().getRotation().getY());
        
        if(_isAlive)
        {
            // Beschleunigung und Geschwindigkeit anwenden
            _velocity = _velocity.add(_gravity.multiply(deltaTime));
            _position = _position.add(_velocity.multiply(deltaTime));
            
            // Bodenhöhe nicht unterschreiten
            if(_position.getY() < 0.0)
            {
                _position.setY(0.0);
                _isAlive = false;
            }
            else if(_lifeTime != null && _elapsedTime >= _lifeTime)
            {
                _isAlive = false;
            }
        }
    }
    
    /**
     * Generiert die Modelmatrix aller Transformationen auf dem BillboardParticle
     * @return Modelmatrix
     */
    private Matrix4 getModelMatrix()
    {
        Matrix4 translation = MatrixGenerator.generateTranslationMatrix(_position);
        Matrix4 rotationX = MatrixGenerator.generateAxialRotationMatrix(new Vector3(1, 0, 0), _rotation.getX());
        Matrix4 rotationY = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 1, 0), _rotation.getY());
        Matrix4 rotationZ = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 0, 1), _rotation.getZ());
        Matrix4 scale = MatrixGenerator.generateScaleMatrix(_scale);
            
        Matrix4 transform = translation.multiply(scale.multiply(rotationZ.multiply(rotationY.multiply(rotationX))));
        return transform;
    }
    
    /**
     * @see IParticle#isAlive()
     */
    public boolean isAlive()
    {
        return _isAlive;
    }
    
    /**
     * @see IParticle#getElapsedTime
     */
    public double getElapsedTime()
    {
        return _elapsedTime;
    }
    
    /**
     * @see IParticle#kill()
     */
    public void kill()
    {
        _isAlive = false;
    }
    
    /**
     * @see IDynamicGameObject#setPosition()
     */
    public void setPosition(Vector3 position)
    {
        _position = new Vector3(position);
    }
    
    /**
     * @see IDynamicGameObject#setRotation()
     */
    public void setRotation(Vector3 rotation)
    {
        _rotation = new Vector3(rotation);
    }
    
    /**
     * @see IDynamicGameObject#setScale()
     */
    public void setScale(Vector3 scale)
    {
        _scale = new Vector3(scale);
    }
    
    /**
     * @see IDynamicGameObject#move()
     */
    public void move(Vector3 delta)
    {
        _position = _position.add(delta);
    }
    
    /**
     * @see IDynamicGameObject#rotate()
     */
    public void rotate(Vector3 rotation)
    {
        _rotation = _rotation.add(rotation);
    }
    
    /**
     * @see IDynamicGameObject#scale()
     */
    public void scale(Vector3 scale)
    {
        _scale = new Vector3(_scale.getX() * scale.getX(), _scale.getY() * scale.getY(), _scale.getZ() * scale.getZ());
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
        return _position;
    }
    
    /**
     * @see IGameObject#getRotation()
     */
    public Vector3 getRotation()
    {
        return _rotation;
    }
    
    /**
     * @see IGameObject#getScale()
     */
    public Vector3 getScale()
    {
        return _scale;
    }
    
    /**
     * @see IGameObject#getColor()
     */
    public TurtleColor getColor()
    {
        return _color;
    }
    
    /**
     * @see IGameObject#setColor()
     */
    public void setColor(TurtleColor color)
    {
        _color = color;
    }
}
