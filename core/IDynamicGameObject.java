package core;

import maths.*;

/**
 * GameObject, das nach Erstellung bewegt werden kann
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
 */

public interface IDynamicGameObject extends IGameObject
{
    /**
     * Setzt die Position
     * @param position Position
     */
    public void setPosition(Vector3 position);
    
    /**
     * Setzt die Rotation
     * @param rotation Rotation
     */
    public void setRotation(Vector3 rotation);
    
    /**
     * Setzt die Skalierung
     * @param scale Skalierung
     */
    public void setScale(Vector3 scale);
    
    /**
     * Bewegt das Objekt um einen Vektor
     * @param delta Bewegungsvektor
     */
    public void move(Vector3 delta);
    
    /**
     * Rotiert das Objekt
     * @param rotation Vektor aus drei Rotationswinkeln in Grad
     */
    public void rotate(Vector3 rotation);
    
    /**
     * Skaliert das Object relativ zu seiner vorherigen Größe
     * @param scale Vektor der Skalierung
     */
    public void scale(Vector3 scale);
}
