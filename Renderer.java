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
     * Zeichnet ein dreidimensionales Mesh aus Linien in einer gegebenen Farbe in das Sichtfeld einer Kamera.
     * @param meshRef Referenz auf ein Mesh
     * @param modelMatrix Model-Transformationsmatrix des Meshs
     * @param farbe Farbe, in der das Mesh gezeichnet werden soll
     * @param camera Kamera, in dessen Sichtfeld das Mesh gerendert werden soll
     */
    public void drawMesh(Mesh meshRef, Matrix4 modelMatrix, String farbe, Camera camera)
    {
        drawMesh(meshRef, modelMatrix, farbe, camera, false);
    }
    
    /**
     * Zeichnet ein dreidimensionales Mesh aus Linien in einer gegebenen Farbe in das Sichtfeld einer Kamera.
     * Optional kann die ViewMatrix der Kamera ignoriert werden, um ein Mesh als 1st Person ViewModel zu rendern.
     * @param meshRef Referenz auf ein Mesh
     * @param modelMatrix Model-Transformationsmatrix des Meshs
     * @param farbe Farbe, in der das Mesh gezeichnet werden soll
     * @param camera Kamera, in dessen Sichtfeld das Mesh gerendert werden soll
     * @param ignoreViewMatrix wenn true, wird die ViewMatrix der Kamera nicht angewandt, was für ViewModels nützlich ist
     */
    public void drawMesh(Mesh meshRef, Matrix4 modelMatrix, String farbe, Camera camera, boolean ignoreViewMatrix)
    {
        for(int i = 0; i < meshRef.lineIndices.size(); i++)
        {
            drawLine3D(meshRef.vertices.get(meshRef.lineIndices.get(i).getKey() - 1), meshRef.vertices.get(meshRef.lineIndices.get(i).getValue() - 1), farbe, modelMatrix, camera, ignoreViewMatrix);
        }
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
        drawLine3D(a, b, "rot", null, camera, false);
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
        drawLine3D(a, b, farbe, null, camera, false);
    }
    
    private Vector3 getLinePlaneIntersection(Vector3 lineP1, Vector3 lineP2, Vector3 planeP1, Vector3 planeNormal)
    {
        Vector3 u = lineP2.subtract(lineP1);
        double dot = planeNormal.dot(u);
        final double eps = 1e-6;
        if (Math.abs(dot) > eps)
        {
            Vector3 w = lineP1.subtract(planeP1);
            double fac = -planeNormal.dot(w) / dot;
            u = u.multiply(fac);
            return lineP1.add(u);
        }
        
        return null;
    }
    
    private boolean isPointInFrustum(Vector3 p, Camera camera)
    {
        Vector3 pDir = p.subtract(camera.getPosition());
        double pDot = pDir.dot(camera.getDirection());
        if (pDot < 0.0)
        {
            return true;
        }
        
        return false;
    }
    
    private boolean isLineInFrustum(Vector3 a, Vector3 b, Camera camera)
    {   
        if (isPointInFrustum(a, camera) && isPointInFrustum(b, camera))
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * Zeichnet eine beliebigfarbige Linie zwischen zwei Punkten im dreidimensionalen Raum
     * @param a Startpunkt
     * @param b Endpunkt
     * @param farbe deutscher ausgeschriebener Name von einer der 13 validen Farben (siehe Turtle)
     * @param model Modelmatrix für die Punkte der Linie. Wenn null, wird nichts angewandt
     * @param camera Kamera für die dreidimensionale Projektion
     * @param ignoreViewMatrix wenn true, wird die ViewMatrix der Kamera nicht angewandt, was für ViewModels nützlich ist
     */
    public void drawLine3D(Vector3 a, Vector3 b, String farbe, Matrix4 model, Camera camera, boolean ignoreViewMatrix)
    {
        a = (model == null)? a : model.multiply(new Vector4(a, 1.0)).getXYZ();
        b = (model == null)? b : model.multiply(new Vector4(b, 1.0)).getXYZ();
        if (isLineInFrustum(a, b, camera))
        {
            Matrix4 transform = camera.getProjectionMatrix().multiply(camera.getViewMatrix());
        
            Vector4 pA = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(a, 1.0)));
            Vector4 pB = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(b, 1.0)));
        
            drawLine(new Vector2(pA.getX(), pA.getY()), new Vector2(pB.getX(), pB.getY()), farbe);
        }
        else
        {
            Vector3 frustumIntersection = getLinePlaneIntersection(a, b, camera.getPosition(), camera.getDirection());
            if (frustumIntersection != null)
            {
                Vector3 pInside;
                if (isPointInFrustum(a, camera))
                {
                    pInside = a;
                }
                else if (isPointInFrustum(b, camera))
                {
                    pInside = b;
                }
                else // Wenn beide Punkte hinter der Camera liegen, soll die Linie gar nicht gerendert werden.
                {
                    return;
                }
                Matrix4 transform = camera.getProjectionMatrix().multiply(camera.getViewMatrix());
        
                // NOTE(sven): Wir müssen den Schnittpunkt um den Wert epsilon (aus getLinePlaneIntersection) verschieben, 
                // weil durch Gleitkommarundungsfehler teilweise die Punkte trotzdem noch hinter der Kamera liegen.
                Vector3 precisionError = pInside.subtract(frustumIntersection).multiply(1e-6);
                
                Vector4 pA = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(frustumIntersection.add(precisionError), 1.0)));
                Vector4 pB = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(pInside, 1.0)));
            
                drawLine(new Vector2(pA.getX(), pA.getY()), new Vector2(pB.getX(), pB.getY()), farbe);
            }
        }
    }
    
    private void drawStripesAroundCorner(Vector3 cornerOrigin, Vector3 pointA, Vector3 pointB, int numStripes, String farbe, Camera camera)
    {
        for (int i = 1; i <= numStripes; i++)
        {
            Vector3 temp = pointA.subtract(cornerOrigin);
            temp = temp.multiply((double)i / (numStripes + 1));
            Vector3 pointLeft = cornerOrigin.add(temp);
            
            temp = pointB.subtract(cornerOrigin);
            temp = temp.multiply((double)i / (numStripes + 1));
            Vector3 pointRight = cornerOrigin.add(temp);
            
            drawLine3D(pointLeft, pointRight, farbe, camera);
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
        drawLine3D(topLeft, bottomRight, farbe, camera);
        
        final int numStripesForOneHalf = 10;
        // Untere Hälfte des Rechtsecks
        drawStripesAroundCorner(bottomLeft, topLeft, bottomRight, numStripesForOneHalf, farbe, camera);
        
        // Obere Hälfte des Rechtecks
        drawStripesAroundCorner(topRight, topLeft, bottomRight, numStripesForOneHalf, farbe, camera);
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
