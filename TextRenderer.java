
/**
 * Beschreiben Sie hier die Klasse TextRenderer.
 * 
 * @author Nico Hädicke
 * @version 01.12.2021
 */
public class TextRenderer
{
    private Renderer _renderer;
    private Turtle _turtle;
    private Vector2 _lastPos;
    
    public TextRenderer(Renderer renderer)
    {
        _renderer = renderer;
        _turtle = new Turtle();
        _lastPos = new Vector2(0, 0);
    }

    public void write(Vector2 start, int size, String text)
    {
        //24x26
        int l = text.length();
        String letter = "";
        Vector2 origStart = new Vector2(start.getX(), start.getY());
        double buffer = 0.1*(size/2);
        for(int i = 0; i < l; i++)
        {
            if(Character.toString(text.charAt(i)).matches("[0-9]"))
            {
                letterNumber(start, size, text.charAt(i));
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
                letterUpperCase(start, size, text.charAt(i));
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
                letterLowerCase(start, size, text.charAt(i));
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
                letterSymbols(start, size, text.charAt(i));
                switch(text.charAt(i))
                {
                    case '\n':
                        start.setX(origStart.getX());
                        start.setY(origStart.getY()+3.2*size+0.3*size);
                        origStart.setY(start.getY());
                        break;
                    case '!':
                        start.setX(start.getX()+0.6*size+buffer);
                        break;
                    case '/':
                        start.setX(start.getX()+1.4*size+buffer);
                        break;
                    case 'ß':
                        start.setX(start.getX()+2.1*size+buffer);
                        break;
                    default:
                        start.setX(start.getX()+1.9*size+buffer);
                }
            }
        }
    }
    
