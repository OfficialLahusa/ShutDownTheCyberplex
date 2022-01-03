package game;

import maths.*;
import core.*;
import physics.*;
import java.util.*;

/**
 * Partikelsystem, das beim der Explosion des Reactor Cores benutzt wird.
 * 
 * @author Sven Schreiber
 * @version 01.01.2022
 */
public class ReactorExplosionParticleSystem implements IParticleSystem
{
    // Parent
    private Room _room;
    
    // Partikel
    private ArrayList<IParticle> _particles;
    
    // Funktionalität
    private double _elapsedTime;
    private Random _random;
    
    // Rendering
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private TurtleColor[] _colors;
    
    private static int PARTICLE_COUNT = 20;
    private static final int SPHERE_RADIUS = 100;
    
    /**
     * Konstruktor für ReactorExplosionParticleSystem
     * @param position Position des Partikelsystems
     * @param room umschließender Raum
     * @param particleMeshes HashMap der registrierten Partikel-Meshes
     */
    public ReactorExplosionParticleSystem(Vector3 position, Room room, HashMap<String, Mesh> particleMeshes)
    {
        if(position == null)
        {
            throw new IllegalArgumentException("position was null when constructing ReactorExplosionParticleSystem");
        }
        if(room == null)
        {
            throw new IllegalArgumentException("room was null when constructing ReactorExplosionParticleSystem");
        }
        if(particleMeshes == null)
        {
            throw new IllegalArgumentException("particleMeshes was null when constructing ReactorExplosionParticleSystem");
        }
        
        _room = room;
        
        _particles = new ArrayList<IParticle>();
        
        _elapsedTime = 0.0;
        _random = new Random();
        
        _position = new Vector3(position);
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);
        _colors = new TurtleColor[] { TurtleColor.RED, TurtleColor.ORANGE, TurtleColor.YELLOW };
        
        for(int i = 0; i < PARTICLE_COUNT; i++)
        {
            // Zufällige Position innerhalb einer Kugel
            double theta = _random.nextDouble() * 2 * Math.PI;
            double v = _random.nextDouble();
            double phi = Math.acos((2 * v) - 1);
            double r = Math.pow(_random.nextDouble() * SPHERE_RADIUS, 1.0/3.0);
            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);
            Vector3 randomPosition = new Vector3(x, y, z);
            
            _particles.add(
                new BillboardParticle(
                    _room, particleMeshes.get("drone_hit_particle"), _colors[_random.nextInt(_colors.length)],
                    _position.add(randomPosition), new Vector3(), new Vector3(1.0, 1.0, 1.0),
                    new Vector3(1.0, 1.0, 1.0), new Vector3(0.0, 0.5, 0.0),
                    _random.nextDouble()
                )
            );
        }
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        for(IParticle particle : _particles)
        {
            particle.draw(renderer, camera);
        }
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        _elapsedTime += deltaTime;
        
        for(IParticle particle : _particles)
        {
            particle.update(deltaTime, runTime, cameraPosition);
        }
        
        // Entfernt iterativ die gestoppten Partikelsysteme
        for(Iterator<IParticle> iter = _particles.iterator(); iter.hasNext();)
        {
            IParticle elem = iter.next();
            if(elem == null || !elem.isAlive())
            {
                iter.remove();
            }
        }
    }
    
    /**
     * @see IParticleSystem#getParticleCount()
     */
    public int getParticleCount()
    {
        return _particles.size();
    }
    
    /**
     * @see IParticleSystem#clear()
     */
    public void clear()
    {
        _particles.clear();
    }
    
    /**
     * @see IParticleSystem#isDone()
     */
    public boolean isDone()
    {
        return false;
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
        return null;
    }
    
    /**
     * @see IGameObject#setColor()
     */
    public void setColor(TurtleColor color)
    {
        return;
    }
}
