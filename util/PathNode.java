package util;

import maths.*;

/**
 * Knotenpunkt w�hrend der Pfadberechnung
 * 
 * @author Lasse Huber-Saffer
 * @version 25.12.2021
 */
public class PathNode implements Comparable<PathNode>
{
    /**
     * (Optional) Vorheriger Knoten
     */
    private PathNode _previousNode;
    
    /**
     * Position der Node im Grid
     */
    private Vector2i _position;
    
    /**
     * Weg von diesem Knoten zum Startknoten
     */
    private double _g;
    
    /**
     * Heuristikwert (Gesch�tzte Distanz von diesem Knoten zum Ziel)
     */
    private double _h;
    
    /**
     * Vollst�ndiger Konstruktor
     * @param position Position des Knotens im Grid
     * @param g Weg von diesem Knoten zum Startknoten
     * @param h Heuristikwert (Gesch�tzte Distanz von diesem Knoten zum Ziel)
     * @param previousNode (Optional) vorhergehender Knoten
     */
    public PathNode(Vector2i position, double g, double h, PathNode previousNode)
    {
        if(position == null)
        {
            throw new IllegalArgumentException("position was null when constructing PathNode");
        }
        
        _previousNode = previousNode;
        _position = position;
        _g = g;
        _h = h;
    }
    
    /**
     * Vergleicht die Knoten anhand ihrer Gesamtkosten
     * @param other anderer Knoten
     * @return Ergebnis des Vergleichs
     */
    @Override
    public int compareTo(PathNode other)
    {
        return Double.compare(getF(), other.getF());
    }
    
    /**
     * Gibt die Gesamtkosten des Knotenpunkts zur�ck
     * Es gilt f = g + h
     * @return Gesamtkosten des Knotenpunkts
     */
    public double getF()
    {
        return _g + _h;
    }
    
    /**
     * Gibt den Weg von diesem Knoten zum Startknoten zur�ck
     * @return Weg von diesem Knoten zum Startknoten
     */
    public double getG()
    {
        return _g;
    }
    
    /**
     * Gibt den Heuristikwert (Gesch�tzte Distanz von diesem Knoten zum Ziel) zur�ck
     * @return Heuristikwert (Gesch�tzte Distanz von diesem Knoten zum Ziel)
     */
    public double getH()
    {
        return _h;
    }
    
    /**
     * Gibt die Position des Knotenpunktes im Grid zur�ck
     * @return Position des Knotenpunktes im Grid
     */
    public Vector2i getPosition()
    {
        return _position;
    }
    
    /**
     * Gibt den vorhergehenden Knoten zur�ck
     * @return vorhergehender Knoten, null, wenn es keinen gibt
     */
    public PathNode getPreviousNode()
    {
        return _previousNode;
    }
    
    /**
     * Setzt den Weg von diesem Knoten zum Startknoten
     * @param g Weg von diesem Knoten zum Startknoten
     */
    public void setG(double g)
    {
        _g = g;
    }
    
    /**
     * Setzt den Heuristikwert (Gesch�tzte Distanz von diesem Knoten zum Ziel)
     * @param Heuristikwert (Gesch�tzte Distanz von diesem Knoten zum Ziel)
     */
    public void setH(double h)
    {
        _h = h;
    }
    
    /**
     * Setzt den vorhergehenden Knoten
     * @param previousNode (Optional) vorhergehender Knoten
     */
    public void setPreviousNode(PathNode previousNode)
    {
        _previousNode = previousNode;
    }
}
