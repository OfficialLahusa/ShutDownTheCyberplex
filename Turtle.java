import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.HashMap;

/**
 * Diese Klasse definiert Turtles, die mit einfachen
 * Operationen "bewegt" werden koennen. Die Bewegungen
 * einer Turtle koennen auf einer Zeichenflaeche sichtbar
 * gemacht werden, die Turtle hinterlaesst quasi eine
 * Spur. Die Turtle selbst ist nicht sichtbar.
 * <br><br>
 * Warum gerade eine Turtle, also eine Schildkroete?
 * Hauptsaechlich aufgrund der Programmiersprache LOGO, mit
 * der die Idee der Turtle Graphics bekannt geworden ist.
 * Etwas mehr Hintergrund ist hier zu finden:
 * http://llk.media.mit.edu/projects/circles/turtles.html
 * 
 * @author Original von Alfred Hermes (14.12.2003)
 * @author Simon Gerlach
 * @author Axel Schmolitzky
 * @author Fredrik Winkler
 * @author Clara Marie Lueders
 * @version 5. Dezember 2018
 */
public class Turtle
{
    // Position dieser Turtle
    private double _xPos;
    private double _yPos;

    // Richtung dieser Turtle
    private double _richtung;
    
    // Die Farbe der Spur, die diese Turtle hinterlaesst
    private Color _farbe;
    
    // Gibt an, ob diese Turtle eine Spur hinterlaesst oder nicht
    private boolean _spurHinterlassen;
    
    // Reaktionszeit einer Turtle auf Befehle
    private int _verzoegerung;
    
    // Moegliche Farbwerte
    private static final HashMap<String, Color> FARBEN;
    private static final Color[] FARB_ARRAY;

    static
    {
        FARBEN = new HashMap<String, Color>();
        FARBEN.put("schwarz", Color.BLACK);
        FARBEN.put("blau", Color.BLUE);
        FARBEN.put("cyan", Color.CYAN);
        FARBEN.put("dunkelgrau", Color.DARK_GRAY);
        FARBEN.put("grau", Color.GRAY);
        FARBEN.put("gruen", Color.GREEN);
        FARBEN.put("hellgrau", Color.LIGHT_GRAY);
        FARBEN.put("magenta", Color.MAGENTA);
        FARBEN.put("orange", Color.ORANGE);
        FARBEN.put("pink", Color.PINK);
        FARBEN.put("rot", Color.RED);
        FARBEN.put("weiss", Color.WHITE);
        FARBEN.put("gelb", Color.YELLOW);
        FARB_ARRAY = FARBEN.values().toArray(new Color[FARBEN.size()]);
    }

    /**
     * Initialisiert eine neue Turtle auf den Mittelpunkt der Welt.
     * Die Ausrichtung ist nach rechts (0 Grad),
     * und es wird eine schwarze Spur hinterlassen.
     */
    public Turtle()
    {
        this(TurtleWelt.WIDTH / 2, TurtleWelt.HEIGHT / 2);
    }

    /**
     * Loescht alle Spuren, die die Turtle bisher hinterlassen hat.
     */
    public void loescheAlleSpuren()
    {
        TurtleWelt.GLOBALEWELT.loescheAlleSpuren();
    }
    
    /**
     * Initialisiert eine neue Turtle auf einen gegebenen Startpunkt.
     * Die Ausrichtung ist nach rechts (0 Grad),
     * und es wird eine schwarze Spur hinterlassen.
     * @param x die X-Koordinate
     * @param y die Y-Koordinate
     */
    public Turtle(double x, double y)
    {
        _xPos = x;
        _yPos = y;
        _richtung = 0;
        _verzoegerung = 1;
        _farbe = Color.BLACK;
        _spurHinterlassen = true;
    }

    /**
     * Bewegt die Turtle vorwaerts in Blickrichtung.
     * @param schritte Anzahl der Pixel, die die Turtle zuruecklegen
     *            soll
     */
    public void geheVor(double schritte)
    {
        double radians = Math.toRadians(_richtung);
        double nextX = _xPos + Math.cos(radians) * schritte;
        double nextY = _yPos + Math.sin(radians) * schritte;
        geheZu(nextX, nextY);
    }

