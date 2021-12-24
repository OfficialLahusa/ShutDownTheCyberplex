
/**
 * Interface für jegliche GameObjects, die gezeichnet und gefärbt werden können, sowie Getter für ihre Position, Rotation, Skalierung und Farbe bereitstellen 
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
 */

public interface IGameObject
{
    /**
     * Zeichnet das GameObject mit dem gegebenen Renderer aus der Perspektive der gegebenen Kamera
     * @param renderer Renderer, der zum Zeichnen benutzt wird
     * @param camera Kamera, aus deren Perspektive gerendert wird
     */
    public void draw(Renderer renderer, Camera camera);
    
    /**
     * Updated das GameObject
     * @param deltaTime Deltazeit des vorigen Frames
     * @param runTime Laufzeit des Programms
     * @param cameraPosition Position der Kamera im dreidimensionalen Raum
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition);
    
    /**
     * Gibt den Collider des GameObjects zurück, falls es einen hat
     * @return (optional) Collider des GameObjects
     */
    public ICollider getCollider();
    
    /**
     * Gibt die Position zurück
     * @return Position
     */
    public Vector3 getPosition();
    
    /**
     * Gibt die Rotation zurück
     * @return Rotation
     */
    public Vector3 getRotation();
    
    /**
     * Gibt die Skalierung zurück
     * @return Skalierung
     */
    public Vector3 getScale();
    
    /**
     * Gibt die Farbe zurück
     * @return Farbe des Objekts
     */
    public String getColor();
    
    /**
     * Setzt die Farbe
     * @param color Farbe des Objekts
     */
    public void setColor(String color);
}
