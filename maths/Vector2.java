package maths;

/**
 * Zweidimensionaler Vektor mit double-Präzision
 * 
 * @author Lasse Huber-Saffer
 * @version 01.12.2021
 */
public class Vector2
{
    // x-Komponente des Vektors
    private double _x;
    // y-Komponente des Vektors
    private double _y;

    /**
     * Parameterloser Konstruktor zu einem Vektor2(0.0, 0.0)
     */
    public Vector2()
    {
        _x = 0.0;
        _y = 0.0;
    }
    
    /**
     * Kopie-Konstruktor
     * @param copy zu kopierender Vektor
     */
    public Vector2(Vector2 copy)
    {
        _x = copy._x;
        _y = copy._y;
    }
    
    /**
     * Konstruktor mit zwei Parametern für jeweilige Komponenten
     * @param x x-Komponente des Vektors
     * @param y y-Komponente des Vektors
     */
    public Vector2(double x, double y)
    {
        _x = x;
        _y = y;
    }

    /**
     * Gibt die x-Komponente des Vektors zurück
     * @return x-Komponente des Vektors
     */
    public double getX()
    {
        return _x;
    }
    
    /**
     * Gibt die y-Komponente des Vektors zurück
     * @return y-Komponente des Vektors
     */
    public double getY()
    {
        return _y;
    }
    
    /**
     * Setzt die x-Komponente des Vektors zu einem bestimmten Wert
     * @param x Wert, zu dem die x-Komponente des Vektors gesetzt wird
     */
    public void setX(double x)
    {
        _x = x;
    }
    
    /**
     * Setzt die y-Komponente des Vektors zu einem bestimmten Wert
     * @param y Wert, zu dem die y-Komponente des Vektors gesetzt wird
     */
    public void setY(double y)
    {
        _y = y;
    }
    
    /**
     * Gibt die Länge des Vektors zurück
     * @return Länge des Vektors
     */
    public double getLength()
    {
        return Math.sqrt(_x*_x+_y*_y);
    }
    
    /**
     * Normalisiert den Vektor zu einem Einheitsvektor (Länge = 1.0)
     * @return Normalisierte Version des Vektors
     */
    public Vector2 normalize()
    {
        double len = getLength();
        if(len <= 0.0) 
        {
            return new Vector2();
        }
        else
        {
            return new Vector2(_x/len, _y/len);
        }
    }
    
    /**
     * Addiert einen Skalar zu dem Vektor
     * @param v skalarer Wert, der addiert wird
     * @return Vektor, der bei der Addition entsteht
     */
    public Vector2 add(double v)
    {
        return new Vector2(_x + v, _y + v);
    }
    
    /**
     * Subtrahiert einen Skalar von dem Vektor
     * @param v skalarer Wert, der subtrahiert wird
     * @return Vektor, der bei der Subtraktion entsteht
     */
    public Vector2 subtract(double v)
    {
        return new Vector2(_x - v, _y - v);
    }
    
    /**
     * Multipliziert den Vektor mit einem Skalar
     * @param v skalarer Faktor
     * @return Vektor, der bei der Multiplikation entsteht
     */
    public Vector2 multiply(double v)
    {
        return new Vector2(_x * v, _y * v);
    }
    
    /**
     * Dividiert den Vektor durch einen Skalar
     * @param v skalarer Divisor (!= 0.0)
     * @return Vektor, der bei der Division entsteht
     */
    public Vector2 divide(double v)
    {
        if(v == 0.0)
        {
            throw new IllegalArgumentException("v is 0.0, cannot divide vector by zero");
        }
        else
        {
            return new Vector2(_x / v, _y / v);
        }
    }
    
    /**
     * Invertiert den Vektor / kehrt den Vektor um
     * @return Inverses des Vektors
     */
    public Vector2 invert()
    {
        return new Vector2(-_x, -_y);
    }
    
    /**
     * Addiert zwei Vektoren
     * @param v zweiter Summand bei der Vektoraddition
     * @return Summe der beiden Vektoren
     */
    public Vector2 add(Vector2 v)
    {
        return new Vector2(_x + v.getX(), _y + v.getY());
    }
    
    /**
     * Subtrahiert zwei Vektoren
     * @param v Subtrahent bei der Vektorsubtraktion
     * @return Differenz der beiden Vektoren
     */
    public Vector2 subtract(Vector2 v)
    {
        return new Vector2(_x - v.getX(), _y - v.getY());
    }
    
    /**
     * Skalarprodukt/Punktprodukt zweier Vektoren
     * @param v zweiter Vektor, mit dem das Skalarprodukt gebildet wird
     * @return skalares Ergebnis des Produkts
     */
    public double dot(Vector2 v)
    {
        return _x * v.getX() + _y * v.getY();
    }
    
    /**
     * Prüft die Orthogonalität zweier Vektoren zueinander
     * @param v Vektor, zu dem die Orthogonalität geprüft werden soll
     * @return Wahrheitswert der Aussage "dieser Vektor ist orthogonal zu v"
     */
    public boolean isOrtogonalTo(Vector2 v)
    {
        return Math.abs(dot(v))<0.001;
    }
    
    /**
     * Gibt den Winkel zwischen zwei Vektoren zurück
     * @param v Vektor, zu dem der Winkel berechnet wird
     * @return Winkel zwischen den beiden Vektoren
     */
    public double getAngleBetween(Vector2 v)
    {
        return Math.toDegrees(Math.acos(dot(v)/(getLength()*v.getLength())));
    }
    
    /**
     * Rotiert den Vektor einen bestimmen Winkel um den Koordinatenursprung
     * @param theta Drehungswinkel in Grad
     * @return Rotierte Version des Vektors
     */
    public Vector2 rotateAroundOrigin(double theta)
    {
        double angle = Math.toRadians(theta);
        return new Vector2(Math.cos(angle) * _x - Math.sin(angle) * _y, Math.sin(angle) * _x + Math.cos(angle) * _y);
    }
}
