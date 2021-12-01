
/**
 * Diese Klasse enthält die Kernlogik des Spiels und den Gameloop
 * 
 * @author Lasse Huber-Saffer
 * @version 01.12.2021
 */
public class Game
{
    private Renderer _renderer;
    private TimeManager _timeManager;

    /**
     * Konstruktor für Objekte der Klasse Game
     */
    public Game()
    {
        _renderer = new Renderer();
        _timeManager = new TimeManager();
    }
    
    /**
     * Startmethode des Spiels
     */
    public void start()
    {
        Matrix4 mat = new Matrix4();
        mat.print();
        runGameLoop();
    }
    
    public void runGameLoop()
    {
        double runTime = 0.0, deltaTime = 0.0;
        Vector2 start, end;
        start = new Vector2(250, 250);
        while(true)
        {
            runTime = _timeManager.getRunTime();
            deltaTime = _timeManager.getDeltaTime();
            //System.out.println("Runtime: " + runTime + " - Delta: " + deltaTime);
            
            double angle=Math.toRadians(30*runTime);
            end = new Vector2(250+200*Math.cos(angle),250+200*Math.sin(angle));
            
            _renderer.clear();
            _renderer.drawLine(start, end);
        }
    }
}