    /**
     * Bewegt die Turtle auf einer Linie zu einer neuen Position.
     * @param x X-Koordinate der neuen Position
     * @param y Y-Koordinate der neuen Position
     */
    public void geheZu(double x, double y)
    {
        if (_spurHinterlassen)
        {
            TurtleWelt.GLOBALEWELT.zeichneLinie(_xPos, _yPos, x, y, _farbe);
        }
        _xPos = x;
        _yPos = y;
        verzoegern();
    }
    
    /**
     * Setzt die Blickrichtung der Turtle.
     * @param winkel 0 = rechts, 90 = unten, 180 = links, 270 = oben
     */
    public void setzeRichtung(double winkel)
    {
        _richtung = winkel;
        verzoegern();
    }

    /**
     * Dreht die Turtle um eine angegebene Winkeldifferenz.
     * @param winkel zu drehende Winkeldifferenz in Grad
     */
    public void drehe(double winkel)
    {
        setzeRichtung(_richtung + winkel);
    }

    /**
     * Laesst die Turtle auf den angegebenen Punkt (x, y) schauen.
     * @param x X-Koordinate
     * @param y Y-Koordinate
     */
    public void schaueAuf(double x, double y)
    {
        double deltaX = x - _xPos;
        double deltaY = y - _yPos;
        setzeRichtung(Math.toDegrees(Math.atan2(deltaY, deltaX)));
    }

    /**
     * Setzt die Farbe der Spur, die diese Turtle hinterlaesst.
     * Moegliche Farben sind "schwarz", "blau", "cyan", "dunkelgrau", 
     * "grau", "gruen", "hellgrau", "magenta", "orange", "pink", "rot", "weiss", "gelb".
     * @param neueFarbe die neue Spur-Farbe der Turtle
     */
    public void setzeFarbe(String neueFarbe)
    {
        if ((neueFarbe == null) || ((_farbe = FARBEN.get(neueFarbe.toLowerCase())) == null))
        {
            _farbe = Color.BLACK;
        }
    }

    /**
     * Setzt eine der 13 moeglichen Farben.
     * @param farbnummer die Farbummer (0 = schwarz, 12 = gelb)
     */
    public void setzeFarbe(int farbnummer)
    {
        _farbe = FARB_ARRAY[Math.abs(farbnummer % 13)];
    }
    
    /**
     * Bewegungen der Turtle sind ab sofort unsichtbar.
     */
    public void hinterlasseKeineSpur()
    {
        _spurHinterlassen = false;
    }

    /**
     * Bewegungen der Turtle sind ab sofort sichtbar.
     */
    public void hinterlasseSpur()
    {
        _spurHinterlassen = true;
    }

    /**
     * Setzt die Geschwindigkeit, mit der die Turtle auf Anweisungen reagiert.
     * @param geschwindigkeit von 0 (langsam) bis 10 (schnell)
     */
    public void setzeGeschwindigkeit(int geschwindigkeit)
    {
        if (geschwindigkeit > 10)
        {
            geschwindigkeit = 10;
        }
        else if (geschwindigkeit < 0)
        {
            geschwindigkeit = 0;
        }
        _verzoegerung = 10 - geschwindigkeit;
    }

    /**
     * Gibt die X-Position der Turtle zurueck.
     * @return die X-Position
     */
    public double gibX()
    {
        return _xPos;
    }

    /**
     * Gibt die Y-Position der Turtle zurueck.
     * @return die Y-Position
     */
    public double gibY()
    {
        return _yPos;
    }

    /**
     * Gibt die Richtung der Turtle zurueck.
     * @return die Richtung
     */
    public double gibRichtung()
    {
        return _richtung;
    }

