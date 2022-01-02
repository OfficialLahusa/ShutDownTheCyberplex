/**
 * Renderer, der auf Basis von Turtles zweidimensional und dreidimensional rendern kann
 *
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
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
     * @param color Farbe, in der das Mesh gezeichnet werden soll
     * @param camera Kamera, in dessen Sichtfeld das Mesh gerendert werden soll
     */
    public void drawMesh(Mesh meshRef, Matrix4 modelMatrix, TurtleColor color, Camera camera)
    {
        drawMesh(meshRef, modelMatrix, color, camera, false);
    }
    
    /**
     * Zeichnet ein dreidimensionales Mesh aus Linien in einer gegebenen Farbe in das Sichtfeld einer Kamera.
     * Optional kann die ViewMatrix der Kamera ignoriert werden, um ein Mesh als 1st Person ViewModel zu rendern.
     * @param meshRef Referenz auf ein Mesh
     * @param modelMatrix Model-Transformationsmatrix des Meshs
     * @param color Farbe, in der das Mesh gezeichnet werden soll
     * @param camera Kamera, in dessen Sichtfeld das Mesh gerendert werden soll
     * @param ignoreViewMatrix wenn true, wird die ViewMatrix der Kamera nicht angewandt, was für ViewModels nützlich ist
     */
    public void drawMesh(Mesh meshRef, Matrix4 modelMatrix, TurtleColor color, Camera camera, boolean ignoreViewMatrix)
    {
        for(int i = 0; i < meshRef.lineIndices.size(); i++)
        {
            drawLine3D(meshRef.vertices.get(meshRef.lineIndices.get(i).getKey() - 1), meshRef.vertices.get(meshRef.lineIndices.get(i).getValue() - 1), color, modelMatrix, camera, ignoreViewMatrix);
        }
    }
    
    /**
     * Zeichnet eine rote Linie zwischen zwei Punkten im zweidimensionalen Screenspace
     * @param a Startpunkt
     * @param b Endpunkt
     */
    public void drawLine(Vector2 a, Vector2 b)
    {
        drawLine(a, b, TurtleColor.RED);
    }
    
    /**
     * Zeichnet eine beliebigfarbige Linie zwischen zwei Punkten im zweidimensionalen Screenspace
     * @param a Startpunkt
     * @param b Endpunkt
     * @param color Farbe
     */
    public void drawLine(Vector2 a, Vector2 b, TurtleColor color)
    {
        if(color == null)
        {
            throw new IllegalArgumentException("color was null when drawing line");
        }
        
        _turtle.hinterlasseKeineSpur();
        _turtle.geheZu(a.getX(), a.getY());
        _turtle.setzeFarbe(colorToString(color));
        _turtle.hinterlasseSpur();
        _turtle.geheZu(b.getX(), b.getY());
        _turtle.hinterlasseKeineSpur();
    }
    
    /**
     * Zeichnet ein Crosshair (Zielhilfe) auf den Bildschirm
     * @param length Länge der einzelnen Linien des Crosshairs
     * @param distance Abstand der Linien zum Mittelpunkt
     * @param color Farbe des Crosshairs
     */
    public void drawCrosshair(double length, double distance, TurtleColor color)
    {
        // Nach oben
        drawLine(new Vector2(250.0, 250.0 + distance), new Vector2(250.0, 250.0 + distance + length), color);
        // Nach unten
        drawLine(new Vector2(250.0, 250.0 - distance), new Vector2(250.0, 250.0 - distance - length), color);
        // Nach links
        drawLine(new Vector2(250.0 - distance, 250.0), new Vector2(250.0 - distance - length, 250.0), color);
        // Nach rechts
        drawLine(new Vector2(250.0 + distance, 250.0), new Vector2(250.0 + distance + length, 250.0), color);
    }
    
    /**
     * Zeichnet eine rote Linie zwischen zwei Punkten im dreidimensionalen Raum
     * @param a Startpunkt
     * @param b Endpunkt
     * @param camera Kamera für die dreidimensionale Projektion
     */
    public void drawLine3D(Vector3 a, Vector3 b, Camera camera)
    {
        drawLine3D(a, b, TurtleColor.RED, null, camera, false);
    }
    
    /**
     * Zeichnet eine beliebigfarbige Linie zwischen zwei Punkten im dreidimensionalen Raum
     * @param a Startpunkt
     * @param b Endpunkt
     * @param color Farbe
     * @param camera Kamera für die dreidimensionale Projektion
     */
    public void drawLine3D(Vector3 a, Vector3 b, TurtleColor color, Camera camera)
    {
        drawLine3D(a, b, color, null, camera, false);
    }
    
    /**
     * Gibt den Schnittpunkt einer Strecke mit einer Ebene zurück
     * @param lineP1 erster Punkt der Linie
     * @param lineP2 zweiter Punkt der Linie
     * @param planeP1 Ortsvektor der Ebene
     * @param planeNormal Normalenvektor der Ebene
     * @return null, wenn es keinen Schnittpunkt gibt, ansonsten den Schnittpunkt
     */
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
    
    /**
     * Gibt zurück ob ein Punkt im Frustum der Kamera liegt
     * @param p Punkt
     * @param camPos Position der Kamera im Raum
     * @param camDir Richtungsvektor der Kamera
     * @return true, wenn Punkt innerhalb des Frustums liegt
     */
    private boolean isPointInFrustum(Vector3 p, Vector3 camPos, Vector3 camDir)
    {
        Vector3 pDir = p.subtract(camPos);
        double pDot = pDir.dot(camDir);
        return pDot < 0.0;
    }
    
    /**
     * Gibt zurück ob eine Linie im Frustum der Kamera liegt
     * @param a erster Punkt der Linie
     * @param b zweiter Punkt der Linie
     * @param camPos Position der Kamera im Raum
     * @param camDir Richtungsvektor der Kamera
     * @return true, wenn Linie innerhalb des Frustums liegt
     */
    private boolean isLineInFrustum(Vector3 a, Vector3 b, Vector3 camPos, Vector3 camDir)
    {   
        return isPointInFrustum(a, camPos, camDir) && isPointInFrustum(b, camPos, camDir);
    }
    
    /**
     * Zeichnet eine beliebigfarbige Linie zwischen zwei Punkten im dreidimensionalen Raum
     * @param a Startpunkt
     * @param b Endpunkt
     * @param color Farbe
     * @param model Modelmatrix für die Punkte der Linie. Wenn null, wird nichts angewandt
     * @param camera Kamera für die dreidimensionale Projektion
     * @param ignoreViewMatrix wenn true, wird die ViewMatrix der Kamera nicht angewandt, was für ViewModels nützlich ist
     */
    public void drawLine3D(Vector3 a, Vector3 b, TurtleColor color, Matrix4 model, Camera camera, boolean ignoreViewMatrix)
    {
        Vector3 camPos = camera.getPosition();
        Vector3 camDir = camera.getDirection();
        Matrix4 viewMatrix = camera.getViewMatrix();

        if (model != null)
        {
            a = model.multiply(new Vector4(a, 1.0)).getXYZ();
            b = model.multiply(new Vector4(b, 1.0)).getXYZ();
        }

        if (ignoreViewMatrix)
        {
            viewMatrix = new Matrix4();
            camPos = new Vector3(0.0, 0.0, 0.0);
            camDir = new Vector3(0.0, 0.0, 1.0);
        }

        Matrix4 transform = camera.getProjectionMatrix().multiply(viewMatrix);

        if (isLineInFrustum(a, b, camPos, camDir))
        {
            Vector4 pA = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(a, 1.0)));
            Vector4 pB = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(b, 1.0)));
        
            drawLine(new Vector2(pA.getX(), pA.getY()), new Vector2(pB.getX(), pB.getY()), color);
        }
        else
        {
            Vector3 frustumIntersection = getLinePlaneIntersection(a, b, camPos, camDir);
            if (frustumIntersection != null)
            {
                Vector3 pInside;
                if (isPointInFrustum(a, camPos, camDir))
                {
                    pInside = a;
                }
                else if (isPointInFrustum(b, camPos, camDir))
                {
                    pInside = b;
                }
                else // Wenn beide Punkte hinter der Camera liegen, soll die Linie gar nicht gerendert werden.
                {
                    return;
                }
        
                // NOTE(sven): Wir müssen den Schnittpunkt um den Wert epsilon (aus getLinePlaneIntersection) verschieben, 
                // weil durch Gleitkommarundungsfehler teilweise die Punkte trotzdem noch hinter der Kamera liegen.
                Vector3 precisionError = pInside.subtract(frustumIntersection).multiply(1e-6);
                
                Vector4 pA = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(frustumIntersection.add(precisionError), 1.0)));
                Vector4 pB = MatrixGenerator.viewportTransform(transform.multiply(new Vector4(pInside, 1.0)));
            
                drawLine(new Vector2(pA.getX(), pA.getY()), new Vector2(pB.getX(), pB.getY()), color);
            }
        }
    }
    
    
    /**
     * Zeichnet die Lebensanzeige eines Spielers
     * 
     * @param player der Spieler von dem die Leben angezeigt werden sollen
     */
    public void drawHealthbar(Player player)
    {   
        // Rahmen
        final double xOffset = 20.0;
        final double yOffset = 460.0;
        final double length = 150.0;
        final double height = 15.0;
        final double cuttedLength = Math.max(0.0, length - height);
        
        Vector2 outerTopLeft = new Vector2(xOffset, yOffset);
        Vector2 outerBottomLeft = new Vector2(xOffset, yOffset + height);
        Vector2 outerTopRight = new Vector2(xOffset + cuttedLength, yOffset);
        Vector2 outerBottomRight = new Vector2(xOffset + length, yOffset + height);
        
        // Aktuelle Leben
        double innerLength = length * ((double)player.getHealth() / 100);
        double innerCuttedLength = Math.max(0.0, innerLength - height);
        double innerHeight = Math.min(height, innerLength);
        
        Vector2 innerTopLeft = new Vector2(xOffset, yOffset + height - innerHeight);
        Vector2 innerBottomLeft = new Vector2(xOffset, yOffset + height);
        Vector2 innerTopRight = new Vector2(xOffset + innerCuttedLength, yOffset + height - innerHeight);
        Vector2 innerBottomRight = new Vector2(xOffset + innerLength, yOffset + height);
        
        // Bestimmt die Farbe der Lebensleiste anhand der Spieler-Lebenspunkte
        TurtleColor color = null;
        if (player.getHealth() > 70)
        {
            color = TurtleColor.GREEN;
        }
        else if (player.getHealth() > 40)
        {
            color = TurtleColor.ORANGE;
        }
        else
        {
            color = TurtleColor.RED;
        }
        
        // Zeichnet die Lebensanzeige
        drawStripedQuad2D(innerTopLeft, innerBottomLeft, innerTopRight, innerBottomRight, color, 5);
        drawQuad2D(outerTopLeft, outerBottomLeft, outerTopRight, outerBottomRight, TurtleColor.WHITE);
    }
    
    /**
     * Zeichnet mehrere Schraffierungsstreifen in 2D um einen Eckpunkt
     * @param cornerOrigin Eckpunkt
     * @param pointA erster begrenzender Punkt
     * @param pointB zweiter begrenzender Punkt
     * @param numStripes Anzahl der Streifen
     * @param color Farbe
     */
    private void drawStripesAroundCorner2D(Vector2 cornerOrigin, Vector2 pointA, Vector2 pointB, int numStripes, TurtleColor color)
    {
        for (int i = 1; i <= numStripes; i++)
        {
            Vector2 temp = pointA.subtract(cornerOrigin);
            temp = temp.multiply((double)i / (numStripes + 1));
            Vector2 pointLeft = cornerOrigin.add(temp);
            
            temp = pointB.subtract(cornerOrigin);
            temp = temp.multiply((double)i / (numStripes + 1));
            Vector2 pointRight = cornerOrigin.add(temp);
            
            drawLine(pointLeft, pointRight, color);
        }
    }
    
    /**
     * Zeichnet mehrere Schraffierungsstreifen in 3D um einen Eckpunkt
     * @param cornerOrigin Eckpunkt
     * @param pointA erster begrenzender Punkt
     * @param pointB zweiter begrenzender Punkt
     * @param numStripes Anzahl der Streifen
     * @param color Farbe
     */
    private void drawStripesAroundCorner3D(Vector3 cornerOrigin, Vector3 pointA, Vector3 pointB, int numStripes, TurtleColor color, Camera camera)
    {
        for (int i = 1; i <= numStripes; i++)
        {
            Vector3 temp = pointA.subtract(cornerOrigin);
            temp = temp.multiply((double)i / (numStripes + 1));
            Vector3 pointLeft = cornerOrigin.add(temp);
            
            temp = pointB.subtract(cornerOrigin);
            temp = temp.multiply((double)i / (numStripes + 1));
            Vector3 pointRight = cornerOrigin.add(temp);
            
            drawLine3D(pointLeft, pointRight, color, camera);
        }
    }
    
    /**
     * Zeichnet ein Viereck in 2D
     * @param topLeft oberer linker Punkt
     * @param bottomLeft unterer linker Punkt
     * @param topRight oberer rechter Punkt
     * @param bottomRight unterer rechter Punkt
     * @param color Farbe
     */
    public void drawQuad2D(Vector2 topLeft, Vector2 bottomLeft, Vector2 topRight, Vector2 bottomRight, TurtleColor color)
    {
        drawLine(topLeft, topRight, color);
        drawLine(topRight, bottomRight, color);
        drawLine(bottomRight, bottomLeft, color);
        drawLine(bottomLeft, topLeft, color);
    }
    
    /**
     * Zeichnet ein gestreiftes Rechteck in 2D-Ansicht
     * 
     * @param topLeft oberer linker Punkt
     * @param bottomLeft unterer linker Punkt
     * @param topRight oberer rechter Punkt
     * @param bottomRight unterer rechter Punkt
     * @param color Farbe
     * @param numDiagonals Anzahl der diagonalen Streifen
     */
    public void drawStripedQuad2D(Vector2 topLeft, Vector2 bottomLeft, Vector2 topRight, Vector2 bottomRight, TurtleColor color, int numDiagonals)
    {
        // 
        drawQuad2D(topLeft, bottomLeft, topRight, bottomRight, color);
        
        // Zeichne diagonalen
        drawLine(topLeft, bottomRight, color);
        
        // Die Anzahl der Diagonalen muss ungerade sein, weil wir immer die eine in der Mitte haben.
        if (numDiagonals % 2 == 0)
        {
            numDiagonals -= 1;
        }
        
        int numStripesForOneHalf = (numDiagonals - 1) / 2;
        
        // Untere Hälfte des Rechtsecks
        drawStripesAroundCorner2D(bottomLeft, topLeft, bottomRight, numStripesForOneHalf, color);
        
        // Obere Hälfte des Rechtecks
        drawStripesAroundCorner2D(topRight, topLeft, bottomRight, numStripesForOneHalf, color);
    }
    
    /**
     * Zeichnet ein gestreiftes Rechteck in 3D-Ansicht
     * 
     * @param topLeft Der Punkt Oben-links
     * @param bottomLeft Der Punkt Unten-links
     * @param topRight Der Punkt Oben-rechts
     * @param bottomRight Der Punkt Unten-rechts
     * @param color Farbe
     * @param camera die benutzte Camera
     */
    public void drawStripedQuad3D(Vector3 topLeft, Vector3 bottomLeft, Vector3 topRight, Vector3 bottomRight, TurtleColor color, Camera camera)
    {
        // 
        drawLine3D(topLeft, topRight, color, camera);
        drawLine3D(topRight, bottomRight, color, camera);
        drawLine3D(bottomRight, bottomLeft, color, camera);
        drawLine3D(bottomLeft, topLeft, color, camera);
        
        // Zeichne diagonalen
        drawLine3D(topLeft, bottomRight, color, camera);
        
        final int numStripesForOneHalf = 10;
        // Untere Hälfte des Rechtsecks
        drawStripesAroundCorner3D(bottomLeft, topLeft, bottomRight, numStripesForOneHalf, color, camera);
        
        // Obere Hälfte des Rechtecks
        drawStripesAroundCorner3D(topRight, topLeft, bottomRight, numStripesForOneHalf, color, camera);
    }
    
    /**
     * Zeichnet aus dem Ursprung heraus Linien entlang jeder Achse
     * @param camera benutze Kamera
     */
    public void drawAxis(Camera camera)
    {
        // X-Achse (rot)
        drawLine3D(new Vector3(), new Vector3(1.0, 0.0, 0.0), TurtleColor.RED, camera);
        // Y-Achse (grün)
        drawLine3D(new Vector3(), new Vector3(0.0, 1.0, 0.0), TurtleColor.GREEN, camera);
        // Z-Achse (blau)
        drawLine3D(new Vector3(), new Vector3(0.0, 0.0, 1.0), TurtleColor.BLUE, camera);
    }
    
    /**
     * Leert das Bild
     */
    public void clear()
    {
        TurtleWelt.GLOBALEWELT.loescheAlleSpuren();
    }
    
    /**
     * Leert das Bild und färbt es in einer bestimmten Farbe
     * @param r Rotkanal der Farbe [0, 255]
     * @param g Grünkanal der Farbe [0, 255]
     * @param b Blaukanal der Farbe [0, 255]
     */
    public void clear(int r, int g, int b)
    {
        TurtleWelt.GLOBALEWELT.bildschirmEinfaerben(r, g, b);
    }
    
    /**
     * Gibt den deutschen Namen einer gegebenen Turtle-Farbe zurück
     * @param color Turtle-Farbe
     * @return deutscher Name der Farbe (Bsp.: TurtleColor.RED -> "rot")
     */
    public static String colorToString(TurtleColor color)
    {
        switch(color)
        {
            default:
                throw new UnsupportedOperationException("color does not have a registered name");
            case BLACK:
                return "schwarz";
            case BLUE:
                return "blau";
            case CYAN:
                return "cyan";
            case DARK_GRAY:
                return "dunkelgrau";
            case GRAY:
                return "grau";
            case GREEN:
                return "gruen";
            case LIGHT_GRAY:
                return "hellgrau";
            case MAGENTA:
                return "magenta";
            case ORANGE:
                return "orange";
            case PINK:
                return "pink";
            case RED:
                return "rot";
            case WHITE:
                return "weiss";
            case YELLOW:
                return "gelb";
        }
    }
}
