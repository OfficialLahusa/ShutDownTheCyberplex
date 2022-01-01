
/**
 * Text-Renderer, der einige Buchstaben, Zahlen und Symbole ins zweidimensionale Screenspace zeichnen kann
 * 
 * @author Nico Hädicke, Lasse Huber-Saffer (geringfügige Änderungen)
 * @version 01.01.2022
 */
public class TextRenderer
{
    // NOTE (Lasse): Eine Überarbeitung von Nicos altem Code wäre von Vorteil
    
    private Renderer _renderer;
    private Vector2 _lastPos;
    
    /**
     * Konstruktor für Objekte der Klasse TextRenderer
     * @param renderer zu verwendender Renderer
     */
    public TextRenderer(Renderer renderer)
    {
        _renderer = renderer;
        _lastPos = new Vector2(0, 0);
    }

    /**
     * Zeichnet einen String in einer bestimmten Größe und Farbe an einen Punkt im zweidimensionalen Screenspace
     * 
     * @param start Startpunkt oben links vom String
     * @param size Schriftgröße
     * @param text Zu rendernder String
     * @param color Textfarbe
     */
    public void write(Vector2 start, int size, String text, TurtleColor color)
    {
        // Buchstabendimensionen: 24x26
        int l = text.length();
        String letter = "";
        Vector2 origStart = new Vector2(start.getX(), start.getY());
        double buffer = 0.1*(size/2);
        for(int i = 0; i < l; i++)
        {
            if(Character.toString(text.charAt(i)).matches("[0-9]"))
            {
                letterNumber(start, size, text.charAt(i), color);
                switch(text.charAt(i))
                {
                    case '1':
                        start.setX(start.getX()+1.1*size+buffer);
                        break;
                    default:
                        start.setX(start.getX()+2.1*size+buffer);
                }
            }
            else if(Character.toString(text.charAt(i)).matches("[A-Z]"))
            {
                letterUpperCase(start, size, text.charAt(i), color);
                switch(text.charAt(i))
                {
                    case 'F':
                        start.setX(start.getX()+2.1*size+buffer);
                        break;
                    case 'I':
                        start.setX(start.getX()+0.6*size+buffer);
                        break;
                    case 'J':
                    case 'L':
                        start.setX(start.getX()+2.0*size+buffer);
                        break;
                    case 'M':
                        start.setX(start.getX()+3.0*size+buffer);
                        break;
                    case 'P':
                    case 'R':
                    case 'S':
                    case 'Z':
                        start.setX(start.getX()+2.3*size+buffer);
                        break;
                    case 'W':
                        start.setX(start.getX()+3.4*size+buffer);
                        break;
                    case 'X':
                        start.setX(start.getX()+2.2*size+buffer);
                        break;
                    default:
                        start.setX(start.getX()+2.4*size+buffer);
                }
            }
            else if(Character.toString(text.charAt(i)).matches("[a-z]"))
            {
                letterLowerCase(start, size, text.charAt(i), color);
                switch(text.charAt(i))
                {
                    case 'f':
                    case 't':
                    case 'v':
                    case 'x':
                    case 'y':
                        start.setX(start.getX()+1.8*size+buffer);
                        break;
                    case 'i':
                    case 'l':
                        start.setX(start.getX()+0.6*size+buffer);
                        break;
                    case 'j':
                        start.setX(start.getX()+1.0*size+buffer);
                        break;
                    case 'k':
                    case 'r':
                        start.setX(start.getX()+1.7*size+buffer);
                        break;
                    case 'm':
                    case 'w':
                        start.setX(start.getX()+3.0*size+buffer);
                        break;
                    default:
                        start.setX(start.getX()+1.9*size+buffer);
                }
            }
            else
            {
                letterSymbols(start, size, text.charAt(i), color);
                switch(text.charAt(i))
                {
                    case '\n':
                        start.setX(origStart.getX());
                        start.setY(origStart.getY()+3.2*size+0.3*size);
                        origStart.setY(start.getY());
                        break;
                    case '!':
                    case ':':
                    case '.':
                    case ',':
                        start.setX(start.getX()+0.6*size+buffer);
                        break;
                    case '/':
                        start.setX(start.getX()+1.4*size+buffer);
                        break;
                    case 'ß':
                        start.setX(start.getX()+2.1*size+buffer);
                        break;
                    case '-':
                        start.setX(start.getX()+1.2*size+buffer);
                        break;
                    default:
                        start.setX(start.getX()+1.9*size+buffer);
                }
            }
        }
    }
    
