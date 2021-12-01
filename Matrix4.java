
/**
 * Vierdimensionale Matrix mit double-Präzision
 * 
 * @author Lasse Huber-Saffer
 * @version 01.12.2021
 */
public class Matrix4
{
    // Zellen der Matrix
    private double[][] _values;
    
    /**
     * Parameterloser Konstruktor einer Identitätsmatrix
     */
    public Matrix4()
    {
        _values = new double[4][4];
        for(int i = 0; i < 4; i++)
        {
            _values[i][i] = 1.0;
        }
    }
    
    /*public Matrix4(double a, double b, double c, double d, double e, double f, double g, double h, double i)
    {
        _values = new double[4][4];
        _values[0][0]=a;_values[1][0]=b;_values[2][0]=c;
        _values[0][1]=d;_values[1][1]=e;_values[2][1]=f;
        _values[0][2]=g;_values[1][2]=h;_values[2][2]=i;
    }*/
    
    /**
     * Konstruktor für eine Matrix, in der alle Zellen mit dem selben Wert gefüllt sind
     * @param initialValue Wert, auf den alle Zellen initialisiert werden
     */
    public Matrix4(double initialValue)
    {
        _values = new double[4][4];
        for(int y = 0; y < 4; y++)
        {
            for(int x = 0; x < 4; x++)
            {
                _values[x][y] = initialValue;
            }
        }
    }
    
    /**
     * Kopie-Konstruktor
     * @param copy Die zu kopierende Matrix
     */
    public Matrix4(Matrix4 copy)
    {
        _values = new double[4][4];
        for(int y = 0; y < 4; y++)
        {
            for(int x = 0; x < 4; x++)
            {
                _values[x][y] = copy._values[x][y];
            }
        }
    }
    
    /**
     * Überprüft, ob ein gegebenes Koordinatenpaar (x, y) in der Matrix enthalten ist
     * @param x x-Stelle, die überprüft wird
     * @param y y-Stelle, die überprüft wird
     * @return Wahrheitswert der Aussage "Das Koordinatenpaar (x, y) liegt innerhalb der Matrix"
     */
    public boolean isInsideBounds(int x, int y)
    {
        return (x >= 0 && x <= 3 && y >= 0 && y <= 3);
    }
    
    /**
     * Gibt den Wert der Matrix an der Stelle (x, y) zurück
     * @param x x-Stelle, die abgefragt wird
     * @param y y-Stelle, die abgefragt wird
     * @return Wert der Matrix an der Stelle (x, y)
     */
    public double get(int x, int y)
    {
        if(!isInsideBounds(x,y))
        {
            throw new IllegalArgumentException("Matrix coordinates are out of bounds");
        }
        else
        {
            return _values[x][y];
        }
    }
    
    /**
     * Setzt den Wert der Matrix an der Stelle (x, y) zu einem Wert v
     * @param x x-Stelle, an der gesetzt wird
     * @param y y-Stelle, an der gesetzt wird
     * @param v Wert, der eingesetzt wird
     */
    public void set(int x, int y, double v)
    {
        if(!isInsideBounds(x,y))
        {
            throw new IllegalArgumentException("Matrix coordinates are out of bounds");
        }
        else
        {
            _values[x][y] = v;
        }
    }
    
    /**
     * Schreibt eine Repräsentation der Matrix in die Standardausgabe
     */
    public void print()
    {
        for(int y = 0; y < 4; y++)
        {
            System.out.println(get(0,y) + " | " + get(1,y) + " | " + get(2,y) + " | " + get(3,y));
        }
    }
    
    /**
     * Addiert eine Matrix zu dieser Matrix
     * @param v Matrix, die zu der aktuellen Matrix addiert wird
     * @return Ergebnismatrix der Addition
     */
    public Matrix4 add(Matrix4 v)
    {
        Matrix4 result = new Matrix4(this);
        
        for(int y = 0; y < 4; y++)
        {
            for(int x = 0; x < 4; x++)
            {
                result.set(x, y, result.get(x, y) + v.get(x, y));
            }
        }
        
        return result;
    }
    
    /**
     * Subtrahiert eine Matrix von dieser Matrix
     * @param v Matrix, die von der aktuellen Matrix subtrahiert wird
     * @return Ergebnismatrix der Subtraktion
     */
    public Matrix4 subtract(Matrix4 v)
    {
        Matrix4 result = new Matrix4(this);
        
        for(int y = 0; y < 4; y++)
        {
            for(int x = 0; x < 4; x++)
            {
                result.set(x, y, result.get(x, y) - v.get(x, y));
            }
        }
        
        return result;
    }
    
    /**
     * Multipliziert die Matrix mit einem Skalar
     * @param v Der skalare Faktor
     * @return Ergebnismatrix der skalaren Multiplikation
     */
    public Matrix4 multiply(double v)
    {
        Matrix4 result = new Matrix4(this);
        
        for(int y = 0; y < 4; y++)
        {
            for(int x = 0; x < 4; x++)
            {
                result.set(x, y, result.get(x, y) * v);
            }
        }
        
        return result;
    }
    
    /**
     * Führt eine Matrixmultiplikation dieser Matrix mit einer anderen Matrix durch
     * @param v Matrix, mit der multipliziert wird
     * @return Ergebnismatrix der Matrixmultiplikation
     */
    public Matrix4 multiply(Matrix4 v)
    {
        Matrix4 result = new Matrix4(this);
        
        for(int y = 0; y < 4; y++)
        {
            for(int x = 0; x < 4; x++)
            {
                double cellValue = 0.0;
                for(int i = 0; i < 4; i++)
                {
                    cellValue += get(i, y) * v.get(x, i);
                }
                result.set(x, y, cellValue);
            }
        }
        
        return result;        
    }
    
    /**
     * Multipliziert die Matrix mit einem Vector4
     * @param v Vektor, der mit der Matrix multipliziert wird
     * @return Vektor, der als Ergebnis der multiplikation herauskommt
     */
    public Vector4 multiply(Vector4 v)
    {
        Vector4 result = new Vector4();
        
        result.setX(get(0,0)*v.getX() + get(1,0)*v.getY() + get(2,0)*v.getZ() + get(3,0)*v.getW());
        System.out.println((get(0,0)*v.getX()) +" + "+ (get(1,0)*v.getY()) +" + "+ (get(2,0)*v.getZ()) +" + "+ (get(3,0)*v.getW()));
        result.setY(get(0,1)*v.getX() + get(1,1)*v.getY() + get(2,1)*v.getZ() + get(3,1)*v.getW());
        result.setZ(get(0,2)*v.getX() + get(1,2)*v.getY() + get(2,2)*v.getZ() + get(3,2)*v.getW());
        result.setW(get(0,3)*v.getX() + get(1,3)*v.getY() + get(2,3)*v.getZ() + get(3,3)*v.getW());
        
        return result; 
    }
}
