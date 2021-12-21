
/**
 * Mögliche Typen des Schnitts eines Kreises und einer Linie
 * 
 * @author  Lasse Huber-Saffer
 * @version 21.12.2021
 */
public enum LineCircleIntersectionType
{
    // Die beiden Collider schneiden sich nicht
    NONE,
    
    // Die Collider berühren sich in einem Punkt
    TANGENT,
    
    // Die Collider schneiden sich in einem Punkt
    HALF_INTERSECTION,
    
    // Die Collider schneiden sich in zwei Punkten
    FULL_INTERSECTION
}
