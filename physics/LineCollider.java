package physics;

import maths.*;

/**
 * Linien-Collider zwischen zwei Punkten
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
 */
public class LineCollider implements ICollider
{
    private PhysicsLayer _layer;
    private Vector2 _pos1;
    private Vector2 _pos2;
    private ICollisionListener _listener;

    /**
     * Konstruktor mit zwei Punkten
     * @param pos1 erster Punkt der Linie
     * @param pos2 zweiter Punkt der Linie
     * @param layer Physik-Ebene auf der dieser Collider agiert
     */
    public LineCollider(Vector2 pos1, Vector2 pos2, PhysicsLayer layer)
    {
        _pos1 = new Vector2(pos1);
        _pos2 = new Vector2(pos2);
        _layer = layer;
        _listener = null;
    }
    
    /**
     * Konstruktor mit Punkt, Richtungsvektor und Länge
     * @param pos Startpunkt
     * @param dir Richtungsvektor
     * @param length Länge des Linie
     * @param layer Physik-Eben auf der dieser Collider agiert
     */
    public LineCollider(Vector2 pos, Vector2 dir, double length, PhysicsLayer layer)
    {
        _pos1 = pos;
        _pos2 = pos.add(dir.normalize().multiply(length));
        _layer = layer;
        _listener = null;
    }
    
