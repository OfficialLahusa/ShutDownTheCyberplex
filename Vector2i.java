
/**
 * Zweidimensionaler Vektor aus Integern
 * 
 * @author Lasse Huber-Saffer
 * @version 13.12.2021
 */
public class Vector2i
{
    // x-Komponente des Vektors
    private int _x;
    // y-Komponente des Vektors
    private int _y;

    /**
     * Parameterloser Konstruktor zu einem Vektor2i(0, 0)
     */
    public Vector2i()
    {
        _x = 0;
        _y = 0;
    }
    
    /**
     * Kopie-Konstruktor
     * @param copy zu kopierender Vektor
     */
    public Vector2i(Vector2i copy)
    {
        _x = copy._x;
        _y = copy._y;
    }
    
    /**
     * Konstruktor mit zwei Parametern für jeweilige Komponenten
     * @param x x-Komponente des Vektors
     * @param y y-Komponente des Vektors
     */
    public Vector2i(int x, int y)
    {
        _x = x;
        _y = y;
    }

    /**
     * Gibt die x-Komponente des Vektors zurück
     * @return x-Komponente des Vektors
     */
    public int getX()
    {
        return _x;
    }
    
    /**
     * Gibt die y-Komponente des Vektors zurück
     * @return y-Komponente des Vektors
     */
    public int getY()
    {
        return _y;
    }
    
    /**
     * Setzt die x-Komponente des Vektors zu einem bestimmten Wert
     * @param x Wert, zu dem die x-Komponente des Vektors gesetzt wird
     */
    public void setX(int x)
    {
        _x = x;
    }
    
    /**
     * Setzt die y-Komponente des Vektors zu einem bestimmten Wert
     * @param y Wert, zu dem die y-Komponente des Vektors gesetzt wird
     */
    public void setY(int y)
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
     * Addiert einen Skalar zu dem Vektor
     * @param v skalarer Wert, der addiert wird
     * @return Vektor, der bei der Addition entsteht
     */
    public Vector2i add(int v)
    {
        return new Vector2i(_x + v, _y + v);
    }
    
    /**
     * Subtrahiert einen Skalar von dem Vektor
     * @param v skalarer Wert, der subtrahiert wird
     * @return Vektor, der bei der Subtraktion entsteht
     */
    public Vector2i subtract(int v)
    {
        return new Vector2i(_x - v, _y - v);
    }
    
    /**
     * Multipliziert den Vektor mit einem Skalar
     * @param v skalarer Faktor
     * @return Vektor, der bei der Multiplikation entsteht
     */
    public Vector2i multiply(int v)
    {
        return new Vector2i(_x * v, _y * v);
    }
    
    /**
     * Dividiert den Vektor durch einen Skalar
     * @param v skalarer Divisor (!= 0.0)
     * @return Vektor, der bei der Division entsteht
     */
    public Vector2i divide(int v)
    {
        if(v == 0)
        {
            throw new IllegalArgumentException("v is 0, cannot divide vector by zero");
        }
        else
        {
            return new Vector2i(_x / v, _y / v);
        }
    }
    
    /**
     * Invertiert den Vektor / kehrt den Vektor um
     * @return Inverses des Vektors
     */
    public Vector2i invert()
    {
        return new Vector2i(-_x, -_y);
    }
    
    /**
     * Addiert zwei Vektoren
     * @param v zweiter Summand bei der Vektoraddition
     * @return Summe der beiden Vektoren
     */
    public Vector2i add(Vector2i v)
    {
        return new Vector2i(_x + v.getX(), _y + v.getY());
    }
    
    /**
     * Subtrahiert zwei Vektoren
     * @param v Subtrahent bei der Vektorsubtraktion
     * @return Differenz der beiden Vektoren
     */
    public Vector2i subtract(Vector2i v)
    {
        return new Vector2i(_x - v.getX(), _y - v.getY());
    }
    
    /**
     * Skalarprodukt/Punktprodukt zweier Vektoren
     * @param v zweiter Vektor, mit dem das Skalarprodukt gebildet wird
     * @return skalares Ergebnis des Produkts
     */
    public int dot(Vector2i v)
    {
        return _x * v.getX() + _y * v.getY();
    }
    
    /**
     * Prüft die Orthogonalität zweier Vektoren zueinander
     * @param v Vektor, zu dem die Orthogonalität geprüft werden soll
     * @return Wahrheitswert der Aussage "dieser Vektor ist orthogonal zu v"
     */
    public boolean isOrtogonalTo(Vector2i v)
    {
        return Math.abs(dot(v))==0;
    }
    
    /**
     * Gibt den Winkel zwischen zwei Vektoren zurück
     * @param v Vektor, zu dem der Winkel berechnet wird
     * @return Winkel zwischen den beiden Vektoren
     */
    public double getAngleBetween(Vector2i v)
    {
        return Math.toDegrees(Math.acos(dot(v)/(getLength()*v.getLength())));
    }
}
