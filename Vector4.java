
/**
 * Vierdimensionaler Vektor mit double-Präzision
 * 
 * @author Lasse Huber-Saffer
 * @version 01.12.2021
 */
public class Vector4
{
    // x-Komponente des Vektors
    private double _x;
    // y-Komponente des Vektors
    private double _y;
    // z-Komponente des Vektors
    private double _z;
    // w-Komponente des Vektors
    private double _w;
    
    /**
     * Parameterloser Konstruktor zu einem Vektor4(0.0, 0.0, 0.0, 0.0)
     */
    public Vector4()
    {
        _x = 0.0;
        _y = 0.0;
        _z = 0.0;
        _w = 0.0;
    }
    
    /**
     * Kopie-Konstruktor
     * @param copy zu kopierender Vektor
     */
    public Vector4(Vector4 copy)
    {
        _x = copy._x;
        _y = copy._y;
        _z = copy._z;
        _w = copy._w;
    }
    
    /**
     * Konstruktor mit zwei Parametern für jeweilige Komponenten
     * @param x x-Komponente des Vektors
     * @param y y-Komponente des Vektors
     * @param z z-Komponente des Vektors
     * @param w w-Komponente des Vektors
     */
    public Vector4(double x, double y, double z, double w)
    {
        _x = x;
        _y = y;
        _z = z;
        _w = w;
    }
    
    /**
     * Konstruktor für einen Vektor4 aus einem Vektor3 und einer w-Komponente. Die x-, y- und z-Komponente des Vektors werden in den Vektor4 übernommen.
     * @param v Vektor, der zu einem Vektor4 ergänzt werden soll
     * @param w w-Komponente des Vektors
     */
    public Vector4(Vector3 v, double w)
    {
        _x = v.getX();
        _y = v.getY();
        _z = v.getZ();
        _w = w;
    }
    
    /**
     * Konstruktor für einen Vektor4 aus einem Vektor2 und einer z-, sowie w-Komponente. Die x- und y-Komponente des Vektors werden in den Vektor4 übernommen.
     * @param v Vektor, der zu einem Vektor4 ergänzt werden soll
     * @param z z-Komponente des Vektors
     * @param w w-Komponente des Vektors
     */
    public Vector4(Vector2 v, double z, double w)
    {
        _x = v.getX();
        _y = v.getY();
        _z = z;
        _w = w;
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
     * Gibt die z-Komponente des Vektors zurück
     * @return z-Komponente des Vektors
     */
    public double getZ()
    {
        return _z;
    }
    
    /**
     * Gibt die w-Komponente des Vektors zurück
     * @return w-Komponente des Vektors
     */
    public double getW()
    {
        return _w;
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
     * Setzt die z-Komponente des Vektors zu einem bestimmten Wert
     * @param z Wert, zu dem die z-Komponente des Vektors gesetzt wird
     */
    public void setZ(double z)
    {
        _z = z;
    }
    
    /**
     * Setzt die w-Komponente des Vektors zu einem bestimmten Wert
     * @param w Wert, zu dem die w-Komponente des Vektors gesetzt wird
     */
    public void setW(double w)
    {
        _w = w;
    }
    
    /**
     * Gibt die Länge des Vektors zurück
     * @return Länge des Vektors
     */
    public double getLength()
    {
        return Math.sqrt(_x*_x+_y*_y+_z*_z+_w*_w);
    }
    
    /**
     * Normalisiert den Vektor zu einem Einheitsvektor (Länge = 1.0)
     * @return Normalisierte Version des Vektors
     */
    public Vector4 normalize()
    {
        double len = getLength();
        if(len < 0.0) 
        {
            return new Vector4();
        }
        else
        {
            return new Vector4(_x/len, _y/len, _z/len, _w/len);
        }
    }
    
    /**
     * Addiert einen Skalar zu dem Vektor
     * @param v skalarer Wert, der addiert wird
     * @return Vektor, der bei der Addition entsteht
     */
    public Vector4 add(double v)
    {
        return new Vector4(_x + v, _y + v, _z + v, _w + v);
    }
    
    /**
     * Subtrahiert einen Skalar von dem Vektor
     * @param v skalarer Wert, der subtrahiert wird
     * @return Vektor, der bei der Subtraktion entsteht
     */
    public Vector4 subtract(double v)
    {
        return new Vector4(_x - v, _y - v, _z - v, _w - v);
    }
    
    /**
     * Multipliziert den Vektor mit einem Skalar
     * @param v skalarer Faktor
     * @return Vektor, der bei der Multiplikation entsteht
     */
    public Vector4 multiply(double v)
    {
        return new Vector4(_x * v, _y * v, _z * v, _w * v);
    }
    
    /**
     * Dividiert den Vektor durch einen Skalar
     * @param v skalarer Divisor (!= 0.0)
     * @return Vektor, der bei der Division entsteht
     */
    public Vector4 divide(double v)
    {
        if(v == 0.0)
        {
            throw new IllegalArgumentException("v is 0.0, cannot divide vector by zero");
        }
        else
        {
            return new Vector4(_x / v, _y / v, _z / v, _w / v);
        }
    }
    
    /**
     * Invertiert den Vektor / kehrt den Vektor um
     * @return Inverses des Vektors
     */
    public Vector4 invert()
    {
        return new Vector4(-_x, -_y, -_z, -_w);
    }
    
    /**
     * Addiert zwei Vektoren
     * @param v zweiter Summand bei der Vektoraddition
     * @return Summe der beiden Vektoren
     */
    public Vector4 add(Vector4 v)
    {
        return new Vector4(_x + v.getX(), _y + v.getY(), _z + v.getZ(), _w + v.getW());
    }
    
    /**
     * Subtrahiert zwei Vektoren
     * @param v Subtrahent bei der Vektorsubtraktion
     * @return Differenz der beiden Vektoren
     */
    public Vector4 subtract(Vector4 v)
    {
        return new Vector4(_x - v.getX(), _y - v.getY(), _z - v.getZ(), _w - v.getW());
    }
    
    /**
     * Skalarprodukt/Punktprodukt zweier Vektoren
     * @param v zweiter Vektor, mit dem das Skalarprodukt gebildet wird
     * @return skalares Ergebnis des Produkts
     */
    public double dot(Vector4 v)
    {
        return _x * v.getX() + _y * v.getY() + _z * v.getZ() + _w * v.getW();
    }
    
    /**
     * Prüft die Orthogonalität zweier Vektoren zueinander
     * @param v Vektor, zu dem die Orthogonalität geprüft werden soll
     * @return Wahrheitswert der Aussage "dieser Vektor ist orthogonal zu v"
     */
    public boolean isOrtogonalTo(Vector4 v)
    {
        return Math.abs(dot(v))<0.001;
    }
    
    /**
     * Gibt den Winkel zwischen zwei Vektoren zurück
     * @param v Vektor, zu dem der Winkel berechnet wird
     * @return Winkel zwischen den beiden Vektoren
     */
    public double getAngleBetween(Vector4 v)
    {
        return Math.toDegrees(Math.acos(dot(v)/(getLength()*v.getLength())));
    }
}