    /**
     * Rendert doppelte Linie von vorherigem Endpunkt bis (wx2, wx2) relativ zum Startpunkt
     * 
     * @param start Startpunkt oben links vom String
     * @param wx2 Rellativer Zielpunkt auf der X-Achse
     * @param wy2 Rellativer Zielpunkt auf der Y-Achse
     * @param size Schriftgröße
     * @param color Farbe
     */
    public void drawLineCheck(Vector2 start, double wx2, double wy2, int size, TurtleColor color)
    {
        double x = start.getX();
        double y = start.getY();
        double wx1 = _lastPos.getX();
        double wy1 = _lastPos.getY();
        
        _renderer.drawLine(new Vector2(x+wx1*size, y+wy1*size), new Vector2(x+wx2*size, y+wy2*size), color);
        if(wx1 == wx2)
        {
            if(wx1 > 1)
            {
                _renderer.drawLine(new Vector2((x+wx1*size)+1, y+wy1*size), new Vector2((x+wx2*size)+1, y+wy2*size), color);
            }
            else
            {
                _renderer.drawLine(new Vector2((x+wx1*size)-1, y+wy1*size), new Vector2((x+wx2*size)-1, y+wy2*size), color);
            }
        }
        else if(wy1 == wy2)
        {
            if(wy1 > 1)
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)+1), new Vector2(x+wx2*size, (y+wy2*size)+1), color);
            }
            else
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)-1), new Vector2(x+wx2*size, (y+wy2*size)-1), color);
            }
        }
        _lastPos.setX(wx2);
        _lastPos.setY(wy2);
    }
    
    /**
     * Rendert doppelte Linie von (wx1, wy1) bis (wx2, wx2) relativ zum startpunkt
     * 
     * @param start Startpunkt oben links vom String
     * @param wx1 Relativer Startpunkt auf der X-Achse
     * @param wy1 Relativer Startpunkt auf der Y-Achse
     * @param wx2 Relativer Zielpunkt auf der X-Achse
     * @param wy2 Relativer Zielpunkt auf der Y-Achse
     * @param size Schriftgröße
     * @param color Farbe
     */
    public void drawLineCheck(Vector2 start, double wx1, double wy1, double wx2, double wy2, int size, TurtleColor color)
    {
        double x = start.getX();
        double y = start.getY();
        
        _renderer.drawLine(new Vector2(x+wx1*size, y+wy1*size), new Vector2(x+wx2*size, y+wy2*size), color);
        if(wx1 == wx2)
        {
            if(wx1 > 1)
            {
                _renderer.drawLine(new Vector2((x+wx1*size)+1, y+wy1*size), new Vector2((x+wx2*size)+1, y+wy2*size), color);
            }
            else
            {
                _renderer.drawLine(new Vector2((x+wx1*size)-1, y+wy1*size), new Vector2((x+wx2*size)-1, y+wy2*size), color);
            }
        }
        else if(wy1 == wy2)
        {
            if(wy1 > 1)
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)+1), new Vector2(x+wx2*size, (y+wy2*size)+1), color);
            }
            else
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)-1), new Vector2(x+wx2*size, (y+wy2*size)-1), color);
            }
        }
        
        _lastPos.setX(wx2);
        _lastPos.setY(wy2);
    }
    
    /**
     * Rendert doppelte Linie von vorherigem Endpunkt bis (wx2, wx2) rellativ zum startpunkt
     * 
     * @param start Startpunkt oben links vom String
     * @param wx2 Rellativer Zielpunkt auf der X-Achse
     * @param wy2 Rellativer Zielpunkt auf der Y-Achse
     * @param size Schriftgröße
     * @param direction Position der doppelten Lilie | 0 = nach oben/links 1 = nach unten/rechts
     * @param color Farbe
     */
    public void drawLineCheck(Vector2 start, double wx2, double wy2, int size, int direction, TurtleColor color)
    {
        double x = start.getX();
        double y = start.getY();
        double wx1 = _lastPos.getX();
        double wy1 = _lastPos.getY();
        
        _renderer.drawLine(new Vector2(x+wx1*size, y+wy1*size), new Vector2(x+wx2*size, y+wy2*size), color);
        if(wx1 == wx2)
        {
            if(direction == 1)
            {
                _renderer.drawLine(new Vector2((x+wx1*size)+1, y+wy1*size), new Vector2((x+wx2*size)+1, y+wy2*size), color);
            }
            else
            {
                _renderer.drawLine(new Vector2((x+wx1*size)-1, y+wy1*size), new Vector2((x+wx2*size)-1, y+wy2*size), color);
            }
        }
        else if(wy1 == wy2)
        {
            if(direction == 1)
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)+1), new Vector2(x+wx2*size, (y+wy2*size)+1), color);
            }
            else
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)-1), new Vector2(x+wx2*size, (y+wy2*size)-1), color);
            }
        }
        _lastPos.setX(wx2);
        _lastPos.setY(wy2);
    }
    
    /**
     * Rendert doppelte Linie von vorherigem Endpunkt bis (wx2, wx2) rellativ zum startpunkt
     * 
     * @param start Startpunkt oben links vom String
     * @param wx1 Rellativer Startpunkt auf der X-Achse
     * @param wy1 Rellativer Startpunkt auf der Y-Achse
     * @param wx2 Rellativer Zielpunkt auf der X-Achse
     * @param wy2 Rellativer Zielpunkt auf der Y-Achse
     * @param size Schriftgröße
     * @param direction Position der doppelten Lilie | 0 = nach oben/links 1 = nach unten/rechts
     * @param color Farbe
     */
    public void drawLineCheck(Vector2 start, double wx1, double wy1, double wx2, double wy2, int size, int direction, TurtleColor color)
    {
        double x = start.getX();
        double y = start.getY();
        
        _renderer.drawLine(new Vector2(x+wx1*size, y+wy1*size), new Vector2(x+wx2*size, y+wy2*size), color);
        if(wx1 == wx2)
        {
            if(direction == 1)
            {
                _renderer.drawLine(new Vector2((x+wx1*size)+1, y+wy1*size), new Vector2((x+wx2*size)+1, y+wy2*size), color);
            }
            else
            {
                _renderer.drawLine(new Vector2((x+wx1*size)-1, y+wy1*size), new Vector2((x+wx2*size)-1, y+wy2*size), color);
            }
        }
        else if(wy1 == wy2)
        {
            if(direction == 1)
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)+1), new Vector2(x+wx2*size, (y+wy2*size)+1), color);
            }
            else
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)-1), new Vector2(x+wx2*size, (y+wy2*size)-1), color);
            }
        }
        
        _lastPos.setX(wx2);
        _lastPos.setY(wy2);
    }
    
    /**
     * Zeichnet einen Kleinbuchstaben
     * 
     * @param start oberer linker Punkt des Buchstaben
     * @param size Schriftgröße
     * @param c Kleinbuchstabe, der gezeichnet werden soll
     * @param color Farbe
     */
    public void letterLowerCase(Vector2 start, int size, char c, TurtleColor color)
    {
        double x = start.getX();
        double y = start.getY();
        switch(c)
        {
            // all must bound l/r at 0.3 and t/b at 0.2
            case 'a':
                //right - bottom to top
                drawLineCheck(start, 1.7, 2.4, 1.7, 1.0, size, color);
                //along
                drawLineCheck(start, 1.5, 0.8, size, color);
                drawLineCheck(start, 0.5, 0.8, size, color);
                //middle - right to left
                drawLineCheck(start, 1.7, 1.5, 0.6, 1.5, size, color);
                drawLineCheck(start, 0.4, 1.7, size, color);
                drawLineCheck(start, 0.4, 2.2, size, color);
                drawLineCheck(start, 0.6, 2.4, size, color);
                drawLineCheck(start, 1.3, 2.4, size, color);
                drawLineCheck(start, 1.7, 2.2, size, color);
                break;
            case 'b':
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.2, size, color);
                //along
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 1.4, 2.4, size, color);
                drawLineCheck(start, 1.6, 2.2, size, color);
                drawLineCheck(start, 1.6, 1.0, size, color);
                drawLineCheck(start, 1.4, 0.8, size, color);
                drawLineCheck(start, 0.7, 0.8, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                break;
            case 'c':
                //start - bottom right
                drawLineCheck(start, 1.6, 2.0, 1.6, 2.2, size, color);
                //along
                drawLineCheck(start, 1.4, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                drawLineCheck(start, 0.5, 0.8, size, color);
                drawLineCheck(start, 1.4, 0.8, size, color);
                drawLineCheck(start, 1.6, 1.0, size, color);
                drawLineCheck(start, 1.6, 1.2, size, color);
                break;
            case 'd':
                //start - bottom right
                drawLineCheck(start, 1.6, 2.2, 1.2, 2.4, size, color);
                //along
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                drawLineCheck(start, 0.5, 0.8, size, color);
                drawLineCheck(start, 1.6, 0.8, size, color);
                //right - top to bottom
                drawLineCheck(start, 1.6, 0.2, 1.6, 2.4, size, color);
                break;
            case 'e':
                //bottom - right to left
                drawLineCheck(start, 1.6, 2.4, 0.5, 2.4, size, color);
                //along
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                drawLineCheck(start, 0.5, 0.8, size, color);
                drawLineCheck(start, 1.4, 0.8, size, color);
                drawLineCheck(start, 1.6, 1.0, size, color);
                drawLineCheck(start, 1.6, 1.6, size, color);
                drawLineCheck(start, 0.3, 1.6, size, color);
                break;
            case 'f':
                //middle - bottom to top
                drawLineCheck(start, 0.8, 2.4, 0.8, 0.4, size, color);
                //along
                drawLineCheck(start, 1.0, 0.2, size, color);
                drawLineCheck(start, 1.5, 0.2, size, color);
                //middle - left to right
                drawLineCheck(start, 0.3, 0.9, 1.5, 0.9, size, color);
                break;
            case 'g':
                //right - top to bottom
                drawLineCheck(start, 1.6, 0.8, 1.6, 3.0, size, color);
                //along
                drawLineCheck(start, 1.4, 3.2, size, color);
                drawLineCheck(start, 0.4, 3.2, size, color);
                //middle - right to left
                drawLineCheck(start, 1.6, 2.4, 0.5, 2.4, size, color);
                //along
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                drawLineCheck(start, 0.5, 0.8, size, color);
                drawLineCheck(start, 1.2, 0.8, size, color);
                drawLineCheck(start, 1.6, 1.0, size, color);
                break;
            case 'h':
                //right - bottom to top
                drawLineCheck(start, 1.6, 2.4, 1.6, 1.0, size, color);
                //along
                drawLineCheck(start, 1.4, 0.8, size, color);
                drawLineCheck(start, 0.7, 0.8, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size, color);
                break;
            case 'i':
                //dot - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 0.3, size, color);
                //middle - top to bottom
                drawLineCheck(start, 0.3, 0.8, 0.3, 2.4, size, color);
                break;
            case 'j':
                //dot - top to bottom
                drawLineCheck(start, 0.6, 0.2, 0.6, 0.3, size, 1, color);
                //middle - top to bottom
                drawLineCheck(start, 0.6, 0.8, 0.6, 3.0, size, 1, color);
                //along
                drawLineCheck(start, 0.4, 3.2, size, color);
                drawLineCheck(start, 0.2, 3.2, size, color);
                break;
            case 'k':
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size, color);
                //middle - left to right
                drawLineCheck(start, 0.3, 1.6, 0.5, 1.6, size, color);
                // / - top to bottom
                drawLineCheck(start, 1.4, 0.8, 0.5, 1.6, size, color);
                //along
                drawLineCheck(start, 1.4, 2.4, size, color);
                break;                
            case 'l':
                //middle - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size, color);
                break;                
            case 'm':
                //right - bottom to top
                drawLineCheck(start, 2.7, 2.4, 2.7, 1.0, size, color);
                //along
                drawLineCheck(start, 2.5, 0.8, size, color);
                drawLineCheck(start, 1.9, 0.8, size, color);
                drawLineCheck(start, 1.5, 1.0, size, color);
                drawLineCheck(start, 1.3, 0.8, size, color);
                drawLineCheck(start, 0.7, 0.8, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.8, size, color);
                //middle - bottom to top
                drawLineCheck(start, 1.5, 2.4, 1.5, 1.0, size, color);
                break;                
            case 'n':
                //right - bottom to top
                drawLineCheck(start, 1.6, 2.4, 1.6, 1.0, size, color);
                //along
                drawLineCheck(start, 1.4, 0.8, size, color);
                drawLineCheck(start, 0.7, 0.8, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.8, size, color);
                break;
            case 'o':
                //start - bottom right
                drawLineCheck(start, 1.6, 1.0, 1.6, 2.2, size, color);
                //along
                drawLineCheck(start, 1.4, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                drawLineCheck(start, 0.5, 0.8, size, color);
                drawLineCheck(start, 1.4, 0.8, size, color);
                drawLineCheck(start, 1.6, 1.0, size, color);
                break;                
            case 'p':
                //start - top left
                drawLineCheck(start, 0.3, 1.0, 0.7, 0.8, size, color);
                //along
                drawLineCheck(start, 1.4, 0.8, size, color);
                drawLineCheck(start, 1.6, 1.0, size, color);
                drawLineCheck(start, 1.6, 2.2, size, color);
                drawLineCheck(start, 1.4, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.4, size, color);
                //left - bottom to top
                drawLineCheck(start, 0.3, 3.2, 0.3, 0.8, size, color);
                break;                
            case 'q':
                //start - middle right
                drawLineCheck(start, 1.6, 2.2, 1.2, 2.4, size, color);
                //along
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                drawLineCheck(start, 0.5, 0.8, size, color);
                drawLineCheck(start, 1.4, 0.8, size, color);
                drawLineCheck(start, 1.6, 1.0, size, color);
                drawLineCheck(start, 1.6, 3.2, size, color);
                break;                
            case 'r':
                //start - right
                drawLineCheck(start, 1.4, 1.2, 1.4, 1.0, size, color);
                //along
                drawLineCheck(start, 1.2, 0.8, size, color);
                drawLineCheck(start, 0.7, 0.8, size, color);
                drawLineCheck(start, 0.3, 1.0, size, color);
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.8, size, color);
                break;
            case 's':
                //start - bottom left
                drawLineCheck(start, 0.3, 2.4, 1.4, 2.4, size, color);
                //along
                drawLineCheck(start, 1.6, 2.2, size, color);
                drawLineCheck(start, 1.6, 1.8, size, color);
                drawLineCheck(start, 1.4, 1.6, size, color);
                drawLineCheck(start, 0.5, 1.6, size, 0, color);
                drawLineCheck(start, 0.5, 1.5, 0.3, 1.3, size, color);
                drawLineCheck(start, 0.3, 0.9, size, color);
                drawLineCheck(start, 0.5, 0.7, size, color);
                drawLineCheck(start, 1.6, 0.7, size, color);
                break;                
            case 't':
                //middle - top to bottom
                drawLineCheck(start, 0.8, 0.2, 0.8, 2.2, size, color);
                //along
                drawLineCheck(start, 1.0, 2.4, size, color);
                drawLineCheck(start, 1.5, 2.4, size, color);
                //middle - left to right
                drawLineCheck(start, 0.3, 0.8, 1.5, 0.8, size, color);
                break;                
            case 'u':
                drawLineCheck(start, 0.3, 0.8, 0.3, 2.2, size, color);
                //along
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 1.2, 2.4, size, color);
                drawLineCheck(start, 1.6, 2.2, size, color);
                //right - bottom to top
                drawLineCheck(start, 1.6, 2.4, 1.6, 0.8, size, color);
                break;                
            case 'v':
                // \ - top to bottom
                drawLineCheck(start, 0.3, 0.8, 0.9, 2.4, size, color);
                //along
                drawLineCheck(start, 1.5, 0.8, size, color);
                break;
            case 'w':
                // left - top to bottom
                drawLineCheck(start, 0.3, 0.8, 0.9, 2.4, size, color);
                //along
                drawLineCheck(start, 1.5, 0.8, size, color);
                drawLineCheck(start, 2.1, 2.4, size, color);
                drawLineCheck(start, 2.7, 0.8, size, color);
                break;                
            case 'x':
                // \ - top to bottom
                drawLineCheck(start, 0.3, 0.8, 1.5, 2.4, size, color);
                // / - top to bottom
                drawLineCheck(start, 1.5, 0.8, 0.3, 2.4, size, color);
                break;                
            case 'y':
                // / - top to bottom
                drawLineCheck(start, 1.5, 0.8, 0.5, 3.2, size, color);
                // \ top to bottom
                drawLineCheck(start, 0.3, 0.8, 0.85, 2.3, size, color);
                break;                
            case 'z':
                //bottom - right to left
                drawLineCheck(start, 1.6, 2.4, 0.3, 2.4, size, color);
                //along
                drawLineCheck(start, 1.6, 0.8, size, color);
                drawLineCheck(start, 0.3, 0.8, size, color);
                break;                
            case ' ':
                break;
        }
    }
    
    /**
     * Zeichnet einen Großbuchstaben
     * 
     * @param start oberer linker Punkt des Buchstaben
     * @param size Schriftgröße
     * @param c Großbuchstabe, der gezeichnet werden soll
     * @param color Farbe
     */
    public void letterUpperCase(Vector2 start, int size, char c, TurtleColor color)
    {
        double x = start.getX();
        double y = start.getY();
        switch(c)
        {
            // all must bound l/r at 0.3 and t/b at 0.2
            case 'A':
                // / - bottom to top
                drawLineCheck(start, 0.3, 2.4, 1.2, 0.2, size, color);
                // along
                drawLineCheck(start, 2.1, 2.4, size, color);
                // middle - left to right
                drawLineCheck(start, 0.6, 1.8, 1.8, 1.8, size, color);
                break;
            case 'B':
                //bottom - left to right
                drawLineCheck(start, 0.3, 2.4, 1.7, 2.4, size, color);
                //along
                drawLineCheck(start, 2.0, 2.1, size, color);
                drawLineCheck(start, 2.0, 1.6, size, color);
                drawLineCheck(start, 1.7, 1.3, size, color);
                drawLineCheck(start, 0.3, 1.3, size, color);
                //next / - bottom to top
                drawLineCheck(start, 1.6, 1.3, 1.9, 1.0, size, color);
                //along
                drawLineCheck(start, 1.9, 0.5, size, color);
                drawLineCheck(start, 1.6, 0.2, size, color);
                drawLineCheck(start, 0.3, 0.2, size, color);
                drawLineCheck(start, 0.3, 2.4, size, color);
                break;
            case 'C':
                //from bottom right around
                drawLineCheck(start, 2.1, 2.0, 2.1, 2.2, size, color);
                //along
                drawLineCheck(start, 1.9, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 0.4, size, color);
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.9, 0.2, size, color);
                drawLineCheck(start, 2.1, 0.4, size, color);
                drawLineCheck(start, 2.1, 0.6, size, color);
                break;
            case 'D':
                //from bottom left around counter clockwise
                drawLineCheck(start, 0.3, 2.4, 1.7, 2.4, size, color);
                //along
                drawLineCheck(start, 2.1, 1.9, size, color);
                drawLineCheck(start, 2.1, 0.7, size, color);
                drawLineCheck(start, 1.7, 0.2, size, color);
                drawLineCheck(start, 0.3, 0.2, size, color);
                drawLineCheck(start, 0.3, 2.4, size, color);
                break;
            case 'E':
                //top - right to left
                drawLineCheck(start, 1.9, 0.2, 0.3, 0.2, size, color);
                //along
                drawLineCheck(start, 0.3, 2.4, size, color);
                drawLineCheck(start, 1.9, 2.4, size, color);
                //middle - left to right
                drawLineCheck(start, 0.3, 1.3, 1.7, 1.3, size, color);
                break;
            case 'F':
                //right - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.2, size, color);
                //along
                drawLineCheck(start, 1.8, 0.2, size, color);
                //middle - left to right
                drawLineCheck(start, 0.3, 1.3, 1.6, 1.3, size, color);
                break;
            case 'G':
                // middle - left to right
                drawLineCheck(start, 1.3, 1.4, 2.1, 1.4, size, color);
                //along
                drawLineCheck(start, 2.1, 2.2, size, color);
                drawLineCheck(start, 1.9, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 0.4, size, color);
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.9, 0.2, size, color);
                drawLineCheck(start, 2.1, 0.4, size, color);
                drawLineCheck(start, 2.1, 0.6, size, color);
                break;
            case 'H':
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size, color);
                //right - top to bottom
                drawLineCheck(start, 2.1, 0.2, 2.1, 2.4, size, color);
                //middle - left to right
                drawLineCheck(start, 0.3, 1.3, 2.1, 1.3, size, color);
                break;
            case 'I':
                //top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size, color);
                break;
            case 'J':
                //bottom left
                drawLineCheck(start, 0.3, 1.9, 0.3, 2.2, size, color);
                //around
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 1.5, 2.4, size, color);
                drawLineCheck(start, 1.7, 2.2, size, color);
                drawLineCheck(start, 1.7, 0.2, size, color);
                break;
            case 'K':
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size, color);
                //middle - left to right
                drawLineCheck(start, 0.3, 1.3, 0.6, 1.3, size, color);
                // / - top to bottom
                drawLineCheck(start, 1.8, 0.2, 0.6, 1.3, size, color);
                //along
                drawLineCheck(start, 1.8, 2.4, size, color);
                break;                
            case 'L':
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size, color);
                //along
                drawLineCheck(start, 1.7, 2.4, size, color);
                break;                
            case 'M':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.2, size, color);
                //along
                drawLineCheck(start, 1.5, 2.4, size, color);
                drawLineCheck(start, 2.7, 0.2, size, color);
                drawLineCheck(start, 2.7, 2.4, size, color);
                break;                
            case 'N':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.2, size, color);
                //along
                drawLineCheck(start, 2.1, 2.4, size, color);
                drawLineCheck(start, 2.1, 0.2, size, color);
                break;
            case 'O':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.2, 0.3, 0.4, size, color);
                //along
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.9, 0.2, size, color);
                drawLineCheck(start, 2.1, 0.4, size, color);
                drawLineCheck(start, 2.1, 2.2, size, color);
                drawLineCheck(start, 1.9, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                break;                
            case 'P':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.2, size, color);
                //along
                drawLineCheck(start, 1.7, 0.2, size, color);
                drawLineCheck(start, 2.0, 0.5, size, color);
                drawLineCheck(start, 2.0, 1.1, size, color);
                drawLineCheck(start, 1.7, 1.4, size, color);
                drawLineCheck(start, 0.3, 1.4, size, color);
                break;                
            case 'Q':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.2, 0.3, 0.4, size, color);
                //along
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.9, 0.2, size, color);
                drawLineCheck(start, 2.1, 0.4, size, color);
                drawLineCheck(start, 2.1, 2.2, size, color);
                drawLineCheck(start, 1.9, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                //der Fummel - bottom to top
                drawLineCheck(start, 2.1, 2.5, 1.7, 2.1, size, color);
                break;                
            case 'R':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.2, size, color);
                //along
                drawLineCheck(start, 1.7, 0.2, size, color);
                drawLineCheck(start, 2.0, 0.5, size, color);
                drawLineCheck(start, 2.0, 1.1, size, color);
                drawLineCheck(start, 1.7, 1.4, size, color);
                drawLineCheck(start, 0.3, 1.4, size, color);
                //dat Bein - top to bottom
                drawLineCheck(start, 1.4, 1.5, 1.9, 2.4, size, color);
                break;
            case 'S':
                //start - bottom left
                drawLineCheck(start, 0.3, 2.0, 0.3, 2.2, size, color);
                //along
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 1.8, 2.4, size, color);
                drawLineCheck(start, 2.0, 2.2, size, color);
                drawLineCheck(start, 2.0, 1.5, size, color);
                drawLineCheck(start, 1.8, 1.3, size, color);
                drawLineCheck(start, 0.5, 1.3, size, color);
                drawLineCheck(start, 0.3, 1.1, size, color);
                drawLineCheck(start, 0.3, 0.4, size, color);
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.8, 0.2, size, color);
                drawLineCheck(start, 2.0, 0.4, size, color);
                drawLineCheck(start, 2.0, 0.6, size, color);
                break;                
            case 'T':
                //top - left to right
                drawLineCheck(start, 0.3, 0.2, 2.1, 0.2, size, color);
                //middle - top to bottom
                drawLineCheck(start, 1.2, 0.2, 1.2, 2.4, size, color);
                break;                
            case 'U':
                //start - top left
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 1.9, 2.4, size, color);
                drawLineCheck(start, 2.1, 2.2, size, color);
                drawLineCheck(start, 2.1, 0.2, size, color);
                break;                
            case 'V':
                //start - top left
                drawLineCheck(start, 0.3, 0.2, 1.2, 2.4, size, color);
                //along
                drawLineCheck(start, 2.1, 0.2, size, color);
                break;
            case 'W':
                //start - top left
                drawLineCheck(start, 0.3, 0.2, 0.9, 2.4, size, color);
                //along
                drawLineCheck(start, 1.7, 0.2, size, color);
                drawLineCheck(start, 2.5, 2.4, size, color);
                drawLineCheck(start, 3.1, 0.2, size, color);
                break;                
            case 'X':
                // \ - top to bottom
                drawLineCheck(start, 0.3, 0.2, 1.9, 2.4, size, color);
                // / - top to bottom
                drawLineCheck(start, 1.9, 0.2, 0.3, 2.4, size, color);
                break;                
            case 'Y':
                // \ - top to bottom
                drawLineCheck(start, 0.3, 0.2, 1.2, 1.5, size, color);
                //along
                drawLineCheck(start, 2.1, 0.2, size, color);
                //middle - top to bottom
                drawLineCheck(start, 1.2, 1.5, 1.2, 2.4, size, color);
                break;                
            case 'Z':
                //start - top left
                drawLineCheck(start, 0.3, 0.2, 2.0, 0.2, size, color);
                //along
                drawLineCheck(start, 0.3, 2.4, size, color);
                drawLineCheck(start, 2.0, 2.4, size, color);
                break;
        }
    }
    
    /**
     * Zeichnet eine Symbol
     * 
     * @param start oberer linker Punkt des Symbols
     * @param size Schriftgröße
     * @param c Symbol, das gezeichnet werden soll
     * @param color Farbe
     */
    public void letterSymbols(Vector2 start, int size, char c, TurtleColor color)
    {
        double x = start.getX();
        double y = start.getY();
        switch(c)
        {
            // all must bound l/r at 0.3 and t/b at 0.2
            case ' ':
                break;
            case '?':
                //start - top left
                drawLineCheck(start, 0.3, 0.6, 0.3, 0.4, size, color);
                //along
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.4, 0.2, size, color);
                drawLineCheck(start, 1.6, 0.4, size, color);
                drawLineCheck(start, 1.6, 0.8, size, color);
                drawLineCheck(start, 0.8, 1.3, size, color);
                drawLineCheck(start, 0.8, 1.5, size, color);
                //dot - top to bottom
                drawLineCheck(start, 0.8, 2.2, 0.8, 2.4, size, color);
                break;
            case '!':
                //middle - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 1.5, size, color);
                //dot - top to bottom
                drawLineCheck(start, 0.3, 2.2, 0.3, 2.4, size, color);
                break;  
            case '/':
                // / - top to bottom
                drawLineCheck(start, 1.1, 0.2, 0.3, 2.4, size, color);
                break;
            case 'ß':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.4, size, color);
                //along
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.5, 0.2, size, color);
                drawLineCheck(start, 1.7, 0.4, size, color);
                drawLineCheck(start, 1.7, 0.7, size, color);
                drawLineCheck(start, 0.9, 1.0, size, color);
                drawLineCheck(start, 0.9, 1.3, size, color);
                drawLineCheck(start, 1.1, 1.5, size, color);
                drawLineCheck(start, 1.6, 1.6, size, color);
                drawLineCheck(start, 1.8, 1.8, size, color);
                drawLineCheck(start, 1.8, 2.2, size, color);
                drawLineCheck(start, 1.6, 2.4, size, color);
                drawLineCheck(start, 0.9, 2.4, size, color);
                break;
            case ':':
                //top dot - top to bottom
                drawLineCheck(start, 0.3, 0.8, 0.3, 1.0, size, color);
                //bottom dot - top to bottom
                drawLineCheck(start, 0.3, 2.2, 0.3, 2.4, size, color);
                break;
            case '.':
                //bottom dot - top to bottom
                drawLineCheck(start, 0.3, 2.2, 0.3, 2.4, size, color);
                break;
            case ',':
                //bottom dot - top to bottom
                drawLineCheck(start, 0.3, 2.2, 0.3, 2.5, size, color);
                drawLineCheck(start, 0.0, 2.7, size, color);
                break;
            case '_':
                drawLineCheck(start, 0.3, 2.4, 1.6, 2.4, size, color);
                break;
            case '-':
                drawLineCheck(start, 0.3, 1.6, 0.9, 1.6, size, color);
                break;
        }
    }
    
    /**
     * Zeichnet eine Zahl
     * 
     * @param start oberer linker Punkt der Zahl
     * @param size Schriftgröße
     * @param c Zahl, die gezeichnet werden soll
     * @param color Farbe
     */
    public void letterNumber(Vector2 start, int size, char c, TurtleColor color)
    {
        double x = start.getX();
        double y = start.getY();
        switch(c)
        {
            // all must bound l/r at 0.3 and t/b at 0.2
            case '0':
                //right - top to bottom
                drawLineCheck(start, 1.8, 0.4, 1.8, 2.2, size, color);
                //along
                drawLineCheck(start, 1.6, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 0.4, size, color);
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.6, 0.2, size, color);
                drawLineCheck(start, 1.8, 0.4, size, color);
                break;
            case '1':
                //top - left to right
                drawLineCheck(start, 0.3, 0.2, 0.8, 0.2, size, color);
                //along
                drawLineCheck(start, 0.8, 2.4, size, color);
                break;
            case '2':
                //start - top left
                drawLineCheck(start, 0.3, 0.6, 0.3, 0.4, size, color);
                //along
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.6, 0.2, size, color);
                drawLineCheck(start, 1.8, 0.4, size, color);
                drawLineCheck(start, 1.8, 1.0, size, color);
                drawLineCheck(start, 1.6, 1.2, size, color);
                drawLineCheck(start, 0.5, 1.7, size, color);
                drawLineCheck(start, 0.3, 1.9, size, color);
                drawLineCheck(start, 0.3, 2.4, size, color);
                drawLineCheck(start, 1.8, 2.4, size, color);
                drawLineCheck(start, 1.8, 2.0, size, color);
                break;
            case '3':
                //start - top left
                drawLineCheck(start, 0.3, 0.6, 0.3, 0.4, size, color);
                //along
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.6, 0.2, size, color);
                drawLineCheck(start, 1.8, 0.4, size, color);
                drawLineCheck(start, 1.8, 1.1, size, color);
                drawLineCheck(start, 1.6, 1.3, size, color);
                drawLineCheck(start, 1.8, 1.5, size, color);
                drawLineCheck(start, 1.8, 2.2, size, color);
                drawLineCheck(start, 1.6, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 2.0, size, color);
                //middle - left to right
                drawLineCheck(start, 0.8, 1.3, 1.6, 1.3, size, color);
                break;
            case '4':
                //left - bottom to top
                drawLineCheck(start, 1.5, 2.4, 1.5, 0.2, size, color);
                //along
                drawLineCheck(start, 0.3, 1.7, size, color);
                drawLineCheck(start, 0.3, 1.9, size, color);
                drawLineCheck(start, 1.8, 1.9, size, color);
                break;
            case '5':
                //top - right to left
                drawLineCheck(start, 1.8, 0.2, 0.3, 0.2, size, color);
                //along
                drawLineCheck(start, 0.3, 1.4, size, color);
                drawLineCheck(start, 1.6, 1.4, size, 0, color);
                drawLineCheck(start, 1.8, 1.5, size, color);
                drawLineCheck(start, 1.8, 2.2, size, color);
                drawLineCheck(start, 1.6, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 2.0, size, color);
                break;
            case '6':
                //middle - left to right
                drawLineCheck(start, 0.3, 1.3, 1.6, 1.3, size, 0, color);
                drawLineCheck(start, 1.8, 1.5, size, color);
                drawLineCheck(start, 1.8, 2.2, size, color);
                drawLineCheck(start, 1.6, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 0.4, size, color);
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.6, 0.2, size, color);
                drawLineCheck(start, 1.8, 0.4, size, color);
                drawLineCheck(start, 1.8, 0.6, size, color);
                break;
            case '7':
                //top - left to right
                drawLineCheck(start, 0.3, 0.2, 1.8, 0.2, size, color);
                //along
                drawLineCheck(start, 0.6, 2.4, size, color);
                break;
            case '8':
                //middle - left to right
                drawLineCheck(start, 0.5, 1.3, 1.6, 1.3, size, 0, color);
                drawLineCheck(start, 1.8, 1.5, size, color);
                drawLineCheck(start, 1.8, 2.2, size, color);
                drawLineCheck(start, 1.6, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 1.5, size, color);
                drawLineCheck(start, 0.5, 1.3, size, color);
                drawLineCheck(start, 0.3, 1.1, size, color);
                drawLineCheck(start, 0.3, 0.4, size, color);
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.6, 0.2, size, color);
                drawLineCheck(start, 1.8, 0.4, size, color);
                drawLineCheck(start, 1.8, 1.1, size, color);
                drawLineCheck(start, 1.6, 1.3, size, color);
                break;
            case '9':
                //middle - right to left
                drawLineCheck(start, 1.8, 1.3, 0.5, 1.3, size, color);
                //along
                drawLineCheck(start, 0.3, 1.1, size, color);
                drawLineCheck(start, 0.3, 0.4, size, color);
                drawLineCheck(start, 0.5, 0.2, size, color);
                drawLineCheck(start, 1.6, 0.2, size, color);
                drawLineCheck(start, 1.8, 0.4, size, color);
                drawLineCheck(start, 1.8, 2.2, size, color);
                drawLineCheck(start, 1.6, 2.4, size, color);
                drawLineCheck(start, 0.5, 2.4, size, color);
                drawLineCheck(start, 0.3, 2.2, size, color);
                drawLineCheck(start, 0.3, 2.0, size, color);
                break;
        }
    }
}