    public void drawLineCheck(Vector2 start, double wx2, double wy2, int size)
    {
        double x = start.getX();
        double y = start.getY();
        double wx1 = _lastPos.getX();
        double wy1 = _lastPos.getY();
        
        _renderer.drawLine(new Vector2(x+wx1*size, y+wy1*size), new Vector2(x+wx2*size, y+wy2*size));
        if(wx1 == wx2)
        {
            if(wx1 > 1)
            {
                _renderer.drawLine(new Vector2((x+wx1*size)+1, y+wy1*size), new Vector2((x+wx2*size)+1, y+wy2*size));
            }
            else
            {
                _renderer.drawLine(new Vector2((x+wx1*size)-1, y+wy1*size), new Vector2((x+wx2*size)-1, y+wy2*size));
            }
        }
        else if(wy1 == wy2)
        {
            if(wy1 > 1)
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)+1), new Vector2(x+wx2*size, (y+wy2*size)+1));
            }
            else
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)-1), new Vector2(x+wx2*size, (y+wy2*size)-1));
            }
        }
        _lastPos.setX(wx2);
        _lastPos.setY(wy2);
    }
    
    public void drawLineCheck(Vector2 start, double wx1, double wy1, double wx2, double wy2, int size)
    {
        double x = start.getX();
        double y = start.getY();
        
        _renderer.drawLine(new Vector2(x+wx1*size, y+wy1*size), new Vector2(x+wx2*size, y+wy2*size));
        if(wx1 == wx2)
        {
            if(wx1 > 1)
            {
                _renderer.drawLine(new Vector2((x+wx1*size)+1, y+wy1*size), new Vector2((x+wx2*size)+1, y+wy2*size));
            }
            else
            {
                _renderer.drawLine(new Vector2((x+wx1*size)-1, y+wy1*size), new Vector2((x+wx2*size)-1, y+wy2*size));
            }
        }
        else if(wy1 == wy2)
        {
            if(wy1 > 1)
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)+1), new Vector2(x+wx2*size, (y+wy2*size)+1));
            }
            else
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)-1), new Vector2(x+wx2*size, (y+wy2*size)-1));
            }
        }
        
        _lastPos.setX(wx2);
        _lastPos.setY(wy2);
    }
    
    public void drawLineCheck(Vector2 start, double wx2, double wy2, int size, int direction)
    {
        double x = start.getX();
        double y = start.getY();
        double wx1 = _lastPos.getX();
        double wy1 = _lastPos.getY();
        
        _renderer.drawLine(new Vector2(x+wx1*size, y+wy1*size), new Vector2(x+wx2*size, y+wy2*size));
        if(wx1 == wx2)
        {
            if(direction == 1)
            {
                _renderer.drawLine(new Vector2((x+wx1*size)+1, y+wy1*size), new Vector2((x+wx2*size)+1, y+wy2*size));
            }
            else
            {
                _renderer.drawLine(new Vector2((x+wx1*size)-1, y+wy1*size), new Vector2((x+wx2*size)-1, y+wy2*size));
            }
        }
        else if(wy1 == wy2)
        {
            if(direction == 1)
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)+1), new Vector2(x+wx2*size, (y+wy2*size)+1));
            }
            else
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)-1), new Vector2(x+wx2*size, (y+wy2*size)-1));
            }
        }
        _lastPos.setX(wx2);
        _lastPos.setY(wy2);
    }
    
    public void drawLineCheck(Vector2 start, double wx1, double wy1, double wx2, double wy2, int size, int direction)
    {
        double x = start.getX();
        double y = start.getY();
        
        _renderer.drawLine(new Vector2(x+wx1*size, y+wy1*size), new Vector2(x+wx2*size, y+wy2*size));
        if(wx1 == wx2)
        {
            if(direction == 1)
            {
                _renderer.drawLine(new Vector2((x+wx1*size)+1, y+wy1*size), new Vector2((x+wx2*size)+1, y+wy2*size));
            }
            else
            {
                _renderer.drawLine(new Vector2((x+wx1*size)-1, y+wy1*size), new Vector2((x+wx2*size)-1, y+wy2*size));
            }
        }
        else if(wy1 == wy2)
        {
            if(direction == 1)
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)+1), new Vector2(x+wx2*size, (y+wy2*size)+1));
            }
            else
            {
                _renderer.drawLine(new Vector2(x+wx1*size, (y+wy1*size)-1), new Vector2(x+wx2*size, (y+wy2*size)-1));
            }
        }
        
        _lastPos.setX(wx2);
        _lastPos.setY(wy2);
    }
    
    public void letterLowerCase(Vector2 start, int size,char c)
    {
        double x = start.getX();
        double y = start.getY();
        switch(c)
        {
            // all must bound l/r at 0.3 and t/b at 0.2
            case 'a':
                //right - bottom to top
                drawLineCheck(start, 1.7, 2.4, 1.7, 1.0, size);
                //along
                drawLineCheck(start, 1.5, 0.8, size);
                drawLineCheck(start, 0.5, 0.8, size);
                //middle - right to left
                drawLineCheck(start, 1.7, 1.5, 0.6, 1.5, size);
                drawLineCheck(start, 0.4, 1.7, size);
                drawLineCheck(start, 0.4, 2.2, size);
                drawLineCheck(start, 0.6, 2.4, size);
                drawLineCheck(start, 1.3, 2.4, size);
                drawLineCheck(start, 1.7, 2.2, size);
                break;
            case 'b':
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.2, size);
                //along
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 1.4, 2.4, size);
                drawLineCheck(start, 1.6, 2.2, size);
                drawLineCheck(start, 1.6, 1.0, size);
                drawLineCheck(start, 1.4, 0.8, size);
                drawLineCheck(start, 0.7, 0.8, size);
                drawLineCheck(start, 0.3, 1.0, size);
                break;
            case 'c':
                //start - bottom right
                drawLineCheck(start, 1.6, 2.0, 1.6, 2.2, size);
                //along
                drawLineCheck(start, 1.4, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 1.0, size);
                drawLineCheck(start, 0.5, 0.8, size);
                drawLineCheck(start, 1.4, 0.8, size);
                drawLineCheck(start, 1.6, 1.0, size);
                drawLineCheck(start, 1.6, 1.2, size);
                break;
            case 'd':
                //start - bottom right
                drawLineCheck(start, 1.6, 2.2, 1.2, 2.4, size);
                //along
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 1.0, size);
                drawLineCheck(start, 0.5, 0.8, size);
                drawLineCheck(start, 1.6, 0.8, size);
                //right - top to bottom
                drawLineCheck(start, 1.6, 0.2, 1.6, 2.4, size);
                break;
            case 'e':
                //bottom - right to left
                drawLineCheck(start, 1.6, 2.4, 0.5, 2.4, size);
                //along
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 1.0, size);
                drawLineCheck(start, 0.5, 0.8, size);
                drawLineCheck(start, 1.4, 0.8, size);
                drawLineCheck(start, 1.6, 1.0, size);
                drawLineCheck(start, 1.6, 1.6, size);
                drawLineCheck(start, 0.3, 1.6, size);
                break;
            case 'f':
                //middle - bottom to top
                drawLineCheck(start, 0.8, 2.4, 0.8, 0.4, size);
                //along
                drawLineCheck(start, 1.0, 0.2, size);
                drawLineCheck(start, 1.5, 0.2, size);
                //middle - left to right
                drawLineCheck(start, 0.3, 0.9, 1.5, 0.9, size);
                break;
            case 'g':
                //right - top to bottom
                drawLineCheck(start, 1.6, 0.8, 1.6, 3.0, size);
                //along
                drawLineCheck(start, 1.4, 3.2, size);
                drawLineCheck(start, 0.4, 3.2, size);
                //middle - right to left
                drawLineCheck(start, 1.6, 2.4, 0.5, 2.4, size);
                //along
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 1.0, size);
                drawLineCheck(start, 0.5, 0.8, size);
                drawLineCheck(start, 1.2, 0.8, size);
                drawLineCheck(start, 1.6, 1.0, size);
                break;
            case 'h':
                //right - bottom to top
                drawLineCheck(start, 1.6, 2.4, 1.6, 1.0, size);
                //along
                drawLineCheck(start, 1.4, 0.8, size);
                drawLineCheck(start, 0.7, 0.8, size);
                drawLineCheck(start, 0.3, 1.0, size);
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size);
                break;
            case 'i':
                //dot - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 0.3, size);
                //middle - top to bottom
                drawLineCheck(start, 0.3, 0.8, 0.3, 2.4, size);
                break;
            case 'j':
                //dot - top to bottom
                drawLineCheck(start, 0.6, 0.2, 0.6, 0.3, size, 1);
                //middle - top to bottom
                drawLineCheck(start, 0.6, 0.8, 0.6, 3.0, size, 1);
                //along
                drawLineCheck(start, 0.4, 3.2, size);
                drawLineCheck(start, 0.2, 3.2, size);
                break;
            case 'k':
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size);
                //middle - left to right
                drawLineCheck(start, 0.3, 1.6, 0.5, 1.6, size);
                // / - top to bottom
                drawLineCheck(start, 1.4, 0.8, 0.5, 1.6, size);
                //along
                drawLineCheck(start, 1.4, 2.4, size);
                break;                
            case 'l':
                //middle - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size);
                break;                
            case 'm':
                //right - bottom to top
                drawLineCheck(start, 2.7, 2.4, 2.7, 1.0, size);
                //along
                drawLineCheck(start, 2.5, 0.8, size);
                drawLineCheck(start, 1.9, 0.8, size);
                drawLineCheck(start, 1.5, 1.0, size);
                drawLineCheck(start, 1.3, 0.8, size);
                drawLineCheck(start, 0.7, 0.8, size);
                drawLineCheck(start, 0.3, 1.0, size);
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.8, size);
                //middle - bottom to top
                drawLineCheck(start, 1.5, 2.4, 1.5, 1.0, size);
                break;                
            case 'n':
                //right - bottom to top
                drawLineCheck(start, 1.6, 2.4, 1.6, 1.0, size);
                //along
                drawLineCheck(start, 1.4, 0.8, size);
                drawLineCheck(start, 0.7, 0.8, size);
                drawLineCheck(start, 0.3, 1.0, size);
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.8, size);
                break;
            case 'o':
                //start - bottom right
                drawLineCheck(start, 1.6, 1.0, 1.6, 2.2, size);
                //along
                drawLineCheck(start, 1.4, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 1.0, size);
                drawLineCheck(start, 0.5, 0.8, size);
                drawLineCheck(start, 1.4, 0.8, size);
                drawLineCheck(start, 1.6, 1.0, size);
                break;                
            case 'p':
                //start - top left
                drawLineCheck(start, 0.3, 1.0, 0.7, 0.8, size);
                //along
                drawLineCheck(start, 1.4, 0.8, size);
                drawLineCheck(start, 1.6, 1.0, size);
                drawLineCheck(start, 1.6, 2.2, size);
                drawLineCheck(start, 1.4, 2.4, size);
                drawLineCheck(start, 0.3, 2.4, size);
                //left - bottom to top
                drawLineCheck(start, 0.3, 3.2, 0.3, 0.8, size);
                break;                
            case 'q':
                //start - middle right
                drawLineCheck(start, 1.6, 2.2, 1.2, 2.4, size);
                //along
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 1.0, size);
                drawLineCheck(start, 0.5, 0.8, size);
                drawLineCheck(start, 1.4, 0.8, size);
                drawLineCheck(start, 1.6, 1.0, size);
                drawLineCheck(start, 1.6, 3.2, size);
                break;                
            case 'r':
                //start - right
                drawLineCheck(start, 1.4, 1.2, 1.4, 1.0, size);
                //along
                drawLineCheck(start, 1.2, 0.8, size);
                drawLineCheck(start, 0.7, 0.8, size);
                drawLineCheck(start, 0.3, 1.0, size);
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.8, size);
                break;
            case 's':
                //start - bottom left
                drawLineCheck(start, 0.3, 2.4, 1.4, 2.4, size);
                //along
                drawLineCheck(start, 1.6, 2.2, size);
                drawLineCheck(start, 1.6, 1.8, size);
                drawLineCheck(start, 1.4, 1.6, size);
                drawLineCheck(start, 0.5, 1.6, size, 0);
                drawLineCheck(start, 0.5, 1.5, 0.3, 1.3, size);
                drawLineCheck(start, 0.3, 0.9, size);
                drawLineCheck(start, 0.5, 0.7, size);
                drawLineCheck(start, 1.6, 0.7, size);
                break;                
            case 't':
                //middle - top to bottom
                drawLineCheck(start, 0.8, 0.2, 0.8, 2.2, size);
                //along
                drawLineCheck(start, 1.0, 2.4, size);
                drawLineCheck(start, 1.5, 2.4, size);
                //middle - left to right
                drawLineCheck(start, 0.3, 0.8, 1.5, 0.8, size);
                break;                
            case 'u':
                drawLineCheck(start, 0.3, 0.8, 0.3, 2.2, size);
                //along
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 1.2, 2.4, size);
                drawLineCheck(start, 1.6, 2.2, size);
                //right - bottom to top
                drawLineCheck(start, 1.6, 2.4, 1.6, 0.8, size);
                break;                
            case 'v':
                // \ - top to bottom
                drawLineCheck(start, 0.3, 0.8, 0.9, 2.4, size);
                //along
                drawLineCheck(start, 1.5, 0.8, size);
                break;
            case 'w':
                // left - top to bottom
                drawLineCheck(start, 0.3, 0.8, 0.9, 2.4, size);
                //along
                drawLineCheck(start, 1.5, 0.8, size);
                drawLineCheck(start, 2.1, 2.4, size);
                drawLineCheck(start, 2.7, 0.8, size);
                break;                
            case 'x':
                // \ - top to bottom
                drawLineCheck(start, 0.3, 0.8, 1.5, 2.4, size);
                // / - top to bottom
                drawLineCheck(start, 1.5, 0.8, 0.3, 2.4, size);
                break;                
            case 'y':
                // / - top to bottom
                drawLineCheck(start, 1.5, 0.8, 0.5, 3.2, size);
                // \ top to bottom
                drawLineCheck(start, 0.3, 0.8, 0.85, 2.3, size);
                break;                
            case 'z':
                //bottom - right to left
                drawLineCheck(start, 1.6, 2.4, 0.3, 2.4, size);
                //along
                drawLineCheck(start, 1.6, 0.8, size);
                drawLineCheck(start, 0.3, 0.8, size);
                break;                
            case ' ':
                break;
        }
    }
    
    public void letterUpperCase(Vector2 start, int size,char c)
    {
        double x = start.getX();
        double y = start.getY();
        switch(c)
        {
            // all must bound l/r at 0.3 and t/b at 0.2
            case 'A':
                // / - bottom to top
                drawLineCheck(start, 0.3, 2.4, 1.2, 0.2, size);
                // along
                drawLineCheck(start, 2.1, 2.4, size);
                // middle - left to right
                drawLineCheck(start, 0.6, 1.8, 1.8, 1.8, size);
                break;
            case 'B':
                //bottom - left to right
                drawLineCheck(start, 0.3, 2.4, 1.7, 2.4, size);
                //along
                drawLineCheck(start, 2.0, 2.1, size);
                drawLineCheck(start, 2.0, 1.6, size);
                drawLineCheck(start, 1.7, 1.3, size);
                drawLineCheck(start, 0.3, 1.3, size);
                //next / - bottom to top
                drawLineCheck(start, 1.6, 1.3, 1.9, 1.0, size);
                //along
                drawLineCheck(start, 1.9, 0.5, size);
                drawLineCheck(start, 1.6, 0.2, size);
                drawLineCheck(start, 0.3, 0.2, size);
                drawLineCheck(start, 0.3, 2.4, size);
                break;
            case 'C':
                //from bottom right around
                drawLineCheck(start, 2.1, 2.0, 2.1, 2.2, size);
                //along
                drawLineCheck(start, 1.9, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 0.4, size);
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.9, 0.2, size);
                drawLineCheck(start, 2.1, 0.4, size);
                drawLineCheck(start, 2.1, 0.6, size);
                break;
            case 'D':
                //from bottom left around counter clockwise
                drawLineCheck(start, 0.3, 2.4, 1.7, 2.4, size);
                //along
                drawLineCheck(start, 2.1, 1.9, size);
                drawLineCheck(start, 2.1, 0.7, size);
                drawLineCheck(start, 1.7, 0.2, size);
                drawLineCheck(start, 0.3, 0.2, size);
                drawLineCheck(start, 0.3, 2.4, size);
                break;
            case 'E':
                //top - right to left
                drawLineCheck(start, 1.9, 0.2, 0.3, 0.2, size);
                //along
                drawLineCheck(start, 0.3, 2.4, size);
                drawLineCheck(start, 1.9, 2.4, size);
                //middle - left to right
                drawLineCheck(start, 0.3, 1.3, 1.7, 1.3, size);
                break;
            case 'F':
                //right - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.2, size);
                //along
                drawLineCheck(start, 1.8, 0.2, size);
                //middle - left to right
                drawLineCheck(start, 0.3, 1.3, 1.6, 1.3, size);
                break;
            case 'G':
                // middle - left to right
                drawLineCheck(start, 1.3, 1.4, 2.1, 1.4, size);
                //along
                drawLineCheck(start, 2.1, 2.2, size);
                drawLineCheck(start, 1.9, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 0.4, size);
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.9, 0.2, size);
                drawLineCheck(start, 2.1, 0.4, size);
                drawLineCheck(start, 2.1, 0.6, size);
                break;
            case 'H':
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size);
                //right - top to bottom
                drawLineCheck(start, 2.1, 0.2, 2.1, 2.4, size);
                //middle - left to right
                drawLineCheck(start, 0.3, 1.3, 2.1, 1.3, size);
                break;
            case 'I':
                //top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size);
                break;
            case 'J':
                //bottom left
                drawLineCheck(start, 0.3, 1.9, 0.3, 2.2, size);
                //around
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 1.5, 2.4, size);
                drawLineCheck(start, 1.7, 2.2, size);
                drawLineCheck(start, 1.7, 0.2, size);
                break;
            case 'K':
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size);
                //middle - left to right
                drawLineCheck(start, 0.3, 1.3, 0.6, 1.3, size);
                // / - top to bottom
                drawLineCheck(start, 1.8, 0.2, 0.6, 1.3, size);
                //along
                drawLineCheck(start, 1.8, 2.4, size);
                break;                
            case 'L':
                //left - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.4, size);
                //along
                drawLineCheck(start, 1.7, 2.4, size);
                break;                
            case 'M':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.2, size);
                //along
                drawLineCheck(start, 1.5, 2.4, size);
                drawLineCheck(start, 2.7, 0.2, size);
                drawLineCheck(start, 2.7, 2.4, size);
                break;                
            case 'N':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.2, size);
                //along
                drawLineCheck(start, 2.1, 2.4, size);
                drawLineCheck(start, 2.1, 0.2, size);
                break;
            case 'O':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.2, 0.3, 0.4, size);
                //along
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.9, 0.2, size);
                drawLineCheck(start, 2.1, 0.4, size);
                drawLineCheck(start, 2.1, 2.2, size);
                drawLineCheck(start, 1.9, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                break;                
            case 'P':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.2, size);
                //along
                drawLineCheck(start, 1.7, 0.2, size);
                drawLineCheck(start, 2.0, 0.5, size);
                drawLineCheck(start, 2.0, 1.1, size);
                drawLineCheck(start, 1.7, 1.4, size);
                drawLineCheck(start, 0.3, 1.4, size);
                break;                
            case 'Q':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.2, 0.3, 0.4, size);
                //along
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.9, 0.2, size);
                drawLineCheck(start, 2.1, 0.4, size);
                drawLineCheck(start, 2.1, 2.2, size);
                drawLineCheck(start, 1.9, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                //der Fummel - bottom to top
                drawLineCheck(start, 2.1, 2.5, 1.7, 2.1, size);
                break;                
            case 'R':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.2, size);
                //along
                drawLineCheck(start, 1.7, 0.2, size);
                drawLineCheck(start, 2.0, 0.5, size);
                drawLineCheck(start, 2.0, 1.1, size);
                drawLineCheck(start, 1.7, 1.4, size);
                drawLineCheck(start, 0.3, 1.4, size);
                //dat Bein - top to bottom
                drawLineCheck(start, 1.4, 1.5, 1.9, 2.4, size);
                break;
            case 'S':
                //start - bottom left
                drawLineCheck(start, 0.3, 2.0, 0.3, 2.2, size);
                //along
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 1.8, 2.4, size);
                drawLineCheck(start, 2.0, 2.2, size);
                drawLineCheck(start, 2.0, 1.5, size);
                drawLineCheck(start, 1.8, 1.3, size);
                drawLineCheck(start, 0.5, 1.3, size);
                drawLineCheck(start, 0.3, 1.1, size);
                drawLineCheck(start, 0.3, 0.4, size);
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.8, 0.2, size);
                drawLineCheck(start, 2.0, 0.4, size);
                drawLineCheck(start, 2.0, 0.6, size);
                break;                
            case 'T':
                //top - left to right
                drawLineCheck(start, 0.3, 0.2, 2.1, 0.2, size);
                //middle - top to bottom
                drawLineCheck(start, 1.2, 0.2, 1.2, 2.4, size);
                break;                
            case 'U':
                //start - top left
                drawLineCheck(start, 0.3, 0.2, 0.3, 2.2, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 1.9, 2.4, size);
                drawLineCheck(start, 2.1, 2.2, size);
                drawLineCheck(start, 2.1, 0.2, size);
                break;                
            case 'V':
                //start - top left
                drawLineCheck(start, 0.3, 0.2, 1.2, 2.4, size);
                //along
                drawLineCheck(start, 2.1, 0.2, size);
                break;
            case 'W':
                //start - top left
                drawLineCheck(start, 0.3, 0.2, 0.9, 2.4, size);
                //along
                drawLineCheck(start, 1.7, 0.2, size);
                drawLineCheck(start, 2.5, 2.4, size);
                drawLineCheck(start, 3.1, 0.2, size);
                break;                
            case 'X':
                // \ - top to bottom
                drawLineCheck(start, 0.3, 0.2, 1.9, 2.4, size);
                // / - top to bottom
                drawLineCheck(start, 1.9, 0.2, 0.3, 2.4, size);
                break;                
            case 'Y':
                // \ - top to bottom
                drawLineCheck(start, 0.3, 0.2, 1.2, 1.5, size);
                //along
                drawLineCheck(start, 2.1, 0.2, size);
                //middle - top to bottom
                drawLineCheck(start, 1.2, 1.5, 1.2, 2.4, size);
                break;                
            case 'Z':
                //start - top left
                drawLineCheck(start, 0.3, 0.2, 2.0, 0.2, size);
                //along
                drawLineCheck(start, 0.3, 2.4, size);
                drawLineCheck(start, 2.0, 2.4, size);
                break;
        }
    }
    
    public void letterSymbols(Vector2 start, int size,char c)
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
                drawLineCheck(start, 0.3, 0.6, 0.3, 0.4, size);
                //along
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.4, 0.2, size);
                drawLineCheck(start, 1.6, 0.4, size);
                drawLineCheck(start, 1.6, 0.8, size);
                drawLineCheck(start, 0.8, 1.3, size);
                drawLineCheck(start, 0.8, 1.5, size);
                //dot - top to bottom
                drawLineCheck(start, 0.8, 2.2, 0.8, 2.4, size);
                break;
            case '!':
                //middle - top to bottom
                drawLineCheck(start, 0.3, 0.2, 0.3, 1.5, size);
                //dot - top to bottom
                drawLineCheck(start, 0.3, 2.2, 0.3, 2.4, size);
                break;  
            case '/':
                // / - top to bottom
                drawLineCheck(start, 1.1, 0.2, 0.3, 2.4, size);
                break;
            case 'ß':
                //left - bottom to top
                drawLineCheck(start, 0.3, 2.4, 0.3, 0.4, size);
                //along
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.5, 0.2, size);
                drawLineCheck(start, 1.7, 0.4, size);
                drawLineCheck(start, 1.7, 0.7, size);
                drawLineCheck(start, 0.9, 1.0, size);
                drawLineCheck(start, 0.9, 1.3, size);
                drawLineCheck(start, 1.1, 1.5, size);
                drawLineCheck(start, 1.6, 1.6, size);
                drawLineCheck(start, 1.8, 1.8, size);
                drawLineCheck(start, 1.8, 2.2, size);
                drawLineCheck(start, 1.6, 2.4, size);
                drawLineCheck(start, 0.9, 2.4, size);
                break;                
        }
    }
    
    public void letterNumber(Vector2 start, int size,char c)
    {
        double x = start.getX();
        double y = start.getY();
        switch(c)
        {
            // all must bound l/r at 0.3 and t/b at 0.2
            case '0':
                //right - top to bottom
                drawLineCheck(start, 1.8, 0.4, 1.8, 2.2, size);
                //along
                drawLineCheck(start, 1.6, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 0.4, size);
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.6, 0.2, size);
                drawLineCheck(start, 1.8, 0.4, size);
                break;
            case '1':
                //top - left to right
                drawLineCheck(start, 0.3, 0.2, 0.8, 0.2, size);
                //along
                drawLineCheck(start, 0.8, 2.4, size);
                break;
            case '2':
                //start - top left
                drawLineCheck(start, 0.3, 0.6, 0.3, 0.4, size);
                //along
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.6, 0.2, size);
                drawLineCheck(start, 1.8, 0.4, size);
                drawLineCheck(start, 1.8, 1.0, size);
                drawLineCheck(start, 1.6, 1.2, size);
                drawLineCheck(start, 0.5, 1.7, size);
                drawLineCheck(start, 0.3, 1.9, size);
                drawLineCheck(start, 0.3, 2.4, size);
                drawLineCheck(start, 1.8, 2.4, size);
                drawLineCheck(start, 1.8, 2.0, size);
                break;
            case '3':
                //start - top left
                drawLineCheck(start, 0.3, 0.6, 0.3, 0.4, size);
                //along
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.6, 0.2, size);
                drawLineCheck(start, 1.8, 0.4, size);
                drawLineCheck(start, 1.8, 1.1, size);
                drawLineCheck(start, 1.6, 1.3, size);
                drawLineCheck(start, 1.8, 1.5, size);
                drawLineCheck(start, 1.8, 2.2, size);
                drawLineCheck(start, 1.6, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 2.0, size);
                //middle - left to right
                drawLineCheck(start, 0.8, 1.3, 1.6, 1.3, size);
                break;
            case '4':
                //left - bottom to top
                drawLineCheck(start, 1.5, 2.4, 1.5, 0.2, size);
                //along
                drawLineCheck(start, 0.3, 1.7, size);
                drawLineCheck(start, 0.3, 1.9, size);
                drawLineCheck(start, 1.8, 1.9, size);
                break;
            case '5':
                //top - right to left
                drawLineCheck(start, 1.8, 0.2, 0.3, 0.2, size);
                //along
                drawLineCheck(start, 0.3, 1.4, size);
                drawLineCheck(start, 1.6, 1.4, size, 0);
                drawLineCheck(start, 1.8, 1.5, size);
                drawLineCheck(start, 1.8, 2.2, size);
                drawLineCheck(start, 1.6, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 2.0, size);
                break;
            case '6':
                //middle - left to right
                drawLineCheck(start, 0.3, 1.3, 1.6, 1.3, size,0);
                drawLineCheck(start, 1.8, 1.5, size);
                drawLineCheck(start, 1.8, 2.2, size);
                drawLineCheck(start, 1.6, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 0.4, size);
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.6, 0.2, size);
                drawLineCheck(start, 1.8, 0.4, size);
                drawLineCheck(start, 1.8, 0.6, size);
                break;
            case '7':
                //top - left to right
                drawLineCheck(start, 0.3, 0.2, 1.8, 0.2, size);
                //along
                drawLineCheck(start, 0.6, 2.4, size);
                break;
            case '8':
                //middle - left to right
                drawLineCheck(start, 0.5, 1.3, 1.6, 1.3, size,0);
                drawLineCheck(start, 1.8, 1.5, size);
                drawLineCheck(start, 1.8, 2.2, size);
                drawLineCheck(start, 1.6, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 1.5, size);
                drawLineCheck(start, 0.5, 1.3, size);
                drawLineCheck(start, 0.3, 1.1, size);
                drawLineCheck(start, 0.3, 0.4, size);
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.6, 0.2, size);
                drawLineCheck(start, 1.8, 0.4, size);
                drawLineCheck(start, 1.8, 1.1, size);
                drawLineCheck(start, 1.6, 1.3, size);
                break;
            case '9':
                //middle - right to left
                drawLineCheck(start, 1.8, 1.3, 0.5, 1.3, size);
                //along
                drawLineCheck(start, 0.3, 1.1, size);
                drawLineCheck(start, 0.3, 0.4, size);
                drawLineCheck(start, 0.5, 0.2, size);
                drawLineCheck(start, 1.6, 0.2, size);
                drawLineCheck(start, 1.8, 0.4, size);
                drawLineCheck(start, 1.8, 2.2, size);
                drawLineCheck(start, 1.6, 2.4, size);
                drawLineCheck(start, 0.5, 2.4, size);
                drawLineCheck(start, 0.3, 2.2, size);
                drawLineCheck(start, 0.3, 2.0, size);
                break;
        }
    }
}