    /**
     * @see ICollider#intersect()
     */
    public boolean intersects(ICollider other)
    {
        boolean didIntersect = false;
        
        if(other instanceof LineCollider)
        {
            LineCollider otherLine = (LineCollider)other;
            Vector2 a = _pos1, b = _pos2, c = otherLine.getFirstPoint(), d = otherLine.getSecondPoint();
            
            double h = computeH(a, b, c, d);
            double g = computeH(c, d, a, b);
            
            // Skalare g und h müssen beide innerhalb von [0.0, 1.0] liegen, um einen Schnitt zu haben
            didIntersect = h >= 0.0 && h <= 1.0 && g >= 0.0 && g <= 1.0;
        }
        else if(other instanceof CircleCollider)
        {
            CircleCollider otherCircle = (CircleCollider)other;
            double dist = getPointDistance(otherCircle.getPosition());
            didIntersect = dist <= otherCircle.getRadius();
        }
        // Compount-Linien-Kollision wird über Compound erkannt
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
     * Berechnet den Skalar h gemäß des Algorithmus nach https://stackoverflow.com/a/563275/13332329
     * @param a 1. Punkt der ersten Linie
     * @param b 2. Punkt der ersten Linie
     * @param c 1. Punkt der zweiten Linie
     * @param d 2. Punkt der zweiten Linie
     * @return h-Wert
     */
    private double computeH(Vector2 a, Vector2 b, Vector2 c, Vector2 d)
    {
        Vector2 e = b.subtract(a), f = d.subtract(c);
        Vector2 p = new Vector2(-e.getY(), e.getX());
        double h = (a.subtract(c).dot(p)) / (f.dot(p));
        return h;
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
     * Berechnet den Schnittpunkt dieses LineColliders mit einem anderen
     * @param otherLine zweiter LineCollider, zu dem der Schnittpunkt berechnet werden soll
     * @return null, wenn es keinen Schnittpunkt gibt, sonst den Schnittpunkt
     */
    public Vector2 getLineIntersection(LineCollider otherLine)
    {
        if(!intersects(otherLine))
        {
            return null;
        }
        else
        {
            Vector2 a = _pos1, b = _pos2, c = otherLine.getFirstPoint(), d = otherLine.getSecondPoint();
            Vector2 e = b.subtract(a), f = d.subtract(c);
            Vector2 p = new Vector2(-e.getY(), e.getX());
            double h = (a.subtract(c).dot(p)) / (f.dot(p));
            return new Vector2(c.getX() + h * f.getX(), c.getY() + h * f.getY());
        }
    }
    
    /**
     * Gibt den Schnitt dieses LineColliders mit einem Kreis zurück
     * @param otherCircle Kreis, mit dem der Schnitt berechnet werden soll
     * @return Ergebnis des Schnitts
     */
    public LineCircleIntersection getCircleIntersection(CircleCollider otherCircle)
    {
        LineCircleIntersection result = null;
        
        Vector2 a = _pos1, b = _pos2, c = otherCircle.getPosition();
        double length_ab = b.subtract(a).getLength();
        double r = otherCircle.getRadius();
        Vector2 dir = b.subtract(a).normalize();
        double t = dir.getX() * (c.getX() - a.getX()) + dir.getY() * (c.getY() - a.getY());
        
        // Nächster Punkt der Linie zum Kreismittelpunkt
        Vector2 e = a.add(dir.multiply(t));
        double length_ec = e.subtract(c).getLength();
        
        // Linie schneidet Kreis
        if(length_ec < r)
        {
            double dt = Math.sqrt(Math.pow(r, 2) - Math.pow(length_ec, 2));
            
            Vector2 firstIntersection = dir.multiply(t-dt).add(a);
            Vector2 secondIntersection = dir.multiply(t+dt).add(a);
            
            // Unterscheidung zwischen HALF_INTERSECTION und FULL_INTERSECTION
            // Beide Punkte liegen auf der Linie
            if(t - dt >= 0.0 && t + dt <= length_ab)
            {
                return new LineCircleIntersection(LineCircleIntersectionType.FULL_INTERSECTION, firstIntersection, secondIntersection);
            }
            // Erster Punkt liegt nicht auf der Linie
            else if(t - dt < 0.0)
            {
                return new LineCircleIntersection(LineCircleIntersectionType.HALF_INTERSECTION, secondIntersection, null);
            }
            // Zweiter Punkt liegt nicht auf der Linie
            else if(t + dt > length_ab)
            {
                return new LineCircleIntersection(LineCircleIntersectionType.HALF_INTERSECTION, firstIntersection, null);
            }
        }
        // Linie ist Tangente
        else if (length_ec == r)
        {
            result = new LineCircleIntersection(LineCircleIntersectionType.TANGENT, new Vector2(e), new Vector2(e));
        }
        else
        {
            result = new LineCircleIntersection(LineCircleIntersectionType.NONE, null, null);
        }
        
        return result;
    }
    
    /**
     * Gibt Punkt auf der Linie zurück, der den geringsten Abstand zu einem anderen gegebenen Punkt hat
     * @param point zweiter Punkt
     * @return Punkt auf der Linie, der dem gegebenen Punkt am nächsten ist.
     */
    public Vector2 getClosestPoint(Vector2 point)
    {
        Vector2 a = _pos1, b = _pos2, c = point;
        Vector2 ab = b.subtract(a);
        double length_ab = ab.getLength();
        Vector2 dir = ab.normalize();
        
        double t = dir.getX() * (c.getX() - a.getX()) + dir.getY() * (c.getY() - a.getY());
        if(t < 0.0)
        {
            t = 0.0;
        }
        else if(t > length_ab)
        {
            t = length_ab;
        }
        
        Vector2 e = a.add(dir.multiply(t));
        return e;
    }
    
    /**
     * Gibt den Abstand eines Punktes zum LineCollider zurück
     * @param point Punkt, dessen Abstand zur Linie berechnet werden soll
     * @return Abstand des Punktes zur Linie (>= 0.0)
     */
    public double getPointDistance(Vector2 point)
    {
        return getClosestPoint(point).subtract(point).getLength();
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
     * Gibt die Position des ersten Punktes der Linie zurück
     * @return Position des ersten Punktes der Linie
     */
    public Vector2 getFirstPoint()
    {
        return new Vector2(_pos1);
    }
    
    /**
     * Gibt die Position des zweiten Punktes der Linie zurück
     * @return Position des zweiten Punktes der Linie
     */
    public Vector2 getSecondPoint()
    {
        return new Vector2(_pos2);
    }
    
    /**
     * Setzt die Position des ersten Punktes der Linie
     * @param Position des ersten Punktes der Linie
     */
    public void setFirstPoint(Vector2 pos)
    {
        _pos1 = new Vector2(pos);
    }
    
    /**
     * Setzt die Position des zweiten Punktes der Linie
     * @param Position des zweiten Punktes der Linie
     */
    public void setSecondPoint(Vector2 pos)
    {
        _pos2 = new Vector2(pos);
    }
}
