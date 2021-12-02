import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

/**
 * Die Klasse InputManger verwaltet den Tastatureingaben zur steuerung des Spiels.
 * 
 * @author Sven Schreiber
 * @version 01.12.2021
 */
public class InputManager
{
    // Ein Array das die Zustände der Tasten speichert (Gedrückt = true, Nicht-Gedrückt = false)
    private static boolean[] _keys;
    
    /**
     * Konstruktor für Objekte der Klasse InputManager
     */
    public InputManager()
    {
        _keys = new boolean[KeyCode.values().length];
        initKeyListener();
    }
    
    /**
     * Gibt zuückt ob eine bestimmte Taste gedrückt ist.
     * 
     * @param key Eine bestimmte Taste
     * @return Liefert true, falls die Taste gedrückt ist.
     */
    public boolean isKeyPressed(KeyCode key)
    {
        return _keys[key.ordinal()];
    }
    
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
                                _keys[KeyCode.KEY_SPACE.ordinal()] = true;
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
}
