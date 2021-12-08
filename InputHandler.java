import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.*;
import java.awt.*;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;

/**
 * Die Klasse InputHandler verwaltet den Tastatureingaben zur Steuerung des Spiels.
 * @author Sven Schreiber, Lasse Huber-Saffer
 * @version 08.12.2021
 */
public class InputHandler
{
    // Ein Array das die Zustände der Tasten speichert (Gedrückt = true, nicht gedrückt = false)
    private static boolean[] _keys;
    
    private boolean _keepMouseInPlace = false;
    private Vector2 _lastGlobalMousePos;
    private Vector2 _lastLocalMousePos;
    private Vector2 _mouseResetAnchorPos;
    private Vector2 _mouseDelta;
    private JFrame _jFrame;
    
    /**
     * Konstruktor für Objekte der Klasse InputHandler
     */
    public InputHandler()
    {       
        _keys = new boolean[KeyCode.values().length];
        _lastGlobalMousePos = null;
        _lastLocalMousePos = null;
        _mouseResetAnchorPos = null;
        _mouseDelta = new Vector2();
        
        // Initialize Event Listeners
        initKeyListener();
        initMouseListener();
    }
    
    /**
     * Lädt das JFrame, welches von der globalen TurtleWelt benutzt wird.
     */
    public void loadJFrame()
    {
        if (_jFrame == null)
        {
            TurtleWelt welt = TurtleWelt.GLOBALEWELT;

            for (Frame frame : Frame.getFrames())
            {
                if (frame instanceof JFrame && frame.getTitle().equals("Turtle Graphics - the canvas can be cleared by right-clicking on it"))
                {
                    _jFrame = (JFrame) frame;
                }
            }
        }
    }
    
    /**
     * Gibt zurück ob eine bestimmte Taste gedrückt ist.
     * @param key Eine bestimmte Taste
     * @return Liefert true, falls die Taste gedrückt ist.
     */
    public boolean isKeyPressed(KeyCode key)
    {
        return _keys[key.ordinal()];
    }
    
    /**
     * Gibt die Position des Mauszeigers relativ zum Fenster zurück
     * @return die Position des Mauszeigers relativ zum Fenster
     */
    public Vector2 getLocalMousePos()
    {
        return new Vector2(_lastLocalMousePos);
    }
    
    /**
     * Gibt die Position des Mauszeigers relativ zum Bildschirm zurück
     * @return die Position des Mauszeigers relativ zum Bildschirm
     */
    public Vector2 getGlobalMousePos()
    {
        return new Vector2(_lastGlobalMousePos);
    }
    
    /**
     * Gibt die relative Mausposition seit dem letzten Zurücksetzen zurück
     * @return relative Mausposition seit dem letzten Zurücksetzen
     */
    public Vector2 getMouseDelta()
    {
        return new Vector2(_mouseDelta);
    }
    
    /**
     * Gibt die relative Mausposition seit dem letzten Zurücksetzen zurück, danach wird sie zurückgesetzt.
     * @return relative Mausposition seit dem letzten Zurücksetzen
     */
    public Vector2 getAndResetMouseDelta()
    {
        Vector2 result = new Vector2(_mouseDelta);
        _mouseDelta = new Vector2();
        return result;
    }
    
    /**
     * Gibt zurück, ob die Maus aktuell bei Bewegung zur vorherigen Position zurückgesetzt wird
     * @return true, wenn die Maus aktuell bei Bewegung zur vorherigen Position zurückgesetzt wird, sonst false
     */
    public boolean getKeepMouseInPlace()
    {
        return _keepMouseInPlace;
    }
    
    /**
     * Setzt den Zustand, ob die Maus bei Bewegung zur vorherigen Position zurückgesetzt werden soll
     * @param keepMouseInPlace ob die Maus bei Bewegung zur vorherigen Position zurückgesetzt werden soll
     */
    public void setKeepMouseInPlace(boolean keepMouseInPlace)
    {
        if(!keepMouseInPlace)
        {
            _mouseResetAnchorPos = null;
            
            _jFrame.setCursor(0);
        }
        else
        {
             _jFrame.setCursor(_jFrame.getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null ));
        }
        
