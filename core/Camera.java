package core;

import maths.*;

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
    private Vector3 _up;
    private Vector3 _right;
    private double _pitch = 0.0;
    private double _yaw = 0.0;
    
    // Seitenverhältnis der Kamera
    private double _aspectRatio = 1.0;
    // Vertikales Sichtfeld der Kamera in Grad
    private double _fov = 45.0;
    
    // Clipping-Ebenen
    private double _near = 0.01;
    private double _far = 1000.0;
    
    // View- und Projektionsmatrix
    private Matrix4 _view;
    private Matrix4 _projection;
    
    // Interne Zustandsflags für die Nötigkeit der Neuberechnung der View- und Projectionmatrix sowie den nach rechts zeigenden Richtungsvektors
    private boolean _recalculateViewMatrix = true;
    private boolean _recalculateProjectionMatrix = true;
    private boolean _recalculateRightVector = true;

    /**
     * Parameterloser Konstruktor
     */
    public Camera()
    {
        this(1.0, 45.0);
    }
    
    /**
     * Konstruktor mit gegebenem Seitenverhältnis und Sichtfeld
     * @param aspectRatio Seitenverhältnis
     * @param fov vertikales Sichtfeld in Grad
     */
    public Camera(double aspectRatio, double fov)
    {
        this(new Vector3(), aspectRatio, fov);
    }
    
    /**
     * Konstruktor mit gegebener Position, Seitenverhältnis und Sichtfeld
     * @param position Position der Kamera
     * @param aspectRatio Seitenverhältnis
     * @param fov vertikales Sichtfeld in Grad
     */
    public Camera(Vector3 position, double aspectRatio, double fov)
    {
        this(position, aspectRatio, fov, 0.01, 1000.0);
    }
    
    /**
     * Konstruktor mit gegebener Position, Seitenverhältnis, Sichtfeld und Clipping-Ebenen
     * @param position Position der Kamera
     * @param aspectRatio Seitenverhältnis
     * @param fov vertikales Sichtfeld in Grad
     * @param near Position der Nah-Ebene des Frustums
     * @param far Position der Fern-Ebene des Frustums
     */
    public Camera(Vector3 position, double aspectRatio, double fov, double near, double far)
    {
        _position = new Vector3(position);
        _up = new Vector3(0.0, 1.0, 0.0);
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
        
        _recalculateViewMatrix = true;
    }
    
    /**
     * Dreht die Kamera entlang der Gierachse
     * @param theta Winkel der Rotation in Grad
     */
    public void rotateYaw(double theta)
    {
        _yaw += theta;
        
        _recalculateViewMatrix = true;
        _recalculateRightVector = true;
    }
    
    /**
     * Dreht die Kamera entlang der Nickachse
     * @param theta Winkel der Rotation in Grad
     */
    public void rotatePitch(double theta)
    {
        _pitch += theta;
        
        _recalculateViewMatrix = true;
        _recalculateRightVector = true;
    }
    
    /**
     * Gibt die Viewmatrix zurück und berechnet sie gegebenenfalls neu
     * @return Viewmatrix
     */
    public Matrix4 getViewMatrix()
    {
        if(_recalculateViewMatrix)
        {
            // Recalculate View Matrix
            _view = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0.0, 1.0, 0.0), -_yaw).multiply(MatrixGenerator.generateTranslationMatrix(_position.invert()));
            //_view = MatrixGenerator.generateTranslationMatrix(_position.invert());
            //_view = MatrixGenerator.generateLookAtMatrix(_position.add(getDirection()), _position, new Vector3(0.0, 1.0, 0.0));            
            _recalculateViewMatrix = false;
        }
        
        return _view;
    }
    
    /**
     * Gibt die Projektionsmatrix zurück und berechnet sie gegebenenfalls neu
     * @return Projektionsmatrix
     */
    public Matrix4 getProjectionMatrix()
    {
        if(_recalculateProjectionMatrix)
        {
            _projection = MatrixGenerator.generatePerspectiveProjectionMatrix(_near, _far, _aspectRatio, _fov);
            _recalculateProjectionMatrix = false;
        }
        
        return _projection;
    }
    
    /**
     * Gibt die Position der Kamera zurück
     * @return Position der Kamera
     */
    public Vector3 getPosition()
    {
        return new Vector3(_position);
    }
    
    /**
     * Gibt die Zeigerichtung der Kamera zurück
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
     * Gibt den von der Kamera aus nach oben zeigenden Richtungsvektor zurück
     * @return Richtungsvektor nach oben
     */
    public Vector3 getUp()
    {
        return _up;
    }
    
    /**
     * Gibt den von der Kamera aus nach rechts zeigenden Richtungsvektor zurück
     * @return Richtungsvektor nach rechts
     */
    public Vector3 getRight()
    {
        if(_recalculateRightVector)
        {
            // Recalculate right vector
            _right = getDirection().cross(_up).normalize();
            _recalculateRightVector = false;
        }
        return _right;
    }
    
    /**
     * Gibt den Nickwinkel der Kamera zurück
     * @return Nickwinkel
     */
    public double getPitch()
    {
        return _pitch;
    }
    
    /**
     * Gibt den Gierungswinkel der Kamera zurück
     * @return Gierungswinkel
     */
    public double getYaw()
    {
        return _yaw;
    }
    
    /**
     * Gibt das Seitenverhältnis der Kamera zurück
     * @return Seitenverhältnis
     */
    public double getAspectRatio()
    {
        return _aspectRatio;
    }
    
    /**
     * Gibt das vertikale Sichtfeld in Grad zurück
     * @return vertikales Sichtfeld in Grad
     */
    public double getFov()
    {
        return _fov;
    }
    
    /**
     * Gibt die Position der Nah-Ebene des Frustums zurück
     * @return Position der Nah-Ebene des Frustums
     */
    public double getNear()
    {
        return _near;
    }
    
    /**
     * Gibt die Position der Fern-Ebene des Frustums zurück
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
        
        _recalculateViewMatrix = true;
    }
    
    /**
     * Setzt den Nickwinkel der Kamera
     * @param pitch Nickwinkel
     */
    public void setPitch(double pitch)
    {
        _pitch = pitch;
        
        _recalculateViewMatrix = true;
        _recalculateRightVector = true;
    }
    
    /**
     * Setzt den Gierungswinkel der Kamera
     * @param yaw Gierungswinkel
     */
    public void setYaw(double yaw)
    {
        _yaw = yaw;
        
        _recalculateViewMatrix = true;
        _recalculateRightVector = true;
    }
    
    /**
     * Setzt das Seitenverhältnis der Kamera
     * @param aspectRatio Seitenverhältnis
     */
    public void setAspectRatio(double aspectRatio)
    {
        _aspectRatio = aspectRatio;
        
        _recalculateProjectionMatrix = true;
    }
    
    /**
     * Setzt das vertikale Sichtfeld der Kamera in Grad
     * @param fov vertikales Sichtfeld der Kamera in Grad
     */
    public void setFov(double fov)
    {
        _fov = fov;
        
        _recalculateProjectionMatrix = true;
    }
    
    /**
     * Setzt die Position der Nah-Ebene des Frustums
     * @param near Nah-Ebene des Frustums
     */
    public void setNear(double near)
    {
        _near = near;
        
        _recalculateProjectionMatrix = true;
    }
    
    /**
     * Setzt die Position der Fern-Ebene des Frustums
     * @param far Fern-Ebene des Frustums
     */
    public void setFar(double far)
    {
        _far = far;
        
        _recalculateProjectionMatrix = true;
    }
}
