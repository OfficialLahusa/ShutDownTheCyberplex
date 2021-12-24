/**
 * Kreis-Collider mit Mittelpunkt und Radius
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
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
        
        // Kreis-Kreis-Kollision wird selbst erkannt
        if(other instanceof CircleCollider)
        {
            CircleCollider otherCircle = (CircleCollider)other;
            double dist = Math.abs(otherCircle.getPosition().subtract(_position).getLength());
            double radiusSum = _radius + otherCircle.getRadius();
            didIntersect = dist <= radiusSum;
        }
        // Linie-Kreis-Kollision wird über Linie erkannt
        else if(other instanceof LineCollider)
        {
            didIntersect = other.intersects(this);
        }
        // Compount-Kreis-Kollision wird über Compound erkannt
        else if(other instanceof CompoundCollider)
        {
            didIntersect = other.intersects(this);
        }
        else
        {
            throw new UnsupportedOperationException("This collider type is not supported by CircleCollider");
        }
        
        return didIntersect;
    }
    
    /**
     * @see ICollider#detectCollision()
     */
    public boolean detectCollision(ICollider other)
    {
        boolean didIntersect = intersects(other);
        
        // Listener beidseitig auslösen
        if(didIntersect)
        {
            if(_listener != null) _listener.onCollision(this, other);
            if(other.getListener() != null) other.getListener().onCollision(other, this);
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
     * Bewegt diesen Collider, um eine Kollision mit einem gegebenen Collider aufzulösen.
     * Im Falle einer tatsächlichen Bewegung wird die Methode onResolution des Listeners ausgeführt.
     * @param otherLine Collider, dem ausgewichen werden soll (Line oder Circle)
     */
    public void resolveCollision(ICollider other)
    {        
        if(other instanceof LineCollider)
        {
            resolveLineCollision((LineCollider)other);
        }
        else if(other instanceof CircleCollider)
        {
            resolveCircleCollision((CircleCollider)other);
        }
        else if(other instanceof CompoundCollider)
        {
            for(ICollider child : ((CompoundCollider)other).getColliders())
            {
                resolveCollision(child);
            }
        }
        else
        {
            throw new UnsupportedOperationException("Tried to resolve a collision between a CircleCollider an an unsupported collider");
        }
    }
    
    /**
     * Bewegt diesen Collider, um eine Kollision mit einem gegebenen LineCollider aufzulösen.
     * @param otherLine LineCollider, dem ausgewichen werden soll
     */
    private void resolveLineCollision(LineCollider otherLine)
    {
        if(intersects(otherLine))
        {
            Vector2 closestLinePoint = otherLine.getClosestPoint(_position);
            Vector2 delta = closestLinePoint.subtract(_position);
            double dist = delta.getLength();
            Vector2 dir = delta.normalize();
            move(dir.multiply(-(_radius - dist)));
            
            // Listener auslösen
            if(_listener != null)
            {
                _listener.onResolution(this, otherLine);
            }
        }
    }
    
    /**
     * Bewegt diesen Collider, um eine Kollision mit einem gegebenen CircleCollider aufzulösen.
     * @param otherCircle CircleCollider, dem ausgewichen werden soll
     */
    private void resolveCircleCollision(CircleCollider otherCircle)
    {
        Vector2 dir = otherCircle.getPosition().subtract(_position).normalize();
        double overlap = getCircleOverlap(otherCircle);
        if(overlap != 0.0)
        {
            move(dir.multiply(-overlap));
            
            // Listener auslösen
            if(_listener != null)
            {
                _listener.onResolution(this, otherCircle);
            }
        }
    }
    
    /**
     * Gibt den Schnitt dieses CircleColliders mit einer Linie
     * @param otherLine Linie, mit der der Schnitt berechnet werden soll
     * @return Ergebnis des Schnitts
     */
    public LineCircleIntersection getLineIntersection(LineCollider otherLine)
    {
        return otherLine.getCircleIntersection(this);
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
