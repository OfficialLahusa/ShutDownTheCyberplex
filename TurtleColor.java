
/**
 * Enumeration der verfügbaren Farben einer Turtle.
 * Jeder Eintrag entspricht einer der statischen Farben aus der Klasse java.awt.Color.
 * Zusätzlich ist zu jedem Eintrag ein numerischer Wert, der dem Index im Farb-Array der Turtle entspricht, zugeordnet.
 * 
 * @author  Lasse Huber-Saffer
 * @version 01.01.2022
 */
public enum TurtleColor
{
    BLACK(0),
    BLUE(1),
    CYAN(2),
    DARK_GRAY(3),
    GRAY(4),
    GREEN(5),
    LIGHT_GRAY(6),
    MAGENTA(7),
    ORANGE(8),
    PINK(9),
    RED(10),
    WHITE(11),
    YELLOW(12);
    
    private Integer _id;
    
    private TurtleColor(final Integer id)
    {
        this._id = id;
    }
    
    public Integer getID()
    {
        return _id;
    }
}
