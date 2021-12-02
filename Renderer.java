/**
 * Render Engine, die auf Basis von Turtles zweidimensional und dreidimensional rendern kann
 *
 * @author Lasse Huber-Saffer
 * @version 30. November 2021
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
        _turtle.setzeGeschwindigkeit(9);
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
    
    /**
     * Zeichnet eine rote Linie zwischen zwei Punkten im dreidimensionalen Raum
     * @param a Startpunkt
     * @param b Endpunkt
     * @param camera Kamera für die dreidimensionale Projektion
     */
    public void drawLine3D(Vector3 a, Vector3 b, Camera camera)
    {
        drawLine3D(a, b, "rot", camera);
    }
    
    /**
     * Zeichnet eine beliebigfarbige Linie zwischen zwei Punkten im dreidimensionalen Raum
     * @param a Startpunkt
     * @param b Endpunkt
     * @param farbe deutscher ausgeschriebener Name von einer der 13 validen Farben (siehe Turtle)#
     * @param camera Kamera für die dreidimensionale Projektion
     */
    public void drawLine3D(Vector3 a, Vector3 b, String farbe, Camera camera)
    {
        Matrix4 transform = camera.getProjectionMatrix().multiply(camera.getViewMatrix());
        Vector4 pA = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(a, 1.0)));
        Vector4 pB = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(b, 1.0)));
        
        drawLine(new Vector2(pA.getX(), pA.getY()), new Vector2(pB.getX(), pB.getY()), farbe);
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
