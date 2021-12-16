/**
 * Linien-Collider zwischen zwei Punkten
 * 
 * @author Lasse Huber-Saffer
 * @version 16.12.2021
 */
public class LineCollider implements ICollider
{
    private PhysicsLayer _layer;
    private Vector2 _pos1;
    private Vector2 _pos2;

    /**
     * Konstruktor für Objekte der Klasse LineCollider
     * @param pos1 erster Punkt der Linie
     * @param pos2 zweiter Punkt der Linie
     * @param layer Physik-Ebene auf der dieser Collider agiert
     */
    public LineCollider(Vector2 pos1, Vector2 pos2, PhysicsLayer layer)
    {
        _pos1 = new Vector2(pos1);
        _pos2 = new Vector2(pos2);
        _layer = layer;
    }
    
    /**
     * @see ICollider#intersect()
     */
    public boolean intersects(ICollider other)
    {
        if(other instanceof LineCollider)
        {
            LineCollider otherLine = (LineCollider)other;
            
            Vector2 a = _pos1, b = _pos2, c = otherLine.getFirstPoint(), d = otherLine.getSecondPoint();
            Vector2 e = b.subtract(a), f = d.subtract(c);
            Vector2 p = new Vector2(-e.getY(), e.getX());
            double h = (a.subtract(c).dot(p)) / (f.dot(p));
            return h >= 0.0 && h <= 1.0;
        }
        else if(other instanceof CircleCollider)
        {
            CircleCollider otherCircle = (CircleCollider)other;
            double dist = getPointDistance(otherCircle.getPosition());
            return dist <= otherCircle.getRadius();
        }
        
        return false;
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
    
    public double getPointDistance(Vector2 point)
    {
        Vector2 a = _pos1, b = _pos2, c = point;
        Vector2 d = b.subtract(a).normalize();
        double t = d.getX() * (c.getX() - a.getX()) + d.getY() * (c.getY() - a.getY());
        Vector2 e = a.add(d.multiply(t));
        return e.subtract(c).getLength();
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
