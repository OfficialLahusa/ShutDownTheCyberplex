
/**
 * Auswertung über den Schnitt eines Kreises mit einer Linie
 * 
 * @author Lasse Huber-Saffer
 * @version 17.12.2021
 */
public class LineCircleIntersection
{
    /**
     * Typ des Schnitts, der die Interpretation der Schnittpunkt-Felder bestimmt
     */
    public LineCircleIntersectionType type;
    /**
     * Erster Schnittpunkt.
     * NONE  -> null,
     * TANGENT, HALF_INTERSECTION, FULL_INTERSECTION -> erster Schnittpunkt
     */
    public Vector2 pos1;
    
    /**
     * Zweiter Schnittpunkt.
     * NONE, HALF_INTERSECTION  -> null,
     * TANGENT -> identisch zu pos1,
     * FULL_INTERSECTION -> zweiter Schnittpunkt
     */
    public Vector2 pos2;

    /**
     * Konstruktor für Objekte der Klasse LineCircleIntersection
     * @param type Art des Schnitts
     * @param pos1 erster Schnittpunkt
     * @param pos2 zweiter Schnittpunkt
     */
    public LineCircleIntersection(LineCircleIntersectionType type, Vector2 pos1, Vector2 pos2)
    {
        this.type = type;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }
}
