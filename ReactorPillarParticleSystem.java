import java.util.*;

/**
 * Partikelsystem, das aus der Säule des Reaktorkerns horizontale Sechsecke emittiert, die mit der Zeit größer werden
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
 */
public class ReactorPillarParticleSystem implements IParticleSystem
{
    // Parent
    private Room _room;
    
    // Partikel
    private ArrayList<IParticle> _particles;
    private HashMap<String, Mesh> _particleMeshes;
    
    // Funktionalität
    private double _elapsedTime;
    private double _timeSinceLastEmission;
    private Random _random;
    
    // Rendering
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private TurtleColor _color;
    
    private static double PARTICLE_LIFETIME = 0.5;
    private static double PARTICLE_EMISSION_COOLDOWN = 0.15;
    
    /**
     * Konstruktor für DroneHitParticleSystems
     * @param position Position des Partikelsystems
     * @param room umschließender Raum
     * @param particleMeshes HashMap der registrierten Partikel-Meshes
     */
    public ReactorPillarParticleSystem(Vector3 position, Room room, HashMap<String, Mesh> particleMeshes)
    {
        if(position == null)
        {
            throw new IllegalArgumentException("position was null when constructing ReactorPillarParticleSystem");
        }
        if(room == null)
        {
            throw new IllegalArgumentException("room was null when constructing ReactorPillarParticleSystem");
        }
        if(particleMeshes == null)
        {
            throw new IllegalArgumentException("particleMeshes was null when constructing ReactorPillarParticleSystem");
        }
        
        _room = room;
        
        _particles = new ArrayList<IParticle>();
        _particleMeshes = particleMeshes;
        
        _elapsedTime = 0.0;
        _timeSinceLastEmission = PARTICLE_EMISSION_COOLDOWN;
        _random = new Random();
        
        _position = new Vector3(position);
        _rotation = new Vector3();
        _scale = new Vector3(1.0, 1.0, 1.0);
        _color = TurtleColor.WHITE;
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
        _timeSinceLastEmission += deltaTime;
        
        // Neuen Partikel hinzufügen
        if(_timeSinceLastEmission >= PARTICLE_EMISSION_COOLDOWN)
        {
            _timeSinceLastEmission = 0.0;
            _particles.add(
                new Particle(
                    _room, _particleMeshes.get("reactor_pillar_emission"), _color,
                    _position, new Vector3(), new Vector3(1.0, 1.0, 1.0),
                    new Vector3(0.0, 1.0, 0.0), new Vector3(),
                    PARTICLE_LIFETIME
                )
            );
        }
        
        // Partikel updaten
        for(IParticle particle : _particles)
        {
            particle.update(deltaTime, runTime, cameraPosition);
            
            double scaleValue = 1.0 + particle.getElapsedTime() / PARTICLE_LIFETIME;
            particle.setScale(new Vector3(scaleValue, scaleValue, scaleValue));
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