        _keepMouseInPlace = keepMouseInPlace;
    }
    
    /**
     * Initialisiert den KeyListener
     */
    private void initKeyListener()
    {
        AWTEventListener listener = new AWTEventListener() 
        {
            @Override
            public void eventDispatched(AWTEvent event)
            {
                try
                {
                    KeyEvent evt = (KeyEvent)event;
                    if (evt.getID() == KeyEvent.KEY_PRESSED)
                    {
                        switch (evt.getKeyCode())
                        {
                            case KeyEvent.VK_W:
                                _keys[KeyCode.KEY_W.ordinal()] = true;
                                break;
                            case KeyEvent.VK_A:
                                _keys[KeyCode.KEY_A.ordinal()] = true;
                                break;
                            case KeyEvent.VK_S:
                                _keys[KeyCode.KEY_S.ordinal()] = true;
                                break;
                            case KeyEvent.VK_D:
                                _keys[KeyCode.KEY_D.ordinal()] = true;
                                break;
                            case KeyEvent.VK_SPACE:
                                _keys[KeyCode.KEY_SPACE.ordinal()] = true;
                                break;
                            case KeyEvent.VK_ESCAPE:
                                _keys[KeyCode.KEY_ESCAPE.ordinal()] = true;
                                break;
                            case KeyEvent.VK_PLUS:
                                _keys[KeyCode.KEY_PLUS.ordinal()] = true;
                                break;
                            case KeyEvent.VK_MINUS:
                                _keys[KeyCode.KEY_MINUS.ordinal()] = true;
                                break;
                        }
                    }
                    else if (evt.getID() == KeyEvent.KEY_RELEASED)
                    {
                        switch (evt.getKeyCode())
                        {
                            case KeyEvent.VK_W:
                                _keys[KeyCode.KEY_W.ordinal()] = false;
                                break;
                            case KeyEvent.VK_A:
                                _keys[KeyCode.KEY_A.ordinal()] = false;
                                break;
                            case KeyEvent.VK_S:
                                _keys[KeyCode.KEY_S.ordinal()] = false;
                                break;
                            case KeyEvent.VK_D:
                                _keys[KeyCode.KEY_D.ordinal()] = false;
                                break;
                            case KeyEvent.VK_SPACE:
                                _keys[KeyCode.KEY_SPACE.ordinal()] = false;
                                break;
                            case KeyEvent.VK_ESCAPE:
                                _keys[KeyCode.KEY_ESCAPE.ordinal()] = false;
                                break;
                            case KeyEvent.VK_PLUS:
                                _keys[KeyCode.KEY_PLUS.ordinal()] = false;
                                break;
                            case KeyEvent.VK_MINUS:
                                _keys[KeyCode.KEY_MINUS.ordinal()] = false;
                                break;
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.KEY_EVENT_MASK);
    }
    
    /**
     * Initialisiert den MouseListener
     */
    private void initMouseListener()
    {
        AWTEventListener listener = new AWTEventListener() 
        {
            @Override
            public void eventDispatched(AWTEvent event)
            {
                try
                {
                    MouseEvent evt = (MouseEvent)event;
                    
                    if(evt.getID() == MouseEvent.MOUSE_MOVED)// || evt.getID() == MouseEvent.MOUSE_DRAGGED)
                    {
                        Point mousePos = evt.getLocationOnScreen();
                        Point localMousePos = evt.getPoint();
                        
                        _lastGlobalMousePos = new Vector2(mousePos.getX(), mousePos.getY());
                        _lastLocalMousePos = new Vector2(localMousePos.getX(), localMousePos.getY());
                        
                        if(_mouseResetAnchorPos != null)
                        {
                            _mouseDelta = _mouseDelta.add(new Vector2(mousePos.getX() - _mouseResetAnchorPos.getX(), mousePos.getY() - _mouseResetAnchorPos.getY()));
                        }
                        
                        if(_keepMouseInPlace)
                        {
                            if(_mouseResetAnchorPos == null)
                            {
                                Rectangle windowBounds = _jFrame.getBounds();
                                double anchorPosX = (windowBounds.x + windowBounds.width) / 2;
                                double anchorPosY = (windowBounds.y + windowBounds.height) / 2;
                                _mouseResetAnchorPos = new Vector2(anchorPosX, anchorPosY);
                            }
                            else
                            {
                                try
                                {
                                    Robot bot = new Robot();
                                    bot.mouseMove((int)_mouseResetAnchorPos.getX(), (int)_mouseResetAnchorPos.getY());
                                }
                                catch (AWTException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }
}