    /**
     * Abhaengig von der Geschwindigkeit dieser Turtle wird hier kurze Zeit verzoegert.
     */
    private void verzoegern()
    {
        if (_verzoegerung > 0)
        {
            try
            {
                Thread.sleep(_verzoegerung);
            }
            catch (InterruptedException ignore)
            {
                //tue nichts
            }
        }
    }
}

/**
 * Eine Welt, in der sich Turtles bewegen.
 * 
 * @author Original von Alfred Hermes (14.12.2003)
 * @author Simon Gerlach
 * @author Axel Schmolitzky
 * @author Fredrik Winkler
 * @version 5. Dezember 2018
 */
class TurtleWelt
{
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    
    public static final TurtleWelt GLOBALEWELT = new TurtleWelt();
    
    private final Graphics2D _graphics;
    private final JFrame _frame;

    /**
     * Initialisiert eine neue TurtleWelt.
     */
    public TurtleWelt()
    {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        _graphics = image.createGraphics();
        _graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);            
        _graphics.setColor(Color.WHITE);
        _graphics.fillRect(0, 0, WIDTH, HEIGHT);
        
        JPanel panel = new ImagePanel(image);
        panel.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                if (e.getButton() != MouseEvent.BUTTON1)
                {
                    loescheAlleSpuren();
                }
            }
        });

        _frame = new JFrame("Turtle Graphics - the canvas can be cleared by right-clicking on it");
        _frame.add(panel);
        _frame.pack();
        _frame.setResizable(false);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setVisible(true);
    }

    /**
     * Loescht alle Spuren, die Turtles bisher hinterlassen haben.
     */
    public void loescheAlleSpuren()
    {
        _graphics.setColor(new Color(16, 68, 116));
        _graphics.fillRect(0, 0, WIDTH, HEIGHT);
        _frame.repaint();
    }
    
    /**
     * Faerbt den Frame in einer bestimmten Farbe mit RGB Wert.
     * 
     * @param r der Rotanteil
     * @param g der Gruenanteil
     * @param b der Blauanteil
     */
    public void bildschirmEinfaerben(int r, int g, int b)
    {
        //Absicherung gegen unzulaessige Werte
        r = r % 256;
        g = g % 256;
        b = b % 256;
   
        _graphics.setColor(new Color(r, g, b));
        _graphics.fillRect(0, 0, WIDTH, HEIGHT);
        _frame.repaint();
    }

    /**
     * Zeichnet eine farbige Linie von (x1/y1) nach (x2/y2).
     * 
     * @param x1 x-Wert vom ersten Koordinatenpaar
     * @param y1 y-Wert vom ersten Koordinatenpaar
     * @param x2 x-Wert vom zweiten Koordinatenpaar
     * @param y2 y-Wert vom ersten Koordinatenpaar
     * @param farbe gewuenschte Farbe fuer die Linie
     */
    public void zeichneLinie(double x1, double y1, double x2, double y2, Color farbe)
    {
        _graphics.setColor(farbe);
        _graphics.drawLine((int) (x1 + 0.5), (int) (y1 + 0.5), (int) (x2 + 0.5), (int) (y2 + 0.5));
        _frame.repaint();
    }
}

/**
 * Ein Panel, das lediglich aus einem Bild besteht.
 * 
 * @author Original von Alfred Hermes (14.12.2003)
 * @author Simon Gerlach
 * @author Axel Schmolitzky
 * @author Fredrik Winkler
 * @version 5. Dezember 2018
 */
class ImagePanel extends JPanel
{
    private final BufferedImage _image;

    /**
     * Initialisiert ein neues ImagePanel mit dem angegebenen Bild.
     * 
     * @param image das Bild fuer das ein ImagePanel angelegt werden soll
     */
    public ImagePanel(BufferedImage image)
    {
        super(null);
        _image = image;
        setPreferredSize(new Dimension(_image.getWidth(), _image.getHeight()));
    }

    /**
     * Zeichnet das ImagePanel.
     * 
     * @param g das Graphicsobjekt fuer das Bild
     */
    public void paintComponent(Graphics g)
    {
        g.drawImage(_image, 0, 0, null);
    }
}
