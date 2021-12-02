
/**
 * Kamera im dreidimensionalen Raum
 * 
 * @author Lasse Huber-Saffer
 * @version 02.12.2021
 */
public class Camera
{
    // Position und Orientation der Kamera
    private Vector3 _position;
    private double _pitch = 0.0;
    private double _yaw = 0.0;
    
    // Seitenverh�ltnis der Kamera
    private double _aspectRatio = 1.0;
    // Vertikales Sichtfeld der Kamera in Grad
    private double _fov = 45.0;
    
    // Clipping-Ebenen
    private double _near = 0.01;
    private double _far = 1000.0;
    
    // View- und Projektionsmatrix
    private Matrix4 _view;
    private Matrix4 _projection;
    
    // Interne Zustandsflags f�r die N�tigkeit der Neuberechnung der View- und Projectionmatrix
    private boolean recalculateViewMatrix = true;
    private boolean recalculateProjectionMatrix = true;

    /**
     * Parameterloser Konstruktor
     */
    public Camera()
    {
        _position = new Vector3();
    }
    
    /**
     * Konstruktor mit gegebenem Seitenverh�ltnis und Sichtfeld
     * @param aspectRatio Seitenverh�ltnis
     * @param fov vertikales Sichtfeld in Grad
     */
    public Camera(double aspectRatio, double fov)
    {
        this(new Vector3(), aspectRatio, fov);
    }
    
    /**
     * Konstruktor mit gegebener Position, Seitenverh�ltnis und Sichtfeld
     * @param position Position der Kamera
     * @param aspectRatio Seitenverh�ltnis
     * @param fov vertikales Sichtfeld in Grad
     */
    public Camera(Vector3 position, double aspectRatio, double fov)
    {
        this(position, aspectRatio, fov, 0.01, 1000.0);
    }
    
    /**
     * Konstruktor mit gegebener Position, Seitenverh�ltnis, Sichtfeld und Clipping-Ebenen
     * @param position Position der Kamera
     * @param aspectRatio Seitenverh�ltnis
     * @param fov vertikales Sichtfeld in Grad
     * @param near Position der Nah-Ebene des Frustums
     * @param far Position der Fern-Ebene des Frustums
     */
    public Camera(Vector3 position, double aspectRatio, double fov, double near, double far)
    {
        _position = new Vector3(position);
        _aspectRatio = aspectRatio;
        _fov = fov;
        _near = near;
        _far = far;
    }
    
    /**
     * Bewegt die Kamera um einen Vektor
     * @param delta Bewegungsvektor
     */
    public void move(Vector3 delta)
    {
        _position = _position.add(delta);
        
        recalculateViewMatrix = true;
    }
    
    /**
     * Dreht die Kamera entlang der Gierachse
     * @param theta Winkel der Rotation in Grad
     */
    public void rotateYaw(double theta)
    {
        _yaw += theta;
        
        recalculateViewMatrix = true;
    }
    
    /**
     * Dreht die Kamera entlang der Nickachse
     * @param theta Winkel der Rotation in Grad
     */
    public void rotatePitch(double theta)
    {
        _pitch += theta;
        
        recalculateViewMatrix = true;
    }
    
