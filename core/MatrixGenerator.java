package core;

import maths.*;

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
    
    /**
     * Wendet eine Translation auf einer gegebenen Matrix an
     * @param v Matrix, auf der die Transformation angewandt wird
     * @param delta Vektor, um den die Matrix verschoben wird
     * @return Ergebnismatrix der Transformation
     */
    public static Matrix4 translate(Matrix4 v, Vector3 delta)
    {
        return generateTranslationMatrix(delta).multiply(v);
    }
    
    /**
     * Wendet eine Skalierung auf einer gegebenen Matrix an
     * @param v Matrix, auf der die Transformation angewandt wird
     * @param scale Vektor, um den die Matrix skaliert wird
     * @return Ergebnismatrix der Transformation
     */
    public static Matrix4 scale(Matrix4 v, Vector3 scale)
    {
        return generateScaleMatrix(scale).multiply(v);
    }
    
    /**
     * Wendet eine axiale Rotation auf einer gegebenen Matrix an
     * @param v Matrix, auf der die Transformation angewandt wird
     * @param axis Achse, um den die Matrix rotiert wird
     * @param theta Winkel, um den die Matrix entlang der Achse rotiert wird
     * @return Ergebnismatrix der Transformation
     */
    public static Matrix4 rotateOnAxis(Matrix4 v, Vector3 axis, double theta)
    {
        return generateAxialRotationMatrix(axis, theta).multiply(v);
    }
    
    /**
     * Generiert eine Translationsmatrix
     * @param delta Vektor der Translation
     * @return Translationsmatrix
     */
    public static Matrix4 generateTranslationMatrix(Vector3 delta)
    {
        Matrix4 result = new Matrix4();
        
        result.set(3, 0, delta.getX());
        result.set(3, 1, delta.getY());
        result.set(3, 2, delta.getZ());
        
        return result;
    }
    
    /**
     * Generiert eine Skalierungsmatrix
     * @param scale Vektor der Skalierung
     * @return Skalierungsmatrix
     */
    public static Matrix4 generateScaleMatrix(Vector3 scale)
    {
        Matrix4 result = new Matrix4();
        
        result.set(0, 0, scale.getX());
        result.set(1, 1, scale.getY());
        result.set(2, 2, scale.getZ());
        
        return result;
    }
    
    /**
     * Generiert eine Axialrotationsmatrix
     * @param axis Achse der Rotation
     * @param theta Winkel der Rotation
     * @return Axialrotationsmatrix
     */
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
    
    /**
     * Generiert eine perspektivische Projektionsmatrix
     * @param near Nahebene des Frustums
     * @param far Fernebene des Frustums
     * @param aspectRatio Seitenverhältnis der Kamera
     * @param fov Eulerwinkel des vertikalen Sichtfelds
     * @return perspektivische Projektionsmatrix
     */
    public static Matrix4 generatePerspectiveProjectionMatrix(double near, double far, double aspectRatio, double fov)
    {
        Matrix4 result = new Matrix4(0.0);
        
        double tanFovDivTwo = Math.tan(Math.toRadians(fov / 2.0));
        
        result.set(0, 0, 1.0 / (aspectRatio * tanFovDivTwo));
        result.set(1, 1, 1.0 / tanFovDivTwo);
        result.set(2, 2, far / (far - near));
        result.set(3, 2, (-far * near) / (far - near));
        result.set(2, 3, 1.0);
        
        return result;
    }
    
    // NOT WORKING YET
    public static Matrix4 generateLookAtMatrix(Vector3 position, Vector3 cameraTarget, Vector3 cameraUp)
    {
        Vector3 front = cameraTarget.subtract(position).normalize();
        Vector3 up = cameraUp.normalize();
        Vector3 right = front.cross(up).normalize();
        up = right.cross(front);
        
        Matrix4 result = new Matrix4();
        result.set(0, 0, right.getX());
        result.set(1, 0, right.getY());
        result.set(2, 0, right.getZ());
        
        result.set(0, 1, up.getX());
        result.set(1, 1, up.getY());
        result.set(2, 1, up.getZ());
        
        result.set(0, 2, -front.getX());
        result.set(1, 2, -front.getY());
        result.set(2, 2, -front.getZ());
        
        result.set(2, 0, -right.dot(position));
        result.set(2, 1, -up.dot(position));
        result.set(2, 2, front.dot(position));
        
        return result;
    }
    
    /**
     * Transformiert einen Vertex in Clip-Koordinaten in das Screenspace der TurtleWelt
     * @param vertex Vertex, der transformiert wird
     * @return Vertex im Screenspace
     */
    public static Vector4 viewportTransform(Vector4 vertex)
    {
        Vector4 result = new Vector4(vertex);
        
        if(result.getW() != 0.0)
        {
            result = result.divide(result.getW());
        }
        result = result.add(1.0);
        result.setX(result.getX() * (TurtleWelt.GLOBALEWELT.WIDTH / 2.0));
        result.setY(result.getY() * (TurtleWelt.GLOBALEWELT.HEIGHT / 2.0));            
        
        return result;
    }
}
