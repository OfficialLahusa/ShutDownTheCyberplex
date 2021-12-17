/**
 * Kreis-Collider mit Mittelpunkt und Radius
 * 
 * @author Lasse Huber-Saffer
 * @version 16.12.2021
 */
public class CircleCollider implements ICollider
{
    private PhysicsLayer _layer;
    private Vector2 _position;
    private double _radius;
    private ICollisionListener _listener;

    /**
     * Konstruktor für Objekte der Klasse CircleCollider
     * @param position Mittelpunkt des Kreises
     * @param radius radius des Kreises (> 0.0)
     * @param layer Physik-Ebene auf der dieser Collider agiert
     */
    public CircleCollider(Vector2 position, double radius, PhysicsLayer layer)
    {
        _position = new Vector2(position);
        if(radius <= 0.0)
        {
            throw new IllegalArgumentException("Circle collider radius was less than or equal to 0.0");
        }
        _radius = radius;
        _layer = layer;
        _listener = null;
    }
    
    /**
     * @see ICollider#intersect()
     */
    public boolean intersects(ICollider other)
    {
        boolean didIntersect = false;
        
        if(other instanceof CircleCollider)
        {
            CircleCollider otherCircle = (CircleCollider)other;
            double dist = Math.abs(otherCircle.getPosition().subtract(_position).getLength());
            double radiusSum = _radius + otherCircle.getRadius();
            didIntersect = dist <= radiusSum;
        }
        
        if(didIntersect && _listener != null)
        {
            _listener.onCollision(this, other);
        }
        
        return didIntersect;
    }
    
    /**
     * Berechnet die maximale Überschneidungstiefe mit einem anderen Kreis-Collider
     * @param otherCircle anderer Kreis-Collider
     */
    public double getCircleOverlap(CircleCollider otherCircle)
    {
        if(!intersects(otherCircle))
        {
            return 0.0;
        }
        else
        {
            double dist = Math.abs(otherCircle.getPosition().subtract(_position).getLength());
            double radiusSum = _radius + otherCircle.getRadius();
            return radiusSum - dist;
        }
    }
    
    /**
     * Bewegt diesen Kreis um einen Bewegungsvektor
     * @param translation Bewegungsvektor
     */
    public void move(Vector2 translation)
    {
        _position = _position.add(translation);
    }
    
    /**
     * @see ICollider#getListener()
     */
    public ICollisionListener getListener()
    {
        return _listener;
    }
    
    /**
     * @see ICollider#setListener()
     */
    public void setListener(ICollisionListener listener)
    {
        _listener = listener;
    }
    
    /**
     * @see ICollider#getLayer()
     */
    public PhysicsLayer getLayer()
    {
        return _layer;
    }
    
    /**
     * @see ICollider#setLayer()
     */
    public void setLayer(PhysicsLayer layer)
    {
        _layer = layer;
    }
    
    /**
     * Gibt den Mittelpunkt des Kreises zurück
     * @return Mittelpunkt des Kreises
     */
    public Vector2 getPosition()
    {
        return new Vector2(_position);
    }
    
    /**
     * Gibt den Radius des Kreises zurück
     * @return Radius des Kreises
     */
    public double getRadius()
    {
        return _radius;
    }
    
    /**
     * Setzt den Mittelpunkt des Kreises
     * @param position Position des Kreismittelpunktes
     */
    public void setPosition(Vector2 position)
    {
        _position = new Vector2(position);
    }
    
    /**
     * Setzt den Radius des Kreises
     * @param radius Radius des Kreises (> 0.0)
     */
    public void setRadius(double radius)
    {
        if(radius <= 0.0)
        {
            throw new IllegalArgumentException("Circle collider radius was less than or equal to 0.0");
        }
        _radius = radius;
    }
}