    /**
     * Gibt die Viewmatrix zur�ck und berechnet sie gegebenenfalls neu
     * @return Viewmatrix
     */
    public Matrix4 getViewMatrix()
    {
        if(recalculateViewMatrix)
        {
            _view = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0.0, 1.0, 0.0), -_yaw).multiply(MatrixGenerator.generateTranslationMatrix(_position.invert()));
            //_view = MatrixGenerator.generateTranslationMatrix(_position.invert());
            //_view = MatrixGenerator.generateLookAtMatrix(_position.add(getDirection()), _position, new Vector3(0.0, 1.0, 0.0));
            recalculateViewMatrix = false;
        }
        
        return _view;
    }
    
    /**
     * Gibt die Projektionsmatrix zur�ck und berechnet sie gegebenenfalls neu
     * @return Projektionsmatrix
     */
    public Matrix4 getProjectionMatrix()
    {
        if(recalculateProjectionMatrix)
        {
            _projection = MatrixGenerator.generatePerspectiveProjectionMatrix(_near, _far, _aspectRatio, _fov);
            recalculateProjectionMatrix = false;
        }
        
        return _projection;
    }
    
    /**
     * Gibt die Position der Kamera zur�ck
     * @return Position der Kamera
     */
    public Vector3 getPosition()
    {
        return new Vector3(_position);
    }
    
    /**
     * Gibt die Zeigerichtung der Kamera zur�ck
     * @return Richtungsvektor der Zeigerichtung 
     */
    public Vector3 getDirection()
    {
        double x = Math.sin(Math.toRadians(_yaw));
        double y = Math.sin(Math.toRadians(_pitch))*Math.cos(Math.toRadians(_yaw));
        double z = Math.cos(Math.toRadians(_pitch))*Math.cos(Math.toRadians(_yaw));
        return new Vector3(x, y, z);
    }
    /**
     * Gibt den Nickwinkel der Kamera zur�ck
     * @return Nickwinkel
     */
    public double getPitch()
    {
        return _pitch;
    }
    
    /**
     * Gibt den Gierungswinkel der Kamera zur�ck
     * @return Gierungswinkel
     */
    public double getYaw()
    {
        return _yaw;
    }
    
    /**
     * Gibt das Seitenverh�ltnis der Kamera zur�ck
     * @return Seitenverh�ltnis
     */
    public double getAspectRatio()
    {
        return _aspectRatio;
    }
    
    /**
     * Gibt das vertikale Sichtfeld in Grad zur�ck
     * @return vertikales Sichtfeld in Grad
     */
    public double getFov()
    {
        return _fov;
    }
    
    /**
     * Gibt die Position der Nah-Ebene des Frustums zur�ck
     * @return Position der Nah-Ebene des Frustums
     */
    public double getNear()
    {
        return _near;
    }
    
    /**
     * Gibt die Position der Fern-Ebene des Frustums zur�ck
     * @return Position der Fern-Ebene des Frustums
     */
    public double getFar()
    {
        return _far;
    }
    
    /**
     * Setzt die Position der Kamera
     * @param pos Position der Kamera
     */
    public void setPosition(Vector3 pos)
    {
        _position = new Vector3(pos);
        
        recalculateViewMatrix = true;
    }
    
    /**
     * Setzt den Nickwinkel der Kamera
     * @param pitch Nickwinkel
     */
    public void setPitch(double pitch)
    {
        _pitch = pitch;
        
        recalculateViewMatrix = true;
    }
    
    /**
     * Setzt den Gierungswinkel der Kamera
     * @param yaw Gierungswinkel
     */
    public void setYaw(double yaw)
    {
        _yaw = yaw;
        
        recalculateViewMatrix = true;
    }
    
    /**
     * Setzt das Seitenverh�ltnis der Kamera
     * @param aspectRatio Seitenverh�ltnis
     */
    public void setAspectRatio(double aspectRatio)
    {
        _aspectRatio = aspectRatio;
        
        recalculateProjectionMatrix = true;
    }
    
    /**
     * Setzt das vertikale Sichtfeld der Kamera in Grad
     * @param fov vertikales Sichtfeld der Kamera in Grad
     */
    public void setFov(double fov)
    {
        _fov = fov;
        
        recalculateProjectionMatrix = true;
    }
    
    /**
     * Setzt die Position der Nah-Ebene des Frustums
     * @param near Nah-Ebene des Frustums
     */
    public void setNear(double near)
    {
        _near = near;
        
        recalculateProjectionMatrix = true;
    }
    
    /**
     * Setzt die Position der Fern-Ebene des Frustums
     * @param far Fern-Ebene des Frustums
     */
    public void setFar(double far)
    {
        _far = far;
        
        recalculateProjectionMatrix = true;
    }
}
