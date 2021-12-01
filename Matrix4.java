
/**
 * Vierdimensionale Matrix mit double-Präzision
 * 
 * @author Lasse Huber-Saffer
 * @version 01.12.2021
 */
public class Matrix4
{
    private double[][] _values;
    
    public Matrix4()
    {
        _values = new double[4][4];
        for(int i = 0; i < 4; i++)
        {
            _values[i][i] = 1.0;
        }
    }
    
    public Matrix4(Matrix4 copy)
    {
        for(int y = 0; y < 4; y++)
        {
            for(int x = 0; x < 4; x++)
            {
                _values[x][y] = copy.get(x,y);
            }
        }
    }
    
    public boolean isInsideBounds(int x, int y)
    {
        return (x >= 0 && x <= 3 && y >= 0 && y <= 3);
    }

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
    
    public void print()
    {
        for(int y = 0; y < 4; y++)
        {
            System.out.println(get(0,y) + " | " + get(1,y) + " | " + get(2,y) + " | " + get(3,y));
        }
    }
}
