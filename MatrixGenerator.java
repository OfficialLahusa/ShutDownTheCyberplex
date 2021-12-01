
/**
 * Generiert verschiedene Transformationsmatrizen für dreidimensionale Geometrie
 * 
 * @author Lasse Huber-Saffer 
 * @version 01.12.2021
 */
public class MatrixGenerator
{

    /**
     * Konstruktor für Objekte der Klasse MatrixGenerator
     */
    public MatrixGenerator()
    {

    }
    
    public static Matrix4 translate(Matrix4 v, double dx, double dy, double dz)
    {
        return v.multiply(generateTranslationMatrix(dx, dy, dz));
    }
    
    public static Matrix4 scale(Matrix4 v, double sx, double sy, double sz)
    {
        return v.multiply(generateScaleMatrix(sx, sy, sz));
    }
    
    public static Matrix4 rotateOnAxis(Matrix4 v, Vector3 axis, double theta)
    {
        return v.multiply(generateAxialRotationMatrix(axis, theta));
    }
    
    public static Matrix4 generateTranslationMatrix(double dx, double dy, double dz)
    {
        Matrix4 result = new Matrix4();
        
        result.set(3, 0, dx);
        result.set(3, 1, dy);
        result.set(3, 2, dz);
        
        return result;
    }
    
    public static Matrix4 generateScaleMatrix(double sx, double sy, double sz)
    {
        Matrix4 result = new Matrix4();
        
        result.set(0, 0, sx);
        result.set(1, 1, sy);
        result.set(2, 2, sz);
        
        return result;
    }
    
    public static Matrix4 generateAxialRotationMatrix(Vector3 axis, double theta)
    {
        Matrix4 result = new Matrix4();
        theta = Math.toRadians(theta);
        double sinT = Math.sin(theta);
        double cosT = Math.cos(theta);
        double oneMinusCosT = 1 - cosT;
        double x = axis.getX();
        double y = axis.getY();
        double z = axis.getZ();
        
        result.set(0, 0, cosT + Math.pow(x, 2) * oneMinusCosT);
        result.set(1, 0, x * y * oneMinusCosT - z * sinT);
        result.set(2, 0, x * z * oneMinusCosT + y * sinT);
        
        result.set(0, 1, y * x * oneMinusCosT + z * sinT);
        result.set(1, 1, cosT + Math.pow(y, 2) * oneMinusCosT);
        result.set(2, 1, y * z * oneMinusCosT - x * sinT);
        
        result.set(0, 2, z * x * oneMinusCosT - y * sinT);
        result.set(1, 2, z * y * oneMinusCosT + x * sinT);
        result.set(2, 2, cosT + Math.pow(z, 2) * oneMinusCosT);
        
        return result;
    }
}
