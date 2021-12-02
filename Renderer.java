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
     * @param farbe deutscher ausgeschriebener Name von einer der 13 validen Farben (siehe Turtle)
     * @param camera Kamera für die dreidimensionale Projektion
     */
    public void drawLine3D(Vector3 a, Vector3 b, String farbe, Camera camera)
    {
        Matrix4 transform = camera.getProjectionMatrix().multiply(camera.getViewMatrix());
        Vector4 pA = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(a, 1.0)));
        Vector4 pB = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(b, 1.0)));
        
        drawLine(new Vector2(pA.getX(), pA.getY()), new Vector2(pB.getX(), pB.getY()), farbe);
    }
    
    /**
     * Zeichnet eine beliebigfarbige Linie zwischen zwei Punkten im dreidimensionalen Raum
     * @param a Startpunkt
     * @param b Endpunkt
     * @param farbe deutscher ausgeschriebener Name von einer der 13 validen Farben (siehe Turtle)
     * @param model Modelmatrix für die Punkte der Linie
     * @param camera Kamera für die dreidimensionale Projektion
     */
    public void drawLine3D(Vector3 a, Vector3 b, String farbe, Matrix4 model, Camera camera)
    {
        Matrix4 transform = camera.getProjectionMatrix().multiply(camera.getViewMatrix().multiply(model));
        Vector4 pA = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(a, 1.0)));
        Vector4 pB = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(b, 1.0)));
        
        drawLine(new Vector2(pA.getX(), pA.getY()), new Vector2(pB.getX(), pB.getY()), farbe);
    }
    
    private void drawStripesAroundCorner(Vector3 cornerOrigin, Vector3 pointA, Vector3 pointB, int numStripes, Camera camera)
    {
        for (int i = 1; i <= numStripes; i++)
        {
            Vector3 temp = pointA.subtract(cornerOrigin);
            temp = temp.multiply((double)i / (numStripes + 1));
            Vector3 pointLeft = cornerOrigin.add(temp);
            
            temp = pointB.subtract(cornerOrigin);
            temp = temp.multiply((double)i / (numStripes + 1));
            Vector3 pointRight = cornerOrigin.add(temp);
            
            drawLine3D(pointLeft, pointRight, camera);
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
    public void drawStripedQuad(Vector3 topLeft, Vector3 bottomLeft, Vector3 topRight, Vector3 bottomRight, String farbe, Camera camera)
    {
        // 
        drawLine3D(topLeft, topRight, farbe, camera);
        drawLine3D(topRight, bottomRight, farbe, camera);
        drawLine3D(bottomRight, bottomLeft, farbe, camera);
        drawLine3D(bottomLeft, topLeft, farbe, camera);
        
        // Zeichne diagonalen
        drawLine3D(topLeft, bottomRight, camera);
        
        final int numStripesForOneHalf = 10;
        // Untere Hälfte des Rechtsecks
        drawStripesAroundCorner(bottomLeft, topLeft, bottomRight, numStripesForOneHalf, camera);
        
        // Obere Hälfte des Rechtecks
        drawStripesAroundCorner(topRight, topLeft, bottomRight, numStripesForOneHalf, camera);
    }
    
    public void drawAxis(Camera camera)
    {
        drawLine3D(new Vector3(), new Vector3(1.0, 0.0, 0.0), "rot", camera);
        drawLine3D(new Vector3(), new Vector3(0.0, 1.0, 0.0), "gruen", camera);
        drawLine3D(new Vector3(), new Vector3(0.0, 0.0, 1.0), "blau", camera);
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
