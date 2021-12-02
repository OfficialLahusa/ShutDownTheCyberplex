/**
 * Render Engine, die auf Basis von Turtles zweidimensional und dreidimensional rendern kann
 *
 * @author Lasse Huber-Saffer
 * @author Sven Schreiber
 * @version 02. November 2021
 */
class Renderer
{
    // Turtle, die zum Zeichnen verwendet wird
    private Turtle _turtle;
    
    /**
     * Der leere explizite Konstruktor von Renderer.
     */
    public Renderer()
    {
        _turtle = new Turtle(0, 0);
        _turtle.setzeGeschwindigkeit(10);
    }
    
    /**
     * Zeichnet eine rote Linie zwischen zwei Punkten im zweidimensionalen Screenspace
     * @param a Startpunkt
     * @param b Endpunkt
     */
    public void drawLine(Vector2 a, Vector2 b)
    {
        drawLine(a, b, "rot");
    }
    
    /**
     * Zeichnet eine beliebigfarbige Linie zwischen zwei Punkten im zweidimensionalen Screenspace
     * @param a Startpunkt
     * @param b Endpunkt
     * @param farbe deutscher ausgeschriebener Name von einer der 13 validen Farben (siehe Turtle)
     */
    public void drawLine(Vector2 a, Vector2 b, String farbe)
    {
        _turtle.hinterlasseKeineSpur();
        _turtle.geheZu(a.getX(), a.getY());
        _turtle.setzeFarbe(farbe);
        _turtle.hinterlasseSpur();
        _turtle.geheZu(b.getX(), b.getY());
        _turtle.hinterlasseKeineSpur();
    }
    
    // Mir fällt noch kein besserer Name ein...
    private void drawStripesAroundCorner(Vector2 cornerOrigin, Vector2 pointA, Vector2 pointB, int numStripes)
    {
        for (int i = 1; i <= numStripes; i++)
        {
            Vector2 temp = pointA.subtract(cornerOrigin);
            temp = temp.multiply((double)i / (numStripes + 1));
            Vector2 pointLeft = cornerOrigin.add(temp);
            
            temp = pointB.subtract(cornerOrigin);
            temp = temp.multiply((double)i / (numStripes + 1));
            Vector2 pointRight = cornerOrigin.add(temp);
            
            drawLine(pointLeft, pointRight);
        }
    }
    
    /**
     * Zeichnet ein gestreiftes Rechteck
     * 
     * @param topLeft Der Punkt Oben-links
     * @param bottomLeft Der Punkt Unten-links
     * @param topRight Der Punkt Oben-rechts
     * @param bottomRight Der Punkt Unten-rechts
     * @param farbe Die gewünschte Farb
     */
    public void drawStripedQuad(Vector2 topLeft, Vector2 bottomLeft, Vector2 topRight, Vector2 bottomRight, String farbe)
    {
        // 
        drawLine(topLeft, topRight, farbe);
        drawLine(topRight, bottomRight, farbe);
        drawLine(bottomRight, bottomLeft, farbe);
        drawLine(bottomLeft, topLeft, farbe);
        
        // Zeichne diagonalen
        drawLine(topLeft, bottomRight);
        
        final int numStripesForOneHalf = 10;
        // Untere Hälfte des Rechtsecks
        drawStripesAroundCorner(bottomLeft, topLeft, bottomRight, numStripesForOneHalf);
        
        // Obere Hälfte des Rechtecks
        drawStripesAroundCorner(topRight, topLeft, bottomRight, numStripesForOneHalf);
    }
    
    public void clear()
    {
        TurtleWelt.GLOBALEWELT.loescheAlleSpuren();
    }
    
    public void fill(int r, int g, int b)
    {
        TurtleWelt.GLOBALEWELT.bildschirmEinfaerben(r, g, b);
    }
}
